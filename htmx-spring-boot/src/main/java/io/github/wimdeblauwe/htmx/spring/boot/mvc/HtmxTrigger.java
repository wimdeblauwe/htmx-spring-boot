package io.github.wimdeblauwe.htmx.spring.boot.mvc;

import java.util.Map;
import java.util.Objects;

/**
 * Represents a value of HX-Trigger, HX-Trigger-After-Settle or HX-Trigger-After-Swap.
 *
 * @see <a href="https://htmx.org/headers/hx-trigger/">HX-Trigger Response Headers</a>
 */
public class HtmxTrigger {

    private final String eventName;
    private final Object eventDetail;

    public HtmxTrigger(String eventName, Object eventDetail) {
        this.eventName = eventName;
        this.eventDetail = eventDetail;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        HtmxTrigger that = (HtmxTrigger) object;
        return Objects.equals(eventName, that.eventName) && Objects.equals(eventDetail, that.eventDetail);
    }

    public Object getEventDetail() {
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
