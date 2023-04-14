package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.dialect.SubQuery;
import io.army.criteria.impl.inner._DerivedTable;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.impl.inner._Predicate;
import io.army.criteria.standard.SQLFunction;
import io.army.dialect.Database;
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

abstract class Expressions {

    Expressions() {
        throw new UnsupportedOperationException();
    }

    static OperationExpression dualExp(final Expression left, final DualOperator operator, final Expression right) {
        if (!(left instanceof OperationExpression)) {
            throw NonOperationExpression.nonOperationExpression(left);
        } else if (!(right instanceof OperationExpression)) {
            throw NonOperationExpression.nonOperationExpression(right);
        }
        final ArmyExpression rightExp = (ArmyExpression) right;
        final TypeMeta returnType;
        switch (operator) {
            case PLUS:
            case MINUS:
            case TIMES:
            case DIVIDE:
            case MOD:
                returnType = Functions._returnType(left, right, ExpTypes::mathExpType);
                break;
            case BITWISE_AND:
            case BITWISE_OR:
            case XOR:
            case RIGHT_SHIFT:
            case LEFT_SHIFT:
                returnType = Functions._returnType(left, right, ExpTypes::bitwiseType);
                break;
            default:
                throw _Exceptions.unexpectedEnum(operator);
        }
        return new DualExpression(left, operator, rightExp, returnType);
    }

    static OperationExpression dialectDualExp(final Expression left, final DualOperator operator, final Expression right,
                                              final TypeMeta returnType) {
        if (!(left instanceof OperationExpression)) {
            throw NonOperationExpression.nonOperationExpression(left);
        } else if (!(right instanceof OperationExpression)) {
            throw NonOperationExpression.nonOperationExpression(right);
        }
        return new DualExpression(left, operator, right, returnType);
    }


    static OperationExpression unaryExp(final Expression expression, final UnaryOperator operator) {
        if (!(expression instanceof OperationExpression)) {
            throw NonOperationExpression.nonOperationExpression(expression);
        } else if (expression instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.operandError(operator, expression);
        }
        switch (operator) {
            case BITWISE_NOT:
            case NEGATE:
                break;
            default:
                throw _Exceptions.unexpectedEnum(operator);
        }
        return new UnaryExpression(expression, operator, expression.typeMeta());
    }

    static OperationExpression dialectUnaryExp(final UnaryOperator operator, final Expression expression,
                                               final TypeMeta returnType) {
        if (!(expression instanceof OperationExpression)) {
            throw NonOperationExpression.nonOperationExpression(expression);
        } else if (expression instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.operandError(operator, expression);
        }
        switch (operator) {
            case BITWISE_NOT:
            case NEGATE:
            case VERTICAL_SLASH:
            case DOUBLE_VERTICAL_SLASH:
                break;
            default:
                throw _Exceptions.unexpectedEnum(operator);
        }
        return new UnaryExpression(expression, operator, returnType);
    }

    static OperationExpression castExp(OperationExpression expression, TypeMeta typeMeta) {
        return new CastExpression(expression, typeMeta);
    }

    static Expression bracketExp(final @Nullable Expression expression) {
        final Expression bracket;
        if (expression == null) {
            throw ContextStack.clearStackAndNullPointer();
        } else if (expression instanceof BracketsExpression
                || expression instanceof BracketPredicate
                || expression instanceof SQLFunction) {
            bracket = expression;
        } else if (expression instanceof IPredicate) {
            bracket = new BracketPredicate((OperationPredicate) expression);
        } else {
            bracket = new BracketsExpression((ArmyExpression) expression);
        }
        return bracket;
    }

    static Expression scalarExpression(final SubQuery subQuery) {
        final List<? extends Selection> selectionList;
        selectionList = ((_DerivedTable) subQuery).refAllSelection();
        if (selectionList.size() != 1) {
            throw ContextStack.criteriaError(ContextStack.peek(), _Exceptions::nonScalarSubQuery, subQuery);
        }
        return new ScalarExpression(selectionList.get(0).typeMeta(), subQuery);
    }


    static OperationPredicate existsPredicate(UnaryOperator operator, @Nullable SubQuery subQuery) {
        if (subQuery == null) {
            throw ContextStack.clearStackAndNullPointer();
        }
        switch (operator) {
            case NOT_EXISTS:
            case EXISTS:
                break;
            default:
                throw _Exceptions.unexpectedEnum(operator);
        }
        return new UnaryPredicate(subQuery, operator);
    }


