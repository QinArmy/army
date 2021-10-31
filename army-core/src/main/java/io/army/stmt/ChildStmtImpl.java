package io.army.stmt;

final class ChildStmtImpl implements ChildStmt {

    private final SimpleStmt parentWrapper;

    private final SimpleStmt childWrapper;

    ChildStmtImpl(SimpleStmt parentWrapper, SimpleStmt childWrapper) {
        this.parentWrapper = parentWrapper;
        this.childWrapper = childWrapper;
    }

    @Override
    public final SimpleStmt parentWrapper() {
        return this.parentWrapper;
    }

    @Override
    public final SimpleStmt childWrapper() {
        return this.childWrapper;
    }
}
