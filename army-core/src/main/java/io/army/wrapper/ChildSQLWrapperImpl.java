package io.army.wrapper;

final class ChildSQLWrapperImpl implements ChildSQLWrapper {

    private SimpleSQLWrapper parentWrapper;

    private SimpleSQLWrapper childWrapper;

    ChildSQLWrapperImpl(SimpleSQLWrapper parentWrapper, SimpleSQLWrapper childWrapper) {
        this.parentWrapper = parentWrapper;
        this.childWrapper = childWrapper;
    }

    @Override
    public final SimpleSQLWrapper parentWrapper() {
        return this.parentWrapper;
    }

    @Override
    public final SimpleSQLWrapper childWrapper() {
        return this.childWrapper;
    }
}
