package io.github.wimdeblauwe.htmx.spring.boot.mvc;

import java.util.Map;
import java.util.TreeMap;

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

    @GetMapping("/with-trigger-settle")
    @HxTrigger(value = "eventTriggered", lifecycle = HxTriggerLifecycle.SETTLE)
    @ResponseBody
    public String methodWithHxTriggerAfterSettle() {
        return "";
    }

    @GetMapping("/with-trigger-swap")
    @HxTrigger(value = "eventTriggered", lifecycle = HxTriggerLifecycle.SWAP)
    @ResponseBody
    public String methodWithHxTriggerAfterSwap() {
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

    @GetMapping("/hx-location-without-context-data")
    @HxLocation("/path")
    @ResponseBody
    public String hxLocationWithoutContextData() {
        return "";
    }

    @GetMapping("/hx-location-with-context-data")
    @HxLocation(path = "/path", source = "source", event = "event", handler = "handler", target = "target", swap = "swap")
    @ResponseBody
    public String hxLocationWithContextData() {
        return "";
    }

    @GetMapping("/hx-push-url")
    @HxPushUrl("/path")
    @ResponseBody
    public String hxPushUrl() {
        return "";
    }

    @GetMapping("/hx-redirect")
    @HxRedirect("/path")
    @ResponseBody
    public String hxRedirect() {
        return "";
    }

    @GetMapping("/hx-replace-url")
    @HxReplaceUrl("/path")
    @ResponseBody
    public String hxReplaceUrl() {
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
