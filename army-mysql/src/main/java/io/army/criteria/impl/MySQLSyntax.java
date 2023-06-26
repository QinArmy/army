package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.dialect.Hint;
import io.army.criteria.mysql.HintStrategy;
import io.army.dialect._Constant;
import io.army.lang.Nullable;
import io.army.mapping.StringType;
import io.army.util._StringUtils;

import java.util.EnumSet;
import java.util.List;
import java.util.regex.Pattern;

/**
 * <p>
 * This class is base class of below:
 *     <ul>
 *         <li>{@link MySQLStringFunctions}</li>
 *     </ul>
 * </p>
 * package class
 */
@SuppressWarnings("unused")
abstract class MySQLSyntax extends MySQLFunctions {

    /**
     * package constructor
     */
    MySQLSyntax() {
    }


    public interface Modifier extends Query.SelectModifier {

    }

    public interface WordDistinct extends Modifier, SQLs.ArgDistinct {

    }

    public interface WordUsing extends SQLWords {

    }

    public interface WordNested extends SQLWords {

    }

    public interface WordExistsPath extends SQLWords {

    }

    public interface WordsAtTimeZone extends SQLWords {

    }

    public interface WordsCharacterSet extends SQLWords {

    }

    public interface WordsCollate extends SQLWords {

    }


    private enum MySQLModifier implements Modifier {


        ALL(" ALL"),
        DISTINCTROW(" DISTINCTROW"),

        HIGH_PRIORITY(" HIGH_PRIORITY"),

        STRAIGHT_JOIN(" STRAIGHT_JOIN"),

        SQL_SMALL_RESULT(" SQL_SMALL_RESULT"),
        SQL_BIG_RESULT(" SQL_BIG_RESULT"),
        SQL_BUFFER_RESULT(" SQL_BUFFER_RESULT"),

        SQL_NO_CACHE(" SQL_NO_CACHE"),
        SQL_CALC_FOUND_ROWS(" SQL_CALC_FOUND_ROWS"),

        LOW_PRIORITY(" LOW_PRIORITY"),
        DELAYED(" DELAYED"),

        QUICK(" QUICK"),
        IGNORE(" IGNORE"),

        CONCURRENT(" CONCURRENT"),
        LOCAL(" LOCAL");

        private final String spaceWords;

        MySQLModifier(String spaceWords) {
            this.spaceWords = spaceWords;
        }


        @Override
        public final String spaceRender() {
            return this.spaceWords;
        }

        @Override
        public final String toString() {
            return keyWordsToString(this);
        }


    }//MySQLModifier


