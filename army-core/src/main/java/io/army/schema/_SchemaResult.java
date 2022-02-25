package io.army.schema;

import io.army.lang.Nullable;
import io.army.meta.TableMeta;

import java.util.List;

public interface _SchemaResult {

    @Nullable
    String catalog();

    @Nullable
    String schema();

    List<TableMeta<?>> newTableList();

    List<_TableResult> changeTableList();


}
