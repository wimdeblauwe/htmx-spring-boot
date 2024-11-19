package io.github.wimdeblauwe.htmx.spring.boot.mvc;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.stereotype.Controller;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;
import java.util.TreeMap;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HtmxHandlerMethodTest.TestController.class)
@ContextConfiguration(classes = HtmxHandlerMethodTest.TestController.class)
@WithMockUser
public class HtmxHandlerMethodTest {

    @Autowired
    private MockMvc mockMvc;

    private static HttpHeaders htmxRequest() {

        var headers = new HttpHeaders();
        headers.add(HtmxRequestHeader.HX_REQUEST.getValue(), HtmxValue.TRUE);
        return headers;
    }

    @Test
    public void testExceptionHandler() throws Exception {

        mockMvc.perform(get("/throw-exception").headers(htmxRequest()))
               .andExpect(status().isOk())
               .andExpect(header().string("HX-Retarget", "#container"))
               .andExpect(content().string("View1\n"));
    }

    @Test
    public void testLocationRedirect() throws Exception {

        mockMvc.perform(get("/location-redirect").headers(htmxRequest()))
               .andExpect(status().isOk())
               .andExpect(header().string("HX-Location", "/path"));
    }

    @Test
    public void testLocationRedirectExposingModelAttributes() throws Exception {

        mockMvc.perform(get("/location-redirect-expose-model-attributes").headers(htmxRequest()))
               .andExpect(status().isOk())
               .andExpect(header().string("HX-Location", "/path?attr=value"));
    }

    @Test
    public void testLocationRedirectViewNamePrefix() throws Exception {

        mockMvc.perform(get("/location-redirect-view-name-prefix").headers(htmxRequest()))
               .andExpect(status().isOk())
               .andExpect(header().string("HX-Location", "/path"));
    }

    @Test
    public void testLocationRedirectViewNamePrefixContextRelative() throws Exception {

        mockMvc.perform(get("/contextpath/location-redirect-view-name-prefix").contextPath("/contextpath").headers(htmxRequest()))
               .andExpect(status().isOk())
               .andExpect(header().string("HX-Location", "/contextpath/path"));
    }

    @Test
    public void testLocationRedirectViewNamePrefixFlashAttributes() throws Exception {

        mockMvc.perform(get("/location-redirect-view-name-prefix-flash-attributes").headers(htmxRequest()))
               .andExpect(status().isOk())
               .andExpect(header().string("HX-Location", "/path"))
               .andExpect(flash().attribute("flash", "test"));
    }

    @Test
    public void testLocationRedirectWithContextData() throws Exception {

        mockMvc.perform(get("/location-redirect-with-context-data").headers(htmxRequest()))
               .andExpect(status().isOk())
               .andExpect(header().string("HX-Location", "{\"path\":\"/path\",\"source\":\"source\",\"event\":\"event\",\"handler\":\"handler\",\"target\":\"target\",\"swap\":\"swap\",\"select\":\"select\",\"values\":{\"value1\":\"v1\",\"value2\":\"v2\"},\"headers\":{\"header1\":\"v1\",\"header2\":\"v2\"}}"));
    }

    @Test
    public void testLocationRedirectWithContextDataAndFlashAttributes() throws Exception {

        mockMvc.perform(get("/location-redirect-context-data-flash-attributes").headers(htmxRequest()))
               .andExpect(status().isOk())
               .andExpect(header().string("HX-Location", "{\"path\":\"/path\",\"source\":\"source\",\"event\":\"event\",\"handler\":\"handler\",\"target\":\"target\",\"swap\":\"swap\",\"select\":\"select\",\"values\":{\"value1\":\"v1\",\"value2\":\"v2\"},\"headers\":{\"header1\":\"v1\",\"header2\":\"v2\"}}"))
               .andExpect(flash().attribute("flash", "test"));
    }

    @Test
    public void testLocationRedirectWithContextDataExposingModelAttributes() throws Exception {

        mockMvc.perform(get("/location-redirect-with-context-data-expose-model-attributes").headers(htmxRequest()))
               .andExpect(status().isOk())
               .andExpect(header().string("HX-Location", "{\"path\":\"/path?attr=value\",\"source\":\"source\",\"event\":\"event\",\"handler\":\"handler\",\"target\":\"target\",\"swap\":\"swap\",\"select\":\"select\",\"values\":{\"value1\":\"v1\",\"value2\":\"v2\"},\"headers\":{\"header1\":\"v1\",\"header2\":\"v2\"}}"));
    }

