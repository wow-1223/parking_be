package com.parking.job.confirm;

import com.google.common.collect.Lists;
import com.parking.enums.lock.LockStatusEnum;
import com.parking.handler.encrypt.AesUtil;
import com.parking.handler.redis.RedisUtil;
import com.parking.handler.task.ThreadPoolUtil;
import com.parking.model.dto.join.OrderUserDTO;
import com.parking.model.dto.parking.ParkingSpotDTO;
import com.parking.model.entity.mybatis.OccupiedSpot;
import com.parking.model.entity.mybatis.Order;
import com.parking.model.entity.mybatis.ParkingSpot;
import com.parking.model.entity.mybatis.User;
import com.parking.model.param.common.PageResponse;
import com.parking.model.param.parking.request.NearbyParkingSpotRequest;
import com.parking.model.param.user.request.CreateOrderRequest;
import com.parking.repository.mybatis.OccupiedSpotRepository;
import com.parking.repository.mybatis.OrderRepository;
import com.parking.repository.mybatis.ParkingSpotRepository;
import com.parking.repository.mybatis.UserRepository;
import com.parking.service.lock.LockService;
import com.parking.service.user.UserOrderService;
import com.parking.service.user.UserParkingService;
import com.parking.util.DateUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;

import static com.parking.enums.order.OrderStatusEnum.*;

/**
 * 订单自动确认
 *  1. 每分钟执行一次，检查15分钟后开始的预定
 *  2. 批量查询订单信息
 *  3. 过滤出待确认的订单
 *  4. 批量查询用户信息
 *  5. 构建映射关系，方便后续使用
 *  6. 批量处理订单确认
 *  7. 批量更新订单
 *  8. 批量发送确认消息
 */
@Slf4j
@Component
public class OrderRemindJob {

    private static final String REVERSED_ORDERS_CACHE = "reversed_orders_cache";
    private static final String PROCESSING_ORDERS_CACHE = "processing_orders_cache";

    private static final int CACHE_EXPIRE_TIME = 10;

    @Autowired
    private OccupiedSpotRepository occupiedSpotRepository;

    @Autowired
    private ParkingSpotRepository parkingSpotRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LockService lockService;

    @Autowired
    private UserParkingService userParkingService;

    @Autowired
    private UserOrderService userOrderService;

    @Autowired
    private ConfirmHandler confirmHandler;

    @Autowired
    private AesUtil aesUtil;

    @Autowired
    private ThreadPoolUtil threadPoolUtil;

