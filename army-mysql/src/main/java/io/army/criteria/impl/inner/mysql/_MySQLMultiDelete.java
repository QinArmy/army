package io.army.criteria.impl.inner.mysql;

import io.army.criteria.impl.inner._MultiDelete;

import java.util.List;

public interface _MySQLMultiDelete extends _MySQLDelete, _MultiDelete {

    List<String> tableAliasList();


    boolean usingSyntax();


}
