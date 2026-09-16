package io.github.wimdeblauwe.htmx.spring.boot.mvc;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.lang.Nullable;

import static io.github.wimdeblauwe.htmx.spring.boot.mvc.HtmxRequestHeader.*;

/**
 * This class can be used as a controller method argument to access
 * the <a href="https://four.htmx.org/reference/headers#request">htmx Request Headers</a>.
 *
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
 * @see <a href="https://four.htmx.org/reference/headers#request">Request Headers Reference</a>
 */
public final class HtmxRequest {

    private final boolean htmxRequest;
    private final boolean boosted;
    private final String currentUrl;
    private final boolean historyRestoreRequest;
    private final String promptResponse;
    private final String target;
    private final HtmxRequestType requestType;
    private final String source;

    /**
     * Return a {@link Builder} to create a {@link HtmxRequest}.
     *
     * @return the builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Create an empty {@link HtmxRequest}.
     *
     * @return the empty HtmxRequest
     */
    public static HtmxRequest empty() {
        return new HtmxRequest(false, false, null, false, null, null, null, null);
    }

    /**
     * Create a new {@link HtmxRequest} from the given {@link HttpServletRequest}.
     *
     * @param request the request to create the HtmxRequest from
     * @return the HtmxRequest
     * @since 3.6.0
     */
    public static HtmxRequest fromRequest(HttpServletRequest request) {

        String hxRequestHeader = request.getHeader(HX_REQUEST.getValue());
        if (hxRequestHeader == null) {
            return empty();
        }

        HtmxRequest.Builder builder = builder();
        if (request.getHeader(HX_BOOSTED.getValue()) != null) {
            builder.boosted(true);
        }
        if (request.getHeader(HX_CURRENT_URL.getValue()) != null) {
            builder.currentUrl(request.getHeader(HX_CURRENT_URL.getValue()));
        }
        if (request.getHeader(HX_HISTORY_RESTORE_REQUEST.getValue()) != null) {
            builder.historyRestoreRequest(true);
        }
        if (request.getHeader(HX_PROMPT.getValue()) != null) {
            builder.promptResponse(request.getHeader(HX_PROMPT.getValue()));
        }
        if (request.getHeader(HX_TARGET.getValue()) != null) {
            builder.target(request.getHeader(HX_TARGET.getValue()));
        }
        if (request.getHeader(HX_REQUEST_TYPE.getValue()) != null) {
            builder.requestType(request.getHeader(HX_REQUEST_TYPE.getValue()));
        }
        if (request.getHeader(HX_SOURCE.getValue()) != null) {
            builder.source(request.getHeader(HX_SOURCE.getValue()));
        }

        return builder.build();
    }

