package io.army.criteria.impl;

import io.army.criteria.Clause;
import io.army.criteria.impl.inner._SelfDescribed;


/**
 * package interface
 */
interface ArmyClause extends Clause, _SelfDescribed {

    void endClause();


}
