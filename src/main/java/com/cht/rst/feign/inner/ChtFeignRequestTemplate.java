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
            path.append(this.urlPart.toString());
        }
        if (path.length() == 0) {
            /* no path indicates the root uri */
            path.append("/");
        }
        return path.toString();
    }

    public String url() {
        StringBuilder url = new StringBuilder(this.path());
        return url.toString();
    }


//    public Request request() {
//        return Request.create(this.method, this.url(), this.headers(), this.requestBody());
//    }

}
