package io.github.wimdeblauwe.hsbt.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/method-arg-resolver")
public class HtmxHandlerMethodArgumentResolverTestController {

    private final TestService service;

    public HtmxHandlerMethodArgumentResolverTestController(TestService service) {
        this.service = service;
    }

    @GetMapping
    @ResponseBody
    public String htmxRequestDetails(HtmxRequestDetails details) {
        service.doSomething(details);

        return "";
    }
}
