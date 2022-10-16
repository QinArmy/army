package io.army.criteria.mysql;

import io.army.criteria.Window;

public interface MySQLWindowBuilder extends Window.Builder {


    @Override
    Window._SimpleAsClause<MySQLWindowBuilder> comma(String windowName);


}
