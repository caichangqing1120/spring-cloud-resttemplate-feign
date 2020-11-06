package com.cht.rst.feign.inner;

import java.io.IOException;
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
    public <T> T execute(ChtFeignRequestTemplate request, Object[] argv, Class<T> returnType) throws IOException {
        return delegate.execute(request.getMethod(),
                request.getBaseUrl(),
                request.getUrlPart(),
                Objects.isNull(argv) || argv.length == 0 ? null : argv[0],
                returnType
        );
    }
}
