package io.army.dialect.mysql;

abstract class MySQL80DDLUtils extends MySQL57DDLUtils {



    static boolean isExpression(String defaultValue) {
        return defaultValue.startsWith("(")
                && defaultValue.endsWith(")");
    }

}
