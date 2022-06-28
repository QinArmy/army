package io.army.criteria.impl.inner;

import io.army.criteria.NullHandleMode;
import io.army.domain.IDomain;

import java.util.List;

public interface _DomainInsert extends _Insert, _Insert._CommonExpInsert {


    NullHandleMode nullHandle();

    boolean isPreferLiteral();


    List<IDomain> domainList();

}
