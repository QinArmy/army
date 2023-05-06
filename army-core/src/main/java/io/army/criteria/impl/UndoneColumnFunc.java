package io.army.criteria.impl;

import io.army.criteria.impl.inner._DerivedTable;
import io.army.criteria.standard.SQLFunction;

/**
 * <p>
 * package interface
 * </p>
 *
 * @since 1.0
 */
interface UndoneColumnFunc extends _DerivedTable, SQLFunction {


    boolean isNoNameField();


}
