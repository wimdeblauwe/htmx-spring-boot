package io.github.wimdeblauwe.htmx.spring.boot.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.util.Assert;

import java.io.IOException;

/**
 * Answers an htmx request that needs authentication (for example after the session expired) with
 * {@link HttpStatus#UNAUTHORIZED} and {@code HX-Redirect} to the page the request was made from, instead of the login
 * page swapped into the request's target.
 * <p>
 * The browser then loads that page as a normal navigation, so the application's own entry point takes over (a login
 * form, an OAuth2 provider) and saves the page as the request to return to after signing in. The htmx request itself,
 * which {@code ExceptionTranslationFilter} saved before calling this entry point, is removed from the
 * {@link RequestCache}: its URL may be a fragment endpoint.
 * <p>
 * The page is the request's own URL for a boosted GET (the link that was clicked), otherwise the path and query of
 * {@code HX-Current-URL}. Without one, {@code HX-Refresh} reloads the page.
 * <p>
 * Give it the entry point your application uses for everything else, and set it as the entry point:
 * <pre> {@code
 * http.exceptionHandling(exceptions -> exceptions
 *         .authenticationEntryPoint(new HxRedirectToPageAuthenticationEntryPoint(
 *                 new LoginUrlAuthenticationEntryPoint("/login"))));
 * } </pre>
 * <p>
 * Registering it with {@code defaultAuthenticationEntryPointFor(entryPoint, htmxRequestMatcher)} instead works for
 * htmx requests, but Spring Security makes the first entry point registered that way the default for requests no
 * other entry point claims, and that registration runs before the login form's. Requests that are no htmx requests
 * and do not ask for HTML (a {@code fetch} without an {@code Accept} header, {@code curl}, a MockMvc test) then reach
 * this entry point and get its {@code nonHtmxEntryPoint}: a plain {@link HttpStatus#UNAUTHORIZED} with the no-argument
 * constructor, where they used to be redirected to the login page.
 * <p>
 * This class is not used by the library itself, but users of the library can use it to configure their security.
 *
 * @see HxRedirectToPageAccessDeniedHandler
 * @since 5.2.0
 */
public class HxRedirectToPageAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final AuthenticationEntryPoint nonHtmxEntryPoint;
    private final RequestCache requestCache;

    public HxRedirectToPageAuthenticationEntryPoint() {
        this(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED));
    }

    /**
     * @param nonHtmxEntryPoint answers the requests that reach this entry point but are no htmx requests
     */
    public HxRedirectToPageAuthenticationEntryPoint(AuthenticationEntryPoint nonHtmxEntryPoint) {
        this(nonHtmxEntryPoint, new HttpSessionRequestCache());
    }

    /**
     * @param nonHtmxEntryPoint answers the requests that reach this entry point but are no htmx requests
     * @param requestCache      the request cache the application configured, if it is not an
     *                          {@link HttpSessionRequestCache}
     */
    public HxRedirectToPageAuthenticationEntryPoint(AuthenticationEntryPoint nonHtmxEntryPoint, RequestCache requestCache) {
        Assert.notNull(nonHtmxEntryPoint, "nonHtmxEntryPoint cannot be null");
        Assert.notNull(requestCache, "requestCache cannot be null");
        this.nonHtmxEntryPoint = nonHtmxEntryPoint;
        this.requestCache = requestCache;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        if (!HxRedirectToPage.isHtmxRequest(request)) {
            nonHtmxEntryPoint.commence(request, response, authException);
            return;
        }
        requestCache.removeRequest(request, response);
        HxRedirectToPage.send(request, response, HttpStatus.UNAUTHORIZED);
    }
}
