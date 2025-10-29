package io.github.wimdeblauwe.htmx.spring.boot.security;

import io.github.wimdeblauwe.htmx.spring.boot.mvc.HtmxLocation;
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HtmxResponseHeader;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.util.Map;

import static io.github.wimdeblauwe.htmx.spring.boot.mvc.HtmxRequestHeader.HX_BOOSTED;
import static io.github.wimdeblauwe.htmx.spring.boot.mvc.HtmxRequestHeader.HX_REQUEST;
import static io.github.wimdeblauwe.htmx.spring.boot.mvc.HtmxResponseHeader.HX_LOCATION;
import static org.springframework.http.HttpStatus.OK;


/**
 * HTMX-friendly redirect strategy to be used with any security handler that performs redirects.
 * <p>
 * When instantiated by the default constructor, it checks for HTMX requests and responds with {@link HttpStatus#OK},
 * including the target URL in the {@link HtmxResponseHeader#HX_LOCATION} header.
 * <p>
 * It also sets the {@code headers} and {@code target} parameters, instructing the client to include the
 * {@code HX-Boosted} header in the new request, and to swap the response into the {@code body} element.
 * <p>
 * Example:
 * <pre> {@code
 * hx-location: {
 *      "path":"/login?unauthorized",
 *      "headers":{"HX-Boosted":"true"},
 *      "target":"body"
 * }
 * } </pre>
 * <p>
 * These parameters are useful if you want to take advantage of existing controller optimizations, to render a fragment
 * instead of the full page for non-boosted, HTMX-driven requests:
 * <p>
 * <pre> {@code
 * @GetMapping("/login")
 * String login(HtmxRequest request) {
 *     return request.isHtmxRequest() && !request.isBoosted()
 *             ? "pages/login :: content"
 *             : "pages/login";
 * }
 * } </pre>
 * <p>
 * You can override this behavior and change the response status code through the constructor.
 * <p>
 * For non-HTMX requests, it delegates to the {@link DefaultRedirectStrategy}.
 *
 * @author LC Nicolau
 * @see <a href="https://htmx.org/headers/hx-location/">HX-Location Response Header</a>
 * @see <a href="https://htmx.org/reference/#headers">HTTP Header Reference</a>
 */
public class HxLocationRedirectStrategy implements RedirectStrategy {

    private final boolean redirectAsBoosted;
    private final HttpStatus status;

    private final RedirectStrategy delegate;
    private final JsonMapper jsonMapper;

    public HxLocationRedirectStrategy() {
        this(true, OK);
    }

    public HxLocationRedirectStrategy(boolean redirectAsBoosted) {
        this(redirectAsBoosted, OK);
    }

    public HxLocationRedirectStrategy(HttpStatus status) {
        this(true, status);
    }

    public HxLocationRedirectStrategy(boolean redirectAsBoosted, HttpStatus status) {
        this.redirectAsBoosted = redirectAsBoosted;
        this.status = status;
        this.delegate = new DefaultRedirectStrategy();
        this.jsonMapper = new JsonMapper();
    }


    @Override
    public void sendRedirect(HttpServletRequest request, HttpServletResponse response, String url) throws IOException {
        if (request.getHeader(HX_REQUEST.getValue()) == null) {
            delegate.sendRedirect(request, response, url);
        } else {
            this.sendLocationRedirect(request, response, url);
        }
    }

    protected void sendLocationRedirect(HttpServletRequest request, HttpServletResponse response, String url) throws IOException {
        var location = redirectAsBoosted ? boosted(url) : url;
        response.setHeader(HX_LOCATION.getValue(), location);
        response.setStatus(status.value());
        response.getWriter().flush();
    }

    protected String boosted(String url) {
        HtmxLocation location = new HtmxLocation(url);
        location.setTarget("body");
        location.setHeaders(Map.of(HX_BOOSTED.getValue(), "true"));
        return jsonMapper.writeValueAsString(location);
    }

}
