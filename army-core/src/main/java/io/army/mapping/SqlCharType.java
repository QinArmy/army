package io.army.mapping;

import io.army.criteria.CriteriaException;
import io.army.meta.ServerMeta;
import io.army.sqltype.*;

public final class SqlCharType extends _ArmyBuildInMapping implements MappingType.SqlStringType {

    public static SqlCharType from(Class<?> javaType) {
        if (javaType != String.class) {
            throw errorJavaType(SqlCharType.class, javaType);
        }
        return INSTANCE;
    }

    public static final SqlCharType INSTANCE = new SqlCharType();

    /**
     * private constructor
     */
    private SqlCharType() {
    }

    @Override
    public Class<?> javaType() {
        return String.class;
    }

    @Override
    public LengthType lengthType() {
        return LengthType.TINY;
    }

    @Override
    public DataType map(final ServerMeta meta) {
        final SqlType type;
        switch (meta.serverDatabase()) {
            case MySQL:
                type = MySQLType.CHAR;
                break;
            case PostgreSQL:
                type = PostgreType.CHAR;
                break;
            case Oracle:
                type = OracleDataType.CHAR;
                break;
            case H2:
            default:
                throw MAP_ERROR_HANDLER.apply(this, meta);

        }
        return type;
    }


    @Override
    public <Z> MappingType compatibleFor(final DataType dataType, final Class<Z> targetType) throws NoMatchMappingException {
        return null;
    }

    @Override
    public String convert(MappingEnv env, Object source) throws CriteriaException {
        return StringType.toString(this, map(env.serverMeta()), source, PARAM_ERROR_HANDLER);
    }

    @Override
    public String beforeBind(DataType dataType, MappingEnv env, Object source) {
        return StringType.toString(this, dataType, source, PARAM_ERROR_HANDLER);
    }

    @Override
    public String afterGet(DataType dataType, MappingEnv env, Object source) {
        return StringType.toString(this, dataType, source, ACCESS_ERROR_HANDLER);
    }





}
