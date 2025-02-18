package com.parking.model.dto.parking;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OccupiedSpotDTO {

    private String id;
    private String name;
    private String location;
    private String description;
    private String status;
    private String startTime;
    private String endTime;
    private String price;
    private String image;
    private String ownerId;
}
