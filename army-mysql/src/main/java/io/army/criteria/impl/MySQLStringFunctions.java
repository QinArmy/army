package io.army.criteria.impl;


import io.army.criteria.CriteriaException;
import io.army.criteria.Expression;
import io.army.criteria.SQLElement;
import io.army.criteria.mysql.MySQLCastType;
import io.army.criteria.mysql.MySQLCharset;
import io.army.criteria.mysql.MySQLLocale;
import io.army.lang.Nullable;
import io.army.mapping.IntegerType;
import io.army.mapping.MappingType;
import io.army.mapping.StringType;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.List;

/**
 * package class
 *
 * @since 1.0
 */
@SuppressWarnings("unused")
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
     * @param n parameter or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_char">CHAR(N,... [USING charset_name])</a>
     */
    public static Expression Char(final Expression n) {
        return FunctionUtils.oneOrMultiArgFunc("CHAR", n, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param list parameter or {@link Expression} list
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_char">CHAR(N,... [USING charset_name])</a>
     */
    public static Expression charFunc(final List<Expression> list) {
        return FunctionUtils.multiArgFunc("CHAR", list, StringType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param n           nullable parameter or non-empty {@link List} or {@link Expression}
     * @param using       {@link MySQLs#USING}
     * @param charsetName non-null, {@link MySQLCharset} or {@link MySQLs#charset(String)} ,output identifier
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_char">CHAR(N,... [USING charset_name])</a>
     */
    public static Expression charFunc(final Expression n, final MySQLs.WordUsing using, final SQLElement charsetName) {
        assert using == MySQLs.USING;
        final String funcName = "CHAR";
        if (!(charsetName instanceof MySQLCharset || charsetName instanceof SQLs.SQLIdentifierImpl)) {
            throw CriteriaUtils.funcArgError(funcName, charsetName);
        }

        final List<Object> argList = new ArrayList<>(3);
        argList.add(n);
        argList.add(MySQLs.USING);
        argList.add(charsetName);
        return FunctionUtils.complexArgFunc(funcName, argList, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param list        parameter or {@link Expression} list
     * @param using       {@link MySQLs#USING}
     * @param charsetName non-null, {@link MySQLCharset} or {@link MySQLs#charset(String)} ,output identifier
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_char">CHAR(N,... [USING charset_name])</a>
     */
    public static Expression charFunc(final List<Expression> list, final MySQLs.WordUsing using
            , final SQLElement charsetName) {//TODO 优化方法定义
        assert using == MySQLs.USING;
        final String name = "CHAR";
        if (!(charsetName instanceof MySQLCharset || charsetName instanceof SQLs.SQLIdentifierImpl)) {
            throw CriteriaUtils.funcArgError(name, charsetName);
        }
        final int size = list.size();
        if (size == 0) {
            throw CriteriaUtils.funcArgListIsEmpty(name);
        }
        final List<Object> argList = new ArrayList<>(size + 2);
        argList.addAll(list);
        argList.add(MySQLs.USING);
        argList.add(charsetName);
        return FunctionUtils.complexArgFunc(name, argList, StringType.INSTANCE);
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
        return FunctionUtils.twoOrMultiArgFunc("CONCAT_WS", separator, str, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param list nullable parameter or {@link Collection} or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_concat-ws">CONCAT_WS(separator,str1,str2,...)</a>
     */
    public static Expression concatWs(List<Expression> list) {
        return FunctionUtils.multiArgFunc("CONCAT_WS", list, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param n   non-null parameter or {@link Expression}
     * @param str non-null parameter or non-empty {@link List} or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_elt">ELT(N,str1,str2,str3,...)</a>
     */
    public static Expression elt(final Expression n, final Expression str) {
        return FunctionUtils.twoOrMultiArgFunc("ELT", n, str, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param n    non-null parameter or {@link Expression}
     * @param list nullable parameter or {@link Collection} or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_elt">ELT(N,str1,str2,str3,...)</a>
     */
    public static Expression elt(final Expression n, List<Expression> list) {
        return FunctionUtils.oneAndMultiArgFunc("ELT", n, list, StringType.INSTANCE);
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
        return exportSet(bits, on, off, null, null);
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
    public static Expression exportSet(final Expression bits, final Expression on, Expression off
            , final @Nullable Expression separator) {
        return exportSet(bits, on, off, separator, null);
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
    public static Expression exportSet(final Expression bits, final Expression on, Expression off
            , @Nullable final Expression separator, @Nullable final Expression numberOfBits) {
        return FunctionUtils.multiArgFunc("EXPORT_SET", StringType.INSTANCE, bits, on, off, separator, numberOfBits);
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
    public static Expression field(final Expression str, final Expression strList) {
        return FunctionUtils.twoOrMultiArgFunc("FIELD", str, strList, IntegerType.INSTANCE);
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
    public static Expression field(final Expression str, final List<Expression> strList) {
        return FunctionUtils.oneAndMultiArgFunc("FIELD", str, strList, IntegerType.INSTANCE);
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
    public static Expression fieldInSet(final Expression str, final Expression strList) {
        return FunctionUtils.twoOrMultiArgFunc("FIND_IN_SET", str, strList, IntegerType.INSTANCE);
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
    public static Expression fieldInSet(final Expression str, final List<Expression> strList) {
        return FunctionUtils.oneAndMultiArgFunc("FIND_IN_SET", str, strList, IntegerType.INSTANCE);
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
    public static Expression format(final Expression x, final Expression d, final @Nullable MySQLLocale locale) {
        return FunctionUtils.complexArgFunc("FORMAT", StringType.INSTANCE, x, d, locale);
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
        return FunctionUtils.multiArgFunc("INSERT", StringType.INSTANCE, str, pos, len, newStr);
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
     * @param strList {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_make-set">MAKE_SET(bits,str1,str2,...)</a>
     */
    public static Expression makeSet(final Expression bits, final Expression strList) {
        return FunctionUtils.twoOrMultiArgFunc("MAKE_SET", bits, strList, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param bits    non-null {@link Expression}
     * @param strList {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_make-set">MAKE_SET(bits,str1,str2,...)</a>
     */
    public static Expression makeSet(final Expression bits, final List<Expression> strList) {
        return FunctionUtils.oneAndMultiArgFunc("MAKE_SET", bits, strList, StringType.INSTANCE);
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
    public static Expression trim(final Expression remstr, SQLs.WordFrom from, final Expression str) {
        assert from == SQLs.FROM;
        return FunctionUtils.complexArgFunc("TRIM", StringType.INSTANCE, remstr, from, str);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param position non-null,should be below:
     *                 <ul>
     *                      <li>{@link MySQLs#BOTH}</li>
     *                      <li>{@link MySQLs#LEADING}</li>
     *                      <li>{@link MySQLs#TRAILING}</li>
     *                 </ul>
     * @param remstr   nullable parameter or {@link Expression}
     * @param str      nullable parameter or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_trim">TRIM([BOTH | LEADING | TRAILING] remstr FROM str), TRIM([remstr FROM] str),TRIM(remstr FROM str)</a>
     */
    public static Expression trim(final @Nullable MySQLs.TrimPosition position, final @Nullable Expression remstr
            , final @Nullable SQLs.WordFrom from, final Expression str) {//TODO 优化方法定义
        final String name = "TRIM";
        if (!(str instanceof ArmyExpression)) {
            throw CriteriaUtils.funcArgError(name, str);
        } else if (position != null && !(position instanceof MySQLSyntax.WordTrimPosition)) {
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
    public static Expression weightString(final Expression str, final SQLs.WordAs as
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
                , str, as, type, SQLSyntax.FuncWord.LEFT_PAREN, n, SQLSyntax.FuncWord.RIGHT_PAREN);
    }


    /*-------------------below package method-------------------*/


    static void assertDistinct(@Nullable SQLSyntax.ArgDistinct distinct) {
        assert distinct == null || distinct == SQLs.DISTINCT || distinct == MySQLs.DISTINCT;
    }


    /*-------------------below private method -------------------*/


}
