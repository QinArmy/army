package io.army.criteria;


import io.army.criteria.impl._ParenExpression;

public interface ParenPredicate<I extends Item> extends _ParenExpression<I>, IPredicate {


    @Override
    ParenPredicate<I> bracket();


}
