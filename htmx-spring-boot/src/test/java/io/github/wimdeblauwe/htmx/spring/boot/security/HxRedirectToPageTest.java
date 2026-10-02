package io.github.wimdeblauwe.htmx.spring.boot.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.stereotype.Controller;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import static io.github.wimdeblauwe.htmx.spring.boot.mvc.HtmxResponseHeader.HX_REDIRECT;
import static io.github.wimdeblauwe.htmx.spring.boot.mvc.HtmxResponseHeader.HX_REFRESH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * An htmx request after the session expired: the browser is sent to the page with {@code HX-Redirect} instead of
 * getting the login page swapped in, and the htmx URL is not saved as the request to return to.
 */
@WebMvcTest(controllers = HxRedirectToPageTest.TestController.class)
@ContextConfiguration(classes = {HxRedirectToPageTest.SecurityConfig.class, HxRedirectToPageTest.TestController.class})
class HxRedirectToPageTest {

    private static final String SAVED_REQUEST = "SPRING_SECURITY_SAVED_REQUEST";

    @Autowired
    private MockMvc mockMvc;

    @Test
    void aNormalRequestStillGoesToTheLoginPage() throws Exception {
        MvcResult result = mockMvc.perform(get("/people").accept(MediaType.TEXT_HTML))
                                  .andExpect(status().is3xxRedirection())
                                  .andExpect(header().string(HttpHeaders.LOCATION, "/login"))
                                  .andExpect(header().doesNotExist(HX_REDIRECT.getValue()))
                                  .andReturn();
        assertThat(result.getRequest().getSession().getAttribute(SAVED_REQUEST)).isNotNull();
    }

    @Test
    void aRequestThatIsNoHtmxRequestGoesToTheApplicationsEntryPointWhateverItAccepts() throws Exception {
        mockMvc.perform(get("/people"))
               .andExpect(status().is3xxRedirection())
               .andExpect(header().string(HttpHeaders.LOCATION, "/login"))
               .andExpect(header().doesNotExist(HX_REDIRECT.getValue()));
    }

    @Test
    void withoutADelegateARequestThatIsNoHtmxRequestGetsAPlainUnauthorized() throws Exception {
        var request = new MockHttpServletRequest("GET", "/people");
        var response = new MockHttpServletResponse();
        new HxRedirectToPageAuthenticationEntryPoint().commence(request, response, new InsufficientAuthenticationException("test"));
        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(response.getHeaderNames()).isEmpty();
    }

    @Test
    void anHtmxRequestIsSentToTheCurrentPage() throws Exception {
        mockMvc.perform(get("/people/rows")
                                .header("HX-Request", "true")
                                .header("HX-Current-URL", "http://localhost/people?sort=name%2Cdesc&q=ada#results"))
               .andExpect(status().isUnauthorized())
               .andExpect(header().string(HX_REDIRECT.getValue(), "/people?sort=name%2Cdesc&q=ada"))
               .andExpect(header().doesNotExist(HttpHeaders.LOCATION))
               .andExpect(header().doesNotExist(HX_REFRESH.getValue()));
    }

    @Test
    void theHtmxRequestIsNotSavedAsTheRequestToReturnTo() throws Exception {
        MvcResult result = mockMvc.perform(get("/people/rows")
                                                   .header("HX-Request", "true")
                                                   .header("HX-Current-URL", "http://localhost/people"))
                                  .andExpect(status().isUnauthorized())
                                  .andReturn();
        var session = result.getRequest().getSession(false);
        assertThat(session == null ? null : session.getAttribute(SAVED_REQUEST)).isNull();
    }

    @Test
    void aBoostedLinkIsSentToThePageItLinksTo() throws Exception {
        mockMvc.perform(get("/people")
                                .queryParam("page", "2")
                                .header("HX-Request", "true")
                                .header("HX-Boosted", "true")
                                .header("HX-Current-URL", "http://localhost/home"))
               .andExpect(status().isUnauthorized())
               .andExpect(header().string(HX_REDIRECT.getValue(), "/people?page=2"));
    }

    @Test
    void aBoostedLinkKeepsTheContextPath() throws Exception {
        mockMvc.perform(get("/app/people")
                                .contextPath("/app")
                                .header("HX-Request", "true")
                                .header("HX-Boosted", "true"))
               .andExpect(header().string(HX_REDIRECT.getValue(), "/app/people"));
    }

    @Test
    void onlyThePathAndQueryOfTheCurrentUrlAreUsed() throws Exception {
        mockMvc.perform(get("/people/rows")
                                .header("HX-Request", "true")
                                .header("HX-Current-URL", "https://elsewhere.example/steal?x=1"))
               .andExpect(header().string(HX_REDIRECT.getValue(), "/steal?x=1"));
    }

    @Test
    void aProtocolRelativePathRefreshesInstead() throws Exception {
        mockMvc.perform(get("/people/rows")
                                .header("HX-Request", "true")
                                .header("HX-Current-URL", "http://localhost//elsewhere.example/steal"))
               .andExpect(status().isUnauthorized())
               .andExpect(header().doesNotExist(HX_REDIRECT.getValue()))
               .andExpect(header().string(HX_REFRESH.getValue(), "true"));
    }

