package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.dialect._Constant;
import io.army.dialect._SqlContext;
import io.army.mapping.*;
import io.army.mapping.optional.IntervalType;
import io.army.mapping.postgre.PostgreInetType;
import io.army.mapping.postgre.PostgreTsQueryType;
import io.army.mapping.spatial.postgre.PostgreBoxType;
import io.army.mapping.spatial.postgre.PostgreGeometricType;
import io.army.mapping.spatial.postgre.PostgrePointType;
import io.army.type.Interval;
import io.army.util._Exceptions;
import io.army.util._StringUtils;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.UnaryOperator;

abstract class PostgreExpressions {

    private PostgreExpressions() {
        throw new UnsupportedOperationException();
    }


    /**
     * @see Postgres#period(Expression, Expression)
     */
    static Postgres._PeriodOverlapsClause overlaps(final @Nullable Expression start,
                                                   final @Nullable Expression endOrLength) {
        if (start == null || endOrLength == null) {
            throw ContextStack.clearStackAndNullPointer();
        } else if (start instanceof SqlValueParam.MultiValue || endOrLength instanceof SqlValueParam.MultiValue) {
            throw overlapsDontSupportMultiValue();
        }
        return new PeriodOverlapsPredicate(start, endOrLength);
    }


    static SimpleExpression unaryExpression(final PostgreUnaryExpOperator operator, final Expression operand,
                                            final UnaryOperator<MappingType> inferFunc) {
        if (!(operand instanceof OperationExpression)) {
            throw NonOperationExpression.nonOperationExpression(operand);
        }
        return new PostgreUnaryExpression(operator, operand, inferFunc);
    }


    static OperationPredicate unaryPredicate(PostgreBooleanUnaryOperator operator, Expression operand) {
        if (!(operand instanceof OperationExpression)) {
            throw NonOperationExpression.nonOperationExpression(operand);
        }
        return new PostgreUnaryPredicate(operator, operand);
    }

    static <T extends SQLExpression> CompoundPredicate dualPredicate(final T left, final PostgreDualBooleanOperator operator,
                                                                     final T right) {
        if (!(left instanceof OperationSQLExpression)) {
            throw ContextStack.clearStackAndNonArmyItem(left);
        } else if (!(right instanceof OperationSQLExpression)) {
            throw ContextStack.clearStackAndNonArmyItem(right);
        }
        if (left instanceof RowExpression && right instanceof RowExpression) {
            RowExpressions.validateColumnSize((RowExpression) left, (RowExpression) right);
        }
        return new PostgreDualPredicate(left, operator, right);
    }


    static MappingType lengthFuncType(final MappingType operandType) {
        final MappingType returnType;
        if (operandType instanceof PostgreGeometricType || operandType instanceof MappingType.SqlGeometryType) {
            returnType = DoubleType.INSTANCE;
        } else {
            returnType = IntegerType.INSTANCE;
        }
        return returnType;
    }


