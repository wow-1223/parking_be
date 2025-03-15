package com.parking.model.entity.mybatis;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * 停车位提现记录实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("parking_spot_withdraw_log")
public class SpotWithdrawLog {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 停车位ID */
    private Long parkingSpotId;

    /** 所有者ID */
    private Long ownerId;

    /** 提现金额 */
    private BigDecimal amount;

    /** 处理状态 */
    private String status;

    /** 错误信息 */
    private String errorMsg;

    /** 创建时间 */
    private Timestamp createTime;

    /** 更新时间 */
    private Timestamp updateTime;

    /** 删除标记 */
    private Long deletedAt;
}