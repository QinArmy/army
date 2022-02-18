package io.army.schema;

import io.army.dialect.Dialect;
import io.army.meta.SchemaMeta;
import io.army.meta.TableMeta;

import java.util.Collection;

public interface _SchemaComparer {

    _SchemaResult compare(_SchemaInfo schemaInfo, SchemaMeta schemaMeta, Collection<TableMeta<?>> tableMetas);

    static _SchemaComparer create(Dialect dialect) {
        throw new UnsupportedOperationException();
    }

}
