package com.parking.mapper.mybatis;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.parking.model.entity.mybatis.Favorite;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FavoriteMapper extends BaseMapper<Favorite> {
}