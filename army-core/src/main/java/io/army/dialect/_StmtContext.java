package io.army.dialect;

import io.army.criteria.Visible;
import io.army.meta.TableMeta;
import io.army.stmt.Stmt;

public interface _StmtContext extends _SqlContext {

    String safeTableAlias(TableMeta<?> table, String alias);

    Stmt build();

    Visible visible();

}
