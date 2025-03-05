package com.parking.job.confirm;

import com.google.common.collect.Lists;
import com.parking.enums.lock.LockStatusEnum;
import com.parking.handler.encrypt.AesUtil;
import com.parking.model.dto.join.OrderUserDTO;
import com.parking.model.entity.mybatis.OccupiedSpot;
import com.parking.model.entity.mybatis.Order;
import com.parking.model.entity.mybatis.ParkingSpot;
import com.parking.model.entity.mybatis.User;
import com.parking.repository.mybatis.OccupiedSpotRepository;
import com.parking.repository.mybatis.OrderRepository;
import com.parking.repository.mybatis.ParkingSpotRepository;
import com.parking.repository.mybatis.UserRepository;
import com.parking.service.lock.LockService;
import com.parking.util.DateUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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
public class OrderAutoConfirmJob {

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
    private ConfirmHandler confirmHandler;

    @Autowired
    private AesUtil aesUtil;


    /**
     * 每小时的15、45min执行，检查15分钟后开始的预定
     * 1. 地锁升起: 无人占用 -> 自动锁定订单
     * 2. 地锁降下: 有人占用
     *    2.1 当前存在订单：其他用户占用
     *      -> 提示预约用户[车位正在被使用，可能产生延迟，可选择取消或与当前用户沟通]
     *    2.2 当前不存在订单：未知原因被占用
     *      -> 提示预约用户[车位被未知用户占用，可能无法使用，可选择取消或与车位租户沟通]
     *      -> 提示车位租户[车位被未知用户占用，请尽快确认车位情况，避免影响用户使用]
     */
    @Scheduled(cron = "0 15,45 * * * ?")
    public void autoConfirmOrders() {
        log.info("Start auto confirming reserved orders");
        try {
            // 1. 获取15分钟后的时间点
            LocalDateTime confirmTime = LocalDateTime.now().plusMinutes(15);

            // 2. 查询即将开始的预定记录
            List<OccupiedSpot> occupiedList = occupiedSpotRepository.findByStartTime(confirmTime);
            if (CollectionUtils.isEmpty(occupiedList)) {
                return;
            }
            Map<Long, OccupiedSpot> occupiedMap = occupiedList.stream()
                    .collect(Collectors.toMap(OccupiedSpot::getId, spot -> spot));

            // 3. 查询占用停车位的地点信息
            List<ParkingSpot> parkingSpots = parkingSpotRepository.findAll(
                    Lists.newArrayList(occupiedMap.keySet()), Lists.newArrayList("id", "owner_id", "location", "device_id"));
            if (CollectionUtils.isEmpty(parkingSpots)) {
                return;
            }
            Map<Long, ParkingSpot> parkingMap = parkingSpots.stream()
                    .collect(Collectors.toMap(ParkingSpot::getId, spot -> spot));

            // 4. 查询地锁状态
            Map<String, String> lockStatus = lockService.getLockStatus(
                    parkingSpots.stream().map(ParkingSpot::getDeviceId).collect(Collectors.toList()));

            // 5. 批量查询订单信息
            List<OrderUserDTO> orderUsers = orderRepository.findOrderWithUserByOccupied(
                    Lists.newArrayList(occupiedMap.keySet()), RESERVED.getStatus());
            if (CollectionUtils.isEmpty(orderUsers)) {
                return;
            }

            // 6. 过滤各个状态的订单
            List<ConfirmOrder> confirmOrders = filterConfirmOrder(orderUsers, parkingMap, occupiedMap, lockStatus);

            List<Order> orders = Lists.newArrayListWithCapacity(confirmOrders.size());
            List<String[]> confirmMessages = Lists.newArrayListWithCapacity(confirmOrders.size());
            for (ConfirmOrder confirmOrder : confirmOrders) {
                orders.add(confirmOrder.getOrder());
                confirmMessages.add(new String[]{
                        aesUtil.decrypt(confirmOrder.getUser().getPhone()), confirmOrder.getUserMessage()});

                if (StringUtils.isNotBlank(confirmOrder.getOwnerMessage())) {
                    confirmMessages.add(new String[]{
                            aesUtil.decrypt(confirmOrder.getOwner().getPhone()), confirmOrder.getOwnerMessage()});
                }
            }

            // 7. 批量处理订单以及发送消息
            confirmHandler.batchConfirmOrders(orders, confirmMessages);

        } catch (Exception e) {
            log.error("Auto confirm orders failed", e);
        }
    }

