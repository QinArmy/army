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

package io.army.criteria.standard;

import io.army.criteria.InsertStatement;
import io.army.criteria.Item;
import io.army.meta.ComplexTableMeta;
import io.army.meta.ParentTableMeta;
import io.army.meta.SimpleTableMeta;

/**
 * <p>
 * This interface representing standard insert statement.
 *
 * @since 0.6.0
 */
public interface StandardInsert extends StandardStatement {


    interface _StandardValuesParensClause<T, I extends Item>
            extends InsertStatement._ValuesParensClause<T, _ValuesParensCommaSpec<T, I>> {

    }

    interface _ValuesParensCommaSpec<T, I extends Item>
            extends _CommaClause<_StandardValuesParensClause<T, I>>, _DmlInsertClause<I> {

    }

    interface _ValuesColumnDefaultSpec<T, I extends Item>
            extends InsertStatement._FullColumnDefaultClause<T, _ValuesColumnDefaultSpec<T, I>>,
            InsertStatement._DomainValueClause<T, _DmlInsertClause<I>>,
            InsertStatement._DynamicValuesClause<T, _DmlInsertClause<I>>,
            InsertStatement._StaticValuesClause<_StandardValuesParensClause<T, I>> {

    }


    interface _ComplexColumnDefaultSpec<T, I extends Item> extends _ValuesColumnDefaultSpec<T, I>,
            InsertStatement._QueryInsertSpaceClause<StandardQuery._SelectSpec<_DmlInsertClause<I>>, _DmlInsertClause<I>> {

    }

    interface _ColumnListSpec<T, I extends Item>
            extends InsertStatement._ColumnListParensClause<T, _ComplexColumnDefaultSpec<T, I>>,
            _ComplexColumnDefaultSpec<T, I> {

    }

    interface _ChildInsertIntoClause<I extends Item, P> extends Item {

        <T> _ColumnListSpec<T, I> insertInto(ComplexTableMeta<P, T> table);

    }


    interface _ChildWithSpec<I extends Item, P> extends _StandardDynamicWithClause<_ChildInsertIntoClause<I, P>>,
            _StandardStaticWithClause<_ChildInsertIntoClause<I, P>>,
            _ChildInsertIntoClause<I, P> {

    }


    /*-------------------below 1.0 api interfaces -------------------*/


    interface _PrimaryInsertInto10Clause<I extends Item> extends Item {

        <T> _ColumnListSpec<T, I> insertInto(SimpleTableMeta<T> table);

        <P> _ColumnListSpec<P, InsertStatement._ParentInsert20<I, _ChildInsertIntoClause<I, P>>> insertInto(ParentTableMeta<P> table);
    }


    interface _PrimaryPreferLiteral10Spec<I extends Item>
            extends InsertStatement._PreferLiteralClause<_PrimaryInsertInto10Clause<I>>,
            _PrimaryInsertInto10Clause<I> {

    }

    interface _PrimaryNullOption10Spec<I extends Item>
            extends InsertStatement._NullOptionClause<_PrimaryPreferLiteral10Spec<I>>,
            _PrimaryPreferLiteral10Spec<I> {

    }

    interface _PrimaryOptionSpec<I extends Item>
            extends InsertStatement._MigrationOptionClause<_PrimaryNullOption10Spec<I>>,
            _PrimaryNullOption10Spec<I> {

    }

    /*-------------------below 2.0 api interfaces -------------------*/


    interface _PrimaryInsertInto20Clause<I extends Item> extends Item {

        <T> _ColumnListSpec<T, I> insertInto(SimpleTableMeta<T> table);

        <P> _ColumnListSpec<P, InsertStatement._ParentInsert20<I, _ChildWithSpec<I, P>>> insertInto(ParentTableMeta<P> table);
    }


    interface _WithSpec<I extends Item> extends _StandardDynamicWithClause<_PrimaryInsertInto20Clause<I>>,
            _StandardStaticWithClause<_PrimaryInsertInto20Clause<I>>,
            _PrimaryInsertInto20Clause<I> {

    }

    interface _PrimaryPreferLiteral20Spec<I extends Item>
            extends InsertStatement._PreferLiteralClause<_WithSpec<I>>, _WithSpec<I> {

    }

    interface _PrimaryNullOption20Spec<I extends Item>
            extends InsertStatement._NullOptionClause<_PrimaryPreferLiteral20Spec<I>>,
            _PrimaryPreferLiteral20Spec<I> {

    }

    interface _PrimaryOption20Spec<I extends Item>
            extends InsertStatement._MigrationOptionClause<_PrimaryNullOption20Spec<I>>,
            _PrimaryNullOption20Spec<I> {

    }


}
