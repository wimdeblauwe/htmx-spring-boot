package io.github.wimdeblauwe.htmx.spring.boot.mvc;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.SmartView;
import org.springframework.web.servlet.View;

import java.util.Map;

/**
 * A View that can be used to signal htmx to refresh the page.
 *
 * @see <a href="https://htmx.org/reference/#response_header">HX-Refresh Response Header</a>
 * @since 3.6.0
 */
public class HtmxRefreshView implements View, SmartView {

    @Override
    public boolean isRedirectView() {
        return true;
    }

    @Override
    public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setHeader(HtmxResponseHeader.HX_REFRESH.getValue(), HtmxValue.TRUE);
    }

}
