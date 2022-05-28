package io.army.dialect;

import io.army.stmt.BatchStmt;

import java.util.List;

interface _DmlContext extends _StmtContext {

    BatchStmt build(List<?> paramList);

}
