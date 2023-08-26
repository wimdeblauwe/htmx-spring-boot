package io.github.wimdeblauwe.htmx.spring.boot.mvc;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableAutoConfiguration(exclude = {SecurityAutoConfiguration.class}) // Security is on by default
@MockBean(PartialsController.TodoRepository.class)
class HtmxHandlerInterceptorIT {

    @Autowired
    protected TestRestTemplate restTemplate;

    @LocalServerPort
    protected int port;

    @Test
    public void testHeaderIsSetOnResponseIfHxTriggerIsPresent() {
        final ResponseEntity<String> response
                = restTemplate.getForEntity("http://localhost:" + port + "/with-trigger", String.class);

        // Passes if preHandle is used, fails if postHandle is used (HtmxHandlerInterceptor)
        assertThat(response.getHeaders().get("HX-Trigger")).isNotNull();
        assertThat(response.getHeaders().get("HX-Trigger")).asList().containsOnly("eventTriggered");
    }
}
