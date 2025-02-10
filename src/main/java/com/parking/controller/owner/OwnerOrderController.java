package com.parking.controller.owner;

import com.parking.model.dto.common.PageResponse;
import com.parking.model.dto.owner.OwnerOrderListItemDTO;
import com.parking.service.owner.OwnerOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "出租方订单管理接口")
@RestController
@RequestMapping("/api/owner/orders")
public class OwnerOrderController {

    @Autowired
    private OwnerOrderService ownerOrderService;

    @ApiOperation("获取订单列表")
    @GetMapping("/getOrders")
    public PageResponse<OwnerOrderListItemDTO> getOrders(
            @RequestParam(required = false) String status,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer pageSize) {
        return ownerOrderService.getOrders(status, page, pageSize);
    }
} 