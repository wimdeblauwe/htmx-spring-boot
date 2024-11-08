package io.github.wimdeblauwe.htmx.spring.boot.mvc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.ObjectFactory;
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
 * @deprecated Using {@link HtmxResponse} as handler method return value is deprecated,
 * use {@link HtmxView}, {@link HtmxRedirectView} or {@link HtmxLocationRedirectView} instead.
 * Will be removed in 4.0.0.
 */
@Deprecated
public class HtmxResponseHandlerMethodReturnValueHandler implements HandlerMethodReturnValueHandler {
    private final ViewResolver views;
    private final ObjectFactory<LocaleResolver> locales;
    private final ObjectMapper objectMapper;

    public HtmxResponseHandlerMethodReturnValueHandler(ViewResolver views,
                                                       ObjectFactory<LocaleResolver> locales,
                                                       ObjectMapper objectMapper) {
        this.views = views;
        this.locales = locales;
        this.objectMapper = objectMapper;
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
        mavContainer.setView(toView(htmxResponse));

        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        HttpServletResponse response = webRequest.getNativeResponse(HttpServletResponse.class);

        addHxHeaders(htmxResponse, request, response, mavContainer);
    }

    View toView(HtmxResponse htmxResponse) {

        Assert.notNull(htmxResponse, "HtmxResponse must not be null!");

        return (model, request, response) -> {
            Locale locale = locales.getObject().resolveLocale(request);
            ContentCachingResponseWrapper wrapper = new ContentCachingResponseWrapper(response);
            for (ModelAndView modelAndView : htmxResponse.getViews()) {
                View view = modelAndView.getView();
                if (view == null) {
                    view = views.resolveViewName(modelAndView.getViewName(), locale);
                }
                for (String key : model.keySet()) {
                    if (!modelAndView.getModel().containsKey(key)) {
                        modelAndView.getModel().put(key, model.get(key));
                    }
                }
                Assert.notNull(view, "Template '" + modelAndView + "' could not be resolved");
                view.render(modelAndView.getModel(), request, wrapper);
            }
            wrapper.copyBodyToResponse();
        };
    }

    void addHxHeaders(HtmxResponse htmxResponse, HttpServletRequest request, HttpServletResponse response, @Nullable ModelAndViewContainer mavContainer) {
        addHxTriggerHeaders(response, HtmxResponseHeader.HX_TRIGGER, htmxResponse.getTriggersInternal());
        addHxTriggerHeaders(response, HtmxResponseHeader.HX_TRIGGER_AFTER_SETTLE, htmxResponse.getTriggersAfterSettleInternal());
        addHxTriggerHeaders(response, HtmxResponseHeader.HX_TRIGGER_AFTER_SWAP, htmxResponse.getTriggersAfterSwapInternal());

        if (htmxResponse.getLocation() != null) {
            HtmxLocation location = htmxResponse.getLocation();
            if (mavContainer != null) {
                saveFlashAttributes(mavContainer, request, response, location.getPath());
            }
            if (location.hasContextData()) {
                location.setPath(RequestContextUtils.createUrl(request, location.getPath(), htmxResponse.isContextRelative()));
                setHeaderJsonValue(response, HtmxResponseHeader.HX_LOCATION.getValue(), location);
            } else {
                response.setHeader(HtmxResponseHeader.HX_LOCATION.getValue(), RequestContextUtils.createUrl(request, location.getPath(), htmxResponse.isContextRelative()));
            }
        }
        if (htmxResponse.getReplaceUrl() != null) {
            response.setHeader(HtmxResponseHeader.HX_REPLACE_URL.getValue(), RequestContextUtils.createUrl(request, htmxResponse.getReplaceUrl(), htmxResponse.isContextRelative()));
        }
        if (htmxResponse.getPushUrl() != null) {
            response.setHeader(HtmxResponseHeader.HX_PUSH_URL.getValue(), RequestContextUtils.createUrl(request, htmxResponse.getPushUrl(), htmxResponse.isContextRelative()));
        }
        if (htmxResponse.getRedirect() != null) {
            if (mavContainer != null) {
                saveFlashAttributes(mavContainer, request, response, htmxResponse.getRedirect());
            }
            response.setHeader(HtmxResponseHeader.HX_REDIRECT.getValue(), RequestContextUtils.createUrl(request, htmxResponse.getRedirect(), htmxResponse.isContextRelative()));
        }
        if (htmxResponse.isRefresh()) {
            response.setHeader(HtmxResponseHeader.HX_REFRESH.getValue(), "true");
        }
        if (htmxResponse.getRetarget() != null) {
            response.setHeader(HtmxResponseHeader.HX_RETARGET.getValue(), htmxResponse.getRetarget());
        }
        if (htmxResponse.getReselect() != null) {
            response.setHeader(HtmxResponseHeader.HX_RESELECT.getValue(), htmxResponse.getReselect());
        }
        if (htmxResponse.getReswap() != null) {
            response.setHeader(HtmxResponseHeader.HX_RESWAP.getValue(), htmxResponse.getReswap().toHeaderValue());
        }
    }

    private void addHxTriggerHeaders(HttpServletResponse response, HtmxResponseHeader headerName, Collection<HtmxTrigger> triggers) {
        if (triggers.isEmpty()) {
            return;
        }

        // separate event names by commas if no additional details are available
        if (triggers.stream().allMatch(t -> t.getEventDetail() == null)) {
            String value = triggers.stream()
                                   .map(HtmxTrigger::getEventName)
                                   .collect(Collectors.joining(","));

            response.setHeader(headerName.getValue(), value);
            return;
        }

        // multiple events with or without details
        var triggerMap = new HashMap<String, Object>();
        for (HtmxTrigger trigger : triggers) {
            triggerMap.put(trigger.getEventName(), trigger.getEventDetail());
        }
        setHeaderJsonValue(response, headerName.getValue(), triggerMap);
    }

    private void setHeaderJsonValue(HttpServletResponse response, String name, Object value) {
        try {
            response.setHeader(name, objectMapper.writeValueAsString(value));
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Unable to set header " + name + " to " + value, e);
        }
    }

    private void saveFlashAttributes(ModelAndViewContainer mavContainer, HttpServletRequest request, HttpServletResponse response, String location) {
        mavContainer.setRedirectModelScenario(true);
        ModelMap model = mavContainer.getModel();

        if (model instanceof RedirectAttributes redirectAttributes) {
            Map<String, ?> flashAttributes = redirectAttributes.getFlashAttributes();
            if (!CollectionUtils.isEmpty(flashAttributes)) {
                if (request != null) {
                    org.springframework.web.servlet.support.RequestContextUtils.getOutputFlashMap(request).putAll(flashAttributes);
                    if (response != null) {
                        org.springframework.web.servlet.support.RequestContextUtils.saveOutputFlashMap(location, request, response);
                    }
                }
            }
        }
    }

}
