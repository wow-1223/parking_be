package com.parking.repository.mybatis.free;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.parking.handler.ParkingIntervalChecker;
import com.parking.mapper.mybatis.ParkingOccupiedMapper;
import com.parking.mapper.mybatis.ParkingSpotMapper;
import com.parking.model.entity.mybatis.ParkingSpot;
import com.parking.model.param.parking.request.NearbyParkingSpotRequest;
import com.parking.model.vo.parking.rule.ParkingSpotRuleVO;
import com.parking.util.tool.DateUtil;
import com.parking.util.tool.JsonUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Repository
public class FreeParkingRepository {

    @Autowired
    private ParkingSpotMapper parkingSpotMapper;

    @Autowired
    private ParkingOccupiedMapper parkingOccupiedMapper;

    public IPage<ParkingSpot> findFavoriteParkingSpots(Long userId, Integer page, Integer size) {
        return parkingSpotMapper.getFavoriteParkingSpots(new Page<>(page, size), userId);
    }

    /**
     * 查找附近可用的停车位
     */
    public IPage<ParkingSpot> findNearbyAvailableSpots(NearbyParkingSpotRequest request) {

        GeometryFactory geometryFactory = new GeometryFactory();
        Coordinate coordinate = new Coordinate(request.getLongitude(), request.getLatitude());
        Point point = geometryFactory.createPoint(coordinate);

        List<ParkingSpot> spots = parkingSpotMapper.getAvailableParkingSpotIdList(request.getLongitude(), request.getLatitude(), request.getRadius(), request.getPrice());
        if (CollectionUtils.isEmpty(spots)) {
            return new Page<>(request.getPage(), request.getSize());
        }

        LocalDateTime start = DateUtil.parseDate(request.getStartTime());
        LocalDateTime end = DateUtil.parseDate(request.getEndTime());

        List<ParkingSpot> availableSpots = filterSpotsByInterval(spots, start, end);
        if (CollectionUtils.isEmpty(availableSpots)) {
            return new Page<>(request.getPage(), request.getSize());
        }

        List<Long> spotIds = new ArrayList<>(availableSpots.stream().map(ParkingSpot::getId).toList());
        List<Long> occupiedSpotIds = parkingOccupiedMapper.getParkingSpotIdByTimeInterval(
                spotIds, start, end);

        spotIds.removeAll(occupiedSpotIds);

        List<String> selectFields = Lists.newArrayList("id", "location", "longitude", "latitude", "price");
        return parkingSpotMapper.selectPage(new Page<>(request.getPage(), request.getSize()), new QueryWrapper<ParkingSpot>().select(selectFields).in("id", spotIds));
    }

    /**
     * 筛选出可用的车位
     * @param parkingSpots parkingSpots
     * @param startTime startTime
     * @param endTime endTime
     * @return List<ParkingSpot>
     */
    private List<ParkingSpot> filterSpotsByInterval(List<ParkingSpot> parkingSpots, LocalDateTime startTime, LocalDateTime endTime) {
        if (CollectionUtils.isEmpty(parkingSpots)) {
            return Collections.emptyList();
        }

        List<ParkingSpot> availableSpots = new ArrayList<>();

        for (ParkingSpot parkingSpot : parkingSpots) {
            List<ParkingSpotRuleVO> intervals =
                    JsonUtil.fromListJson(parkingSpot.getRules(), ParkingSpotRuleVO.class);
            if (CollectionUtils.isEmpty(intervals)) {
                continue;
            }

            boolean availiable = false;
            // 判断startTime和endTime是否在intervals中
            for (ParkingSpotRuleVO intervalVO : intervals) {
                boolean inInterval = ParkingIntervalChecker.isInInterval(startTime, endTime, intervalVO);
                if (inInterval) {
                    availiable = true;
                    break;
                }
            }
            if (availiable) {
                availableSpots.add(parkingSpot);
            }
        }
        return availableSpots;
    }
}
