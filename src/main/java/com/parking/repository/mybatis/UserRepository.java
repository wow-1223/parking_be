package com.parking.repository.mybatis;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.parking.mapper.UserMapper;
import com.parking.model.entity.mybatis.User;
import com.parking.model.vo.UserOrderVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public class UserRepository {

    @Autowired
    private UserMapper userMapper;

    /**
     * 根据ID查找用户
     */
    public User findById(Long id) {
        return userMapper.selectById(id);
    }

    /**
     * 根据openId查找用户
     */
    public User findByOpenId(String openId) {
        return userMapper.selectOne(new QueryWrapper<User>().eq("open_id", openId));
    }

    /**
     * 查找所有用户
     */
    public List<User> findAll() {
        return userMapper.selectList(null);
    }

    /**
     * 根据手机号查找用户
     */
    public User findByPhone(String phone) {
        return userMapper.selectOne(new QueryWrapper<User>().eq("phone", phone));
    }

    /**
     * 查询所有活跃用户
     * @return 活跃用户列表
     */
    public List<User> findAllActiveUsers() {
        return userMapper.selectList(new QueryWrapper<User>().eq("status", 1));
    }

    /**
     * 分页查询用户
     */
    public IPage<User> findByPage(int pageNum, int pageSize) {
        Page<User> page = new Page<>(pageNum, pageSize);
        return userMapper.selectPage(page, null);
    }

    /**
     * 连表分页查询用户订单信息
     * @param page 分页参数
     * @return 分页后的用户订单信息列表
     */
    public IPage<UserOrderVo> selectUserOrderPage(Page<UserOrderVo> page) {
        return userMapper.selectUserOrderPage(page);
    }

    /**
     * 连表分页查询用户订单信息
     * @param page 分页参数
     * @return 分页后的用户订单信息列表
     */
    public List<UserOrderVo> getUserOrdersByPage(Page<UserOrderVo> page) {
        return userMapper.getUserWithOrders();
    }

    public IPage<UserOrderVo> getUserOrdersByPage(int pageNum, int pageSize) {
        Page<UserOrderVo> page = new Page<>(pageNum, pageSize);
        return userMapper.selectUserOrderPage(page);
    }

    public List<UserOrderVo> getUserWithOrders(int role, BigDecimal amount) {
        QueryWrapper<User> query = new QueryWrapper<>();
        query.eq("u.role", role);
        query.gt("o.amount", amount);
        query.select("u.id AS userId", "u.open_id AS openId", "u.phone", "u.nick_name AS nickName", "u.avatar_url AS avatarUrl",
                "u.role", "u.status", "u.create_time AS createTime", "u.update_time AS updateTime", "u.deleted_at AS deletedAt",
                "o.id AS orderId", "o.parking_spots_id AS parkingSpotsId", "o.parking_period_id AS parkingPeriodId",
                "o.amount", "o.status AS orderStatus", "o.payment_id AS paymentId", "o.create_time AS orderCreateTime",
                "o.update_time AS orderUpdateTime", "o.deleted_at AS orderDeletedAt");


        List<User> users = userMapper.selectList(query);
        Long count = userMapper.selectCount(query);
        User user = userMapper.selectOne(query);

        Page<User> page = new Page<>(1, 20);
        Page<User> usersByPage = userMapper.selectPage(page, query);

        // 这里需要自定义 SQL 或者使用 MyBatis-Plus 的多表查询插件来实现
        // 简单示例，假设这里调用的是自定义的 SQL 方法
//        return userMapper.getUserWithOrders(role, amount);
        return null;
    }

    /**
     * 检查手机号是否已存在
     */
    public boolean existsByPhone(String phone) {
        return userMapper.exists(new QueryWrapper<User>().eq("phone", phone));
    }

    /**
     * 检查openId是否已存在
     */
    public boolean existsByOpenId(String openId) {
        return userMapper.exists(new QueryWrapper<User>().eq("open_id", openId));
    }

    /**
     * 查找指定角色的用户
     */
    public List<User> findByRole(String role) {
        return userMapper.selectList(new QueryWrapper<User>().eq("role", role));
    }

    /**
     * 查找指定状态的用户
     */
    public List<User> findByStatus(String status) {
        return userMapper.selectList(new QueryWrapper<User>().eq("status", status));
    }

    /**
     * 搜索用户
     */
    public List<User> search(String keyword) {
        return userMapper.selectList(new QueryWrapper<User>().like("phone", keyword).or().like("nick_name", keyword));
    }

    /**
     * 保存用户
     */
    public void save(User user) {
        userMapper.insert(user);
    }

    /**
     * 更新用户
     */
    public void update(User user) {
        userMapper.updateById(user);
    }

    /**
     * 删除用户
     */
    public void delete(Long id) {
        User user = findById(id);
        if (user != null) {
            // 获取当前时间戳
            user.setDeletedAt(System.currentTimeMillis());
            userMapper.updateUser(user);
        }
    }

}
