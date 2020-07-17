package io.army.criteria.impl.inner;

import io.army.domain.IDomain;

import java.util.List;

@DeveloperForbid
public interface InnerValuesInsert extends InnerInsert {

    boolean migrationData();

    List<IDomain> valueList();

}
