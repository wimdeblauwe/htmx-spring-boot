package io.github.wimdeblauwe.htmx.spring.boot.mvc;

import org.springframework.ui.Model;

public class CommonHtmxResponses {

    public static HtmxResponse sendAlertPartial(Model model, String alertText) {
        model.addAttribute("alertText", alertText);

        HtmxResponse alertResponse = HtmxResponse.builder()
            .template("users :: alert")
            .trigger("alertSent")
            .build();

        return alertResponse;
    }

}
