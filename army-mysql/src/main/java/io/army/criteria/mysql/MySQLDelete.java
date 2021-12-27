package io.army.criteria.mysql;

import io.army.criteria.*;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.meta.SingleTableMeta;
import io.army.meta.TableMeta;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * <p>
 * This interface representing MySQL delete statement,the instance of this interface can only be parsed by MySQL dialect instance.
 * </p>
 */
public interface MySQLDelete extends Delete, MySQLDml {


    interface SingleDeleteSpec<C> {

        SinglePartitionSpec<C> deleteFrom(SingleTableMeta<?> table);

    }


    interface SinglePartitionSpec<C> extends SingleWhereSpec<C>,
            MySQLDml.SingleIndexHintCommandClause<C, MySQLDelete.SingleWhereSpec<C>> {

        SingleWhereSpec<C> partition(String partitionName);

        SingleWhereSpec<C> partition(String partitionName1, String partitionNam2);

        SingleWhereSpec<C> partition(List<String> partitionNameList);

        SingleWhereSpec<C> ifPartition(Function<C, List<String>> function);

    }


    interface SingleWhereSpec<C> {

        SingleWhereAndSpec<C> where(IPredicate predicate);

        OrderBySpec<C> where(List<IPredicate> predicateList);

        OrderBySpec<C> where(Function<C, List<IPredicate>> function);

        OrderBySpec<C> where(Supplier<List<IPredicate>> supplier);

    }

    interface SingleWhereAndSpec<C> extends MySQLDelete.OrderBySpec<C>, Delete.WhereAndSpec<C> {

        @Override
        SingleWhereAndSpec<C> and(IPredicate predicate);

        @Override
        SingleWhereAndSpec<C> and(Function<C, IPredicate> function);

        @Override
        SingleWhereAndSpec<C> and(Supplier<IPredicate> supplier);

        /**
         * @see Expression#ifEqual(Object)
         */
        @Override
        SingleWhereAndSpec<C> ifAnd(@Nullable IPredicate predicate);

        @Override
        SingleWhereAndSpec<C> ifAnd(Function<C, IPredicate> function);

        @Override
        SingleWhereAndSpec<C> ifAnd(Supplier<IPredicate> supplier);

    }


    interface OrderBySpec<C> extends MySQLDelete.SingleLimitSpec<C> {

        SingleLimitSpec<C> orderBy(SortPart sortPart);

        SingleLimitSpec<C> orderBy(SortPart sortPart1, SortPart sortPart2);

        SingleLimitSpec<C> orderBy(List<SortPart> sortPartList);

        SingleLimitSpec<C> orderBy(Function<C, List<SortPart>> function);

        SingleLimitSpec<C> orderBy(Supplier<List<SortPart>> supplier);

        SingleLimitSpec<C> ifOrderBy(@Nullable SortPart sortPart);

        SingleLimitSpec<C> ifOrderBy(Supplier<List<SortPart>> supplier);

        SingleLimitSpec<C> ifOrderBy(Function<C, List<SortPart>> function);
    }

    interface SingleLimitSpec<C> extends Delete.DeleteSpec {

        DeleteSpec limit(long rowCount);

        DeleteSpec limit(Function<C, Long> function);

        DeleteSpec limit(Supplier<Long> supplier);

        DeleteSpec ifLimit(Function<C, Long> function);

        DeleteSpec ifLimit(Supplier<Long> supplier);

    }


    /*################################## blow batch single delete api interface ##################################*/


    interface BatchSingleDeleteSpec<C> {

        BatchSinglePartitionSpec<C> deleteFrom(SingleTableMeta<?> table);

    }


    interface BatchSinglePartitionSpec<C> extends MySQLDelete.BatchSingleIndexHintCommandSpec<C> {

        BatchSingleIndexHintCommandSpec<C> partition(String partitionName);

        BatchSingleIndexHintCommandSpec<C> partition(String partitionName1, String partitionNam2);

        BatchSingleIndexHintCommandSpec<C> partition(List<String> partitionNameList);

        BatchSingleIndexHintCommandSpec<C> ifPartition(Function<C, List<String>> function);

    }


