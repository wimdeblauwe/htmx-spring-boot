package io.github.wimdeblauwe.hsbt.mvc;

import org.springframework.core.annotation.AnnotationUtils;
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
            HxTrigger methodAnnotation = AnnotationUtils.findAnnotation(method, HxTrigger.class);
            if (methodAnnotation != null) {
                response.setHeader("HX-Trigger", methodAnnotation.value());
            }
        }
    }
}
