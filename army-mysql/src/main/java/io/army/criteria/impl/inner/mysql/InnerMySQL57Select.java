package io.army.criteria.impl.inner.mysql;

import io.army.criteria.SQLModifier;
import io.army.criteria.impl.inner.DeveloperForbid;
import io.army.criteria.impl.inner.InnerSpecialSelect;

@DeveloperForbid
public interface InnerMySQL57Select extends InnerSpecialSelect, InnerMySQL57Query {

    boolean groupByWithRollUp();

    SQLModifier lockMode();

}
