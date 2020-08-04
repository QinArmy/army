package io.army.criteria.impl.inner;

import io.army.beans.DomainWrapper;

@DeveloperForbid
public interface InnerReturningInsert extends InnerSingleDML, InnerSpecialInert {

    boolean migrationData();

    DomainWrapper wrapper();
}
