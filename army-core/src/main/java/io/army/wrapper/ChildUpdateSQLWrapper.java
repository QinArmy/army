package io.army.wrapper;

public interface ChildUpdateSQLWrapper extends ChildSQLWrapper {

    @Override
    SimpleUpdateSQLWrapper parentWrapper();

    @Override
    SimpleUpdateSQLWrapper childWrapper();

    static ChildUpdateSQLWrapper build(SimpleUpdateSQLWrapper parentWrapper, SimpleUpdateSQLWrapper childWrapper) {
        return new ChildUpdateSQLWrapperImpl(parentWrapper, childWrapper);
    }

}
