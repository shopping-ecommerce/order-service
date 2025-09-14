package iuh.fit.se.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class OrderUpdateRequest {
    String orderId;
    String status; // PENDING, SHIPPED, DELIVERED, CANCELED, CONFIRMED
}
