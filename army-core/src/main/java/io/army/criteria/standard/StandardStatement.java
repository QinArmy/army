package io.army.criteria.standard;

import io.army.criteria.Item;
import io.army.criteria.Statement;

/**
 * <p>
 * This interface representing statement with standard syntax.
 * </p>
 *
 * @see StandardQuery
 * @see StandardInsert
 * @since 1.0
 */
public interface StandardStatement extends Statement {


    interface _StandardJoinClause<FS extends Item, JS extends Item>
            extends _JoinClause<_OnClause<FS>, _AsClause<JS>>,
            _CrossJoinClause<FS, _AsClause<FS>>,
            _JoinNestedClause<_NestedLeftParenSpec<JS>>,
            _CrossJoinNestedClause<_NestedLeftParenSpec<FS>>,
            _DynamicJoinClause<StandardJoins, FS>,
            _DynamicCrossJoinClause<StandardCrosses, FS> {

    }


    interface _StandardNestedJoinClause<I extends Item>
            extends _JoinClause<_NestedOnSpec<I>, _AsClause<_NestedOnSpec<I>>>,
            _CrossJoinClause<_NestedJoinSpec<I>, _AsClause<_NestedJoinSpec<I>>>,
            _JoinNestedClause<_NestedLeftParenSpec<_NestedOnSpec<I>>>,
            _CrossJoinNestedClause<_NestedLeftParenSpec<_NestedJoinSpec<I>>>,
            _DynamicJoinClause<StandardJoins, _NestedJoinSpec<I>>,
            _DynamicCrossJoinClause<StandardCrosses, _NestedJoinSpec<I>> {

    }


    interface _NestedJoinSpec<I extends Item> extends _StandardNestedJoinClause<I>, _RightParenClause<I> {

    }

    interface _NestedOnSpec<I extends Item> extends _OnClause<_NestedJoinSpec<I>>, _NestedJoinSpec<I> {

    }


    interface _NestedLeftParenSpec<I extends Item>
            extends _NestedLeftParenClause<_StandardNestedJoinClause<I>, _AsClause<_StandardNestedJoinClause<I>>>,
            _LeftParenClause<_NestedLeftParenSpec<_StandardNestedJoinClause<I>>> {

    }


    interface _DynamicJoinSpec extends _StandardJoinClause<_DynamicJoinSpec, _OnClause<_DynamicJoinSpec>> {

    }


}
