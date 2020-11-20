package com.cht.rst.feign.inner;

import com.cht.rst.feign.plugin.ChtFeignInterceptor;
import com.cht.rst.feign.plugin.Plugin;
import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

import static com.cht.rst.feign.inner.Util.checkNotNull;

public class RestTemplateClient implements Client {

    private Joiner.MapJoiner MAP_JOINER = Joiner.on("&").withKeyValueSeparator("=");

    private RestClient delegate;

    public RestTemplateClient() {
        this(new RetryableRestTemplate(new RestTemplate()));

    }

    public RestTemplateClient(RestTemplate restTemplate) {
        this(new RetryableRestTemplate(restTemplate));

    }

    private RestTemplateClient(RetryableRestTemplate delegate) {
        checkNotNull(delegate, "restTemplate must not null");
        this.delegate = delegate;
    }

    @Override
    public void addInterceptors(Collection<ChtFeignInterceptor> interceptors) {
        this.delegate = (RestClient) Plugin.wrap(delegate, interceptors);
    }

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
        String uri = methodMetadata.getUrlPart();
        if (Objects.nonNull(uriValues) && uriValues.length > 0) {
            uri = new DefaultUriBuilderFactory().expand(uri, uriValues).getPath();
        }
        if (!CollectionUtils.isEmpty(queryParams)) {
            uri = uri + "?" + MAP_JOINER.join(queryParams);
        }
        return delegate.doExecute(methodMetadata.getMethod(), methodMetadata.getBaseUrl() + uri,
                requestBody, returnType, headerParams);
    }
}
