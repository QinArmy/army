package io.army.dialect;

import io.army.stmt.SimpleStmt;

public interface _SelectContext extends _StmtContext {


    @Override
    SimpleStmt build();


}
