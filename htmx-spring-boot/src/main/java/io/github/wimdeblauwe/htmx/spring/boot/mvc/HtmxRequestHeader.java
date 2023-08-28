package io.github.wimdeblauwe.htmx.spring.boot.mvc;

/**
 * Enum representing the request headers used by htmx.
 *
 * @see <a href="https://htmx.org/reference/#request_headers">Request Headers Reference</a>
 */
public enum HtmxRequestHeader {
    /**
     * Indicates that the request comes from an element that uses hx-boost.
     *
     * @see <a href="https://htmx.org/reference/#request_headers">HX-Boosted</a>
     */
    HX_BOOSTED("HX-Boosted"),
    /**
     * The current URL of the browser
     *
     * @see <a href="https://htmx.org/reference/#request_headers">HX-Current-URL</a>
     */
    HX_CURRENT_URL("HX-Current-URL"),
    /**
     * Indicates if the request is for history restoration after a miss in the local history cache.
     *
     * @see <a href="https://htmx.org/reference/#request_headers">HX-History-Restore-Request</a>
     */
    HX_HISTORY_RESTORE_REQUEST("HX-History-Restore-Request"),
    /**
     * Contains the user response to a <a href="https://htmx.org/attributes/hx-prompt/">hx-prompt</a>.
     *
     * @see <a href="https://htmx.org/reference/#request_headers">HX-Prompt</a>
     */
    HX_PROMPT("HX-Prompt"),
    /**
     * Only present and {@code true} if the request is issued by htmx.
     *
     * @see <a href="https://htmx.org/reference/#request_headers">HX-Request</a>
     */
    HX_REQUEST("HX-Request"),
    /**
     * The {@code id} of the target element if it exists.
     *
     * @see <a href="https://htmx.org/reference/#request_headers">HX-Target</a>
     */
    HX_TARGET("HX-Target"),
    /**
     * The {@code name} of the triggered element if it exists
     *
     * @see <a href="https://htmx.org/reference/#request_headers">HX-Trigger-Name</a>
     */
    HX_TRIGGER_NAME("HX-Trigger-Name"),
    /**
     * The {@code id} of the triggered element if it exists.
     *
     * @see <a href="https://htmx.org/reference/#request_headers">HX-Trigger</a>
     */
    HX_TRIGGER("HX-Trigger");

    private final String value;

    HtmxRequestHeader(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
