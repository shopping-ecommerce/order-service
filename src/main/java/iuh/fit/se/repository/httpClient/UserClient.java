package iuh.fit.se.repository.httpClient;

import iuh.fit.se.configuration.AuthenticationRequestInterceptor;
import iuh.fit.se.configuration.AuthenticationSkipInterceptor;
import iuh.fit.se.dto.response.ApiResponse;
import iuh.fit.se.dto.response.AuthResponse;
import iuh.fit.se.dto.response.SellerResponse;
import iuh.fit.se.dto.response.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "user-service", configuration = {AuthenticationSkipInterceptor.class}
)
public interface UserClient {
    // GET /profiles/{id}
    @GetMapping("/profiles/{id}")
    ApiResponse<UserResponse> getUserById(@PathVariable("id") String id);
    @GetMapping("/sellers/searchBySellerId/{sellerId}")
    ApiResponse<SellerResponse> searchBySellerId(@PathVariable("sellerId") String sellerId);

    @GetMapping("/profiles/canOrder")
    ApiResponse<Boolean> canOrder(@RequestParam String userId);
}