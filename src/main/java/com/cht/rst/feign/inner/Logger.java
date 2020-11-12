package com.cht.rst.feign.inner;


public abstract class Logger {

    protected abstract void log(String configKey, String format, Object... args);
}
