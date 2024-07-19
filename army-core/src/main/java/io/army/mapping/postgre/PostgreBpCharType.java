package io.army.mapping.postgre;

import io.army.criteria.CriteriaException;
import io.army.dialect.Database;
import io.army.dialect.UnsupportedDialectException;
import io.army.executor.DataAccessException;
import io.army.mapping.MappingEnv;
import io.army.mapping.MappingType;
import io.army.mapping.StringType;
import io.army.mapping._ArmyBuildInType;
import io.army.meta.ServerMeta;
import io.army.sqltype.DataType;
import io.army.sqltype.PostgreType;


/**
 * <p>This class map {@link String} to postgre bpchar .
 * <p>If you need to map varchar ,you can use {@link StringType} instead of this class.
 *
 * @see <a href="https://www.postgresql.org/docs/current/datatype-character.html">bpchar</a>
 * @since 0.6.4
 */
public final class PostgreBpCharType extends _ArmyBuildInType implements MappingType.SqlStringType {

    public static PostgreBpCharType from(final Class<?> javaType) {
        if (javaType != String.class) {
            throw errorJavaType(PostgreBpCharType.class, javaType);
        }
        return INSTANCE;
    }

    public static final PostgreBpCharType INSTANCE = new PostgreBpCharType();

    /**
     * private constructor
     */
    private PostgreBpCharType() {
    }


    @Override
    public Class<?> javaType() {
        return String.class;
    }

    @Override
    public LengthType lengthType() {
        return LengthType.LONG;
    }

    @Override
    public DataType map(final ServerMeta meta) throws UnsupportedDialectException {
        if (meta.serverDatabase() != Database.PostgreSQL || meta.major() < 16) {
            throw MAP_ERROR_HANDLER.apply(this, meta);
        }
        return PostgreType.BPCHAR;
    }

    @Override
    public String convert(MappingEnv env, Object source) throws CriteriaException {
        return StringType.toString(this, map(env.serverMeta()), source, PARAM_ERROR_HANDLER);
    }

    @Override
    public String beforeBind(DataType dataType, MappingEnv env, Object source) throws CriteriaException {
        return StringType.toString(this, dataType, source, PARAM_ERROR_HANDLER);
    }

    @Override
    public String afterGet(DataType dataType, MappingEnv env, Object source) throws DataAccessException {
        return StringType.toString(this, dataType, source, ACCESS_ERROR_HANDLER);
    }


}
