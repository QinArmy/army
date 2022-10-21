package io.army.criteria.impl.inner.mysql;

import io.army.criteria.DialectStatement;
import io.army.criteria.Hint;
import io.army.criteria.impl.MySQLs;
import io.army.criteria.impl.inner._Delete;
import io.army.criteria.impl.inner._Statement;

import java.util.List;

public interface _MySQLDelete extends _Delete, DialectStatement, _Statement._WithClauseSpec {


    List<Hint> hintList();

    List<MySQLs.Modifier> modifierList();


}
