package com.cht.rst.feign.annotatin;

import com.cht.rst.feign.AnnotatedParameterProcessor;
import com.cht.rst.feign.inner.MethodMetadata;
import org.springframework.web.bind.annotation.RequestHeader;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;

import static com.cht.rst.feign.inner.Util.checkState;
import static com.cht.rst.feign.inner.Util.emptyToNull;

public class RequestHeaderParameterProcessor implements AnnotatedParameterProcessor {

    private static final Class<RequestHeader> ANNOTATION = RequestHeader.class;

    @Override
    public Class<? extends Annotation> getAnnotationType() {
        return ANNOTATION;
    }

    @Override
    public boolean processArgument(AnnotatedParameterContext context,
                                   Annotation annotation, Method method) {
//        int parameterIndex = context.getParameterIndex();
//        Class<?> parameterType = method.getParameterTypes()[parameterIndex];
//        MethodMetadata data = context.getMethodMetadata();
//
//        if (Map.class.isAssignableFrom(parameterType)) {
//            checkState(data.headerMapIndex() == null,
//                    "Header map can only be present once.");
//            data.headerMapIndex(parameterIndex);
//
//            return true;
//        }
//
//        String name = ANNOTATION.cast(annotation).value();
//        checkState(emptyToNull(name) != null,
//                "RequestHeader.value() was empty on parameter %s", parameterIndex);
//        context.setParameterName(name);
//        //设置头
//        Collection<String> header = context.setTemplateParameter(name,
//                (Collection<String>) (data.template().headers()).get(name));
//        data.template().header(name, header);
        return true;
    }

}