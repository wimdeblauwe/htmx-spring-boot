package io.github.wimdeblauwe.htmx.spring.boot.mvc;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.security.autoconfigure.web.servlet.ServletWebSecurityAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.stereotype.Controller;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.Duration;

@SpringBootTest(
        classes = HtmxResponseHandlerMethodArgumentResolverIT.Application.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(HtmxResponseHandlerMethodArgumentResolverIT.TestController.class)
@AutoConfigureRestTestClient
@WithMockUser
public class HtmxResponseHandlerMethodArgumentResolverIT {

    @Autowired
    RestTestClient webClient;

    @Test
    public void testPreventHistoryUpdate() throws Exception {

        get("/prevent-history-update")
                .expectHeader()
                .doesNotExist("HX-Replace-Url")
                .expectHeader()
                .valueEquals("HX-Push-Url", "false");
    }

    @Test
    public void testPushUrl() throws Exception {

        get("/push-url")
                .expectHeader()
                .doesNotExist("HX-Replace-Url")
                .expectHeader()
                .valueEquals("HX-Push-Url", "/path");
    }

    @Test
    public void testReselect() throws Exception {

        get("/reselect")
                .expectHeader()
                .valueEquals("HX-Reselect", "#container");
    }

    @Test
    public void testReswap() throws Exception {

        get("/reswap")
                .expectHeader()
                .valueEquals("HX-Reswap", "innerHTML transition:true focus-scroll:true swap:0ms settle:500ms scroll:#scrollTarget:top show:#showTarget:bottom");
    }

    @Test
    public void testReswapAfterBegin() throws Exception {

        get("/reswap-after-begin")
                .expectHeader()
                .valueEquals("HX-Reswap", "afterbegin");
    }

    @Test
    public void testReswapAfterEnd() throws Exception {

        get("/reswap-after-end")
                .expectHeader()
                .valueEquals("HX-Reswap", "afterend");
    }

    @Test
    public void testReswapBeforeBegin() throws Exception {

        get("/reswap-before-begin")
                .expectHeader()
                .valueEquals("HX-Reswap", "beforebegin");
    }

    @Test
    public void testReswapBeforeEnd() throws Exception {

        get("/reswap-before-end")
                .expectHeader()
                .valueEquals("HX-Reswap", "beforeend");
    }

    @Test
    public void testReswapDelete() throws Exception {

        get("/reswap-delete")
                .expectHeader()
                .valueEquals("HX-Reswap", "delete");
    }

    @Test
    public void testReswapInnerHtml() throws Exception {

        get("/reswap-inner-html")
                .expectHeader()
                .valueEquals("HX-Reswap", "innerHTML");
    }

    @Test
    public void testReswapNone() throws Exception {

        get("/reswap-none")
                .expectHeader()
                .valueEquals("HX-Reswap", "none");
    }

    @Test
    public void testReswapOuterHtml() throws Exception {

        get("/reswap-outer-html")
                .expectHeader()
                .valueEquals("HX-Reswap", "outerHTML");
    }

    @Test
    public void testRetarget() throws Exception {

        get("/retarget")
                .expectHeader()
                .valueEquals("HX-Retarget", "#container");
    }

    @Test
    public void testTrigger() throws Exception {

        get("/trigger")
                .expectHeader()
                .valueEquals("HX-Trigger", "trigger1,trigger2");
    }

    @Test
    public void testTriggerAfterSettle() throws Exception {

        get("/trigger-after-settle")
                .expectHeader()
                .valueEquals("HX-Trigger-After-Settle", "trigger1,trigger2");
    }

    @Test
    public void testTriggerAfterSwap() throws Exception {

        get("/trigger-after-swap")
                .expectHeader()
                .valueEquals("HX-Trigger-After-Swap", "trigger1,trigger2");
    }

    @Test
    public void testResponseBodyReturnValue() throws Exception {

        get("/response-body")
                .expectHeader()
                .valueEquals("HX-Trigger", "trigger")
                .expectHeader()
                .valueEquals("HX-Reswap", "none");
    }

    private RestTestClient.ResponseSpec get(String uri) {

        return webClient
                .get()
                .uri(uri)
                .exchange()
                .expectStatus()
                .isOk();
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

    @SpringBootApplication(exclude = ServletWebSecurityAutoConfiguration.class)
    static class Application {

        public static void main(String[] args) {
            SpringApplication.run(Application.class);
        }

    }

}
