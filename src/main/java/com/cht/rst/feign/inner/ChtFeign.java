package com.cht.rst.feign.inner;


import com.cht.rst.feign.SpringMvcContract;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * ChtFeign is easy way for api invocation with restful fashion underling spring'restTemple to establish http
 * connections
 * @author cht
 */
public abstract class ChtFeign {

    public static Builder builder() {
        return new Builder();
    }

    public static String configKey(Class targetType, Method method) {
        StringBuilder builder = new StringBuilder();
        builder.append(targetType.getSimpleName());
        builder.append('#').append(method.getName()).append('(');
        for (Type param : method.getGenericParameterTypes()) {
            param = Types.resolve(targetType, targetType, param);
            builder.append(Types.getRawType(param).getSimpleName()).append(',');
        }
        if (method.getParameterTypes().length > 0) {
            builder.deleteCharAt(builder.length() - 1);
        }
        return builder.append(')').toString();
    }

    public abstract <T> T newInstance(Target<T> target);

    public static class Builder {

//        private final List<RequestInterceptor> requestInterceptors = new ArrayList<RequestInterceptor>();
        private Logger.Level logLevel = Logger.Level.NONE;
        private Contract contract = new SpringMvcContract();
        private Client client = new RestTemplateClient();
        private Retryer retryer = new Retryer.Default();
//        private Logger logger = new Logger.NoOpLogger();
//        private QueryMapEncoder queryMapEncoder = new QueryMapEncoder.Default();
//        private ErrorDecoder errorDecoder = new ErrorDecoder.Default();
//        private Request.Options options = new Request.Options();
        private InvocationHandlerFactory invocationHandlerFactory = new InvocationHandlerFactory.Default();
        private boolean closeAfterDecode = true;
//        private ExceptionPropagationPolicy propagationPolicy = NONE;

        public ChtFeign.Builder logLevel(Logger.Level logLevel) {
            this.logLevel = logLevel;
            return this;
        }

        public ChtFeign.Builder contract(Contract contract) {
            this.contract = contract;
            return this;
        }

        public ChtFeign.Builder client(Client client) {
            this.client = client;
            return this;
        }

        public ChtFeign.Builder retryer(Retryer retryer) {
            this.retryer = retryer;
            return this;
        }

//        public ChtFeign.Builder logger(Logger logger) {
//            this.logger = logger;
//            return this;
//        }
//
//
//        public ChtFeign.Builder options(Request.Options options) {
//            this.options = options;
//            return this;
//        }

        /**
         * Adds a single request interceptor to the builder.
         */
//        public ChtFeign.Builder requestInterceptor(RequestInterceptor requestInterceptor) {
//            this.requestInterceptors.add(requestInterceptor);
//            return this;
//        }

        /**
         * Sets the full set of request interceptors for the builder, overwriting any previous
         * interceptors.
         */
//        public ChtFeign.Builder requestInterceptors(Iterable<RequestInterceptor> requestInterceptors) {
//            this.requestInterceptors.clear();
//            for (RequestInterceptor requestInterceptor : requestInterceptors) {
//                this.requestInterceptors.add(requestInterceptor);
//            }
//            return this;
//        }

        /**
         * Allows you to override how reflective dispatch works inside of Feign.
         */
        public ChtFeign.Builder invocationHandlerFactory(InvocationHandlerFactory invocationHandlerFactory) {
            this.invocationHandlerFactory = invocationHandlerFactory;
            return this;
        }

        /**
         * This flag indicates that the response should not be automatically closed upon completion of
         * decoding the message. This should be set if you plan on processing the response into a
         * lazy-evaluated construct, such as a {@link java.util.Iterator}.
         *
         * </p>
         * Feign standard decoders do not have built in support for this flag. If you are using this
         * flag, you MUST also use a custom Decoder, and be sure to close all resources appropriately
         * somewhere in the Decoder (you can use {@link Util#ensureClosed} for convenience).
         *
         * @since 9.6
         */
        public ChtFeign.Builder doNotCloseAfterDecode() {
            this.closeAfterDecode = false;
            return this;
        }

//        public ChtFeign.Builder exceptionPropagationPolicy(ExceptionPropagationPolicy propagationPolicy) {
//            this.propagationPolicy = propagationPolicy;
//            return this;
//        }

        public <T> T target(Class<T> apiType, String url) {
            return target(new Target.HardCodedTarget<T>(apiType, url));
        }

        public <T> T target(Target<T> target) {
            return build().newInstance(target);
        }

        public ChtFeign build() {
            SynchronousMethodHandler.Factory synchronousMethodHandlerFactory =
                    new SynchronousMethodHandler.Factory(client, retryer);
            ReflectiveFeign.ParseHandlersByName handlersByName =
                    new ReflectiveFeign.ParseHandlersByName(contract, /*, options, encoder, decoder, queryMapEncoder,
                            errorDecoder, */synchronousMethodHandlerFactory);
            return new ReflectiveFeign(handlersByName, invocationHandlerFactory);
        }
    }
}
