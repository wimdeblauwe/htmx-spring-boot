package io.github.wimdeblauwe.htmx.spring.boot.mvc;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

public class HtmxResponseHandlerMethodReturnValueHandler implements HandlerMethodReturnValueHandler {
    private final HtmxResponseHelper responseHelper;

    public HtmxResponseHandlerMethodReturnValueHandler(HtmxResponseHelper responseHelper) {
        this.responseHelper = responseHelper;
    }

    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        return returnType.getParameterType().equals(HtmxResponse.class);
    }

    @Override
    public void handleReturnValue(Object returnValue,
                                  MethodParameter returnType,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest) throws Exception {

        HtmxResponse htmxResponse = (HtmxResponse) returnValue;
        mavContainer.setView(responseHelper.toView(htmxResponse));

        responseHelper.addHxHeaders(htmxResponse, webRequest.getNativeResponse(HttpServletResponse.class));
    }
}
