/*
 * Copyright 2021-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.wimdeblauwe.htmx.spring.boot.mvc;

import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.util.Assert;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.util.ContentCachingResponseWrapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * A {@link HandlerInterceptor} that turns {@link HtmxResponse} instances
 * returned from controller methods into a
 *
 * @author Oliver Drotbohm
 * @author Sascha Woo
 */
class HtmxViewHandlerInterceptor implements HandlerInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(HtmxViewHandlerInterceptor.class);

    private final ViewResolver views;
    private final ObjectFactory<LocaleResolver> locales;
    private final ObjectMapper objectMapper;

    public HtmxViewHandlerInterceptor(ViewResolver views, ObjectFactory<LocaleResolver> locales,
            ObjectMapper objectMapper) {
        this.views = views;
        this.locales = locales;
        this.objectMapper = objectMapper;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.springframework.web.servlet.HandlerInterceptor#postHandle(javax.servlet.
     * http.HttpServletRequest, javax.servlet.http.HttpServletResponse,
     * java.lang.Object, org.springframework.web.servlet.ModelAndView)
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
           ModelAndView modelAndView) throws Exception {

        if (modelAndView == null || !HandlerMethod.class.isInstance(handler)) {
            return;
        }

        HandlerMethod method = (HandlerMethod) handler;

        String partialsAttributeName = null;
        if (method.getReturnType().getParameterType().equals(HtmxResponse.class)) {
            partialsAttributeName = "htmxResponse";
        }
        if (partialsAttributeName == null) {
            return;
        }

        Object attribute = modelAndView.getModel().get(partialsAttributeName);

        if (!HtmxResponse.class.isInstance(attribute)) {
            return;
        }

        HtmxResponse htmxResponse = (HtmxResponse) attribute;

        modelAndView.setView(toView(htmxResponse));

        addHxHeaders(htmxResponse, response);
    }

    private void addHxHeaders(HtmxResponse htmxResponse, HttpServletResponse response) {
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

    private View toView(HtmxResponse partials) {

        Assert.notNull(partials, "HtmxPartials must not be null!");

        return (model, request, response) -> {
            Locale locale = locales.getObject().resolveLocale(request);
            ContentCachingResponseWrapper wrapper = new ContentCachingResponseWrapper(response);
            for (ModelAndView template : partials.getTemplates()) {
                View view = template.getView();
                if (view == null) {
                    view = views.resolveViewName(template.getViewName(), locale);
                }
                for (String key: model.keySet()) {
                    if(!template.getModel().containsKey(key)) {
                        template.getModel().put(key, model.get(key));
                    }
                }
                Assert.notNull(view, "Template '" + template + "' could not be resolved");
                view.render(template.getModel(), request, wrapper);
            }
            wrapper.copyBodyToResponse();
        };
    }

    private void setHeaderJsonValue(HttpServletResponse response, String name, Object value) {
        try {
            response.setHeader(name, objectMapper.writeValueAsString(value));
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Unable to set header " + name + " to " + value, e);
        }
    }

}
