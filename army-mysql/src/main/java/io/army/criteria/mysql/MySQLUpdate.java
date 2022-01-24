package io.army.criteria.mysql;

import io.army.criteria.*;
import io.army.domain.IDomain;
import io.army.meta.TableMeta;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;


/**
 * <p>
 * This interface representing MySQL update statement,the instance of this interface can only be parsed by MySQL dialect instance.
 * </p>
 */
public interface MySQLUpdate extends Update {


    interface LimitClause<C, LR> {

        LR limit(long rowCount);

        LR limit(Supplier<Long> supplier);

        LR limit(Function<C, Long> function);

        LR ifLimit(Supplier<Long> supplier);

        LR ifLimit(Function<C, Long> function);

    }


    interface SingleUpdateClause<UR, UP> {

        UP update(Supplier<List<Hint>> hints, List<SQLModifier> modifiers
                , TableMeta<? extends IDomain> table);

        UR update(Supplier<List<Hint>> hints, List<SQLModifier> modifiers
                , TableMeta<? extends IDomain> table, String tableAlias);

        UP update(TableMeta<? extends IDomain> table);

        UR update(TableMeta<? extends IDomain> table, String tableAlias);

    }


    interface SingleWithAndUpdateSpec<C> extends MySQLQuery.WithClause<C, MySQLUpdate.SingleUpdateSpec<C>>
            , MySQLUpdate.SingleUpdateSpec<C> {

    }


    /**
     * <p>
     * This representing MySQL single-table update syntax update clause.
     * </p>
     */
    interface SingleUpdateSpec<C> extends MySQLUpdate.SingleUpdateClause<MySQLUpdate.SingleIndexHintSpec<C>, MySQLUpdate.SinglePartitionSpec<C>> {


    }


    /**
     * <p>
     * This representing MySQL single-table update syntax partition clause.
     * </p>
     */
    interface SinglePartitionSpec<C>
            extends MySQLQuery.PartitionClause<C, Statement.AsClause<MySQLUpdate.SingleIndexHintSpec<C>>> {

    }


    /**
     * <p>
     * This representing MySQL single-table update syntax index hint clause.
     * </p>
     */
    interface SingleIndexHintSpec<C>
            extends MySQLQuery.IndexHintClause<C, MySQLUpdate.IndexOrderBySpec<C>, MySQLUpdate.SingleIndexHintSpec<C>>
            , MySQLUpdate.SingleSetSpec<C> {

    }

    interface IndexOrderBySpec<C> extends MySQLQuery.IndexOrderByClause<C, MySQLUpdate.SingleIndexHintSpec<C>> {

    }


    /**
     * <p>
     * This representing MySQL single-table update syntax set clause.
     * </p>
     */
    interface SingleSetSpec<C> extends SimpleSetClause<C, SingleWhereSpec<C>> {

    }


    /**
     * <p>
     * This representing MySQL single-table update syntax where clause.
     * </p>
     */
    interface SingleWhereSpec<C> extends MySQLUpdate.SingleSetSpec<C>
            , Statement.WhereClause<C, MySQLUpdate.OrderBySpec<C>, MySQLUpdate.SingleWhereAndSpec<C>> {

    }

    /**
     * <p>
     * This representing MySQL single-table update syntax and clause.
     * </p>
     */
    interface SingleWhereAndSpec<C> extends Statement.WhereAndClause<C, MySQLUpdate.SingleWhereAndSpec<C>>
            , MySQLUpdate.OrderBySpec<C> {

    }

    /**
     * <p>
     * This representing MySQL single-table update syntax order by clause.
     * </p>
     */
    interface OrderBySpec<C> extends Query.OrderByClause<C, MySQLUpdate.LimitSpec<C>>
            , MySQLUpdate.LimitSpec<C> {

    }

    /**
     * <p>
     * This representing MySQL single-table update syntax limit clause.
     * </p>
     */
    interface LimitSpec<C> extends LimitClause<C, Update.UpdateSpec>, Update.UpdateSpec {

    }

    /*################################## blow batch single-table update spec ##################################*/

    interface BatchSingleWithAndUpdateSpec<C> extends MySQLQuery.WithClause<C, MySQLUpdate.BatchSingleUpdateSpec<C>>
            , MySQLUpdate.BatchSingleUpdateSpec<C> {

    }

    /**
     * <p>
     * This representing MySQL batch single-table update syntax update clause.
     * </p>
     */
    interface BatchSingleUpdateSpec<C>
            extends MySQLUpdate.SingleUpdateClause<MySQLUpdate.BatchSingleIndexHintSpec<C>, MySQLUpdate.BatchSinglePartitionSpec<C>> {

    }


