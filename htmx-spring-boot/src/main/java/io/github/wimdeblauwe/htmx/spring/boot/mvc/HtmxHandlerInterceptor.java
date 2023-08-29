package io.github.wimdeblauwe.htmx.spring.boot.mvc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.lang.reflect.Method;
import java.time.Duration;

import static io.github.wimdeblauwe.htmx.spring.boot.mvc.HtmxResponseHeader.*;

public class HtmxHandlerInterceptor implements HandlerInterceptor {

    private final ObjectMapper objectMapper;

    public HtmxHandlerInterceptor(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) {
        if (handler instanceof HandlerMethod) {
            Method method = ((HandlerMethod) handler).getMethod();
            setHxLocation(response, method);
            setHxPushUrl(response, method);
            setHxRedirect(response, method);
            setHxReplaceUrl(response, method);
            setHxReswap(response, method);
            setHxTrigger(response, method);
            setHxRefresh(response, method);
        }

        return true;
    }

    private void setHxLocation(HttpServletResponse response, Method method) {
        HxLocation methodAnnotation = AnnotatedElementUtils.findMergedAnnotation(method, HxLocation.class);
        if (methodAnnotation != null) {
            var location = convertToLocation(methodAnnotation);
            if (location.hasContextData()) {
                setHeaderJsonValue(response, HtmxResponseHeader.HX_LOCATION.getValue(), location);
            } else {
                response.setHeader(HtmxResponseHeader.HX_LOCATION.getValue(), location.getPath());
            }
        }
    }

    private void setHxPushUrl(HttpServletResponse response, Method method) {
        HxPushUrl methodAnnotation = AnnotatedElementUtils.findMergedAnnotation(method, HxPushUrl.class);
        if (methodAnnotation != null) {
            response.setHeader(HX_PUSH_URL.getValue(), methodAnnotation.value());
        }
    }

    private void setHxRedirect(HttpServletResponse response, Method method) {
        HxRedirect methodAnnotation = AnnotatedElementUtils.findMergedAnnotation(method, HxRedirect.class);
        if (methodAnnotation != null) {
            response.setHeader(HX_REDIRECT.getValue(), methodAnnotation.value());
        }
    }

    private void setHxReplaceUrl(HttpServletResponse response, Method method) {
        HxReplaceUrl methodAnnotation = AnnotatedElementUtils.findMergedAnnotation(method, HxReplaceUrl.class);
        if (methodAnnotation != null) {
            response.setHeader(HX_REPLACE_URL.getValue(), methodAnnotation.value());
        }
    }

    private void setHxReswap(HttpServletResponse response, Method method) {
        HxReswap methodAnnotation = AnnotatedElementUtils.findMergedAnnotation(method, HxReswap.class);
        if (methodAnnotation != null) {
            response.setHeader(HX_RESWAP.getValue(), convertToReswap(methodAnnotation));
        }
    }

    private void setHxTrigger(HttpServletResponse response, Method method) {
        HxTrigger methodAnnotation = AnnotatedElementUtils.findMergedAnnotation(method, HxTrigger.class);
        if (methodAnnotation != null) {
            response.setHeader(getHeaderName(methodAnnotation.lifecycle()), methodAnnotation.value());
        }
    }

    private void setHxRefresh(HttpServletResponse response, Method method) {
        HxRefresh methodAnnotation = AnnotatedElementUtils.findMergedAnnotation(method, HxRefresh.class);
        if (methodAnnotation != null) {
            response.setHeader(HX_REFRESH.getValue(), "true");
        }
    }

    private String getHeaderName(HxTriggerLifecycle lifecycle) {
        switch (lifecycle) {
            case RECEIVE:
                return HX_TRIGGER.getValue();
            case SETTLE:
                return HX_TRIGGER_AFTER_SETTLE.getValue();
            case SWAP:
                return HX_TRIGGER_AFTER_SWAP.getValue();
            default:
                throw new IllegalArgumentException("Unknown lifecycle:" + lifecycle);
        }
    }

    private void setHeaderJsonValue(HttpServletResponse response, String name, Object value) {
        try {
            response.setHeader(name, objectMapper.writeValueAsString(value));
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Unable to set header " + name + " to " + value, e);
        }
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

}
