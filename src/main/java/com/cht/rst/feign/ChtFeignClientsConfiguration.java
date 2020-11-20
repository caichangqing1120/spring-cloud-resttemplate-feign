package com.cht.rst.feign;

import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
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
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Configuration
public class ChtFeignClientsConfiguration {

    @Autowired
    private List<HttpMessageConverter<?>> converterList;

    @Bean
    @ConditionalOnMissingBean
    public Retryer feignRetryer() {
        return Retryer.NEVER_RETRY;
    }

    @Bean
    @ConditionalOnBean(RestTemplate.class)
    public Client chtFeignClient(RestTemplate restTemplate) {
        restTemplate.setMessageConverters(converterList);
        return new RestTemplateClient(restTemplate);
    }


    @Bean
    @ConditionalOnMissingBean(RestTemplate.class)
    public Client chtFeignClient2() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setMessageConverters(converterList);
        return new RestTemplateClient(restTemplate);
    }


    @Bean
    @Scope("prototype")
    public ChtFeign.Builder feignBuilder(Retryer retryer) {
        return ChtFeign.builder().retryer(retryer);
    }
}
