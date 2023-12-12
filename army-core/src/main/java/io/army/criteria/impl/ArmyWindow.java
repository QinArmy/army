package io.army.criteria.impl;

import io.army.criteria.impl.inner._Window;

/**
 * <p>
 * Package interface
 * * @since 1.0
 */
interface ArmyWindow extends _Window {

    /**
     * @return the name of named window
     * @throws IllegalStateException throw when this is anonymous window
     */
    String windowName();


    ArmyWindow endWindowClause();

}
