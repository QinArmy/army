package io.army.criteria.mysql;

import io.army.criteria.IPredicate;
import io.army.criteria.SubQuery;
import io.army.criteria.TablePartGroup;
import io.army.criteria.Update;
import io.army.criteria.impl.MySQLs;
import io.army.domain.IDomain;
import io.army.meta.ChildDomain;
import io.army.meta.SingleTableMeta;
import io.army.meta.TableMeta;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;


/**
 * <p>
 * This interface representing MySQL update statement,the instance of this interface can only be parsed by MySQL dialect instance.
 * </p>
 */
public interface MySQLUpdate extends Update, MySQLDml {

    interface SingleUpdateSpec<C> {

        <T extends IDomain> SinglePartitionSpec<T, C> update(SingleTableMeta<T> table);

        <P extends IDomain, T extends P> SinglePartitionSpec<P, C> update(ChildDomain<P, T> table);
    }


    interface SinglePartitionSpec<T extends IDomain, C> extends MySQLUpdate.SingleIndexHintCommandSpec<T, C> {

        SingleIndexHintCommandSpec<T, C> partition(String partitionName);

        SingleIndexHintCommandSpec<T, C> partition(String partitionName1, String partitionNam2);

        SingleIndexHintCommandSpec<T, C> partition(List<String> partitionNameList);

        SingleIndexHintCommandSpec<T, C> ifPartition(Function<C, List<String>> function);

    }


    interface SingleIndexHintCommandSpec<T extends IDomain, C>
            extends SetClause<T, C, SingleWhereSpec<T, C>>
            , MySQLDml.SingleIndexHintCommandClause<C, SetClause<T, C, SingleWhereSpec<T, C>>> {

    }


    interface SingleWhereSpec<T extends IDomain, C> extends SetClause<T, C, SingleWhereSpec<T, C>> {

        SingleWhereAndSpec<C, Update> where(IPredicate predicate);

        SingleOrderBySpec<C, Update> where(List<IPredicate> predicateList);

        SingleOrderBySpec<C, Update> where(Function<C, List<IPredicate>> function);

        SingleOrderBySpec<C, Update> where(Supplier<List<IPredicate>> supplier);

    }



    /*################################## blow batch single update api interface ##################################*/


    interface BatchSingleUpdateSpec<C> {

        <T extends IDomain> BatchSinglePartitionSpec<T, C> update(SingleTableMeta<T> table);

        <P extends IDomain, T extends P> BatchSinglePartitionSpec<P, C> update(ChildDomain<P, T> table);
    }


    interface BatchSinglePartitionSpec<T extends IDomain, C> extends MySQLUpdate.BatchSingleIndexHintCommandSpec<T, C> {

        BatchSingleIndexHintCommandSpec<T, C> partition(String partitionName);

        BatchSingleIndexHintCommandSpec<T, C> partition(String partitionName1, String partitionNam2);

        BatchSingleIndexHintCommandSpec<T, C> partition(List<String> partitionNameList);

        BatchSingleIndexHintCommandSpec<T, C> ifPartition(Function<C, List<String>> function);

    }


    interface BatchSingleIndexHintCommandSpec<T extends IDomain, C>
            extends BatchSetClause<T, C, BatchSingleWhereSpec<T, C>>
            , MySQLDml.SingleIndexHintCommandClause<C, SetClause<T, C, BatchSingleWhereSpec<T, C>>> {

    }


    interface BatchSingleWhereSpec<T extends IDomain, C>
            extends BatchSetClause<T, C, BatchSingleWhereSpec<T, C>> {

        BatchSingleWhereAndSpec<C, Update> where(IPredicate predicate);

        BatchOrderBySpec<C, Update> where(List<IPredicate> predicateList);

        BatchOrderBySpec<C, Update> where(Function<C, List<IPredicate>> function);

        BatchOrderBySpec<C, Update> where(Supplier<List<IPredicate>> supplier);

    }


    /*################################## blow multi-table update api interface ##################################*/

