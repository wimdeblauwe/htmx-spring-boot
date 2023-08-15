package io.github.wimdeblauwe.htmx.spring.boot.thymeleaf;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HtmxDialectTestController {

    @GetMapping("/htmx-dialect")
    public String htmxDialect(Model model) {
        model.addAttribute("trueVariable", true);
        model.addAttribute("falseVariable", false);
        model.addAttribute("listVariable", List.of(1, 2, 3));
        model.addAttribute("stringVariable", "someString");
        model.addAttribute("numberVariable", 12345);
        return "htmx-dialect";
    }

    @GetMapping("/htmx-dialect-expression-object-factory")
    public String expressionObjectFactory() {
        return "htmx-dialect-expression-object-factory";
    }
}
