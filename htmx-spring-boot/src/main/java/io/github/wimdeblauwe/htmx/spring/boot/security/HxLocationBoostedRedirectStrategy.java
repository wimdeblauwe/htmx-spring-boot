package io.github.wimdeblauwe.htmx.spring.boot.security;

import io.github.wimdeblauwe.htmx.spring.boot.mvc.HtmxLocation;
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HtmxResponseHeader;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.DefaultRedirectStrategy;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.util.Map;

import static io.github.wimdeblauwe.htmx.spring.boot.mvc.HtmxRequestHeader.HX_BOOSTED;
import static org.springframework.http.HttpStatus.OK;

/**
 * htmx-friendly redirect strategy to be used with any security handler that performs redirects.
 * <p>
 * When instantiated by the default constructor, it checks for htmx requests and responds with {@link HttpStatus#OK},
 * including the target URL in the {@link HtmxResponseHeader#HX_LOCATION} header.
 * <p>
 * It also sets the {@code headers} and {@code target} parameters, instructing the client to include the
 * {@code HX-Boosted} header in the new request, and to swap the response into the {@code body} element.
 * <p>
 * Example:
 * <pre> {@code
 * HX-Location: {
 *      "path":"/login?unauthorized",
 *      "headers":{"HX-Boosted":"true"},
 *      "target":"body"
 * }
 * } </pre>
 * <p>
 * These parameters are useful if you want to take advantage of existing controller optimizations, to render a fragment
 * instead of the full page for non-boosted, htmx-driven requests:
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
 * In case you don’t need this "boosted" behavior, use the {@link HxLocationRedirectStrategy} instead.
 * <p>
 * For non-htmx requests, it delegates to the {@link DefaultRedirectStrategy}.
 *
 * @author LC Nicolau
 * @see <a href="https://htmx.org/headers/hx-location/">HX-Location Response Header</a>
 * @see <a href="https://htmx.org/reference/#headers">HTTP Header Reference</a>
 * @since 5.0.0
 */
public class HxLocationBoostedRedirectStrategy extends HxLocationRedirectStrategy {

    private final JsonMapper jsonMapper;

    public HxLocationBoostedRedirectStrategy() {
        this(OK);
    }

    public HxLocationBoostedRedirectStrategy(HttpStatus status) {
        super(status);
        this.jsonMapper = new JsonMapper();
    }

    @Override
    protected void sendHxLocationRedirect(HttpServletRequest request, HttpServletResponse response, String url) throws IOException {
        super.sendHxLocationRedirect(request, response, boosted(url));
    }

    protected String boosted(String url) {
        HtmxLocation location = new HtmxLocation(url);
        location.setTarget("body");
        location.setHeaders(Map.of(HX_BOOSTED.getValue(), "true"));
        return jsonMapper.writeValueAsString(location);
    }

}
