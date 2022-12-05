package io.army.criteria.postgre;

import io.army.criteria.dialect.Window;

public interface PostgreWindows extends Window.Builder {


    Window._WindowAsClause<PostgreQuery._WindowPartitionBySpec> window(String name);

}
