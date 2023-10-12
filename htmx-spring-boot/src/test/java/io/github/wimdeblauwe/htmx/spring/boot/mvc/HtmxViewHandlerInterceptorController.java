package io.github.wimdeblauwe.htmx.spring.boot.mvc;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/hvhi")
public class HtmxViewHandlerInterceptorController {

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

}
