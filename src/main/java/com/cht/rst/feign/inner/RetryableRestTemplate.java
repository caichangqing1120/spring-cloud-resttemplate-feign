package com.cht.rst.feign.inner;

import com.google.common.base.Joiner;

import com.cht.rst.feign.inner.ChtFeignRequestTemplate.HttpMethod;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriTemplateHandler;

import java.util.Map;
import java.util.Objects;


public class RetryableRestTemplate {

    private static final Joiner.MapJoiner MAP_JOINER = Joiner.on("&").withKeyValueSeparator("=");

    private UriTemplateHandler uriTemplateHandler = new DefaultUriBuilderFactory();

    private RestTemplate restTemplate = new RestTemplate();

    public <T> T execute(HttpMethod method, String baseUrl, String path, Object request,
                          Class<T> responseType, Map<String, String> queryParams, Object... uriVariables) {
        String uri = path;
        if (Objects.nonNull(uriVariables) && uriVariables.length > 0) {
            uri = uriTemplateHandler.expand(uri, uriVariables).getPath();
        }
        if (!CollectionUtils.isEmpty(queryParams)) {
            uri = uri + "?" + MAP_JOINER.join(queryParams);
        }
        if (Objects.nonNull(uriVariables) && uriVariables.length > 0) {
            return doExecute(method, baseUrl, uri, request, responseType, uriVariables);
        }
        return doExecute(method, baseUrl, uri, request, responseType);
    }

    private <T> T doExecute(HttpMethod method, String baseUrl, String path, Object request,
                            Class<T> responseType, Object... uriVariables) {
        T result = null;
        switch (method) {
            case GET:
                result = restTemplate.getForObject(baseUrl + path, responseType, uriVariables);
                break;
            case POST:
                result =
                        restTemplate.postForEntity(baseUrl + path, request, responseType, uriVariables).getBody();
                break;
            case PUT:
                restTemplate.put(baseUrl + path, request, uriVariables);
                break;
            case DELETE:
                restTemplate.delete(baseUrl + path, uriVariables);
                break;
            default:
                throw new UnsupportedOperationException(String.format("unsupported http method(method=%s)", method));
        }
        return result;
    }

}
