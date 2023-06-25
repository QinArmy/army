package io.army.stmt;

import io.army.lang.Nullable;
import io.army.meta.PrimaryFieldMeta;

import java.util.function.ObjIntConsumer;

public interface InsertStmtParams extends DmlStmtParams {


    @Nullable
    PrimaryFieldMeta<?> idField();


    @Override
    int idSelectionIndex();

    int rowSize();

    ObjIntConsumer<Object> idConsumer();

    boolean isTwoStmtQuery();


}