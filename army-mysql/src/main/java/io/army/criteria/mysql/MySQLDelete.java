package io.army.criteria.mysql;

import io.army.criteria.*;
import io.army.domain.IDomain;
import io.army.meta.SingleTableMeta;
import io.army.meta.TableMeta;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * <p>
 * This interface representing MySQL delete statement,the instance of this interface can only be parsed by MySQL dialect instance.
 * </p>
 */
public interface MySQLDelete extends Delete {

    interface SingleDeleteClause<DR> {

        SingleDeleteFromClause<DR> delete(Supplier<List<Hint>> hints, List<SQLModifier> modifiers);

        DR deleteFrom(SingleTableMeta<? extends IDomain> table);

    }

    interface SingleDeleteFromClause<DR> {

        DR from(SingleTableMeta<? extends IDomain> table);

    }

    interface SingleDelete80Spec<C> extends MySQLQuery.WithClause<C, MySQLDelete.SingleDeleteSpec<C>>
            , MySQLDelete.SingleDeleteSpec<C> {


    }

    interface SingleDeleteSpec<C> extends MySQLDelete.SingleDeleteClause<MySQLDelete.SinglePartitionSpec<C>> {


    }

    interface SinglePartitionSpec<C> extends MySQLQuery.PartitionClause<C, MySQLDelete.SingleWhereSpec<C>>
            , MySQLDelete.SingleWhereSpec<C> {

    }

    interface SingleWhereSpec<C> extends Statement.WhereClause<C, MySQLDelete.OrderBySpec<C>, MySQLDelete.SingleWhereAndSpec<C>> {

    }

    interface SingleWhereAndSpec<C> extends Statement.WhereAndClause<C, MySQLDelete.SingleWhereAndSpec<C>>, MySQLDelete.OrderBySpec<C> {

    }

    interface OrderBySpec<C> extends Query.OrderByClause<C, MySQLDelete.LimitSpec<C>>, MySQLDelete.LimitSpec<C> {

    }

    interface LimitSpec<C> extends MySQLUpdate.LimitClause<C, Delete.DeleteSpec>, Delete.DeleteSpec {

    }


    /*################################## blow batch single delete api interface ##################################*/

    interface BatchSingleDelete80Spec<C> extends MySQLQuery.WithClause<C, MySQLDelete.BatchSingleDeleteSpec<C>>
            , MySQLDelete.BatchSingleDeleteSpec<C> {

    }


    interface BatchSingleDeleteSpec<C> extends MySQLDelete.SingleDeleteClause<MySQLDelete.BatchSinglePartitionSpec<C>> {


    }

    interface BatchSinglePartitionSpec<C> extends MySQLQuery.PartitionClause<C, MySQLDelete.BatchSingleWhereSpec<C>>
            , MySQLDelete.BatchSingleWhereSpec<C> {

    }


    interface BatchSingleWhereSpec<C> extends Statement.WhereClause<C, MySQLDelete.BatchOrderBySpec<C>
            , MySQLDelete.BatchSingleWhereAndSpec<C>> {

    }

    interface BatchSingleWhereAndSpec<C> extends Statement.WhereAndClause<C, MySQLDelete.BatchSingleWhereAndSpec<C>>
            , MySQLDelete.BatchOrderBySpec<C> {

    }

    interface BatchOrderBySpec<C> extends Query.OrderByClause<C, MySQLDelete.BatchLimitSpec<C>>
            , MySQLDelete.BatchLimitSpec<C> {

    }

    interface BatchLimitSpec<C> extends MySQLUpdate.LimitClause<C, Statement.BatchParamClause<C, Delete.DeleteSpec>>
            , Statement.BatchParamClause<C, Delete.DeleteSpec> {

    }


    /*################################## blow multi-table delete api interface ##################################*/

    interface MultiDeleteClause<C, DR, DP> {

        MultiDeleteFromClause<C, DR, DP> delete(Supplier<List<Hint>> hints, List<SQLModifier> modifiers, List<TableMeta<?>> tableList);

        MultiDeleteFromClause<C, DR, DP> delete(List<TableMeta<?>> tableList);

        MultiDeleteUsingClause<C, DR, DP> deleteFrom(Supplier<List<Hint>> hints, List<SQLModifier> modifiers, List<TableMeta<?>> tableList);

        MultiDeleteUsingClause<C, DR, DP> deleteFrom(List<TableMeta<?>> tableList);
    }

    interface MultiDeleteFromClause<C, DR, DP> {

        DR from(TableMeta<?> table, String alias);

