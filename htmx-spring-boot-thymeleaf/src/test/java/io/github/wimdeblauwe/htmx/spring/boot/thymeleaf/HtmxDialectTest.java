package io.github.wimdeblauwe.htmx.spring.boot.thymeleaf;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;

@WebMvcTest(HtmxDialectTest.TestController.class)
@ContextConfiguration(classes = {HtmxDialectTest.TestConfig.class, HtmxDialectTest.TestController.class})
@WithMockUser
class HtmxDialectTest {

    @Autowired
    private MockMvc mockMvc;

    static Stream<Arguments> coreCsrfAttributes() {
        return Stream.of(
                Arguments.of("delete", "/foo"),
                Arguments.of("method", "post"),
                Arguments.of("patch", "/foo"),
                Arguments.of("post", "/foo"),
                Arguments.of("put", "/foo"));
    }

    static Stream<Arguments> coreAttributes() {
        return Stream.of(
                Arguments.of("action", "/foo"),
                Arguments.of("boost", "true"),
                Arguments.of("config", "{&quot;timeout&quot;:12345}"),
                Arguments.of("confirm", "Are you sure?"),
                Arguments.of("disable", "#some-button"),
                Arguments.of("encoding", "multipart/form-data"),
                Arguments.of("get", "/foo"),
                Arguments.of("history-elt", "true"),
                Arguments.of("ignore", "true"),
                Arguments.of("include", "#some-div"),
                Arguments.of("indicator", "#spinner"),
                Arguments.of("morph-skip", "true"),
                Arguments.of("morph-skip-children", "true"),
                Arguments.of("on", "click: alert(1)"),
                Arguments.of("preserve", "true"),
                Arguments.of("push-url", "/foo"),
                Arguments.of("replace-url", "/foo"),
                Arguments.of("select", "#some-div"),
                Arguments.of("select-oob", "#some-div"),
                Arguments.of("status", "404: swap:none"),
                Arguments.of("swap", "outerHTML"),
                Arguments.of("swap-oob", "true"),
                Arguments.of("sync", "closest form:abort"),
                Arguments.of("target", "#some-div"),
                Arguments.of("trigger", "reveal"),
                Arguments.of("validate", "true"),
                Arguments.of("vals", "{&quot;booleanProperty&quot;:true,&quot;stringProperty&quot;:&quot;someString&quot;,&quot;numberProperty&quot;:12345}"),
                Arguments.of("query", "/foo"));
    }

    static Stream<Arguments> extensionAttributes() {
        return Stream.of(
                Arguments.of("browser-indicator", "true"),
                Arguments.of("head", "merge"),
                Arguments.of("headers", "{&quot;X-Custom&quot;:&quot;someString&quot;}"),
                Arguments.of("live", "#some-div"),
                Arguments.of("multipart:close", "done"),
                Arguments.of("multipart:connect", "/foo"),
                Arguments.of("nonce", "someString"),
                Arguments.of("pending", "#some-div"),
                Arguments.of("preload", "mouseover"),
                Arguments.of("prompt", "Enter a value"),
                Arguments.of("ptag", "someString"),
                Arguments.of("sse:close", "done"),
                Arguments.of("sse:connect", "/foo"),
                Arguments.of("targets", "#some-div"),
                Arguments.of("ws:close", "done"),
                Arguments.of("ws:connect", "/foo"));
    }

    @ParameterizedTest(name = "hx-{0}")
    @MethodSource("coreCsrfAttributes")
    void testCsrfCoreAttributes(String attribute, String expectedValue) throws Exception {

        String id = "hx-" + attribute.replace(':', '-');
        MvcResult result = getFragment(id);
        String csrfToken = getCsrfToken(result);
        String fragment = result.getResponse().getContentAsString();

        assertThat(fragment).isEqualTo("<div id=\"%s\" hx-%s=\"%s\" hx-headers=\"{&quot;X-CSRF-TOKEN&quot;:&quot;%s&quot;}\"></div>".formatted(id, attribute, expectedValue, csrfToken));
    }

    @ParameterizedTest(name = "hx-{0}")
    @MethodSource("coreCsrfAttributes")
    void testCsrfCoreAttributesWithExistingHxHeadersAttribute(String attribute, String expectedValue) throws Exception {

        String id = "hx-" + attribute.replace(':', '-') + "-with-headers";
        MvcResult result = getFragment(id);
        String csrfToken = getCsrfToken(result);
        String fragment = result.getResponse().getContentAsString();

        assertThat(fragment).isEqualTo("<div id=\"%s\" hx-%s=\"%s\" hx-headers=\"{&quot;someHeader&quot;:true,&quot;X-CSRF-TOKEN&quot;:&quot;%s&quot;}\"></div>".formatted(id, attribute, expectedValue, csrfToken));
    }

