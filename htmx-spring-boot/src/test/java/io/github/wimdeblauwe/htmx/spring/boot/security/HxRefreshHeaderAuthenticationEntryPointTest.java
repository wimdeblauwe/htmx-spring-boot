package io.github.wimdeblauwe.htmx.spring.boot.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;

import java.io.IOException;

import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class HxRefreshHeaderAuthenticationEntryPointTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private AuthenticationException exception;


    @Test
    void commence() throws ServletException, IOException {
        try (var mocked = mockConstruction(Http403ForbiddenEntryPoint.class)) {
            var handler = new HxRefreshHeaderAuthenticationEntryPoint();
            var delegate = mocked.constructed().get(0);

            handler.commence(request, response, exception);
            verify(response).setHeader("HX-Refresh", "true");
            verify(delegate).commence(request, response, exception);
        }
    }

}
