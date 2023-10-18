package io.github.wimdeblauwe.htmx.spring.boot.mvc;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

public class HtmxResponseTest {

    @Test
    public void testEmptyResponse() {
        var response = HtmxResponse.builder().build();

        assertThat(response.getViews()).isEmpty();
        assertThat(response.getTriggers()).isEmpty();
        assertThat(response.getTriggersAfterSettle()).isEmpty();
        assertThat(response.getTriggersAfterSwap()).isEmpty();
    }

    @Test
    public void testAddingView() {
        String view = "my-view";

        var response = HtmxResponse.builder()
                                   .view(view)
                                   .build();

        assertThat(response.getViews())
                .extracting(mav -> mav.getViewName())
                .containsExactly(view);
    }

    @Test
    public void testAddingViewMultipleTimesShouldBeIgnored() {
        String view = "my-view";
        String viewWithMarkupSelector = "my-view :: fragment";

        var response = HtmxResponse.builder()
                                   .view(view)
                                   .view(viewWithMarkupSelector)
                                   .view(view)
                                   .build();

        assertThat(response.getViews())
                .extracting(mav -> mav.getViewName())
                .containsExactly(view, viewWithMarkupSelector);
    }

    @Test
    public void testAddingTrigger() {
        HtmxResponse response = HtmxResponse.builder()
                                            .trigger("event")
                                            .build();

        assertThat(response.getTriggersInternal())
                .extracting(HtmxTrigger::getEventName)
                .containsExactly("event");
    }

    @Test
    public void testAddingTriggerWithDetails() {
        var eventDetail = Map.of("detail1", "message1", "detail2", "message2");
        HtmxResponse response = HtmxResponse.builder()
            .trigger("event", eventDetail)
            .build();

        assertThat(response.getTriggersInternal())
            .containsExactly(new HtmxTrigger("event", eventDetail));
    }

    @Test
    public void testAddingTriggerAfterSwap() {
        HtmxResponse response = HtmxResponse.builder()
                .triggerAfterSwap("event")
                .build();

        assertThat(response.getTriggersAfterSwapInternal())
                .extracting(HtmxTrigger::getEventName)
                .containsExactly("event");
    }

    @Test
    public void testAddingTriggerAfterSwapWithDetails() {
        var eventDetail = Map.of("detail1", "message1", "detail2", "message2");
        HtmxResponse response = HtmxResponse.builder()
            .triggerAfterSwap("event", eventDetail)
            .build();

        assertThat(response.getTriggersAfterSwapInternal())
            .containsExactly(new HtmxTrigger("event", eventDetail));
    }

    @Test
    public void testAddingTriggerAfterSettle() {
        HtmxResponse response = HtmxResponse.builder()
                .triggerAfterSettle("event")
                .build();

        assertThat(response.getTriggersAfterSettleInternal())
                .extracting(HtmxTrigger::getEventName)
                .containsExactly("event");
    }

    @Test
    public void testAddingTriggerAfterSettleWithDetails() {
        var eventDetail = Map.of("detail1", "message1", "detail2", "message2");
        HtmxResponse response = HtmxResponse.builder()
            .triggerAfterSettle("event", eventDetail)
            .build();

        assertThat(response.getTriggersAfterSettleInternal())
            .containsExactly(new HtmxTrigger("event", eventDetail));
    }

    @Test
    public void testAddingResponseToExistingOneShouldMergeTemplatesAndTriggers() {
        var response1 = HtmxResponse.builder()
                                    .view("view1")
                                    .trigger("trigger1")
                                    .view("view2")
                                    .trigger("trigger2")
                                    .build();

        var response2 = HtmxResponse.builder()
                                    .view("view1")
                                    .trigger("trigger1")
                                    .and(response1)
                                    .build();

        assertThat(response2).satisfies(response -> {
            assertThat(response.getViews())
                    .extracting(m -> m.getViewName())
                    .containsExactly("view1", "view2");

            assertThat(response.getTriggersInternal())
                    .extracting(HtmxTrigger::getEventName)
                    .containsExactly("trigger1", "trigger2");
        });
    }

