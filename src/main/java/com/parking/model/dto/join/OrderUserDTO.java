package com.parking.model.dto.join;

import com.parking.model.entity.mybatis.Order;
import com.parking.model.entity.mybatis.User;
import lombok.Data;

@Data
public class OrderUserDTO {
    private Order order;
    private User user;
}
