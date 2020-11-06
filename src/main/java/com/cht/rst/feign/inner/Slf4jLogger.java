package com.cht.rst.feign.inner;

import org.slf4j.LoggerFactory;

public class Slf4jLogger extends Logger {

    private final org.slf4j.Logger logger;

    public Slf4jLogger() {
        this(Logger.class);
    }

    public Slf4jLogger(Class<?> clazz) {
        this(LoggerFactory.getLogger(clazz));
    }

    public Slf4jLogger(String name) {
        this(LoggerFactory.getLogger(name));
    }

    Slf4jLogger(org.slf4j.Logger logger) {
        this.logger = logger;
    }

    @Override
    protected void log(String configKey, String format, Object... args) {

    }
}
