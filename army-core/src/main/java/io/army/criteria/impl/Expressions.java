package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.dialect.SubQuery;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.impl.inner._Predicate;
import io.army.criteria.impl.inner._RowSet;
import io.army.criteria.standard.SQLFunction;
import io.army.dialect._Constant;
import io.army.dialect._SqlContext;
import io.army.lang.Nullable;
import io.army.meta.TypeMeta;
import io.army.util._Exceptions;
import io.army.util._StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

abstract class Expressions<I extends Item> extends OperationExpression<I> {


    final TypeMeta expType;


    Expressions(final OperationExpression<I> expression) {
        super(expression.function);
        if (expression instanceof Expressions) {
            this.expType = ((Expressions<I>) expression).expType;
        } else {
            this.expType = expression.typeMeta();//TODO field codec
        }

    }

    Expressions(final TypeMeta expType, Function<TypeInfer, I> function) {
        super(function);
        this.expType = expType;//TODO field codec
    }

    @Override
    public final TypeMeta typeMeta() {
        return this.expType;
    }

    @Override
    public final OperationExpression<I> bracket() {
        return bracketExp(this);
    }

    static <I extends Item> OperationExpression<I> dualExp(final OperationExpression<I> left
            , final DualOperator operator, final Expression right) {
        final ArmyExpression rightExp = (ArmyExpression) right;
        switch (operator) {
            case PLUS:
            case MINUS:
            case TIMES:
            case DIVIDE:
            case MOD:
            case BITWISE_AND:
            case BITWISE_OR:
            case XOR:
            case RIGHT_SHIFT:
            case LEFT_SHIFT: {
                if (rightExp.isNullValue()) {
                    throw _Exceptions.operatorRightIsNullable(operator);
                }
            }
            break;
            default:
                throw _Exceptions.unexpectedEnum(operator);
        }
        return new DualExpression<>(left, operator, rightExp);
    }

    static <I extends Item> OperationExpression<I> unaryExp(final OperationExpression<I> expression
            , final UnaryOperator operator) {
        switch (operator) {
            case INVERT:
            case NEGATE: {
                if (expression.isNullValue()) {
                    throw _Exceptions.operatorRightIsNullable(operator);
                }
            }
            break;
            default:
                throw _Exceptions.unexpectedEnum(operator);

        }
        return new UnaryExpression<>(expression, operator);
    }

    static <I extends Item> OperationExpression<I> castExp(OperationExpression<I> expression, TypeMeta typeMeta) {
        return new CastExpression<>(expression, typeMeta);
    }

    static <I extends Item> OperationExpression<I> bracketExp(final OperationExpression<I> expression) {
        final OperationExpression<I> bracket;
        if (expression instanceof BracketsExpression
                || expression instanceof SQLFunction) {
            bracket = expression;
        } else {
            bracket = new BracketsExpression<>(expression);
        }
        return bracket;
    }

    static Expression scalarExpression(final SubQuery subQuery) {
        final List<Selection> selectionList;
        selectionList = ((_RowSet) subQuery).selectionList();
        if (selectionList.size() != 1) {
            throw ContextStack.criteriaError(ContextStack.peek(), _Exceptions::nonScalarSubQuery, subQuery);
        }
        ((ArmyDerivedTable) subQuery).setColumnAliasList(CriteriaUtils.EMPTY_STRING_LIST);
        return new ScalarExpression<>(selectionList.get(0).typeMeta(), subQuery, SQLs._IDENTITY);
    }

    static <I extends Item> OperationExpression<I> wrapExpression(final OperationExpression<?> expression,
                                                                  final Function<TypeInfer, I> function) {
        return new OperationExpressionWrapper<>(expression, function);
    }

    private static final class OperationExpressionWrapper<I extends Item> extends Expressions<I> {

        private final ArmyExpression expression;

