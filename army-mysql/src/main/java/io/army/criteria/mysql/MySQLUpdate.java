package io.army.criteria.mysql;

import io.army.criteria.*;
import io.army.meta.TableMeta;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;


/**
 * <p>
 * This interface representing MySQL update statement,the instance of this interface can only be parsed by MySQL dialect instance.
 * </p>
 *
 * @since 1.0
 */
public interface MySQLUpdate extends Update {


    /**
     * <p>
     * This interface representing single-table UPDATE clause for MySQL syntax.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <UT> next clause java type
     * @since 1.0
     */
    interface _SingleUpdateClause<C, UT> {

        MySQLQuery._PartitionClause<C, _AsClause<UT>> update(Supplier<List<Hint>> hints, List<MySQLWords> modifiers
                , TableMeta<?> table);

        UT update(Supplier<List<Hint>> hints, List<MySQLWords> modifiers
                , TableMeta<?> table, String tableAlias);

        MySQLQuery._PartitionClause<C, _AsClause<UT>> update(TableMeta<?> table);

        UT update(TableMeta<?> table, String tableAlias);

    }


    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link Update._UpdateSpec}</li>
     *          <li>method {@link Statement._RowCountLimitClause}</li>
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
    interface _LimitSpec<C> extends Statement._RowCountLimitClause<C, _UpdateSpec>, _UpdateSpec {

    }


    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link MySQLUpdate._LimitSpec}</li>
     *          <li>method {@link Statement._OrderByClause}</li>
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
    interface _OrderBySpec<C> extends Statement._OrderByClause<C, _LimitSpec<C>>, _LimitSpec<C> {

    }

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link MySQLUpdate._OrderBySpec}</li>
     *          <li>method {@link Statement._WhereAndClause}</li>
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
    interface _SingleWhereAndSpec<C> extends _WhereAndClause<C, _SingleWhereAndSpec<C>>
            , _OrderBySpec<C> {

    }

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link MySQLUpdate._SingleSet57Clause}</li>
     *          <li>method {@link Statement._WhereClause}</li>
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
    interface _SingleWhereSpec<C> extends _WhereClause<C, _OrderBySpec<C>, _SingleWhereAndSpec<C>>
            , _SingleSet57Clause<C> {

    }

    /**
     * <p>
     * This interface representing SET clause for single-table UPDATE syntax.
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
    interface _SingleSet57Clause<C> extends _SimpleSetClause<C, TableField, _SingleWhereSpec<C>> {

    }

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link MySQLQuery._IndexHintForOrderByClause}</li>
     *          <li>method {@link MySQLUpdate._SingleSet57Clause}</li>
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
    interface _SingleIndexHintSpec<C> extends MySQLQuery._IndexHintForOrderByClause<C, _SingleIndexHintSpec<C>>
            , _SingleSet57Clause<C> {

    }


    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link DialectStatement._WithCteClause}</li>
     *          <li>method {@link _SingleUpdateClause}</li>
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
    interface _SingleWithAndUpdateSpec<C>
            extends DialectStatement._WithCteClause<C, SubQuery, _SingleUpdateClause<C, _SingleIndexHintSpec<C>>>
            , _SingleUpdateClause<C, _SingleIndexHintSpec<C>> {

    }

    /*################################## blow batch single-table update spec ##################################*/

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link Statement._RowCountLimitClause}</li>
     *          <li>method {@link Statement._BatchParamClause}</li>
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
    interface _BatchLimitSpec<C> extends _RowCountLimitClause<C, _BatchParamClause<C, _UpdateSpec>>
            , Statement._BatchParamClause<C, _UpdateSpec> {

    }


    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link Statement._OrderByClause}</li>
     *          <li>method {@link _BatchLimitSpec}</li>
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
     *          <li>method {@link _BatchOrderBySpec}</li>
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
     *          <li>{@link Statement._WhereClause}</li>
     *          <li>method {@link MySQLUpdate._BatchSingleSetClause}</li>
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
    interface _BatchSingleWhereSpec<C> extends _BatchSingleSetClause<C>
            , _WhereClause<C, _BatchOrderBySpec<C>, _BatchSingleWhereAndSpec<C>> {

    }


    /**
     * <p>
     * This interface representing SET clause for batch single-table UPDATE.
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
    interface _BatchSingleSetClause<C> extends _BatchSetClause<C, TableField, _BatchSingleWhereSpec<C>> {

    }

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link MySQLQuery._IndexHintForOrderByClause}</li>
     *          <li>method {@link _BatchSingleSetClause}</li>
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
    interface _BatchSingleIndexHintSpec<C> extends MySQLQuery._IndexHintForOrderByClause<C, _BatchSingleIndexHintSpec<C>>
            , _BatchSingleSetClause<C> {

    }


    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link DialectStatement._WithCteClause}</li>
     *          <li>method {@link _SingleUpdateClause}</li>
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
    interface _BatchSingleWithAndUpdateSpec<C>
            extends DialectStatement._WithCteClause<C, SubQuery, _SingleUpdateClause<C, _BatchSingleIndexHintSpec<C>>>
            , _SingleUpdateClause<C, _BatchSingleIndexHintSpec<C>> {

    }


    /*################################## blow multi-table update api interface ##################################*/


    /**
     * <p>
     * This interface representing multi-table UPDATE clause for MySQL syntax.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <C>  criteria object java type
     * @param <UT> next clause java type
     * @param <US> next clause java type
     * @param <UP> next clause java type
     * @since 1.0
     */
    interface MultiUpdateClause<C, UT, US, UP> {

        UP update(Supplier<List<Hint>> hints, List<MySQLWords> modifiers
                , TableMeta<?> table);

        UT update(Supplier<List<Hint>> hints, List<MySQLWords> modifiers
                , TableMeta<?> table, String tableAlias);


        UP update(TableMeta<?> table);

        UT update(TableMeta<?> table, String tableAlias);

        <T extends TableItem> US update(Supplier<List<Hint>> hints, List<MySQLWords> modifiers
                , Supplier<T> supplier, String alias);

        <T extends TableItem> US update(Supplier<List<Hint>> hints, List<MySQLWords> modifiers
                , Function<C, T> function, String alias);


        <T extends TableItem> US update(Supplier<T> supplier, String alias);

        <T extends TableItem> US update(Function<C, T> function, String alias);

        <T extends TableItem> US updateLateral(Supplier<List<Hint>> hints, List<MySQLWords> modifiers
                , Supplier<T> supplier, String alias);

        <T extends TableItem> US updateLateral(Supplier<List<Hint>> hints, List<MySQLWords> modifiers
                , Function<C, T> function, String alias);

        <T extends TableItem> US updateLateral(Supplier<T> supplier, String alias);

        <T extends TableItem> US updateLateral(Function<C, T> function, String alias);

    }


    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link Statement._WhereAndClause}</li>
     *          <li>{@link Update._UpdateSpec}</li>
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
    interface _MultiWhereAndSpec<C> extends _WhereAndClause<C, _MultiWhereAndSpec<C>>
            , _UpdateSpec {


    }

    /**
     * <p>
     * This interface representing SET clause for multi-table update clause.
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
    interface _MultiSetClause<C> extends _SimpleSetClause<C, TableField, _MultiWhereSpec<C>> {

    }

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link Statement._WhereClause}</li>
     *          <li>{@link MySQLUpdate._MultiSetClause}</li>
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
    interface _MultiWhereSpec<C> extends _WhereClause<C, _UpdateSpec, _MultiWhereAndSpec<C>>
            , _MultiSetClause<C> {


    }


    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link MySQLQuery._IndexHintClause}</li>
     *          <li>method {@link Statement._AsClause}</li>
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
    interface _MultiIndexHintOnSpec<C> extends MySQLQuery._IndexHintForJoinClause<C, _MultiIndexHintOnSpec<C>>
            , Statement._OnClause<C, _MultiJoinSpec<C>> {

    }

    /**
     * <p>
     * This interface representing PARTITION clause for multi-table update clause.
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
    interface _MultiPartitionOnClause<C> extends MySQLQuery._PartitionClause<C, _AsClause<_MultiIndexHintOnSpec<C>>> {

    }

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>join clause</li>
     *          <li>{@link  _MultiSetClause}</li>
     *     </ul>
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <C> criteria object java type
     */
    interface _MultiJoinSpec<C> extends MySQLQuery._MySQLJoinClause<C, _MultiIndexHintOnSpec<C>, _OnClause<C, _MultiJoinSpec<C>>>
            , MySQLQuery._MySQLCrossJoinClause<C, _MultiIndexHintJoinSpec<C>, _MultiJoinSpec<C>>
            , MySQLQuery._MySQLIfJoinClause<C, _MultiJoinSpec<C>>
            , MySQLQuery._MySQLDialectJoinClause<_MultiPartitionOnClause<C>>
            , DialectStatement._DialectCrossJoinClause<_MultiPartitionJoinClause<C>>
            , _MultiSetClause<C> {


    }


    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link MySQLQuery._IndexHintForJoinClause}</li>
     *          <li>{@link _MultiJoinSpec}</li>
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
    interface _MultiIndexHintJoinSpec<C>
            extends MySQLQuery._IndexHintForJoinClause<C, _MultiIndexHintJoinSpec<C>>
            , _MultiJoinSpec<C> {

    }

    /**
     * <p>
     * This interface representing PARTITION clause for multi-table update clause.
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
     * This interface representing multi-table UPDATE clause for MySQL 5.7.
     * </p>
     * <p>/
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <C> criteria object java type
     * @since 1.0
     */
    interface _MultiUpdate57Clause<C> extends MySQLUpdate.MultiUpdateClause<
            C,
            _MultiIndexHintJoinSpec<C>,
            _MultiJoinSpec<C>,
            _MultiPartitionJoinClause<C>> {


    }

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link DialectStatement._WithCteClause}</li>
     *          <li>{@link MySQLUpdate._MultiUpdate57Clause}</li>
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
    interface _WithAndMultiUpdateSpec<C> extends DialectStatement._WithCteClause<C, SubQuery, _MultiUpdate57Clause<C>>
            , _MultiUpdate57Clause<C> {

    }

    /*################################## blow batch multi-table update spec ##################################*/


    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link Statement._BatchParamClause}</li>
     *          <li>method {@link Statement._WhereAndClause}</li>
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
            , _BatchParamClause<C, _UpdateSpec> {


    }

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link MySQLUpdate._BatchMultiSetClause}</li>
     *          <li>method {@link Statement._WhereClause}</li>
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
    interface _BatchMultiWhereSpec<C>
            extends _WhereClause<C, _BatchParamClause<C, _UpdateSpec>, _BatchMultiWhereAndSpec<C>>
            , _BatchMultiSetClause<C> {


    }

    /**
     * <p>
     * This interface representing index SET clause for batch multi-table update clause.
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
    interface _BatchMultiSetClause<C> extends _BatchSetClause<C, TableField, _BatchMultiWhereSpec<C>> {

    }


    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link MySQLQuery._IndexHintClause}</li>
     *          <li>{@link Statement._OnClause}</li>
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
    interface _BatchMultiIndexHintOnSpec<C> extends MySQLQuery._IndexHintForJoinClause<C, _BatchMultiIndexHintOnSpec<C>>
            , _OnClause<C, _BatchMultiJoinSpec<C>> {

    }

    /**
     * <p>
     * This interface representing PARTITION clause for batch multi-table UPDATE clause.
     * </p>
     * <p>/
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
     *          <li>{@link MySQLUpdate._BatchMultiSetClause}</li>
     *     </ul>
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <C> criteria object java type
     */
    interface _BatchMultiJoinSpec<C>
            extends MySQLQuery._MySQLJoinClause<C, _BatchMultiIndexHintOnSpec<C>, _OnClause<C, _BatchMultiJoinSpec<C>>>
            , MySQLQuery._MySQLCrossJoinClause<C, _BatchMultiIndexHintJoinSpec<C>, _BatchMultiJoinSpec<C>>
            , MySQLQuery._MySQLIfJoinClause<C, _BatchMultiJoinSpec<C>>
            , MySQLQuery._MySQLDialectJoinClause<_BatchMultiPartitionOnClause<C>>
            , DialectStatement._DialectCrossJoinClause<_BatchMultiPartitionJoinClause<C>>
            , _BatchMultiSetClause<C> {


    }


    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link MySQLQuery._IndexHintForJoinClause}</li>
     *          <li>{@link _BatchMultiJoinSpec}</li>
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
    interface _BatchMultiIndexHintJoinSpec<C>
            extends MySQLQuery._IndexHintForJoinClause<C, _BatchMultiIndexHintJoinSpec<C>>
            , _BatchMultiJoinSpec<C> {

    }

    /**
     * <p>
     * This interface representing PARTITION clause for batch multi-table UPDATE clause.
     * </p>
     * <p>/
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
     * This interface representing batch multi-table UPDATE clause.
     * </p>
     * <p>/
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <C> criteria object java type
     * @since 1.0
     */
    interface _BatchMultiUpdateClause<C> extends MySQLUpdate.MultiUpdateClause<
            C,
            MySQLUpdate._BatchMultiIndexHintJoinSpec<C>,
            MySQLUpdate._BatchMultiJoinSpec<C>,
            MySQLUpdate._BatchMultiPartitionJoinClause<C>> {

    }

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link DialectStatement._WithCteClause}</li>
     *          <li>{@link _BatchMultiUpdateClause}</li>
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
    interface _BatchWithAndMultiUpdateSpec<C> extends DialectStatement._WithCteClause<C, SubQuery, _BatchMultiUpdateClause<C>>
            , _BatchMultiUpdateClause<C> {

    }


}
