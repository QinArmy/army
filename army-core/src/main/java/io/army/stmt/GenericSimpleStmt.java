package io.army.stmt;

import io.army.bean.ObjectWrapper;
import io.army.codec.StatementType;
import io.army.lang.Nullable;


public interface GenericSimpleStmt extends Stmt {

    String sql();

    boolean hasOptimistic();

    @Deprecated
    default StatementType statementType() {
        throw new UnsupportedOperationException();
    }

    @Nullable
    default ObjectWrapper domainWrapper() {
        return null;
    }

}
