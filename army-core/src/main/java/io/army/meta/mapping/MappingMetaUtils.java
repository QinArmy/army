package io.army.meta.mapping;

import io.army.dialect.Database;
import io.army.dialect.NotSupportDialectException;

public abstract class MappingMetaUtils {

    protected MappingMetaUtils() {
        throw new UnsupportedOperationException();
    }

    public static NotSupportDialectException createNotSupportDialectException(
            MappingMeta mappingMeta, Database database) {
        return new NotSupportDialectException("%s not support database[%s]"
                , mappingMeta.getClass().getName(), database);
    }

    public static IllegalArgumentException createNotSupportJavaTypeException(Class<? extends MappingMeta> mappingMetaClass
            , Class<?> javaType) {
        return new IllegalArgumentException(
                String.format("%s not support java type[%s].", mappingMetaClass.getName(), javaType.getName()));
    }
}
