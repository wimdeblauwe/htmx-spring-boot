package io.github.wimdeblauwe.htmx.spring.boot.thymeleaf;

import org.jspecify.annotations.Nullable;
import org.thymeleaf.dialect.AbstractProcessorDialect;
import org.thymeleaf.dialect.IExpressionObjectDialect;
import org.thymeleaf.expression.IExpressionObjectFactory;
import org.thymeleaf.processor.IProcessor;
import tools.jackson.databind.json.JsonMapper;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

public class HtmxDialect extends AbstractProcessorDialect implements IExpressionObjectDialect {

    private static final Set<HtmxAttribute> CORE_ATTRIBUTES;
    private static final Set<HtmxAttribute> EXTENSION_ATTRIBUTES;

    static {
        CORE_ATTRIBUTES = Set.of(
                new HtmxAttribute("action"),
                new HtmxAttribute("boost"),
                new HtmxAttribute("config"),
                new HtmxAttribute("confirm"),
                new HtmxAttribute("delete", true),
                new HtmxAttribute("disable"),
                new HtmxAttribute("encoding"),
                new HtmxAttribute("get"),
                new HtmxAttribute("headers"),
                new HtmxAttribute("history-elt"),
                new HtmxAttribute("ignore"),
                new HtmxAttribute("include"),
                new HtmxAttribute("indicator"),
                new HtmxAttribute("method", value -> value.equals("post") || value.equals("put") || value.equals("delete") || value.equals("patch")),
                new HtmxAttribute("morph-skip"),
                new HtmxAttribute("morph-skip-children"),
                new HtmxAttribute("on"),
                new HtmxAttribute("patch", true),
                new HtmxAttribute("post", true),
                new HtmxAttribute("preserve"),
                new HtmxAttribute("push-url"),
                new HtmxAttribute("put", true),
                new HtmxAttribute("query"),
                new HtmxAttribute("replace-url"),
                new HtmxAttribute("select"),
                new HtmxAttribute("select-oob"),
                new HtmxAttribute("status"),
                new HtmxAttribute("swap"),
                new HtmxAttribute("swap-oob"),
                new HtmxAttribute("sync"),
                new HtmxAttribute("target"),
                new HtmxAttribute("trigger"),
                new HtmxAttribute("validate"),
                new HtmxAttribute("vals"));

        EXTENSION_ATTRIBUTES = Set.of(
                new HtmxAttribute("browser-indicator"),
                new HtmxAttribute("head"),
                new HtmxAttribute("nonce"),
                new HtmxAttribute("live"),
                new HtmxAttribute("multipart:close"),
                new HtmxAttribute("multipart:connect"),
                new HtmxAttribute("pending"),
                new HtmxAttribute("preload"),
                new HtmxAttribute("prompt"),
                new HtmxAttribute("ptag"),
                new HtmxAttribute("sse:close"),
                new HtmxAttribute("sse:connect"),
                new HtmxAttribute("targets"),
                new HtmxAttribute("ws:close"),
                new HtmxAttribute("ws:connect"));
    }

    private final Set<HtmxAttribute> customAttributes = new HashSet<>(0);
    private @Nullable HtmxExpressionObjectFactory expressionObjectFactory;
    private final JsonMapper jsonMapper;

    /**
     * Creates a new instance of the dialect.
     *
     * @param jsonMapper the JSON mapper to use.
     */
    public HtmxDialect(JsonMapper jsonMapper) {
        super("Htmx", "hx", 1000);
        this.jsonMapper = jsonMapper;
    }

    /**
     * Registers a custom attribute to the dialect.
     *
     * @param name the name of the attribute.
     * @return this dialect.
     */
    public HtmxDialect registerCustomAttribute(String name) {
        registerCustomAttribute(new HtmxAttribute(name));
        return this;
    }

    /**
     * Registers a custom CSRF-aware attribute, which adds the CSRF token to the element through {@code hx-headers}.
     *
     * @param name the name of the attribute.
     * @return this dialect.
     * @see <a href="https://docs.spring.io/spring-security/reference/servlet/exploits/csrf.html">Cross Site Request Forgery (CSRF)</a>
     */
    public HtmxDialect registerCustomCsrfAttribute(String name) {
        registerCustomAttribute(new HtmxAttribute(name, true));
        return this;
    }

    /**
     * Registers a custom conditional CSRF-aware attribute, which adds the CSRF token to the element through {@code hx-headers},
     * but only if the value of the attribute meets the condition.
     *
     * @param name               the name of the attribute.
     * @param csrfTokenCondition a condition that determines whether the CSRF token should be rendered based on the value of the attribute.
     * @return this dialect.
     * @see <a href="https://docs.spring.io/spring-security/reference/servlet/exploits/csrf.html">Cross Site Request Forgery (CSRF)</a>
     */
    public HtmxDialect registerCustomCsrfAttribute(String name, CsrfTokenCondition csrfTokenCondition) {
        registerCustomAttribute(new HtmxAttribute(name, csrfTokenCondition));
        return this;
    }

    private void registerCustomAttribute(HtmxAttribute attribute) {

        Stream<HtmxAttribute> existingAttributes = Stream.concat(Stream.concat(CORE_ATTRIBUTES.stream(), EXTENSION_ATTRIBUTES.stream()), customAttributes.stream());
        if (existingAttributes.anyMatch(a -> a.name().equals(attribute.name()))) {
            throw new IllegalArgumentException("An attribute with name '" + attribute.name() + "' is already registered.");
        }
        customAttributes.add(attribute);
    }

    @Override
    public Set<IProcessor> getProcessors(String dialectPrefix) {

        Set<IProcessor> htmxProcessors = new HashSet<>();

        var attributes = new HashSet<HtmxAttribute>(CORE_ATTRIBUTES.size() + EXTENSION_ATTRIBUTES.size() + customAttributes.size());
        attributes.addAll(CORE_ATTRIBUTES);
        attributes.addAll(EXTENSION_ATTRIBUTES);
        attributes.addAll(customAttributes);

        for (HtmxAttribute attribute : attributes) {
            if (attribute.csrfTokenCondition() != null) {
                htmxProcessors.add(new HtmxCsrfAttributeProcessor(dialectPrefix, attribute.name(), attribute.csrfTokenCondition(), jsonMapper));
            } else {
                htmxProcessors.add(new HtmxAttributeProcessor(dialectPrefix, attribute.name(), jsonMapper));
            }
        }

        return htmxProcessors;
    }

    @Override
    public IExpressionObjectFactory getExpressionObjectFactory() {
        if (this.expressionObjectFactory == null) {
            this.expressionObjectFactory = new HtmxExpressionObjectFactory();
        }
        return this.expressionObjectFactory;
    }

    /**
     * Represents an attribute used within the Htmx dialect for Thymeleaf integration.
     *
     * <p>An {@code HtmxAttribute} can optionally include a {@link CsrfTokenCondition},
     * which determines whether a CSRF token should be rendered via {@code hx-headers}
     * based on the attribute's value.
     */
    record HtmxAttribute(String name, @Nullable CsrfTokenCondition csrfTokenCondition) {

        public HtmxAttribute(String name) {
            this(name, null);
        }

        public HtmxAttribute(String name, boolean withCsrfToken) {
            this(name, withCsrfToken ? value -> true : null);
        }

    }

}
