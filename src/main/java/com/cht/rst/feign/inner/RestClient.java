package com.cht.rst.feign.inner;

import javafx.util.Pair;
import org.springframework.http.HttpMethod;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;

public interface RestClient {

    <T> T doExecute(HttpMethod method, String url, Object request, Type responseType,
                    Map<String, String> headerParams, Pair<Integer, String> fileIndex) throws IOException;
}
