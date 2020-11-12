package com.cht.rst.feign.inner;

import java.io.Serializable;

import static com.cht.rst.feign.inner.Util.checkNotNull;

public class ChtFeignRequestTemplate<T> implements Serializable {


    private String baseUrl;

    private String urlPart;

    private HttpMethod method;

    private MethodMetadata methodMetadata;

    public enum HttpMethod {
        GET, HEAD, POST, PUT, DELETE, CONNECT, OPTIONS, TRACE, PATCH
    }

    public ChtFeignRequestTemplate() {
        super();
    }

    public ChtFeignRequestTemplate(String baseUrl,
                                   String urlPart,
                                   HttpMethod method,
                                   MethodMetadata methodMetadata) {
        this.baseUrl = baseUrl;
        this.urlPart = urlPart;
        this.method = method;
        this.methodMetadata = methodMetadata;
    }

    public static ChtFeignRequestTemplate from(ChtFeignRequestTemplate requestTemplate) {
        ChtFeignRequestTemplate template =
                new ChtFeignRequestTemplate(
                        requestTemplate.baseUrl,
                        requestTemplate.urlPart,
                        requestTemplate.method,
                        requestTemplate.methodMetadata);
        return template;
    }

    public ChtFeignRequestTemplate method(HttpMethod method) {
        checkNotNull(method, "method");
        this.method = method;
        return this;
    }

    public void setBaseUrl(String baseUrl) {
        /* target can be empty */
        if (Util.isBlank(baseUrl)) {
        }

        /* verify that the target contains the scheme, host and port */
        if (!(baseUrl != null && !baseUrl.isEmpty() && baseUrl.startsWith("http"))) {
            throw new IllegalArgumentException("target values must be absolute.");
        }
        if (baseUrl.endsWith("/")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        }
        this.baseUrl = baseUrl;
    }

    public String getBaseUrl() {
        return this.baseUrl;
    }

    public HttpMethod getMethod() {
        return this.method;
    }

    public void setUrlPart(String urlPart) {
        this.urlPart = urlPart;
    }

    public String getUrlPart() {
        return this.urlPart;
    }

    public MethodMetadata getMethodMetadata() {
        return this.methodMetadata;
    }

    public String method() {
        return (method != null) ? method.name() : null;
    }

    public String path() {

        StringBuilder path = new StringBuilder();
        if (this.baseUrl != null) {
            path.append(this.baseUrl);
        }
        if (this.urlPart != null) {
            path.append(this.urlPart);
        }
        if (path.length() == 0) {
            /* no path indicates the root uri */
            path.append("/");
        }
        return path.toString();
    }

    public ChtFeignRequestTemplate methodMetadata(MethodMetadata methodMetadata) {
        this.methodMetadata = methodMetadata;
        return this;
    }
}
