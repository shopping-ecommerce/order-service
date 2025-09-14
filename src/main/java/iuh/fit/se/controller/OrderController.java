package iuh.fit.se.controller;

import iuh.fit.se.dto.request.OrderRequest;
import iuh.fit.se.dto.request.SellerOrderUpdateRequest;
import iuh.fit.se.dto.response.ApiResponse;
import iuh.fit.se.dto.response.OrderResponse;
import iuh.fit.se.service.OrderService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class OrderController {
    OrderService orderService;

    @PostMapping("/createOrder")
    public ApiResponse<OrderResponse> createOrder(@RequestBody OrderRequest orderRequest) {
        log.info("Creating order for user: {}", orderRequest.getUserId());
        return ApiResponse.<OrderResponse>builder()
                .message("Order created successfully")
                .result(orderService.create(orderRequest))
                .build();
    }

    @PostMapping("/updateOrder")
    public ApiResponse<OrderResponse> updateOrder(@RequestBody SellerOrderUpdateRequest orderRequest) {
        log.info("Updating order: {}", orderRequest.getOrderId());
        return ApiResponse.<OrderResponse>builder()
                .message("Order updated successfully")
                .result(orderService.updateOrderBySeller(orderRequest))
                .build();
    }

    @PostMapping("/cancelOrder/{orderId}")
    public ApiResponse<OrderResponse> cancelOrder(@PathVariable String orderId, @RequestParam String userId) {
        log.info("Cancelling order: {} for user: {}", orderId, userId);
        return ApiResponse.<OrderResponse>builder()
                .message("Order cancelled successfully")
                .result(orderService.cancelOrderByUser(orderId, userId))
                .build();
    }
}