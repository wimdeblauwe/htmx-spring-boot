package io.github.wimdeblauwe.htmx.spring.boot.mvc;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.ui.ModelMap;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Handles return values that are of type {@link HtmxView}.
 *
 * @since 3.6.0
 */
public class HtmxViewMethodReturnValueHandler implements HandlerMethodReturnValueHandler {

    private final ViewResolver viewResolver;
    private final LocaleResolver localeResolver;

    public HtmxViewMethodReturnValueHandler(ViewResolver viewResolver, LocaleResolver localeResolver) {
        this.viewResolver = viewResolver;
        this.localeResolver = localeResolver;
    }

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
            mavContainer.setView(toView(htmxView));
        } else if (returnValue != null) {
            throw new UnsupportedOperationException("Unexpected return type: " + returnType.getParameterType().getName() + " in method: " + returnType.getMethod());
        }
    }

    private View toView(HtmxView htmxView) {

        return (model, request, response) -> {
            Locale locale = localeResolver.resolveLocale(request);
            ContentCachingResponseWrapper wrapper = new ContentCachingResponseWrapper(response);

            for (ModelAndView mav : htmxView.getViews()) {
                View view;
                if (mav.isReference()) {
                    view = viewResolver.resolveViewName(mav.getViewName(), locale);
                    if (view == null) {
                        throw new IllegalArgumentException("Could not resolve view with name '" + mav.getViewName() + "'.");
                    }
                } else {
                    view = mav.getView();
                }

                for (String key : model.keySet()) {
                    if (!mav.getModel().containsKey(key)) {
                        mav.getModel().put(key, model.get(key));
                    }
                }

                view.render(mav.getModel(), request, wrapper);
            }

            wrapper.copyBodyToResponse();
        };
    }

}
