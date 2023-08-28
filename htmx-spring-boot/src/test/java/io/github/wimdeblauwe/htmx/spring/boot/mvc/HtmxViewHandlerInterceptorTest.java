package io.github.wimdeblauwe.htmx.spring.boot.mvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(HtmxViewHandlerInterceptorController.class)
@WithMockUser
class HtmxViewHandlerInterceptorTest {

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
}
