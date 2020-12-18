package com.cht.rst.feign.inner;

public class DefaultFeignLoggerFactory implements FeignLoggerFactory {

    private Logger logger;

    public DefaultFeignLoggerFactory(Logger logger) {
        this.logger = logger;
    }

    @Override
    public Logger create(Class<?> type) {
        return this.logger != null ? this.logger : new Logger.DefaultLooger();
    }

}
