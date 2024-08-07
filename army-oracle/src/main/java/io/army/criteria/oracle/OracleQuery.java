/*
 * Copyright 2023-2043 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
 * * @see <a href="https://docs.oracle.com/en/database/oracle/oracle-database/21/sqlrf/SELECT.html#GUID-CFA006CA-6FF1-4972-821E-6996142A51C6">Oracle SELECT syntax</a>
 *
 * @since 0.6.0
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


    interface _OracleStaticOrderByClause<OR> {

        OR orderSiblingsBy(Expression exp1);

        OR orderSiblingsBy(Expression exp1, Expression exp2);

        OR orderSiblingsBy(Expression exp1, Expression exp2, Expression exp3);

//        OR orderSiblingsBy(Expression exp1, SQLs.AscDesc ascDesc);
//
//        OR orderSiblingsBy(Expression exp1, SQLs.AscDesc ascDesc, SQLs.NullsFirstLast nullOption);
//
//        OR orderSiblingsBy(Expression exp1, SQLs.AscDesc ascDesc1, Expression exp2, SQLs.AscDesc ascDesc2);

    }

    interface _OracleDynamicOrderByClause<OR> extends _DynamicOrderByClause0<SortNullItems, OR> {

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


    interface _UnionOffsetSpec<I extends Item> extends _QueryFetchOffsetClause<_UnionFetchSpec<I>>
            , _UnionLockSpec<I> {

    }

    interface _UnionOrderByCommaSpec<I extends Item>
            extends _OrderByCommaClause<_UnionOrderByCommaSpec<I>>
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


    interface _OffsetSpec<I extends Item> extends _QueryFetchOffsetClause<_FetchSpec<I>>
            , _LockSpec<I> {

    }

    interface _OrderByCommaSpec<I extends Item>
            extends _OrderByCommaClause<_OrderByCommaSpec<I>>
            , _OffsetSpec<I> {

    }

    interface _OrderBySpec<I extends Item> extends _OracleStaticOrderByClause<_OrderByCommaSpec<I>>
            , _OracleDynamicOrderByClause<_OffsetSpec<I>>
            , _OffsetSpec<I> {

    }

    interface _WindowCommaSpec<I extends Item> extends Window._StaticWindowCommaClause<_WindowCommaSpec<I>>
            , _OrderBySpec<I> {

    }




    interface _MinWithSpec<I extends Item> extends Item {

    }


    interface _UnionAndQuerySpec<I extends Item> extends _MinWithSpec<I>,
            _DynamicParensRowSetClause<_UnionAndQuerySpec<_UnionOrderBySpec<I>>, _UnionOrderBySpec<I>> {

    }

}
