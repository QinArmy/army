package io.army.criteria.postgre;

import io.army.criteria.dialect.Window;

public interface PostgreWindows extends Window.Builder {

    @Override
    PostgreQuery._WindowAsClause<PostgreWindows> window(String name);

}
