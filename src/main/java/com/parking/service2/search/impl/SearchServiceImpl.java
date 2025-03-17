package com.parking.service2.search.impl;

import com.parking.model.dto.parking.ParkingSpotDTO;
import com.parking.model.dto.parking.ParkingSpotDetailDTO;
import com.parking.model.param.common.DetailResponse;
import com.parking.model.param.common.PageResponse;
import com.parking.model.param.parking.request.NearbyParkingSpotRequest;
import com.parking.model.param.parking.request.ParkingSpotDetailRequest;
import com.parking.service2.search.SearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SearchServiceImpl implements SearchService {


    @Override
    public PageResponse<ParkingSpotDTO> getNearbyParkings(NearbyParkingSpotRequest request) {
        return null;
    }

    @Override
    public DetailResponse<ParkingSpotDetailDTO> getParkingDetail(ParkingSpotDetailRequest request) {
        return null;
    }

    @Override
    public PageResponse<ParkingSpotDTO> getFavorites(Long userId, Integer page, Integer size) {
        return null;
    }
}
