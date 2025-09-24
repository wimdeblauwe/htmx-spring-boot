package io.github.wimdeblauwe.htmx.spring.boot.mvc;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * HandlerInterceptor that adds htmx specific headers to the response.
 */
public class HtmxHandlerInterceptor implements HandlerInterceptor {

    private final HtmxHandlerMethodHandler htmxHandlerMethodHandler;

    public HtmxHandlerInterceptor(HtmxHandlerMethodHandler htmxHandlerMethodHandler) {
        this.htmxHandlerMethodHandler = htmxHandlerMethodHandler;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        htmxHandlerMethodHandler.handleMethodArgument(request, response);
    }

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) {

        setVary(request, response);

        if (handler instanceof HandlerMethod handlerMethod) {
            htmxHandlerMethodHandler.handleMethodAnnotations(handlerMethod.getMethod(), request, response);
        }

        return true;
    }

    private void setVary(HttpServletRequest request, HttpServletResponse response) {
        if (request.getHeader(HtmxRequestHeader.HX_REQUEST.getValue()) != null) {
            response.addHeader(HttpHeaders.VARY, HtmxRequestHeader.HX_REQUEST.getValue());
        }
    }

}
