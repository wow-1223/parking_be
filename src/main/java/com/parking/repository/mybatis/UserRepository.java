package com.parking.repository.mybatis;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import com.parking.mapper.mybatis.UserMapper;
import com.parking.model.entity.mybatis.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;

@Repository
public class UserRepository {

    @Autowired
    private UserMapper userMapper;

    /**
     * 保存用户
     */
    public void insert(User user) {
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
        User user = findById(id, Lists.newArrayList("id"));
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        user.setDeletedAt(System.currentTimeMillis());
        userMapper.updateById(user);
    }

    /**
     * 根据ID查找用户
     */
    public User findById(Long id) {
        return userMapper.selectById(id);
    }

    public User findById(Long id, List<String> fields) {
        return userMapper.selectOne(new QueryWrapper<User>().select(fields).eq("id", id).eq("deleted_at", 0));
    }

    /**
     * 根据openId查找用户
     */
    public User findByOpenId(String openId) {
        return userMapper.selectOne(new QueryWrapper<User>().eq("open_id", openId).eq("deleted_at", 0));
    }

    /**
     * 根据手机号查找用户
     */
    public User findByPhone(String phone) {
        return userMapper.selectOne(new QueryWrapper<User>().eq("phone", phone).eq("deleted_at", 0));
    }

    /**
     * 根据ID列表查找用户
     */
    public List<User> findByIds(List<Long> ids) {
        return userMapper.selectBatchIds(ids);
    }

    /**
     * 根据ID列表查找用户
     */
    public List<User> findByIds(List<Long> ids, List<String> fields) {
        QueryWrapper<User> query = new QueryWrapper<>();
        query.select(fields).in("id", ids);
        query.eq("deleted_at", 0);
        return userMapper.selectList(query);
    }
}
