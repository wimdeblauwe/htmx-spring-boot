package io.github.wimdeblauwe.htmx.spring.boot.mvc;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;


@WebMvcTest(HtmxHandlerMethodArgumentResolverTestController.class)
@WithMockUser
class HtmxHandlerMethodArgumentResolverTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
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
                                .header("HX-Request", "true"));

        ArgumentCaptor<HtmxRequest> captor = ArgumentCaptor.forClass(HtmxRequest.class);
        verify(service).doSomething(captor.capture());

        HtmxRequest request = captor.getValue();
        assertThat(request).isNotNull();
        assertThat(request.isHtmxRequest()).isTrue();
        assertThat(request.isBoosted()).isFalse();
    }

    @Test
    void testHxBoosted() throws Exception {
        mockMvc.perform(get("/method-arg-resolver")
                                .header("HX-Request", "true")
                                .header("HX-Boosted", "true"));

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
                                .header("HX-Request", "true")
                                .header("HX-Current-URL", "http://localhost:8080/"));

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
                                .header("HX-Request", "true")
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
                                .header("HX-Request", "true")
                                .header("HX-Prompt", "Yes"));

        ArgumentCaptor<HtmxRequest> captor = ArgumentCaptor.forClass(HtmxRequest.class);
        verify(service).doSomething(captor.capture());

        HtmxRequest request = captor.getValue();
        assertThat(request).isNotNull();
        assertThat(request.isHtmxRequest()).isTrue();
        assertThat(request.isBoosted()).isFalse();
        assertThat(request.getPromptResponse()).isEqualTo("Yes");
    }

    @Test
    void testHxTarget() throws Exception {
        mockMvc.perform(get("/method-arg-resolver")
                                .header("HX-Request", "true")
                                .header("HX-Target", "button-1"));

        ArgumentCaptor<HtmxRequest> captor = ArgumentCaptor.forClass(HtmxRequest.class);
        verify(service).doSomething(captor.capture());

        HtmxRequest request = captor.getValue();
        assertThat(request).isNotNull();
        assertThat(request.isHtmxRequest()).isTrue();
        assertThat(request.isBoosted()).isFalse();
        assertThat(request.getTarget()).isEqualTo("button-1");
    }

    @Test
    void testHxTriggerName() throws Exception {
        mockMvc.perform(get("/method-arg-resolver")
                                .header("HX-Request", "true")
                                .header("HX-Trigger-Name", "myTriggerName"));

        ArgumentCaptor<HtmxRequest> captor = ArgumentCaptor.forClass(HtmxRequest.class);
        verify(service).doSomething(captor.capture());

        HtmxRequest request = captor.getValue();
        assertThat(request).isNotNull();
        assertThat(request.isHtmxRequest()).isTrue();
        assertThat(request.isBoosted()).isFalse();
        assertThat(request.getTriggerName()).isEqualTo("myTriggerName");
    }

    @Test
    void testHxTriggerId() throws Exception {
        mockMvc.perform(get("/method-arg-resolver")
                                .header("HX-Request", "true")
                                .header("HX-Trigger", "myTriggerId"));

        ArgumentCaptor<HtmxRequest> captor = ArgumentCaptor.forClass(HtmxRequest.class);
        verify(service).doSomething(captor.capture());

        HtmxRequest request = captor.getValue();
        assertThat(request).isNotNull();
        assertThat(request.isHtmxRequest()).isTrue();
        assertThat(request.isBoosted()).isFalse();
        assertThat(request.getTriggerId()).isEqualTo("myTriggerId");
    }


    @Test
    void testHxRequestAnnotation() throws Exception {
        mockMvc.perform(get("/method-arg-resolver/users")
                                .header("HX-Request", "true"))
               .andExpect(view().name("users :: list"));
        ;
    }

    @Test
    void testHxRequestAnnotationInheritance() throws Exception {
        mockMvc.perform(get("/method-arg-resolver/users/inherited")
                                .header("HX-Request", "true"))
               .andExpect(view().name("users :: list"));
        ;
    }

    @Test
    void testHxRequestSameUrlNoAnnotation() throws Exception {
        mockMvc.perform(get("/method-arg-resolver/users"))
               .andExpect(view().name("users"));
        ;
    }
}
