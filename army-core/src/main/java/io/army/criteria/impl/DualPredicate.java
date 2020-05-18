package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.meta.ChildTableMeta;
import io.army.meta.FieldExp;
import io.army.meta.PrimaryFieldMeta;
import io.army.meta.TableMeta;

/**
 *
 */
abstract class DualPredicate extends AbstractPredicate {

    static DualPredicate build(Expression<?> left, DualOperator operator, Expression<?> right) {
        DualPredicate predicate;

        if (left instanceof FieldExp && right instanceof FieldExp) {
            predicate = createFieldPairPredicate((FieldExp<?, ?>) left, operator, (FieldExp<?, ?>) right);
        } else {
            predicate = new GenericDualPredicate(left, operator, right);
        }
        return predicate;
    }

    static PrimaryValueEqualPredicateImpl buildPrimaryValueEqual(FieldExp<?, ?> primary, ValueExpression<?> valueExp) {
        return new PrimaryValueEqualPredicateImpl(primary, valueExp);
    }

    static DualPredicate build(Expression<?> left, SubQueryOperator operator, SubQuery subQuery) {
        switch (operator) {
            case IN:
            case NOT_IN:
                break;
            default:
                throw new IllegalArgumentException(String.format("operator[%s] not in[EXISTS,NOT_EXISTS].", operator));
        }
        return new GenericDualPredicate(left, operator, subQuery);
    }

    private static DualPredicate createFieldPairPredicate(FieldExp<?, ?> left, DualOperator operator
            , FieldExp<?, ?> right) {

        DualPredicate predicate;

        if (left.tableMeta() == right.tableMeta()) {
            predicate = new FieldPairDualPredicateImpl(left, operator, right);
        } else if (operator == DualOperator.EQ) {
            predicate = createFieldPairEqualPredicate(left, right);
        } else {
            predicate = new GenericDualPredicate(left, operator, right);
        }
        return predicate;
    }

    private static DualPredicate createFieldPairEqualPredicate(FieldExp<?, ?> left, FieldExp<?, ?> right) {
        TableMeta<?> leftTable = left.tableMeta();
        TableMeta<?> rightTable = right.tableMeta();

        DualPredicate predicate = null;
        if (TableMeta.ID.equals(left.propertyName()) && TableMeta.ID.equals(right.propertyName())) {
            if (leftTable.parentMeta() == rightTable) {
                predicate = new ParentChildJoinPredicateImpl(left, right, (ChildTableMeta<?>) leftTable);
            } else if (rightTable.parentMeta() == leftTable) {
                predicate = new ParentChildJoinPredicateImpl(left, right, (ChildTableMeta<?>) rightTable);
            }
        }
        if (predicate == null) {
            predicate = new GenericDualPredicate(left, DualOperator.EQ, right);
        }
        return predicate;
    }


    private static class GenericDualPredicate extends DualPredicate {

        private final SelfDescribed left;

        private final SQLOperator operator;

        private final SelfDescribed right;


        private GenericDualPredicate(SelfDescribed left, SQLOperator operator, SelfDescribed right) {
            this.left = left;
            this.operator = operator;
            this.right = right;
        }


        @Override
        protected void afterSpace(SQLContext context) {
            left.appendSQL(context);
            context.sqlBuilder()
                    .append(" ")
                    .append(operator.rendered());
            right.appendSQL(context);
        }

        @Override
        public String beforeAs() {
            return String.format("%s %s %s", left, operator, right);
        }

    }


    private static final class ParentChildJoinPredicateImpl extends GenericDualPredicate
            implements ParentChildJoinPredicate {

        private final ChildTableMeta<?> childMeta;

        public ParentChildJoinPredicateImpl(SelfDescribed left, SelfDescribed right
                , ChildTableMeta<?> childMeta) {
            super(left, DualOperator.EQ, right);
            this.childMeta = childMeta;
        }

        @Override
        public ChildTableMeta<?> childMeta() {
            return this.childMeta;
        }
    }


    private static final class FieldPairDualPredicateImpl extends DualPredicate
            implements FieldPairDualPredicate {

        private final FieldExp<?, ?> left;

        private final DualOperator operator;

        private final FieldExp<?, ?> right;

        FieldPairDualPredicateImpl(FieldExp<?, ?> left, DualOperator operator, FieldExp<?, ?> right) {
            this.left = left;
            this.operator = operator;
            this.right = right;
        }

        @Override
        public FieldExp<?, ?> left() {
            return this.left;
        }

        @Override
        public DualOperator operator() {
            return this.operator;
        }

        @Override
        public FieldExp<?, ?> right() {
            return this.right;
        }

        @Override
        protected void afterSpace(SQLContext context) {
            context.appendFieldPair(this);
        }

        @Override
        protected String beforeAs() {
            return String.format("%s %s %s", this.left, this.operator, this.right);
        }
    }

    private static class FieldValuePredicateImpl extends DualPredicate implements FieldValuePredicate {

        private final FieldExp<?, ?> fieldExp;

        private final DualOperator operator;

        private final ValueExpression<?> valueExp;

        private FieldValuePredicateImpl(FieldExp<?, ?> fieldExp, DualOperator operator, ValueExpression<?> valueExp) {
            this.fieldExp = fieldExp;
            this.operator = operator;
            this.valueExp = valueExp;
        }

        @Override
        public final FieldExp<?, ?> fieldExp() {
            return this.fieldExp;
        }

        @Override
        public final DualOperator operator() {
            return this.operator;
        }

        @Override
        public final Object value() {
            return this.valueExp.value();
        }

        @Override
        public final void appendPredicate(SQLContext context) {
            this.fieldExp.appendSQL(context);
            context.sqlBuilder()
                    .append(" ")
                    .append(this.operator.rendered());
            this.valueExp.appendSQL(context);
        }

        @Override
        protected final void afterSpace(SQLContext context) {
            context.appendPredicate(this);
        }

        @Override
        protected String beforeAs() {
            return this.fieldExp + operator().rendered() + valueExp.value();
        }
    }

    private static final class PrimaryValueEqualPredicateImpl extends FieldValuePredicateImpl
            implements PrimaryValueEqualPredicate {
        private PrimaryValueEqualPredicateImpl(FieldExp<?, ?> fieldExp, ValueExpression<?> valueExp) {
            super(fieldExp, DualOperator.EQ, valueExp);
            if (!(fieldExp.fieldMeta() instanceof PrimaryFieldMeta)) {
                throw new IllegalArgumentException(String.format("FieldExp[%s] isn't PrimaryFieldMeta.", fieldExp));
            }
        }
    }

}