    @Test
    public void testLocationRedirectWithFlashAttributes() throws Exception {

        mockMvc.perform(get("/location-redirect-flash-attributes").headers(htmxRequest()))
               .andExpect(status().isOk())
               .andExpect(header().string("HX-Location", "/path"))
               .andExpect(flash().attribute("flash", "test"));
    }

    @Test
    public void testMultipleViews() throws Exception {

        mockMvc.perform(get("/multiple-views").headers(htmxRequest()))
               .andExpect(status().isOk())
               .andExpect(content().string("View1\nView2\n"));
    }

    @Test
    public void testRedirect() throws Exception {

        mockMvc.perform(get("/redirect").headers(htmxRequest()))
               .andExpect(status().isOk())
               .andExpect(header().string("HX-Redirect", "/test"));
    }

    @Test
    public void testRedirectExposingModelAttributes() throws Exception {

        mockMvc.perform(get("/redirect-expose-model-attributes").headers(htmxRequest()))
               .andExpect(status().isOk())
               .andExpect(header().string("HX-Redirect", "/test?attr=value"));
    }

    @Test
    public void testRedirectViewNamePrefix() throws Exception {

        mockMvc.perform(get("/redirect-view-name-prefix").headers(htmxRequest()))
               .andExpect(status().isOk())
               .andExpect(header().string("HX-Redirect", "/test"));
    }

    @Test
    public void testRedirectViewNamePrefixContextRelative() throws Exception {

        mockMvc.perform(get("/contextpath/redirect-view-name-prefix").contextPath("/contextpath").headers(htmxRequest()))
               .andExpect(status().isOk())
               .andExpect(header().string("HX-Redirect", "/contextpath/test"));
    }

    @Test
    public void testRedirectViewNamePrefixFlashAttributes() throws Exception {

        mockMvc.perform(get("/contextpath/redirect-view-name-prefix-flash-attributes").contextPath("/contextpath").headers(htmxRequest()))
               .andExpect(status().isOk())
               .andExpect(header().string("HX-Redirect", "/contextpath/test"))
               .andExpect(flash().attribute("flash", "test"));;
    }

    @Test
    public void testRedirectWithContextPath() throws Exception {

        mockMvc.perform(get("/contextpath/redirect-context-relative").headers(htmxRequest()).contextPath("/contextpath"))
               .andExpect(status().isOk())
               .andExpect(header().string("HX-Redirect", "/contextpath/test"));
    }

    @Test
    public void testRedirectWithFlashAttributes() throws Exception {

        mockMvc.perform(get("/redirect-flash-attributes").headers(htmxRequest()))
               .andExpect(status().isOk())
               .andExpect(header().string("HX-Redirect", "/test"))
               .andExpect(flash().attribute("flash", "test"));
    }

    @Test
    public void testRefresh() throws Exception {

        mockMvc.perform(get("/refresh").headers(htmxRequest()))
               .andExpect(status().isOk())
               .andExpect(header().string("HX-Refresh", HtmxValue.TRUE));
    }

    @Test
    public void testRefreshViewName() throws Exception {

        mockMvc.perform(get("/refresh-view-name").headers(htmxRequest()))
               .andExpect(status().isOk())
               .andExpect(header().string("HX-Refresh", HtmxValue.TRUE));
    }

    @Test
    public void testSingleView() throws Exception {

        mockMvc.perform(get("/single-view").headers(htmxRequest()))
               .andExpect(status().isOk())
               .andExpect(content().string("View1\n"));
    }

    @Controller
    static class TestController {

        @ExceptionHandler(RuntimeException.class)
        public String handleError(RuntimeException ex, HtmxRequest htmxRequest, HtmxResponse htmxResponse) {
            if (htmxRequest.isHtmxRequest()) {
                htmxResponse.setRetarget("#container");
            }
            return "view1";
        }

        @HxRequest
        @GetMapping("/location-redirect")
        public HtmxLocationRedirectView locationRedirect() {
            return new HtmxLocationRedirectView("/path");
        }

        @HxRequest
        @GetMapping("/location-redirect-expose-model-attributes")
        public HtmxLocationRedirectView locationRedirectExposingModelAttributes(RedirectAttributes redirectAttributes) {
            redirectAttributes.addAttribute("attr", "value");
            return new HtmxLocationRedirectView("/path");
        }

        @HxRequest
        @GetMapping("/location-redirect-view-name-prefix")
        public String locationRedirectViewNamePrefix() {
            return "redirect:htmx:location:/path";
        }

        @HxRequest
        @GetMapping("/location-redirect-view-name-prefix-flash-attributes")
        public String locationRedirectWithViewNamePrefixFlashAttributes(RedirectAttributes redirectAttributes) {

            redirectAttributes.addFlashAttribute("flash", "test");
            return "redirect:htmx:location:/path";
        }

