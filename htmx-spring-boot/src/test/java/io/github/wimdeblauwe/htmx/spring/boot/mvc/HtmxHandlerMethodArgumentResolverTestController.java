package io.github.wimdeblauwe.htmx.spring.boot.mvc;

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
    public String htmxRequestDetails(HtmxRequest details) {
        service.doSomething(details);

        return "";
    }

    @GetMapping("/users")
    @HxRequest
    public String htmxRequest(HtmxRequest details) {
        service.doSomething(details);

        return "users :: list";
    }

    @GetMapping("/users")
    public String normalRequest(HtmxRequest details) {
        service.doSomething(details);

        return "users";
    }

    @HxGetMapping("/users/inherited")
    public String htmxRequestInheritance(HtmxRequest details) {
        service.doSomething(details);

        return "users :: list";
    }

    @GetMapping("/users/inherited")
    public String normalRequestInheritance(HtmxRequest details) {
        service.doSomething(details);

        return "users";
    }
}
