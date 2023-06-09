package io.army.schema;

import io.army.meta.SchemaMeta;
import io.army.meta.ServerMeta;
import io.army.meta.TableMeta;
import io.army.util._Exceptions;

import java.util.Collection;

public interface _SchemaComparer {

    _SchemaResult compare(_SchemaInfo schemaInfo, SchemaMeta schemaMeta, Collection<TableMeta<?>> tableMetas);

    static _SchemaComparer create(final ServerMeta serverMeta) {
        final _SchemaComparer comparer;
        switch (serverMeta.dialectDatabase()) {
            case MySQL:
                comparer = MySQLComparer.create(serverMeta);
                break;
            case Postgre:
            case Oracle:
            case H2:

            default:
                throw _Exceptions.unexpectedEnum(serverMeta.dialectDatabase());
        }
        return comparer;
    }

}
