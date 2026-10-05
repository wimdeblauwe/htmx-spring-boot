package io.github.wimdeblauwe.htmx.spring.boot.mvc;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.jspecify.annotations.Nullable;
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

    private @Nullable String path;
    private @Nullable String source;
    private @Nullable String event;
    private @Nullable String handler;
    private @Nullable String target;
    private @Nullable String swap;
    private @Nullable String select;
    private @Nullable Map<String, Object> values;
    private @Nullable Map<String, String> headers;

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

        return o instanceof HtmxLocation that &&
                Objects.equals(path, that.path) &&
                Objects.equals(source, that.source) &&
                Objects.equals(event, that.event) &&
                Objects.equals(handler, that.handler) &&
                Objects.equals(target, that.target) &&
                Objects.equals(swap, that.swap) &&
                Objects.equals(select, that.select) &&
                Objects.equals(values, that.values) &&
                Objects.equals(headers, that.headers);
    }

    public @Nullable String getEvent() {
        return event;
    }

    public @Nullable String getHandler() {
        return handler;
    }

    public @Nullable Map<String, String> getHeaders() {
        return headers;
    }

    public @Nullable String getPath() {
        return path;
    }

    public @Nullable String getSelect() {
        return select;
    }

    public @Nullable String getSource() {
        return source;
    }

    public @Nullable String getSwap() {
        return swap;
    }

    public @Nullable String getTarget() {
        return target;
    }

    public @Nullable Map<String, Object> getValues() {
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
        return Objects.hash(path, source, event, handler, target, swap, select, values, headers);
    }

    public void setEvent(@Nullable String event) {
        this.event = event;
    }

    public void setHandler(@Nullable String handler) {
        this.handler = handler;
    }

    public void setHeaders(@Nullable Map<String, String> headers) {
        this.headers = headers;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setSelect(@Nullable String select) {
        this.select = select;
    }

    public void setSource(@Nullable String source) {
        this.source = source;
    }

    public void setSwap(@Nullable String swap) {
        this.swap = swap;
    }

    public void setTarget(@Nullable String target) {
        this.target = target;
    }

    public void setValues(@Nullable Map<String, Object> values) {
        this.values = values;
    }

}
