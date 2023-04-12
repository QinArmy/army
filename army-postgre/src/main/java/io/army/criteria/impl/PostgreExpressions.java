package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.dialect.Database;
import io.army.dialect._Constant;
import io.army.dialect._SqlContext;
import io.army.lang.Nullable;
import io.army.meta.TypeMeta;
import io.army.util._Exceptions;
import io.army.util._StringUtils;

import java.util.Objects;
import java.util.function.BiFunction;

abstract class PostgreExpressions {

    private PostgreExpressions() {
        throw new UnsupportedOperationException();
    }


    /**
     * @see Postgres#period(Expression, Expression)
     */
    static PostgreSyntax._PeriodOverlapsClause overlaps(final @Nullable Expression start,
                                                        final @Nullable Expression endOrLength) {
        if (start == null || endOrLength == null) {
            throw ContextStack.clearStackAndNullPointer();
        } else if (start instanceof SqlValueParam.MultiValue || endOrLength instanceof SqlValueParam.MultiValue) {
            throw overlapsDontSupportMultiValue();
        }
        return new PeriodOverlapsPredicate(start, endOrLength);
    }

    /**
     * @see Postgres#at(Expression)
     */
    static Expression unaryExp(final UnaryOperator operator, final @Nullable Expression operand,
                               final TypeMeta returnType) {
        if (operator != UnaryOperator.AT) {
            // no bug,never here
            throw _Exceptions.unexpectedEnum(operator);
        }
        if (operand == null) {
            throw ContextStack.clearStackAndNullPointer();
        } else if (operand instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.operandError(operator, operand);
        }
        return new PostgreUnaryExpression(operator, operand, returnType);
    }


    /**
     * @see Postgres#atTimeZone(Expression, Expression)
     * @see Postgres#verticals(Expression, Expression)
     */
    static Expression dualExp(final @Nullable Expression left, final DualOperator operator,
                              @Nullable final Expression right, final TypeMeta returnType) {
        switch (operator) {
            case PLUS:
            case MINUS:
            case TIMES:
            case DIVIDE:
            case MOD:
            case DOUBLE_VERTICAL:// postgre only
            case AT_TIME_ZONE:// postgre only
            case BITWISE_AND:
            case BITWISE_OR:
            case XOR:
            case RIGHT_SHIFT:
            case LEFT_SHIFT:
                break;
            default:
                // no bug,never here
                throw _Exceptions.unexpectedEnum(operator);
        }
        if (left == null || right == null) {
            throw ContextStack.clearStackAndNullPointer();
        } else if (left instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.operandError(operator, left);
        } else if (right instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.operandError(operator, right);
        }
        return new PostgreDualExpression(left, operator, right, returnType);
    }

    /**
     * @see Postgres#caretAt(Expression, Expression)
     */
    static IPredicate dualPredicate(final @Nullable Expression left, final DualOperator operator,
                                    @Nullable final Expression right) {
        if (operator != DualOperator.CARET_AT) {
            // no bug,never here
            throw _Exceptions.unexpectedEnum(operator);
        }

        if (left == null || right == null) {
            throw ContextStack.clearStackAndNullPointer();
        } else if (left instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.operandError(operator, left);
        } else if (right instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.operandError(operator, right);
        }

        return new PostgreDualPredicate(left, operator, right);
    }

    /*-------------------below private -------------------*/

    private static CriteriaException overlapsDontSupportMultiValue() {
        String m = "Postgre OVERLAPS operator don't support multi-value parameter/literal";
        return ContextStack.clearStackAndCriteriaError(m);
    }

