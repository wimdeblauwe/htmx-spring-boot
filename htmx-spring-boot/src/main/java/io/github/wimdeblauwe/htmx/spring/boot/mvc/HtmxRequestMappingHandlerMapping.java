package io.github.wimdeblauwe.htmx.spring.boot.mvc;

import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.mvc.condition.CompositeRequestCondition;
import org.springframework.web.servlet.mvc.condition.HeadersRequestCondition;
import org.springframework.web.servlet.mvc.condition.RequestCondition;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;
import java.util.ArrayList;

import static io.github.wimdeblauwe.htmx.spring.boot.mvc.HtmxRequestHeader.*;

public class HtmxRequestMappingHandlerMapping extends RequestMappingHandlerMapping {

    @Override
    protected RequestCondition<?> getCustomMethodCondition(Method method) {
        HxRequest methodAnnotation = AnnotatedElementUtils.findMergedAnnotation(method, HxRequest.class);
        return createCondition(methodAnnotation);
    }

    @Override
    protected RequestCondition<?> getCustomTypeCondition(Class<?> handlerType) {
        HxRequest typeAnnotation = AnnotatedElementUtils.findMergedAnnotation(handlerType, HxRequest.class);
        return createCondition(typeAnnotation);
    }

    private RequestCondition<?> createCondition(HxRequest hxRequest) {

        if (hxRequest == null) {
            return null;
        }

        var conditions = new ArrayList<RequestCondition<?>>();
        conditions.add(new HeadersRequestCondition(HX_REQUEST.getValue()));

        if (StringUtils.hasText(hxRequest.value())) {
            conditions.add(new HtmxTriggerHeadersRequestCondition(hxRequest.value()));
        } else {
            if (StringUtils.hasText(hxRequest.triggerId())) {
                conditions.add(new HeadersRequestCondition(HX_TRIGGER.getValue() + "=" + hxRequest.triggerId()));
            }
            if (StringUtils.hasText(hxRequest.triggerName())) {
                conditions.add(new HeadersRequestCondition(HX_TRIGGER_NAME.getValue() + "=" + hxRequest.triggerName()));
            }
        }

        if (StringUtils.hasText(hxRequest.target())) {
            conditions.add(new HeadersRequestCondition(HX_TARGET.getValue() + "=" + hxRequest.target()));
        }

        if (!hxRequest.boosted()) {
            // exclude boosted requests by checking whether the header is absent
            conditions.add(new HeadersRequestCondition("!" + HX_BOOSTED.getValue()));
        }

        return new CompositeRequestCondition(conditions.toArray(RequestCondition[]::new));
    }

}
