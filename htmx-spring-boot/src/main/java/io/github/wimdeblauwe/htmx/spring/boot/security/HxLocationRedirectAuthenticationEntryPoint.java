package io.github.wimdeblauwe.htmx.spring.boot.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.WebAttributes;

import java.io.IOException;


/**
 * Handles navigation on {@link HttpStatus#UNAUTHORIZED} access by delegating to the {@link HxLocationRedirectStrategy},
 * providing an HTMX-friendly redirect mechanism.
 * <p>
 * This class is not used by the library itself, but users of the library can use it to configure their security for
 * native htmx redirects.
 *
 * @author LC Nicolau
 */
public class HxLocationRedirectAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final String redirectUrl;
    private final boolean allowSessionCreation;
    private final RedirectStrategy redirectStrategy;

    public HxLocationRedirectAuthenticationEntryPoint(String redirectUrl) {
        this(redirectUrl, true);
    }

    public HxLocationRedirectAuthenticationEntryPoint(String redirectUrl, boolean allowSessionCreation) {
        this(redirectUrl, allowSessionCreation, new HxLocationRedirectStrategy(HttpStatus.UNAUTHORIZED));
    }

    public HxLocationRedirectAuthenticationEntryPoint(String redirectUrl, boolean allowSessionCreation, RedirectStrategy redirectStrategy) {
        this.redirectUrl = redirectUrl;
        this.allowSessionCreation = allowSessionCreation;
        this.redirectStrategy = redirectStrategy;
    }


    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        if (request.getSession(false) != null || this.allowSessionCreation) {
            request.getSession().setAttribute(WebAttributes.AUTHENTICATION_EXCEPTION, authException);
        }
        redirectStrategy.sendRedirect(request, response, redirectUrl);
    }

}