    interface BatchSingleIndexHintCommandSpec<C> extends MySQLDelete.BatchSingleWhereSpec<C> {

        BatchSingleIndexWordClause<C> use();

        BatchSingleIndexWordClause<C> ignore();

        BatchSingleIndexWordClause<C> force();

        /**
         * @return clause , clause no action if predicate return false.
         */
        BatchSingleIndexWordClause<C> ifUse(Predicate<C> predicate);


        /**
         * @return clause , clause no action if predicate return false.
         */
        BatchSingleIndexWordClause<C> ifIgnore(Predicate<C> predicate);

        /**
         * @return clause , clause no action if predicate return false.
         */
        BatchSingleIndexWordClause<C> ifForce(Predicate<C> predicate);

    }

    interface BatchSingleIndexWordClause<C> {

        BatchSingleOrderByClause<C> index();

        BatchSingleOrderByClause<C> key();

        BatchSingleWhereSpec<C> index(List<String> indexNameList);

        BatchSingleWhereSpec<C> key(List<String> indexNameList);

    }


    interface BatchSingleOrderByClause<C> {

        BatchSingleWhereSpec<C> forOrderBy(List<String> indexNameList);
    }


    interface BatchSingleWhereSpec<C> extends BatchDeleteWhereSpec<C> {

        BatchSingleWhereAndSpec<C> where(IPredicate predicate);

        BatchOrderBySpec<C> where(List<IPredicate> predicateList);

        BatchOrderBySpec<C> where(Function<C, List<IPredicate>> function);

        BatchOrderBySpec<C> where(Supplier<List<IPredicate>> supplier);

    }

    interface BatchSingleWhereAndSpec<C> extends MySQLDelete.BatchOrderBySpec<C>, Delete.BatchWhereAndSpec<C> {

        @Override
        BatchSingleWhereAndSpec<C> and(IPredicate predicate);

        @Override
        BatchSingleWhereAndSpec<C> and(Function<C, IPredicate> function);

        @Override
        BatchSingleWhereAndSpec<C> and(Supplier<IPredicate> supplier);

        /**
         * @see Expression#ifEqual(Object)
         */
        @Override
        BatchSingleWhereAndSpec<C> ifAnd(@Nullable IPredicate predicate);

        @Override
        BatchSingleWhereAndSpec<C> ifAnd(Function<C, IPredicate> function);

        @Override
        BatchSingleWhereAndSpec<C> ifAnd(Supplier<IPredicate> supplier);

    }

    interface BatchOrderBySpec<C> extends MySQLDelete.BatchSingleLimitSpec<C> {

        BatchSingleLimitSpec<C> orderBy(SortPart sortPart);

        BatchSingleLimitSpec<C> orderBy(SortPart sortPart1, SortPart sortPart2);

        BatchSingleLimitSpec<C> orderBy(List<SortPart> sortPartList);

        BatchSingleLimitSpec<C> orderBy(Function<C, List<SortPart>> function);

        BatchSingleLimitSpec<C> orderBy(Supplier<List<SortPart>> supplier);

        BatchSingleLimitSpec<C> ifOrderBy(@Nullable SortPart sortPart);

        BatchSingleLimitSpec<C> ifOrderBy(Supplier<List<SortPart>> supplier);

        BatchSingleLimitSpec<C> ifOrderBy(Function<C, List<SortPart>> function);

    }


    interface BatchSingleLimitSpec<C> extends Delete.BatchParamSpec<C> {

        BatchParamSpec<C> limit(long rowCount);

        BatchParamSpec<C> limit(Function<C, Long> function);

        BatchParamSpec<C> limit(Supplier<Long> supplier);

        BatchParamSpec<C> ifLimit(Function<C, Long> function);

        BatchParamSpec<C> ifLimit(Supplier<Long> supplier);

    }

    /*################################## blow multi-table update api interface ##################################*/

    /**
     * @param <C> java type of criteria,see below:
     *            <ul>
     *               <li>{@link io.army.criteria.impl.MySQLs#multiUpdate57(Object)}</li>
     *               <li>{@link io.army.criteria.impl.MySQLs#multiUpdate80(Object)}</li>
     *            </ul>
     */
    interface MultiDeleteSpec<C> {

