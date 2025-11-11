package io.github.wimdeblauwe.htmx.spring.boot.mvc;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({ HtmxResponseBodyAdviceIT.TestRestController.class })
@AutoConfigureRestTestClient
public class HtmxResponseBodyAdviceIT {

    @Autowired
    RestTestClient webClient;

    @Test
    public void testTrigger() throws Exception {

        get("/trigger")
                .expectHeader()
                .valueEquals("HX-Trigger", "trigger1,trigger2");
    }

    @Test
    public void testExceptionHandler() throws Exception {

        get("/throw-exception")
                .expectHeader()
                .valueEquals("HX-Retarget", "#container");
    }

    private RestTestClient.ResponseSpec get(String uri) {

        return webClient
                .get()
                .uri(uri)
                .exchange()
                .expectStatus()
                .isOk();
    }

    @RestController
    static class TestRestController {

        @GetMapping("/trigger")
        public Object triggerResponseBody(HtmxResponse response) {

            response.addTrigger("trigger1");
            response.addTrigger("trigger2");
            return "response";
        }

        @GetMapping("/throw-exception")
        public Object throwException() {

            throw new RuntimeException();
        }

        @ExceptionHandler(RuntimeException.class)
        public Object handleError(RuntimeException ex, HtmxResponse htmxResponse) {

            htmxResponse.setRetarget("#container");
            return "view";
        }

    }

}