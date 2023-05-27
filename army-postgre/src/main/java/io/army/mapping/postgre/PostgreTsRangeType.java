package io.army.mapping.postgre;

import io.army.criteria.CriteriaException;
import io.army.dialect.NotSupportDialectException;
import io.army.lang.Nullable;
import io.army.mapping.MappingEnv;
import io.army.mapping.MappingType;
import io.army.mapping.NoMatchMappingException;
import io.army.meta.ServerMeta;
import io.army.session.DataAccessException;
import io.army.sqltype.SqlType;
import io.army.util._TimeUtils;

import java.time.LocalDateTime;

public final class PostgreTsRangeType extends PostgreRangeType<LocalDateTime> {


    private PostgreTsRangeType(Class<?> javaType) {
        super(javaType, LocalDateTime.class);
    }

    @Override
    public SqlType map(ServerMeta meta) throws NotSupportDialectException {
        return null;
    }

    @Override
    public MappingType compatibleFor(Class<?> targetType) throws NoMatchMappingException {
        return null;
    }

    @Override
    public Object convert(MappingEnv env, Object nonNull) throws CriteriaException {
        return null;
    }

    @Override
    public Object beforeBind(SqlType type, MappingEnv env, Object nonNull) throws CriteriaException {
        return null;
    }

    @Override
    public Object afterGet(SqlType type, MappingEnv env, Object nonNull) throws DataAccessException {
        return null;
    }

    @Nullable
    static LocalDateTime parseDateTime(final String text) {
        final LocalDateTime bound;
        if (INFINITY.equalsIgnoreCase(text)) {
            bound = null;
        } else {
            bound = LocalDateTime.parse(text, _TimeUtils.DATETIME_FORMATTER_6);
        }
        return bound;
    }
}
