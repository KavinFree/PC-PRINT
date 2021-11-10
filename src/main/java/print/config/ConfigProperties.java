package print.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "site")
public class ConfigProperties {
    private String appName;
    private String basePath;
    private int printx;
    private int printw;
}