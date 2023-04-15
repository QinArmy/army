package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.dialect.SubQuery;
import io.army.criteria.dialect.VarExpression;
import io.army.criteria.standard.SQLFunction;
import io.army.dialect._Constant;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.meta.TypeMeta;
import io.army.util._StringUtils;

import java.util.BitSet;
import java.util.Collection;
import java.util.Map;
import java.util.function.Supplier;

/**
 * <p>
 * Package class,this class is base class of {@link SQLs}.
 * </p>
 *
 * @see SQLs
 * @since 1.0
 */
abstract class SQLsSyntax extends Functions {


    /**
     * package constructor
     */
    SQLsSyntax() {
    }


    public interface Modifier extends Query.SelectModifier {

    }

    public interface BooleanTestWord extends SQLWords {

    }

    public interface IsComparisonWord extends SQLWords {

    }

    public interface BetweenModifier extends SQLWords {

    }


    public interface WordAll extends Modifier {

    }

    public interface WordDistinct extends Modifier, SqlSyntax.ArgDistinct {

    }

    public interface WordInterval extends SQLWords {

    }

    public interface WordPercent {

    }

    public interface WordOnly extends Query.TableModifier, Query.FetchOnlyWithTies, SQLWords {

    }

    public interface WordFirst extends Query.FetchFirstNext {

    }


    public interface WordNext extends Query.FetchFirstNext {

    }

    public interface WordRow extends Query.FetchRow {

    }

    public interface WordRows extends Query.FetchRow {

    }

    public interface WordLateral extends Query.DerivedModifier {

    }

    public interface WordsWithTies extends Query.FetchOnlyWithTies {

    }


    private enum KeyWordAll implements SQLWords, WordAll {

        ALL(" ALL");

        private final String spaceWord;

        KeyWordAll(String spaceWord) {
            this.spaceWord = spaceWord;
        }

        @Override
        public final String spaceRender() {
            return this.spaceWord;
        }

        @Override
        public final String toString() {
            return sqlKeyWordsToString(this);
        }


    }//KeyWordAll


    private enum KeyWordDistinct implements SQLWords, WordDistinct {

        DISTINCT(" DISTINCT");

        private final String spaceWord;

        KeyWordDistinct(String spaceWord) {
            this.spaceWord = spaceWord;
        }

        @Override
        public final String spaceRender() {
            return this.spaceWord;
        }

        @Override
        public final String toString() {
            return sqlKeyWordsToString(this);
        }


    }//KeyWordDistinct


    private enum KeyWordInterval implements WordInterval {

        INTERVAL(" INTERVAL");

        private final String spaceWord;

        KeyWordInterval(String spaceWord) {
            this.spaceWord = spaceWord;
        }

        @Override
        public final String spaceRender() {
            return this.spaceWord;
        }

        @Override
        public final String toString() {
            return sqlKeyWordsToString(this);
        }

    }//KeyWordInterval

    private enum KeyWordPercent implements WordPercent, SQLWords {

        PERCENT(" PERCENT");

        private final String spaceWord;

        KeyWordPercent(String spaceWord) {
            this.spaceWord = spaceWord;
        }

        @Override
        public final String spaceRender() {
            return this.spaceWord;
        }

        @Override
        public final String toString() {
            return sqlKeyWordsToString(this);
        }

    }//KeyWordPercent

    private enum KeyWordUnknown implements BooleanTestWord, Functions.ArmyKeyWord {

        UNKNOWN(" UNKNOWN");

        private final String spaceWord;

        KeyWordUnknown(String spaceWord) {
            this.spaceWord = spaceWord;
        }

        @Override
        public final String spaceRender() {
            return this.spaceWord;
        }

        @Override
        public final String toString() {
            return sqlKeyWordsToString(this);
        }

    } //KeyWordUnknown

    private enum KeyWordAscDesc implements Statement.AscDesc, SQLWords {

        ASC(" ASC"),
        DESC(" DESC");

        final String spaceWord;

        KeyWordAscDesc(String spaceWord) {
            this.spaceWord = spaceWord;
        }

        @Override
        public final String spaceRender() {
            return this.spaceWord;
        }

        @Override
        public final String toString() {
            return sqlKeyWordsToString(this);
        }

    }//KeyWordAscDesc

    private enum KeyWordLateral implements WordLateral {

        LATERAL(" LATERAL");

        private final String spaceWord;

        KeyWordLateral(String spaceWord) {
            this.spaceWord = spaceWord;
        }

        @Override
        public final String spaceRender() {
            return this.spaceWord;
        }

