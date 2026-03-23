package io.github.wimdeblauwe.htmx.spring.boot.mvc;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

/**
 * Represents a value of HX-Trigger, HX-Trigger-After-Settle or HX-Trigger-After-Swap.
 *
 * @see <a href="https://four.htmx.org/reference/headers/HX-Trigger">HX-Trigger</a>
 */
public class HtmxTrigger {

    private final String eventName;
    private final @Nullable Object eventDetail;

    public HtmxTrigger(String eventName, @Nullable Object eventDetail) {
        this.eventName = eventName;
        this.eventDetail = eventDetail;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }

        return o instanceof HtmxTrigger that &&
                Objects.equals(eventName, that.eventName) &&
                Objects.equals(eventDetail, that.eventDetail);
    }

    public @Nullable Object getEventDetail() {
        return eventDetail;
    }

    public String getEventName() {
        return eventName;
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventName, eventDetail);
    }

}
