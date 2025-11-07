package iuh.fit.se.batch;

import iuh.fit.event.dto.SellerViolationEvent;
import iuh.fit.se.dto.request.SellerOrderUpdateRequest;
import iuh.fit.se.entity.Order;
import iuh.fit.se.entity.enums.OrderStatusEnum;
import iuh.fit.se.repository.OrderRepository;
import iuh.fit.se.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderSellerViolantionsBatchJob {
    private final OrderRepository orderRepository;
    private final OrderService orderService;
    private final KafkaTemplate<String,Object> kafkaTemplate;
    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Ho_Chi_Minh")
    public void run() {
        log.info("OrderSellerViolantionsBatchJob is running at {}", LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")));
        var cutoffPending = LocalDateTime.now().minusDays(4);
        var cutoffShip = LocalDateTime.now().minusDays(8);

        var pendingTimeout = orderRepository.findByStatusAndCreatedTimeBefore(OrderStatusEnum.PENDING, cutoffPending);
        var confirmedTimeout = orderRepository.findByStatusAndCreatedTimeBefore(OrderStatusEnum.CONFIRMED, cutoffShip);
        for (Order order : pendingTimeout) {
            log.info("OrderSellerViolantionsBatchJob - Canceling PENDING order id={} createdTime={}", order.getId(), order.getCreatedTime());
            orderService.updateOrderBySeller(SellerOrderUpdateRequest.builder()
                            .orderId(order.getId())
                            .sellerId(order.getSellerId())
                            .reason("Đơn đã bị hủy do seller không xác nhận.")
                            .status(OrderStatusEnum.CANCELLED)
                    .build());
            kafkaTemplate.send("seller-violations", SellerViolationEvent.builder()
                    .sellerId(order.getSellerId())
                    .totalPrice(order.getTotalAmount())
                    .orderId(order.getId())
                    .userId(order.getUserId())
                    .build());
        }
        for (Order order : confirmedTimeout) {
            log.info("OrderSellerViolantionsBatchJob - Canceling CONFIRMED order id={} createdTime={}", order.getId(), order.getCreatedTime());
            orderService.updateOrderBySeller(SellerOrderUpdateRequest.builder()
                            .orderId(order.getId())
                            .sellerId(order.getSellerId())
                            .reason("Seller không bàn giao cho đơn vị vận chuyển đúng hạn")
                            .status(OrderStatusEnum.CANCELLED)
                    .build());
            kafkaTemplate.send("seller-violations", SellerViolationEvent.builder()
                    .sellerId(order.getSellerId())
                    .totalPrice(order.getTotalAmount())
                    .orderId(order.getId())
                    .userId(order.getUserId())
                    .build());
        }
    }
}