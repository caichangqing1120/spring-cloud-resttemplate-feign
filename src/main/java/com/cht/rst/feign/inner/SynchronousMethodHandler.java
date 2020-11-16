package com.cht.rst.feign.inner;

import static com.cht.rst.feign.inner.Util.checkNotNull;

public class SynchronousMethodHandler implements InvocationHandlerFactory.MethodHandler {

    private final MethodMetadata metadata;
    private final Client client;
    private final Target<?> target;
    private final Retryer retryer;

    private SynchronousMethodHandler(Target<?> target,
                                     Client client, Retryer retryer,
                                     MethodMetadata metadata) {

        this.target = checkNotNull(target, "target");
        this.client = checkNotNull(client, "client for %s", target);
        this.retryer = checkNotNull(retryer, "retryer for %s", target);
        this.metadata = checkNotNull(metadata, "metadata for %s", target);

    }


    @Override
    public Object invoke(Object[] argv) throws Throwable {
        return client.execute(metadata, argv);
    }


    static class Factory {

        private final Client client;
        private final Retryer retryer;

        Factory(Client client, Retryer retryer) {
            this.client = checkNotNull(client, "client");
            this.retryer = checkNotNull(retryer, "retryer");
        }

        public InvocationHandlerFactory.MethodHandler create(Target<?> target, MethodMetadata md) {
            return new SynchronousMethodHandler(target, client, retryer, md);
        }
    }
}
