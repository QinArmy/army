package io.army.criteria.impl;

import io.army.criteria.IPredicate;
import io.army.criteria.impl.inner._Predicate;
import io.army.mapping._MappingFactory;
import io.army.meta.ParamMeta;
import io.army.stmt.ParamValue;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
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
    public final <C> IPredicate or(Function<C, List<IPredicate>> function) {
        return OrPredicate.create(this, function.apply(CriteriaContextStack.getCriteria()));
    }

    @Override
    public final IPredicate or(Supplier<List<IPredicate>> supplier) {
        return OrPredicate.create(this, supplier.get());
    }

    @Override
    public final IPredicate or(Consumer<List<IPredicate>> consumer) {
        List<IPredicate> list = new ArrayList<>();
        consumer.accept(list);
        return OrPredicate.create(this, list);
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
            match = predicate.right instanceof ValueExpression
                    || predicate.right instanceof ParamValue; // named param
        } else if (predicate.right.isVersion()) {
            match = predicate.left instanceof ValueExpression
                    || predicate.left instanceof ParamValue;// named param

        } else {
            match = false;
        }
        return match;
    }

    /*################################## blow private method ##################################*/


}
