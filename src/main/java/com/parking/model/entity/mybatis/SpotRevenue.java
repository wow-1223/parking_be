package com.parking.model.entity.mybatis;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;

/**
 * 停车位收益实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("parking_spot_revenue")
public class SpotRevenue {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 停车位ID */
    private Long parkingSpotId;

    /** 所有者ID */
    private Long ownerId;

    /** 收益日期 */
    private LocalDate parkingDay;

    /** 总收益 */
    private BigDecimal revenue;

    /** 平台收益 */
    private BigDecimal platformRevenue;

    /** 创建时间 */
    private Timestamp createTime;

    /** 更新时间 */
    private Timestamp updateTime;

    /** 删除标记 */
    private Long deletedAt;
}
