package io.army.criteria.impl;

import io.army.criteria.impl.inner._DerivedTable;
import io.army.criteria.impl.inner._SelfDescribed;

/**
 * <p>
 * package interface
 * </p>
 *
 * @since 1.0
 */
interface ArmyTabularFunction extends _DerivedTable, _SelfDescribed, Functions._TabularFunction {


    boolean hasAnonymousField();


}
