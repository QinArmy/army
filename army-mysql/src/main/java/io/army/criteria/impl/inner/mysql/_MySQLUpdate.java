package io.army.criteria.impl.inner.mysql;


import io.army.criteria.Hint;
import io.army.criteria.SQLModifier;
import io.army.criteria.impl.inner._DialectStatement;
import io.army.criteria.impl.inner._Update;

import java.util.List;

public interface _MySQLUpdate extends _Update, _DialectStatement {

    List<Hint> hintList();

    List<SQLModifier> modifierList();


}
