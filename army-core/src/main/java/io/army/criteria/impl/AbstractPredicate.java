package io.army.criteria.impl;

import io.army.criteria.GenericField;
import io.army.criteria.IPredicate;
import io.army.criteria.NamedParam;
import io.army.criteria.ValueExpression;
import io.army.criteria.impl.inner._Predicate;
import io.army.lang.Nullable;
import io.army.mapping.MappingType;
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
    public final MappingType mappingType() {
        return _MappingFactory.getMapping(Boolean.class);
    }

    @Override
    public final ParamMeta paramMeta() {
        return _MappingFactory.getMapping(Boolean.class);
    }

    @Override
    public final IPredicate or(IPredicate predicate) {
        return OrtPredicate.create(this, predicate);
    }

    @Override
    public final IPredicate or(IPredicate predicate1, IPredicate predicate2) {
        return OrtPredicate.create(this, predicate1, predicate2);
    }

    @Override
    public final IPredicate or(IPredicate predicate1, IPredicate predicate2, IPredicate predicate3) {
        return OrtPredicate.create(this, predicate1, predicate2, predicate3);
    }

    @Override
    public final IPredicate or(List<IPredicate> predicates) {
        return OrtPredicate.create(this, predicates);
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
        if (predicate.operator != DualOperator.EQ
                || !(predicate.left instanceof GenericField)
                || !(predicate.right instanceof ValueExpression)) {
            return -1;
        }
        final GenericField<?, ?> field = (GenericField<?, ?>) predicate.left;
        if (!field.databaseRoute()) {
            return -1;
        }
        final byte index;
        final DatabaseRoute route = (DatabaseRoute) function.apply(field.tableMeta());
        final Object value = ((ValueExpression<?>) predicate.right).value();
        if (value == null) {
            index = -1;
        } else {
            index = route.database(value);
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
    public final FieldMeta<?, ?> tableRouteField(final TableMeta<?> table) {
        if (!(this instanceof DualPredicate)) {
            return null;
        }
        final DualPredicate predicate = (DualPredicate) this;
        if (predicate.operator != DualOperator.EQ
                || !(predicate.left instanceof GenericField)
                || !(predicate.right instanceof NamedParam)) {
            return null;
        }
        final GenericField<?, ?> field = (GenericField<?, ?>) predicate.left;
        final TableMeta<?> belongOf = field.tableMeta();

        final FieldMeta<?, ?> tableRouteField;
        if (!field.tableRoute()) {
            tableRouteField = null;
        } else if (belongOf == table) {
            tableRouteField = field.fieldMeta();
        } else if (table instanceof ChildTableMeta) {
            if (belongOf == ((ChildTableMeta<?>) table).parentMeta()) {
                tableRouteField = field.fieldMeta();
            } else {
                tableRouteField = null;
            }
        } else {
            tableRouteField = null;
        }
        return tableRouteField;
    }


}
