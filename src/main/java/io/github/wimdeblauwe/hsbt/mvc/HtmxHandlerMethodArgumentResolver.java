package io.github.wimdeblauwe.hsbt.mvc;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import static io.github.wimdeblauwe.hsbt.mvc.HtmxRequestHeader.*;

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
        String hxRequestHeader = webRequest.getHeader(HX_REQUEST.getValue());
        if (hxRequestHeader == null) {
            return new HtmxRequest.Builder(false).build();
        }

        HtmxRequest.Builder builder = new HtmxRequest.Builder(true);
        if (webRequest.getHeader(HX_BOOSTED.getValue()) != null) {
            builder.withBoosted(true);
        }
        if (webRequest.getHeader(HX_CURRENT_URL.getValue()) != null) {
            builder.withCurrentUrl(webRequest.getHeader(HX_CURRENT_URL.getValue()));
        }
        if (webRequest.getHeader(HX_HISTORY_RESTORE_REQUEST.getValue()) != null) {
            builder.withHistoryRestoreRequest(true);
        }
        if (webRequest.getHeader(HX_PROMPT.getValue()) != null) {
            builder.withPromptResponse(webRequest.getHeader(HX_PROMPT.getValue()));
        }
        if (webRequest.getHeader(HX_TARGET.getValue()) != null) {
            builder.withTarget(webRequest.getHeader(HX_TARGET.getValue()));
        }
        if (webRequest.getHeader(HX_TRIGGER_NAME.getValue()) != null) {
            builder.withTriggerName(webRequest.getHeader(HX_TRIGGER_NAME.getValue()));
        }
        if (webRequest.getHeader(HX_TRIGGER.getValue()) != null) {
            builder.withTriggerId(webRequest.getHeader(HX_TRIGGER.getValue()));
        }
        return builder.build();
    }
}
