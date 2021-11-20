package io.army.criteria.impl.inner;

import io.army.beans.DomainWrapper;

import java.util.List;

public interface _ValuesInsert extends _Insert {

    boolean migrationData();

    List<DomainWrapper> wrapperList();


}
