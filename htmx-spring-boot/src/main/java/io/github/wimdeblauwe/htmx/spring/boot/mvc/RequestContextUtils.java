package io.github.wimdeblauwe.htmx.spring.boot.mvc;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Utility class for working with the request context.
 *
 * @since 3.6.0
 */
final class RequestContextUtils {

    public static final String HTMX_RESPONSE_CONTEXT_ATTRIBUTE = "htmxResponse";

    /**
     * Creates a URL by prepending the context path if {@code contextRelative}
     * is {@code true} and the URL starts with a slash ("/").
     *
     * @param request         the request to use to obtain the context path
     * @param url             the URL
     * @param contextRelative whether to prepend the context path
     * @return the target URL
     */
    static String createUrl(HttpServletRequest request, String url, boolean contextRelative) {
        if (contextRelative && url.startsWith("/")) {
            return getContextPath(request) + url;
        }
        return url;
    }

    static HtmxResponse getHtmxResponse(HttpServletRequest request) {

        Object contextAttribute = request.getAttribute(HTMX_RESPONSE_CONTEXT_ATTRIBUTE);
        if (contextAttribute instanceof HtmxResponse response) {
            return response;
        } else if (contextAttribute instanceof HtmxResponse.Builder builder) {
            return builder.build();
        }
        return null;
    }

    static HtmxResponse.Builder getHtmxResponseBuilder(HttpServletRequest request) {
        return (HtmxResponse.Builder) request.getAttribute(HTMX_RESPONSE_CONTEXT_ATTRIBUTE);
    }

    private static String getContextPath(HttpServletRequest request) {
        String contextPath = request.getContextPath();
        while (contextPath.startsWith("//")) {
            contextPath = contextPath.substring(1);
        }
        return contextPath;
    }

    private RequestContextUtils() {
    }

}
