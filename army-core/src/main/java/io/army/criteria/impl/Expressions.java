package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.dialect.SubQuery;
import io.army.criteria.impl.inner._DerivedTable;
import io.army.dialect.Database;
import io.army.dialect._Constant;
import io.army.dialect._SqlContext;
import io.army.lang.Nullable;
import io.army.mapping.*;
import io.army.meta.TypeMeta;
import io.army.util._Exceptions;
import io.army.util._StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;

abstract class Expressions {

    Expressions() {
        throw new UnsupportedOperationException();
    }

    static OperationExpression dualExp(final Expression left, final DualExpOperator operator, final Expression right) {
        final BinaryOperator<MappingType> inferFunc;
        switch (operator) {
            case PLUS:
                inferFunc = Expressions::plusType;
                break;
            case MINUS:
            case TIMES:
            case DIVIDE:
            case MOD:
                inferFunc = Expressions::mathExpType;
                break;
            case BITWISE_AND:
            case BITWISE_OR:
            case BITWISE_XOR:
            case RIGHT_SHIFT:
            case LEFT_SHIFT:
                inferFunc = Expressions::bitwiseType;
                break;
            default:
                throw _Exceptions.unexpectedEnum(operator);
        }
        return dialectDualExp(left, operator, right, inferFunc);
    }

    static OperationExpression dialectDualExp(final Expression left, final DualExpOperator operator,
                                              final Expression right, final BinaryOperator<MappingType> inferFunc) {
        if (!(left instanceof OperationExpression)) {
            throw NonOperationExpression.nonOperationExpression(left);
        } else if (!(right instanceof OperationExpression)) {
            throw NonOperationExpression.nonOperationExpression(right);
        }
        final DualExpression result;
        if ((left instanceof TypeInfer.DelayTypeInfer && ((TypeInfer.DelayTypeInfer) left).isDelay())
                || (right instanceof TypeInfer.DelayTypeInfer && ((TypeInfer.DelayTypeInfer) right).isDelay())) {
            result = new DelayDualExpression(left, operator, right, inferFunc);
        } else {
            result = new DualExpression(left, operator, right, inferFunc);
        }
        return result;
    }


    static SimpleExpression unaryExp(final UnaryExpOperator operator, final Expression operand) {
        if (!(operand instanceof OperationExpression)) {
            throw NonOperationExpression.nonOperationExpression(operand);
        }
        final UnaryExpression result;
        if (operand instanceof TypeInfer.DelayTypeInfer && ((TypeInfer.DelayTypeInfer) operand).isDelay()) {
            result = new StandardDelayUnaryExpression(operator, operand, Expressions::identityType);
        } else {
            result = new StandardUnaryExpression(operator, operand, Expressions::identityType);
        }
        return result;
    }


    static OperationExpression castExp(OperationExpression expression, TypeMeta typeMeta) {
        return new CastExpression(expression, typeMeta);
    }

    static Expression scalarExpression(final SubQuery subQuery) {
        final List<? extends Selection> selectionList;
        selectionList = ((_DerivedTable) subQuery).refAllSelection();
        if (selectionList.size() != 1) {
            throw ContextStack.clearStackAnd(_Exceptions::nonScalarSubQuery, subQuery);
        }
        return new ScalarExpression(selectionList.get(0).typeMeta(), subQuery);
    }


    static OperationPredicate existsPredicate(final UnaryBooleanOperator operator, final @Nullable SubQuery operand) {
        if (operand == null) {
            throw ContextStack.clearStackAndNullPointer();
        }
        switch (operator) {
            case NOT_EXISTS:
            case EXISTS:
                break;
            default:
                throw _Exceptions.unexpectedEnum(operator);
        }
        return new ExistsPredicate(operator, operand);
    }


    static OperationPredicate booleanTestPredicate(final OperationExpression expression,
                                                   boolean not, SQLsSyntax.BooleanTestWord operand) {
        if (!(operand == SQLs.NULL
                || operand == SQLs.TRUE
                || operand == SQLs.FALSE
                || operand == SQLs.UNKNOWN
                || operand instanceof SqlWords.BooleanTestKeyWord)) {
            String m = String.format("unknown operand[%s]", operand);
            throw ContextStack.criteriaError(ContextStack.peek(), m);
        }
        return new BooleanTestPredicate(expression, not, operand);
    }

