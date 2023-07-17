package io.github.wimdeblauwe.hsbt.mvc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class HtmxResponseTest {

    private HtmxResponse sut;

    @BeforeEach
    public void setup() {
        sut = new HtmxResponse();
    }

    @Test
    public void respStartsEmpty() {
        assertThat(sut.getTemplates()).isEmpty();
        assertThat(sut.getTriggers()).isEmpty();
        assertThat(sut.getTriggersAfterSettle()).isEmpty();
        assertThat(sut.getTriggersAfterSwap()).isEmpty();
    }

    @Test
    public void addingATemplateShouldWork() {
        String myTemplate = "myTemplate";
        sut.addTemplate(myTemplate);

        assertThat(sut.getTemplates()).containsExactly(myTemplate);
    }

    @Test
    public void addingTheSameTemplateASecondTimeShouldIgnoreDuplicates() {
        String myTemplate = "myTemplate";
        String myTemplateAndFragment = "myTemplate :: andFragment";
        sut.addTemplate(myTemplate);
        sut.addTemplate(myTemplateAndFragment);
        sut.addTemplate(myTemplate);

        assertThat(sut.getTemplates()).containsExactly(myTemplate, myTemplateAndFragment);
    }

    @Test
    public void addingTrigger() {
        String myTrigger = "myEvent";
        sut.addTrigger(myTrigger);
        sut.addTrigger(myTrigger);

        assertThat(sut.getTriggers()).hasSize(1);
        assertThat(sut.getTriggers()).containsOnlyKeys(myTrigger);
    }

    @Test
    public void addingAResponseToExistingMergesTemplatesAndTriggers() {
        String myTrigger = "myEvent";
        String myTemplate = "myTemplate";
        sut.addTrigger(myTrigger);
        sut.addTemplate(myTemplate);

        sut.and(new HtmxResponse().addTemplate("myTemplate2")
                                  .addTrigger("myEvent2")
                        .addTrigger(myTrigger)
                        .addTemplate(myTemplate));

        assertThat(sut.getTriggers()).hasSize(2);
        assertThat(sut.getTemplates()).hasSize(2);
    }

    @Test
    public void extraHxHeaders() {
        sut.pushHistory("/a/history")
                .browserRedirect("/a/new/page")
                .browserRefresh(true)
                .retarget("#theThing");

        assertThat(sut.getHeaderPushHistory()).isEqualTo("/a/history");
        assertThat(sut.getHeaderRedirect()).isEqualTo("/a/new/page");
        assertThat(sut.getHeaderRefresh()).isTrue();
        assertThat(sut.getHeaderRetarget()).isEqualTo("#theThing");
    }

    /**
     * The order of templates can play a role in some scenarios in HTMX,
     * see https://github.com/bigskysoftware/htmx/issues/1198
     */
    @Test
    public void addedTemplatesPreserveTheirOrder() {
        String template1 = "form-validation-fragments :: person-list-item";
        String template2 = "form-validation-fragments :: form";
        sut.addTemplate(template1);
        sut.addTemplate(template2);

        assertThat(sut.getTemplates()).containsExactly(template1, template2);
    }

}
