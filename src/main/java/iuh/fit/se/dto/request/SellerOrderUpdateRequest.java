package iuh.fit.se.dto.request;

import iuh.fit.se.entity.enums.OrderStatusEnum;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SellerOrderUpdateRequest {
     String orderId;
     String sellerId;
     OrderStatusEnum status;
     String reason; // Optional notes from seller
}
