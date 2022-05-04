package io.github.wimdeblauwe.hsbt.mvc;

public final class HtmxRequest {
    private final boolean htmxRequest;

    public HtmxRequest(boolean htmxRequest) {
        this.htmxRequest = htmxRequest;
    }

    public boolean isHtmxRequest() {
        return htmxRequest;
    }
}
