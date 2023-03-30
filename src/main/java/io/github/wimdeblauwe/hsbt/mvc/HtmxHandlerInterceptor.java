package io.github.wimdeblauwe.hsbt.mvc;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.lang.reflect.Method;

import static io.github.wimdeblauwe.hsbt.mvc.HtmxResponseHeader.*;

public class HtmxHandlerInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request,
                           HttpServletResponse response,
                           Object handler) {
        if (handler instanceof HandlerMethod) {
            Method method = ((HandlerMethod) handler).getMethod();
            setHxTrigger(response, method);
            setHxRefresh(response, method);
        }

        return true;
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
}
