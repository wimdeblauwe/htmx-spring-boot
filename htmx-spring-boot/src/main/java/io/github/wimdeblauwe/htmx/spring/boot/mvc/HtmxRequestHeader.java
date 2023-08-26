package io.github.wimdeblauwe.htmx.spring.boot.mvc;

public enum HtmxRequestHeader {
    HX_BOOSTED("HX-Boosted"),
    HX_CURRENT_URL("HX-Current-URL"),
    HX_HISTORY_RESTORE_REQUEST("HX-History-Restore-Request"),
    HX_PROMPT("HX-Prompt"),
    HX_REQUEST("HX-Request"),
    HX_TARGET("HX-Target"),
    HX_TRIGGER_NAME("HX-Trigger-Name"),
    HX_TRIGGER("HX-Trigger");

    private final String value;

    HtmxRequestHeader(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
