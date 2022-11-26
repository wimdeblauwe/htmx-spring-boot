package io.github.wimdeblauwe.hsbt.security;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * This class is not used by the library itself, but users of the library can use
 * it to configure their security to have full page refreshes on auth failures.
 *
 * @see <a href="https://www.wimdeblauwe.com/blog/2022/10/04/htmx-authentication-error-handling/">htmx-authentication-error-handling</a>
 */
public class HxRefreshHeaderAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final AuthenticationEntryPoint forbiddenEntryPoint;

    public HxRefreshHeaderAuthenticationEntryPoint() {
        this.forbiddenEntryPoint = new Http403ForbiddenEntryPoint();
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.addHeader("HX-Refresh", "true");
        forbiddenEntryPoint.commence(request, response, authException);
    }
}
