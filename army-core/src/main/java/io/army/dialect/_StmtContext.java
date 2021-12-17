package io.army.dialect;

import io.army.criteria.Visible;
import io.army.criteria.impl.inner._Statement;
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

    Visible visible();


    _Statement statement();

}
