package io.army.criteria.impl;

import io.army.criteria.CriteriaException;


abstract class MySQLUtils {

    private MySQLUtils() {
        throw new UnsupportedOperationException();
    }

    static CriteriaException indexListIsEmpty() {
        return new CriteriaException("index list must not empty.");
    }

}