    /**
     * 每小时的20、50min执行，检查10分钟后开始与结束的预定
     * - 15min后开始的预定
     *  1. 地锁升起: 无人占用 -> 自动锁定订单
     *  2. 地锁降下: 有人占用
     *      2.1 当前存在订单：其他用户占用
     *          -> 找到附近其他可用车位：提示预约用户[车位正在被使用，可能产生延迟。已为您找到最近的其他可用车位，请至小程序确认是否确认更换]
     *          -> 附近没有其他可用车位：提示预约用户[车位正在被使用，可能产生延迟。附近没有其他可用车位，请选择是否取消或与当前用户沟通]
     *       2.2 当前不存在订单：未知原因被占用 -- 不考虑，交由地锁状态监测Job执行
     *          -> 找到附近其他可用车位：提示预约用户[车位被未知用户占用，可能无法使用。已为您找到附近最近的可用车位，请至小程序确认是否确认更换]
     *          -> 附近没有其他可用车位：提示预约用户[车位被未知用户占用，可能无法使用。附近没有其他可用车位，请选择是否取消或与租户沟通]
     *          -> 提示车位租户[车位被未知用户占用，请尽快确认车位情况，避免影响用户使用]
     *  - 15min后结束的预定
     *      -> 预订车位即将到期，请在xx时间前离开或继续延长使用时间，超时将面临高额罚款
     */
    @Scheduled(cron = "0 20,50 * * * ?")
    public void remindOrder() {
        log.info("Start remind orders");
        try {
            // 1. 获取下一个整点或30min
            LocalDateTime nextHourOrHalfHour = DateUtil.getNextHourOrHalfHour();

            // 2. 即将开始的占用记录 -> 判断是否需要自动确认、提示取消
            List<OccupiedSpot> willStarts = occupiedSpotRepository.findByStartTime(nextHourOrHalfHour);
            RemindOrderSupport wsSupport = generateRemindOrderSupport(
                    willStarts, Lists.newArrayList(RESERVED.getStatus()));


            // 3. 即将结束的占用记录 -> 提示即将到期
            List<OccupiedSpot> willEnds = occupiedSpotRepository.findByEndTime(nextHourOrHalfHour);
            RemindOrderSupport weSupport = generateRemindOrderSupport(
                    willEnds, Lists.newArrayList(PROCESSING.getStatus(), LEAVE_TEMPORARILY.getStatus()));

            threadPoolUtil.executeAsync(new SendRemindMessageForWillStartOrdersTask(wsSupport, weSupport,
                    confirmHandler, aesUtil, userRepository, lockService, userParkingService, userOrderService));

            // 4. 发送即将到期的提示消息
            threadPoolUtil.executeAsync(new SendRemindMessageForWillEndOrdersTask(weSupport, confirmHandler, aesUtil));

        } catch (Exception e) {
            log.error("Auto confirm orders failed", e);
        }
    }


//    /**
//     * 每小时的15、45min执行，检查15分钟后开始的预定
//     * 1. 地锁升起: 无人占用 -> 自动锁定订单
//     * 2. 地锁降下: 有人占用
//     *    2.1 当前存在订单：其他用户占用
//     *      -> 找到附近其他可用车位：提示预约用户[车位正在被使用，可能产生延迟。已为您找到最近的其他可用车位，请至小程序确认是否确认更换]
//     *      -> 附近没有其他可用车位：提示预约用户[车位正在被使用，可能产生延迟。附近没有其他可用车位，请选择是否取消或与当前用户沟通]
//     *    2.2 当前不存在订单：未知原因被占用
//     *      -> 找到附近其他可用车位：提示预约用户[车位被未知用户占用，可能无法使用。已为您找到附近最近的可用车位，请至小程序确认是否确认更换]
//     *      -> 附近没有其他可用车位：提示预约用户[车位被未知用户占用，可能无法使用。附近没有其他可用车位，请选择是否取消或与租户沟通]
//     *      -> 提示车位租户[车位被未知用户占用，请尽快确认车位情况，避免影响用户使用]
//     */
//    @Scheduled(cron = "0 15,45 * * * ?")
//    public void autoConfirmOrders() {
//        log.info("Start auto confirming reserved orders");
//        try {
//            // 1. 获取15分钟后的时间点
//            LocalDateTime confirmTime = LocalDateTime.now().plusMinutes(15);
//
//            // 2. 查询即将开始的预定记录
//            List<OccupiedSpot> occupiedList = occupiedSpotRepository.findByStartTime(confirmTime);
//            if (CollectionUtils.isEmpty(occupiedList)) {
//                return;
//            }
//            Map<Long, OccupiedSpot> occupiedMap = occupiedList.stream()
//                    .collect(Collectors.toMap(OccupiedSpot::getId, spot -> spot));
//
//            // 3. 查询占用停车位的地点信息
//            List<ParkingSpot> parkingSpots = parkingSpotRepository.findAll(
//                    Lists.newArrayList(occupiedMap.keySet()), Lists.newArrayList("id", "owner_id", "location", "device_id"));
//            if (CollectionUtils.isEmpty(parkingSpots)) {
//                return;
//            }
//            Map<Long, ParkingSpot> parkingMap = parkingSpots.stream()
//                    .collect(Collectors.toMap(ParkingSpot::getId, spot -> spot));
//
//            // 4. 查询地锁状态
//            Map<String, String> lockStatus = lockService.getLockStatus(
//                    parkingSpots.stream().map(ParkingSpot::getDeviceId).collect(Collectors.toList()));
//
//            // 5. 批量查询订单信息
//            List<OrderUserDTO> orderUsers = orderRepository.findOrderWithUserByOccupied(
//                    Lists.newArrayList(occupiedMap.keySet()), Lists.newArrayList(RESERVED.getStatus()));
//            if (CollectionUtils.isEmpty(orderUsers)) {
//                return;
//            }
//
//            // 6. 过滤各个状态的订单
//            List<ConfirmOrder> confirmOrders = filterConfirmOrder(orderUsers, parkingMap, occupiedMap, lockStatus);
//
//            List<Order> orders = Lists.newArrayListWithCapacity(confirmOrders.size());
//            List<String[]> confirmMessages = Lists.newArrayListWithCapacity(confirmOrders.size());
//            for (ConfirmOrder confirmOrder : confirmOrders) {
//                orders.add(confirmOrder.getOrder());
//                confirmMessages.add(new String[]{
//                        aesUtil.decrypt(confirmOrder.getUser().getPhone()), confirmOrder.getUserMessage()});
//
//                if (StringUtils.isNotBlank(confirmOrder.getOwnerMessage())) {
//                    confirmMessages.add(new String[]{
//                            aesUtil.decrypt(confirmOrder.getOwner().getPhone()), confirmOrder.getOwnerMessage()});
//                }
//            }
//
//            // 7. 批量处理订单以及发送消息
//            confirmHandler.batchRemindOrders(orders, confirmMessages);
//
//        } catch (Exception e) {
//            log.error("Auto confirm orders failed", e);
//        }
//    }
//
//    private List<ConfirmOrder> filterConfirmOrder(List<OrderUserDTO> ous,
//                                                  Map<Long, ParkingSpot> parkingMap,
//                                                  Map<Long, OccupiedSpot> occupiedMap,
//                                                  Map<String, String> lockStatus) {
//
//        List<ConfirmOrder> notOccupiedCos = Lists.newArrayList();
//        List<ConfirmOrder> occupiedCos = Lists.newArrayList();
//        List<Long> parkingSpotIds = Lists.newArrayList();
//        for (OrderUserDTO ou : ous) {
//            Order order = ou.getOrder();
//            if (Objects.isNull(order)) {
//                continue;
//            }
//            ParkingSpot parkingSpot = parkingMap.get(order.getParkingSpotId());
//            OccupiedSpot occupiedSpot = occupiedMap.get(order.getOccupiedSpotId());
//
//            String status = lockStatus.get(parkingSpot.getDeviceId());
//            if (Objects.equals(LockStatusEnum.RAISED.getStatus(), status)) {
//                order.setStatus(CONFIRMED.getStatus());
//                ConfirmOrder confirmOrder = new ConfirmOrder(order, ou.getUser(), parkingSpot, occupiedSpot);
//                confirmOrder.setUserMessage(confirmHandler.buildConfirmMessage(occupiedSpot, parkingSpot));
//                notOccupiedCos.add(confirmOrder);
//            } else {
//                occupiedCos.add(new ConfirmOrder(order, ou.getUser(), parkingSpot, occupiedSpot));
//                parkingSpotIds.add(parkingSpot.getId());
//            }
//        }
//
//        processOccupied(occupiedCos, parkingSpotIds);
//
//        notOccupiedCos.addAll(occupiedCos);
//        return notOccupiedCos;
//    }
//
//    /**
//     * 处理占用的订单
//     */
//    private void processOccupied(List<ConfirmOrder> cos, List<Long> spotIds) {
//        // 查询当前时间的占用记录
//        List<OccupiedSpot> currentOccupiedSpots = occupiedSpotRepository
//                .findCurrentOccupiedSpots(spotIds, DateUtil.getCurrentDateTime());
//        // key: parking spot id, value: occupied spot
//        Map<Long, OccupiedSpot> curOccupiedMap = currentOccupiedSpots.stream()
//                .collect(Collectors.toMap(OccupiedSpot::getParkingSpotId, spot -> spot));
//
//        // 查询当前时间占用的订单的用户信息
//        List<OrderUserDTO> orderUsers = orderRepository.findOrderWithUserByOccupied(
//                Lists.newArrayList(curOccupiedMap.keySet()), Lists.newArrayList(PROCESSING.getStatus(), LEAVE_TEMPORARILY.getStatus()));
//        // key: parking spot id, value: orderUser
//        Map<Long, OrderUserDTO> orderUserMap = orderUsers.stream().collect(
//                Collectors.toMap(t -> t.getOrder().getParkingSpotId(), t -> t));
//
//        for (ConfirmOrder co : cos) {
//            Long parkingSpotId = co.getOrder().getParkingSpotId();
//            if (curOccupiedMap.containsKey(parkingSpotId)) {
//                // user occupied
//                co.setUserOccupied(true);
//                co.getOrder().setStatus(USER_OCCUPIED.getStatus());
//                co.setUserMessage(confirmHandler.buildUserOccupiedWithoutOtherAvailableSpotMessage(co.getOccupiedSpot(), co.getParkingSpot()));
//
//                OrderUserDTO ou = orderUserMap.get(parkingSpotId);
//                co.setCurOrder(ou.getOrder());
//                co.setCurUser(ou.getUser());
//                co.setCurUserMessage(confirmHandler.buildCurrentUserMessage(curOccupiedMap.get(parkingSpotId), co.getParkingSpot()));
//
//            } else {
//                co.setUnknownOccupied(true);
//                co.getOrder().setStatus(USER_OCCUPIED.getStatus());
//
//                User owner = userRepository.findById(co.getParkingSpot().getOwnerId(), Lists.newArrayList("phone"));
//                co.setOwner(owner);
//                co.setUserMessage(confirmHandler.buildUnknownOccupiedWithoutOtherAvailableSpotsMessage(co.getOccupiedSpot(), co.getParkingSpot()));
//                co.setOwnerMessage(confirmHandler.buildOwnerMessage(co.getParkingSpot()));
//            }
//        }
//    }