        MultiFromSpec<C> delete(List<TableMeta<?>> tableList);

        MultiFromSpec<C> delete(Function<C, List<TableMeta<?>>> function);

        MultiPartitionJoinSpec<C> deleteFrom(List<TableMeta<?>> tableList);

        MultiPartitionJoinSpec<C> deleteFrom(Function<C, List<TableMeta<?>>> function);


    }

    interface MultiFromSpec<C> {

        MultiPartitionJoinSpec<C> from(TableMeta<?> table);

        MultiIndexHintCommandJoinSpec<C> from(TableMeta<?> table, String tableAlias);


    }

    /**
     * @param <C> java type of criteria,see below:
     *            <ul>
     *               <li>{@link io.army.criteria.impl.MySQLs#multiUpdate57(Object)}</li>
     *               <li>{@link io.army.criteria.impl.MySQLs#multiUpdate80(Object)}</li>
     *            </ul>
     */
    interface MultiPartitionJoinSpec<C> extends MySQLDelete.MultiAsJoinSpec<C> {

        MultiAsJoinSpec<C> partition(String partitionName);

        MultiAsJoinSpec<C> partition(String partitionName1, String partitionNam2);

        MultiAsJoinSpec<C> partition(List<String> partitionNameList);

        MultiAsJoinSpec<C> ifPartition(Function<C, List<String>> function);

    }

    /**
     * @param <C> java type of criteria,see below:
     *            <ul>
     *               <li>{@link io.army.criteria.impl.MySQLs#multiUpdate57(Object)}</li>
     *               <li>{@link io.army.criteria.impl.MySQLs#multiUpdate80(Object)}</li>
     *            </ul>
     */
    interface MultiAsJoinSpec<C> {

        MultiIndexHintCommandJoinSpec<C> as(String tableAlias);
    }

    /**
     * @param <C> java type of criteria,see below:
     *            <ul>
     *               <li>{@link io.army.criteria.impl.MySQLs#multiUpdate57(Object)}</li>
     *               <li>{@link io.army.criteria.impl.MySQLs#multiUpdate80(Object)}</li>
     *            </ul>
     */
    interface MultiIndexHintCommandJoinSpec<C> extends MySQLDelete.JoinSpec<C>
            , MySQLDelete.MultiIndexHintCommandClause<C, MySQLDelete.JoinSpec<C>> {

    }

    /**
     * @param <C> java type of criteria,see below:
     *            <ul>
     *               <li>{@link io.army.criteria.impl.MySQLs#multiUpdate57(Object)}</li>
     *               <li>{@link io.army.criteria.impl.MySQLs#multiUpdate80(Object)}</li>
     *            </ul>
     */
    interface MultiPartitionOnSpec<C> extends MultiAsOnSpec<C> {

        MultiAsOnSpec<C> partition(String partitionName);

        MultiAsOnSpec<C> partition(String partitionName1, String partitionNam2);

        MultiAsOnSpec<C> partition(List<String> partitionNameList);

        MultiAsOnSpec<C> ifPartition(Function<C, List<String>> function);

    }

    /**
     * @param <C> java type of criteria,see below:
     *            <ul>
     *               <li>{@link io.army.criteria.impl.MySQLs#multiUpdate57(Object)}</li>
     *               <li>{@link io.army.criteria.impl.MySQLs#multiUpdate80(Object)}</li>
     *            </ul>
     */
    interface MultiAsOnSpec<C> {

        MultiIndexHintCommandOnSpec<C> as(String tableAlias);
    }

    /**
     * @param <C> java type of criteria,see below:
     *            <ul>
     *               <li>{@link io.army.criteria.impl.MySQLs#multiUpdate57(Object)}</li>
     *               <li>{@link io.army.criteria.impl.MySQLs#multiUpdate80(Object)}</li>
     *            </ul>
     */
    interface MultiIndexHintCommandOnSpec<C> extends MySQLDelete.OnSpec<C>
            , MySQLDelete.MultiIndexHintCommandClause<C, MySQLDelete.OnSpec<C>> {

    }


