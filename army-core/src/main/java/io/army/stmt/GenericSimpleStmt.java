package io.army.stmt;

import io.army.codec.StatementType;


public interface GenericSimpleStmt extends Stmt {

    String sqlText();


    @Deprecated
    default StatementType statementType() {
        throw new UnsupportedOperationException();
    }


}