    /**
     * <p>
     *     <ul>
     *         <li>numeric_type + numeric_type → numeric_type</li>
     *         <li>date + integer </li>
     *         <li>date + interval → timestamp or date </li>
     *         <li>date + time → timestamp</li>
     *         <li>timestamp + interval → timestamp</li>
     *         <li>time + interval → time</li>
     *         <li>interval + interval → interval</li>
     *         <li>geometric_type + point → geometric_type</li>
     *         <li>path + path → path</li>
     *         <li>inet + bigint → inet</li>
     *     </ul>
     * </p>
     *
     * @see Postgres#plus(Expression, Expression)
     * @see Expressions#plusType(MappingType, MappingType)
     */
    static MappingType plusType(final MappingType left, final MappingType right) {
        final MappingType returnType;
        if (!(left instanceof MappingType.SqlNumberOrStringType) && right instanceof MappingType.SqlNumberType) { // date + integer → date ; inet + bigint → inet
            returnType = left;
        } else if (left instanceof MappingType.SqlNumberType && !(right instanceof MappingType.SqlNumberOrStringType)) { // date + integer → date ; inet + bigint → inet
            returnType = right;
        } else if (left instanceof MappingType.SqlNumberOrStringType && right instanceof MappingType.SqlNumberOrStringType) { // numeric_type + numeric_type → numeric_type
            returnType = Expressions.plusType(left, right);
        } else if (left instanceof MappingType.SqlLocalDateType || right instanceof MappingType.SqlLocalDateType) {
            if (right instanceof MappingType.SqlIntervalType || left instanceof MappingType.SqlIntervalType) { // date + interval → timestamp or date + time → timestamp
                returnType = LocalDateTimeType.INSTANCE;
            } else {
                returnType = TextType.INSTANCE;
            }
        } else if (left instanceof MappingType.SqlLocalDateTimeType || right instanceof MappingType.SqlLocalDateTimeType) { // timestamp + interval → timestamp
            if (right instanceof MappingType.SqlIntervalType || left instanceof MappingType.SqlIntervalType) {
                returnType = IntervalType.from(Interval.class);
            } else {
                returnType = TextType.INSTANCE;
            }
        } else if ((left instanceof MappingType.SqlLocalTimeType && right instanceof MappingType.SqlIntervalType)
                || (left instanceof MappingType.SqlIntervalType && right instanceof MappingType.SqlLocalTimeType)) { // time + interval → time
            returnType = LocalTimeType.INSTANCE;
        } else if (left instanceof MappingType.SqlTemporalAmountType && right instanceof MappingType.SqlTemporalAmountType) { // interval + interval → interval
            returnType = IntervalType.from(Interval.class);
        } else if ((left instanceof MappingType.SqlGeometryType && right instanceof MappingType.SqlPointType)
                || (right instanceof MappingType.SqlGeometryType && left instanceof MappingType.SqlPointType)) { // geometric_type + point → geometric_type
            returnType = right instanceof MappingType.SqlPointType ? left : right;
        } else if (left instanceof MappingType.SqlLineStringType && right instanceof MappingType.SqlLineStringType) { // path + path → path
            returnType = left;
        } else if (left.isSameType(right)) { // anyrange + anyrange → anyrange
            returnType = left;
        } else {
            returnType = Expressions.plusType(left, right);
        }
        return returnType;
    }

    /**
     * @see Postgres#minus(Expression, Expression)
     */
    static MappingType minusType(final MappingType left, final MappingType right) {
        final MappingType returnType;
        if (left instanceof MappingType.SqlLocalDateType) {
            if (right instanceof MappingType.SqlLocalDateType) { // date - date → integer
                returnType = IntegerType.INSTANCE;
            } else if (right instanceof MappingType.SqlIntegerType) { // date - integer → date
                returnType = left;
            } else if (right instanceof MappingType.SqlIntervalType) { // date - interval → timestamp
                returnType = LocalDateTimeType.INSTANCE;
            } else { // error or unknown
                returnType = TextType.INSTANCE;
            }
        } else if (left instanceof MappingType.SqlLocalTimeType || left instanceof MappingType.SqlOffsetTimeType) {
            if (right instanceof MappingType.SqlLocalTimeType || right instanceof MappingType.SqlOffsetTimeType) {  // time - time → interval
                returnType = IntervalType.from(Interval.class);
            } else if (right instanceof MappingType.SqlTemporalAmountType) { // time - interval → time
                returnType = left;
            } else { // error or unknown
                returnType = TextType.INSTANCE;
            }
        } else if (left instanceof MappingType.SqlLocalDateTimeType || left instanceof MappingType.SqlOffsetDateTimeType) {
            if (right instanceof MappingType.SqlLocalDateTimeType || right instanceof MappingType.SqlOffsetDateTimeType) {  // timestamp - timestamp → interval
                returnType = IntervalType.from(Interval.class);
            } else if (right instanceof MappingType.SqlTemporalAmountType) { // timestamp - interval → timestamp
                returnType = left;
            } else { // error or unknown
                returnType = TextType.INSTANCE;
            }
        } else if (left instanceof MappingType.SqlJsonbType && right instanceof MappingType.SqlNumberOrStringType) {
            returnType = left;
        } else if (left instanceof MappingType.SqlTemporalAmountType && right instanceof MappingType.SqlTemporalAmountType) { // interval - interval → interval
            returnType = IntervalType.from(Interval.class);
        } else if (left instanceof MappingType.SqlGeometryType && right instanceof MappingType.SqlPointType) { // geometric_type - point → geometric_type
            returnType = left;
        } else if (left instanceof PostgreInetType && right instanceof PostgreInetType) {
            returnType = LongType.INSTANCE;
        } else if (left.getClass() == right.getClass()) { // numeric_type - numeric_type → numeric_type ;  date - date → integer ; time - time → interval
            returnType = left;
        } else if (!(left instanceof MappingType.SqlNumberOrStringType) && right instanceof MappingType.SqlNumberType) { // date - integer → date
            returnType = left;
        } else if (left instanceof MappingType.SqlNumberOrStringType && right instanceof MappingType.SqlNumberOrStringType) { // numeric_type - numeric_type → numeric_type
            returnType = Expressions.mathExpType(left, right);
        } else if (left.isSameType(right)) {   // anyrange - anyrange → anyrange
            returnType = left;
        } else { // error or unknown
            returnType = TextType.INSTANCE;
        }
        return returnType;
    }

