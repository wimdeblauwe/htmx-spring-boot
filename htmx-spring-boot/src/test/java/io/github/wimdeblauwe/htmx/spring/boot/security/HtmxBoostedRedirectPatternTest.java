package io.github.wimdeblauwe.htmx.spring.boot.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.stereotype.Controller;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import static io.github.wimdeblauwe.htmx.spring.boot.mvc.HeaderResultMatchers.header;
import static io.github.wimdeblauwe.htmx.spring.boot.mvc.HtmxResponseHeader.HX_LOCATION;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.testSecurityContext;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Verifies that security handlers apply the appropriate htmx-friendly “boosted” redirect strategy based on the request
 * type. Tests include login failure and success, logout, unauthorized access, and forbidden access.
 *
 * @author LC Nicolau
 * @since 5.0.0
 */
@WebMvcTest(controllers = HtmxBoostedRedirectPatternTest.TestController.class)
@ContextConfiguration(classes = HtmxBoostedRedirectPatternTest.SecurityConfig.class)
class HtmxBoostedRedirectPatternTest {

    private static final String USERNAME = "user";
    private static final String PASSWORD = "pass";
    private static final String LOGIN_FAILURE_URL = "/login?failure";
    private static final String LOGIN_SUCCESS_URL = "/home?login";
    private static final String LOGOUT_SUCCESS_URL = "/home?logout";
    private static final String UNAUTHORIZED_URL = "/login?unauthorized";
    private static final String FORBIDDEN_URL = "/error?forbidden";
    private static final String HX_BOOSTED_TEMPLATE = """
            {
              "path": "%s",
              "headers": {
                "HX-Boosted": "true"
              },
              "target": "body"
            }
            """;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldSendRedirectOnFailedLogin() throws Exception {
        mockMvc.perform(post("/login")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .param("username", "Invalid")
                                .param("password", "Invalid")
                                .with(csrf()))
               .andExpect(status().is3xxRedirection())
               .andExpect(header().string(HttpHeaders.LOCATION, LOGIN_FAILURE_URL));
    }

    @Test
    void shouldSendHxRedirectOnFailedHxLogin() throws Exception {
        mockMvc.perform(post("/login")
                                .header("HX-Request", "true")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .param("username", "Invalid")
                                .param("password", "Invalid")
                                .with(csrf()))
               .andExpect(status().isUnauthorized())
               .andExpect(header().json(HX_LOCATION.getValue(), HX_BOOSTED_TEMPLATE.formatted(LOGIN_FAILURE_URL)));
    }

    @Test
    void shouldSendRedirectOnSuccessfulLogin() throws Exception {
        mockMvc.perform(post("/login")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .param("username", USERNAME)
                                .param("password", PASSWORD)
                                .with(csrf()))
               .andExpect(status().is3xxRedirection())
               .andExpect(header().string(HttpHeaders.LOCATION, LOGIN_SUCCESS_URL));
    }

    @Test
    void shouldSendHxRedirectOnSuccessfulHxLogin() throws Exception {
        mockMvc.perform(post("/login")
                                .header("HX-Request", "true")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .param("username", USERNAME)
                                .param("password", PASSWORD)
                                .with(csrf()))
               .andExpect(status().isOk())
               .andExpect(header().json(HX_LOCATION.getValue(), HX_BOOSTED_TEMPLATE.formatted(LOGIN_SUCCESS_URL)));
    }

    @Test
    @WithMockUser
    void shouldSendRedirectOnSuccessfulLogout() throws Exception {
        mockMvc.perform(post("/logout")
                                .with(testSecurityContext())
                                .with(csrf()))
               .andExpect(status().is3xxRedirection())
               .andExpect(header().string(HttpHeaders.LOCATION, LOGOUT_SUCCESS_URL));
    }

    @Test
    @WithMockUser
    void shouldSendHxRedirectOnSuccessfulHxLogout() throws Exception {
        mockMvc.perform(post("/logout")
                                .header("HX-Request", "true")
                                .with(testSecurityContext())
                                .with(csrf()))
               .andExpect(status().isOk())
               .andExpect(header().json(HX_LOCATION.getValue(), HX_BOOSTED_TEMPLATE.formatted(LOGOUT_SUCCESS_URL)));
    }

    @Test
    void shouldSendRedirectOnUnauthorizedRequest() throws Exception {
        mockMvc.perform(get("/unauthorized"))
               .andExpect(status().is3xxRedirection())
               .andExpect(header().string(HttpHeaders.LOCATION, UNAUTHORIZED_URL));
    }

    @Test
    void shouldSendHxRedirectOnUnauthorizedHxRequest() throws Exception {
        mockMvc.perform(get("/unauthorized")
                                .header("HX-Request", "true"))
               .andExpect(status().isUnauthorized())
               .andExpect(header().json(HX_LOCATION.getValue(), HX_BOOSTED_TEMPLATE.formatted(UNAUTHORIZED_URL)));
    }

    @Test
    @WithMockUser
    void shouldSendRedirectOnForbiddenRequest() throws Exception {
        mockMvc.perform(get("/forbidden")
                                .with(testSecurityContext()))
               .andExpect(status().is3xxRedirection())
               .andExpect(header().string(HttpHeaders.LOCATION, FORBIDDEN_URL));
    }

    @Test
    @WithMockUser
    void shouldSendHxRedirectOnForbiddenHxRequest() throws Exception {
        mockMvc.perform(get("/forbidden")
                                .header("HX-Request", "true")
                                .with(testSecurityContext()))
               .andExpect(status().isForbidden())
               .andExpect(header().json(HX_LOCATION.getValue(), HX_BOOSTED_TEMPLATE.formatted(FORBIDDEN_URL)));
    }

    @Controller
    static class TestController {

        @GetMapping("/home")
        @ResponseBody
        String home() {
            return "";
        }

    }

    @EnableWebSecurity
    static class SecurityConfig {

        @Bean
        SecurityFilterChain securityFilterChain(HttpSecurity http) {
            return http.userDetailsService(new InMemoryUserDetailsManager(
                    User.withUsername(USERNAME).password("{noop}" + PASSWORD).build())
            ).formLogin(login -> login
                    .failureHandler(new HxLocationRedirectAuthenticationFailureHandler(LOGIN_FAILURE_URL, new HxLocationBoostedRedirectStrategy(HttpStatus.UNAUTHORIZED)))
                    .successHandler(new HxLocationRedirectAuthenticationSuccessHandler(LOGIN_SUCCESS_URL, new HxLocationBoostedRedirectStrategy()))
            ).logout(logout -> logout
                    .logoutSuccessHandler(new HxLocationRedirectLogoutSuccessHandler(LOGOUT_SUCCESS_URL, new HxLocationBoostedRedirectStrategy()))
            ).exceptionHandling(handler -> handler
                    .authenticationEntryPoint(new HxLocationRedirectAuthenticationEntryPoint(UNAUTHORIZED_URL, new HxLocationBoostedRedirectStrategy(HttpStatus.UNAUTHORIZED)))
                    .accessDeniedHandler(new HxLocationRedirectAccessDeniedHandler(FORBIDDEN_URL, new HxLocationBoostedRedirectStrategy(HttpStatus.FORBIDDEN)))
            ).authorizeHttpRequests(config -> config
                    .requestMatchers("/home", "/login", "/error").permitAll()
                    .requestMatchers("/forbidden").hasRole("ADMIN")
                    .anyRequest().authenticated()
            ).build();
        }

    }

}
