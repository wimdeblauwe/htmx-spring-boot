package io.github.wimdeblauwe.htmx.spring.boot.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class HxLocationRedirectAuthenticationFailureHandlerTest {

    private static final String REDIRECT_URL = "/login?failure";


    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private AuthenticationException exception;


    @Test
    void onAuthenticationFailure() throws ServletException, IOException {
        try (var mocked = mockConstruction(SimpleUrlAuthenticationFailureHandler.class)) {
            var handler = new HxLocationRedirectAuthenticationFailureHandler(REDIRECT_URL);
            var delegate = mocked.constructed().get(0);
            verify(delegate).setDefaultFailureUrl(REDIRECT_URL);
            verify(delegate).setRedirectStrategy(any(HxLocationRedirectStrategy.class));

            handler.onAuthenticationFailure(request, response, exception);
            verify(delegate).onAuthenticationFailure(request, response, exception);
        }
    }

}
