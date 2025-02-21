package com.parking.service.user.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
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
import com.parking.repository.mybatis.OccupiedSpotRepository;
import com.parking.repository.mybatis.ParkingSpotRepository;
import com.parking.repository.mybatis.UserRepository;
import com.parking.repository.mybatis.free.FreeParkingRepository;
import com.parking.service.user.UserParkingService;
import com.parking.util.tool.AesUtil;
import com.parking.util.tool.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    private FreeParkingRepository freeParkingRepository;

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
        IPage<ParkingSpot> page = freeParkingRepository.findNearbyAvailableSpots(request);
        return convertToListResponse(page);
    }

    @Override
    public DetailResponse<ParkingSpotDetailDTO> getParkingDetail(ParkingSpotDetailRequest request) {
        // 获取停车位详情
        ParkingSpot spot = parkingSpotRepository.findById(request.getId());
        if (spot == null) {
            throw new BusinessException("Parking spot not found");
        }
        User owner = userRepository.findById(spot.getOwnerId());
        if (owner == null) {
            throw new BusinessException("Parking spot owner not found");
        }
        List<OccupiedSpot> occupiedSpots = occupiedSpotRepository.findByDay(
                spot.getId(), DateUtil.convertToLocalDate(request.getStartTime()));

        DetailResponse<ParkingSpotDetailDTO> detail = convertToDetailResponse(false,
                spot, owner, occupiedSpots, request.getStartTime(), request.getStartTime());
        if (detail != null && detail.getData() != null && detail.getData().getOwner()!= null) {
            // todo for test
            detail.getData().getOwner().setPhone(aesUtil.decrypt("Uu3+EuYVaOf4/w7QhxGfiA=="));
//            detail.getData().getOwner().setPhone(aesUtil.decrypt(owner.getPhone()));
        }
        return detail;
    }

    @Override
    public PageResponse<ParkingSpotDTO> getFavorites(Long userId, Integer page, Integer size) {
        IPage<ParkingSpot> iPage = freeParkingRepository.findFavoriteParkingSpots(userId, page, size);
        return convertToListResponse(iPage);
    }
}