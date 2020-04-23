package io.army.criteria.impl.inner;

import io.army.criteria.IPredicate;

import java.util.List;

@DeveloperForbid
public interface InnerDelete extends InnerSQL {

    /**
     * @return a unmodifiable list
     */
    List<IPredicate> predicateList();

}
