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
        Map<Integer, String> indexToName = methodMetadata.indexToName();
        if (!indexToName.isEmpty()) {
            indexToName.forEach((k, v) -> {
                if (Objects.nonNull(argv[k])) {
                    queryParams.put(v, String.valueOf(argv[k]));
                }
            });
        }
        Map<String, String> headerParams = Maps.newHashMap();
        Map<Integer, String> indexToHeaderName = methodMetadata.indexToHeaderName();
        if (!indexToHeaderName.isEmpty()) {
            indexToHeaderName.forEach((k, v) -> {
                if (Objects.nonNull(argv[k])) {
                    headerParams.put(v, String.valueOf(argv[k]));
                }
            });
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
