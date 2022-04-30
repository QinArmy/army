package io.army.criteria.impl.inner.mysql;

import io.army.criteria.DialectStatement;
import io.army.criteria.Hint;
import io.army.criteria.impl.inner._Delete;
import io.army.criteria.mysql.MySQLWords;

import java.util.List;

public interface _MySQLDelete extends _Delete, DialectStatement {

    List<Hint> hintList();

    List<MySQLWords> modifierList();


}
