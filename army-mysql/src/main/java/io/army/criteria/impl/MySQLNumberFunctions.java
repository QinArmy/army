package io.army.criteria.impl;

import javax.annotation.Nullable;

/**
 * package class
 *
 * @since 0.6.0
 */
abstract class MySQLNumberFunctions extends Functions {

    MySQLNumberFunctions() {
    }





    /*-------------------below package method-------------------*/


    static void assertDistinct(@Nullable SQLs.ArgDistinct distinct) {
        assert distinct == null || distinct == SQLs.DISTINCT || distinct == MySQLs.DISTINCT;
    }


}