    static OperationPredicate isComparisonPredicate(final OperationExpression left, boolean not,
                                                    SQLsSyntax.IsComparisonWord operator, Expression right) {
        if (!(right instanceof OperationExpression)) {
            throw NonOperationExpression.nonOperationExpression(right);
        }
        //TODO validate operator
        return new IsComparisonPredicate(left, not, operator, (ArmyExpression) right);
    }


    static OperationPredicate dualPredicate(final Expression left, final DualBooleanOperator operator, final Expression right) {
        if (!(left instanceof OperationExpression)) {
            throw NonOperationExpression.nonOperationExpression(left);
        }
        switch (operator) {
            case IN:
            case NOT_IN: {
                if (!(right instanceof OperationExpression
                        || right instanceof NonOperationExpression.MultiValueExpression)) {
                    throw NonOperationExpression.nonOperationExpression(right);
                }
            }
            break;
            default: {
                if (!(right instanceof OperationExpression)) {
                    throw NonOperationExpression.nonOperationExpression(right);
                }
            }

        }
        return new StandardDualPredicate(left, operator, right);
    }

    static OperationPredicate likePredicate(final Expression left, final DualBooleanOperator operator,
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
            throw NonOperationExpression.nonOperationExpression(left);
        } else if (escape != SQLs.ESCAPE) {
            throw CriteriaUtils.errorModifier(escape);
        } else if (!(right instanceof OperationExpression)) {
            throw NonOperationExpression.nonOperationExpression(right);
        }
        return new LikePredicate((OperationExpression) left, operator, right, escapeChar);
    }


    static OperationPredicate betweenPredicate(OperationExpression left, boolean not,
                                               @Nullable SQLsSyntax.BetweenModifier modifier,
                                               Expression center, Expression right) {
        if (!(center instanceof OperationExpression)) {
            throw NonOperationExpression.nonOperationExpression(center);
        } else if (!(right instanceof OperationExpression)) {
            throw NonOperationExpression.nonOperationExpression(right);
        }
        //TODO validate modifier
        return new BetweenPredicate(left, not, modifier, center, right);
    }

    static OperationPredicate compareQueryPredicate(OperationExpression left
            , DualBooleanOperator operator, QueryOperator queryOperator, SubQuery subQuery) {
        switch (operator) {
            case LESS:
            case LESS_EQUAL:
            case EQUAL:
            case NOT_EQUAL:
            case GREATER:
            case GREATER_EQUAL: {
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

    static OperationPredicate inOperator(final OperationExpression left, final DualBooleanOperator operator,
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
     * @see #compareQueryPredicate(OperationExpression, DualBooleanOperator, QueryOperator, SubQuery)
     * @see #inOperator(OperationExpression, DualBooleanOperator, SubQuery)
     */
    private static void assertColumnSubQuery(final DualBooleanOperator operator
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


    static MappingType identityType(MappingType type) {
        return type;
    }


    static MappingType doubleType(MappingType type) {
        return DoubleType.INSTANCE;
    }


    static MappingType plusType(final MappingType left, final MappingType right) {
        final MappingType returnType;
        if (!(left instanceof MappingType.SqlNumberOrStringType) && right instanceof MappingType.SqlNumberType) {
            returnType = left;
        } else if (!(right instanceof MappingType.SqlNumberOrStringType) && left instanceof MappingType.SqlNumberType) {
            returnType = right;
        } else if (!(left instanceof MappingType.SqlNumberOrStringType && right instanceof MappingType.SqlNumberOrStringType)) {
            if (left.getClass() == right.getClass()) {
                returnType = left;
            } else {
                returnType = StringType.INSTANCE;
            }
        } else if (left instanceof MappingType.SqlFloatType || right instanceof MappingType.SqlFloatType) {
            returnType = DoubleType.INSTANCE;
        } else if (left instanceof MappingType.SqlDecimalType || right instanceof MappingType.SqlDecimalType) {
            returnType = BigDecimalType.INSTANCE;
        } else if (!(left instanceof MappingType.SqlIntegerType && right instanceof MappingType.SqlIntegerType)) {
            returnType = left;
        } else if (((MappingType.SqlIntegerType) left).lengthType()
                .compareWith(((MappingType.SqlIntegerType) right).lengthType()) >= 0) {
            returnType = left;
        } else {
            returnType = right;
        }
        return returnType;
    }

    static MappingType mathExpType(final MappingType left, final MappingType right) {
        final MappingType returnType;
        if (left.getClass() == right.getClass()) {
            returnType = left;
        } else if (!(left instanceof MappingType.SqlNumberOrStringType && right instanceof MappingType.SqlNumberOrStringType)) {
            returnType = StringType.INSTANCE;
        } else if (left instanceof MappingType.SqlFloatType || right instanceof MappingType.SqlFloatType) {
            returnType = DoubleType.INSTANCE;
        } else if (left instanceof MappingType.SqlDecimalType || right instanceof MappingType.SqlDecimalType) {
            returnType = BigDecimalType.INSTANCE;
        } else if (!(left instanceof MappingType.SqlIntegerType && right instanceof MappingType.SqlIntegerType)) {
            returnType = left;
        } else if (((MappingType.SqlIntegerType) left).lengthType()
                .compareWith(((MappingType.SqlIntegerType) right).lengthType()) >= 0) {
            returnType = left;
        } else {
            returnType = right;
        }
        return returnType;
    }

    static MappingType bitwiseType(final MappingType left, final MappingType right) {
        final MappingType returnType;
        if (left.getClass() == right.getClass()) {
            returnType = left;
        } else if (left instanceof MappingType.SqlBitType || right instanceof MappingType.SqlBitType) {
            returnType = left instanceof MappingType.SqlBitType ? left : right;
        } else if (!(left instanceof MappingType.SqlIntegerType || right instanceof MappingType.SqlIntegerType)) {
            returnType = StringType.INSTANCE;
        } else if (!(left instanceof MappingType.SqlIntegerType && right instanceof MappingType.SqlIntegerType)) {
            returnType = LongType.INSTANCE;
        } else if (((MappingType.SqlIntegerType) left).lengthType()
                .compareWith(((MappingType.SqlIntegerType) right).lengthType()) >= 0) {
            returnType = left;
        } else {
            returnType = right;
        }
        return returnType;
    }

    private static CriteriaException unsupportedOperator(Operator operator, Database database) {
        String m = String.format("%s isn't supported by %s", operator, database);
        return new CriteriaException(m);
    }


    /**
     * This class is a implementation of {@link Expression}.
     * The expression consist of a left {@link Expression} ,a {@link DualBooleanOperator} and right {@link Expression}.
     *
     * @since 1.0
     */
    private static class DualExpression extends OperationExpression.CompoundExpression {

        final ArmyExpression left;

        final DualExpOperator operator;

        final ArmyExpression right;

        final BinaryOperator<MappingType> inferFunc;


        private MappingType type;

        /**
         * @see #dualExp(Expression, DualExpOperator, Expression)
         * @see #dialectDualExp(Expression, DualExpOperator, Expression, BinaryOperator)
         */
        private DualExpression(final Expression left, final DualExpOperator operator, final Expression right,
                               final BinaryOperator<MappingType> inferFunc) {
            this.left = (ArmyExpression) left;
            this.operator = operator;
            this.right = (ArmyExpression) right;
            this.inferFunc = inferFunc;
        }

        @Override
        public final MappingType typeMeta() {
            MappingType type = this.type;
            if (type == null) {
                if (this.left instanceof DualExpression) {
                    type = ((DualExpression) this.left).inferToLeft(this.operator, this.right.typeMeta().mappingType(),
                            this.inferFunc);
                } else {
                    type = this.inferFunc.apply(this.left.typeMeta().mappingType(),
                            this.right.typeMeta().mappingType());
                }
                this.type = type;
            }
            return type;
        }


        @Override
        public final void appendSql(final _SqlContext context) {

            final DualExpOperator operator = this.operator;
            switch (operator) {
                case EXPONENTIATION:
                case DOUBLE_VERTICAL:
                case AT_TIME_ZONE:
                case DOUBLE_AMP:
                case POUND: {
                    if (context.database() != Database.PostgreSQL) {
                        throw unsupportedOperator(operator, context.database());
                    }
                }
                break;
                default:
                    // no-op
            }

            final StringBuilder sqlBuilder;
            sqlBuilder = context.sqlBuilder();
            final ArmyExpression left = this.left, right = this.right;
            final boolean leftOuterParens, rightOuterParens;
            leftOuterParens = left instanceof OperationPredicate.CompoundPredicate; // if CompoundPredicate must append outer parens

            //1. append left expression
            if (leftOuterParens) {
                sqlBuilder.append(_Constant.SPACE_LEFT_PAREN);
            }

            left.appendSql(context);

            if (leftOuterParens) {
                sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);
            }

            //2. append operator
            if (operator == DualExpOperator.BITWISE_XOR && context.database() == Database.PostgreSQL) {
                sqlBuilder.append(" #");
            } else {
                sqlBuilder.append(operator.spaceOperator);
            }

            rightOuterParens = !(right instanceof ArmySimpleExpression);
            if (rightOuterParens) {
                sqlBuilder.append(_Constant.SPACE_LEFT_PAREN);
            }
            //3. append right expression
            right.appendSql(context);

            if (rightOuterParens) {
                sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);
            }

        }

        @Override
        public final String toString() {
            final StringBuilder sqlBuilder = new StringBuilder();
            final ArmyExpression left = this.left, right = this.right;
            final boolean leftOuterParens, rightOuterParens;
            leftOuterParens = left instanceof OperationPredicate.CompoundPredicate; // if CompoundPredicate must append outer parens

            //1. append left expression
            if (leftOuterParens) {
                sqlBuilder.append(_Constant.SPACE_LEFT_PAREN);
            }

            sqlBuilder.append(left);

            if (leftOuterParens) {
                sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);
            }

            //2. append operator
            sqlBuilder.append(this.operator.spaceOperator);

            rightOuterParens = !(right instanceof ArmySimpleExpression);
            if (rightOuterParens) {
                sqlBuilder.append(_Constant.SPACE_LEFT_PAREN);
            }
            //3. append right expression
            sqlBuilder.append(right);

            if (rightOuterParens) {
                sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);
            }

            return sqlBuilder.toString();
        }


        /**
         * @see #typeMeta()
         */
        private MappingType inferToLeft(final DualExpOperator operator, final MappingType right,
                                        final BinaryOperator<MappingType> rightFunc) {
            final MappingType interim, resultType;
            if (this.operator.precedenceValue - operator.precedenceValue < 0) {
                interim = rightFunc.apply(this.right.typeMeta().mappingType(), right);
                if (this.left instanceof DualExpression) {
                    resultType = ((DualExpression) this.left).inferToLeft(this.operator, interim, this.inferFunc);
                } else {
                    resultType = this.inferFunc.apply(this.left.typeMeta().mappingType(), interim);
                }
            } else {
                if (this.left instanceof DualExpression) {
                    interim = ((DualExpression) this.left).inferToLeft(this.operator,
                            this.right.typeMeta().mappingType(), this.inferFunc);
                } else {
                    interim = this.inferFunc.apply(this.left.typeMeta().mappingType(),
                            this.right.typeMeta().mappingType());
                }
                resultType = rightFunc.apply(interim, right);
            }
            return resultType;
        }


    }//DualExpression


    private static final class DelayDualExpression extends DualExpression implements TypeInfer.DelayTypeInfer {

        /**
         * @see #dualExp(Expression, DualExpOperator, Expression)
         * @see #dialectDualExp(Expression, DualExpOperator, Expression, BinaryOperator)
         */
        private DelayDualExpression(Expression left, DualExpOperator operator, Expression right,
                                    BinaryOperator<MappingType> inferFunc) {
            super(left, operator, right, inferFunc);
        }

        @Override
        public boolean isDelay() {
            final ArmyExpression left = this.left, right = this.right;
            return (left instanceof TypeInfer.DelayTypeInfer && ((DelayTypeInfer) left).isDelay())
                    || (right instanceof TypeInfer.DelayTypeInfer && ((DelayTypeInfer) right).isDelay());
        }


    }//DelayDualExpression

    /**
     * <p>
     * This class representing unary expression,unary expression always out outer bracket.
     * </p>
     * This class is a implementation of {@link Expression}.
     * The expression consist of a  {@link Expression} and a {@link UnaryExpOperator}.
     */
    static class UnaryExpression extends OperationExpression.OperationSimpleExpression {

        private final Operator.SqlUnaryExpOperator operator;

        final ArmyExpression operand;

        private final UnaryOperator<MappingType> inferFunc;

        /**
         * @see #unaryExp(UnaryExpOperator, Expression)
         */
        UnaryExpression(Operator.SqlUnaryExpOperator operator, Expression operand,
                        UnaryOperator<MappingType> inferFunc) {
            this.operator = operator;
            this.operand = (ArmyExpression) operand;
            this.inferFunc = inferFunc;
        }

        /**
         * @return expression couldn't return {@link TableField},avoid to codec filed.
         */
        @Override
        public final MappingType typeMeta() {
            MappingType type = null;
            if (this instanceof DelayUnaryExpression) {
                type = ((DelayUnaryExpression) this).type;
            }

            if (type != null) {
                return type;
            }

            final TypeMeta typeMeta;
            if ((typeMeta = this.operand.typeMeta()) instanceof MappingType) {
                type = (MappingType) typeMeta;
            } else {
                type = typeMeta.mappingType();
            }

            type = this.inferFunc.apply(type); // infer
            if (this instanceof DelayUnaryExpression) {
                ((DelayUnaryExpression) this).type = type;
            }
            return type;
        }


        @Override
        public final void appendSql(final _SqlContext context) {

            final Operator.SqlUnaryExpOperator operator = this.operator;
            final boolean outerParens;
            outerParens = operator != UnaryExpOperator.NEGATE;
            if (!(operator instanceof UnaryExpOperator) && operator.database() != context.database()) {
                throw unsupportedOperator(operator, context.database());
            }

            final StringBuilder builder;
            builder = context.sqlBuilder();

            if (outerParens) {
                builder.append(_Constant.SPACE_LEFT_PAREN);
            }
            builder.append(operator.spaceRender());

            final ArmyExpression operand = this.operand;
            final boolean operandOuterParens = !(operand instanceof ArmySimpleExpression);

            if (operandOuterParens) {
                builder.append(_Constant.SPACE_LEFT_PAREN);
            }
            // append expression
            operand.appendSql(context);

            if (operandOuterParens) {
                builder.append(_Constant.SPACE_RIGHT_PAREN);
            }

            if (outerParens) {
                builder.append(_Constant.SPACE_RIGHT_PAREN);
            }

        }

        @Override
        public final int hashCode() {
            return Objects.hash(this.operator, this.operand);
        }

        @Override
        public final boolean equals(final Object obj) {
            final boolean match;
            if (obj == this) {
                match = true;
            } else if (obj instanceof UnaryExpression) {
                final UnaryExpression o = (UnaryExpression) obj;
                match = o.operator == this.operator
                        && o.operand.equals(this.operand);
            } else {
                match = false;
            }
            return match;
        }

        @Override
        public final String toString() {
            final Operator.SqlUnaryExpOperator operator = this.operator;
            final boolean outerParens;
            outerParens = operator != UnaryExpOperator.NEGATE;

            final StringBuilder builder = new StringBuilder();

            if (outerParens) {
                builder.append(_Constant.SPACE_LEFT_PAREN);
            }
            builder.append(operator.spaceRender());

            final ArmyExpression operand = this.operand;
            final boolean operandOuterParens = !(operand instanceof ArmySimpleExpression);

            if (operandOuterParens) {
                builder.append(_Constant.SPACE_LEFT_PAREN);
            }
            // append expression
            builder.append(operand);

            if (operandOuterParens) {
                builder.append(_Constant.SPACE_RIGHT_PAREN);
            }

            if (outerParens) {
                builder.append(_Constant.SPACE_RIGHT_PAREN);
            }
            return builder.toString();
        }


    }//UnaryExpression

    static abstract class DelayUnaryExpression extends UnaryExpression implements TypeInfer.DelayTypeInfer {

        private MappingType type;


        DelayUnaryExpression(Operator.SqlUnaryExpOperator operator, Expression operand,
                             UnaryOperator<MappingType> inferFunc) {
            super(operator, operand, inferFunc);
        }

        @Override
        public final boolean isDelay() {
            return this.type == null && ((DelayTypeInfer) this.operand).isDelay();
        }


    }//DelayUnaryExpression


    private static final class StandardUnaryExpression extends UnaryExpression {

        /**
         * @see #unaryExp(UnaryExpOperator, Expression)
         */
        private StandardUnaryExpression(UnaryExpOperator operator, Expression operand,
                                        UnaryOperator<MappingType> inferFunc) {
            super(operator, operand, inferFunc);
        }

    }//StandardUnaryExpression

    private static final class StandardDelayUnaryExpression extends DelayUnaryExpression {

        /**
         * @see #unaryExp(UnaryExpOperator, Expression)
         */
        private StandardDelayUnaryExpression(UnaryExpOperator operator, Expression operand,
                                             UnaryOperator<MappingType> inferFunc) {
            super(operator, operand, inferFunc);
        }

    }//StandardDelayUnaryExpression


    private static final class ScalarExpression extends OperationExpression.OperationSimpleExpression {

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

    private static final class ExistsPredicate extends OperationPredicate.CompoundPredicate {

        private final UnaryBooleanOperator operator;

        private final SubQuery subQuery;

        /**
         * @see #existsPredicate(UnaryBooleanOperator, SubQuery)
         */
        private ExistsPredicate(UnaryBooleanOperator operator, SubQuery subQuery) {
            this.operator = operator;
            this.subQuery = subQuery;
        }

        @Override
        public void appendSql(final _SqlContext context) {
            switch (this.operator) {
                case EXISTS:
                case NOT_EXISTS:
                    break;
                default:
                    throw _Exceptions.unexpectedEnum(this.operator);
            }

            context.sqlBuilder().append(this.operator.spaceOperator);
            context.parser().subQuery(this.subQuery, context);
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
                case NOT_EXISTS:
                    builder.append(this.operator.spaceOperator)
                            .append(this.subQuery);
                    break;
                default:
                    throw _Exceptions.unexpectedEnum(this.operator);
            }
            return builder.toString();
        }


    }//UnaryPredicate


    static abstract class DualPredicate extends OperationPredicate.CompoundPredicate {


        final ArmyExpression left;

        final Operator.SqlDualBooleanOperator operator;

        final ArmyExpression right;


        DualPredicate(Expression left, Operator.SqlDualBooleanOperator operator, Expression right) {
            this.left = (ArmyExpression) left;
            this.operator = operator;
            this.right = (ArmyExpression) right;
        }

        @Override
        public final void appendSql(final _SqlContext context) {
            final Operator.SqlDualBooleanOperator operator = this.operator;
            if (!(operator instanceof DualBooleanOperator) && context.database() != operator.database()) {
                throw unsupportedOperator(operator, context.database());
            }

            final StringBuilder sqlBuilder;
            sqlBuilder = context.sqlBuilder();
            final ArmyExpression left = this.left, right = this.right;
            final boolean leftOuterParens, rightOuterParens;
            leftOuterParens = left instanceof OperationPredicate.CompoundPredicate;

            if (leftOuterParens) {
                sqlBuilder.append(_Constant.SPACE_LEFT_PAREN);
            }
            left.appendSql(context);
            if (leftOuterParens) {
                sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);
            }

            sqlBuilder.append(this.operator.spaceRender());

            rightOuterParens = !(right instanceof ArmySimpleExpression);
            if (rightOuterParens) {
                sqlBuilder.append(_Constant.SPACE_LEFT_PAREN);
            }
            right.appendSql(context);

            if (rightOuterParens) {
                sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);
            }

        }

        @Override
        public final int hashCode() {
            return Objects.hash(this.left, this.operator, this.right);
        }

        @Override
        public final boolean equals(final Object obj) {
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
        public final String toString() {

            final StringBuilder sqlBuilder;
            sqlBuilder = new StringBuilder();

            final ArmyExpression left = this.left, right = this.right;
            final boolean leftOuterParens, rightOuterParens;
            leftOuterParens = left instanceof OperationPredicate.CompoundPredicate;

            if (leftOuterParens) {
                sqlBuilder.append(_Constant.SPACE_LEFT_PAREN);
            }
            sqlBuilder.append(left);
            if (leftOuterParens) {
                sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);
            }

            rightOuterParens = !(right instanceof ArmySimpleExpression);

            if (rightOuterParens) {
                sqlBuilder.append(_Constant.SPACE_LEFT_PAREN);
            }
            sqlBuilder.append(right);

            if (rightOuterParens) {
                sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);
            }

            return sqlBuilder.toString();
        }


    }//DualPredicate

    private static final class StandardDualPredicate extends DualPredicate {

        /**
         * @see #dualPredicate(Expression, DualBooleanOperator, Expression)
         */
        private StandardDualPredicate(Expression left, DualBooleanOperator operator, Expression right) {
            super(left, operator, right);
        }

    }//StandardDualPredicate


    private static final class LikePredicate extends OperationPredicate.CompoundPredicate {

        private final ArmyExpression left;

        private final DualBooleanOperator operator;

        private final ArmyExpression right;

        private final ArmyExpression escapeChar;

        /**
         * @see #likePredicate(Expression, DualBooleanOperator, Expression, SqlSyntax.WordEscape, Expression)
         */
        private LikePredicate(OperationExpression left, DualBooleanOperator operator, Expression right,
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

            final ArmyExpression left = this.left, right = this.right, escapeChar = this.escapeChar;
            final boolean leftOuterParens, rightOuterParens, escapeCharOuterParens;
            leftOuterParens = left instanceof IPredicate;// if predicate must append outer parens
            // 1. append left
            if (leftOuterParens) {
                sqlBuilder.append(_Constant.SPACE_LEFT_PAREN);
            }
            left.appendSql(context);
            if (leftOuterParens) {
                sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);
            }

            // 2. append operator
            switch (this.operator) {
                case LIKE:
                case NOT_LIKE:
                    break;
                case SIMILAR_TO:
                case NOT_SIMILAR_TO: {
                    if (context.database() != Database.PostgreSQL) {
                        throw unsupportedOperator(this.operator, context.database());
                    }
                }
                break;
                default:
                    //no bug,never here
                    throw _Exceptions.unexpectedEnum(this.operator);
            }

            sqlBuilder.append(this.operator.spaceOperator);

            rightOuterParens = !(right instanceof ArmySimpleExpression);
            // 3. append right
            if (rightOuterParens) {
                sqlBuilder.append(_Constant.SPACE_LEFT_PAREN);
            }
            right.appendSql(context);
            if (rightOuterParens) {
                sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);
            }
            // 4. append ESCAPES clause
            if (escapeChar != null) {
                sqlBuilder.append(SQLs.ESCAPE.spaceRender());
                escapeCharOuterParens = !(escapeChar instanceof ArmySimpleExpression);
                if (escapeCharOuterParens) {
                    sqlBuilder.append(_Constant.SPACE_LEFT_PAREN);
                }
                escapeChar.appendSql(context);
                if (escapeCharOuterParens) {
                    sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);
                }
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


    private static class BetweenPredicate extends OperationPredicate.CompoundPredicate {

        private final boolean not;

        private final SQLsSyntax.BetweenModifier modifier;

        private final ArmyExpression left;

        private final ArmyExpression center;

        private final ArmyExpression right;

        /**
         * @see #betweenPredicate(OperationExpression, boolean, SQLsSyntax.BetweenModifier, Expression, Expression)
         */
        private BetweenPredicate(Expression left, boolean not, @Nullable SQLsSyntax.BetweenModifier modifier,
                                 Expression center, Expression right) {
            this.not = not;
            this.modifier = modifier;
            this.left = (ArmyExpression) left;
            this.center = (ArmyExpression) center;
            this.right = (ArmyExpression) right;
        }

        @Override
        public void appendSql(final _SqlContext context) {

            final StringBuilder sqlBuilder;
            sqlBuilder = context.sqlBuilder();

            final ArmyExpression left = this.left, center = this.center, right = this.right;
            final boolean leftOuterParens, centerOuterParens, rightOuterParens;
            leftOuterParens = left instanceof OperationPredicate.CompoundPredicate; // if predicate must append outer parens
            // 1. append left
            if (leftOuterParens) {
                sqlBuilder.append(_Constant.SPACE_LEFT_PAREN);
            }
            left.appendSql(context);
            if (leftOuterParens) {
                sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);
            }

            // 2. append NOT operator(or not)
            if (this.not) {
                sqlBuilder.append(" NOT");
            }
            // 3. append BETWEEN operator
            sqlBuilder.append(" BETWEEN");

            // 4. append modifier (or not)
            if (this.modifier != null) {
                //TODO validate database support
                sqlBuilder.append(this.modifier.spaceRender());
            }

            centerOuterParens = !(center instanceof ArmySimpleExpression);
            rightOuterParens = !(right instanceof ArmySimpleExpression);

            // 5. append center operand
            if (centerOuterParens) {
                sqlBuilder.append(_Constant.SPACE_LEFT_PAREN);
            }
            center.appendSql(context);
            if (centerOuterParens) {
                sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);
            }
            // 6. append AND operator
            sqlBuilder.append(_Constant.SPACE_AND);

            // 7. append right operand
            if (rightOuterParens) {
                sqlBuilder.append(_Constant.SPACE_LEFT_PAREN);
            }
            right.appendSql(context);
            if (rightOuterParens) {
                sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);
            }

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
            // 1. append left operand
            builder.append(this.left);
            // 2. append NOT operator(or not)
            if (this.not) {
                builder.append(" NOT");
            }
            // 3. append BETWEEN operator
            builder.append(" BETWEEN");

            // 4. append modifier (or not)
            if (this.modifier != null) {
                //TODO validate database support
                builder.append(this.modifier.spaceRender());
            }
            final ArmyExpression center = this.center, right = this.right;

            final boolean centerOuterParens, rightOuterParens;
            centerOuterParens = !(center instanceof ArmySimpleExpression);
            rightOuterParens = !(right instanceof ArmySimpleExpression);

            // 5. append center operand
            if (centerOuterParens) {
                builder.append(_Constant.SPACE_LEFT_PAREN);
            }
            builder.append(center);
            if (centerOuterParens) {
                builder.append(_Constant.SPACE_RIGHT_PAREN);
            }
            // 6. append AND operator
            builder.append(_Constant.SPACE_AND);

            // 7. append right operand
            if (rightOuterParens) {
                builder.append(_Constant.SPACE_LEFT_PAREN);
            }
            builder.append(right);
            if (rightOuterParens) {
                builder.append(_Constant.SPACE_RIGHT_PAREN);
            }
            return builder.toString();
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
        public MappingType typeMeta() {
            return this.castType.mappingType();
        }

        @Override
        public final void appendSql(final _SqlContext context) {
            this.expression.appendSql(context);
        }

        @Override
        public final String toString() {
            return this.expression.toString();
        }


        @Override
        public int hashCode() {
            return Objects.hash(this.expression, this.castType);
        }

        @Override
        public boolean equals(final Object obj) {
            final boolean match;
            if (obj == this) {
                match = true;
            } else if (obj instanceof CastExpression) {
                final CastExpression o = (CastExpression) obj;
                match = o.expression.equals(this.expression)
                        && o.castType.equals(this.castType);
            } else {
                match = false;
            }
            return match;
        }


    }//CastExpression


    private static final class SubQueryPredicate extends OperationPredicate.CompoundPredicate {
        private final ArmyExpression left;

        private final DualBooleanOperator operator;

        private final QueryOperator queryOperator;

        private final SubQuery subQuery;

        private SubQueryPredicate(OperationExpression left, DualBooleanOperator operator,
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
                sqlBuilder.append(queryOperator.spaceWord);
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
                builder.append(queryOperator.spaceWord);
            }

            return builder.append(this.subQuery)
                    .toString();
        }


    }//SubQueryPredicate


    private static final class BooleanTestPredicate extends OperationPredicate.CompoundPredicate {

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

    private static final class IsComparisonPredicate extends OperationPredicate.CompoundPredicate {

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

            final ArmyExpression right = this.right;
            final boolean rightOuterParens = !(right instanceof ArmySimpleExpression);
            if (rightOuterParens) {
                sqlBuilder.append(_Constant.SPACE_LEFT_PAREN);
            }
            right.appendSql(context);

            if (rightOuterParens) {
                sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);
            }
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
            builder.append(this.operator.spaceRender());

            final ArmyExpression right = this.right;
            final boolean rightOuterParens = !(right instanceof ArmySimpleExpression);
            if (rightOuterParens) {
                builder.append(_Constant.SPACE_LEFT_PAREN);
            }
            builder.append(right);

            if (rightOuterParens) {
                builder.append(_Constant.SPACE_RIGHT_PAREN);
            }
            return builder.toString();
        }


    }//IsComparisonPredicates


}
