package io.army.criteria.impl.inner;

import io.army.meta.FieldMeta;

import java.util.List;

public interface _Update extends _Statement {


    /**
     * @return a unmodifiable list
     */
    List<FieldMeta<?, ?>> fieldList();

    /**
     * @return a unmodifiable list
     */
    List<_Expression<?>> valueExpList();

    List<_Predicate> predicateList();


}
