package io.army.criteria.impl;

import io.army.criteria.Clause;
import io.army.criteria.mysql.inner._SelfDescribed;


/**
 * package interface
 */
interface ArmyFuncClause extends Clause, _SelfDescribed {

    void endClause();


}
