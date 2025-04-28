package io.github.wimdeblauwe.htmx.spring.boot.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.RedirectStrategy;

import java.io.IOException;

/**
 * Handles navigation on {@link HttpStatus#UNAUTHORIZED} access by delegating to the {@link HxLocationRedirectStrategy},
 * providing an htmx-friendly redirect mechanism.
 * <p>
 * This class is not used by the library itself, but users of the library can use it to configure their security for
 * native htmx redirects.
 *
 * @author LC Nicolau
 * @since 5.0.0
 */
public class HxLocationRedirectAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final String redirectUrl;
    private final RedirectStrategy redirectStrategy;

    public HxLocationRedirectAuthenticationEntryPoint(String redirectUrl) {
        this(redirectUrl, new HxLocationRedirectStrategy(HttpStatus.UNAUTHORIZED));
    }

    public HxLocationRedirectAuthenticationEntryPoint(String redirectUrl, RedirectStrategy redirectStrategy) {
        this.redirectUrl = redirectUrl;
        this.redirectStrategy = redirectStrategy;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        redirectStrategy.sendRedirect(request, response, redirectUrl);
    }

}