    private static final class PeriodOverlapsPredicate extends OperationPredicate
            implements PostgreSyntax._PeriodOverlapsClause {

        private final ArmyExpression start1;

        private final ArmyExpression endOrLength1;

        private ArmyExpression start2;

        private ArmyExpression endOrLength2;

        /**
         * @see Postgres#period(Expression, Expression)
         */
        private PeriodOverlapsPredicate(Expression start1, Expression endOrLength1) {
            this.start1 = (OperationExpression) start1;
            this.endOrLength1 = (OperationExpression) endOrLength1;
        }

        @Override
        public IPredicate overlaps(final @Nullable Expression start, final @Nullable Expression endOrLength) {
            if (start == null || endOrLength == null) {
                throw ContextStack.clearStackAndNullPointer();
            } else if (start instanceof SqlValueParam.MultiValue || endOrLength instanceof SqlValueParam.MultiValue) {
                throw overlapsDontSupportMultiValue();
            } else if (this.start2 != null || this.endOrLength2 != null) {
                throw ContextStack.clearStackAnd(_Exceptions::castCriteriaApi);
            }
            this.start2 = (OperationExpression) start;
            this.endOrLength2 = (OperationExpression) endOrLength;
            return this;
        }

        @Override
        public <T> IPredicate overlaps(Expression start, BiFunction<Expression, T, Expression> valueOperator, T value) {
            return this.overlaps(start, valueOperator.apply(start, value));
        }

        @Override
        public <T> IPredicate overlaps(BiFunction<Expression, T, Expression> valueOperator, T value, Expression endOrLength) {
            return this.overlaps(valueOperator.apply(endOrLength, value), endOrLength);
        }

        @Override
        public IPredicate overlaps(TypeInfer type, BiFunction<TypeInfer, Object, Expression> valueOperator, Object start, Object endOrLength) {
            return this.overlaps(valueOperator.apply(type, start), valueOperator.apply(type, endOrLength));
        }

        @Override
        public void appendSql(final _SqlContext context) {
            final ArmyExpression start2 = this.start2, endOrLength2 = this.endOrLength2;
            if (start2 == null || endOrLength2 == null) {
                throw _Exceptions.castCriteriaApi();
            }
            final StringBuilder sqlBuilder;
            sqlBuilder = context.sqlBuilder()
                    .append(_Constant.SPACE_LEFT_PAREN);

            this.start1.appendSql(context);
            sqlBuilder.append(_Constant.SPACE_COMMA);
            this.endOrLength1.appendSql(context);

            sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN)
                    .append(" OVERLAPS")
                    .append(_Constant.SPACE_LEFT_PAREN);

            start2.appendSql(context);
            sqlBuilder.append(_Constant.SPACE_COMMA);
            endOrLength2.appendSql(context);

            sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);
        }


        @Override
        public int hashCode() {
            return Objects.hash(this.start1, this.endOrLength1, this.start2, this.endOrLength2);
        }

        @Override
        public boolean equals(final Object obj) {
            final boolean match;
            if (obj == this) {
                match = true;
            } else if (obj instanceof PeriodOverlapsPredicate) {
                final PeriodOverlapsPredicate o = (PeriodOverlapsPredicate) obj;
                match = o.start1.equals(this.start1)
                        && o.endOrLength1.equals(this.endOrLength1)
                        && Objects.equals(o.start2, this.start2)
                        && Objects.equals(o.endOrLength2, this.endOrLength2);
            } else {
                match = false;
            }
            return match;
        }

        @Override
        public String toString() {
            return _StringUtils.builder()
                    .append(_Constant.SPACE_LEFT_PAREN)
                    .append(this.start1)
                    .append(_Constant.SPACE_COMMA)
                    .append(this.endOrLength1)
                    .append(_Constant.SPACE_RIGHT_PAREN)

                    .append(" OVERLAPS")

                    .append(_Constant.SPACE_LEFT_PAREN)
                    .append(this.start2)
                    .append(_Constant.SPACE_COMMA)
                    .append(this.endOrLength2)
                    .append(_Constant.SPACE_RIGHT_PAREN)
                    .toString();
        }


    }//PeriodOverlapsPredicate


    private static final class PostgreUnaryExpression extends OperationExpression {

        private final UnaryOperator operator;

        private final ArmyExpression operand;

        private final TypeMeta returnType;


        /**
         * @see #unaryExp(UnaryOperator, Expression, TypeMeta)
         */
        private PostgreUnaryExpression(UnaryOperator operator, Expression operand, TypeMeta returnType) {
            this.operator = operator;
            this.operand = (OperationExpression) operand;
            this.returnType = returnType;
        }

        @Override
        public TypeMeta typeMeta() {
            return this.returnType;
        }

        @Override
        public void appendSql(final _SqlContext context) {
            final Database database;
            database = context.parser().dialect().database();
            if (database != Database.PostgreSQL) {
                String m = String.format("%s don't support %s", database, this.getClass().getName());
                throw new CriteriaException(m);
            }

            if (this.operator != UnaryOperator.AT) {
                throw _Exceptions.unexpectedEnum(this.operator);
            }
            context.sqlBuilder().append(this.operator.spaceOperator);
            this.operand.appendSql(context);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.operator, this.operand, this.returnType);
        }

        @Override
        public boolean equals(final Object obj) {
            final boolean match;
            if (obj == this) {
                match = true;
            } else if (obj instanceof PostgreUnaryExpression) {
                final PostgreUnaryExpression o = (PostgreUnaryExpression) obj;
                match = o.operator.equals(this.operator)
                        && o.operand.equals(this.operand)
                        && o.returnType.equals(this.returnType);
            } else {
                match = false;
            }
            return match;
        }

        @Override
        public String toString() {
            return _StringUtils.builder()
                    .append(this.operator.spaceOperator)
                    .append(this.operand)
                    .toString();
        }


    }//PostgreUnaryExpression

    private static final class PostgreDualExpression extends OperationExpression {

        private final ArmyExpression left;

        private final DualOperator operator;

        private final ArmyExpression right;

        private final TypeMeta returnType;


        /**
         * @see #dualExp(Expression, DualOperator, Expression, TypeMeta)
         */
        private PostgreDualExpression(Expression left, DualOperator operator, Expression right, TypeMeta returnType) {
            this.left = (OperationExpression) left;
            this.operator = operator;
            this.right = (OperationExpression) right;
            this.returnType = returnType;
        }

        @Override
        public TypeMeta typeMeta() {
            return this.returnType;
        }

        @Override
        public void appendSql(final _SqlContext context) {
            final Database database;
            database = context.parser().dialect().database();
            if (database != Database.PostgreSQL) {
                String m = String.format("%s don't support %s", database, this.getClass().getName());
                throw new CriteriaException(m);
            }
            // 1. append left expression
            this.left.appendSql(context);
            // 2. append operator
            final DualOperator operator = this.operator;
            if (operator == DualOperator.XOR) {
                context.sqlBuilder().append(" #");
            } else {
                context.sqlBuilder().append(operator.spaceOperator);
            }
            // 3. append right expression
            this.right.appendSql(context);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.left, this.operator, this.right, this.returnType);
        }

        @Override
        public boolean equals(final Object obj) {
            final boolean match;
            if (obj == this) {
                match = true;
            } else if (obj instanceof PostgreDualExpression) {
                final PostgreDualExpression o = (PostgreDualExpression) obj;
                match = o.left.equals(this.left)
                        && o.operator.equals(this.operator)
                        && o.right.equals(this.right)
                        && o.returnType.equals(this.returnType);
            } else {
                match = false;
            }
            return match;
        }

        @Override
        public String toString() {
            final StringBuilder builder = new StringBuilder();
            builder.append(this.left);
            if (this.operator == DualOperator.XOR) {
                builder.append(" #");
            } else {
                builder.append(this.operator.spaceOperator);
            }
            return builder.append(this.right)
                    .toString();
        }


    }//PostgreDualExpression

    private static final class PostgreDualPredicate extends OperationPredicate {

        private final ArmyExpression left;

        private final DualOperator operator;

        private final ArmyExpression right;

        /**
         * @see Postgres#caretAt(Expression, Expression)
         */
        private PostgreDualPredicate(Expression left, DualOperator operator, Expression right) {
            if (operator != DualOperator.CARET_AT) {
                // no bug,never here
                throw _Exceptions.unexpectedEnum(operator);
            }
            this.left = (OperationExpression) left;
            this.operator = operator;
            this.right = (OperationExpression) right;
        }


        @Override
        public void appendSql(final _SqlContext context) {
            final Database database;
            database = context.parser().dialect().database();
            if (database != Database.PostgreSQL) {
                String m = String.format("%s don't support %s", database, this.getClass().getName());
                throw new CriteriaException(m);
            }
            // 1. append left expression
            this.left.appendSql(context);
            // 2. append operator
            context.sqlBuilder().append(operator.spaceOperator);
            // 3. append right expression
            this.right.appendSql(context);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.left, this.operator, this.right);
        }

        @Override
        public boolean equals(final Object obj) {
            final boolean match;
            if (obj == this) {
                match = true;
            } else if (obj instanceof PostgreDualPredicate) {
                final PostgreDualPredicate o = (PostgreDualPredicate) obj;
                match = o.left.equals(this.left)
                        && o.operator.equals(this.operator)
                        && o.right.equals(this.right);
            } else {
                match = false;
            }
            return match;
        }

        @Override
        public String toString() {
            return _StringUtils.builder()
                    .append(this.left)
                    .append(this.operator.spaceOperator)
                    .append(this.right)
                    .toString();
        }


    }//PostgreDualPredicate


}