    interface JoinSpec<C> extends MultiSetSpec<C> {

        MultiPartitionOnSpec<C> leftJoin(TableMeta<?> table);

        MultiIndexHintCommandOnSpec<C> leftJoin(TableMeta<?> table, String tableAlias);

        OnSpec<C> leftJoin(Function<C, SubQuery> function, String subQueryAlia);

        OnSpec<C> leftJoinGroup(Function<C, TablePartGroup> function);

        MultiPartitionOnSpec<C> ifLeftJoin(Predicate<C> predicate, TableMeta<?> table);

        MultiIndexHintCommandOnSpec<C> ifLeftJoin(Predicate<C> predicate, TableMeta<?> table, String tableAlias);

        OnSpec<C> ifLeftJoin(Function<C, SubQuery> function, String subQueryAlia);

        OnSpec<C> ifLeftJoinGroup(Function<C, TablePartGroup> function);

        MultiPartitionOnSpec<C> join(TableMeta<?> table);

        MultiIndexHintCommandOnSpec<C> join(TableMeta<?> table, String tableAlias);

        OnSpec<C> join(Function<C, SubQuery> function, String subQueryAlia);

        OnSpec<C> joinGroup(Function<C, TablePartGroup> function);

        MultiPartitionOnSpec<C> ifJoin(Predicate<C> predicate, TableMeta<?> table);

        MultiIndexHintCommandOnSpec<C> ifJoin(Predicate<C> predicate, TableMeta<?> table, String tableAlias);

        OnSpec<C> ifJoin(Function<C, SubQuery> function, String subQueryAlia);

        OnSpec<C> ifJoinGroup(Function<C, TablePartGroup> function);

        MultiPartitionOnSpec<C> rightJoin(TableMeta<?> table);

        MultiIndexHintCommandOnSpec<C> rightJoin(TableMeta<?> table, String tableAlias);

        OnSpec<C> rightJoin(Function<C, SubQuery> function, String subQueryAlia);

        OnSpec<C> rightJoinGroup(Function<C, TablePartGroup> function);

        MultiPartitionOnSpec<C> ifRightJoin(Predicate<C> predicate, TableMeta<?> table);

        MultiIndexHintCommandOnSpec<C> ifRightJoin(Predicate<C> predicate, TableMeta<?> table, String tableAlias);

        OnSpec<C> ifRightJoin(Function<C, SubQuery> function, String subQueryAlia);

        OnSpec<C> ifRightJoinGroup(Function<C, TablePartGroup> function);

        MultiPartitionOnSpec<C> straightJoin(TableMeta<?> table);

        MultiIndexHintCommandOnSpec<C> straightJoin(TableMeta<?> table, String tableAlias);

        OnSpec<C> straightJoin(Function<C, SubQuery> function, String subQueryAlia);

        OnSpec<C> straightJoinGroup(Function<C, TablePartGroup> function);

        MultiPartitionOnSpec<C> ifStraightJoin(Predicate<C> predicate, TableMeta<?> table);

        MultiIndexHintCommandOnSpec<C> ifStraightJoin(Predicate<C> predicate, TableMeta<?> table, String tableAlias);

        OnSpec<C> ifStraightJoin(Function<C, SubQuery> function, String subQueryAlia);

        OnSpec<C> ifStraightJoinGroup(Function<C, TablePartGroup> function);

    }

    interface OnSpec<C> {

        JoinSpec<C> on(List<IPredicate> predicateList);

        JoinSpec<C> on(IPredicate predicate);

        JoinSpec<C> on(IPredicate predicate1, IPredicate predicate2);

        JoinSpec<C> on(Function<C, List<IPredicate>> function);

        JoinSpec<C> on(Supplier<List<IPredicate>> supplier);

        JoinSpec<C> onId();

    }


    interface MultiSetSpec<C> {

        MultiWhereSpec<C> set(List<FieldMeta<?, ?>> fieldList, List<Expression<?>> valueList);

