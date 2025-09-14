package iuh.fit.se.dto.request;

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
     String status; // PROCESSING, SHIPPED, DELIVERED
     String reason; // Optional notes from seller
}
