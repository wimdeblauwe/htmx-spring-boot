package io.github.wimdeblauwe.htmx.spring.boot.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.stereotype.Controller;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import static io.github.wimdeblauwe.htmx.spring.boot.mvc.HtmxResponseHeader.HX_LOCATION;
import static io.github.wimdeblauwe.htmx.spring.boot.mvc.HtmxResponseHeader.HX_REDIRECT;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * The README's combination: a login form submitted with htmx ({@code HxLocationRedirect*} handlers) and the
 * {@code HxRedirectToPage*} classes for an expired session. After signing in, the user is back on the page, not on the
 * fragment URL of the htmx request that found the session expired.
 */
@WebMvcTest(controllers = HxRedirectToPageWithHtmxLoginTest.TestController.class)
@ContextConfiguration(classes = {HxRedirectToPageWithHtmxLoginTest.SecurityConfig.class, HxRedirectToPageWithHtmxLoginTest.TestController.class})
class HxRedirectToPageWithHtmxLoginTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void afterSigningInWithHtmxTheUserIsBackOnThePage() throws Exception {
        var session = new MockHttpSession();

        // The htmx request that finds the session expired.
        mockMvc.perform(get("/people/rows")
                                .session(session)
                                .header("HX-Request", "true")
                                .header("HX-Current-URL", "http://localhost/people?page=2"))
               .andExpect(status().isUnauthorized())
               .andExpect(header().string(HX_REDIRECT.getValue(), "/people?page=2"));

        // The browser follows HX-Redirect: the login form's entry point saves the page.
        mockMvc.perform(get("/people").queryParam("page", "2").session(session).accept(MediaType.TEXT_HTML))
               .andExpect(status().is3xxRedirection())
               .andExpect(header().string(HttpHeaders.LOCATION, "/login"));

        // The login form, submitted with htmx.
        mockMvc.perform(post("/login")
                                .session(session)
                                .header("HX-Request", "true")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .param("username", "ada")
                                .param("password", "pass")
                                .with(csrf()))
               .andExpect(status().isOk())
               .andExpect(header().string(HX_LOCATION.getValue(), "http://localhost/people?page=2&continue"));
    }

    @Test
    void withoutASavedPageTheSuccessUrlIsUsed() throws Exception {
        mockMvc.perform(post("/login")
                                .header("HX-Request", "true")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .param("username", "ada")
                                .param("password", "pass")
                                .with(csrf()))
               .andExpect(status().isOk())
               .andExpect(header().string(HX_LOCATION.getValue(), "/home?login"));
    }

    @Controller
    static class TestController {

        @GetMapping({"/people", "/people/rows", "/home"})
        @ResponseBody
        String page() {
            return "";
        }

    }

    @EnableWebSecurity
    static class SecurityConfig {

        @Bean
        SecurityFilterChain securityFilterChain(HttpSecurity http) {
            return http.userDetailsService(new InMemoryUserDetailsManager(
                    User.withUsername("ada").password("{noop}pass").roles("USER").build())
            ).formLogin(login -> login
                    .loginPage("/login")
                    .permitAll()
                    .failureHandler(new HxLocationRedirectAuthenticationFailureHandler("/login?failure"))
                    .successHandler(new HxLocationRedirectAuthenticationSuccessHandler("/home?login"))
            ).logout(logout -> logout
                    .logoutSuccessHandler(new HxLocationRedirectLogoutSuccessHandler("/home?logout"))
            ).exceptionHandling(exceptions -> exceptions
                    .authenticationEntryPoint(new HxRedirectToPageAuthenticationEntryPoint(
                            new LoginUrlAuthenticationEntryPoint("/login")))
                    .accessDeniedHandler(new HxRedirectToPageAccessDeniedHandler())
            ).authorizeHttpRequests(config -> config
                    .requestMatchers("/home").permitAll()
                    .anyRequest().authenticated()
            ).build();
        }

    }

}
