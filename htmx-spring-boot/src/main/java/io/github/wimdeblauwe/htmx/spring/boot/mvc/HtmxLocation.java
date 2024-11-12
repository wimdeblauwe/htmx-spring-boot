package io.github.wimdeblauwe.htmx.spring.boot.mvc;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.util.CollectionUtils;

import java.util.Map;
import java.util.Objects;

/**
 * Represents the HX-Location response header value.
 *
 * @see <a href="https://htmx.org/headers/hx-location/">HX-Location Response Header</a>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HtmxLocation {

    private String path;
    private String source;
    private String event;
    private String handler;
    private String target;
    private String swap;
    private String select;
    private Map<String, Object> values;
    private Map<String, String> headers;

    public HtmxLocation() {
    }

    public HtmxLocation(String path) {
        this.path = path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        HtmxLocation location = (HtmxLocation) o;
        return Objects.equals(path, location.path) && Objects.equals(source, location.source) && Objects.equals(event, location.event) && Objects.equals(handler, location.handler) && Objects.equals(target, location.target) && Objects.equals(swap, location.swap)
                && Objects.equals(values, location.values) && Objects.equals(headers, location.headers);
    }

    public String getEvent() {
        return event;
    }

    public String getHandler() {
        return handler;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getPath() {
        return path;
    }

    public String getSelect() {
        return select;
    }

    public String getSource() {
        return source;
    }

    public String getSwap() {
        return swap;
    }

    public String getTarget() {
        return target;
    }

    public Map<String, Object> getValues() {
        return values;
    }

    public boolean hasContextData() {
        return source != null ||
                event != null ||
                handler != null ||
                target != null ||
                swap != null ||
                !CollectionUtils.isEmpty(values) ||
                !CollectionUtils.isEmpty(headers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path, source, event, handler, target, swap, values, headers);
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public void setHandler(String handler) {
        this.handler = handler;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setSelect(String select) {
        this.select = select;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setSwap(String swap) {
        this.swap = swap;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public void setValues(Map<String, Object> values) {
        this.values = values;
    }

}
