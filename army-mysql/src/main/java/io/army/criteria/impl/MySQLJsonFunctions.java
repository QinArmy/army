package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.mysql.MySQLCastType;
import io.army.criteria.mysql.MySQLFunction;
import io.army.mapping.*;

import java.util.*;
import java.util.function.Consumer;

/**
 * package class
 *
 * @since 0.6.0
 */
@SuppressWarnings("unused")
abstract class MySQLJsonFunctions extends MySQLTimeFunctions {
    //TODO 加上 JSON Schema Validation Functions https://dev.mysql.com/doc/refman/8.0/en/json-validation-functions.html
    //TODO 加上 JSON Utility Functions

    MySQLJsonFunctions() {
    }

    /*-------------------below Functions That Create JSON Values-------------------*/

    /**
     * <p>The {@link MappingType} of function return type: {@link JsonType#TEXT}
     *
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-creation-functions.html#function_json-array">JSON_ARRAY([val[, val] ...])</a>
     */
    public static SimpleExpression jsonArray() {
        return FunctionUtils.zeroArgFunc("JSON_ARRAY", JsonType.TEXT);
    }

    /**
     * <p>The {@link MappingType} of function return type: {@link JsonType#TEXT}
     *
     * @param value non-null, one of following :
     *              <ul>
     *                <li>{@link Expression} instance</li>
     *                <li>{@link List} instance</li>
     *                <li>one dimension array</li>
     *                <li>literal</li>
     *              </ul>
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-creation-functions.html#function_json-array">JSON_ARRAY([val[, val] ...])</a>
     */
    public static SimpleExpression jsonArray(final Object value) {
        final String name = "JSON_ARRAY";
        final SimpleExpression func;
        if (value instanceof List) {
            func = LiteralFunctions.multiArgFunc(name, (List<?>) value, JsonType.TEXT);
        } else if (!value.getClass().isArray()) {
            func = LiteralFunctions.oneArgFunc(name, value, JsonType.TEXT);
        } else if (value.getClass().getComponentType().isArray()) {
            throw ContextStack.clearStackAndCriteriaError("reject multi dimension array");
        } else {
            func = LiteralFunctions.multiArgFunc(name, Arrays.asList((Object[]) value), JsonType.TEXT);
        }
        return func;
    }

    /**
     * <p>The {@link MappingType} of function return type: {@link JsonType#TEXT}
     *
     * @param value1   non-null, one of following :
     *                 <ul>
     *                   <li>{@link Expression} instance</li>
     *                   <li>literal</li>
     *                 </ul>
     * @param value2   non-null, one of following :
     *                 <ul>
     *                   <li>{@link Expression} instance</li>
     *                   <li>literal</li>
     *                 </ul>
     * @param variadic non-null,each of variadic is  one of following :
     *                 <ul>
     *                   <li>{@link Expression} instance</li>
     *                   <li>literal</li>
     *                 </ul>
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-creation-functions.html#function_json-array">JSON_ARRAY([val[, val] ...])</a>
     */
    public static SimpleExpression jsonArray(Object value1, Object value2, Object... variadic) {
        return LiteralFunctions.multiArgFunc("JSON_ARRAY", FuncExpUtils.twoAndVariadic(value1, value2, variadic), JsonType.TEXT);
    }


    /**
     * <p>The {@link MappingType} of function return type: {@link JsonType#TEXT}
     *
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-creation-functions.html#function_json-array">JSON_ARRAY([val[, val] ...])</a>
     */
    public static SimpleExpression jsonArray(Consumer<Clause._VariadicSpaceClause> consumer) {
        return LiteralFunctions.multiArgFunc("JSON_ARRAY", FuncExpUtils.variadicList(false, consumer), JsonType.TEXT);
    }

    /**
     * <p>The {@link MappingType} of function return type: {@link JsonType#TEXT}
     *
     * @param space    see {@link SQLs#SPACE}
     * @param consumer non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-creation-functions.html#function_json-array">JSON_ARRAY([val[, val] ...])</a>
     */
    public static SimpleExpression jsonArray(SQLs.SymbolSpace space, Consumer<Clause._VariadicConsumer> consumer) {
        return LiteralFunctions.multiArgFunc("JSON_ARRAY", FuncExpUtils.variadicList(false, consumer), JsonType.TEXT);
    }

    /**
     * <p>The {@link MappingType} of function return type: {@link JsonType#TEXT}
     *
     * @param expMap non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-creation-functions.html#function_json-object">JSON_OBJECT([key, val[, key, val] ...])</a>
     */
    public static SimpleExpression jsonObject(final Map<String, ?> expMap) {
        return LiteralFunctions.jsonMapFunc("JSON_OBJECT", expMap, JsonType.TEXT);
    }


    /**
     * <p>The {@link MappingType} of function return type: {@link JsonType#TEXT}
     *
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-creation-functions.html#function_json-object">JSON_OBJECT([key, val[, key, val] ...])</a>
     */
    public static SimpleExpression jsonObject(final Consumer<Clause._PairVariadicSpaceClause> consumer) {
        return LiteralFunctions.multiArgFunc("JSON_OBJECT", FuncExpUtils.pariVariadicList(false, consumer), JsonType.TEXT);
    }

