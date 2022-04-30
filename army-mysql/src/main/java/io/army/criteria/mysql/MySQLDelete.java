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

    interface SingleDeleteClause<C, DR> {

        SingleDeleteFromClause<DR> delete(Supplier<List<Hint>> hints, List<MySQLWords> modifiers);

        SingleDeleteFromClause<DR> delete(Function<C, List<Hint>> hints, List<MySQLWords> modifiers);

        DR deleteFrom(SingleTableMeta<? extends IDomain> table);

    }

    interface SingleDeleteFromClause<DR> {

        DR from(SingleTableMeta<? extends IDomain> table);

    }

    interface SingleDelete80Spec<C> extends DialectStatement._WithCteClause<C, SingleDeleteSpec<C>>
            , MySQLDelete.SingleDeleteSpec<C> {


    }

    interface SingleDeleteSpec<C> extends MySQLDelete.SingleDeleteClause<C, MySQLDelete.SinglePartitionSpec<C>> {


    }

    interface SinglePartitionSpec<C> extends MySQLQuery._PartitionClause<C, SingleWhereSpec<C>>
            , MySQLDelete.SingleWhereSpec<C> {

    }

    interface SingleWhereSpec<C> extends _WhereClause<C, OrderBySpec<C>, SingleWhereAndSpec<C>> {

    }

    interface SingleWhereAndSpec<C> extends _WhereAndClause<C, SingleWhereAndSpec<C>>, MySQLDelete.OrderBySpec<C> {

    }

    interface OrderBySpec<C> extends _OrderByClause<C, LimitSpec<C>>, MySQLDelete.LimitSpec<C> {

    }

    interface LimitSpec<C> extends MySQLUpdate._RowCountLimitClause<C, DeleteSpec>, Delete.DeleteSpec {

    }


    /*################################## blow batch single delete api interface ##################################*/

    interface BatchSingleDelete80Spec<C> extends DialectStatement._WithCteClause<C, BatchSingleDeleteSpec<C>>
            , MySQLDelete.BatchSingleDeleteSpec<C> {

    }


    interface BatchSingleDeleteSpec<C> extends MySQLDelete.SingleDeleteClause<C, MySQLDelete.BatchSinglePartitionSpec<C>> {


    }

    interface BatchSinglePartitionSpec<C> extends MySQLQuery._PartitionClause<C, BatchSingleWhereSpec<C>>
            , MySQLDelete.BatchSingleWhereSpec<C> {

    }


    interface BatchSingleWhereSpec<C> extends _WhereClause<C, BatchOrderBySpec<C>
            , BatchSingleWhereAndSpec<C>> {

    }

    interface BatchSingleWhereAndSpec<C> extends _WhereAndClause<C, BatchSingleWhereAndSpec<C>>
            , MySQLDelete.BatchOrderBySpec<C> {

    }

    interface BatchOrderBySpec<C> extends _OrderByClause<C, BatchLimitSpec<C>>
            , MySQLDelete.BatchLimitSpec<C> {

    }

    interface BatchLimitSpec<C> extends MySQLUpdate._RowCountLimitClause<C, BatchParamClause<C, DeleteSpec>>
            , Statement.BatchParamClause<C, Delete.DeleteSpec> {

    }


    /*################################## blow multi-table delete api interface ##################################*/

    interface MultiDeleteClause<C, DR, DP> {

        MultiDeleteFromClause<C, DR, DP> delete(Supplier<List<Hint>> hints, List<MySQLWords> modifiers, List<String> tableAliasList);

        MultiDeleteFromClause<C, DR, DP> delete(List<String> tableAliasList);

        MultiDeleteFromClause<C, DR, DP> delete(String tableAlias1, String tableAlias2);

        MultiDeleteUsingClause<C, DR, DP> deleteFrom(Supplier<List<Hint>> hints, List<MySQLWords> modifiers, List<String> tableAliasList);

        MultiDeleteUsingClause<C, DR, DP> deleteFrom(List<String> tableAliasList);

        MultiDeleteUsingClause<C, DR, DP> deleteFrom(String tableAlias1, String tableAlias2);
    }

    interface MultiDeleteFromClause<C, DR, DP> {

        DR from(TableMeta<?> table, String alias);

        DP from(TableMeta<?> table);

        <T extends TableItem> DR from(Supplier<T> supplier, String alias);

        <T extends TableItem> DR from(Function<C, T> function, String alias);

    }

    interface MultiDeleteUsingClause<C, DR, DP> {

        DR using(TableMeta<?> table, String alias);

        DP using(TableMeta<?> table);

        <T extends TableItem> DR using(Supplier<T> supplier, String alias);

        <T extends TableItem> DR using(Function<C, T> function, String alias);

    }


    interface WithMultiDeleteSpec<C> extends DialectStatement._WithCteClause<C, MultiDeleteSpec<C>>
            , MySQLDelete.MultiDeleteSpec<C> {

    }


    interface MultiDeleteSpec<C> extends MySQLDelete.MultiDeleteClause<C, MySQLDelete.MultiJoinSpec<C>, MySQLDelete.MultiPartitionJoinSpec<C>> {

    }


    interface MultiPartitionJoinSpec<C> extends MySQLQuery._PartitionClause<C, MultiAsJoinSpec<C>> {

    }

    interface MultiAsJoinSpec<C> extends _AsClause<MultiJoinSpec<C>> {


    }


    interface MultiJoinSpec<C> extends MySQLQuery._MySQLJoinClause<C, MultiOnSpec<C>, MultiOnSpec<C>, MultiPartitionOnSpec<C>>
            , MySQLDelete.MultiWhereSpec<C> {

    }

    interface MultiPartitionOnSpec<C> extends MySQLQuery._PartitionClause<C, MultiAsOnSpec<C>> {

    }

    interface MultiAsOnSpec<C> extends _AsClause<MultiOnSpec<C>> {


    }

    interface MultiOnSpec<C> extends _OnClause<C, MultiJoinSpec<C>> {

    }

    interface MultiWhereSpec<C> extends _WhereClause<C, DeleteSpec, MultiWhereAndSpec<C>> {


    }

    interface MultiWhereAndSpec<C> extends _WhereAndClause<C, MultiWhereAndSpec<C>>
            , Delete.DeleteSpec {


    }

    /*################################## blow batch multi-table delete interface ##################################*/

    interface BatchWithMultiDeleteSpec<C> extends DialectStatement._WithCteClause<C, BatchMultiDeleteSpec<C>>
            , MySQLDelete.BatchMultiDeleteSpec<C> {

    }

    interface BatchMultiDeleteSpec<C> extends MySQLDelete.MultiDeleteClause<C, MySQLDelete.BatchMultiJoinSpec<C>, MySQLDelete.BatchMultiPartitionJoinSpec<C>> {

    }


    interface BatchMultiPartitionJoinSpec<C> extends MySQLQuery._PartitionClause<C, BatchMultiAsJoinSpec<C>> {

    }

    interface BatchMultiAsJoinSpec<C> extends _AsClause<BatchMultiJoinSpec<C>> {


    }


    interface BatchMultiJoinSpec<C> extends MySQLQuery._MySQLJoinClause<C, BatchMultiOnSpec<C>, BatchMultiOnSpec<C>, BatchMultiPartitionOnSpec<C>>
            , MySQLDelete.BatchMultiWhereSpec<C> {

    }

    interface BatchMultiPartitionOnSpec<C> extends MySQLQuery._PartitionClause<C, BatchMultiAsOnSpec<C>> {

    }

    interface BatchMultiAsOnSpec<C> extends _AsClause<BatchMultiOnSpec<C>> {


    }

    interface BatchMultiOnSpec<C> extends _OnClause<C, BatchMultiJoinSpec<C>> {

    }

    interface BatchMultiWhereSpec<C> extends _WhereClause<C, BatchParamClause<C, DeleteSpec>
            , BatchMultiWhereAndSpec<C>> {


    }

    interface BatchMultiWhereAndSpec<C> extends _WhereAndClause<C, BatchMultiWhereAndSpec<C>>
            , Statement.BatchParamClause<C, Delete.DeleteSpec> {


    }


}
