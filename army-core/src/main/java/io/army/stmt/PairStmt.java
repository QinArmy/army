package io.army.stmt;


public interface PairStmt extends Stmt {

    SimpleStmt parentStmt();

    SimpleStmt childStmt();

    static PairStmt build(SimpleStmt parentWrapper, SimpleStmt childWrapper) {
        return new PairStmtImpl(parentWrapper, childWrapper);
    }

}
