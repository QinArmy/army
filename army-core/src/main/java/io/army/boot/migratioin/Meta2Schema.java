package io.army.boot.migratioin;

import io.army.criteria.MetaException;
import io.army.dialect.Dialect;
import io.army.dialect.SQLDialect;
import io.army.meta.TableMeta;
import io.army.schema.SchemaInfoException;

import java.sql.Connection;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @see MetaSchemaComparator
 * @see SchemaExtractor
 */
public interface Meta2Schema {


    /**
     * @return key : tableMeta name,value : ddl or dml sql list
     */
    Map<String, List<String>> migrate(Collection<TableMeta<?>> tableMetas, Connection connection
            , Dialect dialect) throws SchemaInfoException, MetaException;

    static Meta2Schema build() {
        return new Meta2SchemaImpl();
    }

}
