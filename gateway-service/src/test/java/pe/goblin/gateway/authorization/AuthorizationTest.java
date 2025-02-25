package pe.goblin.gateway.authorization;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.web.reactive.server.WebTestClient;
import pe.goblin.gateway.context.AcceptanceTest;
import pe.goblin.gateway.context.TokenFixture;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

class AuthorizationTest extends AcceptanceTest implements TokenFixture {
    @Autowired
    private WebTestClient webTestClient;

    @Value("${wiremock.server.port}")
    private int wireMockPort;

    @Test
    void shouldBlockRequestWithNoAuthentication() {
        // when
        WebTestClient.ResponseSpec exchange = webTestClient.get()
                .uri("/api/something")
                .exchange();

        // then
        exchange.expectStatus().isUnauthorized()
                .expectHeader().valueEquals("WWW-Authenticate", "Bearer");
    }

    @Test
    void shouldBlockRequestWithMalformedAuthentication() {
        // when
        WebTestClient.ResponseSpec exchange = webTestClient.get()
                .uri("/api/something")
                .header("Authorization", "Bearer " + MALFORM_TOKEN)
                .exchange();

        // then
        exchange.expectStatus().isUnauthorized()
                .expectHeader().valueEquals("WWW-Authenticate", "Bearer error=\"invalid_token\", error_description=\"Bearer token is malformed\", error_uri=\"https://tools.ietf.org/html/rfc6750#section-3.1\"");
    }

    @Test
    void shouldBlockRequestWithExpiredAuthentication() {
        // when
        WebTestClient.ResponseSpec exchange = webTestClient.get()
                .uri("/api/something")
                .header("Authorization", "Bearer " + RSA_SIGNED_EXPIRED_TOKEN)
                .exchange();

        // then
        exchange.expectStatus().isUnauthorized()
                .expectHeader().valueEquals("WWW-Authenticate", "Bearer error=\"invalid_token\", error_description=\"Jwt expired at 2025-02-25T00:01:40Z\", error_uri=\"https://tools.ietf.org/html/rfc6750#section-3.1\"");
    }

    @Test
    void shouldRouteToServer() {
        // given
        givenThat(get(urlEqualTo("/api/something"))
                .willReturn(okJson("")));

        // when
        WebTestClient.ResponseSpec exchange = webTestClient.get()
                .uri("/api/something")
                .header("Authorization", "Bearer " + RSA_SIGNED_TOKEN)
                .exchange();

        // then
        exchange.expectStatus().isOk();
    }
}
