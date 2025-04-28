package io.github.wimdeblauwe.htmx.spring.boot.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import java.io.IOException;


/**
 * Handles post-authentication navigation by delegating to the {@link SavedRequestAwareAuthenticationSuccessHandler},
 * using {@link HxLocationRedirectStrategy} to provide an HTMX-friendly redirect mechanism.
 * <p>
 * This class is not used by the library itself, but users of the library can use it to configure their security for
 * native htmx redirects.
 * <p>
 * For detailed information and an example of how to integrate this into the {@link SecurityFilterChain} bean
 * definition, see the <a href="https://github.com/lcnicolau/cs50-todo-list?tab=readme-ov-file#htmx-redirect-pattern">
 * HTMX Redirect Pattern</a>.
 *
 * @author LC Nicolau
 * @see HxLocationRedirectAccessDeniedHandler
 * @see HxLocationRedirectAuthenticationEntryPoint
 * @see HxLocationRedirectLogoutSuccessHandler
 */
public class HxLocationRedirectAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final AuthenticationSuccessHandler delegate;

    public HxLocationRedirectAuthenticationSuccessHandler(String defaultSuccessUrl) {
        this(defaultSuccessUrl, false);
    }

    public HxLocationRedirectAuthenticationSuccessHandler(String defaultSuccessUrl, boolean alwaysUse) {
        this(defaultSuccessUrl, alwaysUse, new HxLocationRedirectStrategy());
    }

    public HxLocationRedirectAuthenticationSuccessHandler(String defaultSuccessUrl, boolean alwaysUse, RedirectStrategy redirectStrategy) {
        var handler = new SavedRequestAwareAuthenticationSuccessHandler();
        handler.setDefaultTargetUrl(defaultSuccessUrl);
        handler.setAlwaysUseDefaultTargetUrl(alwaysUse);
        handler.setRedirectStrategy(redirectStrategy);
        this.delegate = handler;
    }


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        delegate.onAuthenticationSuccess(request, response, authentication);
    }

}
