package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.dialect.SubQuery;
import io.army.criteria.impl.inner._DerivedTable;
import io.army.dialect.Database;
import io.army.dialect.Dialect;
import io.army.dialect._Constant;
import io.army.dialect._SqlContext;
import io.army.dialect.mysql.MySQLDialect;
import io.army.lang.Nullable;
import io.army.mapping.*;
import io.army.mapping.optional.JsonPathType;
import io.army.meta.TypeMeta;
import io.army.util._Collections;
import io.army.util._Exceptions;
import io.army.util._StringUtils;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
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


    static SimpleExpression arrayElementExp(OperationExpression arrayExp, int index) {
        if (!(arrayExp instanceof ArmyArrayExpression)) {
            throw ContextStack.clearStackAnd(_Exceptions::castCriteriaApi);
        }
        return new OneDimensionArrayElementExpression(arrayExp, index);
    }

    static SimpleExpression arrayElementExp(OperationExpression arrayExp, int index1, int index2) {
        if (!(arrayExp instanceof ArmyArrayExpression)) {
            throw ContextStack.clearStackAnd(_Exceptions::castCriteriaApi);
        }
        return new TwoDimensionArrayElementExpression(arrayExp, index1, index2);
    }

    static SimpleExpression arrayElementExp(OperationExpression arrayExp, int index1, int index2, int index3, int[] restIndex) {
        if (!(arrayExp instanceof ArmyArrayExpression)) {
            throw ContextStack.clearStackAnd(_Exceptions::castCriteriaApi);
        }
        return new ThreeDimensionIndexArrayElementExpression(arrayExp, index1, index2, index3, restIndex);
    }

    static SimpleExpression arrayElementExp(OperationExpression arrayExp, Expression index) {
        if (!(arrayExp instanceof ArmyArrayExpression)) {
            throw ContextStack.clearStackAnd(_Exceptions::castCriteriaApi);
        }
        return new OneDimensionArrayElementExpression(arrayExp, index);
    }

    static SimpleExpression arrayElementExp(OperationExpression arrayExp, Expression index1, Expression index2) {
        if (!(arrayExp instanceof ArmyArrayExpression)) {
            throw ContextStack.clearStackAnd(_Exceptions::castCriteriaApi);
        }
        return new TwoDimensionArrayElementExpression(arrayExp, index1, index2);
    }

    static SimpleExpression arrayElementExp(OperationExpression arrayExp, Expression index1, Expression index2,
                                            Expression index3, Expression[] restIndex) {
        if (!(arrayExp instanceof ArmyArrayExpression)) {
            throw ContextStack.clearStackAnd(_Exceptions::castCriteriaApi);
        }
        return new ThreeDimensionArrayElementExpression(arrayExp, index1, index2, index3, restIndex);
    }

    /*-------------------below array element array expression -------------------*/


    static ArrayExpression arrayElementArrayExp(OperationExpression arrayExp, int index) {
        if (!(arrayExp instanceof ArmyArrayExpression)) {
            throw ContextStack.clearStackAnd(_Exceptions::castCriteriaApi);
        }
        return new OneDimensionArrayElementArrayExpression(arrayExp, index);
    }

    static ArrayExpression arrayElementArrayExp(OperationExpression arrayExp, int index1, int index2) {
        if (!(arrayExp instanceof ArmyArrayExpression)) {
            throw ContextStack.clearStackAnd(_Exceptions::castCriteriaApi);
        }
        return new TwoDimensionArrayElementArrayExpression(arrayExp, index1, index2);
    }

    static ArrayExpression arrayElementArrayExp(OperationExpression arrayExp, int index1, int index2, int index3, int[] restIndex) {
        if (!(arrayExp instanceof ArmyArrayExpression)) {
            throw ContextStack.clearStackAnd(_Exceptions::castCriteriaApi);
        }
        return new ThreeDimensionIndexArrayElementArrayExpression(arrayExp, index1, index2, index3, restIndex);
    }

    static ArrayExpression arrayElementArrayExp(OperationExpression arrayExp, ArraySubscript index) {
        if (!(arrayExp instanceof ArmyArrayExpression)) {
            throw ContextStack.clearStackAnd(_Exceptions::castCriteriaApi);
        }
        return new OneDimensionArrayElementArrayExpression(arrayExp, index);
    }

    static ArrayExpression arrayElementArrayExp(OperationExpression arrayExp, ArraySubscript index1, ArraySubscript index2) {
        if (!(arrayExp instanceof ArmyArrayExpression)) {
            throw ContextStack.clearStackAnd(_Exceptions::castCriteriaApi);
        }
        return new TwoDimensionArrayElementArrayExpression(arrayExp, index1, index2);
    }

    static ArrayExpression arrayElementArrayExp(OperationExpression arrayExp, ArraySubscript index1, ArraySubscript index2,
                                                ArraySubscript index3, ArraySubscript[] restIndex) {
        if (!(arrayExp instanceof ArmyArrayExpression)) {
            throw ContextStack.clearStackAnd(_Exceptions::castCriteriaApi);
        }
        return new ThreeDimensionArrayElementArrayExpression(arrayExp, index1, index2, index3, restIndex);
    }

    static JsonExpression jsonArrayElement(OperationExpression json, int index) {
        if (!(json instanceof JsonExpression)) {
            throw ContextStack.clearStackAnd(_Exceptions::castCriteriaApi);
        }
        return new JsonArrayElementExpression(json, index);
    }

    static JsonExpression jsonObjectAttr(OperationExpression json, String keyName) {
        if (!(json instanceof JsonExpression)) {
            throw ContextStack.clearStackAnd(_Exceptions::castCriteriaApi);
        }
        return new JsonObjectAttrExpression(json, keyName);
    }

    static JsonExpression jsonPathExtract(OperationExpression json, String jsonPath) {
        if (!(json instanceof JsonExpression)) {
            throw ContextStack.clearStackAnd(_Exceptions::castCriteriaApi);
        }
        return new JsonPathExtractExpression(json, jsonPath);
    }

    static JsonExpression jsonPathExtract(OperationExpression json, Expression jsonPath) {
        if (!(json instanceof JsonExpression)) {
            throw ContextStack.clearStackAnd(_Exceptions::castCriteriaApi);
        }
        return new JsonPathExtractExpression(json, jsonPath);
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


    private static abstract class ArrayElementExpression extends OperationExpression.OperationSimpleExpression {

        private final ArmyArrayExpression arrayExp;


        private final TypeMeta type;

        private ArrayElementExpression(final OperationExpression arrayExp) {
            this.arrayExp = (ArmyArrayExpression) arrayExp;
            final TypeMeta arrayType;
            if (arrayExp instanceof TypeInfer.DelayTypeInfer && ((TypeInfer.DelayTypeInfer) arrayExp).isDelay()) {
                arrayType = CriteriaSupports.unaryInfer((TypeInfer.DelayTypeInfer) arrayExp, Expressions::identityType);
            } else {
                arrayType = arrayExp.typeMeta();
            }

            if (this instanceof ArrayExpression) {
                if (arrayType instanceof MappingType || arrayType instanceof TypeMeta.DelayTypeMeta) {
                    this.type = arrayType;
                } else {
                    // avoid to field codec
                    this.type = arrayType.mappingType();
                }
            } else if (arrayType instanceof TypeMeta.DelayTypeMeta) {
                this.type = arrayType;
            } else if (arrayType instanceof MappingType) {
                this.type = ((MappingType.SqlArrayType) arrayType).elementType();
            } else {
                // avoid to field codec
                this.type = ((MappingType.SqlArrayType) arrayType.mappingType()).elementType();
            }
        }

        @Override
        public final TypeMeta typeMeta() {
            return this.type;
        }

        @Override
        public final void appendSql(final _SqlContext context) {

            final StringBuilder sqlBuilder;
            sqlBuilder = context.sqlBuilder();

            final boolean outerParens;
            outerParens = !(this.arrayExp instanceof SimpleArrayExpression);

            if (outerParens) {
                sqlBuilder.append(_Constant.SPACE_LEFT_PAREN);
            }

            this.arrayExp.appendSql(context);

            if (outerParens) {
                sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);
            }

            this.appendSubscripts(sqlBuilder, context);
        }

        @Override
        public final String toString() {
            final StringBuilder sqlBuilder;
            sqlBuilder = new StringBuilder();

            final boolean outerParens;
            outerParens = this.arrayExp instanceof SimpleArrayExpression;

            if (outerParens) {
                sqlBuilder.append(_Constant.SPACE_LEFT_PAREN);
            }

            sqlBuilder.append(this.arrayExp);

            if (outerParens) {
                sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);
            }
            this.subscriptsToString(sqlBuilder);
            return sqlBuilder.toString();
        }


        abstract void appendSubscripts(StringBuilder sqlBuilder, _SqlContext context);

        abstract void subscriptsToString(StringBuilder builder);


        static void appendSubscript(final Object index, final StringBuilder sqlBuilder,
                                    final _SqlContext context) {
            sqlBuilder.append(_Constant.LEFT_SQUARE_BRACKET);
            if (index instanceof Integer) {
                sqlBuilder.append(_Constant.SPACE)
                        .append(index);
            } else {
                ((ArmyArraySubscript) index).appendSql(context);
            }
            sqlBuilder.append(_Constant.SPACE_RIGHT_SQUARE_BRACKET);
        }

        static void subscriptToString(final Object index, final StringBuilder builder) {
            builder.append(_Constant.LEFT_SQUARE_BRACKET);
            if (index instanceof Integer) {
                builder.append(_Constant.SPACE);
            }
            builder.append(index);
            builder.append(_Constant.SPACE_RIGHT_SQUARE_BRACKET);
        }


    }//ArrayElementExpression

    private static class OneDimensionArrayElementExpression extends ArrayElementExpression {

        private final Object index;

        private OneDimensionArrayElementExpression(OperationExpression arrayExp, Object index) {
            super(arrayExp);
            assert index instanceof Integer || index instanceof ArmyArraySubscript;
            this.index = index;
        }

        @Override
        final void appendSubscripts(StringBuilder sqlBuilder, _SqlContext context) {
            appendSubscript(this.index, sqlBuilder, context);
        }

        @Override
        final void subscriptsToString(StringBuilder builder) {
            subscriptToString(this.index, builder);
        }

    }//OneDimensionArrayElementExpression


    private static class TwoDimensionArrayElementExpression extends ArrayElementExpression {

        private final Object index1;

        private final Object index2;

        private TwoDimensionArrayElementExpression(OperationExpression arrayExp, Object index1, Object index2) {
            super(arrayExp);
            assert index1 instanceof Integer || index1 instanceof ArmyArraySubscript;
            assert index2 instanceof Integer || index2 instanceof ArmyArraySubscript;
            this.index1 = index1;
            this.index2 = index2;
        }

        @Override
        final void appendSubscripts(StringBuilder sqlBuilder, _SqlContext context) {
            appendSubscript(this.index1, sqlBuilder, context);
            appendSubscript(this.index2, sqlBuilder, context);
        }

        @Override
        final void subscriptsToString(StringBuilder builder) {
            subscriptToString(this.index1, builder);
            subscriptToString(this.index2, builder);
        }

    }//TwoDimensionArrayElementExpression

    private static class ThreeDimensionIndexArrayElementExpression extends ArrayElementExpression {

        private final int index1;

        private final int index2;

        private final int index3;

        private final int[] restIndex;

        private ThreeDimensionIndexArrayElementExpression(OperationExpression arrayExp, int index1, int index2,
                                                          int index3, int[] restIndex) {
            super(arrayExp);

            this.index1 = index1;
            this.index2 = index2;
            this.index3 = index3;
            this.restIndex = restIndex;
        }

        @Override
        final void appendSubscripts(StringBuilder sqlBuilder, _SqlContext context) {
            appendSubscript(this.index1, sqlBuilder, context);
            appendSubscript(this.index2, sqlBuilder, context);
            appendSubscript(this.index3, sqlBuilder, context);

            for (int index : this.restIndex) {
                appendSubscript(index, sqlBuilder, context);
            }

        }

        @Override
        final void subscriptsToString(StringBuilder builder) {
            subscriptToString(this.index1, builder);
            subscriptToString(this.index2, builder);
            subscriptToString(this.index3, builder);

            for (int index : this.restIndex) {
                subscriptToString(index, builder);
            }
        }


    }//ThreeDimensionIndexArrayElementExpression


    private static class ThreeDimensionArrayElementExpression extends ArrayElementExpression {

        private final ArmyArraySubscript index1;

        private final ArmyArraySubscript index2;

        private final ArmyArraySubscript index3;

        private final ArraySubscript[] restIndex;

        private ThreeDimensionArrayElementExpression(OperationExpression arrayExp, ArraySubscript index1,
                                                     ArraySubscript index2, ArraySubscript index3,
                                                     ArraySubscript[] restIndex) {
            super(arrayExp);
            this.index1 = (ArmyArraySubscript) index1;
            this.index2 = (ArmyArraySubscript) index2;
            this.index3 = (ArmyArraySubscript) index3;
            this.restIndex = restIndex;
        }

        @Override
        void appendSubscripts(StringBuilder sqlBuilder, _SqlContext context) {
            appendSubscript(this.index1, sqlBuilder, context);
            appendSubscript(this.index2, sqlBuilder, context);
            appendSubscript(this.index3, sqlBuilder, context);

            for (ArraySubscript index : this.restIndex) {
                if (!(index instanceof ArmyArraySubscript)) {
                    String m = String.format("%s isn't %s", index, ArmyArraySubscript.class.getName());
                    throw new CriteriaException(m);
                }
                appendSubscript(index, sqlBuilder, context);
            }
        }

        @Override
        void subscriptsToString(StringBuilder builder) {
            subscriptToString(this.index1, builder);
            subscriptToString(this.index2, builder);
            subscriptToString(this.index3, builder);

            for (ArraySubscript index : this.restIndex) {
                subscriptToString(index, builder);
            }

        }


    }//ThreeDimensionArrayElementExpression


    private static final class OneDimensionArrayElementArrayExpression extends OneDimensionArrayElementExpression
            implements ArmyArrayExpression {

        private OneDimensionArrayElementArrayExpression(OperationExpression arrayExp, Object index) {
            super(arrayExp, index);
        }


    }//OneDimensionArrayElementArrayExpression

    private static final class TwoDimensionArrayElementArrayExpression extends TwoDimensionArrayElementExpression
            implements ArmyArrayExpression {

        private TwoDimensionArrayElementArrayExpression(OperationExpression arrayExp, Object index1, Object index2) {
            super(arrayExp, index1, index2);
        }

    }//TwoDimensionArrayElementArrayExpression


    private static final class ThreeDimensionIndexArrayElementArrayExpression
            extends ThreeDimensionIndexArrayElementExpression
            implements ArmyArrayExpression {

        private ThreeDimensionIndexArrayElementArrayExpression(OperationExpression arrayExp, int index1, int index2,
                                                               int index3, int[] restIndex) {
            super(arrayExp, index1, index2, index3, restIndex);
        }


    }//ThreeDimensionIndexArrayElementArrayExpression


    private static final class ThreeDimensionArrayElementArrayExpression extends ThreeDimensionArrayElementExpression
            implements ArmyArrayExpression {

        private ThreeDimensionArrayElementArrayExpression(OperationExpression arrayExp, ArraySubscript index1,
                                                          ArraySubscript index2, ArraySubscript index3,
                                                          ArraySubscript[] restIndex) {
            super(arrayExp, index1, index2, index3, restIndex);
        }

    }//ThreeDimensionArrayElementArrayExpression

    static MappingType jsonIdentityType(final MappingType type) {
        final MappingType returnType;
        if (type instanceof MappingType.SqlJsonType) {
            returnType = JsonType.TEXT;
        } else if (type instanceof MappingType.SqlJsonbType) {
            returnType = JsonbType.TEXT;
        } else {
            String m = String.format("%s isn't json document type", type);
            throw ContextStack.clearStackAndCriteriaError(m);
        }
        return returnType;
    }


    private static abstract class JsonOperatorExpression extends OperationExpression.OperationSimpleExpression {

        final ArmyJsonExpression json;

        final TypeMeta type;

        private JsonOperatorExpression(final OperationExpression json) {
            this.json = (ArmyJsonExpression) json;

            final TypeMeta jsonType;
            if (json instanceof TypeInfer.DelayTypeInfer && ((DelayTypeInfer) json).isDelay()) {
                jsonType = CriteriaSupports.unaryInfer((DelayTypeInfer) json, Expressions::jsonIdentityType);
            } else {
                jsonType = json.typeMeta();
            }

            if (jsonType instanceof MappingType || jsonType instanceof TypeMeta.DelayTypeMeta) {
                this.type = jsonType;
            } else {
                this.type = jsonType.mappingType();
            }
        }

        @Override
        public final TypeMeta typeMeta() {
            return this.type;
        }

        final void jsonToString(StringBuilder builder) {

            final boolean outerParens;
            outerParens = !(this.json instanceof SimpleJsonExpression);

            if (outerParens) {
                builder.append(_Constant.LEFT_PAREN);
            }
            builder.append(this.json);
            if (outerParens) {
                builder.append(_Constant.SPACE_RIGHT_PAREN);
            }

        }

        final void appendJson(final StringBuilder sqlBuilder, final _SqlContext context) {

            final boolean outerParens;
            outerParens = !(this.json instanceof SimpleJsonExpression);

            if (outerParens) {
                sqlBuilder.append(_Constant.LEFT_PAREN);
            }
            this.json.appendSql(context);
            if (outerParens) {
                sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);
            }
        }


    }//JsonOperatorExpression

    private static final class JsonObjectAttrExpression extends JsonOperatorExpression implements ArmyJsonExpression {

        private final String key;

        private JsonObjectAttrExpression(OperationExpression json, String key) {
            super(json);
            this.key = key;
        }

        @Override
        public void appendSql(final _SqlContext context) {
            final StringBuilder sqlBuilder;
            sqlBuilder = context.sqlBuilder();

            this.appendJson(sqlBuilder, context);

            switch (context.database()) {
                case MySQL: {
                    if (context.dialect().version() < MySQLDialect.MySQL80.version()) {
                        throw dontSupportJsonObjectAttrError(context.dialect());
                    }
                    sqlBuilder.append(DualExpOperator.HYPHEN_GT.spaceOperator);
                    context.appendLiteral(NoCastTextType.INSTANCE, "$." + this.key);
                }
                break;
                case PostgreSQL: {
                    sqlBuilder.append(DualExpOperator.HYPHEN_GT.spaceOperator);
                    context.appendLiteral(NoCastTextType.INSTANCE, this.key);
                }
                break;
                case Oracle:
                case H2:
                default: {
                    //TODO add for new dialect
                    throw dontSupportJsonObjectAttrError(context.dialect());
                }

            }//switch


        }


        @Override
        public String toString() {
            final StringBuilder sqlBuilder;
            sqlBuilder = new StringBuilder();

            this.jsonToString(sqlBuilder);

            return sqlBuilder.append(DualExpOperator.HYPHEN_GT.spaceOperator)
                    .append(_Constant.SPACE)
                    .append(_Constant.QUOTE)
                    .append(this.key)
                    .append(_Constant.QUOTE)
                    .toString();
        }


        private static CriteriaException dontSupportJsonObjectAttrError(Dialect dialect) {
            String m = String.format("%s don't support json object access.", dialect);
            return new CriteriaException(m);
        }


    }//JsonObjectAttrExpression


    private static final class JsonArrayElementExpression extends JsonOperatorExpression implements ArmyJsonExpression {

        private final int index;

        private JsonArrayElementExpression(OperationExpression json, int index) {
            super(json);
            this.index = index;
        }

        @Override
        public void appendSql(final _SqlContext context) {
            final StringBuilder sqlBuilder;
            sqlBuilder = context.sqlBuilder();

            this.appendJson(sqlBuilder, context);

            switch (context.database()) {
                case MySQL: {
                    if (context.dialect().version() < MySQLDialect.MySQL80.version()) {
                        throw dontSupportJsonArrayError(context.dialect());
                    }
                    sqlBuilder.append(DualExpOperator.HYPHEN_GT.spaceOperator)
                            .append(" '$.[")
                            .append(this.index)
                            .append("]'");
                }
                break;
                case PostgreSQL:
                    sqlBuilder.append(DualExpOperator.HYPHEN_GT.spaceOperator)
                            .append(this.index);
                    break;
                case Oracle:
                case H2:
                default: {
                    //TODO add for new dialect
                    throw dontSupportJsonArrayError(context.dialect());
                }

            }//switch
        }


        @Override
        public String toString() {
            final StringBuilder sqlBuilder;
            sqlBuilder = new StringBuilder();

            this.jsonToString(sqlBuilder);
            return sqlBuilder.append(DualExpOperator.HYPHEN_GT.spaceOperator)
                    .append(_Constant.SPACE)
                    .append(this.index)
                    .toString();
        }

        private static CriteriaException dontSupportJsonArrayError(Dialect dialect) {
            String m = String.format("%s don't support json array access.", dialect);
            return new CriteriaException(m);
        }


    }//JsonArrayElementExpression


    private static final class JsonPathExtractExpression extends JsonOperatorExpression implements ArmyJsonExpression {

        private final Object jsonPath;

        private JsonPathExtractExpression(OperationExpression json, Object jsonPath) {
            super(json);
            assert jsonPath instanceof String || jsonPath instanceof ArmyExpression;
            this.jsonPath = jsonPath;
        }

        @Override
        public void appendSql(final _SqlContext context) {
            final StringBuilder sqlBuilder;
            sqlBuilder = context.sqlBuilder();

            switch (context.database()) {
                case MySQL: {
                    if (context.dialect().version() < MySQLDialect.MySQL80.version()) {
                        throw dontSupportJsonPathError(context.dialect());
                    }
                    this.appendJson(sqlBuilder, context);
                    sqlBuilder.append(DualExpOperator.HYPHEN_GT.spaceOperator);
                    if (this.jsonPath instanceof String) {
                        context.appendLiteral(JsonPathType.INSTANCE, this.jsonPath);
                    } else {
                        ((ArmyExpression) this.jsonPath).appendSql(context);
                    }
                }
                break;
                case PostgreSQL: {
                    final String funcName;
                    if (this.type.mappingType() instanceof MappingType.SqlJsonType) {
                        funcName = "JSON_PATH_QUERY";
                    } else {
                        funcName = "JSONB_PATH_QUERY";
                    }

                    sqlBuilder.append(_Constant.SPACE);
                    if (context.isLowerFunctionName()) {
                        sqlBuilder.append(funcName.toLowerCase(Locale.ROOT));
                    } else {
                        sqlBuilder.append(funcName);
                    }
                    sqlBuilder.append(_Constant.LEFT_PAREN);
                    this.json.appendSql(context);
                    sqlBuilder.append(_Constant.SPACE_COMMA);
                    if (this.jsonPath instanceof String) {
                        context.appendLiteral(JsonPathType.INSTANCE, this.jsonPath);
                    } else {
                        ((ArmyExpression) this.jsonPath).appendSql(context);
                    }
                    sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);
                }
                break;
                case Oracle:
                case H2:
                default: {
                    //TODO add for new dialect
                    throw dontSupportJsonPathError(context.dialect());
                }

            }//switch
        }


        @Override
        public String toString() {
            final StringBuilder sqlBuilder;
            sqlBuilder = new StringBuilder();

            this.jsonToString(sqlBuilder);

            sqlBuilder.append(DualExpOperator.HYPHEN_GT.spaceOperator);
            sqlBuilder.append(_Constant.SPACE);

            if (this.jsonPath instanceof SingleParamExpression) {
                sqlBuilder.append(this.jsonPath);
            } else {
                sqlBuilder.append(_Constant.QUOTE)
                        .append(this.jsonPath)
                        .append(_Constant.QUOTE);
            }
            return sqlBuilder.toString();
        }

        private static CriteriaException dontSupportJsonPathError(Dialect dialect) {
            String m = String.format("%s don't support json path.", dialect);
            return new CriteriaException(m);
        }


    }//JsonPathExtractExpression


    static SqlSyntax._ArrayConstructorClause array(final Object... elements) {
        return type -> {
            if (!(type instanceof MappingType.SqlArrayType)) {
                String m = String.format("%s isn't %s type.", type, MappingType.SqlArrayType.class.getName());
                throw ContextStack.clearStackAndCriteriaError(m);
            }
            return new ArrayConstructor(Arrays.asList(elements), type);
        };
    }

    static SqlSyntax._ArrayConstructorClause array(final Consumer<Consumer<Object>> consumer) {
        return type -> {
            if (!(type instanceof MappingType.SqlArrayType)) {
                String m = String.format("%s isn't %s type.", type, MappingType.SqlArrayType.class.getName());
                throw ContextStack.clearStackAndCriteriaError(m);
            }
            final List<Object> list = _Collections.arrayList();
            consumer.accept(list::add);
            return new ArrayConstructor(list, type);
        };
    }


    private static final class ArrayConstructor extends OperationExpression.OperationSimpleExpression
            implements ArmyArrayExpression {

        private final List<Object> elementList;

        private final MappingType type;

        private ArrayConstructor(List<Object> elementList, MappingType type) {
            this.elementList = elementList;
            this.type = type;
        }

        @Override
        public MappingType typeMeta() {
            return this.type;
        }

        @Override
        public void appendSql(final _SqlContext context) {

            final StringBuilder sqlBuilder;
            sqlBuilder = context.sqlBuilder()
                    .append(" ARRAY[");

            final List<Object> elementList = this.elementList;
            final int elementSize = elementList.size();
            Object element;
            MappingType elementType;
            for (int i = 0; i < elementSize; i++) {
                if (i > 0) {
                    sqlBuilder.append(_Constant.COMMA);
                }
                element = elementList.get(i);
                if (element == null) {
                    sqlBuilder.append(_Constant.SPACE_NULL);
                } else if (element instanceof Expression) {
                    ((ArmyExpression) element).appendSql(context);
                } else if (element instanceof String) {
                    context.appendLiteral(NoCastTextType.INSTANCE, element);
                } else if (element instanceof Integer
                        || element instanceof Long
                        || element instanceof Short
                        || element instanceof Byte
                        || element instanceof BigInteger) {
                    sqlBuilder.append(element);
                } else if ((elementType = _MappingFactory.getDefaultIfMatch(element.getClass())) == null) {
                    String m = String.format("Not found %s for %s", MappingType.class.getName(),
                            element.getClass().getName());
                    throw new CriteriaException(m);
                } else {
                    context.appendLiteral(elementType, element);
                }
            }

            sqlBuilder.append(_Constant.RIGHT_SQUARE_BRACKET);


            switch (context.database()) {
                case PostgreSQL: {
                    sqlBuilder.append("::");
                    context.parser().typeName(this.type, sqlBuilder);
                }
                break;
                case H2:
                case MySQL:
                default: {
                    String m = String.format("%s don't support array constructor expression.", context.database());
                    throw new CriteriaException(m);
                }

            }//switch


        }

        @Override
        public String toString() {
            final StringBuilder sqlBuilder;
            sqlBuilder = new StringBuilder()
                    .append(" ARRAY[");

            final List<Object> elementList = this.elementList;
            final int elementSize = elementList.size();
            for (int i = 0; i < elementSize; i++) {
                if (i > 0) {
                    sqlBuilder.append(_Constant.COMMA);
                }
                sqlBuilder.append(elementList.get(i));
            }
            return sqlBuilder.append(_Constant.RIGHT_SQUARE_BRACKET)
                    .toString();
        }


    }//ArrayConstructor


}