    HtmxRequest(boolean htmxRequest, boolean boosted, String currentUrl, boolean historyRestoreRequest, String promptResponse,
                String target, HtmxRequestType requestType, String source) {

        this.htmxRequest = htmxRequest;
        this.boosted = boosted;
        this.currentUrl = currentUrl;
        this.historyRestoreRequest = historyRestoreRequest;
        this.promptResponse = promptResponse;
        this.target = target;
        this.requestType = requestType;
        this.source = source;
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
     * Returns {@code true} if the request targets a specific element or {@code false} the whole page.
     *
     * @return if the request targets a specific element or the whole page
     * @throws NullPointerException if the request type is not available, which means that the request was made with an htmx version older than 4.x
     * @since 6.0.0
     */
    public boolean isPartialRequest() {

        if (requestType == null) {
            throw new NullPointerException("Unable to determine if the request is partial because 'HX-Request-Type' header is missing.");
        }
        return requestType == HtmxRequestType.PARTIAL;
    }

    /**
     * The user response to a prompt.
     *
     * @return The response of the user. Can be null.
     * @see <a href="https://four.htmx.org/extensions/hx-prompt">hx-prompt extension</a>
     */
    @Nullable
    public String getPromptResponse() {
        return promptResponse;
    }

    /**
     * The element or id that will receive the response.
     * <p>
     * Format is {@code tag#id} like {@code button#submit}
     *
     * @return the element that will receive the response, or null if it was not passed in {@code HX-Target} header.
     * @see <a href="https://four.htmx.org/reference/headers/HX-Target">HX-Target</a>
     */
    @Nullable
    public String getTarget() {
        return target;
    }

    /**
     * Returns the element name e.g. {@code div} that will receive the response.
     *
     * @return the name of the element, or null if it was not passed in {@code HX-Target} header.
     * @throws NullPointerException if the {@link #target} is not available, which means that the request was made with an htmx version older than 4.x
     * @see <a href="https://four.htmx.org/reference/headers/HX-Target">HX-Target</a>
     */
    @Nullable
    public String getTargetElementName() {

        if (target == null) {
            throw new NullPointerException("Unable to determine the target element name because 'HX-Target' header is missing.");
        }

        int index = target.indexOf("#");
        if (index > -1) {
            return target.substring(0, index);
        }

        return target;
    }

    /**
     * Return the ID of the element that will receive the response.
     *
     * @return the ID of the element, or null if it was not passed in {@code HX-Target} header.
     * @throws NullPointerException if the {@link #target} is not available, which means that the request was made with an htmx version older than 4.x
     * @see <a href="https://four.htmx.org/reference/headers/HX-Target">HX-Target</a>
     */
    @Nullable
    public String getTargetElementId() {

        if (target == null) {
            throw new NullPointerException("Unable to determine the target element id because 'HX-Target' header is missing.");
        }

        int index = target.indexOf("#");
        if (index > -1) {
            return target.substring(index + 1);
        }

        return null;
    }

    /**
     * The element that triggered the request.
     * <p>
     * Format is {@code tag#id} like {@code button#submit}
     *
     * @return the element that triggered the request, or null if it was not passed in {@code HX-Target} header.
     * @see <a href="https://four.htmx.org/reference/headers/HX-Source">HX-Source</a>
     */
    @Nullable
    public String getSource() {
        return source;
    }

    /**
     * The element name e.g. {@code div} that triggered the request.
     *
     * @return the name of the element, or null if it was not passed in {@code HX-Source} header.
     * @throws NullPointerException if the {@link #source} is not available, which means that the request was made with an htmx version older than 4.x
     * @see <a href="https://four.htmx.org/reference/headers/HX-Source">HX-Source</a>
     */
    public String getSourceElementName() {

        if (source == null) {
            throw new NullPointerException("Unable to determine the source element name because 'HX-Source' header is missing.");
        }

        int index = source.indexOf("#");
        if (index > -1) {
            return source.substring(0, index);
        }

        return source;
    }

    /**
     * The ID of the element that triggered the request.
     *
     * @return the ID of the element, or null if it was not passed in {@code HX-Source} header.
     * @throws NullPointerException if the {@link #source} is not available, which means that the request was made with an htmx version older than 4.x
     * @see <a href="https://four.htmx.org/reference/headers/HX-Source">HX-Source</a>
     */
    public String getSourceElementId() {

        if (source == null) {
            throw new NullPointerException("Unable to determine the source element id because 'HX-Source' header is missing.");
        }

        int index = source.indexOf("#");
        if (index > -1) {
            return source.substring(index + 1);
        }

        return null;
    }

    public static final class Builder {

        private boolean boosted;
        private String currentUrl;
        private boolean historyRestoreRequest;
        private String promptResponse;
        private HtmxRequestType requestType;
        private String source;
        private String target;
        private String triggerName;
        private String triggerId;

        private Builder() {
        }

        public Builder boosted(boolean boosted) {
            this.boosted = boosted;
            return this;
        }

        public Builder currentUrl(String currentUrl) {
            this.currentUrl = currentUrl;
            return this;
        }

        public Builder historyRestoreRequest(boolean historyRestoreRequest) {
            this.historyRestoreRequest = historyRestoreRequest;
            return this;
        }

        public Builder promptResponse(String promptResponse) {
            this.promptResponse = promptResponse;
            return this;
        }

        public Builder requestType(String requestType) {
            this.requestType = HtmxRequestType.of(requestType);
            return this;
        }

        public Builder source(String source) {
            this.source = source;
            return this;
        }

        public Builder target(String target) {
            this.target = target;
            return this;
        }

        public Builder triggerName(String triggerName) {
            this.triggerName = triggerName;
            return this;
        }

        public Builder triggerId(String triggerId) {
            this.triggerId = triggerId;
            return this;
        }

        public HtmxRequest build() {
            return new HtmxRequest(true, boosted, currentUrl, historyRestoreRequest, promptResponse, target, requestType, source);
        }
    }
}
