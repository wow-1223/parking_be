package com.parking.service.user;

import com.parking.model.entity.mybatis.User;
import com.parking.model.param.common.OperationResponse;

public interface UserLockService {

    /**
     * 立即开锁
     *  1. 到达用户预约时间（包含预约时间过后）
     *      -> 开锁
     *  2. 未到达预约时间，用户主动点击开锁
     *      -> 校验订单时间，提示用户是否提前开始
     *      -> 用户确认
     *      -> 更新订单信息
     *      -> 更新订单起始与结束时间
     * @param user
     * @return
     */
    OperationResponse openLock(User user);
}
