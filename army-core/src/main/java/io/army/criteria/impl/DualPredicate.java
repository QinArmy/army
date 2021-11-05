package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.meta.*;
import io.army.modelgen.MetaBridge;
import io.army.util.Assert;

import java.util.Collection;

/**
 *
 */
class DualPredicate extends AbstractPredicate {

    static DualPredicate build(Expression<?> left, DualPredicateOperator operator, Expression<?> right) {
        DualPredicate predicate;
        if (left instanceof FieldExpression) {
            predicate = buildLeftFieldPredicate((FieldExpression<?>) left, operator, right);
        } else if (right instanceof FieldExpression) {
            predicate = buildRightFieldPredicate(left, operator, (FieldExpression<?>) right);
        } else {
            predicate = new DualPredicate(left, operator, right);
        }
        return predicate;
    }

    static PrimaryValueEqualPredicate buildPrimaryValueEqual(PrimaryFieldMeta<?, ?> primary
            , ValueExpression<?> valueExp) {
        return new LeftPrimaryValueEqualPredicate(primary, DualPredicateOperator.EQ, valueExp);
    }


    private static DualPredicate buildLeftFieldPredicate(FieldExpression<?> left, DualPredicateOperator operator
            , Expression<?> right) {
        DualPredicate predicate;
        if (right instanceof FieldExpression) {
            predicate = buildFieldPair(left, operator, (FieldExpression<?>) right);
        } else if (left instanceof GenericField) {
            predicate = buildLeftField((GenericField<?, ?>) left, operator, right);
        } else {
            predicate = new FieldPredicateImpl(left, operator, right);
        }
        return predicate;
    }

    private static DualPredicate buildRightFieldPredicate(Expression<?> left, DualPredicateOperator operator
            , FieldExpression<?> right) {
        DualPredicate predicate;
        if (left instanceof FieldExpression) {
            predicate = buildFieldPair((FieldExpression<?>) left, operator, right);
        } else if (right instanceof GenericField) {
            predicate = buildRightField(left, operator, (GenericField<?, ?>) right);
        } else {
            predicate = new FieldPredicateImpl(left, operator, right);
        }
        return predicate;
    }

    private static DualPredicate buildFieldPair(FieldExpression<?> left, DualPredicateOperator operator
            , FieldExpression<?> right) {
        DualPredicate predicate;
        if (left instanceof GenericField && right instanceof GenericField) {
            predicate = buildFieldPair((GenericField<?, ?>) left, operator, (GenericField<?, ?>) right);
        } else if (left instanceof GenericField) {
            predicate = buildLeftField((GenericField<?, ?>) left, operator, right);
        } else if (right instanceof GenericField) {
            predicate = buildRightField(left, operator, (GenericField<?, ?>) right);
        } else {
            predicate = new FieldPredicateImpl(left, operator, right);
        }
        return predicate;
    }

    private static DualPredicate buildLeftField(GenericField<?, ?> left, DualPredicateOperator operator
            , Expression<?> right) {
        DualPredicate predicate;
        if (right instanceof ValueExpression) {
            if ((left instanceof PrimaryFieldMeta) && operator == DualPredicateOperator.EQ) {
                predicate = new LeftPrimaryValueEqualPredicate((PrimaryFieldMeta<?, ?>) left, operator
                        , (ValueExpression<?>) right);
            } else {
                predicate = new LeftFieldValuePredicate(left, operator, (ValueExpression<?>) right);
            }

        } else {
            predicate = new FieldPredicateImpl(left, operator, right);
        }
        return predicate;
    }

    private static DualPredicate buildRightField(Expression<?> left, DualPredicateOperator operator
            , GenericField<?, ?> right) {
        DualPredicate predicate;
        if (left instanceof ValueExpression) {
            if ((right instanceof PrimaryFieldMeta) && operator == DualPredicateOperator.EQ) {
                predicate = new RightPrimaryValueEqualPredicate((ValueExpression<?>) left, operator
                        , (PrimaryFieldMeta<?, ?>) right);
            } else {
                predicate = new RightFieldValuePredicate((ValueExpression<?>) left, operator, right);
            }
        } else {
            predicate = new FieldPredicateImpl(left, operator, right);
        }
        return predicate;
    }

    private static DualPredicate buildFieldPair(GenericField<?, ?> left, DualPredicateOperator operator
            , GenericField<?, ?> right) {
        DualPredicate predicate;
        if (left.tableMeta().parentMeta() == right.tableMeta()) {
            predicate = buildParentChild(left, operator, right, (ChildTableMeta<?>) left.tableMeta());
        } else if (right.tableMeta().parentMeta() == left.tableMeta()) {
            predicate = buildParentChild(left, operator, right, (ChildTableMeta<?>) right.tableMeta());
        } else {
            predicate = new FieldPredicateImpl(left, operator, right);
        }
        return predicate;
    }

    private static DualPredicate buildParentChild(GenericField<?, ?> left, DualPredicateOperator operator
            , GenericField<?, ?> right, ChildTableMeta<?> childMeta) {
        DualPredicate predicate = null;
        if (operator == DualPredicateOperator.EQ) {
            if (MetaBridge.ID.equals(left.propertyName()) && MetaBridge.ID.equals(right.propertyName())) {
                predicate = new ParentChildJoinPredicateImpl(left, operator, right, childMeta);
            }
        }
        if (predicate == null) {
            predicate = new FieldPredicateImpl(left, operator, right);
        }
        return predicate;
    }

    /*################################## blow instance member ##################################*/

    final Expression<?> left;

    final DualPredicateOperator operator;

    final Expression<?> right;

