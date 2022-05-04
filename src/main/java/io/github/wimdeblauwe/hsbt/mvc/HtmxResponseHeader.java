package io.github.wimdeblauwe.hsbt.mvc;

public enum HtmxResponseHeader {
    HX_TRIGGER("HX-Trigger"),
    HX_TRIGGER_AFTER_SETTLE("HX-Trigger-After-Settle"),
    HX_TRIGGER_AFTER_SWAP("HX-Trigger-After-Swap");

    private final String value;

    HtmxResponseHeader(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