        MultiWhereSpec<C> set(FieldMeta<?, ?> field, @Nullable Object value);

        MultiWhereSpec<C> set(FieldMeta<?, ?> field, Expression<?> value);

        MultiWhereSpec<C> set(FieldMeta<?, ?> field, Function<C, Expression<?>> function);

        MultiWhereSpec<C> set(FieldMeta<?, ?> field, Supplier<Expression<?>> supplier);

        MultiWhereSpec<C> setNull(FieldMeta<?, ?> field);

        MultiWhereSpec<C> setDefault(FieldMeta<?, ?> field);

        <F extends Number> MultiWhereSpec<C> setPlus(FieldMeta<?, F> field, F value);

        <F extends Number> MultiWhereSpec<C> setPlus(FieldMeta<?, F> field, Expression<?> value);

        <F extends Number> MultiWhereSpec<C> setMinus(FieldMeta<?, F> field, F value);

        <F extends Number> MultiWhereSpec<C> setMinus(FieldMeta<?, F> field, Expression<?> value);

        <F extends Number> MultiWhereSpec<C> setMultiply(FieldMeta<?, F> field, F value);

        <F extends Number> MultiWhereSpec<C> setMultiply(FieldMeta<?, F> field, Expression<?> value);

        <F extends Number> MultiWhereSpec<C> setDivide(FieldMeta<?, F> field, F value);

        <F extends Number> MultiWhereSpec<C> setDivide(FieldMeta<?, F> field, Expression<F> value);

        <F extends Number> MultiWhereSpec<C> setMod(FieldMeta<?, F> field, F value);

        <F extends Number> MultiWhereSpec<C> setMod(FieldMeta<?, F> field, Expression<F> value);

        MultiWhereSpec<C> ifSet(List<FieldMeta<?, ?>> fieldList, List<Expression<?>> valueList);

        MultiWhereSpec<C> ifSet(FieldMeta<?, ?> field, @Nullable Object value);

        MultiWhereSpec<C> ifSet(FieldMeta<?, ?> field, Function<C, Expression<?>> function);

        MultiWhereSpec<C> ifSet(FieldMeta<?, ?> field, Supplier<Expression<?>> supplier);

        <F extends Number> MultiWhereSpec<C> ifSetPlus(FieldMeta<?, ?> field, @Nullable F value);

        <F extends Number> MultiWhereSpec<C> ifSetMinus(FieldMeta<?, ?> field, @Nullable F value);

        <F extends Number> MultiWhereSpec<C> ifSetMultiply(FieldMeta<?, ?> field, @Nullable F value);

        <F extends Number> MultiWhereSpec<C> ifSetDivide(FieldMeta<?, ?> field, @Nullable F value);

        <F extends Number> MultiWhereSpec<C> ifSetMod(FieldMeta<?, ?> field, @Nullable F value);

    }

    interface MultiWhereSpec<C> extends MultiSetSpec<C> {

        UpdateSpec where(List<IPredicate> predicates);

        UpdateSpec where(Function<C, List<IPredicate>> function);

        UpdateSpec where(Supplier<List<IPredicate>> supplier);

        WhereAndSpec<C> where(IPredicate predicate);

    }

    /*################################## blow batch multi-table update api interface ##################################*/


    /**
     * @param <C> java type of criteria,see below:
     *            <ul>
     *               <li>{@link MySQLs#batchMultiUpdate57(Object)}</li>
     *               <li>{@link MySQLs#batchMultiUpdate80(Object)}</li>
     *            </ul>
     */
    interface BatchMultiUpdateSpec<C> {

        BatchMultiPartitionJoinSpec<C> update(TableMeta<?> table);

        BatchMultiIndexHintCommandJoinSpec<C> update(TableMeta<?> table, String tableAlias);

        BatchJoinSpec<C> update(Function<C, SubQuery> function, String subQueryAlias);

        BatchJoinSpec<C> updateGroup(Function<C, SubQuery> function, String groupAlias);

    }