        @Override
        public final String toString() {
            return sqlKeyWordsToString(this);
        }

    }//KeyWordLateral

    private enum KeyWordFirst implements WordFirst, SQLWords {

        FIRST(" FIRST");

        private final String spaceWord;

        KeyWordFirst(String spaceWord) {
            this.spaceWord = spaceWord;
        }

        @Override
        public final String spaceRender() {
            return this.spaceWord;
        }

        @Override
        public final String toString() {
            return sqlKeyWordsToString(this);
        }

    }//KeyWordFirst

    private enum KeyWordNext implements WordNext, SQLWords {

        NEXT(" NEXT");

        private final String spaceWord;

        KeyWordNext(String spaceWord) {
            this.spaceWord = spaceWord;
        }

        @Override
        public final String spaceRender() {
            return this.spaceWord;
        }

        @Override
        public final String toString() {
            return sqlKeyWordsToString(this);
        }

    }//KeyWordNext


    private enum KeyWordsNullsFirstLast implements Statement.NullsFirstLast, SQLWords {

        NULLS_FIRST(" NULLS FIRST"),
        NULLS_LAST(" NULLS LAST");

        final String spaceWords;

        KeyWordsNullsFirstLast(String spaceWords) {
            this.spaceWords = spaceWords;
        }

        @Override
        public final String spaceRender() {
            return this.spaceWords;
        }

        @Override
        public final String toString() {
            return sqlKeyWordsToString(this);
        }

    }//KeyWordsNullsFirstLast


    private enum KeyWordRow implements WordRow, SQLWords {

        ROW(" ROW");

        private final String spaceWord;

        KeyWordRow(String spaceWord) {
            this.spaceWord = spaceWord;
        }

        @Override
        public final String spaceRender() {
            return this.spaceWord;
        }

        @Override
        public final String toString() {
            return sqlKeyWordsToString(this);
        }

    }//KeyWordRow

    private enum KeyWordRows implements WordRows, SQLWords {

        ROWS(" ROWS");

        private final String spaceWord;

        KeyWordRows(String spaceWord) {
            this.spaceWord = spaceWord;
        }

        @Override
        public final String spaceRender() {
            return this.spaceWord;
        }

        @Override
        public final String toString() {
            return sqlKeyWordsToString(this);
        }

    }//KeyWordRows

    private enum KeyWordWithTies implements WordsWithTies, SQLWords {

        WITH_TIES(" WITH TIES");

        private final String spaceWords;

        KeyWordWithTies(String spaceWords) {
            this.spaceWords = spaceWords;
        }

        @Override
        public final String spaceRender() {
            return this.spaceWords;
        }

        @Override
        public final String toString() {
            return sqlKeyWordsToString(this);
        }

    }//KeyWordNext

    private enum KeyWordOny implements WordOnly, SQLWords {

        ONLY(" ONLY");

        private final String spaceWord;

        KeyWordOny(String spaceWord) {
            this.spaceWord = spaceWord;
        }

        @Override
        public final String spaceRender() {
            return this.spaceWord;
        }

        @Override
        public final String toString() {
            return sqlKeyWordsToString(this);
        }

    }//KeyWordOny


    public static final WordAll ALL = KeyWordAll.ALL;

    public static final WordDistinct DISTINCT = KeyWordDistinct.DISTINCT;

    public static final WordLateral LATERAL = KeyWordLateral.LATERAL;

    public static final WordFirst FIRST = KeyWordFirst.FIRST;

    public static final WordNext NEXT = KeyWordNext.NEXT;

    public static final WordPercent PERCENT = KeyWordPercent.PERCENT;

    public static final Statement.NullsFirstLast NULLS_FIRST = KeyWordsNullsFirstLast.NULLS_FIRST;

    public static final Statement.NullsFirstLast NULLS_LAST = KeyWordsNullsFirstLast.NULLS_LAST;

    public static final WordOnly ONLY = KeyWordOny.ONLY;

    public static final WordRow ROW = KeyWordRow.ROW;

    public static final WordRows ROWS = KeyWordRows.ROWS;

    public static final WordInterval INTERVAL = KeyWordInterval.INTERVAL;

    public static final WordsWithTies WITH_TIES = KeyWordWithTies.WITH_TIES;

    public static final BooleanTestWord UNKNOWN = KeyWordUnknown.UNKNOWN;

    /**
     * package field
     */
    static final Statement.AscDesc ASC = KeyWordAscDesc.ASC;

    /**
     * package field
     */
    static final Statement.AscDesc DESC = KeyWordAscDesc.DESC;


