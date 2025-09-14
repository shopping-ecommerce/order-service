package iuh.fit.se.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class CreateOrderFromCartRequest {
//    @NotNull(message = "Payment method is required")
//    PaymentMethodEnum paymentMethod;

    @NotBlank(message = "Shipping address is required")
    String shippingAddress;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[0-9]{10,11}$", message = "Invalid phone number format")
    String phoneNumber;

    @NotBlank(message = "Recipient name is required")
    String recipientName;

    String notes;
    String couponCode;
}
