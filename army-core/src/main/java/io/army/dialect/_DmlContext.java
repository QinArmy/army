package io.army.dialect;

import io.army.lang.Nullable;
import io.army.stmt.Stmt;

import java.util.List;

interface _DmlContext extends _StmtContext {

    Stmt build(@Nullable List<?> paramList);

}
