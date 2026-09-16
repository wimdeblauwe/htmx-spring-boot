package io.github.wimdeblauwe.htmx.spring.boot.mvc;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.security.autoconfigure.web.servlet.ServletWebSecurityAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.lang.Nullable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.stereotype.Controller;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.Duration;
import java.util.stream.Stream;

import static io.github.wimdeblauwe.htmx.spring.boot.mvc.HtmxResponseHeader.*;

@SpringBootTest(
        classes = HtmxResponseHandlerMethodArgumentResolverIT.Application.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(HtmxResponseHandlerMethodArgumentResolverIT.TestController.class)
@AutoConfigureRestTestClient
@WithMockUser
public class HtmxResponseHandlerMethodArgumentResolverIT {

    @Autowired
    RestTestClient webClient;

    @ParameterizedTest
    @MethodSource("swapTypes")
    public void testReswapType(HxSwapType type) throws Exception {

        get("/reswap-type?type={type}", type.name())
                .expectHeader()
                .valueEquals(HX_RESWAP.getValue(), type.getValue());
    }

    static Stream<Arguments> swapTypes() {
        return Stream.of(HxSwapType.values()).map(Arguments::of);
    }

    @Test
    public void testPreventHistoryUpdate() throws Exception {

        get("/prevent-history-update")
                .expectHeader()
                .doesNotExist(HX_REPLACE_URL.getValue())
                .expectHeader()
                .valueEquals(HX_PUSH_URL.getValue(), "false");
    }

    @Test
    public void testPushUrl() throws Exception {

        get("/push-url")
                .expectHeader()
                .doesNotExist(HX_REPLACE_URL.getValue())
                .expectHeader()
                .valueEquals(HX_PUSH_URL.getValue(), "/path");
    }

    @Test
    public void testReselect() throws Exception {

        get("/reselect")
                .expectHeader()
                .valueEquals(HX_RESELECT.getValue(), "#container");
    }

    @Test
    public void testReswap() throws Exception {

        get("/reswap")
                .expectHeader()
                .valueEquals(HX_RESWAP.getValue(), "innerHTML transition:true focus-scroll:true swap:0ms settle:500ms scroll:#scrollTarget:top show:#showTarget:bottom target:#target ignoreTitle:true strip:true swapEmpty:true");
    }

    @Test
    public void testReswapDefaultWithSwapTiming() throws Exception {

        get("/reswap-default-with-swap-timing")
                .expectHeader()
                .valueEquals(HX_RESWAP.getValue(), "swap:0ms");
    }

    @Test
    public void testReswapShowNone() throws Exception {

        get("/reswap-show-none")
                .expectHeader()
                .valueEquals(HX_RESWAP.getValue(), "show:none");
    }

    @Test
    public void testRetarget() throws Exception {

        get("/retarget")
                .expectHeader()
                .valueEquals(HX_RETARGET.getValue(), "#container");
    }

    @Test
    public void testTrigger() throws Exception {

        get("/trigger")
                .expectHeader()
                .valueEquals(HX_TRIGGER.getValue(), "trigger1,trigger2");
    }

    @Test
    public void testResponseBodyReturnValue() throws Exception {

        get("/response-body")
                .expectHeader()
                .valueEquals(HX_TRIGGER.getValue(), "trigger")
                .expectHeader()
                .valueEquals(HX_RESWAP.getValue(), "none");
    }

    private RestTestClient.ResponseSpec get(String uri, @Nullable Object... uriVariables) {

        return webClient
                .get()
                .uri(uri, uriVariables)
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
                                         .focusScroll(true)
                                         .target("#target")
                                         .ignoreTitle()
                                         .strip()
                                         .swapEmpty());
            return "view";
        }

        @GetMapping("/reswap-type")
        public String reswapType(HxSwapType type, HtmxResponse response) {

            HtmxReswap reswap = switch (type) {
                case AFTER -> HtmxReswap.after();
                case AFTER_BEGIN -> HtmxReswap.afterBegin();
                case AFTER_END -> HtmxReswap.afterEnd();
                case APPEND -> HtmxReswap.append();
                case BEFORE -> HtmxReswap.before();
                case BEFORE_BEGIN -> HtmxReswap.beforeBegin();
                case BEFORE_END -> HtmxReswap.beforeEnd();
                case DEFAULT -> HtmxReswap.defaultSwap();
                case DELETE -> HtmxReswap.delete();
                case INNER_HTML -> HtmxReswap.innerHtml();
                case INNER_MORPH -> HtmxReswap.innerMorph();
                case NONE -> HtmxReswap.none();
                case OUTER_HTML -> HtmxReswap.outerHtml();
                case OUTER_MORPH -> HtmxReswap.outerMorph();
                case OUTER_SYNC -> HtmxReswap.outerSync();
                case PREPEND -> HtmxReswap.prepend();
                case TEXT_CONTENT -> HtmxReswap.textContent();
                case UPSERT -> HtmxReswap.upsert();
                default -> throw new IllegalArgumentException("Unknown type: " + type);
            };

            response.setReswap(reswap);
            return "view";
        }

        @GetMapping("/reswap-default-with-swap-timing")
        public String reswapDefaultWithSwapTiming(HtmxResponse response) {

            response.setReswap(HtmxReswap.defaultSwap()
                                         .swap(Duration.ZERO));
            return "view";
        }

        @GetMapping("/reswap-show-none")
        public String reswapShowNone(HtmxResponse response) {

            response.setReswap(HtmxReswap.defaultSwap()
                                         .show(HtmxReswap.Position.NONE));
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