    /**
     * 构建提醒数据
     */
    private RemindOrderSupport generateRemindOrderSupport(List<OccupiedSpot> occupiedList, List<Integer> orderStatus) {
        if (CollectionUtils.isEmpty(occupiedList)) {
            log.info("order with occupied spots is null");
            return new RemindOrderSupport();
        }
        RemindOrderSupport support = new RemindOrderSupport();
        for (OccupiedSpot ws : occupiedList) {
            support.getOccupiedMap().put(ws.getId(), ws);
            support.getOccupiedIds().add(ws.getId());
            support.getSpotIds().add(ws.getParkingSpotId());
        }

        List<ParkingSpot> spots = parkingSpotRepository.findAll(support.getSpotIds(),
                Lists.newArrayList("id", "owner_id", "location", "device_id"));
        if (CollectionUtils.isEmpty(spots)) {
            log.error("can not find related parking spots");
            return new RemindOrderSupport();
        }

        for (ParkingSpot spot : spots) {
            support.getParkingMap().put(spot.getId(), spot);
            support.getDeviceIds().add(spot.getDeviceId());
        }

        Map<String, String> lockStatus = lockService.getLockStatus(support.getDeviceIds());
        if (MapUtils.isEmpty(lockStatus)) {
            log.error("can not find lock status");
            return new RemindOrderSupport();
        }
        support.setLockStatus(lockStatus);

        List<OrderUserDTO> orderUsers = orderRepository.findOrderWithUserByOccupied(support.getOccupiedIds(), orderStatus);
        if (CollectionUtils.isEmpty(orderUsers)) {
            log.error("order with occupied spots is null");
            return new RemindOrderSupport();
        }
        for (OrderUserDTO ou : orderUsers) {
            support.getOrderMap().put(ou.getOrder().getId(), ou.getOrder());
            support.getUserMap().put(ou.getUser().getId(), ou.getUser());
        }
        return support;
    }

