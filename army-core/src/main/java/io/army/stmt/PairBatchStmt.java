package io.army.stmt;

public interface PairBatchStmt extends Stmt {

    BatchStmt parentStmt();

    BatchStmt childStmt();

    static PairBatchStmt build(BatchStmt parentWrapper, BatchStmt childWrapper) {
        return new PairBatchStmtImpl(parentWrapper, childWrapper);
    }

}
