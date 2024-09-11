package io.github.wimdeblauwe.htmx.spring.boot.jte.endpoint;

import gg.jte.TemplateEngine;
import gg.jte.html.policy.*;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JteConfig {
  final
  TemplateEngine templateEngine;

  public JteConfig(TemplateEngine templateEngine) {
    this.templateEngine = templateEngine;
  }

  @PostConstruct
  void configureTemplateEngine() {
    templateEngine.setHtmlPolicy(new JtePolicy());
  }

  static class JtePolicy extends PolicyGroup {

    JtePolicy() {
      addPolicy(new PreventUppercaseTagsAndAttributes());
      addPolicy(new PreventOutputInTagsAndAttributes(false));
      addPolicy(new PreventUnquotedAttributes());
      addPolicy(new PreventInvalidAttributeNames());
    }
  }
}
