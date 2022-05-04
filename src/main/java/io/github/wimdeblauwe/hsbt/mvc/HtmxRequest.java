package io.github.wimdeblauwe.hsbt.mvc;

public final class HtmxRequest {
    private final boolean htmxRequest;
    private final boolean boosted;

    HtmxRequest(boolean htmxRequest, boolean boosted) {
        this.htmxRequest = htmxRequest;
        this.boosted = boosted;
    }

    public boolean isHtmxRequest() {
        return htmxRequest;
    }

    public boolean isBoosted() {
        return boosted;
    }

    public static final class Builder {
        private final boolean htmxRequest;
        private boolean boosted;

        public Builder(boolean htmxRequest) {
            this.htmxRequest = htmxRequest;
        }

        public Builder withBoosted(boolean boosted) {
            this.boosted = boosted;
            return this;
        }

        public HtmxRequest build() {
            return new HtmxRequest(htmxRequest, boosted);
        }
    }
}
