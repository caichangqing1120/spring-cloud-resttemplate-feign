package com.cht.rst.feign.inner;

import com.cht.rst.feign.plugin.ChtFeignInterceptor;

import java.io.IOException;
import java.util.Collection;

public interface Client {

    void addInterceptors(Collection<ChtFeignInterceptor> interceptors);

    <T> T execute(MethodMetadata request, Object[] argv) throws IOException;
}
