package io.army.criteria.impl;

import io.army.criteria.CriteriaException;
import io.army.criteria.Query;
import io.army.criteria.TablePart;
import io.army.criteria.impl.inner._TableBlock;
import io.army.criteria.impl.inner.mysql._MySQLQuery;
import io.army.criteria.mysql.MySQLQuery;
import io.army.meta.TableMeta;
import io.army.util.CollectionUtils;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;


abstract class MySQLSimpleQuery<C, Q extends Query, SR, FT, FS, FP, JT, JS, IT, WR, AR, GR, HR, OR, LR, UR, SP>
        extends NoFromSimpleQuery<C, Q, SR, FT, FS, JT, JS, WR, AR, GR, HR, OR, LR, UR, SP>
        implements MySQLQuery, _MySQLQuery, MySQLQuery.MySQLJoinClause<C, JT, JS, IT>
        , MySQLQuery.MySQLFromClause<C, FT, FS, FP> {

    private IntoClause intoClause;

    List<TableBlock> tableBlockList;

    MySQLSimpleQuery(CriteriaContext criteriaContext) {
        super(criteriaContext);
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
    public final IntoClause intoClause() {
        return this.intoClause;
    }

    @Override
    public final List<? extends _TableBlock> tableBlockList() {
        prepared();
        return this.tableBlockList;
    }

    abstract IT doJoinTablePartition(JoinType joinType, TableMeta<?> table);


    abstract IT createNoActionPartitionBlock();


    private IT doIfJointTablePartition(Predicate<C> predicate, JoinType joinType, TableMeta<?> table) {
        final IT block;
        if (predicate.test(this.criteria)) {
            block = doJoinTablePartition(joinType, table);
        } else {
            block = createNoActionPartitionBlock();
        }
        return block;
    }


    static CriteriaException intoOptionDuplication() {
        String m = "INTO clause within same statement can appear only once, not in multiple positions.";
        return new CriteriaException(m);
    }

    static CriteriaException intoVarNamesIsEmpty() {
        return new CriteriaException("INTO clause within var name list must not empty.");
    }

    private static final class IntoClauseImpl implements IntoClause {

        private final IntoPosition position;

        private final List<String> intoLit;

        private IntoClauseImpl(IntoPosition position, List<String> intoLit) {
            this.position = position;
            this.intoLit = CollectionUtils.asUnmodifiableList(intoLit);
        }

        @Override
        public IntoPosition position() {
            return this.position;
        }

        @Override
        public List<String> intoList() {
            return this.intoLit;
        }

    }


}
