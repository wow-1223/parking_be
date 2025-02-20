package com.parking.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.google.common.collect.Lists;
import com.google.gson.reflect.TypeToken;
import com.parking.handler.ParkingIntervalChecker;
import com.parking.model.dto.parking.ParkingSpotDTO;
import com.parking.model.dto.parking.ParkingSpotDetailDTO;
import com.parking.model.entity.mybatis.OccupiedSpot;
import com.parking.model.entity.mybatis.ParkingSpot;
import com.parking.model.entity.mybatis.User;
import com.parking.model.param.common.DetailResponse;
import com.parking.model.param.common.PageResponse;
import com.parking.model.vo.parking.rule.ParkingSpotRuleVO;
import com.parking.util.tool.AesUtil;
import com.parking.util.tool.DateUtil;
import com.parking.util.tool.JsonUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public interface ParkSpotService {

    /**
     * 转换为列表响应
     */
    default PageResponse<ParkingSpotDTO> convertToListResponse(IPage<ParkingSpot> iPage) {
        if (iPage == null || iPage.getRecords() == null || iPage.getRecords().isEmpty()) {
            return PageResponse.pageSuccess(null, 0L);
        }

        List<ParkingSpotDTO> spots = iPage.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return PageResponse.pageSuccess(spots, iPage.getTotal());
    }

    /**
     * 转换为详情响应
     */
    default DetailResponse<ParkingSpotDetailDTO> convertToDetailResponse(
            Boolean needAvailableTime, ParkingSpot parkingSpot,
            User owner, List<OccupiedSpot> occupiedSpots,
            String startTime, String endTime) {

        Type type = new TypeToken<List<ParkingSpotRuleVO>>() {}.getType();
        List<ParkingSpotRuleVO> rules = JsonUtil.fromJson(parkingSpot.getRules(), type);

        // 计算可用时间区间
        List<ParkingSpotDetailDTO.IntervalDTO> parkingIntervals = Lists.newArrayListWithCapacity(rules.size());
        for (ParkingSpotRuleVO rule : rules) {
            if (!needAvailableTime) {
                parkingIntervals.add(new ParkingSpotDetailDTO.IntervalDTO(rule.getStartTime(), rule.getEndTime()));
            } else {
                boolean inInterval = ParkingIntervalChecker.isInInterval(
                        DateUtil.parseDate(startTime), DateUtil.parseDate(endTime), rule);
                if (inInterval) {
                    parkingIntervals.add(new ParkingSpotDetailDTO.IntervalDTO(rule.getStartTime(), rule.getEndTime()));
                }
            }
        }
        if (needAvailableTime && CollectionUtils.isEmpty(parkingIntervals)) {
            return DetailResponse.detailSuccess(null, "there are no available spots");
        }

        ParkingSpotDetailDTO detail = new ParkingSpotDetailDTO();
        detail.setId(parkingSpot.getId());
        detail.setLongitude(parkingSpot.getLongitude().doubleValue());
        detail.setLatitude(parkingSpot.getLatitude().doubleValue());
        detail.setLocation(parkingSpot.getLocation());
        detail.setDescription(parkingSpot.getDescription());
        detail.setImages(JsonUtil.fromListJson(parkingSpot.getImages(), String.class));
        detail.setPrice(parkingSpot.getPrice());
        detail.setFacilities(JsonUtil.fromListJson(parkingSpot.getFacilities(), String.class));
        detail.setParkingIntervals(parkingIntervals);

        List<ParkingSpotDetailDTO.IntervalDTO> occupiedIntervals = Lists.newArrayListWithCapacity(occupiedSpots.size());
        for (OccupiedSpot occupiedSpot : occupiedSpots) {
            occupiedIntervals.add(new ParkingSpotDetailDTO.IntervalDTO(
                    occupiedSpot.getStartTime().toLocalTime(), occupiedSpot.getEndTime().toLocalTime()));
        }
        detail.setOccupiedIntervals(occupiedIntervals);

        // 设置所有者信息
        ParkingSpotDetailDTO.OwnerDTO o = new ParkingSpotDetailDTO.OwnerDTO();
        o.setId(owner.getId().toString());
        o.setName(owner.getNickName());
//        owner.setRating(calculateOwnerRating(owner.getId()));
        detail.setOwner(o);

        return DetailResponse.detailSuccess(detail, "get detail success");
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
