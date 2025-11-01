package iuh.fit.se.batch;

import iuh.fit.se.configuration.OrderCleanupProperties;
import iuh.fit.se.entity.Order;
import iuh.fit.se.repository.OrderItemRepository;
import iuh.fit.se.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderCleanBatchJob {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository; // 👈 dùng repo sẵn có
    private final OrderCleanupProperties props;

    @Scheduled(cron = "${order.cleanup.cron:0 0 2 * * MON}")
    public void run() {
        if (!props.isEnabled()) return;

        // Chỉ chạy nếu hôm nay là "Thứ Hai đầu tiên" của tháng
        var zone = java.time.ZoneId.of("Asia/Ho_Chi_Minh");
        var today = java.time.LocalDate.now(zone);
        var firstMonday = today.with(java.time.temporal.TemporalAdjusters.firstInMonth(java.time.DayOfWeek.MONDAY));
        if (!today.equals(firstMonday)) {
            log.info("[OrderCleanup] Skip (không phải Thứ Hai đầu tiên). Today={}, FirstMonday={}", today, firstMonday);
            return;
        }
        final int batchSize = Math.max(50, props.getBatchSize() == null ? 500 : props.getBatchSize());
        final int months = Math.max(1, props.getMonthsBeforeDeletion() == null ? 12 : props.getMonthsBeforeDeletion());

        final LocalDateTime cutoff = LocalDateTime.now(ZoneId.systemDefault()).minusMonths(months);
        log.info("[OrderCleanup] Start. months={}, cutoff={}, batchSize={}", months, cutoff, batchSize);

        long totalDeleted = 0L;
        int round = 0;

        while (true) {
            int deleted = deleteOneBatch(cutoff, batchSize);
            totalDeleted += deleted;
            round++;
            log.info("[OrderCleanup] Round #{} deleted={}", round, deleted);
            if (deleted < batchSize) break;
        }

        log.info("[OrderCleanup] Done. Total deleted={}", totalDeleted);
    }

    /**
     * Xóa 1 lô theo thứ tự: options -> items -> orders
     */
    @Transactional
    public int deleteOneBatch(LocalDateTime cutoff, int batchSize) {
        var page = orderRepository.findByCreatedTimeBefore(cutoff, PageRequest.of(0, batchSize));
        var orders = page.getContent();
        if (orders.isEmpty()) return 0;

        // (Tùy chọn) filter trạng thái trước khi xoá:
        // orders = orders.stream()
        //   .filter(o -> o.getStatus() == OrderStatusEnum.DELIVERED || o.getStatus() == OrderStatusEnum.CANCELLED)
        //   .toList();

        List<String> orderIds = orders.stream().map(Order::getId).toList();

        // 1) Xóa options
        int opt = orderItemRepository.deleteOptionsByOrderIds(orderIds);
        // 2) Xóa items
        int it = orderItemRepository.deleteItemsByOrderIds(orderIds);
        // 3) Xóa orders
        orderRepository.deleteAllByIdInBatch(orderIds);

        log.debug("[OrderCleanup] batch delete: options={}, items={}, orders={}", opt, it, orderIds.size());
        return orderIds.size();
    }
}
