package io.army.wrapper;

final class ChildBatchSQLWrapperImpl implements ChildBatchSQLWrapper {

    private final SimpleBatchSQLWrapper parent;

    private final SimpleBatchSQLWrapper child;

    ChildBatchSQLWrapperImpl(SimpleBatchSQLWrapper parent, SimpleBatchSQLWrapper child) {
        this.parent = parent;
        this.child = child;
    }

    @Override
    public final SimpleBatchSQLWrapper parentWrapper() {
        return this.parent;
    }

    @Override
    public final SimpleBatchSQLWrapper childWrapper() {
        return this.child;
    }
}
