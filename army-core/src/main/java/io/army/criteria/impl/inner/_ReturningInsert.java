package io.army.criteria.impl.inner;

import io.army.beans.DomainWrapper;

public interface _ReturningInsert extends _SingleDml, _SpecialInert {

    boolean migrationData();

    DomainWrapper wrapper();
}