        /**
         * @see #wrapExpression(OperationExpression, Function)
         */
        private OperationExpressionWrapper(OperationExpression<?> expression, Function<TypeInfer, I> function) {
            super(expression.typeMeta(), function);
            this.expression = expression;
        }

        @Override
        public void appendSql(final _SqlContext context) {
            this.expression.appendSql(context);
        }


    }//OperationExpressionWrapper


    static <I extends Item> OperationPredicate<I> wrapPredicate(final OperationPredicate<?> predicate,
                                                                final Function<TypeInfer, I> function) {
        return new OperationPredicateWrapper<>(predicate, function);
    }


    static OperationPredicate<TypeInfer> existsPredicate(UnaryOperator operator, @Nullable SubQuery subQuery) {
        assert subQuery != null;
        switch (operator) {
            case NOT_EXISTS:
            case EXISTS:
                break;
            default:
                throw _Exceptions.unexpectedEnum(operator);
        }
        return new UnaryPredicate<>(subQuery, operator, SQLs._IDENTITY);
    }


    static <I extends Item> OperationPredicate<I> booleanTestPredicate(final OperationExpression<I> expression,
                                                                       boolean not, SQLs.BooleanTestOperand operand) {
        if (!(operand == SQLs.NULL
                || operand == SQLs.TRUE
                || operand == SQLs.FALSE
                || operand == SQLs.UNKNOWN
                || operand instanceof BooleanTestOperand)) {
            String m = String.format("unknown operand[%s]", operand);
            throw ContextStack.criteriaError(ContextStack.peek(), m);
        }
        return new BooleanTestPredicate<>(expression, not, operand);
    }


    static <I extends Item> OperationPredicate<I> dualPredicate(final OperationExpression<I> left
            , final DualOperator operator, final Expression right) {
        switch (operator) {
            case EQUAL:
            case NOT_EQUAL:
            case LESS:
            case LESS_EQUAL:
            case GREAT:
            case GREAT_EQUAL:
            case LIKE:
            case NOT_LIKE:
            case IN:
            case NOT_IN: {
                if (right instanceof SqlValueParam.MultiValue
                        && operator != DualOperator.IN
                        && operator != DualOperator.NOT_IN) {
                    String m = String.format("operator[%s] don't support multi  parameter(literal)", operator);
                    throw ContextStack.criteriaError(ContextStack.peek(), m);
                }
                if (((ArmyExpression) right).isNullValue()) {
                    throw _Exceptions.operatorRightIsNullable(operator);
                }
            }
            break;
            default:
                throw _Exceptions.unexpectedEnum(operator);

        }
        return new DualPredicate<>(left, operator, right);
    }


    static <I extends Item> OperationPredicate<I> bracketPredicate(final OperationPredicate<I> predicate) {
        final OperationPredicate<I> result;
        if (predicate instanceof BracketPredicate) {
            result = predicate;
        } else {
            result = new BracketPredicate<>(predicate);
        }
        return result;
    }

    static <I extends Item> OperationPredicate<I> orPredicate(OperationPredicate<I> left, IPredicate right) {
        return new OrPredicate<>(left, Collections.singletonList((OperationPredicate<?>) right));
    }

    static <I extends Item> OperationPredicate<I> orPredicate(OperationPredicate<I> left, List<IPredicate> rightList) {
        final int size = rightList.size();
        assert size > 0;
        final List<OperationPredicate<?>> list = new ArrayList<>(size);
        for (IPredicate right : rightList) {
            list.add((OperationPredicate<?>) right);
        }
        return new OrPredicate<>(left, Collections.unmodifiableList(list));
    }

    static <I extends Item> AndPredicate<I> andPredicate(OperationPredicate<I> left, @Nullable IPredicate right) {
        assert right != null;
        return new AndPredicate<>(left, (OperationPredicate<?>) right);
    }

    static <I extends Item> OperationPredicate<I> betweenPredicate(boolean not, OperationExpression<I> left,
                                                                   Expression center, Expression right) {
        return new BetweenPredicate<>(not, left, center, right);
    }

