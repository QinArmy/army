package io.army.criteria.impl;


import io.army.criteria.Delete;
import io.army.criteria.Item;
import io.army.criteria.Statement;

/**
 * package interface,application developer invisible.
 */
interface BatchDeleteSpec<R extends Item> extends Delete, Statement._BatchParamClause<R> {


}
