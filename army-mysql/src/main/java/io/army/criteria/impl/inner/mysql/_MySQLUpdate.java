package io.army.criteria.impl.inner.mysql;


import io.army.criteria.dialect.Hint;
import io.army.criteria.impl.MySQLs;
import io.army.criteria.impl.inner._DialectStatement;
import io.army.criteria.impl.inner._Statement;
import io.army.criteria.impl.inner._Update;

import java.util.List;

public interface _MySQLUpdate extends _Update, _DialectStatement, _Statement._WithClauseSpec {


    List<Hint> hintList();

    List<MySQLs.Modifier> modifierList();


}