    private List<ConfirmOrder> filterConfirmOrder(List<OrderUserDTO> ous,
                                                  Map<Long, ParkingSpot> parkingMap,
                                                  Map<Long, OccupiedSpot> occupiedMap,
                                                  Map<String, String> lockStatus) {

        List<ConfirmOrder> notOccupiedCos = Lists.newArrayList();
        List<ConfirmOrder> occupiedCos = Lists.newArrayList();
        List<Long> parkingSpotIds = Lists.newArrayList();
        for (OrderUserDTO ou : ous) {
            Order order = ou.getOrder();
            if (Objects.isNull(order)) {
                continue;
            }
            ParkingSpot parkingSpot = parkingMap.get(order.getParkingSpotId());
            OccupiedSpot occupiedSpot = occupiedMap.get(order.getOccupiedSpotId());

            String status = lockStatus.get(parkingSpot.getDeviceId());
            if (Objects.equals(LockStatusEnum.RAISED.getStatus(), status)) {
                order.setStatus(CONFIRMED.getStatus());
                ConfirmOrder confirmOrder = new ConfirmOrder(order, ou.getUser(), parkingSpot, occupiedSpot);
                confirmOrder.setUserMessage(confirmHandler.buildConfirmMessage(occupiedSpot, parkingSpot));
                notOccupiedCos.add(confirmOrder);
            } else {
                occupiedCos.add(new ConfirmOrder(order, ou.getUser(), parkingSpot, occupiedSpot));
                parkingSpotIds.add(parkingSpot.getId());
            }
        }

        processOccupied(occupiedCos, parkingSpotIds);

        notOccupiedCos.addAll(occupiedCos);
        return notOccupiedCos;
    }

    /**
     * 处理占用的订单
     */
    private void processOccupied(List<ConfirmOrder> cos, List<Long> spotIds) {
        // 查询当前时间的占用记录
        List<OccupiedSpot> currentOccupiedSpots = occupiedSpotRepository
                .findCurrentOccupiedSpots(spotIds, DateUtil.getCurrentDateTime());
        // key: parking spot id, value: occupied spot
        Map<Long, OccupiedSpot> curOccupiedMap = currentOccupiedSpots.stream()
                .collect(Collectors.toMap(OccupiedSpot::getParkingSpotId, spot -> spot));

        // 查询当前时间占用的订单的用户信息
        List<OrderUserDTO> orderUsers = orderRepository.findOrderWithUserByOccupied(
                Lists.newArrayList(curOccupiedMap.keySet()), PROCESSING.getStatus());
        // key: parking spot id, value: orderUser
        Map<Long, OrderUserDTO> orderUserMap = orderUsers.stream().collect(
                Collectors.toMap(t -> t.getOrder().getParkingSpotId(), t -> t));

        for (ConfirmOrder co : cos) {
            Long parkingSpotId = co.getOrder().getParkingSpotId();
            if (curOccupiedMap.containsKey(parkingSpotId)) {
                // user occupied
                co.setUserOccupied(true);
                co.setUserMessage(confirmHandler.buildUserOccupiedMessage(co.getOccupiedSpot(), co.getParkingSpot()));

                OrderUserDTO ou = orderUserMap.get(parkingSpotId);
                co.setCurOrder(ou.getOrder());
                co.setCurUser(ou.getUser());
            } else {
                co.setUnknownOccupied(true);
                User owner = userRepository.findById(co.getParkingSpot().getOwnerId(), Lists.newArrayList("phone"));
                co.setOwner(owner);
                co.setUserMessage(confirmHandler.buildUnknownOccupiedMessage(co.getOccupiedSpot(), co.getParkingSpot()));
                co.setOwnerMessage(confirmHandler.buildOwnerMessage(co.getParkingSpot()));
            }
        }
    }

    @Data
    @AllArgsConstructor
    public static class ConfirmOrder {
        private Order order;
        private User user;
        private ParkingSpot parkingSpot;
        private OccupiedSpot occupiedSpot;
        private String userMessage;

        private Boolean userOccupied;
        private Order curOrder;
        private User curUser;

        private Boolean unknownOccupied;
        private User owner;
        private String ownerMessage;

        public ConfirmOrder(Order order, User user, ParkingSpot parkingSpot, OccupiedSpot occupiedSpot) {
            this.order = order;
            this.user = user;
            this.parkingSpot = parkingSpot;
            this.occupiedSpot = occupiedSpot;
            this.userOccupied = false;
            this.unknownOccupied = false;
        }
    }

    @Data
    @AllArgsConstructor
    public static class OrderCondition {
        private List<ConfirmOrder> notOccupied;
        private List<ConfirmOrder> userOccupied;
        private List<ConfirmOrder> unknownOccupied;
    }

}