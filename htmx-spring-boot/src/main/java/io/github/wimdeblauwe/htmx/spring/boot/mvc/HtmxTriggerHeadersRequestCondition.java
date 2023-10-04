package io.github.wimdeblauwe.htmx.spring.boot.mvc;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.web.cors.CorsUtils;
import org.springframework.web.servlet.mvc.condition.RequestCondition;

/**
 * A request condition that matches a request against the htmx request header values of HX-Trigger or
 * HX-Trigger-Name, in that exact order.
 */
class HtmxTriggerHeadersRequestCondition implements RequestCondition<HtmxTriggerHeadersRequestCondition> {

    private static final HtmxTriggerHeadersRequestCondition EMPTY_CONDITION = new HtmxTriggerHeadersRequestCondition();

    private String value;

    public HtmxTriggerHeadersRequestCondition(String value) {
        this.value = value;
    }

    HtmxTriggerHeadersRequestCondition() {
    }

    @Override
    public HtmxTriggerHeadersRequestCondition combine(HtmxTriggerHeadersRequestCondition other) {
        return other.value != null ? other : this;
    }

    @Override
    public int compareTo(HtmxTriggerHeadersRequestCondition other, HttpServletRequest request) {
        if (this.value == null && other.value == null) {
            return 0;
        } else if (this.value == null) {
            return 1;
        } else if (other.value == null) {
            return -1;
        } else {
            return this.value.compareTo(other.value);
        }
    }

    @Override
    public HtmxTriggerHeadersRequestCondition getMatchingCondition(HttpServletRequest request) {
        if (CorsUtils.isPreFlightRequest(request)) {
            return EMPTY_CONDITION;
        }

        // HX-Trigger
        String headerValue = request.getHeader(HtmxRequestHeader.HX_TRIGGER.getValue());
        if (headerValue != null && headerValue.equals(value)) {
            return this;
        }

        // HX-Trigger-Name
        headerValue = request.getHeader(HtmxRequestHeader.HX_TRIGGER_NAME.getValue());
        if (headerValue != null && headerValue.equals(value)) {
            return this;
        }

        return null;
    }
}
