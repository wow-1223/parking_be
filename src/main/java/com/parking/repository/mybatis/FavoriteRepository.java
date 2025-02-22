package com.parking.repository.mybatis;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.parking.mapper.mybatis.FavoriteMapper;
import com.parking.model.entity.mybatis.Favorite;
import com.parking.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

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
    public void delete(Favorite favorite) {
        favorite.setDeletedAt(DateUtil.getCurrentTimestamp());
        update(favorite);
    }

    // 查询收藏
    public Favorite findById(Long id, List<String> fields) {
        return favoriteMapper.selectOne(new QueryWrapper<Favorite>().eq("id", id).eq("deleted_at", 0L).select(fields));
    }

    public Favorite exist(Long id, Long userId, Long spotId) {
        return exist(id, userId, spotId, false);
    }

    public Favorite exist(Long id, Long userId, Long spotId, Boolean excludeDeleted) {
        QueryWrapper<Favorite> query = new QueryWrapper<>();
        if (id != null) {
            query.eq("id", id);
        } else if (userId != null && spotId != null) {
            query.eq("user_id", userId).eq("parking_spot_id", spotId);
        } else {
            return null;
        }
        if (excludeDeleted) {
            query.eq("deleted_at", 0L);
        }
        query.select("id", "user_id", "parking_spot_id", "deleted_at");
        return favoriteMapper.selectOne(query);
    }

    // 查询用户的所有收藏
    public IPage<Favorite> findByUserId(Long userId, Integer page, Integer size) {
        return favoriteMapper.selectPage(new Page<>(page, size),
                new QueryWrapper<Favorite>().eq("user_id", userId));
    }
}
