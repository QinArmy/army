package io.army.stmt;

final class PairBatchStmtImpl implements PairBatchStmt {

    private final BatchStmt parent;

    private final BatchStmt child;

    PairBatchStmtImpl(BatchStmt parent, BatchStmt child) {
        this.parent = parent;
        this.child = child;
    }

    @Override
    public final BatchStmt parentStmt() {
        return this.parent;
    }

    @Override
    public final BatchStmt childStmt() {
        return this.child;
    }



}
