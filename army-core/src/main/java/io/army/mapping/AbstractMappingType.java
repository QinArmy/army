package io.army.mapping;

import io.army.dialect.NotSupportDialectException;
import io.army.lang.Nullable;
import io.army.meta.ServerMeta;
import io.army.session.ParamException;

public abstract class AbstractMappingType implements MappingType {

    protected AbstractMappingType() {
    }

    @Override
    public final int hashCode() {
        return super.hashCode();
    }

    @Override
    public final boolean equals(Object obj) {
        return this == obj;
    }

    @Override
    public final MappingType mappingMeta() {
        return this;
    }


    @Override
    public final String toString() {
        return String.format("%s javaType[%s] jdbcType[%s]", super.toString(), javaType().getName(), jdbcType());
    }

    protected final ParamException notSupportConvertBeforeBind(final Object nonNull) {
        String m = String.format("Not support convert %s for %s bind.", nonNull, javaType().getName());
        return new ParamException(m);
    }

    protected final ParamException notSupportConvertAfterGet(final Object nonNull) {
        String m = String.format("Not support convert from %s to %s.", nonNull, javaType().getName());
        return new ParamException(m);
    }


    protected final ParamException outRangeOfType(final Object nonNull, @Nullable final Throwable cause) {
        String m = String.format("Parameter[%s] value[%s] out of range %s"
                , nonNull.getClass().getName(), nonNull, this);
        return cause == null ? new ParamException(m) : new ParamException(m, cause);
    }


    protected final NotSupportDialectException noMappingError(ServerMeta serverMeta) {
        String m = String.format("No mapping from java type[%s] to Server[%s]", javaType(), serverMeta);
        return new NotSupportDialectException(m);
    }

    protected static IllegalArgumentException createNotSupportJavaTypeException(
            Class<? extends MappingType> mappingMetaClass, Class<?> javaType) {
        return new IllegalArgumentException(
                String.format("%s not support java type[%s].", mappingMetaClass.getName(), javaType.getName()));
    }


}
