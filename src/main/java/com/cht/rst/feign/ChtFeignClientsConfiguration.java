package com.cht.rst.feign;

import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.cht.rst.feign.inner.ChtFeign;
import com.cht.rst.feign.inner.Client;
import com.cht.rst.feign.inner.RestTemplateClient;
import com.cht.rst.feign.inner.Retryer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Configuration
public class ChtFeignClientsConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public Retryer feignRetryer() {
        return Retryer.NEVER_RETRY;
    }

    private HttpMessageConverter createFastJsonConverter() {

        FastJsonHttpMessageConverter fastConverter = new FastJsonHttpMessageConverter();
        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        SerializerFeature[] serializerFeatures = new SerializerFeature[]{
                SerializerFeature.WriteMapNullValue,
                SerializerFeature.WriteNullListAsEmpty,
                SerializerFeature.WriteNullStringAsEmpty,
                SerializerFeature.WriteNullBooleanAsFalse,
                SerializerFeature.WriteDateUseDateFormat,
                SerializerFeature.DisableCircularReferenceDetect};
        SerializeConfig serializeConfig = SerializeConfig.globalInstance;
        fastJsonConfig.setSerializeConfig(serializeConfig);
        fastJsonConfig.setSerializerFeatures(serializerFeatures);
        fastJsonConfig.setCharset(Charset.forName("UTF-8"));
        fastJsonConfig.setDateFormat("yyyy-MM-dd HH:mm:ss");
        fastConverter.setFastJsonConfig(fastJsonConfig);

        List<MediaType> mediaTypes = new ArrayList();
        mediaTypes.add(MediaType.APPLICATION_JSON);
        mediaTypes.add(MediaType.APPLICATION_JSON_UTF8);
        mediaTypes.add(new MediaType("application", "*+json", Charset.forName("UTF-8")));
        fastConverter.setSupportedMediaTypes(mediaTypes);
        return fastConverter;
    }

//    @Bean
//    @ConditionalOnMissingBean(RestTemplate.class)
//    public RestTemplate getRestTemplate() {
//        return new RestTemplate();
//    }

    @Bean
    @ConditionalOnBean(RestTemplate.class)
    public Client chtFeignClient(RestTemplate restTemplate) {
        restTemplate.setMessageConverters(Collections.singletonList(createFastJsonConverter()));
        return new RestTemplateClient(restTemplate);
    }


    @Bean
    @ConditionalOnMissingBean(RestTemplate.class)
    public Client chtFeignClient2() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setMessageConverters(Collections.singletonList(createFastJsonConverter()));
        return new RestTemplateClient(restTemplate);
    }


    @Bean
    @Scope("prototype")
    public ChtFeign.Builder feignBuilder(Retryer retryer) {
        return ChtFeign.builder().retryer(retryer);
    }
}
