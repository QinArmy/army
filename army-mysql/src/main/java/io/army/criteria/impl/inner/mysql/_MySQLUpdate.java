package io.army.criteria.impl.inner.mysql;


import io.army.criteria.Cte;
import io.army.criteria.Hint;
import io.army.criteria.impl.inner._DialectStatement;
import io.army.criteria.impl.inner._Update;
import io.army.criteria.mysql.MySQLModifier;

import java.util.List;

public interface _MySQLUpdate extends _Update, _DialectStatement {

    boolean isRecursive();

    List<Cte> cteList();

    List<Hint> hintList();

    List<MySQLModifier> modifierList();


}