    /**
     * @see Postgres#times(Expression, Expression)
     */
    static MappingType timesType(final MappingType left, final MappingType right) {
        final MappingType returnType;
        if (left instanceof MappingType.SqlNumberOrStringType && right instanceof MappingType.SqlNumberOrStringType) { // numeric_type * numeric_type → numeric_type
            returnType = Expressions.mathExpType(left, right);
        } else if ((left instanceof MappingType.SqlTemporalAmountType || right instanceof MappingType.SqlTemporalAmountType)
                && (right instanceof MappingType.SqlNumberType || left instanceof MappingType.SqlNumberType)) {  // interval * double precision → interval
            returnType = IntervalType.from(Interval.class);
        } else if (left instanceof MappingType.SqlGeometryType && right instanceof MappingType.SqlPointType) { // geometric_type * point → geometric_type
            returnType = left;
        } else if (left.isSameType(right)) {   // anyrange - anyrange → anyrange
            returnType = left;
        } else { // error or unknown
            returnType = Expressions.mathExpType(left, right);
        }
        return returnType;
    }

    /**
     * @see Postgres#divide(Expression, Expression)
     */
    static MappingType divideType(final MappingType left, final MappingType right) {
        final MappingType returnType;
        if (left instanceof MappingType.SqlNumberOrStringType && right instanceof MappingType.SqlNumberOrStringType) { // numeric_type / numeric_type → numeric_type
            returnType = Expressions.mathExpType(left, right);
        } else if (left instanceof MappingType.SqlTemporalAmountType && right instanceof MappingType.SqlNumberType) {  // interval / double precision → interval
            returnType = IntervalType.from(Interval.class);
        } else if (left instanceof MappingType.SqlGeometryType && right instanceof MappingType.SqlPointType) { // geometric_type / point → geometric_type
            returnType = left;
        } else {
            returnType = StringType.INSTANCE;
        }
        return returnType;
    }

    /**
     * <p>
     *     <ul>
     *         <li>bit || bit → bit</li>
     *         <li>text || text → text</li>
     *         <li>text || anynonarray → text </li>
     *         <li>tsvector || tsvector → tsvector</li>
     *         <li>tsquery || tsquery → tsquery</li>
     *         <li>anycompatiblearray || anycompatiblearray → anycompatiblearray</li>
     *         <li>anycompatible || anycompatiblearray → anycompatiblearray</li>
     *         <li>anycompatiblearray || anycompatible → anycompatiblearray</li>
     *     </ul>
     * </p>
     *
     * @see Postgres#doubleVertical(Expression, Expression)
     */
    static MappingType doubleVerticalType(final MappingType left, final MappingType right) {
        final MappingType returnType;
        if (left.getClass() == right.getClass()) {
            returnType = left;
        } else if (left instanceof MappingType.SqlStringType && right instanceof MappingType.SqlStringType) {
            returnType = TextType.INSTANCE;
        } else if (left instanceof MappingType.SqlBinaryType && right instanceof MappingType.SqlBinaryType) {
            returnType = BinaryType.INSTANCE;
        } else if (left instanceof MappingType.SqlBitType && right instanceof MappingType.SqlBitType) {
            returnType = BitSetType.INSTANCE;
        } else if (left instanceof MappingType.SqlJsonbType && right instanceof MappingType.SqlJsonbType) {
            returnType = left;
        } else if (left instanceof MappingType.SqlArrayType) {
            returnType = left;
        } else if (right instanceof MappingType.SqlArrayType) {
            returnType = right;
        } else if (left instanceof MappingType.SqlStringType) {
            returnType = left;
        } else if (right instanceof MappingType.SqlStringType) {
            returnType = right;
        } else {
            returnType = TextType.INSTANCE;
        }
        return returnType;
    }

