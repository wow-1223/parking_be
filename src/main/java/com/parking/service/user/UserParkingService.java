package com.parking.service.user;

import com.parking.model.dto.parking.request.NearbyParkingSpotRequest;
import com.parking.model.dto.user.response.ParkingListResponse;
import com.parking.model.dto.user.response.ParkingDetailResponse;

public interface UserParkingService {
    ParkingListResponse getNearbyParkings(NearbyParkingSpotRequest request);
    
    ParkingListResponse searchParkings(String keyword, Integer page, Integer pageSize);
    
    ParkingDetailResponse getParkingDetail(String id);
} 