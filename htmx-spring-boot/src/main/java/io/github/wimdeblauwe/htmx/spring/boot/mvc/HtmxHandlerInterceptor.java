package io.github.wimdeblauwe.htmx.spring.boot.mvc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.stream.Collectors;

/**
 * HandlerInterceptor that adds htmx specific headers to the response.
 */
public class HtmxHandlerInterceptor implements HandlerInterceptor {

    private final ObjectMapper objectMapper;
    private final HtmxHandlerMethodAnnotationHandler handlerMethodAnnotationHandler;

    public HtmxHandlerInterceptor(ObjectMapper objectMapper, HtmxHandlerMethodAnnotationHandler handlerMethodAnnotationHandler) {
        this.objectMapper = objectMapper;
        this.handlerMethodAnnotationHandler = handlerMethodAnnotationHandler;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

        HtmxResponse htmxResponse = RequestContextUtils.getHtmxResponse(request);
        if (htmxResponse != null) {
            addHxTriggerHeaders(response, HtmxResponseHeader.HX_TRIGGER, htmxResponse.getTriggers());
            addHxTriggerHeaders(response, HtmxResponseHeader.HX_TRIGGER_AFTER_SETTLE, htmxResponse.getTriggersAfterSettle());
            addHxTriggerHeaders(response, HtmxResponseHeader.HX_TRIGGER_AFTER_SWAP, htmxResponse.getTriggersAfterSwap());

            if (htmxResponse.getReplaceUrl() != null) {
                response.setHeader(HtmxResponseHeader.HX_REPLACE_URL.getValue(), RequestContextUtils.createUrl(request, htmxResponse.getReplaceUrl(), htmxResponse.isContextRelative()));
            }
            if (htmxResponse.getPushUrl() != null) {
                response.setHeader(HtmxResponseHeader.HX_PUSH_URL.getValue(), RequestContextUtils.createUrl(request, htmxResponse.getPushUrl(), htmxResponse.isContextRelative()));
            }
            if (htmxResponse.getRetarget() != null) {
                response.setHeader(HtmxResponseHeader.HX_RETARGET.getValue(), htmxResponse.getRetarget());
            }
            if (htmxResponse.getReselect() != null) {
                response.setHeader(HtmxResponseHeader.HX_RESELECT.getValue(), htmxResponse.getReselect());
            }
            if (htmxResponse.getReswap() != null) {
                response.setHeader(HtmxResponseHeader.HX_RESWAP.getValue(), htmxResponse.getReswap().toHeaderValue());
            }
        }
    }

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) {

        if (handler instanceof HandlerMethod) {
            Method method = ((HandlerMethod) handler).getMethod();
            setVary(request, response);
            handlerMethodAnnotationHandler.handleMethod(method, request, response);
        }

        return true;
    }

    private void setVary(HttpServletRequest request, HttpServletResponse response) {
        if (request.getHeader(HtmxRequestHeader.HX_REQUEST.getValue()) != null) {
            response.addHeader(HttpHeaders.VARY, HtmxRequestHeader.HX_REQUEST.getValue());
        }
    }

    private void setHeaderJsonValue(HttpServletResponse response, HtmxResponseHeader header, Object value) {
        try {
            response.setHeader(header.getValue(), objectMapper.writeValueAsString(value));
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Unable to set header " + header.getValue() + " to " + value, e);
        }
    }

    private void addHxTriggerHeaders(HttpServletResponse response, HtmxResponseHeader headerName, Collection<HtmxTrigger> triggers) {
        if (triggers.isEmpty()) {
            return;
        }

        // separate event names by commas if no additional details are available
        if (triggers.stream().allMatch(t -> t.getEventDetail() == null)) {
            String value = triggers.stream()
                                   .map(HtmxTrigger::getEventName)
                                   .collect(Collectors.joining(","));

            response.setHeader(headerName.getValue(), value);
            return;
        }

        // multiple events with or without details
        var triggerMap = new HashMap<String, Object>();
        for (HtmxTrigger trigger : triggers) {
            triggerMap.put(trigger.getEventName(), trigger.getEventDetail());
        }
        setHeaderJsonValue(response, headerName, triggerMap);
    }

}
