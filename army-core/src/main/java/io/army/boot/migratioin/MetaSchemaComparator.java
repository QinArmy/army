package io.army.boot.migratioin;

import io.army.GenericRmSessionFactory;
import io.army.meta.MetaException;
import io.army.schema.SchemaInfoException;

import java.util.List;

/**
 */
interface MetaSchemaComparator {

    /**
     * @return a unmodifiable List
     */
    List<List<Migration>> compare(SchemaInfo schemaInfo)
            throws SchemaInfoException, MetaException;

    static MetaSchemaComparator build(GenericRmSessionFactory sessionFactory) {
        MetaSchemaComparator comparator;
        switch (sessionFactory.actualDatabase()) {
            case MySQL:
                comparator = new MySQL57MetaSchemaComparator(sessionFactory);
                break;
            case Postgre:
                comparator = new Postgre11MetaSchemaComparator(sessionFactory);
                break;
            case Oracle:
            default:
                throw new IllegalArgumentException(String.format("unsupported dialect %s"
                        , sessionFactory.actualDatabase()));
        }
        return comparator;
    }
}
