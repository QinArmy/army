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

import io.army.criteria.Item;
import io.army.criteria.Statement;

/**
 * <p>
 * This interface representing statement with standard syntax.
 * * @see StandardQuery
 *
 * @see StandardInsert
 * @since 0.6.0
 */
public interface StandardStatement extends Statement {

    interface _StandardDynamicWithClause<WE extends Item> extends _DynamicWithClause<StandardCtes, WE> {

    }

    interface _StandardStaticWithClause<I extends Item> extends _StaticWithClause<StandardQuery._StaticCteParensSpec<I>> {

    }


    interface _StandardJoinClause<FS extends Item, JS extends Item> extends _JoinModifierTabularClause<JS, _AsClause<JS>>,
            _CrossJoinModifierTabularClause<FS, _AsClause<FS>>,
            _JoinNestedClause<_NestedLeftParenSpec<JS>, JS>,
            _CrossJoinNestedClause<_NestedLeftParenSpec<FS>, FS>,
            _DynamicJoinClause<StandardJoins, FS>,
            _DynamicCrossJoinClause<StandardCrosses, FS> {

    }


    interface _StandardNestedJoinClause<I extends Item>
            extends _StandardJoinClause<_NestedJoinSpec<I>, _NestedOnSpec<I>> {

    }


    interface _NestedJoinSpec<I extends Item> extends _StandardNestedJoinClause<I>, _RightParenClause<I> {

    }

    interface _NestedOnSpec<I extends Item> extends _OnClause<_NestedJoinSpec<I>>, _NestedJoinSpec<I> {

    }


    interface _NestedLeftParenSpec<I extends Item>
            extends _NestedLeftParenModifierTabularClause<_StandardNestedJoinClause<I>, _AsClause<_StandardNestedJoinClause<I>>>,
            _LeftParenNestedClause<_NestedLeftParenSpec<_StandardNestedJoinClause<I>>, _StandardNestedJoinClause<I>> {

    }


    interface _DynamicJoinSpec extends _StandardJoinClause<_DynamicJoinSpec, _OnClause<_DynamicJoinSpec>> {

    }

    interface _StandardDynamicNestedClause<R extends Item>
            extends _DynamicTabularNestedClause<_NestedLeftParenSpec<R>, R> {

    }


}
