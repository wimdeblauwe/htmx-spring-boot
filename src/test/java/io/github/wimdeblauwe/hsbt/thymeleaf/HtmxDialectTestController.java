package io.github.wimdeblauwe.hsbt.thymeleaf;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/htmx-dialect")
public class HtmxDialectTestController {
    @GetMapping
    public String htmxDialect(Model model) {
        model.addAttribute("trueVariable", true);
        model.addAttribute("falseVariable", false);
        model.addAttribute("listVariable", List.of(1, 2, 3));
        model.addAttribute("stringVariable", "someString");
        model.addAttribute("numberVariable", 12345);
        return "htmx-dialect-test";
    }
}