    /**
     * @param <C> java type of criteria,see below:
     *            <ul>
     *               <li>{@link io.army.criteria.impl.MySQLs#multiUpdate57(Object)}</li>
     *               <li>{@link io.army.criteria.impl.MySQLs#multiUpdate80(Object)}</li>
     *            </ul>
     */
    interface MultiUpdateSpec<C> {

        MultiPartitionJoinSpec<C> update(TableMeta<?> table);

        MultiIndexHintCommandJoinSpec<C> update(TableMeta<?> table, String tableAlias);

        MultiJoinSpec<C> update(Function<C, SubQuery> function, String subQueryAlias);

        MultiJoinSpec<C> updateGroup(Function<C, TablePartGroup> function, String groupAlias);

    }

    /**
     * @param <C> java type of criteria,see below:
     *            <ul>
     *               <li>{@link io.army.criteria.impl.MySQLs#multiUpdate57(Object)}</li>
     *               <li>{@link io.army.criteria.impl.MySQLs#multiUpdate80(Object)}</li>
     *            </ul>
     */
    interface MultiPartitionJoinSpec<C> extends MySQLUpdate.MultiAsJoinSpec<C> {

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
    interface MultiIndexHintCommandJoinSpec<C> extends MySQLUpdate.MultiJoinSpec<C>
            , MySQLDml.MultiIndexHintCommandClause<C, MySQLUpdate.MultiJoinSpec<C>> {

    }

    /**
     * @param <C> java type of criteria,see below:
     *            <ul>
     *               <li>{@link io.army.criteria.impl.MySQLs#multiUpdate57(Object)}</li>
     *               <li>{@link io.army.criteria.impl.MySQLs#multiUpdate80(Object)}</li>
     *            </ul>
     */
    interface MultiPartitionOnSpec<C> extends MySQLUpdate.MultiAsOnSpec<C> {

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
    interface MultiIndexHintCommandOnSpec<C> extends MySQLUpdate.MultiOnSpec<C>
            , MySQLDml.MultiIndexHintCommandClause<C, MySQLUpdate.MultiOnSpec<C>> {

    }


    interface MultiJoinSpec<C> extends Update.MultiSetSpec<C, Update.MultiWhereSpec<C>> {

        MultiPartitionOnSpec<C> leftJoin(TableMeta<?> table);

        MultiIndexHintCommandOnSpec<C> leftJoin(TableMeta<?> table, String tableAlias);

        MultiOnSpec<C> leftJoin(Function<C, SubQuery> function, String subQueryAlia);

        MultiOnSpec<C> leftJoinGroup(Function<C, TablePartGroup> function);

        MultiPartitionOnSpec<C> ifLeftJoin(Predicate<C> predicate, TableMeta<?> table);

        MultiIndexHintCommandOnSpec<C> ifLeftJoin(Predicate<C> predicate, TableMeta<?> table, String tableAlias);

        MultiOnSpec<C> ifLeftJoin(Function<C, SubQuery> function, String subQueryAlia);

        MultiOnSpec<C> ifLeftJoinGroup(Function<C, TablePartGroup> function);

        MultiPartitionOnSpec<C> join(TableMeta<?> table);

        MultiIndexHintCommandOnSpec<C> join(TableMeta<?> table, String tableAlias);

        MultiOnSpec<C> join(Function<C, SubQuery> function, String subQueryAlia);

        MultiOnSpec<C> joinGroup(Function<C, TablePartGroup> function);

        MultiPartitionOnSpec<C> ifJoin(Predicate<C> predicate, TableMeta<?> table);

        MultiIndexHintCommandOnSpec<C> ifJoin(Predicate<C> predicate, TableMeta<?> table, String tableAlias);

        MultiOnSpec<C> ifJoin(Function<C, SubQuery> function, String subQueryAlia);

        MultiOnSpec<C> ifJoinGroup(Function<C, TablePartGroup> function);

        MultiPartitionOnSpec<C> rightJoin(TableMeta<?> table);

        MultiIndexHintCommandOnSpec<C> rightJoin(TableMeta<?> table, String tableAlias);

        MultiOnSpec<C> rightJoin(Function<C, SubQuery> function, String subQueryAlia);

        MultiOnSpec<C> rightJoinGroup(Function<C, TablePartGroup> function);

