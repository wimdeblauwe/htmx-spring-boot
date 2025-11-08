package io.github.wimdeblauwe.htmx.spring.boot.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.io.IOException;

import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class HxLocationRedirectAccessDeniedHandlerTest {

    private static final String REDIRECT_URL = "/error?forbidden";


    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private AccessDeniedException exception;


    @Test
    void handle() throws ServletException, IOException {
        try (var mocked = mockConstruction(HxLocationRedirectStrategy.class)) {
            var handler = new HxLocationRedirectAccessDeniedHandler(REDIRECT_URL);
            var delegate = mocked.constructed().get(0);

            handler.handle(request, response, exception);
            verify(delegate).sendRedirect(request, response, REDIRECT_URL);
        }
    }

}
