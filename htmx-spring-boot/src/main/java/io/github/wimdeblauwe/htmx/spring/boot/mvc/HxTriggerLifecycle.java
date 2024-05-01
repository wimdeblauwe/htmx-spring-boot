package io.github.wimdeblauwe.htmx.spring.boot.mvc;

/**
 * Represents the HX-Trigger Response Headers.
 *
 * @see <a href="https://htmx.org/headers/hx-trigger/">HX-Trigger Response Headers</a>
 * @deprecated use annotation {@link HxTriggerAfterSettle} or {@link HxTriggerAfterSwap} instead.
 */
@Deprecated
public enum HxTriggerLifecycle {
    /**
     * Trigger events as soon as the response is received.
     *
     * @see <a href="https://htmx.org/headers/hx-trigger/">HX-Trigger</a>
     */
    RECEIVE("HX-Trigger"),
    /**
     * Trigger events after the <a href="https://htmx.org/docs/#request-operations">settling step</a>.
     *
     * @see <a href="https://htmx.org/headers/hx-trigger/">HX-Trigger-After-Settle</a>
     */
    SETTLE("HX-Trigger-After-Settle"),
    /**
     * Trigger events after the <a href="https://htmx.org/docs/#request-operations">swap step</a>.
     *
     * @see <a href="https://htmx.org/headers/hx-trigger/">HX-Trigger-After-Swap</a>
     */
    SWAP("HX-Trigger-After-Swap");

    private final String headerName;

    HxTriggerLifecycle(String headerName) {
        this.headerName = headerName;
    }

    public String getHeaderName() {
        return this.headerName;
    }
}
