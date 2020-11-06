package com.cht.rst.feign.inner;

import java.io.IOException;

public interface Client {

    <T> T execute(ChtFeignRequestTemplate request, Object[] argv, Class<T> returnType) throws IOException;
}
