package io.github.wimdeblauwe.htmx.spring.boot.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HtmxRequestMappingHandlerMappingTestController {

    @HxRequest
    @GetMapping("/hx-request")
    @ResponseBody
    public String hxRequest() {
        return "hx-request";
    }

    @HxRequest(boosted = false)
    @GetMapping("/hx-request-ignore-boosted")
    @ResponseBody
    public String hxRequestIgnoreBoosted() {
        return "boosted-ignored";
    }

    @HxRequest(target = "bar")
    @GetMapping("/hx-request-target")
    @ResponseBody
    public String hxRequestTargetBar() {
        return "bar";
    }

    @HxRequest(target = "foo")
    @GetMapping("/hx-request-target")
    @ResponseBody
    public String hxRequestTargetFoo() {
        return "foo";
    }

    @HxRequest(triggerId = "bar")
    @GetMapping("/hx-request-trigger")
    @ResponseBody
    public String hxRequestTriggerIdBar() {
        return "bar";
    }

    @HxRequest(triggerId = "foo")
    @GetMapping("/hx-request-trigger")
    @ResponseBody
    public String hxRequestTriggerIdFoo() {
        return "foo";
    }

    @HxRequest(triggerName = "bar")
    @GetMapping("/hx-request-trigger")
    @ResponseBody
    public String hxRequestTriggerNameBar() {
        return "bar";
    }

    @HxRequest(triggerName = "foo")
    @GetMapping("/hx-request-trigger")
    @ResponseBody
    public String hxRequestTriggerNameFoo() {
        return "foo";
    }

    @HxRequest("bar")
    @GetMapping("/hx-request-value")
    @ResponseBody
    public String hxRequestValueBar() {
        return "bar";
    }

    @HxRequest("foo")
    @GetMapping("/hx-request-value")
    @ResponseBody
    public String hxRequestValueFoo() {
        return "foo";
    }

}
