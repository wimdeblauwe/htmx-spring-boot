package io.github.wimdeblauwe.htmx.spring.boot.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
import org.springframework.security.web.csrf.MissingCsrfTokenException;
import org.springframework.util.Assert;

import java.io.IOException;

/**
 * Answers an htmx request whose CSRF token is missing on the server, because the session that held it expired, with
 * {@link HttpStatus#FORBIDDEN} and {@code HX-Redirect} to the page the request was made from.
 * <p>
 * Without it, a POST (or PUT, PATCH, DELETE) after the session expired gets a plain 403, which htmx does not swap:
 * nothing happens on the page. With it, the browser loads the page as a normal navigation, the application's entry
 * point asks the user to sign in, and the page is the request to return to.
 * <p>
 * Every other denial, including a signed-in user without the required role, goes to the delegate
 * ({@link AccessDeniedHandlerImpl} by default, or the handler your application had). Set it as the access denied
 * handler, which {@code CsrfFilter} uses too:
 * <pre> {@code
 * http.exceptionHandling(exceptions -> exceptions
 *         .accessDeniedHandler(new HxRedirectToPageAccessDeniedHandler()));
 * } </pre>
 * <p>
 * This class is not used by the library itself, but users of the library can use it to configure their security.
 *
 * @see HxRedirectToPageAuthenticationEntryPoint
 * @since 5.2.0
 */
public class HxRedirectToPageAccessDeniedHandler implements AccessDeniedHandler {

    private final AccessDeniedHandler delegate;

    public HxRedirectToPageAccessDeniedHandler() {
        this(new AccessDeniedHandlerImpl());
    }

    /**
     * @param delegate handles every denial that is not a missing CSRF token on an htmx request
     */
    public HxRedirectToPageAccessDeniedHandler(AccessDeniedHandler delegate) {
        Assert.notNull(delegate, "delegate cannot be null");
        this.delegate = delegate;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        if (accessDeniedException instanceof MissingCsrfTokenException && HxRedirectToPage.isHtmxRequest(request)) {
            HxRedirectToPage.send(request, response, HttpStatus.FORBIDDEN);
        } else {
            delegate.handle(request, response, accessDeniedException);
        }
    }
}
