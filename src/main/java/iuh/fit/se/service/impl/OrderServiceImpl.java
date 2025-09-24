package iuh.fit.se.service.impl;

import iuh.fit.event.dto.OrderCreatedEvent;
import iuh.fit.event.dto.OrderItemPayload;
import iuh.fit.event.dto.OrderStatusChangedEvent;
import iuh.fit.se.dto.request.*;
import iuh.fit.se.dto.response.*;
import iuh.fit.se.entity.Order;
import iuh.fit.se.entity.OrderItem;
import iuh.fit.se.entity.enums.OrderStatusEnum;
import iuh.fit.se.exception.AppException;
import iuh.fit.se.exception.ErrorCode;
import iuh.fit.se.mapper.OrderMapper;
import iuh.fit.se.repository.OrderRepository;
import iuh.fit.se.repository.httpClient.AuthClient;
import iuh.fit.se.repository.httpClient.ProductClient;
import iuh.fit.se.repository.httpClient.UserClient;
import iuh.fit.se.service.OrderService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@RequiredArgsConstructor
@Service
@Slf4j
public class OrderServiceImpl implements OrderService {
    OrderRepository orderRepository;
    OrderMapper orderMapper;
    ProductClient productClient;
    UserClient userClient;
    AuthClient authClient;
    KafkaTemplate<String, Object> kafkaTemplate;
    @Override
    public OrderResponse create(OrderRequest request) {
        List<OrderItem> items = request.getItems().stream()
                .map(reqItem -> {
                    ApiResponse<OrderItemProductResponse> productInfo = productClient.searchBySizeAndID(
                            SearchSizeAndIDRequest.builder()
                                    .size(reqItem.getSize())
                                    .id(reqItem.getProductId())
                                    .build()
                    );
                    if (productInfo == null || productInfo.getResult() == null) {
                        throw new RuntimeException("Product not found for ID: " + reqItem.getProductId() + ", Size: " + reqItem.getSize());
                    }
                    OrderItemProductResponse product = productInfo.getResult();
                    if (!product.getAvailable() || product.getStock() < reqItem.getQuantity()) {
                        throw new RuntimeException("Product " + product.getName() + " is out of stock or unavailable");
                    }

                    return OrderItem.builder()
                            .productId(product.getProductId())
                            .size(product.getSize())
                            .quantity(reqItem.getQuantity())
                            .totalPrice(product.getPrice().multiply(BigDecimal.valueOf(reqItem.getQuantity())))
                            .productName(product.getName())
                            .productImage(product.getImage())
                            .unitPrice(product.getPrice())
                            .build();
                })
                .toList();

        // Build Order object
        Order order = Order.builder()
                .sellerId(request.getSellerId())
                .orderItems(items)
                .userId(request.getUserId())
                .shippingAddress(request.getShippingAddress())
                .phoneNumber(request.getPhoneNumber())
                .recipientName(request.getRecipientName())
                .notes(request.getNotes())
                .status(OrderStatusEnum.PENDING)
                .subtotal(items.stream()
                        .map(OrderItem::getTotalPrice)
                        .reduce(BigDecimal.ZERO, BigDecimal::add))
                .discountAmount(BigDecimal.ZERO) // Add coupon logic if needed
                .shippingFee(BigDecimal.ZERO) // Add shipping fee logic if needed
                .build();

        // Set order reference in OrderItems
        items.forEach(item -> item.setOrder(order));

        // Save to database
        Order savedOrder = orderRepository.save(order);
        ApiResponse<AuthResponse> userInfo = authClient.getMyInfo();
//         Send event to Kafka
        kafkaTemplate.send("create-order", OrderCreatedEvent.builder()
                .orderId(savedOrder.getId())
                        .userEmail(userInfo.getResult().getEmail())
                        .userId(savedOrder.getUserId())
                        .shippingAddress(savedOrder.getShippingAddress())
                        .recipientName(savedOrder.getRecipientName())
                        .sellerId(savedOrder.getSellerId())
                        .items(savedOrder.getOrderItems().stream().map(i -> OrderItemPayload.builder()
                                .productId(i.getProductId())
                                .size(i.getSize())
                                .quantity(i.getQuantity())
                                .productName(i.getProductName())
                                .subTotal(i.getTotalPrice())
                                .unitPrice(i.getUnitPrice())
                                .build()).toList())
                        .subtotal(savedOrder.getSubtotal())
                .build());

        log.info("user info: {}", userInfo.getResult());
        // Map to OrderResponse
        return orderMapper.toOrderResponse(savedOrder);
    }

    @Override
    @Transactional
    public OrderResponse cancelOrderByUser(UserCancelRequest request) {
        Order order = findOrderById(request.getOrderId());
        // Validate user owns this order
        if (!order.getUserId().equals(request.getUserId())) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }
        ApiResponse<SellerResponse> sellerInfo = userClient.searchBySellerId(order.getSellerId());
        log.info("seller info: {}", sellerInfo.getResult());
        kafkaTemplate.send("user-cancel-order", OrderStatusChangedEvent.builder()
                .orderId(order.getId())
                .userEmail(sellerInfo.getResult().getEmail())
                .userId(order.getUserId())
                .reason(request.getReason())
                .shippingAddress(order.getShippingAddress())
                .recipientName(order.getRecipientName())
                .status(OrderStatusEnum.CANCELLED.toString())
                .sellerId(order.getSellerId())
                .items(order.getOrderItems().stream().map(i -> OrderItemPayload.builder()
                        .productId(i.getProductId())
                        .size(i.getSize())
                        .quantity(i.getQuantity())
                        .productName(i.getProductName())
                        .subTotal(i.getTotalPrice())
                        .unitPrice(i.getUnitPrice())
                        .build()).toList())
                .subtotal(order.getSubtotal())
                .build());
        // Only allow cancellation if order is PENDING
        if (order.getStatus() != OrderStatusEnum.PENDING) {
            throw new AppException(ErrorCode.ORDER_CANNOT_BE_CANCELLED);
        }

