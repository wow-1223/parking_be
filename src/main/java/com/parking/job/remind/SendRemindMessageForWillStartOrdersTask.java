package com.parking.job.remind;

import com.google.common.collect.Lists;
import com.parking.enums.lock.LockStatusEnum;
import com.parking.handler.encrypt.AesUtil;
import com.parking.model.dto.parking.ParkingSpotDTO;
import com.parking.model.entity.mybatis.OccupiedSpot;
import com.parking.model.entity.mybatis.Order;
import com.parking.model.entity.mybatis.ParkingSpot;
import com.parking.model.entity.mybatis.User;
import com.parking.model.param.common.PageResponse;
import com.parking.model.param.parking.request.NearbyParkingSpotRequest;
import com.parking.model.param.user.request.CreateOrderRequest;
import com.parking.repository.mybatis.UserRepository;
import com.parking.service.lock.LockService;
import com.parking.service.user.UserOrderService;
import com.parking.service.user.UserParkingService;
import com.parking.util.DateUtil;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.parking.enums.order.OrderStatusEnum.*;

/**
 * 即将到达预约时间的订单
 */
@Data
@AllArgsConstructor
public class SendRemindMessageForWillStartOrdersTask implements Runnable {

    private OrderRemindJob.RemindOrderSupport support;
    private OrderRemindJob.RemindOrderSupport weSupport;
    private RemindHandler remindHandler;
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
            String msg = remindHandler.buildConfirmMessage(occupiedSpot, spot);

            User user = support.getUserMap().get(order.getUserId());
            String phone = aesUtil.decrypt(user.getPhone());
            confirmMessages.add(new String[]{msg, phone});
        }
        remindHandler.batchRemindOrders(orders, confirmMessages);
    }

    private void processUserOccupied(List<Order> orders) {
        List<String[]> confirmMessages = Lists.newArrayListWithCapacity(support.getParkingMap().size());
        for (Order order : orders) {
            ParkingSpot spot = support.getParkingMap().get(order.getParkingSpotId());
            OccupiedSpot occupiedSpot = support.getOccupiedMap().get(order.getOccupiedSpotId());
            User user = support.getUserMap().get(order.getUserId());
            String phone = aesUtil.decrypt(user.getPhone());
            order.setStatus(USER_OCCUPIED.getStatus());

            ParkingSpotDTO availableSpot = findNearbyAvailableParkingSpot(spot, occupiedSpot);

            if (availableSpot == null) {
                String msg = remindHandler.buildUserOccupiedWithoutOtherAvailableSpotMessage(occupiedSpot, spot);
                confirmMessages.add(new String[]{msg, phone});
            } else {
                CreateOrderRequest req = new CreateOrderRequest();
                req.setParkingSpotId(availableSpot.getId());
                req.setStartTime(DateUtil.formatTime(occupiedSpot.getStartTime()));
                req.setEndTime(DateUtil.formatTime(occupiedSpot.getEndTime()));
                req.setCarNumber(order.getCarNumber());
                userOrderService.createOrder(req);
                String msg = remindHandler.buildUserOccupiedWithOtherAvailableSpotMessage(occupiedSpot, spot);
                confirmMessages.add(new String[]{msg, phone});
            }
        }
        remindHandler.batchRemindOrders(orders, confirmMessages);
    }

    private void processUnknownOccupied(List<Order> orders) {
        List<String[]> confirmMessages = Lists.newArrayListWithCapacity(support.getParkingMap().size());
        for (Order order : orders) {
            OccupiedSpot occupiedSpot = support.getOccupiedMap().get(order.getOccupiedSpotId());
            ParkingSpot spot = support.getParkingMap().get(order.getParkingSpotId());
            User user = support.getUserMap().get(order.getUserId());
            String phone = aesUtil.decrypt(user.getPhone());
            order.setStatus(UNKNOWN_OCCUPIED.getStatus());

            ParkingSpotDTO availableSpot = findNearbyAvailableParkingSpot(spot, occupiedSpot);

            if (availableSpot == null) {
                String msg = remindHandler.buildUnknownOccupiedWithoutOtherAvailableSpotsMessage(occupiedSpot, spot);
                confirmMessages.add(new String[]{msg, phone});
            } else {
                CreateOrderRequest req = new CreateOrderRequest();
                req.setCarNumber(order.getCarNumber());
                req.setParkingSpotId(availableSpot.getId());
                req.setStartTime(DateUtil.formatTime(occupiedSpot.getStartTime()));
                req.setEndTime(DateUtil.formatTime(occupiedSpot.getEndTime()));
                userOrderService.createOrder(req);
                String msg = remindHandler.buildUnknownOccupiedWithOtherAvailableSpotsMessage(occupiedSpot, spot);
                confirmMessages.add(new String[]{msg, phone});
            }

            User owner = userRepository.findById(order.getOwnerId(), Lists.newArrayList("id", "phone"));
            String msg = remindHandler.buildOwnerCheckMessage(spot);
            confirmMessages.add(new String[]{msg, aesUtil.decrypt(owner.getPhone())});
        }
        remindHandler.batchRemindOrders(orders, confirmMessages);
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
