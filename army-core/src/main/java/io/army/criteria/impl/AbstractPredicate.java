package io.army.criteria.impl;

import io.army.criteria.Predicate;
import io.army.criteria.SubQuery;
import io.army.lang.Nullable;
import io.army.meta.mapping.MappingFactory;
import io.army.meta.mapping.MappingType;
import io.army.util.ArrayUtils;

import java.util.Collections;
import java.util.List;

/**
 * created  on 2018/11/25.
 */
abstract class AbstractPredicate extends AbstractExpression<Boolean> implements Predicate {

    @Override
    public final MappingType mappingType() {
        return MappingFactory.getDefaultMapping(Boolean.class);
    }

    @Override
    public final Predicate or(@Nullable Predicate... andPredicates) {
        if (andPredicates == null) {
            return this;
        }
        return new OrtPredicate(this, ArrayUtils.asList(andPredicates));
    }

    @Override
    public final Predicate or(List<Predicate> andPredicateList) {
        return new OrtPredicate(this,andPredicateList);
    }


}
