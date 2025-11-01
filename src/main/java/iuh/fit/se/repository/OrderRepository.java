package iuh.fit.se.repository;

import iuh.fit.se.entity.Order;
import iuh.fit.se.entity.enums.OrderStatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order,String> {
    List<Order> findByStatusAndSellerId(String status, String sellerId);

    List<Order> findByUserIdOrderByCreatedTimeDesc(String userId);
    List<Order> findByUserIdAndStatusInOrderByCreatedTimeDesc(String userId, Collection<OrderStatusEnum> statuses);

    List<Order> findBySellerIdOrderByCreatedTimeDesc(String sellerId);
    List<Order> findBySellerIdAndStatusInOrderByCreatedTimeDesc(String sellerId, Collection<OrderStatusEnum> statuses);
    // ==================== SELLER STATISTICS ====================

    /**
     * Tổng doanh thu của seller (chỉ tính đơn DELIVERED)
     */
    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o " +
            "WHERE o.sellerId = :sellerId AND o.status = 'DELIVERED'")
    BigDecimal calculateTotalRevenueBySeller(@Param("sellerId") String sellerId);

    /**
     * Doanh thu theo khoảng thời gian
     */
    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o " +
            "WHERE o.sellerId = :sellerId AND o.status = 'DELIVERED' " +
            "AND o.createdTime BETWEEN :startDate AND :endDate")
    BigDecimal calculateRevenueBySellerAndDateRange(
            @Param("sellerId") String sellerId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    /**
     * Đếm đơn hàng theo status
     */
    Long countBySellerIdAndStatus(String sellerId, OrderStatusEnum status);

    /**
     * Tổng số đơn hàng của seller
     */
    Long countBySellerId(String sellerId);

    /**
     * Giá trị đơn hàng trung bình
     */
    @Query("SELECT AVG(o.totalAmount) FROM Order o " +
            "WHERE o.sellerId = :sellerId AND o.status = 'DELIVERED'")
    BigDecimal calculateAverageOrderValue(@Param("sellerId") String sellerId);

    /**
     * Doanh thu theo ngày (cho biểu đồ)
     */
    @Query("SELECT CAST(o.createdTime AS LocalDate) as date, " +
            "COALESCE(SUM(o.totalAmount), 0) as revenue, " +
            "COUNT(o) as orderCount " +
            "FROM Order o " +
            "WHERE o.sellerId = :sellerId AND o.status = 'DELIVERED' " +
            "AND o.createdTime BETWEEN :startDate AND :endDate " +
            "GROUP BY CAST(o.createdTime AS LocalDate) " +
            "ORDER BY CAST(o.createdTime AS LocalDate) DESC")
    List<Object[]> getRevenueByDateForSeller(
            @Param("sellerId") String sellerId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    /**
     * Top sản phẩm bán chạy (theo quantity)
     * Trả về: productId, totalQuantitySold, totalRevenue
     */
    @Query("SELECT oi.productId, " +
            "SUM(oi.quantity) as totalSold, " +
            "COALESCE(SUM(oi.totalPrice), 0) as revenue " +
            "FROM OrderItem oi " +
            "JOIN oi.order o " +
            "WHERE o.sellerId = :sellerId " +
            "AND o.status = 'DELIVERED' " +
            "AND o.createdTime BETWEEN :startDate AND :endDate " +
            "GROUP BY oi.productId " +
            "ORDER BY totalSold DESC")
    List<Object[]> getTopSellingProductsBySeller(
            @Param("sellerId") String sellerId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    // ==================== ADMIN STATISTICS ====================

    /**
     * Tổng GMV (Gross Merchandise Value) - toàn platform
     */
    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o " +
            "WHERE o.status = 'DELIVERED'")
    BigDecimal calculateTotalGMV();

    /**
     * GMV theo khoảng thời gian
     */
    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o " +
            "WHERE o.status = 'DELIVERED' " +
            "AND o.createdTime BETWEEN :startDate AND :endDate")
    BigDecimal calculateGMVByDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    /**
     * Tổng số đơn hàng toàn platform
     */
    @Query("SELECT COUNT(o) FROM Order o")
    Long countTotalOrders();

    /**
     * Đếm đơn hàng theo status (admin)
     */
    Long countByStatus(OrderStatusEnum status);

    /**
     * Top sellers theo doanh thu
     * Trả về: sellerId, totalRevenue, totalOrders
     */
    @Query("SELECT o.sellerId, " +
            "COALESCE(SUM(o.totalAmount), 0) as revenue, " +
            "COUNT(o) as orderCount " +
            "FROM Order o " +
            "WHERE o.status = 'DELIVERED' " +
            "AND o.createdTime >= :startDate " +
            "GROUP BY o.sellerId " +
            "ORDER BY COALESCE(SUM(o.totalAmount), 0) DESC")
    List<Object[]> findTopSellersByRevenue(@Param("startDate") LocalDateTime startDate);

    @Query("SELECT CAST(o.createdTime AS LocalDate) as date, " +
            "COALESCE(SUM(o.totalAmount), 0) as revenue, " +
            "COUNT(o) as orderCount " +
            "FROM Order o " +
            "WHERE o.status = 'DELIVERED' " +
            "AND o.createdTime BETWEEN :startDate AND :endDate " +
            "GROUP BY CAST(o.createdTime AS LocalDate) " +
            "ORDER BY CAST(o.createdTime AS LocalDate) DESC")
    List<Object[]> getRevenueByDateForPlatform(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    /**
     * Sellers hoạt động (có đơn trong khoảng thời gian)
     */
    @Query("SELECT COUNT(DISTINCT o.sellerId) FROM Order o " +
            "WHERE o.createdTime >= :startDate")
    Long countActiveSellers(@Param("startDate") LocalDateTime startDate);

    /**
     * Tổng số sellers (distinct)
     */
    @Query("SELECT COUNT(DISTINCT o.sellerId) FROM Order o")
    Long countDistinctSellers();

    /**
     * Đếm đơn DELIVERED của seller (dùng cho completion rate)
     */
    @Query("SELECT COUNT(o) FROM Order o " +
            "WHERE o.sellerId = :sellerId AND o.status = 'DELIVERED'")
    Long countDELIVEREDOrdersBySeller(@Param("sellerId") String sellerId);

    Page<Order> findByCreatedTimeBefore(LocalDateTime cutoff, Pageable pageable);
    long deleteByCreatedTimeBefore(LocalDateTime cutoff);
}
