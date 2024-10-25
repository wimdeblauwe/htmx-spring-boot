package io.github.wimdeblauwe.htmx.spring.boot.mvc;

import jakarta.servlet.http.HttpServletRequest;

final class RequestContextUtils {

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
