package io.army.wrapper;

public interface ChildReturningUpdateSQLWrapper extends ChildUpdateSQLWrapper {

    @Override
    ReturningUpdateSQLWrapper parentWrapper();

    @Override
    ReturningUpdateSQLWrapper childWrapper();
}
