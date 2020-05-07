package io.army.dialect;

public interface SubQueryContext extends TableContextSQLContext {

    TableContext parentTableContext();

}
