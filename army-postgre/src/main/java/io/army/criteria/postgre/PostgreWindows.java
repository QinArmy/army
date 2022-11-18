package io.army.criteria.postgre;

import io.army.criteria.dialect.Window;

public interface PostgreWindows extends Window.Builder {


    PostgreQuery._WindowAsClause<PostgreWindows> window(String name);

}
