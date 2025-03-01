package com.parking.service.owner.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.google.common.collect.Lists;
import com.parking.enums.parking.SpotStatusEnum;
import com.parking.enums.user.UserRoleEnum;
import com.parking.exception.BusinessException;
import com.parking.exception.ResourceNotFoundException;
import com.parking.model.dto.parking.ParkingSpotDTO;
import com.parking.model.dto.parking.ParkingSpotDetailDTO;
import com.parking.model.entity.mybatis.ParkingSpot;
import com.parking.model.entity.mybatis.User;
import com.parking.model.param.common.DetailResponse;
import com.parking.model.param.common.OperationResponse;
import com.parking.model.param.common.PageResponse;
import com.parking.model.param.owner.request.DeleteParkingRequest;
import com.parking.model.param.owner.request.OwnerParkingRequest;
import com.parking.model.param.owner.request.UpdateParkingRequest;
import com.parking.model.vo.parking.ParkingSpotRuleStrVO;
import com.parking.model.vo.parking.ParkingSpotRuleVO;
import com.parking.repository.mybatis.ParkingSpotRepository;
import com.parking.repository.mybatis.UserRepository;
import com.parking.service.owner.OwnerParkingService;
import com.parking.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Objects;

@Service
public class OwnerParkingServiceImpl implements OwnerParkingService {

    @Autowired
    private ParkingSpotRepository parkingSpotRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public PageResponse<ParkingSpotDTO> getParkingList(Long userId, Integer status, Integer page, Integer size) {
        if (Objects.isNull(userId)) {
            throw new BusinessException("UserId is required");
        }
        // 查询车位列表
        IPage<ParkingSpot> pages = parkingSpotRepository.findByOwnerAndStatus(userId, status, page, size);
        return convertToListResponse(pages);
    }

    @Override
    public DetailResponse<ParkingSpotDetailDTO> getParkingDetail(Long userId, Long parkingId) {
        ParkingSpot spot = parkingSpotRepository.findById(parkingId);
        if (spot == null) {
            throw new ResourceNotFoundException("Parking spot not found");
        }
        User owner = userRepository.findById(userId);
        if (owner == null) {
            throw new ResourceNotFoundException("User not found");
        }
        return convertToDetailResponse(false, owner, spot, null, null, null);
    }

    @Override
    @Transactional
    public OperationResponse createParking(OwnerParkingRequest request) {
        // 校验参数
        checkParkingSpot(request);

        checkDuplicate(request);

        // 创建车位
        ParkingSpot parkingSpot = new ParkingSpot();
        parkingSpot.setOwnerId(request.getUserId());
        parkingSpot.setLocation(request.getLocation());
        parkingSpot.setLatitude(BigDecimal.valueOf(request.getLatitude()));
        parkingSpot.setLongitude(BigDecimal.valueOf(request.getLongitude()));
        parkingSpot.setDescription(request.getDescription());
        parkingSpot.setPrice(request.getPrice());
        parkingSpot.setRules(JsonUtil.toJson(request.getRules()));
        if (request.getImages() != null) {
            parkingSpot.setImages(JsonUtil.toJson(request.getImages()));
        }
        if (request.getFacilities()!= null) {
            parkingSpot.setFacilities(JsonUtil.toJson(request.getFacilities()));
        }
        // 默认待审核状态
        parkingSpot.setStatus(SpotStatusEnum.APPROVING.getStatus());

        parkingSpotRepository.insert(parkingSpot);

        return OperationResponse.operationSuccess(parkingSpot.getId(), "create success");
    }

    @Override
    @Transactional
    public OperationResponse updateParking(UpdateParkingRequest request) {
        // 获取并验证车位
        ParkingSpot spot = checkParkingSpotForUpd(request);
        
        // 更新信息
        if (request.getLocation() != null) {
            spot.setLocation(request.getLocation());
        }
        if (request.getDescription() != null) {
            spot.setDescription(request.getDescription());
        }
        if (request.getPrice() != null) {
            spot.setPrice(request.getPrice());
        }
        if (request.getImages() != null) {
            spot.setImages(JsonUtil.toJson(request.getImages()));
        }
        if (request.getRules() != null) {
            spot.setRules(JsonUtil.toJson(request.getRules()));
        }
        if (request.getFacilities() != null) {
            spot.setFacilities(JsonUtil.toJson(request.getFacilities()));
        }
        if (request.getStatus() != null) {
            spot.setStatus(request.getStatus());
        }
        
        parkingSpotRepository.update(spot);
        
        return OperationResponse.operationSuccess(spot.getId(), "update success");
    }