    @Test
    public void testAddingResponseToExistingOneShouldOverrideProperties() {
        var response1 = HtmxResponse.builder()
                                    .location("location1")
                                    .pushUrl("url1")
                                    .redirect("url1")
                                    .replaceUrl("url1")
                                    .reswap(HtmxReswap.innerHtml())
                                    .retarget("selector1");

        var response2 = HtmxResponse.builder()
                                    .location("location2")
                                    .pushUrl("url2")
                                    .redirect("url2")
                                    .replaceUrl("url2")
                                    .reswap(HtmxReswap.outerHtml())
                                    .retarget("selector2")
                                    .refresh();

        response1.and(response2.build());

        assertThat(response1.build()).satisfies(response -> {
            assertThat(response.getLocation()).isEqualTo(new HtmxLocation("location2"));
            assertThat(response.getPushUrl()).isEqualTo(null);
            assertThat(response.getRedirect()).isEqualTo("url2");
            assertThat(response.getReplaceUrl()).isEqualTo("url2");
            assertThat(response.getReswap()).isEqualTo(HtmxReswap.outerHtml());
            assertThat(response.getRetarget()).isEqualTo("selector2");
            assertThat(response.isRefresh()).isEqualTo(true);
        });
    }

    @Test
    public void testResponseHeaderProperties() {
        var response = HtmxResponse.builder()
                .trigger("my-trigger")
                .pushUrl("/a/history")
                .redirect("/a/new/page")
                .refresh()
                .retarget("#theThing")
                .reswap(HtmxReswap.afterBegin())
                .build();

        assertThat(response.getTriggers())
            .containsOnlyKeys("my-trigger");
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
    public void testAddingViewsShouldPreserveOrder() {
        String view1 = "view1";
        String view2 = "view2";

        var response = HtmxResponse.builder()
            .view(view1)
            .view(view2)
            .build();

        assertThat(response.getViews())
                .extracting(mav -> mav.getViewName())
                .containsExactly(view1, view2);
    }

    @Test
    public void testAddingResponseToExistingOneShouldPreserveViewOrder() {
        var response2 = HtmxResponse.builder()
                .view("view3")
                .view("view4")
                .build();

        var response1 = HtmxResponse.builder()
                .view("view1")
                .view("view2")
                .and(response2)
                .build();

        assertThat(response1.getViews())
                .extracting(mav -> mav.getViewName())
                .hasSize(4)
                .containsExactly("view1", "view2", "view3", "view4");
    }

    @Deprecated
    @Nested
    @DisplayName("Tests for deprecated methods")
    class DeprecatedTest {
        @Test
        public void testEmptyResponse() {
            var response = new HtmxResponse();

            assertThat(response.getTemplates()).isEmpty();
            assertThat(response.getTriggers()).isEmpty();
            assertThat(response.getTriggersAfterSettle()).isEmpty();
            assertThat(response.getTriggersAfterSwap()).isEmpty();
        }

        @Test
        public void testAddingTemplate() {
            String template = "my-template";

            var response = new HtmxResponse()
                .addTemplate(template);

            assertThat(response.getTemplates())
                .extracting(mav -> mav.getViewName())
                .containsExactly(template);
        }

        @Test
        public void testAddingTemplateMultipleTimesShouldBeIgnored() {
            String template = "my-template";
            String templateWithMarkupSelector = "my-template :: fragment";

            var response = new HtmxResponse()
                .addTemplate(template)
                .addTemplate(templateWithMarkupSelector)
                .addTemplate(template);

            assertThat(response.getViews())
                .extracting(mav -> mav.getViewName())
                .containsExactly(template, templateWithMarkupSelector);
        }

        @Test
        public void testAddingTrigger() {
            var response = new HtmxResponse()
                .addTrigger("event");

            assertThat(response.getTriggers())
                .containsOnlyKeys("event");
        }

        @Test
        public void testAddingTriggerWithDetails() {
            var response = new HtmxResponse()
                    .addTrigger("event", "message", HxTriggerLifecycle.RECEIVE);

            assertThat(response.getTriggers())
                    .containsExactly(Map.entry("event", "message"));
        }

        @Test
        public void testAddingTriggerAfterSwap() {
            var response = new HtmxResponse()
                .addTrigger("event", null, HxTriggerLifecycle.SWAP);

            assertThat(response.getTriggersAfterSwap())
                .containsOnlyKeys("event");
        }

        @Test
        public void testAddingTriggerAfterSwapWithDetails() {
            var response = new HtmxResponse()
                    .addTrigger("event", "message", HxTriggerLifecycle.SWAP);

            assertThat(response.getTriggersAfterSwap())
                    .containsExactly(Map.entry("event", "message"));
        }

        @Test
        public void testAddingTriggerAfterSettle() {
            var response = new HtmxResponse()
                .addTrigger("event", null, HxTriggerLifecycle.SETTLE);

            assertThat(response.getTriggersAfterSettle())
                .containsOnlyKeys("event");
        }

        @Test
        public void testAddingTriggerAfterSettleWithDetails() {
            var response = new HtmxResponse()
                    .addTrigger("event", "message", HxTriggerLifecycle.SETTLE);

            assertThat(response.getTriggersAfterSettle())
                    .containsExactly(Map.entry("event", "message"));
        }

        @Test
        public void testAddingResponseToExistingOneShouldMergeTemplatesAndTriggers() {
            var response1 = new HtmxResponse()
                .addTemplate("view1")
                .addTrigger("trigger1")
                .addTemplate("view2")
                .addTrigger("trigger2");

            var response2 = new HtmxResponse()
                .addTemplate("view1")
                .addTrigger("trigger1")
                .and(response1);

            assertThat(response2).satisfies(response -> {
                assertThat(response.getViews())
                    .extracting(m -> m.getViewName())
                    .containsExactly("view1", "view2");

                assertThat(response.getTriggers())
                    .containsOnlyKeys("trigger1", "trigger2");
            });
        }

        @Test
        public void testAddingResponseToExistingOneShouldOverrideProperties() {
            var response1 = new HtmxResponse()
                .retarget("selector1")
                .reswap(HxSwapType.INNER_HTML)
                .browserRedirect("url1")
                .browserRefresh(false)
                .pushHistory("url1");

            var response2 = new HtmxResponse()
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

        @Test
        public void testResponseHeaderProperties() {
            var response = new HtmxResponse()
                .addTrigger("my-trigger")
                .pushHistory("/a/history")
                .browserRedirect("/a/new/page")
                .browserRefresh(true)
                .retarget("#theThing")
                .reswap(HxSwapType.AFTER_BEGIN);

            assertThat(response.getTriggers())
                .containsOnlyKeys("my-trigger");
            assertThat(response.getHeaderPushHistory()).isEqualTo("/a/history");
            assertThat(response.getHeaderRedirect()).isEqualTo("/a/new/page");
            assertThat(response.getHeaderRefresh()).isTrue();
            assertThat(response.getHeaderRetarget()).isEqualTo("#theThing");
            assertThat(response.getHeaderReswap()).isEqualTo(HxSwapType.AFTER_BEGIN.getValue());
        }

        @Test
        public void testAddingTemplatesShouldPreserveOrder() {
            String template1 = "template1";
            String template2 = "template2";

            var response = new HtmxResponse()
                .addTemplate(template1)
                .addTemplate(template2);

            assertThat(response.getViews())
                .extracting(mav -> mav.getViewName())
                .containsExactly(template1, template2);
        }

        @Test
        public void testAddingResponseToExistingOneShouldPreserveTemplateOrder() {
            var response2 = new HtmxResponse()
                .addTemplate("view3")
                .addTemplate("view4");

            var response1 = new HtmxResponse()
                .addTemplate("view1")
                .addTemplate("view2")
                .and(response2);

            assertThat(response1.getViews())
                .extracting(mav -> mav.getViewName())
                .hasSize(4)
                .containsExactly("view1", "view2", "view3", "view4");
        }
    }
}
