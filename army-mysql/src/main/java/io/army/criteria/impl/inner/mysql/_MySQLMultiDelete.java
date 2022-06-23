package io.army.criteria.impl.inner.mysql;

import io.army.criteria.impl.inner._MultiDelete;
import io.army.criteria.mysql.MySQLDelete;

public interface _MySQLMultiDelete extends _MySQLDelete, _MultiDelete, MySQLDelete {

    boolean usingSyntax();


}
