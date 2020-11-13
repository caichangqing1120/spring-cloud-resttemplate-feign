package com.cht.rst.feign;

import com.cht.rst.feign.inner.ChtFeign;
import com.cht.rst.feign.inner.Client;
import com.cht.rst.feign.inner.RestTemplateClient;
import com.cht.rst.feign.inner.Retryer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ChtFeignClientsConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public Retryer feignRetryer() {
        return Retryer.NEVER_RETRY;
    }

//    @Bean
//    @ConditionalOnMissingBean(RestTemplate.class)
//    public RestTemplate getRestTemplate() {
//        return new RestTemplate();
//    }

    @Bean
    @ConditionalOnBean(RestTemplate.class)
    public Client chtFeignClient(RestTemplate restTemplate) {
//        restTemplate.setMessageConverters();
        return new RestTemplateClient(restTemplate);
    }


    @Bean
    @Scope("prototype")
    public ChtFeign.Builder feignBuilder(Retryer retryer) {
        return ChtFeign.builder().retryer(retryer);
    }
}
