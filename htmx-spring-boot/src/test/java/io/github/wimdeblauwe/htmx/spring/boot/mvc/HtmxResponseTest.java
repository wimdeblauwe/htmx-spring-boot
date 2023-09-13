package io.github.wimdeblauwe.htmx.spring.boot.mvc;

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

        assertThat(sut.getTemplates()).extracting(mav -> mav.getViewName()).containsExactly(myTemplate);
    }

    @Test
    public void addingTheSameTemplateASecondTimeShouldIgnoreDuplicates() {
        String myTemplate = "myTemplate";
        String myTemplateAndFragment = "myTemplate :: andFragment";
        sut.addTemplate(myTemplate);
        sut.addTemplate(myTemplateAndFragment);
        sut.addTemplate(myTemplate);

        assertThat(sut.getTemplates()).extracting(mav -> mav.getViewName()).containsExactly(myTemplate, myTemplateAndFragment);
    }

    @Test
    public void testAddingTriggers() {
        HtmxResponse response = new HtmxResponse()
                .addTrigger("event");

        assertThat(response.getTriggers())
                .containsOnlyKeys("event");
    }

    @Test
    public void testAddingTriggersAfterSwap() {
        HtmxResponse response = new HtmxResponse()
                .addTrigger("event", null, HxTriggerLifecycle.SWAP);

        assertThat(response.getTriggersAfterSwap())
                .containsKey("event");
    }

    @Test
    public void testAddingTriggersAfterSettle() {
        HtmxResponse response = new HtmxResponse()
                .addTrigger("event", null, HxTriggerLifecycle.SETTLE);

        assertThat(response.getTriggersAfterSettle())
                .containsKey("event");
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
           .retarget("#theThing")
           .reswap(HxSwapType.AFTER_BEGIN);

        assertThat(sut.getHeaderPushHistory()).isEqualTo("/a/history");
        assertThat(sut.getHeaderRedirect()).isEqualTo("/a/new/page");
        assertThat(sut.getHeaderRefresh()).isTrue();
        assertThat(sut.getHeaderRetarget()).isEqualTo("#theThing");
        assertThat(sut.getHeaderReswap()).isEqualTo("afterbegin");
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

        assertThat(sut.getTemplates()).extracting(mav -> mav.getViewName()).containsExactly(template1, template2);
    }

    @Test
    public void mergingResponsesPreserveTemplateOrder() {
        HtmxResponse response1 = new HtmxResponse().addTemplate("response1 :: template 11")
                                                   .addTemplate("response1 :: template 12");
        HtmxResponse response2 = new HtmxResponse().addTemplate("response2 :: template 21")
                                                   .addTemplate("response2 :: template 22");

        response1.and(response2);

        assertThat(response1.getTemplates()).extracting(mav -> mav.getViewName())
                .hasSize(4)
                .containsExactly("response1 :: template 11",
                                 "response1 :: template 12",
                                 "response2 :: template 21",
                                 "response2 :: template 22");

    }

    @Test
    public void andShouldOverrideProperties() {
        HtmxResponse response1 = new HtmxResponse()
            .retarget("selector1")
            .reswap(HxSwapType.INNER_HTML)
            .browserRedirect("url1")
            .browserRefresh(false)
            .pushHistory("url1");

        HtmxResponse response2 = new HtmxResponse()
            .retarget("selector2")
            .reswap(HxSwapType.OUTER_HTML)
            .browserRedirect("url2")
            .browserRefresh(true)
            .pushHistory("url2");

        response1.and(response2);

        assertThat(response1).satisfies(response -> {
            assertThat(response.getHeaderRetarget()).isEqualTo("selector2");
            assertThat(response.getHeaderReswap()).isEqualTo(HxSwapType.OUTER_HTML.getValue());
            assertThat(response.getHeaderRedirect()).isEqualTo("url2");
            assertThat(response.getHeaderRefresh()).isEqualTo(true);
            assertThat(response.getHeaderPushHistory()).isEqualTo("url2");
        });
    }
}
