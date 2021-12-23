package io.army.criteria.impl;

import io.army.criteria.GenericField;
import io.army.criteria.IPredicate;
import io.army.criteria.NamedParam;
import io.army.criteria.impl.inner._Predicate;
import io.army.lang.Nullable;
import io.army.mapping._MappingFactory;
import io.army.meta.ChildTableMeta;
import io.army.meta.FieldMeta;
import io.army.meta.ParamMeta;
import io.army.meta.TableMeta;
import io.army.sharding.DatabaseRoute;
import io.army.sharding.Route;
import io.army.sharding.RouteContext;
import io.army.sharding.TableRoute;

import java.util.List;
import java.util.function.Function;

/**
 * This class is base class of all {@link IPredicate} implementation .
 */
abstract class AbstractPredicate extends OperationExpression<Boolean> implements _Predicate {

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
    public final IPredicate or(IPredicate predicate1, IPredicate predicate2, IPredicate predicate3) {
        return OrPredicate.create(this, predicate1, predicate2, predicate3);
    }

    @Override
    public final IPredicate or(List<IPredicate> predicates) {
        return OrPredicate.create(this, predicates);
    }

    @Override
    public final IPredicate not() {
        return NotPredicate.not(this);
    }

    @Override
    public final byte databaseIndex(Function<TableMeta<?>, Route> function) {
        if (!(this instanceof DualPredicate)) {
            return -1;
        }
        final DualPredicate predicate = (DualPredicate) this;
        final byte index;
        if (predicate.operator != DualOperator.EQ) {
            index = -1;
        } else if (predicate.left instanceof GenericField
                && predicate.right instanceof ValueExpression
                && ((GenericField<?, ?>) predicate.left).databaseRoute()) {

            final GenericField<?, ?> field = (GenericField<?, ?>) predicate.left;
            final Object value = ((ValueExpression<?>) predicate.right).value();
            if (value == null) {
                index = -1;
            } else {
                final DatabaseRoute route = (DatabaseRoute) function.apply(field.tableMeta());
                index = route.database(value);
            }
        } else if (predicate.left instanceof ValueExpression
                && predicate.right instanceof GenericField
                && ((GenericField<?, ?>) predicate.right).databaseRoute()) {

            final GenericField<?, ?> field = (GenericField<?, ?>) predicate.right;
            final Object value = ((ValueExpression<?>) predicate.left).value();
            if (value == null) {
                index = -1;
            } else {
                final DatabaseRoute route = (DatabaseRoute) function.apply(field.tableMeta());
                index = route.database(value);
            }

        } else {
            index = -1;
        }

        return index;
    }


    @Override
    public final byte tableIndex(final TableMeta<?> table, final RouteContext context) {
        if (!(this instanceof DualPredicate)) {
            return -1;
        }
        final DualPredicate predicate = (DualPredicate) this;
        if (predicate.operator != DualOperator.EQ
                || !(predicate.left instanceof GenericField)
                || !(predicate.right instanceof ValueExpression)) {
            return -1;
        }
        final GenericField<?, ?> field = (GenericField<?, ?>) predicate.left;
        if (!field.tableRoute()) {
            return -1;
        }
        final TableMeta<?> belongOf = field.tableMeta();
        if (table instanceof ChildTableMeta) {
            if (belongOf != table && belongOf != ((ChildTableMeta<?>) table).parentMeta()) {
                return -1;
            }
        } else if (belongOf != table) {
            return -1;
        }

        final byte index;
        final TableRoute route = (TableRoute) (context.route(field.tableMeta()));
        final Object value = ((ValueExpression<?>) predicate.right).value();
        if (value == null) {
            index = -1;
        } else {
            index = route.table(value);
        }
        return index;
    }

    @Nullable
    @Override
    public final FieldMeta<?, ?> databaseRouteField(final TableMeta<?> table) {
        if (!(this instanceof DualPredicate)) {
            return null;
        }
        final DualPredicate predicate = (DualPredicate) this;
        final FieldMeta<?, ?> routeField;
        if (predicate.operator != DualOperator.EQ) {
            routeField = null;
        } else if (predicate.left instanceof GenericField
                && predicate.right instanceof NamedParam
                && ((GenericField<?, ?>) predicate.left).databaseRoute()) {
            routeField = routeField((GenericField<?, ?>) predicate.left, table);
        } else if (predicate.left instanceof NamedParam
                && predicate.right instanceof GenericField
                && ((GenericField<?, ?>) predicate.right).databaseRoute()) {
            routeField = routeField((GenericField<?, ?>) predicate.right, table);
        } else {
            routeField = null;
        }
        return routeField;
    }

    @Nullable
    @Override
    public final FieldMeta<?, ?> tableRouteField(final TableMeta<?> table) {
        if (!(this instanceof DualPredicate)) {
            return null;
        }
        final DualPredicate predicate = (DualPredicate) this;
        final FieldMeta<?, ?> routeField;
        if (predicate.operator != DualOperator.EQ) {
            routeField = null;
        } else if (predicate.left instanceof GenericField
                && predicate.right instanceof NamedParam
                && ((GenericField<?, ?>) predicate.left).tableRoute()) {
            routeField = routeField((GenericField<?, ?>) predicate.left, table);
        } else if (predicate.left instanceof NamedParam
                && predicate.right instanceof GenericField
                && ((GenericField<?, ?>) predicate.right).tableRoute()) {
            routeField = routeField((GenericField<?, ?>) predicate.right, table);
        } else {
            routeField = null;
        }
        return routeField;
    }

    /*################################## blow private method ##################################*/

    /**
     * @see #databaseRouteField(TableMeta)
     * @see #tableRouteField(TableMeta)
     */
    @Nullable
    private FieldMeta<?, ?> routeField(final GenericField<?, ?> field, final TableMeta<?> table) {
        final TableMeta<?> belongOf = field.tableMeta();
        final FieldMeta<?, ?> routeField;
        if (belongOf == table
                || (table instanceof ChildTableMeta && belongOf == ((ChildTableMeta<?>) table).parentMeta())) {
            if (field instanceof FieldMeta) {
                routeField = (FieldMeta<?, ?>) field;
            } else {
                routeField = field.fieldMeta();
            }
        } else {
            routeField = null;
        }
        return routeField;
    }


}
