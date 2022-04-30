package io.army.criteria.impl.inner.mysql;


import io.army.criteria.impl.inner._DialectStatement;
import io.army.criteria.impl.inner._Update;
import io.army.criteria.mysql.MySQLWords;

import java.util.List;
import java.util.Set;

public interface _MySQLUpdate extends _Update, _DialectStatement {

    List<_MySQLHint> hintList();

    Set<MySQLWords> modifierSet();


}
