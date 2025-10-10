package iuh.fit.se.dto.request;

import iuh.fit.se.entity.enums.PaymentMethodEnum;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderRequest {
    @NotBlank(message = "User ID is required")
    String userId;

    @NotBlank(message = "Seller ID is required")
    String sellerId;
    @NotEmpty(message = "Order items cannot be empty")
    List<OrderItemRequest> items;

    @NotNull(message = "Payment method is required")
    PaymentMethodEnum paymentMethod;

    @NotBlank(message = "Shipping address is required")
    String shippingAddress;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[0-9]{10,11}$", message = "Invalid phone number format")
    String phoneNumber;

    @NotBlank(message = "Recipient name is required")
    String recipientName;

    String notes;

    BigDecimal discountAmount;

}
