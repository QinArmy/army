package io.army.stmt;

final class ChildBatchStmtImpl implements ChildBatchStmt {

    private final BatchSimpleStmt parent;

    private final BatchSimpleStmt child;

    ChildBatchStmtImpl(BatchSimpleStmt parent, BatchSimpleStmt child) {
        this.parent = parent;
        this.child = child;
    }

    @Override
    public final BatchSimpleStmt parentWrapper() {
        return this.parent;
    }

    @Override
    public final BatchSimpleStmt childWrapper() {
        return this.child;
    }
}