    @ParameterizedTest(name = "hx-{0}")
    @MethodSource("coreAttributes")
    void testCoreAttributes(String attribute, String expectedValue) throws Exception {

        String id = "hx-" + attribute.replace(':', '-');
        String fragment = getFragment(id).getResponse().getContentAsString();

        assertThat(fragment).isEqualTo("<div id=\"%s\" hx-%s=\"%s\"></div>".formatted(id, attribute, expectedValue));
    }

    @ParameterizedTest(name = "hx-{0}")
    @MethodSource("extensionAttributes")
    void testExtensionAttributes(String attribute, String expectedValue) throws Exception {

        String id = "hx-" + attribute.replace(':', '-');
        String fragment = getFragment(id).getResponse().getContentAsString();

        assertThat(fragment).isEqualTo("<div id=\"%s\" hx-%s=\"%s\"></div>".formatted(id, attribute, expectedValue));
    }

    @Test
    void testHxSwapWithEmptyString() throws Exception {

        String id = "hx-swap-with-empty-string";
        String fragment = getFragment(id).getResponse().getContentAsString();

        assertThat(fragment).isEqualTo("<div id=\"%s\" hx-swap=\"\"></div>".formatted(id));
    }

    @Test
    void testHxSwapWithNull() throws Exception {

        String id = "hx-swap-with-null";
        String fragment = getFragment(id).getResponse().getContentAsString();

        assertThat(fragment).isEqualTo("<div id=\"%s\"></div>".formatted(id));
    }

    @Test
    void testHxSwapWithThWith() throws Exception {

        String id = "hx-swap-with-th-with";
        String fragment = getFragment(id).getResponse().getContentAsString();

        assertThat(fragment).isEqualTo("<div id=\"%s\" hx-swap=\"afterend\"></div>".formatted(id));
    }

    @Test
    void testHxTargetWithThEach() throws Exception {

        String id = "hx-target-with-th-each";
        String fragment = getFragment(id).getResponse().getContentAsString();

        assertThat(fragment)
                .contains("<div id=\"%s\" hx-target=\"1\"></div>".formatted(id))
                .contains("<div id=\"%s\" hx-target=\"2\"></div>".formatted(id))
                .contains("<div id=\"%s\" hx-target=\"3\"></div>".formatted(id));
    }

    @Test
    public void testHtmxRequestExpressionObject() throws Exception {

        mockMvc.perform(get("/htmx-dialect-expression-object-factory")
                                .header("HX-Request", "true"))
               .andExpect(status().isOk())
               .andExpect(xpath("//div[@id='htmx-request']").exists());
    }

    @Test
    public void testHtmxRequestExpressionObjectWorkEvenWhenNotHtmxRequest() throws Exception {

        mockMvc.perform(get("/htmx-dialect-expression-object-factory")) // No HX-Request header
               .andExpect(status().isOk())
               .andExpect(xpath("//div[@id='htmx-request']").doesNotExist());
    }

    private static String getCsrfToken(MvcResult result) {

        CsrfToken csrf = (CsrfToken) result.getRequest().getAttribute("_csrf");
        return csrf != null ? csrf.getToken() : "";
    }

    private MvcResult getFragment(String id) throws Exception {

        return mockMvc.perform(get("/htmx-dialect")
                                       .queryParam("selector", "div[@id='" + id + "']"))
                      .andExpect(status().isOk())
                      .andReturn();
    }

    @Controller
    static class TestController {

        @GetMapping("/htmx-dialect")
        public String htmxDialect(@RequestParam("selector") String selector, Model model) {

            model.addAttribute("trueVariable", true);
            model.addAttribute("falseVariable", false);
            model.addAttribute("listVariable", List.of(1, 2, 3));
            model.addAttribute("stringVariable", "someString");
            model.addAttribute("numberVariable", 12345);

            return "htmx-dialect :: " + selector;
        }

        @GetMapping("/htmx-dialect-expression-object-factory")
        public String expressionObjectFactory() {
            return "htmx-dialect-expression-object-factory";
        }
    }

    @Configuration
    static class TestConfig {

        @Bean
        SecurityFilterChain securityFilterChain(HttpSecurity http) {

            return http.authorizeHttpRequests(authorize -> authorize
                               .anyRequest().authenticated())
                       .csrf(Customizer.withDefaults())
                       .build();
        }
    }
}
