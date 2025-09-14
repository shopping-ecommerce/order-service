package iuh.fit.se.repository.httpClient;


import iuh.fit.se.configuration.AuthenticationRequestInterceptor;

import iuh.fit.se.dto.response.ApiResponse;
import iuh.fit.se.dto.response.AuthResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "auth-service", configuration = {AuthenticationRequestInterceptor.class})
public interface AuthClient {
    @GetMapping("/users/myinfo")
    ApiResponse<AuthResponse> getMyInfo();
    @GetMapping("/users/search/{userId}")
    ApiResponse<AuthResponse> getUserById(@PathVariable("userId") String userId);
}
