package io.army.boot.migratioin;

import io.army.GenericRmSessionFactory;
import io.army.criteria.MetaException;
import io.army.dialect.Database;
import io.army.schema.SchemaInfoException;

import java.util.List;

/**
 */
interface MetaSchemaComparator {

    /**
     * @return a unmodifiable List
     */
    List<List<Migration>> compare(SchemaInfo schemaInfo, GenericRmSessionFactory sessionFactory)
            throws SchemaInfoException, MetaException;

    static MetaSchemaComparator build(Database database) {
        MetaSchemaComparator comparator;
        switch (database) {
            case MySQL:
            case MySQL57:
                comparator = new MySQL57MetaSchemaComparator();
                break;
            case MySQL80:
                comparator = new MySQL80MetaSchemaComparator();
                break;
            case Postgre:
            case Oracle:
            default:
                throw new IllegalArgumentException(String.format("unsupported dialect %s", database));
        }
        return comparator;
    }
}
