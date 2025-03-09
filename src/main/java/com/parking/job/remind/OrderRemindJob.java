package com.parking.job.remind;

import com.google.common.collect.Lists;
import com.parking.handler.encrypt.AesUtil;
import com.parking.handler.task.ThreadPoolUtil;
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
    private RemindHandler remindHandler;

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
                    remindHandler, aesUtil, userRepository, lockService, userParkingService, userOrderService));

            // 4. 发送即将到期的提示消息
            threadPoolUtil.executeAsync(new SendRemindMessageForWillEndOrdersTask(weSupport, remindHandler, aesUtil));

        } catch (Exception e) {
            log.error("Auto confirm orders failed", e);
        }
    }


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
                Lists.newArrayList("id", "owner_id", "location", "device_id", "status"));
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