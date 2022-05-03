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
            HxTrigger methodAnnotation = AnnotatedElementUtils.findMergedAnnotation(method, HxTrigger.class);
            if (methodAnnotation != null) {
                response.setHeader(getHeaderName(methodAnnotation.policy()), methodAnnotation.value());
            }
        }
    }

    private String getHeaderName(HxTriggerPolicy policy) {
        switch (policy) {
            case RECEIVE:
                return "HX-Trigger";
            case SETTLE:
                return "HX-Trigger-After-Settle";
            case SWAP:
                return "HX-Trigger-After-Swap";
            default:
                throw new IllegalArgumentException("Unknown policy:" + policy);
        }
    }
}
