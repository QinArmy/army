package io.army.criteria.impl;

import io.army.criteria.IPredicate;
import io.army.lang.Nullable;
import io.army.meta.mapping.MappingFactory;
import io.army.meta.mapping.MappingMeta;
import io.army.util.ArrayUtils;

import java.util.List;

/**
 * created  on 2018/11/25.
 */
abstract class AbstractPredicate extends AbstractExpression<Boolean> implements IPredicate {

    @Override
    public final MappingMeta mappingMeta() {
        return MappingFactory.getDefaultMapping(Boolean.class);
    }

    @Override
    public final IPredicate or(@Nullable IPredicate... andIPredicates) {
        if (andIPredicates == null) {
            return this;
        }
        return new OrtPredicateImpl(this, ArrayUtils.asList(andIPredicates));
    }

    @Override
    public final IPredicate or(List<IPredicate> andIPredicateList) {
        return new OrtPredicateImpl(this, andIPredicateList);
    }


}
