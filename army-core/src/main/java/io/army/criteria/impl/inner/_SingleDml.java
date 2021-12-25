package io.army.criteria.impl.inner;

import io.army.meta.TableMeta;

import java.util.List;

public interface _SingleDml extends _Statement {

    String tableAlias();

    TableMeta<?> table();

    /**
     * @return a unmodifiable list
     */
    List<_Predicate> predicateList();
}