        MultiPartitionOnSpec<C> ifRightJoin(Predicate<C> predicate, TableMeta<?> table);

        MultiIndexHintCommandOnSpec<C> ifRightJoin(Predicate<C> predicate, TableMeta<?> table, String tableAlias);

        MultiOnSpec<C> ifRightJoin(Function<C, SubQuery> function, String subQueryAlia);

        MultiOnSpec<C> ifRightJoinGroup(Function<C, TablePartGroup> function);

        MultiPartitionOnSpec<C> straightJoin(TableMeta<?> table);

        MultiIndexHintCommandOnSpec<C> straightJoin(TableMeta<?> table, String tableAlias);

        MultiOnSpec<C> straightJoin(Function<C, SubQuery> function, String subQueryAlia);

        MultiOnSpec<C> straightJoinGroup(Function<C, TablePartGroup> function);

        MultiPartitionOnSpec<C> ifStraightJoin(Predicate<C> predicate, TableMeta<?> table);

        MultiIndexHintCommandOnSpec<C> ifStraightJoin(Predicate<C> predicate, TableMeta<?> table, String tableAlias);

        MultiOnSpec<C> ifStraightJoin(Function<C, SubQuery> function, String subQueryAlia);

        MultiOnSpec<C> ifStraightJoinGroup(Function<C, TablePartGroup> function);

    }

    interface MultiOnSpec<C> {

        MultiJoinSpec<C> on(List<IPredicate> predicateList);

        MultiJoinSpec<C> on(IPredicate predicate);

        MultiJoinSpec<C> on(IPredicate predicate1, IPredicate predicate2);

        MultiJoinSpec<C> on(Function<C, List<IPredicate>> function);

        MultiJoinSpec<C> on(Supplier<List<IPredicate>> supplier);

        MultiJoinSpec<C> onId();

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

        BatchMultiJoinSpec<C> update(Function<C, SubQuery> function, String subQueryAlias);

        BatchMultiJoinSpec<C> updateGroup(Function<C, TablePartGroup> function, String groupAlias);

    }

    /**
     * @param <C> java type of criteria,see below:
     *            <ul>
     *               <li>{@link MySQLs#batchMultiUpdate57(Object)}</li>
     *               <li>{@link MySQLs#batchMultiUpdate80(Object)}</li>
     *            </ul>
     */
    interface BatchMultiPartitionJoinSpec<C> extends MySQLUpdate.BatchMultiAsJoinSpec<C> {

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
    interface BatchMultiIndexHintCommandJoinSpec<C> extends MySQLUpdate.BatchMultiJoinSpec<C>
            , MySQLDml.MultiIndexHintCommandClause<C, MySQLUpdate.BatchMultiJoinSpec<C>> {

    }

    /**
     * @param <C> java type of criteria,see below:
     *            <ul>
     *               <li>{@link MySQLs#batchMultiUpdate57(Object)}</li>
     *               <li>{@link MySQLs#batchMultiUpdate80(Object)}</li>
     *            </ul>
     */
    interface BatchMultiPartitionOnSpec<C> extends MySQLUpdate.BatchMultiAsOnSpec<C> {

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
    interface BatchMultiIndexHintCommandOnSpec<C> extends MySQLUpdate.BatchMultiOnSpec<C>
            , MySQLDml.MultiIndexHintCommandClause<C, MySQLUpdate.BatchMultiOnSpec<C>> {

    }


    /**
     * @param <C> java type of criteria,see below:
     *            <ul>
     *               <li>{@link MySQLs#batchMultiUpdate57(Object)}</li>
     *               <li>{@link MySQLs#batchMultiUpdate80(Object)}</li>
     *            </ul>
     */
    interface BatchMultiJoinSpec<C> extends BatchMultiSetClause<C, BatchMultiWhereSpec<C>> {

        BatchMultiPartitionOnSpec<C> leftJoin(TableMeta<?> table);

        BatchMultiIndexHintCommandOnSpec<C> leftJoin(TableMeta<?> table, String tableAlias);

        BatchMultiOnSpec<C> leftJoin(Function<C, SubQuery> function, String subQueryAlia);

