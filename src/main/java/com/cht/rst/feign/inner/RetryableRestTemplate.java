package com.cht.rst.feign.inner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cht.rst.feign.inner.ChtFeignRequestTemplate.HttpMethod;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriTemplateHandler;


public class RetryableRestTemplate {

    private Logger logger = LoggerFactory.getLogger(RetryableRestTemplate.class);

    private UriTemplateHandler uriTemplateHandler = new DefaultUriBuilderFactory();

    private RestTemplate restTemplate = new RestTemplate(/*httpMessageConverters.getConverters()*/);

//    public <T> T get(String baseUrl, String path, Class<T> responseType, Object... urlVariables) throws RestClientException {
//        return execute(HttpMethod.GET, baseUrl, path, null, responseType, urlVariables);
//    }

//    public <T> ResponseEntity<T> get(String baseUrl, String path, ParameterizedTypeReference<T> reference,
//                                     Object... uriVariables) throws RestClientException {
//
//        return exchangeGet(baseUrl, path, reference, uriVariables);
//    }

//    public <T> T post(String baseUrl, String path, Object request, Class<T> responseType,
//                      Object... uriVariables) throws RestClientException {
//        return execute(HttpMethod.POST, baseUrl, path, request, responseType, uriVariables);
//    }
//
//    public void put(String baseUrl, String path, Object request, Object... urlVariables) throws RestClientException {
//        execute(HttpMethod.PUT, baseUrl, path, request, null, urlVariables);
//    }
//
//    public void delete(String baseUrl, String path, Object... urlVariables) throws RestClientException {
//        execute(HttpMethod.DELETE, baseUrl, path, null, null, urlVariables);
//    }

    public <T> T execute(HttpMethod method, String baseUrl, String path, Object request,
                          Class<T> responseType, Object... uriVariables) {

//        if (path.startsWith("/")) {
//            path = path.substring(1, path.length());
//        }

        String uri = uriTemplateHandler.expand(path, uriVariables).getPath();
        //Transaction ct = Tracer.newTransaction("AdminAPI", uri);
        //ct.addData("Env", env);

        //List<ServiceDTO> services = getAdminServices(env, ct);
        T result = doExecute(method, baseUrl, path, request, responseType, uriVariables);
        return result;
    }

//    private <T> ResponseEntity<T> exchangeGet(Env env, String path, ParameterizedTypeReference<T> reference,
//                                              Object... uriVariables) {
//        if (path.startsWith("/")) {
//            path = path.substring(1, path.length());
//        }
//
//        String uri = uriTemplateHandler.expand(path, uriVariables).getPath();
//        Transaction ct = Tracer.newTransaction("AdminAPI", uri);
//        ct.addData("Env", env);
//
//        List<ServiceDTO> services = getAdminServices(env, ct);
//
//        for (ServiceDTO serviceDTO : services) {
//            try {
//
//                ResponseEntity<T> result = restTemplate.exchange(parseHost(serviceDTO) + path, HttpMethod.GET, null
//                        , reference, uriVariables);
//
//                ct.setStatus(Transaction.SUCCESS);
//                ct.complete();
//                return result;
//            } catch (Throwable t) {
//                logger.error("Http request failed, uri: {}, method: {}", uri, HttpMethod.GET, t);
//                Tracer.logError(t);
//                if (canRetry(t, HttpMethod.GET)) {
//                    Tracer.logEvent(TracerEventType.API_RETRY, uri);
//                } else {// biz exception rethrow
//                    ct.setStatus(t);
//                    ct.complete();
//                    throw t;
//                }
//
//            }
//        }
//
//        //all admin server down
//        ServiceException e = new ServiceException(String.format("Admin servers are unresponsive. meta server " +
//                "address: %s, admin servers: %s", MetaDomainConsts.getDomain(env), services));
//        ct.setStatus(e);
//        ct.complete();
//        throw e;
//
//    }
//
//    private List<ServiceDTO> getAdminServices(Env env, Transaction ct) {
//
//        List<ServiceDTO> services = adminServiceAddressLocator.getServiceList(env);
//
//        if (CollectionUtils.isEmpty(services)) {
//            ServiceException e = new ServiceException(String.format("No available admin server."
//                    + " Maybe because of meta server down or all admin server down. "
//                    + "Meta server address: %s", MetaDomainConsts.getDomain(env)));
//            ct.setStatus(e);
//            ct.complete();
//            throw e;
//        }
//
//        return services;
//    }

    private <T> T doExecute(HttpMethod method, String baseUrl, String path, Object request,
                            Class<T> responseType, Object... uriVariables) {
        T result = null;
        switch (method) {
            case GET:
                result = restTemplate.getForObject(baseUrl + path, responseType, uriVariables);
                break;
            case POST:
                result =
                        restTemplate.postForEntity(baseUrl + path, request, responseType, uriVariables).getBody();
                break;
            case PUT:
                restTemplate.put(baseUrl + path, request, uriVariables);
                break;
            case DELETE:
                restTemplate.delete(baseUrl + path, uriVariables);
                break;
            default:
                throw new UnsupportedOperationException(String.format("unsupported http method(method=%s)", method));
        }
        return result;
    }


    //post,delete,put请求在admin server处理超时情况下不重试
//    private boolean canRetry(Throwable e, HttpMethod method) {
//        Throwable nestedException = e.getCause();
//        if (method == HttpMethod.GET) {
//            return nestedException instanceof SocketTimeoutException || nestedException instanceof
//            HttpHostConnectException || nestedException instanceof ConnectTimeoutException;
//        } else {
//            return nestedException instanceof HttpHostConnectException || nestedException instanceof
//            ConnectTimeoutException;
//        }
//    }

}
