package io.army.generator;

import io.army.ErrorCode;
import io.army.meta.FieldMeta;
import io.army.meta.GeneratorMeta;
import io.army.session.GenericSessionFactory;
import io.army.util.ReflectionUtils;
import io.army.util._Assert;
import io.army.util._ClassUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public abstract class GeneratorFactory {

    public static FieldGenerator getGenerator(FieldMeta<?> fieldMeta, GenericSessionFactory sessionFactory) {
        GeneratorMeta generatorMeta = fieldMeta.generator();
        _Assert.notNull(generatorMeta, "generatorMeta required");

        Method method = getBuilder(generatorMeta);

        try {
            FieldGenerator generator = (FieldGenerator) ReflectionUtils.invokeMethod(method, null
                    , fieldMeta, sessionFactory);
            if (generator == null) {
                throw new GeneratorException(ErrorCode.GENERATOR_ERROR, "Method[%s] return null"
                        , method);
            }
            return generator;
        } catch (Throwable e) {
            throw new GeneratorException(ErrorCode.GENERATOR_ERROR, e, "MultiGenerator[%s]  build method error"
                    , generatorMeta.javaType().getName());
        }
    }

    private static Method getBuilder(GeneratorMeta generatorMeta) {
        Method method = ReflectionUtils.findMethod(generatorMeta.javaType()
                , "build", FieldMeta.class, GenericSessionFactory.class);

        if (method == null
                || !Modifier.isStatic(method.getModifiers())
                || !Modifier.isPublic(method.getModifiers())) {
            throw new GeneratorException(ErrorCode.GENERATOR_ERROR, "FieldGenerator[%s] no build method"
                    , generatorMeta.javaType().getName());
        }
        if (!_ClassUtils.isAssignable(generatorMeta.javaType(), method.getReturnType())) {
            throw new GeneratorException(ErrorCode.GENERATOR_ERROR
                    , "FieldGenerator[%s] return type must be a %s instance"
                    , generatorMeta.javaType().getName()
                    , generatorMeta.javaType().getName()
            );
        }
        return method;
    }
}
