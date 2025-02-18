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
import com.parking.repository.mybatis.OccupiedSpotRepository;
import com.parking.repository.mybatis.ParkingSpotRepository;
import com.parking.repository.mybatis.UserRepository;
import com.parking.repository.mybatis.free.FreeParkingRepository;
import com.parking.service.user.UserParkingService;
import com.parking.util.tool.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户停车位服务实现
 */
@Slf4j
@Service
public class UserParkingServiceImpl implements UserParkingService {

    @Autowired
    private ParkingSpotRepository parkingSpotRepository;

    @Autowired
    private OccupiedSpotRepository occupiedSpotRepository;

    @Autowired
    private FreeParkingRepository freeParkingRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public PageResponse<ParkingSpotDTO> getNearbyParkings(NearbyParkingSpotRequest request) {
        // 查询附近可用的停车位
        IPage<ParkingSpot> page = freeParkingRepository.findNearbyAvailableSpots(request);
        return convertToListResponse(page);
    }

    @Override
    public DetailResponse<ParkingSpotDetailDTO> getParkingDetail(String id, String startTime, String endTime) {
        // 获取停车位详情
        ParkingSpot parkingSpot = parkingSpotRepository.findById(Long.parseLong(id));
        if (parkingSpot == null) {
            throw new BusinessException("Parking spot not found");
        }
        User owner = userRepository.findById(parkingSpot.getOwnerId());
        if (owner == null) {
            throw new BusinessException("Parking spot owner not found");
        }
        List<OccupiedSpot> occupiedSpots = occupiedSpotRepository.findByDay(
                parkingSpot.getId(), DateUtil.convertToDate(startTime));

        return convertToDetailResponse(parkingSpot, owner, occupiedSpots, startTime, endTime);
    }

    @Override
    public PageResponse<ParkingSpotDTO> getFavorites(Long userId, Integer page, Integer size) {
        IPage<ParkingSpot> iPage = freeParkingRepository.findFavoriteParkingSpots(userId, page, size);
        PageResponse<ParkingSpotDTO> response = new PageResponse<>();
        if (iPage == null || iPage.getRecords() == null || iPage.getRecords().isEmpty()) {
            response.setTotal(0L);
            return response;
        }

        List<ParkingSpotDTO> spots = iPage.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        response.setTotal(iPage.getTotal());
        response.setList(spots);
        return response;
    }
}