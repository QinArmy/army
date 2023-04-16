package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.dialect._Constant;
import io.army.dialect._SqlContext;
import io.army.lang.Nullable;
import io.army.mapping.*;
import io.army.type.Interval;
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
     * @see Postgres#plus(Expression, Expression)
     */
    static MappingType plusType(final MappingType left, final MappingType right) {
        final MappingType returnType;
        if (left instanceof AbstractMappingType.SqlNumberOrStringType && right instanceof AbstractMappingType.SqlNumberOrStringType) { // numeric_type + numeric_type → numeric_type
            returnType = Expressions.mathExpType(left, right);
        } else if (left instanceof AbstractMappingType.SqlLocalDateType || right instanceof AbstractMappingType.SqlLocalDateType) {
            if ((right instanceof AbstractMappingType.SqlIntegerType
                    && ((AbstractMappingType.SqlIntegerType) right).lengthType().compareWith(AbstractMappingType.LengthType.DEFAULT) <= 0)
                    || (left instanceof AbstractMappingType.SqlIntegerType
                    && ((AbstractMappingType.SqlIntegerType) left).lengthType().compareWith(AbstractMappingType.LengthType.DEFAULT) <= 0)) {  // date + integer → date
                returnType = left instanceof AbstractMappingType.SqlLocalDateType ? left : right;
            } else if (right instanceof AbstractMappingType.SqlIntervalType || right instanceof AbstractMappingType.SqlLocalTimeType
                    || left instanceof AbstractMappingType.SqlIntervalType || left instanceof AbstractMappingType.SqlLocalTimeType) { // date + interval → timestamp or date + time → timestamp
                returnType = LocalDateTimeType.INSTANCE;
            } else {
                returnType = StringType.INSTANCE;
            }
        } else if (left instanceof AbstractMappingType.SqlLocalDateTimeType || right instanceof AbstractMappingType.SqlLocalDateTimeType) { // timestamp + interval → timestamp
            if (right instanceof AbstractMappingType.SqlIntervalType || left instanceof AbstractMappingType.SqlIntervalType) {
                returnType = IntervalType.from(Interval.class);
            } else {
                returnType = StringType.INSTANCE;
            }
        } else if ((left instanceof AbstractMappingType.SqlLocalTimeType && right instanceof AbstractMappingType.SqlIntervalType)
                || (left instanceof AbstractMappingType.SqlIntervalType && right instanceof AbstractMappingType.SqlLocalTimeType)) { // time + interval → time
            returnType = LocalTimeType.INSTANCE;
        } else if (left instanceof AbstractMappingType.SqlTemporalAmountType && right instanceof AbstractMappingType.SqlTemporalAmountType) { // interval + interval → interval
            returnType = IntervalType.from(Interval.class);
        } else if ((left instanceof AbstractMappingType.SqlGeometryType && right instanceof AbstractMappingType.SqlPointType)
                || (right instanceof AbstractMappingType.SqlGeometryType && left instanceof AbstractMappingType.SqlPointType)) { // geometric_type + point → geometric_type
            returnType = right instanceof AbstractMappingType.SqlPointType ? left : right;
        } else if (left instanceof AbstractMappingType.SqlLineStringType && right instanceof AbstractMappingType.SqlLineStringType) { // path + path → path
            returnType = left;
        } else {
            returnType = StringType.INSTANCE;
        }
        return returnType;
    }

    /**
     * @see Postgres#minus(Expression, Expression)
     */
    static MappingType minusType(final MappingType left, final MappingType right) {
        final MappingType returnType;
        if (left instanceof AbstractMappingType.SqlNumberOrStringType && right instanceof AbstractMappingType.SqlNumberOrStringType) { // numeric_type - numeric_type → numeric_type
            returnType = Expressions.mathExpType(left, right);
        } else if (left instanceof AbstractMappingType.SqlLocalDateType) {
            if (right instanceof AbstractMappingType.SqlLocalDateType) { // date - date → integer
                returnType = IntegerType.INSTANCE;
            } else if (right instanceof AbstractMappingType.SqlIntegerType) { // date - integer → date
                returnType = left;
            } else if (right instanceof AbstractMappingType.SqlIntervalType) { // date - interval → timestamp
                returnType = LocalDateTimeType.INSTANCE;
            } else { // error or unknown
                returnType = StringType.INSTANCE;
            }
        } else if (left instanceof AbstractMappingType.SqlLocalTimeType || left instanceof AbstractMappingType.SqlOffsetTimeType) {
            if (right instanceof AbstractMappingType.SqlLocalTimeType || right instanceof AbstractMappingType.SqlOffsetTimeType) {  // time - time → interval
                returnType = IntervalType.from(Interval.class);
            } else if (right instanceof AbstractMappingType.SqlTemporalAmountType) { // time - interval → time
                returnType = left;
            } else { // error or unknown
                returnType = StringType.INSTANCE;
            }
        } else if (left instanceof AbstractMappingType.SqlLocalDateTimeType || left instanceof AbstractMappingType.SqlOffsetDateTimeType) {
            if (right instanceof AbstractMappingType.SqlLocalDateTimeType || right instanceof AbstractMappingType.SqlOffsetDateTimeType) {  // timestamp - timestamp → interval
                returnType = IntervalType.from(Interval.class);
            } else if (right instanceof AbstractMappingType.SqlTemporalAmountType) { // timestamp - interval → timestamp
                returnType = left;
            } else { // error or unknown
                returnType = StringType.INSTANCE;
            }
        } else if (left instanceof AbstractMappingType.SqlTemporalAmountType && right instanceof AbstractMappingType.SqlTemporalAmountType) { // interval - interval → interval
            returnType = IntervalType.from(Interval.class);
        } else if (left instanceof AbstractMappingType.SqlGeometryType && right instanceof AbstractMappingType.SqlPointType) { // geometric_type - point → geometric_type
            returnType = left;
        } else { // error or unknown
            returnType = StringType.INSTANCE;
        }
        return returnType;
    }

    /**
     * @see Postgres#times(Expression, Expression)
     */
    static MappingType timesType(final MappingType left, final MappingType right) {
        final MappingType returnType;
        if (left instanceof AbstractMappingType.SqlNumberOrStringType && right instanceof AbstractMappingType.SqlNumberOrStringType) { // numeric_type * numeric_type → numeric_type
            returnType = Expressions.mathExpType(left, right);
        } else if ((left instanceof AbstractMappingType.SqlTemporalAmountType || right instanceof AbstractMappingType.SqlTemporalAmountType)
                && (right instanceof AbstractMappingType.SqlNumberType || left instanceof AbstractMappingType.SqlNumberType)) {  // interval * double precision → interval
            returnType = IntervalType.from(Interval.class);
        } else if (left instanceof AbstractMappingType.SqlGeometryType && right instanceof AbstractMappingType.SqlPointType) { // geometric_type * point → geometric_type
            returnType = left;
        } else {
            returnType = StringType.INSTANCE;
        }
        return returnType;
    }

    /**
     * @see Postgres#divide(Expression, Expression)
     */
    static MappingType divideType(final MappingType left, final MappingType right) {
        final MappingType returnType;
        if (left instanceof AbstractMappingType.SqlNumberOrStringType && right instanceof AbstractMappingType.SqlNumberOrStringType) { // numeric_type / numeric_type → numeric_type
            returnType = Expressions.mathExpType(left, right);
        } else if (left instanceof AbstractMappingType.SqlTemporalAmountType && right instanceof AbstractMappingType.SqlNumberType) {  // interval / double precision → interval
            returnType = IntervalType.from(Interval.class);
        } else if (left instanceof AbstractMappingType.SqlGeometryType && right instanceof AbstractMappingType.SqlPointType) { // geometric_type / point → geometric_type
            returnType = left;
        } else {
            returnType = StringType.INSTANCE;
        }
        return returnType;
    }



    /*-------------------below private -------------------*/

    private static CriteriaException overlapsDontSupportMultiValue() {
        String m = "Postgre OVERLAPS operator don't support multi-value parameter/literal";
        return ContextStack.clearStackAndCriteriaError(m);
    }


    private static final class PeriodOverlapsPredicate extends OperationPredicate.CompoundPredicate
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


}