    private enum KeyWordDistinct implements WordDistinct {

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
            return keyWordsToString(this);
        }


    }//WordDistinct


    private enum KeyWordUsing implements WordUsing {

        USING(" USING");

        private final String spaceWord;

        KeyWordUsing(String spaceWord) {
            this.spaceWord = spaceWord;
        }

        @Override
        public final String spaceRender() {
            return this.spaceWord;
        }


        @Override
        public final String toString() {
            return keyWordsToString(this);
        }

    }//KeyWordUsing

    private enum KeyWordNested implements WordNested {

        NESTED(" NESTED");

        private final String spaceWord;

        KeyWordNested(String spaceWord) {
            this.spaceWord = spaceWord;
        }

        @Override
        public final String spaceRender() {
            return this.spaceWord;
        }


        @Override
        public final String toString() {
            return keyWordsToString(this);
        }

    }//KeyWordNested

    private enum KeyWordExistsPath implements WordExistsPath {

        EXISTS_PATH(" EXISTS PATH");

        private final String spaceWord;

        KeyWordExistsPath(String spaceWord) {
            this.spaceWord = spaceWord;
        }

        @Override
        public final String spaceRender() {
            return this.spaceWord;
        }


        @Override
        public final String toString() {
            return keyWordsToString(this);
        }


    }//KeyWordExistsPath


    private enum KeyWordsAtTimeZone implements WordsAtTimeZone {

        AT_TIME_ZONE(" AT TIME ZONE");


        private final String spaceWord;

        KeyWordsAtTimeZone(String spaceWord) {
            this.spaceWord = spaceWord;
        }

        @Override
        public final String spaceRender() {
            return this.spaceWord;
        }


        @Override
        public final String toString() {
            return keyWordsToString(this);
        }


    }//KeyWordsAtTimeZone

    private enum KeyWordsCharacterSet implements WordsCharacterSet {

        CHARACTER_SET(" CHARACTER SET");


        private final String spaceWord;

        KeyWordsCharacterSet(String spaceWord) {
            this.spaceWord = spaceWord;
        }

        @Override
        public final String spaceRender() {
            return this.spaceWord;
        }


        @Override
        public final String toString() {
            return keyWordsToString(this);
        }

    }//KeyWordsCharacterSet

    private enum KeyWordsCollate implements WordsCollate {

        COLLATE(" COLLATE");


        private final String spaceWord;

        KeyWordsCollate(String spaceWord) {
            this.spaceWord = spaceWord;
        }

        @Override
        public final String spaceRender() {
            return this.spaceWord;
        }


        @Override
        public final String toString() {
            return keyWordsToString(this);
        }

    }//KeyWordsCollate


    public static final Modifier ALL = MySQLModifier.ALL;
    public static final WordDistinct DISTINCT = KeyWordDistinct.DISTINCT;

    public static final Modifier DISTINCTROW = MySQLModifier.DISTINCTROW;
    public static final Modifier HIGH_PRIORITY = MySQLModifier.HIGH_PRIORITY;

    public static final Modifier STRAIGHT_JOIN = MySQLModifier.STRAIGHT_JOIN;
    public static final Modifier SQL_SMALL_RESULT = MySQLModifier.SQL_SMALL_RESULT;
    public static final Modifier SQL_BIG_RESULT = MySQLModifier.SQL_BIG_RESULT;
    public static final Modifier SQL_BUFFER_RESULT = MySQLModifier.SQL_BUFFER_RESULT;

    public static final Modifier SQL_NO_CACHE = MySQLModifier.SQL_NO_CACHE;
    public static final Modifier SQL_CALC_FOUND_ROWS = MySQLModifier.SQL_CALC_FOUND_ROWS;
    public static final Modifier LOW_PRIORITY = MySQLModifier.LOW_PRIORITY;
    public static final Modifier DELAYED = MySQLModifier.DELAYED;

    public static final Modifier QUICK = MySQLModifier.QUICK;
    public static final Modifier IGNORE = MySQLModifier.IGNORE;
    public static final Modifier CONCURRENT = MySQLModifier.CONCURRENT;
    public static final Modifier LOCAL = MySQLModifier.LOCAL;


    public static final WordUsing USING = KeyWordUsing.USING;


    public static final SQLs.WordPath PATH = SqlWords.KeyWordPath.PATH;

    public static final WordExistsPath EXISTS_PATH = KeyWordExistsPath.EXISTS_PATH;


    public static final SQLs.WordsForOrdinality FOR_ORDINALITY = SqlWords.KeyWordsForOrdinality.FOR_ORDINALITY;

    // public static final WordNested NESTED = KeyWordNested.NESTED;

    public static final WordsAtTimeZone AT_TIME_ZONE = KeyWordsAtTimeZone.AT_TIME_ZONE;

    public static final WordsCharacterSet CHARACTER_SET = KeyWordsCharacterSet.CHARACTER_SET;

    public static final WordsCollate COLLATE = KeyWordsCollate.COLLATE;


    public static final Expression LITERAL_one = SQLs.literal(StringType.INSTANCE, "one");

    public static final Expression LITERAL_all = SQLs.literal(StringType.INSTANCE, "all");


    /**
     * @see io.army.criteria.mysql.MySQLCharset
     */
    public static SQLIdentifier charset(String charsetName) {
        final Pattern pattern;
        pattern = Pattern.compile("a-zA-Z[_\\w]*]");
        if (!pattern.matcher(charsetName).matches()) {
            String m = String.format("Illegal charset name[%s]", charsetName);
            throw ContextStack.criteriaError(ContextStack.peek(), charsetName);
        }
        return SQLs._identifier(charsetName);
    }

    /*################################## blow join-order hint ##################################*/

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/optimizer-hints.html#optimizer-hints-join-order">Join-Order Optimizer Hints</a>
     */
    public static Hint joinFixedOrder(@Nullable String queryBlockName) {
        return MySQLHints.joinFixedOrder(queryBlockName);
    }

    public static LogicalPredicate xor(IPredicate left, IPredicate right) {
        throw new UnsupportedOperationException();
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/optimizer-hints.html#optimizer-hints-join-order">Join-Order Optimizer Hints</a>
     */
    public static Hint joinOrder(@Nullable String queryBlockName, List<String> tableNameList) {
        return MySQLHints.joinOrderHint(MySQLHints.HintType.JOIN_ORDER, queryBlockName, tableNameList);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/optimizer-hints.html#optimizer-hints-join-order">Join-Order Optimizer Hints</a>
     */
    public static Hint joinPrefix(@Nullable String queryBlockName, List<String> tableNameList) {
        return MySQLHints.joinOrderHint(MySQLHints.HintType.JOIN_PREFIX, queryBlockName, tableNameList);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/optimizer-hints.html#optimizer-hints-join-order">Join-Order Optimizer Hints</a>
     */
    public static Hint joinSuffix(@Nullable String queryBlockName, List<String> tableNameList) {
        return MySQLHints.joinOrderHint(MySQLHints.HintType.JOIN_SUFFIX, queryBlockName, tableNameList);
    }

    /*################################## blow table-level hint ##################################*/

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/optimizer-hints.html#optimizer-hints-table-level">Table-Level Optimizer Hints</a>
     */
    public static Hint bka(@Nullable String queryBlockName, List<String> tableNameList) {
        return MySQLHints.tableLevelHint(MySQLHints.HintType.BKA, queryBlockName, tableNameList);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/optimizer-hints.html#optimizer-hints-table-level">Table-Level Optimizer Hints</a>
     */
    public static Hint noBka(@Nullable String queryBlockName, List<String> tableNameList) {
        return MySQLHints.tableLevelHint(MySQLHints.HintType.NO_BKA, queryBlockName, tableNameList);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/optimizer-hints.html#optimizer-hints-table-level">Table-Level Optimizer Hints</a>
     */
    public static Hint bnl(@Nullable String queryBlockName, List<String> tableNameList) {
        return MySQLHints.tableLevelHint(MySQLHints.HintType.BNL, queryBlockName, tableNameList);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/optimizer-hints.html#optimizer-hints-table-level">Table-Level Optimizer Hints</a>
     */
    public static Hint noBnl(@Nullable String queryBlockName, List<String> tableNameList) {
        return MySQLHints.tableLevelHint(MySQLHints.HintType.NO_BNL, queryBlockName, tableNameList);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/optimizer-hints.html#optimizer-hints-table-level">Table-Level Optimizer Hints</a>
     */
    public static Hint derivedConditionPushdown(@Nullable String queryBlockName, List<String> tableNameList) {
        return MySQLHints.tableLevelHint(MySQLHints.HintType.DERIVED_CONDITION_PUSHDOWN, queryBlockName, tableNameList);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/optimizer-hints.html#optimizer-hints-table-level">Table-Level Optimizer Hints</a>
     */
    public static Hint noDerivedConditionPushdown(@Nullable String queryBlockName, List<String> tableNameList) {
        return MySQLHints.tableLevelHint(MySQLHints.HintType.NO_DERIVED_CONDITION_PUSHDOWN, queryBlockName, tableNameList);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/optimizer-hints.html#optimizer-hints-table-level">Table-Level Optimizer Hints</a>
     */
    public static Hint hashJoin(@Nullable String queryBlockName, List<String> tableNameList) {
        return MySQLHints.tableLevelHint(MySQLHints.HintType.HASH_JOIN, queryBlockName, tableNameList);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/optimizer-hints.html#optimizer-hints-table-level">Table-Level Optimizer Hints</a>
     */
    public static Hint noHashJoin(@Nullable String queryBlockName, List<String> tableNameList) {
        return MySQLHints.tableLevelHint(MySQLHints.HintType.NO_HASH_JOIN, queryBlockName, tableNameList);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/optimizer-hints.html#optimizer-hints-table-level">Table-Level Optimizer Hints</a>
     */
    public static Hint merge(@Nullable String queryBlockName, List<String> tableNameList) {
        return MySQLHints.tableLevelHint(MySQLHints.HintType.MERGE, queryBlockName, tableNameList);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/optimizer-hints.html#optimizer-hints-table-level">Table-Level Optimizer Hints</a>
     */
    public static Hint noMerge(@Nullable String queryBlockName, List<String> tableNameList) {
        return MySQLHints.tableLevelHint(MySQLHints.HintType.NO_MERGE, queryBlockName, tableNameList);
    }

    /*################################## blow index-level hint ##################################*/

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/optimizer-hints.html#optimizer-hints-index-level">Index-Level Optimizer Hints</a>
     */
    public static Hint groupIndex(@Nullable String queryBlockName, String tableName, List<String> indexNameList) {
        return MySQLHints.indexLevelHint(MySQLHints.HintType.GROUP_INDEX, queryBlockName, tableName, indexNameList);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/optimizer-hints.html#optimizer-hints-index-level">Index-Level Optimizer Hints</a>
     */
    public static Hint noGroupIndex(@Nullable String queryBlockName, String tableName, List<String> indexNameList) {
        return MySQLHints.indexLevelHint(MySQLHints.HintType.NO_GROUP_INDEX, queryBlockName, tableName, indexNameList);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/optimizer-hints.html#optimizer-hints-index-level">Index-Level Optimizer Hints</a>
     */
    public static Hint index(@Nullable String queryBlockName, String tableName, List<String> indexNameList) {
        return MySQLHints.indexLevelHint(MySQLHints.HintType.INDEX, queryBlockName, tableName, indexNameList);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/optimizer-hints.html#optimizer-hints-index-level">Index-Level Optimizer Hints</a>
     */
    public static Hint noIndex(@Nullable String queryBlockName, String tableName, List<String> indexNameList) {
        return MySQLHints.indexLevelHint(MySQLHints.HintType.NO_INDEX, queryBlockName, tableName, indexNameList);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/optimizer-hints.html#optimizer-hints-index-level">Index-Level Optimizer Hints</a>
     */
    public static Hint indexMerge(@Nullable String queryBlockName, String tableName, List<String> indexNameList) {
        return MySQLHints.indexLevelHint(MySQLHints.HintType.INDEX_MERGE, queryBlockName, tableName, indexNameList);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/optimizer-hints.html#optimizer-hints-index-level">Index-Level Optimizer Hints</a>
     */
    public static Hint noIndexMerge(@Nullable String queryBlockName, String tableName, List<String> indexNameList) {
        return MySQLHints.indexLevelHint(MySQLHints.HintType.NO_INDEX_MERGE, queryBlockName, tableName, indexNameList);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/optimizer-hints.html#optimizer-hints-index-level">Index-Level Optimizer Hints</a>
     */
    public static Hint joinIndex(@Nullable String queryBlockName, String tableName, List<String> indexNameList) {
        return MySQLHints.indexLevelHint(MySQLHints.HintType.JOIN_INDEX, queryBlockName, tableName, indexNameList);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/optimizer-hints.html#optimizer-hints-index-level">Index-Level Optimizer Hints</a>
     */
    public static Hint noJoinIndex(@Nullable String queryBlockName, String tableName, List<String> indexNameList) {
        return MySQLHints.indexLevelHint(MySQLHints.HintType.NO_JOIN_INDEX, queryBlockName, tableName, indexNameList);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/optimizer-hints.html#optimizer-hints-index-level">Index-Level Optimizer Hints</a>
     */
    public static Hint mrr(@Nullable String queryBlockName, String tableName, List<String> indexNameList) {
        return MySQLHints.indexLevelHint(MySQLHints.HintType.MRR, queryBlockName, tableName, indexNameList);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/optimizer-hints.html#optimizer-hints-index-level">Index-Level Optimizer Hints</a>
     */
    public static Hint noMrr(@Nullable String queryBlockName, String tableName, List<String> indexNameList) {
        return MySQLHints.indexLevelHint(MySQLHints.HintType.NO_MRR, queryBlockName, tableName, indexNameList);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/optimizer-hints.html#optimizer-hints-index-level">Index-Level Optimizer Hints</a>
     */
    public static Hint noIcp(@Nullable String queryBlockName, String tableName, List<String> indexNameList) {
        return MySQLHints.indexLevelHint(MySQLHints.HintType.NO_ICP, queryBlockName, tableName, indexNameList);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/optimizer-hints.html#optimizer-hints-index-level">Index-Level Optimizer Hints</a>
     */
    public static Hint noRangeOptimization(@Nullable String queryBlockName, String tableName, List<String> indexNameList) {
        return MySQLHints.indexLevelHint(MySQLHints.HintType.NO_RANGE_OPTIMIZATION, queryBlockName, tableName, indexNameList);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/optimizer-hints.html#optimizer-hints-index-level">Index-Level Optimizer Hints</a>
     */
    public static Hint orderIndex(@Nullable String queryBlockName, String tableName, List<String> indexNameList) {
        return MySQLHints.indexLevelHint(MySQLHints.HintType.ORDER_INDEX, queryBlockName, tableName, indexNameList);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/optimizer-hints.html#optimizer-hints-index-level">Index-Level Optimizer Hints</a>
     */
    public static Hint noOrderIndex(@Nullable String queryBlockName, String tableName, List<String> indexNameList) {
        return MySQLHints.indexLevelHint(MySQLHints.HintType.NO_ORDER_INDEX, queryBlockName, tableName, indexNameList);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/optimizer-hints.html#optimizer-hints-index-level">Index-Level Optimizer Hints</a>
     */
    public static Hint skipScan(@Nullable String queryBlockName, String tableName, List<String> indexNameList) {
        return MySQLHints.indexLevelHint(MySQLHints.HintType.SKIP_SCAN, queryBlockName, tableName, indexNameList);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/optimizer-hints.html#optimizer-hints-index-level">Index-Level Optimizer Hints</a>
     */
    public static Hint noSkipScan(@Nullable String queryBlockName, String tableName, List<String> indexNameList) {
        return MySQLHints.indexLevelHint(MySQLHints.HintType.NO_SKIP_SCAN, queryBlockName, tableName, indexNameList);
    }

    /*################################## blow SubQuery hint ##################################*/

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/optimizer-hints.html#optimizer-hints-subquery">Subquery Optimizer Hints</a>
     */
    public static Hint semijoin(@Nullable String queryBlockName, EnumSet<HintStrategy> strategySet) {
        return MySQLHints.subQueryHint(MySQLHints.HintType.SEMIJOIN, queryBlockName, strategySet);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/optimizer-hints.html#optimizer-hints-subquery">Subquery Optimizer Hints</a>
     */
    public static Hint noSemijoin(@Nullable String queryBlockName, EnumSet<HintStrategy> strategySet) {
        return MySQLHints.subQueryHint(MySQLHints.HintType.NO_SEMIJOIN, queryBlockName, strategySet);
    }

    /*################################## blow Statement Execution Time Optimizer hint ##################################*/

    /**
     * @param millis null or non-negative
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/optimizer-hints.html#optimizer-hints-execution-time">Statement Execution Time Optimizer Hints</a>
     */
    public static Hint maxExecutionTime(@Nullable Long millis) {
        return MySQLHints.maxExecutionTime(millis);
    }

    /*################################## blow Variable-Setting Hint ##################################*/

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/optimizer-hints.html#optimizer-hints-set-var">Variable-Setting Hint Syntax</a>
     */
    public static Hint setVar(String varValuePair) {
        return MySQLHints.setVar(varValuePair);
    }

    /*################################## blow Resource Group Hint ##################################*/

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/optimizer-hints.html#optimizer-hints-resource-group">Resource Group Hint Syntax</a>
     */
    public static Hint resourceGroup(String groupName) {
        return MySQLHints.resourceGroup(groupName);
    }

    /*################################## blow Optimizer Hint ##################################*/

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/optimizer-hints.html#optimizer-hints-query-block-naming">Optimizer Hints for Naming Query Blocks</a>
     */
    public static Hint qbName(String name) {
        return MySQLHints.qbName(name);
    }

    /*-------------------below private method -------------------*/


    private static String keyWordsToString(Enum<?> words) {
        return _StringUtils.builder()
                .append(MySQLs.class.getSimpleName())
                .append(_Constant.PERIOD)
                .append(words.name())
                .toString();
    }

}
