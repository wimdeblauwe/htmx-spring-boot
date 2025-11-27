package io.github.wimdeblauwe.htmx.spring.boot.security;

import io.github.wimdeblauwe.htmx.spring.boot.mvc.HtmxResponseHeader;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;

import java.io.IOException;

import static io.github.wimdeblauwe.htmx.spring.boot.mvc.HtmxRequestHeader.HX_REQUEST;
import static io.github.wimdeblauwe.htmx.spring.boot.mvc.HtmxResponseHeader.HX_LOCATION;
import static org.springframework.http.HttpStatus.OK;

/**
 * htmx-friendly redirect strategy to be used with any security handler that performs redirects.
 * <p>
 * When instantiated by the default constructor, it checks for htmx requests and responds with {@link HttpStatus#OK},
 * including the target URL in the {@link HtmxResponseHeader#HX_LOCATION} header.
 * <p>
 * For non-htmx requests, it delegates to the {@link DefaultRedirectStrategy}.
 *
 * @author LC Nicolau
 * @see <a href="https://htmx.org/headers/hx-location/">HX-Location Response Header</a>
 * @see <a href="https://htmx.org/reference/#headers">HTTP Header Reference</a>
 * @since 5.0.0
 */
public class HxLocationRedirectStrategy implements RedirectStrategy {

    private final HttpStatus status;
    private final RedirectStrategy delegate;

    public HxLocationRedirectStrategy() {
        this(OK);
    }

    public HxLocationRedirectStrategy(HttpStatus status) {
        this.status = status;
        this.delegate = new DefaultRedirectStrategy();
    }

    @Override
    public void sendRedirect(HttpServletRequest request, HttpServletResponse response, String url) throws IOException {
        if (request.getHeader(HX_REQUEST.getValue()) == null) {
            delegate.sendRedirect(request, response, url);
        } else {
            this.sendHxLocationRedirect(request, response, url);
        }
    }

    protected void sendHxLocationRedirect(HttpServletRequest request, HttpServletResponse response, String url) throws IOException {
        response.setHeader(HX_LOCATION.getValue(), url);
        response.setStatus(status.value());
        response.getWriter().flush();
    }

}
