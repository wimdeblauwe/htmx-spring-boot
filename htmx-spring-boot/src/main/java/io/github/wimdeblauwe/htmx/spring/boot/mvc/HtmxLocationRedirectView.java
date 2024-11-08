package io.github.wimdeblauwe.htmx.spring.boot.mvc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.util.Map;

/**
 * A specialization of {@link RedirectView} that can be used to signal htmx to perform a client-side redirect without reloading the page.
 * This View supports all the features of RedirectView e.g. exposing model attributes, flash attributes, etc.
 *
 * @see <a href="https://htmx.org/headers/hx-location/">HX-Location Response Header</a>
 * @since 3.6.0
 */
public class HtmxLocationRedirectView extends RedirectView {

    private final JsonMapper jsonMapper = new JsonMapper();

    private String source;
    private String event;
    private String handler;
    private String target;
    private String swap;
    private String select;
    private Map<String, Object> values;
    private Map<String, String> headers;

    /**
     * Create a new HtmxLocationRedirectView.
     */
    public HtmxLocationRedirectView() {
    }

    /**
     * Create a new HtmxLocationRedirectView with the given URL.
     *
     * <p>The given URL will be considered as relative to the web server, not as relative to the current ServletContext.
     *
     * @param url the URL to redirect to
     * @see #HtmxLocationRedirectView(String, boolean)
     */
    public HtmxLocationRedirectView(String url) {
        super(url);
    }

    /**
     * Create a new HtmxLocationRedirectView with the given URL.
     *
     * @param url             the URL to redirect to
     * @param contextRelative whether to interpret the given URL as relative to the current ServletContext
     */
    public HtmxLocationRedirectView(String url, boolean contextRelative) {
        super(url, contextRelative);
    }

    /**
     * Create a new HtmxLocationRedirectView with the given URL.
     *
     * @param url                   the URL to redirect to
     * @param contextRelative       whether to interpret the given URL as relative to the current ServletContext
     * @param exposeModelAttributes whether model attributes should be exposed as query parameters
     */
    public HtmxLocationRedirectView(String url, boolean contextRelative, boolean exposeModelAttributes) {
        super(url, contextRelative, exposeModelAttributes);
    }

    /**
     * Set the event that “triggered” the request.
     *
     * @param event an event name
     */
    public void setEvent(String event) {
        this.event = event;
    }

    /**
     * Set a callback that will handle the response HTML.
     *
     * @param handler a handler callback
     */
    public void setHandler(String handler) {
        this.handler = handler;
    }

    /**
     * Set headers to submit with the request.
     *
     * @param headers the headers
     */
    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    /**
     * A CSS selector to select the content you want swapped from a response.
     *
     * @param cssSelector a CSS selector
     */
    public void setSelect(String cssSelector) {
        this.select = cssSelector;
    }

    /**
     * Set the source element of the request.
     *
     * @param source the source element
     */
    public void setSource(String source) {
        this.source = source;
    }

    /**
     * Set how the response will be swapped in relative to the target.
     *
     * @param swap the swap mode
     */
    public void setSwap(String swap) {
        this.swap = swap;
    }

    /**
     * Set the target to swap the response into.
     *
     * @param target the target
     */
    public void setTarget(String target) {
        this.target = target;
    }

    /**
     * Set the values to submit with the request.
     *
     * @param values the values
     */
    public void setValues(Map<String, Object> values) {
        this.values = values;
    }

    @Override
    protected void sendRedirect(HttpServletRequest request, HttpServletResponse response, String targetUrl, boolean http10Compatible) throws IOException {

        String encodedURL = (isRemoteHost(targetUrl) ? targetUrl : response.encodeRedirectURL(targetUrl));
        HtmxLocation location = createLocation(encodedURL);

        if (location.hasContextData()) {
            setHeaderAsJson(response, HtmxResponseHeader.HX_LOCATION.getValue(), location);
        } else {
            response.setHeader(HtmxResponseHeader.HX_LOCATION.getValue(), location.getPath());
        }
    }

    private HtmxLocation createLocation(String url) {

        var location = new HtmxLocation();
        location.setPath(url);
        location.setSource(source);
        location.setEvent(event);
        location.setHandler(handler);
        location.setTarget(target);
        location.setSwap(swap);
        location.setSelect(select);
        location.setValues(values);
        location.setHeaders(headers);

        return location;
    }

    private void setHeaderAsJson(HttpServletResponse response, String name, Object value) {
        try {
            response.setHeader(name, jsonMapper.writeValueAsString(value));
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Unable to set header " + name + " to " + value, e);
        }
    }
}
