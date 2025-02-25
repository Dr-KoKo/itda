package pe.goblin.gateway.fallback;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;
import pe.goblin.gateway.context.AcceptanceTest;
import pe.goblin.gateway.context.TokenFixture;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

class FallbackTest extends AcceptanceTest implements TokenFixture {
    @Autowired
    private WebTestClient webTestClient;

    @Value("${wiremock.server.port}")
    private int wireMockPort;

    @Test
    void shouldFallbackWhenResourceServiceNotResponse() {
        // given
        givenThat(get(urlEqualTo("/api/something"))
                .willReturn(aResponse().withFixedDelay(Integer.MAX_VALUE)));

        // when
        WebTestClient.ResponseSpec exchange = webTestClient.get()
                .uri("/api/something")
                .header("Authorization", "Bearer " + RSA_SIGNED_TOKEN)
                .exchange();

        // then
        exchange.expectStatus().isEqualTo(HttpStatus.SERVICE_UNAVAILABLE)
                .expectBody(String.class).isEqualTo("Resource Service is currently unavailable. Please try again later.");
    }
}
