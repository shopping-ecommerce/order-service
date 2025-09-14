package iuh.fit.se.dto.response;

import iuh.fit.se.entity.enums.OrderStatusEnum;
import iuh.fit.se.entity.enums.PaymentMethodEnum;
import iuh.fit.se.entity.enums.PaymentStatusEnum;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderResponse {
    String id;
    String userId;
    String sellerId;
    List<OrderItemResponse> orderItems;
    BigDecimal subtotal;
    BigDecimal discountAmount;
    BigDecimal shippingFee;
    BigDecimal totalAmount;
    OrderStatusEnum status;
    PaymentStatusEnum paymentStatus;
    String shippingAddress;
    String phoneNumber;
    String recipientName;
    String notes;
    LocalDateTime createdTime;
    LocalDateTime modifiedTime;
}
