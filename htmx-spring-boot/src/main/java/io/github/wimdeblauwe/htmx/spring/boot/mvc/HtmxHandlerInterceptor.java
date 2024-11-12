package io.github.wimdeblauwe.htmx.spring.boot.mvc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.Collection;
import java.util.HashMap;
import java.util.stream.Collectors;

import static io.github.wimdeblauwe.htmx.spring.boot.mvc.HtmxResponseHeader.*;

/**
 * HandlerInterceptor that adds htmx specific headers to the response.
 */
public class HtmxHandlerInterceptor implements HandlerInterceptor {

    private final ObjectMapper objectMapper;

    public HtmxHandlerInterceptor(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

        HtmxResponse htmxResponse = RequestContextUtils.getHtmxResponse(request);
        if (htmxResponse != null) {
            addHxTriggerHeaders(response, HtmxResponseHeader.HX_TRIGGER, htmxResponse.getTriggersInternal());
            addHxTriggerHeaders(response, HtmxResponseHeader.HX_TRIGGER_AFTER_SETTLE, htmxResponse.getTriggersAfterSettleInternal());
            addHxTriggerHeaders(response, HtmxResponseHeader.HX_TRIGGER_AFTER_SWAP, htmxResponse.getTriggersAfterSwapInternal());

            if (htmxResponse.getLocation() != null) {
                HtmxLocation location = htmxResponse.getLocation();
                if (location.hasContextData()) {
                    location.setPath(RequestContextUtils.createUrl(request, location.getPath(), htmxResponse.isContextRelative()));
                    setHeaderJsonValue(response, HtmxResponseHeader.HX_LOCATION, location);
                } else {
                    response.setHeader(HtmxResponseHeader.HX_LOCATION.getValue(), RequestContextUtils.createUrl(request, location.getPath(), htmxResponse.isContextRelative()));
                }
            }
            if (htmxResponse.getReplaceUrl() != null) {
                response.setHeader(HtmxResponseHeader.HX_REPLACE_URL.getValue(), RequestContextUtils.createUrl(request, htmxResponse.getReplaceUrl(), htmxResponse.isContextRelative()));
            }
            if (htmxResponse.getPushUrl() != null) {
                response.setHeader(HtmxResponseHeader.HX_PUSH_URL.getValue(), RequestContextUtils.createUrl(request, htmxResponse.getPushUrl(), htmxResponse.isContextRelative()));
            }
            if (htmxResponse.getRedirect() != null) {
                response.setHeader(HtmxResponseHeader.HX_REDIRECT.getValue(), RequestContextUtils.createUrl(request, htmxResponse.getRedirect(), htmxResponse.isContextRelative()));
            }
            if (htmxResponse.isRefresh()) {
                response.setHeader(HtmxResponseHeader.HX_REFRESH.getValue(), "true");
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
            setHxLocation(request, response, method);
            setHxPushUrl(request, response, method);
            setHxRedirect(request, response, method);
            setHxReplaceUrl(request, response, method);
            setHxReswap(response, method);
            setHxRetarget(response, method);
            setHxReselect(response, method);
            setHxTrigger(response, method);
            setHxTriggerAfterSettle(response, method);
            setHxTriggerAfterSwap(response, method);
            setHxRefresh(response, method);
            setVary(request, response);
        }

        return true;
    }

    private void setVary(HttpServletRequest request, HttpServletResponse response) {
        if (request.getHeader(HtmxRequestHeader.HX_REQUEST.getValue()) != null) {
            response.addHeader(HttpHeaders.VARY, HtmxRequestHeader.HX_REQUEST.getValue());
        }
    }

    private void setHxLocation(HttpServletRequest request, HttpServletResponse response, Method method) {
        HxLocation methodAnnotation = AnnotatedElementUtils.findMergedAnnotation(method, HxLocation.class);
        if (methodAnnotation != null) {
            var location = convertToLocation(methodAnnotation);
            if (location.hasContextData()) {
                location.setPath(RequestContextUtils.createUrl(request, location.getPath(), methodAnnotation.contextRelative()));
                setHeaderJsonValue(response, HtmxResponseHeader.HX_LOCATION, location);
            } else {
                setHeader(response, HtmxResponseHeader.HX_LOCATION, RequestContextUtils.createUrl(request, location.getPath(), methodAnnotation.contextRelative()));
            }
        }
    }

    private void setHxPushUrl(HttpServletRequest request, HttpServletResponse response, Method method) {
        HxPushUrl methodAnnotation = AnnotatedElementUtils.findMergedAnnotation(method, HxPushUrl.class);
        if (methodAnnotation != null) {
            if (HtmxValue.TRUE.equals(methodAnnotation.value())) {
                setHeader(response, HX_PUSH_URL, getRequestUrl(request));
            } else {
                setHeader(response, HX_PUSH_URL, RequestContextUtils.createUrl(request, methodAnnotation.value(), methodAnnotation.contextRelative()));
            }
        }
    }

    private void setHxRedirect(HttpServletRequest request, HttpServletResponse response, Method method) {
        HxRedirect methodAnnotation = AnnotatedElementUtils.findMergedAnnotation(method, HxRedirect.class);
        if (methodAnnotation != null) {
            setHeader(response, HX_REDIRECT, RequestContextUtils.createUrl(request, methodAnnotation.value(), methodAnnotation.contextRelative()));
        }
    }

    private void setHxReplaceUrl(HttpServletRequest request, HttpServletResponse response, Method method) {
        HxReplaceUrl methodAnnotation = AnnotatedElementUtils.findMergedAnnotation(method, HxReplaceUrl.class);
        if (methodAnnotation != null) {
            if (HtmxValue.TRUE.equals(methodAnnotation.value())) {
                setHeader(response, HX_REPLACE_URL, getRequestUrl(request));
            } else {
                setHeader(response, HX_REPLACE_URL, RequestContextUtils.createUrl(request, methodAnnotation.value(), methodAnnotation.contextRelative()));
            }
        }
    }

    private void setHxReswap(HttpServletResponse response, Method method) {
        HxReswap methodAnnotation = AnnotatedElementUtils.findMergedAnnotation(method, HxReswap.class);
        if (methodAnnotation != null) {
            setHeader(response, HX_RESWAP, convertToReswap(methodAnnotation));
        }
    }

    private void setHxRetarget(HttpServletResponse response, Method method) {
        HxRetarget methodAnnotation = AnnotatedElementUtils.findMergedAnnotation(method, HxRetarget.class);
        if (methodAnnotation != null) {
            setHeader(response, HX_RETARGET, methodAnnotation.value());
        }
    }

    private void setHxReselect(HttpServletResponse response, Method method) {
        HxReselect methodAnnotation = AnnotatedElementUtils.findMergedAnnotation(method, HxReselect.class);
        if (methodAnnotation != null) {
            setHeader(response, HX_RESELECT, methodAnnotation.value());
        }
    }

    private void setHxTrigger(HttpServletResponse response, Method method) {
        HxTrigger methodAnnotation = AnnotatedElementUtils.findMergedAnnotation(method, HxTrigger.class);
        if (methodAnnotation != null) {
            setHeader(response, convertToHeader(methodAnnotation.lifecycle()), methodAnnotation.value());
        }
    }

    private void setHxTriggerAfterSettle(HttpServletResponse response, Method method) {
        HxTriggerAfterSettle methodAnnotation = AnnotatedElementUtils.findMergedAnnotation(method, HxTriggerAfterSettle.class);
        if (methodAnnotation != null) {
            setHeader(response, HtmxResponseHeader.HX_TRIGGER_AFTER_SETTLE, methodAnnotation.value());
        }
    }

    private void setHxTriggerAfterSwap(HttpServletResponse response, Method method) {
        HxTriggerAfterSwap methodAnnotation = AnnotatedElementUtils.findMergedAnnotation(method, HxTriggerAfterSwap.class);
        if (methodAnnotation != null) {
            setHeader(response, HtmxResponseHeader.HX_TRIGGER_AFTER_SWAP, methodAnnotation.value());
        }
    }

    private void setHxRefresh(HttpServletResponse response, Method method) {
        HxRefresh methodAnnotation = AnnotatedElementUtils.findMergedAnnotation(method, HxRefresh.class);
        if (methodAnnotation != null) {
            setHeader(response, HX_REFRESH, HtmxValue.TRUE);
        }
    }

    private HtmxResponseHeader convertToHeader(HxTriggerLifecycle lifecycle) {
        switch (lifecycle) {
            case RECEIVE:
                return HX_TRIGGER;
            case SETTLE:
                return HX_TRIGGER_AFTER_SETTLE;
            case SWAP:
                return HX_TRIGGER_AFTER_SWAP;
            default:
                throw new IllegalArgumentException("Unknown lifecycle:" + lifecycle);
        }
    }

    private void setHeaderJsonValue(HttpServletResponse response, HtmxResponseHeader header, Object value) {
        try {
            response.setHeader(header.getValue(), objectMapper.writeValueAsString(value));
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Unable to set header " + header.getValue() + " to " + value, e);
        }
    }

    private void setHeader(HttpServletResponse response, HtmxResponseHeader header, String value) {
        response.setHeader(header.getValue(), value);
    }

    private void setHeader(HttpServletResponse response, HtmxResponseHeader header, String[] values) {
        response.setHeader(header.getValue(), String.join(",", values));
    }

    private HtmxLocation convertToLocation(HxLocation annotation) {
        var location = new HtmxLocation();
        location.setPath(annotation.path());
        if (!annotation.source().isEmpty()) {
            location.setSource(annotation.source());
        }
        if (!annotation.event().isEmpty()) {
            location.setEvent(annotation.event());
        }
        if (!annotation.handler().isEmpty()) {
            location.setHandler(annotation.handler());
        }
        if (!annotation.target().isEmpty()) {
            location.setTarget(annotation.target());
        }
        if (!annotation.target().isEmpty()) {
            location.setSwap(annotation.swap());
        }
        if (!annotation.select().isEmpty()) {
            location.setSelect(annotation.select());
        }
        return location;
    }

    private String convertToReswap(HxReswap annotation) {

        var reswap = new HtmxReswap(annotation.value());
        if (annotation.swap() != -1) {
            reswap.swap(Duration.ofMillis(annotation.swap()));
        }
        if (annotation.settle() != -1) {
            reswap.swap(Duration.ofMillis(annotation.settle()));
        }
        if (annotation.transition()) {
            reswap.transition();
        }
        if (annotation.focusScroll() != HxReswap.FocusScroll.UNDEFINED) {
            reswap.focusScroll(annotation.focusScroll() == HxReswap.FocusScroll.TRUE);
        }
        if (annotation.show() != HxReswap.Position.UNDEFINED) {
            reswap.show(convertToPosition(annotation.show()));
            if (!annotation.showTarget().isEmpty()) {
                reswap.scrollTarget(annotation.showTarget());
            }
        }
        if (annotation.scroll() != HxReswap.Position.UNDEFINED) {
            reswap.scroll(convertToPosition(annotation.scroll()));
            if (!annotation.scrollTarget().isEmpty()) {
                reswap.scrollTarget(annotation.scrollTarget());
            }
        }

        return reswap.toString();
    }

    private HtmxReswap.Position convertToPosition(HxReswap.Position position) {
        return switch (position) {
            case TOP -> HtmxReswap.Position.TOP;
            case BOTTOM -> HtmxReswap.Position.BOTTOM;
            default -> throw new IllegalStateException("Unexpected value: " + position);
        };
    }

    private String getRequestUrl(HttpServletRequest request) {
        String path = request.getRequestURI();
        String queryString = request.getQueryString();

        if (queryString != null && !queryString.isEmpty()) {
            path += "?" + queryString;
        }
        return path;
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
