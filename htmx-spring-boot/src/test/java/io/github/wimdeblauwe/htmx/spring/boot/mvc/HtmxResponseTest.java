package io.github.wimdeblauwe.htmx.spring.boot.mvc;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class HtmxResponseTest {

    @Test
    public void respStartsEmpty() {
        HtmxResponse response = HtmxResponse.builder().build();

        assertThat(response.getTemplates()).isEmpty();
        assertThat(response.getTriggers()).isEmpty();
        assertThat(response.getTriggersAfterSettle()).isEmpty();
        assertThat(response.getTriggersAfterSwap()).isEmpty();
    }

    @Test
    public void addingATemplateShouldWork() {
        String myTemplate = "myTemplate";

        var response = HtmxResponse.builder()
            .template(myTemplate)
            .build();

        assertThat(response.getTemplates()).extracting(mav -> mav.getViewName()).containsExactly(myTemplate);
    }

    @Test
    public void addingTheSameTemplateASecondTimeShouldIgnoreDuplicates() {
        String myTemplate = "myTemplate";
        String myTemplateAndFragment = "myTemplate :: andFragment";

        var response = HtmxResponse.builder()
            .template(myTemplate)
            .template(myTemplateAndFragment)
            .template(myTemplate)
            .build();

        assertThat(response.getTemplates()).extracting(mav -> mav.getViewName()).containsExactly(myTemplate, myTemplateAndFragment);
    }

    @Test
    public void addingTrigger() {
        String myTrigger = "myEvent";

        var response = HtmxResponse.builder()
            .trigger(myTrigger)
            .trigger(myTrigger)
            .build();

        assertThat(response.getTriggers()).hasSize(1);
        assertThat(response.getTriggers()).first().extracting(HtmxTrigger::getEventName).isEqualTo("myEvent");
    }

    @Test
    public void addingAResponseToExistingMergesTemplatesAndTriggers() {
        String myTrigger = "myEvent";
        String myTemplate = "myTemplate";

        var response2 = HtmxResponse.builder()
            .template("myTemplate2")
            .trigger("myEvent2")
            .trigger(myTrigger)
            .template(myTemplate)
            .build();

        var response1 = HtmxResponse.builder()
            .trigger(myTrigger)
            .template(myTemplate)
            .and(response2)
            .build();

        assertThat(response1.getTriggers()).hasSize(2);
        assertThat(response1.getTemplates()).hasSize(2);
    }

    @Test
    public void extraHxHeaders() {
        var response = HtmxResponse.builder()
            .pushUrl("/a/history")
            .redirect("/a/new/page")
            .refresh()
            .retarget("#theThing")
            .reswap(HtmxReswap.afterBegin())
            .build();

        assertThat(response.getPushUrl()).isEqualTo("/a/history");
        assertThat(response.getRedirect()).isEqualTo("/a/new/page");
        assertThat(response.isRefresh()).isTrue();
        assertThat(response.getRetarget()).isEqualTo("#theThing");
        assertThat(response.getReswap()).isEqualTo(HtmxReswap.afterBegin());
    }

    /**
     * The order of templates can play a role in some scenarios in HTMX,
     * see https://github.com/bigskysoftware/htmx/issues/1198
     */
    @Test
    public void addedTemplatesPreserveTheirOrder() {
        String template1 = "form-validation-fragments :: person-list-item";
        String template2 = "form-validation-fragments :: form";

        var response = HtmxResponse.builder()
            .template(template1)
            .template(template2)
            .build();

        assertThat(response.getTemplates()).extracting(mav -> mav.getViewName()).containsExactly(template1, template2);
    }

    @Test
    public void mergingResponsesPreserveTemplateOrder() {
        var response2 = HtmxResponse.builder()
            .template("response2 :: template 21")
            .template("response2 :: template 22")
            .build();

        var response1 = HtmxResponse.builder()
            .template("response1 :: template 11")
            .template("response1 :: template 12")
            .and(response2)
            .build();

        assertThat(response1.getTemplates())
                .extracting(mav -> mav.getViewName())
                .hasSize(4)
                .containsExactly("response1 :: template 11",
                                 "response1 :: template 12",
                                 "response2 :: template 21",
                                 "response2 :: template 22");

    }
}
