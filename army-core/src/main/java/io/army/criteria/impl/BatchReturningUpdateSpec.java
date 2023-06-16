package io.army.criteria.impl;


import io.army.criteria.Item;
import io.army.criteria.Statement;
import io.army.criteria.dialect.ReturningUpdate;

/**
 * package interface,application developer invisible.
 */
interface BatchReturningUpdateSpec<R extends Item> extends ReturningUpdate, Statement._BatchParamClause<R> {


}
