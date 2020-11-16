package com.cht.rst.feign.inner;

import java.io.IOException;

public interface Client {

    <T> T execute(MethodMetadata request, Object[] argv) throws IOException;
}