    /**
     * @param <C> java type of criteria,see below:
     *            <ul>
     *               <li>{@link MySQLs#batchMultiUpdate57(Object)}</li>
     *               <li>{@link MySQLs#batchMultiUpdate80(Object)}</li>
     *            </ul>
     */
    interface BatchMultiPartitionJoinSpec<C> extends MySQLDelete.BatchMultiAsJoinSpec<C> {

        BatchMultiAsJoinSpec<C> partition(String partitionName);

        BatchMultiAsJoinSpec<C> partition(String partitionName1, String partitionNam2);

        BatchMultiAsJoinSpec<C> partition(List<String> partitionNameList);

        BatchMultiAsJoinSpec<C> ifPartition(Function<C, List<String>> function);

    }

    /**
     * @param <C> java type of criteria,see below:
     *            <ul>
     *               <li>{@link MySQLs#batchMultiUpdate57(Object)}</li>
     *               <li>{@link MySQLs#batchMultiUpdate80(Object)}</li>
     *            </ul>
     */
    interface BatchMultiAsJoinSpec<C> {

        BatchMultiIndexHintCommandJoinSpec<C> as(String tableAlias);
    }

    /**
     * @param <C> java type of criteria,see below:
     *            <ul>
     *               <li>{@link MySQLs#batchMultiUpdate57(Object)}</li>
     *               <li>{@link MySQLs#batchMultiUpdate80(Object)}</li>
     *            </ul>
     */
    interface BatchMultiIndexHintCommandJoinSpec<C> extends MySQLDelete.BatchJoinSpec<C>
            , MySQLDelete.MultiIndexHintCommandClause<C, MySQLDelete.BatchJoinSpec<C>> {

    }

    /**
     * @param <C> java type of criteria,see below:
     *            <ul>
     *               <li>{@link MySQLs#batchMultiUpdate57(Object)}</li>
     *               <li>{@link MySQLs#batchMultiUpdate80(Object)}</li>
     *            </ul>
     */
    interface BatchMultiPartitionOnSpec<C> extends MySQLDelete.BatchMultiAsOnSpec<C> {

        BatchMultiAsOnSpec<C> partition(String partitionName);

        BatchMultiAsOnSpec<C> partition(String partitionName1, String partitionNam2);

        BatchMultiAsOnSpec<C> partition(List<String> partitionNameList);

        BatchMultiAsOnSpec<C> ifPartition(Function<C, List<String>> function);

    }

    /**
     * @param <C> java type of criteria,see below:
     *            <ul>
     *               <li>{@link MySQLs#batchMultiUpdate57(Object)}</li>
     *               <li>{@link MySQLs#batchMultiUpdate80(Object)}</li>
     *            </ul>
     */
    interface BatchMultiAsOnSpec<C> {

        BatchMultiIndexHintCommandOnSpec<C> as(String tableAlias);
    }

    /**
     * @param <C> java type of criteria,see below:
     *            <ul>
     *               <li>{@link MySQLs#batchMultiUpdate57(Object)}</li>
     *               <li>{@link MySQLs#batchMultiUpdate80(Object)}</li>
     *            </ul>
     */
    interface BatchMultiIndexHintCommandOnSpec<C> extends MySQLDelete.BatchOnSpec<C>
            , MySQLDelete.MultiIndexHintCommandClause<C, MySQLDelete.BatchOnSpec<C>> {

    }


    /**
     * @param <C> java type of criteria,see below:
     *            <ul>
     *               <li>{@link MySQLs#batchMultiUpdate57(Object)}</li>
     *               <li>{@link MySQLs#batchMultiUpdate80(Object)}</li>
     *            </ul>
     */
    interface BatchJoinSpec<C> extends MySQLDelete.BatchMultiSetSpec<C> {

        BatchMultiPartitionOnSpec<C> leftJoin(TableMeta<?> table);

        BatchMultiIndexHintCommandOnSpec<C> leftJoin(TableMeta<?> table, String tableAlias);

        BatchOnSpec<C> leftJoin(Function<C, SubQuery> function, String subQueryAlia);

        BatchOnSpec<C> leftJoinGroup(Function<C, TablePartGroup> function);

