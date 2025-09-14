package iuh.fit.se.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.engine.jdbc.Size;

import java.time.Instant;
import java.util.List;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductClientResponse {
    String id;
    String sellerId;
    String name;
//    String description;
    List<Size> sizes;
//    Status status;
//    String categoryId;
//    Instant createdAt;
}