    static <I extends Item> OperationPredicate<I> notPredicate(final OperationPredicate<I> predicate) {
        final OperationPredicate<I> notPredicate;
        if (predicate instanceof NotPredicate) {
            notPredicate = ((NotPredicate<I>) predicate).predicate;
        } else {
            notPredicate = new NotPredicate<>(predicate);
        }
        return notPredicate;
    }

    static <I extends Item> OperationPredicate<I> compareQueryPredicate(OperationExpression<I> left
            , DualOperator operator, QueryOperator queryOperator, SubQuery subQuery) {
        switch (operator) {
            case LESS:
            case LESS_EQUAL:
            case EQUAL:
            case NOT_EQUAL:
            case GREAT:
            case GREAT_EQUAL: {
                switch (queryOperator) {
                    case ALL:
                    case ANY:
                    case SOME:
                        break;
                    default:
                        throw _Exceptions.unexpectedEnum(queryOperator);
                }
                assertColumnSubQuery(operator, queryOperator, subQuery);
            }
            break;
            default:
                throw _Exceptions.unexpectedEnum(operator);
        }
        return new SubQueryPredicate<>(left, operator, queryOperator, subQuery);
    }

    static <I extends Item> OperationPredicate<I> inOperator(final OperationExpression<I> left
            , final DualOperator operator, final SubQuery subQuery) {
        switch (operator) {
            case IN:
            case NOT_IN:
                assertColumnSubQuery(operator, null, subQuery);
                break;
            default:
                throw _Exceptions.unexpectedEnum(operator);
        }
        return new SubQueryPredicate<>(left, operator, null, subQuery);

    }

    /**
     * @see #compareQueryPredicate(OperationExpression, DualOperator, QueryOperator, SubQuery)
     * @see #inOperator(OperationExpression, DualOperator, SubQuery)
     */
    private static void assertColumnSubQuery(final DualOperator operator
            , final @Nullable QueryOperator queryOperator, final SubQuery subQuery) {
        if (((_RowSet) subQuery).selectionList().size() != 1) {
            StringBuilder builder = new StringBuilder();
            builder.append("Operator ")
                    .append(operator.name());
            if (queryOperator != null) {
                builder.append(_Constant.SPACE)
                        .append(queryOperator.name());
            }
            builder.append(" only support column sub query.");
            throw ContextStack.clearStackAndCriteriaError(builder.toString());
        }

    }


    /**
     * This class is a implementation of {@link Expression}.
     * The expression consist of a left {@link Expression} ,a {@link DualOperator} and right {@link Expression}.
     *
     * @since 1.0
     */
    private static final class DualExpression<I extends Item> extends Expressions<I> {


        private final ArmyExpression left;

        private final DualOperator operator;

        private final ArmyExpression right;


        private DualExpression(OperationExpression<I> left, DualOperator operator, ArmyExpression right) {
            super(left);
            this.left = left;
            this.operator = operator;
            this.right = right;
        }