        BatchMultiPartitionOnSpec<C> ifLeftJoin(Predicate<C> predicate, TableMeta<?> table);

        BatchMultiIndexHintCommandOnSpec<C> ifLeftJoin(Predicate<C> predicate, TableMeta<?> table, String tableAlias);

        BatchOnSpec<C> ifLeftJoin(Function<C, SubQuery> function, String subQueryAlia);

        BatchOnSpec<C> ifLeftJoinGroup(Function<C, TablePartGroup> function);

        BatchMultiPartitionOnSpec<C> join(TableMeta<?> table);

        BatchMultiIndexHintCommandOnSpec<C> join(TableMeta<?> table, String tableAlias);

        BatchOnSpec<C> join(Function<C, SubQuery> function, String subQueryAlia);

        BatchOnSpec<C> joinGroup(Function<C, TablePartGroup> function);

        BatchMultiPartitionOnSpec<C> ifJoin(Predicate<C> predicate, TableMeta<?> table);

        BatchMultiIndexHintCommandOnSpec<C> ifJoin(Predicate<C> predicate, TableMeta<?> table, String tableAlias);

        BatchOnSpec<C> ifJoin(Function<C, SubQuery> function, String subQueryAlia);

        BatchOnSpec<C> ifJoinGroup(Function<C, TablePartGroup> function);

        BatchMultiPartitionOnSpec<C> rightJoin(TableMeta<?> table);

        BatchMultiIndexHintCommandOnSpec<C> rightJoin(TableMeta<?> table, String tableAlias);

        BatchOnSpec<C> rightJoin(Function<C, SubQuery> function, String subQueryAlia);

        BatchOnSpec<C> rightJoinGroup(Function<C, TablePartGroup> function);

        BatchMultiPartitionOnSpec<C> ifRightJoin(Predicate<C> predicate, TableMeta<?> table);

        BatchMultiIndexHintCommandOnSpec<C> ifRightJoin(Predicate<C> predicate, TableMeta<?> table, String tableAlias);

        BatchOnSpec<C> ifRightJoin(Function<C, SubQuery> function, String subQueryAlia);

        BatchOnSpec<C> ifRightJoinGroup(Function<C, TablePartGroup> function);

        BatchMultiPartitionOnSpec<C> straightJoin(TableMeta<?> table);

        BatchMultiIndexHintCommandOnSpec<C> straightJoin(TableMeta<?> table, String tableAlias);

        BatchOnSpec<C> straightJoin(Function<C, SubQuery> function, String subQueryAlia);

        BatchOnSpec<C> straightJoinGroup(Function<C, TablePartGroup> function);

        BatchMultiPartitionOnSpec<C> ifStraightJoin(Predicate<C> predicate, TableMeta<?> table);

        BatchMultiIndexHintCommandOnSpec<C> ifStraightJoin(Predicate<C> predicate, TableMeta<?> table, String tableAlias);

        BatchOnSpec<C> ifStraightJoin(Function<C, SubQuery> function, String subQueryAlia);

        BatchOnSpec<C> ifStraightJoinGroup(Function<C, TablePartGroup> function);

    }

    /**
     * @param <C> java type of criteria,see below:
     *            <ul>
     *               <li>{@link MySQLs#batchMultiUpdate57(Object)}</li>
     *               <li>{@link MySQLs#batchMultiUpdate80(Object)}</li>
     *            </ul>
     */
    interface BatchOnSpec<C> {

        BatchJoinSpec<C> on(List<IPredicate> predicateList);

        BatchJoinSpec<C> on(IPredicate predicate);

        BatchJoinSpec<C> on(IPredicate predicate1, IPredicate predicate2);

        BatchJoinSpec<C> on(Function<C, List<IPredicate>> function);

        BatchJoinSpec<C> on(Supplier<List<IPredicate>> supplier);

        BatchJoinSpec<C> onId();

    }


    /**
     * @param <C> java type of criteria,see below:
     *            <ul>
     *               <li>{@link MySQLs#batchMultiUpdate57(Object)}</li>
     *               <li>{@link MySQLs#batchMultiUpdate80(Object)}</li>
     *            </ul>
     */
    interface BatchMultiSetSpec<C> {