        order.setStatus(OrderStatusEnum.CANCELLED);
        order.setCancelledReason(request.getReason());
        Order savedOrder = orderRepository.save(order);
        return orderMapper.toOrderResponse(savedOrder);
    }

    @Override
    @Transactional
    public OrderResponse updateOrderBySeller(SellerOrderUpdateRequest request) {
        Order order = findOrderById(request.getOrderId());
        log.info("Updating order {} to status {}",order.getUserId());
        // Validate seller owns this order
        if (!order.getSellerId().equals(request.getSellerId())) {
            throw new AppException(ErrorCode.SELLER_NOT_FOUND);
        }

        OrderStatusEnum newStatus = OrderStatusEnum.valueOf(request.getStatus());
        validateSellerStatusTransition(order.getStatus(), newStatus);

        order.setStatus(newStatus);
        if (request.getReason() != null) {
            order.setCancelledReason(request.getReason());
        }

        Order savedOrder = orderRepository.save(order);
        ApiResponse<UserResponse> userInfo = userClient.getUserById(order.getUserId());
        log.info("user info: {}", userInfo.getResult());
        ApiResponse<AuthResponse> authInfo = authClient.getUserById(userInfo.getResult().getAccountId());
        log.info("auth info: {}", authInfo.getResult());
        // Send update event
        kafkaTemplate.send("order-updated", OrderStatusChangedEvent.builder()
                .orderId(savedOrder.getId())
                .userEmail(authInfo.getResult().getEmail())
                .userId(savedOrder.getUserId())
                .reason(savedOrder.getCancelledReason())
                .shippingAddress(savedOrder.getShippingAddress())
                .recipientName(savedOrder.getRecipientName())
                .status(newStatus.toString())
                .sellerId(savedOrder.getSellerId())
                .items(savedOrder.getOrderItems().stream().map(i -> OrderItemPayload.builder()
                        .productId(i.getProductId())
                        .size(i.getSize())
                        .quantity(i.getQuantity())
                        .productName(i.getProductName())
                        .subTotal(i.getTotalPrice())
                        .unitPrice(i.getUnitPrice())
                        .build()).toList())
                .subtotal(savedOrder.getSubtotal())
                .build());

        return orderMapper.toOrderResponse(savedOrder);
    }

    public Order findOrderById(String orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));
    }
    @Override
    public OrderResponse getOrderById(String orderId) {
        log.info("Fetching order with ID: {}", orderId);
        Order order = findOrderById(orderId);
        return orderMapper.toOrderResponse(order);
    }


    @Override
    public List<OrderResponse> getOrdersByUserId(String userId, List<OrderStatusEnum> statuses) {
        log.info("Fetching orders for user ID: {} with statuses: {}", userId, statuses);
        List<Order> orders;
        if (statuses == null || statuses.isEmpty()) {
            orders = orderRepository.findByUserIdOrderByCreatedTimeDesc(userId);
        } else {
            orders = orderRepository.findByUserIdAndStatusInOrderByCreatedTimeDesc(userId, statuses);
        }
        return orders.stream()
                .map(orderMapper::toOrderResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderResponse> getOrdersBySellerId(String sellerId, List<OrderStatusEnum> statuses) {
        log.info("Fetching orders for seller ID: {} with statuses: {}", sellerId, statuses);
        List<Order> orders;
        if (statuses == null || statuses.isEmpty()) {
            orders = orderRepository.findBySellerIdOrderByCreatedTimeDesc(sellerId);
        } else {
            orders = orderRepository.findBySellerIdAndStatusInOrderByCreatedTimeDesc(sellerId, statuses);
        }
        return orders.stream()
                .map(orderMapper::toOrderResponse)
                .collect(Collectors.toList());
    }

    private void validateSellerStatusTransition(OrderStatusEnum currentStatus, OrderStatusEnum newStatus) {
        Map<OrderStatusEnum, List<OrderStatusEnum>> allowedTransitions = Map.of(
                OrderStatusEnum.PENDING, List.of(OrderStatusEnum.CONFIRMED, OrderStatusEnum.CANCELLED),
                OrderStatusEnum.CONFIRMED, List.of(OrderStatusEnum.PROCESSING, OrderStatusEnum.CANCELLED),
                OrderStatusEnum.PROCESSING, List.of(OrderStatusEnum.SHIPPED, OrderStatusEnum.CANCELLED),
                OrderStatusEnum.SHIPPED, List.of(OrderStatusEnum.DELIVERED, OrderStatusEnum.RETURNED),
                OrderStatusEnum.DELIVERED, List.of(OrderStatusEnum.RETURNED),
                OrderStatusEnum.RETURNED, List.of(OrderStatusEnum.REFUNDED),
                // CANCELLED, REFUNDED là trạng thái cuối, không thể chuyển sang trạng thái khác
                OrderStatusEnum.CANCELLED, List.of(),
                OrderStatusEnum.REFUNDED, List.of()
        );

        List<OrderStatusEnum> allowed = allowedTransitions.get(currentStatus);
        if (allowed == null || !allowed.contains(newStatus)) {
            throw new AppException(ErrorCode.INVALID_ORDER_STATUS_TRANSITION);
        }
    }

}