    /**
     * <p>
     * Value must be below types:
     *     <ul>
     *         <li>{@link Boolean}</li>
     *         <li>{@link String}</li>
     *         <li>{@link Integer}</li>
     *         <li>{@link Long}</li>
     *         <li>{@link Short}</li>
     *         <li>{@link Byte}</li>
     *         <li>{@link Double}</li>
     *         <li>{@link Float}</li>
     *         <li>{@link java.math.BigDecimal}</li>
     *         <li>{@link java.math.BigInteger}</li>
     *         <li>{@code  byte[]}</li>
     *         <li>{@link BitSet}</li>
     *         <li>{@link io.army.struct.CodeEnum}</li>
     *         <li>{@link io.army.struct.TextEnum}</li>
     *         <li>{@link java.time.LocalTime}</li>
     *         <li>{@link java.time.LocalDate}</li>
     *         <li>{@link java.time.LocalDateTime}</li>
     *         <li>{@link java.time.OffsetDateTime}</li>
     *         <li>{@link java.time.ZonedDateTime}</li>
     *         <li>{@link java.time.OffsetTime}</li>
     *         <li>{@link java.time.ZoneId}</li>
     *         <li>{@link java.time.Month}</li>
     *         <li>{@link java.time.DayOfWeek}</li>
     *         <li>{@link java.time.Year}</li>
     *         <li>{@link java.time.YearMonth}</li>
     *         <li>{@link java.time.MonthDay}</li>
     *     </ul>
     * </p>
     *
     * @param value non null
     * @return parameter expression
     * @see #literalFrom(Object)
     */
    public static Expression paramFrom(final Object value) {
        return SingleParamExpression.from(value);
    }


    /**
     * <p>
     * Create parameter expression, parameter expression output parameter placeholder({@code ?})
     * </p>
     *
     * @param value nullable,if value is instance of {@link Supplier},then {@link Supplier#get()} will be invoked.
     * @see #param(TypeInfer, Object)
     * @see #literal(TypeInfer, Object)
     */
    public static Expression param(final TypeInfer type, final @Nullable Object value) {
        final Expression result;
        if (value instanceof Supplier) {
            result = SingleParamExpression.single(type, ((Supplier<?>) value).get());
        } else {
            result = SingleParamExpression.single(type, value);
        }
        return result;
    }


    /**
     * <p>
     * Create multi parameter expression, multi parameter expression will output multi parameter placeholders like below:
     * ? , ? , ? ...
     * but as right operand of  IN(or NOT IN) operator, will output (  ? , ? , ? ... )
     * </p>
     *
     * @param type   non-null,the type of element of values.
     * @param values non-null and non-empty
     * @see #multiParam(TypeInfer, Collection)
     * @see #multiLiteral(TypeInfer, Collection)
     */
    public static Expression multiParam(final TypeInfer type, final Collection<?> values) {
        return MultiParamExpression.multi(type, values);
    }


    /**
     * <p>
     * Create named non-null parameter expression for batch update(delete) and values insert.
     * </p>
     *
     * @see #namedParam(TypeInfer, String)
     * @see #namedNullableParam(TypeInfer, String)
     * @see #namedLiteral(TypeInfer, String)
     * @see #namedNullableLiteral(TypeInfer, String)
     */
    public static Expression namedParam(final TypeInfer type, final String name) {
        return SingleParamExpression.named(type, name);
    }

    /**
     * <p>
     * Create named non-null parameter expression for batch update(delete) and values insert.
     * </p>
     *
     * @see #namedParam(TypeInfer, String)
     * @see #namedNullableParam(TypeInfer, String)
     * @see #namedLiteral(TypeInfer, String)
     * @see #namedNullableLiteral(TypeInfer, String)
     */
    public static Expression namedNullableParam(final TypeInfer type, final String name) {
        return SingleParamExpression.namedNullable(type, name);
    }


    /**
     * <p>
     * Create named non-null multi parameter expression, multi parameter expression will output multi parameter placeholders like below:
     * ? , ? , ? ...
     * but as right operand of  IN(or NOT IN) operator, will output (  ? , ? , ? ... )
     * </p>
     * <p>
     * Named multi parameter expression is used in batch update(or delete) and values insert.
     * </p>
     *
     * @param type non-null,the type of element of {@link Collection}
     * @param name non-null,the key name of {@link Map} or the field name of java bean.
     * @param size positive,the size of {@link Collection}
     * @return named non-null multi parameter expression
     * @see #namedMultiParam(TypeInfer, String, int)
     * @see #namedMultiLiteral(TypeInfer, String, int)
     */
    public static Expression namedMultiParam(final TypeInfer type, final String name, final int size) {
        return MultiParamExpression.named(type, name, size);
    }


