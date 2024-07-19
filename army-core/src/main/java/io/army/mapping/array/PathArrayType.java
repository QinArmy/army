package io.army.mapping.array;

import io.army.criteria.CriteriaException;
import io.army.dialect.UnsupportedDialectException;
import io.army.executor.DataAccessException;
import io.army.mapping.MappingEnv;
import io.army.mapping.MappingType;
import io.army.mapping.PathType;
import io.army.mapping._ArmyBuildInType;
import io.army.meta.ServerMeta;
import io.army.sqltype.DataType;
import io.army.util.ArrayUtils;

import java.nio.file.Path;
import java.nio.file.Paths;

public class PathArrayType extends _ArmyBuildInType implements MappingType.SqlArrayType {


    public static PathArrayType from(final Class<?> arrayType) {
        final PathArrayType instance;
        if (arrayType == Path[].class) {
            instance = LINEAR;
        } else if (arrayType.isArray() && ArrayUtils.underlyingComponent(arrayType) == Path.class) {
            instance = new PathArrayType(arrayType);
        } else {
            throw errorJavaType(PathArrayType.class, arrayType);
        }
        return instance;
    }

    public static PathArrayType fromUnlimited() {
        return UNLIMITED;
    }


    public static final PathArrayType UNLIMITED = new PathArrayType(Object.class);

    public static final PathArrayType LINEAR = new PathArrayType(Path[].class);

    private final Class<?> javaType;

    /**
     * private constructor
     */
    private PathArrayType(Class<?> javaType) {
        this.javaType = javaType;
    }

    @Override
    public Class<?> javaType() {
        return this.javaType;
    }

    @Override
    public Class<?> underlyingJavaType() {
        return String.class;
    }

    @Override
    public MappingType elementType() {
        final Class<?> javaType = this.javaType;
        final MappingType instance;
        if (javaType == Object.class) {
            instance = this;
        } else if (javaType == Path[].class) {
            instance = PathType.INSTANCE;
        } else {
            instance = from(javaType.getComponentType());
        }
        return instance;
    }

    @Override
    public MappingType arrayTypeOfThis() throws CriteriaException {
        final Class<?> javaType = this.javaType;
        if (javaType == Object.class) { // unlimited dimension array
            return this;
        }
        return from(ArrayUtils.arrayClassOf(javaType));
    }

    @Override
    public DataType map(ServerMeta meta) throws UnsupportedDialectException {
        return StringArrayType.mapToSqlType(this, meta);
    }

    @Override
    public Object convert(MappingEnv env, Object source) throws CriteriaException {
        return PostgreArrays.arrayAfterGet(this, map(env.serverMeta()), source, false, PathArrayType::decodeElement,
                PARAM_ERROR_HANDLER);
    }

    @Override
    public String beforeBind(DataType dataType, MappingEnv env, Object source) throws CriteriaException {
        return PostgreArrays.arrayBeforeBind(source, PathArrayType::appendToText, dataType, this,
                PARAM_ERROR_HANDLER
        );
    }

    @Override
    public Object afterGet(DataType dataType, MappingEnv env, Object source) throws DataAccessException {
        return PostgreArrays.arrayAfterGet(this, dataType, source, false,
                PathArrayType::decodeElement, ACCESS_ERROR_HANDLER
        );
    }


    private static void appendToText(final Object element, final StringBuilder builder) {

        final String value;
        if (element instanceof Path) {
            value = element.toString();
        } else if (element instanceof String) {
            value = (String) element;
        } else {
            throw new IllegalArgumentException("Not java.nio.file.Path or java.lang.String");
        }
        PostgreArrays.encodeElement(value, builder);
    }


    private static Path decodeElement(final String text, final int offset, final int end) {
        return Paths.get(text.substring(offset, end));
    }


}
