package pe.goblin.gateway.route;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.web.reactive.server.WebTestClient;
import pe.goblin.gateway.context.AcceptanceTest;
import pe.goblin.gateway.context.TokenFixture;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

class ResourceServiceRoutingTest extends AcceptanceTest implements TokenFixture {
    @Autowired
    private WebTestClient webTestClient;

    @Value("${wiremock.server.port}")
    private int wireMockPort;

    @Test
    void shouldRouteToResourceServer() {
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
