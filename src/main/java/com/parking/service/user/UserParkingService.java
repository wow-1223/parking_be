package com.parking.service.user;

import com.parking.model.dto.user.response.ParkingListResponse;
import com.parking.model.dto.user.response.ParkingDetailResponse;

public interface UserParkingService {
    ParkingListResponse getNearbyParkings(Double latitude, Double longitude, 
            Integer radius, Integer page, Integer pageSize);
    
    ParkingListResponse searchParkings(String keyword, Integer page, Integer pageSize);
    
    ParkingDetailResponse getParkingDetail(String id);
} 