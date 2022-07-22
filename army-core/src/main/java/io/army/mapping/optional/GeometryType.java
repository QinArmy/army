package io.army.mapping.optional;

import io.army.dialect.Database;
import io.army.mapping.AbstractMappingType;
import io.army.mapping.MappingEnv;
import io.army.meta.ServerMeta;
import io.army.sqltype.MySqlType;
import io.army.sqltype.SqlType;

import java.nio.file.Path;

/**
 * @see <a href="https://www.ogc.org/standards/sfa">Simple Feature Access - Part 1: Common Architecture PDF</a>
 */
public final class GeometryType extends AbstractMappingType {

    public static final GeometryType BYTES_INSTANCE = new GeometryType(byte[].class);

    public static final GeometryType STRING_INSTANCE = new GeometryType(String.class);

    public static final GeometryType PATH_INSTANCE = new GeometryType(Path.class);

    public static GeometryType from(final Class<?> javaType) {
        final GeometryType instance;
        if (javaType == byte[].class) {
            instance = BYTES_INSTANCE;
        } else if (javaType == String.class) {
            instance = STRING_INSTANCE;
        } else if (javaType == Path.class) {
            instance = PATH_INSTANCE;
        } else {
            throw errorJavaType(GeometryType.class, javaType);
        }
        return instance;
    }


    private final Class<?> javaType;

    private GeometryType(Class<?> javaType) {
        this.javaType = javaType;
    }

    @Override
    public Class<?> javaType() {
        return this.javaType;
    }

    @Override
    public SqlType map(final ServerMeta meta) {
        if (meta.database() != Database.MySQL) {
            throw noMappingError(meta);
        }
        return MySqlType.GEOMETRY;
    }

    @Override
    public Object beforeBind(SqlType sqlType, MappingEnv env, Object nonNull) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object afterGet(SqlType sqlType, MappingEnv env, Object nonNull) {
        throw new UnsupportedOperationException();
    }


}
