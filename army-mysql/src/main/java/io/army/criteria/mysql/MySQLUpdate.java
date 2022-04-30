package io.army.criteria.mysql;

import io.army.criteria.*;
import io.army.domain.IDomain;
import io.army.meta.TableMeta;

import java.util.EnumSet;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;


/**
 * <p>
 * This interface representing MySQL update statement,the instance of this interface can only be parsed by MySQL dialect instance.
 * </p>
 */
public interface MySQLUpdate extends Update {


    interface _SingleUpdateClause<UR, UP> {

        UP update(Supplier<List<Hint>> hints, List<MySQLWords> modifiers
                , TableMeta<?> table);

        UR update(Supplier<List<Hint>> hints, List<MySQLWords> modifiers
                , TableMeta<?> table, String tableAlias);

        UP update(TableMeta<?> table);

        UR update(TableMeta<?> table, String tableAlias);

    }


    interface SingleWithAndUpdateSpec<C> extends DialectStatement._WithCteClause<C, SingleUpdateSpec<C>>
            , MySQLUpdate.SingleUpdateSpec<C> {

    }


    /**
     * <p>
     * This representing MySQL single-table update syntax update clause.
     * </p>
     */
    interface SingleUpdateSpec<C> extends _SingleUpdateClause<SingleIndexHintSpec<C>, SinglePartitionSpec<C>> {


    }


    /**
     * <p>
     * This representing MySQL single-table update syntax partition clause.
     * </p>
     */
    interface SinglePartitionSpec<C>
            extends MySQLQuery._PartitionClause<C, _AsClause<SingleIndexHintSpec<C>>> {

    }


    /**
     * <p>
     * This representing MySQL single-table update syntax index hint clause.
     * </p>
     */
    interface SingleIndexHintSpec<C>
            extends MySQLQuery._IndexHintClause<C, IndexOrderBySpec<C>, SingleIndexHintSpec<C>>
            , MySQLUpdate.SingleSetSpec<C> {

    }

    interface IndexOrderBySpec<C> extends MySQLQuery._IndexOrderByClause<C, SingleIndexHintSpec<C>> {

    }


    /**
     * <p>
     * This representing MySQL single-table update syntax set clause.
     * </p>
     */
    interface SingleSetSpec<C> extends _SimpleSetClause<C, SingleWhereSpec<C>> {

    }


    /**
     * <p>
     * This representing MySQL single-table update syntax where clause.
     * </p>
     */
    interface SingleWhereSpec<C> extends MySQLUpdate.SingleSetSpec<C>
            , _WhereClause<C, OrderBySpec<C>, SingleWhereAndSpec<C>> {

    }

    /**
     * <p>
     * This representing MySQL single-table update syntax and clause.
     * </p>
     */
    interface SingleWhereAndSpec<C> extends _WhereAndClause<C, SingleWhereAndSpec<C>>
            , MySQLUpdate.OrderBySpec<C> {

    }

    /**
     * <p>
     * This representing MySQL single-table update syntax order by clause.
     * </p>
     */
    interface OrderBySpec<C> extends _OrderByClause<C, LimitSpec<C>>
            , MySQLUpdate.LimitSpec<C> {

    }

    /**
     * <p>
     * This representing MySQL single-table update syntax limit clause.
     * </p>
     */
    interface LimitSpec<C> extends _RowCountLimitClause<C, UpdateSpec>, Update.UpdateSpec {

    }

    /*################################## blow batch single-table update spec ##################################*/

    interface BatchSingleWithAndUpdateSpec<C> extends DialectStatement._WithCteClause<C, BatchSingleUpdateSpec<C>>
            , MySQLUpdate.BatchSingleUpdateSpec<C> {

    }

    /**
     * <p>
     * This representing MySQL batch single-table update syntax update clause.
     * </p>
     */
    interface BatchSingleUpdateSpec<C>
            extends _SingleUpdateClause<BatchSingleIndexHintSpec<C>, BatchSinglePartitionSpec<C>> {

    }


    /**
     * <p>
     * This representing MySQL batch single-table update syntax partition clause.
     * </p>
     */
    interface BatchSinglePartitionSpec<C>
            extends MySQLQuery._PartitionClause<C, _AsClause<BatchSingleIndexHintSpec<C>>> {

    }

    /**
     * <p>
     * This representing MySQL batch single-table update syntax index hint clause.
     * </p>
     */
    interface BatchSingleIndexHintSpec<C>
            extends MySQLQuery._IndexHintClause<C, BatchIndexOrderBySpec<C>, BatchSingleIndexHintSpec<C>>
            , MySQLUpdate.BatchSingleSetSpec<C> {

    }

    interface BatchIndexOrderBySpec<C> extends MySQLQuery._IndexOrderByClause<C, BatchSingleIndexHintSpec<C>> {

    }


