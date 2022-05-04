package io.github.wimdeblauwe.hsbt.mvc;

import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

public class HtmxHandlerInterceptor implements HandlerInterceptor {
    @Override
    public void postHandle(HttpServletRequest request,
                           HttpServletResponse response,
                           Object handler,
                           ModelAndView modelAndView) throws Exception {
        if (handler instanceof HandlerMethod) {
            Method method = ((HandlerMethod) handler).getMethod();
            setHxTrigger(response, method);
            setHxRefresh(response, method);
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
            response.setHeader("HX-Refresh", "true");
        }
    }

    private String getHeaderName(HxTriggerLifecycle lifecycle) {
        switch (lifecycle) {
            case RECEIVE:
                return "HX-Trigger";
            case SETTLE:
                return "HX-Trigger-After-Settle";
            case SWAP:
                return "HX-Trigger-After-Swap";
            default:
                throw new IllegalArgumentException("Unknown lifecycle:" + lifecycle);
        }
    }
}
