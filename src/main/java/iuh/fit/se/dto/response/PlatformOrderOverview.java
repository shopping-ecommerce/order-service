package iuh.fit.se.dto.response;


import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlatformOrderOverview {
    BigDecimal totalGMV; // Gross Merchandise Value
    BigDecimal totalCommission;
    Long totalOrders;
    Long activeSellers; // Có đơn hàng trong 30 ngày
    Double averageOrderValue;
}