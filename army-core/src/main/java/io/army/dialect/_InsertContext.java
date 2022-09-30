package io.army.dialect;

import io.army.criteria.LiteralMode;
import io.army.meta.TableMeta;

public interface _InsertContext extends StmtContext {

    TableMeta<?> insertTable();

    LiteralMode literalMode();

}
