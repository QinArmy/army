package io.army.criteria.impl.inner.mysql;

import io.army.criteria.impl.inner._MultiDelete;
import io.army.meta.TableMeta;

import java.util.List;

public interface _MySQLMultiDelete extends _MySQLDelete, _MultiDelete {

    List<TableMeta<?>> tableList();


    boolean usingSyntax();


}
