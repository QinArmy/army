package io.army.mapping.array;

import io.army.criteria.CriteriaException;
import io.army.dialect.Database;
import io.army.dialect.UnsupportedDialectException;
import io.army.mapping.MappingEnv;
import io.army.mapping.MappingType;
import io.army.mapping.NoMatchMappingException;
import io.army.mapping._ArmyBuildInMapping;
import io.army.meta.ServerMeta;
import io.army.session.DataAccessException;
import io.army.sqltype.DataType;
import io.army.sqltype.PostgreType;
import io.army.util.ArrayUtils;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;

public final class StringArrayType extends _ArmyBuildInMapping {

    public static StringArrayType from(final Class<?> javaType) {
        if (!javaType.isArray()) {
            throw errorJavaType(StringArrayType.class, javaType);
        }

        return INSTANCE_MAP.computeIfAbsent(javaType, StringArrayType::new);
    }

    public static final StringArrayType UNLIMITED = new StringArrayType();

    public static final StringArrayType LINEAR = new StringArrayType(String[].class);

    private static final ConcurrentMap<Class<?>, StringArrayType> INSTANCE_MAP = new ConcurrentHashMap<>();


    private final Class<?> javaType;

    private final Class<?> underlyingType;

    /**
     * @see #UNLIMITED
     */
    private StringArrayType() {
        this.javaType = Object.class;
        this.underlyingType = Object.class;
    }

    private StringArrayType(Class<?> javaType) {
        this.javaType = javaType;
        this.underlyingType = ArrayUtils.underlyingComponent(javaType);
    }

    @Override
    public Class<?> javaType() {
        return this.javaType;
    }

    @Override
    public DataType map(final ServerMeta meta) throws UnsupportedDialectException {
        if (meta.serverDatabase() != Database.PostgreSQL) {
            throw MAP_ERROR_HANDLER.apply(this, meta);
        }
        return PostgreType.VARCHAR_ARRAY;
    }

    @Override
    public <Z> MappingType compatibleFor(final DataType dataType, final Class<Z> targetType) throws NoMatchMappingException {
        return null;
    }

    @Override
    public Object convert(MappingEnv env, Object source) throws CriteriaException {
        // TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public Object beforeBind(DataType dataType, MappingEnv env, Object source) throws CriteriaException {
        // TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public Object afterGet(DataType dataType, MappingEnv env, Object source) throws DataAccessException {
        // TODO
        throw new UnsupportedOperationException();
    }


    public static String parseDecimal(String text, int offset, int end) {
        return text.substring(offset, end);
    }

    public static void appendToText(final Object element, final Consumer<String> appender) {
        if (!(element instanceof String)) {
            // no bug,never here
            throw new IllegalArgumentException();
        }
        appender.accept((String) element);
    }


}
