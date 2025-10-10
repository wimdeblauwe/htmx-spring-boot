package io.github.wimdeblauwe.htmx.spring.boot.endpoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;

@Component
public class HtmxFormConverter implements HttpMessageConverter<Object> {

    private final Charset charset = StandardCharsets.UTF_8;

    @Override
    public boolean canRead(@NonNull Class<?> clazz, MediaType mediaType) {
        if (mediaType == null) {
            return false;
        }
        return mediaType.includes(MediaType.APPLICATION_FORM_URLENCODED);
    }

    @Override
    public boolean canWrite(Class<?> clazz, MediaType mediaType) {
        return false;
    }

    @Override
    public List<MediaType> getSupportedMediaTypes() {
        return List.of();
    }

    @NonNull
    @Override
    public List<MediaType> getSupportedMediaTypes(@NonNull Class<?> clazz) {
        return List.of(MediaType.APPLICATION_FORM_URLENCODED);
    }


    @NonNull
    @Override
    public Object read(@NonNull Class<?> clazz, HttpInputMessage inputMessage)
            throws IOException, HttpMessageNotReadableException {
        MediaType contentType = inputMessage.getHeaders().getContentType();
        Charset charset = (contentType != null && contentType.getCharset() != null ?
                contentType.getCharset() : this.charset);
        String body = StreamUtils.copyToString(inputMessage.getBody(), charset);

        String[] pairs = StringUtils.tokenizeToStringArray(body, "&");
        HashMap<String, Object> result = new HashMap<>(pairs.length);
        for (String pair : pairs) {
            int idx = pair.indexOf('=');
            if (idx == -1) {
                result.put(URLDecoder.decode(pair, charset), null);
            } else {
                String name = URLDecoder.decode(pair.substring(0, idx), charset);
                String value = URLDecoder.decode(pair.substring(idx + 1), charset);
                result.put(name, value);
            }
        }
        final ObjectMapper mapper = new ObjectMapper();
        return mapper.convertValue(result, clazz);
    }

    @Override
    public void write(@NonNull Object o, MediaType contentType, HttpOutputMessage outputMessage)
            throws HttpMessageNotWritableException {

    }
}
