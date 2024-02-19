package io.army.mapping.optional;

import io.army.criteria.CriteriaException;
import io.army.dialect.UnsupportedDialectException;
import io.army.mapping.MappingEnv;
import io.army.mapping.MappingType;
import io.army.mapping._ArmyBuildInMapping;
import io.army.meta.ServerMeta;
import io.army.session.DataAccessException;
import io.army.sqltype.DataType;


/**
 * <p>This class mapping List to database record (for example : postgre record ,oid : 2249)
 *
 * @see <a href="https://www.postgresql.org/docs/current/catalog-pg-type.html">Postgre pg_type table ,oid : 2249</a>
 */
public final class RecordType extends _ArmyBuildInMapping implements MappingType.SqlRecordType {


    private RecordType() {
    }

    @Override
    public Class<?> javaType() {
        return Object.class;
    }

    @Override
    public DataType map(ServerMeta meta) throws UnsupportedDialectException {
        return null;
    }

    @Override
    public MappingType arrayTypeOfThis() throws CriteriaException {
        return super.arrayTypeOfThis();
    }

    @Override
    public Object convert(MappingEnv env, Object source) throws CriteriaException {
        return null;
    }

    @Override
    public Object beforeBind(DataType dataType, MappingEnv env, Object source) throws CriteriaException {
        return null;
    }

    @Override
    public Object afterGet(DataType dataType, MappingEnv env, Object source) throws DataAccessException {
        return null;
    }


}
