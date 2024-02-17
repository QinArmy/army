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
import io.army.criteria.impl.Postgres;
import io.army.criteria.impl.SQLs;
import io.army.meta.FieldMeta;
import io.army.meta.SimpleTableMeta;
import io.army.meta.TableMeta;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * <p>This interface representing Postgre MERGE syntax.
 *
 * @see Postgres#singleMerge()
 * @see <a href="https://www.postgresql.org/docs/current/sql-merge.html">MERGE — conditionally insert, update, or delete rows of a table</a>
 * @since 0.6.0
 */
public interface PostgreMerge extends PostgreStatement, SimpleDmlStatement {


    interface _MergeUpdateSetClause<T> extends UpdateStatement._StaticRowSetClause<FieldMeta<T>, _MergeUpdateSetSpec<T>>,
            UpdateStatement._DynamicSetClause<UpdateStatement._RowPairs<FieldMeta<T>>, _MergeUpdateSetSpec<T>> {


    }

    interface _MergeUpdateSetSpec<T> extends _MergeUpdateSetClause<T>, _EndFlag {

    }


    interface _MergeInsertStaticValuesParensClause<T> extends InsertStatement._ValuesParensClause<T, _EndFlag> {

    }


    interface _ValuesDefaultSpec<T> extends InsertStatement._FullColumnDefaultClause<T, _ValuesDefaultSpec<T>>,
            InsertStatement._DomainValueClause<T, _EndFlag>,
            InsertStatement._DynamicValuesClause<T, _EndFlag>,
            InsertStatement._StaticValuesClause<_MergeInsertStaticValuesParensClause<T>> {

    }


    interface _MergeInsertOverridingValueSpec<T> extends PostgreInsert._OverridingValueClause<_ValuesDefaultSpec<T>>,
            _ValuesDefaultSpec<T> {

    }


    /**
     * <p>This interface is public interface that developer can directly use,but just delete(), for example:
     * <pre>
     *     <code><br/>
     *    &#64;Test
     *    public void simple(final SyncLocalSession session) {
     *
     *        final PostgreMerge stmt;
     *        stmt = Postgres.singleMerge()
     *                .mergeInto(Captcha_.T, AS, "c")
     *                .using(RegisterRecord_.T, AS, "r").on(Captcha_.requestNo::equal, RegisterRecord_.requestNo)
     *                .whenNotMatched().then(Statement.DoNothingClause::doNothing)
     *                .whenMatched().then(PostgreMerge.MatchedMergeActionSpec::delete)
     *                .asCommand();
     *
     *        final long rows;
     *        rows = session.update(stmt);
     *        LOG.debug("{} row : {}", session.name(), rows);
     *    }
     *     </code>
     * </pre>
     */
    interface MatchedMergeActionSpec<T> extends DoNothingClause<_EndFlag> {

        _MergeUpdateSetClause<T> update();

        _EndFlag delete();

    }

    interface _MergeInsertClause<T> {

        _MergeInsertOverridingValueSpec<T> insert();

        _MergeInsertOverridingValueSpec<T> insert(Consumer<InsertStatement._StaticColumnSpaceClause<T>> consumer);

        _MergeInsertOverridingValueSpec<T> insert(SQLs.SymbolSpace space, Consumer<Consumer<FieldMeta<T>>> consumer);

    }

    interface _MergeInsertPreferLiteralSpec<T> extends InsertStatement._PreferLiteralClause<_MergeInsertClause<T>>,
            _MergeInsertClause<T> {

    }

    interface _MergeInsertNullOptionSpec<T> extends InsertStatement._NullOptionClause<_MergeInsertPreferLiteralSpec<T>>,
            _MergeInsertPreferLiteralSpec<T> {

    }

    interface _NotMatchedMergeActionClause<T> extends InsertStatement._MigrationOptionClause<_MergeInsertNullOptionSpec<T>>,
            _MergeInsertNullOptionSpec<T>, DoNothingClause<_EndFlag> {

    }



