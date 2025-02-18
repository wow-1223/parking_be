package com.parking.repository.mybatis;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.parking.mapper.mybatis.UserMapper;
import com.parking.model.entity.mybatis.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

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
        User user = findById(id);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        user.setDeletedAt(System.currentTimeMillis());
        userMapper.updateUser(user);
    }

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

}
