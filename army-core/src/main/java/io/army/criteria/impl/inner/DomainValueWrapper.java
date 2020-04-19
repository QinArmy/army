package io.army.criteria.impl.inner;

import io.army.domain.IDomain;

import java.util.List;

public interface DomainValueWrapper extends ValueWrapper {

    List<IDomain> domainList();
}
