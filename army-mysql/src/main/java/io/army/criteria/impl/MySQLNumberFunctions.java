package io.army.criteria.impl;

import io.army.lang.Nullable;

/**
 * package class
 *
 * @since 1.0
 */
abstract class MySQLNumberFunctions extends Functions {

    MySQLNumberFunctions() {
    }





    /*-------------------below package method-------------------*/


    static void assertDistinct(@Nullable SqlSyntax.ArgDistinct distinct) {
        assert distinct == null || distinct == SQLs.DISTINCT || distinct == MySQLs.DISTINCT;
    }


}
