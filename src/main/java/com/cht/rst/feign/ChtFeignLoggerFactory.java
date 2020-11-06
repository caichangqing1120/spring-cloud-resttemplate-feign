package com.cht.rst.feign;

import com.cht.rst.feign.inner.Logger;

public interface ChtFeignLoggerFactory {

    Logger create(Class<?> type);

}
