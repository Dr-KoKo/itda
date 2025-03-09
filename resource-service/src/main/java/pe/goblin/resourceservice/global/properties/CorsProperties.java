package pe.goblin.resourceservice.global.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
@ConfigurationProperties("cors")
public class CorsProperties {
    private List<String> allowedOrigins = new ArrayList<>();
    private List<String> allowedHeaders = new ArrayList<>();
    private List<String> allowedMethods = new ArrayList<>();
    private Boolean allowCredentials;

    public List<String> getAllowedOrigins() {
        return allowedOrigins;
    }

    public List<String> getAllowedHeaders() {
        return allowedHeaders;
    }

    public List<String> getAllowedMethods() {
        return allowedMethods;
    }

    public Boolean getAllowCredentials() {
        return allowCredentials;
    }

    public void setAllowedOrigins(List<String> allowedOrigins) {
        this.allowedOrigins = allowedOrigins;
    }

    public void setAllowedHeaders(List<String> allowedHeaders) {
        this.allowedHeaders = allowedHeaders;
    }

    public void setAllowedMethods(List<String> allowedMethods) {
        this.allowedMethods = allowedMethods;
    }

    public void setAllowCredentials(Boolean allowCredentials) {
        this.allowCredentials = allowCredentials;
    }
}
