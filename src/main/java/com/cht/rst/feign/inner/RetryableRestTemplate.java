package com.cht.rst.feign.inner;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;


public class RetryableRestTemplate implements RestClient {

    private final RestTemplate restTemplate;

    public RetryableRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public <T> T doExecute(HttpMethod method, String url, Object request, Type responseType,
                           Map<String, String> headerParams, boolean isFile) throws IOException {
        Object requestCopy;
        if (isFile) {
            MultipartFile multipartFile = (MultipartFile) request;
            ByteArrayResource file = new ByteArrayResource(multipartFile.getBytes()) {
                @Override
                public String getFilename() {
                    return multipartFile.getOriginalFilename();
                }
            };
            MultiValueMap fileMultiValueMap = new LinkedMultiValueMap(1);
            fileMultiValueMap.add("file", file);
            requestCopy = fileMultiValueMap;
        } else {
            requestCopy = request;
        }
        HttpHeaders httpHeaders = new HttpHeaders();
        headerParams.forEach((k, v) -> httpHeaders.add(k, v));
        HttpEntity<Object> httpEntity = new HttpEntity<>(requestCopy, httpHeaders);

        ParameterizedTypeReference<T> objectParameterizedTypeReference =
                ParameterizedTypeReference.forType(responseType);

        ResponseEntity<T> result = restTemplate.exchange(url, method, httpEntity, objectParameterizedTypeReference);
        return result.getBody();
    }

}
