package io.github.wimdeblauwe.htmx.spring.boot.mvc;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * A {@link ResponseBodyAdvice} implementation that adds htmx response headers
 * based on {@link HtmxResponse}.
 * 
 * @since 5.0.0
 */
public class HtmxResponseBodyAdvice implements ResponseBodyAdvice<Object> {

    private final HtmxHandlerMethodHandler htmxHandlerMethodHandler;

    public HtmxResponseBodyAdvice(HtmxHandlerMethodHandler htmxHandlerMethodHandler) {
        this.htmxHandlerMethodHandler = htmxHandlerMethodHandler;
    }

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true; // Apply to all @ResponseBody methods
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
            Class<? extends HttpMessageConverter<?>> selectedConverterType,
            ServerHttpRequest request, ServerHttpResponse response) {

        if (request instanceof ServletServerHttpRequest servletRequest
                && response instanceof ServletServerHttpResponse servletResponse) {
            htmxHandlerMethodHandler.handleMethodArgument(servletRequest.getServletRequest(),
                    servletResponse.getServletResponse());
        }
        return body;
    }
}