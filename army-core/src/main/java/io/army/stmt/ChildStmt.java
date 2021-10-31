package io.army.stmt;


public interface ChildStmt extends Stmt {

    SimpleStmt parentWrapper();

    SimpleStmt childWrapper();

    static ChildStmt build(SimpleStmt parentWrapper, SimpleStmt childWrapper) {
        return new ChildStmtImpl(parentWrapper, childWrapper);
    }

}
