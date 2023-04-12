package io.army.criteria.impl;


import io.army.criteria.*;
import io.army.dialect._Constant;
import io.army.mapping.*;
import io.army.meta.FieldMeta;
import io.army.util._StringUtils;

import java.util.Collection;
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
    public static Expression at(Expression operand) {
        return PostgreExpressions.unaryExp(UnaryOperator.AT, operand, operand.typeMeta());
    }


    /**
     * <p>
     * The {@link MappingType} of operator return type: the {@link  MappingType} of leftText.
     * </p>
     *
     * @param left not {@link SQLs#DEFAULT} etc.
     * @see Expression#apply(BiFunction, Expression)
     * @see Expression#apply(BiFunction, BiFunction, Object)
     * @see Postgres#startsWith(Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-OTHER">text ^@ text → boolean</a>
     */
    public static IPredicate caretAt(Expression left, Expression right) {
        return PostgreExpressions.dualPredicate(left, DualOperator.CARET_AT, right);
    }

    /**
     * <p>
     * The {@link MappingType} of operator return type:
     * <ol>
     *     <li>If the {@link MappingType} of left is text type(eg: {@link StringType}) or {@link BitSetType},then the {@link MappingType} of left</li>
     *     <li>Else {@link StringType}</li>
     * </ol>
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
    public static Expression verticals(final Expression left, final Expression right) {
        return PostgreExpressions.dualExp(left, DualOperator.DOUBLE_VERTICAL, right,
                _returnType(left, PostgreSyntax::doubleVerticalType)
        );
    }


    /**
     * <p>
     * OVERLAPS operator
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-datetime.html"> OVERLAPS operato</a>
     */
    public static _PeriodOverlapsClause period(final Expression start, final Expression endOrLength) {
        return PostgreExpressions.overlaps(start, endOrLength);
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


    /**
     * <p>
     * AT TIME ZONE operator,The {@link MappingType} of operator return type:
     *     <ol>
     *         <li>If The {@link MappingType} of source is {@link LocalDateTimeType},then {@link OffsetDateTimeType}</li>
     *         <li>If The {@link MappingType} of source is {@link OffsetDateTimeType} or {@link ZonedDateTimeType},then {@link LocalDateTimeType}</li>
     *         <li>If The {@link MappingType} of source is {@link LocalTimeType},then {@link OffsetTimeType}</li>
     *         <li>If The {@link MappingType} of source is {@link OffsetTimeType},then {@link LocalTimeType}</li>
     *         <li>Else raise {@link CriteriaException}</li>
     *     </ol>
     * </p>
     *
     * @param source non-multi value parameter/literal
     * @param zone   non-multi value parameter/literal
     * @throws CriteriaException throw when <ul>
     *                           <li>source is multi value parameter/literal</li>
     *                           <li>zone is multi value parameter/literal</li>
     *                           <li>The {@link MappingType} of source error</li>
     *                           </ul>
     * @see <a href="https://www.postgresql.org/docs/current/functions-datetime.html#FUNCTIONS-DATETIME-ZONECONVERT-TABLE"> AT TIME ZONE Variants</a>
     */
    public static Expression atTimeZone(final Expression source, final Expression zone) {
        return PostgreExpressions.dualExp(source, DualOperator.AT_TIME_ZONE, zone,
                _returnType(source, PostgreSyntax::atTimeZoneType)
        );
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


    /**
     * @see #atTimeZone(Expression, Expression)
     */
    private static MappingType atTimeZoneType(final MappingType type) {
        final MappingType returnType;
        if (type instanceof LocalDateTimeType) {
            returnType = OffsetDateTimeType.INSTANCE;
        } else if (type instanceof OffsetDateTimeType || type instanceof ZonedDateTimeType) {
            returnType = LocalDateTimeType.INSTANCE;
        } else if (type instanceof LocalTimeType) {
            returnType = OffsetTimeType.INSTANCE;
        } else if (type instanceof OffsetTimeType) {
            returnType = LocalTimeType.INSTANCE;
        } else {
            String m = String.format("AT TIME ZONE operator don't support %s", type);
            throw ContextStack.clearStackAndCriteriaError(m);
        }
        return returnType;
    }

    /**
     * @see #verticals(Expression, Expression)
     */
    private static MappingType doubleVerticalType(final MappingType type) {
        final MappingType returnType;
        if (type instanceof _SQLStringType || type instanceof BitSetType) {
            returnType = type;
        } else {
            returnType = StringType.INSTANCE;
        }
        return returnType;
    }


}
