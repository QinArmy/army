package io.army.wrapper;

public interface ChildBatchUpdateSQLWrapper extends BatchSQLWrapper {

    BatchSimpleUpdateSQLWrapper parentWrapper();

    BatchSimpleUpdateSQLWrapper childWrapper();

    static ChildBatchUpdateSQLWrapper build(BatchSimpleUpdateSQLWrapper parentWrapper
            , BatchSimpleUpdateSQLWrapper childWrapper) {
        return new ChildBatchUpdateSQLWrapperImpl(parentWrapper, childWrapper);
    }
}
