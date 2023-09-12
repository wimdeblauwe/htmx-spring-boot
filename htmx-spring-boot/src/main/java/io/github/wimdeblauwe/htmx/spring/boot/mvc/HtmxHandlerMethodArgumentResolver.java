package io.github.wimdeblauwe.htmx.spring.boot.mvc;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.Objects;

import static io.github.wimdeblauwe.htmx.spring.boot.mvc.HtmxRequestHeader.*;

public class HtmxHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(HtmxRequest.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) throws Exception {
        return createHtmxRequest(Objects.requireNonNull(webRequest.getNativeRequest(HttpServletRequest.class)));
    }

    public static HtmxRequest createHtmxRequest(HttpServletRequest request) {
        String hxRequestHeader = request.getHeader(HX_REQUEST.getValue());
        if (hxRequestHeader == null) {
            return HtmxRequest.empty();
        }

        HtmxRequest.Builder builder = HtmxRequest.builder();
        if (request.getHeader(HX_BOOSTED.getValue()) != null) {
            builder.boosted(true);
        }
        if (request.getHeader(HX_CURRENT_URL.getValue()) != null) {
            builder.currentUrl(request.getHeader(HX_CURRENT_URL.getValue()));
        }
        if (request.getHeader(HX_HISTORY_RESTORE_REQUEST.getValue()) != null) {
            builder.historyRestoreRequest(true);
        }
        if (request.getHeader(HX_PROMPT.getValue()) != null) {
            builder.promptResponse(request.getHeader(HX_PROMPT.getValue()));
        }
        if (request.getHeader(HX_TARGET.getValue()) != null) {
            builder.target(request.getHeader(HX_TARGET.getValue()));
        }
        if (request.getHeader(HX_TRIGGER_NAME.getValue()) != null) {
            builder.triggerName(request.getHeader(HX_TRIGGER_NAME.getValue()));
        }
        if (request.getHeader(HX_TRIGGER.getValue()) != null) {
            builder.triggerId(request.getHeader(HX_TRIGGER.getValue()));
        }
        return builder.build();
    }
}
