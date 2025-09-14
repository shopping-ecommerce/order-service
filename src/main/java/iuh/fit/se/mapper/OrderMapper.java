package iuh.fit.se.mapper;

import iuh.fit.se.dto.request.OrderItemRequest;
import iuh.fit.se.dto.request.OrderRequest;
import iuh.fit.se.dto.response.OrderItemResponse;
import iuh.fit.se.dto.response.OrderResponse;
import iuh.fit.se.entity.Order;
import iuh.fit.se.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    @Mapping(source = "orderItems", target = "orderItems")
    OrderResponse toOrderResponse(Order order);

    @Mapping(source = "productImage", target = "productImage")
    OrderItemResponse toOrderItemResponse(OrderItem orderItem);

    @Mapping(source = "items", target = "orderItems")
    Order toOrder(OrderRequest orderRequest);

    OrderItem toOrderItem(OrderItemRequest orderItemRequest);
}