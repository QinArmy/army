package io.army.boot.migratioin;

import io.army.criteria.MetaException;
import io.army.dialect.Dialect;
import io.army.dialect.SQLDialect;
import io.army.meta.TableMeta;
import io.army.schema.SchemaInfoException;

import java.util.Collection;
import java.util.List;

/**
 * @see Meta2Schema
 * @see SchemaExtractor
 */
interface MetaSchemaComparator {

    /**
     * @return a unmodifiable List
     */
    List<Migration> compare(Collection<TableMeta<?>> tableMetas, SchemaInfo schemaInfo, Dialect dialect)
            throws SchemaInfoException, MetaException;

    static MetaSchemaComparator build(SQLDialect sqlDialect) {
        MetaSchemaComparator comparator;
        switch (sqlDialect) {
            case MySQL:
            case MySQL57:
                comparator = new MySQL57MetaSchemaComparator();
                break;
            case MySQL80:
                comparator = new MySQL80MetaSchemaComparator();
                break;
            case SQL_Server:
            case OceanBase:
            case Postgre:
            case Oracle:
            case Db2:
            default:
                throw new IllegalArgumentException(String.format("unsupported dialect %s", sqlDialect));
        }
        return comparator;
    }
}
