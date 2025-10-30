package io.github.wimdeblauwe.htmx.spring.boot.mvc;

import static io.restassured.RestAssured.get;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.restassured.RestAssured;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({ HtmxResponseBodyAdviceIT.TestRestController.class })
public class HtmxResponseBodyAdviceIT {

    @LocalServerPort
    private int port;

    @BeforeEach
    void before() {
        RestAssured.port = port;
    }

    @Test
    public void testTrigger() throws Exception {

        get("/trigger")
                .then()
                .statusCode(200)
                .header("HX-Trigger", "trigger1,trigger2");
    }

    @Test
    public void testExceptionHandler() throws Exception {

        get("/throw-exception")
                .then()
                .statusCode(200)
                .header("HX-Retarget", "#container");
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
