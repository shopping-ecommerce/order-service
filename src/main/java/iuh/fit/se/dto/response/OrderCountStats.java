package iuh.fit.se.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderCountStats {
    Long totalOrders;
    Long pendingOrders;
    Long shippingOrders;
    Long completedOrders;
    Long cancelledOrders;
    Double cancellationRate; // %
    Double completionRate; // %
}