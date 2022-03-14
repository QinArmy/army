package io.army.dialect;

import io.army.criteria.Visible;
import io.army.stmt.Stmt;

public interface _StmtContext extends _SqlContext {

    Stmt build();

    Visible visible();

}
