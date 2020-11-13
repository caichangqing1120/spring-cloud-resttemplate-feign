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

        public ChtFeign.Builder retryer(Retryer retryer) {
            this.retryer = retryer;
            return this;
        }

        public ChtFeign.Builder client(Client client) {
            this.client = client;
            return this;
        }

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
                    new ReflectiveFeign.ParseHandlersByName(contract, synchronousMethodHandlerFactory);
            return new ReflectiveFeign(handlersByName, invocationHandlerFactory);
        }
    }
}
