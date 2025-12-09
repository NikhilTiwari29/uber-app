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

        // ✅ Avoid double-wrapping if it's already ApiResponse
        return !returnType.getParameterType().equals(ApiResponse.class);
    }

    @Override
    public Object beforeBodyWrite(Object body,
                                  MethodParameter returnType,
                                  MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request,
                                  ServerHttpResponse response) {

        // ✅ Allow null responses (like DELETE)
        if (body == null) {
            return new ApiResponse<>(null);
        }

        // ✅ Special case for String responses
        // Because Spring uses a different converter for String
        if (body instanceof String) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                return mapper.writeValueAsString(new ApiResponse<>(body));
            } catch (Exception e) {
                throw new RuntimeException("Error wrapping String response");
            }
        }

        // ✅ Normal case
        return new ApiResponse<>(body);
    }
}
