package iuh.fit.se.service;

import iuh.fit.se.dto.response.AdminOrderStatsResponse;
import iuh.fit.se.dto.response.OrderStatsResponse;

import java.time.LocalDateTime;

public interface OrderStatisticsService {
    /**
     * Lấy thống kê đơn hàng cho seller
     *
     * @param sellerId ID của seller
     * @param startDate Ngày bắt đầu
     * @param endDate Ngày kết thúc
     * @return OrderStatsResponse
     */
    OrderStatsResponse getOrderStatsBySeller(String sellerId,
                                             LocalDateTime startDate,
                                             LocalDateTime endDate);

    /**
     * Lấy thống kê đơn hàng cho admin (toàn platform)
     *
     * @param startDate Ngày bắt đầu
     * @param endDate Ngày kết thúc
     * @return AdminOrderStatsResponse
     */
    AdminOrderStatsResponse getAdminOrderStats(LocalDateTime startDate,
                                               LocalDateTime endDate);
}
