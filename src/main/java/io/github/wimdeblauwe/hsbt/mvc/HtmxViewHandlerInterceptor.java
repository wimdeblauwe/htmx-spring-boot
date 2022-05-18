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
package io.github.wimdeblauwe.hsbt.mvc;

import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.Assert;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;

/**
 * A {@link HandlerInterceptor} that turns {@link HtmxResponse} instances returned from controller methods into a
 *
 * @author Oliver Drotbohm
 */
class HtmxViewHandlerInterceptor implements HandlerInterceptor {
    private static final Logger LOG = LoggerFactory.getLogger(HtmxViewHandlerInterceptor.class);

    private final ViewResolver views;
    private final ObjectFactory<LocaleResolver> locales;
    private final ObjectMapper objectMapper;

    public HtmxViewHandlerInterceptor(ViewResolver views, ObjectFactory<LocaleResolver> locales, ObjectMapper objectMapper) {
        this.views = views;
        this.locales = locales;
        this.objectMapper = objectMapper;
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.web.servlet.HandlerInterceptor#postHandle(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object, org.springframework.web.servlet.ModelAndView)
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

        if(htmxResponse.getHeaderPushHistory() != null) {
            response.setHeader("HX-Push", htmxResponse.getHeaderPushHistory());
        }
        if(htmxResponse.getHeaderRedirect() != null) {
            response.setHeader("HX-Redirect", htmxResponse.getHeaderRedirect());
        }
        if(htmxResponse.getHeaderRefresh()) {
            response.setHeader("HX-Refresh", "true");
        }
        if(htmxResponse.getHeaderRetarget() != null) {
            response.setHeader("HX-Retarget", htmxResponse.getHeaderRetarget());
        }
    }

    private void setTriggerHeader(HxTriggerLifecycle triggerHeader, Map<String, String> triggers, HttpServletResponse response) {
        if(triggers.isEmpty()) {
            return;
        }
        if(triggers.size() == 1) {
            Map.Entry<String, String> singleHeader = triggers.entrySet().stream().findFirst().orElseThrow();
            if(singleHeader.getValue() == null || singleHeader.getValue().isBlank()) {
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
            for (String template : partials.getTemplates()) {
                views.resolveViewName(template, locale)
                     .render(model, request, response);

            }
        };
    }
}
