package io.github.wimdeblauwe.htmx.spring.boot.mvc;

import static io.github.wimdeblauwe.htmx.spring.boot.mvc.HtmxRequestHeader.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(HtmxRequestMappingHandlerMappingTestController.class)
@WithMockUser
public class HtmxRequestMappingHandlerMappingTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testHxRequestTargetBar() throws Exception {
        mockMvc.perform(get("/hx-request-target")
                                .header(HX_REQUEST.getValue(), "true")
                                .header(HX_TARGET.getValue(), "bar"))
               .andExpect(status().isOk())
               .andExpect(content().string("bar"));
    }

    @Test
    void testHxRequestTargetFoo() throws Exception {
        mockMvc.perform(get("/hx-request-target")
                                .header(HX_REQUEST.getValue(), "true")
                                .header(HX_TARGET.getValue(), "foo"))
               .andExpect(status().isOk())
               .andExpect(content().string("foo"));
    }

    @Test
    void testHxRequestTriggerIdBar() throws Exception {
        mockMvc.perform(get("/hx-request-trigger")
                                .header(HX_REQUEST.getValue(), "true")
                                .header(HX_TRIGGER.getValue(), "bar"))
               .andExpect(status().isOk())
               .andExpect(content().string("bar"));
    }

    @Test
    void testHxRequestTriggerIdFoo() throws Exception {
        mockMvc.perform(get("/hx-request-trigger")
                                .header(HX_REQUEST.getValue(), "true")
                                .header(HX_TRIGGER.getValue(), "foo"))
               .andExpect(status().isOk())
               .andExpect(content().string("foo"));
    }

    @Test
    void testHxRequestTriggerNameBar() throws Exception {
        mockMvc.perform(get("/hx-request-trigger")
                                .header(HX_REQUEST.getValue(), "true")
                                .header(HX_TRIGGER_NAME.getValue(), "bar"))
               .andExpect(status().isOk())
               .andExpect(content().string("bar"));
    }

    @Test
    void testHxRequestTriggerNameFoo() throws Exception {
        mockMvc.perform(get("/hx-request-trigger")
                                .header(HX_REQUEST.getValue(), "true")
                                .header(HX_TRIGGER_NAME.getValue(), "foo"))
               .andExpect(status().isOk())
               .andExpect(content().string("foo"));
    }

    @Test
    void testHxRequestValueWithHeaderHxTriggerNameBar() throws Exception {
        mockMvc.perform(get("/hx-request-value")
                                .header(HX_REQUEST.getValue(), "true")
                                .header(HX_TRIGGER_NAME.getValue(), "bar"))
               .andExpect(status().isOk())
               .andExpect(content().string("bar"));
    }

    @Test
    void testHxRequestValueWithHeaderHxTriggerNameFoo() throws Exception {
        mockMvc.perform(get("/hx-request-value")
                                .header(HX_REQUEST.getValue(), "true")
                                .header(HX_TRIGGER_NAME.getValue(), "foo"))
               .andExpect(status().isOk())
               .andExpect(content().string("foo"));
    }

    @Test
    void testHxRequestValueWithHxHeaderTriggerBar() throws Exception {
        mockMvc.perform(get("/hx-request-value")
                                .header(HX_REQUEST.getValue(), "true")
                                .header(HX_TRIGGER.getValue(), "bar"))
               .andExpect(status().isOk())
               .andExpect(content().string("bar"));
    }

    @Test
    void testHxRequestValueWithHxHeaderTriggerFoo() throws Exception {
        mockMvc.perform(get("/hx-request-value")
                                .header(HX_REQUEST.getValue(), "true")
                                .header(HX_TRIGGER.getValue(), "foo"))
               .andExpect(status().isOk())
               .andExpect(content().string("foo"));
    }

}
