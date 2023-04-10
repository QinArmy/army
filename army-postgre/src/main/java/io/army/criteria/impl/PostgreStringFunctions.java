package io.army.criteria.impl;

import io.army.criteria.Expression;
import io.army.mapping.IntegerType;
import io.army.mapping.MappingType;

/**
 * <p>
 * package class,hold postgre string functions and operators method.
 * </p>
 *
 * @see <a href="https://www.postgresql.org/docs/current/functions-string.html">String Functions and Operators</a>
 */
abstract class PostgreStringFunctions extends PostgreFuncSyntax {

    /**
     * package constructor
     */
    PostgreStringFunctions() {
    }


    /*-------------------below SQL String Functions and Operators-------------------*/

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  IntegerType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-SQL">bit_length ( text ) → integer</a>
     */
    public static Expression bitLength(Expression exp) {
        return FunctionUtils.oneArgFunc("BIT_LENGTH", exp, IntegerType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  IntegerType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-SQL">char_length ( text ) → integer</a>
     */
    public static Expression charLength(Expression exp) {
        return FunctionUtils.oneArgFunc("CHAR_LENGTH", exp, IntegerType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link  MappingType} of exp.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-SQL">lower ( text ) → text</a>
     */
    public static Expression lower(Expression exp) {
        return FunctionUtils.oneArgFunc("LOWER", exp, exp.typeMeta());
    }


}
