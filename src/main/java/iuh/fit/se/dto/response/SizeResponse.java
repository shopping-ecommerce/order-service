package iuh.fit.se.dto.response;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record SizeResponse(
        String size,
        BigDecimal  price,
        BigDecimal  compareAtPrice,
        Integer quantity,
        Boolean available
) {
}
