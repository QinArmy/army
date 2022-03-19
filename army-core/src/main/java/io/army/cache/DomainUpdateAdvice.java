package io.army.cache;

import io.army.criteria.impl.inner._Predicate;
import io.army.meta.FieldMeta;

import java.util.List;
import java.util.Set;

public interface DomainUpdateAdvice {

    Object readonlyWrapper();

    void updateFinish();

    boolean hasUpdate();

    Set<FieldMeta<?>> targetFieldSet();

    List<_Predicate> predicateList();

}
