package io.github.wimdeblauwe.hsbt.mvc;

import org.springframework.lang.Nullable;

public final class HtmxRequest {
    private final boolean htmxRequest;
    private final boolean boosted;
    private final String currentUrl;
    private final boolean historyRestoreRequest;
    private final String promptResponse;

    HtmxRequest(boolean htmxRequest, boolean boosted, String currentUrl, boolean historyRestoreRequest, String promptResponse) {
        this.htmxRequest = htmxRequest;
        this.boosted = boosted;
        this.currentUrl = currentUrl;
        this.historyRestoreRequest = historyRestoreRequest;
        this.promptResponse = promptResponse;
    }

    public boolean isHtmxRequest() {
        return htmxRequest;
    }

    public boolean isBoosted() {
        return boosted;
    }

    @Nullable
    public String getCurrentUrl() {
        return currentUrl;
    }

    public boolean isHistoryRestoreRequest() {
        return historyRestoreRequest;
    }

    @Nullable
    public String getPromptResponse() {
        return promptResponse;
    }

    public static final class Builder {
        private final boolean htmxRequest;
        private boolean boosted;
        private String currentUrl;
        private boolean historyRestoreRequest;
        private String promptResponse;

        public Builder(boolean htmxRequest) {
            this.htmxRequest = htmxRequest;
        }

        public Builder withBoosted(boolean boosted) {
            this.boosted = boosted;
            return this;
        }

        public Builder withCurrentUrl(String currentUrl) {
            this.currentUrl = currentUrl;
            return this;
        }

        public Builder withHistoryRestoreRequest(boolean historyRestoreRequest) {
            this.historyRestoreRequest = historyRestoreRequest;
            return this;
        }

        public Builder withPromptResponse(String promptResponse) {
            this.promptResponse = promptResponse;
            return this;
        }

        public HtmxRequest build() {
            return new HtmxRequest(htmxRequest, boosted, currentUrl, historyRestoreRequest, promptResponse);
        }
    }
}
