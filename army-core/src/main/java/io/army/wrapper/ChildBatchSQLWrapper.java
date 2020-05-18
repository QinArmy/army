package io.army.wrapper;


public interface ChildBatchSQLWrapper extends BatchSQLWrapper {

    BatchSimpleSQLWrapper parentWrapper();

    BatchSimpleSQLWrapper childWrapper();


    static ChildBatchSQLWrapper build(BatchSimpleSQLWrapper parent, BatchSimpleSQLWrapper child) {
        return new ChildBatchSQLWrapperImpl(parent, child);
    }

}