        @Override
        public void appendSql(final _SqlContext context) {

            final _Expression left = this.left, right = this.right;
            final boolean outerBracket, leftInnerBracket, rightInnerBracket;
            switch (this.operator) {
                case PLUS:
                case MINUS:
                case TIMES:
                case DIVIDE:
                case MOD:
                    outerBracket = leftInnerBracket = rightInnerBracket = false;
                    break;
                case LEFT_SHIFT:
                case RIGHT_SHIFT:
                case BITWISE_AND:
                case BITWISE_OR:
                case XOR: {
                    outerBracket = true;
                    leftInnerBracket = !(left instanceof SqlValueParam.SingleValue
                            || left instanceof TableField
                            || left instanceof BracketsExpression);

                    rightInnerBracket = !(right instanceof SqlValueParam.SingleValue
                            || right instanceof TableField
                            || right instanceof BracketsExpression);
                }
                break;
                default:
                    throw _Exceptions.unexpectedEnum(this.operator);
            }

            final StringBuilder builder = context.sqlBuilder();

            if (outerBracket) {
                builder.append(_Constant.SPACE_LEFT_PAREN);
            }

            if (leftInnerBracket) {
                builder.append(_Constant.SPACE_LEFT_PAREN);
            }
            //1. append left expression
            left.appendSql(context);

            if (leftInnerBracket) {
                builder.append(_Constant.SPACE_RIGHT_PAREN);
            }

            //2. append operator
            builder.append(this.operator.spaceOperator);

            if (rightInnerBracket) {
                builder.append(_Constant.SPACE_LEFT_PAREN);
            }

            //3. append right expression
            right.appendSql(context);

            if (rightInnerBracket) {
                builder.append(_Constant.SPACE_RIGHT_PAREN);
            }

            if (outerBracket) {
                builder.append(_Constant.SPACE_RIGHT_PAREN);
            }


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
            } else if (obj instanceof DualExpression) {
                final DualExpression<?> o = (DualExpression<?>) obj;
                match = o.left.equals(this.left)
                        && o.operator == this.operator
                        && o.right.equals(this.right);
            } else {
                match = false;
            }
            return match;
        }