    /**
     * <p>
     * Value must be below types:
     *     <ul>
     *         <li>{@link Boolean}</li>
     *         <li>{@link String}</li>
     *         <li>{@link Integer}</li>
     *         <li>{@link Long}</li>
     *         <li>{@link Short}</li>
     *         <li>{@link Byte}</li>
     *         <li>{@link Double}</li>
     *         <li>{@link Float}</li>
     *         <li>{@link java.math.BigDecimal}</li>
     *         <li>{@link java.math.BigInteger}</li>
     *         <li>{@code  byte[]}</li>
     *         <li>{@link BitSet}</li>
     *         <li>{@link io.army.struct.CodeEnum}</li>
     *         <li>{@link io.army.struct.TextEnum}</li>
     *         <li>{@link java.time.LocalTime}</li>
     *         <li>{@link java.time.LocalDate}</li>
     *         <li>{@link java.time.LocalDateTime}</li>
     *         <li>{@link java.time.OffsetDateTime}</li>
     *         <li>{@link java.time.ZonedDateTime}</li>
     *         <li>{@link java.time.OffsetTime}</li>
     *         <li>{@link java.time.ZoneId}</li>
     *         <li>{@link java.time.Month}</li>
     *         <li>{@link java.time.DayOfWeek}</li>
     *         <li>{@link java.time.Year}</li>
     *         <li>{@link java.time.YearMonth}</li>
     *         <li>{@link java.time.MonthDay}</li>
     *     </ul>
     * </p>
     *
     * @param value non null
     * @return literal expression
     * @see #paramFrom(Object)
     */
    public static Expression literalFrom(final Object value) {
        return SingleLiteralExpression.from(value);
    }


    /**
     * <p>
     * Create literal expression,literal expression will output literal of value
     * </p>
     *
     * @param type  non-null
     * @param value nullable,if value is instance of {@link Supplier},then {@link Supplier#get()} will invoked.
     * @see #param(TypeInfer, Object)
     * @see #literal(TypeInfer, Object)
     */
    public static Expression literal(final TypeInfer type, final @Nullable Object value) {
        final Expression result;
        if (value instanceof Supplier) {
            result = SingleLiteralExpression.single(type, ((Supplier<?>) value).get());
        } else {
            result = SingleLiteralExpression.single(type, value);
        }
        return result;
    }


    /**
     * <p>
     * Create multi literal expression, multi literal expression will output multi LITERAL like below:
     * LITERAL , LITERAL , LITERAL ...
     * but as right operand of  IN(or NOT IN) operator, will output (  LITERAL , LITERAL , LITERAL ... )
     * </p>
     *
     * @param type   non-null,the type of element of values.
     * @param values non-null and non-empty
     * @see #multiParam(TypeInfer, Collection)
     * @see #multiLiteral(TypeInfer, Collection)
     */
    public static Expression multiLiteral(final TypeInfer type, final Collection<?> values) {
        return MultiLiteralExpression.multi(type, values);
    }


    /**
     * <p>
     * Create named non-null literal expression. This expression can only be used in values insert statement.
     * </p>
     * <p>
     * Note: this method couldn't be used in batch update(delete) statement.
     * </p>
     *
     * @param type non-null
     * @param name non-null and non-empty
     * @return non-null named literal expression
     * @see #namedParam(TypeInfer, String)
     * @see #namedNullableParam(TypeInfer, String)
     * @see #namedLiteral(TypeInfer, String)
     * @see #namedNullableLiteral(TypeInfer, String)
     */
    public static Expression namedLiteral(final TypeInfer type, final String name) {
        return SingleLiteralExpression.named(type, name);
    }

    /**
     * <p>
     * Create named non-null literal expression. This expression can only be used in values insert statement.
     * </p>
     * <p>
     * Note: this method couldn't be used in batch update(delete) statement.
     * </p>
     *
     * @param type non-null
     * @param name non-null and non-empty
     * @return non-null named literal expression
     * @see #namedParam(TypeInfer, String)
     * @see #namedNullableParam(TypeInfer, String)
     * @see #namedLiteral(TypeInfer, String)
     * @see #namedNullableLiteral(TypeInfer, String)
     */
    public static Expression namedNullableLiteral(final TypeInfer type, final String name) {
        return SingleLiteralExpression.namedNullable(type, name);
    }


