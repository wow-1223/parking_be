package com.parking.repository.mybatis.free;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.google.gson.reflect.TypeToken;
import com.parking.handler.ParkingIntervalChecker;
import com.parking.mapper.mybatis.ParkingOccupiedMapper;
import com.parking.mapper.mybatis.ParkingSpotMapper;
import com.parking.model.entity.mybatis.ParkingSpot;
import com.parking.model.param.parking.request.NearbyParkingSpotRequest;
import com.parking.model.vo.parking.rule.ParkingSpotRuleVO;
import com.parking.util.tool.DateUtil;
import com.parking.util.tool.JsonUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.lang.reflect.Type;
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

//        GeometryFactory geometryFactory = new GeometryFactory();
//        Coordinate coordinate = new Coordinate(request.getLongitude(), request.getLatitude());
//        Point point = geometryFactory.createPoint(coordinate);
//        point.setSRID(4326);

        // 构造WKT格式的POINT
//        String point = String.format("POINT(%f %f)", request.getLongitude(), request.getLatitude());

        List<ParkingSpot> spots = parkingSpotMapper.getAvailableParkingSpotIdList(
                request.getLongitude(), request.getLatitude(), request.getRadius(), request.getPrice());
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
        String spotIdStr = StringUtils.join(spotIds, ",");
        List<Long> occupiedSpotIds = parkingOccupiedMapper.getParkingSpotIdByTimeInterval(
                spotIdStr, DateUtil.convertToLocalDate(request.getStartTime()), start, end);

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
            Type type =  new TypeToken<List<ParkingSpotRuleVO>>(){}.getType();
            List<ParkingSpotRuleVO> rules = JsonUtil.fromJson(parkingSpot.getRules(), type);
            if (CollectionUtils.isEmpty(rules)) {
                continue;
            }

            boolean availiable = false;
            // 判断startTime和endTime是否在rule.startTime - rule.endTime中
            for (ParkingSpotRuleVO rule : rules) {
                boolean inInterval = ParkingIntervalChecker.isInInterval(startTime, endTime, rule);
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
