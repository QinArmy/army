package io.army.mapping.array;

import io.army.criteria.CriteriaException;
import io.army.dialect.UnsupportedDialectException;
import io.army.mapping.MappingEnv;
import io.army.mapping.MappingType;
import io.army.mapping._ArmyBuildInMapping;
import io.army.meta.ServerMeta;
import io.army.session.DataAccessException;
import io.army.sqltype.DataType;


/**
 * <p>Package class
 * <p>This class is base class of following :
 * <ul>
 *     <li>{@link TinyTextArrayType}</li>
 *     <li>{@link TextArrayType}</li>
 *     <li>{@link MediumTextArrayType}</li>
 * </ul>
 *
 * @since 1.0
 */
abstract class ArmyTextArrayType extends _ArmyBuildInMapping implements MappingType.SqlArrayType {

    final Class<?> javaType;

    /**
     * package constructor
     */
    ArmyTextArrayType(Class<?> javaType) {
        this.javaType = javaType;
    }

    @Override
    public final Class<?> javaType() {
        return this.javaType;
    }

    @Override
    public final Class<?> underlyingJavaType() {
        return String.class;
    }

    @Override
    public final DataType map(ServerMeta meta) throws UnsupportedDialectException {
        // currently ,same mapping
        return TextArrayType.mapToSqlType(this, meta);
    }

    @Override
    public final Object convert(MappingEnv env, Object source) throws CriteriaException {
        return PostgreArrays.arrayAfterGet(this, map(env.serverMeta()), source, false, PostgreArrays::decodeElement,
                PARAM_ERROR_HANDLER);
    }

    @Override
    public final String beforeBind(DataType dataType, MappingEnv env, Object source) throws CriteriaException {
        return PostgreArrays.arrayBeforeBind(source, TextArrayType::appendToText, dataType, this,
                PARAM_ERROR_HANDLER
        );
    }

    @Override
    public final Object afterGet(DataType dataType, MappingEnv env, Object source) throws DataAccessException {
        return PostgreArrays.arrayAfterGet(this, dataType, source, false,
                PostgreArrays::decodeElement, ACCESS_ERROR_HANDLER
        );
    }


}
