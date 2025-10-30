package iuh.fit.se.entity;

import iuh.fit.se.entity.enums.OrderStatusEnum;
import iuh.fit.se.entity.enums.PaymentStatusEnum;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "orders")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Column(name = "user_id")
    String userId;

    @Column(name = "seller_id")
    String sellerId;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    List<OrderItem> orderItems = new ArrayList<>();

    @Column(name = "subtotal", precision = 19, scale = 2)
    BigDecimal subtotal;

    @Column(name = "discount_amount", precision = 19, scale = 2)
    BigDecimal discountAmount;

    String voucherCode;

    @Column(name = "shipping_fee", precision = 19, scale = 2)
    BigDecimal shippingFee;

    @Column(name = "total_amount", precision = 19, scale = 2)
    BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    OrderStatusEnum status;

    @Enumerated(EnumType.STRING)
    PaymentStatusEnum paymentStatus;

    @Column(name = "shipping_address", columnDefinition = "TEXT")
    String shippingAddress;

    @Column(name = "phone_number")
    String phoneNumber;

    @Column(name = "recipient_name")
    String recipientName;

    @Column(name = "notes", columnDefinition = "TEXT")
    String notes;
    @Column(name = "cancelled_reason")
    String cancelledReason;

    @Column(name = "created_time")
    LocalDateTime createdTime;

    @Column(name = "modified_time")
    LocalDateTime modifiedTime;

    @PrePersist
    void prePersist() {
        this.status = OrderStatusEnum.PENDING;
        this.createdTime = LocalDateTime.now();
        this.modifiedTime = LocalDateTime.now();
    }

    @PreUpdate
    void preUpdate() {
        this.modifiedTime = LocalDateTime.now();
    }
}
