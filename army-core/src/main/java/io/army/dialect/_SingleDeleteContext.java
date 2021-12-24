package io.army.dialect;

import io.army.lang.Nullable;
import io.army.meta.SingleTableMeta;
import io.army.stmt.SimpleStmt;

public interface _SingleDeleteContext extends _DeleteContext, _Block {

    SingleTableMeta<?> table();

    boolean multiTableUpdateChild();

    @Nullable
    _Block childBlock();

    @Override
    SimpleStmt build();
}
