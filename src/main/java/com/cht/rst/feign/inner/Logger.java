package com.cht.rst.feign.inner;

import org.springframework.http.HttpMethod;

import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * the logger control of cht-feign
 */
public abstract class Logger {

    private Consumer<String> logConsumer;

    Logger() {
        this(null);
    }

    Logger(Consumer<String> logConsumer) {
        this.logConsumer = logConsumer;
    }

    protected static String methodTag(String configKey) {
        return new StringBuilder().append('[').append(configKey, 0, configKey.indexOf('('))
                .append("] ").toString();
    }


    protected void log(String configKey, String format, Object... args) {
        if (Objects.nonNull(logConsumer)) {
            logConsumer.accept(methodTag(configKey) + String.format(format, args));
        }
    }

    public void logRequestStr(String configKey, HttpMethod method, String finalUrl, Object requestBody,
                              Map<String, String> headerParams) {
        log(configKey, "%s", logRequest(method, finalUrl, requestBody, headerParams));
    }

    public abstract String logRequest(HttpMethod method, String finalUrl, Object requestBody,
                                      Map<String, String> headerParams);

    public <T> void logResponseStr(String configKey, T t, long elapsedTime) {

        log(configKey, "%s", logResponse(t, elapsedTime));
    }

    public abstract <T> String logResponse(T t, long elapsedTime);


    public enum Level {
        DEBUG,
        INFO,
        WARN,
        ERROR
    }

    public static class NoOpLogger extends Logger {

        @Override
        public String logRequest(HttpMethod method, String finalUrl, Object requestBody,
                                 Map<String, String> headerParams) {
            return "";
        }

        @Override
        public <T> String logResponse(T t, long elapsedTime) {
            return "";
        }
    }
}
