package io.army.criteria.impl.inner;

import io.army.beans.DomainWrapper;

import java.util.List;

@DeveloperForbid
public interface InnerValuesInsert extends InnerInsert {

    boolean migrationData();

    List<DomainWrapper> valueList();


}
