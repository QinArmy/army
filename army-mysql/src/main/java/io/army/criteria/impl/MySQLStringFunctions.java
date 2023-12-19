package io.army.criteria.impl;


import io.army.criteria.Clause;
import io.army.criteria.CriteriaException;
import io.army.criteria.Expression;
import io.army.criteria.SimpleExpression;
import io.army.criteria.mysql.MySQLCastType;
import io.army.criteria.mysql.MySQLLocale;
import io.army.mapping.IntegerType;
import io.army.mapping.MappingType;
import io.army.mapping.StringType;
import io.army.util._Collections;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * package class
 *
 * @since 0.6.0
 */
@SuppressWarnings("unused")
abstract class MySQLStringFunctions extends MySQLNumberFunctions {

    MySQLStringFunctions() {
        throw new UnsupportedOperationException();
    }



    /*-------------------below String Functions-------------------*/

    /**
     * <p>The {@link MappingType} of function return type:{@link IntegerType}
     *
     * @param str non-null, one of following :
     *            <ul>
     *                 <li>{@link Expression} instance</li>
     *                 <li>literal</li>
     *            </ul>
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_ascii">ASCII(str)</a>
     */
    public static SimpleExpression ascii(final Object str) {
        return LiteralFunctions.oneArgFunc("ASCII", str, IntegerType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type:{@link StringType}
     *
     * @param n non-null, one of following :
     *          <ul>
     *               <li>{@link Expression} instance</li>
     *               <li>literal</li>
     *          </ul>
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_bin">BIN(n)</a>
     */
    public static SimpleExpression bin(final Object n) {
        return LiteralFunctions.oneArgFunc("BIN", n, StringType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type:{@link IntegerType}
     *
     * @param str non-null, one of following :
     *            <ul>
     *                 <li>{@link Expression} instance</li>
     *                 <li>literal</li>
     *            </ul>
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_bit-length">BIT_LENGTH(str)</a>
     */
    public static SimpleExpression binLength(final Object str) {
        return LiteralFunctions.oneArgFunc("BIT_LENGTH", str, IntegerType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type:{@link StringType}
     *
     * @param n non-null, one of following :
     *          <ul>
     *               <li>{@link Expression} instance</li>
     *               <li>literal</li>
     *          </ul>
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_char">CHAR(N,... [USING charset_name])</a>
     */
    public static SimpleExpression charFunc(final Object n) {
        return LiteralFunctions.oneArgFunc("CHAR", n, StringType.INSTANCE);
    }

    /**
     * <p>CHAR function static method.
     * <p>The {@link MappingType} of function return type:{@link StringType}
     *
     * @param consumer each of clause is  one of following :
     *                 <ul>
     *                      <li>{@link Expression} instance</li>
     *                      <li>literal</li>
     *                 </ul>
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_char">CHAR(N,... [USING charset_name])</a>
     */
    public static SimpleExpression charFunc(final Consumer<Clause._VariadicSpaceClause> consumer) {
        return MySQLFunctionUtils.charFunc(consumer, null);
    }

    /**
     * <p>CHAR function static method with charsetName
     * <p>The {@link MappingType} of function return type:{@link StringType}
     *
     * @param consumer each of clause is  one of following :
     *                 <ul>
     *                      <li>{@link Expression} instance</li>
     *                      <li>literal</li>
     *                 </ul>
     * @param using    see {@link SQLs#USING}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_char">CHAR(N,... [USING charset_name])</a>
     */
    public static SimpleExpression charFunc(final Consumer<Clause._VariadicSpaceClause> consumer, SQLs.WordUsing using, String charsetName) {
        Objects.requireNonNull(charsetName);
        return MySQLFunctionUtils.charFunc(consumer, charsetName);
    }

    /**
     * <p>CHAR function dynamic method.
     * <p>The {@link MappingType} of function return type:{@link StringType}
     *
     * @param space    see {@link SQLs#SPACE}
     * @param consumer each of clause is  one of following :
     *                 <ul>
     *                      <li>{@link Expression} instance</li>
     *                      <li>literal</li>
     *                 </ul>
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_char">CHAR(N,... [USING charset_name])</a>
     */
    public static SimpleExpression charFunc(SQLs.SymbolSpace space, final Consumer<Clause._VariadicConsumer> consumer) {
        return MySQLFunctionUtils.charFunc(consumer, null);
    }

    /**
     * <p>CHAR function dynamic method  with charsetName
     * <p>The {@link MappingType} of function return type:{@link StringType}
     *
     * @param space    see {@link SQLs#SPACE}
     * @param consumer each of clause is  one of following :
     *                 <ul>
     *                      <li>{@link Expression} instance</li>
     *                      <li>literal</li>
     *                 </ul>
     * @param using    see {@link SQLs#USING}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_char">CHAR(N,... [USING charset_name])</a>
     */
    public static SimpleExpression charFunc(SQLs.SymbolSpace space, Consumer<Clause._VariadicConsumer> consumer, SQLs.WordUsing using, String charsetName) {
        Objects.requireNonNull(charsetName);
        return MySQLFunctionUtils.charFunc(consumer, charsetName);
    }


    /**
     * <p>The {@link MappingType} of function return type:{@link IntegerType}
     *
     * @param str non-null, one of following :
     *            <ul>
     *                 <li>{@link Expression} instance</li>
     *                 <li>literal</li>
     *            </ul>
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_char">CHAR_LENGTH(str)</a>
     */
    public static SimpleExpression charLength(final Object str) {
        return LiteralFunctions.oneArgFunc("CHAR_LENGTH", str, IntegerType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type:{@link StringType}
     *
     * @param str1 non-null, one of following :
     *             <ul>
     *                  <li>{@link Expression} instance</li>
     *                  <li>{@link String} literal</li>
     *             </ul>
     * @param str2 non-null, one of following :
     *             <ul>
     *                  <li>{@link Expression} instance</li>
     *                  <li>{@link String} literal</li>
     *             </ul>
     * @throws CriteriaException throw when argument error
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_concat">CONCAT(str1,str2,...)</a>
     */
    public static SimpleExpression concat(final Object str1, Object str2) {
        FuncExpUtils.assertTextExp(str1);
        FuncExpUtils.assertTextExp(str2);
        return LiteralFunctions.twoArgFunc("CONCAT", str1, str2, StringType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type:{@link StringType}
     *
     * @param str1        non-null, one of following :
     *                    <ul>
     *                         <li>{@link Expression} instance</li>
     *                         <li>{@link String} literal</li>
     *                    </ul>
     * @param str2        non-null, one of following :
     *                    <ul>
     *                         <li>{@link Expression} instance</li>
     *                         <li>{@link String} literal</li>
     *                    </ul>
     * @param str3        non-null, one of following :
     *                    <ul>
     *                         <li>{@link Expression} instance</li>
     *                         <li>{@link String} literal</li>
     *                    </ul>
     * @param strVariadic each of strVariadic is one of following :
     *                    <ul>
     *                         <li>{@link Expression} instance</li>
     *                         <li>{@link String} literal</li>
     *                    </ul>
     * @throws CriteriaException throw when argument error
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_concat">CONCAT(str1,str2,...)</a>
     */
    public static SimpleExpression concat(final Object str1, Object str2, Object str3, Object... strVariadic) {
        FuncExpUtils.assertTextExp(str1);
        FuncExpUtils.assertTextExp(str2);
        FuncExpUtils.assertTextExp(str3);

        final List<Object> argList = _Collections.arrayList(3 + strVariadic.length);

        argList.add(str1);
        argList.add(str2);
        argList.add(str3);

        FuncExpUtils.addAllTextExp(argList, "str", strVariadic);
        return LiteralFunctions.multiArgFunc("CONCAT", argList, StringType.INSTANCE);
    }

    /**
     * <p>CONCAT function dynamic method.
     * <p>The {@link MappingType} of function return type:{@link StringType}
     *
     * @param consumer each of clause is  one of following :
     *                 <ul>
     *                      <li>{@link Expression} instance</li>
     *                      <li>{@link String} literal</li>
     *                 </ul>
     * @throws CriteriaException throw when argument error
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_concat">CONCAT(str1,str2,...)</a>
     */
    public static SimpleExpression concat(Consumer<Clause._VariadicSpaceClause> consumer) {
        final List<?> argList;
        argList = FuncExpUtils.variadicList(true, String.class, consumer);
        if (argList.size() < 2) {
            throw ContextStack.clearStackAndCriteriaError("argument count must great than 1");
        }
        return LiteralFunctions.multiArgFunc("CONCAT", argList, StringType.INSTANCE);
    }

    /**
     * <p>CONCAT function dynamic method.
     * <p>The {@link MappingType} of function return type:{@link StringType}
     *
     * @param space    see {@link SQLs#SPACE}
     * @param consumer each of clause is  one of following :
     *                 <ul>
     *                      <li>{@link Expression} instance</li>
     *                      <li>{@link String} literal</li>
     *                 </ul>
     * @throws CriteriaException throw when argument error
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_concat">CONCAT(str1,str2,...)</a>
     */
    public static SimpleExpression concat(SQLs.SymbolSpace space, Consumer<Clause._VariadicConsumer> consumer) {
        final List<?> argList;
        argList = FuncExpUtils.variadicList(true, String.class, consumer);
        if (argList.size() < 2) {
            throw ContextStack.clearStackAndCriteriaError("argument count must great than 1");
        }
        return LiteralFunctions.multiArgFunc("CONCAT", argList, StringType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type:{@link StringType}
     *
     * @param separator non-null, one of following :
     *                  <ul>
     *                       <li>{@link Expression} instance</li>
     *                       <li>{@link String} literal</li>
     *                  </ul>
     * @param str1      non-null, one of following :
     *                  <ul>
     *                       <li>{@link Expression} instance</li>
     *                       <li>{@link String} literal</li>
     *                  </ul>
     * @param str2      non-null, one of following :
     *                  <ul>
     *                       <li>{@link Expression} instance</li>
     *                       <li>{@link String} literal</li>
     *                  </ul>
     * @throws CriteriaException throw when argument error
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_concat-ws">CONCAT_WS(separator,str1,str2,...)</a>
     */
    public static SimpleExpression concatWs(Object separator, Object str1, Object str2) {
        return LiteralFunctions.threeArgFunc("CONCAT_WS", separator, str1, str2, StringType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type:{@link StringType}
     *
     * @param separator   non-null, one of following :
     *                    <ul>
     *                         <li>{@link Expression} instance</li>
     *                         <li>{@link String} literal</li>
     *                    </ul>
     * @param str1        non-null, one of following :
     *                    <ul>
     *                         <li>{@link Expression} instance</li>
     *                         <li>{@link String} literal</li>
     *                    </ul>
     * @param str2        non-null, one of following :
     *                    <ul>
     *                         <li>{@link Expression} instance</li>
     *                         <li>{@link String} literal</li>
     *                    </ul>
     * @param str3        non-null, one of following :
     *                    <ul>
     *                         <li>{@link Expression} instance</li>
     *                         <li>{@link String} literal</li>
     *                    </ul>
     * @param strVariadic each of strVariadic is one of following :
     *                    <ul>
     *                         <li>{@link Expression} instance</li>
     *                         <li>{@link String} literal</li>
     *                    </ul>
     * @throws CriteriaException throw when argument error
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_concat-ws">CONCAT_WS(separator,str1,str2,...)</a>
     */
    public static SimpleExpression concatWs(Object separator, Object str1, Object str2, Object str3, Object... strVariadic) {
        FuncExpUtils.assertTextExp(separator);
        FuncExpUtils.assertTextExp(str1);
        FuncExpUtils.assertTextExp(str2);
        FuncExpUtils.assertTextExp(str3);

        final List<Object> argList = _Collections.arrayList(4 + strVariadic.length);

        argList.add(separator);
        argList.add(str1);
        argList.add(str2);
        argList.add(str3);

        FuncExpUtils.addAllTextExp(argList, "str", strVariadic);
        return LiteralFunctions.multiArgFunc("CONCAT_WS", argList, StringType.INSTANCE);
    }


    /**
     * <p>CONCAT_WS function dynamic method.
     * <p>The {@link MappingType} of function return type:{@link StringType}
     *
     * @param separator non-null, one of following :
     *                  <ul>
     *                       <li>{@link Expression} instance</li>
     *                       <li>{@link String} literal</li>
     *                  </ul>
     * @param consumer  each of clause is  one of following :
     *                  <ul>
     *                       <li>{@link Expression} instance</li>
     *                       <li>{@link String} literal</li>
     *                  </ul>
     * @throws CriteriaException throw when argument error
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_concat-ws">CONCAT_WS(separator,str1,str2,...)</a>
     */
    public static SimpleExpression concatWs(Object separator, Consumer<Clause._VariadicSpaceClause> consumer) {
        FuncExpUtils.assertTextExp(separator);
        return _oneAndVariadicString("CONCAT_WS", separator, 2, consumer);
    }

    /**
     * <p>CONCAT_WS function dynamic method.
     * <p>The {@link MappingType} of function return type:{@link StringType}
     *
     * @param separator non-null, one of following :
     *                  <ul>
     *                       <li>{@link Expression} instance</li>
     *                       <li>{@link String} literal</li>
     *                  </ul>
     * @param space     see {@link SQLs#SPACE}
     * @param consumer  each of clause is  one of following :
     *                  <ul>
     *                       <li>{@link Expression} instance</li>
     *                       <li>{@link String} literal</li>
     *                  </ul>
     * @throws CriteriaException throw when argument error
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_concat-ws">CONCAT_WS(separator,str1,str2,...)</a>
     */
    public static SimpleExpression concatWs(Object separator, SQLs.SymbolSpace space, Consumer<Clause._VariadicConsumer> consumer) {
        FuncExpUtils.assertTextExp(separator);
        return _oneAndVariadicString("CONCAT_WS", separator, 2, consumer);
    }


    /**
     * <p>The {@link MappingType} of function return type:{@link StringType}
     *
     * @param n           non-null, one of following :
     *                    <ul>
     *                         <li>{@link Expression} instance</li>
     *                         <li>{@link Integer} literal</li>
     *                    </ul>
     * @param str1        non-null, one of following :
     *                    <ul>
     *                         <li>{@link Expression} instance</li>
     *                         <li>{@link String} literal</li>
     *                    </ul>
     * @param str2        non-null, one of following :
     *                    <ul>
     *                         <li>{@link Expression} instance</li>
     *                         <li>{@link String} literal</li>
     *                    </ul>
     * @param str3        non-null, one of following :
     *                    <ul>
     *                         <li>{@link Expression} instance</li>
     *                         <li>{@link String} literal</li>
     *                    </ul>
     * @param strVariadic each of strVariadic is one of following :
     *                    <ul>
     *                         <li>{@link Expression} instance</li>
     *                         <li>{@link String} literal</li>
     *                    </ul>
     * @throws CriteriaException throw when argument error
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_elt">ELT(N,str1,str2,str3,...)</a>
     */
    public static SimpleExpression elt(Object n, Object str1, Object str2, Object str3, Object... strVariadic) {
        FuncExpUtils.assertIntExp(n);
        FuncExpUtils.assertTextExp(str1);
        FuncExpUtils.assertTextExp(str2);
        FuncExpUtils.assertTextExp(str3);

        final List<Object> argList = _Collections.arrayList(4 + strVariadic.length);

        argList.add(n);
        argList.add(str1);
        argList.add(str2);
        argList.add(str3);

        FuncExpUtils.addAllTextExp(argList, "str", strVariadic);
        return LiteralFunctions.multiArgFunc("ELT", argList, StringType.INSTANCE);
    }

    /**
     * <p>ELT function dynamic method.
     * <p>The {@link MappingType} of function return type:{@link StringType}
     *
     * @param n        non-null, one of following :
     *                 <ul>
     *                      <li>{@link Expression} instance</li>
     *                      <li>{@link Integer} literal</li>
     *                 </ul>
     * @param consumer each of clause is  one of following :
     *                 <ul>
     *                      <li>{@link Expression} instance</li>
     *                      <li>{@link String} literal</li>
     *                 </ul>
     * @throws CriteriaException throw when argument error
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_elt">ELT(N,str1,str2,str3,...)</a>
     */
    public static SimpleExpression elt(Object n, Consumer<Clause._VariadicSpaceClause> consumer) {
        FuncExpUtils.assertIntExp(n);
        return _oneAndVariadicString("ELT", n, 3, consumer);
    }

    /**
     * <p>ELT function dynamic method.
     * <p>The {@link MappingType} of function return type:{@link StringType}
     *
     * @param n        non-null, one of following :
     *                 <ul>
     *                      <li>{@link Expression} instance</li>
     *                      <li>{@link Integer} literal</li>
     *                 </ul>
     * @param space    see {@link SQLs#SPACE}
     * @param consumer each of clause is  one of following :
     *                 <ul>
     *                      <li>{@link Expression} instance</li>
     *                      <li>{@link String} literal</li>
     *                 </ul>
     * @throws CriteriaException throw when argument error
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_elt">ELT(N,str1,str2,str3,...)</a>
     */
    public static SimpleExpression elt(Object n, SQLs.SymbolSpace space, Consumer<Clause._VariadicConsumer> consumer) {
        FuncExpUtils.assertIntExp(n);
        return _oneAndVariadicString("ELT", n, 3, consumer);
    }

    /**
     * <p>The {@link MappingType} of function return type:{@link StringType}
     *
     * @param bits non-null parameter or {@link Expression}
     * @param on   non-null parameter or {@link Expression}
     * @param off  non-null parameter or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see #exportSet(Expression, Expression, Expression, Expression)
     * @see #exportSet(Expression, Expression, Expression, Expression, Expression)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_export-set">EXPORT_SET(bits,on,off[,separator[,number_of_bits]])</a>
     */
    public static SimpleExpression exportSet(final Expression bits, final Expression on, Expression off) {
        return exportSet(bits, on, off, null, null);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * *
     *
     * @param bits non-null parameter or {@link Expression}
     * @param on   non-null parameter or {@link Expression}
     * @param off  non-null parameter or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see #exportSet(Expression, Expression, Expression)
     * @see #exportSet(Expression, Expression, Expression, Expression, Expression)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_export-set">EXPORT_SET(bits,on,off[,separator[,number_of_bits]])</a>
     */
    public static SimpleExpression exportSet(final Expression bits, final Expression on, Expression off
            , final @Nullable Expression separator) {
        return exportSet(bits, on, off, separator, null);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * *
     *
     * @param bits non-null parameter or {@link Expression}
     * @param on   non-null parameter or {@link Expression}
     * @param off  non-null parameter or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see #exportSet(Expression, Expression, Expression)
     * @see #exportSet(Expression, Expression, Expression, Expression)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_export-set">EXPORT_SET(bits,on,off[,separator[,number_of_bits]])</a>
     */
    public static SimpleExpression exportSet(final Expression bits, final Expression on, Expression off
            , @Nullable final Expression separator, @Nullable final Expression numberOfBits) {
        return FunctionUtils.multiArgFunc("EXPORT_SET", StringType.INSTANCE, bits, on, off, separator, numberOfBits);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:{@link IntegerType}
     * *
     *
     * @param str     nullable parameter or {@link Expression}
     * @param strList non-null literal or non-empty {@link List}  or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_field">FIELD(str,str1,str2,str3,...)</a>
     */
    public static SimpleExpression field(final Expression str, final Expression strList) {
        return FunctionUtils.twoOrMultiArgFunc("FIELD", str, strList, IntegerType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link IntegerType}
     * *
     *
     * @param str     nullable parameter or {@link Expression}
     * @param strList non-null literal or non-empty {@link List}  or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_field">FIELD(str,str1,str2,str3,...)</a>
     */
    public static SimpleExpression field(final Expression str, final List<Expression> strList) {
        return FunctionUtils.oneAndMultiArgFunc("FIELD", str, strList, IntegerType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link IntegerType}
     * *
     *
     * @param str     nullable parameter or {@link Expression}
     * @param strList nullable parameter or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_find-in-set">FIND_IN_SET(str,strlist)</a>
     */
    public static SimpleExpression fieldInSet(final Expression str, final Expression strList) {
        return FunctionUtils.twoOrMultiArgFunc("FIND_IN_SET", str, strList, IntegerType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:{@link IntegerType}
     * *
     *
     * @param str     nullable parameter or {@link Expression}
     * @param strList nullable parameter or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_find-in-set">FIND_IN_SET(str,strlist)</a>
     */
    public static SimpleExpression fieldInSet(final Expression str, final List<Expression> strList) {
        return FunctionUtils.oneAndMultiArgFunc("FIND_IN_SET", str, strList, IntegerType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * *
     *
     * @param x non-null
     * @param d non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see #format(Expression, Expression, MySQLLocale)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_format">FORMAT(X,D[,locale])</a>
     */
    public static SimpleExpression format(final Expression x, final Expression d) {
        return FunctionUtils.twoArgFunc("FORMAT", x, d, StringType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * *
     *
     * @param x      non-null
     * @param d      non-null
     * @param locale non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see #format(Expression, Expression)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_format">FORMAT(X,D[,locale])</a>
     */
    public static SimpleExpression format(final Expression x, final Expression d, final @Nullable MySQLLocale locale) {
        return FunctionUtils.complexArgFunc("FORMAT", StringType.INSTANCE, x, d, locale);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * *
     *
     * @param str nullable parameter or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see #toBase64(Expression)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_from-base64">FROM_BASE64(str)</a>
     */
    public static SimpleExpression fromBase64(final Expression str) {
        return FunctionUtils.oneArgFunc("FROM_BASE64", str, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * *
     *
     * @param str nullable parameter or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see #fromBase64(Expression)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_to-base64">TO_BASE64(str)</a>
     */
    public static SimpleExpression toBase64(final Expression str) {
        return FunctionUtils.oneArgFunc("TO_BASE64", str, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * *
     *
     * @param strOrNum nullable parameter or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see #unhex(Expression)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_hex">HEX(str), HEX(N)</a>
     */
    public static SimpleExpression hex(final Expression strOrNum) {
        return FunctionUtils.oneArgFunc("HEX", strOrNum, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * *
     *
     * @param str nullable parameter or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see #hex(Expression)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_unhex">UNHEX(str)</a>
     */
    public static SimpleExpression unhex(final Expression str) {
        return FunctionUtils.oneArgFunc("UNHEX", str, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * *
     *
     * @param str    nullable parameter or {@link Expression}
     * @param pos    nullable parameter or {@link Expression}
     * @param len    nullable parameter or {@link Expression}
     * @param newStr nullable parameter or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_insert">INSERT(str,pos,len,newstr)</a>
     */
    public static SimpleExpression insert(final Expression str, final Expression pos
            , final Expression len, final Expression newStr) {
        return FunctionUtils.multiArgFunc("INSERT", StringType.INSTANCE, str, pos, len, newStr);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link IntegerType}
     * *
     *
     * @param str    nullable parameter or {@link Expression}
     * @param substr nullable parameter or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_instr">INSTR(str,substr)</a>
     */
    public static SimpleExpression instr(final Expression str, final Expression substr) {
        return FunctionUtils.twoArgFunc("INSTR", str, substr, IntegerType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * *
     *
     * @param str nullable parameter or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_lower">LOWER(str)</a>
     */
    public static SimpleExpression lower(final Expression str) {
        return FunctionUtils.oneArgFunc("LOWER", str, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * *
     *
     * @param str nullable parameter or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see #lower(Expression)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_upper">UPPER(str)</a>
     */
    public static SimpleExpression upper(final Expression str) {
        return FunctionUtils.oneArgFunc("UPPER", str, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * *
     *
     * @param str nullable parameter or {@link Expression}
     * @param len nullable parameter or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_left">LEFT(str,len)</a>
     */
    public static SimpleExpression left(final Expression str, final Expression len) {
        return FunctionUtils.twoArgFunc("LEFT", str, len, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link IntegerType}
     * *
     *
     * @param str nullable parameter or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_length">LENGTH(str)</a>
     */
    public static SimpleExpression length(final Expression str) {
        return FunctionUtils.oneArgFunc("LENGTH", str, IntegerType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * *
     *
     * @param fileName non-null parameter or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_load-file">LOAD_FILE(fileName)</a>
     */
    public static SimpleExpression loadFile(final Expression fileName) {
        return FunctionUtils.oneArgFunc("LOAD_FILE", fileName, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link IntegerType}
     * *
     *
     * @param substr nullable parameter or {@link Expression}
     * @param str    nullable parameter or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see #locate(Expression, Expression, Expression)
     * @see #position(Expression, Expression)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_locate">LOCATE(substr,str)</a>
     */
    public static SimpleExpression locate(final Expression substr, final Expression str) {
        return FunctionUtils.twoArgFunc("LOCATE", substr, str, IntegerType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link IntegerType}
     * *
     *
     * @param substr nullable parameter or {@link Expression}
     * @param str    nullable parameter or {@link Expression}
     * @param pos    nullable parameter or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see #locate(Expression, Expression)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_locate">LOCATE(substr,str,pos)</a>
     */
    public static SimpleExpression locate(final Expression substr, final Expression str, final Expression pos) {
        return FunctionUtils.threeArgFunc("LOCATE", substr, str, pos, IntegerType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * *
     *
     * @param str    nullable parameter or {@link Expression}
     * @param len    nullable parameter or {@link Expression}
     * @param padstr nullable parameter or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see #rpad(Expression, Expression, Expression)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_lpad">LPAD(str,len,padstr)</a>
     */
    public static SimpleExpression lpad(final Expression str, final Expression len, final Expression padstr) {
        return FunctionUtils.threeArgFunc("LPAD", str, len, padstr, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * *
     *
     * @param str    nullable parameter or {@link Expression}
     * @param len    nullable parameter or {@link Expression}
     * @param padstr nullable parameter or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see #lpad(Expression, Expression, Expression)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_rpad">RPAD(str,len,padstr)</a>
     */
    public static SimpleExpression rpad(final Expression str, final Expression len, final Expression padstr) {
        return FunctionUtils.threeArgFunc("RPAD", str, len, padstr, StringType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * *
     *
     * @param str nullable parameter or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see #rtrim(Expression)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_ltrim">LTRIM(str)</a>
     */
    public static SimpleExpression ltrim(final Expression str) {
        return FunctionUtils.oneArgFunc("LTRIM", str, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * *
     *
     * @param str nullable parameter or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see #ltrim(Expression)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_rtrim">RTRIM(str)</a>
     */
    public static SimpleExpression rtrim(final Expression str) {
        return FunctionUtils.oneArgFunc("RTRIM", str, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * *
     *
     * @param bits    non-null {@link Long} or {@link Integer} or {@link BitSet} or {@link Expression}
     * @param strList {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_make-set">MAKE_SET(bits,str1,str2,...)</a>
     */
    public static SimpleExpression makeSet(final Expression bits, final Expression strList) {
        return FunctionUtils.twoOrMultiArgFunc("MAKE_SET", bits, strList, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * *
     *
     * @param bits    non-null {@link Expression}
     * @param strList {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_make-set">MAKE_SET(bits,str1,str2,...)</a>
     */
    public static SimpleExpression makeSet(final Expression bits, final List<Expression> strList) {
        return FunctionUtils.oneAndMultiArgFunc("MAKE_SET", bits, strList, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * *
     *
     * @param str nullable parameter or {@link Expression}
     * @param pos nullable parameter or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_substring">SUBSTRING(str,pos)</a>
     */
    public static SimpleExpression subString(final Expression str, final Expression pos) {
        return FunctionUtils.twoArgFunc("SUBSTRING", str, pos, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * *
     *
     * @param str nullable parameter or {@link Expression}
     * @param pos nullable parameter or {@link Expression}
     * @param len nullable parameter or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_substring">SUBSTRING(str,pos,len)</a>
     */
    public static SimpleExpression subString(final Expression str, final Expression pos, final Expression len) {
        return FunctionUtils.threeArgFunc("SUBSTRING", str, pos, len, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * *
     *
     * @param n nullable parameter or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_oct">OCT(N)</a>
     */
    public static SimpleExpression oct(final Expression n) {
        return FunctionUtils.oneArgFunc("OCT", n, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link IntegerType}
     * *
     *
     * @param str nullable parameter or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_ord">ORD(str)</a>
     */
    public static SimpleExpression ord(final Expression str) {
        return FunctionUtils.oneArgFunc("ORD", str, IntegerType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link IntegerType}
     * *
     *
     * @param substr nullable parameter or {@link Expression}
     * @param str    nullable parameter or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see #locate(Expression, Expression)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_position">POSITION(substr IN str)</a>
     */
    public static SimpleExpression position(final Expression substr, final Expression str) {
        return FunctionUtils.twoArgFunc("POSITION", substr, str, IntegerType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * *
     *
     * @param str nullable parameter or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_quote">QUOTE(str)</a>
     */
    public static SimpleExpression quote(final Expression str) {
        return FunctionUtils.oneArgFunc("QUOTE", str, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * *
     *
     * @param str   nullable parameter or {@link Expression}
     * @param count nullable parameter or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_repeat">REPEAT(str,count)</a>
     */
    public static SimpleExpression repeat(final Expression str, final Expression count) {
        return FunctionUtils.twoArgFunc("REPEAT", str, count, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * *
     *
     * @param str     nullable parameter or {@link Expression}
     * @param fromStr nullable parameter or {@link Expression}
     * @param toStr   nullable parameter or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_replace">REPLACE(str,from_str,to_str)</a>
     */
    public static SimpleExpression replace(final Expression str, final Expression fromStr, final Expression toStr) {
        return FunctionUtils.threeArgFunc("REPLACE", str, fromStr, toStr, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * *
     *
     * @param str nullable parameter or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_reverse">REVERSE(str)</a>
     */
    public static SimpleExpression reverse(final Expression str) {
        return FunctionUtils.oneArgFunc("REVERSE", str, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * *
     *
     * @param str nullable parameter or {@link Expression}
     * @param len nullable parameter or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_right">RIGHT(str,len)</a>
     */
    public static SimpleExpression right(final Expression str, final Expression len) {
        return FunctionUtils.twoArgFunc("RIGHT", str, len, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * *
     *
     * @param str nullable parameter or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_soundex">SOUNDEX(str)</a>
     */
    public static SimpleExpression soundex(final Expression str) {
        return FunctionUtils.oneArgFunc("SOUNDEX", str, StringType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * *
     *
     * @param n nullable parameter or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_space">SPACE(n)</a>
     */
    public static SimpleExpression space(final Expression n) {
        return FunctionUtils.oneArgFunc("SPACE", n, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * *
     *
     * @param str   nullable parameter or {@link Expression}
     * @param delim nullable parameter or {@link Expression}
     * @param count nullable parameter or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_substring-index">SUBSTRING_INDEX(str,delim,count)</a>
     */
    public static SimpleExpression substringIndex(final Expression str, final Expression delim, final Expression count) {
        return FunctionUtils.threeArgFunc("SUBSTRING_INDEX", str, delim, count, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * *
     *
     * @param str nullable parameter or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_trim">TRIM(str)</a>
     */
    public static SimpleExpression trim(final Expression str) {
        return FunctionUtils.oneArgFunc("TRIM", str, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * *
     *
     * @param remstr nullable parameter or {@link Expression}
     * @param str    nullable parameter or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_trim">TRIM(remstr FROM str)</a>
     */
    public static SimpleExpression trim(final Expression remstr, SQLs.WordFrom from, final Expression str) {
        assert from == SQLs.FROM;
        return FunctionUtils.complexArgFunc("TRIM", StringType.INSTANCE, remstr, from, str);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * *
     *
     * @param position non-null,should be below:
     *                 <ul>
     *                      <li>{@link SQLs#BOTH}</li>
     *                      <li>{@link SQLs#LEADING}</li>
     *                      <li>{@link SQLs#TRAILING}</li>
     *                 </ul>
     * @param remstr   nullable parameter or {@link Expression}
     * @param str      nullable parameter or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_trim">TRIM([BOTH | LEADING | TRAILING] remstr FROM str), TRIM([remstr FROM] str),TRIM(remstr FROM str)</a>
     */
    public static SimpleExpression trim(final @Nullable SQLs.TrimPosition position, final @Nullable Expression remstr
            , final @Nullable SQLs.WordFrom from, final Expression str) {//TODO 优化方法定义
        final String name = "TRIM";
        if (!(str instanceof ArmyExpression)) {
            throw CriteriaUtils.funcArgError(name, str);
        } else if (position != null && !(position instanceof SqlWords.WordTrimPosition)) {
            throw CriteriaUtils.funcArgError(name, position);
        } else if (remstr != null && from != SQLs.FROM) {
            throw ContextStack.criteriaError(ContextStack.peek(), "remstr and from syntax error");
        } else if (position != null && from != SQLs.FROM) {
            throw ContextStack.criteriaError(ContextStack.peek(), "position and from syntax error");
        }
        return FunctionUtils.complexArgFunc(name, StringType.INSTANCE, position, remstr, from, str);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * *
     *
     * @param str nullable parameter or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_weight-string">WEIGHT_STRING(str)</a>
     */
    public static SimpleExpression weightString(final Expression str) {
        return FunctionUtils.oneArgFunc("WEIGHT_STRING", str, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * *
     *
     * @param str  nullable parameter or {@link Expression}
     * @param type non-null {@link  MySQLCastType#CHAR} or {@link  MySQLCastType#BINARY}
     * @param n    non-null parameter or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_weight-string">WEIGHT_STRING(str [AS {CHAR|BINARY}(N)]</a>
     */
    public static SimpleExpression weightString(final Expression str, final SQLs.WordAs as
            , final MySQLCastType type, final Expression n) {
        assert as == SQLs.AS;
        final String name = "WEIGHT_STRING";
        switch (type) {
            case CHAR:
            case BINARY:
                break;
            default:
                throw CriteriaUtils.funcArgError(name, type);
        }
        if (!(str instanceof ArmyExpression)) {
            throw CriteriaUtils.funcArgError(name, str);
        } else if (!(n instanceof ArmyExpression)) {
            throw CriteriaUtils.funcArgError(name, n);
        }
        return FunctionUtils.complexArgFunc(name, StringType.INSTANCE
                , str, as, type, Functions.FuncWord.LEFT_PAREN, n, Functions.FuncWord.RIGHT_PAREN);
    }


    /*-------------------below private method -------------------*/


    /**
     * @param min min value of variadic
     * @see #concatWs(Object, Consumer)
     * @see #concatWs(Object, SQLs.SymbolSpace, Consumer)
     * @see #elt(Object, Consumer)
     * @see #elt(Object, SQLs.SymbolSpace, Consumer)
     */
    private static SimpleExpression _oneAndVariadicString(final String name, Object one, final int min,
                                                          Consumer<? super FuncExpUtils.VariadicClause> consumer) {
        final ArrayList<Object> arrayList = _Collections.arrayList(1 + min + 2);
        arrayList.add(one);

        final List<?> argList;
        argList = FuncExpUtils.variadicList(true, arrayList, String.class, consumer);
        if (argList.size() < (1 + min)) {
            throw ContextStack.clearStackAndCriteriaError(String.format("variadic argument count must great than or equal %s", min));
        }
        return LiteralFunctions.multiArgFunc(name, argList, StringType.INSTANCE);
    }


}
