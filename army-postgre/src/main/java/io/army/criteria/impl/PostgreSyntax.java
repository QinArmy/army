package io.army.criteria.impl;


import io.army.criteria.*;
import io.army.dialect._Constant;
import io.army.dialect._SqlContext;
import io.army.lang.Nullable;
import io.army.mapping.MappingType;
import io.army.meta.FieldMeta;
import io.army.util._Exceptions;
import io.army.util._StringUtils;

import java.util.Collection;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * <p>
 * Package class
 * </p>
 *
 * @since 1.0
 */
abstract class PostgreSyntax extends PostgreMiscellaneousFunctions {

    /**
     * Package constructor
     */
    PostgreSyntax() {
    }


    public interface Modifier extends Query.SelectModifier {

    }

    public interface WordDistinct extends Modifier, SqlSyntax.ArgDistinct {

    }

    public interface WordMaterialized extends SQLWords {

    }

    public interface _PeriodOverlapsClause {

        IPredicate overlaps(Expression start, Expression endOrLength);

        <T> IPredicate overlaps(Expression start, BiFunction<Expression, T, Expression> valueOperator, T value);

        <T> IPredicate overlaps(BiFunction<Expression, T, Expression> valueOperator, T value, Expression endOrLength);

        IPredicate overlaps(TypeInfer type, BiFunction<TypeInfer, Object, Expression> valueOperator, Object start, Object endOrLength);


    }

    private enum SelectModifier implements Modifier {

        ALL(" ALL");

        private final String spaceWord;

        SelectModifier(String spaceWord) {
            this.spaceWord = spaceWord;
        }


        @Override
        public final String render() {
            return this.spaceWord;
        }

        @Override
        public final String toString() {
            return keyWordToString(this);
        }


    }//SelectModifier


    private enum KeyWordDistinct implements WordDistinct {

        DISTINCT(" DISTINCT");

        private final String spaceWord;

        KeyWordDistinct(String spaceWord) {
            this.spaceWord = spaceWord;
        }


        @Override
        public final String render() {
            return this.spaceWord;
        }

        @Override
        public final String toString() {
            return keyWordToString(this);
        }
    }//KeyWordDistinct


    private enum KeyWordMaterialized implements WordMaterialized {

        MATERIALIZED(" MATERIALIZED"),
        NOT_MATERIALIZED(" NOT MATERIALIZED");

        private final String spaceWord;

        KeyWordMaterialized(String spaceWord) {
            this.spaceWord = spaceWord;
        }

        @Override
        public final String render() {
            return this.spaceWord;
        }


        @Override
        public final String toString() {
            return keyWordToString(this);
        }


    }//KeyWordMaterialized

    private enum FromNormalizedWord implements SQLsSyntax.BooleanTestWord {
        FROM_NORMALIZED(" FROM NORMALIZED"),
        NORMALIZED(" NORMALIZED");

        private final String spaceWords;

        FromNormalizedWord(String spaceWords) {
            this.spaceWords = spaceWords;
        }

        @Override
        public final String render() {
            return this.spaceWords;
        }


        @Override
        public String toString() {
            return keyWordToString(this);
        }


    }//FromNormalizedWord


    public static final Modifier ALL = SelectModifier.ALL;

    public static final WordDistinct DISTINCT = KeyWordDistinct.DISTINCT;

    public static final WordMaterialized MATERIALIZED = KeyWordMaterialized.MATERIALIZED;

    public static final WordMaterialized NOT_MATERIALIZED = KeyWordMaterialized.NOT_MATERIALIZED;

    public static final SQLsSyntax.BooleanTestWord FROM_NORMALIZED = FromNormalizedWord.FROM_NORMALIZED;

    public static final SQLsSyntax.BooleanTestWord NORMALIZED = FromNormalizedWord.NORMALIZED;


    public static Expression excluded(FieldMeta<?> field) {
        return ContextStack.peek().insertValueField(field, PostgreExcludedField::excludedField);
    }


    /**
     * @param expression couldn't be multi-value parameter/literal, for example {@link SQLs#multiParams(TypeInfer, Collection)}
     * @see <a href="https://www.postgresql.org/docs/15/sql-syntax-calling-funcs.html#SQL-SYNTAX-CALLING-FUNCS-POSITIONAL">Using Positional Notation</a>
     * @see <a href="https://www.postgresql.org/docs/15/sql-syntax-calling-funcs.html#SQL-SYNTAX-CALLING-FUNCS-NAMED">Using Named Notation</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-datetime.html#FUNCTIONS-DATETIME-TABLE">make_interval ( [ years int [, months int [, weeks int [, days int [, hours int [, mins int [, secs double precision ]]]]]]] ) → interval</a>
     */
    public static Expression namedNotation(String name, Expression expression) {
        return FunctionUtils.namedNotation(name, expression);
    }

