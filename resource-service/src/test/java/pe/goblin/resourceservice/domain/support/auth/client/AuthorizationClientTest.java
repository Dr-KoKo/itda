package pe.goblin.resourceservice.domain.support.auth.client;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import pe.goblin.resourceservice.domain.support.auth.exception.AuthorizationClientException;
import pe.goblin.resourceservice.domain.support.auth.model.Permission;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "spring.profiles.active=test")
@AutoConfigureWireMock(port = 8080)
class AuthorizationClientTest {
    @Autowired
    protected WireMockServer wireMockServer;
    @Autowired
    private AuthorizationClient authorizationClient;
    @Value("${wiremock.server.port}")
    private int wireMockPort;

    @BeforeEach
    void before() {
        wireMockServer.resetToDefaultMappings();
    }

    @Nested
    class ObtainClientAccessToken {
        @Test
        void must_invoke_token_endpoint() {
            // given
            String accessToken = "mock-access-token";
            givenThat(post(urlEqualTo("/realms/myrealm/protocol/openid-connect/token"))
                    .withHeader("Content-Type", containing("application/x-www-form-urlencoded"))
                    .withRequestBody(containing("grant_type=client_credentials"))
                    .willReturn(aResponse()
                            .withBody("{\"access_token\":\"" + accessToken + "\", \"refresh_token\":\"refresh_token\", \"expires_in\":3600}")
                            .withStatus(200)));

            // when
            String returnValue = authorizationClient.obtainClientAccessToken();

            // then
            verify(postRequestedFor(urlEqualTo("/realms/myrealm/protocol/openid-connect/token")));
            assertEquals(accessToken, returnValue);
        }

        @Test
        void must_throw_exception_when_error_occur() {
            // given
            givenThat(post(urlEqualTo("/realms/myrealm/protocol/openid-connect/token"))
                    .withHeader("Content-Type", containing("application/x-www-form-urlencoded"))
                    .withRequestBody(containing("grant_type=client_credentials"))
                    .willReturn(aResponse().withStatus(403)));

            // when & then
            Assertions.assertThrows(AuthorizationClientException.class, () -> authorizationClient.obtainClientAccessToken());
        }
    }

    @Nested
    class IntrospectRpt {
        @Test
        void must_invoke_introspection_endpoint() {
            // given
            String rptToken = "mock-rpt-token";
            givenThat(post(urlEqualTo("/realms/myrealm/protocol/openid-connect/token/introspect"))
                    .withHeader("Content-Type", containing("application/x-www-form-urlencoded"))
                    .withRequestBody(containing("grant_type=client_credentials")
                            .and(containing("token_type_hint=requesting_party_token"))
                            .and(containing("token=" + rptToken)))
                    .willReturn(aResponse()
                            .withHeader("Content-Type", "application/json")
                            .withBody("""
                                        {
                                            "active": "true",
                                            "permissions": [
                                                {
                                                    "rsid": "resource-1",
                                                    "rsname": "Resource 1",
                                                    "scopes": ["read", "write"],
                                                    "claims": {}
                                                }
                                            ]
                                        }
                                    """)
                            .withStatus(200)));

            // when
            List<Permission> permissions = authorizationClient.introspectRpt(rptToken);

            // then
            assertNotNull(permissions);
            assertEquals(1, permissions.size());
            assertEquals("resource-1", permissions.get(0).resourceId());
            assertEquals("Resource 1", permissions.get(0).resourceName());
            assertTrue(permissions.get(0).scopes().contains("read"));
            assertTrue(permissions.get(0).scopes().contains("write"));
        }
    }
}
