package io.army.criteria.mysql;

import io.army.criteria.*;
import io.army.criteria.impl.MySQLSyntax;
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
public interface MySQLDelete extends Delete, DialectStatement {

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
     * @param <DT> next clause java type
     * @since 1.0
     */
    interface _MySQLSingleDeleteClause<C, DT> extends _DeleteSimpleClause<DT> {

        _SingleDeleteFromClause<DT> delete(Supplier<List<Hint>> hints, List<MySQLSyntax._MySQLModifier> modifiers);

        _SingleDeleteFromClause<DT> delete(Function<C, List<Hint>> hints, List<MySQLSyntax._MySQLModifier> modifiers);
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


    interface _SinglePartitionSpec<C> extends MySQLQuery._PartitionClause<C, _SingleWhereClause<C>>
            , _SingleWhereClause<C> {

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
    interface _SingleDelete57Clause<C>
            extends _MySQLSingleDeleteClause<C, _SinglePartitionSpec<C>> {


    }


    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link _WithCteClause2}</li>
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
    interface _WithAndSingleDeleteSpec<C> extends _WithCteClause2<C, SubQuery, _SingleDelete57Clause<C>>
            , _SingleDelete57Clause<C> {


    }


    /*################################## blow batch single delete api interface ##################################*/

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
    interface _BatchLimitSpec<C> extends MySQLUpdate._RowCountLimitClause<C, Statement._BatchParamClause<C, _DeleteSpec>>
            , Statement._BatchParamClause<C, _DeleteSpec> {

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
    interface _BatchSingleDeleteClause<C> extends _MySQLSingleDeleteClause<C, _BatchSinglePartitionSpec<C>> {


    }


    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link _WithCteClause2}</li>
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
    interface _BatchWithAndSingleDeleteSpec<C>
            extends _WithCteClause2<C, SubQuery, _BatchSingleDeleteClause<C>>
            , _BatchSingleDeleteClause<C> {

    }



