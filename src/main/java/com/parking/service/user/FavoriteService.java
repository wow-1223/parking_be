package com.parking.service.user;

import com.parking.model.param.user.request.FavoriteRequest;
import com.parking.model.param.common.OperationResponse;

public interface FavoriteService {
    /**
     * 收藏/取消收藏停车位
     * @param request 收藏请求
     * @return 操作结果
     */
    OperationResponse toggleFavorite(FavoriteRequest request);
} 