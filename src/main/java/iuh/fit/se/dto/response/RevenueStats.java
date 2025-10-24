package iuh.fit.se.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RevenueStats {
    BigDecimal totalRevenue;
    BigDecimal todayRevenue;
    BigDecimal monthRevenue;
    BigDecimal averageOrderValue;
    Double revenueGrowthRate; // % so với tháng trước
}