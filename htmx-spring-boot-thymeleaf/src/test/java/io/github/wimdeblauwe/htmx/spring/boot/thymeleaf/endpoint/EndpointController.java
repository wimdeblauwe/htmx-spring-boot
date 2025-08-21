package io.github.wimdeblauwe.htmx.spring.boot.thymeleaf.endpoint;

import io.github.wimdeblauwe.htmx.spring.boot.endpoint.HtmxEndpoint;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class EndpointController {


    public HtmxEndpoint<?, ModelAndView> htmxTestEndpoint = new HtmxEndpoint<>(
            "/test", HttpMethod.GET, this::test);

    private ModelAndView test() {
        return new ModelAndView("test");
    }


    public record UserForm(
            String userName, String password) {
    }

    public HtmxEndpoint<UserForm, ModelAndView> createUserEndpoint = new HtmxEndpoint<>(
            "/createUser",
            HttpMethod.POST,
            this::createUser
    );

    private ModelAndView createUser(UserForm userForm) {
        return new ModelAndView("test");
    }
}
