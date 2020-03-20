package io.army.criteria.impl.inner;

import io.army.criteria.MySQLSelect;

@DeveloperForbid
public interface InnerMySQLSelect extends InnerSelect, MySQLSelect {

    boolean withRollUp();

}
