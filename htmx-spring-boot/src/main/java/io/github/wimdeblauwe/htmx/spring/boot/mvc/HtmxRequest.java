package io.github.wimdeblauwe.htmx.spring.boot.mvc;

import org.springframework.lang.Nullable;

/**
 * This class can be used as a controller method argument to access
 * the <a href="https://htmx.org/reference/#request_headers">htmx Request Headers</a>.
 * <p/>
 * <pre>
 * {@code
 * @GetMapping("/users")
 * @HxRequest
 * public String users(HtmxRequest htmxRequest) {
 *     if (htmxRequest.isBoosted()) {
 *         ...
 *     }
 * }
 * }
 * </pre>
 *
 * @see <a href="https://htmx.org/reference/#request_headers">Request Headers Reference</a>
 */
public final class HtmxRequest {
    private final boolean htmxRequest;
    private final boolean boosted;
    private final String currentUrl;
    private final boolean historyRestoreRequest;
    private final String promptResponse;
    private final String target;
    private final String triggerName;
    private final String triggerId;

    HtmxRequest(boolean htmxRequest, boolean boosted, String currentUrl, boolean historyRestoreRequest, String promptResponse, String target, String triggerName, String triggerId) {
        this.htmxRequest = htmxRequest;
        this.boosted = boosted;
        this.currentUrl = currentUrl;
        this.historyRestoreRequest = historyRestoreRequest;
        this.promptResponse = promptResponse;
        this.target = target;
        this.triggerName = triggerName;
        this.triggerId = triggerId;
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

    /**
     * The name of the triggered element if it exists.
     *
     * @return the name of the trigger, or null if no name was passed in the request
     */
    @Nullable
    public String getTriggerName() {
        return triggerName;
    }

    /**
     * The id of the triggered element if it exists.
     *
     * @return the id of the trigger, or null if no name was passed in the request
     */
    @Nullable
    public String getTriggerId() {
        return triggerId;
    }

    public static final class Builder {
        private final boolean htmxRequest;
        private boolean boosted;
        private String currentUrl;
        private boolean historyRestoreRequest;
        private String promptResponse;
        private String target;
        private String triggerName;
        private String triggerId;

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

        public Builder withTriggerName(String triggerName) {
            this.triggerName = triggerName;
            return this;
        }

        public Builder withTriggerId(String triggerId) {
            this.triggerId = triggerId;
            return this;
        }

        public HtmxRequest build() {
            return new HtmxRequest(htmxRequest, boosted, currentUrl, historyRestoreRequest, promptResponse, target, triggerName, triggerId);
        }
    }
}
