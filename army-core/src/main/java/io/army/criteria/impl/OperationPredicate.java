package io.army.criteria.impl;

import io.army.criteria.GenericField;
import io.army.criteria.IPredicate;
import io.army.criteria.NamedParam;
import io.army.criteria.impl.inner._Predicate;
import io.army.mapping._MappingFactory;
import io.army.meta.ChildTableMeta;
import io.army.meta.ParamMeta;
import io.army.meta.TableMeta;
import io.army.modelgen._MetaBridge;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * This class is base class of all {@link IPredicate} implementation .
 */
abstract class OperationPredicate extends OperationExpression implements _Predicate {

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
    public final IPredicate and(IPredicate predicate) {
        return AndPredicate.create(this, predicate);
    }

    @Override
    public final IPredicate and(List<IPredicate> predicates) {
        return AndPredicate.create(this, predicates);
    }

    @Override
    public final IPredicate not() {
        return NotPredicate.not(this);
    }

    @Override
    public final boolean isOptimistic() {
        final boolean match;
        final DualPredicate predicate;
        if (!(this instanceof DualPredicate) || (predicate = (DualPredicate) this).operator != DualOperator.EQ) {
            match = false;
        } else if (predicate.left instanceof GenericField
                && _MetaBridge.VERSION.equals(((GenericField<?>) predicate.left).fieldName())) {
            match = predicate.right instanceof ValueExpression
                    || predicate.right instanceof NamedParam;
        } else if (predicate.right instanceof GenericField
                && _MetaBridge.VERSION.equals(((GenericField<?>) predicate.right).fieldName())) {
            match = predicate.left instanceof ValueExpression
                    || predicate.left instanceof NamedParam;

        } else {
            match = false;
        }
        return match;
    }

    @Override
    public final boolean isParentChildJoin() {
        final boolean match;
        final DualPredicate predicate;
        if (!(this instanceof DualPredicate) || (predicate = (DualPredicate) this).operator != DualOperator.EQ) {
            match = false;
        } else if (!(predicate.left instanceof GenericField && predicate.right instanceof GenericField)) {
            match = false;
        } else if (_MetaBridge.ID.equals(((GenericField<?>) predicate.left).fieldName())
                && _MetaBridge.ID.equals(((GenericField<?>) predicate.right).fieldName())) {
            final TableMeta<?> leftTable, rightTable;
            leftTable = ((GenericField<?>) predicate.left).tableMeta();
            rightTable = ((GenericField<?>) predicate.right).tableMeta();
            if (leftTable instanceof ChildTableMeta) {
                match = ((ChildTableMeta<?>) leftTable).parentMeta() == rightTable;
            } else if (rightTable instanceof ChildTableMeta) {
                match = ((ChildTableMeta<?>) rightTable).parentMeta() == leftTable;
            } else {
                match = false;
            }
        } else {
            match = false;
        }
        return match;
    }

    /*################################## blow private method ##################################*/


}
