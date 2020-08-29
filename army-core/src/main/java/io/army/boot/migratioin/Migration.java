package io.army.boot.migratioin;

import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.meta.IndexMeta;
import io.army.meta.TableMeta;

import java.util.List;

interface Migration {

    TableMeta<?> tableMeta();

    @Nullable
    String tableSuffix();

    String actualTableName();

    interface TableMigration extends Migration {

    }

    interface MemberMigration extends Migration {

        boolean modifyTableComment();

        List<FieldMeta<?, ?>> columnsToAdd();

        List<FieldMeta<?, ?>> columnsToChange();

        List<FieldMeta<?, ?>> columnToModifyComment();

        List<IndexMeta<?>> indexesToAdd();

        List<IndexMeta<?>> indexesToAlter();

        List<String> indexesToDrop();


    }
}
