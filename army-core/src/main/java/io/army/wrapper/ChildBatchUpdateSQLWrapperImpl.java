package io.army.wrapper;

final class ChildBatchUpdateSQLWrapperImpl implements ChildBatchUpdateSQLWrapper {

    private final BatchSimpleUpdateSQLWrapper parentWrapper;

    private final BatchSimpleUpdateSQLWrapper childWrapper;


    ChildBatchUpdateSQLWrapperImpl(BatchSimpleUpdateSQLWrapper parentWrapper
            , BatchSimpleUpdateSQLWrapper childWrapper) {
        this.parentWrapper = parentWrapper;
        this.childWrapper = childWrapper;
    }

    @Override
    public final BatchSimpleUpdateSQLWrapper parentWrapper() {
        return this.parentWrapper;
    }

    @Override
    public final BatchSimpleUpdateSQLWrapper childWrapper() {
        return this.childWrapper;
    }
}
