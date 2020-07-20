package io.army.dialect;

import io.army.lang.NonNull;

public interface SubQueryContext extends TableContextSQLContext {

    @NonNull
    TableContext parentTableContext();

}
