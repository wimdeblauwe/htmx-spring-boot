package io.github.wimdeblauwe.htmx.spring.boot.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.Duration;
import java.util.Map;
import java.util.TreeMap;

@Controller
@RequestMapping("/hvhi")
public class HtmxResponseHandlerMethodReturnValueHandlerController {

    @GetMapping("/hx-location-with-context-data")
    public HtmxResponse hxLocationWithContextData() {
        var location = new HtmxLocation();
        location.setPath("/path");
        location.setSource("source");
        location.setEvent("event");
        location.setHandler("handler");
        location.setTarget("target");
        location.setSwap("swap");
        location.setSelect("select");
        location.setValues(new TreeMap<>(Map.of("value1", "v1", "value2", "v2")));
        location.setHeaders(new TreeMap<>(Map.of("header1", "v1", "header2", "v2")));

        return HtmxResponse.builder().location(location).build();
    }

    @GetMapping("/hx-location-without-context-data")
    public HtmxResponse hxLocationWithoutContextData() {
        return HtmxResponse.builder().location("/path").build();
    }

    @GetMapping("/hx-location-with-flash-attributes")
    public HtmxResponse hxLocationWithoutContextData(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("flash", "test");
        return HtmxResponse.builder().location("/path").build();
    }

    @GetMapping("/hx-push-url")
    public HtmxResponse hxPushUrl() {
        return HtmxResponse.builder().pushUrl("/path").build();
    }

    @GetMapping("/hx-redirect")
    public HtmxResponse hxRedirect() {
        return HtmxResponse.builder().redirect("/path").build();
    }

    @GetMapping("/hx-redirect-with-flash-attributes")
    public HtmxResponse hxRedirectWithFlashAttributes(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("flash", "test");
        return HtmxResponse.builder().redirect("/path").build();
    }

    @GetMapping("/hx-refresh")
    public HtmxResponse hxRefresh() {
        return HtmxResponse.builder().refresh().build();
    }

    @GetMapping("/hx-replace-url")
    public HtmxResponse hxReplaceUrl() {
        return HtmxResponse.builder().replaceUrl("/path").build();
    }

    @GetMapping("/hx-reselect")
    public HtmxResponse hxReselect() {
        return HtmxResponse.builder().reselect("#target").build();
    }

    @GetMapping("/hx-reswap")
    public HtmxResponse hxReswapOuterHtmlWithSwap() {
        var reswap = HtmxReswap.outerHtml()
                               .settle(Duration.ofMillis(300))
                               .swap(Duration.ofMillis(100))
                               .show(HtmxReswap.Position.TOP)
                               .showTarget("#target")
                               .scroll(HtmxReswap.Position.BOTTOM)
                               .scrollTarget("#target")
                               .transition()
                               .focusScroll(true);

        return HtmxResponse.builder().reswap(reswap).build();
    }

    @GetMapping("/hx-retarget")
    public HtmxResponse hxRetarget() {
        return HtmxResponse.builder().retarget("#target").build();
    }

    @GetMapping("/hx-trigger-after-settle-with-details")
    public HtmxResponse hxTriggerAfterSettleWithDetails() {
        return HtmxResponse.builder()
                           .triggerAfterSettle("event1")
                           .triggerAfterSettle("event2", Map.of("var", "value"))
                           .build();
    }

    @GetMapping("/hx-trigger-after-settle-without-details")
    public HtmxResponse hxTriggerAfterSettleWithoutDetails() {
        return HtmxResponse.builder()
                           .triggerAfterSettle("event1")
                           .triggerAfterSettle("event2")
                           .build();
    }

    @GetMapping("/hx-trigger-after-swap-with-details")
    public HtmxResponse hxTriggerAfterSwapWithDetails() {
        return HtmxResponse.builder()
                           .triggerAfterSwap("event1")
                           .triggerAfterSwap("event2", Map.of("var", "value"))
                           .build();
    }

    @GetMapping("/hx-trigger-after-swap-without-details")
    public HtmxResponse hxTriggerAfterSwapWithoutDetails() {
        return HtmxResponse.builder()
                           .triggerAfterSwap("event1")
                           .triggerAfterSwap("event2")
                           .build();
    }

    @GetMapping("/hx-trigger-with-details")
    public HtmxResponse hxTriggerWithDetails() {
        return HtmxResponse.builder()
                           .trigger("event1")
                           .trigger("event2", Map.of("var", "value"))
                           .build();
    }

    @GetMapping("/hx-trigger-without-details")
    public HtmxResponse hxTriggerWithoutDetails() {
        return HtmxResponse.builder()
                           .trigger("event1")
                           .trigger("event2")
                           .build();
    }

    @GetMapping("/prevent-history-update")
    public HtmxResponse preventHistoryUpdate() {
        return HtmxResponse.builder().preventHistoryUpdate().build();
    }

    @GetMapping("/exception")
    public void throwException() {
        throw new RuntimeException("Fake exception");
    }

    @GetMapping("/argument")
    public String argument(HtmxResponse.Builder htmxResponse) {
        htmxResponse.trigger("event1");
        return "argument";
    }



    @ExceptionHandler(Exception.class)
    public HtmxResponse handleError(Exception ex) {
        return HtmxResponse.builder()
                           .reswap(HtmxReswap.none())
                           .view(new ModelAndView("fragments :: error-message", Map.of("message", ex.getMessage())))
                           .build();
    }
}
