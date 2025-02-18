package com.parking.repository.mybatis;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.parking.mapper.mybatis.FavoriteMapper;
import com.parking.model.entity.mybatis.Favorite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class FavoriteRepository {

    @Autowired
    private FavoriteMapper favoriteMapper;


    // 新增收藏
    public void insert(Favorite favorite) {
        // 执行数据库操作
        favoriteMapper.insert(favorite);
    }

    // 修改收藏
    public void update(Favorite favorite) {
        favoriteMapper.updateById(favorite);
    }

    // 删除收藏
    public void delete(Long id) {
        favoriteMapper.deleteById(id);
    }

    // 查询收藏
    public Favorite findById(Long id) {
        return favoriteMapper.selectById(id);
    }

    // 查询用户的所有收藏
    public IPage<Favorite> findByUserId(Long userId, Integer page, Integer size) {
        return favoriteMapper.selectPage(new Page<>(page, size),
                new QueryWrapper<Favorite>().eq("user_id", userId));
    }
}
