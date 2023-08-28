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

import java.util.Locale;
import java.util.Map;

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

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * A {@link HandlerInterceptor} that turns {@link HtmxResponse} instances
 * returned from controller methods into a
 *
 * @author Oliver Drotbohm
 */
class HtmxViewHandlerInterceptor implements HandlerInterceptor {
    private static final Logger LOG = LoggerFactory.getLogger(HtmxViewHandlerInterceptor.class);

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
        setTriggerHeader(HxTriggerLifecycle.RECEIVE, htmxResponse.getTriggers(), response);
        setTriggerHeader(HxTriggerLifecycle.SETTLE, htmxResponse.getTriggersAfterSettle(), response);
        setTriggerHeader(HxTriggerLifecycle.SWAP, htmxResponse.getTriggersAfterSwap(), response);

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
            response.setHeader(HtmxResponseHeader.HX_RESWAP.getValue(), htmxResponse.getReswap().getValue());
        }
    }

    private void setTriggerHeader(HxTriggerLifecycle triggerHeader, Map<String, String> triggers,
            HttpServletResponse response) {
        if (triggers.isEmpty()) {
            return;
        }
        if (triggers.size() == 1) {
            Map.Entry<String, String> singleHeader = triggers.entrySet().stream().findFirst().orElseThrow();
            if (singleHeader.getValue() == null || singleHeader.getValue().isBlank()) {
                response.setHeader(triggerHeader.getHeaderName(), singleHeader.getKey());
            } else {
                try {
                    response.setHeader(triggerHeader.getHeaderName(), objectMapper.writeValueAsString(triggers));
                } catch (Exception e) {
                    LOG.warn("Unable to set header {} to {}", triggerHeader.getHeaderName(), triggers, e);
                }
            }
        } else {
            try {
                response.setHeader(triggerHeader.getHeaderName(), objectMapper.writeValueAsString(triggers));
            } catch (Exception e) {
                LOG.warn("Unable to set header {} to {}", triggerHeader.getHeaderName(), triggers, e);
            }
        }
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
}
