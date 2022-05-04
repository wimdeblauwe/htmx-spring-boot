package io.github.wimdeblauwe.hsbt.mvc;

import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

import static io.github.wimdeblauwe.hsbt.mvc.HtmxResponseHeader.*;

public class HtmxHandlerInterceptor implements HandlerInterceptor {
    @Override
    public void postHandle(HttpServletRequest request,
                           HttpServletResponse response,
                           Object handler,
                           ModelAndView modelAndView) throws Exception {
        if (handler instanceof HandlerMethod) {
            Method method = ((HandlerMethod) handler).getMethod();
            HxTrigger methodAnnotation = AnnotatedElementUtils.findMergedAnnotation(method, HxTrigger.class);
            if (methodAnnotation != null) {
                response.setHeader(getHeaderName(methodAnnotation.lifecycle()), methodAnnotation.value());
            }
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
