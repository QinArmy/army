package io.army.criteria.impl;

import io.army.criteria.IPredicate;
import io.army.criteria.impl.inner._Predicate;
import io.army.mapping._MappingFactory;
import io.army.meta.ParamMeta;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * This class is base class of all {@link IPredicate} implementation .
 */
abstract class OperationPredicate extends OperationExpression<Boolean> implements _Predicate {

    @Override
    public final ParamMeta paramMeta() {
        return _MappingFactory.getMapping(Boolean.class);
    }

    @Override
    public final IPredicate or(IPredicate predicate) {
        return OrPredicate.create(this, predicate);
    }

    @Override
    public final IPredicate or(List<IPredicate> predicates) {
        return OrPredicate.create(this, predicates);
    }

    @Override
    public final <C> IPredicate orMulti(Function<C, List<IPredicate>> function) {
        return this.or(function.apply(CriteriaContextStack.getCriteria()));
    }

    @Override
    public final IPredicate orMulti(Supplier<List<IPredicate>> supplier) {
        return this.or(supplier.get());
    }

    @Override
    public final IPredicate not() {
        return NotPredicate.not(this);
    }

    @Override
    public final boolean isOptimistic() {
        final boolean match;
        final DualPredicate predicate;
        if (!(this instanceof DualPredicate)) {
            match = false;
        } else if ((predicate = (DualPredicate) this).operator != DualOperator.EQ) {
            match = false;
        } else if (predicate.left.isVersion()) {
            match = predicate.right instanceof ValueExpression;
        } else if (predicate.right.isVersion()) {
            match = predicate.left instanceof ValueExpression;
        } else {
            match = false;
        }
        return match;
    }

    /*################################## blow private method ##################################*/


}