    /**
     * <p>
     *     <ul>
     *         <li>tsquery && tsquery → tsquery</li>
     *     </ul>
     * </p>
     *
     * @see Postgres#ampAmp(Expression, Expression)
     */
    static MappingType doubleAmpType(final MappingType left, final MappingType right) {
        final MappingType returnType;
        if (left instanceof PostgreTsQueryType && right instanceof PostgreTsQueryType) {
            returnType = left;
        } else {
            returnType = StringType.INSTANCE;
        }
        return returnType;
    }

    /**
     * @see Postgres#hyphenGt(Expression, Expression)
     */
    static MappingType hyphenGtType(final MappingType left, final MappingType right) {
        final MappingType returnType;
        if (left instanceof MappingType.SqlJsonDocumentType && (right instanceof MappingType.SqlNumberOrStringType)) {
            returnType = left;
        } else {
            returnType = StringType.INSTANCE;
        }
        return returnType;
    }

    static MappingType hyphenGtGtType(final MappingType left, final MappingType right) {
        final MappingType returnType;
        if ((left instanceof MappingType.SqlJsonType || left instanceof MappingType.SqlJsonbType)
                && (right instanceof MappingType.SqlNumberOrStringType)) {
            returnType = TextType.INSTANCE;
        } else {
            returnType = StringType.INSTANCE;
        }
        return returnType;
    }

    static MappingType poundGtType(final MappingType left, final MappingType right) {
        final MappingType returnType;
        if ((left instanceof MappingType.SqlJsonType || left instanceof MappingType.SqlJsonbType)
                && (right instanceof MappingType.SqlArrayType && right instanceof MappingType.SqlStringType)) {
            returnType = left;
        } else {
            returnType = StringType.INSTANCE;
        }
        return returnType;
    }

    /**
     * @see Postgres#poundHyphen(Expression, Expression)
     */
    static MappingType poundHyphenType(final MappingType left, final MappingType right) {
        final MappingType returnType;
        if (left instanceof MappingType.SqlJsonbType
                && right instanceof MappingType.SqlStringType
                && right instanceof MappingType.SqlArrayType) {
            returnType = left;
        } else {
            returnType = StringType.INSTANCE;
        }
        return returnType;
    }

    /**
     * @see Postgres#poundGtGt(Expression, Expression)
     */
    static MappingType poundGtGtType(final MappingType left, final MappingType right) {
        final MappingType returnType;
        if (left instanceof MappingType.SqlJsonDocumentType
                && (right instanceof MappingType.SqlArrayType && right instanceof MappingType.SqlStringType)) {
            returnType = TextType.INSTANCE;
        } else {
            returnType = StringType.INSTANCE;
        }
        return returnType;
    }


    /**
     * @see Postgres#atHyphenAt(Expression)
     */
    static MappingType atHyphenAtType(final MappingType operandType) {
        final MappingType returnType;
        if (operandType instanceof MappingType.SqlGeometryType) {
            returnType = DoubleType.INSTANCE;
        } else { // error or unknown
            returnType = StringType.INSTANCE;
        }
        return returnType;
    }

    /**
     * @see Postgres#atAt(Expression)
     */
    static MappingType atAtType(final MappingType operandType) {
        final MappingType returnType;
        if (operandType instanceof MappingType.SqlGeometryType) {
            returnType = PostgrePointType.INSTANCE;
        } else { // error or unknown
            returnType = StringType.INSTANCE;
        }
        return returnType;
    }

    /**
     * @see Postgres#pound(Expression)
     */
    static MappingType unaryPoundType(final MappingType operandType) {
        final MappingType returnType;
        if (operandType instanceof MappingType.SqlGeometryType) {
            returnType = IntegerType.INSTANCE;
        } else { // error or unknown
            returnType = StringType.INSTANCE;
        }
        return returnType;
    }

    /**
     * @see Postgres#pound(Expression, Expression)
     */
    static MappingType dualPoundType(final MappingType left, final MappingType right) {
        final MappingType returnType;
        if (left instanceof PostgreBoxType && right instanceof PostgreBoxType) {
            returnType = PostgreBoxType.INSTANCE;
        } else if (left instanceof MappingType.SqlGeometryType && right instanceof MappingType.SqlGeometryType) {
            returnType = PostgrePointType.INSTANCE;
        } else { // error or unknown
            returnType = StringType.INSTANCE;
        }
        return returnType;
    }

