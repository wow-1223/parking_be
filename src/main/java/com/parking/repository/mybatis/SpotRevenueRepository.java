package com.parking.repository.mybatis;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.parking.mapper.mybatis.SpotRevenueMapper;
import com.parking.model.entity.mybatis.SpotRevenue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SpotRevenueRepository {

    @Autowired
    private SpotRevenueMapper spotRevenueMapper;

    public void batchInsert(List<SpotRevenue> spotRevenues) {
        spotRevenueMapper.insert(spotRevenues);
    }

    public SpotRevenue findByParkingDay(String parkingDay) {
        QueryWrapper<SpotRevenue> query = new QueryWrapper<>();
        query.eq("parking_day", parkingDay);
        query.eq("delete_at", 0);
        return spotRevenueMapper.selectOne(query);
    }

}
