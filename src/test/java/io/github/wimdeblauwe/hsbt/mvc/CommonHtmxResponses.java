package io.github.wimdeblauwe.hsbt.mvc;

import org.springframework.ui.Model;

public class CommonHtmxResponses {

    public static HtmxResponse sendAlertPartial(Model model, String alertText) {
        HtmxResponse alertResponse = new HtmxResponse();
        alertResponse.addTemplate("users :: alert");
        model.addAttribute("alertText", alertText);
        alertResponse.addTrigger("alertSent");
        return alertResponse;
    }

}
