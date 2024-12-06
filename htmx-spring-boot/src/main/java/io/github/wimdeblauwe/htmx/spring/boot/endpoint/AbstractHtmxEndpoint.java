package io.github.wimdeblauwe.htmx.spring.boot.endpoint;

import jakarta.servlet.ServletException;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.function.*;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class AbstractHtmxEndpoint<T, R> implements RouterFunction<ServerResponse> {

    public final String path;
    public final HttpMethod method;
    private final Supplier<R> supplier;
    private final Function<T, R> function;
    private final Class<T> tClass;

    public AbstractHtmxEndpoint(String path, HttpMethod method, Function<T, R> function, T... tClass) {
        this.path = path;
        this.method = method;
        this.function = function;
        this.tClass = getClassOf(tClass);
        this.supplier = null;

    }

    static <T> Class<T> getClassOf(T[] array) {
        return (Class<T>) array.getClass().getComponentType();
    }

    public AbstractHtmxEndpoint(String path, HttpMethod method, Supplier<R> supplier) {
        this.path = path;
        this.method = method;
        this.supplier = supplier;
        this.function = null;
        this.tClass = null;
    }

    @Override
    @NonNull
    public Optional<HandlerFunction<ServerResponse>> route(@NonNull ServerRequest request) {
        RequestPredicate predicate = RequestPredicates.method(method).and(RequestPredicates.path(path));
        if (predicate.test(request)) {
            R model = getBody(request);
            if (model instanceof ModelAndView modelAndView) {
                Map<String, Object> modelData = modelAndView.getModel();
                return Optional.of(
                        req -> RenderingResponse.create(templateName(model))
                                                .modelAttributes(modelData)
                                                .build()
                );
            } else {
                return Optional.of(
                        req -> RenderingResponse.create(templateName(model))
                                                .modelAttribute(model)
                                                .build());
            }
        }
        return Optional.empty();
    }

    abstract String templateName(R modelAndView);

    private R getBody(ServerRequest req) {
        if (function == null && supplier != null) {
            return supplier.get();
        }
        try {
            if (function != null) {
                MediaType mediaType = req.headers().contentType().orElse(MediaType.APPLICATION_OCTET_STREAM);
                if(mediaType.includes(MediaType.APPLICATION_FORM_URLENCODED)){
                    T formData = req.bind(tClass);
                    return function.apply(
                            formData
                    );
                }
                T body = req.body(tClass);
                return function.apply(
                        body
                );
            }
        } catch (ServletException | IOException e) {
            throw new RuntimeException(e);
        } catch (BindException e) {
            throw new RuntimeException(e);
        }
        throw new RuntimeException("Failed to get body");
    }

    public String call() {
        return "hx-" + method.name().toLowerCase() + " =\"" + path + "\"";
    }

}