    interface _MatchedThenClause<T, I extends Item> extends _WhereAndClause<_MatchedThenClause<T, I>> {

        _MergeWhenSpec<T, I> then(Function<MatchedMergeActionSpec<T>, _EndFlag> function);
    }

    interface _NotMatchedThenClause<T, I extends Item> extends _WhereAndClause<_NotMatchedThenClause<T, I>> {

        _MergeWhenSpec<T, I> then(Function<_NotMatchedMergeActionClause<T>, _EndFlag> function);
    }


    interface _MatchedDynamicThenClause<T> {

        _MergeDynamicWhenClause<T> then(Function<MatchedMergeActionSpec<T>, _EndFlag> function);
    }

    interface _NotMatchedDynamicThenClause<T> {

        _MergeDynamicWhenClause<T> then(Function<_NotMatchedMergeActionClause<T>, _EndFlag> function);
    }


    interface _MergeDynamicWhenClause<T> {

        _MatchedDynamicThenClause<T> matched();

        _NotMatchedDynamicThenClause<T> notMatched();

    }


    interface _MergeWhenClause<T, I extends Item> extends Item {

        _MatchedThenClause<T, I> whenMatched();

        _NotMatchedThenClause<T, I> whenNotMatched();

        _MergeWhenSpec<T, I> when(Consumer<_MergeDynamicWhenClause<T>> consumer);
    }


    interface _MergeWhenSpec<T, I extends Item> extends _MergeWhenClause<T, I>, _AsCommandClause<I> {

    }


    interface _MergeUsingClause<T, I extends Item> {

        _OnClause<_MergeWhenClause<T, I>> using(SQLs.WordOnly only, TableMeta<?> sourceTable, SQLs.WordAs as, String sourceAlias);

        _OnClause<_MergeWhenClause<T, I>> using(TableMeta<?> sourceTable, SQLs.WordAs as, String sourceAlias);

        _AsClause<_OnClause<_MergeWhenClause<T, I>>> using(SubQuery sourceQuery);

        _AsClause<_OnClause<_MergeWhenClause<T, I>>> using(Supplier<SubQuery> supplier);

    }


//    interface _ChildMergeIntoClause<P> extends Item {
//
//        <T> _MergeUsingClause<T, PostgreMerge> mergeInto(SQLs.WordOnly only, ComplexTableMeta<P, T> targetTable, SQLs.WordAs as, String targetAlias);
//
//        <T> _MergeUsingClause<T, PostgreMerge> mergeInto(ComplexTableMeta<P, T> targetTable, SQLs.WordAs as, String targetAlias);
//    }
//
//    interface _ChildWithSpec<P> extends _PostgreDynamicWithClause<_ChildMergeIntoClause<P>>,
//            PostgreQuery._PostgreStaticWithClause<_ChildMergeIntoClause<P>>,
//            _ChildMergeIntoClause<P> {
//
//    }
//
//    interface _MergeChildClause<P> extends InsertStatement._ChildPartClause<_ChildWithSpec<P>>, PostgreMerge {
//
//    }


    interface _MergeIntoClause extends Item {

        <T> _MergeUsingClause<T, PostgreMerge> mergeInto(SQLs.WordOnly only, SimpleTableMeta<T> targetTable, SQLs.WordAs as, String targetAlias);

        <T> _MergeUsingClause<T, PostgreMerge> mergeInto(SimpleTableMeta<T> targetTable, SQLs.WordAs as, String targetAlias);


//        <T> _MergeUsingClause<T, _MergeChildClause<T>> mergeInto(SQLs.WordOnly only, ParentTableMeta<T> targetTable, SQLs.WordAs as, String targetAlias);
//
//        <T> _MergeUsingClause<T, _MergeChildClause<T>> mergeInto(ParentTableMeta<T> targetTable, SQLs.WordAs as, String targetAlias);

    }


    interface _WithSpec extends _PostgreDynamicWithClause<_MergeIntoClause>,
            PostgreQuery._PostgreStaticWithClause<_MergeIntoClause>,
            _MergeIntoClause {

    }


}