    /**
     * <p>
     * Create named non-null multi literal expression, multi literal expression will output multi LITERAL like below:
     * LITERAL , LITERAL , LITERAL ...
     * but as right operand of  IN(or NOT IN) operator, will output (  LITERAL , LITERAL , LITERAL ... )
     * </p>
     * <p>
     * This expression can only be used in values insert statement,this method couldn't be used in batch update(delete) statement.
     * </p>
     *
     * @param type non-null,the type of element of {@link Collection}
     * @param name non-null,the key name of {@link Map} or the field name of java bean.
     * @param size positive,the size of {@link Collection}
     * @return named non-null multi literal expression
     * @see #namedMultiParam(TypeInfer, String, int)
     * @see #namedMultiLiteral(TypeInfer, String, int)
     */
    public static Expression namedMultiLiteral(final TypeInfer type, final String name, final int size) {
        return MultiLiteralExpression.named(type, name, size);
    }


    /**
     * <p>
     * Get a {@link QualifiedField}. You don't need a {@link QualifiedField},if no self-join in statement.
     * </p>
     */
    public static <T> QualifiedField<T> field(String tableAlias, FieldMeta<T> field) {
        return ContextStack.peek().field(tableAlias, field);
    }

    public static DerivedField refThis(String derivedAlias, String selectionName) {
        return ContextStack.peek().refThis(derivedAlias, selectionName);
    }

    public static DerivedField refOuter(String derivedAlias, String selectionName) {
        return ContextStack.peek().refOuter(derivedAlias, selectionName);
    }


    /**
     * <p>
     * Reference a {@link  Selection} of current statement after selection list end,eg: ORDER BY clause.
     * </p>
     */
    public static Expression ref(String selectionAlias) {
        return ContextStack.peek().refSelection(selectionAlias);
    }

    /**
     * <p>
     * Reference a {@link  Selection} of current statement after selection list end,eg: ORDER BY clause.
     * </p>
     *
     * @param selectionOrdinal based 1 .
     */
    public static Expression ref(int selectionOrdinal) {
        return ContextStack.peek().refSelection(selectionOrdinal);
    }

    /**
     * <p>
     * Reference session variable.
     * </p>
     *
     * @throws CriteriaException when var not exists
     */
    public static VarExpression var(String varName) {
        return ContextStack.root().var(varName);
    }


    /**
     * <p>
     * Create session variable.
     * </p>
     *
     * @throws CriteriaException when var exists.
     */
    public static VarExpression createVar(String varName, TypeMeta paramMeta)
            throws CriteriaException {
        return ContextStack.root().createVar(varName, paramMeta);
    }

    public static Expression parens(Expression expression) {
        return Expressions.bracketExp(expression);
    }

    public static IPredicate bracket(IPredicate predicate) {
        return Expressions.bracketPredicate(predicate);
    }

    public static Expression bitwiseNot(Expression exp) {
        return Expressions.unaryExp(UnaryOperator.BITWISE_NOT, exp);
    }

    public static Expression negate(Expression exp) {
        return Expressions.unaryExp(UnaryOperator.NEGATE, exp);
    }

    public static IPredicate not(IPredicate predicate) {
        return Expressions.notPredicate(predicate);
    }


    public static SQLFunction._CaseFuncWhenClause cases() {
        return FunctionUtils.caseFunction(null);
    }


    /*################################## blow sql key word operate method ##################################*/

    /**
     * @param subQuery non-null
     */
    public static IPredicate exists(SubQuery subQuery) {
        return Expressions.existsPredicate(UnaryOperator.EXISTS, subQuery);
    }

    /**
     * @param subQuery non-null
     */
    public static IPredicate notExists(SubQuery subQuery) {
        return Expressions.existsPredicate(UnaryOperator.NOT_EXISTS, subQuery);
    }

    public static ItemPair plusEqual(final DataField field, final Expression value) {
        return SQLs._itemPair(field, AssignOperator.PLUS_EQUAL, value);
    }

    public static ItemPair minusEqual(final DataField field, final Expression value) {
        return SQLs._itemPair(field, AssignOperator.MINUS_EQUAL, value);
    }


//    public static <I extends Item, R extends Expression> SQLFunction._CaseFuncWhenClause<R> Case(
//            Function<_ItemExpression<I>, R> endFunc, Function<TypeInfer, I> asFunc) {
//        return FunctionUtils.caseFunction(null, endFunc, asFunc);
//    }




    /*-------------------below package method -------------------*/



    /*-------------------below private method-------------------*/

    static String sqlKeyWordsToString(Enum<?> wordEnum) {
        return _StringUtils.builder()
                .append(SQLs.class.getSimpleName())
                .append(_Constant.POINT)
                .append(wordEnum.name())
                .toString();
    }


}
