package io.army.criteria.postgre;

import io.army.criteria.*;
import io.army.criteria.dialect.SubQuery;
import io.army.criteria.impl.SQLs;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;

import java.util.function.Supplier;

/**
 * <p>
 * This interface representing Postgre UPDATE syntax.
 * </p>
 *
 * @see <a href="https://www.postgresql.org/docs/current/sql-update.html">Postgre UPDATE syntax</a>
 * @since 1.0
 */
public interface PostgreMerge extends PostgreStatement, DmlStatement {


    interface _AsMergeClause<I extends Item> extends Item {

        I asMerge();

    }

    interface _MergeInsertValuesLeftParenClause<T, I extends Item>
            extends InsertStatement._StaticValueLeftParenClause<T, _MergeInsertValuesLeftParenSpec<T, I>> {

    }

    interface _MergeInsertValuesLeftParenSpec<T, I extends Item>
            extends _MergeInsertValuesLeftParenClause<T, I>, _DmlInsertClause<I> {

    }


    interface _MergeInsertValuesDefaultSpec<T, I extends Item>
            extends InsertStatement._ColumnDefaultClause<T, _MergeInsertValuesDefaultSpec<T, I>>
            , InsertStatement._DomainValueClause<T, _DmlInsertClause<I>>
            , InsertStatement._DynamicValuesClause<T, _DmlInsertClause<I>>
            , InsertStatement._StaticValuesClause<_MergeInsertValuesLeftParenClause<T, I>> {

    }


    interface _MergeInsertOverridingValueSpec<T, I extends Item>
            extends _MergeInsertValuesDefaultSpec<T, I>
            , PostgreInsert._OverridingValueClause<_MergeInsertValuesDefaultSpec<T, I>> {

    }


    interface _MergeInsertColumnListSpec<T, I extends Item>
            extends InsertStatement._ColumnListClause<T, _MergeInsertOverridingValueSpec<T, I>>
            , _MergeInsertOverridingValueSpec<T, I> {

    }

    interface _MergerUpdateSetClause<T, I extends Item>
            extends UpdateStatement._StaticRowSetClause<FieldMeta<T>, _MergerUpdateSetSpec<T, I>>
            , UpdateStatement._DynamicSetClause<RowPairs<FieldMeta<T>>, _DmlUpdateSpec<I>> {

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
