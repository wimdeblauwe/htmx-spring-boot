package io.github.wimdeblauwe.htmx.spring.boot.endpoint;

import org.springframework.http.HttpMethod;

import java.util.function.Function;
import java.util.function.Supplier;

import org.springframework.web.servlet.ModelAndView;

public class HtmxEndpoint<T, S> extends AbstractHtmxEndpoint<T, S> {

    public HtmxEndpoint(String path, HttpMethod method,
                        Supplier<S> supplier) {
        super(path, method, supplier);
    }

    @Override
    String templateName(S responseType) {
        if(responseType instanceof String viewName){
            return viewName;
        } else if (responseType instanceof ModelAndView modelAndView) {
            return modelAndView.getViewName();
        }
        throw new IllegalArgumentException("HtmxEndpoint doesnt support" + responseType.getClass());
    }

    public HtmxEndpoint(String path, HttpMethod method, Function<T, S> function, T... tClass) {
        super(path, method, function, tClass);
    }
}
