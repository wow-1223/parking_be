package com.parking.job.remind;

import com.google.common.collect.Lists;
import com.parking.handler.encrypt.AesUtil;
import com.parking.model.entity.mybatis.OccupiedSpot;
import com.parking.model.entity.mybatis.Order;
import com.parking.model.entity.mybatis.ParkingSpot;
import com.parking.model.entity.mybatis.User;
import lombok.AllArgsConstructor;

import java.util.List;

/**
 * 即将到达预约时间的订单
 */
@AllArgsConstructor
public class SendRemindMessageForWillEndOrdersTask implements Runnable {

    private OrderRemindJob.RemindOrderSupport support;
    private OrderRemindHandler orderRemindHandler;
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
            String msg = orderRemindHandler.buildWillEndOrderRemindMessage(occupiedSpot, spot);

            User user = support.getUserMap().get(order.getUserId());
            String phone = aesUtil.decrypt(user.getPhone());

            remindMessages.add(new String[]{msg, phone});
        }

        orderRemindHandler.batchRemindOrders(null, remindMessages);
    }
}
