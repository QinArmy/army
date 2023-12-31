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

package io.army.criteria.postgre;

import io.army.criteria.*;
import io.army.criteria.impl.SQLs;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;

import java.util.function.Supplier;

/**
 * <p>
 * This interface representing Postgre UPDATE syntax.
 *
 * @see <a href="https://www.postgresql.org/docs/current/sql-update.html">Postgre UPDATE syntax</a>
 * @since 0.6.0
 */
public interface PostgreMerge extends PostgreStatement, DmlStatement {


    interface _AsMergeClause<I extends Item> extends Item {

        I asMerge();

    }

    interface _MergeInsertValuesParensClause<T, I extends Item>
            extends InsertStatement._ValuesParensClause<T, _MergeInsertValuesParenCommaSpec<T, I>> {

    }

    interface _MergeInsertValuesParenCommaSpec<T, I extends Item>
            extends _CommaClause<_MergeInsertValuesParensClause<T, I>>, _DmlInsertClause<I> {

    }


    interface _MergeInsertValuesDefaultSpec<T, I extends Item>
            extends InsertStatement._StaticColumnDefaultClause<T, _MergeInsertValuesDefaultSpec<T, I>>
            , InsertStatement._DomainValueClause<T, _DmlInsertClause<I>>
            , InsertStatement._DynamicValuesClause<T, _DmlInsertClause<I>>
            , InsertStatement._StaticValuesClause<_MergeInsertValuesParensClause<T, I>> {

    }


    interface _MergeInsertOverridingValueSpec<T, I extends Item>
            extends _MergeInsertValuesDefaultSpec<T, I>
            , PostgreInsert._OverridingValueClause<_MergeInsertValuesDefaultSpec<T, I>> {

    }


    interface _MergeInsertColumnListSpec<T, I extends Item>
            extends InsertStatement._ColumnListParensClause<T, _MergeInsertOverridingValueSpec<T, I>>
            , _MergeInsertOverridingValueSpec<T, I> {

    }

    interface _MergerUpdateSetClause<T, I extends Item>
            extends UpdateStatement._StaticRowSetClause<FieldMeta<T>, _MergerUpdateSetSpec<T, I>>
            , UpdateStatement._DynamicSetClause<UpdateStatement._RowPairs<FieldMeta<T>>, _DmlUpdateSpec<I>> {

    }

    interface _MergerUpdateSetSpec<T, I extends Item> extends _MergerUpdateSetClause<T, I>
            , _DmlUpdateSpec<I> {

    }


    interface _MergeWhenThenClause<T, I extends Item> {

        _MergeWhenSpec<T, I> thenDoNothing();
    }

    interface _MatchedThenClause<T, I extends Item> extends _MergeWhenThenClause<T, I> {

        _MergerUpdateSetClause<T, _MergeWhenSpec<T, I>> thenUpdate();


        _MergeWhenSpec<T, I> thenDelete();

    }

    interface _NotMatchedThenClause<T, I extends Item> extends _MergeWhenThenClause<T, I> {

        _MergeInsertColumnListSpec<T, _MergeWhenSpec<T, I>> thenInsert();

    }

    interface _MatchedThenSpec<T, I extends Item> extends _MatchedThenClause<T, I>
            , _WhereAndClause<_MatchedThenSpec<T, I>> {

    }

    interface _NotMatchedThenSpec<T, I extends Item> extends _NotMatchedThenClause<T, I>
            , _WhereAndClause<_NotMatchedThenSpec<T, I>> {

    }


    interface _MergeWhenClause<T, I extends Item> {

        _MatchedThenSpec<T, I> whenMatched();

        _NotMatchedThenSpec<T, I> whenNotMatched();

    }

    interface _MergeWhenSpec<T, I extends Item> extends _MergeWhenClause<T, I>
            , _AsMergeClause<I> {

    }


    interface _MergeOnClause<T, I extends Item> extends _OnClause<_MergeWhenClause<T, I>> {

    }


    interface _MergeUsingDataSourceClause<T, I extends Item> {

        _MergeOnClause<T, I> using(TableMeta<?> table);

        _MergeOnClause<T, I> using(TableMeta<?> table, SQLs.WordAs as, String alias);

        _AsClause<_MergeOnClause<T, I>> using(Supplier<SubQuery> supplier);

    }


}
