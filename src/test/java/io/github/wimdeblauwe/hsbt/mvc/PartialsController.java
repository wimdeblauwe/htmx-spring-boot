package io.github.wimdeblauwe.hsbt.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PartialsController {

    @GetMapping("/partials/first")
    public HtmxPartials getFirstPartials() {
        return new HtmxPartials().replace("aThing").with("users :: list");
    }

    @GetMapping("/partials/main-and-partial")
    public HtmxPartials getMainAndPartial(Model model) {
        model.addAttribute("userCount", 5);
        return new HtmxPartials()
                .main("users :: list")
                .replace("userCounts").with("users :: count");
    }

}
