package io.github.wimdeblauwe.htmx.spring.boot.mvc;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.test.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.Duration;

import static io.restassured.RestAssured.get;
import static org.hamcrest.Matchers.nullValue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(HtmxResponseHandlerMethodArgumentResolverIT.TestController.class)
public class HtmxResponseHandlerMethodArgumentResolverIT {

    @LocalServerPort
    private int port;

    @BeforeEach
    void before() {
        RestAssured.port = port;
    }

    @Test
    public void testPreventHistoryUpdate() throws Exception {

        get("/prevent-history-update")
                .then()
                .statusCode(200)
                .header("HX-Push-Url", "false")
                .header("HX-Replace-Url", nullValue());
    }

    @Test
    public void testPushUrl() throws Exception {

        get("/push-url")
                .then()
                .statusCode(200)
                .header("HX-Push-Url", "/path")
                .header("HX-Replace-Url", nullValue());
    }

    @Test
    public void testReselect() throws Exception {

        get("/reselect")
                .then()
                .statusCode(200)
                .header("HX-Reselect", "#container");
    }

    @Test
    public void testReswap() throws Exception {

        get("/reswap")
                .then()
                .statusCode(200)
                .header("HX-Reswap", "innerHTML transition:true focus-scroll:true swap:0ms settle:500ms scroll:#scrollTarget:top show:#showTarget:bottom");
    }

    @Test
    public void testReswapAfterBegin() throws Exception {

        get("/reswap-after-begin")
                .then()
                .statusCode(200)
                .header("HX-Reswap", "afterbegin");
    }

    @Test
    public void testReswapAfterEnd() throws Exception {

        get("/reswap-after-end")
                .then()
                .statusCode(200)
                .header("HX-Reswap", "afterend");
    }

    @Test
    public void testReswapBeforeBegin() throws Exception {

        get("/reswap-before-begin")
                .then()
                .statusCode(200)
                .header("HX-Reswap", "beforebegin");
    }

    @Test
    public void testReswapBeforeEnd() throws Exception {

        get("/reswap-before-end")
                .then()
                .statusCode(200)
                .header("HX-Reswap", "beforeend");
    }

    @Test
    public void testReswapDelete() throws Exception {

        get("/reswap-delete")
                .then()
                .statusCode(200)
                .header("HX-Reswap", "delete");
    }

    @Test
    public void testReswapInnerHtml() throws Exception {

        get("/reswap-inner-html")
                .then()
                .statusCode(200)
                .header("HX-Reswap", "innerHTML");
    }

    @Test
    public void testReswapNone() throws Exception {

        get("/reswap-none")
                .then()
                .statusCode(200)
                .header("HX-Reswap", "none");
    }

    @Test
    public void testReswapOuterHtml() throws Exception {

        get("/reswap-outer-html")
                .then()
                .statusCode(200)
                .header("HX-Reswap", "outerHTML");
    }

    @Test
    public void testRetarget() throws Exception {

        get("/retarget")
                .then()
                .statusCode(200)
                .header("HX-Retarget", "#container");
    }

    @Test
    public void testTrigger() throws Exception {

        get("/trigger")
                .then()
                .statusCode(200)
                .header("HX-Trigger", "trigger1,trigger2");
    }

    @Test
    public void testTriggerAfterSettle() throws Exception {

        get("/trigger-after-settle")
                .then()
                .statusCode(200)
                .header("HX-Trigger-After-Settle", "trigger1,trigger2");
    }

    @Test
    public void testTriggerAfterSwap() throws Exception {

        get("/trigger-after-swap")
                .then()
                .statusCode(200)
                .header("HX-Trigger-After-Swap", "trigger1,trigger2");
    }

    @Test
    public void testResponseBodyReturnValue() throws Exception {

        get("/response-body")
                .then()
                .statusCode(200)
                .header("HX-Trigger", "trigger")
                .header("HX-Reswap", "none");
    }

    @Controller
    static class TestController {

        @GetMapping("/prevent-history-update")
        public String preventHistoryUpdate(HtmxResponse response) {

            response.preventHistoryUpdate();
            return "view";
        }

        @GetMapping("/push-url")
        public String pushUrl(HtmxResponse response) {

            response.setPushUrl("/path");
            return "view";
        }

        @GetMapping("/replace-url")
        public String replaceUrl(HtmxResponse response) {

            response.setReplaceUrl("/path");
            return "view";
        }

        @GetMapping("/reselect")
        public String reselect(HtmxResponse response) {

            response.setReselect("#container");
            return "view";
        }

        @GetMapping("/reswap")
        public String reswap(HtmxResponse response) {

            response.setReswap(HtmxReswap.innerHtml()
                                         .swap(Duration.ZERO)
                                         .settle(Duration.ofMillis(500))
                                         .scroll(HtmxReswap.Position.TOP)
                                         .scrollTarget("#scrollTarget")
                                         .show(HtmxReswap.Position.BOTTOM)
                                         .showTarget("#showTarget")
                                         .transition()
                                         .focusScroll(true));
            return "view";
        }

        @GetMapping("/reswap-after-begin")
        public String reswapAfterBegin(HtmxResponse response) {

            response.setReswap(HtmxReswap.afterBegin());
            return "view";
        }

        @GetMapping("/reswap-after-end")
        public String reswapAfterEnd(HtmxResponse response) {

            response.setReswap(HtmxReswap.afterEnd());
            return "view";
        }

        @GetMapping("/reswap-before-begin")
        public String reswapBeforeBegin(HtmxResponse response) {

            response.setReswap(HtmxReswap.beforeBegin());
            return "view";
        }

        @GetMapping("/reswap-before-end")
        public String reswapBeforeEnd(HtmxResponse response) {

            response.setReswap(HtmxReswap.beforeEnd());
            return "view";
        }

        @GetMapping("/reswap-delete")
        public String reswapDelete(HtmxResponse response) {

            response.setReswap(HtmxReswap.delete());
            return "view";
        }

        @GetMapping("/reswap-inner-html")
        public String reswapInnerHtml(HtmxResponse response) {

            response.setReswap(HtmxReswap.innerHtml());
            return "view";
        }

        @GetMapping("/reswap-none")
        public String reswapNone(HtmxResponse response) {

            response.setReswap(HtmxReswap.none());
            return "view";
        }

        @GetMapping("/reswap-outer-html")
        public String reswapOuterHtml(HtmxResponse response) {

            response.setReswap(HtmxReswap.outerHtml());
            return "view";
        }

        @GetMapping("/retarget")
        public String retarget(HtmxResponse response) {

            response.setRetarget("#container");
            return "view";
        }

        @GetMapping("/trigger")
        public String trigger(HtmxResponse response) {

            response.addTrigger("trigger1");
            response.addTrigger("trigger2");
            return "view";
        }

        @GetMapping("/trigger-after-settle")
        public String triggerAfterSettle(HtmxResponse response) {

            response.addTriggerAfterSettle("trigger1");
            response.addTriggerAfterSettle("trigger2");
            return "view";
        }

        @GetMapping("/trigger-after-swap")
        public String triggerAfterSwap(HtmxResponse response) {

            response.addTriggerAfterSwap("trigger1");
            response.addTriggerAfterSwap("trigger2");
            return "view";
        }

        @GetMapping("/response-body")
        @ResponseBody
        public void responseBody(HtmxResponse response) {

            response.addTrigger("trigger");
            response.setReswap(HtmxReswap.none());
        }

    }

}
