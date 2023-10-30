package io.army.schema;

import javax.annotation.Nullable;

import io.army.meta.TableMeta;

import java.util.Collection;
import java.util.List;

public interface _SchemaResult {

    @Nullable
    String catalog();

    @Nullable
    String schema();

    List<TableMeta<?>> dropTableList();

    List<TableMeta<?>> newTableList();

    List<_TableResult> changeTableList();

    static _SchemaResult dropCreate(@Nullable String catalog, @Nullable String schema, Collection<TableMeta<?>> tables) {
        return new DropCreateSchemaResult(catalog, schema, tables);
    }


}
