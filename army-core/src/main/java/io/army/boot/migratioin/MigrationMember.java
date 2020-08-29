package io.army.boot.migratioin;

import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.meta.IndexMeta;
import io.army.meta.TableMeta;

import java.util.List;

interface MigrationMember {

    @Nullable
    String tableSuffix();

    TableMeta<?> table();

    String actualTableName();

    boolean newTable();

    boolean modifyTableComment();

    List<FieldMeta<?, ?>> columnsToAdd();

    List<FieldMeta<?, ?>> columnsToChange();

    List<FieldMeta<?, ?>> commentToModify();

    List<IndexMeta<?>> indexesToAdd();

    List<IndexMeta<?>> indexesToAlter();

    List<String> indexesToDrop();

}
