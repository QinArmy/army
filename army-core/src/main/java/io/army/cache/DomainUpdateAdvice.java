package io.army.cache;

import io.army.beans.DomainReadonlyWrapper;
import io.army.criteria.IPredicate;
import io.army.meta.FieldMeta;

import java.util.List;
import java.util.Set;

public interface DomainUpdateAdvice {

    DomainReadonlyWrapper readonlyWrapper();

    void updateFinish();

    boolean hasUpdate();

    Set<FieldMeta<?, ?>> targetFieldSet();

    List<IPredicate> predicateList();

}
