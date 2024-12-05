package io.github.wimdeblauwe.htmx.spring.boot.mvc;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;
import org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod;

import java.lang.reflect.Method;

/**
 * A custom {@link ExceptionHandlerExceptionResolver} that handles htmx annotations
 * present on exception handler methods.
 *
 * @since 3.6.2
 */
public class HtmxExceptionHandlerExceptionResolver extends ExceptionHandlerExceptionResolver {

    private final HtmxHandlerMethodHandler htmxHandlerMethodHandler;

    public HtmxExceptionHandlerExceptionResolver(HtmxHandlerMethodHandler htmxHandlerMethodHandler) {
        this.htmxHandlerMethodHandler = htmxHandlerMethodHandler;
    }

    @Override
    protected ModelAndView doResolveHandlerMethodException(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod, Exception exception) {

        ServletWebRequest webRequest = new ServletWebRequest(request, response);
        ServletInvocableHandlerMethod exceptionHandlerMethod = this.getExceptionHandlerMethod(handlerMethod, exception, webRequest);
        if (exceptionHandlerMethod != null) {
            Method method = exceptionHandlerMethod.getMethod();
            htmxHandlerMethodHandler.handleMethodAnnotations(method, request, response);
        }

        ModelAndView modelAndView = super.doResolveHandlerMethodException(request, response, handlerMethod, exception);

        htmxHandlerMethodHandler.handleMethodArgument(request, response);

        return modelAndView;
    }

}
