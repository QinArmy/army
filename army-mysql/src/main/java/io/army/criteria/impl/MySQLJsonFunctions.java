package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.mysql.MySQLCastType;
import io.army.criteria.mysql.MySQLClause;
import io.army.criteria.mysql.MySQLJsonContainWord;
import io.army.mapping.*;
import io.army.mapping.optional.JsonListType;
import io.army.mapping.optional.JsonMapType;
import io.army.mapping.optional.JsonType;
import io.army.meta.TypeMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * package class
 *
 * @since 1.0
 */
abstract class MySQLJsonFunctions extends MySQLTimeFunctions {


    MySQLJsonFunctions() {
    }

    /*-------------------below Functions That Create JSON Values-------------------*/

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonListType}
     * ;the {@link MappingType} of element:the {@link MappingType} val.
     * </p>
     *
     * @param val non-null,multi parameter or literal {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-creation-functions.html#function_json-array">JSON_ARRAY([val[, val] ...])</a>
     */
    public static Expression jsonArray(final Expression val) {
        final TypeMeta returnType;
        returnType = _returnType((ArmyExpression) val, JsonListType::from);
        return FunctionUtils.oneOrMultiArgFunc("JSON_ARRAY", val, returnType);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonListType}
     * ;the {@link MappingType} of element:the {@link MappingType} of first element.
     * </p>
     *
     * @param expList non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-creation-functions.html#function_json-array">JSON_ARRAY([val[, val] ...])</a>
     */
    public static Expression jsonArray(final List<Expression> expList) {
        final TypeMeta returnType;
        if (expList.size() == 0) {
            returnType = _NullType.INSTANCE;
        } else {
            returnType = _returnType((ArmyExpression) expList.get(0), JsonListType::from);
        }
        return FunctionUtils.complexArgFunc("JSON_ARRAY", _createSimpleMultiArgList(expList), returnType);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonMapType}
     * ;the {@link MappingType} of element:the {@link MappingType} of first element.
     * </p>
     *
     * @param expMap non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-creation-functions.html#function_json-object">JSON_OBJECT([key, val[, key, val] ...])</a>
     */
    public static Expression jsonObject(final Map<String, Expression> expMap) {
        final String name = "JSON_OBJECT";
        final Expression func;
        if (expMap.size() == 0) {
            func = FunctionUtils.noArgFunc(name, JsonMapType.from(_NullType.INSTANCE, _NullType.INSTANCE));
        } else {
            TypeMeta valueType = null;
            for (Expression value : expMap.values()) {
                valueType = value.typeMeta();
                break;
            }
            final TypeMeta returnType;
            if (valueType instanceof TypeMeta.Delay && !((TypeMeta.Delay) valueType).isPrepared()) {
                returnType = CriteriaSupports.delayParamMeta(StringType.INSTANCE, valueType, JsonMapType::from);
            } else {
                returnType = JsonMapType.from(StringType.INSTANCE, valueType.mappingType());
            }
            func = FunctionUtils.jsonObjectFunc(name, expMap, returnType);
        }
        return func;
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonMapType}
     * ;the {@link MappingType} of element:the {@link MappingType} of first element.
     * </p>
     *
     * @param expList non-null,empty or size even
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-creation-functions.html#function_json-object">JSON_OBJECT([key, val[, key, val] ...])</a>
     */
    public static Expression jsonObject(final List<Expression> expList) {
        final String name = "JSON_OBJECT";
        final int expSize = expList.size();
        final Expression func;
        if (expSize == 0) {
            func = FunctionUtils.noArgFunc(name, JsonMapType.from(_NullType.INSTANCE, _NullType.INSTANCE));
        } else if ((expSize & 1) != 0) {
            throw CriteriaUtils.funcArgError(name, expList);
        } else {
            final ArmyExpression keyExp, valueExp;
            keyExp = (ArmyExpression) expList.get(0);
            valueExp = (ArmyExpression) expList.get(1);
            final TypeMeta returnType;
            returnType = _returnType(keyExp, valueExp, JsonMapType::from);
            func = FunctionUtils.complexArgFunc(name, _createSimpleMultiArgList(expList), returnType);
        }
        return func;
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link StringType}
     * </p>
     *
     * @param string non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-creation-functions.html#function_json-quote">JSON_QUOTE(string)</a>
     */
    public static Expression jsonQuote(final Expression string) {
        return FunctionUtils.oneArgFunc("JSON_QUOTE", string, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link BooleanType}
     * </p>
     *
     * @param target    non-null
     * @param candidate non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see #jsonContains(Expression, Expression, Expression)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-creation-functions.html#function_json-contains">JSON_CONTAINS(target, candidate[, path])</a>
     */
    public static Expression jsonContains(final Expression target, final Expression candidate) {
        return _simpleTowArgFunc("JSON_CONTAINS", target, candidate, BooleanType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link BooleanType}
     * </p>
     *
     * @param target    non-null
     * @param candidate non-null
     * @param path      non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see #jsonContains(Expression, Expression)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-creation-functions.html#function_json-contains">JSON_CONTAINS(target, candidate[, path])</a>
     */
    public static Expression jsonContains(final Expression target, final Expression candidate, final Expression path) {
        return _simpleThreeArgFunc("JSON_CONTAINS", target, candidate, path, BooleanType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link BooleanType}
     * </p>
     *
     * @param jsonDoc  non-null
     * @param oneOrAll non-null
     * @param paths    non-null,multi parameter(literal) {@link Expression} is allowed
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see #jsonContainsPath(Expression, MySQLJsonContainWord, List)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-creation-functions.html#function_json-contains-path">JSON_CONTAINS_PATH(json_doc, one_or_all, path[, path] ...)</a>
     */
    public static Expression jsonContainsPath(final Expression jsonDoc, final MySQLJsonContainWord oneOrAll
            , final Expression paths) {
        final String name = "JSON_CONTAINS_PATH";
        if (jsonDoc instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, jsonDoc);
        }
        final List<Object> orgList = new ArrayList<>(5);

        orgList.add(jsonDoc);
        orgList.add(SQLSyntax.FuncWord.COMMA);
        orgList.add(oneOrAll);
        orgList.add(SQLSyntax.FuncWord.COMMA);

        orgList.add(paths);
        return FunctionUtils.complexArgFunc(name, orgList, BooleanType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link BooleanType}
     * </p>
     *
     * @param jsonDoc  non-null
     * @param oneOrAll non-null
     * @param pathList non-null,non-empty
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see #jsonContainsPath(Expression, MySQLJsonContainWord, Expression)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-creation-functions.html#function_json-contains-path">JSON_CONTAINS_PATH(json_doc, one_or_all, path[, path] ...)</a>
     */
    public static Expression jsonContainsPath(final Expression jsonDoc, final MySQLJsonContainWord oneOrAll
            , final List<Expression> pathList) {
        final String name = "JSON_CONTAINS_PATH";
        if (jsonDoc instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, jsonDoc);
        }
        if (pathList.size() == 0) {
            throw CriteriaUtils.funcArgError(name, pathList);
        }
        final List<Object> argList = new ArrayList<>(((2 + pathList.size()) << 1) - 1);
        argList.add(jsonDoc);
        argList.add(SQLSyntax.FuncWord.COMMA);
        argList.add(oneOrAll);

        for (Expression path : pathList) {
            argList.add(SQLSyntax.FuncWord.COMMA);
            argList.add(path);
        }
        return FunctionUtils.complexArgFunc(name, argList, BooleanType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link StringType}
     * </p>
     *
     * @param jsonDoc non-null
     * @param paths   non-null,multi parameter(literal) {@link Expression} is allowed
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see #jsonExtract(Expression, List)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-creation-functions.html#function_json-extract">JSON_EXTRACT(json_doc, path[, path] ...)</a>
     */
    public static Expression jsonExtract(final Expression jsonDoc, final Expression paths) {
        final String name = "JSON_EXTRACT";
        if (jsonDoc instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, jsonDoc);
        }
        final List<Object> argList = new ArrayList<>(3);
        argList.add(jsonDoc);
        argList.add(SQLSyntax.FuncWord.COMMA);
        argList.add(paths);
        return FunctionUtils.complexArgFunc(name, argList, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link StringType}
     * </p>
     *
     * @param jsonDoc  non-null
     * @param pathList non-null,non-empty
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see #jsonExtract(Expression, Expression)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-creation-functions.html#function_json-extract">JSON_EXTRACT(json_doc, path[, path] ...)</a>
     */
    public static Expression jsonExtract(final Expression jsonDoc, final List<Expression> pathList) {
        final String name = "JSON_EXTRACT";
        if (jsonDoc instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, jsonDoc);
        }
        if (pathList.size() == 0) {
            throw CriteriaUtils.funcArgError(name, pathList);
        }
        final List<Object> argList = new ArrayList<>(((1 + pathList.size()) << 1) - 1);

        argList.add(jsonDoc);
        for (Expression path : pathList) {
            argList.add(SQLSyntax.FuncWord.COMMA);
            argList.add(path);
        }
        return FunctionUtils.complexArgFunc(name, argList, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link StringType}
     * </p>
     *
     * @param jsonDoc non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see #jsonKeys(Expression, Expression)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-creation-functions.html#function_json-keys">JSON_KEYS(json_doc[, path])</a>
     */
    public static Expression jsonKeys(final Expression jsonDoc) {
        return FunctionUtils.oneArgFunc("JSON_KEYS", jsonDoc, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link StringType}
     * </p>
     *
     * @param jsonDoc non-null
     * @param path    non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see #jsonKeys(Expression)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-creation-functions.html#function_json-keys">JSON_KEYS(json_doc[, path])</a>
     */
    public static Expression jsonKeys(final Expression jsonDoc, final Expression path) {
        return _simpleTowArgFunc("JSON_KEYS", jsonDoc, path, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link BooleanType}
     * </p>
     *
     * @param jsonDoc1 non-null
     * @param jsonDoc2 non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-creation-functions.html#function_json-overlaps">JSON_OVERLAPS(json_doc1, json_doc2)</a>
     */
    public static Expression jsonOverlaps(final Expression jsonDoc1, final Expression jsonDoc2) {
        return _simpleTowArgFunc("JSON_OVERLAPS", jsonDoc1, jsonDoc2, BooleanType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link StringType}
     * </p>
     *
     * @param jsonDoc   non-null
     * @param oneOrAll  non-null
     * @param searchStr non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see #jsonSearch(Expression, MySQLJsonContainWord, Expression, List)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-creation-functions.html#function_json-search">JSON_SEARCH(json_doc, one_or_all, search_str[, escape_char[, path] ...])</a>
     */
    public static Expression jsonSearch(final Expression jsonDoc, final MySQLJsonContainWord oneOrAll
            , final Expression searchStr) {
        return jsonSearch(jsonDoc, oneOrAll, searchStr, Collections.emptyList());
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link StringType}
     * </p>
     *
     * @param jsonDoc            non-null
     * @param oneOrAll           non-null
     * @param searchStr          non-null
     * @param escapeCharAndPaths non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see #jsonSearch(Expression, MySQLJsonContainWord, Expression)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-creation-functions.html#function_json-search">JSON_SEARCH(json_doc, one_or_all, search_str[, escape_char[, path] ...])</a>
     */
    public static Expression jsonSearch(final Expression jsonDoc, final MySQLJsonContainWord oneOrAll
            , final Expression searchStr, final List<Expression> escapeCharAndPaths) {

        final String name = "JSON_SEARCH";
        if (jsonDoc instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, jsonDoc);
        }
        if (searchStr instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, searchStr);
        }
        final List<Object> argList = new ArrayList<>(((3 + escapeCharAndPaths.size()) << 1) - 1);
        argList.add(jsonDoc);
        argList.add(SQLSyntax.FuncWord.COMMA);
        argList.add(searchStr);

        for (Expression exp : escapeCharAndPaths) {
            argList.add(SQLSyntax.FuncWord.COMMA);
            argList.add(exp);
        }
        return FunctionUtils.complexArgFunc(name, argList, StringType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:
     *      <ul>
     *          <li>If don't specified RETURNING clause then {@link StringType}</li>
     *          <li>Else if type is {@link MySQLCastType#BINARY }then {@link ByteArrayType}</li>
     *          <li>Else if type is {@link MySQLCastType#CHAR }then {@link StringType}</li>
     *          <li>Else if type is {@link MySQLCastType#NCHAR }then {@link StringType}</li>
     *          <li>Else if type is {@link MySQLCastType#TIME }then {@link LocalTimeType}</li>
     *          <li>Else if type is {@link MySQLCastType#DATE }then {@link LocalDateType}</li>
     *          <li>Else if type is {@link MySQLCastType#YEAR }then {@link YearType}</li>
     *          <li>Else if type is {@link MySQLCastType#DATETIME }then {@link LocalDateTimeType}</li>
     *          <li>Else if type is {@link MySQLCastType#SIGNED_INTEGER }then {@link LongType}</li>
     *          <li>Else if type is {@link MySQLCastType#UNSIGNED_INTEGER }then {@link UnsignedBigIntegerType}</li>
     *          <li>Else if type is {@link MySQLCastType#DECIMAL }then {@link BigDecimalType}</li>
     *          <li>Else if type is {@link MySQLCastType#FLOAT }then {@link FloatType}</li>
     *          <li>Else if type is {@link MySQLCastType#REAL }then {@link DoubleType}</li>
     *          <li>Else if type is {@link MySQLCastType#DOUBLE }then {@link DoubleType}</li>
     *          <li>Else if type is {@link MySQLCastType#JSON }then {@link JsonType}</li>
     *          <li>Else if type is {@link MySQLCastType#Point }then {@link ByteArrayType}</li>
     *          <li>Else if type is {@link MySQLCastType#MultiPoint }then {@link ByteArrayType}</li>
     *          <li>Else if type is {@link MySQLCastType#MultiLineString }then {@link ByteArrayType}</li>
     *          <li>Else if type is {@link MySQLCastType#LineString }then {@link ByteArrayType}</li>
     *          <li>Else if type is {@link MySQLCastType#Polygon }then {@link ByteArrayType}</li>
     *          <li>Else if type is {@link MySQLCastType#MultiPolygon }then {@link ByteArrayType}</li>
     *          <li>Else if type is {@link MySQLCastType#GeometryCollection }then {@link ByteArrayType}</li>
     *      </ul>
     * </p>
     *
     * @param jsonDoc non-null
     * @param path    non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-creation-functions.html#function_json-value">JSON_VALUE(json_doc, path)</a>
     */
    public static MySQLClause._JsonValueReturningSpec jsonValue(final Expression jsonDoc, final Expression path) {
        return MySQLFunctionUtils.jsonValueFunc(jsonDoc, path);
    }




    /*-------------------below Functions That Modify JSON Values-------------------*/

    /**
     * <p>
     * The {@link MappingType} of function return type:the {@link MappingType} of jsonDoc
     * </p>
     *
     * @param jsonDoc     non-null
     * @param pathValList non-null,non-empty,size is even
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-modification-functions.html#function_json-array-append">JSON_ARRAY_APPEND(json_doc, path, val[, path, val] ...)</a>
     */
    public static Expression jsonArrayAppend(final Expression jsonDoc, final List<Expression> pathValList) {
        return _jsonPathValOperateFunc("JSON_ARRAY_APPEND", jsonDoc, pathValList);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:the {@link MappingType} of jsonDoc
     * </p>
     *
     * @param jsonDoc     non-null
     * @param pathValList non-null,non-empty,size is even
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-modification-functions.html#function_json-array-insert">JSON_ARRAY_INSERT(json_doc, path, val[, path, val] ...)</a>
     */
    public static Expression jsonArrayInsert(final Expression jsonDoc, final List<Expression> pathValList) {
        return _jsonPathValOperateFunc("JSON_ARRAY_INSERT", jsonDoc, pathValList);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:the {@link MappingType} of jsonDoc
     * </p>
     *
     * @param jsonDoc     non-null
     * @param pathValList non-null,non-empty,size is even
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-modification-functions.html#function_json-insert">JSON_INSERT(json_doc, path, val[, path, val] ...)</a>
     */
    public static Expression jsonInsert(final Expression jsonDoc, final List<Expression> pathValList) {
        return _jsonPathValOperateFunc("JSON_INSERT", jsonDoc, pathValList);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:the {@link MappingType} of jsonDoc
     * </p>
     *
     * @param jsonDoc     non-null
     * @param pathValList non-null,non-empty,size is even
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-modification-functions.html#function_json-replace">JSON_REPLACE(json_doc, path, val[, path, val] ...)</a>
     */
    public static Expression jsonReplace(final Expression jsonDoc, final List<Expression> pathValList) {
        return _jsonPathValOperateFunc("JSON_REPLACE", jsonDoc, pathValList);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:the {@link MappingType} of jsonDoc
     * </p>
     *
     * @param jsonDoc     non-null
     * @param pathValList non-null,non-empty,size is even
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-modification-functions.html#function_json-set">JSON_SET(json_doc, path, val[, path, val] ...)</a>
     */
    public static Expression jsonSet(final Expression jsonDoc, final List<Expression> pathValList) {
        return _jsonPathValOperateFunc("JSON_SET", jsonDoc, pathValList);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:the {@link MappingType} of jsonDoc
     * </p>
     * <p>
     * You should use {@link #jsonMergePreserve(Expression, Expression)},if database is 8.0+.
     * </p>
     *
     * @param jsonDoc     non-null
     * @param jsonDocList non-null,multi parameter(literal) {@link Expression} is allowed.
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-modification-functions.html#function_json-merge">JSON_MERGE(json_doc, json_doc[, json_doc] ...)</a>
     */
    public static Expression jsonMerge(final Expression jsonDoc, final Expression jsonDocList) {
        return _singleAndMultiArgFunc("JSON_MERGE", jsonDoc, jsonDocList, jsonDoc.typeMeta());
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:the {@link MappingType} of jsonDoc
     * </p>
     * <p>
     * You should use {@link #jsonMergePreserve(List)},if database is 8.0+.
     * </p>
     *
     * @param jsonDocList non-null,non-empty,multi parameter(literal) {@link Expression} is allowed.
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-modification-functions.html#function_json-merge">JSON_MERGE(json_doc, json_doc[, json_doc] ...)</a>
     */
    public static Expression jsonMerge(final List<Expression> jsonDocList) {
        return _jsonMergerFunc("JSON_MERGE", jsonDocList);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:the {@link MappingType} of jsonDoc
     * </p>
     *
     * @param jsonDoc     non-null
     * @param jsonDocList non-null,multi parameter(literal) {@link Expression} is allowed.
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-modification-functions.html#function_json-merge-preserve">JSON_MERGE_PRESERVE(json_doc, json_doc[, json_doc] ...)</a>
     */
    public static Expression jsonMergePreserve(final Expression jsonDoc, final Expression jsonDocList) {
        return _singleAndMultiArgFunc("JSON_MERGE_PRESERVE", jsonDoc, jsonDocList, jsonDoc.typeMeta());
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:the {@link MappingType} of jsonDoc
     * </p>
     *
     * @param jsonDocList non-null,non-empty,multi parameter(literal) {@link Expression} is allowed.
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-modification-functions.html#function_json-merge-preserve">JSON_MERGE_PRESERVE(json_doc, json_doc[, json_doc] ...)</a>
     */
    public static Expression jsonMergePreserve(final List<Expression> jsonDocList) {
        return _jsonMergerFunc("JSON_MERGE_PRESERVE", jsonDocList);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:the {@link MappingType} of jsonDoc
     * </p>
     *
     * @param jsonDoc     non-null
     * @param jsonDocList non-null,multi parameter(literal) {@link Expression} is allowed.
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-modification-functions.html#function_json-merge-patch">JSON_MERGE_PATCH(json_doc, json_doc[, json_doc] ...)</a>
     */
    public static Expression jsonMergePatch(final Expression jsonDoc, final Expression jsonDocList) {
        return _singleAndMultiArgFunc("JSON_MERGE_PATCH", jsonDoc, jsonDocList, jsonDoc.typeMeta());
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:the {@link MappingType} of jsonDoc
     * </p>
     *
     * @param jsonDocList non-null,non-empty,multi parameter(literal) {@link Expression} is allowed.
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-modification-functions.html#function_json-merge-patch">JSON_MERGE_PATCH(json_doc, json_doc[, json_doc] ...)</a>
     */
    public static Expression jsonMergePatch(final List<Expression> jsonDocList) {
        return _jsonMergerFunc("JSON_MERGE_PATCH", jsonDocList);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:the {@link MappingType} of jsonDoc
     * </p>
     *
     * @param jsonDoc  non-null
     * @param pathList non-null,multi parameter(literal) {@link Expression} is allowed.
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-modification-functions.html#function_json-remove">JSON_REMOVE(json_doc, path[, path] ...)</a>
     */
    public static Expression jsonRemove(final Expression jsonDoc, final Expression pathList) {
        return _singleAndMultiArgFunc("JSON_REMOVE", jsonDoc, pathList, jsonDoc.typeMeta());
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:the {@link MappingType} of jsonDoc
     * </p>
     *
     * @param jsonDoc  non-null
     * @param pathList non-null,non-empty,multi parameter(literal) {@link Expression} is allowed.
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-modification-functions.html#function_json-remove">JSON_REMOVE(json_doc, path[, path] ...)</a>
     */
    public static Expression jsonRemove(final Expression jsonDoc, final List<Expression> pathList) {
        final String name = "JSON_REMOVE";
        if (jsonDoc instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, jsonDoc);
        }
        final int pathSize = pathList.size();
        if (pathSize == 0) {
            throw CriteriaUtils.funcArgError(name, pathList);
        }
        final List<Object> argList = new ArrayList<>(((1 + pathSize) << 1) - 1);
        argList.add(jsonDoc);
        for (Expression path : pathList) {
            argList.add(SQLSyntax.FuncWord.COMMA);
            argList.add(path);
        }
        return FunctionUtils.complexArgFunc(name, argList, jsonDoc.typeMeta());
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link StringType}
     * </p>
     *
     * @param jsonVal non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-modification-functions.html#function_json-unquote">JSON_UNQUOTE(json_val)</a>
     */
    public static Expression jsonUnquote(final Expression jsonVal) {
        return FunctionUtils.oneArgFunc("JSON_UNQUOTE", jsonVal, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link IntegerType}
     * </p>
     *
     * @param jsonDoc non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-modification-functions.html#function_json-depth">JSON_DEPTH(json_doc)</a>
     */
    public static Expression jsonDepth(final Expression jsonDoc) {
        return FunctionUtils.oneArgFunc("JSON_DEPTH", jsonDoc, IntegerType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link IntegerType}
     * </p>
     *
     * @param jsonDoc non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-modification-functions.html#function_json-length">JSON_LENGTH(json_doc[, path])</a>
     */
    public static Expression jsonLength(final Expression jsonDoc) {
        return FunctionUtils.oneArgFunc("JSON_LENGTH", jsonDoc, IntegerType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link IntegerType}
     * </p>
     *
     * @param jsonDoc non-null
     * @param path    non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-modification-functions.html#function_json-length">JSON_LENGTH(json_doc[, path])</a>
     */
    public static Expression jsonLength(final Expression jsonDoc, final Expression path) {
        return _simpleTowArgFunc("JSON_LENGTH", jsonDoc, path, IntegerType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link StringType}
     * </p>
     *
     * @param jsonVal non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-modification-functions.html#function_json-type">JSON_TYPE(json_val)</a>
     */
    public static Expression jsonType(final Expression jsonVal) {
        //TODO 是否要 enum
        return FunctionUtils.oneArgFunc("JSON_TYPE", jsonVal, StringType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link BooleanType}
     * </p>
     *
     * @param val non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-modification-functions.html#function_json-valid">JSON_VALID(val)</a>
     */
    public static IPredicate jsonValid(final Expression val) {
        return FunctionUtils.oneArgFuncPredicate("JSON_VALID", val);
    }

    /*-------------------below JSON Table Functions-------------------*/

    public static MySQLClause._JsonTableColumnsClause<TabularItem> jsonTable(Expression expr, Expression path) {
        return MySQLFunctionUtils.jsonTable(expr, path);
    }


}
