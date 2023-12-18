package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.mysql.MySQLCastType;
import io.army.criteria.mysql.MySQLFunction;
import io.army.mapping.*;
import io.army.util._Collections;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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
     * @throws CriteriaException throw when argument error.
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
     * @throws CriteriaException throw when argument error.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-creation-functions.html#function_json-array">JSON_ARRAY([val[, val] ...])</a>
     */
    public static SimpleExpression jsonArray(final Object value) {
        final String name = "JSON_ARRAY";
        final SimpleExpression func;
        if (value instanceof List) {
            final List<Object> argList = _Collections.arrayList(((List<?>) value).size());
            FuncExpUtils.addTextExpList(argList, "element", (List<?>) value);
            func = LiteralFunctions.multiArgFunc(name, argList, JsonType.TEXT);
        } else if (!value.getClass().isArray()) {
            func = LiteralFunctions.oneArgFunc(name, value, JsonType.TEXT);
        } else if (value.getClass().getComponentType().isArray()) {
            throw CriteriaUtils.rejectMultiDimensionArray();
        } else {
            final List<Object> argList = _Collections.arrayList(((Object[]) value).length);
            FuncExpUtils.addAllTextExp(argList, "element", (Object[]) value);
            func = LiteralFunctions.multiArgFunc(name, argList, JsonType.TEXT);
        }
        return func;
    }

    /**
     * <p>JSON_ARRAY function variadic method.
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
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-creation-functions.html#function_json-array">JSON_ARRAY([val[, val] ...])</a>
     */
    public static SimpleExpression jsonArray(Object value1, Object value2, Object... variadic) {
        return LiteralFunctions.multiArgFunc("JSON_ARRAY", FuncExpUtils.twoAndVariadic(value1, value2, variadic), JsonType.TEXT);
    }


    /**
     * <p>JSON_ARRAY function static method.
     * <p>The {@link MappingType} of function return type: {@link JsonType#TEXT}
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-creation-functions.html#function_json-array">JSON_ARRAY([val[, val] ...])</a>
     */
    public static SimpleExpression jsonArray(Consumer<Clause._VariadicSpaceClause> consumer) {
        return LiteralFunctions.multiArgFunc("JSON_ARRAY", FuncExpUtils.variadicList(false, consumer), JsonType.TEXT);
    }

    /**
     * <p>JSON_ARRAY function dynamic method.
     * <p>The {@link MappingType} of function return type: {@link JsonType#TEXT}
     *
     * @param space    see {@link SQLs#SPACE},for distinguishing both static and dynamic method.
     * @param consumer non-null
     * @throws CriteriaException throw when argument error
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-creation-functions.html#function_json-array">JSON_ARRAY([val[, val] ...])</a>
     */
    public static SimpleExpression jsonArray(SQLs.SymbolSpace space, Consumer<Clause._VariadicConsumer> consumer) {
        return LiteralFunctions.multiArgFunc("JSON_ARRAY", FuncExpUtils.variadicList(false, consumer), JsonType.TEXT);
    }

    /**
     * <p>The {@link MappingType} of function return type: {@link JsonType#TEXT}
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-creation-functions.html#function_json-object">JSON_OBJECT([key, val[, key, val] ...])</a>
     */
    public static SimpleExpression jsonObject() {
        return LiteralFunctions.zeroArgFunc("JSON_OBJECT", JsonType.TEXT);
    }

    /**
     * <p>The {@link MappingType} of function return type: {@link JsonType#TEXT}
     *
     * @param expMap non-null
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-creation-functions.html#function_json-object">JSON_OBJECT([key, val[, key, val] ...])</a>
     */
    public static SimpleExpression jsonObject(final Map<String, ?> expMap) {
        return LiteralFunctions.jsonMapFunc("JSON_OBJECT", expMap, JsonType.TEXT);
    }


    /**
     * <p>jsonObject function static method.
     * <p>The {@link MappingType} of function return type: {@link JsonType#TEXT}
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-creation-functions.html#function_json-object">JSON_OBJECT([key, val[, key, val] ...])</a>
     */
    public static SimpleExpression jsonObject(final Consumer<Clause._PairVariadicSpaceClause> consumer) {
        return LiteralFunctions.multiArgFunc("JSON_OBJECT", FuncExpUtils.pariVariadicList(false, consumer), JsonType.TEXT);
    }

    /**
     * <p>jsonObject function dynamic method.
     * <p>The {@link MappingType} of function return type: {@link JsonType#TEXT}
     *
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
     *                 <li>{@link String} literal</li>
     *               </ul>
     * @throws CriteriaException throw when argument error
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
     * @param target    json expression
     *                  <ul>
     *                       <li>{@link Expression} instance</li>
     *                       <li>the instance that  can be accepted by {@link JsonType#TEXT},here it will output literal. For example : {@code "[1,2]"} is equivalent to {@code SQLs.literal(JsonType.TEXT,"[1,2]") } </li>
     *                  </ul>
     * @param candidate json expression
     *                  <ul>
     *                       <li>{@link Expression} instance</li>
     *                       <li>the instance that  can be accepted by {@link JsonType#TEXT},here it will output literal. For example : {@code "[1,2]"} is equivalent to {@code SQLs.literal(JsonType.TEXT,"[1,2]") } </li>
     *                  </ul>
     * @throws CriteriaException throw when argument error
     * @see #jsonContains(Object, Object, Object)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-search-functions.html#function_json-contains">JSON_CONTAINS(target, candidate[, path])</a>
     */
    public static SimplePredicate jsonContains(final Object target, final Object candidate) {
        return LiteralFunctions.twoArgPredicate("JSON_CONTAINS", FuncExpUtils.jsonDocExp(target),
                FuncExpUtils.jsonDocExp(candidate)
        );
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link BooleanType}
     * *
     *
     * @param target    json expression
     *                  <ul>
     *                       <li>{@link Expression} instance</li>
     *                       <li>the instance that  can be accepted by {@link JsonType#TEXT},here it will output literal. For example : {@code "[1,2]"} is equivalent to {@code SQLs.literal(JsonType.TEXT,"[1,2]") } </li>
     *                  </ul>
     * @param candidate json expression
     *                  <ul>
     *                       <li>{@link Expression} instance</li>
     *                       <li>the instance that  can be accepted by {@link JsonType#TEXT},here it will output literal. For example : {@code "[1,2]"} is equivalent to {@code SQLs.literal(JsonType.TEXT,"[1,2]") } </li>
     *                  </ul>
     * @param path      path expression
     *                  <ul>
     *                        <li>{@link Expression} instance</li>
     *                        <li>{@link String} literal. For example : {@code "$[*]"} is equivalent to {@code SQLs.literal(StringType.INSTANCE,"$[*]") }</li>
     *                  </ul>
     * @throws CriteriaException throw when argument error
     * @see #jsonContains(Object, Object)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-search-functions.html#function_json-contains">JSON_CONTAINS(target, candidate[, path])</a>
     */
    public static SimplePredicate jsonContains(final Object target, final Object candidate, final Object path) {
        FuncExpUtils.assertPathExp(path);
        return LiteralFunctions.threeArgPredicate("JSON_CONTAINS", FuncExpUtils.jsonDocExp(target),
                FuncExpUtils.jsonDocExp(candidate), path
        );
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link BooleanType}
     * *
     *
     * @param jsonDoc  json expression
     *                 <ul>
     *                      <li>{@link Expression} instance</li>
     *                      <li>the instance that  can be accepted by {@link JsonType#TEXT},here it will output literal. For example : {@code "[1,2]"} is equivalent to {@code SQLs.literal(JsonType.TEXT,"[1,2]") } </li>
     *                 </ul>
     * @param oneOrAll one of following <:ul>
     *                 <li>literal 'one' or 'all'</li>
     *                 <li>{@link Expression}</li>
     *                 </ul>
     * @param paths    non-null, one of following :
     *                 <ul>
     *                   <li>{@link Expression} instance</li>
     *                   <li>{@link List} of {@link Expression} and {@link String}</li>
     *                   <li>one dimension array  of {@link Expression} and {@link String}</li>
     *                   <li>{@link String} literal</li>
     *                 </ul>
     * @throws CriteriaException throw when argument error
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-search-functions.html#function_json-contains-path">JSON_CONTAINS_PATH(json_doc, one_or_all, path[, path] ...)</a>
     */
    public static SimplePredicate jsonContainsPath(Object jsonDoc, final Object oneOrAll, final Object paths) {
        FuncExpUtils.assertTextExp(oneOrAll);
        jsonDoc = FuncExpUtils.jsonDocExp(jsonDoc);

        final String name = "JSON_CONTAINS_PATH";
        final SimplePredicate func;
        if (paths instanceof List) {
            final List<Object> argList = _Collections.arrayList(2 + ((List<?>) paths).size());
            argList.add(jsonDoc);
            argList.add(oneOrAll);
            FuncExpUtils.addTextExpList(argList, "path", (List<?>) paths);
            func = LiteralFunctions.multiArgPredicate(name, argList);
        } else if (!paths.getClass().isArray()) {
            FuncExpUtils.assertPathExp(paths);
            func = LiteralFunctions.threeArgPredicate(name, jsonDoc, oneOrAll, paths);
        } else if (paths.getClass().getComponentType().isArray()) {
            throw CriteriaUtils.rejectMultiDimensionArray();
        } else {
            final List<Object> argList = _Collections.arrayList(2 + ((Object[]) paths).length);
            argList.add(jsonDoc);
            argList.add(oneOrAll);
            FuncExpUtils.addAllTextExp(argList, "path", (Object[]) paths);
            func = LiteralFunctions.multiArgPredicate(name, argList);
        }
        return func;
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link BooleanType}
     *
     * @param jsonDoc      json expression
     *                     <ul>
     *                          <li>{@link Expression} instance</li>
     *                          <li>the instance that  can be accepted by {@link JsonType#TEXT},here it will output literal. For example : {@code "[1,2]"} is equivalent to {@code SQLs.literal(JsonType.TEXT,"[1,2]") } </li>
     *                     </ul>
     * @param oneOrAll     one of following <:ul>
     *                     <li>literal 'one' or 'all'</li>
     *                     <li>{@link Expression}</li>
     *                     </ul>
     * @param path1        non-null, one of following :
     *                     <ul>
     *                       <li>{@link Expression} instance</li>
     *                       <li>{@link String} literal</li>
     *                     </ul>
     * @param path2        non-null, one of following :
     *                     <ul>
     *                       <li>{@link Expression} instance</li>
     *                       <li>{@link String} literal</li>
     *                     </ul>
     * @param pathVariadic non-null,each of pathVariadic is  one of following :
     *                     <ul>
     *                       <li>{@link Expression} instance</li>
     *                       <li>{@link String} literal</li>
     *                     </ul>
     * @throws CriteriaException throw when argument error
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-search-functions.html#function_json-contains-path">JSON_CONTAINS_PATH(json_doc, one_or_all, path[, path] ...)</a>
     */
    public static SimplePredicate jsonContainsPath(Object jsonDoc, final Object oneOrAll, Object path1, Object path2, Object... pathVariadic) {
        FuncExpUtils.assertTextExp(oneOrAll);
        FuncExpUtils.assertPathExp(path1);
        FuncExpUtils.assertPathExp(path2);

        final List<Object> argList = _Collections.arrayList(4 + pathVariadic.length);

        argList.add(FuncExpUtils.jsonDocExp(jsonDoc));
        argList.add(oneOrAll);
        argList.add(path1);
        argList.add(path2);

        FuncExpUtils.addAllTextExp(argList, "path", pathVariadic);

        return LiteralFunctions.multiArgPredicate("JSON_CONTAINS_PATH", argList);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonType#TEXT}
     *
     * @param jsonDoc json expression
     *                <ul>
     *                     <li>{@link Expression} instance</li>
     *                     <li>the instance that  can be accepted by {@link JsonType#TEXT},here it will output literal. For example : {@code "[1,2]"} is equivalent to {@code SQLs.literal(JsonType.TEXT,"[1,2]") } </li>
     *                </ul>
     * @param paths   non-null, one of following :
     *                <ul>
     *                  <li>{@link Expression} instance</li>
     *                  <li>{@link List} of {@link Expression} and {@link String}</li>
     *                  <li>one dimension array  of {@link Expression} and {@link String}</li>
     *                  <li>{@link String} literal</li>
     *                </ul>
     * @throws CriteriaException throw when argument error
     * @see #jsonExtract(Object, Object, Object, Object...)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-search-functions.html#function_json-extract">JSON_EXTRACT(json_doc, path[, path] ...)</a>
     */
    public static SimpleExpression jsonExtract(Object jsonDoc, final Object paths) {
        jsonDoc = FuncExpUtils.jsonDocExp(jsonDoc);

        final String name = "JSON_EXTRACT";
        final SimpleExpression func;
        if (paths instanceof List) {
            final List<Object> argList = _Collections.arrayList(1 + ((List<?>) paths).size());
            argList.add(jsonDoc);
            FuncExpUtils.addTextExpList(argList, "path", (List<?>) paths);
            func = LiteralFunctions.multiArgFunc(name, argList, JsonType.TEXT);
        } else if (!paths.getClass().isArray()) {
            FuncExpUtils.assertPathExp(paths);
            func = LiteralFunctions.twoArgFunc(name, jsonDoc, paths, JsonType.TEXT);
        } else if (paths.getClass().getComponentType().isArray()) {
            throw CriteriaUtils.rejectMultiDimensionArray();
        } else {
            final List<Object> argList = _Collections.arrayList(1 + ((Object[]) paths).length);
            argList.add(jsonDoc);
            FuncExpUtils.addAllTextExp(argList, "path", (Object[]) paths);
            func = LiteralFunctions.multiArgFunc(name, argList, JsonType.TEXT);
        }
        return func;
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonType#TEXT}
     *
     * @param jsonDoc      json expression
     *                     <ul>
     *                          <li>{@link Expression} instance</li>
     *                          <li>the instance that  can be accepted by {@link JsonType#TEXT},here it will output literal. For example : {@code "[1,2]"} is equivalent to {@code SQLs.literal(JsonType.TEXT,"[1,2]") } </li>
     *                     </ul>
     * @param path1        non-null, one of following :
     *                     <ul>
     *                       <li>{@link Expression} instance</li>
     *                       <li>{@link String} literal</li>
     *                     </ul>
     * @param path2        non-null, one of following :
     *                     <ul>
     *                       <li>{@link Expression} instance</li>
     *                       <li>{@link String} literal</li>
     *                     </ul>
     * @param pathVariadic non-null,each of pathVariadic is  one of following :
     *                     <ul>
     *                       <li>{@link Expression} instance</li>
     *                       <li>{@link String} literal</li>
     *                     </ul>
     * @throws CriteriaException throw when argument error
     * @see #jsonExtract(Object, Object)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-creation-functions.html#function_json-extract">JSON_EXTRACT(json_doc, path[, path] ...)</a>
     */
    public static SimpleExpression jsonExtract(Object jsonDoc, Object path1, Object path2, Object... pathVariadic) {
        FuncExpUtils.assertPathExp(path1);
        FuncExpUtils.assertPathExp(path2);

        final List<Object> argList = _Collections.arrayList(3 + pathVariadic.length);

        argList.add(FuncExpUtils.jsonDocExp(jsonDoc));
        argList.add(path1);
        argList.add(path2);

        FuncExpUtils.addAllTextExp(argList, "path", pathVariadic);

        return LiteralFunctions.multiArgFunc("JSON_EXTRACT", argList, JsonType.TEXT);
    }


    /**
     * <p>The {@link MappingType} of function return type: {@link JsonType#TEXT}
     *
     * @param jsonDoc json expression
     *                <ul>
     *                     <li>{@link Expression} instance</li>
     *                     <li>the instance that  can be accepted by {@link JsonType#TEXT},here it will output literal. For example : {@code "[1,2]"} is equivalent to {@code SQLs.literal(JsonType.TEXT,"[1,2]") } </li>
     *                </ul>
     * @throws CriteriaException throw when argument error
     * @see #jsonKeys(Object, Object)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-search-functions.html#function_json-keys">JSON_KEYS(json_doc[, path])</a>
     */
    public static SimpleExpression jsonKeys(final Object jsonDoc) {
        return LiteralFunctions.oneArgFunc("JSON_KEYS", FuncExpUtils.jsonDocExp(jsonDoc), JsonType.TEXT);
    }

    /**
     * <p>The {@link MappingType} of function return type: {@link JsonType#TEXT}
     *
     * @param jsonDoc json expression
     *                <ul>
     *                     <li>{@link Expression} instance</li>
     *                     <li>the instance that  can be accepted by {@link JsonType#TEXT},here it will output literal. For example : {@code "[1,2]"} is equivalent to {@code SQLs.literal(JsonType.TEXT,"[1,2]") } </li>
     *                </ul>
     * @param path    non-null, one of following :
     *                <ul>
     *                  <li>{@link Expression} instance</li>
     *                  <li>{@link String} literal</li>
     *                </ul>
     * @throws CriteriaException throw when argument error
     * @see #jsonKeys(Object)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-search-functions.html#function_json-keys">JSON_KEYS(json_doc[, path])</a>
     */
    public static SimpleExpression jsonKeys(final Object jsonDoc, final Object path) {
        FuncExpUtils.assertPathExp(path);
        return LiteralFunctions.twoArgFunc("JSON_KEYS", FuncExpUtils.jsonDocExp(jsonDoc), path, JsonType.TEXT);
    }


    /**
     * <p>The {@link MappingType} of function return type: {@link BooleanType}
     *
     * @param jsonDoc1 json expression
     *                 <ul>
     *                      <li>{@link Expression} instance</li>
     *                      <li>the instance that  can be accepted by {@link JsonType#TEXT},here it will output literal. For example : {@code "[1,2]"} is equivalent to {@code SQLs.literal(JsonType.TEXT,"[1,2]") } </li>
     *                 </ul>
     * @param jsonDoc2 json expression
     *                 <ul>
     *                      <li>{@link Expression} instance</li>
     *                      <li>the instance that  can be accepted by {@link JsonType#TEXT},here it will output literal. For example : {@code "[1,2]"} is equivalent to {@code SQLs.literal(JsonType.TEXT,"[1,2]") } </li>
     *                 </ul>
     * @throws CriteriaException throw when argument error
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-search-functions.html#function_json-overlaps">JSON_OVERLAPS(json_doc1, json_doc2)</a>
     */
    public static SimplePredicate jsonOverlaps(final Object jsonDoc1, final Object jsonDoc2) {
        return LiteralFunctions.twoArgPredicate("JSON_OVERLAPS", FuncExpUtils.jsonDocExp(jsonDoc1),
                FuncExpUtils.jsonDocExp(jsonDoc2)
        );
    }


    /**
     * <p>The {@link MappingType} of function return type: {@link JsonType#TEXT}
     *
     * @param jsonDoc   json expression
     *                  <ul>
     *                       <li>{@link Expression} instance</li>
     *                       <li>the instance that  can be accepted by {@link JsonType#TEXT},here it will output literal. For example : {@code "[1,2]"} is equivalent to {@code SQLs.literal(JsonType.TEXT,"[1,2]") } </li>
     *                  </ul>
     * @param oneOrAll  one of following <:ul>
     *                  <li>literal 'one' or 'all'</li>
     *                  <li>{@link Expression}</li>
     *                  </ul>
     * @param searchStr non-null, one of following :
     *                  <ul>
     *                    <li>{@link Expression} instance</li>
     *                    <li>{@link String} literal</li>
     *                  </ul>
     * @throws CriteriaException throw when argument error
     * @see #jsonSearch(Object, Object, Object, Object)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-search-functions.html#function_json-search">JSON_SEARCH(json_doc, one_or_all, search_str[, escape_char[, path] ...])</a>
     */
    public static SimpleExpression jsonSearch(final Object jsonDoc, final Object oneOrAll, final Object searchStr) {
        FuncExpUtils.assertTextExp(oneOrAll);
        FuncExpUtils.assertTextExp(searchStr);
        return LiteralFunctions.threeArgFunc("JSON_SEARCH", FuncExpUtils.jsonDocExp(jsonDoc),
                oneOrAll, searchStr, JsonType.TEXT);
    }

    /**
     * <p>The {@link MappingType} of function return type: {@link JsonType#TEXT}
     *
     * @param jsonDoc    json expression
     *                   <ul>
     *                        <li>{@link Expression} instance</li>
     *                        <li>the instance that  can be accepted by {@link JsonType#TEXT},here it will output literal. For example : {@code "[1,2]"} is equivalent to {@code SQLs.literal(JsonType.TEXT,"[1,2]") } </li>
     *                   </ul>
     * @param oneOrAll   one of following <:ul>
     *                   <li>literal 'one' or 'all'</li>
     *                   <li>{@link Expression}</li>
     *                   </ul>
     * @param searchStr  non-null, one of following :
     *                   <ul>
     *                     <li>{@link Expression} instance</li>
     *                     <li>{@link String} literal</li>
     *                   </ul>
     * @param escapeChar nullable, one of following :
     *                   <ul>
     *                     <li>null</li>
     *                     <li>{@link Expression} instance</li>
     *                     <li>{@link String} literal</li>
     *                   </ul>
     * @throws CriteriaException throw when argument error
     * @see #jsonSearch(Object, Object, Object)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-search-functions.html#function_json-search">JSON_SEARCH(json_doc, one_or_all, search_str[, escape_char[, path] ...])</a>
     */
    public static SimpleExpression jsonSearch(final Object jsonDoc, Object oneOrAll, Object searchStr, @Nullable Object escapeChar) {
        return jsonSearch(jsonDoc, oneOrAll, searchStr, escapeChar, Collections.emptyList());
    }

    /**
     * <p>The {@link MappingType} of function return type: {@link JsonType#TEXT}
     *
     * @param jsonDoc    json expression
     *                   <ul>
     *                        <li>{@link Expression} instance</li>
     *                        <li>the instance that  can be accepted by {@link JsonType#TEXT},here it will output literal. For example : {@code "[1,2]"} is equivalent to {@code SQLs.literal(JsonType.TEXT,"[1,2]") } </li>
     *                   </ul>
     * @param oneOrAll   one of following <:ul>
     *                   <li>literal 'one' or 'all'</li>
     *                   <li>{@link Expression}</li>
     *                   </ul>
     * @param searchStr  non-null, one of following :
     *                   <ul>
     *                     <li>{@link Expression} instance</li>
     *                     <li>{@link String} literal</li>
     *                   </ul>
     * @param escapeChar nullable, one of following :
     *                   <ul>
     *                     <li>null</li>
     *                     <li>{@link Expression} instance</li>
     *                     <li>{@link String} literal</li>
     *                   </ul>
     * @param paths      non-null, one of following :
     *                   <ul>
     *                     <li>{@link Expression} instance</li>
     *                     <li>{@link List} of {@link Expression} and {@link String}</li>
     *                     <li>one dimension array  of {@link Expression} and {@link String}</li>
     *                     <li>{@link String} literal</li>
     *                   </ul>
     * @throws CriteriaException throw when argument error
     * @see #jsonSearch(Object, Object, Object)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-search-functions.html#function_json-search">JSON_SEARCH(json_doc, one_or_all, search_str[, escape_char[, path] ...])</a>
     */
    public static SimpleExpression jsonSearch(final Object jsonDoc, Object oneOrAll, Object searchStr,
                                              @Nullable Object escapeChar, final Object paths) {
        FuncExpUtils.assertTextExp(oneOrAll);
        FuncExpUtils.assertTextExp(searchStr);
        if (escapeChar != null) {
            FuncExpUtils.assertTextExp(escapeChar);
        }

        final List<Object> argList;
        if (paths instanceof List) {
            argList = _Collections.arrayList(4 + ((List<?>) paths).size());
        } else if (paths instanceof Object[]) {
            argList = _Collections.arrayList(4 + ((Object[]) paths).length);
        } else {
            argList = _Collections.arrayList(4);
        }

        argList.add(FuncExpUtils.jsonDocExp(jsonDoc));
        argList.add(oneOrAll);
        argList.add(searchStr);
        argList.add(escapeChar);

        final SimpleExpression func;
        if (paths instanceof List) {
            FuncExpUtils.addTextExpList(argList, "path", (List<?>) paths);
        } else if (!paths.getClass().isArray()) {
            FuncExpUtils.assertPathExp(paths);
            argList.add(paths);
        } else if (paths.getClass().getComponentType().isArray()) {
            throw CriteriaUtils.rejectMultiDimensionArray();
        } else {
            assert paths instanceof Object[];
            FuncExpUtils.addAllTextExp(argList, "path", (Object[]) paths);
        }
        return LiteralFunctions.multiArgFunc("JSON_SEARCH", argList, JsonType.TEXT);
    }

    /**
     * <p>The {@link MappingType} of function return type: {@link JsonType#TEXT}
     *
     * @param jsonDoc      json expression
     *                     <ul>
     *                          <li>{@link Expression} instance</li>
     *                          <li>the instance that  can be accepted by {@link JsonType#TEXT},here it will output literal. For example : {@code "[1,2]"} is equivalent to {@code SQLs.literal(JsonType.TEXT,"[1,2]") } </li>
     *                     </ul>
     * @param oneOrAll     one of following <:ul>
     *                     <li>literal 'one' or 'all'</li>
     *                     <li>{@link Expression}</li>
     *                     </ul>
     * @param searchStr    non-null, one of following :
     *                     <ul>
     *                       <li>{@link Expression} instance</li>
     *                       <li>{@link String} literal</li>
     *                     </ul>
     * @param escapeChar   nullable, one of following :
     *                     <ul>
     *                       <li>null</li>
     *                       <li>{@link Expression} instance</li>
     *                       <li>{@link String} literal</li>
     *                     </ul>
     * @param path1        non-null, one of following :
     *                     <ul>
     *                       <li>{@link Expression} instance</li>
     *                       <li>{@link String} literal</li>
     *                     </ul>
     * @param path2        non-null, one of following :
     *                     <ul>
     *                       <li>{@link Expression} instance</li>
     *                       <li>{@link String} literal</li>
     *                     </ul>
     * @param pathVariadic non-null,each of pathVariadic is  one of following :
     *                     <ul>
     *                       <li>{@link Expression} instance</li>
     *                       <li>{@link String} literal</li>
     *                     </ul>
     * @throws CriteriaException throw when argument error
     * @see #jsonSearch(Object, Object, Object)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-search-functions.html#function_json-search">JSON_SEARCH(json_doc, one_or_all, search_str[, escape_char[, path] ...])</a>
     */
    public static SimpleExpression jsonSearch(final Object jsonDoc, Object oneOrAll, Object searchStr,
                                              @Nullable Object escapeChar, Object path1, Object path2,
                                              Object... pathVariadic) {
        FuncExpUtils.assertTextExp(oneOrAll);
        FuncExpUtils.assertTextExp(searchStr);
        if (escapeChar != null) {
            FuncExpUtils.assertTextExp(escapeChar);
        }
        FuncExpUtils.assertPathExp(path1);
        FuncExpUtils.assertPathExp(path2);

        final List<Object> argList = _Collections.arrayList(6 + pathVariadic.length);

        argList.add(FuncExpUtils.jsonDocExp(jsonDoc));
        argList.add(oneOrAll);
        argList.add(searchStr);
        argList.add(escapeChar);

        argList.add(path1);
        argList.add(path2);

        FuncExpUtils.addAllTextExp(argList, "path", pathVariadic);

        return LiteralFunctions.multiArgFunc("JSON_SEARCH", argList, JsonType.TEXT);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonType#TEXT}
     * * <p>
     *
     * @param jsonDoc json expression
     *                <ul>
     *                     <li>{@link Expression} instance</li>
     *                     <li>the instance that  can be accepted by {@link JsonType#TEXT},here it will output literal. For example : {@code "[1,2]"} is equivalent to {@code SQLs.literal(JsonType.TEXT,"[1,2]") } </li>
     *                </ul>
     * @param path    path expression
     *                <ul>
     *                      <li>{@link Expression} instance</li>
     *                      <li>{@link String} instance. For example : {@code "$[*]"} is equivalent to {@code SQLs.literal(StringType.INSTANCE,"$[*]") }</li>
     *                </ul>
     * @throws CriteriaException throw when argument error
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-search-functions.html#function_json-value">JSON_VALUE(json_doc, path)</a>
     */
    public static SimpleExpression jsonValue(final Object jsonDoc, final Object path) {
        FuncExpUtils.assertPathExp(path);
        return LiteralFunctions.twoArgFunc("JSON_VALUE", FuncExpUtils.jsonDocExp(jsonDoc), path, JsonType.TEXT);
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
     * </ul>, it's up to RETURNING clause.
     *
     * @param jsonDoc  json expression
     *                 <ul>
     *                      <li>{@link Expression} instance</li>
     *                      <li>the instance that  can be accepted by {@link JsonType#TEXT},here it will output literal. For example : {@code "[1,2]"} is equivalent to {@code SQLs.literal(JsonType.TEXT,"[1,2]") } </li>
     *                 </ul>
     * @param path     path expression
     *                 <ul>
     *                       <li>{@link Expression} instance</li>
     *                       <li>{@link String} instance. For example : {@code "$[*]"} is equivalent to {@code SQLs.literal(StringType.INSTANCE,"$[*]") }</li>
     *                 </ul>
     * @param consumer consumer that can accept option clause
     * @throws CriteriaException throw when
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-search-functions.html#function_json-value">JSON_VALUE(json_doc, path)</a>
     */
    public static SimpleExpression jsonValue(final Object jsonDoc, final Object path,
                                             final Consumer<MySQLFunction._JsonValueReturningSpec> consumer) {
        return MySQLFunctionUtils.jsonValueFunc(jsonDoc, path, consumer);
    }




    /*-------------------below Functions That Modify JSON Values-------------------*/

    /**
     * <p>JSON_ARRAY_APPEND function static method
     * <p>The {@link MappingType} of function return type:the {@link MappingType} of jsonDoc
     *
     * @param jsonDoc  json expression
     *                 <ul>
     *                      <li>{@link Expression} instance</li>
     *                      <li>the instance that  can be accepted by {@link JsonType#TEXT},here it will output literal. For example : {@code "[1,2]"} is equivalent to {@code SQLs.literal(JsonType.TEXT,"[1,2]") } </li>
     *                 </ul>
     * @param consumer non-null
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-modification-functions.html#function_json-array-append">JSON_ARRAY_APPEND(json_doc, path, val[, path, val] ...)</a>
     */
    public static SimpleExpression jsonArrayAppend(final Object jsonDoc, Consumer<Clause._PairVariadicSpaceClause> consumer) {
        return _jsonDocAndPairVariadic("JSON_ARRAY_APPEND", jsonDoc, consumer);
    }

    /**
     * <p>JSON_ARRAY_APPEND function dynamic method
     * <p>The {@link MappingType} of function return type:the {@link MappingType} of jsonDoc
     *
     * @param jsonDoc  json expression
     *                 <ul>
     *                      <li>{@link Expression} instance</li>
     *                      <li>the instance that  can be accepted by {@link JsonType#TEXT},here it will output literal. For example : {@code "[1,2]"} is equivalent to {@code SQLs.literal(JsonType.TEXT,"[1,2]") } </li>
     *                 </ul>
     * @param space    see {@link SQLs#SPACE} ,for distinguishing static and dynamic method.
     * @param consumer non-null
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-modification-functions.html#function_json-array-append">JSON_ARRAY_APPEND(json_doc, path, val[, path, val] ...)</a>
     */
    public static SimpleExpression jsonArrayAppend(final Object jsonDoc, SQLs.SymbolSpace space, Consumer<Clause._PairVariadicConsumerClause> consumer) {
        return _jsonDocAndPairVariadic("JSON_ARRAY_APPEND", jsonDoc, consumer);
    }


    /**
     * <p>JSON_ARRAY_INSERT function static method
     * <p>The {@link MappingType} of function return type:the {@link MappingType} of jsonDoc
     *
     * @param jsonDoc  json expression
     *                 <ul>
     *                      <li>{@link Expression} instance</li>
     *                      <li>the instance that  can be accepted by {@link JsonType#TEXT},here it will output literal. For example : {@code "[1,2]"} is equivalent to {@code SQLs.literal(JsonType.TEXT,"[1,2]") } </li>
     *                 </ul>
     * @param consumer non-null
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-modification-functions.html#function_json-array-insert">JSON_ARRAY_INSERT(json_doc, path, val[, path, val] ...)</a>
     */
    public static SimpleExpression jsonArrayInsert(final Object jsonDoc, Consumer<Clause._PairVariadicSpaceClause> consumer) {
        return _jsonDocAndPairVariadic("JSON_ARRAY_INSERT", jsonDoc, consumer);
    }


    /**
     * <p>JSON_ARRAY_INSERT function dynamic method
     * <p>The {@link MappingType} of function return type:the {@link MappingType} of jsonDoc
     *
     * @param jsonDoc  json expression
     *                 <ul>
     *                      <li>{@link Expression} instance</li>
     *                      <li>the instance that  can be accepted by {@link JsonType#TEXT},here it will output literal. For example : {@code "[1,2]"} is equivalent to {@code SQLs.literal(JsonType.TEXT,"[1,2]") } </li>
     *                 </ul>
     * @param space    see {@link SQLs#SPACE} ,for distinguishing static and dynamic method.
     * @param consumer non-null
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-modification-functions.html#function_json-array-insert">JSON_ARRAY_INSERT(json_doc, path, val[, path, val] ...)</a>
     */
    public static SimpleExpression jsonArrayInsert(final Object jsonDoc, SQLs.SymbolSpace space, Consumer<Clause._PairVariadicConsumerClause> consumer) {
        return _jsonDocAndPairVariadic("JSON_ARRAY_INSERT", jsonDoc, consumer);
    }


    /**
     * <p>JSON_INSERT function static method
     * <p>The {@link MappingType} of function return type:the {@link MappingType} of jsonDoc
     *
     * @param jsonDoc  json expression
     *                 <ul>
     *                      <li>{@link Expression} instance</li>
     *                      <li>the instance that  can be accepted by {@link JsonType#TEXT},here it will output literal. For example : {@code "[1,2]"} is equivalent to {@code SQLs.literal(JsonType.TEXT,"[1,2]") } </li>
     *                 </ul>
     * @param consumer non-null
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-modification-functions.html#function_json-insert">JSON_INSERT(json_doc, path, val[, path, val] ...)</a>
     */
    public static SimpleExpression jsonInsert(final Object jsonDoc, Consumer<Clause._PairVariadicSpaceClause> consumer) {
        return _jsonDocAndPairVariadic("JSON_INSERT", jsonDoc, consumer);
    }


    /**
     * <p>JSON_INSERT function dynamic method
     * <p>The {@link MappingType} of function return type:the {@link MappingType} of jsonDoc
     *
     * @param jsonDoc  json expression
     *                 <ul>
     *                      <li>{@link Expression} instance</li>
     *                      <li>the instance that  can be accepted by {@link JsonType#TEXT},here it will output literal. For example : {@code "[1,2]"} is equivalent to {@code SQLs.literal(JsonType.TEXT,"[1,2]") } </li>
     *                 </ul>
     * @param space    see {@link SQLs#SPACE} ,for distinguishing static and dynamic method.
     * @param consumer non-null
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-modification-functions.html#function_json-insert">JSON_INSERT(json_doc, path, val[, path, val] ...)</a>
     */
    public static SimpleExpression jsonInsert(final Object jsonDoc, SQLs.SymbolSpace space, Consumer<Clause._PairVariadicConsumerClause> consumer) {
        return _jsonDocAndPairVariadic("JSON_INSERT", jsonDoc, consumer);
    }


    /**
     * <p>JSON_REPLACE function static method
     * <p>The {@link MappingType} of function return type:the {@link MappingType} of jsonDoc
     *
     * @param jsonDoc  json expression
     *                 <ul>
     *                      <li>{@link Expression} instance</li>
     *                      <li>the instance that  can be accepted by {@link JsonType#TEXT},here it will output literal. For example : {@code "[1,2]"} is equivalent to {@code SQLs.literal(JsonType.TEXT,"[1,2]") } </li>
     *                 </ul>
     * @param consumer non-null
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-modification-functions.html#function_json-replace">JSON_REPLACE(json_doc, path, val[, path, val] ...)</a>
     */
    public static SimpleExpression jsonReplace(final Object jsonDoc, Consumer<Clause._PairVariadicSpaceClause> consumer) {
        return _jsonDocAndPairVariadic("JSON_REPLACE", jsonDoc, consumer);
    }

    /**
     * <p>JSON_REPLACE function dynamic method
     * <p>The {@link MappingType} of function return type:the {@link MappingType} of jsonDoc
     *
     * @param jsonDoc  json expression
     *                 <ul>
     *                      <li>{@link Expression} instance</li>
     *                      <li>the instance that  can be accepted by {@link JsonType#TEXT},here it will output literal. For example : {@code "[1,2]"} is equivalent to {@code SQLs.literal(JsonType.TEXT,"[1,2]") } </li>
     *                 </ul>
     * @param space    see {@link SQLs#SPACE} ,for distinguishing static and dynamic method.
     * @param consumer non-null
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-modification-functions.html#function_json-replace">JSON_REPLACE(json_doc, path, val[, path, val] ...)</a>
     */
    public static SimpleExpression jsonReplace(final Object jsonDoc, SQLs.SymbolSpace space, Consumer<Clause._PairVariadicConsumerClause> consumer) {
        return _jsonDocAndPairVariadic("JSON_REPLACE", jsonDoc, consumer);
    }

    /**
     * <p>JSON_SET function static method
     * <p>The {@link MappingType} of function return type:the {@link MappingType} of jsonDoc
     *
     * @param jsonDoc  json expression
     *                 <ul>
     *                      <li>{@link Expression} instance</li>
     *                      <li>the instance that  can be accepted by {@link JsonType#TEXT},here it will output literal. For example : {@code "[1,2]"} is equivalent to {@code SQLs.literal(JsonType.TEXT,"[1,2]") } </li>
     *                 </ul>
     * @param consumer non-null
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-modification-functions.html#function_json-set">JSON_SET(json_doc, path, val[, path, val] ...)</a>
     */
    public static SimpleExpression jsonSet(final Object jsonDoc, Consumer<Clause._PairVariadicSpaceClause> consumer) {
        return _jsonDocAndPairVariadic("JSON_SET", jsonDoc, consumer);
    }

    /**
     * <p>JSON_SET function dynamic method
     * <p>The {@link MappingType} of function return type:the {@link MappingType} of jsonDoc
     *
     * @param jsonDoc  json expression
     *                 <ul>
     *                      <li>{@link Expression} instance</li>
     *                      <li>the instance that  can be accepted by {@link JsonType#TEXT},here it will output literal. For example : {@code "[1,2]"} is equivalent to {@code SQLs.literal(JsonType.TEXT,"[1,2]") } </li>
     *                 </ul>
     * @param space    see {@link SQLs#SPACE} ,for distinguishing static and dynamic method.
     * @param consumer non-null
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-modification-functions.html#function_json-set">JSON_SET(json_doc, path, val[, path, val] ...)</a>
     */
    public static SimpleExpression jsonSet(final Object jsonDoc, SQLs.SymbolSpace space, Consumer<Clause._PairVariadicConsumerClause> consumer) {
        return _jsonDocAndPairVariadic("JSON_SET", jsonDoc, consumer);
    }


    /**
     * <p>The {@link MappingType} of function return type:the {@link MappingType} of jsonDoc
     * <p>You should use {@link #jsonMergePreserve(Expression, Expression, Expression...)},if database is 8.0+.
     *
     * @param jsonDoc1 json expression
     *                 <ul>
     *                      <li>{@link Expression} instance</li>
     *                      <li>the instance that  can be accepted by {@link JsonType#TEXT},here it will output literal. For example : {@code "[1,2]"} is equivalent to {@code SQLs.literal(JsonType.TEXT,"[1,2]") } </li>
     *                 </ul>
     * @param jsonDoc2 json expression
     *                 <ul>
     *                      <li>{@link Expression} instance</li>
     *                      <li>the instance that  can be accepted by {@link JsonType#TEXT},here it will output literal. For example : {@code "[1,2]"} is equivalent to {@code SQLs.literal(JsonType.TEXT,"[1,2]") } </li>
     *                 </ul>
     * @param variadic each of variadic is one of following
     *                 <ul>
     *                      <li>{@link Expression} instance</li>
     *                      <li>the instance that  can be accepted by {@link JsonType#TEXT},here it will output literal. For example : {@code "[1,2]"} is equivalent to {@code SQLs.literal(JsonType.TEXT,"[1,2]") } </li>
     *                 </ul>
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-modification-functions.html#function_json-merge">JSON_MERGE(json_doc, json_doc[, json_doc] ...)</a>
     */
    public static SimpleExpression jsonMerge(final Object jsonDoc1, final Object jsonDoc2, Object... variadic) {
        return _jsonMergeOperationFunction("JSON_MERGE", jsonDoc1, jsonDoc2, variadic);
    }

    /**
     * <p>The {@link MappingType} of function return type:the {@link MappingType} of jsonDoc
     * You should use {@link #jsonMergePreserve(List)},if database is 8.0+.
     *
     * @param jsonDocList non-null,non-empty, each element of jsonDocList is one of following :
     *                    <ul>
     *                         <li>{@link Expression} instance</li>
     *                         <li>the instance that  can be accepted by {@link JsonType#TEXT},here it will output literal. For example : {@code "[1,2]"} is equivalent to {@code SQLs.literal(JsonType.TEXT,"[1,2]") } </li>
     *                    </ul>
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-modification-functions.html#function_json-merge">JSON_MERGE(json_doc, json_doc[, json_doc] ...)</a>
     */
    public static SimpleExpression jsonMerge(final List<?> jsonDocList) {
        return _jsonMergeList("JSON_MERGE", jsonDocList);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:the {@link MappingType} of jsonDoc
     *
     * @param jsonDoc1 json expression
     *                 <ul>
     *                      <li>{@link Expression} instance</li>
     *                      <li>the instance that  can be accepted by {@link JsonType#TEXT},here it will output literal. For example : {@code "[1,2]"} is equivalent to {@code SQLs.literal(JsonType.TEXT,"[1,2]") } </li>
     *                 </ul>
     * @param jsonDoc2 json expression
     *                 <ul>
     *                      <li>{@link Expression} instance</li>
     *                      <li>the instance that  can be accepted by {@link JsonType#TEXT},here it will output literal. For example : {@code "[1,2]"} is equivalent to {@code SQLs.literal(JsonType.TEXT,"[1,2]") } </li>
     *                 </ul>
     * @param variadic each of variadic is one of following
     *                 <ul>
     *                      <li>{@link Expression} instance</li>
     *                      <li>the instance that  can be accepted by {@link JsonType#TEXT},here it will output literal. For example : {@code "[1,2]"} is equivalent to {@code SQLs.literal(JsonType.TEXT,"[1,2]") } </li>
     *                 </ul>
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-modification-functions.html#function_json-merge-preserve">JSON_MERGE_PRESERVE(json_doc, json_doc[, json_doc] ...)</a>
     */
    public static SimpleExpression jsonMergePreserve(final Object jsonDoc1, final Object jsonDoc2, Object... variadic) {
        return _jsonMergeOperationFunction("JSON_MERGE_PRESERVE", jsonDoc1, jsonDoc2, variadic);
    }


    /**
     * <p>The {@link MappingType} of function return type:the {@link MappingType} of jsonDoc
     *
     * @param jsonDocList non-null,non-empty, each element of jsonDocList is one of following :
     *                    <ul>
     *                         <li>{@link Expression} instance</li>
     *                         <li>the instance that  can be accepted by {@link JsonType#TEXT},here it will output literal. For example : {@code "[1,2]"} is equivalent to {@code SQLs.literal(JsonType.TEXT,"[1,2]") } </li>
     *                    </ul>
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-modification-functions.html#function_json-merge-preserve">JSON_MERGE_PRESERVE(json_doc, json_doc[, json_doc] ...)</a>
     */
    public static SimpleExpression jsonMergePreserve(final List<?> jsonDocList) {
        return _jsonMergeList("JSON_MERGE_PRESERVE", jsonDocList);
    }

    /**
     * <p>The {@link MappingType} of function return type:the {@link MappingType} of jsonDoc
     *
     * @param jsonDoc1 json expression
     *                 <ul>
     *                      <li>{@link Expression} instance</li>
     *                      <li>the instance that  can be accepted by {@link JsonType#TEXT},here it will output literal. For example : {@code "[1,2]"} is equivalent to {@code SQLs.literal(JsonType.TEXT,"[1,2]") } </li>
     *                 </ul>
     * @param jsonDoc2 json expression
     *                 <ul>
     *                      <li>{@link Expression} instance</li>
     *                      <li>the instance that  can be accepted by {@link JsonType#TEXT},here it will output literal. For example : {@code "[1,2]"} is equivalent to {@code SQLs.literal(JsonType.TEXT,"[1,2]") } </li>
     *                 </ul>
     * @param variadic each of variadic is one of following
     *                 <ul>
     *                      <li>{@link Expression} instance</li>
     *                      <li>the instance that  can be accepted by {@link JsonType#TEXT},here it will output literal. For example : {@code "[1,2]"} is equivalent to {@code SQLs.literal(JsonType.TEXT,"[1,2]") } </li>
     *                 </ul>
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-modification-functions.html#function_json-merge-patch">JSON_MERGE_PATCH(json_doc, json_doc[, json_doc] ...)</a>
     */
    public static SimpleExpression jsonMergePatch(final Object jsonDoc1, final Object jsonDoc2, Object... variadic) {
        return _jsonMergeOperationFunction("JSON_MERGE_PATCH", jsonDoc1, jsonDoc2, variadic);
    }


    /**
     * <p>
     * <p>The {@link MappingType} of function return type:the {@link MappingType} of jsonDoc
     *
     * @param jsonDocList non-null,non-empty, each element of jsonDocList is one of following :
     *                    <ul>
     *                         <li>{@link Expression} instance</li>
     *                         <li>the instance that  can be accepted by {@link JsonType#TEXT},here it will output literal. For example : {@code "[1,2]"} is equivalent to {@code SQLs.literal(JsonType.TEXT,"[1,2]") } </li>
     *                    </ul>
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-modification-functions.html#function_json-merge-patch">JSON_MERGE_PATCH(json_doc, json_doc[, json_doc] ...)</a>
     */
    public static SimpleExpression jsonMergePatch(final List<?> jsonDocList) {
        return _jsonMergeList("JSON_MERGE_PATCH", jsonDocList);
    }

    /**
     * <p>The {@link MappingType} of function return type:the {@link MappingType} of jsonDoc
     *
     * @param jsonDoc json expression
     *                <ul>
     *                     <li>{@link Expression} instance</li>
     *                     <li>the instance that  can be accepted by {@link JsonType#TEXT},here it will output literal. For example : {@code "[1,2]"} is equivalent to {@code SQLs.literal(JsonType.TEXT,"[1,2]") } </li>
     *                </ul>
     * @param path    non-null, one of following :
     *                <ul>
     *                  <li>{@link Expression} instance</li>
     *                  <li>{@link String} literal</li>
     *                </ul>
     * @throws CriteriaException throw when argument error
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-modification-functions.html#function_json-remove">JSON_REMOVE(json_doc, path[, path] ...)</a>
     */
    public static SimpleExpression jsonRemove(final Object jsonDoc, Object path) {
        FuncExpUtils.assertPathExp(path);

        final Expression jsonDocExp;
        jsonDocExp = FuncExpUtils.jsonDocExp(jsonDoc);
        return LiteralFunctions.twoArgFunc("JSON_REMOVE", jsonDocExp, path, jsonDocExp.typeMeta());
    }

    /**
     * <p>The {@link MappingType} of function return type:the {@link MappingType} of jsonDoc
     *
     * @param jsonDoc      json expression
     *                     <ul>
     *                          <li>{@link Expression} instance</li>
     *                          <li>the instance that  can be accepted by {@link JsonType#TEXT},here it will output literal. For example : {@code "[1,2]"} is equivalent to {@code SQLs.literal(JsonType.TEXT,"[1,2]") } </li>
     *                     </ul>
     * @param path         non-null, one of following :
     *                     <ul>
     *                       <li>{@link Expression} instance</li>
     *                       <li>{@link String} literal</li>
     *                     </ul>
     * @param pathVariadic non-null,each of pathVariadic is  one of following :
     *                     <ul>
     *                       <li>{@link Expression} instance</li>
     *                       <li>{@link String} literal</li>
     *                     </ul>
     * @throws CriteriaException throw when argument error
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-modification-functions.html#function_json-remove">JSON_REMOVE(json_doc, path[, path] ...)</a>
     */
    public static SimpleExpression jsonRemove(final Object jsonDoc, Object path, Object... pathVariadic) {
        FuncExpUtils.assertPathExp(path);

        final Expression jsonDocExp;
        jsonDocExp = FuncExpUtils.jsonDocExp(jsonDoc);

        final List<Object> argList = _Collections.arrayList(2 + pathVariadic.length);

        argList.add(jsonDocExp);
        argList.add(path);

        FuncExpUtils.addAllTextExp(argList, "path", pathVariadic);
        return LiteralFunctions.multiArgFunc("JSON_REMOVE", argList, jsonDocExp.typeMeta());
    }

    /**
     * <p>JSON_REMOVE function dynamic method
     * <p>The {@link MappingType} of function return type:the {@link MappingType} of jsonDoc
     *
     * @param jsonDoc json expression
     *                <ul>
     *                     <li>{@link Expression} instance</li>
     *                     <li>the instance that  can be accepted by {@link JsonType#TEXT},here it will output literal. For example : {@code "[1,2]"} is equivalent to {@code SQLs.literal(JsonType.TEXT,"[1,2]") } </li>
     *                </ul>
     * @throws CriteriaException throw when argument error
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-modification-functions.html#function_json-remove">JSON_REMOVE(json_doc, path[, path] ...)</a>
     */
    public static SimpleExpression jsonRemove(final Object jsonDoc, SQLs.SymbolSpace space, Consumer<Clause._VariadicConsumer> consumer) {
        final Expression jsonDocExp;
        jsonDocExp = FuncExpUtils.jsonDocExp(jsonDoc);

        final ArrayList<Object> arrayList = _Collections.arrayList(4);
        arrayList.add(jsonDocExp);

        final List<?> argList;
        argList = FuncExpUtils.variadicList(true, arrayList, String.class, consumer);
        return LiteralFunctions.multiArgFunc("JSON_REMOVE", argList, jsonDocExp.typeMeta());
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link StringType}
     * *
     *
     * @param jsonVal json expression
     *                <ul>
     *                     <li>{@link Expression} instance</li>
     *                     <li>the instance that  can be accepted by {@link JsonType#TEXT},here it will output literal. For example : {@code "[1,2]"} is equivalent to {@code SQLs.literal(JsonType.TEXT,"[1,2]") } </li>
     *                </ul>
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-modification-functions.html#function_json-unquote">JSON_UNQUOTE(json_val)</a>
     */
    public static SimpleExpression jsonUnquote(final Object jsonVal) {
        return LiteralFunctions.oneArgFunc("JSON_UNQUOTE", FuncExpUtils.jsonDocExp(jsonVal), StringType.INSTANCE);
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
     *                      <li>the instance that  can be accepted by {@link JsonType#TEXT},here it will output literal. For example : {@code "[1,2]"} is equivalent to {@code SQLs.literal(JsonType.TEXT,"[1,2]") } </li>
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
     *                      <li>the instance that  can be accepted by {@link JsonType#TEXT},here it will output literal. For example : {@code "[1,2]"} is equivalent to {@code SQLs.literal(JsonType.TEXT,"[1,2]") } </li>
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
    private static SimpleExpression _jsonMergeOperationFunction(final String name, final Object jsonDoc1,
                                                                final Object jsonDoc2, Object... jsonDocArray) {
        final Expression jsonDocExp;
        jsonDocExp = FuncExpUtils.jsonDocExp(jsonDoc1);

        final List<Object> argList = _Collections.arrayList(2 + jsonDocArray.length);
        argList.add(jsonDocExp);
        argList.add(FuncExpUtils.jsonDocExp(jsonDoc2));
        for (Object jsonDoc : jsonDocArray) {
            argList.add(FuncExpUtils.jsonDocExp(jsonDoc));
        }
        return LiteralFunctions.multiArgFunc(name, argList, jsonDocExp.typeMeta());
    }

    /**
     * @see #jsonMerge(List)
     */
    private static SimpleExpression _jsonMergeList(final String name, final List<?> jsonDocList) {
        final int size;
        if ((size = jsonDocList.size()) < 2) {
            throw CriteriaUtils.funcArgError(name, jsonDocList);
        }
        final List<Expression> argList = _Collections.arrayList(size);
        for (Object json : jsonDocList) {
            if (json instanceof Expression) {
                argList.add((Expression) json);
            } else {
                argList.add(SQLs.literal(JsonType.TEXT, json));
            }
        }
        return LiteralFunctions.multiArgFunc(name, argList, argList.get(0).typeMeta());
    }

    /**
     * @see #jsonArrayAppend(Object, Consumer)
     * @see #jsonArrayAppend(Object, SQLs.SymbolSpace, Consumer)
     * @see #jsonArrayInsert(Object, Consumer)
     * @see #jsonArrayInsert(Object, SQLs.SymbolSpace, Consumer)
     * @see #jsonInsert(Object, Consumer)
     * @see #jsonInsert(Object, SQLs.SymbolSpace, Consumer)
     */
    private static SimpleExpression _jsonDocAndPairVariadic(final String name, final Object jsonDoc,
                                                            Consumer<? super FuncExpUtils.PairVariadicClause> consumer) {
        final Expression jsonDocExpr;
        jsonDocExpr = FuncExpUtils.jsonDocExp(jsonDoc);

        final ArrayList<Object> arrayList = _Collections.arrayList(5);
        arrayList.add(jsonDocExpr);

        final List<?> argList;
        argList = FuncExpUtils.pariVariadicExpList(true, arrayList, JsonType.TEXT, consumer);
        return LiteralFunctions.multiArgFunc(name, argList, jsonDocExpr.typeMeta());
    }


}
