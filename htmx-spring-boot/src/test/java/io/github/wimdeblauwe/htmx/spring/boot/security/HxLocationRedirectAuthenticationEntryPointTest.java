package io.github.wimdeblauwe.htmx.spring.boot.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.AuthenticationException;

import java.io.IOException;

import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class HxLocationRedirectAuthenticationEntryPointTest {

    private static final String REDIRECT_URL = "/login?unauthorized";


    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private AuthenticationException exception;


    @Test
    void commence() throws ServletException, IOException {
        try (var mocked = mockConstruction(HxLocationRedirectStrategy.class)) {
            var handler = new HxLocationRedirectAuthenticationEntryPoint(REDIRECT_URL);
            var delegate = mocked.constructed().get(0);

            handler.commence(request, response, exception);
            verify(delegate).sendRedirect(request, response, REDIRECT_URL);
        }
    }

}
