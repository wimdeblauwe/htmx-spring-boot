package io.github.wimdeblauwe.htmx.spring.boot.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/")
public class TestController {

    @GetMapping("/without-trigger")
    @ResponseBody
    public String methodWithoutAnnotations() {
        return "";
    }

    @GetMapping("/with-trigger")
    @HxTrigger("eventTriggered")
    @ResponseBody
    public String methodWithHxTrigger() {
        return "";
    }

    @GetMapping("/with-trigger-multiple-events")
    @HxTrigger({ "event1", "event2" })
    @ResponseBody
    public String methodWithHxTriggerAndMultipleEvents() {
        return "";
    }

    @GetMapping("/with-trigger-settle")
    @HxTrigger(value = "eventTriggered", lifecycle = HxTriggerLifecycle.SETTLE)
    @ResponseBody
    public String methodWithHxTriggerAndLifecycleSettle() {
        return "";
    }

    @GetMapping("/with-trigger-swap")
    @HxTrigger(value = "eventTriggered", lifecycle = HxTriggerLifecycle.SWAP)
    @ResponseBody
    public String methodWithHxTriggerAndLifecycleSwap() {
        return "";
    }

    @GetMapping("/with-trigger-after-settle")
    @HxTriggerAfterSettle("eventTriggered")
    @ResponseBody
    public String methodWithHxTriggerAfterSettle() {
        return "";
    }

    @GetMapping("/with-trigger-after-settle-multiple-events")
    @HxTriggerAfterSettle({ "event1", "event2" })
    @ResponseBody
    public String methodWithHxTriggerAfterSettleAndMultipleEvents() {
        return "";
    }

    @GetMapping("/with-trigger-after-swap")
    @HxTriggerAfterSwap("eventTriggered")
    @ResponseBody
    public String methodWithHxTriggerAfterSwap() {
        return "";
    }

    @GetMapping("/with-trigger-after-swap-multiple-events")
    @HxTriggerAfterSwap({ "event1", "event2" })
    @ResponseBody
    public String methodWithHxTriggerAfterSwapAndMultipleEvents() {
        return "";
    }

    @GetMapping("/with-triggers")
    @HxTrigger({ "event1", "event2" })
    @HxTriggerAfterSettle({ "event1", "event2" })
    @HxTriggerAfterSwap({ "event1", "event2" })
    @ResponseBody
    public String methodWithHxTriggers() {
        return "";
    }

    @GetMapping("/updates-sidebar")
    @HxUpdatesSidebar
    @ResponseBody
    public String updatesSidebar() {
        return "";
    }

    @GetMapping("/hx-trigger-alias-for")
    @HxTriggerWithAliasFor(event = "updateTrigger")
    @ResponseBody
    public String hxTriggerWithAliasForOverride() {
        return "";
    }

    @GetMapping("/hx-refresh")
    @HxRefresh
    @ResponseBody
    public String hxRefresh() {
        return "";
    }

    @GetMapping("/hx-vary")
    @ResponseBody
    public String hxVary() {
        return "";
    }

    @GetMapping("/hx-location-without-context-data")
    @HxLocation("/path")
    @ResponseBody
    public String hxLocationWithoutContextData() {
        return "";
    }

    @GetMapping("/hx-location-with-context-data")
    @HxLocation(path = "/path", source = "source", event = "event", handler = "handler", target = "target", swap = "swap", select = "select")
    @ResponseBody
    public String hxLocationWithContextData() {
        return "";
    }

    @GetMapping("/hx-push-url-path")
    @HxPushUrl("/path")
    @ResponseBody
    public String hxPushUrlPath() {
        return "";
    }

    @GetMapping("/hx-push-url")
    @HxPushUrl
    @ResponseBody
    public String hxPushUrl() {
        return "";
    }

    @GetMapping("/hx-push-url-false")
    @HxPushUrl(HtmxValue.FALSE)
    @ResponseBody
    public String hxPushUrlFalse() {
        return "";
    }

    @GetMapping("/hx-redirect")
    @HxRedirect("/path")
    @ResponseBody
    public String hxRedirect() {
        return "";
    }

    @GetMapping("/hx-replace-url-path")
    @HxReplaceUrl("/path")
    @ResponseBody
    public String hxReplaceUrlPath() {
        return "";
    }
    @GetMapping("/hx-replace-url")
    @HxReplaceUrl
    @ResponseBody
    public String hxReplaceUrl() {
        return "";
    }
    @GetMapping("/hx-replace-url-false")
    @HxReplaceUrl(HtmxValue.FALSE)
    @ResponseBody
    public String hxReplaceUrlFalse() {
        return "";
    }

    @GetMapping("/hx-reswap")
    @HxReswap(value = HxSwapType.INNER_HTML, swap = 300)
    @ResponseBody
    public String hxReswap() {
        return "";
    }

    @GetMapping("/hx-retarget")
    @HxRetarget("#target")
    @ResponseBody
    public String hxRetarget() {
        return "";
    }

    @GetMapping("/hx-reselect")
    @HxReselect("#target")
    @ResponseBody
    public String hxReselect() {
        return "";
    }

}
