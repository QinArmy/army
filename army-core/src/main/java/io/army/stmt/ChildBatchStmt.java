package io.army.stmt;

public interface ChildBatchStmt extends Stmt {

    BatchSimpleStmt parentWrapper();

    BatchSimpleStmt childWrapper();

    static ChildBatchStmt build(BatchSimpleStmt parentWrapper, BatchSimpleStmt childWrapper) {
        return new ChildBatchStmtImpl(parentWrapper, childWrapper);
    }

}
