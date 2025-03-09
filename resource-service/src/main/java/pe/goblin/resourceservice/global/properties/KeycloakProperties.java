package pe.goblin.resourceservice.global.properties;

import org.keycloak.representations.adapters.config.PolicyEnforcerConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("keycloak")
public class KeycloakProperties extends PolicyEnforcerConfig {
}
