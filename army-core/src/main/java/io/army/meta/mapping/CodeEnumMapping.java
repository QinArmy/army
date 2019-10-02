package io.army.meta.mapping;

import io.army.struct.CodeEnum;
import io.army.util.Precision;

import javax.annotation.Nonnull;
import java.lang.reflect.Constructor;
import java.sql.JDBCType;
import java.sql.SQLException;

/**
 * @see Enum
 * @see io.army.struct.CodeEnum
 */
public class CodeEnumMapping<T extends Enum<T> & CodeEnum> extends AbstractMappingType<T> {

    static final Constructor<CodeEnumMapping> CONSTRUCTOR;

    static {
        try {
            CONSTRUCTOR = CodeEnumMapping.class.getConstructor(Class.class);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private final Class<T> javaType;


    public CodeEnumMapping(Class<T> javaType) {
        this.javaType = javaType;
    }

    @Override
    public Class<?> javaType() {
        return javaType;
    }

    @Override
    public JDBCType jdbcType() {
        return JDBCType.INTEGER;
    }

    @Override
    protected Object nonNullToSql(T t) {
        return t.code();
    }

    @Override
    protected T nonNullToJava(Object databaseValue) throws SQLException {
        int code;
        if (databaseValue instanceof Integer) {
            code = (Integer) databaseValue;
        } else if (databaseValue instanceof String) {
            code = Integer.parseInt((String) databaseValue);
        } else {
            throw convertToJavaException(databaseValue, javaType);
        }
        T t = CodeEnum.getCodeMap(this.javaType).get(code);
        if (t == null) {
            throw convertToJavaException(databaseValue, javaType);
        }
        return t;
    }

    @Nonnull
    @Override
    public Precision precision() {
        return Precision.DEFAULT_INT_PRECISION;
    }
}
