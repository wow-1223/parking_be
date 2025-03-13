package com.parking.service.admin;

import com.parking.model.dto.parking.ParkingSpotDTO;
import com.parking.model.param.admin.request.AdminParkingRequest;
import com.parking.model.param.common.PageResponse;
import com.parking.service.ParkSpotService;

public interface AdminParkingService extends ParkSpotService {

    PageResponse<ParkingSpotDTO> getParkingList(AdminParkingRequest request);
}
