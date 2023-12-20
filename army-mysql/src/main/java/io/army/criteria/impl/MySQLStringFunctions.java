package io.army.criteria.impl;


import io.army.criteria.*;
import io.army.mapping.*;
import io.army.meta.TypeMeta;
import io.army.sqltype.DataType;
import io.army.sqltype.MySQLType;
import io.army.util._Collections;

import javax.annotation.Nullable;
import java.util.*;
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
        return _oneAndThreeStrVariadic("CONCAT_WS", StringType.INSTANCE, separator, str1, str2, str3, strVariadic);
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
        return _oneAndVariadicStringConsumer("CONCAT_WS", separator, 2, consumer, StringType.INSTANCE);
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
        return _oneAndVariadicStringConsumer("CONCAT_WS", separator, 2, consumer, StringType.INSTANCE);
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
        return _oneAndThreeStrVariadic("ELT", StringType.INSTANCE, n, str1, str2, str3, str3);
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
        return _oneAndVariadicStringConsumer("ELT", n, 3, consumer, StringType.INSTANCE);
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
        return _oneAndVariadicStringConsumer("ELT", n, 3, consumer, StringType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type:{@link StringType}
     *
     * @param bits non-null ,one of following :
     *             <ul>
     *                  <li>{@link Expression}</li>
     *                  <li>The literal that can be accepted by {@link io.army.mapping.mysql.MySqlBitType#beforeBind(DataType, MappingEnv, Object)}</li>
     *             </ul>
     * @param on   non-null, one of following :
     *             <ul>
     *                  <li>{@link Expression} instance</li>
     *                  <li>{@link String} literal</li>
     *             </ul>
     * @param off  non-null, one of following :
     *             <ul>
     *                  <li>{@link Expression} instance</li>
     *                  <li>{@link String} literal</li>
     *             </ul>
     * @return little-endian bit string expression
     * @throws CriteriaException throw when argument error
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_export-set">EXPORT_SET(bits,on,off[,separator[,number_of_bits]])</a>
     */
    public static SimpleExpression exportSet(final Object bits, final Object on, Object off) {
        return _exportSet(bits, on, off, null, null);
    }

    /**
     * <p>The {@link MappingType} of function return type:{@link StringType}
     *
     * @param bits      non-null ,one of following :
     *                  <ul>
     *                       <li>{@link Expression}</li>
     *                       <li>The literal that can be accepted by {@link io.army.mapping.mysql.MySqlBitType#beforeBind(DataType, MappingEnv, Object)}</li>
     *                  </ul>
     * @param on        non-null, one of following :
     *                  <ul>
     *                       <li>{@link Expression} instance</li>
     *                       <li>{@link String} literal</li>
     *                  </ul>
     * @param off       non-null, one of following :
     *                  <ul>
     *                       <li>{@link Expression} instance</li>
     *                       <li>{@link String} literal</li>
     *                  </ul>
     * @param separator non-null, one of following :
     *                  <ul>
     *                       <li>{@link Expression} instance</li>
     *                       <li>{@link String} literal</li>
     *                  </ul>
     * @return little-endian bit string expression
     * @throws CriteriaException throw when argument error
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_export-set">EXPORT_SET(bits,on,off[,separator[,number_of_bits]])</a>
     */
    public static SimpleExpression exportSet(Object bits, Object on, Object off, Object separator) {
        ContextStack.assertNonNull(separator);
        return _exportSet(bits, on, off, separator, null);
    }

    /**
     * <p>The {@link MappingType} of function return type:{@link StringType}
     *
     * @param bits         non-null ,one of following :
     *                     <ul>
     *                          <li>{@link Expression}</li>
     *                          <li>literal,for example : {@link Integer},{@link Long},{@link BitSet}</li>
     *                     </ul>
     * @param on           non-null, one of following :
     *                     <ul>
     *                          <li>{@link Expression} instance</li>
     *                          <li>{@link String} literal</li>
     *                     </ul>
     * @param off          non-null, one of following :
     *                     <ul>
     *                          <li>{@link Expression} instance</li>
     *                          <li>{@link String} literal</li>
     *                     </ul>
     * @param separator    non-null, one of following :
     *                     <ul>
     *                          <li>{@link Expression} instance</li>
     *                          <li>{@link String} literal</li>
     *                     </ul>
     * @param numberOfBits non-null, one of following :
     *                     <ul>
     *                          <li>{@link Expression} instance</li>
     *                          <li>{@link Integer} literal</li>
     *                     </ul>
     * @return little-endian bit string expression
     * @throws CriteriaException throw when argument error
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_export-set">EXPORT_SET(bits,on,off[,separator[,number_of_bits]])</a>
     */
    public static SimpleExpression exportSet(Object bits, Object on, Object off, Object separator, Object numberOfBits) {
        ContextStack.assertNonNull(separator);
        ContextStack.assertNonNull(numberOfBits);
        return _exportSet(bits, on, off, separator, numberOfBits);
    }


    /**
     * <p>The {@link MappingType} of function return type:{@link IntegerType}
     *
     * @param str         non-null, one of following :
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
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_field">FIELD(str,str1,str2,str3,...)</a>
     */
    public static SimpleExpression field(Object str, Object str1, Object str2, Object str3, Object... strVariadic) {
        FuncExpUtils.assertTextExp(str);
        return _oneAndThreeStrVariadic("FIELD", StringType.INSTANCE, str, str1, str2, str3, strVariadic);
    }

    /**
     * <p>FIELD function static method
     * <p>The {@link MappingType} of function return type:{@link IntegerType}
     *
     * @param str      non-null, one of following :
     *                 <ul>
     *                      <li>{@link Expression} instance</li>
     *                      <li>{@link String} literal</li>
     *                 </ul>
     * @param consumer each of clause is one of following :
     *                 <ul>
     *                      <li>{@link Expression} instance</li>
     *                      <li>{@link String} literal</li>
     *                 </ul>
     * @throws CriteriaException throw when argument error
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_field">FIELD(str,str1,str2,str3,...)</a>
     */
    public static SimpleExpression field(Object str, Consumer<Clause._VariadicSpaceClause> consumer) {
        FuncExpUtils.assertTextExp(str);
        return _oneAndVariadicStringConsumer("FIELD", str, 3, consumer, IntegerType.INSTANCE);
    }

    /**
     * <p>FIELD function dynamic method
     * <p>The {@link MappingType} of function return type:{@link IntegerType}
     *
     * @param str      non-null, one of following :
     *                 <ul>
     *                      <li>{@link Expression} instance</li>
     *                      <li>{@link String} literal</li>
     *                 </ul>
     * @param space    see {@link SQLs#SPACE}
     * @param consumer each of clause is one of following :
     *                 <ul>
     *                      <li>{@link Expression} instance</li>
     *                      <li>{@link String} literal</li>
     *                 </ul>
     * @throws CriteriaException throw when argument error
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_field">FIELD(str,str1,str2,str3,...)</a>
     */
    public static SimpleExpression field(Object str, SQLs.SymbolSpace space, Consumer<Clause._VariadicConsumer> consumer) {
        FuncExpUtils.assertTextExp(str);
        return _oneAndVariadicStringConsumer("FIELD", str, 3, consumer, IntegerType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type:{@link IntegerType}
     *
     * @param str     non-null, one of following :
     *                <ul>
     *                     <li>{@link Expression} instance</li>
     *                     <li>{@link String} literal</li>
     *                </ul>
     * @param strList non-null, one of following :
     *                <ul>
     *                     <li>{@link Expression} instance</li>
     *                     <li>{@link String} literal</li>
     *                </ul>
     * @throws CriteriaException throw when argument error
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_find-in-set">FIND_IN_SET(str,strlist)</a>
     */
    public static SimpleExpression fieldInSet(Object str, Object strList) {
        FuncExpUtils.assertTextExp(str);
        FuncExpUtils.assertTextExp(strList);
        return LiteralFunctions.twoArgFunc("FIND_IN_SET", str, strList, IntegerType.INSTANCE);
    }


    /**
     * <p>The {@link MappingType} of function return type:{@link StringType}
     *
     * @param x non-null, one of following :
     *          <ul>
     *               <li>{@link Expression} instance</li>
     *               <li>{@link Number} literal</li>
     *          </ul>
     * @param d non-null, one of following :
     *          <ul>
     *               <li>{@link Expression} instance</li>
     *               <li>{@link Integer} literal</li>
     *          </ul>
     * @throws CriteriaException throw when argument error
     * @see #format(Object, Object, Object)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_format">FORMAT(X,D[,locale])</a>
     */
    public static SimpleExpression format(final Object x, final Object d) {
        FuncExpUtils.assertNumberExp(x);
        FuncExpUtils.assertIntExp(d);
        return LiteralFunctions.twoArgFunc("FORMAT", x, d, StringType.INSTANCE);
    }


    /**
     * <p>The {@link MappingType} of function return type:{@link StringType}
     *
     * @param x      non-null, one of following :
     *               <ul>
     *                    <li>{@link Expression} instance</li>
     *                    <li>{@link Number} literal</li>
     *               </ul>
     * @param d      non-null, one of following :
     *               <ul>
     *                    <li>{@link Expression} instance</li>
     *                    <li>{@link Integer} literal</li>
     *               </ul>
     * @param locale non-null, one of following :
     *               <ul>
     *                    <li>{@link Expression} instance</li>
     *                    <li>{@link String} literal</li>
     *               </ul>
     *               see <a href="https://dev.mysql.com/doc/refman/8.0/en/locale-support.html">MySQL Server Locale Support</a>
     * @throws CriteriaException throw when argument error
     * @see #format(Object, Object)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_format">FORMAT(X,D[,locale])</a>
     */
    public static SimpleExpression format(Object x, Object d, Object locale) {
        FuncExpUtils.assertNumberExp(x);
        FuncExpUtils.assertIntExp(d);
        FuncExpUtils.assertTextExp(locale);
        return LiteralFunctions.threeArgFunc("FORMAT", x, d, locale, StringType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type:{@link VarBinaryType}
     *
     * @param str non-null, one of following :
     *            <ul>
     *                 <li>{@link Expression} instance</li>
     *                 <li>{@link String} literal</li>
     *                 <li>byte[] literal</li>
     *            </ul>
     * @see #toBase64(Object)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_from-base64">FROM_BASE64(str)</a>
     */
    public static SimpleExpression fromBase64(final Object str) {
        return LiteralFunctions.oneArgFunc("FROM_BASE64", str, VarBinaryType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type:{@link StringType}
     *
     * @param str non-null, one of following :
     *            <ul>
     *                 <li>{@link Expression} instance</li>
     *                 <li>{@link String} literal</li>
     *                 <li>byte[] literal</li>
     *            </ul>
     * @see #fromBase64(Object)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_to-base64">TO_BASE64(str)</a>
     */
    public static SimpleExpression toBase64(final Object str) {
        return LiteralFunctions.oneArgFunc("TO_BASE64", str, StringType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type:{@link StringType}
     *
     * @param strOrNum non-null, one of following :
     *                 <ul>
     *                      <li>{@link Expression} instance</li>
     *                      <li>{@link String} literal</li>
     *                      <li>{@link Number} literal</li>
     *                      <li>byte[] literal</li>
     *                 </ul>
     * @see #unhex(Object)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_hex">HEX(str), HEX(N)</a>
     */
    public static SimpleExpression hex(final Object strOrNum) {
        return LiteralFunctions.oneArgFunc("HEX", strOrNum, StringType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type:{@link VarBinaryType}
     *
     * @param str non-null, one of following :
     *            <ul>
     *                 <li>{@link Expression} instance</li>
     *                 <li>{@link String} literal</li>
     *                 <li>byte[] literal</li>
     *            </ul>
     * @see #hex(Object)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_unhex">UNHEX(str)</a>
     */
    public static SimpleExpression unhex(Object str) {
        return LiteralFunctions.oneArgFunc("UNHEX", str, VarBinaryType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type:{@link StringType}
     *
     * @param str    non-null, one of following :
     *               <ul>
     *                    <li>{@link Expression} instance</li>
     *                    <li>{@link String} literal</li>
     *               </ul>
     * @param pos    non-null, one of following :
     *               <ul>
     *                    <li>{@link Expression} instance</li>
     *                    <li>{@link Integer} literal</li>
     *               </ul>
     * @param len    non-null, one of following :
     *               <ul>
     *                    <li>{@link Expression} instance</li>
     *                    <li>{@link Integer} literal</li>
     *               </ul>
     * @param newStr non-null, one of following :
     *               <ul>
     *                    <li>{@link Expression} instance</li>
     *                    <li>{@link String} literal</li>
     *               </ul>
     * @throws CriteriaException throw when argument error
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_insert">INSERT(str,pos,len,newstr)</a>
     */
    public static SimpleExpression insert(Object str, Object pos, Object len, Object newStr) {
        FuncExpUtils.assertTextExp(str);
        FuncExpUtils.assertIntExp(pos);
        FuncExpUtils.assertIntExp(len);
        FuncExpUtils.assertTextExp(newStr);

        return LiteralFunctions.fourArgFunc("INSERT", str, pos, len, newStr, StringType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type:{@link IntegerType}
     *
     * @param str    non-null, one of following :
     *               <ul>
     *                    <li>{@link Expression} instance</li>
     *                    <li>{@link String} literal</li>
     *               </ul>
     * @param substr non-null, one of following :
     *               <ul>
     *                    <li>{@link Expression} instance</li>
     *                    <li>{@link String} literal</li>
     *               </ul>
     * @throws CriteriaException throw when argument error
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_instr">INSTR(str,substr)</a>
     */
    public static SimpleExpression instr(Object str, Object substr) {
        FuncExpUtils.assertTextExp(str);
        FuncExpUtils.assertTextExp(substr);

        return LiteralFunctions.twoArgFunc("INSTR", str, substr, IntegerType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type:{@link StringType}
     *
     * @param str non-null, one of following :
     *            <ul>
     *                 <li>{@link Expression} instance</li>
     *                 <li>{@link String} literal</li>
     *            </ul>
     * @throws CriteriaException throw when argument error
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_lower">LOWER(str)</a>
     */
    public static SimpleExpression lower(Object str) {
        FuncExpUtils.assertTextExp(str);
        return LiteralFunctions.oneArgFunc("LOWER", str, StringType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type:{@link StringType}
     *
     * @param str non-null, one of following :
     *            <ul>
     *                 <li>{@link Expression} instance</li>
     *                 <li>{@link String} literal</li>
     *            </ul>
     * @throws CriteriaException throw when argument error
     * @see #lower(Object)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_upper">UPPER(str)</a>
     */
    public static SimpleExpression upper(Object str) {
        FuncExpUtils.assertTextExp(str);
        return LiteralFunctions.oneArgFunc("UPPER", str, StringType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type:{@link StringType}
     *
     * @param str non-null, one of following :
     *            <ul>
     *                 <li>{@link Expression} instance</li>
     *                 <li>{@link String} literal</li>
     *            </ul>
     * @throws CriteriaException throw when argument error
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_left">LEFT(str,len)</a>
     */
    public static SimpleExpression left(Object str, Object len) {
        FuncExpUtils.assertTextExp(str);
        FuncExpUtils.assertIntExp(len);
        return LiteralFunctions.twoArgFunc("LEFT", str, len, StringType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type:{@link IntegerType}
     *
     * @param str non-null, one of following :
     *            <ul>
     *                 <li>{@link Expression} instance</li>
     *                 <li>{@link String} literal</li>
     *            </ul>
     * @throws CriteriaException throw when argument error
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_length">LENGTH(str)</a>
     */
    public static SimpleExpression length(Object str) {
        FuncExpUtils.assertTextExp(str);
        return LiteralFunctions.oneArgFunc("LENGTH", str, IntegerType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type:{@link StringType}
     *
     * @param fileName non-null, one of following :
     *                 <ul>
     *                      <li>{@link Expression} instance</li>
     *                      <li>{@link String} literal</li>
     *                 </ul>
     * @throws CriteriaException throw when argument error
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_load-file">LOAD_FILE(fileName)</a>
     */
    public static SimpleExpression loadFile(Object fileName) {
        FuncExpUtils.assertTextExp(fileName);
        return LiteralFunctions.oneArgFunc("LOAD_FILE", fileName, StringType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type:{@link IntegerType}
     *
     * @param substr non-null, one of following :
     *               <ul>
     *                    <li>{@link Expression} instance</li>
     *                    <li>{@link String} literal</li>
     *               </ul>
     * @param str    non-null, one of following :
     *               <ul>
     *                    <li>{@link Expression} instance</li>
     *                    <li>{@link String} literal</li>
     *               </ul>
     * @return int {@link Expression} ,based one .
     * @throws CriteriaException throw when argument error
     * @see #locate(Object, Object, Object)
     * @see #position(Object, SQLs.WordIn, Object)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_locate">LOCATE(substr,str)</a>
     */
    public static SimpleExpression locate(final Object substr, final Object str) {
        FuncExpUtils.assertTextExp(substr);
        FuncExpUtils.assertTextExp(str);
        return LiteralFunctions.twoArgFunc("LOCATE", substr, str, IntegerType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type:{@link IntegerType}
     *
     * @param substr non-null, one of following :
     *               <ul>
     *                    <li>{@link Expression} instance</li>
     *                    <li>{@link String} literal</li>
     *               </ul>
     * @param str    non-null, one of following :
     *               <ul>
     *                    <li>{@link Expression} instance</li>
     *                    <li>{@link String} literal</li>
     *               </ul>
     * @param pos    non-null, one of following :
     *               <ul>
     *                    <li>{@link Expression} instance</li>
     *                    <li>{@link Integer} literal</li>
     *               </ul>
     * @throws CriteriaException throw when argument error
     * @see #locate(Object, Object, Object)
     * @see #position(Object, SQLs.WordIn, Object)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_locate">LOCATE(substr,str,pos)</a>
     */
    public static SimpleExpression locate(Object substr, Object str, Object pos) {
        FuncExpUtils.assertTextExp(substr);
        FuncExpUtils.assertTextExp(str);
        FuncExpUtils.assertIntExp(pos);
        return LiteralFunctions.threeArgFunc("LOCATE", substr, str, pos, IntegerType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type:{@link StringType}
     *
     * @param str    non-null, one of following :
     *               <ul>
     *                    <li>{@link Expression} instance</li>
     *                    <li>{@link String} literal</li>
     *               </ul>
     * @param len    non-null, one of following :
     *               <ul>
     *                    <li>{@link Expression} instance</li>
     *                    <li>{@link Integer} literal</li>
     *               </ul>
     * @param padstr non-null, one of following :
     *               <ul>
     *                    <li>{@link Expression} instance</li>
     *                    <li>{@link String} literal</li>
     *               </ul>
     * @throws CriteriaException throw when argument error
     * @see #rpad(Object, Object, Object)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_lpad">LPAD(str,len,padstr)</a>
     */
    public static SimpleExpression lpad(final Object str, final Object len, final Object padstr) {
        FuncExpUtils.assertTextExp(str);
        FuncExpUtils.assertIntExp(len);
        FuncExpUtils.assertTextExp(padstr);
        return LiteralFunctions.threeArgFunc("LPAD", str, len, padstr, StringType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type:{@link StringType}
     *
     * @param str    non-null, one of following :
     *               <ul>
     *                    <li>{@link Expression} instance</li>
     *                    <li>{@link String} literal</li>
     *               </ul>
     * @param len    non-null, one of following :
     *               <ul>
     *                    <li>{@link Expression} instance</li>
     *                    <li>{@link Integer} literal</li>
     *               </ul>
     * @param padstr non-null, one of following :
     *               <ul>
     *                    <li>{@link Expression} instance</li>
     *                    <li>{@link String} literal</li>
     *               </ul>
     * @throws CriteriaException throw when argument error
     * @see #lpad(Object, Object, Object)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_rpad">RPAD(str,len,padstr)</a>
     */
    public static SimpleExpression rpad(final Object str, final Object len, final Object padstr) {
        FuncExpUtils.assertTextExp(str);
        FuncExpUtils.assertIntExp(len);
        FuncExpUtils.assertTextExp(padstr);
        return LiteralFunctions.threeArgFunc("RPAD", str, len, padstr, StringType.INSTANCE);
    }


    /**
     * <p>The {@link MappingType} of function return type:{@link StringType}
     *
     * @param str non-null, one of following :
     *            <ul>
     *                 <li>{@link Expression} instance</li>
     *                 <li>{@link String} literal</li>
     *            </ul>
     * @throws CriteriaException throw when argument error
     * @see #rtrim(Object)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_ltrim">LTRIM(str)</a>
     */
    public static SimpleExpression ltrim(final Object str) {
        FuncExpUtils.assertTextExp(str);
        return LiteralFunctions.oneArgFunc("LTRIM", str, StringType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type:{@link StringType}
     *
     * @param str non-null, one of following :
     *            <ul>
     *                 <li>{@link Expression} instance</li>
     *                 <li>{@link String} literal</li>
     *            </ul>
     * @throws CriteriaException throw when argument error
     * @see #ltrim(Object)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_rtrim">RTRIM(str)</a>
     */
    public static SimpleExpression rtrim(final Object str) {
        FuncExpUtils.assertTextExp(str);
        return LiteralFunctions.oneArgFunc("RTRIM", str, StringType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type:{@link StringType}
     *
     * @param bits        non-null, one of following :
     *                    <ul>
     *                         <li>{@link Expression} instance</li>
     *                         <li>{@link Integer} literal</li>
     *                         <li>{@link Long} literal</li>
     *                         <li>{@link BitSet} instance</li>
     *                    </ul>
     * @param str1        nullable, one of following :
     *                    <ul>
     *                         <li>{@link Expression} instance</li>
     *                         <li>{@link String} literal</li>
     *                    </ul>
     * @param str2        nullable, one of following :
     *                    <ul>
     *                         <li>{@link Expression} instance</li>
     *                         <li>{@link String} literal</li>
     *                    </ul>
     * @param strVariadic nullable, each of strVariadic is  one of following :
     *                    <ul>
     *                         <li>{@link Expression} instance</li>
     *                         <li>{@link String} literal</li>
     *                    </ul>
     * @throws CriteriaException throw when argument
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_make-set">MAKE_SET(bits,str1,str2,...)</a>
     */
    public static SimpleExpression makeSet(final Object bits, @Nullable Object str1, @Nullable Object str2, @Nullable Object... strVariadic) {
        if (str1 != null) {
            FuncExpUtils.assertTextExp(str1);
        }
        if (str2 != null) {
            FuncExpUtils.assertTextExp(str2);
        }


        final List<Object> argList;
        if (strVariadic == null) {
            argList = _Collections.arrayList(3);
        } else {
            argList = _Collections.arrayList(3 + strVariadic.length);
        }

        argList.add(bits);
        argList.add(str1);
        argList.add(str2);

        if (strVariadic != null) {
            FuncExpUtils.addAllTextExp(argList, "str", strVariadic);
        }

        return LiteralFunctions.multiArgFunc("MAKE_SET", argList, StringType.INSTANCE);
    }

    /**
     * <p>MAKE_SET function static method
     * <p>The {@link MappingType} of function return type:{@link StringType}
     *
     * @param bits     non-null, one of following :
     *                 <ul>
     *                      <li>{@link Expression} instance</li>
     *                      <li>{@link Integer} literal</li>
     *                      <li>{@link Long} literal</li>
     *                      <li>{@link BitSet} instance</li>
     *                 </ul>
     * @param consumer non-null,each item of clause is one of following :
     *                 <ul>
     *                      <li>{@link Expression} instance</li>
     *                      <li>{@link String} literal</li>
     *                 </ul>
     * @throws CriteriaException throw when argument
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_make-set">MAKE_SET(bits,str1,str2,...)</a>
     */
    public static SimpleExpression makeSet(final Object bits, Consumer<Clause._VariadicSpaceClause> consumer) {
        return _oneAndVariadicStringConsumer("MAKE_SET", bits, 2, consumer, StringType.INSTANCE);
    }

    /**
     * <p>MAKE_SET function dynamic method
     * <p>The {@link MappingType} of function return type:{@link StringType}
     *
     * @param bits     non-null, one of following :
     *                 <ul>
     *                      <li>{@link Expression} instance</li>
     *                      <li>{@link Integer} literal</li>
     *                      <li>{@link Long} literal</li>
     *                      <li>{@link BitSet} instance</li>
     *                 </ul>
     * @param space    see {@link SQLs#SPACE}
     * @param consumer non-null,each item of clause is one of following :
     *                 <ul>
     *                      <li>{@link Expression} instance</li>
     *                      <li>{@link String} literal</li>
     *                 </ul>
     * @throws CriteriaException throw when argument
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_make-set">MAKE_SET(bits,str1,str2,...)</a>
     */
    public static SimpleExpression makeSet(final Object bits, SQLs.SymbolSpace space, Consumer<Clause._VariadicConsumer> consumer) {
        return _oneAndVariadicStringConsumer("MAKE_SET", bits, 2, consumer, StringType.INSTANCE);
    }


    /**
     * <p>The {@link MappingType} of function return type:{@link StringType}
     *
     * @param str non-null, one of following :
     *            <ul>
     *                 <li>{@link Expression} instance</li>
     *                 <li>{@link String} literal</li>
     *            </ul>
     * @param pos non-null, one of following :
     *            <ul>
     *                 <li>{@link Expression} instance</li>
     *                 <li>{@link Integer} literal</li>
     *            </ul>
     * @throws CriteriaException throw when argument
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_substring">SUBSTRING(str,pos)</a>
     */
    public static SimpleExpression subString(final Object str, final Object pos) {
        FuncExpUtils.assertTextExp(str);
        FuncExpUtils.assertIntExp(pos);
        return LiteralFunctions.twoArgFunc("SUBSTRING", str, pos, StringType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type:{@link StringType}
     *
     * @param str non-null, one of following :
     *            <ul>
     *                 <li>{@link Expression} instance</li>
     *                 <li>{@link String} literal</li>
     *            </ul>
     * @param pos non-null, one of following :
     *            <ul>
     *                 <li>{@link Expression} instance</li>
     *                 <li>{@link Integer} literal</li>
     *            </ul>
     * @param len non-null, one of following :
     *            <ul>
     *                 <li>{@link Expression} instance</li>
     *                 <li>{@link Integer} literal</li>
     *            </ul>
     * @throws CriteriaException throw when argument
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_substring">SUBSTRING(str,pos,len)</a>
     */
    public static SimpleExpression subString(final Object str, final Object pos, final Object len) {
        FuncExpUtils.assertTextExp(str);
        FuncExpUtils.assertIntExp(pos);
        FuncExpUtils.assertIntExp(len);
        return LiteralFunctions.threeArgFunc("SUBSTRING", str, pos, len, StringType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type:{@link StringType}
     *
     * @param n one of following :
     *          <ul>
     *               <li>{@link Expression} instance</li>
     *               <li>{@link Integer} literal</li>
     *               <li>{@link Long} literal</li>
     *               <li>{@link java.math.BigInteger} instance</li>
     *          </ul>
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_oct">OCT(N)</a>
     */
    public static SimpleExpression oct(final Object n) {
        return LiteralFunctions.oneArgFunc("OCT", n, StringType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type:{@link IntegerType}
     *
     * @param str non-null, one of following :
     *            <ul>
     *                 <li>{@link Expression} instance</li>
     *                 <li>{@link String} literal</li>
     *            </ul>
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_ord">ORD(str)</a>
     */
    public static SimpleExpression ord(final Object str) {
        FuncExpUtils.assertTextExp(str);
        return LiteralFunctions.oneArgFunc("ORD", str, IntegerType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type:{@link IntegerType}
     *
     * @param substr non-null, one of following :
     *               <ul>
     *                    <li>{@link Expression} instance</li>
     *                    <li>{@link String} literal</li>
     *               </ul>
     * @param in     see {@link SQLs#IN}
     * @param str    non-null, one of following :
     *               <ul>
     *                    <li>{@link Expression} instance</li>
     *                    <li>{@link String} literal</li>
     *               </ul>
     * @return int {@link Expression} ,based one .
     * @throws CriteriaException throw when argument error
     * @see #locate(Object, Object)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_position">POSITION(substr IN str)</a>
     */
    public static SimpleExpression position(final Object substr, SQLs.WordIn in, final Object str) {
        FuncExpUtils.assertTextExp(substr);
        FuncExpUtils.assertTextExp(str);
        if (in != SQLs.IN) {
            throw CriteriaUtils.unknownWords(in);
        }
        return LiteralFunctions.compositeFunc("POSITION", Arrays.asList(substr, in, str), IntegerType.INSTANCE);
    }


    /**
     * <p>The {@link MappingType} of function return type:{@link StringType}
     *
     * @param str non-null, one of following :
     *            <ul>
     *                 <li>{@link Expression} instance</li>
     *                 <li>{@link String} literal</li>
     *            </ul>
     * @throws CriteriaException throw when argument error
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_quote">QUOTE(str)</a>
     */
    public static SimpleExpression quote(final Object str) {
        FuncExpUtils.assertTextExp(str);
        return LiteralFunctions.oneArgFunc("QUOTE", str, StringType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type:{@link StringType}
     *
     * @param str   non-null, one of following :
     *              <ul>
     *                   <li>{@link Expression} instance</li>
     *                   <li>{@link String} literal</li>
     *              </ul>
     * @param count non-null, one of following :
     *              <ul>
     *                   <li>{@link Expression} instance</li>
     *                   <li>{@link Integer} literal</li>
     *              </ul>
     * @throws CriteriaException throw when argument error
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_repeat">REPEAT(str,count)</a>
     */
    public static SimpleExpression repeat(final Object str, final Object count) {
        FuncExpUtils.assertTextExp(str);
        FuncExpUtils.assertIntExp(count);
        return LiteralFunctions.twoArgFunc("REPEAT", str, count, StringType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type:{@link StringType}
     *
     * @param str     non-null, one of following :
     *                <ul>
     *                     <li>{@link Expression} instance</li>
     *                     <li>{@link String} literal</li>
     *                </ul>
     * @param fromStr non-null, one of following :
     *                <ul>
     *                     <li>{@link Expression} instance</li>
     *                     <li>{@link String} literal</li>
     *                </ul>
     * @param toStr   non-null, one of following :
     *                <ul>
     *                     <li>{@link Expression} instance</li>
     *                     <li>{@link String} literal</li>
     *                </ul>
     * @throws CriteriaException throw when argument error
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_replace">REPLACE(str,from_str,to_str)</a>
     */
    public static SimpleExpression replace(final Object str, final Object fromStr, final Object toStr) {
        FuncExpUtils.assertTextExp(str);
        FuncExpUtils.assertTextExp(fromStr);
        FuncExpUtils.assertTextExp(toStr);
        return LiteralFunctions.threeArgFunc("REPLACE", str, fromStr, toStr, StringType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type:{@link StringType}
     *
     * @param str non-null, one of following :
     *            <ul>
     *                 <li>{@link Expression} instance</li>
     *                 <li>{@link String} literal</li>
     *            </ul>
     * @throws CriteriaException throw when argument error
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_reverse">REVERSE(str)</a>
     */
    public static SimpleExpression reverse(final Object str) {
        FuncExpUtils.assertTextExp(str);
        return LiteralFunctions.oneArgFunc("REVERSE", str, StringType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type:{@link StringType}
     *
     * @param str non-null, one of following :
     *            <ul>
     *                 <li>{@link Expression} instance</li>
     *                 <li>{@link String} literal</li>
     *            </ul>
     * @param len non-null, one of following :
     *            <ul>
     *                 <li>{@link Expression} instance</li>
     *                 <li>{@link Integer} literal</li>
     *            </ul>
     * @throws CriteriaException throw when argument error
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_right">RIGHT(str,len)</a>
     */
    public static SimpleExpression right(final Object str, final Object len) {
        FuncExpUtils.assertTextExp(str);
        FuncExpUtils.assertIntExp(len);
        return LiteralFunctions.twoArgFunc("RIGHT", str, len, StringType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type:{@link StringType}
     *
     * @param str non-null, one of following :
     *            <ul>
     *                 <li>{@link Expression} instance</li>
     *                 <li>{@link String} literal</li>
     *            </ul>
     * @throws CriteriaException throw when argument error
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_soundex">SOUNDEX(str)</a>
     */
    public static SimpleExpression soundex(final Object str) {
        FuncExpUtils.assertTextExp(str);
        return LiteralFunctions.oneArgFunc("SOUNDEX", str, StringType.INSTANCE);
    }


    /**
     * <p>The {@link MappingType} of function return type:{@link StringType}
     *
     * @param n non-null, one of following :
     *          <ul>
     *               <li>{@link Expression} instance</li>
     *               <li>{@link Integer} literal</li>
     *          </ul>
     * @throws CriteriaException throw when argument error
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_space">SPACE(n)</a>
     */
    public static SimpleExpression space(final Object n) {
        FuncExpUtils.assertIntExp(n);
        return LiteralFunctions.oneArgFunc("SPACE", n, StringType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type:{@link StringType}
     *
     * @param str   non-null, one of following :
     *              <ul>
     *                   <li>{@link Expression} instance</li>
     *                   <li>{@link String} literal</li>
     *              </ul>
     * @param delim non-null, one of following :
     *              <ul>
     *                   <li>{@link Expression} instance</li>
     *                   <li>{@link String} literal</li>
     *              </ul>
     * @param count non-null, one of following :
     *              <ul>
     *                   <li>{@link Expression} instance</li>
     *                   <li>{@link Integer} literal</li>
     *              </ul>
     * @throws CriteriaException throw when argument error
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_substring-index">SUBSTRING_INDEX(str,delim,count)</a>
     */
    public static SimpleExpression substringIndex(Object str, Object delim, Object count) {
        FuncExpUtils.assertTextExp(str);
        FuncExpUtils.assertTextExp(delim);
        FuncExpUtils.assertIntExp(count);
        return LiteralFunctions.threeArgFunc("SUBSTRING_INDEX", str, delim, count, StringType.INSTANCE);
    }


    /**
     * <p>The {@link MappingType} of function return type:{@link StringType}
     *
     * @param str non-null, one of following :
     *            <ul>
     *                 <li>{@link Expression} instance</li>
     *                 <li>{@link String} literal</li>
     *            </ul>
     * @throws CriteriaException throw when argument error
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_trim">TRIM(str)</a>
     */
    public static SimpleExpression trim(final Object str) {
        FuncExpUtils.assertTextExp(str);
        return LiteralFunctions.oneArgFunc("TRIM", str, StringType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type:{@link StringType}
     *
     * @param remstr non-null, one of following :
     *               <ul>
     *                    <li>{@link Expression} instance</li>
     *                    <li>{@link String} literal</li>
     *               </ul>
     * @param from   see {@link SQLs#FROM}
     * @param str    non-null, one of following :
     *               <ul>
     *                    <li>{@link Expression} instance</li>
     *                    <li>{@link String} literal</li>
     *               </ul>
     * @throws CriteriaException throw when argument error
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_trim">TRIM(remstr FROM str)</a>
     */
    public static SimpleExpression trim(Object remstr, SQLs.WordFrom from, Object str) {
        FuncExpUtils.assertTextExp(remstr);
        FuncExpUtils.assertTextExp(str);
        if (from != SQLs.FROM) {
            throw CriteriaUtils.unknownWords(from);
        }
        return LiteralFunctions.compositeFunc("TRIM", Arrays.asList(remstr, from, str), StringType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type:{@link StringType}
     *
     * @param position non-null,should be below:
     *                 <ul>
     *                      <li>{@link SQLs#BOTH}</li>
     *                      <li>{@link SQLs#LEADING}</li>
     *                      <li>{@link SQLs#TRAILING}</li>
     *                 </ul>
     * @param from     see {@link SQLs#FROM}
     * @param str      non-null, one of following :
     *                 <ul>
     *                      <li>{@link Expression} instance</li>
     *                      <li>{@link String} literal</li>
     *                 </ul>
     * @throws CriteriaException throw when argument error
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_trim">TRIM([BOTH | LEADING | TRAILING] remstr FROM str), TRIM([remstr FROM] str),TRIM(remstr FROM str)</a>
     */
    public static SimpleExpression trim(SQLs.TrimPosition position, SQLs.WordFrom from, Object str) {
        FuncExpUtils.assertTrimPosition(position);
        FuncExpUtils.assertWord(from, SQLs.FROM);
        FuncExpUtils.assertTextExp(str);

        return LiteralFunctions.compositeFunc("TRIM", Arrays.asList(position, from, str), StringType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type:{@link StringType}
     *
     * @param position non-null,should be below:
     *                 <ul>
     *                      <li>{@link SQLs#BOTH}</li>
     *                      <li>{@link SQLs#LEADING}</li>
     *                      <li>{@link SQLs#TRAILING}</li>
     *                 </ul>
     * @param remstr   non-null, one of following :
     *                 <ul>
     *                      <li>{@link Expression} instance</li>
     *                      <li>{@link String} literal</li>
     *                 </ul>
     * @param from     see {@link SQLs#FROM}
     * @param str      non-null, one of following :
     *                 <ul>
     *                      <li>{@link Expression} instance</li>
     *                      <li>{@link String} literal</li>
     *                 </ul>
     * @throws CriteriaException throw when argument error
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_trim">TRIM([BOTH | LEADING | TRAILING] remstr FROM str), TRIM([remstr FROM] str),TRIM(remstr FROM str)</a>
     */
    public static SimpleExpression trim(SQLs.TrimPosition position, Object remstr, SQLs.WordFrom from, Object str) {
        FuncExpUtils.assertTrimPosition(position);
        FuncExpUtils.assertTextExp(remstr);
        FuncExpUtils.assertWord(from, SQLs.FROM);
        FuncExpUtils.assertTextExp(str);

        return LiteralFunctions.compositeFunc("TRIM", Arrays.asList(position, remstr, from, str), StringType.INSTANCE);
    }


    /**
     * <p>The {@link MappingType} of function return type:{@link StringType}
     *
     * @param str non-null, one of following :
     *            <ul>
     *                 <li>{@link Expression} instance</li>
     *                 <li>{@link String} literal</li>
     *            </ul>
     * @throws CriteriaException throw when argument error
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_weight-string">WEIGHT_STRING(str)</a>
     */
    public static SimpleExpression weightString(Object str) {
        FuncExpUtils.assertTextExp(str);
        return LiteralFunctions.oneArgFunc("WEIGHT_STRING", str, StringType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type:{@link StringType}
     *
     * @param str  non-null, one of following :
     *             <ul>
     *                  <li>{@link Expression} instance</li>
     *                  <li>{@link String} literal</li>
     *             </ul>
     * @param as   see {@link SQLs#AS}
     * @param type non-null,one of following :
     *             <ul>
     *                  <li>{@link io.army.sqltype.MySQLType#CHAR}</li>
     *                  <li>{@link io.army.sqltype.MySQLType#BINARY}</li>
     *                  <li>{@link TypeDef}, see {@link TypeDefs#space(DataType, int)}</li>
     *             </ul>
     * @throws CriteriaException throw when argument error
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_weight-string">WEIGHT_STRING(str [AS {CHAR|BINARY}(N)]</a>
     */
    public static SimpleExpression weightString(Object str, SQLs.WordAs as, TypeDef type) {
        _checkWeightStringArgs(str, as, type);
        return LiteralFunctions.compositeFunc("WEIGHT_STRING", Arrays.asList(str, as, type), StringType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type:{@link StringType}
     *
     * @param str   non-null, one of following :
     *              <ul>
     *                   <li>{@link Expression} instance</li>
     *                   <li>{@link String} literal</li>
     *              </ul>
     * @param as    see {@link SQLs#AS}
     * @param type  non-null,one of following :
     *              <ul>
     *                   <li>{@link io.army.sqltype.MySQLType#CHAR}</li>
     *                   <li>{@link io.army.sqltype.MySQLType#BINARY}</li>
     *                   <li>{@link TypeDef}, see {@link TypeDefs#space(DataType, int)}</li>
     *              </ul>
     * @param flags non-null, one of following :
     *              <ul>
     *                   <li>{@link Expression} instance</li>
     *                   <li> literal</li>
     *              </ul>
     * @throws CriteriaException throw when argument error
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_weight-string">WEIGHT_STRING(str [AS {CHAR|BINARY}(N)]</a>
     */
    public static SimpleExpression weightString(Object str, SQLs.WordAs as, TypeDef type, Object flags) {
        _checkWeightStringArgs(str, as, type);
        return LiteralFunctions.compositeFunc("WEIGHT_STRING", Arrays.asList(str, as, type, flags), StringType.INSTANCE);
    }


    /*-------------------below private method -------------------*/


    /**
     * @param min min value of variadic
     * @see #concatWs(Object, Consumer)
     * @see #concatWs(Object, SQLs.SymbolSpace, Consumer)
     * @see #elt(Object, Consumer)
     * @see #elt(Object, SQLs.SymbolSpace, Consumer)
     */
    private static SimpleExpression _oneAndVariadicStringConsumer(final String name, Object one, final int min,
                                                                  Consumer<? super FuncExpUtils.VariadicClause> consumer,
                                                                  TypeMeta returnType) {
        final ArrayList<Object> arrayList = _Collections.arrayList(1 + min + 2);
        arrayList.add(one);

        final List<?> argList;
        argList = FuncExpUtils.variadicList(true, arrayList, String.class, consumer);
        if (argList.size() < (1 + min)) {
            throw ContextStack.clearStackAndCriteriaError(String.format("variadic argument count must great than or equal %s", min));
        }
        return LiteralFunctions.multiArgFunc(name, argList, returnType);
    }


    /**
     * @see #exportSet(Object, Object, Object)
     * @see #exportSet(Object, Object, Object, Object)
     * @see #exportSet(Object, Object, Object, Object, Object)
     */
    private static SimpleExpression _exportSet(Object bits, Object on, Object off,
                                               @Nullable Object separator, @Nullable Object numberOfBits) {
        FuncExpUtils.assertTextExp(on);
        FuncExpUtils.assertTextExp(off);

        final String name = "EXPORT_SET";

        final SimpleExpression func;
        if (separator == null && numberOfBits == null) {
            func = LiteralFunctions.threeArgFunc(name, bits, on, off, StringType.INSTANCE);
        } else if (numberOfBits != null) {
            FuncExpUtils.assertTextExp(separator);
            FuncExpUtils.assertIntExp(numberOfBits);
            func = LiteralFunctions.fiveArgFunc(name, bits, on, off, separator, numberOfBits, StringType.INSTANCE);
        } else {
            FuncExpUtils.assertTextExp(separator);
            func = LiteralFunctions.fourArgFunc(name, bits, on, off, separator, StringType.INSTANCE);
        }
        return func;
    }

    /**
     * @see #concatWs(Object, Object, Object, Object, Object...)
     * @see #elt(Object, Object, Object, Object, Object...)
     * @see #field(Object, Object, Object, Object, Object...)
     */
    private static SimpleExpression _oneAndThreeStrVariadic(final String name, TypeMeta returnType, Object one,
                                                            Object str1, Object str2, Object str3, Object... strVariadic) {
        FuncExpUtils.assertTextExp(str1);
        FuncExpUtils.assertTextExp(str2);
        FuncExpUtils.assertTextExp(str3);

        final List<Object> argList = _Collections.arrayList(4 + strVariadic.length);

        argList.add(one);
        argList.add(str1);
        argList.add(str2);
        argList.add(str3);

        FuncExpUtils.addAllTextExp(argList, "str", strVariadic);
        return LiteralFunctions.multiArgFunc(name, argList, StringType.INSTANCE);
    }


    /**
     * @see #weightString(Object, SQLs.WordAs, TypeDef)
     * @see #weightString(Object, SQLs.WordAs, TypeDef, Object)
     */
    private static void _checkWeightStringArgs(Object str, SQLs.WordAs as, TypeDef type) {
        FuncExpUtils.assertTextExp(str);
        FuncExpUtils.assertWord(as, SQLs.AS);

        final DataType dataType;
        if (type instanceof MySQLType) {
            dataType = (MySQLType) type;
        } else if (type instanceof TypeDefs.TypeDefLength) {
            dataType = ((TypeDefs) type).dataType;
        } else {
            throw CriteriaUtils.unknownWords(type);
        }

        if (!(dataType instanceof MySQLType)) {
            throw CriteriaUtils.unknownWords(type);
        }

        switch ((MySQLType) dataType) {
            case CHAR:
            case BINARY:
                break;
            default:
                throw CriteriaUtils.unknownWords(type);
        }

    }


}
