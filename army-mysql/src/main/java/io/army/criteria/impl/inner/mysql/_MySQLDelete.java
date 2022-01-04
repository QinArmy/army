package io.army.criteria.impl.inner.mysql;

import io.army.criteria.DialectStatement;
import io.army.criteria.Hint;
import io.army.criteria.SQLModifier;
import io.army.criteria.impl.inner._Delete;

import java.util.List;

public interface _MySQLDelete extends _Delete, DialectStatement {

    List<Hint> hintList();

    List<SQLModifier> modifierList();


}
