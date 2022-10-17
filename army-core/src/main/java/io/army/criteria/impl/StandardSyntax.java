package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.dialect._Constant;
import io.army.dialect._SqlContext;
import io.army.mapping.LongType;
import io.army.mapping.MappingType;
import io.army.mapping._NullType;
import io.army.meta.TypeMeta;
import io.army.util._StringUtils;

/**
 * <p>
 * Package class,for standard syntax utils.This class is base class of {@link SQLs}.
 * </p>
 *
 * @see SQLs
 * @since 1.0
 */
abstract class StandardSyntax extends Functions {

    /**
     * package constructor
     */
    StandardSyntax() {
    }

    public interface SymbolStar {

    }


    public interface SelectModifier extends Query.SelectModifier {

    }


    public interface WordAll extends SelectModifier {

    }

    public interface WordDistinct extends SelectModifier, FuncDistinct {

    }

    public interface WordAs {

    }

    public interface WordAnd {

    }

    public interface WordOnly extends Query.TableModifier, Query.FetchOnlyWithTies {

    }

    public interface WordFirst extends Query.FetchFirstNext {

    }

    public interface WordNext extends Query.FetchFirstNext {

    }

    public interface WordRow extends Query.FetchRow {

    }

    public interface WordRows extends Query.FetchRow {

    }

    public interface WordLateral extends Query.TabularModifier {

    }

    public interface WordsWithTies extends Query.FetchOnlyWithTies {

    }

    public interface SymbolPeriod {

    }


    private enum KeyWordAll implements SQLWords, WordAll {

        ALL(" ALL");

        private final String spaceWord;

        KeyWordAll(String spaceWord) {
            this.spaceWord = spaceWord;
        }

        @Override
        public final String render() {
            return this.spaceWord;
        }

        @Override
        public final String toString() {
            return _StringUtils.builder()
                    .append(SQLs.class.getSimpleName())
                    .append(_Constant.POINT)
                    .append(this.name())
                    .toString();
        }


    }//KeyWordAll

