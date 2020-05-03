package io.army.wrapper;


public interface ChildBatchSQLWrapper extends BatchSQLWrapper {

    SimpleBatchSQLWrapper parentWrapper();

    SimpleBatchSQLWrapper childWrapper();

}
