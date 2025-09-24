package io.github.wimdeblauwe.htmx.spring.boot.mvc;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.stereotype.Controller;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import static io.github.wimdeblauwe.htmx.spring.boot.mvc.HeaderResultMatchers.header;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.collection.IsIterableContainingInRelativeOrder.containsInRelativeOrder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HtmxHandlerInterceptorTest.TestController.class)
@ContextConfiguration(classes = HtmxHandlerInterceptorTest.TestController.class)
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

    @Controller
    @RequestMapping("/")
    static class TestController {

        @GetMapping("/without-trigger")
        @ResponseBody
        public String methodWithoutAnnotations() {
            return "";
        }

        @GetMapping("/with-trigger")
        @HxTrigger("eventTriggered")
        @ResponseBody
        public String methodWithHxTrigger() {
            return "";
        }

        @GetMapping("/with-trigger-multiple-events")
        @HxTrigger({ "event1", "event2" })
        @ResponseBody
        public String methodWithHxTriggerAndMultipleEvents() {
            return "";
        }

        @GetMapping("/with-trigger-after-settle")
        @HxTriggerAfterSettle("eventTriggered")
        @ResponseBody
        public String methodWithHxTriggerAfterSettle() {
            return "";
        }

        @GetMapping("/with-trigger-after-settle-multiple-events")
        @HxTriggerAfterSettle({ "event1", "event2" })
        @ResponseBody
        public String methodWithHxTriggerAfterSettleAndMultipleEvents() {
            return "";
        }

        @GetMapping("/with-trigger-after-swap")
        @HxTriggerAfterSwap("eventTriggered")
        @ResponseBody
        public String methodWithHxTriggerAfterSwap() {
            return "";
        }

        @GetMapping("/with-trigger-after-swap-multiple-events")
        @HxTriggerAfterSwap({ "event1", "event2" })
        @ResponseBody
        public String methodWithHxTriggerAfterSwapAndMultipleEvents() {
            return "";
        }

        @GetMapping("/with-triggers")
        @HxTrigger({ "event1", "event2" })
        @HxTriggerAfterSettle({ "event1", "event2" })
        @HxTriggerAfterSwap({ "event1", "event2" })
        @ResponseBody
        public String methodWithHxTriggers() {
            return "";
        }

        @GetMapping("/updates-sidebar")
        @HxUpdatesSidebar
        @ResponseBody
        public String updatesSidebar() {
            return "";
        }

        @GetMapping("/hx-trigger-alias-for")
        @HxTriggerWithAliasFor(event = "updateTrigger")
        @ResponseBody
        public String hxTriggerWithAliasForOverride() {
            return "";
        }

        @GetMapping("/hx-vary")
        @ResponseBody
        public String hxVary() {
            return "";
        }

        @GetMapping("/hx-push-url-path")
        @HxPushUrl("/path")
        @ResponseBody
        public String hxPushUrlPath() {
            return "";
        }

        @GetMapping("/hx-push-url")
        @HxPushUrl
        @ResponseBody
        public String hxPushUrl() {
            return "";
        }

        @GetMapping("/hx-push-url-false")
        @HxPushUrl(HtmxValue.FALSE)
        @ResponseBody
        public String hxPushUrlFalse() {
            return "";
        }

        @GetMapping("/hx-replace-url-path")
        @HxReplaceUrl("/path")
        @ResponseBody
        public String hxReplaceUrlPath() {
            return "";
        }

        @GetMapping("/hx-replace-url")
        @HxReplaceUrl
        @ResponseBody
        public String hxReplaceUrl() {
            return "";
        }

        @GetMapping("/hx-replace-url-false")
        @HxReplaceUrl(HtmxValue.FALSE)
        @ResponseBody
        public String hxReplaceUrlFalse() {
            return "";
        }

        @GetMapping("/hx-reswap")
        @HxReswap(value = HxSwapType.INNER_HTML, swap = 300)
        @ResponseBody
        public String hxReswap() {
            return "";
        }

        @GetMapping("/hx-retarget")
        @HxRetarget("#target")
        @ResponseBody
        public String hxRetarget() {
            return "";
        }

        @GetMapping("/hx-reselect")
        @HxReselect("#target")
        @ResponseBody
        public String hxReselect() {
            return "";
        }

    }

}
