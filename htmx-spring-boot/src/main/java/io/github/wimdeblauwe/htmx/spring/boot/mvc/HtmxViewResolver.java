package io.github.wimdeblauwe.htmx.spring.boot.mvc;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.lang.Nullable;
import org.springframework.web.context.support.WebApplicationObjectSupport;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Locale;

/**
 * A simple implementation of {@link org.springframework.web.servlet.ViewResolver}
 * that interprets a view name as htmx specific operations e.g. redirecting to a URL.
 *
 * @since 3.6.0
 */
public class HtmxViewResolver extends WebApplicationObjectSupport implements ViewResolver, Ordered {

    /**
     * Prefix for special view names that specify a redirect URL
     * that htmx should navigate to.
     */
    public static final String REDIRECT_URL_PREFIX = "redirect:htmx:";

    /**
     * Prefix for special view names that specify a redirect URL that
     * htmx should navigate to without a full page reload.
     */
    public static final String LOCATION_URL_PREFIX = "redirect:htmx:location:";

    /**
     * Prefix for special view names that specify a refresh of the current page.
     */
    public static final String REFRESH_VIEW_NAME = "refresh:htmx";

    private int order = Ordered.LOWEST_PRECEDENCE;

    private boolean redirectContextRelative = true;

    @Nullable
    private String[] redirectHosts;

    @Override
    public int getOrder() {
        return order;
    }

    /**
     * Return the configured application hosts for redirect purposes.
     */
    @Nullable
    public String[] getRedirectHosts() {
        return this.redirectHosts;
    }

    @Override
    public View resolveViewName(String viewName, Locale locale) throws Exception {

        if (viewName.equals(REFRESH_VIEW_NAME)) {
            return new HtmxRefreshView();
        }

        if (viewName.startsWith(LOCATION_URL_PREFIX)) {
            String redirectUrl = viewName.substring(LOCATION_URL_PREFIX.length());
            RedirectView view = new HtmxLocationRedirectView(redirectUrl, isRedirectContextRelative());
            String[] hosts = getRedirectHosts();
            if (hosts != null) {
                view.setHosts(hosts);
            }
            return view;
        }

        if (viewName.startsWith(REDIRECT_URL_PREFIX)) {
            String redirectUrl = viewName.substring(REDIRECT_URL_PREFIX.length());
            RedirectView view = new HtmxRedirectView(redirectUrl, isRedirectContextRelative());
            String[] hosts = getRedirectHosts();
            if (hosts != null) {
                view.setHosts(hosts);
            }
            return view;
        }

        return null;
    }

    /**
     * Specify the order value for this ViewResolver bean.
     * <p>The default value is {@code Ordered.LOWEST_PRECEDENCE}, meaning non-ordered.
     *
     * @see org.springframework.core.Ordered#getOrder()
     */
    public void setOrder(int order) {
        this.order = order;
    }

    /**
     * Set whether to interpret a given redirect URL that starts with a
     * slash ("/") as relative to the current ServletContext, i.e. as
     * relative to the web application root.
     *
     * <p>Default is {@code true}: A redirect URL that starts with a slash will be
     * interpreted as relative to the web application root, i.e. the context
     * path will be prepended to the URL.
     *
     * @see HtmxRedirectView#setContextRelative
     * @see HtmxLocationRedirectView#setContextRelative
     * @see #REDIRECT_URL_PREFIX
     */
    public void setRedirectContextRelative(boolean redirectContextRelative) {
        this.redirectContextRelative = redirectContextRelative;
    }

    /**
     * Configure one or more hosts associated with the application.
     * All other hosts will be considered external hosts.
     *
     * <p>In effect, this property provides a way turn off encoding on redirect
     * via {@link HttpServletResponse#encodeRedirectURL} for URLs that have a
     * host and that host is not listed as a known host.
     *
     * <p>If not set (the default) all URLs are encoded through the response.
     *
     * @param redirectHosts one or more application hosts
     */
    public void setRedirectHosts(@Nullable String... redirectHosts) {
        this.redirectHosts = redirectHosts;
    }

    /**
     * Return whether to interpret a given redirect URL that starts with a
     * slash ("/") as relative to the current ServletContext, i.e. as
     * relative to the web application root.
     */
    protected boolean isRedirectContextRelative() {
        return this.redirectContextRelative;
    }

}
