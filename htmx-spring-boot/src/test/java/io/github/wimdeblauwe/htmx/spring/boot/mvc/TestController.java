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

}
