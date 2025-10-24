package iuh.fit.se.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SellerRevenueResponse {
    String sellerId;
    BigDecimal totalRevenue;
    Long totalOrders;
    Long completedOrders;
    Double completionRate;
}