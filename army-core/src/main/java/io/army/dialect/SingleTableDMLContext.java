package io.army.dialect;

public interface SingleTableDMLContext extends TableContextSQLContext{

    String relationAlias();
}
