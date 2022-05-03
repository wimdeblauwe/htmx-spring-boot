package io.github.wimdeblauwe.hsbt.mvc;

public final class HtmxRequestDetails {
    private final boolean htmxRequest;

    public HtmxRequestDetails(boolean htmxRequest) {
        this.htmxRequest = htmxRequest;
    }

    public boolean isHtmxRequest() {
        return htmxRequest;
    }
}
