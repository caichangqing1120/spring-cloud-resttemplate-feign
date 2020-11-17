package com.cht.rst.feign.inner;

import com.google.common.collect.Maps;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Objects;

public class RestTemplateClient implements Client {

    private final RetryableRestTemplate delegate;

    public RestTemplateClient() {
        this(new RetryableRestTemplate(new RestTemplate()));
    }

    public RestTemplateClient(RestTemplate restTemplate) {
        this(new RetryableRestTemplate(restTemplate));
    }

    private RestTemplateClient(RetryableRestTemplate delegate) {
        this.delegate = delegate;
    }

    @Override
    public <T> T execute(MethodMetadata methodMetadata, Object[] argv) {

        Object requestBody = Objects.nonNull(argv) && Objects.nonNull(methodMetadata.bodyIndex()) ?
                argv[methodMetadata.bodyIndex()] : null;
        Object[] uriValues = Objects.nonNull(argv) && !CollectionUtils.isEmpty(methodMetadata.uriVariableIndex()) ?
                methodMetadata.uriVariableIndex().stream().map(index -> argv[index]).toArray() : null;
        Type returnType = methodMetadata.returnType();
        Map<String, String> queryParams = Maps.newHashMap();
        if(!CollectionUtils.isEmpty(methodMetadata.indexToName())){
            for (Map.Entry<Integer, String> entry : methodMetadata.indexToName().entrySet()) {
                queryParams.put(entry.getValue(), String.valueOf(argv[entry.getKey()]));
            }
        }
        Map<String, String> headerParams = Maps.newHashMap();
        if(!CollectionUtils.isEmpty(methodMetadata.indexToHeaderName())){
            for (Map.Entry<Integer, String> entry : methodMetadata.indexToHeaderName().entrySet()) {
                headerParams.put(entry.getValue(), String.valueOf(argv[entry.getKey()]));
            }
        }
        String urlPart = methodMetadata.getUrlPart();

        return delegate.execute(methodMetadata.getMethod(),
                methodMetadata.getBaseUrl(),
                urlPart,
                requestBody,
                returnType,
                queryParams,
                headerParams,
                uriValues
        );
    }
}
