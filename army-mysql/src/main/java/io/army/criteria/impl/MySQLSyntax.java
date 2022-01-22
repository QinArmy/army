package io.army.criteria.impl;

import io.army.criteria.CriteriaException;

/**
 * <p>
 * This class is base class of below:
 *     <ul>
 *         <li>{@link MySQLs}</li>
 *         <li>{@link MySQLs80}</li>
 *     </ul>
 * </p>
 * package class
 */
abstract class MySQLSyntax extends StandardFunctions {

    /**
     * package constructor
     */
    MySQLSyntax() {
    }

    static CriteriaException indexListIsEmpty() {
        return new CriteriaException("index list must not empty.");
    }

}