    private enum KeyWordDistinct implements SQLWords, WordDistinct {

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
            return _StringUtils.builder()
                    .append(SQLs.class.getSimpleName())
                    .append(_Constant.POINT)
                    .append(this.name())
                    .toString();
        }


    }//KeyWordDistinct


    private enum KeyWordAs implements WordAs {

        AS;

        @Override
        public final String toString() {
            return _StringUtils.builder()
                    .append(SQLs.class.getSimpleName())
                    .append(_Constant.POINT)
                    .append(this.name())
                    .toString();
        }

    }//KeyWordAs

    private enum KeyWordAnd implements WordAnd {

        AND;

        @Override
        public final String toString() {
            return _StringUtils.builder()
                    .append(SQLs.class.getSimpleName())
                    .append(_Constant.POINT)
                    .append(this.name())
                    .toString();
        }

    }//KeyWordAnd

    private enum KeyWordLateral implements WordLateral {

        LATERAL(" LATERAL");

        private final String spaceWord;

        KeyWordLateral(String spaceWord) {
            this.spaceWord = spaceWord;
        }

        @Override
        public final String render() {
            return this.spaceWord;
        }

        @Override
        public final String toString() {
            return _StringUtils.builder()
                    .append(SQLs.class.getSimpleName())
                    .append(_Constant.POINT)
                    .append(this.name())
                    .toString();
        }

    }//KeyWordLateral

    private enum KeyWordFirst implements WordFirst, SQLWords {

        FIRST(" FIRST");

        private final String spaceWord;

        KeyWordFirst(String spaceWord) {
            this.spaceWord = spaceWord;
        }

        @Override
        public final String render() {
            return this.spaceWord;
        }

        @Override
        public final String toString() {
            return _StringUtils.builder()
                    .append(SQLs.class.getSimpleName())
                    .append(_Constant.POINT)
                    .append(this.name())
                    .toString();
        }

    }//KeyWordFirst

    private enum KeyWordNext implements WordNext, SQLWords {

        NEXT(" NEXT");

        private final String spaceWord;

        KeyWordNext(String spaceWord) {
            this.spaceWord = spaceWord;
        }

        @Override
        public final String render() {
            return this.spaceWord;
        }

        @Override
        public final String toString() {
            return _StringUtils.builder()
                    .append(SQLs.class.getSimpleName())
                    .append(_Constant.POINT)
                    .append(this.name())
                    .toString();
        }

    }//KeyWordNext

    private enum KeyWordRow implements WordRow, SQLWords {

        ROW(" ROW");

        private final String spaceWord;

        KeyWordRow(String spaceWord) {
            this.spaceWord = spaceWord;
        }

        @Override
        public final String render() {
            return this.spaceWord;
        }

        @Override
        public final String toString() {
            return _StringUtils.builder()
                    .append(SQLs.class.getSimpleName())
                    .append(_Constant.POINT)
                    .append(this.name())
                    .toString();
        }

    }//KeyWordRow

    private enum KeyWordRows implements WordRows, SQLWords {

        ROWS(" ROWS");

        private final String spaceWord;

        KeyWordRows(String spaceWord) {
            this.spaceWord = spaceWord;
        }

        @Override
        public final String render() {
            return this.spaceWord;
        }

        @Override
        public final String toString() {
            return _StringUtils.builder()
                    .append(SQLs.class.getSimpleName())
                    .append(_Constant.POINT)
                    .append(this.name())
                    .toString();
        }

    }//KeyWordRows

    private enum KeyWordWithTies implements WordsWithTies, SQLWords {

        WITH_TIES(" WITH TIES");

        private final String spaceWords;

        KeyWordWithTies(String spaceWords) {
            this.spaceWords = spaceWords;
        }

        @Override
        public final String render() {
            return this.spaceWords;
        }

        @Override
        public final String toString() {
            return _StringUtils.builder()
                    .append(SQLs.class.getSimpleName())
                    .append(_Constant.POINT)
                    .append(this.name())
                    .toString();
        }

    }//KeyWordNext

    private enum KeyWordOny implements WordOnly, SQLWords {

        ONLY(" ONLY");

        private final String spaceWord;

        KeyWordOny(String spaceWord) {
            this.spaceWord = spaceWord;
        }

        @Override
        public final String render() {
            return this.spaceWord;
        }

        @Override
        public final String toString() {
            return _StringUtils.builder()
                    .append(SQLs.class.getSimpleName())
                    .append(_Constant.POINT)
                    .append(this.name())
                    .toString();
        }

    }//KeyWordOny


    private enum SQLSymbolPeriod implements SymbolPeriod {

        PERIOD;

        @Override
        public final String toString() {
            return _StringUtils.builder()
                    .append(SQLs.class.getSimpleName())
                    .append(_Constant.POINT)
                    .append(this.name())
                    .toString();
        }

    }//SQLSymbolPoint

    private enum SQLSymbolStar implements SymbolStar {

        STAR;

        @Override
        public final String toString() {
            return _StringUtils.builder()
                    .append(SQLs.class.getSimpleName())
                    .append(_Constant.POINT)
                    .append(this.name())
                    .toString();
        }

    }//SQLSymbolStar


    /**
     * <p>
     * This class representing sql {@code DEFAULT} key word.
     * </p>
     */
    private static final class DefaultWord extends NonOperationExpression {

        private DefaultWord() {
        }


        @Override
        public TypeMeta typeMeta() {
            throw unsupportedOperation();
        }

        @Override
        public void appendSql(final _SqlContext context) {
            context.sqlBuilder().append(_Constant.SPACE_DEFAULT);
        }

        @Override
        public String toString() {
            return _Constant.SPACE_DEFAULT;
        }

    }// DefaultWord


    /**
     * <p>
     * This class representing sql {@code NULL} key word.
     * </p>
     */
    private static final class NullWord extends NonOperationExpression
            implements SqlValueParam.SingleNonNamedValue {


        private NullWord() {
        }

        @Override
        public void appendSql(_SqlContext context) {
            context.sqlBuilder().append(_Constant.SPACE_NULL);
        }

        @Override
        public TypeMeta typeMeta() {
            return _NullType.INSTANCE;
        }

        @Override
        public Object value() {
            //always null
            return null;
        }

        @Override
        public String toString() {
            return _Constant.SPACE_NULL;
        }


    }// NullWord


    private static final class LiteralSymbolStar extends NonOperationExpression {

        private static final LiteralSymbolStar STAR = new LiteralSymbolStar();

        private LiteralSymbolStar() {
        }

        @Override
        public TypeMeta typeMeta() {
            throw unsupportedOperation();
        }

        @Override
        public void appendSql(final _SqlContext context) {
            context.sqlBuilder().append(" *");
        }

        @Override
        public String toString() {
            return " *";
        }


    }//StarLiteral

    /**
     * @see #TRUE
     * @see #FALSE
     */
    private static final class BooleanWord extends OperationPredicate {

        private final boolean value;

        private BooleanWord(boolean value) {
            this.value = value;
        }


        @Override
        public void appendSql(final _SqlContext context) {
            context.sqlBuilder()
                    .append(this.value ? " TRUE" : " FALSE");
        }

        @Override
        public String toString() {
            return this.value ? " TRUE" : " FALSE";
        }


    }//BooleanWord

    public static final WordAll ALL = KeyWordAll.ALL;

    public static final WordDistinct DISTINCT = KeyWordDistinct.DISTINCT;

    public static final WordAs AS = KeyWordAs.AS;

    public static final WordAnd AND = KeyWordAnd.AND;

    public static final WordLateral LATERAL = KeyWordLateral.LATERAL;

    public static final WordFirst FIRST = KeyWordFirst.FIRST;

    public static final WordNext NEXT = KeyWordNext.NEXT;

    public static final SymbolPeriod PERIOD = SQLSymbolPeriod.PERIOD;

    public static final WordOnly ONLY = KeyWordOny.ONLY;

    public static final WordRow ROW = KeyWordRow.ROW;

    public static final WordRows ROWS = KeyWordRows.ROWS;

    public static final WordsWithTies WITH_TIES = KeyWordWithTies.WITH_TIES;

    public static final SymbolStar START = SQLSymbolStar.STAR;

    public static final IPredicate TRUE = new BooleanWord(true);

    public static final IPredicate FALSE = new BooleanWord(false);

    public static final Expression DEFAULT = new DefaultWord();

    public static final Expression NULL = new NullWord();

    /*-------------------below Aggregate Function-------------------*/


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  LongType}
     * </p>
     *
     * @see SQLs#START
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_count">COUNT(expr) [over_clause]</a>
     */
    public static Expression count(final SymbolStar star) {
        assert star == SQLs.START;
        return SQLFunctions.oneArgFunc("COUNT", LiteralSymbolStar.STAR, LongType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  LongType}
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_count">COUNT(expr) [over_clause]</a>
     */
    public static Expression count(Expression expr) {
        return SQLFunctions.oneArgFunc("COUNT", expr, LongType.INSTANCE);
    }


}
