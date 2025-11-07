package iuh.fit.se.configuration;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
public class AuthenticationSkipInterceptor implements RequestInterceptor {

    // Tuỳ chọn: token dịch vụ cho call nền (set trong application.yml)
    @Value("${internal.service-token:}")
    private String internalServiceToken;
    @Override
    public void apply(RequestTemplate requestTemplate) {
        var attrs = RequestContextHolder.getRequestAttributes();
        if (attrs instanceof ServletRequestAttributes servletAttrs) {
            String token = servletAttrs.getRequest().getHeader("Authorization");
            if (StringUtils.hasText(token)) {
                requestTemplate.header("Authorization", token);
                return;
            }
        }

        // Không có request (job/async) -> dùng service token nếu có
        if (StringUtils.hasText(internalServiceToken)) {
            requestTemplate.header("Authorization", "Bearer " + internalServiceToken);
            log.debug("[FeignAuth] Use internal service token for background call");
        } else {
            log.debug("[FeignAuth] No request context and no internal token -> skip Authorization header");
        }
    }
}