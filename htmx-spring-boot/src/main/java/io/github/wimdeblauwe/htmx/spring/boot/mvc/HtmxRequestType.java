package io.github.wimdeblauwe.htmx.spring.boot.mvc;

enum HtmxRequestType {
    /**
     * Used when the <a href="https://four.htmx.org/docs/core-concepts/hypermedia-controls">target</a> is the document body,
     * or when selecting elements from the response (with <a href="https://four.htmx.org/reference/attributes/hx-select">hx-select</a>).
     */
    FULL,
    /**
     * Used when the target is any other element.
     */
    PARTIAL;

    /**
     * Return the {@link HtmxRequestType} for the given string.
     *
     * @param requestType the string representation of the request type
     * @return the type or {@code null} if the given string is {@code null}
     */
    public static HtmxRequestType of(String requestType) {

        if (requestType == null) {
            return null;
        }
        return valueOf(requestType.toUpperCase());
    }
}
