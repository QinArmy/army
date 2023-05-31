package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.dialect.SubQuery;
import io.army.criteria.standard.SQLFunction;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.meta.TypeMeta;

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
            return SQLs.sqlKeyWordsToString(this);
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
            return SQLs.sqlKeyWordsToString(this);
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
            return SQLs.sqlKeyWordsToString(this);
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
            return SQLs.sqlKeyWordsToString(this);
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
            return SQLs.sqlKeyWordsToString(this);
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
            return SQLs.sqlKeyWordsToString(this);
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
            return SQLs.sqlKeyWordsToString(this);
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
            return SQLs.sqlKeyWordsToString(this);
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
            return SQLs.sqlKeyWordsToString(this);
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
            return SQLs.sqlKeyWordsToString(this);
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
            return SQLs.sqlKeyWordsToString(this);
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
            return SQLs.sqlKeyWordsToString(this);
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
            return SQLs.sqlKeyWordsToString(this);
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
            return SQLs.sqlKeyWordsToString(this);
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
     * @see #literalValue(Object)
     */
    public static SimpleExpression paramValue(final Object value) {
        return SingleParamExpression.from(value);
    }


    /**
     * <p>
     * Create parameter expression, parameter expression output parameter placeholder({@code ?})
     * </p>
     *
     * @param value nullable,if value is instance of {@link Supplier},then {@link Supplier#get()} will be invoked.
     * @throws io.army.criteria.CriteriaException throw when infer is codec {@link FieldMeta}.
     * @see #param(TypeInfer, Object)
     * @see #literal(TypeInfer, Object)
     */
    public static SimpleExpression param(final TypeInfer type, final @Nullable Object value) {
        final SimpleExpression result;
        if (value instanceof Supplier) {
            result = SingleParamExpression.single(type, ((Supplier<?>) value).get());
        } else {
            result = SingleParamExpression.single(type, value);
        }
        return result;
    }

    /**
     * <p>
     * Create encoding parameter expression, parameter expression output parameter placeholder({@code ?})
     * </p>
     *
     * @param value nullable,if value is instance of {@link Supplier},then {@link Supplier#get()} will be invoked.
     * @see #param(TypeInfer, Object)
     * @see #literal(TypeInfer, Object)
     */
    public static SimpleExpression encodingParam(final TypeInfer type, final @Nullable Object value) {
        final SimpleExpression result;
        if (value instanceof Supplier) {
            result = SingleParamExpression.encodingSingle(type, ((Supplier<?>) value).get());
        } else {
            result = SingleParamExpression.encodingSingle(type, value);
        }
        return result;
    }


    /**
     * <p>
     * Create named non-null parameter expression for batch update(delete) and values insert.
     * </p>
     *
     * @throws CriteriaException throw when <ul>
     *                           <li>infer is codec {@link FieldMeta}.</li>
     *                           <li>name have no text</li>
     *                           </ul>
     * @see #namedNullableParam(TypeInfer, String)
     * @see #namedLiteral(TypeInfer, String)
     * @see #namedNullableLiteral(TypeInfer, String)
     * @see #encodingNamedParam(TypeInfer, String)
     * @see #encodingNamedNullableParam(TypeInfer, String)
     * @see #encodingNamedLiteral(TypeInfer, String)
     * @see #encodingNamedNullableLiteral(TypeInfer, String)
     */
    public static SimpleExpression namedParam(final TypeInfer type, final String name) {
        return SingleParamExpression.named(type, name);
    }

    /**
     * <p>
     * Create named non-null parameter expression for batch update(delete) and values insert.
     * </p>
     *
     * @throws CriteriaException throw when <ul>
     *                           <li>infer isn't codec {@link FieldMeta}.</li>
     *                           <li>name have no text</li>
     *                           </ul>
     * @see #namedParam(TypeInfer, String)
     * @see #namedNullableParam(TypeInfer, String)
     * @see #namedLiteral(TypeInfer, String)
     * @see #namedNullableLiteral(TypeInfer, String)
     * @see #encodingNamedNullableParam(TypeInfer, String)
     * @see #encodingNamedLiteral(TypeInfer, String)
     * @see #encodingNamedNullableLiteral(TypeInfer, String)
     */
    public static SimpleExpression encodingNamedParam(final TypeInfer type, final String name) {
        return SingleParamExpression.encodingNamed(type, name);
    }

    /**
     * <p>
     * Create named non-null parameter expression for batch update(delete) and values insert.
     * </p>
     *
     * @throws CriteriaException throw when <ul>
     *                           <li>infer is codec {@link FieldMeta}.</li>
     *                           <li>name have no text</li>
     *                           </ul>
     * @see #namedParam(TypeInfer, String)
     * @see #namedLiteral(TypeInfer, String)
     * @see #namedNullableLiteral(TypeInfer, String)
     * @see #encodingNamedParam(TypeInfer, String)
     * @see #encodingNamedNullableParam(TypeInfer, String)
     * @see #encodingNamedLiteral(TypeInfer, String)
     * @see #encodingNamedNullableLiteral(TypeInfer, String)
     */
    public static SimpleExpression namedNullableParam(final TypeInfer type, final String name) {
        return SingleParamExpression.namedNullable(type, name);
    }

    /**
     * <p>
     * Create named non-null parameter expression for batch update(delete) and values insert.
     * </p>
     *
     * @throws CriteriaException throw when <ul>
     *                           <li>infer isn't codec {@link FieldMeta}.</li>
     *                           <li>name have no text</li>
     *                           </ul>
     * @see #namedParam(TypeInfer, String)
     * @see #namedNullableParam(TypeInfer, String)
     * @see #namedLiteral(TypeInfer, String)
     * @see #namedNullableLiteral(TypeInfer, String)
     * @see #encodingNamedParam(TypeInfer, String)
     * @see #encodingNamedLiteral(TypeInfer, String)
     * @see #encodingNamedNullableLiteral(TypeInfer, String)
     */
    public static SimpleExpression encodingNamedNullableParam(final TypeInfer type, final String name) {
        return SingleParamExpression.encodingNamedNullable(type, name);
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
     * @see #paramValue(Object)
     */
    public static SimpleExpression literalValue(final Object value) {
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
    public static SimpleExpression literal(final TypeInfer type, final @Nullable Object value) {
        final SimpleExpression result;
        if (value instanceof Supplier) {
            result = SingleLiteralExpression.single(type, ((Supplier<?>) value).get());
        } else {
            result = SingleLiteralExpression.single(type, value);
        }
        return result;
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
    public static SimpleExpression encodingLiteral(final TypeInfer type, final @Nullable Object value) {
        final SimpleExpression result;
        if (value instanceof Supplier) {
            result = SingleLiteralExpression.encodingSingle(type, ((Supplier<?>) value).get());
        } else {
            result = SingleLiteralExpression.encodingSingle(type, value);
        }
        return result;
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
     * @throws CriteriaException throw when <ul>
     *                           <li>infer is codec {@link TableField}.</li>
     *                           <li>name have no text</li>
     *                           </ul>
     * @see #namedParam(TypeInfer, String)
     * @see #namedNullableParam(TypeInfer, String)
     * @see #namedNullableLiteral(TypeInfer, String)
     * @see #encodingNamedParam(TypeInfer, String)
     * @see #encodingNamedNullableParam(TypeInfer, String)
     * @see #encodingNamedLiteral(TypeInfer, String)
     * @see #encodingNamedNullableLiteral(TypeInfer, String)
     */
    public static SimpleExpression namedLiteral(final TypeInfer type, final String name) {
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
     * @throws CriteriaException throw when <ul>
     *                           <li>infer isn't codec {@link TableField}.</li>
     *                           <li>name have no text</li>
     *                           </ul>
     * @see #namedParam(TypeInfer, String)
     * @see #namedNullableParam(TypeInfer, String)
     * @see #namedLiteral(TypeInfer, String)
     * @see #namedNullableLiteral(TypeInfer, String)
     * @see #encodingNamedParam(TypeInfer, String)
     * @see #encodingNamedNullableParam(TypeInfer, String)
     * @see #encodingNamedNullableLiteral(TypeInfer, String)
     */
    public static SimpleExpression encodingNamedLiteral(final TypeInfer type, final String name) {
        return SingleLiteralExpression.encodingNamed(type, name);
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
     * @throws CriteriaException throw when <ul>
     *                           <li>infer is codec {@link FieldMeta}.</li>
     *                           <li>name have no text</li>
     *                           </ul>
     * @see #namedParam(TypeInfer, String)
     * @see #namedNullableParam(TypeInfer, String)
     * @see #namedLiteral(TypeInfer, String)
     * @see #encodingNamedParam(TypeInfer, String)
     * @see #encodingNamedNullableParam(TypeInfer, String)
     * @see #encodingNamedLiteral(TypeInfer, String)
     * @see #encodingNamedNullableLiteral(TypeInfer, String)
     */
    public static SimpleExpression namedNullableLiteral(final TypeInfer type, final String name) {
        return SingleLiteralExpression.namedNullable(type, name);
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
     * @throws CriteriaException throw when <ul>
     *                           <li>infer isn't codec {@link TableField}.</li>
     *                           <li>name have no text</li>
     *                           </ul>
     * @see #namedParam(TypeInfer, String)
     * @see #namedNullableParam(TypeInfer, String)
     * @see #namedLiteral(TypeInfer, String)
     * @see #namedNullableLiteral(TypeInfer, String)
     * @see #encodingNamedParam(TypeInfer, String)
     * @see #encodingNamedNullableParam(TypeInfer, String)
     * @see #encodingNamedLiteral(TypeInfer, String)
     */
    public static SimpleExpression encodingNamedNullableLiteral(final TypeInfer type, final String name) {
        return SingleLiteralExpression.encodingNamedNullable(type, name);
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
     * @throws CriteriaException throw when <ul>
     *                           <li>values is empty</li>
     *                           <li>infer return codec {@link TableField}</li>
     *                           </ul>
     * @see #multiParam(TypeInfer, Collection)
     * @see #multiLiteral(TypeInfer, Collection)
     */
    public static Expression multiParam(final TypeInfer type, final Collection<?> values) {
        return MultiParamExpression.multi(type, values);
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
     */
    public static Expression multiLiteral(final TypeInfer type, final Collection<?> values) {
        return MultiLiteralExpression.multi(type, values);
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
     * @throws CriteriaException throw when <ul>
     *                           <li>name have no text</li>
     *                           <li>size less than 1</li>
     *                           <li>infer return codec {@link TableField}</li>
     *                           </ul>
     * @see #namedMultiLiteral(TypeInfer, String, int)
     */
    public static Expression namedMultiParam(final TypeInfer type, final String name, final int size) {
        return MultiParamExpression.named(type, name, size);
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
     */
    public static Expression namedMultiLiteral(final TypeInfer type, final String name, final int size) {
        return MultiLiteralExpression.named(type, name, size);
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
     * @throws CriteriaException throw when <ul>
     *                           <li>values is empty</li>
     *                           <li>infer isn't codec {@link TableField}</li>
     *                           </ul>
     * @see #encodingMultiLiteral(TypeInfer, Collection)
     */
    public static Expression encodingMultiParam(final TypeInfer type, final Collection<?> values) {
        return MultiParamExpression.encodingMulti(type, values);
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
     * @throws CriteriaException throw when <ul>
     *                           <li>values is empty</li>
     *                           <li>infer isn't codec {@link TableField}</li>
     *                           </ul>
     * @see #encodingMultiParam(TypeInfer, Collection)
     */
    public static Expression encodingMultiLiteral(final TypeInfer type, final Collection<?> values) {
        return MultiLiteralExpression.encodingMulti(type, values);
    }

    /**
     * <p>
     * Create named non-null multi parameter expression, multi parameter expression will output multi parameter placeholders like below:
     * ? [, ...]
     * but as the right operand of  IN(or NOT IN) operator, will output (  ? [, ...] )
     * </p>
     * <p>
     * Named multi parameter expression is used in batch update(or delete) and values insert.
     * </p>
     *
     * @param type non-null,the type of element of {@link Collection}
     * @param name non-null,the key name of {@link Map} or the field name of java bean.
     * @param size positive,the size of {@link Collection}
     * @return named non-null multi parameter expression
     * @throws CriteriaException throw when <ul>
     *                           <li>name have no text</li>
     *                           <li>size less than 1</li>
     *                           <li>infer isn't codec {@link TableField}</li>
     *                           </ul>
     * @see #encodingNamedMultiLiteral(TypeInfer, String, int)
     */
    public static Expression encodingNamedMultiParam(final TypeInfer type, final String name, final int size) {
        return MultiParamExpression.encodingNamed(type, name, size);
    }


    /**
     * <p>
     * Create named non-null multi literal expression, multi literal expression will output multi LITERAL like below:
     * LITERAL [, ...]
     * but as the right operand of  IN(or NOT IN) operator, will output ( LITERAL  [, ...] )
     * </p>
     * <p>
     * This expression can only be used in values insert statement,this method couldn't be used in batch update(delete) statement.
     * </p>
     *
     * @param type non-null,the type of element of {@link Collection}
     * @param name non-null,the key name of {@link Map} or the field name of java bean.
     * @param size positive,the size of {@link Collection}
     * @return named non-null multi literal expression
     * @throws CriteriaException throw when <ul>
     *                           <li>name have no text</li>
     *                           <li>size less than 1</li>
     *                           <li>infer isn't codec {@link TableField}</li>
     *                           </ul>
     * @see #encodingNamedMultiParam(TypeInfer, String, int)
     */
    public static Expression encodingNamedMultiLiteral(final TypeInfer type, final String name, final int size) {
        return MultiLiteralExpression.encodingNamed(type, name, size);
    }


    /**
     * <p>
     * Get a {@link QualifiedField}. You don't need a {@link QualifiedField},if no self-join in statement.
     * </p>
     *
     * @throws CriteriaException throw when<ul>
     *                           <li>current statement don't support this method,eg: single-table UPDATE statement</li>
     *                           <li>qualified field don't exists,here always is deferred,because army validate qualified field when statement end.</li>
     *                           </ul>
     */
    public static <T> QualifiedField<T> field(String tableAlias, FieldMeta<T> field) {
        return ContextStack.peek().field(tableAlias, field);
    }

    /**
     * <p>
     * Reference a derived field from current statement.
     * </p>
     *
     * @param derivedAlias   derived table alias,
     * @param selectionAlias derived field alias
     */
    public static DerivedField refThis(String derivedAlias, String selectionAlias) {
        return ContextStack.peek().refThis(derivedAlias, selectionAlias);
    }

    /**
     * <p>
     * Reference a derived field from outer statement.
     * </p>
     *
     * @param derivedAlias   derived table alias,
     * @param selectionAlias derived field alias
     */
    public static DerivedField refOuter(String derivedAlias, String selectionAlias) {
        return ContextStack.peek().refOuter(derivedAlias, selectionAlias);
    }


    /**
     * <p>
     * Reference a {@link  Selection} of current statement ,eg: ORDER BY clause.
     * The {@link Expression} returned don't support {@link Expression#as(String)} method.
     * </p>
     *
     * @return the {@link Expression#typeMeta()} of the {@link Expression} returned always return {@link TypeMeta#mappingType()} of {@link Selection#typeMeta()} .
     * @throws CriteriaException then when <ul>
     *                           <li>current statement don't support this method,eg: UPDATE statement</li>
     *                           <li>the {@link Selection} not exists,here possibly is deferred,if you invoke this method before SELECT clause end. eg: postgre DISTINCT ON clause</li>
     *                           </ul>
     */
    public static Expression refSelection(String selectionAlias) {
        return ContextStack.peek().refSelection(selectionAlias);
    }

    /**
     * <p>
     * Reference a {@link  Selection} of current statement ,eg: ORDER BY clause.
     * The {@link Expression} returned don't support {@link Expression#as(String)} method.
     * </p>
     *
     * @param selectionOrdinal based 1 .
     * @return the {@link Expression#typeMeta()} of the {@link Expression} returned always return {@link io.army.mapping.IntegerType#INSTANCE}
     * @throws CriteriaException throw when<ul>
     *                           <li>selectionOrdinal less than 1</li>
     *                           <li>the {@link Selection} not exists,here possibly is deferred,if you invoke this method before SELECT clause end. eg: postgre DISTINCT ON clause</li>
     *                           <li>current statement don't support this method,eg: UPDATE statement</li>
     *                           </ul>
     */
    public static Expression ref(int selectionOrdinal) {
        return ContextStack.peek().refSelection(selectionOrdinal);
    }


    public static Expression parens(Expression expression) {
        return OperationExpression.bracketExp(expression);
    }

    public static SimplePredicate bracket(IPredicate predicate) {
        return OperationPredicate.bracketPredicate(predicate);
    }

    public static SimpleExpression bitwiseNot(Expression exp) {
        return Expressions.unaryExp(UnaryExpOperator.BITWISE_NOT, exp);
    }

    public static SimpleExpression negate(Expression exp) {
        return Expressions.unaryExp(UnaryExpOperator.NEGATE, exp);
    }

    public static IPredicate not(IPredicate predicate) {
        return OperationPredicate.notPredicate(predicate);
    }


    public static SQLFunction._CaseFuncWhenClause cases() {
        return FunctionUtils.caseFunction(null);
    }

    public static SQLFunction._CaseFuncWhenClause cases(Expression expression) {
        ContextStack.assertNonNull(expression);
        return FunctionUtils.caseFunction(expression);
    }


    /*################################## blow sql key word operate method ##################################*/

    /**
     * @param subQuery non-null
     */
    public static IPredicate exists(SubQuery subQuery) {
        return Expressions.existsPredicate(UnaryBooleanOperator.EXISTS, subQuery);
    }

    /**
     * @param subQuery non-null
     */
    public static IPredicate notExists(SubQuery subQuery) {
        return Expressions.existsPredicate(UnaryBooleanOperator.NOT_EXISTS, subQuery);
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


}
