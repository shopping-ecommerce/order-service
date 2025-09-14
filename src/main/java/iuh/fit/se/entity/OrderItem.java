package iuh.fit.se.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "order_items")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    Order order;

    @Column(name = "product_id")
    String productId;

    @Column(name = "product_name")
    String productName;

    @Column(name = "product_image")
    String productImage;

    @Column(name = "size")
    String size;

    @Column(name = "color")
    String color;

    @Column(name = "unit_price", precision = 19, scale = 2)
    BigDecimal unitPrice;

    @Column(name = "quantity")
    Integer quantity;

    @Column(name = "total_price", precision = 19, scale = 2)
    BigDecimal totalPrice;

    @Column(name = "discount_percent")
    Double discountPercent;
}
