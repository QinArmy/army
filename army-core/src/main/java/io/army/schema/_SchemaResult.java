package io.army.schema;

import io.army.meta.TableMeta;

import java.util.List;

public interface _SchemaResult {

    String catalog();

    String schema();

    List<TableMeta<?>> newTableList();

    List<_TableResult> changeTableList();


}
