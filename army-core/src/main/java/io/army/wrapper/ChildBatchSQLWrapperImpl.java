package io.army.wrapper;

final class ChildBatchSQLWrapperImpl implements ChildBatchSQLWrapper {

    private final BatchSimpleSQLWrapper parent;

    private final BatchSimpleSQLWrapper child;

    ChildBatchSQLWrapperImpl(BatchSimpleSQLWrapper parent, BatchSimpleSQLWrapper child) {
        this.parent = parent;
        this.child = child;
    }

    @Override
    public final BatchSimpleSQLWrapper parentWrapper() {
        return this.parent;
    }

    @Override
    public final BatchSimpleSQLWrapper childWrapper() {
        return this.child;
    }
}
