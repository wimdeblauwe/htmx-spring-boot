package io.github.wimdeblauwe.hsbt.mvc;

import org.springframework.ui.Model;

public class MyHtmxResponse extends HtmxResponse {

    private final Model model;

    public MyHtmxResponse(Model model) {
        this.model = model;
    }

    public void sendAlertPartial(String alertText) {
        addTemplate("users :: alert");
        model.addAttribute("alertText", alertText);
        addTrigger("alertSent");
    }

}
