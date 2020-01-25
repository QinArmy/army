package io.army.boot.migratioin;

import io.army.meta.FieldMeta;
import io.army.meta.IndexMeta;
import io.army.meta.TableMeta;

import java.util.List;

public interface Migration {

    TableMeta<?> table();

    boolean newTable();

    List<FieldMeta<?,?>> columnsToAdd();

    List<FieldMeta<?,?>> columnsToChange();

    List<IndexMeta<?>> indexesToAdd();

    List<IndexMeta<?>> indexesToAlter();

    List<String> indexesToDrop();

}
