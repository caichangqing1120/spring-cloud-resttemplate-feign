package com.cht.rst.feign;

import com.cht.rst.feign.inner.Logger;

public interface FeignLoggerFactory {

    Logger create(Class<?> type);
}
