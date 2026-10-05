package io.github.wimdeblauwe.htmx.spring.boot.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.net.URI;

import static io.github.wimdeblauwe.htmx.spring.boot.mvc.HtmxRequestHeader.HX_BOOSTED;
import static io.github.wimdeblauwe.htmx.spring.boot.mvc.HtmxRequestHeader.HX_CURRENT_URL;
import static io.github.wimdeblauwe.htmx.spring.boot.mvc.HtmxRequestHeader.HX_REQUEST;
import static io.github.wimdeblauwe.htmx.spring.boot.mvc.HtmxResponseHeader.HX_REDIRECT;
import static io.github.wimdeblauwe.htmx.spring.boot.mvc.HtmxResponseHeader.HX_REFRESH;

/**
 * Sends an htmx request's browser to the page it was made from, as a normal navigation, with {@code HX-Redirect}.
 * <p>
 * The page is the request's own URL for a boosted GET (the link that was clicked), otherwise the path and query of
 * {@code HX-Current-URL}. Only the path and query are used, so the redirect never leaves the application's origin.
 * Without a usable page, it sends {@code HX-Refresh} instead.
 *
 * @since 5.2.0
 */
final class HxRedirectToPage {

    private HxRedirectToPage() {
    }

    static boolean isHtmxRequest(HttpServletRequest request) {
        return request.getHeader(HX_REQUEST.getValue()) != null;
    }

    static void send(HttpServletRequest request, HttpServletResponse response, HttpStatus status) throws IOException {
        String page = page(request);
        if (page != null) {
            response.setHeader(HX_REDIRECT.getValue(), page);
        } else {
            response.setHeader(HX_REFRESH.getValue(), "true");
        }
        response.setStatus(status.value());
        response.getWriter().flush();
    }

    static @Nullable String page(HttpServletRequest request) {
        if (request.getHeader(HX_BOOSTED.getValue()) != null && HttpMethod.GET.matches(request.getMethod())) {
            String query = request.getQueryString();
            return query != null ? request.getRequestURI() + "?" + query : request.getRequestURI();
        }
        return pathAndQuery(request.getHeader(HX_CURRENT_URL.getValue()));
    }

    private static @Nullable String pathAndQuery(@Nullable String url) {
        if (url == null || url.isBlank()) {
            return null;
        }
        URI uri;
        try {
            uri = URI.create(url);
        } catch (IllegalArgumentException e) {
            return null;
        }
        String path = uri.getRawPath();
        // "//host" would be a protocol-relative URL: another origin.
        if (path == null || !path.startsWith("/") || path.startsWith("//")) {
            return null;
        }
        String query = uri.getRawQuery();
        return query != null ? path + "?" + query : path;
    }
}