    static OperationPredicate booleanTestPredicate(final OperationExpression expression,
                                                   boolean not, SQLsSyntax.BooleanTestWord operand) {
        if (!(operand == SQLs.NULL
                || operand == SQLs.TRUE
                || operand == SQLs.FALSE
                || operand == SQLs.UNKNOWN
                || operand instanceof BooleanTestWord)) {
            String m = String.format("unknown operand[%s]", operand);
            throw ContextStack.criteriaError(ContextStack.peek(), m);
        }
        return new BooleanTestPredicate(expression, not, operand);
    }

    static OperationPredicate isComparisonPredicate(final OperationExpression left, boolean not,
                                                    SQLsSyntax.IsComparisonWord operator, @Nullable Expression right) {
        if (right == null) {
            throw ContextStack.clearStackAndNullPointer();
        }
        //TODO validate operator
        return new IsComparisonPredicate(left, not, operator, (ArmyExpression) right);
    }


    static OperationPredicate dualPredicate(final Expression left, final DualOperator operator, final Expression right) {
        if (!(left instanceof OperationExpression)) {
            throw NonOperationExpression.nonOperationExpression(left);
        } else if (!(right instanceof OperationExpression)) {
            throw NonOperationExpression.nonOperationExpression(right);
        } else if (left instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.operandError(operator, left);
        }
        switch (operator) {
            case EQUAL:
            case NOT_EQUAL:
            case LESS:
            case LESS_EQUAL:
            case GREAT:
            case GREAT_EQUAL:

            case CARET_AT:
            case TILDE:
            case NOT_TILDE:
            case TILDE_STAR:
            case NOT_TILDE_STAR: {
                if (right instanceof SqlValueParam.MultiValue) {
                    throw CriteriaUtils.operandError(operator, right);
                }
            }
            break;
            case IN:
            case NOT_IN:
                break;
            default:
                throw _Exceptions.unexpectedEnum(operator);

        }
        return new DualPredicate(left, operator, right);
    }

    static OperationPredicate likePredicate(final Expression left, final DualOperator operator,
                                            final @Nullable Expression right, SqlSyntax.WordEscape escape,
                                            @Nullable final Expression escapeChar) {
        switch (operator) {
            case LIKE:
            case NOT_LIKE:
            case SIMILAR_TO: // currently,postgre only
            case NOT_SIMILAR_TO: // currently,postgre only
                break;
            default:
                // no bug,never here
                throw _Exceptions.unexpectedEnum(operator);
        }
        if (!(left instanceof OperationExpression)) {
            throw NonOperationExpression.unsupportedOperation();
        } else if (escape != SQLs.ESCAPE) {
            throw CriteriaUtils.errorModifier(escape);
        } else if (right == null) {
            throw ContextStack.clearStackAndNullPointer();
        }
        return new LikePredicate((OperationExpression) left, operator, right, escapeChar);
    }


    static OperationPredicate bracketPredicate(final IPredicate predicate) {
        final OperationPredicate result;
        if (predicate instanceof BracketPredicate) {
            result = (BracketPredicate) predicate;
        } else {
            result = new BracketPredicate((OperationPredicate) predicate);
        }
        return result;
    }

    static OperationPredicate orPredicate(OperationPredicate left, IPredicate right) {
        return new OrPredicate(left, Collections.singletonList((OperationPredicate) right));
    }

    static OperationPredicate orPredicate(OperationPredicate left, List<IPredicate> rightList) {
        final int size = rightList.size();
        assert size > 0;
        final List<OperationPredicate> list = new ArrayList<>(size);
        for (IPredicate right : rightList) {
            list.add((OperationPredicate) right);
        }
        return new OrPredicate(left, Collections.unmodifiableList(list));
    }

    static AndPredicate andPredicate(OperationPredicate left, @Nullable IPredicate right) {
        assert right != null;
        return new AndPredicate(left, (OperationPredicate) right);
    }

    static OperationPredicate betweenPredicate(
            boolean not, @Nullable SQLsSyntax.BetweenModifier modifier, OperationExpression left,
            Expression center, Expression right) {
        //TODO validate modifier
        return new BetweenPredicate(not, modifier, left, center, right);
    }

