package io.github.wimdeblauwe.htmx.spring.boot.thymeleaf;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;

@WebMvcTest(HtmxDialectTestController.class)
@WithMockUser
class HtmxDialectTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testHxGet() throws Exception {
        String html = mockMvc.perform(get("/htmx-dialect"))
                             .andExpect(status().isOk())
                             .andReturn().getResponse().getContentAsString();
        assertThat(html).containsPattern("hx-get-div.*hx-get=\"/foo\"");
    }

    @Test
    void testHxPost() throws Exception {
        String html = mockMvc.perform(get("/htmx-dialect"))
                             .andExpect(status().isOk())
                             .andReturn().getResponse().getContentAsString();
        assertThat(html).containsPattern("hx-post-div.*hx-post=\"/foo\"");
    }

    @Test
    void testHxPut() throws Exception {
        String html = mockMvc.perform(get("/htmx-dialect"))
                             .andExpect(status().isOk())
                             .andReturn().getResponse().getContentAsString();
        assertThat(html).containsPattern("hx-put-div.*hx-put=\"/foo\"");
    }

    @Test
    void testHxDelete() throws Exception {
        String html = mockMvc.perform(get("/htmx-dialect"))
                             .andExpect(status().isOk())
                             .andReturn().getResponse().getContentAsString();
        assertThat(html).containsPattern("hx-delete-div.*hx-delete=\"/foo\"");
    }

    @Test
    void testHxPatch() throws Exception {
        String html = mockMvc.perform(get("/htmx-dialect"))
                             .andExpect(status().isOk())
                             .andReturn().getResponse().getContentAsString();
        assertThat(html).containsPattern("hx-patch-div.*hx-patch=\"/foo\"");
    }

    @Test
    void testHxTarget() throws Exception {
        String html = mockMvc.perform(get("/htmx-dialect"))
                             .andExpect(status().isOk())
                             .andReturn().getResponse().getContentAsString();
        assertThat(html).containsPattern("hx-target-div.*hx-target=\"#some-div\"");
    }

    @Test
    void testHxTrigger() throws Exception {
        String html = mockMvc.perform(get("/htmx-dialect"))
                             .andExpect(status().isOk())
                             .andReturn().getResponse().getContentAsString();
        assertThat(html).containsPattern("hx-trigger-div.*hx-trigger=\"reveal\"");
    }

    @Test
    void testHxSwap() throws Exception {
        String html = mockMvc.perform(get("/htmx-dialect"))
                             .andExpect(status().isOk())
                             .andReturn().getResponse().getContentAsString();
        assertThat(html).containsPattern("hx-swap-div.*hx-swap=\"outerHTML\"");
    }

    @Test
    void testHxSwapWithEmptyString() throws Exception {
        String html = mockMvc.perform(get("/htmx-dialect"))
                             .andExpect(status().isOk())
                             .andReturn().getResponse().getContentAsString();
        assertThat(html).containsPattern("hx-swap-div-with-empty-string.*hx-swap=\"\"");
    }

    @Test
    void testHxSwapWithNull() throws Exception {
        String html = mockMvc.perform(get("/htmx-dialect"))
                             .andExpect(status().isOk())
                             .andReturn().getResponse().getContentAsString();
        assertThat(html).doesNotContainPattern("hx-swap-div-with-null.*hx-swap");
    }

    @Test
    void testWithThWith() throws Exception {
        String html = mockMvc.perform(get("/htmx-dialect"))
                             .andExpect(status().isOk())
                             .andReturn().getResponse().getContentAsString();
        assertThat(html).containsPattern("hx-swap-div-with-th-with.*hx-swap=\"afterend\"");
    }

    @Test
    void testWithThEach() throws Exception {
        String html = mockMvc.perform(get("/htmx-dialect"))
                             .andExpect(status().isOk())
                             .andReturn().getResponse().getContentAsString();
        assertThat(html)
                .containsPattern("hx-target-div-with-th-each.*hx-target=\"1\"")
                .containsPattern("hx-target-div-with-th-each.*hx-target=\"2\"")
                .containsPattern("hx-target-div-with-th-each.*hx-target=\"3\"");
    }

    @Test
    void testWithHxVals() throws Exception {
        String html = mockMvc.perform(get("/htmx-dialect"))
                             .andExpect(status().isOk())
                             .andReturn().getResponse().getContentAsString();

        assertThat(html)
                .containsPattern("hx-vals-div-boolean.*hx-vals=\"\\{&quot;someBooleanProperty&quot;:true}\"")
                .containsPattern("hx-vals-div-string.*hx-vals=\"\\{&quot;someStringProperty&quot;:&quot;someString&quot;}\"")
                .containsPattern("hx-vals-div-number.*hx-vals=\"\\{&quot;someNumberProperty&quot;:12345}\"")
                .containsPattern("hx-vals-div-multiple.*hx-vals=\"\\{&quot;someBooleanProperty&quot;:true,&quot;someNumberProperty&quot;:12345}\"");
    }

    @Test
    void testHxValidate() throws Exception {
        String html = mockMvc.perform(get("/htmx-dialect"))
                             .andExpect(status().isOk())
                             .andReturn().getResponse().getContentAsString();
        assertThat(html).containsPattern("hx-validate-div.*hx-validate=\"true\"");
    }

    @Test
    public void testHtmxRequestExpressionObject() throws Exception {
        mockMvc.perform(get("/htmx-dialect-expression-object-factory")
                                .header("HX-Request", "true"))
               .andExpect(status().isOk())
               .andExpect(xpath("//div[@id='htmxRequest']").exists());
    }

    @Test
    public void testHtmxRequestExpressionObjectWorkEvenWhenNotHtmxRequest() throws Exception {
        mockMvc.perform(get("/htmx-dialect-expression-object-factory")) // No HX-Request header
               .andExpect(status().isOk())
               .andExpect(xpath("//div[@id='htmxRequest']").doesNotExist());
    }

}