        BatchMultiWhereSpec<C> set(List<FieldMeta<?, ?>> fieldList);

        BatchMultiWhereSpec<C> set(FieldMeta<?, ?> field);

        BatchMultiWhereSpec<C> set(FieldMeta<?, ?> field, Expression<?> value);

        BatchMultiWhereSpec<C> set(FieldMeta<?, ?> field, Function<C, Expression<?>> function);

        BatchMultiWhereSpec<C> set(FieldMeta<?, ?> field, Supplier<Expression<?>> supplier);

        BatchMultiWhereSpec<C> setNull(FieldMeta<?, ?> field);

        BatchMultiWhereSpec<C> setDefault(FieldMeta<?, ?> field);

        <F extends Number> BatchMultiWhereSpec<C> setPlus(FieldMeta<?, F> field);

        <F extends Number> BatchMultiWhereSpec<C> setPlus(FieldMeta<?, F> field, Expression<F> value);

        <F extends Number> BatchMultiWhereSpec<C> setMinus(FieldMeta<?, F> field);

        <F extends Number> BatchMultiWhereSpec<C> setMinus(FieldMeta<?, F> field, Expression<F> value);

        <F extends Number> BatchMultiWhereSpec<C> setMultiply(FieldMeta<?, F> field);

        <F extends Number> BatchMultiWhereSpec<C> setMultiply(FieldMeta<?, F> field, Expression<F> value);

        <F extends Number> BatchMultiWhereSpec<C> setDivide(FieldMeta<?, F> field);

        <F extends Number> BatchMultiWhereSpec<C> setDivide(FieldMeta<?, F> field, Expression<F> value);

        <F extends Number> BatchMultiWhereSpec<C> setMod(FieldMeta<?, F> field);

        <F extends Number> BatchMultiWhereSpec<C> setMod(FieldMeta<?, F> field, Expression<F> value);

        BatchMultiWhereSpec<C> ifSetDefault(Predicate<C> predicate, FieldMeta<?, ?> field);

        BatchMultiWhereSpec<C> ifSet(Function<C, List<FieldMeta<?, ?>>> function);

        BatchMultiWhereSpec<C> ifSet(Supplier<List<FieldMeta<?, ?>>> supplier);

        BatchMultiWhereSpec<C> ifSet(Predicate<C> predicate, FieldMeta<?, ?> field);

        <F> BatchMultiWhereSpec<C> ifSet(FieldMeta<?, ?> field, Function<C, Expression<F>> function);

        <F> BatchMultiWhereSpec<C> ifSet(FieldMeta<?, ?> field, Supplier<Expression<F>> supplier);

        <F extends Number> BatchMultiWhereSpec<C> ifSetPlus(Predicate<C> predicate, FieldMeta<?, F> field);

        <F extends Number> BatchMultiWhereSpec<C> ifSetMinus(Predicate<C> predicate, FieldMeta<?, F> field);

        <F extends Number> BatchMultiWhereSpec<C> ifSetMultiply(Predicate<C> predicate, FieldMeta<?, F> field);

        <F extends Number> BatchMultiWhereSpec<C> ifSetDivide(Predicate<C> predicate, FieldMeta<?, F> field);

        <F extends Number> BatchMultiWhereSpec<C> ifSetMod(Predicate<C> predicate, FieldMeta<?, F> field);

    }

    /**
     * @param <C> java type of criteria,see below:
     *            <ul>
     *               <li>{@link MySQLs#batchMultiUpdate57(Object)}</li>
     *               <li>{@link MySQLs#batchMultiUpdate80(Object)}</li>
     *            </ul>
     */
    interface BatchMultiWhereSpec<C> extends MySQLDelete.BatchMultiSetSpec<C> {

        BatchParamSpec<C> where(List<IPredicate> predicates);

        BatchParamSpec<C> where(Function<C, List<IPredicate>> function);

        BatchParamSpec<C> where(Supplier<List<IPredicate>> supplier);

        BatchWhereAndSpec<C> where(IPredicate predicate);

    }


}
