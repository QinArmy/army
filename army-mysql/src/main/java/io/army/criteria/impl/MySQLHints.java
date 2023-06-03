package io.army.criteria.impl;

import io.army.criteria.CriteriaException;
import io.army.criteria.dialect.Hint;
import io.army.criteria.impl.inner._SelfDescribed;
import io.army.criteria.mysql.HintStrategy;
import io.army.dialect.Dialect;
import io.army.dialect.DialectParser;
import io.army.dialect._Constant;
import io.army.dialect._SqlContext;
import io.army.dialect.mysql.MySQLDialect;
import io.army.lang.Nullable;
import io.army.util._Collections;
import io.army.util._Exceptions;

import java.util.EnumSet;
import java.util.List;

/**
 * <p>
 * This class is all MySQL optimizer hint base class.
 * </p>
 *
 * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/optimizer-hints.html">Optimizer Hints</a>
 * @see <a href="https://dev.mysql.com/doc/refman/5.7/en/optimizer-hints.html">MySQL 5.7 Optimizer Hints</a>
 */
abstract class MySQLHints implements Hint, _SelfDescribed {

    @Nullable
    static MySQLHints castHint(final Hint hint) {
        final MySQLHints h;
        if (hint instanceof MySQLHints) {
            h = (MySQLHints) hint;
        } else {
            h = null;
        }
        return h;
    }

    private MySQLHints() {

    }


    enum HintType {

        JOIN_FIXED_ORDER,
        JOIN_ORDER,
        JOIN_PREFIX,
        JOIN_SUFFIX,
        /*################################## blow table-level hint ##################################*/
        BKA,
        NO_BKA,
        BNL,
        NO_BNL,

        DERIVED_CONDITION_PUSHDOWN,
        NO_DERIVED_CONDITION_PUSHDOWN,
        HASH_JOIN,
        NO_HASH_JOIN,

        MERGE,
        NO_MERGE,
        /*################################## blow index-level hint ##################################*/
        GROUP_INDEX,
        NO_GROUP_INDEX,
        INDEX,
        NO_INDEX,

        INDEX_MERGE,
        NO_INDEX_MERGE,
        JOIN_INDEX,
        NO_JOIN_INDEX,

        MRR,
        NO_MRR,
        NO_ICP,
        NO_RANGE_OPTIMIZATION,

