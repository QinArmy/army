package io.army.criteria.impl;

import io.army.criteria.IPredicate;
import io.army.criteria.impl.inner._Predicate;
import io.army.lang.Nullable;
import io.army.mapping.MappingType;
import io.army.mapping._MappingFactory;

import java.util.List;

/**
 * This class is base class of all {@link IPredicate} implementation .
 */
abstract class AbstractPredicate extends AbstractExpression<Boolean> implements _Predicate {

    @Override
    public final MappingType mappingMeta() {
        return _MappingFactory.getMapping(Boolean.class);
    }

    @Override
    public final IPredicate or(@Nullable IPredicate... andIPredicates) {
        if (andIPredicates == null || andIPredicates.length == 0) {
            return this;
        }
        return new OrtPredicateImpl(this, andIPredicates);
    }

    @Override
    public final IPredicate or(List<IPredicate> andIPredicateList) {
        if (andIPredicateList.size() == 0) {
            return this;
        }
        return new OrtPredicateImpl(this, andIPredicateList);
    }

    @Override
    public final IPredicate not(IPredicate predicate) {
        return NotPredicateImpl.build((_Predicate) predicate);
    }
}
