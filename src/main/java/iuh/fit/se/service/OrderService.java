package iuh.fit.se.service;

import iuh.fit.se.dto.request.*;
import iuh.fit.se.dto.response.OrderResponse;
import org.springframework.transaction.annotation.Transactional;

public interface OrderService {
    OrderResponse create(OrderRequest request);
    OrderResponse cancelOrderByUser(UserCancelRequest request);

    OrderResponse updateOrderBySeller(SellerOrderUpdateRequest request);
}