    /**
     * <p>
     * This representing MySQL batch single-table update syntax partition clause.
     * </p>
     */
    interface BatchSinglePartitionSpec<C>
            extends MySQLQuery.PartitionClause<C, Statement.AsClause<MySQLUpdate.BatchSingleIndexHintSpec<C>>> {

    }

    /**
     * <p>
     * This representing MySQL batch single-table update syntax index hint clause.
     * </p>
     */
    interface BatchSingleIndexHintSpec<C>
            extends MySQLQuery.IndexHintClause<C, MySQLUpdate.BatchIndexOrderBySpec<C>, MySQLUpdate.BatchSingleIndexHintSpec<C>>
            , MySQLUpdate.BatchSingleSetSpec<C> {

    }

    interface BatchIndexOrderBySpec<C> extends MySQLQuery.IndexOrderByClause<C, MySQLUpdate.BatchSingleIndexHintSpec<C>> {

    }


    /**
     * <p>
     * This representing MySQL batch single-table update syntax set clause.
     * </p>
     */
    interface BatchSingleSetSpec<C> extends Update.BatchSetClause<C, MySQLUpdate.BatchSingleWhereSpec<C>> {

    }

    /**
     * <p>
     * This representing MySQL batch single-table update syntax where clause.
     * </p>
     */
    interface BatchSingleWhereSpec<C> extends MySQLUpdate.BatchSingleSetSpec<C>
            , Statement.WhereClause<C, MySQLUpdate.BatchOrderBySpec<C>, MySQLUpdate.BatchSingleWhereAndSpec<C>> {

    }

    /**
     * <p>
     * This representing MySQL batch single-table update syntax and clause.
     * </p>
     */
    interface BatchSingleWhereAndSpec<C> extends Statement.WhereAndClause<C, MySQLUpdate.BatchSingleWhereAndSpec<C>>
            , MySQLUpdate.BatchOrderBySpec<C> {

    }

    /**
     * <p>
     * This representing MySQL batch single-table update syntax order by clause.
     * </p>
     */
    interface BatchOrderBySpec<C> extends Query.OrderByClause<C, MySQLUpdate.BatchLimitSpec<C>>
            , MySQLUpdate.BatchLimitSpec<C> {

    }

    /**
     * <p>
     * This representing MySQL batch single-table update syntax limit clause.
     * </p>
     */
    interface BatchLimitSpec<C> extends MySQLUpdate.LimitClause<C, Statement.BatchParamClause<C, UpdateSpec>>
            , Statement.BatchParamClause<C, UpdateSpec> {

    }




    /*################################## blow multi-table update api interface ##################################*/


    interface MultiIndexHintClause<C, IR> {

        IR useIndex(List<String> indexNames);

        IR ignoreIndex(List<String> indexNames);

        IR forceIndex(List<String> indexNames);

        IR useIndexForJoin(List<String> indexNames);

        IR ignoreIndexForJoin(List<String> indexNames);

        IR forceIndexForJoin(List<String> indexNames);

        /**
         * @return clause , clause no action if predicate return false.
         */
        IR ifUseIndex(Function<C, List<String>> function);

        /**
         * @return clause , clause no action if predicate return false.
         */
        IR ifIgnoreIndex(Function<C, List<String>> function);

        /**
         * @return clause , clause no action if predicate return false.
         */
        IR ifForceIndex(Function<C, List<String>> function);

        /**
         * @return clause , clause no action if predicate return false.
         */
        IR ifUseIndexForJoin(Function<C, List<String>> function);

        /**
         * @return clause , clause no action if predicate return false.
         */
        IR ifIgnoreIndexForJoin(Function<C, List<String>> function);

        /**
         * @return clause , clause no action if predicate return false.
         */
        IR ifForceIndexForJoin(Function<C, List<String>> function);

    }

    interface MultiUpdateClause<C, UP, UT, US> {

        UP update(Supplier<List<Hint>> hints, List<SQLModifier> modifiers
                , TableMeta<? extends IDomain> table);

        UT update(Supplier<List<Hint>> hints, List<SQLModifier> modifiers
                , TableMeta<? extends IDomain> table, String tableAlias);

        UP update(TableMeta<? extends IDomain> table);

        UT update(TableMeta<? extends IDomain> table, String tableAlias);

        <T extends TableItem> US update(Supplier<List<Hint>> hints, List<SQLModifier> modifiers
                , Supplier<T> supplier, String alias);

        <T extends TableItem> US update(Supplier<T> tablePart, String alias);

