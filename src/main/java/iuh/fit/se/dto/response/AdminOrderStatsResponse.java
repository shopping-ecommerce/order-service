package iuh.fit.se.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AdminOrderStatsResponse {
    PlatformOrderOverview platformOverview;
    List<SellerRevenueResponse> topSellers;
    List<RevenueByDateResponse> platformRevenueChart;
    OrderDistribution orderDistribution;
}
