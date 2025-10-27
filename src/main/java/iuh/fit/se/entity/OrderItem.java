package iuh.fit.se.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.Map;

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

    @ElementCollection
    @CollectionTable(name = "order_item_options", joinColumns = @JoinColumn(name = "order_item_id"))
    @MapKeyColumn(name = "option_key")
    @Column(name = "option_value")
    Map<String, String> options;

    @Column(name = "unit_price", precision = 19, scale = 2)
    BigDecimal unitPrice;

    @Column(name = "quantity")
    Integer quantity;

    @Column(name = "total_price", precision = 19, scale = 2)
    BigDecimal totalPrice;

    @Column(name = "discount_percent")
    Double discountPercent;
}