        DP from(TableMeta<?> table);

        <T extends TablePart> DR from(Supplier<T> supplier, String alias);

        <T extends TablePart> DR from(Function<C, T> function, String alias);

    }

    interface MultiDeleteUsingClause<C, DR, DP> {

        DR using(TableMeta<?> table, String alias);

        DP using(TableMeta<?> table);

        <T extends TablePart> DR using(Supplier<T> supplier, String alias);

        <T extends TablePart> DR using(Function<C, T> function, String alias);

    }


    interface WithMultiDeleteSpec<C> extends MySQLQuery.WithClause<C, MySQLDelete.MultiDeleteSpec<C>>
            , MySQLDelete.MultiDeleteSpec<C> {

    }


    interface MultiDeleteSpec<C> extends MySQLDelete.MultiDeleteClause<C, MySQLDelete.MultiJoinSpec<C>, MySQLDelete.MultiPartitionJoinSpec<C>> {

    }


    interface MultiPartitionJoinSpec<C> extends MySQLQuery.PartitionClause<C, MySQLDelete.MultiAsJoinSpec<C>> {

    }

    interface MultiAsJoinSpec<C> extends Statement.AsClause<MySQLDelete.MultiJoinSpec<C>> {


    }


    interface MultiJoinSpec<C> extends MySQLQuery.MySQLJoinClause<C, MySQLDelete.MultiOnSpec<C>, MySQLDelete.MultiOnSpec<C>, MySQLDelete.MultiPartitionOnSpec<C>>
            , MySQLDelete.MultiWhereSpec<C> {

    }

    interface MultiPartitionOnSpec<C> extends MySQLQuery.PartitionClause<C, MySQLDelete.MultiAsOnSpec<C>> {

    }

    interface MultiAsOnSpec<C> extends Statement.AsClause<MySQLDelete.MultiOnSpec<C>> {


    }

    interface MultiOnSpec<C> extends Statement.OnClause<C, MySQLDelete.MultiJoinSpec<C>> {

    }

    interface MultiWhereSpec<C> extends Statement.WhereClause<C, Delete.DeleteSpec, MySQLDelete.MultiWhereAndSpec<C>> {


    }

    interface MultiWhereAndSpec<C> extends Statement.WhereAndClause<C, MySQLDelete.MultiWhereAndSpec<C>>
            , Delete.DeleteSpec {


    }

    /*################################## blow batch multi-table delete interface ##################################*/

    interface BatchWithMultiDeleteSpec<C> extends MySQLQuery.WithClause<C, MySQLDelete.BatchMultiDeleteSpec<C>>
            , MySQLDelete.BatchMultiDeleteSpec<C> {

    }

    interface BatchMultiDeleteSpec<C> extends MySQLDelete.MultiDeleteClause<C, MySQLDelete.BatchMultiJoinSpec<C>, MySQLDelete.BatchMultiPartitionJoinSpec<C>> {

    }


    interface BatchMultiPartitionJoinSpec<C> extends MySQLQuery.PartitionClause<C, MySQLDelete.BatchMultiAsJoinSpec<C>> {

    }

    interface BatchMultiAsJoinSpec<C> extends Statement.AsClause<MySQLDelete.BatchMultiJoinSpec<C>> {


    }


    interface BatchMultiJoinSpec<C> extends MySQLQuery.MySQLJoinClause<C, MySQLDelete.BatchMultiOnSpec<C>, MySQLDelete.BatchMultiOnSpec<C>, MySQLDelete.BatchMultiPartitionOnSpec<C>>
            , MySQLDelete.BatchMultiWhereSpec<C> {

    }

    interface BatchMultiPartitionOnSpec<C> extends MySQLQuery.PartitionClause<C, MySQLDelete.BatchMultiAsOnSpec<C>> {

    }

    interface BatchMultiAsOnSpec<C> extends Statement.AsClause<MySQLDelete.BatchMultiOnSpec<C>> {


    }

    interface BatchMultiOnSpec<C> extends Statement.OnClause<C, MySQLDelete.BatchMultiJoinSpec<C>> {

    }

    interface BatchMultiWhereSpec<C> extends Statement.WhereClause<C, Statement.BatchParamClause<C, Delete.DeleteSpec>
            , MySQLDelete.BatchMultiWhereAndSpec<C>> {


    }

    interface BatchMultiWhereAndSpec<C> extends Statement.WhereAndClause<C, BatchMultiWhereAndSpec<C>>
            , Statement.BatchParamClause<C, Delete.DeleteSpec> {


    }


}
