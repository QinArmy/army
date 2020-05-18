package io.army.wrapper;

public interface ChildUpdateSQLWrapper extends ChildSQLWrapper, UpdateSQLWrapper {

    @Override
    SimpleUpdateSQLWrapper parentWrapper();

    @Override
    SimpleUpdateSQLWrapper childWrapper();

}
