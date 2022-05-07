package io.army.criteria.mysql;

import io.army.criteria.*;
import io.army.meta.SingleTableMeta;
import io.army.meta.TableMeta;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * <p>
 * This interface representing MySQL delete statement,the instance of this interface can only be parsed by MySQL dialect instance.
 * </p>
 *
 * @since 1.0
 */
public interface MySQLDelete extends Delete {

    /**
     * <p>
     * This interface representing single-table DELETE clause for MySQL syntax.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <C>  criteria object java type
     * @param <DR> next clause java type
     * @since 1.0
     */
    interface _SingleDeleteClause<C, DR> {

        _SingleDeleteFromClause<DR> delete(Supplier<List<Hint>> hints, List<MySQLWords> modifiers);

        _SingleDeleteFromClause<DR> delete(Function<C, List<Hint>> hints, List<MySQLWords> modifiers);

        DR deleteFrom(SingleTableMeta<?> table, String alias);

    }

    /**
     * <p>
     * This interface representing  FROM clause for MySQL single-table DELETE syntax.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>/
     *
     * @param <DR> next clause java type
     * @since 1.0
     */
    interface _SingleDeleteFromClause<DR> {

        DR from(SingleTableMeta<?> table, String alias);

    }

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link DialectStatement._WithCteClause}</li>
     *          <li>{@link MySQLDelete._SingleDelete57Clause}</li>
     *     </ul>
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <C> criteria object java type
     * @since 1.0
     */
    interface _WithAndSingleDeleteSpec<C> extends DialectStatement._WithCteClause<C, _SingleDelete57Clause<C>>
            , _SingleDelete57Clause<C> {


    }

    /**
     * <p>
     * This interface representing single-table DELETE clause.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <C> criteria object java type.
     * @since 1.0
     */
    interface _SingleDelete57Clause<C> extends _SingleDeleteClause<C, _SinglePartitionSpec<C>> {


    }

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link MySQLQuery._PartitionClause}</li>
     *          <li>{@link _SingleWhereClause}</li>
     *     </ul>
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <C> criteria object java type
     * @since 1.0
     */
    interface _SinglePartitionSpec<C> extends MySQLQuery._PartitionClause<C, _SinglePartitionSpec<C>>
            , _SingleWhereClause<C> {

    }

    /**
     * <p>
     * This interface representing WHERE clause for single-table DELETE syntax.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <C> criteria object java type.
     * @since 1.0
     */
    interface _SingleWhereClause<C> extends _WhereClause<C, _OrderBySpec<C>, _SingleWhereAndSpec<C>> {

    }

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link io.army.criteria.Statement._WhereAndClause}</li>
     *          <li>{@link _OrderBySpec}</li>
     *     </ul>
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <C> criteria object java type
     * @since 1.0
     */
    interface _SingleWhereAndSpec<C> extends _WhereAndClause<C, _SingleWhereAndSpec<C>>, _OrderBySpec<C> {

    }

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link io.army.criteria.Statement._OrderByClause}</li>
     *          <li>{@link _LimitSpec}</li>
     *     </ul>
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <C> criteria object java type
     * @since 1.0
     */
    interface _OrderBySpec<C> extends _OrderByClause<C, _LimitSpec<C>>, _LimitSpec<C> {

    }

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link io.army.criteria.Statement._RowCountLimitClause}</li>
     *          <li>{@link _DeleteSpec}</li>
     *     </ul>
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <C> criteria object java type
     * @since 1.0
     */
    interface _LimitSpec<C> extends Statement._RowCountLimitClause<C, _DeleteSpec>, _DeleteSpec {

    }


    /*################################## blow batch single delete api interface ##################################*/

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link DialectStatement._WithCteClause}</li>
     *          <li>{@link MySQLDelete._BatchSingleDeleteClause}</li>
     *     </ul>
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <C> criteria object java type
     * @since 1.0
     */
    interface _BatchWithAndSingleDeleteSpec<C> extends DialectStatement._WithCteClause<C, _BatchSingleDeleteClause<C>>
            , _BatchSingleDeleteClause<C> {

    }


    /**
     * <p>
     * This interface representing single-table DELETE clause.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <C> criteria object java type.
     * @since 1.0
     */
    interface _BatchSingleDeleteClause<C> extends _SingleDeleteClause<C, _BatchSinglePartitionSpec<C>> {


    }

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link MySQLQuery._PartitionClause}</li>
     *          <li>{@link MySQLDelete._BatchSingleWhereClause}</li>
     *     </ul>
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <C> criteria object java type
     * @since 1.0
     */
    interface _BatchSinglePartitionSpec<C> extends MySQLQuery._PartitionClause<C, _BatchSingleWhereClause<C>>
            , _BatchSingleWhereClause<C> {

    }

    /**
     * <p>
     * This interface representing WHERE clause for single-table DELETE syntax.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <C> criteria object java type.
     * @since 1.0
     */
    interface _BatchSingleWhereClause<C> extends _WhereClause<C, _BatchOrderBySpec<C>, _BatchSingleWhereAndSpec<C>> {

    }

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link Statement._WhereAndClause}</li>
     *          <li>{@link MySQLDelete._BatchOrderBySpec}</li>
     *     </ul>
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <C> criteria object java type
     * @since 1.0
     */
    interface _BatchSingleWhereAndSpec<C> extends _WhereAndClause<C, _BatchSingleWhereAndSpec<C>>
            , _BatchOrderBySpec<C> {

    }

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link Statement._OrderByClause}</li>
     *          <li>{@link MySQLDelete._BatchLimitSpec}</li>
     *     </ul>
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <C> criteria object java type
     * @since 1.0
     */
    interface _BatchOrderBySpec<C> extends _OrderByClause<C, _BatchLimitSpec<C>>
            , _BatchLimitSpec<C> {

    }

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link Statement._RowCountLimitClause}</li>
     *          <li>{@link Statement._BatchParamClause}</li>
     *     </ul>
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <C> criteria object java type
     * @since 1.0
     */
    interface _BatchLimitSpec<C> extends MySQLUpdate._RowCountLimitClause<C, _BatchParamClause<C, _DeleteSpec>>
            , _BatchParamClause<C, _DeleteSpec> {

    }


    /*################################## blow multi-table delete api interface ##################################*/

    /**
     * <p>
     * This interface representing multi-table DELETE clause for MySQL syntax.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <C>  criteria object java type
     * @param <DP> next clause java type
     * @since 1.0
     */
    interface _MultiDeleteClause<C, DS, DP> {

        _MultiDeleteFromClause<C, DS, DP> delete(Supplier<List<Hint>> hints, List<MySQLWords> modifiers, List<String> tableAliasList);

        _MultiDeleteFromClause<C, DS, DP> delete(List<String> tableAliasList);

        _MultiDeleteFromClause<C, DS, DP> delete(String tableAlias1, String tableAlias2);

        _MultiDeleteUsingClause<C, DS, DP> deleteFrom(Supplier<List<Hint>> hints, List<MySQLWords> modifiers, List<String> tableAliasList);

        _MultiDeleteUsingClause<C, DS, DP> deleteFrom(List<String> tableAliasList);

        _MultiDeleteUsingClause<C, DS, DP> deleteFrom(String tableAlias1, String tableAlias2);
    }


    /**
     * <p>
     * This interface representing FROM clause multi-table DELETE syntax.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <C>  criteria object java type
     * @param <DP> next clause java type
     * @since 1.0
     */
    interface _MultiDeleteFromClause<C, DS, DP> extends _FromClause<C, DS, DS>
            , DialectStatement._DialectFromClause<DP>, DialectStatement._FromCteClause<DS> {

    }

    /**
     * <p>
     * This interface representing USING clause multi-table DELETE syntax.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <C>  criteria object java type
     * @param <DS> next clause java type
     * @param <DP> next clause java type
     * @since 1.0
     */
    interface _MultiDeleteUsingClause<C, DS, DP> {

        DS using(TableMeta<?> table, String alias);

        <T extends TableItem> DS using(Supplier<T> supplier, String alias);

        <T extends TableItem> DS using(Function<C, T> function, String alias);

        DS using(String cteName);

        DS using(String cteName, String alias);

        DP using(TableMeta<?> table);

    }

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link DialectStatement._WithCteClause}</li>
     *          <li>{@link _MultiDeleteSpec}</li>
     *     </ul>
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <C> criteria object java type
     * @since 1.0
     */
    interface _WithAndMultiDeleteSpec<C> extends DialectStatement._WithCteClause<C, _MultiDeleteSpec<C>>
            , _MultiDeleteSpec<C> {

    }

    /**
     * <p>
     * This interface representing DELETE clause for multi-table DELETE syntax.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <C> criteria object java type
     * @since 1.0
     */
    interface _MultiDeleteSpec<C> extends _MultiDeleteClause<C, _MultiJoinSpec<C>, _MultiPartitionJoinClause<C>> {

    }

    /**
     * /**
     * <p>
     * This interface representing PARTITION clause multi-table DELETE syntax.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <C> criteria object java type
     * @since 1.0
     */
    interface _MultiPartitionJoinClause<C> extends MySQLQuery._PartitionClause<C, _AsClause<_MultiJoinSpec<C>>> {

    }

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link MySQLQuery._MySQLJoinClause}</li>
     *          <li>{@link MySQLQuery._MySQLJoinCteClause}</li>
     *          <li>{@link DialectStatement._CrossJoinCteClause}</li>
     *          <li>{@link MySQLQuery._MySQLDialectJoinClause}</li>
     *          <li>{@link DialectStatement._DialectCrossJoinClause}</li>
     *          <li>{@link MySQLDelete._MultiWhereClause}</li>
     *     </ul>
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <C> criteria object java type
     * @since 1.0
     */
    interface _MultiJoinSpec<C> extends
            MySQLQuery._MySQLJoinClause<C, _OnClause<C, _MultiJoinSpec<C>>, _OnClause<C, _MultiJoinSpec<C>>>
            , MySQLQuery._MySQLJoinCteClause<_OnClause<C, _MultiJoinSpec<C>>>
            , DialectStatement._CrossJoinCteClause<_MultiJoinSpec<C>>
            , DialectStatement._DialectCrossJoinClause<C, _MultiPartitionJoinClause<C>>
            , MySQLQuery._MySQLDialectJoinClause<C, _MultiPartitionOnClause<C>>
            , _MultiWhereClause<C> {

    }

    /**
     * /**
     * <p>
     * This interface representing PARTITION clause multi-table DELETE syntax.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <C> criteria object java type
     * @since 1.0
     */
    interface _MultiPartitionOnClause<C> extends MySQLQuery._PartitionClause<C, _AsClause<_OnClause<C, _MultiJoinSpec<C>>>> {

    }

    /**
     * /**
     * <p>
     * This interface representing WHERE clause multi-table DELETE syntax.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <C> criteria object java type
     * @since 1.0
     */
    interface _MultiWhereClause<C> extends _WhereClause<C, _DeleteSpec, _MultiWhereAndSpec<C>> {

    }

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link Statement._WhereAndClause}</li>
     *          <li>{@link Delete._DeleteSpec}</li>
     *     </ul>
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <C> criteria object java type
     * @since 1.0
     */
    interface _MultiWhereAndSpec<C> extends _WhereAndClause<C, _MultiWhereAndSpec<C>>, _DeleteSpec {

    }

    /*################################## blow batch multi-table delete interface ##################################*/

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link DialectStatement._WithCteClause}</li>
     *          <li>{@link _BatchMultiDeleteClause}</li>
     *     </ul>
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <C> criteria object java type
     * @since 1.0
     */
    interface _BatchWithAndMultiDeleteSpec<C> extends DialectStatement._WithCteClause<C, _BatchMultiDeleteClause<C>>
            , _BatchMultiDeleteClause<C> {

    }

    /**
     * <p>
     * This interface representing batch DELETE clause for multi-table DELETE syntax.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <C> criteria object java type
     * @since 1.0
     */
    interface _BatchMultiDeleteClause<C>
            extends _MultiDeleteClause<C, _BatchMultiJoinSpec<C>, _BatchMultiPartitionJoinClause<C>> {

    }

    /**
     * <p>
     * This interface representing PARTITION clause for batch multi-table DELETE.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <C> criteria object java type
     * @since 1.0
     */
    interface _BatchMultiPartitionJoinClause<C>
            extends MySQLQuery._PartitionClause<C, _AsClause<_BatchMultiJoinSpec<C>>> {

    }

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link MySQLQuery._MySQLJoinClause}</li>
     *          <li>{@link MySQLQuery._MySQLJoinCteClause}</li>
     *          <li>{@link DialectStatement._CrossJoinCteClause}</li>
     *          <li>{@link MySQLQuery._MySQLDialectJoinClause}</li>
     *          <li>{@link DialectStatement._DialectCrossJoinClause}</li>
     *          <li>{@link MySQLDelete._MultiWhereClause}</li>
     *     </ul>
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <C> criteria object java type
     * @since 1.0
     */
    interface _BatchMultiJoinSpec<C> extends
            MySQLQuery._MySQLJoinClause<C, _OnClause<C, _BatchMultiJoinSpec<C>>, _OnClause<C, _BatchMultiJoinSpec<C>>>
            , MySQLQuery._MySQLJoinCteClause<_OnClause<C, _BatchMultiJoinSpec<C>>>
            , DialectStatement._CrossJoinCteClause<_BatchMultiJoinSpec<C>>
            , DialectStatement._DialectCrossJoinClause<C, _BatchMultiPartitionJoinClause<C>>
            , MySQLQuery._MySQLDialectJoinClause<C, _BatchMultiPartitionOnClause<C>>
            , _BatchMultiWhereClause<C> {

    }

    /**
     * <p>
     * This interface representing PARTITION clause for batch multi-table DELETE.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <C> criteria object java type
     * @since 1.0
     */
    interface _BatchMultiPartitionOnClause<C>
            extends MySQLQuery._PartitionClause<C, _AsClause<_OnClause<C, _BatchMultiJoinSpec<C>>>> {

    }

    /**
     * <p>
     * This interface representing AND clause for batch multi-table DELETE.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <C> criteria object java type
     * @since 1.0
     */
    interface _BatchMultiWhereClause<C>
            extends _WhereClause<C, _BatchParamClause<C, _DeleteSpec>, _BatchMultiWhereAndSpec<C>> {


    }

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link Statement._WhereAndClause}</li>
     *          <li>{@link Statement._BatchParamClause}</li>
     *     </ul>
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <C> criteria object java type
     * @since 1.0
     */
    interface _BatchMultiWhereAndSpec<C> extends _WhereAndClause<C, _BatchMultiWhereAndSpec<C>>
            , _BatchParamClause<C, _DeleteSpec> {


    }


}
