package io.github.wimdeblauwe.htmx.spring.boot.mvc;

public enum HxTriggerLifecycle {
    /**
     * Maps to HX-Trigger
     */
    RECEIVE("HX-Trigger"),
    /**
     * Maps to HX-Trigger-After-Settle
     */
    SETTLE("HX-Trigger-After-Settle"),
    /**
     * Maps to HX-Trigger-After-Swap
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
