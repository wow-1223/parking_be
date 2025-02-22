package com.parking.model.param.owner.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatisticsResponse {

    private LocalDate date;

    private Integer totalAmount;

    private Integer orderCount;
}