    /**
     * 即将到达预约时间的订单
     */
    @Data
    @AllArgsConstructor
    static class SendRemindMessageForWillStartOrdersTask implements Runnable {
        private RemindOrderSupport support;
        private RemindOrderSupport weSupport;
        private ConfirmHandler confirmHandler;
        private AesUtil aesUtil;
        private UserRepository userRepository;
        private LockService lockService;
        private UserParkingService userParkingService;
        private UserOrderService userOrderService;

        @Override
        public void run() {
            if (support == null) {
                return;
            }

            List<Order> notOccupied = new ArrayList<>();
            List<Order> userOccupied = new ArrayList<>();
            List<Order> unknownOccupied = new ArrayList<>();

            support.getOrderIds().removeAll(weSupport.getOrderIds());
            for (Long orderId : support.getOrderIds()) {
                Order order = support.getOrderMap().get(orderId);
                notOccupied.add(order);
                support.getOrderMap().remove(orderId);
            }

            for (Map.Entry<Long, Order> entry : support.getOrderMap().entrySet()) {
                Order order = entry.getValue();
                if (weSupport.getOccupiedMap().containsKey(order.getOccupiedSpotId())) {
                    userOccupied.add(order);
                } else {
                    unknownOccupied.add(order);
                }
            }

            processNotOccupied(notOccupied);
            processUserOccupied(userOccupied);
            processUnknownOccupied(unknownOccupied);

        }

