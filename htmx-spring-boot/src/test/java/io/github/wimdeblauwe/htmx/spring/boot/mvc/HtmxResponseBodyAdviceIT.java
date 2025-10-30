package io.github.wimdeblauwe.htmx.spring.boot.mvc;

import static io.restassured.RestAssured.get;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import io.restassured.RestAssured;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({ HtmxResponseBodyAdviceIT.TestController.class, HtmxResponseBodyAdviceIT.TestRestController.class })
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

        get("/rest/trigger")
                .then()
                .statusCode(200)
                .header("HX-Trigger", "trigger1,trigger2");
    }

    @Controller
    static class TestController {

        @GetMapping(value = "/trigger")
        @ResponseBody
        public Object triggerResponseBody(HtmxResponse response) {

            response.addTrigger("trigger1");
            response.addTrigger("trigger2");
            return "response";
        }

    }

    @RestController
    static class TestRestController {

        @GetMapping(value = "/rest/trigger")
        public Object triggerResponseBody(HtmxResponse response) {

            response.addTrigger("trigger1");
            response.addTrigger("trigger2");
            return "response";
        }

    }

}