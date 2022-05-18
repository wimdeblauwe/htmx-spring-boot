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

        assertThat(sut.getTemplates()).containsExactlyInAnyOrder(myTemplate, myTemplateAndFragment);
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

}