    private DualPredicate(Expression<?> left, DualPredicateOperator operator, Expression<?> right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    @Override
    public void appendSQL(SQLContext context) {
        doAppendSQL(context);
    }

    final void doAppendSQL(SQLContext context) {
        left.appendSQL(context);
        context.sqlBuilder()
                .append(" ")
                .append(operator.rendered());
        right.appendSQL(context);
    }

    @Override
    public boolean containsSubQuery() {
        return this.left.containsSubQuery() || this.right.containsSubQuery();
    }

    @Override
    public final String toString() {
        return String.format("%s %s %s", left, operator, right);
    }

    /*################################## blow private static inner class ##################################*/


    private static class FieldPredicateImpl extends DualPredicate
            implements FieldPredicate {

        private FieldPredicateImpl(Expression<?> left, DualPredicateOperator operator, Expression<?> right) {
            super(left, operator, right);
        }

        @Override
        public final void appendSQL(SQLContext context) {
            context.appendFieldPredicate(this);
        }

        @Override
        public final void appendPredicate(SQLContext context) {
            doAppendSQL(context);
        }

        @Override
        public boolean containsField(Collection<FieldMeta<?, ?>> fieldMetas) {
            return this.left.containsField(fieldMetas)
                    || this.right.containsField(fieldMetas);
        }

        @Override
        public boolean containsFieldOf(TableMeta<?> tableMeta) {
            return this.left.containsFieldOf(tableMeta)
                    || this.right.containsFieldOf(tableMeta);
        }

        @Override
        public int containsFieldCount(TableMeta<?> tableMeta) {
            return this.left.containsFieldCount(tableMeta)
                    + this.right.containsFieldCount(tableMeta);
        }


    }

    private static class LeftFieldValuePredicate extends FieldPredicateImpl implements FieldValuePredicate {

        private LeftFieldValuePredicate(GenericField<?, ?> left, DualPredicateOperator operator
                , ValueExpression<?> right) {
            super(left, operator, right);
        }

        @Override
        public final DualPredicateOperator operator() {
            return this.operator;
        }

        @Override
        public final Object value() {
            return ((ValueExpression<?>) this.right).value();
        }

        @Override
        public FieldMeta<?, ?> fieldMeta() {
            return ((GenericField<?, ?>) this.left).fieldMeta();
        }

        @Override
        public final boolean containsField(Collection<FieldMeta<?, ?>> fieldMetas) {
            return this.left.containsField(fieldMetas);
        }

        @Override
        public final boolean containsFieldOf(TableMeta<?> tableMeta) {
            return this.left.containsFieldOf(tableMeta);
        }

        @Override
        public final int containsFieldCount(TableMeta<?> tableMeta) {
            return this.left.containsFieldCount(tableMeta);
        }

        @Override
        public final boolean containsSubQuery() {
            return false;
        }
    }

    private static class RightFieldValuePredicate extends FieldPredicateImpl implements FieldValuePredicate {

        private RightFieldValuePredicate(ValueExpression<?> left, DualPredicateOperator operator
                , GenericField<?, ?> right) {
            super(left, operator, right);
        }

        @Override
        public final DualPredicateOperator operator() {
            return this.operator;
        }

        @Override
        public final Object value() {
            return ((ValueExpression<?>) this.left).value();
        }

        @Override
        public FieldMeta<?, ?> fieldMeta() {
            return ((GenericField<?, ?>) this.right).fieldMeta();
        }

        @Override
        public final boolean containsField(Collection<FieldMeta<?, ?>> fieldMetas) {
            return this.right.containsField(fieldMetas);
        }

        @Override
        public final boolean containsFieldOf(TableMeta<?> tableMeta) {
            return this.right.containsFieldOf(tableMeta);
        }

        @Override
        public final int containsFieldCount(TableMeta<?> tableMeta) {
            return this.right.containsFieldCount(tableMeta);
        }

        @Override
        public final boolean containsSubQuery() {
            return false;
        }
    }

    private static final class LeftPrimaryValueEqualPredicate extends LeftFieldValuePredicate
            implements PrimaryValueEqualPredicate {

        private LeftPrimaryValueEqualPredicate(PrimaryFieldMeta<?, ?> left, DualPredicateOperator operator
                , ValueExpression<?> right) {
            super(left, operator, right);
            Assert.isTrue(operator == DualPredicateOperator.EQ, "");
        }

        @Override
        public PrimaryFieldMeta<?, ?> fieldMeta() {
            return (PrimaryFieldMeta<?, ?>) this.left;
        }
    }

    private static final class RightPrimaryValueEqualPredicate extends RightFieldValuePredicate
            implements PrimaryValueEqualPredicate {

        private RightPrimaryValueEqualPredicate(ValueExpression<?> left, DualPredicateOperator operator
                , PrimaryFieldMeta<?, ?> right) {
            super(left, operator, right);
            Assert.isTrue(operator == DualPredicateOperator.EQ, "");
        }

        @Override
        public PrimaryFieldMeta<?, ?> fieldMeta() {
            return (PrimaryFieldMeta<?, ?>) this.right;
        }
    }

    private static final class ParentChildJoinPredicateImpl extends FieldPredicateImpl
            implements ParentChildJoinPredicate {

        private final ChildTableMeta<?> childMeta;

        private ParentChildJoinPredicateImpl(Expression<?> left, DualPredicateOperator operator, Expression<?> right
                , ChildTableMeta<?> childMeta) {
            super(left, operator, right);
            this.childMeta = childMeta;
        }

        @Override
        public ChildTableMeta<?> childMeta() {
            return this.childMeta;
        }

        @Override
        public boolean containsSubQuery() {
            return false;
        }
    }


}