        private void processNotOccupied(List<Order> orders) {
            List<String[]> confirmMessages = Lists.newArrayListWithCapacity(support.getParkingMap().size());
            for (Order order : orders) {
                order.setStatus(CONFIRMED.getStatus());

                OccupiedSpot occupiedSpot = support.getOccupiedMap().get(order.getOccupiedSpotId());
                ParkingSpot spot = support.getParkingMap().get(order.getParkingSpotId());
                String msg = confirmHandler.buildConfirmMessage(occupiedSpot, spot);

                User user = support.getUserMap().get(order.getUserId());
                String phone = aesUtil.decrypt(user.getPhone());
                confirmMessages.add(new String[]{msg, phone});
            }
            confirmHandler.batchRemindOrders(orders, confirmMessages);
        }

        private void processUserOccupied(List<Order> orders) {
            List<String[]> confirmMessages = Lists.newArrayListWithCapacity(support.getParkingMap().size());
            for (Order order : orders) {
                ParkingSpot spot = support.getParkingMap().get(order.getParkingSpotId());
                OccupiedSpot occupiedSpot = support.getOccupiedMap().get(order.getOccupiedSpotId());
                User user = support.getUserMap().get(order.getUserId());
                String phone = aesUtil.decrypt(user.getPhone());
//                order.setStatus(USER_OCCUPIED.getStatus());

                ParkingSpotDTO availableSpot = findNearbyAvailableParkingSpot(spot, occupiedSpot);

                if (availableSpot == null) {
                    String msg = confirmHandler.buildUserOccupiedWithoutOtherAvailableSpotMessage(occupiedSpot, spot);
                    confirmMessages.add(new String[]{msg, phone});
                } else {
                    CreateOrderRequest req = new CreateOrderRequest();
                    req.setParkingSpotId(availableSpot.getId());
                    req.setStartTime(DateUtil.formatTime(occupiedSpot.getStartTime()));
                    req.setEndTime(DateUtil.formatTime(occupiedSpot.getEndTime()));
                    req.setCarNumber(order.getCarNumber());
                    userOrderService.createOrder(req);
                    String msg = confirmHandler.buildUserOccupiedWithOtherAvailableSpotMessage(occupiedSpot, spot);
                    confirmMessages.add(new String[]{msg, phone});
                }
            }
            confirmHandler.batchRemindOrders(orders, confirmMessages);
        }

        private void processUnknownOccupied(List<Order> orders) {
            List<String[]> confirmMessages = Lists.newArrayListWithCapacity(support.getParkingMap().size());
            for (Order order : orders) {
                OccupiedSpot occupiedSpot = support.getOccupiedMap().get(order.getOccupiedSpotId());
                ParkingSpot spot = support.getParkingMap().get(order.getParkingSpotId());
                User user = support.getUserMap().get(order.getUserId());
                String phone = aesUtil.decrypt(user.getPhone());
//                order.setStatus(UNKNOWN_OCCUPIED.getStatus());

                ParkingSpotDTO availableSpot = findNearbyAvailableParkingSpot(spot, occupiedSpot);

                if (availableSpot == null) {
                    String msg = confirmHandler.buildUnknownOccupiedWithoutOtherAvailableSpotsMessage(occupiedSpot, spot);
                    confirmMessages.add(new String[]{msg, phone});
                } else {
                    CreateOrderRequest req = new CreateOrderRequest();
                    req.setCarNumber(order.getCarNumber());
                    req.setParkingSpotId(availableSpot.getId());
                    req.setStartTime(DateUtil.formatTime(occupiedSpot.getStartTime()));
                    req.setEndTime(DateUtil.formatTime(occupiedSpot.getEndTime()));
                    userOrderService.createOrder(req);
                    String msg = confirmHandler.buildUnknownOccupiedWithOtherAvailableSpotsMessage(occupiedSpot, spot);
                    confirmMessages.add(new String[]{msg, phone});
                }

                User owner = userRepository.findById(order.getOwnerId(), Lists.newArrayList("phone"));
                String msg = confirmHandler.buildOwnerCheckMessage(spot);
                confirmMessages.add(new String[]{msg, aesUtil.decrypt(owner.getPhone())});
            }
            confirmHandler.batchRemindOrders(orders, confirmMessages);
        }

