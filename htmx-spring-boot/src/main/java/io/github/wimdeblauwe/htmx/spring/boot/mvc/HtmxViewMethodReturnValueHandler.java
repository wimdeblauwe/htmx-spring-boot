package io.github.wimdeblauwe.htmx.spring.boot.mvc;

import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.view.FragmentsRendering;

/**
 * Handles return values that are of type {@link HtmxView}.
 *
 * @since 3.6.0
 * @deprecated since 4.0.0 for removal in 4.1.0 in favor of <a href="https://docs.spring.io/spring-framework/reference/web/webmvc-view/mvc-fragments.html">HTML Fragments</a> support.
 */
@Deprecated
public class HtmxViewMethodReturnValueHandler implements HandlerMethodReturnValueHandler {

    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        return HtmxView.class.isAssignableFrom(returnType.getParameterType());
    }

    @Override
    public void handleReturnValue(Object returnValue,
                                  MethodParameter returnType,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest) throws Exception {

        if (returnValue instanceof HtmxView htmxView) {
            mavContainer.setView(FragmentsRendering.with(htmxView.getViews()).build());
        } else if (returnValue != null) {
            throw new UnsupportedOperationException("Unexpected return type: " + returnType.getParameterType().getName() + " in method: " + returnType.getMethod());
        }
    }

}
