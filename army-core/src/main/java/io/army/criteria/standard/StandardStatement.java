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


    interface _StandardJoinClause<FS extends Item, JS extends Item> extends _JoinClause<JS, _AsClause<JS>>,
            _CrossJoinClause<FS, _AsClause<FS>>,
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
            extends _NestedLeftParenClause<_StandardNestedJoinClause<I>, _AsClause<_StandardNestedJoinClause<I>>>,
            _LeftParenNestedClause<_NestedLeftParenSpec<_StandardNestedJoinClause<I>>, _StandardNestedJoinClause<I>> {

    }


    interface _DynamicJoinSpec extends _StandardJoinClause<_DynamicJoinSpec, _OnClause<_DynamicJoinSpec>> {

    }

    interface _StandardDynamicNestedClause<R extends Item>
            extends _DynamicTabularNestedClause<_NestedLeftParenSpec<R>, R> {

    }


}
