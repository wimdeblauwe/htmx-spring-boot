package io.github.wimdeblauwe.htmx.spring.boot.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.DefaultRedirectStrategy;

import java.io.IOException;
import java.io.PrintWriter;

import static io.github.wimdeblauwe.htmx.spring.boot.mvc.HtmxRequestHeader.HX_REQUEST;
import static io.github.wimdeblauwe.htmx.spring.boot.mvc.HtmxResponseHeader.HX_LOCATION;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HxLocationRedirectStrategyTest {

    private static final String REDIRECT_URL = "/home";
    private static final String REDIRECT_DETAILS = "{\"path\":\"/home\",\"headers\":{\"HX-Boosted\":\"true\"},\"target\":\"body\"}";


    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private PrintWriter writer;


    @Test
    void sendRedirect() throws IOException {
        when(request.getHeader((HX_REQUEST.getValue()))).thenReturn(null);

        try (var mocked = mockConstruction(DefaultRedirectStrategy.class)) {
            var strategy = new HxLocationRedirectStrategy();
            var delegate = mocked.constructed().get(0);

            strategy.sendRedirect(request, response, REDIRECT_URL);
            verify(delegate).sendRedirect(request, response, REDIRECT_URL);
        }
    }

    @Test
    void sendRedirectByHtmx() throws IOException {
        when(request.getHeader((HX_REQUEST.getValue()))).thenReturn(Boolean.TRUE.toString());
        when(response.getWriter()).thenReturn(writer);

        var strategy = new HxLocationRedirectStrategy(false);
        strategy.sendRedirect(request, response, REDIRECT_URL);

        verify(response).setHeader(HX_LOCATION.getValue(), REDIRECT_URL);
        verify(response).setStatus(HttpStatus.OK.value());
        verify(writer).flush();
    }

    @Test
    void sendRedirectByHtmxBoosted() throws IOException {
        when(request.getHeader((HX_REQUEST.getValue()))).thenReturn(Boolean.TRUE.toString());
        when(response.getWriter()).thenReturn(writer);

        var strategy = new HxLocationRedirectStrategy();
        strategy.sendRedirect(request, response, REDIRECT_URL);

        verify(response).setHeader(HX_LOCATION.getValue(), REDIRECT_DETAILS);
        verify(response).setStatus(HttpStatus.OK.value());
        verify(writer).flush();
    }

    @Test
    void sendRedirectByHtmxWithCustomStatus() throws IOException {
        when(request.getHeader((HX_REQUEST.getValue()))).thenReturn(Boolean.TRUE.toString());
        when(response.getWriter()).thenReturn(writer);

        var strategy = new HxLocationRedirectStrategy(HttpStatus.FORBIDDEN);
        strategy.sendRedirect(request, response, REDIRECT_URL);

        verify(response).setHeader(HX_LOCATION.getValue(), REDIRECT_DETAILS);
        verify(response).setStatus(HttpStatus.FORBIDDEN.value());
        verify(writer).flush();
    }

}
