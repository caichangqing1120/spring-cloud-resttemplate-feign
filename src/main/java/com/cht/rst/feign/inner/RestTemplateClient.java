package com.cht.rst.feign.inner;

import com.google.common.collect.Maps;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

public class RestTemplateClient implements Client {

    private final RetryableRestTemplate delegate;

    public RestTemplateClient() {
        this(new RetryableRestTemplate());
    }

    public RestTemplateClient(RetryableRestTemplate delegate) {
        this.delegate = delegate;
    }

    @Override
    public <T> T execute(ChtFeignRequestTemplate request, Object[] argv) {

        MethodMetadata methodMetadata = request.getMethodMetadata();
        Object requestBody = Objects.nonNull(argv) && Objects.nonNull(methodMetadata.bodyIndex()) ?
                argv[methodMetadata.bodyIndex()] : null;
        Object[] uriValues = Objects.nonNull(argv) && !CollectionUtils.isEmpty(methodMetadata.uriVariableIndex()) ?
                methodMetadata.uriVariableIndex().stream().map(index -> argv[index]).toArray() : null;
        Class<T> returnType = methodMetadata.returnType();
        Map<String, String> queryParams = Maps.newHashMap();
        if(!CollectionUtils.isEmpty(methodMetadata.indexToName())){
            for (Map.Entry<Integer, String> entry : methodMetadata.indexToName().entrySet()) {
                queryParams.put(entry.getValue(), argv[entry.getKey()].toString());
            }
        }
        String urlPart = request.getUrlPart();

        return delegate.execute(request.getMethod(),
                request.getBaseUrl(),
                urlPart,
                requestBody,
                returnType,
                queryParams,
                uriValues
        );
    }
}
