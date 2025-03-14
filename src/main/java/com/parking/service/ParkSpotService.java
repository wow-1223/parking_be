package com.parking.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.google.common.collect.Lists;
import com.google.gson.reflect.TypeToken;
import com.parking.handler.encrypt.AesUtil;
import com.parking.util.business.ParkingIntervalChecker;
import com.parking.model.dto.parking.ParkingSpotDTO;
import com.parking.model.dto.parking.ParkingSpotDetailDTO;
import com.parking.model.dto.user.UserDTO;
import com.parking.model.entity.mybatis.OccupiedSpot;
import com.parking.model.entity.mybatis.ParkingSpot;
import com.parking.model.entity.mybatis.User;
import com.parking.model.param.common.DetailResponse;
import com.parking.model.param.common.PageResponse;
import com.parking.model.vo.parking.ParkingSpotRuleStrVO;
import com.parking.model.vo.parking.ParkingSpotRuleVO;
import com.parking.util.DateUtil;
import com.parking.util.JsonUtil;
import jakarta.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;

import java.lang.reflect.Type;
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
    default DetailResponse<ParkingSpotDetailDTO> convertToDetailResponse(Boolean needAvailableTime,
                                                                         User owner,
                                                                         ParkingSpot parkingSpot,
                                                                         List<OccupiedSpot> occupiedSpots,
                                                                         String startTime, String endTime) {

        Type type = new TypeToken<List<ParkingSpotRuleStrVO>>() {}.getType();
        List<ParkingSpotRuleStrVO> ruleStrList = JsonUtil.fromJson(parkingSpot.getRules(), type);
        if (ruleStrList == null || ruleStrList.isEmpty()) {
            return DetailResponse.detailSuccess(null, "there are no available spots");
        }
        List<ParkingSpotRuleVO> rules = ruleStrList.stream().map(ParkingSpotRuleVO::new).toList();
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
        detail.setPrice(parkingSpot.getPrice());
        detail.setDescription(parkingSpot.getDescription());
        detail.setImages(JsonUtil.toListString(parkingSpot.getImages()));
        detail.setFacilities(JsonUtil.toListString(parkingSpot.getFacilities()));
        detail.setParkingIntervals(parkingIntervals);

        if (CollectionUtils.isNotEmpty(occupiedSpots)) {
            List<ParkingSpotDetailDTO.IntervalDTO> occupiedIntervals = Lists.newArrayListWithCapacity(occupiedSpots.size());
            for (OccupiedSpot occupiedSpot : occupiedSpots) {
                occupiedIntervals.add(new ParkingSpotDetailDTO.IntervalDTO(
                        occupiedSpot.getStartTime().toLocalTime(), occupiedSpot.getEndTime().toLocalTime()));
            }
            detail.setOccupiedIntervals(occupiedIntervals);
        }

        // 设置所有者信息
        UserDTO owr = new UserDTO();
        owr.setId(owner.getId());
        owr.setName(owner.getNickName());
        owr.setPhone(owr.getPhone());
//        owner.setRating(calculateOwnerRating(owner.getId()));
        detail.setOwner(owr);

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
