package io.github.wimdeblauwe.htmx.spring.boot.mvc;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import static io.github.wimdeblauwe.htmx.spring.boot.mvc.HtmxRequestHeader.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;


@WebMvcTest(HtmxHandlerMethodArgumentResolverTest.TestController.class)
@ContextConfiguration(classes = HtmxHandlerMethodArgumentResolverTest.TestController.class)
@WithMockUser
class HtmxHandlerMethodArgumentResolverTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private TestService service;

    @Test
    void testIfNonHtmxRequest() throws Exception {
        mockMvc.perform(get("/method-arg-resolver"));

        ArgumentCaptor<HtmxRequest> captor = ArgumentCaptor.forClass(HtmxRequest.class);
        verify(service).doSomething(captor.capture());

        HtmxRequest request = captor.getValue();
        assertThat(request).isNotNull();
        assertThat(request.isHtmxRequest()).isFalse();
    }

    @Test
    void testIfHtmxRequest() throws Exception {
        mockMvc.perform(get("/method-arg-resolver")
                                .header(HX_REQUEST.getValue(), "true"));

        ArgumentCaptor<HtmxRequest> captor = ArgumentCaptor.forClass(HtmxRequest.class);
        verify(service).doSomething(captor.capture());

        HtmxRequest request = captor.getValue();
        assertThat(request).isNotNull();
        assertThat(request.isHtmxRequest()).isTrue();
        assertThat(request.isBoosted()).isFalse();
    }

    @Test
    void testIsPartialShouldReturnFalseOnFullRequest() throws Exception {

        mockMvc.perform(get("/method-arg-resolver")
                                .header(HX_REQUEST.getValue(), "true")
                                .header(HX_REQUEST_TYPE.getValue(), "full"));

        ArgumentCaptor<HtmxRequest> captor = ArgumentCaptor.forClass(HtmxRequest.class);
        verify(service).doSomething(captor.capture());

        HtmxRequest request = captor.getValue();
        assertThat(request).isNotNull();
        assertThat(request.isHtmxRequest()).isTrue();
        assertThat(request.isPartialRequest()).isFalse();
    }

    @Test
    void testIsPartialShouldReturnTrueOnPartialRequest() throws Exception {

        mockMvc.perform(get("/method-arg-resolver")
                                .header(HX_REQUEST.getValue(), "true")
                                .header(HX_REQUEST_TYPE.getValue(), "partial"));

        ArgumentCaptor<HtmxRequest> captor = ArgumentCaptor.forClass(HtmxRequest.class);
        verify(service).doSomething(captor.capture());

        HtmxRequest request = captor.getValue();
        assertThat(request).isNotNull();
        assertThat(request.isHtmxRequest()).isTrue();
        assertThat(request.isPartialRequest()).isTrue();
    }

    @Test
    void testIsPartialShouldThrowExceptionOnMissingHeader() throws Exception {

        mockMvc.perform(get("/method-arg-resolver")
                                .header(HX_REQUEST.getValue(), "true"));

        ArgumentCaptor<HtmxRequest> captor = ArgumentCaptor.forClass(HtmxRequest.class);
        verify(service).doSomething(captor.capture());

        HtmxRequest request = captor.getValue();
        assertThat(request).isNotNull();
        assertThat(request.isHtmxRequest()).isTrue();
        assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(request::isPartialRequest)
                .withMessageContaining("'HX-Request-Type' header is missing");
    }

    @Test
    void testHxBoosted() throws Exception {
        mockMvc.perform(get("/method-arg-resolver")
                                .header(HX_REQUEST.getValue(), "true")
                                .header(HX_BOOSTED.getValue(), "true"));

        ArgumentCaptor<HtmxRequest> captor = ArgumentCaptor.forClass(HtmxRequest.class);
        verify(service).doSomething(captor.capture());

        HtmxRequest request = captor.getValue();
        assertThat(request).isNotNull();
        assertThat(request.isHtmxRequest()).isTrue();
        assertThat(request.isBoosted()).isTrue();
    }

    @Test
    void testHxCurrentUrl() throws Exception {
        mockMvc.perform(get("/method-arg-resolver")
                                .header(HX_REQUEST.getValue(), "true")
                                .header(HX_CURRENT_URL.getValue(), "http://localhost:8080/"));

        ArgumentCaptor<HtmxRequest> captor = ArgumentCaptor.forClass(HtmxRequest.class);
        verify(service).doSomething(captor.capture());

        HtmxRequest request = captor.getValue();
        assertThat(request).isNotNull();
        assertThat(request.isHtmxRequest()).isTrue();
        assertThat(request.isBoosted()).isFalse();
        assertThat(request.getCurrentUrl()).isEqualTo("http://localhost:8080/");
    }

    @Test
    void testHxHistoryRestoreRequest() throws Exception {
        mockMvc.perform(get("/method-arg-resolver")
                                .header(HX_REQUEST.getValue(), "true")
                                .header("HX-History-Restore-Request", "true"));

        ArgumentCaptor<HtmxRequest> captor = ArgumentCaptor.forClass(HtmxRequest.class);
        verify(service).doSomething(captor.capture());

        HtmxRequest request = captor.getValue();
        assertThat(request).isNotNull();
        assertThat(request.isHtmxRequest()).isTrue();
        assertThat(request.isBoosted()).isFalse();
        assertThat(request.isHistoryRestoreRequest()).isTrue();
    }

    @Test
    void testHxPrompt() throws Exception {
        mockMvc.perform(get("/method-arg-resolver")
                                .header(HX_REQUEST.getValue(), "true")
                                .header(HX_PROMPT.getValue(), "Yes"));

        ArgumentCaptor<HtmxRequest> captor = ArgumentCaptor.forClass(HtmxRequest.class);
        verify(service).doSomething(captor.capture());

        HtmxRequest request = captor.getValue();
        assertThat(request).isNotNull();
        assertThat(request.isHtmxRequest()).isTrue();
        assertThat(request.isBoosted()).isFalse();
        assertThat(request.getPromptResponse()).isEqualTo("Yes");
    }

    @Test
    void testHxSource() throws Exception {

        mockMvc.perform(get("/method-arg-resolver")
                                .header(HX_REQUEST.getValue(), "true")
                                .header(HX_SOURCE.getValue(), "tag#id"));

        ArgumentCaptor<HtmxRequest> captor = ArgumentCaptor.forClass(HtmxRequest.class);
        verify(service).doSomething(captor.capture());

        HtmxRequest request = captor.getValue();
        assertThat(request).isNotNull();
        assertThat(request.isHtmxRequest()).isTrue();
        assertThat(request.getSource()).isEqualTo("tag#id");
        assertThat(request.getSourceElementName()).isEqualTo("tag");
        assertThat(request.getSourceElementId()).isEqualTo("id");
    }

    @Test
    void testHxTarget() throws Exception {
        mockMvc.perform(get("/method-arg-resolver")
                                .header(HX_REQUEST.getValue(), "true")
                                .header(HX_TARGET.getValue(), "element#id"));

        ArgumentCaptor<HtmxRequest> captor = ArgumentCaptor.forClass(HtmxRequest.class);
        verify(service).doSomething(captor.capture());

        HtmxRequest request = captor.getValue();
        assertThat(request).isNotNull();
        assertThat(request.isHtmxRequest()).isTrue();
        assertThat(request.isBoosted()).isFalse();
        assertThat(request.getTarget()).isEqualTo("element#id");
        assertThat(request.getTargetElementName()).isEqualTo("element");
        assertThat(request.getTargetElementId()).isEqualTo("id");
    }

    @Test
    void testHxRequestAnnotation() throws Exception {
        mockMvc.perform(get("/method-arg-resolver/users")
                                .header(HX_REQUEST.getValue(), "true"))
               .andExpect(view().name("users :: list"));
    }

    @Test
    void testHxRequestAnnotationInheritance() throws Exception {
        mockMvc.perform(get("/method-arg-resolver/users/inherited")
                                .header(HX_REQUEST.getValue(), "true"))
               .andExpect(view().name("users :: list"));
    }

    @Test
    void testHxRequestSameUrlNoAnnotation() throws Exception {
        mockMvc.perform(get("/method-arg-resolver/users"))
               .andExpect(view().name("users"));
    }

    @Controller
    @RequestMapping("/method-arg-resolver")
    static class TestController {

        @Autowired
        private TestService service;

        @GetMapping
        @ResponseBody
        public String htmxRequestDetails(HtmxRequest details) {
            service.doSomething(details);

            return "";
        }

        @GetMapping("/users")
        @HxRequest
        public String htmxRequest(HtmxRequest details) {
            service.doSomething(details);

            return "users :: list";
        }

        @GetMapping("/users")
        public String normalRequest(HtmxRequest details) {
            service.doSomething(details);

            return "users";
        }

        @HxGetMapping("/users/inherited")
        public String htmxRequestInheritance(HtmxRequest details) {
            service.doSomething(details);

            return "users :: list";
        }

        @GetMapping("/users/inherited")
        public String normalRequestInheritance(HtmxRequest details) {
            service.doSomething(details);

            return "users";
        }
    }

    @Service
    public class TestService {
        void doSomething(HtmxRequest details) {
        }
    }

}
