package com.nikhil.project.uber.uberApp.advices;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@RestControllerAdvice
public class GlobalResponseHandler implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType,
                            Class<? extends HttpMessageConverter<?>> converterType) {
        // Keep as-is (guard based on declared return type).
        // We still must defensively handle the body instance in beforeBodyWrite.
        return !returnType.getParameterType().equals(ApiResponse.class);
    }

    @Override
    public Object beforeBodyWrite(Object body,
                                  MethodParameter returnType,
                                  MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request,
                                  ServerHttpResponse response) {

        // If the controller/exception handler already returned ApiResponse, don't re-wrap.
        if (body instanceof ApiResponse) {
            return body;
        }

        // Allow null responses (like DELETE) -> empty success wrapper
        if (body == null) {
            return new ApiResponse<>(null);
        }

        // Special case for String responses: Spring expects a raw String (different converter).
        if (body instanceof String) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                return mapper.writeValueAsString(new ApiResponse<>(body));
            } catch (Exception e) {
                throw new RuntimeException("Error wrapping String response", e);
            }
        }

        // Normal case: wrap body into ApiResponse
        return new ApiResponse<>(body);
    }
}
