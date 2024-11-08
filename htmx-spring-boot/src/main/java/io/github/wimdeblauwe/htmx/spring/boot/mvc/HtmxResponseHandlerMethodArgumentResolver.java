package io.github.wimdeblauwe.htmx.spring.boot.mvc;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class HtmxResponseHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return (parameter.getParameterType() == HtmxResponse.class ||
                parameter.getParameterType() == HtmxResponse.Builder.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  @Nullable ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  @Nullable WebDataBinderFactory binderFactory) throws Exception {

        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);

        if (parameter.getParameterType() == HtmxResponse.class) {
            var htmxResponse = new HtmxResponse();
            request.setAttribute(RequestContextUtils.HTMX_RESPONSE_CONTEXT_ATTRIBUTE, htmxResponse);
            return htmxResponse;
        } else {
            HtmxResponse.Builder htmxResponseBuilder = HtmxResponse.builder();
            request.setAttribute(RequestContextUtils.HTMX_RESPONSE_CONTEXT_ATTRIBUTE, htmxResponseBuilder);
            return htmxResponseBuilder;
        }
    }

}
