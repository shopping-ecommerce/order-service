package iuh.fit.se.controller;

import iuh.fit.se.dto.response.AdminOrderStatsResponse;
import iuh.fit.se.dto.response.ApiResponse;
import iuh.fit.se.dto.response.OrderStatsResponse;
import iuh.fit.se.service.OrderStatisticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@RestController
@RequestMapping("/order-statistics")
@RequiredArgsConstructor
@Slf4j
public class OrderStatisticsController {

    private final OrderStatisticsService orderStatisticsService;

    /**
     * Lấy thống kê đơn hàng cho seller
     * GET /api/order-statistics/seller/{sellerId}?startDate=2024-01-01&endDate=2024-12-31
     */
    @GetMapping("/seller/{sellerId}")
    @PreAuthorize("hasRole('SELLER') or hasRole('ADMIN')")
    public ApiResponse<OrderStatsResponse> getSellerOrderStats(
            @PathVariable String sellerId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        log.info("GET /order-statistics/seller/{} - startDate: {}, endDate: {}",
                sellerId, startDate, endDate);

        // Default: 30 ngày gần nhất
        if (startDate == null) {
            startDate = LocalDate.now().minusDays(30);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }

        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(LocalTime.MAX);

        OrderStatsResponse stats = orderStatisticsService.getOrderStatsBySeller(sellerId, start, end);

        return ApiResponse.<OrderStatsResponse>builder()
                .code(200)
                .message("Lấy thống kê đơn hàng seller thành công")
                .result(stats)
                .build();
    }

    /**
     * Lấy thống kê đơn hàng cho admin (toàn platform)
     * GET /api/order-statistics/admin?startDate=2024-01-01&endDate=2024-12-31
     */
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<AdminOrderStatsResponse> getAdminOrderStats(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        log.info("GET /api/order-statistics/admin - startDate: {}, endDate: {}", startDate, endDate);

        // Default: 30 ngày gần nhất
        if (startDate == null) {
            startDate = LocalDate.now().minusDays(30);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }

        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(LocalTime.MAX);

        AdminOrderStatsResponse stats = orderStatisticsService.getAdminOrderStats(start, end);

        return ApiResponse.<AdminOrderStatsResponse>builder()
                .code(200)
                .message("Lấy thống kê đơn hàng admin thành công")
                .result(stats)
                .build();
    }
}
