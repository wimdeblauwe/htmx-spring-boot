package io.github.wimdeblauwe.htmx.spring.boot.mvc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.util.Assert;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.stream.Collectors;

public class HtmxResponseHelper {
    private final ViewResolver views;
    private final ObjectFactory<LocaleResolver> locales;
    private final ObjectMapper objectMapper;

    public HtmxResponseHelper(ViewResolver views,
                              ObjectFactory<LocaleResolver> locales,
                              ObjectMapper objectMapper) {
        this.views = views;
        this.locales = locales;
        this.objectMapper = objectMapper;
    }

    public View toView(HtmxResponse htmxResponse) {

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

    public void addHxHeaders(HtmxResponse htmxResponse, HttpServletResponse response) {
        addHxTriggerHeaders(response, HtmxResponseHeader.HX_TRIGGER, htmxResponse.getTriggersInternal());
        addHxTriggerHeaders(response, HtmxResponseHeader.HX_TRIGGER_AFTER_SETTLE, htmxResponse.getTriggersAfterSettleInternal());
        addHxTriggerHeaders(response, HtmxResponseHeader.HX_TRIGGER_AFTER_SWAP, htmxResponse.getTriggersAfterSwapInternal());

        if (htmxResponse.getLocation() != null) {
            if (htmxResponse.getLocation().hasContextData()) {
                setHeaderJsonValue(response, HtmxResponseHeader.HX_LOCATION.getValue(), htmxResponse.getLocation());
            } else {
                response.setHeader(HtmxResponseHeader.HX_LOCATION.getValue(), htmxResponse.getLocation().getPath());
            }
        }
        if (htmxResponse.getReplaceUrl() != null) {
            response.setHeader(HtmxResponseHeader.HX_REPLACE_URL.getValue(), htmxResponse.getReplaceUrl());
        }
        if (htmxResponse.getPushUrl() != null) {
            response.setHeader(HtmxResponseHeader.HX_PUSH_URL.getValue(), htmxResponse.getPushUrl());
        }
        if (htmxResponse.getRedirect() != null) {
            response.setHeader(HtmxResponseHeader.HX_REDIRECT.getValue(), htmxResponse.getRedirect());
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
}
