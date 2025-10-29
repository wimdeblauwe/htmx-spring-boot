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

    @Test
    public void testResponseBodyReturnValue() throws Exception {

        mockMvc.perform(get("/response-body"))
               .andExpect(status().isOk())
               .andExpect(header().string("HX-Trigger", "trigger"))
                .andExpect(header().string("HX-Reswap", "none"));
    }

    @Controller
    static class TestController {

        @GetMapping("/prevent-history-update")
        public String preventHistoryUpdate(HtmxResponse response) {

            response.preventHistoryUpdate();
            return "view";
        }

        @GetMapping("/push-url")
        public String pushUrl(HtmxResponse response) {

            response.setPushUrl("/path");
            return "view";
        }

        @GetMapping("/replace-url")
        public String replaceUrl(HtmxResponse response) {

            response.setReplaceUrl("/path");
            return "view";
        }

        @GetMapping("/reselect")
        public String reselect(HtmxResponse response) {

            response.setReselect("#container");
            return "view";
        }

        @GetMapping("/reswap")
        public String reswap(HtmxResponse response) {

            response.setReswap(HtmxReswap.innerHtml()
                                         .swap(Duration.ZERO)
                                         .settle(Duration.ofMillis(500))
                                         .scroll(HtmxReswap.Position.TOP)
                                         .scrollTarget("#scrollTarget")
                                         .show(HtmxReswap.Position.BOTTOM)
                                         .showTarget("#showTarget")
                                         .transition()
                                         .focusScroll(true));
            return "view";
        }

        @GetMapping("/reswap-after-begin")
        public String reswapAfterBegin(HtmxResponse response) {

            response.setReswap(HtmxReswap.afterBegin());
            return "view";
        }

        @GetMapping("/reswap-after-end")
        public String reswapAfterEnd(HtmxResponse response) {

            response.setReswap(HtmxReswap.afterEnd());
            return "view";
        }

        @GetMapping("/reswap-before-begin")
        public String reswapBeforeBegin(HtmxResponse response) {

            response.setReswap(HtmxReswap.beforeBegin());
            return "view";
        }

        @GetMapping("/reswap-before-end")
        public String reswapBeforeEnd(HtmxResponse response) {

            response.setReswap(HtmxReswap.beforeEnd());
            return "view";
        }

        @GetMapping("/reswap-delete")
        public String reswapDelete(HtmxResponse response) {

            response.setReswap(HtmxReswap.delete());
            return "view";
        }

        @GetMapping("/reswap-inner-html")
        public String reswapInnerHtml(HtmxResponse response) {

            response.setReswap(HtmxReswap.innerHtml());
            return "view";
        }

        @GetMapping("/reswap-none")
        public String reswapNone(HtmxResponse response) {

            response.setReswap(HtmxReswap.none());
            return "view";
        }

        @GetMapping("/reswap-outer-html")
        public String reswapOuterHtml(HtmxResponse response) {

            response.setReswap(HtmxReswap.outerHtml());
            return "view";
        }

        @GetMapping("/retarget")
        public String retarget(HtmxResponse response) {

            response.setRetarget("#container");
            return "view";
        }

        @GetMapping("/trigger")
        public String trigger(HtmxResponse response) {

            response.addTrigger("trigger1");
            response.addTrigger("trigger2");
            return "view";
        }

        @GetMapping("/trigger-after-settle")
        public String triggerAfterSettle(HtmxResponse response) {

            response.addTriggerAfterSettle("trigger1");
            response.addTriggerAfterSettle("trigger2");
            return "view";
        }

        @GetMapping("/trigger-after-swap")
        public String triggerAfterSwap(HtmxResponse response) {

            response.addTriggerAfterSwap("trigger1");
            response.addTriggerAfterSwap("trigger2");
            return "view";
        }

        @GetMapping("/response-body")
        @ResponseBody
        public void responseBody(HtmxResponse response) {

            response.addTrigger("trigger");
            response.setReswap(HtmxReswap.none());
        }

    }

}
