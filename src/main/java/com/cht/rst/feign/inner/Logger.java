package com.cht.rst.feign.inner;

import org.springframework.http.HttpMethod;

import java.util.Map;

/**
 * the logger control of cht-feign
 */
public abstract class Logger {


    protected static String methodTag(String configKey) {
        return new StringBuilder().append('[').append(configKey, 0, configKey.indexOf('('))
                .append("] ").toString();
    }

    public abstract void logRequest(String configKey, HttpMethod method, String finalUrl, Object requestBody,
                                    Map<String, String> headerParams);

    public abstract <T> void logResponse(String configKey, T t, long elapsedTime);

    public enum Level {
        DEBUG,
        INFO,
        WARN,
        ERROR
    }

    public static class NoOpLogger extends Logger {

        @Override
        public void logRequest(String configKey, HttpMethod method, String finalUrl, Object requestBody,
                               Map<String, String> headerParams) {

        }

        @Override
        public <T> void logResponse(String configKey, T t, long elapsedTime) {

        }
    }
}