    @Override
    public OperationResponse deleteParking(DeleteParkingRequest request) {
        if (request.getUserId() == null || request.getParkingSpotId() == null) {
            throw new ResourceNotFoundException("UserId and Parking spot id is required");
        }
        ParkingSpot spot = parkingSpotRepository.findById(request.getParkingSpotId(), Lists.newArrayList("id", "owner_id"));
        if (spot == null) {
            throw new ResourceNotFoundException("Parking spot not found");
        }
        if (!Objects.equals(spot.getOwnerId(), request.getUserId())) {
            throw new BusinessException("Parking spot owner id mismatch");
        }
        parkingSpotRepository.delete(request.getParkingSpotId());
        return OperationResponse.operationSuccess(request.getParkingSpotId(), "delete success");
    }

    public ParkingSpot checkParkingSpotForUpd(UpdateParkingRequest request) {
        if (request.getId() == null) {
            throw new ResourceNotFoundException("Parking spot id is required");
        }

        if (request.getRules() != null) {
            checkRules(request);
        }

        // 获取并验证车位
        ParkingSpot spot = parkingSpotRepository.findById(
                request.getId(), Lists.newArrayList("id", "owner_id"));
        if (spot == null) {
            throw new ResourceNotFoundException("Parking spot not found");
        }

        User user = checkOwner(request);
        if (user.getRole() == UserRoleEnum.ADMIN.getRole()) {
            return spot;
        }
        if (!Objects.equals(spot.getStatus(), request.getStatus())) {
            throw new BusinessException("User is not allowed to change status");
        }

        if (!spot.getOwnerId().equals(request.getUserId())) {
            throw new BusinessException("Parking spot owner id mismatch");
        }

        return spot;
    }

    public void checkDuplicate(OwnerParkingRequest request) {

    }

    public void checkParkingSpot(OwnerParkingRequest request) {

        checkOwner(request);

        if (request.getLocation() == null) {
            throw new ResourceNotFoundException("Location is required");
        }

        if (request.getLatitude() == null) {
            throw new ResourceNotFoundException("Latitude is required");
        }

        if (request.getLongitude() == null) {
            throw new ResourceNotFoundException("Longitude is required");
        }

//        if (request.getDescription() == null) {
//            throw new ResourceNotFoundException("Description not found");
//        }

        if (request.getPrice() == null) {
            throw new ResourceNotFoundException("Price is required");
        }

//        if (request.getImages() == null) {
//            throw new ResourceNotFoundException("Images not found");
//        }

        if (request.getRules() == null) {
            throw new ResourceNotFoundException("Rules are required");
        }

        checkRules(request);
    }

    public User checkOwner(OwnerParkingRequest request) {
        if (request.getUserId() == null) {
            throw new ResourceNotFoundException("Owner is required");
        }
        User user = userRepository.findById(request.getUserId(), Lists.newArrayList("id", "role"));
        if (user == null) {
            throw new ResourceNotFoundException("Owner not found");
        }
        if (user.getRole() != UserRoleEnum.OWNER.getRole() &&
                user.getRole() != UserRoleEnum.ADMIN.getRole()) {
            throw new BusinessException("User is not an owner or admin, cannot create parking spots");
        }
        return user;
    }

    public void checkRules(OwnerParkingRequest request) {
        int mode = -1;
        for (ParkingSpotRuleStrVO ruleStr : request.getRules()) {
            if (ruleStr == null) {
                throw new ResourceNotFoundException("Invalid Rule item");
            }
//            Type type = new TypeToken<ParkingSpotRuleVO>() {}.getType();
            ParkingSpotRuleVO rule = new ParkingSpotRuleVO(ruleStr);

            if (mode == -1) {
                mode = rule.getMode().getMode();
            }
            if (mode != rule.getMode().getMode()) {
                throw new BusinessException("One parking spot can't have the different mode");
            }
        }
    }

}