        private ParkingSpotDTO findNearbyAvailableParkingSpot(ParkingSpot spot, OccupiedSpot occupiedSpot) {
            NearbyParkingSpotRequest request = new NearbyParkingSpotRequest();
            request.setLongitude(spot.getLongitude().doubleValue());
            request.setLatitude(spot.getLatitude().doubleValue());
            request.setRadius(5000);
            request.setStartTime(DateUtil.formatTime(occupiedSpot.getStartTime()));
            request.setEndTime(DateUtil.formatTime(occupiedSpot.getEndTime()));
            int pageIndex = 1;
            int pageSize = 20;
            request.setPage(pageIndex);
            request.setSize(pageSize);

            PageResponse<ParkingSpotDTO> page;
            while (true) {
                page = userParkingService.getNearbyParkings(request);
                for (ParkingSpotDTO ps : page.getList()) {
                    String lockStatus = lockService.getLockStatus(ps.getDeviceId());
                    if (LockStatusEnum.RAISED.getStatus().equals(lockStatus)) {
                        return ps;
                    }
                }

                if (page.getTotal() < pageSize) {
                    break;
                }
                request.setPage(pageIndex++);
            }
            return null;
        }
    }

    /**
     * 即将到达结束时间的订单
     */
    @Data
    @AllArgsConstructor
    static class SendRemindMessageForWillEndOrdersTask implements Runnable {

        private RemindOrderSupport support;
        private ConfirmHandler confirmHandler;
        private AesUtil aesUtil;

        @Override
        public void run() {
            if (support == null) {
                return;
            }
            List<String[]> remindMessages = Lists.newArrayListWithCapacity(support.getParkingMap().size());
            for (Order order : support.getOrderMap().values()) {
                OccupiedSpot occupiedSpot = support.getOccupiedMap().get(order.getOccupiedSpotId());
                ParkingSpot spot = support.getParkingMap().get(order.getParkingSpotId());
                String msg = confirmHandler.buildWillEndOrderRemindMessage(occupiedSpot, spot);

                User user = support.getUserMap().get(order.getUserId());
                String phone = aesUtil.decrypt(user.getPhone());

                remindMessages.add(new String[]{msg, phone});
            }

            confirmHandler.batchRemindOrders(null, remindMessages);
        }
    }

//    @Data
//    @AllArgsConstructor
//    public static class ConfirmOrder {
//        private Order order;
//        private User user;
//        private ParkingSpot parkingSpot;
//        private OccupiedSpot occupiedSpot;
//        private String userMessage;
//
//        private Boolean userOccupied;
//        private Order curOrder;
//        private User curUser;
//        private String curUserMessage;
//
//        private Boolean unknownOccupied;
//        private User owner;
//        private String ownerMessage;
//
//        public ConfirmOrder(Order order, User user, ParkingSpot parkingSpot, OccupiedSpot occupiedSpot) {
//            this.order = order;
//            this.user = user;
//            this.parkingSpot = parkingSpot;
//            this.occupiedSpot = occupiedSpot;
//            this.userOccupied = false;
//            this.unknownOccupied = false;
//        }
//    }

    @Data
    @AllArgsConstructor
    public static class RemindOrderSupport {
        private List<Long> orderIds;
        private Map<Long, Order> orderMap;
        private Map<Long, User> userMap;
        private List<Long> occupiedIds;
        private List<Long> spotIds;
        private Map<Long, OccupiedSpot> occupiedMap;
        private List<String> deviceIds;
        private Map<Long, ParkingSpot> parkingMap;
        private Map<String, String> lockStatus;

        public RemindOrderSupport() {
            this.orderIds = new ArrayList<>();
            this.orderMap = new HashMap<>();
            this.userMap = new HashMap<>();
            this.occupiedIds = new ArrayList<>();
            this.occupiedMap = new HashMap<>();
            this.spotIds = new ArrayList<>();
            this.deviceIds = new ArrayList<>();
            this.parkingMap = new HashMap<>();
            this.lockStatus = new HashMap<>();
        }
    }

}