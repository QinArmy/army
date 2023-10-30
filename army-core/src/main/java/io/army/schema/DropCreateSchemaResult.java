package io.army.schema;

import javax.annotation.Nullable;

import io.army.meta.TableMeta;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

final class DropCreateSchemaResult implements _SchemaResult {


    private final String catalog;

    private final String schema;

    private final List<TableMeta<?>> tableList;

    DropCreateSchemaResult(@Nullable String catalog, @Nullable String schema, Collection<TableMeta<?>> tables) {
        this.catalog = catalog;
        this.schema = schema;
        this.tableList = Collections.unmodifiableList(new ArrayList<>(tables));
    }

    @Override
    public String catalog() {
        return this.catalog;
    }

    @Override
    public String schema() {
        return this.schema;
    }

    @Override
    public List<TableMeta<?>> dropTableList() {
        return this.tableList;
    }

    @Override
    public List<TableMeta<?>> newTableList() {
        return this.tableList;
    }

    @Override
    public List<_TableResult> changeTableList() {
        return Collections.emptyList();
    }


}
