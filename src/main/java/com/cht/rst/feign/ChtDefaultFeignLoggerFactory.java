package com.cht.rst.feign;

import com.cht.rst.feign.inner.Logger;
import com.cht.rst.feign.inner.Slf4jLogger;

public class ChtDefaultFeignLoggerFactory implements ChtFeignLoggerFactory {

    private Logger logger;

    public ChtDefaultFeignLoggerFactory(Logger logger) {
        this.logger = logger;
    }

    @Override
    public Logger create(Class<?> type) {
        return this.logger != null ? this.logger : new Slf4jLogger(type);
    }
}