        <T extends TableItem> US update(Supplier<List<Hint>> hints, List<SQLModifier> modifiers
                , Function<C, T> tablePart, String alias);

        <T extends TableItem> US update(Function<C, T> tablePart, String alias);
    }


    interface WithAndMultiUpdateSpec<C> extends MySQLQuery.WithClause<C, MySQLUpdate.MultiUpdateSpec<C>>
            , MySQLUpdate.MultiUpdateSpec<C> {

    }


    /**
     * <p>
     * This interface representing MySQL multi-table update syntax update clause.
     * </p>
     */
    interface MultiUpdateSpec<C> extends MySQLUpdate.MultiUpdateClause<
            C,
            MySQLUpdate.MultiPartitionJoinSpec<C>,
            IndexHintJoinSpec<C>,
            MySQLUpdate.MultiJoinSpec<C>> {


    }


    /**
     * <p>
     * This interface representing MySQL multi-table update syntax partition clause(after from clause and before join clause).
     * </p>
     */
    interface MultiPartitionJoinSpec<C>
            extends MySQLQuery.PartitionClause<C, Statement.AsClause<IndexHintJoinSpec<C>>> {

    }


    /**
     * <p>
     * This interface representing MySQL multi-table update syntax index hint clause(after from clause and before join clause).
     * </p>
     */
    interface IndexHintJoinSpec<C>
            extends MySQLQuery.IndexHintClause<C, MySQLUpdate.IndexJoinJoinSpec<C>, MySQLUpdate.IndexHintJoinSpec<C>>
            , MySQLUpdate.MultiJoinSpec<C> {

    }

    interface IndexJoinJoinSpec<C> extends MySQLQuery.IndexJoinClause<C, MySQLUpdate.IndexHintJoinSpec<C>> {

    }

    /**
     * <p>
     * This interface representing MySQL multi-table update syntax partition clause(after join clause).
     * </p>
     */
    interface MultiPartitionOnSpec<C> extends MySQLQuery.PartitionClause<C, Statement.AsClause<MultiIndexHintOnSpec<C>>> {

    }

    /**
     * <p>
     * This interface representing MySQL multi-table update syntax index hint clause(after join clause).
     * </p>
     */
    interface MultiIndexHintOnSpec<C> extends MySQLQuery.IndexHintClause<C, MySQLUpdate.IndexJoinOnSpec<C>, MySQLUpdate.MultiIndexHintOnSpec<C>>
            , MySQLUpdate.MultiOnSpec<C> {

    }

    interface IndexJoinOnSpec<C> extends MySQLQuery.IndexJoinClause<C, MySQLUpdate.MultiIndexHintOnSpec<C>> {

    }


    /**
     * <p>
     * This interface representing MySQL multi-table update syntax join clause.
     * This interface extends below interfaces:
     * <ul>
     *     <li>{@link  MySQLQuery.MySQLJoinClause}</li>
     *     <li>{@link MySQLUpdate.MultiSetSpec}</li>
     * </ul>
     * </p>
     */
    interface MultiJoinSpec<C> extends MySQLQuery.MySQLJoinClause<C, MySQLUpdate.MultiIndexHintOnSpec<C>, MySQLUpdate.MultiOnSpec<C>, MySQLUpdate.MultiPartitionOnSpec<C>>
            , MySQLUpdate.MultiSetSpec<C> {


    }

    /**
     * <p>
     * This interface representing MySQL multi-table update syntax on clause.
     * </p>
     */
    interface MultiOnSpec<C> extends Statement.OnClause<C, MySQLUpdate.MultiJoinSpec<C>> {


    }

    /**
     * <p>
     * This interface representing MySQL multi-table update syntax set clause.
     * </p>
     */
    interface MultiSetSpec<C> extends SimpleSetClause<C, MultiWhereSpec<C>> {

    }

    /**
     * <p>
     * This interface representing MySQL multi-table update syntax where clause.
     * </p>
     */
    interface MultiWhereSpec<C> extends Statement.WhereClause<C, Update.UpdateSpec, MySQLUpdate.MultiWhereAndSpec<C>>
            , MySQLUpdate.MultiSetSpec<C> {


    }

    /**
     * <p>
     * This interface representing MySQL multi-table update syntax and clause.
     * </p>
     */
    interface MultiWhereAndSpec<C> extends Statement.WhereAndClause<C, MySQLUpdate.MultiWhereAndSpec<C>>
            , Update.UpdateSpec {


    }

    /*################################## blow batch multi-table update spec ##################################*/

    interface BatchWithAndMultiUpdateSpec<C> extends MySQLQuery.WithClause<C, MySQLUpdate.BatchMultiUpdateSpec<C>>
            , MySQLUpdate.BatchMultiUpdateSpec<C> {

    }

