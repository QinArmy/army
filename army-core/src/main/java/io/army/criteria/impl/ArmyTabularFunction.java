package io.army.criteria.impl;

import io.army.criteria.impl.inner._DerivedTable;

/**
 * <p>
 * package interface
*
 * @since 1.0
 */
interface ArmyTabularFunction extends _DerivedTable, ArmySQLFunction, Functions._TabularFunction {


    boolean hasAnonymousField();

    boolean hasWithOrdinality();


}
