package io.army.dialect;

import io.army.criteria._SqlContext;
import io.army.meta.TableMeta;
import io.army.stmt.Stmt;

public interface _StmtContext extends _SqlContext {

    void appendTable(TableMeta<?> tableMeta);

    Stmt build();

}
