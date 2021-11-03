package io.army.mapping;

import io.army.meta.ServerMeta;
import io.army.sqldatatype.SqlType;

import java.sql.JDBCType;

public final class DoubleType extends AbstractMappingType {


    public static final DoubleType INSTANCE = new DoubleType();

    public static DoubleType build(Class<?> typeClass) {
        if (typeClass != Double.class) {
            throw MappingMetaUtils.createNotSupportJavaTypeException(DoubleType.class, typeClass);
        }
        return INSTANCE;
    }

    private DoubleType() {
    }

    @Override
    public Class<?> javaType() {
        return Double.class;
    }

    @Override
    public JDBCType jdbcType() {
        return JDBCType.DOUBLE;
    }

    @Override
    public SqlType sqlDataType(ServerMeta serverMeta) throws NoMappingException {
        return null;
    }

    @Override
    public Object convertBeforeBind(ServerMeta serverMeta, Object nonNull) {
        return null;
    }

    @Override
    public Object convertAfterGet(ServerMeta serverMeta, Object nonNull) {
        if (!(nonNull instanceof Double)) {
            throw notSupportConvertAfterGet(nonNull);
        }
        return nonNull;
    }


}
