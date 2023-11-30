package io.army.mapping.array;

import io.army.criteria.CriteriaException;
import io.army.dialect.Database;
import io.army.dialect.UnsupportedDialectException;
import io.army.mapping.*;
import io.army.meta.ServerMeta;
import io.army.session.DataAccessException;
import io.army.sqltype.DataType;
import io.army.sqltype.PostgreType;
import io.army.sqltype.SqlType;
import io.army.util.ArrayUtils;

import javax.annotation.Nullable;
import java.lang.ref.SoftReference;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

public final class TextArrayType extends _ArmyBuildInMapping implements MappingType.SqlArrayType,
        MappingType.SqlStringType {


    public static TextArrayType from(final Class<?> javaType) {
        final TextArrayType instance;
        if (javaType == List.class) {
            instance = LIST;
        } else if (javaType == String[].class) {
            instance = LINEAR;
        } else if (javaType == Object.class) {
            instance = UNLIMITED;
        } else if (List.class.isAssignableFrom(javaType)
                || (javaType.isArray() && ArrayUtils.underlyingComponent(javaType) == String.class)) {
            instance = getInstance(javaType);
        } else {
            throw errorJavaType(StringArrayType.class, javaType);
        }
        return instance;
    }

    public static TextArrayType supplier(final Supplier<List<String>> constructor) {
        Objects.requireNonNull(constructor);
        return new TextArrayType(constructor);
    }

    private static TextArrayType getInstance(final Class<?> javaType) {
        final SoftReference<TextArrayType> reference;
        reference = INSTANCE_MAP.compute(javaType, TextArrayType::compute);

        TextArrayType instance;
        instance = reference.get();
        if (instance == null) {
            instance = new TextArrayType(javaType);
        }
        return instance;
    }

    private static SoftReference<TextArrayType> compute(final Class<?> javaType,
                                                        final @Nullable SoftReference<TextArrayType> old) {
        final SoftReference<TextArrayType> reference;
        if (old == null || old.get() == null) {
            reference = new InstanceRef<>(new TextArrayType(javaType));
        } else {
            reference = old;
        }
        return reference;
    }


    public static final TextArrayType UNLIMITED = new TextArrayType(Object.class);
    public static final TextArrayType LINEAR = new TextArrayType(String[].class);

    public static final TextArrayType LIST = new TextArrayType(List.class);

    private static final ConcurrentMap<Class<?>, SoftReference<TextArrayType>> INSTANCE_MAP = new ConcurrentHashMap<>();


    private final Object source;


    private TextArrayType(Class<?> source) {
        this.source = source;
    }

    private TextArrayType(Supplier<List<String>> source) {
        this.source = source;
    }


    @Override
    public Class<?> javaType() {
        final Object source = this.source;
        final Class<?> javaClass;
        if (source instanceof Class) {
            javaClass = (Class<?>) source;
        } else if (source instanceof Supplier) {
            javaClass = List.class;
        } else {
            // no bug,never here
            throw new IllegalStateException();
        }
        return javaClass;
    }

    @Override
    public MappingType elementType() {
        return TextType.INSTANCE;
    }

    @Override
    public LengthType lengthType() {
        return LengthType.DEFAULT;
    }

    @Override
    public SqlType map(final ServerMeta meta) throws UnsupportedDialectException {
        if (meta.serverDatabase() != Database.PostgreSQL) {
            throw MAP_ERROR_HANDLER.apply(this, meta);
        }
        return PostgreType.TEXT_ARRAY;
    }

    @Override
    public <Z> MappingType compatibleFor(Class<Z> targetType) throws NoMatchMappingException {
        return null;
    }

    @Override
    public Object convert(MappingEnv env, Object source) throws CriteriaException {
        // TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public Object beforeBind(DataType dataType, MappingEnv env, Object source) throws CriteriaException {
//        final Object value;
//        if (source instanceof String || source.getClass().isArray()) {
//            value = source;
//        } else if (source instanceof List) {
//            value = listToArray(type, (List<?>) source);
//        } else {
//            // TODO
//
//        }
//        return value;
        throw new UnsupportedOperationException();
    }

    @Override
    public Object afterGet(DataType dataType, MappingEnv env, Object source) throws DataAccessException {
        // TODO
        throw new UnsupportedOperationException();
    }


    private String[] listToArray(final SqlType type, final List<?> list) {
        final String[] array = new String[list.size()];
        Object element;
        for (int i = 0; i < array.length; i++) {
            element = list.get(i);
            if (element == null) {
                array[i] = null;
            } else if (element instanceof String) {
                array[i] = (String) element;
            } else {
                throw PARAM_ERROR_HANDLER.apply(this, type, list, null);
            }
        }
        return array;
    }


}
