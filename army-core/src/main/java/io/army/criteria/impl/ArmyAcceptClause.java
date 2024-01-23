package io.army.criteria.impl;


import io.army.criteria.Clause;

import java.util.List;

/**
 * package interface
 */
interface ArmyAcceptClause<T> extends Clause {


    List<T> endClause();


}
