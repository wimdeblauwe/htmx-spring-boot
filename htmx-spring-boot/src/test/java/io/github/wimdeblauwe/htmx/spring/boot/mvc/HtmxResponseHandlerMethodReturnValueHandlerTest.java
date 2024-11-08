package io.github.wimdeblauwe.htmx.spring.boot.mvc;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HtmxResponseHandlerMethodReturnValueHandlerController.class)
@WithMockUser
public class HtmxResponseHandlerMethodReturnValueHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testHxLocationWithContextData() throws Exception {
        mockMvc.perform(get("/hvhi/hx-location-with-context-data"))
               .andExpect(status().isOk())
               .andExpect(header().string("HX-Location", "{\"path\":\"/path\",\"source\":\"source\",\"event\":\"event\",\"handler\":\"handler\",\"target\":\"target\",\"swap\":\"swap\",\"select\":\"select\",\"values\":{\"value1\":\"v1\",\"value2\":\"v2\"},\"headers\":{\"header1\":\"v1\",\"header2\":\"v2\"}}"));
    }

    @Test
    public void testHxLocationWithContextDataPathShouldPrependContextPath() throws Exception {
        mockMvc.perform(get("/test/hvhi/hx-location-with-context-data").contextPath("/test"))
               .andExpect(status().isOk())
               .andExpect(header().string("HX-Location", "{\"path\":\"/test/path\",\"source\":\"source\",\"event\":\"event\",\"handler\":\"handler\",\"target\":\"target\",\"swap\":\"swap\",\"select\":\"select\",\"values\":{\"value1\":\"v1\",\"value2\":\"v2\"},\"headers\":{\"header1\":\"v1\",\"header2\":\"v2\"}}"));
    }

    @Test
    public void testHxLocationWithoutContextData() throws Exception {
        mockMvc.perform(get("/hvhi/hx-location-without-context-data"))
               .andExpect(status().isOk())
               .andExpect(header().string("HX-Location", "/path"));
    }

    @Test
    public void testHxLocationWithoutContextDataShouldPrependContextPath() throws Exception {
        mockMvc.perform(get("/test/hvhi/hx-location-without-context-data").contextPath("/test"))
               .andExpect(status().isOk())
               .andExpect(header().string("HX-Location", "/test/path"));
    }

    @Test
    public void testHxLocationWithFlashAttributes() throws Exception {
        mockMvc.perform(get("/hvhi/hx-location-with-flash-attributes"))
               .andExpect(status().isOk())
               .andExpect(header().string("HX-Location", "/path"))
               .andExpect(flash().attribute("flash", "test"));
    }

    @Test
    public void testHxPushUrl() throws Exception {
        mockMvc.perform(get("/hvhi/hx-push-url"))
               .andExpect(status().isOk())
               .andExpect(header().string("HX-Push-Url", "/path"));
    }

    @Test
    public void testHxPushUrlShouldPrependContextPath() throws Exception {
        mockMvc.perform(get("/test/hvhi/hx-push-url").contextPath("/test"))
               .andExpect(status().isOk())
               .andExpect(header().string("HX-Push-Url", "/test/path"));
    }

    @Test
    public void testHxRedirect() throws Exception {
        mockMvc.perform(get("/hvhi/hx-redirect"))
               .andExpect(status().isOk())
               .andExpect(header().string("HX-Redirect", "/path"));
    }

    @Test
    public void testHxRedirectShouldPrependContextPath() throws Exception {
        mockMvc.perform(get("/test/hvhi/hx-redirect").contextPath("/test"))
               .andExpect(status().isOk())
               .andExpect(header().string("HX-Redirect", "/test/path"));
    }

    @Test
    public void testHxRedirectWithFlashAttributes() throws Exception {
        mockMvc.perform(get("/hvhi/hx-redirect-with-flash-attributes"))
               .andExpect(status().isOk())
               .andExpect(header().string("HX-Redirect", "/path"))
               .andExpect(flash().attribute("flash", "test"));
    }

    @Test
    public void testHxRefresh() throws Exception {
        mockMvc.perform(get("/hvhi/hx-refresh"))
               .andExpect(status().isOk())
               .andExpect(header().string("HX-Refresh", "true"));
    }

    @Test
    public void testHxReplaceUrl() throws Exception {
        mockMvc.perform(get("/hvhi/hx-replace-url"))
               .andExpect(status().isOk())
               .andExpect(header().string("HX-Replace-Url", "/path"));
    }

    @Test
    public void testHxReplaceUrlShouldPrependContextPath() throws Exception {
        mockMvc.perform(get("/test/hvhi/hx-replace-url").contextPath("/test"))
               .andExpect(status().isOk())
               .andExpect(header().string("HX-Replace-Url", "/test/path"));
    }

    @Test
    public void testHxReselect() throws Exception {
        mockMvc.perform(get("/hvhi/hx-reselect"))
               .andExpect(status().isOk())
               .andExpect(header().string("HX-Reselect", "#target"));
    }

    @Test
    public void testHxReswap() throws Exception {
        mockMvc.perform(get("/hvhi/hx-reswap"))
               .andExpect(status().isOk())
               .andExpect(header().string("HX-Reswap", "outerHTML transition:true focus-scroll:true swap:100ms settle:300ms scroll:#target:bottom show:#target:top"));
    }

    @Test
    public void testHxRetarget() throws Exception {
        mockMvc.perform(get("/hvhi/hx-retarget"))
               .andExpect(status().isOk())
               .andExpect(header().string("HX-Retarget", "#target"));
    }

    @Test
    public void testHxTriggerAfterSettleWithDetails() throws Exception {
        mockMvc.perform(get("/hvhi/hx-trigger-after-settle-with-details"))
               .andExpect(status().isOk())
               .andExpect(header().string("HX-Trigger-After-Settle", "{\"event1\":null,\"event2\":{\"var\":\"value\"}}"));
    }

    @Test
    public void testHxTriggerAfterSettleWithoutDetails() throws Exception {
        mockMvc.perform(get("/hvhi/hx-trigger-after-settle-without-details"))
               .andExpect(status().isOk())
               .andExpect(header().string("HX-Trigger-After-Settle", "event1,event2"));
    }

    @Test
    public void testHxTriggerAfterSwapWithDetails() throws Exception {
        mockMvc.perform(get("/hvhi/hx-trigger-after-swap-with-details"))
               .andExpect(status().isOk())
               .andExpect(header().string("HX-Trigger-After-Swap", "{\"event1\":null,\"event2\":{\"var\":\"value\"}}"));
    }

    @Test
    public void testHxTriggerAfterSwapWithoutDetails() throws Exception {
        mockMvc.perform(get("/hvhi/hx-trigger-after-swap-without-details"))
               .andExpect(status().isOk())
               .andExpect(header().string("HX-Trigger-After-Swap", "event1,event2"));
    }

    @Test
    public void testHxTriggerWithDetails() throws Exception {
        mockMvc.perform(get("/hvhi/hx-trigger-with-details"))
               .andExpect(status().isOk())
               .andExpect(header().string("HX-Trigger", "{\"event1\":null,\"event2\":{\"var\":\"value\"}}"));
    }

    @Test
    public void testHxTriggerWithoutDetails() throws Exception {
        mockMvc.perform(get("/hvhi/hx-trigger-without-details"))
               .andExpect(status().isOk())
               .andExpect(header().string("HX-Trigger", "event1,event2"));
    }

    @Test
    public void testPreventHistoryUpdate() throws Exception {
        mockMvc.perform(get("/hvhi/prevent-history-update"))
               .andExpect(status().isOk())
               .andExpect(header().string("HX-Push-Url", "false"))
               .andExpect(header().doesNotExist("HX-Replace-Url"));
    }

    @Test
    public void testException() throws Exception {
        String html = mockMvc.perform(get("/hvhi/exception"))
                             .andExpect(status().isOk())
                             .andExpect(header().string("HX-Reswap", "none"))
                             .andReturn().getResponse().getContentAsString();
        assertThat(html).contains("""
                                          <span hx-swap-oob="true">
                                              <span>Fake exception</span>
                                          </span>""");
    }

    @Test
    public void testHxTriggerArgument() throws Exception {
        mockMvc.perform(get("/hvhi/argument"))
               .andExpect(status().isOk())
               .andExpect(header().string("HX-Trigger", "event1"));
    }
}
