package iuh.fit.se.repository;

import iuh.fit.se.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem,String> {
    @Modifying
    @Transactional
    @Query(value = """
        DELETE FROM order_item_options
        WHERE order_item_id IN (
            SELECT id FROM order_items WHERE order_id IN (:orderIds)
        )
    """, nativeQuery = true)
    int deleteOptionsByOrderIds(@Param("orderIds") List<String> orderIds);

    /**
     * Xóa order_items theo danh sách orderIds (JPQL)
     */
    @Modifying
    @Transactional
    @Query("""
        DELETE FROM OrderItem oi
        WHERE oi.order.id IN :orderIds
    """)
    int deleteItemsByOrderIds(@Param("orderIds") List<String> orderIds);
}
