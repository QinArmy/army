package io.army.generator;

import io.army.bean.ReadWrapper;
import io.army.meta.FieldMeta;

import java.util.UUID;

public final class UUIDGenerator implements FieldGenerator {

    private static final UUIDGenerator INSTANCE = new UUIDGenerator();

    public static UUIDGenerator create(final FieldMeta<?> field) {
        if (field.javaType() != String.class) {
            throw errorFiled(field);
        }
        return INSTANCE;
    }

    @Override
    public Object next(FieldMeta<?> field, ReadWrapper domain) throws GeneratorException {
        if (field.javaType() != String.class) {
            throw errorFiled(field);
        }
        return UUID.randomUUID().toString();
    }

    private static IllegalArgumentException errorFiled(FieldMeta<?> field) {
        String m = String.format("%s java type isn't %s.", field, field.javaType().getName());
        return new IllegalArgumentException(m);
    }


}
