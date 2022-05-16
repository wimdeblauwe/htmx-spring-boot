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

import io.github.wimdeblauwe.hsbt.mvc.HtmxPartials.Partial;

import java.io.PrintWriter;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.util.Assert;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;

/**
 * A {@link HandlerInterceptor} that turns {@link HtmxPartials} instances returned from controller methods into a
 *
 * @author Oliver Drotbohm
 */
class HtmxViewHandlerInterceptor implements HandlerInterceptor {

  private final ViewResolver views;
  private final ObjectFactory<LocaleResolver> locales;

	public HtmxViewHandlerInterceptor(ViewResolver views, ObjectFactory<LocaleResolver> locales) {
		this.views = views;
		this.locales = locales;
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

		if (!method.getReturnType().getParameterType().equals(HtmxPartials.class)) {
			return;
		}

		Object attribute = modelAndView.getModel().get("htmxPartials");

		if (!HtmxPartials.class.isInstance(attribute)) {
			return;
		}

		HtmxPartials streams = (HtmxPartials) attribute;

		modelAndView.setView(toView(streams));
	}
	
	private View toView(HtmxPartials partials) {

		Assert.notNull(partials, "HtmxPartials must not be null!");

		return (model, request, response) -> {

			Locale locale = locales.getObject().resolveLocale(request);
			PrintWriter writer = response.getWriter();

			for (Partial partial : partials.toIterable()) {

				writer.write(partial.openWrapper());

				if (!partial.isRemove()) {
					views.resolveViewName(partial.getTemplate(), locale)
							.render(model, request, response);
				}

				writer.write(partial.closeWrapper());
			}
		};
	}
}
