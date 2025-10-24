package iuh.fit.se.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderStatsResponse {
    RevenueStats revenueStats;
    OrderCountStats orderCountStats;
    List<RevenueByDateResponse> revenueChart;
    List<ProductSalesResponse> topProductSales;
}