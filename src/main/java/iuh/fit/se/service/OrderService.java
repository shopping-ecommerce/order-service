package iuh.fit.se.service;

import iuh.fit.se.dto.request.*;
import iuh.fit.se.dto.response.OrderResponse;
import iuh.fit.se.entity.enums.OrderStatusEnum;

import java.util.List;

public interface OrderService {
    OrderResponse create(OrderRequest request);
    OrderResponse cancelOrderByUser(UserCancelRequest request);

    OrderResponse updateOrderBySeller(SellerOrderUpdateRequest request);
    OrderResponse findOrderById(String orderId);

    List<OrderResponse> getOrdersByUserId(String userId, List<OrderStatusEnum> statuses);
    List<OrderResponse> getOrdersBySellerId(String sellerId, List<OrderStatusEnum> statuses);
}
