package io.github.wimdeblauwe.htmx.spring.boot.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import java.io.IOException;

/**
 * Handles a failed authentication attempt by delegating to the {@link SimpleUrlAuthenticationFailureHandler}, using
 * {@link HxLocationRedirectStrategy} to provide an htmx-friendly redirect mechanism.
 * <p>
 * This class is not used by the library itself, but users of the library can use it to configure their security for
 * native htmx redirects.
 *
 * @author LC Nicolau
 * @since 5.0.0
 */
public class HxLocationRedirectAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private final AuthenticationFailureHandler delegate;

    public HxLocationRedirectAuthenticationFailureHandler(String defaultFailureUrl) {
        this(defaultFailureUrl, new HxLocationRedirectStrategy(HttpStatus.UNAUTHORIZED));
    }

    public HxLocationRedirectAuthenticationFailureHandler(String defaultFailureUrl, RedirectStrategy redirectStrategy) {
        var handler = new SimpleUrlAuthenticationFailureHandler();
        handler.setDefaultFailureUrl(defaultFailureUrl);
        handler.setRedirectStrategy(redirectStrategy);
        this.delegate = handler;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        delegate.onAuthenticationFailure(request, response, exception);
    }

}