    @Test
    void withoutACurrentUrlThePageIsRefreshed() throws Exception {
        mockMvc.perform(get("/people/rows")
                                .header("HX-Request", "true"))
               .andExpect(status().isUnauthorized())
               .andExpect(header().doesNotExist(HX_REDIRECT.getValue()))
               .andExpect(header().string(HX_REFRESH.getValue(), "true"));
    }

    @Test
    void anUnusableCurrentUrlRefreshesThePage() throws Exception {
        mockMvc.perform(get("/people/rows")
                                .header("HX-Request", "true")
                                .header("HX-Current-URL", "not a url"))
               .andExpect(header().doesNotExist(HX_REDIRECT.getValue()))
               .andExpect(header().string(HX_REFRESH.getValue(), "true"));
    }

    @Test
    void anHtmxPostWhoseSessionExpiredIsSentToTheCurrentPage() throws Exception {
        // No session, so no CSRF token on the server: CsrfFilter calls the access denied handler, not the entry point.
        mockMvc.perform(post("/people/1/delete")
                                .param("_csrf", "token-of-the-expired-session")
                                .header("HX-Request", "true")
                                .header("HX-Current-URL", "http://localhost/people?page=3"))
               .andExpect(status().isForbidden())
               .andExpect(header().string(HX_REDIRECT.getValue(), "/people?page=3"));
    }

    @Test
    void aBoostedFormWhoseSessionExpiredIsSentToThePageOfTheForm() throws Exception {
        mockMvc.perform(post("/people/1/delete")
                                .header("HX-Request", "true")
                                .header("HX-Boosted", "true")
                                .header("HX-Current-URL", "http://localhost/people/1"))
               .andExpect(status().isForbidden())
               .andExpect(header().string(HX_REDIRECT.getValue(), "/people/1"));
    }

    @Test
    void aNormalPostWhoseSessionExpiredIsAPlainForbidden() throws Exception {
        mockMvc.perform(post("/people/1/delete"))
               .andExpect(status().isForbidden())
               .andExpect(header().doesNotExist(HX_REDIRECT.getValue()))
               .andExpect(header().doesNotExist(HX_REFRESH.getValue()));
    }

    @Test
    void anInvalidCsrfTokenIsAPlainForbidden() throws Exception {
        mockMvc.perform(post("/people/1/delete")
                                .with(csrf().useInvalidToken())
                                .header("HX-Request", "true")
                                .header("HX-Current-URL", "http://localhost/people"))
               .andExpect(status().isForbidden())
               .andExpect(header().doesNotExist(HX_REDIRECT.getValue()))
               .andExpect(header().doesNotExist(HX_REFRESH.getValue()));
    }

    @Test
    void aSignedInUserWithoutTheRoleGetsAPlainForbidden() throws Exception {
        mockMvc.perform(get("/admin")
                                .with(user("grace").roles("USER"))
                                .header("HX-Request", "true")
                                .header("HX-Current-URL", "http://localhost/people"))
               .andExpect(status().isForbidden())
               .andExpect(header().doesNotExist(HX_REDIRECT.getValue()))
               .andExpect(header().doesNotExist(HX_REFRESH.getValue()));
    }

    @Test
    void aSignedInHtmxPostWorks() throws Exception {
        mockMvc.perform(post("/people/1/delete")
                                .with(user("ada"))
                                .with(csrf())
                                .header("HX-Request", "true")
                                .header("HX-Current-URL", "http://localhost/people"))
               .andExpect(status().isOk())
               .andExpect(header().doesNotExist(HX_REDIRECT.getValue()));
    }

    @Controller
    static class TestController {

        @GetMapping({"/people", "/people/rows", "/admin", "/home"})
        @ResponseBody
        String page() {
            return "";
        }

        @PostMapping("/people/{id}/delete")
        @ResponseBody
        String delete() {
            return "";
        }

    }

    @EnableWebSecurity
    static class SecurityConfig {

        @Bean
        SecurityFilterChain securityFilterChain(HttpSecurity http) {
            return http.userDetailsService(new InMemoryUserDetailsManager(
                    User.withUsername("ada").password("{noop}pass").roles("ADMIN").build())
            ).formLogin(login -> login
                    .loginPage("/login")
                    .permitAll()
            ).exceptionHandling(exceptions -> exceptions
                    .authenticationEntryPoint(new HxRedirectToPageAuthenticationEntryPoint(
                            new LoginUrlAuthenticationEntryPoint("/login")))
                    .accessDeniedHandler(new HxRedirectToPageAccessDeniedHandler())
            ).authorizeHttpRequests(config -> config
                    .requestMatchers("/home").permitAll()
                    .requestMatchers("/admin").hasRole("ADMIN")
                    .anyRequest().authenticated()
            ).build();
        }

    }

}
