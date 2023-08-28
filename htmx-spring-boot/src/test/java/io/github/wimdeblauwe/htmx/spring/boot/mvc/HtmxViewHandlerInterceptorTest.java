package io.github.wimdeblauwe.htmx.spring.boot.mvc;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HtmxViewHandlerInterceptorController.class)
@WithMockUser
public class HtmxViewHandlerInterceptorTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testHxLocationWithContextData() throws Exception {
        mockMvc.perform(get("/hvhi/hx-location-with-context-data"))
               .andExpect(status().isOk())
               .andExpect(header().string("HX-Location", "{\"path\":\"/path\",\"source\":\"source\",\"event\":\"event\",\"handler\":\"handler\",\"target\":\"target\",\"swap\":\"swap\",\"values\":{\"value1\":\"v1\",\"value2\":\"v2\"},\"headers\":{\"header1\":\"v1\",\"header2\":\"v2\"}}"));
    }

    @Test
    public void testHxLocationWithoutContextData() throws Exception {
        mockMvc.perform(get("/hvhi/hx-location-without-context-data"))
               .andExpect(status().isOk())
               .andExpect(header().string("HX-Location", "/path"));
    }

    @Test
    public void testHxReplaceUrl() throws Exception {
        mockMvc.perform(get("/hvhi/hx-replace-url"))
               .andExpect(status().isOk())
               .andExpect(header().string("HX-Replace-Url", "/path"));
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

}
