package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.mysql.MySQLFunction;
import io.army.mapping.*;
import io.army.mapping.optional.JsonListType;
import io.army.mapping.optional.JsonMapType;
import io.army.meta.TypeMeta;

import java.util.*;
import java.util.function.Function;

/**
 * package class
 *
 * @since 1.0
 */
@SuppressWarnings("unused")
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
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-search-functions.html#function_json-contains">JSON_CONTAINS(target, candidate[, path])</a>
     */
    public static IPredicate jsonContains(final Expression target, final Expression candidate) {
        return FunctionUtils.twoArgPredicateFunc("JSON_CONTAINS", target, candidate);
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
    public static IPredicate jsonContains(final Expression target, final Expression candidate, final Expression path) {
        return FunctionUtils.threeArgPredicateFunc("JSON_CONTAINS", target, candidate, path);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link BooleanType}
     * </p>
     *
     * @param jsonDoc  non-null
     * @param oneOrAll literal 'one' or 'all'
     * @param paths    non-null,multi parameter(literal) {@link Expression} is allowed
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see #jsonContainsPath(Expression, Expression, List)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-creation-functions.html#function_json-contains-path">JSON_CONTAINS_PATH(json_doc, one_or_all, path[, path] ...)</a>
     */
    public static IPredicate jsonContainsPath(final Expression jsonDoc, final Expression oneOrAll
            , final Expression paths) {
        return FunctionUtils.threeArgPredicateFunc("JSON_CONTAINS_PATH", jsonDoc, oneOrAll, paths);
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
     * @see #jsonContainsPath(Expression, Expression, Expression)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-creation-functions.html#function_json-contains-path">JSON_CONTAINS_PATH(json_doc, one_or_all, path[, path] ...)</a>
     */
    public static IPredicate jsonContainsPath(final Expression jsonDoc, final Expression oneOrAll
            , final List<Expression> pathList) {
        return FunctionUtils.twoAndMultiArgFuncPredicate("JSON_CONTAINS_PATH", jsonDoc, oneOrAll, pathList);
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
        return FunctionUtils.twoOrMultiArgFunc("JSON_EXTRACT", jsonDoc, paths, StringType.INSTANCE);
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
        return FunctionUtils.oneAndMultiArgFunc("JSON_EXTRACT", jsonDoc, pathList, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link StringType}
     * </p>
     *
     * @param jsonDoc   non-null,wrap to {@link SQLs#param(TypeInfer, Object)}
     * @param firstPath non-null,non-empty,wrap to {@link SQLs#param(TypeInfer, Object)} or {@link SQLs#multiParams(TypeInfer, Collection)}
     * @param paths     optional paths ,wrap to {@link SQLs#multiParams(TypeInfer, Collection)}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see #jsonExtract(Expression, Expression)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-creation-functions.html#function_json-extract">JSON_EXTRACT(json_doc, path[, path] ...)</a>
     */
    public static Expression jsonExtract(final Expression jsonDoc, final String firstPath, String... paths) {

        final Expression pathLiterals;
        if (paths.length == 0) {
            pathLiterals = SQLs.param(StringType.INSTANCE, firstPath);
        } else {
            final List<String> pathList;
            pathList = new ArrayList<>(1 + paths.length);
            pathList.add(firstPath);
            Collections.addAll(pathList, paths);
            pathLiterals = SQLs.multiParams(StringType.INSTANCE, pathList);
        }
        return FunctionUtils.twoOrMultiArgFunc("JSON_EXTRACT", jsonDoc, pathLiterals, StringType.INSTANCE);
    }

    /**
     * @param jsonDoc wrap to {@link SQLs#param(TypeInfer, Object)}
     * @see #jsonExtract(Expression, String, String...)
     */
    public static Expression jsonExtract(final String jsonDoc, final String firstPath, String... paths) {
        return jsonExtract(SQLs.param(StringType.INSTANCE, jsonDoc), firstPath, paths);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link StringType}
     * </p>
     *
     * @param jsonDoc non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see #jsonKeys(Expression, Expression)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-search-functions.html#function_json-keys">JSON_KEYS(json_doc[, path])</a>
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
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-search-functions.html#function_json-keys">JSON_KEYS(json_doc[, path])</a>
     */
    public static Expression jsonKeys(final Expression jsonDoc, final Expression path) {
        return FunctionUtils.twoArgFunc("JSON_KEYS", jsonDoc, path, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link StringType}
     * </p>
     *
     * @param jsonDoc non-null
     * @param path    wrap to {@link SQLs#param(TypeInfer, Object)}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see #jsonKeys(Expression)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-search-functions.html#function_json-keys">JSON_KEYS(json_doc[, path])</a>
     */
    public static Expression jsonKeys(final Expression jsonDoc, final String path) {
        return FunctionUtils.twoArgFunc("JSON_KEYS", jsonDoc, SQLs.param(StringType.INSTANCE, path)
                , StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link StringType}
     * </p>
     *
     * @param jsonDoc non-null, wrap to {@link SQLs#param(TypeInfer, Object)}
     * @param path    non-null,wrap to {@link SQLs#param(TypeInfer, Object)}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see #jsonKeys(Expression)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-search-functions.html#function_json-keys">JSON_KEYS(json_doc[, path])</a>
     */
    public static Expression jsonKeys(final String jsonDoc, final String path) {
        return FunctionUtils.twoArgFunc("JSON_KEYS", SQLs.param(StringType.INSTANCE, jsonDoc)
                , SQLs.param(StringType.INSTANCE, path)
                , StringType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link BooleanType}
     * </p>
     *
     * @param jsonDoc1 non-null
     * @param jsonDoc2 non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-search-functions.html#function_json-overlaps">JSON_OVERLAPS(json_doc1, json_doc2)</a>
     */
    public static IPredicate jsonOverlaps(final Expression jsonDoc1, final Expression jsonDoc2) {
        return FunctionUtils.twoArgPredicateFunc("JSON_OVERLAPS", jsonDoc1, jsonDoc2);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link BooleanType}
     * </p>
     *
     * @param jsonDoc1 non-null
     * @param jsonDoc2 non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-search-functions.html#function_json-overlaps">JSON_OVERLAPS(json_doc1, json_doc2)</a>
     */
    public static IPredicate jsonOverlaps(final String jsonDoc1, final String jsonDoc2) {
        return jsonOverlaps(SQLs.param(StringType.INSTANCE, jsonDoc1), SQLs.param(StringType.INSTANCE, jsonDoc2));
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link StringType}
     * </p>
     *
     * @param jsonDoc   non-null
     * @param oneOrAll  non-null,{@link MySQLs#LITERAL_one} or {@link MySQLs#LITERAL_all} ,or equivalence
     * @param searchStr non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see #jsonSearch(Expression, Expression, Expression, Expression)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-search-functions.html#function_json-search">JSON_SEARCH(json_doc, one_or_all, search_str[, escape_char[, path] ...])</a>
     */
    public static Expression jsonSearch(final Expression jsonDoc, final Expression oneOrAll
            , final Expression searchStr) {
        return FunctionUtils.threeArgFunc("JSON_SEARCH", jsonDoc, oneOrAll, searchStr, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link StringType}
     * </p>
     *
     * @param jsonDoc   non-null
     * @param oneOrAll  non-null,{@link MySQLs#LITERAL_one} or {@link MySQLs#LITERAL_all} ,or equivalence
     * @param searchStr non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see #jsonSearch(Expression, Expression, Expression, Expression)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-search-functions.html#function_json-search">JSON_SEARCH(json_doc, one_or_all, search_str[, escape_char[, path] ...])</a>
     */
    public static Expression jsonSearch(final Expression jsonDoc, final Expression oneOrAll
            , final Expression searchStr, final Expression escapeChar) {
        return FunctionUtils.multiArgFunc("JSON_SEARCH", StringType.INSTANCE, jsonDoc, oneOrAll, searchStr, escapeChar);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link StringType}
     * </p>
     *
     * @param jsonDoc   non-null
     * @param oneOrAll  non-null,{@link MySQLs#LITERAL_one} or {@link MySQLs#LITERAL_all} ,or equivalence
     * @param searchStr non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see #jsonSearch(Expression, Expression, Expression)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-search-functions.html#function_json-search">JSON_SEARCH(json_doc, one_or_all, search_str[, escape_char[, path] ...])</a>
     */
    public static Expression jsonSearch(final Expression jsonDoc, final Expression oneOrAll
            , final Expression searchStr, final Expression escapeChar, Expression path) {
        return FunctionUtils.multiArgFunc("JSON_SEARCH", StringType.INSTANCE
                , jsonDoc, oneOrAll, searchStr, escapeChar, path);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link StringType}
     * </p>
     *
     * @param jsonDoc   non-null
     * @param oneOrAll  non-null,{@link MySQLs#LITERAL_one} or {@link MySQLs#LITERAL_all} ,or equivalence
     * @param searchStr non-null
     * @param pathList  non-null,empty or path list
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see #jsonSearch(Expression, Expression, Expression)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-search-functions.html#function_json-search">JSON_SEARCH(json_doc, one_or_all, search_str[, escape_char[, path] ...])</a>
     */
    public static Expression jsonSearch(final Expression jsonDoc, final Expression oneOrAll
            , final Expression searchStr, final Expression escapeChar, List<Expression> pathList) {
        final List<ArmyExpression> argList = new ArrayList<>(4 + pathList.size());

        argList.add((ArmyExpression) jsonDoc);
        argList.add((ArmyExpression) oneOrAll);
        argList.add((ArmyExpression) searchStr);
        argList.add((ArmyExpression) escapeChar);

        for (Expression path : pathList) {
            argList.add((ArmyExpression) path);
        }
        return FunctionUtils.safeMultiArgFunc("JSON_SEARCH", argList, StringType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link StringType}
     * </p>
     *
     * @param jsonDoc    non-null
     * @param oneOrAll   non-null,{@link MySQLs#LITERAL_one} or {@link MySQLs#LITERAL_all} ,or equivalence
     * @param searchStr  non-null,wrap to literal expression
     * @param escapeChar non-null,wrap to literal expression
     * @param paths      non-null,wrap to literal expressions
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see #jsonSearch(Expression, Expression, Expression)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-search-functions.html#function_json-search">JSON_SEARCH(json_doc, one_or_all, search_str[, escape_char[, path] ...])</a>
     */
    public static Expression jsonSearch(final Expression jsonDoc, final Expression oneOrAll
            , final String searchStr, String escapeChar, String... paths) {

        final List<String> stringList = new ArrayList<>(2 + paths.length);
        stringList.add(searchStr);
        stringList.add(escapeChar);
        Collections.addAll(stringList, paths);

        final Expression multiLiteral;
        multiLiteral = LiteralExpression.safeMulti(StringType.INSTANCE, stringList);
        return FunctionUtils.threeArgFunc("JSON_SEARCH", jsonDoc, oneOrAll, multiLiteral, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link StringType}
     * </p>
     *
     * @param jsonDoc    non-null wrap parameter expression
     * @param oneOrAll   non-null,{@link MySQLs#LITERAL_one} or {@link MySQLs#LITERAL_all} ,or equivalence
     * @param searchStr  non-null,wrap to literal expression
     * @param escapeChar non-null,wrap to literal expression
     * @param paths      non-null,wrap to literal expressions
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see #jsonSearch(Expression, Expression, Expression)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-search-functions.html#function_json-search">JSON_SEARCH(json_doc, one_or_all, search_str[, escape_char[, path] ...])</a>
     */
    public static Expression jsonSearch(final String jsonDoc, final Expression oneOrAll
            , final String searchStr, String escapeChar, String... paths) {
        return jsonSearch(SQLs.param(StringType.INSTANCE, jsonDoc), oneOrAll, searchStr, escapeChar, paths);
    }


    /**
     * @see MySQLs#jsonValue(Function, Function)
     */
    public static Expression jsonValue(final Expression jsonDoc, final Expression path) {
        return MySQLs.jsonValue(SQLs::_asExp, SQLs::_identity)
                .leftParen(jsonDoc, path)
                .rightParen();
    }

    /**
     * @see MySQLs#jsonValue(Function, Function)
     */
    public static MySQLFunction._JsonValueLeftParenClause<Expression> jsonValue() {
        return MySQLs.jsonValue(SQLs::_asExp, SQLs::_identity);
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
     * @param jsonDoc       non-null
     * @param firstPath     wrap to literal expression
     * @param firstValue    wrap to parameter expression
     * @param pathValuePair non-null, size must even,path wrap to literal expression,value wrap to parameter expression
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-modification-functions.html#function_json-array-append">JSON_ARRAY_APPEND(json_doc, path, val[, path, val] ...)</a>
     */
    public static Expression jsonArrayAppend(final Expression jsonDoc, String firstPath, Object firstValue
            , Object... pathValuePair) {
        return _jsonPathValOperateFunc("JSON_ARRAY_APPEND", jsonDoc, firstPath, firstValue, pathValuePair);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:the {@link MappingType} of jsonDoc
     * </p>
     *
     * @param jsonDoc       non-null,wrap to parameter expression
     * @param firstPath     non-null,wrap to literal expression
     * @param firstValue    non-null,wrap to parameter expression
     * @param pathValuePair non-null, size must even,path wrap to literal expression,value wrap to parameter expression
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-modification-functions.html#function_json-array-append">JSON_ARRAY_APPEND(json_doc, path, val[, path, val] ...)</a>
     */
    public static Expression jsonArrayAppend(final String jsonDoc, String firstPath, Object firstValue
            , Object... pathValuePair) {
        return _jsonPathValOperateFunc("JSON_ARRAY_APPEND", SQLs.param(StringType.INSTANCE, jsonDoc)
                , firstPath, firstValue, pathValuePair);
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
     * @param jsonDoc       non-null
     * @param firstPath     wrap to literal expression
     * @param firstValue    wrap to parameter expression
     * @param pathValuePair non-null, size must even,path wrap to literal expression,value wrap to parameter expression
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-modification-functions.html#function_json-array-insert">JSON_ARRAY_INSERT(json_doc, path, val[, path, val] ...)</a>
     */
    public static Expression jsonArrayInsert(final Expression jsonDoc, String firstPath, Object firstValue
            , Object... pathValuePair) {
        return _jsonPathValOperateFunc("JSON_ARRAY_INSERT", jsonDoc, firstPath, firstValue, pathValuePair);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:the {@link MappingType} of jsonDoc
     * </p>
     *
     * @param jsonDoc       non-null,wrap to parameter expression
     * @param firstPath     non-null,wrap to literal expression
     * @param firstValue    non-null,wrap to parameter expression
     * @param pathValuePair non-null, size must even,path wrap to literal expression,value wrap to parameter expression
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-modification-functions.html#function_json-array-insert">JSON_ARRAY_INSERT(json_doc, path, val[, path, val] ...)</a>
     */
    public static Expression jsonArrayInsert(final String jsonDoc, String firstPath, Object firstValue
            , Object... pathValuePair) {
        return _jsonPathValOperateFunc("JSON_ARRAY_INSERT", SQLs.param(StringType.INSTANCE, jsonDoc)
                , firstPath, firstValue, pathValuePair);
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
     * @param jsonDoc       non-null
     * @param firstPath     wrap to literal expression
     * @param firstValue    wrap to parameter expression
     * @param pathValuePair non-null, size must even,path wrap to literal expression,value wrap to parameter expression
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-modification-functions.html#function_json-insert">JSON_INSERT(json_doc, path, val[, path, val] ...)</a>
     */
    public static Expression jsonInsert(final Expression jsonDoc, String firstPath, Object firstValue
            , Object... pathValuePair) {
        return _jsonPathValOperateFunc("JSON_INSERT", jsonDoc, firstPath, firstValue, pathValuePair);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:the {@link MappingType} of jsonDoc
     * </p>
     *
     * @param jsonDoc       non-null,wrap to parameter expression
     * @param firstPath     non-null,wrap to literal expression
     * @param firstValue    non-null,wrap to parameter expression
     * @param pathValuePair non-null, size must even,path wrap to literal expression,value wrap to parameter expression
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-modification-functions.html#function_json-insert">JSON_INSERT(json_doc, path, val[, path, val] ...)</a>
     */
    public static Expression jsonInsert(final String jsonDoc, String firstPath, Object firstValue
            , Object... pathValuePair) {
        return _jsonPathValOperateFunc("JSON_INSERT", SQLs.param(StringType.INSTANCE, jsonDoc)
                , firstPath, firstValue, pathValuePair);
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
     * @param jsonDoc       non-null
     * @param firstPath     wrap to literal expression
     * @param firstValue    wrap to parameter expression
     * @param pathValuePair non-null, size must even,path wrap to literal expression,value wrap to parameter expression
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-modification-functions.html#function_json-replace">JSON_REPLACE(json_doc, path, val[, path, val] ...)</a>
     */
    public static Expression jsonReplace(final Expression jsonDoc, String firstPath, Object firstValue
            , Object... pathValuePair) {
        return _jsonPathValOperateFunc("JSON_REPLACE", jsonDoc, firstPath, firstValue, pathValuePair);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:the {@link MappingType} of jsonDoc
     * </p>
     *
     * @param jsonDoc       non-null,wrap to parameter expression
     * @param firstPath     non-null,wrap to literal expression
     * @param firstValue    non-null,wrap to parameter expression
     * @param pathValuePair non-null, size must even,path wrap to literal expression,value wrap to parameter expression
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-modification-functions.html#function_json-replace">JSON_REPLACE(json_doc, path, val[, path, val] ...)</a>
     */
    public static Expression jsonReplace(final String jsonDoc, String firstPath, Object firstValue
            , Object... pathValuePair) {
        return _jsonPathValOperateFunc("JSON_REPLACE", SQLs.param(StringType.INSTANCE, jsonDoc)
                , firstPath, firstValue, pathValuePair);
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
     *
     * @param jsonDoc       non-null
     * @param firstPath     wrap to literal expression
     * @param firstValue    wrap to parameter expression
     * @param pathValuePair non-null, size must even,path wrap to literal expression,value wrap to parameter expression
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-modification-functions.html#function_json-set">JSON_SET(json_doc, path, val[, path, val] ...)</a>
     */
    public static Expression jsonSet(final Expression jsonDoc, String firstPath, Object firstValue
            , Object... pathValuePair) {
        return _jsonPathValOperateFunc("JSON_SET", jsonDoc, firstPath, firstValue, pathValuePair);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:the {@link MappingType} of jsonDoc
     * </p>
     *
     * @param jsonDoc       non-null,wrap to parameter expression
     * @param firstPath     non-null,wrap to literal expression
     * @param firstValue    non-null,wrap to parameter expression
     * @param pathValuePair non-null, size must even,path wrap to literal expression,value wrap to parameter expression
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-modification-functions.html#function_json-set">JSON_SET(json_doc, path, val[, path, val] ...)</a>
     */
    public static Expression jsonSet(final String jsonDoc, String firstPath, Object firstValue
            , Object... pathValuePair) {
        return _jsonPathValOperateFunc("JSON_SET", SQLs.param(StringType.INSTANCE, jsonDoc)
                , firstPath, firstValue, pathValuePair);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:the {@link MappingType} of jsonDoc
     * </p>
     * <p>
     * You should use {@link #jsonMergePreserve(Expression, Expression)},if database is 8.0+.
     * </p>
     *
     * @param jsonDoc1 non-null
     * @param jsonDoc2 non-null,multi parameter(literal) {@link Expression} is allowed.
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-modification-functions.html#function_json-merge">JSON_MERGE(json_doc, json_doc[, json_doc] ...)</a>
     */
    public static Expression jsonMerge(final Expression jsonDoc1, final Expression jsonDoc2, Expression... jsonDocArray) {
        return _jsonMergeOperationFunction("JSON_MERGE", jsonDoc1, jsonDoc2, jsonDocArray);
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
        final String name = "JSON_MERGE";
        if (jsonDocList.size() < 2) {
            throw CriteriaUtils.funcArgError(name, jsonDocList);
        }
        return FunctionUtils.multiArgFunc(name, jsonDocList, jsonDocList.get(0).typeMeta());
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:the {@link MappingType} of jsonDoc
     * </p>
     *
     * @param jsonDoc1 non-null
     * @param jsonDoc2 non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-modification-functions.html#function_json-merge-preserve">JSON_MERGE_PRESERVE(json_doc, json_doc[, json_doc] ...)</a>
     */
    public static Expression jsonMergePreserve(final Expression jsonDoc1, final Expression jsonDoc2, Expression... jsonDocArray) {
        return _jsonMergeOperationFunction("JSON_MERGE_PRESERVE", jsonDoc1, jsonDoc2, jsonDocArray);
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
        final String name = "JSON_MERGE_PRESERVE";
        if (jsonDocList.size() < 2) {
            throw CriteriaUtils.funcArgError(name, jsonDocList);
        }
        return FunctionUtils.multiArgFunc(name, jsonDocList, jsonDocList.get(0).typeMeta());
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:the {@link MappingType} of jsonDoc
     * </p>
     *
     * @param jsonDoc1 non-null
     * @param jsonDoc2 non-null,multi parameter(literal) {@link Expression} is allowed.
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-modification-functions.html#function_json-merge-patch">JSON_MERGE_PATCH(json_doc, json_doc[, json_doc] ...)</a>
     */
    public static Expression jsonMergePatch(final Expression jsonDoc1, final Expression jsonDoc2, Expression... jsonDocArray) {
        return _jsonMergeOperationFunction("JSON_MERGE_PATCH", jsonDoc1, jsonDoc2, jsonDocArray);
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
        final String name = "JSON_MERGE_PATCH";
        if (jsonDocList.size() < 2) {
            throw CriteriaUtils.funcArgError(name, jsonDocList);
        }
        return FunctionUtils.multiArgFunc(name, jsonDocList, jsonDocList.get(0).typeMeta());
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:the {@link MappingType} of jsonDoc
     * </p>
     *
     * @param jsonDoc   non-null
     * @param firstPath non-null,multi parameter(literal) {@link Expression} is allowed.
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-modification-functions.html#function_json-remove">JSON_REMOVE(json_doc, path[, path] ...)</a>
     */
    public static Expression jsonRemove(final Expression jsonDoc, final Expression firstPath, Expression... paths) {
        return FunctionUtils.twoAndMultiArgFunc("JSON_REMOVE", jsonDoc, firstPath, Arrays.asList(paths), jsonDoc.typeMeta());
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
        if (pathList.size() == 0) {
            throw CriteriaUtils.funcArgError(name, pathList);
        }
        return FunctionUtils.oneAndMultiArgFunc(name, jsonDoc, pathList, jsonDoc.typeMeta());
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:the {@link MappingType} of jsonDoc
     * </p>
     *
     * @param jsonDoc   non-null
     * @param firstPath non-null,wrap to literal expression
     * @param paths     non-null, empty or paths,wrap to literal expressions
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-modification-functions.html#function_json-remove">JSON_REMOVE(json_doc, path[, path] ...)</a>
     */
    public static Expression jsonRemove(final Expression jsonDoc, final String firstPath, String... paths) {
        List<ArmyExpression> argList = new ArrayList<>(2 + paths.length);
        argList.add((ArmyExpression) jsonDoc);
        argList.add(((ArmyExpression) SQLs.literal(StringType.INSTANCE, firstPath)));

        for (String path : paths) {
            argList.add(((ArmyExpression) SQLs.literal(StringType.INSTANCE, path)));
        }
        return FunctionUtils.safeMultiArgFunc("JSON_REMOVE", argList, jsonDoc.typeMeta());
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:the {@link MappingType} of jsonDoc
     * </p>
     *
     * @param jsonDoc   non-null,wrap to parameter expression
     * @param firstPath non-null,wrap to literal expression
     * @param paths     non-null, empty or paths,wrap to literal expressions
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-modification-functions.html#function_json-remove">JSON_REMOVE(json_doc, path[, path] ...)</a>
     */
    public static Expression jsonRemove(final String jsonDoc, final String firstPath, String... paths) {
        return jsonRemove(SQLs.param(StringType.INSTANCE, jsonDoc), firstPath, paths);
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
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-attribute-functions.html#function_json-depth">JSON_DEPTH(json_doc)</a>
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
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-attribute-functions.html#function_json-length">JSON_LENGTH(json_doc[, path])</a>
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
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-attribute-functions.html#function_json-length">JSON_LENGTH(json_doc[, path])</a>
     */
    public static Expression jsonLength(final Expression jsonDoc, final Expression path) {
        return FunctionUtils.twoArgFunc("JSON_LENGTH", jsonDoc, path, IntegerType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link StringType}
     * </p>
     *
     * @param jsonVal non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-attribute-functions.html#function_json-type">JSON_TYPE(json_val)</a>
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
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-attribute-functions.html#function_json-valid">JSON_VALID(val)</a>
     */
    public static IPredicate jsonValid(final Expression val) {
        return FunctionUtils.oneArgFuncPredicate("JSON_VALID", val);
    }

    /*-------------------below JSON Table Functions-------------------*/

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link BooleanType}
     * </p>
     *
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-table-functions.html#function_json-table">JSON_TABLE(expr, path COLUMNS (column_list) [AS] alias)</a>
     */
    public static MySQLFunction._JsonTableColumnsClause<TabularItem> jsonTable(Expression expr, Expression path) {
        return MySQLFunctionUtils.jsonTable(expr, path);
    }

    /*-------------------below private method -------------------*/


    /**
     * @see #jsonArrayAppend(Expression, List)
     * @see #jsonArrayInsert(Expression, List)
     * @see #jsonInsert(Expression, List)
     */
    private static Expression _jsonPathValOperateFunc(final String name, final Expression jsonDoc
            , final List<Expression> pathValList) {
        if (jsonDoc instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, jsonDoc);
        }
        final int size = pathValList.size();
        if (size == 0 || (size & 1) != 0) {
            throw CriteriaUtils.funcArgError(name, pathValList);
        }
        return FunctionUtils.oneAndMultiArgFunc(name, jsonDoc, pathValList, jsonDoc.typeMeta());
    }

    /**
     * @see #jsonArrayAppend(Expression, List)
     * @see #jsonArrayInsert(Expression, List)
     * @see #jsonInsert(Expression, List)
     * @see #jsonReplace(Expression, List)
     * @see #jsonSet(Expression, String, Object, Object...)
     */
    private static Expression _jsonPathValOperateFunc(final String name, final Expression jsonDoc, final String firstPath
            , final Object firstValue, final Object... pathValuePair) {
        if (pathValuePair.length == 0 || (pathValuePair.length & 1) != 0) {
            throw CriteriaUtils.funcArgError(name, pathValuePair);
        }
        final List<ArmyExpression> argList = new ArrayList<>(3 + pathValuePair.length);
        argList.add((ArmyExpression) jsonDoc);
        argList.add((ArmyExpression) SQLs.literal(StringType.INSTANCE, firstPath));
        argList.add((ArmyExpression) SQLs.paramFrom(firstValue));

        Object o;
        for (int i = 0; i < pathValuePair.length; i++) {
            o = pathValuePair[i];
            if ((i & 1) == 0) { //even
                if (o == null) {
                    argList.add((ArmyExpression) SQLs.param(_NullType.INSTANCE, null));
                } else {
                    argList.add((ArmyExpression) SQLs.paramFrom(o));
                }
            } else if (!(o instanceof String)) {
                throw ContextStack.criteriaError(ContextStack.peek(), "path must be String type");
            } else {
                argList.add((ArmyExpression) SQLs.literal(StringType.INSTANCE, o));
            }
        }
        return FunctionUtils.safeMultiArgFunc(name, argList, jsonDoc.typeMeta());
    }


    /**
     * @see #jsonMerge(List)
     */
    private static Expression _jsonMergerFunc(final String name, final List<Expression> jsonDocList) {
        final int size = jsonDocList.size();
        if (size < 2) {
            throw CriteriaUtils.funcArgError(name, jsonDocList);
        }
        final List<Object> argList = new ArrayList<>(size);
        int index = 0;
        for (Expression jsonDoc : jsonDocList) {
            if (index > 0) {
                argList.add(SQLSyntax.FuncWord.COMMA);
            }
            argList.add(jsonDoc);
            index++;
        }
        return FunctionUtils.complexArgFunc(name, argList, jsonDocList.get(0).typeMeta());
    }

    /**
     * @see #jsonMerge(Expression, Expression, Expression...)
     * @see #jsonMergePreserve(Expression, Expression, Expression...)
     * @see #jsonMergePatch(Expression, Expression, Expression...)
     */
    private static Expression _jsonMergeOperationFunction(final String name, final Expression jsonDoc1
            , final Expression jsonDoc2, Expression... jsonDocArray) {
        final List<ArmyExpression> argList = new ArrayList<>(2 + jsonDocArray.length);
        argList.add((ArmyExpression) jsonDoc1);
        argList.add((ArmyExpression) jsonDoc2);
        for (Expression jsonDoc : jsonDocArray) {
            argList.add((ArmyExpression) jsonDoc);
        }
        return FunctionUtils.safeMultiArgFunc(name, argList, jsonDoc1.typeMeta());
    }


}
