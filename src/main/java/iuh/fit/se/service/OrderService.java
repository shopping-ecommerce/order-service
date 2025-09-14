package iuh.fit.se.service;

import iuh.fit.se.dto.request.OrderCreationRequest;
import iuh.fit.se.dto.request.OrderRequest;
import iuh.fit.se.dto.request.OrderUpdateRequest;
import iuh.fit.se.dto.request.SellerOrderUpdateRequest;
import iuh.fit.se.dto.response.OrderResponse;
import org.springframework.transaction.annotation.Transactional;

public interface OrderService {
    OrderResponse create(OrderRequest request);
    OrderResponse cancelOrderByUser(String orderId, String userId);

    OrderResponse updateOrderBySeller(SellerOrderUpdateRequest request);
}
