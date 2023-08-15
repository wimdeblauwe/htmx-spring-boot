package io.github.wimdeblauwe.htmx.spring.boot.mvc.support;

import org.hamcrest.Matcher;
import org.springframework.lang.Nullable;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.util.XpathExpectationsHelper;
import org.springframework.test.web.servlet.ResultMatcher;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPathExpressionException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Factory for assertions on the response content using XPath expressions when using HtmxPartials
 *
 * <p>An instance of this class is typically accessed via
 * {@link PartialXpathResultMatchers#partialXpath}.
 *
 */
public class PartialXpathResultMatchers {

    private final XpathExpectationsHelper xpathHelper;

    public static PartialXpathResultMatchers partialXpath(String expression, Object... args) throws XPathExpressionException {
        return new PartialXpathResultMatchers("//htmxPartial"+expression, null, args);
    }


    /**
     * Protected constructor, not for direct instantiation. Use
     * {@link PartialXpathResultMatchers#partialXpath(String, Object...)} or
     * {@link PartialXpathResultMatchers#partialXpath(String, Map, Object...)}.
     * @param expression the XPath expression
     * @param namespaces the XML namespaces referenced in the XPath expression, or {@code null}
     * @param args arguments to parameterize the XPath expression with using the
     * formatting specifiers defined in {@link String#format(String, Object...)}
     */
    protected PartialXpathResultMatchers(String expression, @Nullable Map<String, String> namespaces, Object ... args)
            throws XPathExpressionException {

        this.xpathHelper = new XpathExpectationsHelper(expression, namespaces, args);
    }

    private byte[] wrapContentWithHtmxRoot(MockHttpServletResponse response) throws UnsupportedEncodingException {
        String wrappedContent = "<htmxPartial>"+response.getContentAsString()+"</htmxPartial>";
        return wrappedContent.getBytes();
    }


    /**
     * Evaluate the XPath and assert the {@link Node} content found with the
     * given Hamcrest {@link Matcher}.
     */
    public ResultMatcher node(Matcher<? super Node> matcher) {
        return result -> {
            MockHttpServletResponse response = result.getResponse();
            this.xpathHelper.assertNode(wrapContentWithHtmxRoot(response), getDefinedEncoding(response), matcher);
        };
    }

    /**
     * Evaluate the XPath and assert the {@link NodeList} content found with the
     * given Hamcrest {@link Matcher}.
     * @since 5.2.2
     */
    public ResultMatcher nodeList(Matcher<? super NodeList> matcher) {
        return result -> {
            MockHttpServletResponse response = result.getResponse();
            this.xpathHelper.assertNodeList(wrapContentWithHtmxRoot(response), getDefinedEncoding(response), matcher);
        };
    }

    /**
     * Get the response encoding if explicitly defined in the response, {code null} otherwise.
     */
    @Nullable
    private String getDefinedEncoding(MockHttpServletResponse response) {
        return (response.isCharset() ? response.getCharacterEncoding() : null);
    }

    /**
     * Evaluate the XPath and assert that content exists.
     */
    public ResultMatcher exists() {
        return result -> {
            MockHttpServletResponse response = result.getResponse();
            this.xpathHelper.exists(wrapContentWithHtmxRoot(response), getDefinedEncoding(response));
        };
    }

    /**
     * Evaluate the XPath and assert that content doesn't exist.
     */
    public ResultMatcher doesNotExist() {
        return result -> {
            MockHttpServletResponse response = result.getResponse();
            this.xpathHelper.doesNotExist(wrapContentWithHtmxRoot(response), getDefinedEncoding(response));
        };
    }

    /**
     * Evaluate the XPath and assert the number of nodes found with the given
     * Hamcrest {@link Matcher}.
     */
    public ResultMatcher nodeCount(Matcher<? super Integer> matcher) {
        return result -> {
            MockHttpServletResponse response = result.getResponse();
            this.xpathHelper.assertNodeCount(wrapContentWithHtmxRoot(response), getDefinedEncoding(response), matcher);
        };
    }

    /**
     * Evaluate the XPath and assert the number of nodes found.
     */
    public ResultMatcher nodeCount(int expectedCount) {
        return result -> {
            MockHttpServletResponse response = result.getResponse();
            this.xpathHelper.assertNodeCount(wrapContentWithHtmxRoot(response), getDefinedEncoding(response), expectedCount);
        };
    }

    /**
     * Apply the XPath and assert the {@link String} value found with the given
     * Hamcrest {@link Matcher}.
     */
    public ResultMatcher string(Matcher<? super String> matcher) {
        return result -> {
            MockHttpServletResponse response = result.getResponse();
            this.xpathHelper.assertString(wrapContentWithHtmxRoot(response), getDefinedEncoding(response), matcher);
        };
    }

    /**
     * Apply the XPath and assert the {@link String} value found.
     */
    public ResultMatcher string(String expectedValue) {
        return result -> {
            MockHttpServletResponse response = result.getResponse();
            this.xpathHelper.assertString(response.getContentAsByteArray(), getDefinedEncoding(response), expectedValue);
        };
    }

    /**
     * Evaluate the XPath and assert the {@link Double} value found with the
     * given Hamcrest {@link Matcher}.
     */
    public ResultMatcher number(Matcher<? super Double> matcher) {
        return result -> {
            MockHttpServletResponse response = result.getResponse();
            this.xpathHelper.assertNumber(response.getContentAsByteArray(), getDefinedEncoding(response), matcher);
        };
    }

    /**
     * Evaluate the XPath and assert the {@link Double} value found.
     */
    public ResultMatcher number(Double expectedValue) {
        return result -> {
            MockHttpServletResponse response = result.getResponse();
            this.xpathHelper.assertNumber(response.getContentAsByteArray(), getDefinedEncoding(response), expectedValue);
        };
    }

    /**
     * Evaluate the XPath and assert the {@link Boolean} value found.
     */
    public ResultMatcher booleanValue(Boolean value) {
        return result -> {
            MockHttpServletResponse response = result.getResponse();
            this.xpathHelper.assertBoolean(response.getContentAsByteArray(), getDefinedEncoding(response), value);
        };
    }

}
