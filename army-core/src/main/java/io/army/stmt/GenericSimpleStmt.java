package io.army.stmt;

import io.army.beans.ObjectWrapper;
import io.army.codec.StatementType;
import io.army.lang.Nullable;


public interface GenericSimpleStmt extends Stmt {

    String sql();

    boolean hasVersion();

    StatementType statementType();

    @Nullable
    default ObjectWrapper domainWrapper(){
        return null;
    }

}
