package io.army.criteria.impl;

import io.army.criteria.CriteriaException;
import io.army.criteria.Query;
import io.army.criteria.SQLModifier;
import io.army.criteria.TablePart;
import io.army.criteria.impl.inner._TableBlock;
import io.army.criteria.impl.inner.mysql._MySQLQuery;
import io.army.criteria.mysql.MySQLQuery;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.util.CollectionUtils;
import io.army.util._Exceptions;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;


abstract class MySQLSimpleQuery<C, Q extends Query, SR, FT, FS, FP, IR, JT, JS, IT, WR, AR, GR, HR, OR, LR, UR, SP>
        extends SimpleQuery<C, Q, SR, FT, FS, JT, JS, WR, AR, GR, HR, OR, LR, UR, SP>
        implements MySQLQuery, _MySQLQuery, MySQLQuery.MySQLJoinClause<C, JT, JS, IT>
        , MySQLQuery.MySQLFromClause<C, FT, FS, FP>, MySQLQuery.IndexHintClause<C, IR, FT>
        , MySQLQuery.IndexPurposeClause<C, FT> {

    private MySQLIndexHint.Command indexHintCommand;

    private List<TableBlock> tableBlockList;

    MySQLSimpleQuery(CriteriaContext criteriaContext) {
        super(criteriaContext);
    }

    @Override
    public final FP from(TableMeta<?> table) {
        final List<TableBlock> tableBlockList = this.tableBlockList;
        if (!CollectionUtils.isEmpty(tableBlockList)) {
            throw _Exceptions.castCriteriaApi();
        }
        return createFromBlockWithPartition(table, this::addFromWithPartition);
    }

    @Override
    public final IR useIndex() {
        this.setIndexHintCommand(MySQLIndexHint.Command.USER_INDEX);
        return (IR) this;
    }

    @Override
    public final IR ignoreIndex() {
        this.setIndexHintCommand(MySQLIndexHint.Command.IGNORE_INDEX);
        return (IR) this;
    }

    @Override
    public final IR forceIndex() {
        this.setIndexHintCommand(MySQLIndexHint.Command.FORCE_INDEX);
        return (IR) this;
    }

    @Override
    public final IR ifUseIndex(Predicate<C> predicate) {
        if (predicate.test(this.criteria)) {
            this.useIndex();
        } else {
            this.indexHintCommand = null;
        }
        return (IR) this;
    }

    @Override
    public final IR ifIgnoreIndex(Predicate<C> predicate) {
        if (predicate.test(this.criteria)) {
            this.ignoreIndex();
        } else {
            this.indexHintCommand = null;
        }
        return (IR) this;
    }

    @Override
    public final IR ifForceIndex(Predicate<C> predicate) {
        if (predicate.test(this.criteria)) {
            this.forceIndex();
        } else {
            this.indexHintCommand = null;
        }
        return (IR) this;
    }

    @Override
    public final FT useIndex(List<String> indexList) {
        this.addIndexHint(MySQLIndexHint.Command.USER_INDEX, null, indexList);
        return (FT) this;
    }

    @Override
    public final FT ignoreIndex(List<String> indexList) {
        this.addIndexHint(MySQLIndexHint.Command.IGNORE_INDEX, null, indexList);
        return (FT) this;
    }

    @Override
    public final FT forceIndex(List<String> indexList) {
        this.addIndexHint(MySQLIndexHint.Command.FORCE_INDEX, null, indexList);
        return (FT) this;
    }

    @Override
    public final FT ifUseIndex(Function<C, List<String>> function) {
        final List<String> list;
        list = function.apply(this.criteria);
        if (!CollectionUtils.isEmpty(list)) {
            this.useIndex(list);
        }
        return (FT) this;
    }

    @Override
    public final FT ifIgnoreIndex(Function<C, List<String>> function) {
        final List<String> list;
        list = function.apply(this.criteria);
        if (!CollectionUtils.isEmpty(list)) {
            this.ignoreIndex(list);
        }
        return (FT) this;
    }

    @Override
    public final FT ifForceIndex(Function<C, List<String>> function) {
        final List<String> list;
        list = function.apply(this.criteria);
        if (!CollectionUtils.isEmpty(list)) {
            this.forceIndex(list);
        }
        return (FT) this;
    }

    @Override
    public final FT forJoin(List<String> indexList) {
        final MySQLIndexHint.Command command = this.indexHintCommand;
        if (command != null) {
            this.indexHintCommand = null;//firstly clear command
            this.addIndexHint(command, MySQLIndexHint.Purpose.FOR_JOIN, indexList);

        }
        return (FT) this;
    }

    @Override
    public final FT forOrderBy(List<String> indexList) {
        final MySQLIndexHint.Command command = this.indexHintCommand;
        if (command != null) {
            this.indexHintCommand = null;//firstly clear command
            this.addIndexHint(command, MySQLIndexHint.Purpose.FOR_ORDER_BY, indexList);

        }
        return (FT) this;
    }

    @Override
    public final FT forGroupBy(List<String> indexList) {
        final MySQLIndexHint.Command command = this.indexHintCommand;
        if (command != null) {
            this.indexHintCommand = null;//firstly clear command
            this.addIndexHint(command, MySQLIndexHint.Purpose.FOR_GROUP_BY, indexList);
        }
        return (FT) this;
    }

    @Override
    public final FT forJoin(Function<C, List<String>> function) {
        if (this.indexHintCommand != null) {
            this.forJoin(function.apply(this.criteria));
        }
        return (FT) this;
    }

    @Override
    public final FT forOrderBy(Function<C, List<String>> function) {
        if (this.indexHintCommand != null) {
            this.forOrderBy(function.apply(this.criteria));
        }
        return (FT) this;
    }

    @Override
    public final FT forGroupBy(Function<C, List<String>> function) {
        if (this.indexHintCommand != null) {
            this.forGroupBy(function.apply(this.criteria));
        }
        return (FT) this;
    }


    @Override
    public final JT straightJoin(TableMeta<?> table, String tableAlias) {
        return this.doJoinTable(JoinType.STRAIGHT_JOIN, table, tableAlias);
    }

    @Override
    public final JT ifStraightJoin(Predicate<C> predicate, TableMeta<?> table, String alias) {
        return this.doIfJoinTable(predicate, JoinType.STRAIGHT_JOIN, table, alias);
    }

    @Override
    public final <T extends TablePart> JS straightJoin(Function<C, T> function, String alias) {
        return this.doJoinTablePart(JoinType.STRAIGHT_JOIN, function.apply(this.criteria), alias);
    }

    @Override
    public final <T extends TablePart> JS straightJoin(Supplier<T> supplier, String alias) {
        return this.doJoinTablePart(JoinType.STRAIGHT_JOIN, supplier.get(), alias);
    }

    @Override
    public final <T extends TablePart> JS ifStraightJoin(Function<C, T> function, String alias) {
        return this.doIfJoinTablePart(JoinType.STRAIGHT_JOIN, function, alias);
    }

    @Override
    public final <T extends TablePart> JS ifStraightJoin(Supplier<T> supplier, String alias) {
        return this.doIfJoinTablePart(JoinType.STRAIGHT_JOIN, supplier, alias);
    }

    @Override
    public final IT leftJoin(TableMeta<?> table) {
        return this.doJoinTablePartition(JoinType.LEFT_JOIN, table);
    }

    @Override
    public final IT ifLeftJoin(Predicate<C> predicate, TableMeta<?> table) {
        return this.doIfJointTablePartition(predicate, JoinType.LEFT_JOIN, table);
    }

    @Override
    public final IT join(TableMeta<?> table) {
        return this.doJoinTablePartition(JoinType.JOIN, table);
    }

    @Override
    public final IT ifJoin(Predicate<C> predicate, TableMeta<?> table) {
        return this.doIfJointTablePartition(predicate, JoinType.JOIN, table);
    }

    @Override
    public final IT rightJoin(TableMeta<?> table) {
        return this.doJoinTablePartition(JoinType.RIGHT_JOIN, table);
    }

    @Override
    public final IT ifRightJoin(Predicate<C> predicate, TableMeta<?> table) {
        return this.doIfJointTablePartition(predicate, JoinType.RIGHT_JOIN, table);
    }

    @Override
    public final IT straightJoin(TableMeta<?> table) {
        return this.doJoinTablePartition(JoinType.STRAIGHT_JOIN, table);
    }

    @Override
    public final IT ifStraightJoin(Predicate<C> predicate, TableMeta<?> table) {
        return this.doIfJointTablePartition(predicate, JoinType.STRAIGHT_JOIN, table);
    }

    @Override
    public final IT fullJoin(TableMeta<?> table) {
        return this.doJoinTablePartition(JoinType.FULL_JOIN, table);
    }

    @Override
    public final IT ifFullJoin(Predicate<C> predicate, TableMeta<?> table) {
        return this.doIfJointTablePartition(predicate, JoinType.FULL_JOIN, table);
    }


    @Override
    public final List<? extends _TableBlock> tableBlockList() {
        prepared();
        return this.tableBlockList;
    }


    abstract IT createPartitionOnBlock(JoinType joinType, TableMeta<?> table);

    abstract JT createIndexHintOnBlock(JoinType joinType, TableMeta<?> table, String tableAlias);

    abstract JS createOnBlock(JoinType joinType, TablePart tablePart, String alias);

    abstract IT createNoActionPartitionBlock();

    abstract FP createFromBlockWithPartition(TableMeta<?> table, Function<MySQLFromTableBlock, FT> function);

    @Override
    final JT addTableBlock(JoinType joinType, TableMeta<?> table, String tableAlias) {
        final JT block;
        block = createIndexHintOnBlock(joinType, table, tableAlias);
        this.tableBlockList.add((TableBlock) block);
        return block;
    }

    @Override
    final JS addOnBlock(JoinType joinType, TablePart tablePart, String tableAlias) {
        final JS block;
        block = createOnBlock(joinType, tablePart, tableAlias);
        this.tableBlockList.add((TableBlock) block);
        return block;
    }

    @Override
    final FT addTableFromBlock(TableMeta<?> table, String tableAlias) {
        final List<TableBlock> tableBlockList = this.tableBlockList;
        if (!CollectionUtils.isEmpty(tableBlockList)) {
            throw _Exceptions.castCriteriaApi();
        }
        tableBlockList.add(new MySQLFromTableBlock(table, tableAlias));
        return (FT) this;
    }

    @Override
    final FS addTablePartFromBlock(TablePart tablePart, String alias) {
        final List<TableBlock> tableBlockList = this.tableBlockList;
        if (!CollectionUtils.isEmpty(tableBlockList)) {
            throw _Exceptions.castCriteriaApi();
        }
        tableBlockList.add(TableBlock.fromBlock(tablePart, alias));
        this.criteriaContext.onAddTablePart(tablePart, alias);
        return (FS) this;
    }


    /*################################## blow private method ##################################*/


    private IT doIfJointTablePartition(Predicate<C> predicate, JoinType joinType, TableMeta<?> table) {
        final IT block;
        if (predicate.test(this.criteria)) {
            block = doJoinTablePartition(joinType, table);
        } else {
            block = createNoActionPartitionBlock();
        }
        return block;
    }


    /**
     * @see #useIndex()
     * @see #ignoreIndex()
     * @see #forceIndex()
     */
    private void setIndexHintCommand(MySQLIndexHint.Command command) {
        if (this.indexHintCommand != null) {
            throw _Exceptions.castCriteriaApi();
        }
        final List<TableBlock> tableBlockList = this.tableBlockList;
        if (tableBlockList.size() != 1) {
            throw _Exceptions.castCriteriaApi();
        }
        final TableBlock block = tableBlockList.get(0);
        if (!(block instanceof MySQLFromTableBlock)) {
            throw _Exceptions.castCriteriaApi();
        }
        this.indexHintCommand = command;
    }

    /**
     * @see #useIndex(List)
     * @see #ignoreIndex(List)
     * @see #forceIndex(List)
     * @see #forOrderBy(List)
     * @see #forGroupBy(List)
     * @see #forJoin(List)
     */
    private void addIndexHint(MySQLIndexHint.Command command, @Nullable MySQLIndexHint.Purpose purpose
            , List<String> indexNames) {

        final List<TableBlock> tableBlockList = this.tableBlockList;
        if (tableBlockList.size() != 1) {
            throw _Exceptions.castCriteriaApi();
        }
        if (this.indexHintCommand != null) {
            throw _Exceptions.castCriteriaApi();
        }
        final TableBlock block = tableBlockList.get(0);
        if (!(block instanceof MySQLFromTableBlock)) {
            throw _Exceptions.castCriteriaApi();
        }
        if (CollectionUtils.isEmpty(indexNames)) {
            throw new CriteriaException("index name list must not empty.");
        }
        final MySQLFromTableBlock tableBlock = (MySQLFromTableBlock) block;
        List<MySQLIndexHint> indexHintList = tableBlock.indexHintList;
        if (indexHintList == null) {
            indexHintList = new ArrayList<>();
            tableBlock.indexHintList = indexHintList;
        }
        indexHintList.add(new MySQLIndexHint(command, purpose, indexNames));

    }

    private IT doJoinTablePartition(JoinType joinType, TableMeta<?> table) {
        final IT block;
        block = createPartitionOnBlock(joinType, table);
        this.tableBlockList.add((TableBlock) block);
        return block;
    }

    /**
     * @see #from(TableMeta)
     */
    private FT addFromWithPartition(MySQLFromTableBlock block) {
        final List<TableBlock> tableBlockList = this.tableBlockList;
        if (!CollectionUtils.isEmpty(tableBlockList)) {
            throw _Exceptions.castCriteriaApi();
        }
        tableBlockList.add(block);
        return (FT) this;
    }


    enum MySQLLock implements SQLModifier {

        FOR_UPDATE("FOR UPDATE"),
        LOCK_IN_SHARE_MODE("LOCK IN SHARE MODE"),
        SHARE("SHARE");

        final String words;

        MySQLLock(String words) {
            this.words = words;
        }

        @Override
        public final String render() {
            return this.words;
        }


    }

    enum MySQLLockOption implements SQLModifier {

        NOWAIT("NOWAIT"),
        SKIP_LOCKED("SKIP LOCKED");

        final String words;

        MySQLLockOption(String words) {
            this.words = words;
        }

        @Override
        public final String render() {
            return null;
        }

    }


}