package com.cht.rst.feign.inner;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class MethodMetadata implements Serializable {

    private static final long serialVersionUID = 1L;
    private String configKey;
    private transient Class<?> returnType;
    //requestBody位置
    private Integer bodyIndex;
    //变量位置-变量名
    private final Map<Integer, String> indexToName = new LinkedHashMap<>();
    //uri变量位置
    private final List<Integer> uriVariableIndex = new ArrayList<>();
    //headers
    private final Map<Integer, String> indexToHeaderName = new LinkedHashMap<>();

    private final ChtFeignRequestTemplate template = new ChtFeignRequestTemplate();

    MethodMetadata() {
        template.methodMetadata(this);
    }

    public String configKey() {
        return configKey;
    }

    public MethodMetadata configKey(String configKey) {
        this.configKey = configKey;
        return this;
    }

    public Class returnType() {
        return returnType;
    }

    public MethodMetadata returnType(Class returnType) {
        this.returnType = returnType;
        return this;
    }

    public Integer bodyIndex() {
        return bodyIndex;
    }

    public MethodMetadata bodyIndex(Integer bodyIndex) {
        this.bodyIndex = bodyIndex;
        return this;
    }

    public ChtFeignRequestTemplate template() {
        return template;
    }
    //返回参数名 与 他位置
    public Map<Integer, String> indexToName() {
        return indexToName;
    }

    public List<Integer> uriVariableIndex() {
        return uriVariableIndex;
    }

    public Map<Integer, String> indexToHeaderName() {
        return indexToHeaderName;
    }
}
