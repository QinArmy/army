package io.army.criteria.impl.inner.mysql;

import io.army.criteria.DialectStatement;
import io.army.criteria.Hint;
import io.army.criteria.impl.inner._Cte;
import io.army.criteria.impl.inner._Delete;
import io.army.criteria.mysql.MySQLModifier;

import java.util.List;

public interface _MySQLDelete extends _Delete, DialectStatement {

    boolean isRecursive();

    List<_Cte> cteList();

    List<Hint> hintList();

    List<MySQLModifier> modifierList();


}
