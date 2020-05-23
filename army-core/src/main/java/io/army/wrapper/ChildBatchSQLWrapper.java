package io.army.wrapper;

public interface ChildBatchSQLWrapper extends SQLWrapper {

    BatchSimpleSQLWrapper parentWrapper();

    BatchSimpleSQLWrapper childWrapper();

    static ChildBatchSQLWrapper build(BatchSimpleSQLWrapper parentWrapper, BatchSimpleSQLWrapper childWrapper) {
        return new ChildBatchSQLWrapperImpl(parentWrapper, childWrapper);
    }

}