    /**
     * <p>The {@link MappingType} of function return type: {@link JsonType#TEXT}
     *
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-creation-functions.html#function_json-object">JSON_OBJECT([key, val[, key, val] ...])</a>
     */
    public static SimpleExpression jsonObject(SQLs.SymbolSpace space, Consumer<Clause._PairVariadicConsumerClause> consumer) {
        return LiteralFunctions.multiArgFunc("JSON_OBJECT", FuncExpUtils.pariVariadicList(false, consumer), JsonType.TEXT);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link StringType}
     * *
     *
     * @param string non-null,each of variadic is  one of following :
     *               <ul>
     *                 <li>{@link Expression} instance</li>
     *                 <li>{@link String} instance</li>
     *               </ul>
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-creation-functions.html#function_json-quote">JSON_QUOTE(string)</a>
     */
    public static SimpleExpression jsonQuote(final Object string) {
        FuncExpUtils.assertTextExp(string);
        return LiteralFunctions.oneArgFunc("JSON_QUOTE", string, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link BooleanType}
     * *
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
     * *
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
     * *
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
     * *
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
     * *
     *
     * @param jsonDoc non-null
     * @param paths   non-null,multi parameter(literal) {@link Expression} is allowed
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see #jsonExtract(Expression, List)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-creation-functions.html#function_json-extract">JSON_EXTRACT(json_doc, path[, path] ...)</a>
     */
    public static SimpleExpression jsonExtract(final Expression jsonDoc, final Expression paths) {
        return FunctionUtils.twoOrMultiArgFunc("JSON_EXTRACT", jsonDoc, paths, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link StringType}
     * *
     *
     * @param jsonDoc  non-null
     * @param pathList non-null,non-empty
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see #jsonExtract(Expression, Expression)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-creation-functions.html#function_json-extract">JSON_EXTRACT(json_doc, path[, path] ...)</a>
     */
    public static SimpleExpression jsonExtract(final Expression jsonDoc, final List<Expression> pathList) {
        return FunctionUtils.oneAndMultiArgFunc("JSON_EXTRACT", jsonDoc, pathList, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link StringType}
     * *
     *
     * @param jsonDoc   non-null,wrap to {@link SQLs#param(TypeInfer, Object)}
     * @param firstPath non-null,non-empty,wrap to {@link SQLs#param(TypeInfer, Object)} or {@link SQLs#rowParam(TypeInfer, Collection)}
     * @param paths     optional paths ,wrap to {@link SQLs#rowParam(TypeInfer, Collection)}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see #jsonExtract(Expression, Expression)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-creation-functions.html#function_json-extract">JSON_EXTRACT(json_doc, path[, path] ...)</a>
     */
    public static SimpleExpression jsonExtract(final Expression jsonDoc, final String firstPath, String... paths) {
        //TODO
        throw new UnsupportedOperationException();
    }

    /**
     * @param jsonDoc wrap to {@link SQLs#param(TypeInfer, Object)}
     * @see #jsonExtract(Expression, String, String...)
     */
    public static SimpleExpression jsonExtract(final String jsonDoc, final String firstPath, String... paths) {
        return jsonExtract(SQLs.param(StringType.INSTANCE, jsonDoc), firstPath, paths);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link StringType}
     * *
     *
     * @param jsonDoc non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see #jsonKeys(Expression, Expression)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-search-functions.html#function_json-keys">JSON_KEYS(json_doc[, path])</a>
     */
    public static SimpleExpression jsonKeys(final Expression jsonDoc) {
        return FunctionUtils.oneArgFunc("JSON_KEYS", jsonDoc, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link StringType}
     * *
     *
     * @param jsonDoc non-null
     * @param path    non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see #jsonKeys(Expression)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-search-functions.html#function_json-keys">JSON_KEYS(json_doc[, path])</a>
     */
    public static SimpleExpression jsonKeys(final Expression jsonDoc, final Expression path) {
        return FunctionUtils.twoArgFunc("JSON_KEYS", jsonDoc, path, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link StringType}
     * *
     *
     * @param jsonDoc non-null
     * @param path    wrap to {@link SQLs#param(TypeInfer, Object)}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see #jsonKeys(Expression)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-search-functions.html#function_json-keys">JSON_KEYS(json_doc[, path])</a>
     */
    public static SimpleExpression jsonKeys(final Expression jsonDoc, final String path) {
        return FunctionUtils.twoArgFunc("JSON_KEYS", jsonDoc, SQLs.param(StringType.INSTANCE, path)
                , StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link StringType}
     * *
     *
     * @param jsonDoc non-null, wrap to {@link SQLs#param(TypeInfer, Object)}
     * @param path    non-null,wrap to {@link SQLs#param(TypeInfer, Object)}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see #jsonKeys(Expression)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-search-functions.html#function_json-keys">JSON_KEYS(json_doc[, path])</a>
     */
    public static SimpleExpression jsonKeys(final String jsonDoc, final String path) {
        return FunctionUtils.twoArgFunc("JSON_KEYS", SQLs.param(StringType.INSTANCE, jsonDoc)
                , SQLs.param(StringType.INSTANCE, path)
                , StringType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link BooleanType}
     * *
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
     * *
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
     * *
     *
     * @param jsonDoc   non-null
     * @param oneOrAll  non-null,{@link MySQLs#LITERAL_one} or {@link MySQLs#LITERAL_all} ,or equivalence
     * @param searchStr non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see #jsonSearch(Expression, Expression, Expression, Expression)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-search-functions.html#function_json-search">JSON_SEARCH(json_doc, one_or_all, search_str[, escape_char[, path] ...])</a>
     */
    public static SimpleExpression jsonSearch(final Expression jsonDoc, final Expression oneOrAll
            , final Expression searchStr) {
        return FunctionUtils.threeArgFunc("JSON_SEARCH", jsonDoc, oneOrAll, searchStr, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link StringType}
     * *
     *
     * @param jsonDoc   non-null
     * @param oneOrAll  non-null,{@link MySQLs#LITERAL_one} or {@link MySQLs#LITERAL_all} ,or equivalence
     * @param searchStr non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see #jsonSearch(Expression, Expression, Expression, Expression)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-search-functions.html#function_json-search">JSON_SEARCH(json_doc, one_or_all, search_str[, escape_char[, path] ...])</a>
     */
    public static SimpleExpression jsonSearch(final Expression jsonDoc, final Expression oneOrAll
            , final Expression searchStr, final Expression escapeChar) {
        return FunctionUtils.multiArgFunc("JSON_SEARCH", StringType.INSTANCE, jsonDoc, oneOrAll, searchStr, escapeChar);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link StringType}
     * *
     *
     * @param jsonDoc   non-null
     * @param oneOrAll  non-null,{@link MySQLs#LITERAL_one} or {@link MySQLs#LITERAL_all} ,or equivalence
     * @param searchStr non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see #jsonSearch(Expression, Expression, Expression)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-search-functions.html#function_json-search">JSON_SEARCH(json_doc, one_or_all, search_str[, escape_char[, path] ...])</a>
     */
    public static SimpleExpression jsonSearch(final Expression jsonDoc, final Expression oneOrAll
            , final Expression searchStr, final Expression escapeChar, Expression path) {
        return FunctionUtils.multiArgFunc("JSON_SEARCH", StringType.INSTANCE
                , jsonDoc, oneOrAll, searchStr, escapeChar, path);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link StringType}
     * *
     *
     * @param jsonDoc   non-null
     * @param oneOrAll  non-null,{@link MySQLs#LITERAL_one} or {@link MySQLs#LITERAL_all} ,or equivalence
     * @param searchStr non-null
     * @param pathList  non-null,empty or path list
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see #jsonSearch(Expression, Expression, Expression)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-search-functions.html#function_json-search">JSON_SEARCH(json_doc, one_or_all, search_str[, escape_char[, path] ...])</a>
     */
    public static SimpleExpression jsonSearch(final Expression jsonDoc, final Expression oneOrAll
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
     * *
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
    public static SimpleExpression jsonSearch(final Expression jsonDoc, final Expression oneOrAll
            , final String searchStr, String escapeChar, String... paths) {

        final List<String> stringList = new ArrayList<>(2 + paths.length);
        stringList.add(searchStr);
        stringList.add(escapeChar);
        Collections.addAll(stringList, paths);

        final Expression multiLiteral = null; //TODO
        // multiLiteral = MultiLiteralExpression.unsafeMulti(StringType.INSTANCE, stringList);
        return FunctionUtils.threeArgFunc("JSON_SEARCH", jsonDoc, oneOrAll, multiLiteral, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link StringType}
     * *
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
    public static SimpleExpression jsonSearch(final String jsonDoc, final Expression oneOrAll
            , final String searchStr, String escapeChar, String... paths) {
        return jsonSearch(SQLs.param(StringType.INSTANCE, jsonDoc), oneOrAll, searchStr, escapeChar, paths);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link StringType}
     * * <p>
     *
     * @param jsonDoc json expression
     *                <ul>
     *                     <li>{@link Expression} instance</li>
     *                     <li>the instance that {@link JsonType#TEXT} can accept,here it will output literal. For example : {@code "[1,2]"} is equivalent to {@code SQLs.literal(JsonType.TEXT,"[1,2]") } </li>
     *                </ul>
     * @param path    path expression
     *                <ul>
     *                      <li>{@link Expression} instance</li>
     *                      <li>{@link String} instance. For example : {@code "$[*]"} is equivalent to {@code SQLs.literal(StringType.INSTANCE,"$[*]") }</li>
     *                </ul>
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-search-functions.html#function_json-value">JSON_VALUE(json_doc, path)</a>
     */
    public static SimpleExpression jsonValue(final Object jsonDoc, final Object path) {
        return FunctionUtils.twoArgFunc("JSON_VALUE", FuncExpUtils.jsonDocExp(jsonDoc), FuncExpUtils.jsonPathExp(path),
                StringType.INSTANCE
        );
    }


    /**
     * The {@link MappingType} of function return type:
     * <ul>
     *     <li>If don't specified RETURNING clause then {@link StringType}</li>
     *     <li>Else if type is {@link MySQLCastType#CHAR }then {@link StringType}</li>
     *     <li>Else if type is {@link MySQLCastType#TIME }then {@link LocalTimeType}</li>
     *     <li>Else if type is {@link MySQLCastType#DATE }then {@link LocalDateType}</li>
     *     <li>Else if type is {@link MySQLCastType#YEAR }then {@link YearType}</li>
     *     <li>Else if type is {@link MySQLCastType#DATETIME }then {@link LocalDateTimeType}</li>
     *     <li>Else if type is {@link MySQLCastType#SIGNED }then {@link LongType}</li>
     *     <li>Else if type is {@link MySQLCastType#UNSIGNED }then {@link UnsignedBigIntegerType}</li>
     *     <li>Else if type is {@link MySQLCastType#DECIMAL }then {@link BigDecimalType}</li>
     *     <li>Else if type is {@link MySQLCastType#FLOAT }then {@link FloatType}</li>
     *     <li>Else if type is {@link MySQLCastType#DOUBLE }then {@link DoubleType}</li>
     *     <li>Else if type is {@link MySQLCastType#JSON }then {@link JsonType#TEXT}</li>
     * </ul>, it's up to option clause.
     *
     * @param jsonDoc  json expression
     *                 <ul>
     *                      <li>{@link Expression} instance</li>
     *                      <li>the instance that {@link JsonType#TEXT} can accept,here it will output literal. For example : {@code "[1,2]"} is equivalent to {@code SQLs.literal(JsonType.TEXT,"[1,2]") } </li>
     *                 </ul>
     * @param path     path expression
     *                 <ul>
     *                       <li>{@link Expression} instance</li>
     *                       <li>{@link String} instance. For example : {@code "$[*]"} is equivalent to {@code SQLs.literal(StringType.INSTANCE,"$[*]") }</li>
     *                 </ul>
     * @param consumer consumer that can accept option clause
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-search-functions.html#function_json-value">JSON_VALUE(json_doc, path)</a>
     */
    public static SimpleExpression jsonValue(final Object jsonDoc, final Object path,
                                             final Consumer<MySQLFunction._JsonValueReturningSpec> consumer) {
        return MySQLFunctionUtils.jsonValueFunc(jsonDoc, path, consumer);
    }




    /*-------------------below Functions That Modify JSON Values-------------------*/

    /**
     * <p>
     * The {@link MappingType} of function return type:the {@link MappingType} of jsonDoc
     * *
     *
     * @param jsonDoc     non-null
     * @param pathValList non-null,non-empty,size is even
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-modification-functions.html#function_json-array-append">JSON_ARRAY_APPEND(json_doc, path, val[, path, val] ...)</a>
     */
    public static SimpleExpression jsonArrayAppend(final Expression jsonDoc, final List<Expression> pathValList) {
        return _jsonPathValOperateFunc("JSON_ARRAY_APPEND", jsonDoc, pathValList);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:the {@link MappingType} of jsonDoc
     * *
     *
     * @param jsonDoc       non-null
     * @param firstPath     wrap to literal expression
     * @param firstValue    wrap to parameter expression
     * @param pathValuePair non-null, size must even,path wrap to literal expression,value wrap to parameter expression
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-modification-functions.html#function_json-array-append">JSON_ARRAY_APPEND(json_doc, path, val[, path, val] ...)</a>
     */
    public static SimpleExpression jsonArrayAppend(final Expression jsonDoc, String firstPath, Object firstValue
            , Object... pathValuePair) {
        return _jsonPathValOperateFunc("JSON_ARRAY_APPEND", jsonDoc, firstPath, firstValue, pathValuePair);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:the {@link MappingType} of jsonDoc
     * *
     *
     * @param jsonDoc       non-null,wrap to parameter expression
     * @param firstPath     non-null,wrap to literal expression
     * @param firstValue    non-null,wrap to parameter expression
     * @param pathValuePair non-null, size must even,path wrap to literal expression,value wrap to parameter expression
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-modification-functions.html#function_json-array-append">JSON_ARRAY_APPEND(json_doc, path, val[, path, val] ...)</a>
     */
    public static SimpleExpression jsonArrayAppend(final String jsonDoc, String firstPath, Object firstValue
            , Object... pathValuePair) {
        return _jsonPathValOperateFunc("JSON_ARRAY_APPEND", SQLs.param(StringType.INSTANCE, jsonDoc)
                , firstPath, firstValue, pathValuePair);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:the {@link MappingType} of jsonDoc
     * *
     *
     * @param jsonDoc     non-null
     * @param pathValList non-null,non-empty,size is even
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-modification-functions.html#function_json-array-insert">JSON_ARRAY_INSERT(json_doc, path, val[, path, val] ...)</a>
     */
    public static SimpleExpression jsonArrayInsert(final Expression jsonDoc, final List<Expression> pathValList) {
        return _jsonPathValOperateFunc("JSON_ARRAY_INSERT", jsonDoc, pathValList);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:the {@link MappingType} of jsonDoc
     * *
     *
     * @param jsonDoc       non-null
     * @param firstPath     wrap to literal expression
     * @param firstValue    wrap to parameter expression
     * @param pathValuePair non-null, size must even,path wrap to literal expression,value wrap to parameter expression
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-modification-functions.html#function_json-array-insert">JSON_ARRAY_INSERT(json_doc, path, val[, path, val] ...)</a>
     */
    public static SimpleExpression jsonArrayInsert(final Expression jsonDoc, String firstPath, Object firstValue
            , Object... pathValuePair) {
        return _jsonPathValOperateFunc("JSON_ARRAY_INSERT", jsonDoc, firstPath, firstValue, pathValuePair);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:the {@link MappingType} of jsonDoc
     * *
     *
     * @param jsonDoc       non-null,wrap to parameter expression
     * @param firstPath     non-null,wrap to literal expression
     * @param firstValue    non-null,wrap to parameter expression
     * @param pathValuePair non-null, size must even,path wrap to literal expression,value wrap to parameter expression
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-modification-functions.html#function_json-array-insert">JSON_ARRAY_INSERT(json_doc, path, val[, path, val] ...)</a>
     */
    public static SimpleExpression jsonArrayInsert(final String jsonDoc, String firstPath, Object firstValue
            , Object... pathValuePair) {
        return _jsonPathValOperateFunc("JSON_ARRAY_INSERT", SQLs.param(StringType.INSTANCE, jsonDoc)
                , firstPath, firstValue, pathValuePair);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:the {@link MappingType} of jsonDoc
     * *
     *
     * @param jsonDoc     non-null
     * @param pathValList non-null,non-empty,size is even
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-modification-functions.html#function_json-insert">JSON_INSERT(json_doc, path, val[, path, val] ...)</a>
     */
    public static SimpleExpression jsonInsert(final Expression jsonDoc, final List<Expression> pathValList) {
        return _jsonPathValOperateFunc("JSON_INSERT", jsonDoc, pathValList);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:the {@link MappingType} of jsonDoc
     * *
     *
     * @param jsonDoc       non-null
     * @param firstPath     wrap to literal expression
     * @param firstValue    wrap to parameter expression
     * @param pathValuePair non-null, size must even,path wrap to literal expression,value wrap to parameter expression
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-modification-functions.html#function_json-insert">JSON_INSERT(json_doc, path, val[, path, val] ...)</a>
     */
    public static SimpleExpression jsonInsert(final Expression jsonDoc, String firstPath, Object firstValue
            , Object... pathValuePair) {
        return _jsonPathValOperateFunc("JSON_INSERT", jsonDoc, firstPath, firstValue, pathValuePair);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:the {@link MappingType} of jsonDoc
     * *
     *
     * @param jsonDoc       non-null,wrap to parameter expression
     * @param firstPath     non-null,wrap to literal expression
     * @param firstValue    non-null,wrap to parameter expression
     * @param pathValuePair non-null, size must even,path wrap to literal expression,value wrap to parameter expression
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-modification-functions.html#function_json-insert">JSON_INSERT(json_doc, path, val[, path, val] ...)</a>
     */
    public static SimpleExpression jsonInsert(final String jsonDoc, String firstPath, Object firstValue
            , Object... pathValuePair) {
        return _jsonPathValOperateFunc("JSON_INSERT", SQLs.param(StringType.INSTANCE, jsonDoc)
                , firstPath, firstValue, pathValuePair);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:the {@link MappingType} of jsonDoc
     * *
     *
     * @param jsonDoc     non-null
     * @param pathValList non-null,non-empty,size is even
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-modification-functions.html#function_json-replace">JSON_REPLACE(json_doc, path, val[, path, val] ...)</a>
     */
    public static SimpleExpression jsonReplace(final Expression jsonDoc, final List<Expression> pathValList) {
        return _jsonPathValOperateFunc("JSON_REPLACE", jsonDoc, pathValList);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:the {@link MappingType} of jsonDoc
     * *
     *
     * @param jsonDoc       non-null
     * @param firstPath     wrap to literal expression
     * @param firstValue    wrap to parameter expression
     * @param pathValuePair non-null, size must even,path wrap to literal expression,value wrap to parameter expression
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-modification-functions.html#function_json-replace">JSON_REPLACE(json_doc, path, val[, path, val] ...)</a>
     */
    public static SimpleExpression jsonReplace(final Expression jsonDoc, String firstPath, Object firstValue
            , Object... pathValuePair) {
        return _jsonPathValOperateFunc("JSON_REPLACE", jsonDoc, firstPath, firstValue, pathValuePair);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:the {@link MappingType} of jsonDoc
     * *
     *
     * @param jsonDoc       non-null,wrap to parameter expression
     * @param firstPath     non-null,wrap to literal expression
     * @param firstValue    non-null,wrap to parameter expression
     * @param pathValuePair non-null, size must even,path wrap to literal expression,value wrap to parameter expression
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-modification-functions.html#function_json-replace">JSON_REPLACE(json_doc, path, val[, path, val] ...)</a>
     */
    public static SimpleExpression jsonReplace(final String jsonDoc, String firstPath, Object firstValue
            , Object... pathValuePair) {
        return _jsonPathValOperateFunc("JSON_REPLACE", SQLs.param(StringType.INSTANCE, jsonDoc)
                , firstPath, firstValue, pathValuePair);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:the {@link MappingType} of jsonDoc
     * *
     *
     * @param jsonDoc     non-null
     * @param pathValList non-null,non-empty,size is even
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-modification-functions.html#function_json-set">JSON_SET(json_doc, path, val[, path, val] ...)</a>
     */
    public static SimpleExpression jsonSet(final Expression jsonDoc, final List<Expression> pathValList) {
        return _jsonPathValOperateFunc("JSON_SET", jsonDoc, pathValList);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:the {@link MappingType} of jsonDoc
     * *
     *
     * @param jsonDoc       non-null
     * @param firstPath     wrap to literal expression
     * @param firstValue    wrap to parameter expression
     * @param pathValuePair non-null, size must even,path wrap to literal expression,value wrap to parameter expression
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-modification-functions.html#function_json-set">JSON_SET(json_doc, path, val[, path, val] ...)</a>
     */
    public static SimpleExpression jsonSet(final Expression jsonDoc, String firstPath, Object firstValue
            , Object... pathValuePair) {
        return _jsonPathValOperateFunc("JSON_SET", jsonDoc, firstPath, firstValue, pathValuePair);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:the {@link MappingType} of jsonDoc
     * *
     *
     * @param jsonDoc       non-null,wrap to parameter expression
     * @param firstPath     non-null,wrap to literal expression
     * @param firstValue    non-null,wrap to parameter expression
     * @param pathValuePair non-null, size must even,path wrap to literal expression,value wrap to parameter expression
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-modification-functions.html#function_json-set">JSON_SET(json_doc, path, val[, path, val] ...)</a>
     */
    public static SimpleExpression jsonSet(final String jsonDoc, String firstPath, Object firstValue
            , Object... pathValuePair) {
        return _jsonPathValOperateFunc("JSON_SET", SQLs.param(StringType.INSTANCE, jsonDoc)
                , firstPath, firstValue, pathValuePair);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:the {@link MappingType} of jsonDoc
     * * <p>
     * You should use {@link #jsonMergePreserve(Expression, Expression, Expression...)},if database is 8.0+.
     * *
     *
     * @param jsonDoc1 non-null
     * @param jsonDoc2 non-null,multi parameter(literal) {@link Expression} is allowed.
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-modification-functions.html#function_json-merge">JSON_MERGE(json_doc, json_doc[, json_doc] ...)</a>
     */
    public static SimpleExpression jsonMerge(final Expression jsonDoc1, final Expression jsonDoc2, Expression... jsonDocArray) {
        return _jsonMergeOperationFunction("JSON_MERGE", jsonDoc1, jsonDoc2, jsonDocArray);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:the {@link MappingType} of jsonDoc
     * * <p>
     * You should use {@link #jsonMergePreserve(List)},if database is 8.0+.
     * *
     *
     * @param jsonDocList non-null,non-empty,multi parameter(literal) {@link Expression} is allowed.
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-modification-functions.html#function_json-merge">JSON_MERGE(json_doc, json_doc[, json_doc] ...)</a>
     */
    public static SimpleExpression jsonMerge(final List<Expression> jsonDocList) {
        final String name = "JSON_MERGE";
        if (jsonDocList.size() < 2) {
            throw CriteriaUtils.funcArgError(name, jsonDocList);
        }
        return FunctionUtils.multiArgFunc(name, jsonDocList, jsonDocList.get(0).typeMeta());
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:the {@link MappingType} of jsonDoc
     * *
     *
     * @param jsonDoc1 non-null
     * @param jsonDoc2 non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-modification-functions.html#function_json-merge-preserve">JSON_MERGE_PRESERVE(json_doc, json_doc[, json_doc] ...)</a>
     */
    public static SimpleExpression jsonMergePreserve(final Expression jsonDoc1, final Expression jsonDoc2, Expression... jsonDocArray) {
        return _jsonMergeOperationFunction("JSON_MERGE_PRESERVE", jsonDoc1, jsonDoc2, jsonDocArray);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:the {@link MappingType} of jsonDoc
     * *
     *
     * @param jsonDocList non-null,non-empty,multi parameter(literal) {@link Expression} is allowed.
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-modification-functions.html#function_json-merge-preserve">JSON_MERGE_PRESERVE(json_doc, json_doc[, json_doc] ...)</a>
     */
    public static SimpleExpression jsonMergePreserve(final List<Expression> jsonDocList) {
        final String name = "JSON_MERGE_PRESERVE";
        if (jsonDocList.size() < 2) {
            throw CriteriaUtils.funcArgError(name, jsonDocList);
        }
        return FunctionUtils.multiArgFunc(name, jsonDocList, jsonDocList.get(0).typeMeta());
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:the {@link MappingType} of jsonDoc
     * *
     *
     * @param jsonDoc1 non-null
     * @param jsonDoc2 non-null,multi parameter(literal) {@link Expression} is allowed.
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-modification-functions.html#function_json-merge-patch">JSON_MERGE_PATCH(json_doc, json_doc[, json_doc] ...)</a>
     */
    public static SimpleExpression jsonMergePatch(final Expression jsonDoc1, final Expression jsonDoc2, Expression... jsonDocArray) {
        return _jsonMergeOperationFunction("JSON_MERGE_PATCH", jsonDoc1, jsonDoc2, jsonDocArray);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:the {@link MappingType} of jsonDoc
     * *
     *
     * @param jsonDocList non-null,non-empty,multi parameter(literal) {@link Expression} is allowed.
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-modification-functions.html#function_json-merge-patch">JSON_MERGE_PATCH(json_doc, json_doc[, json_doc] ...)</a>
     */
    public static SimpleExpression jsonMergePatch(final List<Expression> jsonDocList) {
        final String name = "JSON_MERGE_PATCH";
        if (jsonDocList.size() < 2) {
            throw CriteriaUtils.funcArgError(name, jsonDocList);
        }
        return FunctionUtils.multiArgFunc(name, jsonDocList, jsonDocList.get(0).typeMeta());
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:the {@link MappingType} of jsonDoc
     * *
     *
     * @param jsonDoc   non-null
     * @param firstPath non-null,multi parameter(literal) {@link Expression} is allowed.
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-modification-functions.html#function_json-remove">JSON_REMOVE(json_doc, path[, path] ...)</a>
     */
    public static SimpleExpression jsonRemove(final Expression jsonDoc, final Expression firstPath, Expression... paths) {
        return FunctionUtils.twoAndMultiArgFunc("JSON_REMOVE", jsonDoc, firstPath, Arrays.asList(paths), jsonDoc.typeMeta());
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:the {@link MappingType} of jsonDoc
     * *
     *
     * @param jsonDoc  non-null
     * @param pathList non-null,non-empty,multi parameter(literal) {@link Expression} is allowed.
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-modification-functions.html#function_json-remove">JSON_REMOVE(json_doc, path[, path] ...)</a>
     */
    public static SimpleExpression jsonRemove(final Expression jsonDoc, final List<Expression> pathList) {
        final String name = "JSON_REMOVE";
        if (pathList.size() == 0) {
            throw CriteriaUtils.funcArgError(name, pathList);
        }
        return FunctionUtils.oneAndMultiArgFunc(name, jsonDoc, pathList, jsonDoc.typeMeta());
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:the {@link MappingType} of jsonDoc
     * *
     *
     * @param jsonDoc   non-null
     * @param firstPath non-null,wrap to literal expression
     * @param paths     non-null, empty or paths,wrap to literal expressions
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-modification-functions.html#function_json-remove">JSON_REMOVE(json_doc, path[, path] ...)</a>
     */
    public static SimpleExpression jsonRemove(final Expression jsonDoc, final String firstPath, String... paths) {
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
     * *
     *
     * @param jsonDoc   non-null,wrap to parameter expression
     * @param firstPath non-null,wrap to literal expression
     * @param paths     non-null, empty or paths,wrap to literal expressions
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-modification-functions.html#function_json-remove">JSON_REMOVE(json_doc, path[, path] ...)</a>
     */
    public static SimpleExpression jsonRemove(final String jsonDoc, final String firstPath, String... paths) {
        return jsonRemove(SQLs.param(StringType.INSTANCE, jsonDoc), firstPath, paths);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link StringType}
     * *
     *
     * @param jsonVal non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-modification-functions.html#function_json-unquote">JSON_UNQUOTE(json_val)</a>
     */
    public static SimpleExpression jsonUnquote(final Expression jsonVal) {
        return FunctionUtils.oneArgFunc("JSON_UNQUOTE", jsonVal, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link IntegerType}
     * *
     *
     * @param jsonDoc non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-attribute-functions.html#function_json-depth">JSON_DEPTH(json_doc)</a>
     */
    public static SimpleExpression jsonDepth(final Expression jsonDoc) {
        return FunctionUtils.oneArgFunc("JSON_DEPTH", jsonDoc, IntegerType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link IntegerType}
     * *
     *
     * @param jsonDoc non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-attribute-functions.html#function_json-length">JSON_LENGTH(json_doc[, path])</a>
     */
    public static SimpleExpression jsonLength(final Expression jsonDoc) {
        return FunctionUtils.oneArgFunc("JSON_LENGTH", jsonDoc, IntegerType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link IntegerType}
     * *
     *
     * @param jsonDoc non-null
     * @param path    non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-attribute-functions.html#function_json-length">JSON_LENGTH(json_doc[, path])</a>
     */
    public static SimpleExpression jsonLength(final Expression jsonDoc, final Expression path) {
        return FunctionUtils.twoArgFunc("JSON_LENGTH", jsonDoc, path, IntegerType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link StringType}
     * *
     *
     * @param jsonVal non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-attribute-functions.html#function_json-type">JSON_TYPE(json_val)</a>
     */
    public static SimpleExpression jsonType(final Expression jsonVal) {
        //TODO 是否要 enum
        return FunctionUtils.oneArgFunc("JSON_TYPE", jsonVal, StringType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link BooleanType}
     * *
     *
     * @param val non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-attribute-functions.html#function_json-valid">JSON_VALID(val)</a>
     */
    public static SimplePredicate jsonValid(final Expression val) {
        return FunctionUtils.oneArgPredicateFunc("JSON_VALID", val);
    }

    /*-------------------below JSON Table Functions-------------------*/


    /**
     * <p>MySQL jsonTable function static method
     * <pre>
     *     <code><br/>
     *    &#64;Test
     *    public void jsonTableStatic(final ReactiveLocalSession session) {
     *        final String jsonDocument;
     *        jsonDocument = "[{\"a\":\"3\"},{\"a\":2},{\"b\":1},{\"a\":0},{\"a\":[1,2]}]";
     *
     *        final Select stmt;
     *        stmt = MySQLs.query()
     *                .select(s -> s.space("t", PERIOD, ASTERISK))
     *                .from(jsonTable(jsonDocument, "$[*]", COLUMNS, s -> s.space("rowId", FOR_ORDINALITY)
     *                                .comma("ac", TypeDefs.space(MySQLType.VARCHAR, 100), PATH, "$.a", o -> o.spaceDefault("111").onEmpty().spaceDefault("999").onError())
     *                                .comma("aj", MySQLType.JSON, PATH, "$.a", o -> o.spaceDefault("{\"x\":333}").onEmpty())
     *                                .comma("bx", MySQLType.INT, EXISTS, PATH, "$.b")
     *                        )
     *                )
     *                .as("t")
     *                .asQuery();
     *
     *        final Supplier&lt;Map&lt;String, Object>> constructor = HashMap::new;
     *
     *        session.queryObject(stmt, constructor)
     *                .doOnNext(System.out::println)
     *                .blockLast();
     *
     *    }
     *     </code>
     * </pre>
     *
     * @param expr     json expression
     *                 <ul>
     *                      <li>{@link Expression} instance</li>
     *                      <li>the instance that {@link JsonType#TEXT} can accept,here it will output literal. For example : {@code "[1,2]"} is equivalent to {@code SQLs.literal(JsonType.TEXT,"[1,2]") } </li>
     *                 </ul>
     * @param pathExp  path expression
     *                 <ul>
     *                       <li>{@link Expression} instance</li>
     *                       <li>{@link String} instance. For example : {@code "$[*]"} is equivalent to {@code SQLs.literal(StringType.INSTANCE,"$[*]") }</li>
     *                 </ul>
     * @param columns  see {@link MySQLs#COLUMNS}
     * @param consumer the consumer can accept jsonTable static columns clause.
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see #jsonTable(Object, Object, SQLs.WordColumns, SQLs.SymbolSpace, Consumer) dynamic method
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-table-functions.html#function_json-table">JSON_TABLE(expr, path COLUMNS (column_list) [AS] alias)</a>
     */
    public static _TabularFunction jsonTable(Object expr, Object pathExp, SQLs.WordColumns columns,
                                             Consumer<MySQLFunction._JsonTableColumnSpaceClause> consumer) {

        return MySQLFunctionUtils.jsonTable(expr, pathExp, consumer);
    }

    /**
     * <p>MySQL jsonTable function dynamic method
     * <pre>
     *     <code><br/>
     *    &#64;Test
     *    public void jsonTableDynamic(final ReactiveLocalSession session) {
     *        final String jsonDocument;
     *        jsonDocument = "[{\"a\":\"3\"},{\"a\":2},{\"b\":1},{\"a\":0},{\"a\":[1,2]}]";
     *
     *        final boolean needAjRow = false;
     *        final Select stmt;
     *        stmt = MySQLs.query()
     *                .select(s -> s.space("t", PERIOD, ASTERISK))
     *                .from(jsonTable(jsonDocument, "$[*]", COLUMNS, s -> {
     *                               s.accept("rowId", FOR_ORDINALITY)
     *                                .accept("ac", TypeDefs.space(MySQLType.VARCHAR, 100), PATH, "$.a", o -> o.spaceDefault("111").onEmpty().spaceDefault("999").onError());
     *                                if(needAjRow){
     *                                     s.accept("aj", MySQLType.JSON, PATH, "$.a", o -> o.spaceDefault("{\"x\":333}").onEmpty())
     *                                }
     *                                s.accept("bx", MySQLType.INT, EXISTS, PATH, "$.b")
     *                        })
     *                )
     *                .as("t")
     *                .asQuery();
     *
     *        final Supplier&lt;Map&lt;String, Object>> constructor = HashMap::new;
     *
     *        session.queryObject(stmt, constructor)
     *                .doOnNext(System.out::println)
     *                .blockLast();
     *
     *    }
     *     </code>
     * </pre>
     *
     * @param expr     json expression
     *                 <ul>
     *                      <li>{@link Expression} instance</li>
     *                      <li>the instance that {@link JsonType#TEXT} can accept,here it will output literal. For example : {@code "[1,2]"} is equivalent to {@code SQLs.literal(JsonType.TEXT,"[1,2]") } </li>
     *                 </ul>
     * @param pathExp  path expression
     *                 <ul>
     *                       <li>{@link Expression} instance</li>
     *                       <li>{@link String} instance. For example : {@code "$[*]"} is equivalent to {@code SQLs.literal(StringType.INSTANCE,"$[*]") }</li>
     *                 </ul>
     * @param columns  see {@link MySQLs#COLUMNS}
     * @param space    see {@link SQLs#SPACE} for distinguishing both static method and dynamic method.
     * @param consumer the consumer can accept jsonTable dynamic columns clause.
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see #jsonTable(Object, Object, SQLs.WordColumns, Consumer) static method
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-table-functions.html#function_json-table">JSON_TABLE(expr, path COLUMNS (column_list) [AS] alias)</a>
     */
    public static _TabularFunction jsonTable(Object expr, Object pathExp, SQLs.WordColumns columns,
                                             SQLs.SymbolSpace space,
                                             Consumer<MySQLFunction._JsonTableColumnConsumerClause> consumer) {

        return MySQLFunctionUtils.jsonTable(expr, pathExp, consumer);
    }


    /*-------------------below private method -------------------*/


    /**
     * @see #jsonArrayAppend(Expression, List)
     * @see #jsonArrayInsert(Expression, List)
     * @see #jsonInsert(Expression, List)
     */
    private static SimpleExpression _jsonPathValOperateFunc(final String name, final Expression jsonDoc
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
    private static SimpleExpression _jsonPathValOperateFunc(final String name, final Expression jsonDoc, final String firstPath
            , final Object firstValue, final Object... pathValuePair) {
        if (pathValuePair.length == 0 || (pathValuePair.length & 1) != 0) {
            throw CriteriaUtils.funcArgError(name, pathValuePair);
        }
        final List<ArmyExpression> argList = new ArrayList<>(3 + pathValuePair.length);
        argList.add((ArmyExpression) jsonDoc);
        argList.add((ArmyExpression) SQLs.literal(StringType.INSTANCE, firstPath));
        argList.add((ArmyExpression) SQLs.paramValue(firstValue));

        Object o;
        for (int i = 0; i < pathValuePair.length; i++) {
            o = pathValuePair[i];
            if ((i & 1) == 0) { //even
                if (o == null) {
                    argList.add((ArmyExpression) SQLs.param(SQLs._NullType.INSTANCE, null));
                } else {
                    argList.add((ArmyExpression) SQLs.paramValue(o));
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
    private static SimpleExpression _jsonMergerFunc(final String name, final List<Expression> jsonDocList) {
        final int size = jsonDocList.size();
        if (size < 2) {
            throw CriteriaUtils.funcArgError(name, jsonDocList);
        }
        final List<Object> argList = new ArrayList<>(size);
        int index = 0;
        for (Expression jsonDoc : jsonDocList) {
            if (index > 0) {
                argList.add(Functions.FuncWord.COMMA);
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
    private static SimpleExpression _jsonMergeOperationFunction(final String name, final Expression jsonDoc1
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
