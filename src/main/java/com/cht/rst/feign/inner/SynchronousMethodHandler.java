package com.cht.rst.feign.inner;

import static com.cht.rst.feign.inner.Util.checkNotNull;

public class SynchronousMethodHandler implements InvocationHandlerFactory.MethodHandler {

    private final MethodMetadata metadata;
    private final Client client;
    private final Target<?> target;
    private final Retryer retryer;

    private SynchronousMethodHandler(Target<?> target, Client client, Retryer retryer,
                                     MethodMetadata metadata) {

        this.target = checkNotNull(target, "target");
        this.client = checkNotNull(client, "client for %s", target);
        this.retryer = checkNotNull(retryer, "retryer for %s", target);
        this.metadata = checkNotNull(metadata, "metadata for %s", target);
    }


    @Override
    public Object invoke(Object[] argv) throws Throwable {
        ChtFeignRequestTemplate template = metadata.template();
        target.apply(template);
        return client.execute(template, argv, metadata.returnType());
    }


    static class Factory {

        private final Client client;
        private final Retryer retryer;
//        private final List<RequestInterceptor> requestInterceptors;
//        private final Logger logger;
//        private final Logger.Level logLevel;
//        private final boolean decode404;
//        private final boolean closeAfterDecode;
//        private final ExceptionPropagationPolicy propagationPolicy;
//        private final boolean forceDecoding;

        Factory(Client client, Retryer retryer/*, List<RequestInterceptor> requestInterceptors,
                Logger logger, Logger.Level logLevel, boolean decode404, boolean closeAfterDecode,
                ExceptionPropagationPolicy propagationPolicy, boolean forceDecoding*/) {
            this.client = checkNotNull(client, "client");
            this.retryer = checkNotNull(retryer, "retryer");
//            this.requestInterceptors = checkNotNull(requestInterceptors, "requestInterceptors");
//            this.logger = checkNotNull(logger, "logger");
//            this.logLevel = checkNotNull(logLevel, "logLevel");
//            this.decode404 = decode404;
//            this.closeAfterDecode = closeAfterDecode;
//            this.propagationPolicy = propagationPolicy;
//            this.forceDecoding = forceDecoding;
        }

        public InvocationHandlerFactory.MethodHandler create(Target<?> target, MethodMetadata md) {
            return new SynchronousMethodHandler(target, client, retryer, md);
        }
    }
}
