package com.parking.mapper.mybatis;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.parking.model.entity.mybatis.User;
import com.parking.model.vo.UserOrderVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserMapper extends BaseMapper<User> {

//    List<User> getAllUsers();
//
//    User getUserById(Long id);
//
//    int insertUser(User user);
//
//    int updateUser(User user);
//
//    int deleteUser(Long id);

//    /**
//     * 连表分页查询用户订单信息
//     * @param page 分页参数
//     * @return 分页后的用户订单信息列表
//     */
//    @Select("SELECT u.id AS userId, u.name AS userName, o.id AS orderId, o.amount AS orderAmount " +
//            "FROM users u JOIN orders o ON u.id = o.userId")
//    IPage<UserOrderVo> selectUserOrderPage(Page<UserOrderVo> page);

//    @Select("SELECT u.id AS userId, u.open_id AS openId, u.phone, u.nick_name AS nickName, u.avatar_url AS avatarUrl, " +
//            "u.role, u.status, u.create_time AS createTime, u.update_time AS updateTime, u.deleted_at AS deletedAt, " +
//            "o.id AS orderId, o.parking_spots_id AS parkingSpotsId, o.parking_period_id AS parkingPeriodId, " +
//            "o.amount, o.status AS orderStatus, o.payment_id AS paymentId, o.create_time AS orderCreateTime, " +
//            "o.update_time AS orderUpdateTime, o.deleted_at AS orderDeletedAt " +
//            "FROM users u " +
//            "JOIN orders o ON u.id = o.user_id")
//    List<UserOrderVo> getUserWithOrders();

//    @Select("SELECT u.id AS userId, u.open_id AS openId, u.phone, u.nick_name AS nickName, u.avatar_url AS avatarUrl, " +
//            "u.role, u.status, u.create_time AS createTime, u.update_time AS updateTime, u.deleted_at AS deletedAt, " +
//            "o.id AS orderId, o.parking_spots_id AS parkingSpotsId, o.parking_period_id AS parkingPeriodId, " +
//            "o.amount, o.status AS orderStatus, o.payment_id AS paymentId, o.create_time AS orderCreateTime, " +
//            "o.update_time AS orderUpdateTime, o.deleted_at AS orderDeletedAt " +
//            "FROM users u " +
//            "JOIN orders o ON u.id = o.user_id " +
//            "WHERE u.role = #{role} AND o.amount > #{amount}")
//    List<UserOrderVo> getUserWithOrders(@Param("role") Byte role, @Param("amount") BigDecimal amount);

}