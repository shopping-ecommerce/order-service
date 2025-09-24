package iuh.fit.se.controller;

import iuh.fit.se.dto.request.OrderRequest;
import iuh.fit.se.dto.request.SellerOrderUpdateRequest;
import iuh.fit.se.dto.request.UserCancelRequest;
import iuh.fit.se.dto.response.ApiResponse;
import iuh.fit.se.dto.response.OrderResponse;
import iuh.fit.se.entity.enums.OrderStatusEnum;
import iuh.fit.se.service.OrderService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @PostMapping("/cancelOrder")
    public ApiResponse<OrderResponse> cancelOrder(@RequestBody UserCancelRequest request) {
        log.info("Cancelling order: {} for user: {}", request.getOrderId(), request.getUserId());
        return ApiResponse.<OrderResponse>builder()
                .message("Order cancelled successfully")
                .result(orderService.cancelOrderByUser(request))
                .build();
    }

    @GetMapping("/{orderId}")
    public ApiResponse<OrderResponse> getOrderById(@PathVariable String orderId) {
        log.info("Getting order by ID: {}", orderId);
        return ApiResponse.<OrderResponse>builder()
                .message("Order fetched successfully")
                .result(orderService.getOrderById(orderId))
                .build();
    }

    @GetMapping("/user/{userId}")
    public ApiResponse<List<OrderResponse>> getOrdersByUserId(
            @PathVariable String userId,
            @RequestParam(required = false) List<OrderStatusEnum> statuses) {
        log.info("Getting orders for user: {} with statuses: {}", userId, statuses);
        return ApiResponse.<List<OrderResponse>>builder()
                .message("Orders by user fetched successfully")
                .result(orderService.getOrdersByUserId(userId, statuses))
                .build();
    }

    @GetMapping("/seller/{sellerId}")
    public ApiResponse<List<OrderResponse>> getOrdersBySellerId(
            @PathVariable String sellerId,
            @RequestParam(required = false) List<OrderStatusEnum> statuses) {
        log.info("Getting orders for seller: {} with statuses: {}", sellerId, statuses);
        return ApiResponse.<List<OrderResponse>>builder()
                .message("Orders by seller fetched successfully")
                .result(orderService.getOrdersBySellerId(sellerId, statuses))
                .build();
    }
}