package io.army.criteria.impl.inner.mysql;

import io.army.criteria.SQLModifier;
import io.army.criteria.impl.inner._SpecialSelect;

public interface _MySQL57Select extends _SpecialSelect, _MySQL57Query {

    boolean groupByWithRollUp();

    SQLModifier lockMode();

}
