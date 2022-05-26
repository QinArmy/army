package io.army.dialect;

import io.army.stmt.BatchStmt;
import io.army.stmt.SimpleStmt;

import java.util.List;

interface _DmlContext extends _StmtContext {

    SimpleStmt build();

    BatchStmt build(List<?> paramList);

}
