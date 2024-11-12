package io.github.wimdeblauwe.htmx.spring.boot.mvc;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.collection.IsIterableContainingInRelativeOrder.containsInRelativeOrder;
import static org.hamcrest.Matchers.not;

@WebMvcTest(TestController.class)
@WithMockUser
class HtmxHandlerInterceptorTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testHeaderIsSetOnResponseIfHxTriggerIsPresent() throws Exception {
        mockMvc.perform(get("/with-trigger"))
               .andExpect(status().isOk())
               .andExpect(header().string("HX-Trigger", "eventTriggered"));
    }

    @Test
    public void testHeaderIsSetOnResponseWithMultipleEventsIfHxTriggerIsPresent() throws Exception {
        mockMvc.perform(get("/with-trigger-multiple-events"))
               .andExpect(status().isOk())
               .andExpect(header().string("HX-Trigger", "event1,event2"));
    }

    @Test
    public void testAfterSettleHeaderIsSetOnResponseIfHxTriggerIsPresent() throws Exception {
        mockMvc.perform(get("/with-trigger-settle"))
               .andExpect(status().isOk())
               .andExpect(header().string("HX-Trigger-After-Settle", "eventTriggered"));
    }

    @Test
    public void testAfterSwapHeaderIsSetOnResponseIfHxTriggerIsPresent() throws Exception {
        mockMvc.perform(get("/with-trigger-swap"))
               .andExpect(status().isOk())
               .andExpect(header().string("HX-Trigger-After-Swap", "eventTriggered"));
    }

    @Test
    public void testAfterSettleHeaderIsSetOnResponseIfHxTriggerAfterSettleIsPresent() throws Exception {
        mockMvc.perform(get("/with-trigger-after-settle"))
               .andExpect(status().isOk())
               .andExpect(header().string("HX-Trigger-After-Settle", "eventTriggered"));
    }

    @Test
    public void testAfterSettleHeaderIsSetOnResponseWithMultipleEventsIfHxTriggerAfterSettleIsPresent() throws Exception {
        mockMvc.perform(get("/with-trigger-after-settle-multiple-events"))
               .andExpect(status().isOk())
               .andExpect(header().string("HX-Trigger-After-Settle", "event1,event2"));
    }

    @Test
    public void testAfterSwapHeaderIsSetOnResponseIfHxTriggerAfterSwapIsPresent() throws Exception {
        mockMvc.perform(get("/with-trigger-after-swap"))
               .andExpect(status().isOk())
               .andExpect(header().string("HX-Trigger-After-Swap", "eventTriggered"));
    }

    @Test
    public void testAfterSwapHeaderIsSetOnResponseWithMultipleEventsIfHxTriggerAfterSwapIsPresent() throws Exception {
        mockMvc.perform(get("/with-trigger-after-swap-multiple-events"))
               .andExpect(status().isOk())
               .andExpect(header().string("HX-Trigger-After-Swap", "event1,event2"));
    }

    @Test
    public void testHeadersAreSetOnResponseIfHxTriggersArePresent() throws Exception {
        mockMvc.perform(get("/with-triggers"))
               .andExpect(status().isOk())
               .andExpect(header().string("HX-Trigger", "event1,event2"))
               .andExpect(header().string("HX-Trigger-After-Settle", "event1,event2"))
               .andExpect(header().string("HX-Trigger-After-Swap", "event1,event2"));
    }

    @Test
    public void testHeaderIsNotSetOnResponseIfHxTriggerNotPresent() throws Exception {
        mockMvc.perform(get("/without-trigger"))
               .andExpect(status().isOk())
               .andExpect(header().doesNotExist("HX-Trigger"));
    }

    @Test
    public void testAnnotationComposition() throws Exception {
        mockMvc.perform(get("/updates-sidebar"))
               .andExpect(status().isOk())
               .andExpect(header().string("HX-Trigger", "updatesSidebar"));
    }

    @Test
    public void testAnnotationCompositionWithAliasFor() throws Exception {
        mockMvc.perform(get("/hx-trigger-alias-for"))
               .andExpect(status().isOk())
               .andExpect(header().string("HX-Trigger", "updateTrigger"));
    }

    @Test
    public void testVary() throws Exception {
        mockMvc.perform(get("/hx-vary").header("HX-Request", "true"))
               .andExpect(status().isOk())
               .andExpect(header().stringValues("Vary", containsInRelativeOrder("HX-Request")));
    }

    @Test
    public void testVaryNoHxRequest() throws Exception {
        mockMvc.perform(get("/hx-vary"))
               .andExpect(status().isOk())
               .andExpect(header().stringValues("Vary", not(containsInRelativeOrder("HX-Request"))));
    }

    @Test
    public void testHxRefresh() throws Exception {
        mockMvc.perform(get("/hx-refresh"))
               .andExpect(status().isOk())
               .andExpect(header().string("HX-Refresh", "true"));
    }

    @Test
    public void testHxLocationWithContextData() throws Exception {
        mockMvc.perform(get("/hx-location-with-context-data"))
               .andExpect(status().isOk())
               .andExpect(header().string("HX-Location", "{\"path\":\"/path\",\"source\":\"source\",\"event\":\"event\",\"handler\":\"handler\",\"target\":\"target\",\"swap\":\"swap\",\"select\":\"select\"}"));
    }

    @Test
    public void testHxLocationWithContextDataPathShouldPrependContextPath() throws Exception {
        mockMvc.perform(get("/test/hx-location-with-context-data").contextPath("/test"))
               .andExpect(status().isOk())
               .andExpect(header().string("HX-Location", "{\"path\":\"/test/path\",\"source\":\"source\",\"event\":\"event\",\"handler\":\"handler\",\"target\":\"target\",\"swap\":\"swap\",\"select\":\"select\"}"));
    }

    @Test
    public void testHxLocationWithoutContextData() throws Exception {
        mockMvc.perform(get("/test/hx-location-without-context-data").contextPath("/test"))
               .andExpect(status().isOk())
               .andExpect(header().string("HX-Location", "/test/path"));
    }

    @Test
    public void testHxLocationWithoutContextDataShouldPrependContextPath() throws Exception {
        mockMvc.perform(get("/test/hx-location-without-context-data").contextPath("/test"))
               .andExpect(status().isOk())
               .andExpect(header().string("HX-Location", "/test/path"));
    }

    @Test
    public void testHxPushUrlPath() throws Exception {
        mockMvc.perform(get("/hx-push-url-path"))
               .andExpect(status().isOk())
               .andExpect(header().string("HX-Push-Url", "/path"));
    }

    @Test
    public void testHxPushUrl() throws Exception {
        mockMvc.perform(get("/hx-push-url?test=hello"))
               .andExpect(status().isOk())
               .andExpect(header().string("HX-Push-Url", "/hx-push-url?test=hello"));
    }

    @Test
    public void testHxPushUrlFalse() throws Exception {
        mockMvc.perform(get("/hx-push-url-false?test=hello"))
               .andExpect(status().isOk())
               .andExpect(header().string("HX-Push-Url", "false"));
    }

    @Test
    public void testHxPushUrlShouldPrependContextPath() throws Exception {
        mockMvc.perform(get("/test/hx-push-url-path").contextPath("/test"))
               .andExpect(status().isOk())
               .andExpect(header().string("HX-Push-Url", "/test/path"));
    }

    @Test
    public void testHxRedirect() throws Exception {
        mockMvc.perform(get("/hx-redirect"))
               .andExpect(status().isOk())
               .andExpect(header().string("HX-Redirect", "/path"));
    }

    @Test
    public void testHxRedirectShouldPrependContextPath() throws Exception {
        mockMvc.perform(get("/test/hx-redirect").contextPath("/test"))
               .andExpect(status().isOk())
               .andExpect(header().string("HX-Redirect", "/test/path"));
    }

    @Test
    public void testHxReplaceUrlPath() throws Exception {
        mockMvc.perform(get("/hx-replace-url-path"))
               .andExpect(status().isOk())
               .andExpect(header().string("HX-Replace-Url", "/path"));
    }

    @Test
    public void testHxReplaceUrl() throws Exception {
        mockMvc.perform(get("/hx-replace-url?test=hello&test2=hello2"))
               .andExpect(status().isOk())
               .andExpect(header().string("HX-Replace-Url", "/hx-replace-url?test=hello&test2=hello2"));
    }

    @Test
    public void testHxReplaceUrlFalse() throws Exception {
        mockMvc.perform(get("/hx-replace-url-false?test=hello"))
               .andExpect(status().isOk())
               .andExpect(header().string("HX-Replace-Url", "false"));
    }

    @Test
    public void testHxReplaceUrlShouldPrependContextPath() throws Exception {
        mockMvc.perform(get("/test/hx-replace-url-path").contextPath("/test"))
               .andExpect(status().isOk())
               .andExpect(header().string("HX-Replace-Url", "/test/path"));
    }

    @Test
    public void testHxReswap() throws Exception {
        mockMvc.perform(get("/hx-reswap"))
               .andExpect(status().isOk())
               .andExpect(header().string("HX-Reswap", "innerHTML swap:300ms"));
    }

    @Test
    public void testHxRetarget() throws Exception {
        mockMvc.perform(get("/hx-retarget"))
               .andExpect(status().isOk())
               .andExpect(header().string("HX-Retarget", "#target"));
    }

    @Test
    public void testHxReselect() throws Exception {
        mockMvc.perform(get("/hx-reselect"))
               .andExpect(status().isOk())
               .andExpect(header().string("HX-Reselect", "#target"));
    }

}
