package io.army.meta.mapping;

import io.army.domain.IDomain;
import io.army.util.Precision;
import org.springframework.lang.NonNull;

import java.sql.JDBCType;
import java.sql.SQLException;

public final class BooleanMapping extends AbstractMappingType<Boolean> {

    public static final BooleanMapping INSTANCE = new BooleanMapping();

    private static final Precision PRECISION = new Precision(1, 0);


    private BooleanMapping() {
    }

    @Override
    public Class<?> javaType() {
        return Boolean.class;
    }

    @Override
    public JDBCType jdbcType() {
        return JDBCType.CHAR;
    }


    @Override
    protected Object nonNullToSql(Boolean aBoolean) {
        return aBoolean ? IDomain.Y : IDomain.N;
    }

    @Override
    protected Boolean nonNullToJava(Object databaseValue) throws SQLException {
        Boolean value;
        if (databaseValue instanceof Character) {
            char charValue = (Character) databaseValue;
            if (charValue == 'Y') {
                value = Boolean.TRUE;
            } else if (charValue == 'N') {
                value = Boolean.FALSE;
            } else {
                throw convertToJavaException(databaseValue, javaType());
            }
        } else if (databaseValue instanceof String) {
            String textValue = (String) databaseValue;
            if ("Y".equals(textValue)) {
                value = Boolean.TRUE;
            } else if ("N".equals(textValue)) {
                value = Boolean.FALSE;
            } else {
                throw convertToJavaException(databaseValue, javaType());
            }
        } else {
            throw convertToJavaException(databaseValue, javaType());
        }
        return value;
    }


    @NonNull
    @Override
    public Precision precision() {
        return PRECISION;
    }
}