    /*################################## blow multi-table delete api interface ##################################*/

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
    interface _MultiDeleteFromClause<C, DT, DS, DP> extends MySQLQuery._MySQLFromClause<C, DT, DS>
            , DialectStatement._DialectFromClause<DP> {

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
    interface _MultiDeleteUsingClause<C, DT, DS, DP> {

        DT using(TableMeta<?> table, String alias);

        <T extends TabularItem> DS using(Supplier<T> supplier, String alias);

        <T extends TabularItem> DS using(Function<C, T> function, String alias);

        <T extends SubQuery> DS usingLateral(Supplier<T> supplier, String alias);

        <T extends SubQuery> DS usingLateral(Function<C, T> function, String alias);

        DS using(String cteName);

        DS using(String cteName, String alias);

        DP using(TableMeta<?> table);
    }

    interface _MultiDeleteFromAliasClause<C, DT, DS, DP> {

        _MultiDeleteUsingClause<C, DT, DS, DP> from(List<String> tableAliasList);

        _MultiDeleteUsingClause<C, DT, DS, DP> from(String tableAlias1, String tableAlias2);

    }


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
    interface _MultiDeleteClause<C, DT, DS, DP> {

        _MultiDeleteFromClause<C, DT, DS, DP> delete(Supplier<List<Hint>> hints, List<MySQLSyntax._MySQLModifier> modifiers, List<String> tableAliasList);

        _MultiDeleteFromAliasClause<C, DT, DS, DP> delete(Supplier<List<Hint>> hints, List<MySQLSyntax._MySQLModifier> modifiers);

        _MultiDeleteFromClause<C, DT, DS, DP> delete(List<String> tableAliasList);

        _MultiDeleteFromClause<C, DT, DS, DP> delete(String tableAlias1, String tableAlias2);

        _MultiDeleteUsingClause<C, DT, DS, DP> deleteFrom(List<String> tableAliasList);

        _MultiDeleteUsingClause<C, DT, DS, DP> deleteFrom(String tableAlias1, String tableAlias2);
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


    interface _MultiIndexHintOnSpec<C> extends MySQLQuery._IndexHintForJoinClause<C, _MultiIndexHintOnSpec<C>>
            , Statement._OnClause<C, _MultiJoinSpec<C>> {

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
    interface _MultiPartitionOnClause<C>
            extends MySQLQuery._PartitionClause<C, _AsClause<_MultiIndexHintOnSpec<C>>> {

    }

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>JOIN clause</li>
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
    interface _MultiJoinSpec<C> extends MySQLQuery._MySQLJoinClause<C, _MultiIndexHintOnSpec<C>, _OnClause<C, _MultiJoinSpec<C>>>
            , MySQLQuery._MySQLCrossJoinClause<C, _MultiIndexHintJoinSpec<C>, _MultiJoinSpec<C>>
            , MySQLQuery._MySQLIfJoinClause<C, _MultiJoinSpec<C>>
            , MySQLQuery._MySQLDialectJoinClause<_MultiPartitionOnClause<C>>
            , DialectStatement._DialectCrossJoinClause<_MultiPartitionJoinClause<C>>
            , _MultiWhereClause<C> {

    }

    interface _MultiIndexHintJoinSpec<C> extends MySQLQuery._IndexHintForJoinClause<C, _MultiIndexHintJoinSpec<C>>
            , _MultiJoinSpec<C> {

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
    interface _MultiPartitionJoinClause<C>
            extends MySQLQuery._PartitionClause<C, _AsClause<_MultiIndexHintJoinSpec<C>>> {

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
    interface _MultiDelete57Clause<C> extends _MultiDeleteClause<
            C,
            _MultiIndexHintJoinSpec<C>,
            _MultiJoinSpec<C>,
            _MultiPartitionJoinClause<C>> {

    }

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link _WithCteClause2}</li>
     *          <li>{@link _MultiDelete57Clause}</li>
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
    interface _WithAndMultiDeleteSpec<C> extends _WithCteClause2<C, SubQuery, _MultiDelete57Clause<C>>
            , _MultiDelete57Clause<C> {

    }



    /*################################## blow batch multi-table delete interface ##################################*/

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


    interface _BatchMultiIndexHintOnSpec<C>
            extends MySQLQuery._IndexHintForJoinClause<C, _BatchMultiIndexHintOnSpec<C>>
            , Statement._OnClause<C, _BatchMultiJoinSpec<C>> {

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
            extends MySQLQuery._PartitionClause<C, _AsClause<_BatchMultiIndexHintOnSpec<C>>> {

    }

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>JOIN clause</li>
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
    interface _BatchMultiJoinSpec<C>
            extends MySQLQuery._MySQLJoinClause<C, _BatchMultiIndexHintOnSpec<C>, _OnClause<C, _BatchMultiJoinSpec<C>>>
            , MySQLQuery._MySQLCrossJoinClause<C, _BatchMultiIndexHintJoinSpec<C>, _BatchMultiJoinSpec<C>>
            , MySQLQuery._MySQLIfJoinClause<C, _BatchMultiJoinSpec<C>>
            , MySQLQuery._MySQLDialectJoinClause<_BatchMultiPartitionOnClause<C>>
            , DialectStatement._DialectCrossJoinClause<_BatchMultiPartitionJoinClause<C>>
            , _BatchMultiWhereClause<C> {

    }

    interface _BatchMultiIndexHintJoinSpec<C>
            extends MySQLQuery._IndexHintForJoinClause<C, _BatchMultiIndexHintJoinSpec<C>>
            , _BatchMultiJoinSpec<C> {

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
            extends MySQLQuery._PartitionClause<C, _AsClause<_BatchMultiIndexHintJoinSpec<C>>> {

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
    interface _BatchMultiDeleteClause<C> extends _MultiDeleteClause<
            C,
            _BatchMultiIndexHintJoinSpec<C>,
            _BatchMultiJoinSpec<C>
            , _BatchMultiPartitionJoinClause<C>> {

    }


    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link _WithCteClause2}</li>
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
    interface _BatchWithAndMultiDeleteSpec<C>
            extends _WithCteClause2<C, SubQuery, _BatchMultiDeleteClause<C>>
            , _BatchMultiDeleteClause<C> {

    }


}
