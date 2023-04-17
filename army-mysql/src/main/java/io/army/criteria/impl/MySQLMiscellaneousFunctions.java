package io.army.criteria.impl;


import io.army.criteria.*;
import io.army.mapping.*;
import io.army.meta.FieldMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * package class
 *
 * @since 1.0
 */
@SuppressWarnings("unused")
abstract class MySQLMiscellaneousFunctions extends MySQLSpatialFunctions {

    MySQLMiscellaneousFunctions() {
    }

    /*-------------------below XML Functions-------------------*/


    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param xmlFrag   non-null
     * @param xpathExpr non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/xml-functions.html#function_extractvalue">ExtractValue(xml_frag, xpath_expr)</a>
     */
    public static SimpleExpression extractValue(final Expression xmlFrag, final Expression xpathExpr) {
        return FunctionUtils.twoArgFunc("ExtractValue", xmlFrag, xpathExpr, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param xmlTarget non-null
     * @param xpathExpr non-null
     * @param newXml    non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/xml-functions.html#function_updatexml">UpdateXML(xml_target, xpath_expr, new_xml)</a>
     */
    public static SimpleExpression updateXml(final Expression xmlTarget, final Expression xpathExpr, final Expression newXml) {
        return FunctionUtils.threeArgFunc("UpdateXML", xmlTarget, xpathExpr, newXml, StringType.INSTANCE);
    }



    /*-------------------below Locking Functions-------------------*/

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link IntegerType}
     * </p>
     *
     * @param str non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/locking-functions.html#function_get-lock">GET_LOCK(str,timeout)</a>
     */
    public static SimpleExpression getLock(final Expression str, final Expression timeout) {
        return FunctionUtils.twoArgFunc("GET_LOCK", str, timeout, IntegerType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link BooleanType}, sql function return 0 or 1 or null.
     * </p>
     *
     * @param str non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/locking-functions.html#function_is-free-lock">IS_FREE_LOCK(str)</a>
     */
    public static SimpleExpression isFreeLock(final Expression str) {
        return FunctionUtils.oneArgFunc("IS_FREE_LOCK", str, BooleanType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link LongType}
     * </p>
     *
     * @param str non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/locking-functions.html#function_is-used-lock">IS_USED_LOCK(str)</a>
     */
    public static SimpleExpression isUsedLock(final Expression str) {
        return FunctionUtils.oneArgFunc("IS_USED_LOCK", str, LongType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link IntegerType}
     * </p>
     *
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/locking-functions.html#function_release-all-locks">RELEASE_ALL_LOCKS()</a>
     */
    public static SimpleExpression releaseAllLocks() {
        return FunctionUtils.zeroArgFunc("RELEASE_ALL_LOCKS()", IntegerType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link IntegerType}
     * </p>
     *
     * @param str non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/locking-functions.html#function_release-lock">RELEASE_LOCK(str)</a>
     */
    public static SimpleExpression releaseLock(final Expression str) {
        return FunctionUtils.oneArgFunc("RELEASE_LOCK", str, IntegerType.INSTANCE);
    }



    /*-------------------below Information Functions-------------------*/

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link IntegerType}
     * </p>
     *
     * @param count non-null
     * @param expr  non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/information-functions.html#function_benchmark">BENCHMARK(count,expr)</a>
     */
    public static SimpleExpression benchmark(final Expression count, final Expression expr) {
        return FunctionUtils.twoArgFunc("BENCHMARK", count, expr, IntegerType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param str non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/information-functions.html#function_charset">CHARSET(str)</a>
     */
    public static SimpleExpression charset(final Expression str) {
        return FunctionUtils.oneArgFunc("CHARSET", str, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link IntegerType}
     * </p>
     *
     * @param str non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/information-functions.html#function_coercibility">COERCIBILITY(str)</a>
     */
    public static SimpleExpression coercibility(final Expression str) {
        return FunctionUtils.oneArgFunc("COERCIBILITY", str, IntegerType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param str non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/information-functions.html#function_collation">COLLATION(str)</a>
     */
    public static SimpleExpression collation(final Expression str) {
        return FunctionUtils.oneArgFunc("COLLATION", str, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link LongType}
     * </p>
     *
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/information-functions.html#function_connection-id">CONNECTION_ID()</a>
     */
    public static SimpleExpression connectionId() {
        return FunctionUtils.zeroArgFunc("CONNECTION_ID", LongType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/information-functions.html#function_current-role">CURRENT_ROLE()</a>
     */
    public static SimpleExpression currentRole() {
        return FunctionUtils.zeroArgFunc("CURRENT_ROLE", StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/information-functions.html#function_current-user">CURRENT_USER()</a>
     */
    public static SimpleExpression currentUser() {
        return FunctionUtils.zeroArgFunc("CURRENT_USER", StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/information-functions.html#function_database">DATABASE()</a>
     */
    public static SimpleExpression database() {
        return FunctionUtils.zeroArgFunc("DATABASE", StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/information-functions.html#function_icu-version">ICU_VERSION()</a>
     */
    public static SimpleExpression icuVersion() {
        return FunctionUtils.zeroArgFunc("ICU_VERSION", StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link LongType}
     * </p>
     *
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/information-functions.html#function_last-insert-id">LAST_INSERT_ID()</a>
     */
    public static SimpleExpression lastInsertId() {
        return FunctionUtils.zeroArgFunc("LAST_INSERT_ID", LongType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link LongType}
     * </p>
     *
     * @param expr non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/information-functions.html#function_last-insert-id">LAST_INSERT_ID()</a>
     */
    public static SimpleExpression lastInsertId(final Expression expr) {
        return FunctionUtils.oneArgFunc("LAST_INSERT_ID", expr, LongType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/information-functions.html#function_roles-graphml">ROLES_GRAPHML()</a>
     */
    public static SimpleExpression rolesGraphml() {
        return FunctionUtils.zeroArgFunc("ROLES_GRAPHML", StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link LongType}
     * </p>
     *
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/information-functions.html#function_row-count">ROW_COUNT()</a>
     */
    public static SimpleExpression rowCount() {
        return FunctionUtils.zeroArgFunc("ROW_COUNT", LongType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/information-functions.html#function_user">USER()</a>
     */
    public static SimpleExpression user() {
        return FunctionUtils.zeroArgFunc("USER", StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/information-functions.html#function_version">VERSION()</a>
     */
    public static SimpleExpression version() {
        return FunctionUtils.zeroArgFunc("VERSION", StringType.INSTANCE);
    }



    /*-------------------below Encryption and Compression Functions-------------------*/

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param cryptStr non-null
     * @param keyStr   non-null
     * @param rest     non-null,size in [0,4]
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/encryption-functions.html#function_aes-decrypt">AES_DECRYPT(crypt_str,key_str[,init_vector][,kdf_name][,salt][,info | iterations])</a>
     */
    public static SimpleExpression aesDecrypt(Expression cryptStr, Expression keyStr, Expression... rest) {
        return FunctionUtils.twoAndMaxRestForSingleExpFunc("AES_DECRYPT", StringType.INSTANCE, cryptStr, keyStr, 4, rest);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param str    non-null
     * @param keyStr non-null
     * @param rest   non-null,size in [0,4]
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/encryption-functions.html#function_aes-encrypt">AES_ENCRYPT(str,key_str[,init_vector][,kdf_name][,salt][,info | iterations])</a>
     */
    public static SimpleExpression aesEncrypt(final Expression str, final Expression keyStr
            , final Expression... rest) {
        return FunctionUtils.twoAndMaxRestForSingleExpFunc("AES_ENCRYPT", StringType.INSTANCE, str, keyStr, 4, rest);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param stringToCompress non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/encryption-functions.html#function_compress">COMPRESS(string_to_compress)</a>
     */
    public static SimpleExpression compress(final Expression stringToCompress) {
        return FunctionUtils.oneArgFunc("COMPRESS", stringToCompress, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param stringToUnCompress non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/encryption-functions.html#function_uncompress">UNCOMPRESS(string_to_uncompress)</a>
     */
    public static SimpleExpression unCompress(final Expression stringToUnCompress) {
        return FunctionUtils.oneArgFunc("UNCOMPRESS", stringToUnCompress, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param str non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/encryption-functions.html#function_md5">MD5(str)</a>
     */
    public static SimpleExpression md5(final Expression str) {
        return FunctionUtils.oneArgFunc("MD5", str, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param len non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/encryption-functions.html#function_md5">RANDOM_BYTES(len)</a>
     */
    public static SimpleExpression randomBytes(final Expression len) {
        return FunctionUtils.oneArgFunc("RANDOM_BYTES", len, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param str non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/encryption-functions.html#function_sha1">SHA1(str)</a>
     */
    public static SimpleExpression sha1(final Expression str) {
        return FunctionUtils.oneArgFunc("SHA1", str, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param str non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/encryption-functions.html#function_sha1">SHA(str)</a>
     */
    public static SimpleExpression sha(final Expression str) {
        return FunctionUtils.oneArgFunc("SHA", str, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param str        non-null
     * @param hashLength non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/encryption-functions.html#function_sha2">SHA2(str, hash_length)</a>
     */
    public static SimpleExpression sha2(final Expression str, final Expression hashLength) {
        return FunctionUtils.twoArgFunc("SHA2", str, hashLength, StringType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param stmt    non-null,stmt must produce one one sql(couldn't be multi sql)
     * @param visible non-null
     * @param literal true:output literal
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/encryption-functions.html#function_statement-digest">STATEMENT_DIGEST(statement)</a>
     */
    public static SimpleExpression statementDigest(final PrimaryStatement stmt, final Visible visible, final boolean literal) {
        return MySQLFunctionUtils.statementDigest(stmt, visible, literal);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param statement parameter or literal expression
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/encryption-functions.html#function_statement-digest">STATEMENT_DIGEST(statement)</a>
     */
    public static SimpleExpression statementDigest(Expression statement) {
        return FunctionUtils.oneArgFunc("STATEMENT_DIGEST", statement, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param stmt    non-null,stmt must produce one one sql(couldn't be multi sql)
     * @param visible non-null
     * @param literal true:output literal
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/encryption-functions.html#function_statement-digest-text">STATEMENT_DIGEST_TEXT(statement)</a>
     */
    public static SimpleExpression statementDigestText(final PrimaryStatement stmt, final Visible visible
            , final boolean literal) {
        return MySQLFunctionUtils.statementDigestText(stmt, visible, literal);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param statement parameter or literal expression
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/encryption-functions.html#function_statement-digest">STATEMENT_DIGEST(statement)</a>
     */
    public static SimpleExpression statementDigestText(Expression statement) {
        return FunctionUtils.oneArgFunc("STATEMENT_DIGEST_TEXT", statement, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link IntegerType}
     * </p>
     *
     * @param compressedString non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/encryption-functions.html#function_uncompressed-length">UNCOMPRESSED_LENGTH(compressed_string)</a>
     */
    public static SimpleExpression unCompressedLength(final Expression compressedString) {
        return FunctionUtils.oneArgFunc("UNCOMPRESSED_LENGTH", compressedString, IntegerType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:{@link IntegerType}
     * </p>
     *
     * @param str non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/encryption-functions.html#function_validate-password-strength">VALIDATE_PASSWORD_STRENGTH(str)</a>
     */
    public static SimpleExpression validatePasswordStrength(final Expression str) {
        return FunctionUtils.oneArgFunc("VALIDATE_PASSWORD_STRENGTH", str, IntegerType.INSTANCE);
    }



    /*-------------------below Miscellaneous Functions-------------------*/

    /**
     * <p>
     * The {@link MappingType} of function return type:the {@link  MappingType} of arg
     * </p>
     *
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/miscellaneous-functions.html#function_any-value">ANY_VALUE(arg)</a>
     */
    public static SimpleExpression anyValue(final Expression arg) {
        return FunctionUtils.oneArgFunc("ANY_VALUE", arg, arg.typeMeta());
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link StringType}
     * </p>
     *
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/miscellaneous-functions.html#function_bin-to-uuid">BIN_TO_UUID(binary_uuid, swap_flag)</a>
     */
    public static SimpleExpression binToUuid(final Expression binaryUuid) {
        return FunctionUtils.oneArgFunc("BIN_TO_UUID", binaryUuid, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link StringType}
     * </p>
     *
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/miscellaneous-functions.html#function_bin-to-uuid">BIN_TO_UUID(binary_uuid, swap_flag)</a>
     */
    public static SimpleExpression binToUuid(final Expression binaryUuid, final Expression swapFlag) {
        return FunctionUtils.twoArgFunc("BIN_TO_UUID", binaryUuid, swapFlag, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:the {@link  MappingType} of field
     * </p>
     *
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/miscellaneous-functions.html#function_default">DEFAULT(col_name)</a>
     */
    public static SimpleExpression defaultValue(final TableField field) {
        return FunctionUtils.oneArgFunc("DEFAULT", field, field);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link BooleanType}
     * </p>
     *
     * @param exp non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/miscellaneous-functions.html#function_default">GROUPING(expr [, expr] ...)</a>
     */
    public static IPredicate grouping(final Expression exp, Expression... restExp) {
        return FunctionUtils.oneAndRestFuncPredicate("GROUPING", exp, restExp);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link BooleanType}
     * </p>
     *
     * @param expList non-null,non-empty
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/miscellaneous-functions.html#function_default">GROUPING(expr [, expr] ...)</a>
     */
    public static IPredicate grouping(List<Expression> expList) {
        return FunctionUtils.multiArgFuncPredicate("GROUPING", expList);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link IntegerType}
     * </p>
     *
     * @param expr non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/miscellaneous-functions.html#function_inet-aton">INET_ATON(expr)</a>
     */
    public static SimpleExpression inetAton(final Expression expr) {
        return FunctionUtils.oneArgFunc("INET_ATON", expr, IntegerType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link StringType}
     * </p>
     *
     * @param expr non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/miscellaneous-functions.html#function_inet-ntoa">INET_NTOA(expr)</a>
     */
    public static SimpleExpression inetNtoa(final Expression expr) {
        return FunctionUtils.oneArgFunc("INET_NTOA", expr, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link PrimitiveByteArrayType}
     * </p>
     *
     * @param expr non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/miscellaneous-functions.html#function_inet6-aton">INET6_ATON(expr)</a>
     */
    public static SimpleExpression inet6Aton(final Expression expr) {
        return FunctionUtils.oneArgFunc("INET6_ATON", expr, PrimitiveByteArrayType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link StringType}
     * </p>
     *
     * @param expr non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/miscellaneous-functions.html#function_inet6-ntoa">INET6_NTOA(expr)</a>
     */
    public static SimpleExpression inet6Ntoa(final Expression expr) {
        return FunctionUtils.oneArgFunc("INET6_NTOA", expr, StringType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link BooleanType}
     * </p>
     *
     * @param expr non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/miscellaneous-functions.html#function_is-ipv4">IS_IPV4(expr)</a>
     */
    public static IPredicate isIpv4(final Expression expr) {
        return FunctionUtils.oneArgFuncPredicate("IS_IPV4", expr);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link BooleanType}
     * </p>
     *
     * @param expr non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/miscellaneous-functions.html#function_is-ipv4-compat">IS_IPV4_COMPAT(expr)</a>
     */
    public static IPredicate isIpv4Compat(final Expression expr) {
        return FunctionUtils.oneArgFuncPredicate("IS_IPV4_COMPAT", expr);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link BooleanType}
     * </p>
     *
     * @param expr non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/miscellaneous-functions.html#function_is-ipv4-mapped">IS_IPV4_MAPPED(expr)</a>
     */
    public static IPredicate isIpv4Mapped(final Expression expr) {
        return FunctionUtils.oneArgFuncPredicate("IS_IPV4_MAPPED", expr);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link BooleanType}
     * </p>
     *
     * @param expr non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/miscellaneous-functions.html#function_is-ipv6">IS_IPV6(expr)</a>
     */
    public static IPredicate isIpv6(final Expression expr) {
        return FunctionUtils.oneArgFuncPredicate("IS_IPV6", expr);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link BooleanType}
     * </p>
     *
     * @param stringUuid non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/miscellaneous-functions.html#function_is-uuid">IS_UUID(string_uuid)</a>
     */
    public static IPredicate isUuid(final Expression stringUuid) {
        return FunctionUtils.oneArgFuncPredicate("IS_UUID", stringUuid);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link IntegerType}
     * </p>
     *
     * @param logName non-null
     * @param logPos  non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/miscellaneous-functions.html#function_master-pos-wait">MASTER_POS_WAIT(log_name,log_pos[,timeout][,channel])</a>
     */
    public static SimpleExpression masterPosWait(final Expression logName, Expression logPos) {
        return FunctionUtils.twoArgFunc("MASTER_POS_WAIT", logName, logPos, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link IntegerType}
     * </p>
     *
     * @param logName non-null
     * @param logPos  non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/miscellaneous-functions.html#function_master-pos-wait">MASTER_POS_WAIT(log_name,log_pos[,timeout][,channel])</a>
     */
    public static SimpleExpression masterPosWait(final Expression logName, Expression logPos, Expression timeout) {
        return FunctionUtils.threeArgFunc("MASTER_POS_WAIT", logName, logPos, timeout, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link IntegerType}
     * </p>
     *
     * @param logName non-null
     * @param logPos  non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/miscellaneous-functions.html#function_master-pos-wait">MASTER_POS_WAIT(log_name,log_pos[,timeout][,channel])</a>
     */
    public static SimpleExpression masterPosWait(final Expression logName, Expression logPos, Expression timeout
            , Expression channel) {
        return FunctionUtils.multiArgFunc("MASTER_POS_WAIT", StringType.INSTANCE, logName, logPos, timeout, channel);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of value
     * </p>
     *
     * @param name  non-null,parameter {@link Expression} or literal {@link Expression}
     *              ,couldn't be named parameter {@link Expression} or named literal {@link Expression}
     * @param value non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/miscellaneous-functions.html#function_name-const">NAME_CONST(name,value)</a>
     */
    public static NamedExpression nameConst(final Expression name, final Expression value) {
        final String funcName = "NAME_CONST";
        final Object paramValue;
        if (!(name instanceof SqlValueParam.SingleAnonymousValue
                && (paramValue = ((SqlValueParam.SingleAnonymousValue) name).value()) instanceof String)) {
            throw CriteriaUtils.funcArgError(funcName, name);
        }
        final List<Object> argList = new ArrayList<>(3);
        argList.add(name);
        argList.add(Functions.FuncWord.COMMA);
        argList.add(value);
        return FunctionUtils.namedComplexArgFunc("NAME_CONST", argList, value.typeMeta(), (String) paramValue);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link IntegerType}
     * </p>
     *
     * @param duration non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/miscellaneous-functions.html#function_sleep">SLEEP(duration)</a>
     */
    public static SimpleExpression sleep(final Expression duration) {
        return FunctionUtils.oneArgFunc("SLEEP", duration, IntegerType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link IntegerType}
     * </p>
     *
     * @param logName non-null
     * @param logPos  non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/miscellaneous-functions.html#function_source-pos-wait">SOURCE_POS_WAIT(log_name,log_pos[,timeout][,channel])</a>
     */
    public static SimpleExpression sourcePosWait(final Expression logName, Expression logPos) {
        return FunctionUtils.twoArgFunc("SOURCE_POS_WAIT", logName, logPos, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link IntegerType}
     * </p>
     *
     * @param logName non-null
     * @param logPos  non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/miscellaneous-functions.html#function_source-pos-wait">SOURCE_POS_WAIT(log_name,log_pos[,timeout][,channel])</a>
     */
    public static SimpleExpression sourcePosWait(final Expression logName, Expression logPos, Expression timeout) {
        return FunctionUtils.threeArgFunc("SOURCE_POS_WAIT", logName, logPos, timeout, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link IntegerType}
     * </p>
     *
     * @param logName non-null
     * @param logPos  non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/miscellaneous-functions.html#function_source-pos-wait">SOURCE_POS_WAIT(log_name,log_pos[,timeout][,channel])</a>
     */
    public static SimpleExpression sourcePosWait(final Expression logName, Expression logPos, Expression timeout
            , Expression channel) {
        return FunctionUtils.multiArgFunc("SOURCE_POS_WAIT", StringType.INSTANCE, logName, logPos, timeout, channel);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link StringType}
     * </p>
     *
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/miscellaneous-functions.html#function_uuid">UUID()</a>
     */
    public static SimpleExpression uuid() {
        return FunctionUtils.zeroArgFunc("UUID", StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link LongType}
     * </p>
     *
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/miscellaneous-functions.html#function_uuid-short">UUID_SHORT()</a>
     */
    public static SimpleExpression uuidShort() {
        return FunctionUtils.zeroArgFunc("UUID_SHORT", LongType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link PrimitiveByteArrayType}
     * </p>
     *
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/miscellaneous-functions.html#function_uuid-to-bin">UUID_TO_BIN(string_uuid)</a>
     */
    public static SimpleExpression uuidToBin(final Expression stringUuid) {
        return FunctionUtils.oneArgFunc("UUID_TO_BIN", stringUuid, PrimitiveByteArrayType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link PrimitiveByteArrayType}
     * </p>
     *
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/miscellaneous-functions.html#function_uuid-to-bin">UUID_TO_BIN(string_uuid, swap_flag)</a>
     */
    public static SimpleExpression uuidToBin(final Expression stringUuid, final Expression swapFlag) {
        return FunctionUtils.twoArgFunc("UUID_TO_BIN", stringUuid, swapFlag, PrimitiveByteArrayType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of field
     * </p>
     *
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/miscellaneous-functions.html#function_values">VALUES(col_name)</a>
     */
    public static SimpleExpression values(final FieldMeta<?> field) {
        return FunctionUtils.oneArgFunc("VALUES", field, field);
    }




    /*-------------------below Performance Schema Functions-------------------*/

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param count non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/performance-schema-functions.html#function_format-bytes">FORMAT_BYTES(count)</a>
     */
    public static SimpleExpression formatBytes(final Expression count) {
        return FunctionUtils.oneArgFunc("FORMAT_BYTES", count, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param timeVal non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/performance-schema-functions.html#function_format-pico-time">FORMAT_PICO_TIME(time_val)</a>
     */
    public static SimpleExpression formatPicoTime(final Expression timeVal) {
        return FunctionUtils.oneArgFunc("FORMAT_PICO_TIME", timeVal, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link LongType}
     * </p>
     *
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/performance-schema-functions.html#function_ps-current-thread-id">PS_CURRENT_THREAD_ID()</a>
     */
    public static SimpleExpression psCurrentThreadId() {
        return FunctionUtils.zeroArgFunc("PS_CURRENT_THREAD_ID", LongType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link LongType}
     * </p>
     *
     * @param connectionId non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/performance-schema-functions.html#function_ps-thread-id">PS_THREAD_ID(connection_id)</a>
     */
    public static SimpleExpression psThreadId(final Expression connectionId) {
        return FunctionUtils.oneArgFunc("PS_THREAD_ID", connectionId, LongType.INSTANCE);
    }


    /*-------------------below Functions Used with Global Transaction Identifiers-------------------*/

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link BooleanType}
     * </p>
     *
     * @param set1 non-null
     * @param set2 non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gtid-functions.html#function_gtid-subset">GTID_SUBSET(set1,set2)</a>
     */
    public static IPredicate gtidSubset(final Expression set1, final Expression set2) {
        return FunctionUtils.twoArgPredicateFunc("GTID_SUBSET", set1, set2);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:the {@link MappingType} set1
     * </p>
     *
     * @param set1 non-null
     * @param set2 non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gtid-functions.html#function_gtid-subtract">GTID_SUBTRACT(set1,set2)</a>
     */
    public static SimpleExpression gtidSubtract(final Expression set1, final Expression set2) {
        return FunctionUtils.twoArgFunc("GTID_SUBTRACT", set1, set2, set1.typeMeta());
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link IntegerType}
     * </p>
     *
     * @param gtidSet non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gtid-functions.html#function_wait-for-executed-gtid-set">WAIT_FOR_EXECUTED_GTID_SET(gtid_set[, timeout])</a>
     */
    public static SimpleExpression waitForExecutedGtidSet(final Expression gtidSet) {
        return FunctionUtils.oneArgFunc("WAIT_FOR_EXECUTED_GTID_SET", gtidSet, IntegerType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link IntegerType}
     * </p>
     *
     * @param gtidSet non-null
     * @param timeout non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gtid-functions.html#function_wait-for-executed-gtid-set">WAIT_FOR_EXECUTED_GTID_SET(gtid_set[, timeout])</a>
     */
    public static SimpleExpression waitForExecutedGtidSet(final Expression gtidSet, final Expression timeout) {
        return FunctionUtils.twoArgFunc("WAIT_FOR_EXECUTED_GTID_SET", gtidSet, timeout, IntegerType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link IntegerType}
     * </p>
     *
     * @param gtidSet non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gtid-functions.html#function_wait-for-executed-gtid-set">WAIT_UNTIL_SQL_THREAD_AFTER_GTIDS(gtid_set[, timeout][,channel])</a>
     */
    static Expression waitUntilSqlThreadAfterGtids(final Expression gtidSet) {
        return FunctionUtils.oneArgFunc("WAIT_UNTIL_SQL_THREAD_AFTER_GTIDS", gtidSet, IntegerType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link IntegerType}
     * </p>
     *
     * @param gtidSet non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gtid-functions.html#function_wait-for-executed-gtid-set">WAIT_UNTIL_SQL_THREAD_AFTER_GTIDS(gtid_set[, timeout][,channel])</a>
     */
    static Expression waitUntilSqlThreadAfterGtids(Expression gtidSet, Expression timeout) {
        return FunctionUtils.twoArgFunc("WAIT_UNTIL_SQL_THREAD_AFTER_GTIDS", gtidSet, timeout, IntegerType.INSTANCE);
    }


}
