package io.army.criteria.impl;


import io.army.criteria.*;
import io.army.criteria.mysql.MySQLCastType;
import io.army.criteria.mysql.MySQLCharset;
import io.army.criteria.mysql.MySQLLocale;
import io.army.criteria.mysql.MySQLWords;
import io.army.lang.Nullable;
import io.army.mapping.*;
import io.army.meta.TypeMeta;

import java.util.*;
import java.util.function.Function;

/**
 * package class
 *
 * @since 1.0
 */
abstract class MySQLStringFunctions extends Functions {

    MySQLStringFunctions() {
        throw new UnsupportedOperationException();
    }



    /*-------------------below String Functions-------------------*/

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link IntegerType}
     * </p>
     *
     * @param str nullable parameter or {@link Expression}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_ascii">ASCII(str)</a>
     */
    public static Expression ascii(final Expression str) {
        return FunctionUtils.oneArgFunc("ASCII", str, IntegerType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param n nullable parameter or {@link Expression}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_bin">BIN(n)</a>
     */
    public static Expression bin(final Expression n) {
        return FunctionUtils.oneArgFunc("BIN", n, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link IntegerType}
     * </p>
     *
     * @param str nullable parameter or {@link Expression}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_bit-length">BIT_LENGTH(str)</a>
     */
    public static Expression binLength(final Expression str) {
        return FunctionUtils.oneArgFunc("BIT_LENGTH", str, IntegerType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param n nullable parameter or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_char">CHAR(N,... [USING charset_name])</a>
     */
    public static Expression charFunc(final Expression n) {
        return FunctionUtils.complexArgFunc("CHAR", Collections.singletonList(n), StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param n           nullable parameter or non-empty {@link List} or {@link Expression}
     * @param charsetName non-null, {@link io.army.criteria.mysql.MySQLCharset} or {@link String} ,output identifier
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_char">CHAR(N,... [USING charset_name])</a>
     */
    public static Expression charFunc(final Expression n, final Object charsetName) {
        final String funcName = "CHAR";

        final List<Object> argList = new ArrayList<>(3);
        argList.add(_funcParamList(StringType.INSTANCE, n));
        argList.add(FunctionUtils.FuncWord.USING);
        if (charsetName instanceof MySQLCharset) {
            argList.add(charsetName);
        } else if (charsetName instanceof String) {
            argList.add(FunctionUtils.sqlIdentifier((String) charsetName));// sql identifier
        } else {
            throw CriteriaUtils.funcArgError(funcName, charsetName);
        }
        return FunctionUtils.complexArgFunc(funcName, argList, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link IntegerType}
     * </p>
     *
     * @param str nullable parameter or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_char">CHAR_LENGTH(str)</a>
     */
    public static Expression charLength(final Expression str) {
        return FunctionUtils.oneArgFunc("CHAR_LENGTH", str, IntegerType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param str nullable parameter or {@link Collection} or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_concat">CONCAT(str1,str2,...)</a>
     */
    public static Expression concat(final Expression str) {
        return FunctionUtils.oneArgFunc("CONCAT", str, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param separator nullable parameter or {@link Expression}
     * @param str       nullable parameter or {@link Collection} or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_concat-ws">CONCAT_WS(separator,str1,str2,...)</a>
     */
    public static Expression concatWs(final Expression separator, final Expression str) {
        final String name = "CONCAT_WS";
        if (separator instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, separator);
        }
        final List<Object> argList = new ArrayList<>(3);
        argList.add(separator);
        argList.add(FunctionUtils.FuncWord.COMMA);
        argList.add(str);
        return FunctionUtils.complexArgFunc(name, argList, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param n       non-null parameter or {@link Expression}
     * @param strList non-null parameter or non-empty {@link List} or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_elt">ELT(N,str1,str2,str3,...)</a>
     */
    public static Expression elt(final Expression n, final Object strList) {
        return _singleAndListFunc("ELT", n, StringType.INSTANCE, strList, StringType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param bits non-null parameter or {@link Expression}
     * @param on   non-null parameter or {@link Expression}
     * @param off  non-null parameter or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see #exportSet(Expression, Expression, Expression, Expression)
     * @see #exportSet(Expression, Expression, Expression, Expression, Expression)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_export-set">EXPORT_SET(bits,on,off[,separator[,number_of_bits]])</a>
     */
    public static Expression exportSet(final Expression bits, final Expression on, Expression off) {
        return FunctionUtils.threeArgFunc("EXPORT_SET", bits, on, off, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param bits non-null parameter or {@link Expression}
     * @param on   non-null parameter or {@link Expression}
     * @param off  non-null parameter or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see #exportSet(Expression, Expression, Expression)
     * @see #exportSet(Expression, Expression, Expression, Expression, Expression)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_export-set">EXPORT_SET(bits,on,off[,separator[,number_of_bits]])</a>
     */
    public static Expression exportSet(final Expression bits, final Expression on, Expression off, final Expression separator) {
        final List<Object> argList = new ArrayList<>(7);

        argList.add(bits);
        argList.add(FunctionUtils.FuncWord.COMMA);
        argList.add(on);
        argList.add(FunctionUtils.FuncWord.COMMA);

        argList.add(off);
        argList.add(FunctionUtils.FuncWord.COMMA);
        argList.add(separator);
        return FunctionUtils.complexArgFunc("EXPORT_SET", argList, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param bits non-null parameter or {@link Expression}
     * @param on   non-null parameter or {@link Expression}
     * @param off  non-null parameter or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see #exportSet(Expression, Expression, Expression)
     * @see #exportSet(Expression, Expression, Expression, Expression)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_export-set">EXPORT_SET(bits,on,off[,separator[,number_of_bits]])</a>
     */
    public static Expression exportSet(final Expression bits, final Expression on, Expression off, final Expression separator
            , final Expression numberOfBits) {
        final List<Object> argList = new ArrayList<>(9);

        argList.add(bits);
        argList.add(FunctionUtils.FuncWord.COMMA);
        argList.add(on);
        argList.add(FunctionUtils.FuncWord.COMMA);

        argList.add(off);
        argList.add(FunctionUtils.FuncWord.COMMA);
        argList.add(separator);
        argList.add(FunctionUtils.FuncWord.COMMA);

        argList.add(numberOfBits);
        return FunctionUtils.complexArgFunc("EXPORT_SET", argList, StringType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:{@link IntegerType}
     * </p>
     *
     * @param str     nullable parameter or {@link Expression}
     * @param strList non-null literal or non-empty {@link List}  or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_field">FIELD(str,str1,str2,str3,...)</a>
     */
    public static Expression field(final Expression str, final Object strList) {
        return _singleAndListFunc("FIELD", str, StringType.INSTANCE, strList, IntegerType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:{@link IntegerType}
     * </p>
     *
     * @param str     nullable parameter or {@link Expression}
     * @param strList nullable parameter or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_find-in-set">FIND_IN_SET(str,strlist)</a>
     */
    public static Expression fieldInSet(final Expression str, final Object strList) {
        return _singleAndListFunc("FIND_IN_SET", str, StringType.INSTANCE, strList, IntegerType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param x non-null
     * @param d non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see #format(Expression, Expression, MySQLLocale)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_format">FORMAT(X,D[,locale])</a>
     */
    public static Expression format(final Expression x, final Expression d) {
        return FunctionUtils.twoArgFunc("FORMAT", x, d, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param x      non-null
     * @param d      non-null
     * @param locale non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see #format(Expression, Expression)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_format">FORMAT(X,D[,locale])</a>
     */
    public static Expression format(final Expression x, final Expression d, final MySQLLocale locale) {
        final List<Object> argList = new ArrayList<>(5);

        argList.add(x);
        argList.add(FunctionUtils.FuncWord.COMMA);
        argList.add(d);
        argList.add(FunctionUtils.FuncWord.COMMA);

        argList.add(locale);
        return FunctionUtils.complexArgFunc("FORMAT", argList, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param str nullable parameter or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see #toBase64(Expression)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_from-base64">FROM_BASE64(str)</a>
     */
    public static Expression fromBase64(final Expression str) {
        return FunctionUtils.oneArgFunc("FROM_BASE64", str, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param str nullable parameter or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see #fromBase64(Expression)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_to-base64">TO_BASE64(str)</a>
     */
    public static Expression toBase64(final Expression str) {
        return FunctionUtils.oneArgFunc("TO_BASE64", str, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param strOrNum nullable parameter or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see #unhex(Expression)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_hex">HEX(str), HEX(N)</a>
     */
    public static Expression hex(final Expression strOrNum) {
        return FunctionUtils.oneArgFunc("HEX", strOrNum, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param str nullable parameter or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see #hex(Expression)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_unhex">UNHEX(str)</a>
     */
    public static Expression unhex(final Expression str) {
        return FunctionUtils.oneArgFunc("UNHEX", str, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param str    nullable parameter or {@link Expression}
     * @param pos    nullable parameter or {@link Expression}
     * @param len    nullable parameter or {@link Expression}
     * @param newStr nullable parameter or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_insert">INSERT(str,pos,len,newstr)</a>
     */
    public static Expression insert(final Expression str, final Expression pos
            , final Expression len, final Expression newStr) {
        final List<Object> argList = new ArrayList<>(7);

        argList.add(str);
        argList.add(FunctionUtils.FuncWord.COMMA);
        argList.add(pos);
        argList.add(FunctionUtils.FuncWord.COMMA);

        argList.add(len);
        argList.add(FunctionUtils.FuncWord.COMMA);
        argList.add(newStr);
        return FunctionUtils.complexArgFunc("INSERT", argList, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link IntegerType}
     * </p>
     *
     * @param str    nullable parameter or {@link Expression}
     * @param substr nullable parameter or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_instr">INSTR(str,substr)</a>
     */
    public static Expression instr(final Expression str, final Expression substr) {
        return FunctionUtils.twoArgFunc("INSTR", str, substr, IntegerType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param str nullable parameter or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_lower">LOWER(str)</a>
     */
    public static Expression lower(final Expression str) {
        return FunctionUtils.oneArgFunc("LOWER", str, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param str nullable parameter or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see #lower(Expression)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_upper">UPPER(str)</a>
     */
    public static Expression upper(final Expression str) {
        return FunctionUtils.oneArgFunc("UPPER", str, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param str nullable parameter or {@link Expression}
     * @param len nullable parameter or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_left">LEFT(str,len)</a>
     */
    public static Expression left(final Expression str, final Expression len) {
        return FunctionUtils.twoArgFunc("LEFT", str, len, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link IntegerType}
     * </p>
     *
     * @param str nullable parameter or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_length">LENGTH(str)</a>
     */
    public static Expression length(final Expression str) {
        return FunctionUtils.oneArgFunc("LENGTH", str, IntegerType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param fileName non-null parameter or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_load-file">LOAD_FILE(fileName)</a>
     */
    public static Expression loadFile(final Expression fileName) {
        return FunctionUtils.oneArgFunc("LOAD_FILE", fileName, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link IntegerType}
     * </p>
     *
     * @param substr nullable parameter or {@link Expression}
     * @param str    nullable parameter or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see #locate(Expression, Expression, Expression)
     * @see #position(Expression, Expression)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_locate">LOCATE(substr,str)</a>
     */
    public static Expression locate(final Expression substr, final Expression str) {
        return FunctionUtils.twoArgFunc("LOCATE", substr, str, IntegerType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link IntegerType}
     * </p>
     *
     * @param substr nullable parameter or {@link Expression}
     * @param str    nullable parameter or {@link Expression}
     * @param pos    nullable parameter or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see #locate(Expression, Expression)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_locate">LOCATE(substr,str,pos)</a>
     */
    public static Expression locate(final Expression substr, final Expression str, final Expression pos) {
        return FunctionUtils.threeArgFunc("LOCATE", substr, str, pos, IntegerType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param str    nullable parameter or {@link Expression}
     * @param len    nullable parameter or {@link Expression}
     * @param padstr nullable parameter or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see #rpad(Expression, Expression, Expression)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_lpad">LPAD(str,len,padstr)</a>
     */
    public static Expression lpad(final Expression str, final Expression len, final Expression padstr) {
        return FunctionUtils.threeArgFunc("LPAD", str, len, padstr, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param str    nullable parameter or {@link Expression}
     * @param len    nullable parameter or {@link Expression}
     * @param padstr nullable parameter or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see #lpad(Expression, Expression, Expression)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_rpad">RPAD(str,len,padstr)</a>
     */
    public static Expression rpad(final Expression str, final Expression len, final Expression padstr) {
        return FunctionUtils.threeArgFunc("RPAD", str, len, padstr, StringType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param str nullable parameter or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see #rtrim(Expression)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_ltrim">LTRIM(str)</a>
     */
    public static Expression ltrim(final Expression str) {
        return FunctionUtils.oneArgFunc("LTRIM", str, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param str nullable parameter or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see #ltrim(Expression)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_rtrim">RTRIM(str)</a>
     */
    public static Expression rtrim(final Expression str) {
        return FunctionUtils.oneArgFunc("RTRIM", str, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param bits    non-null {@link Long} or {@link Integer} or {@link BitSet} or {@link Expression}
     * @param strList non-null {@link String} or {@link  List} or  {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_substring">MAKE_SET(bits,str1,str2,...)</a>
     */
    public static Expression makeSet(final Expression bits, final Object strList) {
        return _singleAndListFunc("MAKE_SET", bits, StringType.INSTANCE, strList, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param str nullable parameter or {@link Expression}
     * @param pos nullable parameter or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_substring">SUBSTRING(str,pos)</a>
     */
    public static Expression subString(final Expression str, final Expression pos) {
        return FunctionUtils.twoArgFunc("SUBSTRING", str, pos, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param str nullable parameter or {@link Expression}
     * @param pos nullable parameter or {@link Expression}
     * @param len nullable parameter or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_substring">SUBSTRING(str,pos,len)</a>
     */
    public static Expression subString(final Expression str, final Expression pos, final Expression len) {
        return FunctionUtils.threeArgFunc("SUBSTRING", str, pos, len, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param n nullable parameter or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_oct">OCT(N)</a>
     */
    public static Expression oct(final Expression n) {
        return FunctionUtils.oneArgFunc("OCT", n, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link IntegerType}
     * </p>
     *
     * @param str nullable parameter or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_ord">ORD(str)</a>
     */
    public static Expression ord(final Expression str) {
        return FunctionUtils.oneArgFunc("ORD", str, IntegerType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link IntegerType}
     * </p>
     *
     * @param substr nullable parameter or {@link Expression}
     * @param str    nullable parameter or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see #locate(Expression, Expression)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_position">POSITION(substr IN str)</a>
     */
    public static Expression position(final Expression substr, final Expression str) {
        return FunctionUtils.twoArgFunc("POSITION", substr, str, IntegerType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param str nullable parameter or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_quote">QUOTE(str)</a>
     */
    public static Expression quote(final Expression str) {
        return FunctionUtils.oneArgFunc("QUOTE", str, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param str   nullable parameter or {@link Expression}
     * @param count nullable parameter or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_repeat">REPEAT(str,count)</a>
     */
    public static Expression repeat(final Expression str, final Expression count) {
        return FunctionUtils.twoArgFunc("REPEAT", str, count, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param str     nullable parameter or {@link Expression}
     * @param fromStr nullable parameter or {@link Expression}
     * @param toStr   nullable parameter or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_replace">REPLACE(str,from_str,to_str)</a>
     */
    public static Expression replace(final Expression str, final Expression fromStr, final Expression toStr) {
        return FunctionUtils.threeArgFunc("REPLACE", str, fromStr, toStr, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param str nullable parameter or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_reverse">REVERSE(str)</a>
     */
    public static Expression reverse(final Expression str) {
        return FunctionUtils.oneArgFunc("REVERSE", str, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param str nullable parameter or {@link Expression}
     * @param len nullable parameter or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_right">RIGHT(str,len)</a>
     */
    public static Expression right(final Expression str, final Expression len) {
        return FunctionUtils.twoArgFunc("RIGHT", str, len, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param str nullable parameter or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_soundex">SOUNDEX(str)</a>
     */
    public static Expression soundex(final Expression str) {
        return FunctionUtils.oneArgFunc("SOUNDEX", str, StringType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param n nullable parameter or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_space">SPACE(n)</a>
     */
    public static Expression space(final Expression n) {
        return FunctionUtils.oneArgFunc("SPACE", n, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param str   nullable parameter or {@link Expression}
     * @param delim nullable parameter or {@link Expression}
     * @param count nullable parameter or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_substring-index">SUBSTRING_INDEX(str,delim,count)</a>
     */
    public static Expression substringIndex(final Expression str, final Expression delim, final Expression count) {
        return FunctionUtils.threeArgFunc("SUBSTRING_INDEX", str, delim, count, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param str nullable parameter or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_trim">TRIM(str)</a>
     */
    public static Expression trim(final Expression str) {
        return FunctionUtils.oneArgFunc("TRIM", str, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param remstr nullable parameter or {@link Expression}
     * @param str    nullable parameter or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_trim">TRIM(remstr FROM str)</a>
     */
    public static Expression trim(final Expression remstr, final Expression str) {
        return FunctionUtils.twoArgFunc("TRIM", remstr, str, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param position non-null,should be below:
     *                 <ul>
     *                      <li>{@link MySQLWords#BOTH}</li>
     *                      <li>{@link MySQLWords#LEADING}</li>
     *                      <li>{@link MySQLWords#TRAILING}</li>
     *                 </ul>
     * @param remstr   nullable parameter or {@link Expression}
     * @param str      nullable parameter or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_trim">TRIM([BOTH | LEADING | TRAILING] remstr FROM str), TRIM([remstr FROM] str),TRIM(remstr FROM str)</a>
     */
    public static Expression trim(final MySQLWords position, final Expression remstr, final Expression str) {
        final String funcName = "TRIM";
        switch (position) {
            case BOTH:
            case LEADING:
            case TRAILING:
                break;
            default:
                throw CriteriaUtils.funcArgError(funcName, position);
        }

        final List<Object> argList = new ArrayList<>(4);

        argList.add(position);
        argList.add(remstr);
        argList.add(FunctionUtils.FuncWord.FROM);
        argList.add(str);
        return FunctionUtils.complexArgFunc(funcName, argList, StringType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param str nullable parameter or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_weight-string">WEIGHT_STRING(str)</a>
     */
    public static Expression weightString(final Expression str) {
        return FunctionUtils.oneArgFunc("WEIGHT_STRING", str, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param str  nullable parameter or {@link Expression}
     * @param type non-null {@link  MySQLCastType#CHAR} or {@link  MySQLCastType#BINARY}
     * @param n    non-null parameter or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_weight-string">WEIGHT_STRING(str [AS {CHAR|BINARY}(N)]</a>
     */
    public static Expression weightString(final Expression str, final MySQLCastType type, final Expression n) {
        final String funcName = "WEIGHT_STRING";
        switch (type) {
            case CHAR:
            case BINARY:
                break;
            default:
                throw CriteriaUtils.funcArgError(funcName, type);
        }
        final List<Object> argList = new ArrayList<>(6);

        argList.add(str);
        argList.add(FunctionUtils.FuncWord.AS);
        argList.add(type);
        argList.add(FunctionUtils.FuncWord.LEFT_PAREN);

        argList.add(n);
        argList.add(FunctionUtils.FuncWord.RIGHT_PAREN);

        return FunctionUtils.complexArgFunc(funcName, argList, StringType.INSTANCE);
    }



    /*-------------------below private method -------------------*/


    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link  MappingType} of expr.
     * </p>
     *
     * @param name     MIN or MAX
     * @param distinct null or {@link  SQLsSyntax.WordAll#DISTINCT}
     * @param expr     non-null parameter or {@link Expression},but couldn't be {@link SQLs#nullWord()}.
     * @see #min(Expression)
     * @see #min(SQLsSyntax.WordAll, Expression)
     * @see #max(Expression)
     * @see #max(SQLsSyntax.WordAll, Expression)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_min">MIN([DISTINCT] expr) [over_clause]</a>
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_max">MAX([DISTINCT] expr) [over_clause]</a>
     */
    private static _AggregateOverSpec _distinctOneAggregateWindow(final String name, final @Nullable SQLsSyntax.WordAll distinct
            , final Expression expr, final TypeMeta returnType) {
        final _AggregateOverSpec func;
        if (distinct == null) {
            func = MySQLFunctionUtils.oneArgAggregateWindow(name, expr, returnType);
        } else if (distinct != SQLs.DISTINCT) {
            throw CriteriaUtils.funcArgError(name, distinct);
        } else if (expr instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, expr);
        } else {
            final List<Object> argList = new ArrayList<>(2);
            argList.add(distinct);
            argList.add(expr);
            func = MySQLFunctionUtils.complexAggregateWindow(name, argList, returnType);
        }
        return func;
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of expr.
     * </p>
     *
     * @param funcName   LAG or LEAD
     * @param expr       non-null parameter or {@link  Expression},but couldn't be {@link  SQLs#nullWord()}
     * @param n          nullable,probably is below:
     *                   <ul>
     *                       <li>null</li>
     *                       <li>{@link Long} type</li>
     *                       <li>{@link Integer} type</li>
     *                       <li>{@link SQLs#paramFrom(Object)},argument type is {@link Long} or {@link Integer}</li>
     *                       <li>{@link SQLs#literalFrom(Object) },argument type is {@link Long} or {@link Integer}</li>
     *                   </ul>
     * @param useDefault if n is non-nul and useDefault is true,output sql key word {@code DEFAULT}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_lag">LAG(expr [, N[, default]]) [null_treatment] over_clause</a>
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_lead">LEAD(expr [, N[, default]]) [null_treatment] over_clause</a>
     */
    private static MySQLFunctionSyntax._OverSpec _lagOrLead(final String funcName, final Object expr
            , final @Nullable Object n, final boolean useDefault) {

        assert funcName.equals("LAG") || funcName.equals("LEAD");

        final ArmyExpression expression;
        expression = SQLs._funcParam(expr);
        if (expr == SQLs.NULL) {
            throw CriteriaUtils.funcArgError(funcName, expr);
        }

        final ArmyExpression nExp;
        final TypeMeta nType;
        if (n == null) {
            nExp = null;
            nType = null;
        } else {
            nExp = SQLs._funcParam(n);
            nType = nExp.typeMeta();
        }

        final MySQLFunctionSyntax._OverSpec overSpec;
        if (nExp == null) {
            overSpec = MySQLFunctionUtils.oneArgWindowFunc(funcName, null, expression, expression.typeMeta());
        } else if (!(nExp instanceof ParamExpression.SingleParamExpression
                || nExp instanceof LiteralExpression.SingleLiteral)) {
            throw CriteriaUtils.funcArgError(funcName, n);
        } else if (nExp.isNullValue()) {
            throw CriteriaUtils.funcArgError(funcName, n);
        } else if (!(nType instanceof LongType || nType instanceof IntegerType)) {
            throw CriteriaUtils.funcArgError(funcName, n);
        } else if (useDefault) {
            final List<ArmyExpression> argList;
            argList = Arrays.asList(expression, nExp, (ArmyExpression) SQLs.defaultWord());
            overSpec = MySQLFunctionUtils.safeMultiArgWindowFunc(funcName, null, argList, expression.typeMeta());
        } else {
            final List<ArmyExpression> argList;
            argList = Arrays.asList(expression, nExp);
            overSpec = MySQLFunctionUtils.safeMultiArgWindowFunc(funcName, null, argList, expression.typeMeta());
        }
        return overSpec;
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of expr.
     * </p>
     *
     * @see #firstValue(Expression)
     * @see #lastValue(Expression)
     */
    private static MySQLFunctionSyntax._OverSpec _nonNullArgWindowFunc(final String funcName, final Object expr) {
        final ArmyExpression expression;
        expression = SQLs._funcParam(expr);
        if (expr == SQLs.NULL) {
            throw CriteriaUtils.funcArgError(funcName, expr);
        }
        return MySQLFunctionUtils.oneArgWindowFunc(funcName, null, expression, expression.typeMeta());
    }


    /**
     * @see #groupConcat(Object)
     * @see #groupConcat(SQLsSyntax.WordAll, Object)
     */
    private static Expression _groupConcat(@Nullable SQLsSyntax.WordAll distinct, @Nullable Object expressions
            , @Nullable Clause clause) {

        final String funcName = "GROUP_CONCAT";

        if (distinct != null && distinct != SQLs.DISTINCT) {
            throw CriteriaUtils.funcArgError(funcName, distinct);
        }
        if (clause != null && !(clause instanceof MySQLFunctionUtils.GroupConcatClause)) {
            throw CriteriaUtils.funcArgError(funcName, clause);
        }
        final Expression func;
        if (expressions instanceof List) {
            func = FunctionUtils.multiArgOptionFunc(funcName, distinct, (List<?>) expressions
                    , clause, StringType.INSTANCE);
        } else {
            func = FunctionUtils.oneArgOptionFunc(funcName, distinct, expressions, clause, StringType.INSTANCE);
        }
        return func;
    }

    /**
     * @see #aesEncrypt(Expression, Expression, List)
     * @see #aesDecrypt(Expression, Expression, List)
     */
    private static Expression _aesEncryptOrDecrypt(final String funcName, final Expression str, final Expression keyStr
            , final List<Expression> argExpList) {
        if (argExpList.size() > 4) {
            throw CriteriaUtils.funcArgError(funcName, argExpList);
        }
        final List<Object> argList = new ArrayList<>();

        argList.add(str);
        argList.add(FunctionUtils.FuncWord.COMMA);
        argList.add(keyStr);

        for (Expression argExp : argExpList) {
            argList.add(FunctionUtils.FuncWord.COMMA);
            argList.add(argExp);
        }
        return FunctionUtils.complexArgFunc(funcName, argList, StringType.INSTANCE);
    }


    /**
     * @see #geometryCollection(List)
     * @see #lineString(List)
     */
    private static Expression _geometryTypeFunc(final String name, final List<Expression> geometryList) {
        final Expression func;
        final int geometrySize = geometryList.size();
        switch (geometrySize) {
            case 0:
                func = FunctionUtils.noArgFunc(name, ByteArrayType.INSTANCE);
                break;
            case 1:
                func = FunctionUtils.oneArgFunc(name, geometryList.get(0), ByteArrayType.INSTANCE);
                break;
            default:
                func = FunctionUtils.complexArgFunc(name, _createSimpleMultiArgList(geometryList)
                        , ByteArrayType.INSTANCE);

        }
        return func;
    }

    /**
     * @see #lag(Expression, Expression, SQLs.WordDefault, Function, Function)
     * @see #lead(Expression, Expression, SQLs.WordDefault, Function, Function)
     */
    private static <R extends Expression, I extends Item> MySQLFunctionSyntax._OverSpec<R, I> leadOrLog(
            final String name, final @Nullable Expression expr
            , final @Nullable Expression n, final @Nullable SQLs.WordDefault defaultWord
            , Function<_AliasExpression<I>, R> endFunction, Function<Selection, I> asFunction) {
        if (expr == null) {
            throw ContextStack.nullPointer(ContextStack.peek());
        }
        assert defaultWord == null || defaultWord == SQLs.DEFAULT;
        final MySQLFunctionSyntax._OverSpec<R, I> spec;
        if (n == null) {
            spec = MySQLFunctionUtils.oneArgWindowFunc(name, expr, expr.typeMeta(), endFunction, asFunction);
        } else if (defaultWord == null) {
            spec = MySQLFunctionUtils.twoArgWindowFunc(name, expr, n, expr.typeMeta(), endFunction, asFunction);
        } else {
            spec = MySQLFunctionUtils.threeArgWindow(name, expr, n, defaultWord, expr.typeMeta(), endFunction, asFunction);
        }
        return spec;
    }


}
