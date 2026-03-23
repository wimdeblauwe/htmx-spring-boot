package io.github.wimdeblauwe.htmx.spring.boot.mvc;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.stereotype.Controller;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import static io.github.wimdeblauwe.htmx.spring.boot.mvc.HtmxRequestHeader.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HtmxRequestMappingHandlerMappingTest.TestController.class)
@ContextConfiguration(classes = HtmxRequestMappingHandlerMappingTest.TestController.class)
@WithMockUser
public class HtmxRequestMappingHandlerMappingTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testHxRequest() throws Exception {
        mockMvc.perform(get("/hx-request")
                                .header(HX_REQUEST.getValue(), "true"))
               .andExpect(status().isOk())
               .andExpect(content().string("hx-request"));
    }

    @Test
    void testHxRequestShouldBeAppliedForNonBoostedRequest() throws Exception {
        mockMvc.perform(get("/hx-request-ignore-boosted")
                                .header(HX_REQUEST.getValue(), "true"))
               .andExpect(status().isOk())
               .andExpect(content().string("boosted-ignored"));
    }

    @Test
    void testHxRequestShouldIgnoreBoostedRequest() throws Exception {
        mockMvc.perform(get("/hx-request-ignore-boosted")
                                .header(HX_REQUEST.getValue(), "true")
                                .header(HX_BOOSTED.getValue(), "true"))
               .andExpect(status().isNotFound());
    }

    @Test
    void testHxRequestShouldHandleHistoryRestoreRequest() throws Exception {
        mockMvc.perform(get("/hx-request-handle-history-restore-request")
                                .header(HX_REQUEST.getValue(), "true")
                                .header(HX_HISTORY_RESTORE_REQUEST.getValue(), "true"))
               .andExpect(status().isOk())
               .andExpect(content().string("history-restore-request-handled"));
    }

    @Test
    void testHxRequestShouldIgnoreHistoryRestoreRequest() throws Exception {
        mockMvc.perform(get("/hx-request")
                                .header(HX_REQUEST.getValue(), "true")
                                .header(HX_HISTORY_RESTORE_REQUEST.getValue(), "true"))
               .andExpect(status().isNotFound());
    }

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
    void testHxRequestSourceElement() throws Exception {
        mockMvc.perform(get("/hx-request-source")
                                .header(HX_REQUEST.getValue(), "true")
                                .header(HX_SOURCE.getValue(), "element"))
               .andExpect(status().isOk())
               .andExpect(content().string("element"));
    }

    @Test
    void testHxRequestSourceElementAndId() throws Exception {
        mockMvc.perform(get("/hx-request-source")
                                .header(HX_REQUEST.getValue(), "true")
                                .header(HX_SOURCE.getValue(), "element#id"))
               .andExpect(status().isOk())
               .andExpect(content().string("element#id"));
    }

    @Controller
    static class TestController {

        @HxRequest
        @GetMapping("/hx-request")
        @ResponseBody
        public String hxRequest() {
            return "hx-request";
        }

        @HxRequest(boosted = false)
        @GetMapping("/hx-request-ignore-boosted")
        @ResponseBody
        public String hxRequestIgnoreBoosted() {
            return "boosted-ignored";
        }

        @HxRequest(historyRestoreRequest = true)
        @GetMapping("/hx-request-handle-history-restore-request")
        @ResponseBody
        public String hxRequestHandleHistoryRestoreRequest() {
            return "history-restore-request-handled";
        }

        @HxRequest(target = "bar")
        @GetMapping("/hx-request-target")
        @ResponseBody
        public String hxRequestTargetBar() {
            return "bar";
        }

        @HxRequest(target = "foo")
        @GetMapping("/hx-request-target")
        @ResponseBody
        public String hxRequestTargetFoo() {
            return "foo";
        }

        @HxRequest("element")
        @GetMapping("/hx-request-source")
        @ResponseBody
        public String hxRequestSourceElement() {
            return "element";
        }

        @HxRequest("element#id")
        @GetMapping("/hx-request-source")
        @ResponseBody
        public String hxRequestSourceElementAndId() {
            return "element#id";
        }

    }

}
