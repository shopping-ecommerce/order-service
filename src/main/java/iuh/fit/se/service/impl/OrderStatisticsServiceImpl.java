package iuh.fit.se.service.impl;

import iuh.fit.se.dto.response.*;
import iuh.fit.se.entity.enums.OrderStatusEnum;
import iuh.fit.se.repository.OrderRepository;
import iuh.fit.se.service.OrderStatisticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderStatisticsServiceImpl implements OrderStatisticsService {

    private final OrderRepository orderRepository;

    @Override
    public OrderStatsResponse getOrderStatsBySeller(String sellerId,
                                                    LocalDateTime startDate,
                                                    LocalDateTime endDate) {
        log.info("Fetching order statistics for seller: {}, period: {} to {}",
                sellerId, startDate, endDate);

        return OrderStatsResponse.builder()
                .revenueStats(getRevenueStats(sellerId, startDate, endDate))
                .orderCountStats(getOrderCountStats(sellerId))
                .revenueChart(getRevenueChart(sellerId, startDate, endDate))
                .topProductSales(getTopProductSales(sellerId, startDate, endDate))
                .build();
    }

    @Override
    public AdminOrderStatsResponse getAdminOrderStats(LocalDateTime startDate,
                                                      LocalDateTime endDate) {
        log.info("Fetching admin order statistics, period: {} to {}", startDate, endDate);

        return AdminOrderStatsResponse.builder()
                .platformOverview(getPlatformOverview(startDate, endDate))
                .topSellers(getTopSellers(startDate))
                .platformRevenueChart(getPlatformRevenueChart(startDate, endDate))
                .orderDistribution(getOrderDistribution())
                .build();
    }

    // ==================== PRIVATE METHODS - SELLER STATS ====================

    private RevenueStats getRevenueStats(String sellerId,
                                         LocalDateTime start,
                                         LocalDateTime end) {
        // Tổng doanh thu
        BigDecimal totalRevenue = orderRepository.calculateTotalRevenueBySeller(sellerId);

        // Doanh thu hôm nay
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime todayEnd = LocalDate.now().atTime(LocalTime.MAX);
        BigDecimal todayRevenue = orderRepository.calculateRevenueBySellerAndDateRange(
                sellerId, todayStart, todayEnd);

        // Doanh thu tháng này
        LocalDateTime monthStart = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        LocalDateTime monthEnd = LocalDate.now().atTime(LocalTime.MAX);
        BigDecimal monthRevenue = orderRepository.calculateRevenueBySellerAndDateRange(
                sellerId, monthStart, monthEnd);

        // Doanh thu tháng trước
        LocalDateTime lastMonthStart = LocalDate.now().minusMonths(1).withDayOfMonth(1).atStartOfDay();
        LocalDateTime lastMonthEnd = LocalDate.now().minusMonths(1)
                .withDayOfMonth(LocalDate.now().minusMonths(1).lengthOfMonth())
                .atTime(LocalTime.MAX);
        BigDecimal lastMonthRevenue = orderRepository.calculateRevenueBySellerAndDateRange(
                sellerId, lastMonthStart, lastMonthEnd);

        // Tính growth rate
        Double growthRate = 0.0;
        if (lastMonthRevenue != null && lastMonthRevenue.compareTo(BigDecimal.ZERO) > 0) {
            growthRate = monthRevenue.subtract(lastMonthRevenue)
                    .divide(lastMonthRevenue, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .doubleValue();
        }

        // Giá trị đơn hàng trung bình
        BigDecimal avgOrderValue = orderRepository.calculateAverageOrderValue(sellerId);
        if (avgOrderValue == null) {
            avgOrderValue = BigDecimal.ZERO;
        }

        return RevenueStats.builder()
                .totalRevenue(totalRevenue != null ? totalRevenue : BigDecimal.ZERO)
                .todayRevenue(todayRevenue != null ? todayRevenue : BigDecimal.ZERO)
                .monthRevenue(monthRevenue != null ? monthRevenue : BigDecimal.ZERO)
                .averageOrderValue(avgOrderValue)
                .revenueGrowthRate(Math.round(growthRate * 100.0) / 100.0)
                .build();
    }

    private OrderCountStats getOrderCountStats(String sellerId) {
        Long totalOrders = orderRepository.countBySellerId(sellerId);
        Long pendingOrders = orderRepository.countBySellerIdAndStatus(
                sellerId, OrderStatusEnum.PENDING);
        Long shippingOrders = orderRepository.countBySellerIdAndStatus(
                sellerId, OrderStatusEnum.SHIPPED);
        Long completedOrders = orderRepository.countBySellerIdAndStatus(
                sellerId, OrderStatusEnum.DELIVERED);
        Long cancelledOrders = orderRepository.countBySellerIdAndStatus(
                sellerId, OrderStatusEnum.CANCELLED);

        Double cancellationRate = totalOrders > 0
                ? (cancelledOrders.doubleValue() / totalOrders) * 100 : 0.0;
        Double completionRate = totalOrders > 0
                ? (completedOrders.doubleValue() / totalOrders) * 100 : 0.0;

        return OrderCountStats.builder()
                .totalOrders(totalOrders)
                .pendingOrders(pendingOrders)
                .shippingOrders(shippingOrders)
                .completedOrders(completedOrders)
                .cancelledOrders(cancelledOrders)
                .cancellationRate(Math.round(cancellationRate * 100.0) / 100.0)
                .completionRate(Math.round(completionRate * 100.0) / 100.0)
                .build();
    }

    private List<RevenueByDateResponse> getRevenueChart(String sellerId,
                                                        LocalDateTime start,
                                                        LocalDateTime end) {
        List<Object[]> results = orderRepository.getRevenueByDateForSeller(
                sellerId, start, end);

        return results.stream()
                .map(row -> RevenueByDateResponse.builder()
                        .date((LocalDate) row[0])
                        .revenue((BigDecimal) row[1])
                        .orderCount((Long) row[2])
                        .build())
                .collect(Collectors.toList());
    }

    private List<ProductSalesResponse> getTopProductSales(String sellerId,
                                                          LocalDateTime start,
                                                          LocalDateTime end) {
        List<Object[]> results = orderRepository.getTopSellingProductsBySeller(
                sellerId, start, end);

        return results.stream()
                .limit(10) // Top 10
                .map(row -> ProductSalesResponse.builder()
                        .productId((String) row[0])
                        .totalSold(((Number) row[1]).intValue())
                        .revenue((BigDecimal) row[2])
                        .build())
                .collect(Collectors.toList());
    }

    // ==================== PRIVATE METHODS - ADMIN STATS ====================

    private PlatformOrderOverview getPlatformOverview(LocalDateTime start, LocalDateTime end) {
        BigDecimal totalGMV = orderRepository.calculateTotalGMV();
        if (totalGMV == null) {
            totalGMV = BigDecimal.ZERO;
        }

        // Commission giả sử 8% của GMV
        BigDecimal commissionRate = BigDecimal.valueOf(0.08);
        BigDecimal totalCommission = totalGMV.multiply(commissionRate);

        Long totalOrders = orderRepository.countTotalOrders();
        Long activeSellers = orderRepository.countActiveSellers(start);

        Double avgOrderValue = 0.0;
        if (totalOrders > 0) {
            avgOrderValue = totalGMV.divide(BigDecimal.valueOf(totalOrders),
                    2, RoundingMode.HALF_UP).doubleValue();
        }

        return PlatformOrderOverview.builder()
                .totalGMV(totalGMV)
                .totalCommission(totalCommission)
                .totalOrders(totalOrders)
                .activeSellers(activeSellers)
                .averageOrderValue(avgOrderValue)
                .build();
    }

    private List<SellerRevenueResponse> getTopSellers(LocalDateTime startDate) {
        List<Object[]> results = orderRepository.findTopSellersByRevenue(startDate);

        return results.stream()
                .limit(10) // Top 10 sellers
                .map(row -> {
                    String sellerId = (String) row[0];
                    BigDecimal revenue = (BigDecimal) row[1];
                    Long orderCount = ((Number) row[2]).longValue();

                    // Lấy số đơn hoàn thành
                    Long completedOrders = orderRepository.countDELIVEREDOrdersBySeller(sellerId);

                    Double completionRate = orderCount > 0
                            ? (completedOrders.doubleValue() / orderCount) * 100 : 0.0;

                    return SellerRevenueResponse.builder()
                            .sellerId(sellerId)
                            .totalRevenue(revenue)
                            .totalOrders(orderCount)
                            .completedOrders(completedOrders)
                            .completionRate(Math.round(completionRate * 100.0) / 100.0)
                            .build();
                })
                .collect(Collectors.toList());
    }

    private List<RevenueByDateResponse> getPlatformRevenueChart(LocalDateTime start,
                                                                LocalDateTime end) {
        List<Object[]> results = orderRepository.getRevenueByDateForPlatform(start, end);

        return results.stream()
                .map(row -> RevenueByDateResponse.builder()
                        .date((LocalDate) row[0])
                        .revenue((BigDecimal) row[1])
                        .orderCount((Long) row[2])
                        .build())
                .collect(Collectors.toList());
    }

    private OrderDistribution getOrderDistribution() {
        Map<String, Long> ordersByStatus = new HashMap<>();

        Long pending = orderRepository.countByStatus(OrderStatusEnum.PENDING);
        Long shipping = orderRepository.countByStatus(OrderStatusEnum.SHIPPED);
        Long completed = orderRepository.countByStatus(OrderStatusEnum.DELIVERED);
        Long cancelled = orderRepository.countByStatus(OrderStatusEnum.CANCELLED);

        ordersByStatus.put("PENDING", pending);
        ordersByStatus.put("SHIPPED", shipping);
        ordersByStatus.put("DELIVERED", completed);
        ordersByStatus.put("CANCELLED", cancelled);

        return OrderDistribution.builder()
                .pending(pending)
                .shipping(shipping)
                .completed(completed)
                .cancelled(cancelled)
                .ordersByStatus(ordersByStatus)
                .build();
    }
}