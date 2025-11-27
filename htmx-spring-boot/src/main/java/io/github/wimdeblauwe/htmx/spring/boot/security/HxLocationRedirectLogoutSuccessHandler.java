package io.github.wimdeblauwe.htmx.spring.boot.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;

import java.io.IOException;

/**
 * Handles post-logout navigation by delegating to the {@link SimpleUrlLogoutSuccessHandler}, using
 * {@link HxLocationRedirectStrategy} to provide an htmx-friendly redirect mechanism.
 * <p>
 * This class is not used by the library itself, but users of the library can use it to configure their security for
 * native htmx redirects.
 *
 * @author LC Nicolau
 * @since 5.0.0
 */
public class HxLocationRedirectLogoutSuccessHandler implements LogoutSuccessHandler {

    private final LogoutSuccessHandler delegate;

    public HxLocationRedirectLogoutSuccessHandler(String logoutSuccessUrl) {
        this(logoutSuccessUrl, false);
    }

    public HxLocationRedirectLogoutSuccessHandler(String logoutSuccessUrl, boolean alwaysUse) {
        this(logoutSuccessUrl, alwaysUse, new HxLocationRedirectStrategy());
    }

    public HxLocationRedirectLogoutSuccessHandler(String logoutSuccessUrl, RedirectStrategy redirectStrategy) {
        this(logoutSuccessUrl, false, redirectStrategy);
    }

    public HxLocationRedirectLogoutSuccessHandler(String logoutSuccessUrl, boolean alwaysUse, RedirectStrategy redirectStrategy) {
        var handler = new SimpleUrlLogoutSuccessHandler();
        handler.setDefaultTargetUrl(logoutSuccessUrl);
        handler.setAlwaysUseDefaultTargetUrl(alwaysUse);
        handler.setRedirectStrategy(redirectStrategy);
        this.delegate = handler;
    }

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        delegate.onLogoutSuccess(request, response, authentication);
    }

}
