package com.cht.rst.feign;

import com.cht.rst.feign.inner.ChtFeign;
import com.cht.rst.feign.inner.Client;
import com.cht.rst.feign.inner.Target;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.Assert;

public class ChtFeignClientFactoryBean
        implements FactoryBean<Object>, InitializingBean, ApplicationContextAware {

    private Class<?> type;

    private String name;

    private String url;

    private String contextId;

    private ApplicationContext applicationContext;

    @Override
    public Object getObject() {
        ChtFeignContext context = this.applicationContext.getBean(ChtFeignContext.class);
        ChtFeign.Builder builder = get(context, ChtFeign.Builder.class);
        //如果有，设置自定义client
        Client client = getOptional(context, Client.class);
        if (client != null) {
            builder.client(client);
        }
        return new DefaultTargeter().target(this, builder, context, new Target.HardCodedTarget<>(
                this.type, this.name, url));
    }

    protected <T> T getOptional(ChtFeignContext context, Class<T> type) {
        return context.getInstance(this.contextId, type);
    }

    protected <T> T get(ChtFeignContext context, Class<T> type) {
        T instance = context.getInstance(this.contextId, type);
        if (instance == null) {
            throw new IllegalStateException("No bean found of type " + type + " for " + this.contextId);
        }
        return instance;
    }

    @Override
    public Class<?> getObjectType() {
        return this.type;
    }

    public Class<?> getType() {
        return type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getContextId() {
        return contextId;
    }

    public void setContextId(String contextId) {
        this.contextId = contextId;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.hasText(this.contextId, "Context id must be set");
        Assert.hasText(this.name, "Name must be set");
    }

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        this.applicationContext = context;
    }
}
