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

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * A {@link HandlerInterceptor} that turns {@link HtmxResponse} instances
 * returned from controller methods into a
 *
 * @author Oliver Drotbohm
 * @author Sascha Woo
 */
class HtmxViewHandlerInterceptor implements HandlerInterceptor {

    private final HtmxResponseHelper responseHelper;

    public HtmxViewHandlerInterceptor(HtmxResponseHelper responseHelper) {
        this.responseHelper = responseHelper;
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

        modelAndView.setView(responseHelper.toView(htmxResponse));

        responseHelper.addHxHeaders(htmxResponse, response);
    }
}
