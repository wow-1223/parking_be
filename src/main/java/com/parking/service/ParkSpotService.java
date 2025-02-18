package com.parking.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.google.common.collect.Lists;
import com.parking.handler.ParkingIntervalChecker;
import com.parking.model.dto.parking.ParkingSpotDTO;
import com.parking.model.dto.parking.ParkingSpotDetailDTO;
import com.parking.model.entity.mybatis.OccupiedSpot;
import com.parking.model.entity.mybatis.ParkingSpot;
import com.parking.model.entity.mybatis.User;
import com.parking.model.param.common.DetailResponse;
import com.parking.model.param.common.PageResponse;
import com.parking.model.vo.parking.rule.ParkingSpotRuleVO;
import com.parking.util.tool.DateUtil;
import com.parking.util.tool.JsonUtil;

import java.util.List;
import java.util.stream.Collectors;

public interface ParkSpotService {

    default PageResponse<ParkingSpotDTO> convertToListResponse(IPage<ParkingSpot> iPage) {
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

    /**
     * 转换为详情响应
     */
    default DetailResponse<ParkingSpotDetailDTO> convertToDetailResponse(
            ParkingSpot parkingSpot, User owner, List<OccupiedSpot> occupiedSpots, String startTime, String endTime) {
        DetailResponse<ParkingSpotDetailDTO> response = new DetailResponse<>();
        response.setCode("200");
        response.setMessage("success");

        ParkingSpotDetailDTO detail = new ParkingSpotDetailDTO();
        detail.setId(parkingSpot.getId());
        detail.setLocation(parkingSpot.getLocation());
        detail.setDescription(parkingSpot.getDescription());
        detail.setImages(JsonUtil.fromListJson(parkingSpot.getImages(), String.class));
        detail.setPrice(parkingSpot.getPrice());
        detail.setFacilities(JsonUtil.fromListJson(parkingSpot.getFacilities(), String.class));

        List<ParkingSpotRuleVO> intervals =
                JsonUtil.fromListJson(parkingSpot.getRules(), ParkingSpotRuleVO.class);

        List<ParkingSpotDetailDTO.IntervalDTO> parkingIntervals = Lists.newArrayListWithCapacity(intervals.size());
        for (ParkingSpotRuleVO interval : intervals) {
            boolean inInterval = ParkingIntervalChecker.isInInterval(
                    DateUtil.parseDate(startTime), DateUtil.parseDate(endTime), interval);
            if (inInterval) {
                parkingIntervals.add(new ParkingSpotDetailDTO.IntervalDTO(interval.getStartTime(), interval.getEndTime()));
            }
        }
        detail.setParkingIntervals(parkingIntervals);

        List<ParkingSpotDetailDTO.IntervalDTO> occupiedIntervals = Lists.newArrayListWithCapacity(occupiedSpots.size());
        for (OccupiedSpot occupiedSpot : occupiedSpots) {
            occupiedIntervals.add(new ParkingSpotDetailDTO.IntervalDTO(
                    occupiedSpot.getStartTime().toLocalTime(), occupiedSpot.getEndTime().toLocalTime()));
        }
        detail.setOccupiedIntervals(occupiedIntervals);

        response.setData(detail);

        // 设置所有者信息
        ParkingSpotDetailDTO.OwnerDTO o = new ParkingSpotDetailDTO.OwnerDTO();
        o.setId(owner.getId().toString());
        o.setName(owner.getNickName());
        o.setPhone(owner.getPhone());
//        owner.setRating(calculateOwnerRating(owner.getId()));
        detail.setOwner(o);


        return response;
    }

    /**
     * 转换为列表DTO
     */
    default ParkingSpotDTO convertToDTO(ParkingSpot parkingSpot) {
        ParkingSpotDTO dto = new ParkingSpotDTO();
        dto.setId(parkingSpot.getId());
        dto.setLocation(parkingSpot.getLocation());
        dto.setLatitude(parkingSpot.getLatitude().doubleValue());
        dto.setLongitude(parkingSpot.getLongitude().doubleValue());
        dto.setPrice(parkingSpot.getPrice());
        if (parkingSpot.getImages() != null) {
            dto.setImages(JsonUtil.fromListJson(parkingSpot.getImages(), String.class));
        }
        if (parkingSpot.getFacilities() != null) {
            dto.setFacilities(JsonUtil.fromListJson(parkingSpot.getFacilities(), String.class));
        }
        if (parkingSpot.getRules() != null) {
            dto.setRules(JsonUtil.fromListJson(parkingSpot.getRules(), ParkingSpotRuleVO.class));
        }

        return dto;
    }

    /**
     * 计算车位所有者评分
     */
    default Double calculateOwnerRating(Long ownerId) {
        // TODO: 实现评分计算逻辑
        return 4.5;
    }

}
