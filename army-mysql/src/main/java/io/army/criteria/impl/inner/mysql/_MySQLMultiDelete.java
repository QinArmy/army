package io.army.criteria.impl.inner.mysql;

import io.army.criteria.impl.inner._MultiDelete;
import io.army.criteria.mysql.MySQLDelete;

import java.util.List;

public interface _MySQLMultiDelete extends _MySQLDelete, _MultiDelete, MySQLDelete {

    List<String> tableAliasList();

    boolean usingSyntax();


}
