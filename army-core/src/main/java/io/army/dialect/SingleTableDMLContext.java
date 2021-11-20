package io.army.dialect;

public interface SingleTableDMLContext extends _TableSqlContext {

    String relationAlias();
}