    /**
     * <p>
     * This representing MySQL batch single-table update syntax set clause.
     * </p>
     */
    interface BatchSingleSetSpec<C> extends _BatchSetClause<C, BatchSingleWhereSpec<C>> {

    }

    /**
     * <p>
     * This representing MySQL batch single-table update syntax where clause.
     * </p>
     */
    interface BatchSingleWhereSpec<C> extends MySQLUpdate.BatchSingleSetSpec<C>
            , _WhereClause<C, BatchOrderBySpec<C>, BatchSingleWhereAndSpec<C>> {

    }

    /**
     * <p>
     * This representing MySQL batch single-table update syntax and clause.
     * </p>
     */
    interface BatchSingleWhereAndSpec<C> extends _WhereAndClause<C, BatchSingleWhereAndSpec<C>>
            , MySQLUpdate.BatchOrderBySpec<C> {

    }

    /**
     * <p>
     * This representing MySQL batch single-table update syntax order by clause.
     * </p>
     */
    interface BatchOrderBySpec<C> extends _OrderByClause<C, BatchLimitSpec<C>>
            , MySQLUpdate.BatchLimitSpec<C> {

    }

    /**
     * <p>
     * This representing MySQL batch single-table update syntax limit clause.
     * </p>
     */
    interface BatchLimitSpec<C> extends _RowCountLimitClause<C, BatchParamClause<C, UpdateSpec>>
            , Statement.BatchParamClause<C, UpdateSpec> {

    }




    /*################################## blow multi-table update api interface ##################################*/



    interface MultiUpdateClause<C, UP, UT, US> {

        UP update(Supplier<List<Hint>> hints, EnumSet<MySQLWords> modifiers
                , TableMeta<? extends IDomain> table);

        UT update(Supplier<List<Hint>> hints, EnumSet<MySQLWords> modifiers
                , TableMeta<? extends IDomain> table, String tableAlias);

        UP update(TableMeta<? extends IDomain> table);

        UT update(TableMeta<? extends IDomain> table, String tableAlias);

        <T extends TableItem> US update(Supplier<List<Hint>> hints, EnumSet<MySQLWords> modifiers
                , Supplier<T> supplier, String alias);

        <T extends TableItem> US update(Supplier<T> tablePart, String alias);

        <T extends TableItem> US update(Supplier<List<Hint>> hints, EnumSet<MySQLWords> modifiers
                , Function<C, T> tablePart, String alias);

        <T extends TableItem> US update(Function<C, T> tablePart, String alias);
    }


    interface WithAndMultiUpdateSpec<C> extends DialectStatement._WithCteClause<C, MultiUpdateSpec<C>>
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
            extends MySQLQuery._PartitionClause<C, _AsClause<IndexHintJoinSpec<C>>> {

    }


    /**
     * <p>
     * This interface representing MySQL multi-table update syntax index hint clause(after from clause and before join clause).
     * </p>
     */
    interface IndexHintJoinSpec<C>
            extends MySQLQuery._IndexHintClause<C, IndexJoinJoinSpec<C>, IndexHintJoinSpec<C>>
            , MySQLUpdate.MultiJoinSpec<C> {

    }

    interface IndexJoinJoinSpec<C> extends MySQLQuery._IndexJoinClause<C, IndexHintJoinSpec<C>> {

    }

    /**
     * <p>
     * This interface representing MySQL multi-table update syntax partition clause(after join clause).
     * </p>
     */
    interface MultiPartitionOnSpec<C> extends MySQLQuery._PartitionClause<C, _AsClause<MultiIndexHintOnSpec<C>>> {

    }

    /**
     * <p>
     * This interface representing MySQL multi-table update syntax index hint clause(after join clause).
     * </p>
     */
    interface MultiIndexHintOnSpec<C> extends MySQLQuery._IndexHintClause<C, IndexJoinOnSpec<C>, MultiIndexHintOnSpec<C>>
            , MySQLUpdate.MultiOnSpec<C> {

    }

    interface IndexJoinOnSpec<C> extends MySQLQuery._IndexJoinClause<C, MultiIndexHintOnSpec<C>> {

    }


    /**
     * <p>
     * This interface representing MySQL multi-table update syntax join clause.
     * This interface extends below interfaces:
     * <ul>
     *     <li>{@link  MySQLQuery._MySQLJoinClause}</li>
     *     <li>{@link MySQLUpdate.MultiSetSpec}</li>
     * </ul>
     * </p>
     */
    interface MultiJoinSpec<C> extends MySQLQuery._MySQLJoinClause<C, MultiIndexHintOnSpec<C>, MultiOnSpec<C>, MultiPartitionOnSpec<C>>
            , MySQLUpdate.MultiSetSpec<C> {


    }

