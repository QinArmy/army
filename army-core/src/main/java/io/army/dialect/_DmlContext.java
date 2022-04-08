package io.army.dialect;

import io.army.criteria.impl.inner._Predicate;
import io.army.criteria.impl.inner._Update;

import java.util.List;

interface _DmlContext extends _StmtContext {


    /**
     * @see _Update#predicateList()
     */
    List<_Predicate> predicateList();

}
