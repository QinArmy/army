package io.army.example.common;

import io.army.generator.FieldGenerator;
import io.army.generator.FieldGeneratorFactory;
import io.army.generator.FieldGeneratorUtils;
import io.army.generator.UUIDGenerator;
import io.army.generator.snowflake.SingleJvmSnowflakeClient;
import io.army.generator.snowflake.SnowflakeGenerator;
import io.army.meta.FieldMeta;
import io.army.meta.GeneratorMeta;

public final class SimpleFieldGeneratorFactory implements FieldGeneratorFactory {


    @Override
    public FieldGenerator get(final FieldMeta<?> field) {
        final GeneratorMeta meta;
        meta = field.generator();
        if (meta == null) {
            throw FieldGeneratorUtils.noGeneratorMeta(field);
        }
        final Class<?> javaType = field.javaType();
        final FieldGenerator fieldGenerator;
        if (javaType == SnowflakeGenerator.class) {
            fieldGenerator = SnowflakeGenerator.create(field, SingleJvmSnowflakeClient.INSTANCE);
        } else if (javaType == UUIDGenerator.class) {
            fieldGenerator = UUIDGenerator.create(field);
        } else {
            String m = String.format("Don't support %s.", javaType.getName());
            throw new IllegalArgumentException(m);
        }
        return fieldGenerator;
    }


}
