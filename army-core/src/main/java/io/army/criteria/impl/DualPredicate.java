package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._Expression;
import io.army.dialect._SqlContext;
import io.army.meta.*;
import io.army.modelgen._MetaBridge;
import io.army.util.Assert;

import java.util.Collection;

/**
 *
 */
class DualPredicate extends AbstractPredicate {

    static DualPredicate build(_Expression<?> left, DualPredicateOperator operator, _Expression<?> right) {
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
            , _Expression<?> right) {
        DualPredicate predicate;
        if (right instanceof FieldExpression) {
            predicate = buildFieldPair(left, operator, (FieldExpression<?>) right);
        } else if (left instanceof GenericField) {
            predicate = buildLeftField((GenericField<?, ?>) left, operator, right);
        } else {
            predicate = new FieldPredicateImpl((_Expression<?>) left, operator, right);
        }
        return predicate;
    }

    private static DualPredicate buildRightFieldPredicate(_Expression<?> left, DualPredicateOperator operator
            , FieldExpression<?> right) {
        DualPredicate predicate;
        if (left instanceof FieldExpression) {
            predicate = buildFieldPair((FieldExpression<?>) left, operator, right);
        } else if (right instanceof GenericField) {
            predicate = buildRightField(left, operator, (GenericField<?, ?>) right);
        } else {
            predicate = new FieldPredicateImpl(left, operator, (_Expression<?>) right);
        }
        return predicate;
    }

    private static DualPredicate buildFieldPair(FieldExpression<?> left, DualPredicateOperator operator
            , FieldExpression<?> right) {
        DualPredicate predicate;
        if (left instanceof GenericField && right instanceof GenericField) {
            predicate = buildFieldPair((GenericField<?, ?>) left, operator, (GenericField<?, ?>) right);
        } else if (left instanceof GenericField) {
            predicate = buildLeftField((GenericField<?, ?>) left, operator, (_Expression<?>) right);
        } else if (right instanceof GenericField) {
            predicate = buildRightField((_Expression<?>) left, operator, (GenericField<?, ?>) right);
        } else {
            predicate = new FieldPredicateImpl((_Expression<?>) left, operator, (_Expression<?>) right);
        }
        return predicate;
    }

    private static DualPredicate buildLeftField(GenericField<?, ?> left, DualPredicateOperator operator
            , _Expression<?> right) {
        DualPredicate predicate;
        if (right instanceof ValueExpression) {
            if ((left instanceof PrimaryFieldMeta) && operator == DualPredicateOperator.EQ) {
                predicate = new LeftPrimaryValueEqualPredicate((PrimaryFieldMeta<?, ?>) left, operator
                        , (ValueExpression<?>) right);
            } else {
                predicate = new LeftFieldValuePredicate(left, operator, (ValueExpression<?>) right);
            }

        } else {
            predicate = new FieldPredicateImpl((_Expression<?>) left, operator, right);
        }
        return predicate;
    }

    private static DualPredicate buildRightField(_Expression<?> left, DualPredicateOperator operator
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
            predicate = new FieldPredicateImpl(left, operator, (_Expression<?>) right);
        }
        return predicate;
    }

    private static DualPredicate buildFieldPair(GenericField<?, ?> left, DualPredicateOperator operator
            , GenericField<?, ?> right) {
//        DualPredicate predicate;
//        if (left.tableMeta().parentMeta() == right.tableMeta()) {
//            predicate = buildParentChild(left, operator, right, (ChildTableMeta<?>) left.tableMeta());
//        } else if (right.tableMeta().parentMeta() == left.tableMeta()) {
//            predicate = buildParentChild(left, operator, right, (ChildTableMeta<?>) right.tableMeta());
//        } else {
//            predicate = new FieldPredicateImpl(left, operator, right);
//        }
        return null;
    }

    private static DualPredicate buildParentChild(GenericField<?, ?> left, DualPredicateOperator operator
            , GenericField<?, ?> right, ChildTableMeta<?> childMeta) {
        DualPredicate predicate = null;
        if (operator == DualPredicateOperator.EQ) {
            if (_MetaBridge.ID.equals(left.fieldName()) && _MetaBridge.ID.equals(right.fieldName())) {
                predicate = new ParentChildJoinPredicateImpl((_Expression<?>) left, operator, (_Expression<?>) right, childMeta);
            }
        }
        if (predicate == null) {
            predicate = new FieldPredicateImpl((_Expression<?>) left, operator, (_Expression<?>) right);
        }
        return predicate;
    }

    /*################################## blow instance member ##################################*/

    final _Expression<?> left;

    final DualPredicateOperator operator;

    final _Expression<?> right;

    private DualPredicate(_Expression<?> left, DualPredicateOperator operator, _Expression<?> right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    @Override
    public void appendSql(_SqlContext context) {
        doAppendSQL(context);
    }

    final void doAppendSQL(_SqlContext context) {
        left.appendSql(context);
        context.sqlBuilder()
                .append(" ")
                .append(operator.rendered());
        right.appendSql(context);
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

        private FieldPredicateImpl(_Expression<?> left, DualPredicateOperator operator, _Expression<?> right) {
            super(left, operator, right);
        }

        @Override
        public final void appendSql(_SqlContext context) {
            context.appendFieldPredicate(this);
        }

        //@Override
        public final void appendPredicate(_SqlContext context) {
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
            super((_Expression<?>) left, operator, (_Expression<?>) right);
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
            super((_Expression<?>) left, operator, (_Expression<?>) right);
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

        private ParentChildJoinPredicateImpl(_Expression<?> left, DualPredicateOperator operator, _Expression<?> right
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
