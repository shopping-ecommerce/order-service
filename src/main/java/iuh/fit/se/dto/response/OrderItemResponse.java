package iuh.fit.se.dto.response;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderItemResponse {
    @NotBlank(message = "Product ID is required")
    String productId;
    String productName;
    String productImage;
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    Integer quantity;
//    String size;
//    String color;
    Map<String,String> options;
    BigDecimal unitPrice; // Optional: added to align with OrderItem
    BigDecimal totalPrice; // Optional: added to align with OrderItem
//    Double discountPercent; // Optional: added to align with OrderItem
}
