package io.github.wimdeblauwe.htmx.spring.boot.mvc;

public enum HtmxResponseHeader {
    HX_PUSH("HX-Push"),
    HX_REDIRECT("HX-Redirect"),
    HX_REFRESH("HX-Refresh"),
    HX_RETARGET("HX-Retarget"),
    HX_TRIGGER("HX-Trigger"),
    HX_TRIGGER_AFTER_SETTLE("HX-Trigger-After-Settle"),
    HX_TRIGGER_AFTER_SWAP("HX-Trigger-After-Swap"),
    HX_RESWAP("HX-Reswap");

    private final String value;

    HtmxResponseHeader(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
