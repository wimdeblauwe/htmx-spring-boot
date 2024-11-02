package io.github.wimdeblauwe.htmx.spring.boot.mvc;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.stereotype.Controller;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.Duration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HtmxResponseHandlerMethodArgumentResolverTest.TestController.class)
@ContextConfiguration(classes = HtmxResponseHandlerMethodArgumentResolverTest.TestController.class)
@WithMockUser
public class HtmxResponseHandlerMethodArgumentResolverTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testPreventHistoryUpdate() throws Exception {

        mockMvc.perform(get("/prevent-history-update"))
               .andExpect(status().isOk())
               .andExpect(header().string("HX-Push-Url", "false"))
               .andExpect(header().doesNotExist("HX-Replace-Url"));
    }

    @Test
    public void testPushUrl() throws Exception {

        mockMvc.perform(get("/push-url"))
               .andExpect(status().isOk())
               .andExpect(header().string("HX-Push-Url", "/path"))
               .andExpect(header().doesNotExist("HX-Replace-Url"));
    }

    @Test
    public void testPushUrlContextRelative() throws Exception {

        mockMvc.perform(get("/push-url"))
               .andExpect(status().isOk())
               .andExpect(header().string("HX-Push-Url", "/path"))
               .andExpect(header().doesNotExist("HX-Replace-Url"));
    }

    @Test
    public void testReplaceUrlContextRelative() throws Exception {

        mockMvc.perform(get("/contextpath/replace-url").contextPath("/contextpath"))
               .andExpect(status().isOk())
               .andExpect(header().string("HX-Replace-Url", "/contextpath/path"))
               .andExpect(header().doesNotExist("HX-Push-Url"));
    }

    @Test
    public void testReselect() throws Exception {

        mockMvc.perform(get("/reselect"))
               .andExpect(status().isOk())
               .andExpect(header().string("HX-Reselect", "#container"));
    }

    @Test
    public void testReswap() throws Exception {

        mockMvc.perform(get("/reswap"))
               .andExpect(status().isOk())
               .andExpect(header().string("HX-Reswap", "innerHTML transition:true focus-scroll:true swap:0ms settle:500ms scroll:#scrollTarget:top show:#showTarget:bottom"));
    }

    @Test
    public void testReswapAfterBegin() throws Exception {

        mockMvc.perform(get("/reswap-after-begin"))
               .andExpect(status().isOk())
               .andExpect(header().string("HX-Reswap", "afterbegin"));
    }

    @Test
    public void testReswapAfterEnd() throws Exception {

        mockMvc.perform(get("/reswap-after-end"))
               .andExpect(status().isOk())
               .andExpect(header().string("HX-Reswap", "afterend"));
    }

    @Test
    public void testReswapBeforeBegin() throws Exception {

        mockMvc.perform(get("/reswap-before-begin"))
               .andExpect(status().isOk())
               .andExpect(header().string("HX-Reswap", "beforebegin"));
    }

    @Test
    public void testReswapBeforeEnd() throws Exception {

        mockMvc.perform(get("/reswap-before-end"))
               .andExpect(status().isOk())
               .andExpect(header().string("HX-Reswap", "beforeend"));
    }

    @Test
    public void testReswapDelete() throws Exception {

        mockMvc.perform(get("/reswap-delete"))
               .andExpect(status().isOk())
               .andExpect(header().string("HX-Reswap", "delete"));
    }

    @Test
    public void testReswapInnerHtml() throws Exception {

        mockMvc.perform(get("/reswap-inner-html"))
               .andExpect(status().isOk())
               .andExpect(header().string("HX-Reswap", "innerHTML"));
    }

    @Test
    public void testReswapNone() throws Exception {

        mockMvc.perform(get("/reswap-none"))
               .andExpect(status().isOk())
               .andExpect(header().string("HX-Reswap", "none"));
    }

    @Test
    public void testReswapOuterHtml() throws Exception {

        mockMvc.perform(get("/reswap-outer-html"))
               .andExpect(status().isOk())
               .andExpect(header().string("HX-Reswap", "outerHTML"));
    }

    @Test
    public void testRetarget() throws Exception {

        mockMvc.perform(get("/retarget"))
               .andExpect(status().isOk())
               .andExpect(header().string("HX-Retarget", "#container"));
    }

    @Test
    public void testTrigger() throws Exception {

        mockMvc.perform(get("/trigger"))
               .andExpect(status().isOk())
               .andExpect(header().string("HX-Trigger", "trigger1,trigger2"));
    }

    @Test
    public void testTriggerAfterSettle() throws Exception {

        mockMvc.perform(get("/trigger-after-settle"))
               .andExpect(status().isOk())
               .andExpect(header().string("HX-Trigger-After-Settle", "trigger1,trigger2"));
    }

    @Test
    public void testTriggerAfterSwap() throws Exception {

        mockMvc.perform(get("/trigger-after-swap"))
               .andExpect(status().isOk())
               .andExpect(header().string("HX-Trigger-After-Swap", "trigger1,trigger2"));
    }

    @Controller
    static class TestController {

        @GetMapping("/prevent-history-update")
        @ResponseBody
        public void preventHistoryUpdate(HtmxResponse response) {
            response.preventHistoryUpdate();
        }

        @GetMapping("/push-url")
        @ResponseBody
        public void pushUrl(HtmxResponse response) {
            response.setPushUrl("/path");
        }

        @GetMapping("/replace-url")
        @ResponseBody
        public void replaceUrl(HtmxResponse response) {
            response.setReplaceUrl("/path");
        }

        @GetMapping("/reselect")
        @ResponseBody
        public void reselect(HtmxResponse response) {
            response.setReselect("#container");
        }

        @GetMapping("/reswap")
        @ResponseBody
        public void reswap(HtmxResponse response) {
            response.setReswap(HtmxReswap.innerHtml()
                                         .swap(Duration.ZERO)
                                         .settle(Duration.ofMillis(500))
                                         .scroll(HtmxReswap.Position.TOP)
                                         .scrollTarget("#scrollTarget")
                                         .show(HtmxReswap.Position.BOTTOM)
                                         .showTarget("#showTarget")
                                         .transition()
                                         .focusScroll(true));
        }

        @GetMapping("/reswap-after-begin")
        @ResponseBody
        public void reswapAfterBegin(HtmxResponse response) {
            response.setReswap(HtmxReswap.afterBegin());
        }

        @GetMapping("/reswap-after-end")
        @ResponseBody
        public void reswapAfterEnd(HtmxResponse response) {
            response.setReswap(HtmxReswap.afterEnd());
        }

        @GetMapping("/reswap-before-begin")
        @ResponseBody
        public void reswapBeforeBegin(HtmxResponse response) {
            response.setReswap(HtmxReswap.beforeBegin());
        }

        @GetMapping("/reswap-before-end")
        @ResponseBody
        public void reswapBeforeEnd(HtmxResponse response) {
            response.setReswap(HtmxReswap.beforeEnd());
        }

        @GetMapping("/reswap-delete")
        @ResponseBody
        public void reswapDelete(HtmxResponse response) {
            response.setReswap(HtmxReswap.delete());
        }

        @GetMapping("/reswap-inner-html")
        @ResponseBody
        public void reswapInnerHtml(HtmxResponse response) {
            response.setReswap(HtmxReswap.innerHtml());
        }

        @GetMapping("/reswap-none")
        @ResponseBody
        public void reswapNone(HtmxResponse response) {
            response.setReswap(HtmxReswap.none());
        }

        @GetMapping("/reswap-outer-html")
        @ResponseBody
        public void reswapOuterHtml(HtmxResponse response) {
            response.setReswap(HtmxReswap.outerHtml());
        }

        @GetMapping("/retarget")
        @ResponseBody
        public void retarget(HtmxResponse response) {
            response.setRetarget("#container");
        }

        @GetMapping("/trigger")
        @ResponseBody
        public void trigger(HtmxResponse response) {
            response.addTrigger("trigger1");
            response.addTrigger("trigger2");
        }

        @GetMapping("/trigger-after-settle")
        @ResponseBody
        public void triggerAfterSettle(HtmxResponse response) {
            response.addTriggerAfterSettle("trigger1");
            response.addTriggerAfterSettle("trigger2");
        }

        @GetMapping("/trigger-after-swap")
        @ResponseBody
        public void triggerAfterSwap(HtmxResponse response) {
            response.addTriggerAfterSwap("trigger1");
            response.addTriggerAfterSwap("trigger2");
        }

    }

}
