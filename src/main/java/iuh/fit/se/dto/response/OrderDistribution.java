package iuh.fit.se.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderDistribution {
    Long pending;
    Long shipping;
    Long completed;
    Long cancelled;
    Map<String, Long> ordersByStatus;
}