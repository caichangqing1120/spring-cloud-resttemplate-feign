package com.cht.rst.feign;

import com.cht.rst.feign.inner.ChtFeign;
import com.cht.rst.feign.inner.Logger;
import com.cht.rst.feign.inner.Retryer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class ChtFeignClientsConfiguration {

    @Autowired(required = false)
    private Logger logger;

    @Bean
    @ConditionalOnMissingBean
    public Retryer feignRetryer() {
        return Retryer.NEVER_RETRY;
    }

    @Bean
    @Scope("prototype")
    @ConditionalOnMissingBean
    public ChtFeign.Builder feignBuilder(Retryer retryer) {
        return ChtFeign.builder().retryer(retryer);
    }

    @Bean
    @ConditionalOnMissingBean(ChtFeignLoggerFactory.class)
    public ChtFeignLoggerFactory feignLoggerFactory() {
        return new ChtDefaultFeignLoggerFactory(logger);
    }
}
