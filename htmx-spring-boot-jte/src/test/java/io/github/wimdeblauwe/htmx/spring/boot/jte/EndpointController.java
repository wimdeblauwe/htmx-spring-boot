package io.github.wimdeblauwe.htmx.spring.boot.jte;

import io.github.wimdeblauwe.htmx.spring.boot.endpoint.HtmxEndpoint;

import java.util.Map;

import java.util.Optional;

import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class EndpointController {


    public HtmxEndpoint<ModelMap, ModelAndView> htmxTestEndpoint = new HtmxEndpoint<>(
            "/test", HttpMethod.GET, this::test);

    private ModelAndView test() {
        return new ModelAndView("test",Map.of("endpointController", this, "userForm", Optional.empty()));
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
        return new ModelAndView("test",Map.of("endpointController", this,"userForm",Optional.ofNullable(userForm)));
    }
}
