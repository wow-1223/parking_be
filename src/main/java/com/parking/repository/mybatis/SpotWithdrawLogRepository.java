package com.parking.repository.mybatis;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.parking.enums.parking.SpotWithdrawStatus;
import com.parking.mapper.mybatis.SpotWithdrawLogMapper;
import com.parking.model.entity.mybatis.SpotWithdrawLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SpotWithdrawLogRepository {

    @Autowired
    private SpotWithdrawLogMapper spotWithdrawLogMapper;

    public int insert(SpotWithdrawLog spotWithdrawLog) {
        return spotWithdrawLogMapper.insert(spotWithdrawLog);
    }

    public SpotWithdrawLog findByOwner(Long ownerId) {
        QueryWrapper<SpotWithdrawLog> query = new QueryWrapper<>();
        query.eq("owner_id", ownerId);
        query.eq("status", SpotWithdrawStatus.SUCCESS.getStatus());
        query.eq("delete_at", 0);
        return spotWithdrawLogMapper.selectOne(query);
    }

    public List<SpotWithdrawLog> findByParkingSpotId(Long parkingSpotId) {
        QueryWrapper<SpotWithdrawLog> query = new QueryWrapper<>();
        query.eq("parking_spot_id", parkingSpotId);
        query.eq("status", SpotWithdrawStatus.SUCCESS.getStatus());
        query.eq("delete_at", 0);
        return spotWithdrawLogMapper.selectList(query);
    }
    
}
