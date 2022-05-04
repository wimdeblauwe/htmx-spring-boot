package io.github.wimdeblauwe.hsbt.mvc;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.servlet.mvc.condition.HeadersRequestCondition;
import org.springframework.web.servlet.mvc.condition.RequestCondition;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;

import static io.github.wimdeblauwe.hsbt.mvc.HtmxRequestHeader.HX_REQUEST;

public class HtmxRequestMappingHandlerMapping extends RequestMappingHandlerMapping {
    @Override
    protected RequestCondition<?> getCustomTypeCondition(Class<?> handlerType) {
        HxRequest typeAnnotation = AnnotationUtils.findAnnotation(handlerType, HxRequest.class);
        return createCondition(typeAnnotation);
    }

    @Override
    protected RequestCondition<?> getCustomMethodCondition(Method method) {
        HxRequest methodAnnotation = AnnotationUtils.findAnnotation(method, HxRequest.class);
        return createCondition(methodAnnotation);
    }

    private RequestCondition<?> createCondition(HxRequest hxRequest) {
        if (hxRequest != null) {
            return new HeadersRequestCondition(HX_REQUEST.getValue());
        }
        return null;
    }
}
