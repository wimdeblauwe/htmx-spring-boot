package io.github.wimdeblauwe.htmx.spring.boot.mvc;

/**
 * Enum representing the response headers used by htmx.
 *
 * @see <a href="https://htmx.org/reference/#response_headers">Response Headers Reference</a>
 */
public enum HtmxResponseHeader {
    /**
     * Allows you to do a client-side redirect that does not do a full page reload.
     *
     * @see <a href="https://htmx.org/headers/hx-location/">HX-Location</a>
     */
    HX_LOCATION("HX-Location"),
    /**
     * Allows you to replace the current URL in the location bar.
     *
     * @see <a href="https://htmx.org/headers/hx-replace-url/">HX-Replace-Url</a>
     */
    HX_REPLACE_URL("HX-Replace-Url"),
    /**
     * Pushes a new url into the history stack.
     *
     * @see <a href="https://htmx.org/headers/hx-push-url">HX-Push</a>
     */
    HX_PUSH_URL("HX-Push-Url"),
    /**
     * Can be used to do a client-side redirect to a new location.
     *
     * @see <a href="https://htmx.org/reference/#response_headers">HX-Redirect</a>
     */
    HX_REDIRECT("HX-Redirect"),
    /**
     * Can be used to do a full refresh of the page on the client-side.
     *
     * @see <a href="https://htmx.org/reference/#response_headers">HX-Refresh</a>
     */
    HX_REFRESH("HX-Refresh"),
    /**
     * A CSS selector that updates the target of the content update to a different element on the page.
     *
     * @see <a href="https://htmx.org/reference/#response_headers">HX-Retarget</a>
     */
    HX_RETARGET("HX-Retarget"),
    /**
     * A CSS selector that allows you to choose which part of the response is used to be swapped in.
     *
     * @see <a href="https://htmx.org/reference/#response_headers">HX-Reselect</a>
     */
    HX_RESELECT("HX-Reselect"),
    /**
     * Can be used to trigger client side events.
     *
     * @see <a href="https://htmx.org/headers/hx-trigger/">HX-Trigger</a>
     */
    HX_TRIGGER("HX-Trigger"),
    /**
     * Can be used to trigger client side events after the <a href="https://htmx.org/docs/#request-operations">settling step</a>.
     *
     * @see <a href="https://htmx.org/headers/hx-trigger/">HX-Trigger-After-Settle</a>
     */
    HX_TRIGGER_AFTER_SETTLE("HX-Trigger-After-Settle"),
    /**
     * Can be used to trigger client side events after the <a href="https://htmx.org/docs/#request-operations">swap step</a>.
     *
     * @see <a href="https://htmx.org/headers/hx-trigger/">HX-Trigger-After-Settle</a>
     */
    HX_TRIGGER_AFTER_SWAP("HX-Trigger-After-Swap"),
    /**
     * Allows you to specify how the response will be swapped.
     *
     * @see <a href="https://htmx.org/reference/#response_headers">HX-Reswap</a>
     */
    HX_RESWAP("HX-Reswap");

    private final String value;

    HtmxResponseHeader(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

