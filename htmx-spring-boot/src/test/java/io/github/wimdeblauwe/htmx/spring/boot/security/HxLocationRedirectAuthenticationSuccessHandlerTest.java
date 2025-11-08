package io.github.wimdeblauwe.htmx.spring.boot.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class HxLocationRedirectAuthenticationSuccessHandlerTest {

    private static final String REDIRECT_URL = "/home?login";


    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private Authentication authentication;


    @Test
    void onAuthenticationSuccess() throws ServletException, IOException {
        try (var mocked = mockConstruction(SavedRequestAwareAuthenticationSuccessHandler.class)) {
            var handler = new HxLocationRedirectAuthenticationSuccessHandler(REDIRECT_URL);
            var delegate = mocked.constructed().get(0);
            verify(delegate).setDefaultTargetUrl(REDIRECT_URL);
            verify(delegate).setAlwaysUseDefaultTargetUrl(false);
            verify(delegate).setRedirectStrategy(any(HxLocationRedirectStrategy.class));

            handler.onAuthenticationSuccess(request, response, authentication);
            verify(delegate).onAuthenticationSuccess(request, response, authentication);
        }
    }

}