    /**
     * @see Postgres#ltHyphenGt(Expression, Expression)
     */
    static MappingType ltHyphenGtType(final MappingType left, final MappingType right) {
        final MappingType returnType;
        if (left instanceof PostgreTsQueryType && right instanceof PostgreTsQueryType) {
            returnType = left;
        } else if (left instanceof PostgreGeometricType && right instanceof PostgreGeometricType) {
            returnType = DoubleType.INSTANCE;
        } else if (left instanceof MappingType.SqlGeometryType && right instanceof MappingType.SqlGeometryType) {
            returnType = DoubleType.INSTANCE;
        } else { // error or unknown
            returnType = StringType.INSTANCE;
        }
        return returnType;
    }





    /*-------------------below private -------------------*/

    private static CriteriaException overlapsDontSupportMultiValue() {
        String m = "Postgre OVERLAPS operator don't support multi-value parameter/literal";
        return ContextStack.clearStackAndCriteriaError(m);
    }


    private static final class PeriodOverlapsPredicate extends OperationPredicate.OperationCompoundPredicate
            implements Postgres._PeriodOverlapsClause {

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
        public void appendSql(final StringBuilder sqlBuilder, final _SqlContext context) {
            final ArmyExpression start2 = this.start2, endOrLength2 = this.endOrLength2;
            if (start2 == null || endOrLength2 == null) {
                throw _Exceptions.castCriteriaApi();
            }

            sqlBuilder.append(_Constant.SPACE_LEFT_PAREN);

            this.start1.appendSql(sqlBuilder, context);
            sqlBuilder.append(_Constant.SPACE_COMMA);
            this.endOrLength1.appendSql(sqlBuilder, context);

            sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN)
                    .append(" OVERLAPS")
                    .append(_Constant.SPACE_LEFT_PAREN);

            start2.appendSql(sqlBuilder, context);
            sqlBuilder.append(_Constant.SPACE_COMMA);
            endOrLength2.appendSql(sqlBuilder, context);

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

    private static final class PostgreDualPredicate extends Expressions.DualPredicate {

        /**
         * @see #dualPredicate(SQLExpression, PostgreDualBooleanOperator, SQLExpression)
         */
        private PostgreDualPredicate(SQLExpression left, PostgreDualBooleanOperator operator, RightOperand right) {
            super((OperationSQLExpression) left, operator, right);
        }


    }//PostgreDualPredicate


    private static final class PostgreUnaryExpression extends Expressions.UnaryExpression {

        /**
         * @see #unaryExpression(PostgreUnaryExpOperator, Expression, UnaryOperator)
         */
        private PostgreUnaryExpression(PostgreUnaryExpOperator operator, Expression operand,
                                       UnaryOperator<MappingType> inferFunc) {
            super(operator, operand, inferFunc);
        }
    }//PostgreUnaryExpression



    private static final class PostgreUnaryPredicate extends OperationPredicate.OperationCompoundPredicate {

        private final PostgreBooleanUnaryOperator operator;

        private final ArmyExpression operand;

        /**
         * @see #unaryPredicate(PostgreBooleanUnaryOperator, Expression)
         */
        private PostgreUnaryPredicate(PostgreBooleanUnaryOperator operator, Expression operand) {
            this.operator = operator;
            this.operand = (ArmyExpression) operand;
        }

        @Override
        public void appendSql(final StringBuilder sqlBuilder, final _SqlContext context) {
            sqlBuilder.append(this.operator.spaceOperator);
            this.operand.appendSql(sqlBuilder, context);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.operator, this.operand);
        }

        @Override
        public boolean equals(final Object obj) {
            final boolean match;
            if (obj == this) {
                match = true;
            } else if (obj instanceof PostgreUnaryPredicate) {
                final PostgreUnaryPredicate o = (PostgreUnaryPredicate) obj;
                match = o.operator == this.operator
                        && o.operand.equals(this.operand);
            } else {
                match = false;
            }
            return match;
        }

        @Override
        public String toString() {
            return _StringUtils.builder()
                    .append(this.operator)
                    .append(this.operand)
                    .toString();
        }


    }//PostgreUnaryPredicate


}
