package io.army.criteria.impl.inner.mysql;

import io.army.criteria.MySQLSelect;
import io.army.criteria.impl.inner.DeveloperForbid;
import io.army.criteria.impl.inner.InnerSelect;

@DeveloperForbid
public interface InnerMySQLSelect extends InnerSelect, MySQLSelect {

    boolean withRollUp();

}
