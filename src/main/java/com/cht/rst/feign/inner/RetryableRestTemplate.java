package com.cht.rst.feign.inner;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.common.base.Joiner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriTemplateHandler;

import java.lang.reflect.Type;
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
                         Type responseType, Map<String, String> queryParams, Map<String, String> headerParams,
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
                            Type responseType, Map<String, String> headerParams, Object... uriVariables) {
        long startTime = System.currentTimeMillis();
        String url = baseUrl + path;
        HttpHeaders httpHeaders = new HttpHeaders();
        headerParams.forEach((k, v) -> httpHeaders.add(k, v));
        HttpEntity<Object> httpEntity = new HttpEntity<>(request, httpHeaders);

        ParameterizedTypeReference<T> objectParameterizedTypeReference =
                ParameterizedTypeReference.forType(responseType);

        ResponseEntity<T> result = restTemplate.exchange(url, method, httpEntity, objectParameterizedTypeReference,
                uriVariables);

        logger.info("\n\t >>>>cht-feign<<< invocation cost {} ms: \n\turl={} \n\trequestBoy={} \n\theaders={}" +
                        " " +
                        "\n\turiVariables={} " +
                        "\n\tresult={}",
                System.currentTimeMillis()-startTime,
                url,
                JSONObject.toJSONString(request),
                headerParams,
                uriVariables,  JSONObject.toJSONString(result.getBody(), SerializerFeature.WriteMapNullValue));
        return result.getBody();
    }

}
