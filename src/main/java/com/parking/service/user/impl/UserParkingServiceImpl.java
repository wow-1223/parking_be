package com.parking.service.user.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.google.gson.reflect.TypeToken;
import com.parking.exception.BusinessException;
import com.parking.model.dto.parking.ParkingSpotDTO;
import com.parking.model.dto.parking.ParkingSpotDetailDTO;
import com.parking.model.entity.mybatis.OccupiedSpot;
import com.parking.model.entity.mybatis.User;
import com.parking.model.param.common.DetailResponse;
import com.parking.model.param.common.PageResponse;
import com.parking.model.param.parking.request.NearbyParkingSpotRequest;
import com.parking.model.entity.mybatis.ParkingSpot;
import com.parking.model.param.parking.request.ParkingSpotDetailRequest;
import com.parking.model.vo.parking.ParkingSpotRuleStrVO;
import com.parking.model.vo.parking.ParkingSpotRuleVO;
import com.parking.repository.mybatis.OccupiedSpotRepository;
import com.parking.repository.mybatis.ParkingSpotRepository;
import com.parking.repository.mybatis.UserRepository;
import com.parking.service.user.UserParkingService;
import com.parking.handler.encrypt.AesUtil;
import com.parking.util.DateUtil;
import com.parking.util.JsonUtil;
import com.parking.util.business.ParkingIntervalChecker;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * 用户停车位服务实现
 */
@Slf4j
@Service
public class UserParkingServiceImpl implements UserParkingService {

    private static final Integer DEFAULT_START_TIME = 3000;

    @Autowired
    private ParkingSpotRepository parkingSpotRepository;

    @Autowired
    private OccupiedSpotRepository occupiedSpotRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AesUtil aesUtil;

    @Override
    public PageResponse<ParkingSpotDTO> getNearbyParkings(NearbyParkingSpotRequest request) {
        // 查询附近可用的停车位
        if (Objects.isNull(request.getRadius())) {
            request.setRadius(DEFAULT_START_TIME);
        }
        IPage<ParkingSpot> page = findNearbyAvailableSpots(request);
        return convertToListResponse(page);
    }

    @Override
    public DetailResponse<ParkingSpotDetailDTO> getParkingDetail(ParkingSpotDetailRequest request) {
        // 获取停车位详情
        ParkingSpot spot = parkingSpotRepository.findById(request.getId());
        if (spot == null) {
            throw new BusinessException("Parking spot not found");
        }

        User owner = userRepository.findById(spot.getOwnerId(), Lists.newArrayList("id", "nick_name", "phone"));
        if (owner == null) {
            throw new BusinessException("Parking spot owner not found");
        }

        List<OccupiedSpot> occupiedSpots = occupiedSpotRepository.findByDay(
                spot.getId(), DateUtil.convertToLocalDate(request.getStartTime()));

        DetailResponse<ParkingSpotDetailDTO> detail = convertToDetailResponse(false,
                owner, spot, occupiedSpots, request.getStartTime(), request.getStartTime());
        if (detail != null && detail.getData() != null && detail.getData().getOwner()!= null) {
            // todo for test
            detail.getData().getOwner().setPhone(aesUtil.decrypt("Uu3+EuYVaOf4/w7QhxGfiA=="));
//            detail.getData().getOwner().setPhone(aesUtil.decrypt(owner.getPhone()));
        }
        return detail;
    }

    @Override
    public PageResponse<ParkingSpotDTO> getFavorites(Long userId, Integer page, Integer size) {
        IPage<ParkingSpot> iPage = parkingSpotRepository.findFavoriteParkingSpots(userId, page, size);
        return convertToListResponse(iPage);
    }

    /**
     * 查找附近可用的停车位
     */
    public IPage<ParkingSpot> findNearbyAvailableSpots(NearbyParkingSpotRequest request) {
        List<ParkingSpot> spots = parkingSpotRepository.findAvailableParkingSpotIdList(
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
        List<Long> occupiedSpotIds = occupiedSpotRepository.findParkingSpotIdByTimeInterval(
                spotIdStr, DateUtil.convertToLocalDate(request.getStartTime()), start, end);

        spotIds.removeAll(occupiedSpotIds);

        List<String> selectFields = Lists.newArrayList("id", "location", "longitude", "latitude", "price");
        return parkingSpotRepository.findByPage(spotIds, selectFields, request.getPage(), request.getSize());
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
            Type type =  new TypeToken<List<ParkingSpotRuleStrVO>>(){}.getType();
            List<ParkingSpotRuleStrVO> ruleStrList = JsonUtil.fromJson(parkingSpot.getRules(), type);
            if (CollectionUtils.isEmpty(ruleStrList)) {
                continue;
            }
            List<ParkingSpotRuleVO> rules = ruleStrList.stream().map(ParkingSpotRuleVO::new).toList();

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