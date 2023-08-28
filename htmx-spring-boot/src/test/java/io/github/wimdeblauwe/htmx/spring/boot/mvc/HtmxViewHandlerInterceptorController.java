package io.github.wimdeblauwe.htmx.spring.boot.mvc;

import java.util.Map;
import java.util.TreeMap;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/hvhi")
class HtmxViewHandlerInterceptorController {

    @GetMapping("/hx-location-without-context-data")
    public HtmxResponse hxLocationWithoutContextData() {
        return HtmxResponse.builder().location("/path").build();
    }

    @GetMapping("/hx-location-with-context-data")
    public HtmxResponse hxLocationWithContextData() {
        var location = new HtmxLocation();
        location.setPath("/path");
        location.setSource("source");
        location.setEvent("event");
        location.setHandler("handler");
        location.setTarget("target");
        location.setSwap("swap");
        location.setValues(new TreeMap<>(Map.of("value1", "v1", "value2", "v2")));
        location.setHeaders(new TreeMap<>(Map.of("header1", "v1", "header2", "v2")));

        return HtmxResponse.builder().location(location).build();
    }

    @GetMapping("/hx-replace-url")
    public HtmxResponse hxReplaceUrl() {
        return HtmxResponse.builder().replaceUrl("/path").build();
    }

}
