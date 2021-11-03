package io.army.stmt;

public interface PairBatchStmt extends Stmt {

    BatchSimpleStmt parentStmt();

    BatchSimpleStmt childStmt();

    static PairBatchStmt build(BatchSimpleStmt parentWrapper, BatchSimpleStmt childWrapper) {
        return new PairBatchStmtImpl(parentWrapper, childWrapper);
    }

}
