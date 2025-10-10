package io.github.wimdeblauwe.htmx.spring.boot.endpoint;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.support.RouterFunctionMapping;

@AutoConfiguration
@EnableConfigurationProperties(HtmxEndpointConfig.HtmxEndpointProperties.class)
public class HtmxEndpointConfig {

  private final Logger logger = LoggerFactory.getLogger(
      io.github.wimdeblauwe.htmx.spring.boot.endpoint.HtmxEndpointConfig.class);

  private final ApplicationContext applicationContext;
  private final RouterFunctionMapping routerFunctionMapping;
  private final HtmxEndpointProperties htmxEndpointProperties;

  public HtmxEndpointConfig(ApplicationContext applicationContext,
      RouterFunctionMapping routerFunctionMapping,
      HtmxEndpointProperties htmxEndpointProperties) {
    this.applicationContext = applicationContext;
    this.routerFunctionMapping = routerFunctionMapping;
    this.htmxEndpointProperties = htmxEndpointProperties;
  }

  @ConfigurationProperties("htmx.endpoint")
  public static class HtmxEndpointProperties {

    private final List<Class<? extends Annotation>> annotationClassesToScan;

    public HtmxEndpointProperties(List<Class<? extends Annotation>> annotationClassesToScan) {
      if (annotationClassesToScan == null || annotationClassesToScan.isEmpty()) {
        this.annotationClassesToScan = Collections.singletonList(Controller.class);
      } else {
        this.annotationClassesToScan = annotationClassesToScan;
      }
    }

    public List<Class<? extends Annotation>> annotationClassesToScan() {
      return annotationClassesToScan;
    }
  }


  @Bean
  ApplicationRunner applicationRunner() {
    return args -> {
      Map<String, Object> beans = new HashMap<>();
      for (Class<? extends Annotation> aClass : htmxEndpointProperties.annotationClassesToScan()) {
        beans.putAll(applicationContext.getBeansWithAnnotation(aClass));
      }
      beans.values().forEach(bean ->
          {
            List<Field> fieldList = Arrays.stream(bean.getClass().getDeclaredFields())
                .filter(method -> AbstractHtmxEndpoint.class.isAssignableFrom(method.getType()))
                .toList();

            fieldList.forEach(field -> {
              AbstractHtmxEndpoint<?, ?> function = (AbstractHtmxEndpoint<?, ?>) ReflectionUtils.getField(field, bean);
              if (function == null) {
                throw new RuntimeException("Router function could not be found");
              }
              logger.info("Add endpoint: {}, with method: {}", function.path, function.method);
              if (routerFunctionMapping.getRouterFunction() == null) {
                routerFunctionMapping.setRouterFunction(function);
              } else {
                RouterFunction<?> routerFunction = routerFunctionMapping.getRouterFunction().andOther(function);
                routerFunctionMapping.setRouterFunction(routerFunction);
              }
            });
          }
      );
      System.out.println(routerFunctionMapping.getRouterFunction());
    };
  }
}
