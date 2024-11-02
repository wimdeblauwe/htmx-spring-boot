package io.github.wimdeblauwe.htmx.spring.boot.mvc;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;

/**
 * A specialization of {@link RedirectView} that can be used to signal htmx to perform a client-side redirect.
 * This View supports all the features of RedirectView e.g. exposing model attributes, flash attributes, etc.
 *
 * @see <a href="https://htmx.org/headers/hx-redirect/">HX-Redirect Response Header</a>
 * @since 3.6.0
 */
public class HtmxRedirectView extends RedirectView {

    /**
     * Create a new HtmxRedirectView.
     */
    public HtmxRedirectView() {
    }

    /**
     * Create a new HtmxRedirectView with the given URL.
     *
     * <p>The given URL will be considered as relative to the web server, not as relative to the current ServletContext.
     *
     * @param url the URL to redirect to
     * @see #HtmxRedirectView(String, boolean)
     */
    public HtmxRedirectView(String url) {
        super(url);
    }

    /**
     * Create a new HtmxRedirectView with the given URL.
     *
     * @param url             the URL to redirect to
     * @param contextRelative whether to interpret the given URL as relative to the current ServletContext
     */
    public HtmxRedirectView(String url, boolean contextRelative) {
        super(url, contextRelative);
    }

    /**
     * Create a new HtmxRedirectView with the given URL.
     *
     * @param url                   the URL to redirect to
     * @param contextRelative       whether to interpret the given URL as relative to the current ServletContext
     * @param exposeModelAttributes whether model attributes should be exposed as query parameters
     */
    public HtmxRedirectView(String url, boolean contextRelative, boolean exposeModelAttributes) {
        super(url, contextRelative, false, exposeModelAttributes);
    }

    @Override
    protected void sendRedirect(HttpServletRequest request, HttpServletResponse response, String targetUrl, boolean http10Compatible) throws IOException {

        String encodedURL = (isRemoteHost(targetUrl) ? targetUrl : response.encodeRedirectURL(targetUrl));
        response.setHeader(HtmxResponseHeader.HX_REDIRECT.getValue(), encodedURL);
    }

}
