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
import org.springframework.web.servlet.View;

import java.lang.reflect.Method;
import java.time.Duration;

import static io.github.wimdeblauwe.htmx.spring.boot.mvc.HtmxResponseHeader.*;

public class HtmxHandlerInterceptor implements HandlerInterceptor {

    private final ObjectMapper objectMapper;
    private final HtmxResponseHandlerMethodReturnValueHandler htmxResponseHandlerMethodReturnValueHandler;

    public HtmxHandlerInterceptor(ObjectMapper objectMapper, HtmxResponseHandlerMethodReturnValueHandler htmxResponseHandlerMethodReturnValueHandler) {
        this.objectMapper = objectMapper;
        this.htmxResponseHandlerMethodReturnValueHandler = htmxResponseHandlerMethodReturnValueHandler;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        if (modelAndView != null) {
            for (Object value : modelAndView.getModel().values()) {
                if (value instanceof HtmxResponse res) {
                    buildAndRender(res, modelAndView, request, response);
                } else if (value instanceof HtmxResponse.Builder builder) {
                    buildAndRender(builder.build(), modelAndView, request, response);
                }
            }
        }
    }

    private void buildAndRender(HtmxResponse htmxResponse, ModelAndView mav, HttpServletRequest request, HttpServletResponse response) {
        View v = htmxResponseHandlerMethodReturnValueHandler.toView(htmxResponse);
        try {
            v.render(mav.getModel(), request, response);
            // ModelAndViewContainer is not available here, so flash attributes won't work
            htmxResponseHandlerMethodReturnValueHandler.addHxHeaders(htmxResponse, request, response, null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) {

        if (handler instanceof HandlerMethod) {
            Method method = ((HandlerMethod) handler).getMethod();
            setHxLocation(response, method);
            setHxPushUrl(request, response, method);
            setHxRedirect(response, method);
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

    private void setHxLocation(HttpServletResponse response, Method method) {
        HxLocation methodAnnotation = AnnotatedElementUtils.findMergedAnnotation(method, HxLocation.class);
        if (methodAnnotation != null) {
            var location = convertToLocation(methodAnnotation);
            if (location.hasContextData()) {
                setHeaderJsonValue(response, HtmxResponseHeader.HX_LOCATION, location);
            } else {
                setHeader(response, HtmxResponseHeader.HX_LOCATION, location.getPath());
            }
        }
    }

    private void setHxPushUrl(HttpServletRequest request, HttpServletResponse response, Method method) {
        HxPushUrl methodAnnotation = AnnotatedElementUtils.findMergedAnnotation(method, HxPushUrl.class);
        if (methodAnnotation != null) {
            if (HtmxValue.TRUE.equals(methodAnnotation.value())) {
                setHeader(response, HX_PUSH_URL, getRequestUrl(request));
            } else {
                setHeader(response, HX_PUSH_URL, methodAnnotation.value());
            }
        }
    }

    private void setHxRedirect(HttpServletResponse response, Method method) {
        HxRedirect methodAnnotation = AnnotatedElementUtils.findMergedAnnotation(method, HxRedirect.class);
        if (methodAnnotation != null) {
            setHeader(response, HX_REDIRECT, methodAnnotation.value());
        }
    }

    private void setHxReplaceUrl(HttpServletRequest request, HttpServletResponse response, Method method) {
        HxReplaceUrl methodAnnotation = AnnotatedElementUtils.findMergedAnnotation(method, HxReplaceUrl.class);
        if (methodAnnotation != null) {
            if (HtmxValue.TRUE.equals(methodAnnotation.value())) {
                setHeader(response, HX_REPLACE_URL, getRequestUrl(request));
            } else {
                setHeader(response, HX_REPLACE_URL, methodAnnotation.value());
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


}
