package io.army.criteria.mysql;

import io.army.criteria.Delete;
import io.army.criteria.IPredicate;
import io.army.criteria.SubQuery;
import io.army.criteria.TablePartGroup;
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


    interface SinglePartitionSpec<C> extends MySQLDelete.SingleIndexHintCommandSpec<C> {

        SingleIndexHintCommandSpec<C> partition(String partitionName);

        SingleIndexHintCommandSpec<C> partition(String partitionName1, String partitionNam2);

        SingleIndexHintCommandSpec<C> partition(List<String> partitionNameList);

        SingleIndexHintCommandSpec<C> ifPartition(Function<C, List<String>> function);

    }

    interface SingleIndexHintCommandSpec<C>
            extends MySQLDelete.SingleWhereSpec<C>
            , MySQLDml.SingleIndexHintCommandClause<C, MySQLDelete.SingleWhereSpec<C>> {

    }


    interface SingleWhereSpec<C> extends WhereSpec<C> {

        SingleWhereAndSpec<C, Delete> where(IPredicate predicate);

        SingleOrderBySpec<C, Delete> where(List<IPredicate> predicateList);

        SingleOrderBySpec<C, Delete> where(Function<C, List<IPredicate>> function);

        SingleOrderBySpec<C, Delete> where(Supplier<List<IPredicate>> supplier);

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

    interface BatchSingleIndexHintCommandSpec<C>
            extends MySQLDelete.BatchSingleWhereSpec<C>
            , MySQLDml.SingleIndexHintCommandClause<C, MySQLDelete.BatchSingleWhereSpec<C>> {

    }


    interface BatchSingleWhereSpec<C> extends Delete.BatchWhereSpec<C> {

        BatchSingleWhereAndSpec<C, Delete> where(IPredicate predicate);

        BatchOrderBySpec<C, Delete> where(List<IPredicate> predicateList);

        BatchOrderBySpec<C, Delete> where(Function<C, List<IPredicate>> function);

        BatchOrderBySpec<C, Delete> where(Supplier<List<IPredicate>> supplier);

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

        MultiUsingSpec<C> deleteFrom(List<TableMeta<?>> tableList);

        MultiUsingSpec<C> deleteFrom(Function<C, List<TableMeta<?>>> function);


    }


    interface MultiFromSpec<C> {

        MultiPartitionJoinSpec<C> from(TableMeta<?> table);

        MultiIndexHintCommandJoinSpec<C> from(TableMeta<?> table, String tableAlias);

    }

    interface MultiUsingSpec<C> {

        MultiPartitionJoinSpec<C> using(TableMeta<?> table);

        MultiIndexHintCommandJoinSpec<C> using(TableMeta<?> table, String tableAlias);

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
    interface MultiIndexHintCommandJoinSpec<C> extends MySQLDelete.MultiJoinSpec<C>
            , MySQLDml.MultiIndexHintCommandClause<C, MySQLDelete.MultiJoinSpec<C>> {

    }

    /**
     * @param <C> java type of criteria,see below:
     *            <ul>
     *               <li>{@link io.army.criteria.impl.MySQLs#multiUpdate57(Object)}</li>
     *               <li>{@link io.army.criteria.impl.MySQLs#multiUpdate80(Object)}</li>
     *            </ul>
     */
    interface MultiPartitionOnSpec<C> extends MySQLDelete.MultiAsOnSpec<C> {

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
    interface MultiIndexHintCommandOnSpec<C> extends MySQLDelete.MultiOnSpec<C>
            , MySQLDelete.MultiIndexHintCommandClause<C, MySQLDelete.MultiOnSpec<C>> {

    }


    interface MultiJoinSpec<C> extends Delete.WhereSpec<C> {

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



    /*################################## blow batch multi-table delete api interface ##################################*/


    interface BatchMultiDeleteSpec<C> {

        BatchMultiFromSpec<C> delete(List<TableMeta<?>> tableList);

        BatchMultiFromSpec<C> delete(Function<C, List<TableMeta<?>>> function);

        BatchMultiUsingSpec<C> deleteFrom(List<TableMeta<?>> tableList);

        BatchMultiUsingSpec<C> deleteFrom(Function<C, List<TableMeta<?>>> function);

    }


    interface BatchMultiFromSpec<C> {

        BatchMultiPartitionJoinSpec<C> from(TableMeta<?> table);

        BatchMultiIndexHintCommandJoinSpec<C> from(TableMeta<?> table, String tableAlias);

    }

    interface BatchMultiUsingSpec<C> {

        BatchMultiPartitionJoinSpec<C> using(TableMeta<?> table);

        BatchMultiIndexHintCommandJoinSpec<C> using(TableMeta<?> table, String tableAlias);

    }

    /**
     * @param <C> java type of criteria,see below:
     *            <ul>
     *               <li>{@link io.army.criteria.impl.MySQLs#multiUpdate57(Object)}</li>
     *               <li>{@link io.army.criteria.impl.MySQLs#multiUpdate80(Object)}</li>
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
     *               <li>{@link io.army.criteria.impl.MySQLs#multiUpdate57(Object)}</li>
     *               <li>{@link io.army.criteria.impl.MySQLs#multiUpdate80(Object)}</li>
     *            </ul>
     */
    interface BatchMultiAsJoinSpec<C> {

        BatchMultiIndexHintCommandJoinSpec<C> as(String tableAlias);
    }

    /**
     * @param <C> java type of criteria,see below:
     *            <ul>
     *               <li>{@link io.army.criteria.impl.MySQLs#multiUpdate57(Object)}</li>
     *               <li>{@link io.army.criteria.impl.MySQLs#multiUpdate80(Object)}</li>
     *            </ul>
     */
    interface BatchMultiIndexHintCommandJoinSpec<C> extends MySQLDelete.BatchMultiJoinSpec<C>
            , MySQLDml.MultiIndexHintCommandClause<C, MySQLDelete.BatchMultiJoinSpec<C>> {

    }

    /**
     * @param <C> java type of criteria,see below:
     *            <ul>
     *               <li>{@link io.army.criteria.impl.MySQLs#multiUpdate57(Object)}</li>
     *               <li>{@link io.army.criteria.impl.MySQLs#multiUpdate80(Object)}</li>
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
     *               <li>{@link io.army.criteria.impl.MySQLs#multiUpdate57(Object)}</li>
     *               <li>{@link io.army.criteria.impl.MySQLs#multiUpdate80(Object)}</li>
     *            </ul>
     */
    interface BatchMultiAsOnSpec<C> {

        BatchMultiIndexHintCommandOnSpec<C> as(String tableAlias);
    }

    /**
     * @param <C> java type of criteria,see below:
     *            <ul>
     *               <li>{@link io.army.criteria.impl.MySQLs#multiUpdate57(Object)}</li>
     *               <li>{@link io.army.criteria.impl.MySQLs#multiUpdate80(Object)}</li>
     *            </ul>
     */
    interface BatchMultiIndexHintCommandOnSpec<C> extends MySQLDelete.BatchMultiOnSpec<C>
            , MySQLDml.MultiIndexHintCommandClause<C, MySQLDelete.BatchMultiOnSpec<C>> {

    }


    interface BatchMultiJoinSpec<C> extends Delete.BatchWhereSpec<C> {

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

    interface BatchMultiOnSpec<C> {

        BatchMultiJoinSpec<C> on(List<IPredicate> predicateList);

        BatchMultiJoinSpec<C> on(IPredicate predicate);

        BatchMultiJoinSpec<C> on(IPredicate predicate1, IPredicate predicate2);

        BatchMultiJoinSpec<C> on(Function<C, List<IPredicate>> function);

        BatchMultiJoinSpec<C> on(Supplier<List<IPredicate>> supplier);

        BatchMultiJoinSpec<C> onId();

    }





}