        ORDER_INDEX,
        NO_ORDER_INDEX,
        SKIP_SCAN,
        NO_SKIP_SCAN,
        /*################################## blow SubQuery hint ##################################*/
        SEMIJOIN,
        NO_SEMIJOIN,
        /*################################## blow Statement Execution Time Optimizer hint ##################################*/
        MAX_EXECUTION_TIME,
        /*################################## blow Variable-Setting Hint ##################################*/
        SET_VAR,
        /*################################## blow Resource Group Hint ##################################*/
        RESOURCE_GROUP,
        /*################################## blow Optimizer Hint ##################################*/
        QB_NAME

    }//Hint

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/optimizer-hints.html#optimizer-hints-join-order">Join-Order Optimizer Hints</a>
     */
    static MySQLHints joinFixedOrder(@Nullable String queryBlockName) {
        return new JoinFixedOrder(queryBlockName);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/optimizer-hints.html#optimizer-hints-join-order">Join-Order Optimizer Hints</a>
     */
    static MySQLHints joinOrderHint(HintType hint, @Nullable String queryBlockName, List<String> tableNameList) {
        if (tableNameList.size() == 0) {
            throw new CriteriaException("tableNameList must non-empty.");
        }
        return new JoinOrder(hint, queryBlockName, tableNameList);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/optimizer-hints.html#optimizer-hints-table-level">Table-Level Optimizer Hints</a>
     */
    static MySQLHints tableLevelHint(HintType hint, @Nullable String queryBlockName, List<String> tableNameList) {
        if (tableNameList.size() == 0) {
            throw new CriteriaException("tableNameList must non-empty.");
        }
        return new TableLevelHint(hint, queryBlockName, tableNameList);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/optimizer-hints.html#optimizer-hints-index-level">Index-Level Optimizer Hints</a>
     */
    static MySQLHints indexLevelHint(HintType hint, @Nullable String queryBlockName, String tableName
            , List<String> indexNameList) {
        if (indexNameList.size() == 0) {
            throw new CriteriaException("indexNameList must non-empty.");
        }
        return new IndexLevelHint(hint, queryBlockName, tableName, indexNameList);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/optimizer-hints.html#optimizer-hints-subquery">Subquery Optimizer Hints</a>
     */
    static MySQLHints subQueryHint(HintType hint, @Nullable String queryBlockName, EnumSet<HintStrategy> strategySet) {
        if (strategySet.size() == 0) {
            throw new CriteriaException("strategySet must non-empty.");
        }
        return new SubQueryHint(hint, queryBlockName, strategySet);
    }

    /**
     * @param millis null or non-negative
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/optimizer-hints.html#optimizer-hints-execution-time">Statement Execution Time Optimizer Hints</a>
     */
    static MySQLHints maxExecutionTime(@Nullable Long millis) {
        return new MaxExecutionTimeHint(millis);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/optimizer-hints.html#optimizer-hints-set-var">Variable-Setting Hint Syntax</a>
     */
    static MySQLHints setVar(String varValuePair) {
        return new VariableSettingHint(varValuePair);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/optimizer-hints.html#optimizer-hints-resource-group">Resource Group Hint Syntax</a>
     */
    static MySQLHints resourceGroup(String groupName) {
        return new ResourceGroupHint(groupName);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/optimizer-hints.html#optimizer-hints-query-block-naming">Optimizer Hints for Naming Query Blocks</a>
     */
    static MySQLHints qbName(String name) {
        return new QbNameHint(name);
    }


    private static final class JoinFixedOrder extends MySQLHints {

        private final String queryBlockName;

        private JoinFixedOrder(@Nullable String queryBlockName) {
            this.queryBlockName = queryBlockName;
        }

        @Override
        public void appendSql(final _SqlContext context) {
            final DialectParser parser = context.parser();
            final Dialect dialect;
            dialect = parser.dialect();
            if (dialect.version() < MySQLDialect.MySQL80.version()) {
                throw _Exceptions.dontSupportHint(dialect, HintType.JOIN_FIXED_ORDER);
            }
            final StringBuilder builder;
            builder = context.sqlBuilder()
                    .append(_Constant.SPACE)
                    .append(HintType.JOIN_FIXED_ORDER.name())
                    .append(_Constant.LEFT_PAREN);
            final String queryBlockName = this.queryBlockName;
            if (queryBlockName != null) {
                builder.append(_Constant.SPACE_AT);
                parser.identifier(queryBlockName, builder);
            }
            builder.append(_Constant.RIGHT_PAREN);

        }

    }// JoinFixedOrder


    /**
     * <p>
     * Support below join-order hints:
     *     <ul>
     *         <li>{@link HintType#JOIN_ORDER}</li>
     *         <li>{@link HintType#JOIN_PREFIX}</li>
     *         <li>{@link HintType#JOIN_SUFFIX}</li>
     *     </ul>
     * </p>
     */
    private static final class JoinOrder extends MySQLHints {

        private final HintType hintType;

        private final String queryBlockName;

        private final List<String> tableNameList;

        private JoinOrder(HintType hintType, @Nullable String queryBlockName, List<String> tableNameList) {
            if (tableNameList.size() == 0) {
                throw MySQLHints.hintTableListIsEmpty();
            }
            switch (hintType) {
                case JOIN_ORDER:
                case JOIN_PREFIX:
                case JOIN_SUFFIX:
                    this.hintType = hintType;
                    break;
                default:
                    throw _Exceptions.unexpectedEnum(hintType);
            }
            this.queryBlockName = queryBlockName;
            this.tableNameList = _Collections.asUnmodifiableList(tableNameList);
        }

        @Override
        public void appendSql(final _SqlContext context) {
            final DialectParser parser = context.parser();
            final Dialect dialect = parser.dialect();
            if (dialect.version() < MySQLDialect.MySQL80.version()) {
                throw _Exceptions.dontSupportHint(dialect, this.hintType);
            }
            final StringBuilder builder;
            builder = context.sqlBuilder()
                    .append(_Constant.SPACE)
                    .append(this.hintType.name())
                    .append(_Constant.LEFT_PAREN);

            final String queryBlockName = this.queryBlockName;

            if (queryBlockName != null) {
                builder.append(_Constant.SPACE_AT);
                parser.identifier(queryBlockName, builder);
            }
            builder.append(_Constant.SPACE);

            final List<String> tableNameList = this.tableNameList;
            final int size = tableNameList.size();

            String tableName;
            for (int i = 0, index; i < size; i++) {
                if (i > 0) {
                    builder.append(_Constant.SPACE_COMMA_SPACE);
                }
                if (queryBlockName == null) {
                    parser.identifier(tableNameList.get(i), builder);
                    continue;
                }
                tableName = tableNameList.get(i);
                index = tableName.indexOf(_Constant.AT_CHAR);
                if (index < 0) {
                    parser.identifier(tableName, builder);
                } else if (index < tableName.length() - 1) {
                    parser.identifier(tableName.substring(0, index), builder)
                            .append(_Constant.AT_CHAR);
                    parser.identifier(tableName.substring(index + 1), builder);
                } else {
                    throw MySQLHints.hintTableNameError(tableName);
                }
            }
            builder.append(_Constant.SPACE_RIGHT_PAREN);
        }

    }//JoinOrder


    private static final class TableLevelHint extends MySQLHints {

        private final HintType hintType;

        private final String queryBlockName;

        private final List<String> tableNameList;

        private TableLevelHint(HintType hintType, @Nullable String queryBlockName, List<String> tableNameList) {
            if (tableNameList.size() == 0) {
                throw MySQLHints.hintTableListIsEmpty();
            }
            switch (hintType) {
                case BKA:
                case NO_BKA:
                case BNL:
                case NO_BNL:
                case DERIVED_CONDITION_PUSHDOWN:
                case NO_DERIVED_CONDITION_PUSHDOWN:
                case HASH_JOIN:
                case NO_HASH_JOIN:
                case MERGE:
                case NO_MERGE:
                    this.hintType = hintType;
                    break;
                default:
                    throw _Exceptions.unexpectedEnum(hintType);
            }
            this.queryBlockName = queryBlockName;
            this.tableNameList = _Collections.asUnmodifiableList(tableNameList);
        }


        @Override
        public void appendSql(final _SqlContext context) {
            final DialectParser parser = context.parser();
            switch (this.hintType) {
                case BKA:
                case NO_BKA:
                case BNL:
                case NO_BNL:
                    break;
                default: {
                    final Dialect dialect = parser.dialect();
                    if (dialect.version() < MySQLDialect.MySQL80.version()) {
                        throw _Exceptions.dontSupportHint(dialect, this.hintType);
                    }
                }
            }

            final StringBuilder builder;
            builder = context.sqlBuilder()
                    .append(_Constant.SPACE)
                    .append(this.hintType.name())
                    .append(_Constant.LEFT_PAREN);

            final String queryBlockName = this.queryBlockName;

            if (queryBlockName != null) {
                builder.append(_Constant.SPACE_AT);
                parser.identifier(queryBlockName, builder);
            }
            builder.append(_Constant.SPACE);

            final List<String> tableNameList = this.tableNameList;
            final int size = tableNameList.size();

            String tableName;
            for (int i = 0, index; i < size; i++) {
                if (i > 0) {
                    builder.append(_Constant.SPACE_COMMA_SPACE);
                }
                tableName = tableNameList.get(i);
                index = tableName.indexOf(_Constant.AT_CHAR);
                if (queryBlockName != null) {
                    if (index > -1) {
                        throw MySQLHints.hintTableNameError(tableName);
                    }
                } else if (index > 0 && index < tableName.length() - 1) {
                    parser.identifier(tableName.substring(0, index), builder)
                            .append(_Constant.AT_CHAR);
                    parser.identifier(tableName.substring(index + 1), builder);
                } else {
                    throw MySQLHints.hintTableNameError(tableName);
                }
            }
            builder.append(_Constant.SPACE_RIGHT_PAREN);
        }

    }//TableLevelHint

    private static final class IndexLevelHint extends MySQLHints {

        private final HintType hintType;

        private final String queryBlockName;

        private final String tableName;

        private final List<String> indexNameList;

        private IndexLevelHint(HintType hintType, @Nullable String queryBlockName, String tableName, List<String> indexNameList) {
            switch (hintType) {
                case GROUP_INDEX:
                case NO_GROUP_INDEX:
                case INDEX:
                case NO_INDEX:
                case INDEX_MERGE:
                case NO_INDEX_MERGE:
                case JOIN_INDEX:
                case NO_JOIN_INDEX:
                case MRR:
                case NO_MRR:
                case NO_ICP:
                case NO_RANGE_OPTIMIZATION:
                case ORDER_INDEX:
                case NO_ORDER_INDEX:
                case SKIP_SCAN:
                case NO_SKIP_SCAN:
                    this.hintType = hintType;
                    break;
                default:
                    throw _Exceptions.unexpectedEnum(hintType);
            }
            this.queryBlockName = queryBlockName;
            this.tableName = tableName;
            this.indexNameList = _Collections.asUnmodifiableList(indexNameList);
        }

        @Override
        public void appendSql(final _SqlContext context) {
            final DialectParser parser = context.parser();
            switch (this.hintType) {
                case MRR:
                case NO_MRR:
                case NO_ICP:
                case NO_RANGE_OPTIMIZATION:
                    break;
                default: {
                    final Dialect dialect = parser.dialect();
                    if (dialect.version() < MySQLDialect.MySQL80.version()) {
                        throw _Exceptions.dontSupportHint(dialect, this.hintType);
                    }
                }
            }
            final StringBuilder builder;
            builder = context.sqlBuilder()
                    .append(_Constant.SPACE)
                    .append(this.hintType.name())
                    .append(_Constant.LEFT_PAREN);

            final String queryBlockName = this.queryBlockName;

            if (queryBlockName != null) {
                builder.append(_Constant.SPACE_AT);
                parser.identifier(queryBlockName, builder);
            }
            builder.append(_Constant.SPACE);
            parser.identifier(this.tableName, builder)
                    .append(_Constant.SPACE);
            final List<String> indexNameList = this.indexNameList;
            final int size = indexNameList.size();
            for (int i = 0; i < size; i++) {
                if (i > 0) {
                    builder.append(_Constant.SPACE_COMMA_SPACE);
                }
                parser.identifier(indexNameList.get(i), builder);
            }
            builder.append(_Constant.SPACE_RIGHT_PAREN);
        }

    }//IndexLevelHint


    private static final class SubQueryHint extends MySQLHints {

        private final HintType hintType;

        private final String queryBlockName;

        private final EnumSet<HintStrategy> strategySet;

        private SubQueryHint(HintType hintType, @Nullable String queryBlockName, EnumSet<HintStrategy> strategySet) {
            switch (hintType) {
                case SEMIJOIN:
                case NO_SEMIJOIN:
                    this.hintType = hintType;
                    break;
                default:
                    throw _Exceptions.unexpectedEnum(hintType);
            }
            this.queryBlockName = queryBlockName;
            this.strategySet = EnumSet.copyOf(strategySet);
        }

        @Override
        public void appendSql(final _SqlContext context) {
            final DialectParser parser = context.parser();

            final StringBuilder builder;
            builder = context.sqlBuilder()
                    .append(_Constant.SPACE)
                    .append(this.hintType.name())
                    .append(_Constant.LEFT_PAREN);

            final String queryBlockName = this.queryBlockName;

            if (queryBlockName != null) {
                builder.append(_Constant.SPACE_AT);
                parser.identifier(queryBlockName, builder);
            }
            builder.append(_Constant.SPACE);

            int index = 0;
            for (HintStrategy strategy : this.strategySet) {
                if (index > 0) {
                    builder.append(_Constant.SPACE_COMMA_SPACE);
                }
                builder.append(strategy.name());
                index++;
            }
            builder.append(_Constant.SPACE_RIGHT_PAREN);
        }

    }//SbuQueryHint


    private static final class MaxExecutionTimeHint extends MySQLHints {

        private final Long millis;

        private MaxExecutionTimeHint(@Nullable Long millis) {
            if (millis != null && millis < 0) {
                throw new IllegalArgumentException("millis must non-negative");
            }
            this.millis = millis;
        }

        @Override
        public void appendSql(final _SqlContext context) {
            final StringBuilder builder;
            builder = context.sqlBuilder()
                    .append(_Constant.SPACE)
                    .append(HintType.MAX_EXECUTION_TIME.name())
                    .append(_Constant.LEFT_PAREN);

            final Long millis = this.millis;
            if (millis == null) {
                builder.append(_Constant.RIGHT_PAREN);
            } else {
                builder.append(_Constant.SPACE)
                        .append(millis)
                        .append(_Constant.SPACE_RIGHT_PAREN);
            }

        }

    }//MaxExecutionTimeHint

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/optimizer-hints.html#optimizer-hints-set-var">Variable-Setting Hint Syntax</a>
     */
    private static final class VariableSettingHint extends MySQLHints {

        private final String varValuePair;

        private VariableSettingHint(String varValuePair) {
            if (varValuePair.indexOf(_Constant.EQUAL) < 0) {
                throw MySQLHints.varValuePairError(varValuePair);
            }
            MySQLHints.assertNoCommentBoundary(varValuePair);
            this.varValuePair = varValuePair;
        }

        @Override
        public void appendSql(final _SqlContext context) {
            final Dialect dialect = context.parser().dialect();
            if (dialect.version() < MySQLDialect.MySQL80.version()) {
                throw _Exceptions.dontSupportHint(dialect, HintType.SET_VAR);
            }
            context.sqlBuilder()
                    .append(_Constant.SPACE)
                    .append(HintType.SET_VAR.name())
                    .append(_Constant.LEFT_PAREN)
                    .append(_Constant.SPACE)
                    .append(this.varValuePair)
                    .append(_Constant.SPACE_RIGHT_PAREN);
        }

    }//VariableSettingHint

    private static final class ResourceGroupHint extends MySQLHints {

        private final String groupName;

        private ResourceGroupHint(String groupName) {
            MySQLHints.assertNoCommentBoundary(groupName);
            this.groupName = groupName;
        }

        @Override
        public void appendSql(final _SqlContext context) {
            final Dialect dialect = context.parser().dialect();
            if (dialect.version() < MySQLDialect.MySQL80.version()) {
                throw _Exceptions.dontSupportHint(dialect, HintType.SET_VAR);
            }
            context.sqlBuilder()
                    .append(_Constant.SPACE)
                    .append(HintType.RESOURCE_GROUP.name())
                    .append(_Constant.LEFT_PAREN)
                    .append(_Constant.SPACE)
                    .append(this.groupName)
                    .append(_Constant.SPACE_RIGHT_PAREN);
        }

    }//ResourceGroupHint

    private static final class QbNameHint extends MySQLHints {

        private final String name;

        private QbNameHint(String name) {
            MySQLHints.assertNoCommentBoundary(name);
            this.name = name;
        }

        @Override
        public void appendSql(final _SqlContext context) {
            context.sqlBuilder()
                    .append(_Constant.SPACE)
                    .append(HintType.QB_NAME.name())
                    .append(_Constant.LEFT_PAREN)
                    .append(_Constant.SPACE)
                    .append(this.name)
                    .append(_Constant.SPACE_RIGHT_PAREN);
        }

    }//QbNameHint


    private static void assertNoCommentBoundary(final String str) {
        final int index, last;
        last = str.length() - 1;
        index = str.indexOf(_Constant.ASTERISK);
        if (index > -1) {
            if (index < last && str.charAt(index + 1) == _Constant.SLASH) {
                throw ContextStack.clearStackAndCriteriaError(MySQLHints::varValuePairError, str);
            }
            if (index > 0 && str.charAt(index - 1) == _Constant.SLASH) {
                throw ContextStack.clearStackAndCriteriaError(MySQLHints::varValuePairError, str);
            }
        }
    }

    private static CriteriaException hintTableListIsEmpty() {
        return new CriteriaException("hint table name list must not empty.");
    }

    private static CriteriaException hintTableNameError(String tableName) {
        return new CriteriaException(String.format("Hint table name %s error.", tableName));
    }

    private static CriteriaException varValuePairError(String varValuePair) {
        return new CriteriaException(String.format("%s var and value pair %s error."
                , HintType.SET_VAR.name(), varValuePair));
    }


}
