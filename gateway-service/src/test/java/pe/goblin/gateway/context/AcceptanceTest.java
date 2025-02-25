package pe.goblin.gateway.context;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "spring.profiles.active=test")
@AutoConfigureWireMock(port = 8080)
public abstract class AcceptanceTest {
    @Autowired
    protected WireMockServer wireMockServer;

    @BeforeEach
    public void before() {
        wireMockServer.resetToDefaultMappings();
    }
}