    /**
     * @param valueOperator couldn't return multi-value parameter/literal, for example {@link SQLs#multiParams(TypeInfer, Collection)}
     * @see <a href="https://www.postgresql.org/docs/15/sql-syntax-calling-funcs.html#SQL-SYNTAX-CALLING-FUNCS-POSITIONAL">Using Positional Notation</a>
     * @see <a href="https://www.postgresql.org/docs/15/sql-syntax-calling-funcs.html#SQL-SYNTAX-CALLING-FUNCS-NAMED">Using Named Notation</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-datetime.html#FUNCTIONS-DATETIME-TABLE">make_interval ( [ years int [, months int [, weeks int [, days int [, hours int [, mins int [, secs double precision ]]]]]]] ) → interval</a>
     */
    public static <T> Expression namedNotation(String name, Function<T, Expression> valueOperator, T value) {
        return FunctionUtils.namedNotation(name, valueOperator.apply(value));
    }




    /*-------------------below operator method -------------------*/

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link  MappingType} of exp
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-OP-TABLE">Absolute value operator</a>
     */
    public static Expression at(Expression exp) {
        return Expressions.unaryExp(exp, UnaryOperator.AT);
    }


    /**
     * <p>
     * The {@link MappingType} of operator return type: the {@link  MappingType} of leftText.
     * </p>
     *
     * @param leftText not {@link SQLs#DEFAULT} etc.
     * @see Expression#apply(BiFunction, Expression)
     * @see Expression#apply(BiFunction, BiFunction, Object)
     * @see Postgres#startsWith(Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-OTHER">text ^@ text → boolean</a>
     */
    public static Expression caretAt(Expression leftText, Expression rightText) {
        return Expressions.dualExp((OperationExpression) leftText, DualOperator.CARET_AT, rightText);
    }

    /**
     * <p>
     * The {@link MappingType} of operator return type: the {@link  MappingType} of leftText.
     * </p>
     *
     * @param left  not {@link SQLs#DEFAULT} etc.
     * @param right not {@link SQLs#DEFAULT} etc.
     * @see Expression#apply(BiFunction, Expression)
     * @see Expression#apply(BiFunction, BiFunction, Object)
     * @see <a href="https://www.postgresql.org/docs/current/functions-bitstring.html#FUNCTIONS-BIT-STRING-OP-TABLE">bit || bit → bit</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-SQL">text || text → text <br/>
     * text || anynonarray → text <br/>
     * anynonarray || text → text
     * </a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-binarystring.html#FUNCTIONS-BINARYSTRING-SQL">bytea || bytea → bytea</a>
     */
    public static Expression doubleVertical(Expression left, Expression right) {
        return Expressions.dualExp((OperationExpression) left, DualOperator.DOUBLE_VERTICAL, right);
    }


    /**
     * <p>
     * OVERLAPS operator
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-datetime.html"> OVERLAPS operato</a>
     */
    public static _PeriodOverlapsClause period(final Expression start, final Expression endOrLength) {
        if (start instanceof SqlValueParam.MultiValue || endOrLength instanceof SqlValueParam.MultiValue) {
            throw overlapsDontSupportMultiValue();
        }
        return new PeriodOverlapsPredicate((ArmyExpression) start, (ArmyExpression) endOrLength);
    }


    /**
     * <p>
     * OVERLAPS operator
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-datetime.html"> OVERLAPS operato</a>
     */
    public static <T> _PeriodOverlapsClause period(Expression start, BiFunction<Expression, T, Expression> valueOperator, T value) {
        return period(start, valueOperator.apply(start, value));
    }

    /**
     * <p>
     * OVERLAPS operator
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-datetime.html"> OVERLAPS operato</a>
     */
    public static <T> _PeriodOverlapsClause period(BiFunction<Expression, T, Expression> valueOperator, T value, Expression endOrLength) {
        return period(valueOperator.apply(endOrLength, value), endOrLength);
    }

    /**
     * <p>
     * OVERLAPS operator
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-datetime.html"> OVERLAPS operato</a>
     */
    public static _PeriodOverlapsClause period(TypeInfer type, BiFunction<TypeInfer, Object, Expression> valueOperator, Object start, Object endOrLength) {
        return period(valueOperator.apply(type, start), valueOperator.apply(type, endOrLength));
    }





    /*-------------------below package method -------------------*/

    static String keyWordToString(Enum<?> keyWordEnum) {
        return _StringUtils.builder()
                .append(Postgres.class.getSimpleName())
                .append(_Constant.POINT)
                .append(keyWordEnum.name())
                .toString();
    }


    /*-------------------below private method -------------------*/


    private static CriteriaException overlapsDontSupportMultiValue() {
        String m = "Postgre OVERLAPS operator don't support multi-value parameter/literal";
        return ContextStack.clearStackAndCriteriaError(m);
    }


    private static final class PeriodOverlapsPredicate extends OperationPredicate
            implements _PeriodOverlapsClause {

        private final ArmyExpression start1;

        private final ArmyExpression endOrLength1;

        private ArmyExpression start2;

        private ArmyExpression endOrLength2;

        /**
         * @see #period(Expression, Expression)
         */
        private PeriodOverlapsPredicate(@Nullable ArmyExpression start1, @Nullable ArmyExpression endOrLength1) {
            if (start1 == null || endOrLength1 == null) {
                throw ContextStack.clearStackAndNullPointer();
            }
            this.start1 = start1;
            this.endOrLength1 = endOrLength1;
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
            this.start2 = (ArmyExpression) start;
            this.endOrLength2 = (ArmyExpression) endOrLength;
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
