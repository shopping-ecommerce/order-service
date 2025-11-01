package iuh.fit.se.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter @Setter
@Configuration
@ConfigurationProperties(prefix = "order.cleanup")
public class OrderCleanupProperties {
    private boolean enabled = true;
    private Integer monthsBeforeDeletion = 12;
    private String cron = "0 0 2 * * MON";
    private Integer batchSize = 100;
}
