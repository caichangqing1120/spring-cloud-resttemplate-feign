package com.cht.rst.feign.inner;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Joiner;

import com.cht.rst.feign.inner.ChtFeignRequestTemplate.HttpMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriTemplateHandler;

import java.util.Map;
import java.util.Objects;


public class RetryableRestTemplate {

    private Logger logger = LoggerFactory.getLogger(RetryableRestTemplate.class);

    private static final Joiner.MapJoiner MAP_JOINER = Joiner.on("&").withKeyValueSeparator("=");

    private UriTemplateHandler uriTemplateHandler = new DefaultUriBuilderFactory();

    private final RestTemplate restTemplate;

    public RetryableRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public <T> T execute(HttpMethod method, String baseUrl, String path, Object request,
                         Class<T> responseType, Map<String, String> queryParams, Map<String, String> headerParams,
                         Object... uriVariables) {
        String uri = path;
        if (Objects.nonNull(uriVariables) && uriVariables.length > 0) {
            uri = uriTemplateHandler.expand(uri, uriVariables).getPath();
        }
        if (!CollectionUtils.isEmpty(queryParams)) {
            uri = uri + "?" + MAP_JOINER.join(queryParams);
        }
        if (Objects.nonNull(uriVariables) && uriVariables.length > 0) {
            return doExecute(method, baseUrl, uri, request, responseType, headerParams, uriVariables);
        }
        return doExecute(method, baseUrl, uri, request, responseType, headerParams);
    }

    private <T> T doExecute(HttpMethod method, String baseUrl, String path, Object request,
                            Class<T> responseType, Map<String, String> headerParams, Object... uriVariables) {
        String url = baseUrl + path;
        HttpHeaders httpHeaders = new HttpHeaders();
        headerParams.forEach((k, v) -> httpHeaders.add(k, v));
        HttpEntity<Object> httpEntity = new HttpEntity<>(request, httpHeaders);

        T result = null;
        switch (method) {
            case GET:
                result = restTemplate.getForObject(url, responseType, uriVariables);
                break;
            case POST:
                result = restTemplate.postForEntity(url, httpEntity, responseType, uriVariables).getBody();
                break;
            case PUT:
                restTemplate.put(url, httpEntity, uriVariables);
                break;
            case DELETE:
                restTemplate.delete(url, uriVariables);
                break;
            default:
                throw new UnsupportedOperationException(String.format("unsupported http method(method=%s)", method));
        }
        logger.info("cht feign invocation, \nurl={}, \nrequestBoy={}, \nheaders={}, \nuriVariables={} \nresult={}", url,
                JSONObject.toJSONString(request),
                headerParams,
                uriVariables,  JSONObject.toJSONString(result));
        return result;
    }

}
