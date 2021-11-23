package io.army.dialect;

public interface SingleTableDMLContext extends _TablesSqlContext {

    String relationAlias();
}
