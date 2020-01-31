package io.army.generator;

import io.army.ErrorCode;
import io.army.env.Environment;
import io.army.meta.FieldMeta;
import io.army.meta.GeneratorMeta;
import io.army.util.Assert;
import io.army.util.ReflectionUtils;

import java.lang.reflect.Method;

public abstract class GeneratorFactory {

    public static MultiGenerator getGenerator(FieldMeta<?, ?> fieldMeta, Environment env) {
        GeneratorMeta generatorMeta = fieldMeta.generator();
        Assert.notNull(generatorMeta, "generatorMeta required");

        Method method = getBuilder(generatorMeta);

        try {
            MultiGenerator generator = (MultiGenerator) ReflectionUtils.invokeMethod(method, null, fieldMeta, env);
            if (generator == null) {
                throw new GeneratorException(ErrorCode.GENERATOR_ERROR, "Method[%s] return null"
                        , method);
            }
            return generator;
        } catch (Throwable e) {
            throw new GeneratorException(ErrorCode.GENERATOR_ERROR, "MultiGenerator[%s]  getInstance method error"
                    , generatorMeta.type().getName());
        }
    }

    private static Method getBuilder(GeneratorMeta generatorMeta) {
        Method method = ReflectionUtils.findMethod(generatorMeta.type()
                , "getInstance", FieldMeta.class, Environment.class);
        if (method == null) {
            throw new GeneratorException(ErrorCode.GENERATOR_ERROR, "MultiGenerator[%s] no getInstance method"
                    , generatorMeta.type().getName());
        }
        return method;
    }
}