    /**
     * <p>
     * This interface representing MySQL batch multi-table update syntax update clause
     * </p>
     */
    interface BatchMultiUpdateSpec<C> extends MySQLUpdate.MultiUpdateClause<
            C,
            MySQLUpdate.BatchMultiPartitionJoinSpec<C>,
            MySQLUpdate.BatchMultiIndexHintJoinSpec<C>,
            MySQLUpdate.BatchMultiJoinSpec<C>> {

    }


    /**
     * <p>
     * This interface representing MySQL batch multi-table update syntax partition clause(after update clause and before join clause).
     * </p>
     */
    interface BatchMultiPartitionJoinSpec<C>
            extends MySQLQuery.PartitionClause<C, Statement.AsClause<MySQLUpdate.BatchMultiIndexHintJoinSpec<C>>> {

    }

    /**
     * <p>
     * This interface representing MySQL batch multi-table update syntax index hint clause(after update clause and before join clause).
     * </p>
     */
    interface BatchMultiIndexHintJoinSpec<C>
            extends MySQLQuery.IndexHintClause<C, MySQLUpdate.BatchIndexJoinJoinSpec<C>, MySQLUpdate.BatchMultiIndexHintJoinSpec<C>>
            , MySQLUpdate.BatchMultiJoinSpec<C> {

    }

    interface BatchIndexJoinJoinSpec<C> extends MySQLQuery.IndexJoinClause<C, MySQLUpdate.BatchMultiIndexHintJoinSpec<C>> {

    }

    /**
     * <p>
     * This interface representing MySQL batch multi-table update syntax partition clause(after join clause).
     * </p>
     */
    interface BatchMultiPartitionOnSpec<C> extends MySQLQuery.PartitionClause<C, Statement.AsClause<MySQLUpdate.BatchMultiIndexHintOnSpec<C>>> {

    }


    /**
     * <p>
     * This interface representing MySQL batch multi-table update syntax index hint clause(after join clause).
     * </p>
     */
    interface BatchMultiIndexHintOnSpec<C> extends MySQLQuery.IndexHintClause<C, MySQLUpdate.BatchIndexJoinOnSpec<C>, MySQLUpdate.BatchMultiIndexHintOnSpec<C>>
            , MySQLUpdate.BatchMultiOnSpec<C> {

    }

    interface BatchIndexJoinOnSpec<C> extends MySQLQuery.IndexJoinClause<C, MySQLUpdate.BatchMultiIndexHintOnSpec<C>> {

    }


    /**
     * <p>
     * This interface representing MySQL batch multi-table update syntax join clause.
     * This interface extends below interfaces:
     * <ul>
     *     <li>{@link  MySQLQuery.MySQLJoinClause}</li>
     *     <li>{@link MySQLUpdate.BatchMultiSetSpec}</li>
     * </ul>
     * </p>
     */
    interface BatchMultiJoinSpec<C> extends MySQLQuery.MySQLJoinClause<C, MySQLUpdate.BatchMultiIndexHintOnSpec<C>, MySQLUpdate.BatchMultiOnSpec<C>, MySQLUpdate.BatchMultiPartitionOnSpec<C>>
            , MySQLUpdate.BatchMultiSetSpec<C> {


    }

    /**
     * <p>
     * This interface representing MySQL batch multi-table update syntax on clause.
     * </p>
     */
    interface BatchMultiOnSpec<C> extends Statement.OnClause<C, MySQLUpdate.BatchMultiJoinSpec<C>> {


    }

    /**
     * <p>
     * This interface representing MySQL batch multi-table update syntax set clause.
     * </p>
     */
    interface BatchMultiSetSpec<C> extends Update.BatchSetClause<C, MySQLUpdate.BatchMultiWhereSpec<C>> {

    }

    /**
     * <p>
     * This interface representing MySQL batch multi-table update syntax where clause.
     * </p>
     */
    interface BatchMultiWhereSpec<C>
            extends Statement.WhereClause<C, Statement.BatchParamClause<C, Update.UpdateSpec>, MySQLUpdate.BatchMultiWhereAndSpec<C>>
            , MySQLUpdate.BatchMultiSetSpec<C> {


    }

    /**
     * <p>
     * This interface representing MySQL batch multi-table update syntax and clause.
     * </p>
     */
    interface BatchMultiWhereAndSpec<C> extends Statement.WhereAndClause<C, MySQLUpdate.BatchMultiWhereAndSpec<C>>
            , Statement.BatchParamClause<C, Update.UpdateSpec> {


    }


}
