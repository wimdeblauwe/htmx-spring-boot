package io.github.wimdeblauwe.hsbt.mvc;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

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
        String hxRequestHeader = webRequest.getHeader("HX-Request");
        if (hxRequestHeader == null) {
            return new HtmxRequest.Builder(false).build();
        }

        HtmxRequest.Builder builder = new HtmxRequest.Builder(true);
        if (webRequest.getHeader("HX-Boosted") != null) {
            builder.withBoosted(true);
        }
        if (webRequest.getHeader("HX-Current-URL") != null) {
            builder.withCurrentUrl(webRequest.getHeader("HX-Current-URL"));
        }
        if (webRequest.getHeader("HX-History-Restore-Request") != null) {
            builder.withHistoryRestoreRequest(true);
        }
        if (webRequest.getHeader("HX-Prompt") != null) {
            builder.withPromptResponse(webRequest.getHeader("HX-Prompt"));
        }
        return builder.build();
    }
}
