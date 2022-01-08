package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner.mysql._MySQL80Query;
import io.army.criteria.mysql.MySQL80Query;
import io.army.meta.TableMeta;
import io.army.util.CollectionUtils;
import io.army.util._Exceptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

abstract class MySQL80SimpleQuery<C, Q extends Query> extends MySQLSimpleQuery<
        C,
        Q,
        MySQL80Query.From80Spec<C, Q>,// SR
        MySQL80Query.IndexHintJoin80Spec<C, Q>, //FT
        MySQL80Query.Join80Spec<C, Q>,          //FS
        MySQL80Query.PartitionJoin80Spec<C, Q>, //FP
        MySQL80Query.IndexPurposeJoin80Spec<C, Q>,//IR
        MySQL80Query.IndexHintOn80Spec<C, Q>,    //JR
        MySQL80Query.On80Spec<C, Q>,            //JS
        MySQL80Query.PartitionOn80Spec<C, Q>,   //IT
        MySQL80Query.GroupBy80Spec<C, Q>,       //WR
        MySQL80Query.WhereAnd80Spec<C, Q>,      //AR
        MySQL80Query.GroupByWithRollup80Spec<C, Q>,//GR
        MySQL80Query.Window80Spec<C, Q>,     //HR
        MySQL80Query.OrderByWithRollup80Spec<C, Q>,     //OR
        MySQL80Query.Lock80Spec<C, Q>,       //LR
        MySQL80Query.UnionOrderBy80Spec<C, Q>, //UR
        MySQL80Query.With80Spec<C, Q>>       //SP
        implements MySQL80Query, MySQL80Query.With80Spec<C, Q>, MySQL80Query.From80Spec<C, Q>
        , MySQL80Query.IndexHintJoin80Spec<C, Q>, MySQL80Query.IndexPurposeJoin80Spec<C, Q>
        , MySQL80Query.Join80Spec<C, Q>, MySQL80Query.WhereAnd80Spec<C, Q>, MySQL80Query.Having80Spec<C, Q>
        , _MySQL80Query, MySQL80Query.GroupByWithRollup80Spec<C, Q>, MySQL80Query.OrderByWithRollup80Spec<C, Q>
        , MySQL80Query.Lock80LockOfOptionSpec<C, Q>, MySQL80Query.Lock80LockOptionSpec<C, Q> {


    static <C, Q extends Query> With80Spec<C, Q> unionAndSelect(final Q left, final UnionType unionType) {
        return null;
    }

    private boolean recursive;

    private List<Cte> cteList;

    private Boolean groupByWithRollup;

    private boolean orderByWithRollup;

    private MySQLLock lock;

    private List<TableMeta<?>> ofTableList;

    private MySQLLockOption lockOption;


    private MySQL80SimpleQuery(CriteriaContext criteriaContext) {
        super(criteriaContext);
    }

    @Override
    public final Select80Spec<C, Q> with(String cteName, Supplier<SubQuery> supplier) {
        this.cteList = Collections.singletonList(CteImpl.create(cteName, supplier.get()));
        return this;
    }

    @Override
    public final Select80Spec<C, Q> with(String cteName, Function<C, SubQuery> function) {
        this.cteList = Collections.singletonList(CteImpl.create(cteName, function.apply(this.criteria)));
        return this;
    }

    @Override
    public final Select80Spec<C, Q> with(Supplier<List<Cte>> supplier) {
        return this.doWithCte(supplier.get());
    }


    @Override
    public final Select80Spec<C, Q> with(Function<C, List<Cte>> function) {
        return this.doWithCte(function.apply(this.criteria));
    }

    @Override
    public final Select80Spec<C, Q> withRecursive(String cteName, Supplier<SubQuery> supplier) {
        this.recursive = true;
        return this.with(cteName, supplier);
    }

    @Override
    public final Select80Spec<C, Q> withRecursive(String cteName, Function<C, SubQuery> function) {
        this.recursive = true;
        return this.with(cteName, function);
    }

    @Override
    public final Select80Spec<C, Q> withRecursive(Supplier<List<Cte>> supplier) {
        this.recursive = true;
        return this.doWithCte(supplier.get());
    }

    @Override
    public final Select80Spec<C, Q> withRecursive(Function<C, List<Cte>> function) {
        this.recursive = true;
        return this.doWithCte(function.apply(this.criteria));
    }

    /**
     * @see #afterOrderBy()
     */
    @Override
    public final Having80Spec<C, Q> withRollup() {
        if (this.hasOrderBy()) {
            this.orderByWithRollup = true;
        } else if (this.hasGroupBy()) {
            if (this.groupByWithRollup == null) {
                //@see this.afterOrderBy()
                this.groupByWithRollup = Boolean.TRUE;
            }
        } else {
            this.groupByWithRollup = Boolean.FALSE;
        }
        return this;
    }

    @Override
    public final Having80Spec<C, Q> ifWithRollup(Predicate<C> predicate) {
        if ((this.hasOrderBy() || this.hasGroupBy()) && predicate.test(this.criteria)) {
            this.withRollup();
        }
        return this;
    }

    @Override
    public final OrderBy80Spec<C, Q> window(String name, Expression<?> partition) {
        //TODO
        return this;
    }

    @Override
    public final OrderBy80Spec<C, Q> window(String name, Expression<?> partition, SortPart order) {
        return this;
    }

    @Override
    public final OrderBy80Spec<C, Q> window(NamedWindow namedWindow) {
        return this;
    }

    @Override
    public final OrderBy80Spec<C, Q> window(NamedWindow namedWindow1, NamedWindow namedWindow2) {
        return this;
    }

    @Override
    public final OrderBy80Spec<C, Q> window(Supplier<List<NamedWindow>> supplier) {
        return this;
    }

    @Override
    public final OrderBy80Spec<C, Q> window(Function<C, List<NamedWindow>> function) {
        return this;
    }

    @Override
    public final OrderBy80Spec<C, Q> ifWindow(Supplier<List<NamedWindow>> supplier) {
        return this;
    }

    @Override
    public final OrderBy80Spec<C, Q> ifWindow(Function<C, List<NamedWindow>> function) {
        return this;
    }

    @Override
    public final Lock80LockOfOptionSpec<C, Q> forUpdate() {
        this.lock = MySQLLock.FOR_UPDATE;
        return this;
    }

    @Override
    public final Lock80LockOfOptionSpec<C, Q> forShare() {
        this.lock = MySQLLock.SHARE;
        return this;
    }

    @Override
    public final Lock80LockOfOptionSpec<C, Q> ifForUpdate(Predicate<C> predicate) {
        if (predicate.test(this.criteria)) {
            this.lock = MySQLLock.FOR_UPDATE;
        }
        return this;
    }

    @Override
    public final Lock80LockOfOptionSpec<C, Q> ifForShare(Predicate<C> predicate) {
        if (predicate.test(this.criteria)) {
            this.lock = MySQLLock.SHARE;
        }
        return this;
    }

    @Override
    public final Union80Spec<C, Q> lockInShareMode() {
        this.lock = MySQLLock.LOCK_IN_SHARE_MODE;
        return this;
    }

    @Override
    public final Union80Spec<C, Q> ifLockInShareMode(Predicate<C> predicate) {
        if (predicate.test(this.criteria)) {
            this.lock = MySQLLock.LOCK_IN_SHARE_MODE;
        }
        return this;
    }

    @Override
    public final Lock80LockOptionSpec<C, Q> of(TableMeta<?> table) {
        if (this.lock != null) {
            this.of(Collections.singletonList(table));
        }
        return this;
    }

    @Override
    public final Lock80LockOptionSpec<C, Q> of(TableMeta<?> table1, TableMeta<?> table2) {
        if (this.lock != null) {
            this.of(Arrays.asList(table1, table2));
        }
        return this;
    }

    @Override
    public final Lock80LockOptionSpec<C, Q> of(List<TableMeta<?>> tableList) {
        final MySQLLock lock = this.lock;
        if (lock != null) {
            switch (lock) {
                case FOR_UPDATE:
                case SHARE:
                    this.ofTableList = CollectionUtils.asUnmodifiableList(tableList);
                    break;
                case LOCK_IN_SHARE_MODE:
                    break;
                default:
                    throw _Exceptions.unexpectedEnum(lock);
            }
        }
        return this;
    }

    @Override
    public final Lock80LockOptionSpec<C, Q> ifOf(Function<C, List<TableMeta<?>>> function) {
        if (this.lock != null) {
            final List<TableMeta<?>> list;
            list = function.apply(this.criteria);
            if (!CollectionUtils.isEmpty(list)) {
                this.of(list);
            }
        }
        return this;
    }

    @Override
    public final Lock80LockOptionSpec<C, Q> ifOf(Supplier<List<TableMeta<?>>> supplier) {
        if (this.lock != null) {
            final List<TableMeta<?>> list;
            list = supplier.get();
            if (!CollectionUtils.isEmpty(list)) {
                this.of(list);
            }
        }
        return this;
    }

    @Override
    public final Union80Spec<C, Q> nowait() {
        return this.lockOption(MySQLLockOption.NOWAIT);
    }

    @Override
    public final Union80Spec<C, Q> skipLocked() {
        return this.lockOption(MySQLLockOption.SKIP_LOCKED);
    }

    @Override
    public final Union80Spec<C, Q> ifNowait(Predicate<C> predicate) {
        if (this.lock != null && predicate.test(this.criteria)) {
            this.nowait();
        }
        return this;
    }

    @Override
    public final Union80Spec<C, Q> ifSkipLocked(Predicate<C> predicate) {
        if (this.lock != null && predicate.test(this.criteria)) {
            this.skipLocked();
        }
        return this;
    }

    @Override
    public final UnionOrderBy80Spec<C, Q> bracketsQuery() {
        return null;
    }

    @Override
    final OrderByWithRollup80Spec<C, Q> afterOrderBy() {
        if (this.groupByWithRollup == null) {
            this.groupByWithRollup = Boolean.FALSE;
        }
        return this;
    }

    @Override
    final UnionOrderBy80Spec<C, Q> createUnionQuery(Q left, UnionType unionType, Q right) {
        return MySQL80UnionQuery.unionQuery(left, unionType, right);
    }

    @Override
    final With80Spec<C, Q> asQueryAndQuery(UnionType unionType) {
        return MySQL80SimpleQuery.unionAndSelect(this.asQuery(), unionType);
    }

    @Override
    final Q onAsQuery(boolean outer) {
        return null;
    }

    @Override
    final void onClear() {

    }

    @Override
    final IndexHintOn80Spec<C, Q> createNoActionTableBlock() {
        return null;
    }

    @Override
    final On80Spec<C, Q> createNoActionOnBlock() {
        return null;
    }

    @Override
    final PartitionOn80Spec<C, Q> createPartitionOnBlock(JoinType joinType, TableMeta<?> table) {
        return null;
    }

    @Override
    final IndexHintOn80Spec<C, Q> createIndexHintOnBlock(JoinType joinType, TableMeta<?> table, String tableAlias) {
        return null;
    }

    @Override
    final On80Spec<C, Q> createOnBlock(JoinType joinType, TablePart tablePart, String alias) {
        return null;
    }

    @Override
    final PartitionOn80Spec<C, Q> createNoActionPartitionBlock() {
        return null;
    }

    @Override
    final PartitionJoin80Spec<C, Q> createFromBlockWithPartition(TableMeta<?> table
            , Function<MySQLFromTableBlock, IndexHintJoin80Spec<C, Q>> function) {
        return null;
    }


    @Override
    public final boolean groupByWithRollUp() {
        final Boolean withRollup = this.groupByWithRollup;
        return withRollup != null && withRollup;
    }

    @Override
    public final boolean orderByWithRollup() {
        return this.orderByWithRollup;
    }

    /*################################## blow private method ##################################*/


    private Union80Spec<C, Q> lockOption(MySQLLockOption lockOption) {
        final MySQLLock lock = this.lock;
        if (lock != null) {
            switch (lock) {
                case FOR_UPDATE:
                case SHARE:
                    this.lockOption = lockOption;
                    break;
                case LOCK_IN_SHARE_MODE:
                    break;
                default:
                    throw _Exceptions.unexpectedEnum(lock);
            }
        }
        return this;
    }

    private Select80Spec<C, Q> doWithCte(List<Cte> withCteList) {
        final List<Cte> cetList = new ArrayList<>(withCteList.size());
        for (Cte cte : withCteList) {
            _MySQLCounselor.assertCet(cte);
            cetList.add(cte);
        }
        this.cteList = cetList;
        return this;
    }


}
