package io.army.criteria.impl.inner.mysql;

import io.army.criteria.SQLModifier;
import io.army.criteria.impl.inner.DeveloperForbid;
import io.army.criteria.impl.inner._SpecialSelect;

@DeveloperForbid
public interface InnerMySQL57Select extends _SpecialSelect, InnerMySQL57Query {

    boolean groupByWithRollUp();

    SQLModifier lockMode();

}
