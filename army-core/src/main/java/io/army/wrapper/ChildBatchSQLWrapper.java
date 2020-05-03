package io.army.wrapper;


public interface ChildBatchSQLWrapper extends BatchSQLWrapper {

    SimpleBatchSQLWrapper parentWrapper();

    SimpleBatchSQLWrapper childWrapper();


    static ChildBatchSQLWrapper build(SimpleBatchSQLWrapper parent, SimpleBatchSQLWrapper child) {
        return new ChildBatchSQLWrapperImpl(parent, child);
    }

}
