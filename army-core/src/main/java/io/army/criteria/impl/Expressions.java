package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.dialect.SubQuery;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.impl.inner._Predicate;
import io.army.criteria.impl.inner._RowSet;
import io.army.criteria.impl.inner._SelfDescribed;
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


    /**
     * private constructor
     */
    Expressions(Function<TypeInfer, I> function) {
        super(function);
    }

    @Override
    public final Expressions<I> bracket() {
        return this instanceof BracketsExpression ? this : new BracketsExpression<>(this);
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

    static <I extends Item> OperationExpression<I> bracketExp(OperationExpression<I> expression) {
        return new BracketsExpression<>(expression);
    }

    static Expression scalarExpression(final SubQuery subQuery) {
        final List<? extends SelectItem> selectItemList;
        selectItemList = ((_RowSet) subQuery).selectItemList();
        if (!(selectItemList.size() == 1 && selectItemList.get(0) instanceof Selection)) {
            throw ContextStack.criteriaError(ContextStack.peek(), _Exceptions::nonScalarSubQuery, subQuery);
        }
        return new ScalarExpression(subQuery);
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
        return new UnaryPredicate<>(subQuery, operator, SQLs::_identity);
    }

    static <I extends Item> OperationPredicate<I> unaryPredicate(final UnaryOperator operator
            , final OperationExpression<I> expression) {
        if (expression instanceof SubQuery) {
            throw new IllegalArgumentException("expression couldn't be sub query.");
        }
        switch (operator) {
            case IS_NULL:
            case IS_NOT_NULL:
                break;
            default:
                throw _Exceptions.unexpectedEnum(operator);
        }
        return new UnaryPredicate<>(operator, expression);
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
        if (predicate instanceof BracketPredicate
                || predicate instanceof DataField
                || predicate instanceof SqlValueParam.SingleValue) {
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

    static <I extends Item> OperationPredicate<I> betweenPredicate(OperationExpression<I> left, Expression center
            , Expression right) {
        return new BetweenPredicate<>(left, center, right);
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
        final List<? extends SelectItem> selectItemList;
        selectItemList = subQuery.selectItemList();
        if (selectItemList.size() != 1 || !(selectItemList.get(0) instanceof Selection)) {
            StringBuilder builder = new StringBuilder();
            builder.append("Operator ")
                    .append(operator.name());
            if (queryOperator != null) {
                builder.append(_Constant.SPACE)
                        .append(queryOperator.name());
            }
            builder.append(" only support column sub query.");
            throw ContextStack.criteriaError(ContextStack.peek(), builder.toString());
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
            super(left.function);
            this.left = left;
            this.operator = operator;
            this.right = right;
        }


        @Override
        public TypeMeta typeMeta() {
            return this.left.typeMeta();
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
                DualExpression<?> o = (DualExpression<?>) obj;
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
            super(expression.function);
            this.expression = expression;
            this.operator = operator;
        }

        @Override
        public TypeMeta typeMeta() {
            return this.expression.typeMeta();
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

            builder.append(this.operator.rendered());

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
                    .append(this.operator.rendered());

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

        private final ArmyExpression expression;

        private BracketsExpression(OperationExpression<I> expression) {
            super(expression.function);
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
        public TypeMeta typeMeta() {
            return this.expression.typeMeta();
        }

        @Override
        public String toString() {
            return String.format(" (%s )", this.expression);
        }


    }//BracketsExpression

    private static final class ScalarExpression extends Expressions<TypeInfer> {

        private final SubQuery subQuery;

        private ScalarExpression(SubQuery subQuery) {
            super(SQLs::_identity);
            this.subQuery = subQuery;
        }

        @Override
        public TypeMeta typeMeta() {
            return ((Selection) ((_RowSet) this.subQuery).selectItemList().get(0)).typeMeta();
        }


        @Override
        public void appendSql(final _SqlContext context) {
            context.parser().scalarSubQuery(this.subQuery, context);
        }


    }//ScalarExpression

    /*-------------------below predicate class-------------------*/

    static final class UnaryPredicate<I extends Item> extends OperationPredicate<I> {

        private final UnaryOperator operator;

        private final _SelfDescribed expressionOrSubQuery;

        private UnaryPredicate(UnaryOperator operator, OperationExpression<I> expression) {
            super(expression.function);
            this.operator = operator;
            this.expressionOrSubQuery = expression;
        }

        private UnaryPredicate(SubQuery query, UnaryOperator operator, Function<TypeInfer, I> function) {
            super(function);
            this.operator = operator;
            this.expressionOrSubQuery = (_SelfDescribed) query;
        }

        @Override
        public void appendSql(final _SqlContext context) {
            final _SelfDescribed expressionOrSubQuery = this.expressionOrSubQuery;
            switch (this.operator) {
                case IS_NOT_NULL:
                case IS_NULL: {
                    final boolean innerBracket;
                    innerBracket = !(expressionOrSubQuery instanceof DataField
                            || expressionOrSubQuery instanceof SqlValueParam.SingleValue);

                    final StringBuilder sqlBuilder = context.sqlBuilder();
                    if (innerBracket) {
                        sqlBuilder.append(_Constant.SPACE_LEFT_PAREN);
                    }
                    expressionOrSubQuery.appendSql(context);
                    if (innerBracket) {
                        sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);
                    }
                    sqlBuilder.append(this.operator.rendered());
                }
                break;
                case EXISTS:
                case NOT_EXISTS: {
                    context.sqlBuilder()
                            .append(this.operator.rendered());
                    expressionOrSubQuery.appendSql(context);
                }
                break;
                default:
                    throw _Exceptions.unexpectedEnum(this.operator);
            }

        }

        @Override
        public int hashCode() {
            return Objects.hash(this.operator, this.expressionOrSubQuery);
        }

        @Override
        public boolean equals(final Object obj) {
            final boolean match;
            if (obj == this) {
                match = true;
            } else if (obj instanceof UnaryPredicate) {
                final UnaryPredicate<?> o = (UnaryPredicate<?>) obj;
                match = o.operator == this.operator
                        && o.expressionOrSubQuery.equals(this.expressionOrSubQuery);
            } else {
                match = false;
            }
            return match;
        }

        @Override
        public String toString() {
            final StringBuilder builder = new StringBuilder();
            switch (this.operator) {
                case IS_NOT_NULL:
                case IS_NULL: {
                    builder.append(this.expressionOrSubQuery)
                            .append(this.operator.rendered());
                }
                break;
                case EXISTS:
                case NOT_EXISTS: {
                    builder.append(this.operator.rendered())
                            .append(this.expressionOrSubQuery);
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

        final ArmyExpression left;

        final ArmyExpression center;

        final ArmyExpression right;

        private BetweenPredicate(OperationExpression<I> left, Expression center, Expression right) {
            super(left.function);
            this.left = left;
            this.center = (ArmyExpression) center;
            this.right = (ArmyExpression) right;
        }

        @Override
        public void appendSql(final _SqlContext context) {
            this.left.appendSql(context);
            final StringBuilder builder = context.sqlBuilder()
                    .append(" BETWEEN");
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
            return String.format(" %s BETWEEN%s AND%s", left, center, right);
        }


    }//BetweenPredicate


    private static class CastExpression<I extends Item> extends Expressions<I> {

        private final _Expression expression;

        private final TypeMeta typeMeta;

        private CastExpression(OperationExpression<I> expression, TypeMeta typeMeta) {
            super(expression.function);
            this.expression = expression;
            this.typeMeta = typeMeta;
        }

        @Override
        public final void appendSql(final _SqlContext context) {
            this.expression.appendSql(context);
        }


        @Override
        public TypeMeta typeMeta() {
            return this.typeMeta;
        }


        @Override
        public final String toString() {
            return _StringUtils.builder()
                    .append(_Constant.SPACE)
                    .append(this.expression)
                    .append(" typeMeta:")
                    .append(this.typeMeta)
                    .toString();
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
            context.parser().scalarSubQuery(this.subQuery, context);
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


}
