package io.army.criteria.impl.inner;

import io.army.criteria.IPredicate;

import java.util.List;

@DeveloperForbid
public interface InnerUpdate extends InnerSQL {


    List<IPredicate> predicateList();

    void clear();

}
