package io.army.criteria.impl;

import io.army.criteria.impl.inner._Window;

/**
 * <p>
 * Package interface
 * * @since 0.6.0
 */
interface ArmyWindow extends _Window {

    /**
     * @return the name of named window
     * @throws IllegalStateException throw when this is anonymous window
     */
    String windowName();


    ArmyWindow endWindowClause();

}
