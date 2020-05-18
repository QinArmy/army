package io.army.wrapper;

public interface ChildBatchUpdateSQLWrapper extends ChildBatchSQLWrapper {

    @Override
    BatchSimpleUpdateSQLWrapper parentWrapper();

    @Override
    BatchSimpleUpdateSQLWrapper childWrapper();
}
