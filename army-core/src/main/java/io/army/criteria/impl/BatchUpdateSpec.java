package io.army.criteria.impl;

import io.army.criteria.Item;
import io.army.criteria.Statement;
import io.army.criteria.Update;


/**
 * package interface,application developer invisible.
 */
interface BatchUpdateSpec<R extends Item> extends Update, Statement._BatchParamClause<R> {


}
