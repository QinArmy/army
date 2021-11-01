package io.army.stmt;

final class PairStmtImpl implements PairStmt {

    private final SimpleStmt parentWrapper;

    private final SimpleStmt childWrapper;

    PairStmtImpl(SimpleStmt parentWrapper, SimpleStmt childWrapper) {
        this.parentWrapper = parentWrapper;
        this.childWrapper = childWrapper;
    }

    @Override
    public final SimpleStmt parentStmt() {
        return this.parentWrapper;
    }

    @Override
    public final SimpleStmt childStmt() {
        return this.childWrapper;
    }
}