        BatchMultiOnSpec<C> leftJoinGroup(Function<C, TablePartGroup> function);

        BatchMultiPartitionOnSpec<C> ifLeftJoin(Predicate<C> predicate, TableMeta<?> table);

        BatchMultiIndexHintCommandOnSpec<C> ifLeftJoin(Predicate<C> predicate, TableMeta<?> table, String tableAlias);

        BatchMultiOnSpec<C> ifLeftJoin(Function<C, SubQuery> function, String subQueryAlia);

        BatchMultiOnSpec<C> ifLeftJoinGroup(Function<C, TablePartGroup> function);

        BatchMultiPartitionOnSpec<C> join(TableMeta<?> table);

        BatchMultiIndexHintCommandOnSpec<C> join(TableMeta<?> table, String tableAlias);

        BatchMultiOnSpec<C> join(Function<C, SubQuery> function, String subQueryAlia);

        BatchMultiOnSpec<C> joinGroup(Function<C, TablePartGroup> function);

        BatchMultiPartitionOnSpec<C> ifJoin(Predicate<C> predicate, TableMeta<?> table);

        BatchMultiIndexHintCommandOnSpec<C> ifJoin(Predicate<C> predicate, TableMeta<?> table, String tableAlias);

        BatchMultiOnSpec<C> ifJoin(Function<C, SubQuery> function, String subQueryAlia);

        BatchMultiOnSpec<C> ifJoinGroup(Function<C, TablePartGroup> function);

        BatchMultiPartitionOnSpec<C> rightJoin(TableMeta<?> table);

        BatchMultiIndexHintCommandOnSpec<C> rightJoin(TableMeta<?> table, String tableAlias);

        BatchMultiOnSpec<C> rightJoin(Function<C, SubQuery> function, String subQueryAlia);

        BatchMultiOnSpec<C> rightJoinGroup(Function<C, TablePartGroup> function);

        BatchMultiPartitionOnSpec<C> ifRightJoin(Predicate<C> predicate, TableMeta<?> table);

        BatchMultiIndexHintCommandOnSpec<C> ifRightJoin(Predicate<C> predicate, TableMeta<?> table, String tableAlias);

        BatchMultiOnSpec<C> ifRightJoin(Function<C, SubQuery> function, String subQueryAlia);

        BatchMultiOnSpec<C> ifRightJoinGroup(Function<C, TablePartGroup> function);

        BatchMultiPartitionOnSpec<C> straightJoin(TableMeta<?> table);

        BatchMultiIndexHintCommandOnSpec<C> straightJoin(TableMeta<?> table, String tableAlias);

        BatchMultiOnSpec<C> straightJoin(Function<C, SubQuery> function, String subQueryAlia);

        BatchMultiOnSpec<C> straightJoinGroup(Function<C, TablePartGroup> function);

        BatchMultiPartitionOnSpec<C> ifStraightJoin(Predicate<C> predicate, TableMeta<?> table);

        BatchMultiIndexHintCommandOnSpec<C> ifStraightJoin(Predicate<C> predicate, TableMeta<?> table, String tableAlias);

        BatchMultiOnSpec<C> ifStraightJoin(Function<C, SubQuery> function, String subQueryAlia);

        BatchMultiOnSpec<C> ifStraightJoinGroup(Function<C, TablePartGroup> function);

    }

    /**
     * @param <C> java type of criteria,see below:
     *            <ul>
     *               <li>{@link MySQLs#batchMultiUpdate57(Object)}</li>
     *               <li>{@link MySQLs#batchMultiUpdate80(Object)}</li>
     *            </ul>
     */
    interface BatchMultiOnSpec<C> {

        BatchMultiJoinSpec<C> on(List<IPredicate> predicateList);

        BatchMultiJoinSpec<C> on(IPredicate predicate);

        BatchMultiJoinSpec<C> on(IPredicate predicate1, IPredicate predicate2);

        BatchMultiJoinSpec<C> on(Function<C, List<IPredicate>> function);

        BatchMultiJoinSpec<C> on(Supplier<List<IPredicate>> supplier);

        BatchMultiJoinSpec<C> onId();

    }





}
