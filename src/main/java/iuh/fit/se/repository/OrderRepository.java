package iuh.fit.se.repository;

import iuh.fit.se.entity.Order;
import iuh.fit.se.entity.enums.OrderStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order,String> {
    List<Order> findByStatusAndSellerId(String status, String sellerId);

    List<Order> findByUserIdOrderByCreatedTimeDesc(String userId);
    List<Order> findByUserIdAndStatusInOrderByCreatedTimeDesc(String userId, Collection<OrderStatusEnum> statuses);

    List<Order> findBySellerIdOrderByCreatedTimeDesc(String sellerId);
    List<Order> findBySellerIdAndStatusInOrderByCreatedTimeDesc(String sellerId, Collection<OrderStatusEnum> statuses);
}