        @HxRequest
        @GetMapping("/location-redirect-with-context-data")
        public HtmxLocationRedirectView locationRedirectWithContextData() {

            var redirectView = new HtmxLocationRedirectView("/path");
            redirectView.setSource("source");
            redirectView.setEvent("event");
            redirectView.setHandler("handler");
            redirectView.setTarget("target");
            redirectView.setSwap("swap");
            redirectView.setSelect("select");
            redirectView.setValues(new TreeMap<>(Map.of("value1", "v1", "value2", "v2")));
            redirectView.setHeaders(new TreeMap<>(Map.of("header1", "v1", "header2", "v2")));

            return redirectView;
        }

        @HxRequest
        @GetMapping("/location-redirect-context-data-flash-attributes")
        public HtmxLocationRedirectView locationRedirectWithContextDataAndFlashAttributes(RedirectAttributes redirectAttributes) {

            var redirectView = new HtmxLocationRedirectView("/path");
            redirectView.setSource("source");
            redirectView.setEvent("event");
            redirectView.setHandler("handler");
            redirectView.setTarget("target");
            redirectView.setSwap("swap");
            redirectView.setSelect("select");
            redirectView.setValues(new TreeMap<>(Map.of("value1", "v1", "value2", "v2")));
            redirectView.setHeaders(new TreeMap<>(Map.of("header1", "v1", "header2", "v2")));

            redirectAttributes.addFlashAttribute("flash", "test");

            return redirectView;
        }

        @HxRequest
        @GetMapping("/location-redirect-with-context-data-expose-model-attributes")
        public HtmxLocationRedirectView locationRedirectWithContextDataExposingModelAttributes(RedirectAttributes redirectAttributes) {

            var redirectView = new HtmxLocationRedirectView("/path");
            redirectView.setSource("source");
            redirectView.setEvent("event");
            redirectView.setHandler("handler");
            redirectView.setTarget("target");
            redirectView.setSwap("swap");
            redirectView.setSelect("select");
            redirectView.setValues(new TreeMap<>(Map.of("value1", "v1", "value2", "v2")));
            redirectView.setHeaders(new TreeMap<>(Map.of("header1", "v1", "header2", "v2")));

            redirectAttributes.addAttribute("attr", "value");

            return redirectView;
        }

        @HxRequest
        @GetMapping("/location-redirect-flash-attributes")
        public HtmxLocationRedirectView locationRedirectWithFlashAttributes(RedirectAttributes redirectAttributes) {

            redirectAttributes.addFlashAttribute("flash", "test");
            return new HtmxLocationRedirectView("/path");
        }

        @HxRequest
        @GetMapping("/multiple-views")
        public HtmxView multipleViews() {
            return new HtmxView("view1", "view2");
        }

        @HxRequest
        @GetMapping("/redirect")
        public HtmxRedirectView redirect() {
            return new HtmxRedirectView("/test");
        }

        @HxRequest
        @GetMapping("/redirect-context-relative")
        public HtmxRedirectView redirectContextRelative() {
            return new HtmxRedirectView("/test", true);
        }

        @HxRequest
        @GetMapping("/redirect-expose-model-attributes")
        public HtmxRedirectView redirectExposingModelAttributes(RedirectAttributes attributes) {

            attributes.addAttribute("attr", "value");
            return new HtmxRedirectView("/test");
        }

        @HxRequest
        @GetMapping("/redirect-view-name-prefix-flash-attributes")
        public String redirectWithViewNamePrefixFlashAttributes(RedirectAttributes redirectAttributes) {

            redirectAttributes.addFlashAttribute("flash", "test");
            return "redirect:htmx:/test";
        }

        @HxRequest
        @GetMapping("/redirect-view-name-prefix")
        public String redirectViewNamePrefix() {
            return "redirect:htmx:/test";
        }

        @HxRequest
        @GetMapping("/redirect-flash-attributes")
        public HtmxRedirectView redirectWithFlashAttributes(RedirectAttributes redirectAttributes) {

            redirectAttributes.addFlashAttribute("flash", "test");
            return new HtmxRedirectView("/test");
        }

        @HxRequest
        @GetMapping("/refresh")
        public HtmxRefreshView refresh() {
            return new HtmxRefreshView();
        }

        @HxRequest
        @GetMapping("/refresh-view-name")
        public String refreshViewName() {
            return "refresh:htmx";
        }

        @HxRequest
        @GetMapping("/single-view")
        public HtmxView singleView() {
            return new HtmxView("view1");
        }

        @HxRequest
        @GetMapping("/throw-exception")
        public void throwException() {
            throw new RuntimeException();
        }

    }

}
