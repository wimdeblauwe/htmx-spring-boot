package io.github.wimdeblauwe.htmx.spring.boot.mvc;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
    public void testHxRefresh() throws Exception {
        mockMvc.perform(get("/hx-refresh"))
               .andExpect(status().isOk())
               .andExpect(header().string("HX-Refresh", "true"));
    }
}
