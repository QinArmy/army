package io.army.criteria.mysql;

import io.army.criteria.dialect.Window;

public interface MySQLWindows extends Window.Builder {


    @Override
    Window._SimpleAsClause<MySQLWindows> window(String windowName);


}
