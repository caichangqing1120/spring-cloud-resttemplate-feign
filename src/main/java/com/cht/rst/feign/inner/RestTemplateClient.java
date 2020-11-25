package com.cht.rst.feign.inner;

import com.cht.rst.feign.plugin.ChtFeignInterceptor;
import com.cht.rst.feign.plugin.Plugin;
import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.io.IOException;
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

    public <T> T execute(MethodMetadata methodMetadata, Object[] argv) throws IOException {

        Integer fileIndex = methodMetadata.fileIndex();
        Integer bodyIndex = methodMetadata.bodyIndex();
        boolean isFile = Objects.nonNull(fileIndex);

        Object requestBody = isFile ? (Objects.nonNull(argv) ? argv[fileIndex] : null) :
                (Objects.nonNull(argv) && Objects.nonNull(bodyIndex) ? argv[bodyIndex] : null);

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
        String uri = methodMetadata.getUrlPart();
        if (Objects.nonNull(uriValues) && uriValues.length > 0) {
            uri = new DefaultUriBuilderFactory().expand(uri, uriValues).getPath();
        }
        if (!CollectionUtils.isEmpty(queryParams)) {
            uri = uri + "?" + MAP_JOINER.join(queryParams);
        }
        return delegate.doExecute(methodMetadata.getMethod(), methodMetadata.getBaseUrl() + uri,
                requestBody, returnType, headerParams, isFile);
    }
}
