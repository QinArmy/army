package io.army.mapping.optional;

import io.army.criteria.CriteriaException;
import io.army.dialect.Database;
import io.army.dialect.UnsupportedDialectException;
import io.army.mapping.MappingEnv;
import io.army.mapping.MappingType;
import io.army.mapping._ArmyBuildInMapping;
import io.army.mapping.array.SqlRecordArrayType;
import io.army.meta.ServerMeta;
import io.army.session.DataAccessException;
import io.army.sqltype.DataType;
import io.army.sqltype.PostgreType;
import io.army.type.SqlRecord;
import io.army.util._Collections;

import java.util.Collections;
import java.util.List;
import java.util.Objects;


/**
 * <p>This class mapping List to database record (for example : postgre record ,oid : 2249)
 *
 * @see io.army.type.SqlRecord
 * @see io.army.mapping.array.SqlRecordArrayType
 * @see <a href="https://www.postgresql.org/docs/current/catalog-pg-type.html">Postgre pg_type table ,oid : 2249</a>
 */
public final class SqlRecordType extends _ArmyBuildInMapping implements MappingType.SqlRecordColumnType {

    public static SqlRecordType fromColumn(final MappingType columnType) {
        Objects.requireNonNull(columnType);
        return new SqlRecordType(Collections.singletonList(columnType));
    }


    public static SqlRecordType fromRow(final List<MappingType> columnTypeList) {
        if (columnTypeList.size() == 0) {
            throw new IllegalArgumentException("column type list must be non-empty");
        }
        return new SqlRecordType(_Collections.asUnmodifiableList(columnTypeList));
    }


    public static SqlRecordType fromUnlimited() {
        return UNLIMITED;
    }


    public static final SqlRecordType UNLIMITED = new SqlRecordType(Collections.emptyList());

    private final List<MappingType> columnTypeList;

    /**
     * private constructor
     */
    private SqlRecordType(List<MappingType> columnTypeList) {
        this.columnTypeList = columnTypeList;
    }

    @Override
    public Class<?> javaType() {
        return SqlRecord.class;
    }

    @Override
    public DataType map(final ServerMeta meta) throws UnsupportedDialectException {
        if (meta.serverDatabase() != Database.PostgreSQL) {
            throw MAP_ERROR_HANDLER.apply(this, meta);
        }
        return PostgreType.RECORD;
    }

    @Override
    public MappingType arrayTypeOfThis() throws CriteriaException {
        final MappingType instance;
        switch (this.columnTypeList.size()) {
            case 0:
                instance = SqlRecordArrayType.UNLIMITED;
                break;
            case 1:
                instance = SqlRecordArrayType.fromColumn(SqlRecord[].class, this.columnTypeList.get(0));
                break;
            default:
                instance = SqlRecordArrayType.fromRow(SqlRecord[].class, this.columnTypeList);
        }
        return instance;
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
