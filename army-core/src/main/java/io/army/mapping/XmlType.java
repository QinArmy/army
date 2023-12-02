package io.army.mapping;

import io.army.criteria.CriteriaException;
import io.army.meta.ServerMeta;
import io.army.sqltype.DataType;
import io.army.sqltype.MySQLType;
import io.army.sqltype.PostgreType;
import io.army.sqltype.SqlType;
import io.army.util._Collections;

import java.util.concurrent.ConcurrentMap;

public final class XmlType extends _ArmyBuildInMapping {


    public static XmlType from(final Class<?> javaType) {
        final XmlType instance;
        if (javaType == String.class) {
            instance = TEXT;
        } else {
            instance = INSTANCE_MAP.computeIfAbsent(javaType, XmlType::new);
        }
        return instance;
    }

    public static final XmlType TEXT = new XmlType(String.class);

    private static final ConcurrentMap<Class<?>, XmlType> INSTANCE_MAP = _Collections.concurrentHashMap();

    private final Class<?> javaType;

    /**
     * private constructor
     */
    private XmlType(Class<?> javaType) {
        this.javaType = javaType;
    }

    @Override
    public Class<?> javaType() {
        return this.javaType;
    }

    @Override
    public DataType map(final ServerMeta meta) {
        final SqlType sqlDataType;
        switch (meta.serverDatabase()) {
            case MySQL:
                sqlDataType = MySQLType.TEXT;
                break;
            case PostgreSQL:
                sqlDataType = PostgreType.XML;
                break;
            case Oracle:

            case H2:
            default:
                throw MAP_ERROR_HANDLER.apply(this, meta);

        }
        return sqlDataType;
    }

    @Override
    public <Z> MappingType compatibleFor(final DataType dataType, final Class<Z> targetType) throws NoMatchMappingException {
        return null;
    }

    @Override
    public Object convert(MappingEnv env, Object source) throws CriteriaException {
        //TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public String beforeBind(DataType dataType, MappingEnv env, Object source) {
        //TODO
        if (source instanceof String) {
            return (String) source;
        }
        throw new UnsupportedOperationException();
    }

    @Override
    public String afterGet(DataType dataType, MappingEnv env, Object source) {
        return (String) source;
    }


}
