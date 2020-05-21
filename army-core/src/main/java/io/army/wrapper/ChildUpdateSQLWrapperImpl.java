package io.army.wrapper;

final class ChildUpdateSQLWrapperImpl implements ChildUpdateSQLWrapper {

    private final SimpleUpdateSQLWrapper parentWrapper;

    private final SimpleUpdateSQLWrapper childWrapper;

    ChildUpdateSQLWrapperImpl(SimpleUpdateSQLWrapper parentWrapper, SimpleUpdateSQLWrapper childWrapper) {
        this.parentWrapper = parentWrapper;
        this.childWrapper = childWrapper;
    }

    @Override
    public SimpleUpdateSQLWrapper parentWrapper() {
        return this.parentWrapper;
    }

    @Override
    public SimpleUpdateSQLWrapper childWrapper() {
        return this.childWrapper;
    }
}
