package io.army.boot.migratioin;

import io.army.criteria.MetaException;
import io.army.dialect.Dialect;
import io.army.meta.TableMeta;
import io.army.schema.SchemaInfoException;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 *
 * @see MetaSchemaComparator
 */
public interface Meta2Schema {


    /**
     * @return key : tableMeta name,value : ddl or dml dml list
     */
    Map<String, List<String>> migrate(Collection<TableMeta<?>> tableMetas, SchemaExtractor schemaExtractor
            , Dialect dialect) throws SchemaInfoException, MetaException;

    static Meta2Schema build() {
        return new Meta2SchemaImpl();
    }

}
