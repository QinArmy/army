package io.army.criteria.oracle;

import io.army.criteria.Expression;
import io.army.criteria.Item;
import io.army.criteria.Query;
import io.army.criteria.TableField;
import io.army.criteria.dialect.SortNullItems;
import io.army.criteria.dialect.Window;

import java.util.function.Consumer;
import java.util.function.Supplier;


/**
 * <p>
 * This interface representing Oracle SELECT syntax.
 * </p>
 *
 * @see <a href="https://docs.oracle.com/en/database/oracle/oracle-database/21/sqlrf/SELECT.html#GUID-CFA006CA-6FF1-4972-821E-6996142A51C6">Oracle SELECT syntax</a>
 * @since 1.0
 */
public interface OracleQuery extends Query, OracleStatement {

    /**
     * @see <a href="https://docs.oracle.com/en/database/oracle/oracle-database/21/sqlrf/SELECT.html#GUID-CFA006CA-6FF1-4972-821E-6996142A51C6">Lock wait syntax</a>
     */
    interface _OracleLockWaitOptionClause<LR> extends _MinLockStrengthClause<LR> {

        LR wait(int seconds);

        LR wait(Supplier<Integer> supplier);

        LR ifWait(Supplier<Integer> supplier);
    }


    /**
     * @see <a href="https://docs.oracle.com/en/database/oracle/oracle-database/21/sqlrf/SELECT.html#GUID-CFA006CA-6FF1-4972-821E-6996142A51C6">Lock of colunn syntax</a>
     */
    interface _OracleLockOfColumnClause<OR> {

        OR of(TableField field);

        OR of(TableField field1, TableField field2);

        OR of(TableField field1, TableField field2, TableField field3);

        OR of(TableField field1, TableField field2, TableField field3, TableField field4);

        OR of(Consumer<Consumer<TableField>> consumer);

        OR ifOf(Consumer<Consumer<TableField>> consumer);

    }


    interface _OracleStaticOrderByClause<OR> extends _StaticOrderByClause<OR> {

        OR orderSiblingsBy(Expression exp1);

        OR orderSiblingsBy(Expression exp1, Expression exp2);

        OR orderSiblingsBy(Expression exp1, Expression exp2, Expression exp3);

        OR orderSiblingsBy(Expression exp1, AscDesc ascDesc);

        OR orderSiblingsBy(Expression exp1, AscDesc ascDesc, NullsFirstLast nullOption);

        OR orderSiblingsBy(Expression exp1, AscDesc ascDesc1, Expression exp2, AscDesc ascDesc2);

    }

    interface _OracleDynamicOrderByClause<OR> extends _DynamicOrderByClause<SortNullItems,OR> {

        OR orderSiblingsBy(Consumer<SortNullItems> consumer);

        OR ifOrderSiblingsBy(Consumer<SortNullItems> consumer);
    }





    interface _WindowAsClause<I extends Item> {

    }


    interface _UnionLockWaitOptionSpec<I extends Item>
            extends _OracleLockWaitOptionClause<_AsQueryClause<I>>, _AsQueryClause<I> {

    }


    interface _UnionLockOfColumnSpec<I extends Item>
            extends _OracleLockOfColumnClause<_UnionLockWaitOptionSpec<I>>
            , _UnionLockWaitOptionSpec<I> {

    }


    interface _UnionLockSpec<I extends Item>
            extends _StaticForUpdateClause<_UnionLockOfColumnSpec<I>>
            , _AsQueryClause<I> {


    }

    interface _UnionFetchSpec<I extends Item> extends _QueryFetchClause<_UnionLockSpec<I>>
            , _UnionLockSpec<I> {

    }


    interface _UnionOffsetSpec<I extends Item> extends _QueryOffsetClause<_UnionFetchSpec<I>>
            , _UnionLockSpec<I> {

    }

    interface _UnionOrderByCommaSpec<I extends Item>
            extends _StaticOrderByNullsCommaClause<_UnionOrderByCommaSpec<I>>
            , _UnionOffsetSpec<I> {

    }


    interface _UnionSpec<I extends Item> extends _StaticUnionClause<_UnionAndQuerySpec<I>>
            , _StaticIntersectClause<_UnionAndQuerySpec<I>>
            , _StaticMinusClause<_UnionAndQuerySpec<I>>
            , _AsQueryClause<I> {

    }

    interface _UnionOrderBySpec<I extends Item> extends _OracleStaticOrderByClause<_UnionOrderByCommaSpec<I>>
            , _OracleDynamicOrderByClause<_UnionOffsetSpec<I>>
            , _UnionOffsetSpec<I>
            , _UnionSpec<I> {

    }


    interface _LockWaitOptionSpec<I extends Item>
            extends _OracleLockWaitOptionClause<_AsQueryClause<I>>, _AsQueryClause<I> {

    }


    interface _LockOfColumnSpec<I extends Item>
            extends _OracleLockOfColumnClause<_LockWaitOptionSpec<I>>
            , _LockWaitOptionSpec<I> {

    }


    interface _LockSpec<I extends Item>
            extends _StaticForUpdateClause<_LockOfColumnSpec<I>>
            , _AsQueryClause<I> {


    }

    interface _FetchSpec<I extends Item> extends _QueryFetchClause<_LockSpec<I>>
            , _LockSpec<I> {

    }


    interface _OffsetSpec<I extends Item> extends _QueryOffsetClause<_FetchSpec<I>>
            , _LockSpec<I> {

    }

    interface _OrderByCommaSpec<I extends Item>
            extends _StaticOrderByNullsCommaClause<_OrderByCommaSpec<I>>
            , _OffsetSpec<I> {

    }

    interface _OrderBySpec<I extends Item> extends _OracleStaticOrderByClause<_OrderByCommaSpec<I>>
            , _OracleDynamicOrderByClause<_OffsetSpec<I>>
            , _OffsetSpec<I> {

    }

    interface _WindowCommaSpec<I extends Item> extends Window._StaticWindowCommaClause<_WindowCommaSpec<I>>
            , _OrderBySpec<I> {

    }

    interface _WindowsSpec<I extends Item> extends Window._StaticWindowClause<_WindowCommaSpec<I>>
            , Window._DynamicWindowClause<OracleWindowBuilder, _OrderBySpec<I>>
            , _OrderBySpec<I> {

    }


    interface _MinWithSpec<I extends Item> extends Item {

    }


    interface _UnionAndQuerySpec<I extends Item> extends _MinWithSpec<I>,
            _DynamicParensRowSetClause<_UnionAndQuerySpec<_UnionOrderBySpec<I>>, _UnionOrderBySpec<I>> {

    }

}
