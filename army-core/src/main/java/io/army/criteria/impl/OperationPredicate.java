package io.army.criteria.impl;

import io.army.criteria.IPredicate;
import io.army.criteria.impl.inner._Predicate;
import io.army.lang.Nullable;
import io.army.mapping._MappingFactory;
import io.army.meta.FieldMeta;
import io.army.meta.ParamMeta;
import io.army.meta.TableMeta;
import io.army.sharding.RouteContext;

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
    public final IPredicate or(IPredicate predicate1, IPredicate predicate2) {
        return OrPredicate.create(this, predicate1, predicate2);
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
    public final byte databaseIndex(TableMeta<?> table, RouteContext context) {
        return 0;
    }


    @Override
    public final byte tableIndex(final TableMeta<?> table, final RouteContext context) {
        return 0;
    }

    @Nullable
    @Override
    public final FieldMeta<?, ?> databaseRouteField(final TableMeta<?> table) {
        return null;
    }

    @Nullable
    @Override
    public final FieldMeta<?, ?> tableRouteField(final TableMeta<?> table) {
        return null;
    }

    /*################################## blow private method ##################################*/


}