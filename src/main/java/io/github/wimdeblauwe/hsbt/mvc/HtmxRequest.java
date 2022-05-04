package io.github.wimdeblauwe.hsbt.mvc;

import org.springframework.lang.Nullable;

public final class HtmxRequest {
    private final boolean htmxRequest;
    private final boolean boosted;
    private final String currentUrl;
    private final boolean historyRestoreRequest;
    private final String promptResponse;
    private final String target;

    HtmxRequest(boolean htmxRequest, boolean boosted, String currentUrl, boolean historyRestoreRequest, String promptResponse, String target) {
        this.htmxRequest = htmxRequest;
        this.boosted = boosted;
        this.currentUrl = currentUrl;
        this.historyRestoreRequest = historyRestoreRequest;
        this.promptResponse = promptResponse;
        this.target = target;
    }

    public boolean isHtmxRequest() {
        return htmxRequest;
    }

    /**
     * Indicates that the request is via an element using hx-boost.
     *
     * @return true if the request was made via hx-boost, false otherwise
     */
    public boolean isBoosted() {
        return boosted;
    }

    /**
     * The current URL of the browser when the htmx request was made.
     *
     * @return the URL, or null if the URL was not passed
     */
    @Nullable
    public String getCurrentUrl() {
        return currentUrl;
    }

    /**
     * Indicates if the request is for history restoration after a miss in the local history cache
     *
     * @return true if this request is for history restoration, false otherwise
     */
    public boolean isHistoryRestoreRequest() {
        return historyRestoreRequest;
    }

    /**
     * The user response to an hx-prompt.
     *
     * @return The response of the user. Can be null.
     */
    @Nullable
    public String getPromptResponse() {
        return promptResponse;
    }

    /**
     * The id of the target element if it exists.
     *
     * @return the id, or null if no id was passed in the request
     */
    @Nullable
    public String getTarget() {
        return target;
    }

    public static final class Builder {
        private final boolean htmxRequest;
        private boolean boosted;
        private String currentUrl;
        private boolean historyRestoreRequest;
        private String promptResponse;
        private String target;

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

        public Builder withTarget(String target) {
            this.target = target;
            return this;
        }

        public HtmxRequest build() {
            return new HtmxRequest(htmxRequest, boosted, currentUrl, historyRestoreRequest, promptResponse, target);
        }
    }
}