    /**
     * <p>
     * This interface representing MySQL multi-table update syntax on clause.
     * </p>
     */
    interface MultiOnSpec<C> extends _OnClause<C, MultiJoinSpec<C>> {


    }

    /**
     * <p>
     * This interface representing MySQL multi-table update syntax set clause.
     * </p>
     */
    interface MultiSetSpec<C> extends _SimpleSetClause<C, MultiWhereSpec<C>> {

    }

    /**
     * <p>
     * This interface representing MySQL multi-table update syntax where clause.
     * </p>
     */
    interface MultiWhereSpec<C> extends _WhereClause<C, UpdateSpec, MultiWhereAndSpec<C>>
            , MySQLUpdate.MultiSetSpec<C> {


    }

    /**
     * <p>
     * This interface representing MySQL multi-table update syntax and clause.
     * </p>
     */
    interface MultiWhereAndSpec<C> extends _WhereAndClause<C, MultiWhereAndSpec<C>>
            , Update.UpdateSpec {


    }

    /*################################## blow batch multi-table update spec ##################################*/

    interface BatchWithAndMultiUpdateSpec<C> extends DialectStatement._WithCteClause<C, BatchMultiUpdateSpec<C>>
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
            extends MySQLQuery._PartitionClause<C, _AsClause<BatchMultiIndexHintJoinSpec<C>>> {

    }

    /**
     * <p>
     * This interface representing MySQL batch multi-table update syntax index hint clause(after update clause and before join clause).
     * </p>
     */
    interface BatchMultiIndexHintJoinSpec<C>
            extends MySQLQuery._IndexHintClause<C, BatchIndexJoinJoinSpec<C>, BatchMultiIndexHintJoinSpec<C>>
            , MySQLUpdate.BatchMultiJoinSpec<C> {

    }

    interface BatchIndexJoinJoinSpec<C> extends MySQLQuery._IndexJoinClause<C, BatchMultiIndexHintJoinSpec<C>> {

    }

    /**
     * <p>
     * This interface representing MySQL batch multi-table update syntax partition clause(after join clause).
     * </p>
     */
    interface BatchMultiPartitionOnSpec<C> extends MySQLQuery._PartitionClause<C, _AsClause<BatchMultiIndexHintOnSpec<C>>> {

    }


    /**
     * <p>
     * This interface representing MySQL batch multi-table update syntax index hint clause(after join clause).
     * </p>
     */
    interface BatchMultiIndexHintOnSpec<C> extends MySQLQuery._IndexHintClause<C, BatchIndexJoinOnSpec<C>, BatchMultiIndexHintOnSpec<C>>
            , MySQLUpdate.BatchMultiOnSpec<C> {

    }

    interface BatchIndexJoinOnSpec<C> extends MySQLQuery._IndexJoinClause<C, BatchMultiIndexHintOnSpec<C>> {

    }


    /**
     * <p>
     * This interface representing MySQL batch multi-table update syntax join clause.
     * This interface extends below interfaces:
     * <ul>
     *     <li>{@link  MySQLQuery._MySQLJoinClause}</li>
     *     <li>{@link MySQLUpdate.BatchMultiSetSpec}</li>
     * </ul>
     * </p>
     */
    interface BatchMultiJoinSpec<C> extends MySQLQuery._MySQLJoinClause<C, BatchMultiIndexHintOnSpec<C>, BatchMultiOnSpec<C>, BatchMultiPartitionOnSpec<C>>
            , MySQLUpdate.BatchMultiSetSpec<C> {


    }

    /**
     * <p>
     * This interface representing MySQL batch multi-table update syntax on clause.
     * </p>
     */
    interface BatchMultiOnSpec<C> extends _OnClause<C, BatchMultiJoinSpec<C>> {


    }

    /**
     * <p>
     * This interface representing MySQL batch multi-table update syntax set clause.
     * </p>
     */
    interface BatchMultiSetSpec<C> extends _BatchSetClause<C, BatchMultiWhereSpec<C>> {

    }

    /**
     * <p>
     * This interface representing MySQL batch multi-table update syntax where clause.
     * </p>
     */
    interface BatchMultiWhereSpec<C>
            extends _WhereClause<C, BatchParamClause<C, UpdateSpec>, BatchMultiWhereAndSpec<C>>
            , MySQLUpdate.BatchMultiSetSpec<C> {


    }

    /**
     * <p>
     * This interface representing MySQL batch multi-table update syntax and clause.
     * </p>
     */
    interface BatchMultiWhereAndSpec<C> extends _WhereAndClause<C, BatchMultiWhereAndSpec<C>>
            , Statement.BatchParamClause<C, Update.UpdateSpec> {


    }


}