    static OperationPredicate notPredicate(final Expression predicate) {
        final OperationPredicate notPredicate;
        if (predicate instanceof NotPredicate) {
            notPredicate = ((NotPredicate) predicate).predicate;
        } else {
            notPredicate = new NotPredicate((OperationPredicate) predicate);
        }
        return notPredicate;
    }

    static OperationPredicate compareQueryPredicate(OperationExpression left
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
        return new SubQueryPredicate(left, operator, queryOperator, subQuery);
    }

    static OperationPredicate inOperator(final OperationExpression left, final DualOperator operator,
                                         final SubQuery subQuery) {
        switch (operator) {
            case IN:
            case NOT_IN:
                assertColumnSubQuery(operator, null, subQuery);
                break;
            default:
                throw _Exceptions.unexpectedEnum(operator);
        }
        return new SubQueryPredicate(left, operator, null, subQuery);

    }


    /**
     * @see #compareQueryPredicate(OperationExpression, DualOperator, QueryOperator, SubQuery)
     * @see #inOperator(OperationExpression, DualOperator, SubQuery)
     */
    private static void assertColumnSubQuery(final DualOperator operator
            , final @Nullable QueryOperator queryOperator, final SubQuery subQuery) {
        if (((_DerivedTable) subQuery).refAllSelection().size() != 1) {
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

    private static CriteriaException unsupportedOperator(Enum<?> operator, Database database) {
        String m = String.format("%s isn't supported by %s", operator, database);
        return new CriteriaException(m);
    }


    /**
     * This class is a implementation of {@link Expression}.
     * The expression consist of a left {@link Expression} ,a {@link DualOperator} and right {@link Expression}.
     *
     * @since 1.0
     */
    private static final class DualExpression extends OperationExpression.CompoundExpression {


        private final ArmyExpression left;

        private final DualOperator operator;

        private final ArmyExpression right;

        private final TypeMeta returnType;

        /**
         * @see #dualExp(Expression, DualOperator, Expression)
         * @see #dialectDualExp(Expression, DualOperator, Expression, TypeMeta)
         */
        private DualExpression(final Expression left, final DualOperator operator, final Expression right,
                               final TypeMeta returnType) {
            if (left instanceof SqlValueParam.MultiValue) {
                throw CriteriaUtils.operandError(operator, left);
            } else if (right instanceof SqlValueParam.MultiValue) {
                throw CriteriaUtils.operandError(operator, right);
            }
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
                case LEFT_SHIFT:

                case CARET:
                case DOUBLE_VERTICAL:
                case AT_TIME_ZONE:
                    break;
                default:
                    throw _Exceptions.unexpectedEnum(operator);
            }

            this.left = (ArmyExpression) left;
            this.operator = operator;
            this.right = (ArmyExpression) right;
            this.returnType = returnType;
        }


        @Override
        public TypeMeta typeMeta() {
            return this.returnType;
        }

        @Override
        public void appendSql(final _SqlContext context) {

            final DualOperator operator = this.operator;
            switch (operator) {
                case CARET:
                case DOUBLE_VERTICAL:
                case AT_TIME_ZONE: {
                    final Database database;
                    database = context.parser().dialect().database();
                    if (database != Database.PostgreSQL) {
                        throw unsupportedOperator(operator, database);
                    }
                }
                break;
                default:
                    // no-op
            }

            final ArmyExpression left = this.left, right = this.right;
            final boolean leftInnerParens, rightInnerParens;
            leftInnerParens = !(left instanceof OperationExpression.SimpleExpression);
            rightInnerParens = !(right instanceof OperationExpression.SimpleExpression);

            final StringBuilder sqlBuilder;
            sqlBuilder = context.sqlBuilder();

            if (leftInnerParens) {
                sqlBuilder.append(_Constant.SPACE_LEFT_PAREN);
            }
            //1. append left expression
            left.appendSql(context);

            if (leftInnerParens) {
                sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);
            }

            //2. append operator
            if (operator == DualOperator.XOR
                    && context.parser().dialect().database() == Database.PostgreSQL) {
                sqlBuilder.append(" #");
            } else {
                sqlBuilder.append(operator.spaceOperator);
            }

            if (rightInnerParens) {
                sqlBuilder.append(_Constant.SPACE_LEFT_PAREN);
            }

            //3. append right expression
            right.appendSql(context);

            if (rightInnerParens) {
                sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);
            }

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
            } else if (obj instanceof DualExpression) {
                final DualExpression o = (DualExpression) obj;
                match = o.left.equals(this.left)
                        && o.operator == this.operator
                        && o.right.equals(this.right)
                        && o.returnType.equals(this.returnType);
            } else {
                match = false;
            }
            return match;
        }

        @Override
        public String toString() {
            final ArmyExpression left = this.left, right = this.right;
            final boolean leftInnerParens, rightInnerParens;
            leftInnerParens = !(left instanceof OperationExpression.SimpleExpression);
            rightInnerParens = !(right instanceof OperationExpression.SimpleExpression);

            final StringBuilder sqlBuilder;
            sqlBuilder = new StringBuilder();

            if (leftInnerParens) {
                sqlBuilder.append(_Constant.SPACE_LEFT_PAREN);
            }
            //1. append left expression
            sqlBuilder.append(this.left);

            if (leftInnerParens) {
                sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);
            }

            //2. append operator
            sqlBuilder.append(this.operator.spaceOperator);

            if (rightInnerParens) {
                sqlBuilder.append(_Constant.SPACE_LEFT_PAREN);
            }

            //3. append right expression
            sqlBuilder.append(this.right);

            if (rightInnerParens) {
                sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);
            }

            return sqlBuilder.toString();
        }


    }//DualExpression

    /**
     * <p>
     * This class representing unary expression,unary expression always out outer bracket.
     * </p>
     * This class is a implementation of {@link Expression}.
     * The expression consist of a  {@link Expression} and a {@link UnaryOperator}.
     */
    private static final class UnaryExpression extends OperationExpression.CompoundExpression {

        final ArmyExpression expression;

        private final UnaryOperator operator;

        private final TypeMeta returnType;

        /**
         * @see #unaryExp(Expression, UnaryOperator)
         */
        private UnaryExpression(Expression expression, UnaryOperator operator, TypeMeta returnType) {
            this.expression = (ArmyExpression) expression;
            this.operator = operator;
            this.returnType = returnType;
        }

        @Override
        public TypeMeta typeMeta() {
            return this.returnType;
        }


        @Override
        public void appendSql(final _SqlContext context) {

            final UnaryOperator operator = this.operator;

            if (operator == UnaryOperator.AT
                    && context.parser().dialect().database() != Database.PostgreSQL) {
                String m;
                m = String.format("%s don't support %s", context.parser().dialect().database(), this.operator);
                throw new CriteriaException(m);
            }
            final StringBuilder builder;
            builder = context.sqlBuilder()
                    .append(operator.spaceOperator);

            final _Expression expression = this.expression;
            final boolean innerBracket = !(expression instanceof SimpleExpression);

            if (innerBracket) {
                builder.append(_Constant.SPACE_LEFT_PAREN);
            }
            // append expression
            expression.appendSql(context);

            if (innerBracket) {
                builder.append(_Constant.SPACE_RIGHT_PAREN);
            }

        }

        @Override
        public int hashCode() {
            return Objects.hash(this.operator, this.expression, this.returnType);
        }

        @Override
        public boolean equals(final Object obj) {
            final boolean match;
            if (obj == this) {
                match = true;
            } else if (obj instanceof UnaryExpression) {
                final UnaryExpression o = (UnaryExpression) obj;
                match = o.operator == this.operator
                        && o.expression.equals(this.expression)
                        && o.returnType.equals(this.returnType);
            } else {
                match = false;
            }
            return match;
        }

        @Override
        public String toString() {

            final StringBuilder builder = new StringBuilder();
            builder.append(this.operator.spaceOperator);

            final _Expression expression = this.expression;
            final boolean innerBracket = !(expression instanceof SimpleExpression);

            if (innerBracket) {
                builder.append(_Constant.SPACE_LEFT_PAREN);
            }
            // append expression
            builder.append(expression);

            if (innerBracket) {
                builder.append(_Constant.SPACE_RIGHT_PAREN);
            }
            return builder.toString();
        }


    }//UnaryExpression


    private static final class BracketsExpression extends OperationExpression.SimpleExpression {

        private final ArmyExpression expression;


        private BracketsExpression(ArmyExpression expression) {
            this.expression = expression;
        }

        @Override
        public TypeMeta typeMeta() {
            return this.expression.typeMeta();
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
                match = ((BracketsExpression) obj).expression.equals(this.expression);
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

    private static final class ScalarExpression extends OperationExpression.SimpleExpression {

        private final SubQuery subQuery;

        private final TypeMeta type;


        private ScalarExpression(TypeMeta expType, SubQuery subQuery) {
            this.subQuery = subQuery;
            this.type = expType;
        }

        @Override
        public TypeMeta typeMeta() {
            return this.type;
        }

        @Override
        public void appendSql(final _SqlContext context) {
            context.parser().subQuery(this.subQuery, context);
        }


        @Override
        public String toString() {
            return _StringUtils.builder()
                    .append(_Constant.SPACE_LEFT_PAREN)
                    .append(" scalar sub query: ")
                    .append(this.subQuery.getClass().getName())
                    .append(_Constant.SPACE_RIGHT_PAREN)
                    .toString();
        }


    }//ScalarExpression

    /*-------------------below predicate class-------------------*/

    static final class UnaryPredicate extends OperationPredicate {

        private final UnaryOperator operator;

        private final SubQuery subQuery;


        private UnaryPredicate(SubQuery query, UnaryOperator operator) {
            this.operator = operator;
            this.subQuery = query;
        }

        @Override
        public void appendSql(final _SqlContext context) {
            switch (this.operator) {
                case EXISTS:
                case NOT_EXISTS: {
                    context.sqlBuilder()
                            .append(this.operator.spaceRender());
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
                    builder.append(this.operator.spaceRender())
                            .append(this.subQuery);
                }
                break;
                default:
                    throw _Exceptions.unexpectedEnum(this.operator);
            }
            return builder.toString();
        }


    }//UnaryPredicate


    static final class DualPredicate extends OperationPredicate {


        final ArmyExpression left;

        final DualOperator operator;

        final ArmyExpression right;

        /**
         * @see #dualPredicate(Expression, DualOperator, Expression)
         */
        private DualPredicate(Expression left, DualOperator operator, Expression right) {
            this.left = (ArmyExpression) left;
            this.operator = operator;
            this.right = (ArmyExpression) right;
        }

        @Override
        public void appendSql(final _SqlContext context) {
            final DualOperator operator = this.operator;
            switch (operator) {
                case CARET_AT:
                case TILDE:
                case NOT_TILDE:
                case TILDE_STAR:
                case NOT_TILDE_STAR: {
                    if (context.database() != Database.PostgreSQL) {
                        throw unsupportedOperator(operator, context.database());
                    }
                }
                break;
                default:
                    //no-op

            }
            final ArmyExpression left = this.left, right = this.right;
            final boolean leftInnerParens, rightInnerParens;
            leftInnerParens = !(left instanceof SimpleExpression);
            rightInnerParens = !(right instanceof SimpleExpression);

            final StringBuilder sqlBuilder;
            sqlBuilder = context.sqlBuilder();

            if (leftInnerParens) {
                sqlBuilder.append(_Constant.SPACE_LEFT_PAREN);
            }
            left.appendSql(context);

            if (leftInnerParens) {
                sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);
            }

            sqlBuilder.append(operator.spaceOperator);

            if (rightInnerParens) {
                sqlBuilder.append(_Constant.SPACE_LEFT_PAREN);
            }
            right.appendSql(context);

            if (rightInnerParens) {
                sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);
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
            } else if (obj instanceof DualPredicate) {
                final DualPredicate p = (DualPredicate) obj;
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
            final ArmyExpression left = this.left, right = this.right;
            final boolean leftInnerParens, rightInnerParens;
            leftInnerParens = !(left instanceof SimpleExpression);
            rightInnerParens = !(right instanceof SimpleExpression);

            final StringBuilder sqlBuilder;
            sqlBuilder = new StringBuilder();

            if (leftInnerParens) {
                sqlBuilder.append(_Constant.SPACE_LEFT_PAREN);
            }
            sqlBuilder.append(left);

            if (leftInnerParens) {
                sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);
            }

            sqlBuilder.append(operator.spaceOperator);

            if (rightInnerParens) {
                sqlBuilder.append(_Constant.SPACE_LEFT_PAREN);
            }
            sqlBuilder.append(right);

            if (rightInnerParens) {
                sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);
            }

            return sqlBuilder.toString();
        }


    }//DualPredicate


    private static final class LikePredicate extends OperationPredicate {

        private final ArmyExpression left;

        private final DualOperator operator;

        private final ArmyExpression right;

        private final ArmyExpression escapeChar;

        /**
         * @see #likePredicate(Expression, DualOperator, Expression, SqlSyntax.WordEscape, Expression)
         */
        private LikePredicate(OperationExpression left, DualOperator operator, Expression right,
                              @Nullable Expression escapeChar) {
            this.left = left;
            this.operator = operator;
            this.right = (ArmyExpression) right;
            this.escapeChar = (ArmyExpression) escapeChar;
        }

        @Override
        public void appendSql(final _SqlContext context) {
            final StringBuilder sqlBuilder;
            sqlBuilder = context.sqlBuilder();

            // 1. append left
            this.left.appendSql(context);

            // 2. append operator
            switch (this.operator) {
                case LIKE:
                case NOT_LIKE:
                    break;
                case SIMILAR_TO:
                case NOT_SIMILAR_TO: {
                    final Database database;
                    database = context.parser().dialect().database();
                    if (database != Database.PostgreSQL) {
                        String m = String.format("%s don't support %s.", database, this.operator);
                        throw new CriteriaException(m);
                    }
                }
                break;
                default:
                    //no bug,never here
                    throw _Exceptions.unexpectedEnum(this.operator);
            }

            sqlBuilder.append(this.operator.spaceOperator);

            // 3. append right
            this.right.appendSql(context);

            // 4. append ESCAPES clause
            if (this.escapeChar != null) {
                sqlBuilder.append(SQLs.ESCAPE.spaceRender());
                this.escapeChar.appendSql(context);
            }
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.left, this.operator, this.right, this.escapeChar);
        }

        @Override
        public boolean equals(final Object obj) {
            final boolean match;
            if (obj == this) {
                match = true;
            } else if (obj instanceof LikePredicate) {
                final LikePredicate o = (LikePredicate) obj;
                match = o.left.equals(this.left)
                        && o.operator == this.operator
                        && o.right.equals(this.right)
                        && Objects.equals(o.escapeChar, this.escapeChar);
            } else {
                match = false;
            }
            return match;
        }

        @Override
        public String toString() {
            final StringBuilder builder = new StringBuilder();
            builder.append(this.left)
                    .append(this.operator.spaceOperator)
                    .append(this.right);

            if (this.escapeChar != null) {
                builder.append(SQLs.ESCAPE.spaceRender())
                        .append(this.escapeChar);
            }
            return builder.toString();
        }


    }//LikePredicate


    private static final class BracketPredicate extends OperationPredicate {

        private final OperationPredicate predicate;

        private BracketPredicate(OperationPredicate predicate) {
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


    private static final class OrPredicate extends OperationPredicate {

        private final OperationPredicate left;

        private final List<OperationPredicate> rightList;

        private OrPredicate(OperationPredicate left, List<OperationPredicate> rightList) {
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
                final OrPredicate o = (OrPredicate) obj;
                match = o.left.equals(this.left)
                        && o.rightList.equals(this.rightList);
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

            final OperationPredicate left = this.left;

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
            for (OperationPredicate right : this.rightList) {

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


    static final class AndPredicate extends OperationPredicate {

        final OperationPredicate left;

        final OperationPredicate right;

        private AndPredicate(OperationPredicate left, OperationPredicate right) {
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


    private static class BetweenPredicate extends OperationPredicate {

        private final boolean not;

        private final SQLsSyntax.BetweenModifier modifier;

        private final ArmyExpression left;

        private final ArmyExpression center;

        private final ArmyExpression right;

        /**
         * @see #betweenPredicate(boolean, SQLsSyntax.BetweenModifier, OperationExpression, Expression, Expression)
         */
        private BetweenPredicate(boolean not, @Nullable SQLsSyntax.BetweenModifier modifier, OperationExpression left,
                                 Expression center, Expression right) {
            this.not = not;
            this.modifier = modifier;
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
            if (this.modifier != null) {
                //TODO validate database support
                builder.append(this.modifier.spaceRender());
            }
            this.center.appendSql(context);
            builder.append(_Constant.SPACE_AND);
            this.right.appendSql(context);
        }


        @Override
        public int hashCode() {
            return Objects.hash(this.not, this.modifier, this.left, this.center, this.right);
        }

        @Override
        public boolean equals(final Object obj) {
            final boolean match;
            if (obj == this) {
                match = true;
            } else if (obj instanceof BetweenPredicate) {
                final BetweenPredicate o = (BetweenPredicate) obj;
                match = o.not == this.not
                        && o.modifier == this.modifier
                        && o.left.equals(this.left)
                        && o.center.equals(this.center)
                        && o.right.equals(this.right);
            } else {
                match = false;
            }
            return match;
        }

        @Override
        public String toString() {
            final StringBuilder builder = new StringBuilder();
            builder.append(this.left);
            if (this.not) {
                builder.append(" NOT");
            }
            builder.append(" BETWEEN");
            if (this.modifier != null) {
                builder.append(this.modifier.spaceRender());
            }
            return builder.append(this.center)
                    .append(_Constant.SPACE_AND)
                    .append(this.right)
                    .toString();
        }


    }//BetweenPredicate


    private static class CastExpression extends OperationExpression.CompoundExpression {

        private final ArmyExpression expression;

        private final TypeMeta castType;


        private CastExpression(OperationExpression expression, TypeMeta castType) {
            this.expression = expression;
            this.castType = castType;
        }

        @Override
        public TypeMeta typeMeta() {
            return this.castType;
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


    private static final class NotPredicate extends OperationPredicate {

        private final OperationPredicate predicate;

        private NotPredicate(OperationPredicate predicate) {
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
            return this.predicate.hashCode();
        }

        @Override
        public boolean equals(final Object obj) {
            final boolean match;
            if (obj == this) {
                match = true;
            } else if (obj instanceof NotPredicate) {
                match = ((NotPredicate) obj).predicate.equals(this.predicate);
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


    private static final class SubQueryPredicate extends OperationPredicate {
        private final ArmyExpression left;

        private final DualOperator operator;

        private final QueryOperator queryOperator;

        private final SubQuery subQuery;

        private SubQueryPredicate(OperationExpression left, DualOperator operator,
                                  @Nullable QueryOperator queryOperator, SubQuery subQuery) {
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


    private static final class BooleanTestPredicate extends OperationPredicate {

        private final OperationExpression expression;

        private final boolean not;


        private final SQLsSyntax.BooleanTestWord operand;

        /**
         * @see #booleanTestPredicate(OperationExpression, boolean, SQLsSyntax.BooleanTestWord)
         */
        private BooleanTestPredicate(OperationExpression expression, boolean not, SQLsSyntax.BooleanTestWord operand) {
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
            sqlBuilder.append(this.operand.spaceRender());
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
                final BooleanTestPredicate o = (BooleanTestPredicate) obj;
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

            return sqlBuilder.append(this.operand.spaceRender())
                    .toString();
        }


    }//BooleanTestPredicate

    private static final class IsComparisonPredicate extends OperationPredicate {

        private final ArmyExpression left;

        private final boolean not;

        private final SQLsSyntax.IsComparisonWord operator;

        private final ArmyExpression right;


        /**
         * @see #isComparisonPredicate(OperationExpression, boolean, SQLsSyntax.IsComparisonWord, Expression)
         */
        private IsComparisonPredicate(OperationExpression left, boolean not, SQLsSyntax.IsComparisonWord operator,
                                      ArmyExpression right) {
            this.left = left;
            this.not = not;
            this.operator = operator;
            this.right = right;
        }

        @Override
        public void appendSql(final _SqlContext context) {
            //TODO validate database
            this.left.appendSql(context);
            final StringBuilder sqlBuilder;
            sqlBuilder = context.sqlBuilder()
                    .append(" IS");

            if (this.not) {
                sqlBuilder.append(" NOT");
            }
            sqlBuilder.append(this.operator.spaceRender());
            this.right.appendSql(context);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.left, this.not, this.operator, this.right);
        }

        @Override
        public boolean equals(final Object obj) {
            final boolean match;
            if (obj == this) {
                match = true;
            } else if (obj instanceof IsComparisonPredicate) {
                final IsComparisonPredicate o = (IsComparisonPredicate) obj;
                match = o.left.equals(this.left)
                        && o.not == this.not
                        && o.operator == this.operator
                        && o.right.equals(this.right);
            } else {
                match = false;
            }
            return match;
        }

        @Override
        public String toString() {
            final StringBuilder builder = new StringBuilder();
            builder.append(this.left)
                    .append(" IS");
            if (this.not) {
                builder.append(" NOT");
            }
            return builder.append(this.operator.spaceRender())
                    .append(this.right)
                    .toString();
        }


    }//IsComparisonPredicates


}
