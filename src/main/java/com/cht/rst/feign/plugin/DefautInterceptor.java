package com.cht.rst.feign.plugin;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class DefautInterceptor implements ChtFeignInterceptor {

    private Logger logger = LoggerFactory.getLogger(DefautInterceptor.class);

    private long startTime;

    public void preProcess(String url, Object requestBody,
                            Map<String, String> headerParams) {

        startTime = System.currentTimeMillis();
    }

    @Override
    public void postProcess(String url, Object requestBody,
                            Map<String, String> headerParams, Object result) {

        logger.info("\n\tcht-feign invocation cost {} ms: \n\turl = {} \n\theaders = {} \n\trequestBoy = {}" +
                        " " +
                        "\n\tresult = {}",
                System.currentTimeMillis() - startTime,
                url,
                headerParams,
                JSONObject.toJSONString(requestBody),
                JSONObject.toJSONString(result, SerializerFeature.WriteMapNullValue));
    }
}
