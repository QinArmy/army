package io.army.criteria.impl;

import io.army.criteria.IPredicate;
import io.army.lang.Nullable;
import io.army.meta.mapping.MappingFactory;
import io.army.meta.mapping.MappingType;
import io.army.util.ArrayUtils;

import java.util.Collections;
import java.util.List;

/**
 * This class is base class of all {@link IPredicate} implementation .
 */
abstract class AbstractPredicate extends AbstractExpression<Boolean> implements IPredicate {

    @Override
    public final MappingType mappingMeta() {
        return MappingFactory.getDefaultMapping(Boolean.class);
    }

    @Override
    public final IPredicate or(@Nullable IPredicate... andIPredicates) {
        if (andIPredicates == null || andIPredicates.length == 0) {
            return this;
        }
        List<IPredicate> predicateList;
        if (andIPredicates.length == 1) {
            predicateList = Collections.singletonList(andIPredicates[0]);
        } else {
            predicateList = ArrayUtils.asList(andIPredicates);
        }
        return new OrtPredicateImpl(this, predicateList);
    }

    @Override
    public final IPredicate or(List<IPredicate> andIPredicateList) {
        return new OrtPredicateImpl(this, andIPredicateList);
    }

    @Override
    public final IPredicate not(IPredicate predicate) {
        return NotPredicateImpl.build(predicate);
    }
}
