package io.github.wimdeblauwe.htmx.spring.boot.thymeleaf;

import org.junit.jupiter.api.Test;
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
import org.springframework.web.bind.annotation.GetMapping;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Verifies automatic CSRF token injection into elements using unsafe HTTP methods
 * ({@code hx:post}, {@code hx:put}, {@code hx:patch}, {@code hx:delete}).
 * <p>
 * Tests cover the following scenarios:
 * <ul>
 *     <li>The CSRF token is automatically injected into the {@code hx-headers} attribute.</li>
 *     <li>Correct merging when {@code hx-headers} already contains additional entries.</li>
 *     <li>Proper behavior when CSRF protection is disabled or no token is present.</li>
 * </ul>
 *
 * @author LC Nicolau
 * @since 5.1.0
 */
@WebMvcTest(controllers = HtmxCsrfTest.TestController.class)
@ContextConfiguration(classes = {HtmxCsrfTest.TestController.class, HtmxCsrfTest.SecurityConfig.class})
@WithMockUser
class HtmxCsrfTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testHxPostCsrf() throws Exception {
        MvcResult result = mockMvc.perform(get("/htmx-csrf"))
                                  .andExpect(status().isOk())
                                  .andReturn();

        String html = result.getResponse().getContentAsString();
        assertThat(html)
                .containsPattern("hx-post-div.*hx-post=\"/foo\"")
                .containsPattern("hx-post-headers.*hx-post=\"/foo\"");

        CsrfToken csrf = ((CsrfToken) result.getRequest().getAttribute("_csrf"));
        if (csrf == null) {
            assertThat(html)
                    .doesNotContainPattern("hx-post-div.*hx-headers=")
                    .doesNotContainPattern("hx-post-headers.*hx-headers=.*X-CSRF-TOKEN");
        } else {
            String token = csrf.getToken();
            assertThat(html)
                    .containsPattern("hx-post-div.*hx-headers=\"\\{&quot;X-CSRF-TOKEN&quot;:&quot;" + token + "&quot;}\"")
                    .containsPattern("hx-post-headers.*hx-headers=\"\\{&quot;someHeader&quot;:true,&quot;X-CSRF-TOKEN&quot;:&quot;" + token + "&quot;}\"");
        }
    }

    @Test
    void testHxPutCsrf() throws Exception {
        MvcResult result = mockMvc.perform(get("/htmx-csrf"))
                                  .andExpect(status().isOk())
                                  .andReturn();

        String html = result.getResponse().getContentAsString();
        assertThat(html)
                .containsPattern("hx-put-div.*hx-put=\"/foo\"")
                .containsPattern("hx-put-headers.*hx-put=\"/foo\"");

        CsrfToken csrf = ((CsrfToken) result.getRequest().getAttribute("_csrf"));
        if (csrf == null) {
            assertThat(html)
                    .doesNotContainPattern("hx-put-div.*hx-headers=")
                    .doesNotContainPattern("hx-put-headers.*hx-headers=.*X-CSRF-TOKEN");
        } else {
            String token = csrf.getToken();
            assertThat(html)
                    .containsPattern("hx-put-div.*hx-headers=\"\\{&quot;X-CSRF-TOKEN&quot;:&quot;" + token + "&quot;}\"")
                    .containsPattern("hx-put-headers.*hx-headers=\"\\{&quot;someHeader&quot;:true,&quot;X-CSRF-TOKEN&quot;:&quot;" + token + "&quot;}\"");
        }
    }

    @Test
    void testHxPatchCsrf() throws Exception {
        MvcResult result = mockMvc.perform(get("/htmx-csrf"))
                                  .andExpect(status().isOk())
                                  .andReturn();

        String html = result.getResponse().getContentAsString();
        assertThat(html)
                .containsPattern("hx-patch-div.*hx-patch=\"/foo\"")
                .containsPattern("hx-patch-headers.*hx-patch=\"/foo\"");

        CsrfToken csrf = ((CsrfToken) result.getRequest().getAttribute("_csrf"));
        if (csrf == null) {
            assertThat(html)
                    .doesNotContainPattern("hx-patch-div.*hx-headers=")
                    .doesNotContainPattern("hx-patch-headers.*hx-headers=.*X-CSRF-TOKEN");
        } else {
            String token = csrf.getToken();
            assertThat(html)
                    .containsPattern("hx-patch-div.*hx-headers=\"\\{&quot;X-CSRF-TOKEN&quot;:&quot;" + token + "&quot;}\"")
                    .containsPattern("hx-patch-headers.*hx-headers=\"\\{&quot;someHeader&quot;:true,&quot;X-CSRF-TOKEN&quot;:&quot;" + token + "&quot;}\"");
        }
    }

    @Test
    void testHxDeleteCsrf() throws Exception {
        MvcResult result = mockMvc.perform(get("/htmx-csrf"))
                                  .andExpect(status().isOk())
                                  .andReturn();

        String html = result.getResponse().getContentAsString();
        assertThat(html)
                .containsPattern("hx-delete-div.*hx-delete=\"/foo\"")
                .containsPattern("hx-delete-headers.*hx-delete=\"/foo\"");

        CsrfToken csrf = ((CsrfToken) result.getRequest().getAttribute("_csrf"));
        if (csrf == null) {
            assertThat(html)
                    .doesNotContainPattern("hx-delete-div.*hx-headers=")
                    .doesNotContainPattern("hx-delete-headers.*hx-headers=.*X-CSRF-TOKEN");
        } else {
            String token = csrf.getToken();
            assertThat(html)
                    .containsPattern("hx-delete-div.*hx-headers=\"\\{&quot;X-CSRF-TOKEN&quot;:&quot;" + token + "&quot;}\"")
                    .containsPattern("hx-delete-headers.*hx-headers=\"\\{&quot;someHeader&quot;:true,&quot;X-CSRF-TOKEN&quot;:&quot;" + token + "&quot;}\"");
        }
    }

    @Controller
    static class TestController {

        @GetMapping("/htmx-csrf")
        public String csrf() {
            return "htmx-csrf";
        }

    }

    @Configuration
    static class SecurityConfig {

        @Bean
        SecurityFilterChain securityFilterChain(HttpSecurity http) {
            return http.authorizeHttpRequests(config -> config
                               .requestMatchers("/htmx-csrf").authenticated())
                       .csrf(Customizer.withDefaults())
                       .build();
        }

    }

}
