package io.army.util;

import io.army.stmt.Stmt;

public abstract class Executions extends ExceptionUtils{

    protected Executions() {
        throw new UnsupportedOperationException();
    }

    public static ArmyException unexpectedStmt(Stmt stmt){
        return new ArmyException(String.format("Unexpected Stmt type[%s]",stmt));
    }



}
