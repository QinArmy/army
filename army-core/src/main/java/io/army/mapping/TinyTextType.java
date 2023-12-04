package io.army.mapping;

import io.army.dialect.UnsupportedDialectException;
import io.army.mapping.array.TinyTextArrayType;
import io.army.meta.ServerMeta;
import io.army.sqltype.DataType;
import io.army.sqltype.MySQLType;
import io.army.sqltype.PostgreType;
import io.army.sqltype.SqlType;

public final class TinyTextType extends ArmyTextType {

    public static TinyTextType from(final Class<?> javaType) {
        if (javaType != String.class) {
            throw errorJavaType(TinyTextType.class, javaType);
        }
        return INSTANCE;
    }

    public static final TinyTextType INSTANCE = new TinyTextType();

    /**
     * private constructor
     */
    private TinyTextType() {
    }


    @Override
    public LengthType lengthType() {
        return LengthType.DEFAULT;
    }

    @Override
    public MappingType arrayTypeOfThis() {
        return TinyTextArrayType.LINEAR;
    }


    @Override
    public DataType map(ServerMeta meta) throws UnsupportedDialectException {
        final SqlType sqlType;
        switch (meta.serverDatabase()) {
            case MySQL:
                sqlType = MySQLType.TINYTEXT;
                break;
            case PostgreSQL:
                sqlType = PostgreType.TEXT;
                break;
            default:
                throw MAP_ERROR_HANDLER.apply(this, meta);
        }
        return sqlType;
    }


}
