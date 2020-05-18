package io.army.wrapper;

public interface BatchSimpleUpdateSQLWrapper extends BatchSimpleSQLWrapper, BatchSQLWrapper {

    boolean hasVersion();
}
