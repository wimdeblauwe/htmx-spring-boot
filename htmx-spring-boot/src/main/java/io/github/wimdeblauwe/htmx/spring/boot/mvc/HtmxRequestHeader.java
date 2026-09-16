package io.github.wimdeblauwe.htmx.spring.boot.mvc;

/**
 * Enum representing the request headers used by htmx.
 *
 * @see <a href="https://four.htmx.org/reference/headers#request">Request Headers Reference</a>
 */
public enum HtmxRequestHeader {
    /**
     * Indicates that the request comes from an element that uses hx-boost.
     *
     * @see <a href="https://four.htmx.org/reference/headers/HX-Boosted">HX-Boosted</a>
     */
    HX_BOOSTED("HX-Boosted"),
    /**
     * The current URL of the browser
     *
     * @see <a href="https://four.htmx.org/reference/headers/HX-Current-URL">HX-Current-URL</a>
     */
    HX_CURRENT_URL("HX-Current-URL"),
    /**
     * Indicates if the request is for history restoration after a miss in the local history cache.
     *
     * @see <a href="https://four.htmx.org/reference/headers/HX-Boosted">HX-History-Restore-Request</a>
     */
    HX_HISTORY_RESTORE_REQUEST("HX-History-Restore-Request"),
    /**
     * Contains the user response to a <a href="https://four.htmx.org/reference/attributes/hx-prompt/">hx-prompt</a>.
     *
     * @see <a href="https://four.htmx.org/reference/headers/HX-Prompt">HX-Prompt</a>
     */
    HX_PROMPT("HX-Prompt"),
    /**
     * Only present and {@code true} if the request is issued by htmx.
     *
     * @see <a href="https://four.htmx.org/reference/headers/HX-Request">HX-Request</a>
     */
    HX_REQUEST("HX-Request"),
    /**
     * The {@code id} of the target element if it exists.
     *
     * @see <a href="https://four.htmx.org/reference/headers/HX-Target">HX-Target</a>
     */
    HX_TARGET("HX-Target"),
    /**
     * The {@code HX-Request-Type} request header indicates if request targets a specific element or the whole page.
     *
     * @see <a href="https://four.htmx.org/reference/headers/HX-Request-Type">HX-Request-Type</a>
     * @since 6.0.0
     */
    HX_REQUEST_TYPE("HX-Request-Type"),
    /**
     * The element that triggered the request.
     *
     * @see <a href="https://four.htmx.org/reference/headers/HX-Source">HX-Source</a>
     * @since 6.0.0
     */
    HX_SOURCE("HX-SOURCE");

    private final String value;

    HtmxRequestHeader(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