        @Override
        public String toString() {
            return String.format("%s %s%s", this.left, this.operator.spaceOperator, this.right);
        }


    }//DualExpression

    /**
     * <p>
     * This class representing unary expression,unary expression always out outer bracket.
     * </p>
     * This class is a implementation of {@link Expression}.
     * The expression consist of a  {@link Expression} and a {@link UnaryOperator}.
     */
    private static final class UnaryExpression<I extends Item> extends Expressions<I> {

        final ArmyExpression expression;

        private final UnaryOperator operator;

        private UnaryExpression(OperationExpression<I> expression, UnaryOperator operator) {
            super(expression);
            this.expression = expression;
            this.operator = operator;
        }


        @Override
        public void appendSql(final _SqlContext context) {
            final boolean outerBracket;
            switch (this.operator) {
                case NEGATE:
                    outerBracket = false;
                    break;
                case INVERT:
                    outerBracket = true;
                    break;
                default:
                    throw _Exceptions.unexpectedEnum(this.operator);
            }
            final StringBuilder builder = context.sqlBuilder();

            if (outerBracket) {
                builder.append(_Constant.SPACE_LEFT_PAREN);
            }

            builder.append(this.operator.render());

            final _Expression expression = this.expression;
            final boolean innerBracket = !(expression instanceof SqlValueParam.SingleValue
                    || expression instanceof DataField
                    || expression instanceof BracketsExpression);

            if (innerBracket) {
                builder.append(_Constant.SPACE_LEFT_PAREN);
            }
            // append expression
            expression.appendSql(context);

            if (innerBracket) {
                builder.append(_Constant.SPACE_RIGHT_PAREN);
            }

            if (outerBracket) {
                builder.append(_Constant.SPACE_RIGHT_PAREN);
            }

        }

        @Override
        public String toString() {
            final boolean outerBracket;
            switch (this.operator) {
                case NEGATE:
                    outerBracket = false;
                    break;
                case INVERT:
                    outerBracket = true;
                    break;
                default:
                    throw _Exceptions.unexpectedEnum(this.operator);
            }

            final StringBuilder builder = new StringBuilder();

            if (outerBracket) {
                builder.append(_Constant.SPACE_LEFT_PAREN);
            }
            builder.append(_Constant.SPACE)
                    .append(this.operator.render());

            final _Expression expression = this.expression;
            final boolean innerBracket = !(expression instanceof SqlValueParam.SingleValue
                    || expression instanceof TableField
                    || expression instanceof BracketsExpression);

            if (innerBracket) {
                builder.append(_Constant.SPACE_LEFT_PAREN);
            }
            // append expression
            builder.append(expression);

            if (innerBracket) {
                builder.append(_Constant.SPACE_RIGHT_PAREN);
            }

            if (outerBracket) {
                builder.append(_Constant.SPACE_RIGHT_PAREN);
            }
            return builder.toString();
        }


    }//UnaryExpression


    private static final class BracketsExpression<I extends Item> extends Expressions<I> {

        private final OperationExpression<I> expression;


        private BracketsExpression(OperationExpression<I> expression) {
            super(expression);
            this.expression = expression;
        }


        @Override
        public void appendSql(final _SqlContext context) {
            final StringBuilder builder = context.sqlBuilder()
                    .append(_Constant.SPACE_LEFT_PAREN);

            this.expression.appendSql(context);

            builder.append(_Constant.SPACE_RIGHT_PAREN);
        }


        @Override
        public int hashCode() {
            return this.expression.hashCode();
        }

        @Override
        public boolean equals(final Object obj) {
            final boolean match;
            if (obj == this) {
                match = true;
            } else if (obj instanceof BracketsExpression) {
                match = ((BracketsExpression<?>) obj).expression.equals(this.expression);
            } else {
                match = false;
            }
            return match;
        }

        @Override
        public String toString() {
            return _StringUtils.builder()
                    .append(_Constant.SPACE_LEFT_PAREN)
                    .append(this.expression)
                    .append(_Constant.SPACE_RIGHT_PAREN)
                    .toString();
        }


    }//BracketsExpression

    private static final class ScalarExpression<I extends Item> extends Expressions<I> {

        private final SubQuery subQuery;

        private ScalarExpression(TypeMeta expType, SubQuery subQuery, Function<TypeInfer, I> endFunc) {
            super(expType, endFunc);
            this.subQuery = subQuery;
        }

        @Override
        public void appendSql(final _SqlContext context) {
            context.parser().subQuery(this.subQuery, context);
        }


    }//ScalarExpression

    /*-------------------below predicate class-------------------*/

    static final class UnaryPredicate<I extends Item> extends OperationPredicate<I> {

        private final UnaryOperator operator;

        private final SubQuery subQuery;


        private UnaryPredicate(SubQuery query, UnaryOperator operator, Function<TypeInfer, I> function) {
            super(function);
            this.operator = operator;
            this.subQuery = query;
        }

        @Override
        public void appendSql(final _SqlContext context) {
            switch (this.operator) {
                case EXISTS:
                case NOT_EXISTS: {
                    context.sqlBuilder()
                            .append(this.operator.render());
                    context.parser().subQuery(this.subQuery, context);
                }
                break;
                default:
                    throw _Exceptions.unexpectedEnum(this.operator);
            }

        }

        @Override
        public int hashCode() {
            return super.hashCode();
        }

        @Override
        public boolean equals(final Object obj) {
            return obj == this;
        }

        @Override
        public String toString() {
            final StringBuilder builder = new StringBuilder();
            switch (this.operator) {
                case EXISTS:
                case NOT_EXISTS: {
                    builder.append(this.operator.render())
                            .append(this.subQuery);
                }
                break;
                default:
                    throw _Exceptions.unexpectedEnum(this.operator);
            }
            return builder.toString();
        }


    }//UnaryPredicate


    static final class DualPredicate<I extends Item> extends OperationPredicate<I> {


        final ArmyExpression left;

        final DualOperator operator;

        final ArmyExpression right;

        private DualPredicate(OperationExpression<I> left, DualOperator operator, Expression right) {
            super(left.function);
            this.left = left;
            this.operator = operator;
            this.right = (ArmyExpression) right;
        }

        @Override
        public void appendSql(final _SqlContext context) {
            this.left.appendSql(context);

            final DualOperator operator = this.operator;
            context.sqlBuilder().append(operator.spaceOperator);

            final ArmyExpression right = this.right;
            switch (operator) {
                case IN:
                case NOT_IN: {
                    if (right instanceof MultiValueExpression) {
                        ((MultiValueExpression) right).appendSqlWithParens(context);
                    } else {
                        right.appendSql(context);
                    }
                }
                break;
                default:
                    right.appendSql(context);

            }//switch

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
            } else if (obj instanceof DualPredicate) {
                final DualPredicate<?> p = (DualPredicate<?>) obj;
                match = p.operator == this.operator
                        && p.left.equals(this.left)
                        && p.right.equals(this.right);
            } else {
                match = false;
            }
            return match;
        }

        @Override
        public String toString() {
            return _StringUtils.builder()
                    .append(_Constant.SPACE)
                    .append(this.left)
                    .append(this.operator)
                    .append(this.right)
                    .toString();
        }


    }//DualPredicate


    private static final class BracketPredicate<I extends Item> extends OperationPredicate<I> {

        private final OperationPredicate<I> predicate;

        private BracketPredicate(OperationPredicate<I> predicate) {
            super(predicate.function);
            this.predicate = predicate;
        }

        @Override
        public void appendSql(final _SqlContext context) {
            final StringBuilder sqlBuilder;
            sqlBuilder = context.sqlBuilder()
                    .append(_Constant.SPACE_LEFT_PAREN);

            this.predicate.appendSql(context);

            sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);

        }


    }//BracketPredicate


    private static final class OrPredicate<I extends Item> extends OperationPredicate<I> {

        private final OperationPredicate<I> left;

        private final List<OperationPredicate<?>> rightList;

        private OrPredicate(OperationPredicate<I> left, List<OperationPredicate<?>> rightList) {
            super(left.function);
            this.left = left;
            this.rightList = rightList;
        }


        @Override
        public void appendSql(final _SqlContext context) {
            this.appendOrPredicate(context.sqlBuilder(), context);
        }


        @Override
        public int hashCode() {
            return Objects.hash(this.left, this.rightList);
        }

        @Override
        public boolean equals(final Object obj) {
            final boolean match;
            if (obj == this) {
                match = true;
            } else if (obj instanceof OrPredicate) {
                final OrPredicate<?> o = (OrPredicate<?>) obj;
                match = o.left.equals(this.left) && o.rightList.equals(this.rightList);
            } else {
                match = false;
            }
            return match;
        }

        @Override
        public String toString() {
            final StringBuilder builder;
            builder = new StringBuilder();
            this.appendOrPredicate(builder, null);
            return builder.toString();
        }

        private void appendOrPredicate(final StringBuilder builder, final @Nullable _SqlContext context) {
            builder.append(_Constant.SPACE_LEFT_PAREN);// outer left paren

            final OperationPredicate<?> left = this.left;

            if (left instanceof AndPredicate) {
                builder.append(_Constant.SPACE_LEFT_PAREN); //left inner left bracket
            }
            if (context == null) {
                builder.append(left);
            } else {
                left.appendSql(context);
            }
            if (left instanceof AndPredicate) {
                builder.append(_Constant.SPACE_RIGHT_PAREN); //left inner left bracket
            }

            boolean rightInnerParen;
            for (OperationPredicate<?> right : this.rightList) {

                builder.append(_Constant.SPACE_OR);
                rightInnerParen = right instanceof AndPredicate;
                if (rightInnerParen) {
                    builder.append(_Constant.SPACE_LEFT_PAREN); // inner left bracket
                }

                if (context == null) {
                    builder.append(right);
                } else {
                    right.appendSql(context);
                }

                if (rightInnerParen) {
                    builder.append(_Constant.SPACE_RIGHT_PAREN);// inner right bracket
                }
            }

            builder.append(_Constant.SPACE_RIGHT_PAREN); // outer right paren
        }


    }//OrPredicate


    static final class AndPredicate<I extends Item> extends OperationPredicate<I> {

        final OperationPredicate<I> left;

        final OperationPredicate<?> right;

        private AndPredicate(OperationPredicate<I> left, OperationPredicate<?> right) {
            super(left.function);
            this.left = left;
            this.right = right;
        }


        @Override
        public void appendSql(final _SqlContext context) {
            this.left.appendSql(context);

            context.sqlBuilder().append(_Constant.SPACE_AND);

            this.right.appendSql(context);

        }


        @Override
        public String toString() {
            return _StringUtils.builder()
                    .append(this.left)
                    .append(_Constant.SPACE_AND)
                    .append(this.right)
                    .toString();
        }


    }//AndPredicate


    private static class BetweenPredicate<I extends Item> extends OperationPredicate<I> {

        final boolean not;

        final ArmyExpression left;

        final ArmyExpression center;

        final ArmyExpression right;

        /**
         * @see #betweenPredicate(boolean, OperationExpression, Expression, Expression)
         */
        private BetweenPredicate(boolean not, OperationExpression<I> left, Expression center, Expression right) {
            super(left.function);
            this.not = not;
            this.left = left;
            this.center = (ArmyExpression) center;
            this.right = (ArmyExpression) right;
        }

        @Override
        public void appendSql(final _SqlContext context) {
            this.left.appendSql(context);
            final StringBuilder builder;
            builder = context.sqlBuilder();
            if (this.not) {
                builder.append(" NOT");
            }
            builder.append(" BETWEEN");
            this.center.appendSql(context);
            builder.append(" AND");
            this.right.appendSql(context);
        }


        @Override
        public int hashCode() {
            return Objects.hash(this.left, this.center, this.right);
        }

        @Override
        public boolean equals(final Object obj) {
            final boolean match;
            if (obj == this) {
                match = true;
            } else if (obj instanceof BetweenPredicate) {
                final BetweenPredicate<?> o = (BetweenPredicate<?>) obj;
                match = o.left.equals(this.left)
                        && o.center.equals(this.center)
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
                    .append(" BETWEEN")
                    .append(this.center)
                    .append(" AND")
                    .append(this.right)
                    .toString();
        }


    }//BetweenPredicate


    private static class CastExpression<I extends Item> extends Expressions<I> {

        private final ArmyExpression expression;


        private CastExpression(OperationExpression<I> expression, TypeMeta castType) {
            super(castType, expression.function);
            this.expression = expression;
        }


        @Override
        public final void appendSql(final _SqlContext context) {
            this.expression.appendSql(context);
        }

        @Override
        public final String toString() {
            return this.expression.toString();
        }


    }//CastExpression


    private static final class NotPredicate<I extends Item> extends OperationPredicate<I> {

        private final OperationPredicate<I> predicate;

        private NotPredicate(OperationPredicate<I> predicate) {
            super(predicate.function);
            this.predicate = predicate;

        }

        @Override
        public void appendSql(final _SqlContext context) {
            final StringBuilder builder;
            builder = context.sqlBuilder()
                    .append(" NOT");

            final _Predicate predicate = this.predicate;
            final boolean needBracket = !(predicate instanceof OrPredicate);

            if (needBracket) {
                builder.append(_Constant.SPACE_LEFT_PAREN);
            }
            this.predicate.appendSql(context);

            if (needBracket) {
                builder.append(_Constant.SPACE_RIGHT_PAREN);
            }

        }

        @Override
        public int hashCode() {
            return Objects.hash(this.predicate);
        }

        @Override
        public boolean equals(final Object obj) {
            final boolean match;
            if (obj == this) {
                match = true;
            } else if (obj instanceof NotPredicate) {
                final NotPredicate<?> o = (NotPredicate<?>) obj;
                match = o.predicate.equals(this.predicate);
            } else {
                match = false;
            }
            return match;
        }

        @Override
        public String toString() {
            final StringBuilder builder;
            builder = new StringBuilder()
                    .append(" NOT");

            final _Predicate predicate = this.predicate;
            final boolean needBracket = !(predicate instanceof OrPredicate);

            if (needBracket) {
                builder.append(_Constant.SPACE_LEFT_PAREN);
            }
            builder.append(this.predicate);

            if (needBracket) {
                builder.append(_Constant.SPACE_RIGHT_PAREN);
            }
            return builder.toString();
        }


    }//NotPredicate


    private static final class SubQueryPredicate<I extends Item> extends OperationPredicate<I> {
        private final ArmyExpression left;

        private final DualOperator operator;

        private final QueryOperator queryOperator;

        private final SubQuery subQuery;

        private SubQueryPredicate(OperationExpression<I> left, DualOperator operator
                , @Nullable QueryOperator queryOperator, SubQuery subQuery) {
            super(left.function);
            this.left = left;
            this.operator = operator;
            this.queryOperator = queryOperator;
            this.subQuery = subQuery;
        }


        @Override
        public void appendSql(final _SqlContext context) {

            this.left.appendSql(context);

            final StringBuilder sqlBuilder;
            sqlBuilder = context.sqlBuilder()
                    .append(this.operator.spaceOperator);

            final QueryOperator queryOperator = this.queryOperator;
            if (queryOperator != null) {
                sqlBuilder.append(this.queryOperator.rendered());
            }
            context.parser().subQuery(this.subQuery, context);
        }

        @Override
        public String toString() {
            final StringBuilder builder = new StringBuilder()
                    .append(this.left)
                    .append(this.operator.spaceOperator);

            final QueryOperator queryOperator = this.queryOperator;
            if (queryOperator != null) {
                builder.append(this.queryOperator.rendered());
            }

            return builder.append(this.subQuery)
                    .toString();
        }


    }//SubQueryPredicate


    private static final class BooleanTestPredicate<I extends Item> extends OperationPredicate<I> {

        private final OperationExpression<I> expression;

        private final boolean not;


        private final SQLs.BooleanTestOperand operand;

        /**
         * @see #booleanTestPredicate(OperationExpression, boolean, SQLs.BooleanTestOperand)
         */
        private BooleanTestPredicate(OperationExpression<I> expression, boolean not, SQLs.BooleanTestOperand operand) {
            super(expression.function);
            this.expression = expression;
            this.not = not;
            this.operand = operand;
        }

        @Override
        public void appendSql(final _SqlContext context) {
            this.expression.appendSql(context);

            final StringBuilder sqlBuilder;
            sqlBuilder = context.sqlBuilder();
            if (this.not) {
                sqlBuilder.append(" IS NOT");
            } else {
                sqlBuilder.append(" IS");
            }
            sqlBuilder.append(this.operand.render());
        }


        @Override
        public int hashCode() {
            return Objects.hash(this.expression, this.not, this.operand);
        }

        @Override
        public boolean equals(final Object obj) {
            final boolean match;
            if (obj == this) {
                match = true;
            } else if (obj instanceof BooleanTestPredicate) {
                final BooleanTestPredicate<?> o = (BooleanTestPredicate<?>) obj;
                match = o.expression.equals(this.expression)
                        && o.not == this.not
                        && o.operand == this.operand;
            } else {
                match = false;
            }
            return match;
        }

        @Override
        public String toString() {
            final StringBuilder sqlBuilder;
            sqlBuilder = new StringBuilder()
                    .append(this.expression);
            if (this.not) {
                sqlBuilder.append(" IS NOT");
            } else {
                sqlBuilder.append(" IS");
            }

            return sqlBuilder.append(this.operand.render())
                    .toString();
        }


    }//BooleanTestPredicate

    private static final class OperationPredicateWrapper<I extends Item> extends OperationPredicate<I> {

        private final OperationPredicate<?> predicate;

        /**
         * @see #wrapPredicate(OperationPredicate, Function)
         */
        private OperationPredicateWrapper(OperationPredicate<?> predicate, Function<TypeInfer, I> function) {
            super(function);
            this.predicate = predicate;
        }

        @Override
        public void appendSql(final _SqlContext context) {
            this.predicate.appendSql(context);
        }


    }//OperationPredicateWrapper


}
