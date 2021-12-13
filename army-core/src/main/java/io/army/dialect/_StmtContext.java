package io.army.dialect;

import io.army.lang.Nullable;
import io.army.stmt.Stmt;

public interface _StmtContext extends _SqlContext {


    /**
     * @return primary tableIndex
     */
    byte tableIndex();

    @Nullable
    String tableSuffix();

    Stmt build();

}
