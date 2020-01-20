package io.army.schema.migration;

import io.army.meta.FieldMeta;
import io.army.meta.IndexMeta;
import io.army.meta.TableMeta;

import java.util.List;

public interface Migration {

    TableMeta<?> table();

    boolean newTable();

    List<FieldMeta<?,?>> columnsToAdd();

    List<FieldMeta<?,?>> columnsToModify();

    List<IndexMeta<?>> indexesToAdd();

    List<IndexMeta<?>> indexesToModify();

    List<String> indexesToDrop();

}
