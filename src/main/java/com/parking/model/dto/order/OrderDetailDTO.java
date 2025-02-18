package com.parking.model.dto.order;

import com.alipay.api.domain.UserDTO;
import com.parking.model.dto.parking.OccupiedSpotDTO;
import com.parking.model.dto.parking.ParkingSpotDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetailDTO extends OrderDTO {

    private UserDTO user;

    private ParkingSpotDTO parkingSpot;

    private OccupiedSpotDTO occupiedSpot;

}
