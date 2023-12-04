package io.army.mapping;

import io.army.criteria.CriteriaException;
import io.army.mapping.array.JsonArrayType;
import io.army.meta.ServerMeta;
import io.army.sqltype.DataType;
import io.army.sqltype.MySQLType;
import io.army.sqltype.PostgreType;
import io.army.sqltype.SqlType;
import io.army.util._Collections;

import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;


public final class JsonType extends ArmyJsonType implements MappingType.SqlJsonType {

    public static JsonType from(final Class<?> javaType) {
        final JsonType instance;
        if (javaType == String.class) {
            instance = TEXT;
        } else {
            instance = INSTANCE_MAP.computeIfAbsent(javaType, CONSTRUCTOR);
        }
        return instance;
    }

    public static final JsonType TEXT = new JsonType(String.class);

    private static final ConcurrentMap<Class<?>, JsonType> INSTANCE_MAP = _Collections.concurrentHashMap();

    private static final Function<Class<?>, JsonType> CONSTRUCTOR = JsonType::new;


    /**
     * private constructor
     */
    private JsonType(Class<?> javaType) {
        super(javaType);
    }

    @Override
    public MappingType arrayTypeOfThis() throws CriteriaException {
        return JsonArrayType.from(this.javaType);
    }

    @Override
    public DataType map(final ServerMeta meta) {
        final SqlType sqlDataType;
        switch (meta.serverDatabase()) {
            case MySQL:
                sqlDataType = MySQLType.JSON;
                break;
            case PostgreSQL:
                sqlDataType = PostgreType.JSON;
                break;
            case Oracle:

            case H2:
            default:
                throw MAP_ERROR_HANDLER.apply(this, meta);

        }
        return sqlDataType;
    }


}
