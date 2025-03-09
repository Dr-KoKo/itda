package pe.goblin.resourceservice.global.config;

import jakarta.servlet.DispatcherType;
import org.keycloak.adapters.authorization.integration.jakarta.ServletPolicyEnforcerFilter;
import org.keycloak.representations.adapters.config.PolicyEnforcerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import pe.goblin.resourceservice.global.properties.CorsProperties;
import pe.goblin.resourceservice.global.properties.KeycloakProperties;

@Configuration
public class SecurityConfig {
    @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
    private String jwkSetUri;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, CorsProperties corsProperties, KeycloakProperties keycloakProperties) throws Exception {
        // basic security
        http
                .cors(cors -> cors.configurationSource(createCorsConfigurationSource(corsProperties)))
                .csrf(CsrfConfigurer::disable);

        // session
        http
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // authn
        http
                .httpBasic(HttpBasicConfigurer::disable)
                .formLogin(FormLoginConfigurer::disable);

        // authz
        http
                .authorizeHttpRequests(httpRequests -> httpRequests
                        .dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()
                        .anyRequest().fullyAuthenticated())
                .oauth2ResourceServer(oAuth2ResourceServer -> oAuth2ResourceServer
                        .jwt(Customizer.withDefaults()))
                .addFilterBefore(createPolicyEnforcerFilter(keycloakProperties), BearerTokenAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withJwkSetUri(this.jwkSetUri).build();
    }

    /**
     * TODO
     *  Spring Security 와 통합시키기
     *
     * @param policyEnforcerConfig
     * @return
     */
    private ServletPolicyEnforcerFilter createPolicyEnforcerFilter(PolicyEnforcerConfig policyEnforcerConfig) {
        return new ServletPolicyEnforcerFilter(request -> policyEnforcerConfig);
    }

    private CorsConfigurationSource createCorsConfigurationSource(CorsProperties corsProperties) {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(corsProperties.getAllowedOrigins());
        configuration.setAllowedMethods(corsProperties.getAllowedMethods());
        configuration.setAllowCredentials(corsProperties.getAllowCredentials());
        configuration.setAllowedHeaders(corsProperties.getAllowedHeaders());
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
