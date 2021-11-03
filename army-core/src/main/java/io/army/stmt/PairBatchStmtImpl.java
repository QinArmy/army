package io.army.stmt;

final class PairBatchStmtImpl implements PairBatchStmt {

    private final BatchSimpleStmt parent;

    private final BatchSimpleStmt child;

    PairBatchStmtImpl(BatchSimpleStmt parent, BatchSimpleStmt child) {
        this.parent = parent;
        this.child = child;
    }

    @Override
    public final BatchSimpleStmt parentStmt() {
        return this.parent;
    }

    @Override
    public final BatchSimpleStmt childStmt() {
        return this.child;
    }


    @Override
    public int getTimeout() {
        return 0;
    }

}
