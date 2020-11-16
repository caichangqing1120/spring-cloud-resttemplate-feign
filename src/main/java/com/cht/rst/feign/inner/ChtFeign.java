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

        private Contract contract = new SpringMvcContract();
        private Client client = new RestTemplateClient();
        private Retryer retryer = new Retryer.Default();
        private InvocationHandlerFactory invocationHandlerFactory = new InvocationHandlerFactory.Default();

        /**
         * set custom retryer
         * @param retryer the customized retryer
         * @return this
         */
        public ChtFeign.Builder retryer(Retryer retryer) {
            this.retryer = retryer;
            return this;
        }

        /**
         * set custom client
         * @param client the customized client
         * @return this
         */
        public ChtFeign.Builder client(Client client) {
            this.client = client;
            return this;
        }

        /**
         * generate the target proxy implement instance of this feign interface
         * @param target the middle data
         * @param <T> this feign interface
         * @return this proxy implement
         */
        public <T> T target(Target<T> target) {
            return build().newInstance(target);
        }

        public ChtFeign build() {
            SynchronousMethodHandler.Factory synchronousMethodHandlerFactory =
                    new SynchronousMethodHandler.Factory(client, retryer);
            ReflectiveFeign.ParseHandlersByName handlersByName =
                    new ReflectiveFeign.ParseHandlersByName(contract, synchronousMethodHandlerFactory);
            return new ReflectiveFeign(handlersByName, invocationHandlerFactory);
        }
    }
}
