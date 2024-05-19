/*
 * Copyright 2023-2043 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.army.criteria.postgre;

import io.army.criteria.*;
import io.army.criteria.standard.SQLs;
import io.army.mapping.*;
import io.army.mapping.array.IntegerArrayType;
import io.army.mapping.array.ShortArrayType;
import io.army.mapping.array.TextArrayType;
import io.army.mapping.postgre.PostgreAclItemType;
import io.army.mapping.postgre.PostgreInetType;
import io.army.mapping.postgre.PostgreRangeType;
import io.army.mapping.postgre.PostgreTsVectorType;
import io.army.mapping.postgre.array.PostgreAclItemArrayType;
import io.army.util.ArrayUtils;
import io.army.util._Collections;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

abstract class PostgreMiscellaneous2Functions extends PostgreMiscellaneousFunctions {

    /**
     * package constructor
     */
    PostgreMiscellaneous2Functions() {
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  StringType}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-textsearch.html#TEXTSEARCH-FUNCTIONS-TABLE">get_current_ts_config ( ) → regconfig</a>
     */
    public static SimpleExpression getCurrentTsConfig() {
        return FunctionUtils.zeroArgFunc("GET_CURRENT_TS_CONFIG", StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  IntegerType}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-textsearch.html#TEXTSEARCH-FUNCTIONS-TABLE">numnode ( tsquery ) → integer</a>
     */
    public static SimpleExpression numNode(Expression exp) {
        return FunctionUtils.oneArgFunc("NUMNODE", exp, IntegerType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function returned fields type:<ol>
     * <li>alias {@link TextType}</li>
     * <li>description {@link TextType}</li>
     * <li>token {@link TextType}</li>
     * <li>dictionaries {@link TextArrayType#LINEAR}</li>
     * <li>dictionary {@link TextType}</li>
     * <li>lexemes {@link TextArrayType#LINEAR}</li>
     * <li>ordinality (this is optional) {@link LongType},see {@link _WithOrdinalityClause#withOrdinality()}</li>
     * </ol>
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-textsearch.html#TEXTSEARCH-FUNCTIONS-DEBUG-TABLE">ts_debug ( [ config regconfig, ] document text ) → setof record ( alias text, description text, token text, dictionaries regdictionary[], dictionary regdictionary, lexemes text[] )<br/>
     * Extracts and normalizes tokens from the document according to the specified or default text search configuration, and returns information about how each token was processed.  <br/>
     * ts_debug('english', 'The Brightest supernovaes') → (asciiword,"Word, all ASCII",The,{english_stem},english_stem,{})
     * </a>
     */
    public static _TabularWithOrdinalityFunction tsDebug(Expression document) {
        return _tsDebug(null, document);
    }

    /**
     * <p>
     * The {@link MappingType} of function returned fields type:<ol>
     * <li>alias {@link TextType}</li>
     * <li>description {@link TextType}</li>
     * <li>token {@link TextType}</li>
     * <li>dictionaries {@link TextArrayType#LINEAR}</li>
     * <li>dictionary {@link TextType}</li>
     * <li>lexemes {@link TextArrayType#LINEAR}</li>
     * <li>ordinality (this is optional) {@link LongType},see {@link _WithOrdinalityClause#withOrdinality()}</li>
     * </ol>
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-textsearch.html#TEXTSEARCH-FUNCTIONS-DEBUG-TABLE">ts_debug ( [ config regconfig, ] document text ) → setof record ( alias text, description text, token text, dictionaries regdictionary[], dictionary regdictionary, lexemes text[] )<br/>
     * Extracts and normalizes tokens from the document according to the specified or default text search configuration, and returns information about how each token was processed.  <br/>
     * ts_debug('english', 'The Brightest supernovaes') → (asciiword,"Word, all ASCII",The,{english_stem},english_stem,{})
     * </a>
     */
    public static _TabularWithOrdinalityFunction tsDebug(Expression config, Expression document) {
        return _tsDebug(config, document);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  TextArrayType#LINEAR}.
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-textsearch.html#TEXTSEARCH-FUNCTIONS-TABLE">ts_lexize ( dict regdictionary, token text ) → text[]</a>
     */
    public static SimpleExpression tsLexize(Expression dict, Expression token) {
        return FunctionUtils.twoArgFunc("TS_LEXIZE", dict, token, TextArrayType.LINEAR);
    }

    /**
     * <p>
     * The {@link MappingType} of function returned fields type:<ol>
     * <li>tokid {@link IntegerType}</li>
     * <li>token {@link TextType}</li>
     * <li>ordinality (this is optional) {@link LongType},see {@link _WithOrdinalityClause#withOrdinality()}</li>
     * </ol>
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-textsearch.html#TEXTSEARCH-FUNCTIONS-DEBUG-TABLE">ts_parse ( parser_name text, document text ) → setof record ( tokid integer, token text )<br/>
     * Extracts tokens from the document using the named parser.  <br/>
     * ts_parse('default', 'foo - bar') → (1,foo) <br/>
     * ts_parse ( parser_oid oid, document text ) → setof record ( tokid integer, token text )  <br/>
     * Extracts tokens from the document using a parser specified by OID.  <br/>
     * ts_parse(3722, 'foo - bar') → (1,foo)   <br/>
     * </a>
     */
    public static _TabularWithOrdinalityFunction tsParse(Expression parserName, Expression document) {
        final List<Selection> fieldList = _Collections.arrayList(2);

        fieldList.add(ArmySelections.forName("tokid", IntegerType.INSTANCE));
        fieldList.add(ArmySelections.forName("token", TextType.INSTANCE));

        return DialectFunctionUtils.twoArgTabularFunc("TS_PARSE", parserName, document, fieldList);
    }

    /**
     * <p>
     * The {@link MappingType} of function returned fields type:<ol>
     * <li>tokid {@link IntegerType}</li>
     * <li>alias {@link TextType}</li>
     * <li>description {@link TextType}</li>
     * <li>ordinality (this is optional) {@link LongType},see {@link _WithOrdinalityClause#withOrdinality()}</li>
     * </ol>
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-textsearch.html#TEXTSEARCH-FUNCTIONS-DEBUG-TABLE">ts_token_type ( parser_name text ) → setof record ( tokid integer, alias text, description text )<br/>
     * Returns a table that describes each type of token the named parser can recognize.<br/>
     * ts_token_type('default') → (1,asciiword,"Word, all ASCII") <br/>
     * ts_token_type ( parser_oid oid ) → setof record ( tokid integer, alias text, description text ) <br/>
     * Returns a table that describes each type of token a parser specified by OID can recognize. <br/>
     * ts_token_type(3722) → (1,asciiword,"Word, all ASCII") <br/>
     * </a>
     */
    public static _TabularWithOrdinalityFunction tsTokenType(Expression exp) {
        final List<Selection> fieldList = _Collections.arrayList(3);

        fieldList.add(ArmySelections.forName("tokid", IntegerType.INSTANCE));
        fieldList.add(ArmySelections.forName("alias", TextType.INSTANCE));
        fieldList.add(ArmySelections.forName("description", TextType.INSTANCE));

        return DialectFunctionUtils.oneArgTabularFunc("TS_TOKEN_TYPE", exp, fieldList);
    }


    /**
     * <p>
     * The {@link MappingType} of function returned fields type:<ol>
     * <li>word {@link TextType}</li>
     * <li>ndoc {@link IntegerType}</li>
     * <li>nentry {@link IntegerType}</li>
     * <li>ordinality (this is optional) {@link LongType},see {@link _WithOrdinalityClause#withOrdinality()}</li>
     * </ol>
     *
     *
     * @see #tsStat(Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-textsearch.html#TEXTSEARCH-FUNCTIONS-DEBUG-TABLE">ts_stat ( sqlquery text [, weights text ] ) → setof record ( word text, ndoc integer, nentry integer )<br/>
     * Executes the sqlquery, which must return a single tsvector column, and returns statistics about each distinct lexeme contained in the data.<br/>
     * ts_stat('SELECT vector FROM apod') → (foo,10,15)<br/>
     * </a>
     */
    public static _TabularWithOrdinalityFunction tsStat(Expression sqlQuery) {
        return _tsStat(sqlQuery, null);
    }

    /**
     * <p>
     * The {@link MappingType} of function returned fields type:<ol>
     * <li>word {@link TextType}</li>
     * <li>ndoc {@link IntegerType}</li>
     * <li>nentry {@link IntegerType}</li>
     * <li>ordinality (this is optional) {@link LongType},see {@link _WithOrdinalityClause#withOrdinality()}</li>
     * </ol>
     *
     *
     * @see #tsStat(Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-textsearch.html#TEXTSEARCH-FUNCTIONS-DEBUG-TABLE">ts_stat ( sqlquery text [, weights text ] ) → setof record ( word text, ndoc integer, nentry integer )<br/>
     * Executes the sqlquery, which must return a single tsvector column, and returns statistics about each distinct lexeme contained in the data.<br/>
     * ts_stat('SELECT vector FROM apod') → (foo,10,15)<br/>
     * </a>
     */
    public static _TabularWithOrdinalityFunction tsStat(Expression sqlQuery, Expression weights) {
        return _tsStat(sqlQuery, weights);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  UUIDType}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-uuid.html">UUID Functions</a>
     */
    public static SimpleExpression genRandomUuid() {
        return FunctionUtils.zeroArgFunc("GEN_RANDOM_UUID", UUIDType.INSTANCE);
    }

    /*-------------------below Sequence Manipulation Functions-------------------*/

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  LongType}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-sequence.html">nextval ( regclass ) → bigint</a>
     */
    public static SimpleExpression nextVal(Expression exp) {
        return FunctionUtils.oneArgFunc("NEXTVAL", exp, LongType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  LongType}
     *
     * @see #setVal(Expression, Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-sequence.html">setval ( regclass, bigint [, boolean ] ) → bigint</a>
     */
    public static SimpleExpression setVal(Expression regClass, Expression value) {
        return FunctionUtils.twoArgFunc("SETVAL", regClass, value, LongType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  LongType}
     *
     *
     * @param isCalled in most case {@link SQLs#TRUE} or {@link SQLs#FALSE}
     * @see #setVal(Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-sequence.html">setval ( regclass, bigint [, boolean ] ) → bigint</a>
     */
    public static SimpleExpression setVal(Expression regClass, Expression value, Expression isCalled) {
        return FunctionUtils.threeArgFunc("SETVAL", regClass, value, isCalled, LongType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  LongType}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-sequence.html">currval ( regclass ) → bigint</a>
     */
    public static SimpleExpression currVal(Expression exp) {
        return FunctionUtils.oneArgFunc("CURRVAL", exp, LongType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  LongType}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-sequence.html">lastval ( regclass ) → bigint</a>
     */
    public static SimpleExpression lastVal(Expression exp) {
        return FunctionUtils.oneArgFunc("LASTVAL", exp, LongType.INSTANCE);
    }

    /*-------------------below Conditional Expressions-------------------*/


    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of firstValue
     *
     *
     * @throws CriteriaException throw when<ul>
     *                           <li>firstValue isn't operable {@link Expression},eg:{@link SQLs#DEFAULT}</li>
     *                           <li>firstValue is multi value {@link Expression},eg: {@link SQLs#rowLiteral(TypeInfer, Collection)}</li>
     *                           <li>the element of rest isn't operable {@link Expression},eg:{@link SQLs#DEFAULT}</li>
     *                           </ul>
     * @see <a href="https://www.postgresql.org/docs/current/functions-conditional.html#FUNCTIONS-COALESCE-NVL-IFNULL">COALESCE(value [, ...])</a>
     */
    public static SimpleExpression coalesce(Expression firstValue, Expression... rest) {
        return FunctionUtils.oneAndRestFunc("COALESCE", _returnType(firstValue, Expressions::identityType),
                firstValue, rest
        );
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of fist argument
     *
     *
     * @throws CriteriaException throw when<ul>
     *                           <li>the element of consumer isn't operable {@link Expression},eg:{@link SQLs#DEFAULT}</li>
     *                           </ul>
     * @see <a href="https://www.postgresql.org/docs/current/functions-conditional.html#FUNCTIONS-COALESCE-NVL-IFNULL">COALESCE(value [, ...])</a>
     */
    public static SimpleExpression coalesce(Consumer<Consumer<Expression>> consumer) {
        return FunctionUtils.consumerAndFirstTypeFunc("COALESCE", consumer);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of firstValue
     *
     *
     * @throws CriteriaException throw when<ul>
     *                           <li>firstValue isn't operable {@link Expression},eg:{@link SQLs#DEFAULT}</li>
     *                           <li>firstValue is multi value {@link Expression},eg: {@link SQLs#rowLiteral(TypeInfer, Collection)}</li>
     *                           <li>the element of rest isn't operable {@link Expression},eg:{@link SQLs#DEFAULT}</li>
     *                           </ul>
     * @see <a href="https://www.postgresql.org/docs/current/functions-conditional.html#FUNCTIONS-GREATEST-LEAST">GREATEST(value [, ...])</a>
     */
    public static SimpleExpression greatest(Expression firstValue, Expression... rest) {
        return FunctionUtils.oneAndRestFunc("GREATEST", _returnType(firstValue, Expressions::identityType),
                firstValue, rest
        );
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of fist argument
     *
     *
     * @throws CriteriaException throw when<ul>
     *                           <li>the element of consumer isn't operable {@link Expression},eg:{@link SQLs#DEFAULT}</li>
     *                           </ul>
     * @see <a href="https://www.postgresql.org/docs/current/functions-conditional.html#FUNCTIONS-GREATEST-LEAST">GREATEST(value [, ...])</a>
     */
    public static SimpleExpression greatest(Consumer<Consumer<Expression>> consumer) {
        return FunctionUtils.consumerAndFirstTypeFunc("GREATEST", consumer);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of firstValue
     *
     *
     * @throws CriteriaException throw when<ul>
     *                           <li>firstValue isn't operable {@link Expression},eg:{@link SQLs#DEFAULT}</li>
     *                           <li>firstValue is multi value {@link Expression},eg: {@link SQLs#rowLiteral(TypeInfer, Collection)}</li>
     *                           <li>the element of rest isn't operable {@link Expression},eg:{@link SQLs#DEFAULT}</li>
     *                           </ul>
     * @see <a href="https://www.postgresql.org/docs/current/functions-conditional.html#FUNCTIONS-GREATEST-LEAST">LEAST(value [, ...])</a>
     */
    public static SimpleExpression least(Expression firstValue, Expression... rest) {
        return FunctionUtils.oneAndRestFunc("LEAST", _returnType(firstValue, Expressions::identityType),
                firstValue, rest
        );
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of fist argument
     *
     *
     * @throws CriteriaException throw when<ul>
     *                           <li>the element of consumer isn't operable {@link Expression},eg:{@link SQLs#DEFAULT}</li>
     *                           </ul>
     * @see <a href="https://www.postgresql.org/docs/current/functions-conditional.html#FUNCTIONS-GREATEST-LEAST">LEAST(value [, ...])</a>
     */
    public static SimpleExpression least(Consumer<Consumer<Expression>> consumer) {
        return FunctionUtils.consumerAndFirstTypeFunc("LEAST", consumer);
    }

    /*-------------------below  Array Functions-------------------*/

    /**
     * <p>The {@link MappingType} of function return type: the {@link MappingType} of fist anyCompatibleArray
     *
     * @throws CriteriaException throw when<ul>
     *                           <li>the element of consumer isn't operable {@link Expression},eg:{@link SQLs#DEFAULT}</li>
     *                           </ul>
     * @see <a href="https://www.postgresql.org/docs/current/functions-array.html#ARRAY-FUNCTIONS-TABLE">array_append ( anycompatiblearray, anycompatible ) → anycompatiblearray</a>
     */
    public static SimpleExpression arrayAppend(Expression anyCompatibleArray, Expression anyCompatible) {
        return FunctionUtils.twoArgFunc("ARRAY_APPEND", anyCompatibleArray, anyCompatible,
                _returnType(anyCompatibleArray, Expressions::identityType)
        );
    }

    /**
     * <p>The {@link MappingType} of function return type: the {@link MappingType} of fist anyCompatibleArray1
     *
     * @throws CriteriaException throw when<ul>
     *                           <li>the element of consumer isn't operable {@link Expression},eg:{@link SQLs#DEFAULT}</li>
     *                           </ul>
     * @see <a href="https://www.postgresql.org/docs/current/functions-array.html#ARRAY-FUNCTIONS-TABLE">array_cat ( anycompatiblearray, anycompatiblearray ) → anycompatiblearray</a>
     */
    public static SimpleExpression arrayCat(Expression anyCompatibleArray1, Expression anyCompatibleArray2) {
        return FunctionUtils.twoArgFunc("ARRAY_CAT", anyCompatibleArray1, anyCompatibleArray2,
                _returnType(anyCompatibleArray1, Expressions::identityType)
        );
    }

    /**
     * <p>The {@link MappingType} of function return type: {@link TextType#INSTANCE}
     *
     *
     * @throws CriteriaException throw when<ul>
     *                           <li>the element of consumer isn't operable {@link Expression},eg:{@link SQLs#DEFAULT}</li>
     *                           </ul>
     * @see <a href="https://www.postgresql.org/docs/current/functions-array.html#ARRAY-FUNCTIONS-TABLE">array_dims ( anyarray ) → text</a>
     */
    public static SimpleExpression arrayDims(Expression anyArray) {
        return FunctionUtils.oneArgFunc("ARRAY_DIMS", anyArray, TextType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type: the array type of {@link MappingType} of anyElement.
     *
     * @param funcRefForDimension the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                            <ul>
     *                                <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                                <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                                <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                                <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                                <li>developer custom method</li>
     *                            </ul>.
     *                            The first argument of funcRefForDimension always is {@link IntegerArrayType#LINEAR}.
     * @param dimensions          non-null,it will be passed to funcRefForDimension as the second argument of funcRefForDimension
     * @throws CriteriaException throw when<ul>
     *                           <li>the element of consumer isn't operable {@link Expression},eg:{@link SQLs#DEFAULT}</li>
     *                           </ul>
     * @see <a href="https://www.postgresql.org/docs/current/functions-array.html#ARRAY-FUNCTIONS-TABLE">array_fill ( anyelement, integer[] [, integer[] ] ) → anyarray</a>
     */
    public static <T> Expression arrayFill(Expression anyElement, BiFunction<MappingType, T, Expression> funcRefForDimension,
                                                 T dimensions) {
        return _arrayFill(anyElement, funcRefForDimension.apply(IntegerArrayType.LINEAR, dimensions), null);
    }

    /**
     * <p>The {@link MappingType} of function return type: the array type of {@link MappingType} of anyElement.
     *
     * @throws CriteriaException throw when<ul>
     *                           <li>the element of consumer isn't operable {@link Expression},eg:{@link SQLs#DEFAULT}</li>
     *                           </ul>
     * @see <a href="https://www.postgresql.org/docs/current/functions-array.html#ARRAY-FUNCTIONS-TABLE">array_fill ( anyelement, integer[] [, integer[] ] ) → anyarray</a>
     */
    public static SimpleExpression arrayFill(Expression anyElement, Expression dimensions) {
        return _arrayFill(anyElement, dimensions, null);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the array type of {@link MappingType} of anyElement.
     *
     *
     * @param funcRefForDimension the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                            <ul>
     *                                <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                                <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                                <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                                <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                                <li>developer custom method</li>
     *                            </ul>.
     *                            The first argument of funcRefForDimension always is {@link IntegerArrayType#LINEAR}.
     * @param dimensions          non-null,it will be passed to funcRefForDimension as the second argument of funcRefForDimension
     * @param funcRefForBound     the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                            <ul>
     *                                <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                                <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                                <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                                <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                                <li>developer custom method</li>
     *                            </ul>.
     *                            The first argument of funcRefForBound always is {@link IntegerArrayType#LINEAR}.
     * @param bounds              non-null,it will be passed to funcRefForBound as the second argument of funcRefForBound
     * @throws CriteriaException throw when<ul>
     *                           <li>the element of consumer isn't operable {@link Expression},eg:{@link SQLs#DEFAULT}</li>
     *                           </ul>
     * @see <a href="https://www.postgresql.org/docs/current/functions-array.html#ARRAY-FUNCTIONS-TABLE">array_fill ( anyelement, integer[] [, integer[] ] ) → anyarray</a>
     */
    public static <T, U> Expression arrayFill(Expression anyElement, BiFunction<MappingType, T, Expression> funcRefForDimension,
                                                    T dimensions, BiFunction<MappingType, U, Expression> funcRefForBound, U bounds) {
        return _arrayFill(anyElement, funcRefForDimension.apply(IntegerArrayType.LINEAR, dimensions),
                funcRefForBound.apply(IntegerArrayType.LINEAR, bounds)
        );
    }

    /**
     * <p>The {@link MappingType} of function return type: the array type of {@link MappingType} of anyElement.
     *
     * @throws CriteriaException throw when<ul>
     *                           <li>the element of consumer isn't operable {@link Expression},eg:{@link SQLs#DEFAULT}</li>
     *                           </ul>
     * @see <a href="https://www.postgresql.org/docs/current/functions-array.html#ARRAY-FUNCTIONS-TABLE">array_fill ( anyelement, integer[] [, integer[] ] ) → anyarray</a>
     */
    public static SimpleExpression arrayFill(Expression anyElement, Expression dimensions, Expression bounds) {
        ContextStack.assertNonNull(bounds);
        return _arrayFill(anyElement, dimensions, bounds);
    }

    /**
     * <p>The {@link MappingType} of function return type: {@link IntegerType#INSTANCE}
     *
     *
     * @param funcRef   the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                  <ul>
     *                      <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                      <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                      <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                      <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                      <li>developer custom method</li>
     *                  </ul>.
     *                  The first argument of funcRef always is {@link IntegerType#INSTANCE}.
     * @param dimension non-null,it will be passed to funcRef as the second argument of funcRef
     * @throws CriteriaException throw when<ul>
     *                           <li>the element of consumer isn't operable {@link Expression},eg:{@link SQLs#DEFAULT}</li>
     *                           </ul>
     * @see #arrayLength(Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-array.html#ARRAY-FUNCTIONS-TABLE">array_length ( anyarray, integer ) → integer</a>
     */
    public static <T> Expression arrayLength(Expression anyArray, BiFunction<MappingType, T, Expression> funcRef,
                                                   T dimension) {
        return arrayLength(anyArray, funcRef.apply(IntegerType.INSTANCE, dimension));
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link IntegerType#INSTANCE}
     *
     *
     * @throws CriteriaException throw when<ul>
     *                           <li>the element of consumer isn't operable {@link Expression},eg:{@link SQLs#DEFAULT}</li>
     *                           </ul>
     * @see <a href="https://www.postgresql.org/docs/current/functions-array.html#ARRAY-FUNCTIONS-TABLE">array_length ( anyarray, integer ) → integer</a>
     */
    public static SimpleExpression arrayLength(Expression anyArray, Expression dimension) {
        return FunctionUtils.twoArgFunc("ARRAY_LENGTH", anyArray, dimension, IntegerType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link IntegerType#INSTANCE}
     *
     *
     * @throws CriteriaException throw when<ul>
     *                           <li>the element of consumer isn't operable {@link Expression},eg:{@link SQLs#DEFAULT}</li>
     *                           </ul>
     * @see #arrayUpper(Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-array.html#ARRAY-FUNCTIONS-TABLE">array_lower ( anyarray, integer ) → integer</a>
     */
    public static SimpleExpression arrayLower(Expression anyArray, Expression dimension) {
        return FunctionUtils.twoArgFunc("ARRAY_LOWER", anyArray, dimension, IntegerType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link IntegerType#INSTANCE}
     *
     *
     * @throws CriteriaException throw when<ul>
     *                           <li>the element of consumer isn't operable {@link Expression},eg:{@link SQLs#DEFAULT}</li>
     *                           </ul>
     * @see <a href="https://www.postgresql.org/docs/current/functions-array.html#ARRAY-FUNCTIONS-TABLE">array_ndims ( anyarray ) → integer</a>
     */
    public static SimpleExpression arrayNDims(Expression anyArray) {
        return FunctionUtils.oneArgFunc("ARRAY_NDIMS", anyArray, IntegerType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link IntegerType#INSTANCE}
     *
     *
     * @throws CriteriaException throw when<ul>
     *                           <li>the element of consumer isn't operable {@link Expression},eg:{@link SQLs#DEFAULT}</li>
     *                           </ul>
     * @see #arrayPosition(Expression, Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-array.html#ARRAY-FUNCTIONS-TABLE">array_position ( anycompatiblearray, anycompatible [, integer ] ) → integer</a>
     */
    public static SimpleExpression arrayPosition(Expression anyCompatibleArray, Expression anyCompatible) {
        return FunctionUtils.twoArgFunc("ARRAY_POSITION", anyCompatibleArray, anyCompatible, IntegerType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link IntegerType#INSTANCE}
     *
     *
     * @param funcRef   the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                  <ul>
     *                      <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                      <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                      <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                      <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                      <li>{@link SQLs#encodingParam(TypeInfer, Object)},used when only <strong>this</strong> is instance of {@link TableField} and {@link TableField#codec()} is true</li>
     *                      <li>{@link SQLs#encodingLiteral(TypeInfer, Object)},used when only <strong>this</strong> is instance of {@link TableField} and {@link TableField#codec()} is true</li>
     *                      <li>{@link SQLs#encodingNamedParam(TypeInfer, String)} ,used when only <strong>this</strong> is instance of {@link TableField} and {@link TableField#codec()} is true
     *                      and in INSERT( or batch update/delete ) syntax</li>
     *                      <li>{@link SQLs#encodingNamedLiteral(TypeInfer, String)} ,used when only <strong>this</strong> is instance of {@link TableField} and {@link TableField#codec()} is true
     *                      and in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                      <li>developer custom method</li>
     *                  </ul>.
     *                  The first argument of funcRef always is {@link IntegerType#INSTANCE}.
     * @param subscript non-null,it will be passed to funcRef as the second argument of funcRef
     * @throws CriteriaException throw when<ul>
     *                           <li>the element of consumer isn't operable {@link Expression},eg:{@link SQLs#DEFAULT}</li>
     *                           </ul>
     * @see #arrayPosition(Expression, Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-array.html#ARRAY-FUNCTIONS-TABLE">array_position ( anycompatiblearray, anycompatible [, integer ] ) → integer</a>
     */
    public static <T> Expression arrayPosition(Expression anyCompatibleArray, Expression anyCompatible,
                                                     BiFunction<MappingType, T, Expression> funcRef, T subscript) {
        return arrayPosition(anyCompatibleArray, anyCompatible, funcRef.apply(IntegerType.INSTANCE, subscript));
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link IntegerType#INSTANCE}
     *
     *
     * @throws CriteriaException throw when<ul>
     *                           <li>the element of consumer isn't operable {@link Expression},eg:{@link SQLs#DEFAULT}</li>
     *                           </ul>
     * @see <a href="https://www.postgresql.org/docs/current/functions-array.html#ARRAY-FUNCTIONS-TABLE">array_position ( anycompatiblearray, anycompatible [, integer ] ) → integer</a>
     */
    public static SimpleExpression arrayPosition(Expression anyCompatibleArray, Expression anyCompatible,
                                                 Expression subscript) {
        return FunctionUtils.threeArgFunc("ARRAY_POSITION", anyCompatibleArray, anyCompatible, subscript,
                IntegerType.INSTANCE
        );
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link IntegerArrayType#LINEAR}
     *
     *
     * @throws CriteriaException throw when<ul>
     *                           <li>the element of consumer isn't operable {@link Expression},eg:{@link SQLs#DEFAULT}</li>
     *                           </ul>
     * @see <a href="https://www.postgresql.org/docs/current/functions-array.html#ARRAY-FUNCTIONS-TABLE">array_positions ( anycompatiblearray, anycompatible ) → integer[]</a>
     */
    public static SimpleExpression arrayPositions(Expression anyCompatibleArray, Expression anyCompatible) {
        return FunctionUtils.twoArgFunc("ARRAY_POSITIONS", anyCompatibleArray, anyCompatible, IntegerArrayType.LINEAR);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of anyCompatibleArray.
     *
     *
     * @throws CriteriaException throw when<ul>
     *                           <li>the element of consumer isn't operable {@link Expression},eg:{@link SQLs#DEFAULT}</li>
     *                           </ul>
     * @see <a href="https://www.postgresql.org/docs/current/functions-array.html#ARRAY-FUNCTIONS-TABLE">array_prepend ( anycompatible, anycompatiblearray ) → anycompatiblearray</a>
     */
    public static SimpleExpression arrayPrepend(Expression anyCompatible, Expression anyCompatibleArray) {
        return FunctionUtils.twoArgFunc("ARRAY_PREPEND", anyCompatible, anyCompatibleArray,
                _returnType(anyCompatibleArray, Expressions::identityType)
        );
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of anyCompatibleArray.
     *
     *
     * @throws CriteriaException throw when<ul>
     *                           <li>the element of consumer isn't operable {@link Expression},eg:{@link SQLs#DEFAULT}</li>
     *                           </ul>
     * @see <a href="https://www.postgresql.org/docs/current/functions-array.html#ARRAY-FUNCTIONS-TABLE">array_remove ( anycompatiblearray, anycompatible ) → anycompatiblearray</a>
     */
    public static SimpleExpression arrayRemove(Expression anyCompatibleArray, Expression anyCompatible) {
        return FunctionUtils.twoArgFunc("ARRAY_REMOVE", anyCompatibleArray, anyCompatible,
                _returnType(anyCompatibleArray, Expressions::identityType)
        );
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of anyCompatibleArray.
     *
     *
     * @throws CriteriaException throw when<ul>
     *                           <li>the element of consumer isn't operable {@link Expression},eg:{@link SQLs#DEFAULT}</li>
     *                           </ul>
     * @see <a href="https://www.postgresql.org/docs/current/functions-array.html#ARRAY-FUNCTIONS-TABLE">array_replace ( anycompatiblearray, anycompatible, anycompatible ) → anycompatiblearray</a>
     */
    public static SimpleExpression arrayReplace(Expression anyCompatibleArray, Expression anyCompatible,
                                                Expression replacement) {
        return FunctionUtils.threeArgFunc("ARRAY_REPLACE", anyCompatibleArray, anyCompatible, replacement,
                _returnType(anyCompatibleArray, Expressions::identityType)
        );
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link TextType#INSTANCE}.
     *
     *
     * @throws CriteriaException throw when<ul>
     *                           <li>the element of consumer isn't operable {@link Expression},eg:{@link SQLs#DEFAULT}</li>
     *                           </ul>
     * @see <a href="https://www.postgresql.org/docs/current/functions-array.html#ARRAY-FUNCTIONS-TABLE">array_to_string ( array anyarray, delimiter text [, null_string text ] ) → text</a>
     */
    public static SimpleExpression arrayToString(Expression array, Expression delimiter, Expression nullString) {
        return FunctionUtils.threeArgFunc("ARRAY_TO_STRING", array, delimiter, nullString, TextType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link IntegerType#INSTANCE}.
     *
     *
     * @throws CriteriaException throw when<ul>
     *                           <li>the element of consumer isn't operable {@link Expression},eg:{@link SQLs#DEFAULT}</li>
     *                           </ul>
     * @see #arrayLower(Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-array.html#ARRAY-FUNCTIONS-TABLE">array_upper ( anyarray, integer ) → integer</a>
     */
    public static SimpleExpression arrayUpper(Expression anyArray, Expression dimension) {
        return FunctionUtils.twoArgFunc("ARRAY_UPPER", anyArray, dimension, IntegerType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link IntegerType#INSTANCE}.
     *
     *
     * @throws CriteriaException throw when<ul>
     *                           <li>the element of consumer isn't operable {@link Expression},eg:{@link SQLs#DEFAULT}</li>
     *                           </ul>
     * @see #arrayLower(Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-array.html#ARRAY-FUNCTIONS-TABLE">cardinality ( anyarray ) → integer</a>
     */
    public static SimpleExpression cardinality(Expression anyArray) {
        return FunctionUtils.oneArgFunc("CARDINALITY", anyArray, IntegerType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of array.
     *
     *
     * @throws CriteriaException throw when<ul>
     *                           <li>the element of consumer isn't operable {@link Expression},eg:{@link SQLs#DEFAULT}</li>
     *                           </ul>
     * @see #arrayLower(Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-array.html#ARRAY-FUNCTIONS-TABLE">trim_array ( array anyarray, n integer ) → anyarray</a>
     */
    public static SimpleExpression trimArray(Expression array, Expression n) {
        return FunctionUtils.twoArgFunc("TRIM_ARRAY", array, n, _returnType(array, Expressions::identityType));
    }

    /**
     * <p>
     * The {@link MappingType} of function returned fields type:<ol>
     * <li>lexeme {@link TextType}</li>
     * <li>positions {@link ShortArrayType} with one dimension</li>
     * <li>weights {@link TextType}</li>
     * <li>ordinality (this is optional) {@link LongType},see {@link _WithOrdinalityClause#withOrdinality()}</li>
     * </ol>
     *
     * <p>
     * <pre>
     *          select * from unnest('cat:3 fat:2,4 rat:5A'::tsvector) →
     *
     *          lexeme | positions | weights
     *          --------+-----------+---------
     *          cat    | {3}       | {D}
     *          fat    | {2,4}     | {D,D}
     *          rat    | {5}       | {A}
     *   </pre>
     *
     *
     * <p>
     * If exp is array,then the {@link MappingType} of function returned is the {@link MappingType} of the element.
     * <pre><br/>
     * unnest ( anyarray ) → setof anyelement
     *
     * Expands an array into a set of rows. The array's elements are read out in storage order.
     *
     * unnest(ARRAY[1,2]) →
     *
     *  1
     *  2
     * unnest(ARRAY[['foo','bar'],['baz','quux']]) →
     *
     *  foo
     *  bar
     *  baz
     *  quux
     *     </pre>
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-textsearch.html#TEXTSEARCH-FUNCTIONS-TABLE">unnest ( tsvector ) → setof record ( lexeme text, positions smallint[], weights text ) <br/>
     * Expands a tsvector into a set of rows, one per lexeme
     * </a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-array.html#ARRAY-FUNCTIONS-TABLE">unnest ( anyarray ) → setof anyelement<br/>
     * Expands an array into a set of rows. The array's elements are read out in storage order.
     * </a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-range.html#MULTIRANGE-FUNCTIONS-TABLE">unnest ( anymultirange ) → setof anyrange<br/>
     * Expands a multirange into a set of ranges. The ranges are read out in storage order (ascending).<br/>
     * unnest('{[1,2), [3,4)}'::int4multirange) →
     * [1,2)
     * [3,4)
     * </a>
     */
    public static _TabularWithOrdinalityFunction unnest(final Expression exp) {
        final String name = "UNNEST";

        final MappingType type;
        type = exp.typeMeta().mappingType();
        final _TabularWithOrdinalityFunction func;
        if (type instanceof MappingType.SqlArrayType) {
            func = DialectFunctionUtils.oneArgColumnFunction(name, exp, null, CriteriaUtils.arrayUnderlyingType(type));
        } else if (type instanceof PostgreTsVectorType) {
            final List<Selection> fieldList = _Collections.arrayList(3);

            fieldList.add(ArmySelections.forName("lexeme", TextType.INSTANCE));
            fieldList.add(ArmySelections.forName("positions", ShortArrayType.from(Short[].class)));
            fieldList.add(ArmySelections.forName("weights", TextType.INSTANCE));

            func = DialectFunctionUtils.oneArgTabularFunc(name, exp, fieldList);
        } else if (type instanceof PostgreRangeType.MultiRangeType) {
            func = DialectFunctionUtils.oneArgColumnFunction(name, exp, null,
                    ((PostgreRangeType.MultiRangeType) type).rangeType());
        } else {
            throw CriteriaUtils.funcArgError(name, exp);
        }
        return func;
    }

    /**
     * <p>
     * If exp is array,then the {@link MappingType} of function returned is the {@link MappingType} of the element.
     * <pre><br/>
     * unnest ( anyarray ) → setof anyelement
     *
     * Expands an array into a set of rows. The array's elements are read out in storage order.
     *
     * unnest(ARRAY[1,2]) →
     *
     *  1
     *  2
     * unnest(ARRAY[['foo','bar'],['baz','quux']]) →
     *
     *  foo
     *  bar
     *  baz
     *  quux
     *     </pre>
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-array.html#ARRAY-FUNCTIONS-TABLE">unnest ( anyarray ) → setof anyelement<br/>
     * Expands an array into a set of rows. The array's elements are read out in storage order.
     * </a>
     */
    public static _ColumnWithOrdinalityFunction unnest(final ArrayExpression exp) {
        return DialectFunctionUtils.oneArgColumnFunction("UNNEST", exp, null,
                CriteriaUtils.arrayUnderlyingType(exp.typeMeta().mappingType())
        );
    }

    /**
     * <p>
     * If exp is array,then the {@link MappingType} of function returned is the {@link MappingType} of the element.
     * <pre><br/>
     * select * from unnest(ARRAY[1,2], ARRAY['foo','bar','baz']) as x(a,b) →
     *
     *  a |  b
     * ---+-----
     *  1 | foo
     *  2 | bar
     *    | baz
     *
     *     </pre>
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-array.html#ARRAY-FUNCTIONS-TABLE">unnest ( anyarray, anyarray [, ... ] ) → setof anyelement, anyelement [, ... ]<br/>
     * Expands multiple arrays (possibly of different data types) into a set of rows. If the arrays are not all the same length then the shorter ones are padded with NULLs. This form is only allowed in a query's FROM clause;
     * </a>
     */
    public static _TabularWithOrdinalityFunction unnest(ArrayExpression array1, ArrayExpression array2) {
        final List<Selection> fieldList = _Collections.arrayList(2);

        fieldList.add(ArmySelections.forAnonymous(CriteriaUtils.arrayUnderlyingType(array1.typeMeta())));
        fieldList.add(ArmySelections.forAnonymous(CriteriaUtils.arrayUnderlyingType(array2.typeMeta())));
        return DialectFunctionUtils.twoArgTabularFunc("UNNEST", array1, array2, fieldList);
    }

    /**
     * <p>
     * If exp is array,then the {@link MappingType} of function returned is the {@link MappingType} of the element.
     * <pre><br/>
     * select * from unnest(ARRAY[1,2], ARRAY['foo','bar','baz']) as x(a,b) →
     *
     *  a |  b
     * ---+-----
     *  1 | foo
     *  2 | bar
     *    | baz
     *
     *     </pre>
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-array.html#ARRAY-FUNCTIONS-TABLE">unnest ( anyarray, anyarray [, ... ] ) → setof anyelement, anyelement [, ... ]<br/>
     * Expands multiple arrays (possibly of different data types) into a set of rows. If the arrays are not all the same length then the shorter ones are padded with NULLs. This form is only allowed in a query's FROM clause;
     * </a>
     */
    public static _TabularWithOrdinalityFunction unnest(ArrayExpression array1, ArrayExpression array2,
                                                        ArrayExpression array3, ArrayExpression... restArray) {
        final String name = "UNNEST";

        final List<Selection> fieldList = _Collections.arrayList(3 + restArray.length);
        fieldList.add(ArmySelections.forAnonymous(CriteriaUtils.arrayUnderlyingType(array1.typeMeta())));
        fieldList.add(ArmySelections.forAnonymous(CriteriaUtils.arrayUnderlyingType(array2.typeMeta())));
        fieldList.add(ArmySelections.forAnonymous(CriteriaUtils.arrayUnderlyingType(array3.typeMeta())));


        final _TabularWithOrdinalityFunction func;
        if (restArray.length == 0) {
            func = DialectFunctionUtils.threeArgTabularFunc(name, array1, array2, array3, fieldList);
        } else {
            final List<ArmyExpression> argList = _Collections.arrayList(3 + restArray.length);
            argList.add((ArmyExpression) array1);
            argList.add((ArmyExpression) array2);
            argList.add((ArmyExpression) array3);

            for (ArrayExpression array : restArray) {
                argList.add((ArmyExpression) array);
                fieldList.add(ArmySelections.forAnonymous(CriteriaUtils.arrayUnderlyingType(array.typeMeta())));
            }
            func = DialectFunctionUtils.multiArgTabularFunc(name, argList, fieldList);
        }
        return func;
    }

    /**
     * <p>
     * If exp is array,then the {@link MappingType} of function returned is the {@link MappingType} of the element.
     * <pre><br/>
     * select * from unnest(ARRAY[1,2], ARRAY['foo','bar','baz']) as x(a,b) →
     *
     *  a |  b
     * ---+-----
     *  1 | foo
     *  2 | bar
     *    | baz
     *
     *     </pre>
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-array.html#ARRAY-FUNCTIONS-TABLE">unnest ( anyarray, anyarray [, ... ] ) → setof anyelement, anyelement [, ... ]<br/>
     * Expands multiple arrays (possibly of different data types) into a set of rows. If the arrays are not all the same length then the shorter ones are padded with NULLs. This form is only allowed in a query's FROM clause;
     * </a>
     */
    public static _TabularWithOrdinalityFunction unnest(final Consumer<Consumer<ArrayExpression>> consumer) {
        final String name = "UNNEST";
        final List<ArmyExpression> argList = _Collections.arrayList();
        final List<Selection> fieldList = _Collections.arrayList();
        consumer.accept(exp -> {
            argList.add((ArmyExpression) exp);
            fieldList.add(ArmySelections.forAnonymous(CriteriaUtils.arrayUnderlyingType(exp.typeMeta())));
        });

        if (argList.size() == 0) {
            throw CriteriaUtils.dontAddAnyItem();
        }
        return DialectFunctionUtils.multiArgTabularFunc(name, argList, fieldList);
    }

    /*-------------------below Range/Multirange Functions and Operators -------------------*/

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link BooleanType#INSTANCE}.
     *
     *
     * @throws CriteriaException throw when<ul>
     *                           <li>the element of consumer isn't operable {@link Expression},eg:{@link SQLs#DEFAULT}</li>
     *                           </ul>
     * @see <a href="https://www.postgresql.org/docs/current/functions-range.html#RANGE-FUNCTIONS-TABLE">isempty ( anyrange ) → boolean<br/>
     * Is the range empty?<br/>
     * isempty(numrange(1.1,2.2)) → f
     * </a>
     */
    public static SimplePredicate isEmpty(Expression exp) {
        return FunctionUtils.oneArgPredicateFunc("ISEMPTY", exp);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link BooleanType#INSTANCE}.
     *
     *
     * @throws CriteriaException throw when<ul>
     *                           <li>the element of consumer isn't operable {@link Expression},eg:{@link SQLs#DEFAULT}</li>
     *                           </ul>
     * @see <a href="https://www.postgresql.org/docs/current/functions-range.html#RANGE-FUNCTIONS-TABLE">lower_inc ( anyrange ) → boolean<br/>
     * Is the range's lower bound inclusive?<br/>
     * lower_inc(numrange(1.1,2.2)) → t
     * </a>
     */
    public static SimplePredicate lowerInc(Expression exp) {
        return FunctionUtils.oneArgPredicateFunc("LOWER_INC", exp);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link BooleanType#INSTANCE}.
     *
     *
     * @throws CriteriaException throw when<ul>
     *                           <li>the element of consumer isn't operable {@link Expression},eg:{@link SQLs#DEFAULT}</li>
     *                           </ul>
     * @see <a href="https://www.postgresql.org/docs/current/functions-range.html#RANGE-FUNCTIONS-TABLE">upper_inc ( anyrange ) → boolean<br/>
     * Is the range's upper bound inclusive?<br/>
     * upper_inc(numrange(1.1,2.2)) → t
     * </a>
     */
    public static SimplePredicate upperInc(Expression exp) {
        return FunctionUtils.oneArgPredicateFunc("UPPER_INC", exp);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link BooleanType#INSTANCE}.
     *
     *
     * @throws CriteriaException throw when<ul>
     *                           <li>the element of consumer isn't operable {@link Expression},eg:{@link SQLs#DEFAULT}</li>
     *                           </ul>
     * @see <a href="https://www.postgresql.org/docs/current/functions-range.html#RANGE-FUNCTIONS-TABLE">lower_inf ( anyrange ) → boolean<br/>
     * Is the range's lower bound infinite?<br/>
     * lower_inf(numrange(1.1,2.2)) → t
     * </a>
     */
    public static SimplePredicate lowerInf(Expression exp) {
        return FunctionUtils.oneArgPredicateFunc("LOWER_INF", exp);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link BooleanType#INSTANCE}.
     *
     *
     * @throws CriteriaException throw when<ul>
     *                           <li>the element of consumer isn't operable {@link Expression},eg:{@link SQLs#DEFAULT}</li>
     *                           </ul>
     * @see <a href="https://www.postgresql.org/docs/current/functions-range.html#RANGE-FUNCTIONS-TABLE">upper_inf ( anyrange ) → boolean<br/>
     * Is the range's upper bound infinite?<br/>
     * upper_inf(numrange(1.1,2.2)) → t
     * </a>
     */
    public static SimplePredicate upperInf(Expression exp) {
        return FunctionUtils.oneArgPredicateFunc("UPPER_INF", exp);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of range1.
     *
     *
     * @throws CriteriaException throw when<ul>
     *                           <li>the element of consumer isn't operable {@link Expression},eg:{@link SQLs#DEFAULT}</li>
     *                           </ul>
     * @see <a href="https://www.postgresql.org/docs/current/functions-range.html#RANGE-FUNCTIONS-TABLE">range_merge ( anyrange, anyrange ) → anyrange<br/>
     * Computes the smallest range that includes both of the given ranges.<br/>
     * range_merge('[1,2)'::int4range, '[3,4)'::int4range) → [1,4)
     * </a>
     */
    public static SimpleExpression rangeMerge(Expression range1, Expression range2) {
        return FunctionUtils.twoArgFunc("RANGE_MERGE", range1, range2, _returnType(range1, Expressions::identityType));
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: <ul>
     * <li>If anyRange is {@link PostgreRangeType.SingleRangeType} ,then the multi range of the {@link MappingType} of anyRange.</li>
     * <li>Else {@link TextType#INSTANCE}</li>
     * </ul>
     *
     *
     * @throws CriteriaException throw when<ul>
     *                           <li>the element of consumer isn't operable {@link Expression},eg:{@link SQLs#DEFAULT}</li>
     *                           </ul>
     * @see <a href="https://www.postgresql.org/docs/current/functions-range.html#MULTIRANGE-FUNCTIONS-TABLE">multirange ( anyrange ) → anymultirange<br/>
     * Returns a multirange containing just the given range.<br/>
     * multirange('[1,2)'::int4range) → {[1,2)}
     * </a>
     */
    public static SimpleExpression multiRange(final Expression anyRange) {
        final UnaryOperator<MappingType> func;
        func = t -> {
            final MappingType type;
            if (t instanceof PostgreRangeType.SingleRangeType) {
                type = ((PostgreRangeType.SingleRangeType) t).multiRangeType();
            } else {
                type = TextType.INSTANCE;
            }
            return type;
        };
        return FunctionUtils.oneArgFunc("multirange", anyRange, _returnType(anyRange, func));
    }

    /*-------------------below Series Generating Functions-------------------*/

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of start
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-srf.html">Set Returning Functions<br/>
     * </a>
     */
    public static _ColumnWithOrdinalityFunction generateSeries(Expression start, Expression stop) {
        return DialectFunctionUtils.twoArgColumnFunction("generate_series", start, stop, null,
                _returnType(start, Expressions::identityType)
        );
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of start
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-srf.html">Set Returning Functions<br/>
     * </a>
     */
    public static _ColumnWithOrdinalityFunction generateSeries(Expression start, Expression stop, Expression step) {
        return DialectFunctionUtils.threeArgColumnFunction("generate_series", start, stop, step, null,
                _returnType(start, Expressions::identityType)
        );
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link IntegerType#INSTANCE} rt
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-srf.html#FUNCTIONS-SRF-SUBSCRIPTS">Subscript Generating Functions<br/>
     * </a>
     */
    public static _ColumnWithOrdinalityFunction generateSubscripts(Expression array, Expression dim) {
        return DialectFunctionUtils.twoArgColumnFunction("generate_subscripts", array, dim, null,
                IntegerType.INSTANCE
        );
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link IntegerType#INSTANCE}
     *
     *
     * @param reverse in mose case {@link SQLs#TRUE} or {@link SQLs#FALSE}
     * @see <a href="https://www.postgresql.org/docs/current/functions-srf.html#FUNCTIONS-SRF-SUBSCRIPTS">Subscript Generating Functions<br/>
     * </a>
     */
    public static _ColumnWithOrdinalityFunction generateSubscripts(Expression array, Expression dim, Expression reverse) {
        return DialectFunctionUtils.threeArgColumnFunction("generate_subscripts", array, dim, reverse, null,
                IntegerType.INSTANCE
        );
    }

    /*-------------------below  System Information Functions-------------------*/

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link TextType#INSTANCE}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-INFO-SESSION-TABLE">current_database () → name<br/>
     * </a>
     */
    public static SimpleExpression currentDatabase() {
        return FunctionUtils.zeroArgFunc("current_database", TextType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link TextType#INSTANCE}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-INFO-SESSION-TABLE">current_query () → text<br/>
     * </a>
     */
    public static SimpleExpression currentQuery() {
        return FunctionUtils.zeroArgFunc("current_query", TextType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link TextType#INSTANCE}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-INFO-SESSION-TABLE">currentSchema () → name<br/>
     * </a>
     */
    public static SimpleExpression currentSchema() {
        return FunctionUtils.zeroArgFunc("current_schema", TextType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link TextArrayType#LINEAR}
     *
     *
     * @param includeImplicit in mose case {@link SQLs#TRUE} or {@link SQLs#FALSE}
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-INFO-SESSION-TABLE">current_schemas ( include_implicit boolean ) → name[]<br/>
     * </a>
     */
    public static SimpleExpression currentSchema(Expression includeImplicit) {
        return FunctionUtils.oneArgFunc("current_schema", includeImplicit, TextArrayType.LINEAR);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link PostgreInetType#INSTANCE}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-INFO-SESSION-TABLE">inet_client_addr () → inet<br/>
     * </a>
     */
    public static SimpleExpression inetClientAddr() {
        return FunctionUtils.zeroArgFunc("inet_client_addr", PostgreInetType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link IntegerType#INSTANCE}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-INFO-SESSION-TABLE">inet_client_port () → integer<br/>
     * </a>
     */
    public static SimpleExpression inetClientPort() {
        return FunctionUtils.zeroArgFunc("inet_client_port", IntegerType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link PostgreInetType#INSTANCE}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-INFO-SESSION-TABLE">inet_server_addr () → inet<br/>
     * </a>
     */
    public static SimpleExpression inetServerAddr() {
        return FunctionUtils.zeroArgFunc("inet_server_addr", PostgreInetType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link IntegerType#INSTANCE}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-INFO-SESSION-TABLE">inet_server_port () → integer<br/>
     * </a>
     */
    public static SimpleExpression inetServerPort() {
        return FunctionUtils.zeroArgFunc("inet_server_port", IntegerType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link IntegerType#INSTANCE}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-INFO-SESSION-TABLE">pg_backend_pid () → integer<br/>
     * </a>
     */
    public static SimpleExpression pgBackendPid() {
        return FunctionUtils.zeroArgFunc("pg_backend_pid", IntegerType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link IntegerArrayType#PRIMITIVE_LINEAR}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-INFO-SESSION-TABLE">pg_blocking_pids ( integer ) → integer[]<br/>
     * </a>
     */
    public static SimpleExpression pgBlockingPids(Expression exp) {
        return FunctionUtils.oneArgFunc("pg_blocking_pids", exp, IntegerArrayType.PRIMITIVE_LINEAR);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link OffsetDateTimeType#INSTANCE}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-INFO-SESSION-TABLE">pg_conf_load_time () → timestamp with time zone<br/>
     * </a>
     */
    public static SimpleExpression pgConfLoadTime() {
        return FunctionUtils.zeroArgFunc("pg_conf_load_time", OffsetDateTimeType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link TextType#INSTANCE}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-INFO-SESSION-TABLE">pg_current_logfile ( [ text ] ) → text<br/>
     * </a>
     */
    public static SimpleExpression pgCurrentLogFile() {
        return FunctionUtils.zeroArgFunc("pg_current_logfile", TextType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link TextType#INSTANCE}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-INFO-SESSION-TABLE">pg_current_logfile ( [ text ] ) → text<br/>
     * </a>
     */
    public static SimpleExpression pgCurrentLogFile(Expression exp) {
        return FunctionUtils.oneArgFunc("pg_current_logfile", exp, TextType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link LongType#INSTANCE}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-INFO-SESSION-TABLE">pg_my_temp_schema () → oid<br/>
     * </a>
     */
    public static SimpleExpression pgMyTempSchema() {
        return FunctionUtils.zeroArgFunc("pg_my_temp_schema", LongType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link BooleanType#INSTANCE}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-INFO-SESSION-TABLE">pg_is_other_temp_schema ( oid ) → boolean<br/>
     * </a>
     */
    public static SimplePredicate pgIsOtherTempSchema(Expression exp) {
        return FunctionUtils.oneArgPredicateFunc("pg_is_other_temp_schema", exp);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link BooleanType#INSTANCE}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-INFO-SESSION-TABLE">pg_jit_available () → boolean<br/>
     * </a>
     */
    public static SimplePredicate pgJitAvailable() {
        return FunctionUtils.zeroArgFuncPredicate("pg_jit_available");
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link TextType#INSTANCE}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-INFO-SESSION-TABLE">pg_listening_channels () → setof text<br/>
     * </a>
     */
    public static _ColumnWithOrdinalityFunction pgListeningChannels() {
        return DialectFunctionUtils.zeroArgColumnFunction("pg_listening_channels", null, TextType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link DoubleType#INSTANCE}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-INFO-SESSION-TABLE">pg_notification_queue_usage () → double precision<br/>
     * </a>
     */
    public static SimpleExpression pgNotificationQueueUsage() {
        return FunctionUtils.zeroArgFunc("pg_notification_queue_usage", DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link OffsetDateTimeType#INSTANCE}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-INFO-SESSION-TABLE">pg_postmaster_start_time () → timestamp with time zone<br/>
     * </a>
     */
    public static SimpleExpression pgPostMasterStartTime() {
        return FunctionUtils.zeroArgFunc("pg_postmaster_start_time", OffsetDateTimeType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link IntegerArrayType#PRIMITIVE_LINEAR}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-INFO-SESSION-TABLE">pg_safe_snapshot_blocking_pids ( integer ) → integer[]<br/>
     * </a>
     */
    public static SimpleExpression pgSafeSnapshotBlockingPids(Expression exp) {
        return FunctionUtils.oneArgFunc("pg_safe_snapshot_blocking_pids", exp, IntegerArrayType.PRIMITIVE_LINEAR);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link IntegerType#INSTANCE}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-INFO-SESSION-TABLE">pg_trigger_depth () → integer<br/>
     * </a>
     */
    public static SimpleExpression pgTriggerDepth() {
        return FunctionUtils.zeroArgFunc("pg_trigger_depth", IntegerType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link TextType#INSTANCE}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-INFO-SESSION-TABLE">version () → text<br/>
     * </a>
     */
    public static SimpleExpression version() {
        return FunctionUtils.zeroArgFunc("version", TextType.INSTANCE);
    }


    /*-------------------below Access Privilege Inquiry Functions-------------------*/

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link BooleanType#INSTANCE}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-INFO-ACCESS-TABLE">has_any_column_privilege ( [ user name or oid, ] table text or oid, privilege text ) → boolean<br/>
     * </a>
     */
    public static SimplePredicate hasAnyColumnPrivilege(Expression table, Expression privilege) {
        return FunctionUtils.twoArgPredicateFunc("has_any_column_privilege", table, privilege);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link BooleanType#INSTANCE}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-INFO-ACCESS-TABLE">has_any_column_privilege ( [ user name or oid, ] table text or oid, privilege text ) → boolean<br/>
     * </a>
     */
    public static SimplePredicate hasAnyColumnPrivilege(Expression user, Expression table, Expression privilege) {
        return FunctionUtils.threeArgPredicateFunc("has_any_column_privilege", user, table, privilege);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link BooleanType#INSTANCE}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-INFO-ACCESS-TABLE">has_column_privilege ( [ user name or oid, ] table text or oid, column text or smallint, privilege text ) → boolean<br/>
     * </a>
     */
    public static SimplePredicate hasColumnPrivilege(Expression table, Expression column, Expression privilege) {
        return FunctionUtils.threeArgPredicateFunc("has_column_privilege", table, column, privilege);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link BooleanType#INSTANCE}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-INFO-ACCESS-TABLE">has_column_privilege ( [ user name or oid, ] table text or oid, column text or smallint, privilege text ) → boolean<br/>
     * </a>
     */
    public static SimplePredicate hasColumnPrivilege(Expression user, Expression table, Expression column, Expression privilege) {
        return FunctionUtils.fourArgPredicateFunc("has_column_privilege", user, table, column, privilege);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link BooleanType#INSTANCE}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-INFO-ACCESS-TABLE">has_database_privilege ( [ user name or oid, ] database text or oid, privilege text ) → boolean<br/>
     * </a>
     */
    public static SimplePredicate hasDatabasePrivilege(Expression database, Expression privilege) {
        return FunctionUtils.twoArgPredicateFunc("has_database_privilege", database, privilege);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link BooleanType#INSTANCE}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-INFO-ACCESS-TABLE">has_database_privilege ( [ user name or oid, ] database text or oid, privilege text ) → boolean<br/>
     * </a>
     */
    public static SimplePredicate hasDatabasePrivilege(Expression user, Expression database, Expression privilege) {
        return FunctionUtils.threeArgPredicateFunc("has_database_privilege", user, database, privilege);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link BooleanType#INSTANCE}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-INFO-ACCESS-TABLE">has_foreign_data_wrapper_privilege ( [ user name or oid, ] fdw text or oid, privilege text ) → boolean<br/>
     * </a>
     */
    public static SimplePredicate hasForeignDataWrapperPrivilege(Expression fdw, Expression privilege) {
        return FunctionUtils.twoArgPredicateFunc("has_foreign_data_wrapper_privilege", fdw, privilege);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link BooleanType#INSTANCE}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-INFO-ACCESS-TABLE">has_foreign_data_wrapper_privilege ( [ user name or oid, ] fdw text or oid, privilege text ) → boolean<br/>
     * </a>
     */
    public static SimplePredicate hasForeignDataWrapperPrivilege(Expression user, Expression fdw, Expression privilege) {
        return FunctionUtils.threeArgPredicateFunc("has_foreign_data_wrapper_privilege", user, fdw, privilege);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link BooleanType#INSTANCE}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-INFO-ACCESS-TABLE">has_function_privilege ( [ user name or oid, ] function text or oid, privilege text ) → boolean<br/>
     * </a>
     */
    public static SimplePredicate hasFunctionPrivilege(Expression function, Expression privilege) {
        return FunctionUtils.twoArgPredicateFunc("has_function_privilege", function, privilege);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link BooleanType#INSTANCE}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-INFO-ACCESS-TABLE">has_function_privilege ( [ user name or oid, ] function text or oid, privilege text ) → boolean<br/>
     * </a>
     */
    public static SimplePredicate hasFunctionPrivilege(Expression user, Expression function, Expression privilege) {
        return FunctionUtils.threeArgPredicateFunc("has_function_privilege", user, function, privilege);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link BooleanType#INSTANCE}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-INFO-ACCESS-TABLE">has_language_privilege ( [ user name or oid, ] language text or oid, privilege text ) → boolean<br/>
     * </a>
     */
    public static SimplePredicate hasLanguagePrivilege(Expression language, Expression privilege) {
        return FunctionUtils.twoArgPredicateFunc("has_language_privilege", language, privilege);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link BooleanType#INSTANCE}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-INFO-ACCESS-TABLE">has_language_privilege ( [ user name or oid, ] language text or oid, privilege text ) → boolean<br/>
     * </a>
     */
    public static SimplePredicate hasLanguagePrivilege(Expression user, Expression language, Expression privilege) {
        return FunctionUtils.threeArgPredicateFunc("has_language_privilege", user, language, privilege);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link BooleanType#INSTANCE}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-INFO-ACCESS-TABLE">has_parameter_privilege ( [ user name or oid, ] parameter text, privilege text ) → boolean<br/>
     * </a>
     */
    public static SimplePredicate hasParameterPrivilege(Expression parameter, Expression privilege) {
        return FunctionUtils.twoArgPredicateFunc("has_parameter_privilege", parameter, privilege);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link BooleanType#INSTANCE}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-INFO-ACCESS-TABLE">has_parameter_privilege ( [ user name or oid, ] parameter text, privilege text ) → boolean<br/>
     * </a>
     */
    public static SimplePredicate hasParameterPrivilege(Expression user, Expression parameter, Expression privilege) {
        return FunctionUtils.threeArgPredicateFunc("has_parameter_privilege", user, parameter, privilege);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link BooleanType#INSTANCE}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-INFO-ACCESS-TABLE">has_schema_privilege ( [ user name or oid, ] schema text or oid, privilege text ) → boolean<br/>
     * </a>
     */
    public static SimplePredicate hasSchemaPrivilege(Expression schema, Expression privilege) {
        return FunctionUtils.twoArgPredicateFunc("has_schema_privilege", schema, privilege);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link BooleanType#INSTANCE}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-INFO-ACCESS-TABLE">has_schema_privilege ( [ user name or oid, ] schema text or oid, privilege text ) → boolean<br/>
     * </a>
     */
    public static SimplePredicate hasSchemaPrivilege(Expression user, Expression schema, Expression privilege) {
        return FunctionUtils.threeArgPredicateFunc("has_schema_privilege", user, schema, privilege);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link BooleanType#INSTANCE}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-INFO-ACCESS-TABLE">has_sequence_privilege ( [ user name or oid, ] sequence text or oid, privilege text ) → boolean<br/>
     * </a>
     */
    public static SimplePredicate hasSequencePrivilege(Expression sequence, Expression privilege) {
        return FunctionUtils.twoArgPredicateFunc("has_sequence_privilege", sequence, privilege);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link BooleanType#INSTANCE}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-INFO-ACCESS-TABLE">has_sequence_privilege ( [ user name or oid, ] sequence text or oid, privilege text ) → boolean<br/>
     * </a>
     */
    public static SimplePredicate hasSequencePrivilege(Expression user, Expression sequence, Expression privilege) {
        return FunctionUtils.threeArgPredicateFunc("has_sequence_privilege", user, sequence, privilege);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link BooleanType#INSTANCE}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-INFO-ACCESS-TABLE">has_server_privilege ( [ user name or oid, ] server text or oid, privilege text ) → boolean<br/>
     * </a>
     */
    public static SimplePredicate hasServerPrivilege(Expression server, Expression privilege) {
        return FunctionUtils.twoArgPredicateFunc("has_server_privilege", server, privilege);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link BooleanType#INSTANCE}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-INFO-ACCESS-TABLE">has_server_privilege ( [ user name or oid, ] server text or oid, privilege text ) → boolean<br/>
     * </a>
     */
    public static SimplePredicate hasServerPrivilege(Expression user, Expression server, Expression privilege) {
        return FunctionUtils.threeArgPredicateFunc("has_server_privilege", user, server, privilege);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link BooleanType#INSTANCE}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-INFO-ACCESS-TABLE">has_table_privilege ( [ user name or oid, ] table text or oid, privilege text ) → boolean<br/>
     * </a>
     */
    public static SimplePredicate hasTablePrivilege(Expression table, Expression privilege) {
        return FunctionUtils.twoArgPredicateFunc("has_table_privilege", table, privilege);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link BooleanType#INSTANCE}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-INFO-ACCESS-TABLE">has_table_privilege ( [ user name or oid, ] table text or oid, privilege text ) → boolean<br/>
     * </a>
     */
    public static SimplePredicate hasTablePrivilege(Expression user, Expression table, Expression privilege) {
        return FunctionUtils.threeArgPredicateFunc("has_table_privilege", user, table, privilege);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link BooleanType#INSTANCE}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-INFO-ACCESS-TABLE">has_tablespace_privilege ( [ user name or oid, ] tablespace text or oid, privilege text ) → boolean<br/>
     * </a>
     */
    public static SimplePredicate hasTablespacePrivilege(Expression tablespace, Expression privilege) {
        return FunctionUtils.twoArgPredicateFunc("has_tablespace_privilege", tablespace, privilege);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link BooleanType#INSTANCE}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-INFO-ACCESS-TABLE">has_tablespace_privilege ( [ user name or oid, ] tablespace text or oid, privilege text ) → boolean<br/>
     * </a>
     */
    public static SimplePredicate hasTablespacePrivilege(Expression user, Expression tablespace, Expression privilege) {
        return FunctionUtils.threeArgPredicateFunc("has_tablespace_privilege", user, tablespace, privilege);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link BooleanType#INSTANCE}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-INFO-ACCESS-TABLE">has_type_privilege ( [ user name or oid, ] type text or oid, privilege text ) → boolean<br/>
     * </a>
     */
    public static SimplePredicate hasTypePrivilege(Expression type, Expression privilege) {
        return FunctionUtils.twoArgPredicateFunc("has_type_privilege", type, privilege);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link BooleanType#INSTANCE}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-INFO-ACCESS-TABLE">has_type_privilege ( [ user name or oid, ] type text or oid, privilege text ) → boolean<br/>
     * </a>
     */
    public static SimplePredicate hasTypePrivilege(Expression user, Expression type, Expression privilege) {
        return FunctionUtils.threeArgPredicateFunc("has_type_privilege", user, type, privilege);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link BooleanType#INSTANCE}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-INFO-ACCESS-TABLE">pg_has_role ( [ user name or oid, ] role text or oid, privilege text ) → boolean<br/>
     * </a>
     */
    public static SimplePredicate pgHasRole(Expression role, Expression privilege) {
        return FunctionUtils.twoArgPredicateFunc("pg_has_role", role, privilege);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link BooleanType#INSTANCE}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-INFO-ACCESS-TABLE">pg_has_role ( [ user name or oid, ] role text or oid, privilege text ) → boolean<br/>
     * </a>
     */
    public static SimplePredicate pgHasRole(Expression user, Expression role, Expression privilege) {
        return FunctionUtils.threeArgPredicateFunc("pg_has_role", user, role, privilege);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link BooleanType#INSTANCE}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-INFO-ACCESS-TABLE">row_security_active ( table text or oid ) → boolean<br/>
     * </a>
     */
    public static SimplePredicate rowSecurityActive(Expression table) {
        return FunctionUtils.oneArgPredicateFunc("row_security_active", table);
    }

    /*-------------------below aclitem Functions-------------------*/

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link PostgreAclItemArrayType#LINEAR}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-ACLITEM-FN-TABLE">acldefault ( type "char", ownerId oid ) → aclitem[]<br/>
     * </a>
     */
    public static SimpleExpression aclDefault(Expression type, Expression ownerId) {
        return FunctionUtils.twoArgFunc("acldefault", type, ownerId, PostgreAclItemArrayType.LINEAR);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: <ul>
     * <li>grantor : {@link LongType#INSTANCE}</li>
     * <li>grantee : {@link LongType#INSTANCE}</li>
     * <li>privilege_type : {@link TextType#INSTANCE}</li>
     * <li>is_grantable : {@link BooleanType#INSTANCE}</li>
     * </ul>
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-ACLITEM-FN-TABLE">aclexplode ( aclitem[] ) → setof record ( grantor oid, grantee oid, privilege_type text, is_grantable boolean )<br/>
     * </a>
     */
    public static _TabularWithOrdinalityFunction aclExplode(Expression exp) {
        final List<Selection> fieldList = _Collections.arrayList(4);

        fieldList.add(ArmySelections.forName("grantor", LongType.INSTANCE));
        fieldList.add(ArmySelections.forName("grantee", LongType.INSTANCE));
        fieldList.add(ArmySelections.forName("privilege_type", TextType.INSTANCE));
        fieldList.add(ArmySelections.forName("is_grantable", BooleanType.INSTANCE));

        return DialectFunctionUtils.oneArgTabularFunc("aclexplode", exp, fieldList);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link PostgreAclItemType#TEXT}
     *
     *
     * @param isGrantable in most case {@link SQLs#TRUE} or {@link SQLs#FALSE}
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-ACLITEM-FN-TABLE">makeaclitem ( grantee oid, grantor oid, privileges text, is_grantable boolean ) → aclitem<br/>
     * </a>
     */
    public static SimpleExpression makeAclItem(Expression grantee, Expression grantor, Expression privileges, Expression isGrantable) {
        return FunctionUtils.fourArgFunc("makeaclitem", grantee, grantor, privileges, isGrantable, PostgreAclItemType.TEXT);
    }


    /*-------------------below  Schema Visibility Inquiry Functions-------------------*/

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link BooleanType#INSTANCE}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-INFO-SCHEMA-TABLE">pg_collation_is_visible ( collation oid ) → boolean<br/>
     * </a>
     */
    public static SimplePredicate pgCollationIsVisible(Expression collation) {
        return FunctionUtils.oneArgPredicateFunc("pg_collation_is_visible", collation);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link BooleanType#INSTANCE}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-INFO-SCHEMA-TABLE">pg_conversion_is_visible ( conversion oid ) → boolean<br/>
     * </a>
     */
    public static SimplePredicate pgConversionIsVisible(Expression conversion) {
        return FunctionUtils.oneArgPredicateFunc("pg_conversion_is_visible", conversion);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link BooleanType#INSTANCE}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-INFO-SCHEMA-TABLE">pg_function_is_visible ( function oid ) → boolean<br/>
     * </a>
     */
    public static SimplePredicate pgFunctionIsVisible(Expression function) {
        return FunctionUtils.oneArgPredicateFunc("pg_function_is_visible", function);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link BooleanType#INSTANCE}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-INFO-SCHEMA-TABLE">pg_opclass_is_visible ( opclass oid ) → boolean<br/>
     * </a>
     */
    public static SimplePredicate pgOpClassIsVisible(Expression opclass) {
        return FunctionUtils.oneArgPredicateFunc("pg_opclass_is_visible", opclass);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link BooleanType#INSTANCE}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-INFO-SCHEMA-TABLE">pg_operator_is_visible ( operator oid ) → boolean<br/>
     * </a>
     */
    public static SimplePredicate pgOperatorIsVisible(Expression operator) {
        return FunctionUtils.oneArgPredicateFunc("pg_operator_is_visible", operator);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link BooleanType#INSTANCE}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-INFO-SCHEMA-TABLE">pg_opfamily_is_visible ( opclass oid ) → boolean<br/>
     * </a>
     */
    public static SimplePredicate pgOpFamilyIsVisible(Expression opClass) {
        return FunctionUtils.oneArgPredicateFunc("pg_opfamily_is_visible", opClass);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link BooleanType#INSTANCE}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-INFO-SCHEMA-TABLE">pg_statistics_obj_is_visible ( stat oid ) → boolean<br/>
     * </a>
     */
    public static SimplePredicate pgStatisticsObjIsVisible(Expression stat) {
        return FunctionUtils.oneArgPredicateFunc("pg_statistics_obj_is_visible", stat);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link BooleanType#INSTANCE}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-INFO-SCHEMA-TABLE">pg_table_is_visible ( table oid ) → boolean<br/>
     * </a>
     */
    public static SimplePredicate pgTableIsVisible(Expression table) {
        return FunctionUtils.oneArgPredicateFunc("pg_table_is_visible", table);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link BooleanType#INSTANCE}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-INFO-SCHEMA-TABLE">pg_ts_config_is_visible ( config oid ) → boolean<br/>
     * </a>
     */
    public static SimplePredicate pgTsConfigIsVisible(Expression config) {
        return FunctionUtils.oneArgPredicateFunc("pg_ts_config_is_visible", config);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link BooleanType#INSTANCE}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-INFO-SCHEMA-TABLE">pg_ts_dict_is_visible ( dict oid ) → boolean<br/>
     * </a>
     */
    public static SimplePredicate pgTsDictIsVisible(Expression dict) {
        return FunctionUtils.oneArgPredicateFunc("pg_ts_dict_is_visible", dict);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link BooleanType#INSTANCE}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-INFO-SCHEMA-TABLE">pg_ts_parser_is_visible ( parser oid ) → boolean<br/>
     * </a>
     */
    public static SimplePredicate pgTsParserIsVisible(Expression parser) {
        return FunctionUtils.oneArgPredicateFunc("pg_ts_parser_is_visible", parser);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link BooleanType#INSTANCE}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-INFO-SCHEMA-TABLE">pg_ts_template_is_visible ( template oid ) → boolean<br/>
     * </a>
     */
    public static SimplePredicate pgTsTemplateIsVisible(Expression template) {
        return FunctionUtils.oneArgPredicateFunc("pg_ts_template_is_visible", template);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link BooleanType#INSTANCE}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-INFO-SCHEMA-TABLE">pg_type_is_visible ( type oid ) → boolean<br/>
     * </a>
     */
    public static SimplePredicate pgTypeIsVisible(Expression type) {
        return FunctionUtils.oneArgPredicateFunc("pg_type_is_visible", type);
    }


    /*-------------------below System Catalog Information Functions-------------------*/

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link TextType#INSTANCE}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-INFO-CATALOG-TABLE">format_type ( type oid, typemod integer ) → text<br/>
     * </a>
     */
    public static SimpleExpression formatType(Expression type, Expression typeMode) {
        return FunctionUtils.twoArgFunc("format_type", type, typeMode, TextType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link IntegerType#INSTANCE}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-INFO-CATALOG-TABLE">pg_char_to_encoding ( encoding name ) → integer<br/>
     * </a>
     */
    public static SimpleExpression pgCharToEncoding(Expression encoding) {
        return FunctionUtils.oneArgFunc("pg_char_to_encoding", encoding, IntegerType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link TextType#INSTANCE}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-INFO-CATALOG-TABLE">pg_encoding_to_char ( encoding integer ) → name<br/>
     * </a>
     */
    public static SimpleExpression pgEncodingToChar(Expression encoding) {
        return FunctionUtils.oneArgFunc("pg_encoding_to_char", encoding, TextType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: <ul>
     * <li>fktable : {@link TextType#INSTANCE}</li>
     * <li>fkcols : {@link TextArrayType#LINEAR}</li>
     * <li>pktable : {@link TextType#INSTANCE}</li>
     * <li>pkcols : {@link TextArrayType#LINEAR}</li>
     * <li>is_array : {@link BooleanType#INSTANCE}</li>
     * <li>is_opt : {@link BooleanType#INSTANCE}</li>
     * <li>ordinality (optional) : {@link LongType#INSTANCE} ,see {@link io.army.criteria.impl.Functions._WithOrdinalityClause}</li>
     * </ul>
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-INFO-CATALOG-TABLE">pg_get_catalog_foreign_keys () → setof record ( fktable regclass, fkcols text[], pktable regclass, pkcols text[], is_array boolean, is_opt boolean )<br/>
     * </a>
     */
    public static _TabularWithOrdinalityFunction pgGetCatalogForeignKeys() {
        final List<Selection> fieldList = _Collections.arrayList(6);

        fieldList.add(ArmySelections.forName("fktable", TextType.INSTANCE));
        fieldList.add(ArmySelections.forName("fkcols", TextArrayType.LINEAR));
        fieldList.add(ArmySelections.forName("pktable", TextType.INSTANCE));
        fieldList.add(ArmySelections.forName("pkcols", TextArrayType.LINEAR));

        fieldList.add(ArmySelections.forName("is_array", BooleanType.INSTANCE));
        fieldList.add(ArmySelections.forName("is_opt", BooleanType.INSTANCE));
        return DialectFunctionUtils.zeroArgTabularFunc("pg_get_catalog_foreign_keys", fieldList);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link TextType#INSTANCE}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-INFO-CATALOG-TABLE">pg_get_constraintdef ( constraint oid [, pretty boolean ] ) → text<br/>
     * </a>
     */
    public static SimpleExpression pgGetConstraintDef(Expression constraint) {
        return FunctionUtils.oneArgFunc("pg_get_constraintdef", constraint, TextType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link TextType#INSTANCE}
     *
     *
     * @param pretty in most case {@link SQLs#TRUE} or {@link SQLs#FALSE}
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-INFO-CATALOG-TABLE">pg_get_constraintdef ( constraint oid [, pretty boolean ] ) → text<br/>
     * </a>
     */
    public static SimpleExpression pgGetConstraintDef(Expression constraint, Expression pretty) {
        return FunctionUtils.twoArgFunc("pg_get_constraintdef", constraint, pretty, TextType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link TextType#INSTANCE}
     *
     *
     * @param pretty in most case {@link SQLs#TRUE} or {@link SQLs#FALSE}
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-INFO-CATALOG-TABLE">pg_get_expr ( expr pg_node_tree, relation oid [, pretty boolean ] ) → text<br/>
     * </a>
     */
    public static SimpleExpression pgGetExpr(Expression expr, Expression relation, Expression pretty) {
        // TODO hwo to pg_node_tree ?
        return FunctionUtils.threeArgFunc("pg_get_expr", expr, relation, pretty, TextType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link TextType#INSTANCE}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-INFO-CATALOG-TABLE">pg_get_functiondef ( func oid ) → text<br/>
     * </a>
     */
    public static SimpleExpression pgGetFunctionDef(Expression func) {
        return FunctionUtils.oneArgFunc("pg_get_functiondef", func, TextType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link TextType#INSTANCE}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-INFO-CATALOG-TABLE">pg_get_function_arguments ( func oid ) → text<br/>
     * </a>
     */
    public static SimpleExpression pgGetFunctionArguments(Expression func) {
        return FunctionUtils.oneArgFunc("pg_get_function_arguments", func, TextType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link TextType#INSTANCE}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-INFO-CATALOG-TABLE">pg_get_function_identity_arguments ( func oid ) → text<br/>
     * </a>
     */
    public static SimpleExpression pgGetFunctionIdentityArguments(Expression func) {
        return FunctionUtils.oneArgFunc("pg_get_function_identity_arguments", func, TextType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link TextType#INSTANCE}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-INFO-CATALOG-TABLE">pg_get_function_result ( func oid ) → text<br/>
     * </a>
     */
    public static SimpleExpression pgGetFunctionResult(Expression func) {
        return FunctionUtils.oneArgFunc("pg_get_function_result", func, TextType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link TextType#INSTANCE}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-INFO-CATALOG-TABLE">pg_get_indexdef ( index oid [, column integer, pretty boolean ] ) → text<br/>
     * </a>
     */
    public static SimpleExpression pgGetIndexDef(Expression func) {
        return FunctionUtils.oneArgFunc("pg_get_indexdef", func, TextType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link TextType#INSTANCE}
     *
     *
     * @param pretty in most case {@link SQLs#TRUE} or {@link SQLs#FALSE}
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-INFO-CATALOG-TABLE">pg_get_indexdef ( index oid [, column integer, pretty boolean ] ) → text<br/>
     * </a>
     */
    public static SimpleExpression pgGetIndexDef(Expression func, Expression column, Expression pretty) {
        return FunctionUtils.threeArgFunc("pg_get_indexdef", func, column, pretty, TextType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: <ul>
     * <li>word : {@link TextType#INSTANCE}</li>
     * <li>catcode : {@link CharacterType#INSTANCE}</li>
     * <li>barelabel : {@link BooleanType#INSTANCE}</li>
     * <li>catdesc : {@link TextType#INSTANCE}</li>
     * <li>baredesc : {@link TextType#INSTANCE}</li>
     * <li>ordinality (optional) : {@link LongType#INSTANCE} ,see {@link io.army.criteria.impl.Functions._WithOrdinalityClause}</li>
     * </ul>
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-INFO-CATALOG-TABLE">pg_get_keywords () → setof record ( word text, catcode "char", barelabel boolean, catdesc text, baredesc text )<br/>
     * </a>
     */
    public static _TabularWithOrdinalityFunction pgGetKeywords() {
        final List<Selection> fieldList = _Collections.arrayList(5);

        fieldList.add(ArmySelections.forName("word", TextType.INSTANCE));
        fieldList.add(ArmySelections.forName("catcode", CharacterType.INSTANCE));
        fieldList.add(ArmySelections.forName("barelabel", BooleanType.INSTANCE));
        fieldList.add(ArmySelections.forName("catdesc", TextType.INSTANCE));

        fieldList.add(ArmySelections.forName("baredesc", TextType.INSTANCE));
        return DialectFunctionUtils.zeroArgTabularFunc("pg_get_keywords", fieldList);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link TextType#INSTANCE}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-INFO-CATALOG-TABLE">pg_get_ruledef ( rule oid [, pretty boolean ] ) → text<br/>
     * </a>
     */
    public static SimpleExpression pgGetRuleDef(Expression rule) {
        return FunctionUtils.oneArgFunc("pg_get_ruledef", rule, TextType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link TextType#INSTANCE}
     *
     *
     * @param pretty in most case {@link SQLs#TRUE} or {@link SQLs#FALSE}
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-INFO-CATALOG-TABLE">pg_get_ruledef ( rule oid [, pretty boolean ] ) → text<br/>
     * </a>
     */
    public static SimpleExpression pgGetRuleDef(Expression rule, Expression pretty) {
        return FunctionUtils.twoArgFunc("pg_get_ruledef", rule, pretty, TextType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link TextType#INSTANCE}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-INFO-CATALOG-TABLE">pg_get_serial_sequence ( table text, column text ) → text<br/>
     * </a>
     */
    public static SimpleExpression pgGetSerialSequence(Expression table, Expression column) {
        return FunctionUtils.twoArgFunc("pg_get_serial_sequence", table, column, TextType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link TextType#INSTANCE}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-INFO-CATALOG-TABLE">pg_get_statisticsobjdef ( statobj oid ) → text<br/>
     * </a>
     */
    public static SimpleExpression pgGetStatisticsObjDef(Expression statObj) {
        return FunctionUtils.oneArgFunc("pg_get_statisticsobjdef", statObj, TextType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link TextType#INSTANCE}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-INFO-CATALOG-TABLE">pg_get_triggerdef ( trigger oid [, pretty boolean ] ) → text<br/>
     * </a>
     */
    public static SimpleExpression pgGetTriggerDef(Expression trigger) {
        return FunctionUtils.oneArgFunc("pg_get_triggerdef", trigger, TextType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link TextType#INSTANCE}
     *
     *
     * @param pretty in most case {@link SQLs#TRUE} or {@link SQLs#FALSE}
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-INFO-CATALOG-TABLE">pg_get_triggerdef ( trigger oid [, pretty boolean ] ) → text<br/>
     * </a>
     */
    public static SimpleExpression pgGetTriggerDef(Expression trigger, Expression pretty) {
        return FunctionUtils.twoArgFunc("pg_get_triggerdef", trigger, pretty, TextType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link TextType#INSTANCE}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-INFO-CATALOG-TABLE">pg_get_userbyid ( role oid ) → name<br/>
     * </a>
     */
    public static SimpleExpression pgGetUserById(Expression role) {
        return FunctionUtils.oneArgFunc("pg_get_userbyid", role, TextType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link TextType#INSTANCE}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-INFO-CATALOG-TABLE">pg_get_viewdef ( view oid [, pretty boolean ] ) → text<br/>
     * pg_get_viewdef ( view oid, wrap_column integer ) → text<br/>
     * pg_get_viewdef ( view text [, pretty boolean ] ) → text<br/>
     * </a>
     */
    public static SimpleExpression pgGetViewDef(Expression view) {
        return FunctionUtils.oneArgFunc("pg_get_viewdef", view, TextType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link TextType#INSTANCE}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-INFO-CATALOG-TABLE">pg_get_viewdef ( view oid [, pretty boolean ] ) → text<br/>
     * pg_get_viewdef ( view oid, wrap_column integer ) → text<br/>
     * pg_get_viewdef ( view text [, pretty boolean ] ) → text<br/>
     * </a>
     */
    public static SimpleExpression pgGetViewDef(Expression view, Expression exp) {
        return FunctionUtils.twoArgFunc("pg_get_viewdef", view, exp, TextType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link BooleanType#INSTANCE}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-INFO-CATALOG-TABLE">pg_index_column_has_property ( index regclass, column integer, property text ) → boolean<br/>
     * </a>
     */
    public static SimplePredicate pgIndexColumnHasProperty(Expression index, Expression column, Expression property) {
        return FunctionUtils.threeArgPredicateFunc("pg_index_column_has_property", index, column, property);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link BooleanType#INSTANCE}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-INFO-CATALOG-TABLE">pg_index_has_property ( index regclass, property text ) → boolean<br/>
     * </a>
     */
    public static SimplePredicate pgIndexHasProperty(Expression index, Expression property) {
        return FunctionUtils.twoArgPredicateFunc("pg_index_has_property", index, property);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link BooleanType#INSTANCE}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-INFO-CATALOG-TABLE">pg_indexam_has_property ( am oid, property text ) → boolean<br/>
     * </a>
     */
    public static SimplePredicate pgIndexAmHasProperty(Expression am, Expression property) {
        return FunctionUtils.twoArgPredicateFunc("pg_indexam_has_property", am, property);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: <ul>
     * <li>option_name : {@link TextType#INSTANCE}</li>
     * <li>option_value : {@link TextType#INSTANCE}</li>
     * <li>ordinality (optional) : {@link LongType#INSTANCE} ,see {@link io.army.criteria.impl.Functions._WithOrdinalityClause}</li>
     * </ul>
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-INFO-CATALOG-TABLE">pg_options_to_table ( options_array text[] ) → setof record ( option_name text, option_value text )<br/>
     * </a>
     */
    public static _TabularWithOrdinalityFunction pgOptionsToTable(Expression optionsArray) {
        final List<Selection> fieldList;
        fieldList = ArrayUtils.of(
                ArmySelections.forName("option_name", TextType.INSTANCE),
                ArmySelections.forName("option_value", TextType.INSTANCE)
        );
        return DialectFunctionUtils.oneArgTabularFunc("pg_options_to_table", optionsArray, fieldList);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link TextArrayType#LINEAR}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-INFO-CATALOG-TABLE">pg_settings_get_flags ( guc text ) → text[]<br/>
     * </a>
     */
    public static SimpleExpression pgSettingsGetFlags(Expression guc) {
        return FunctionUtils.oneArgFunc("pg_settings_get_flags", guc, TextArrayType.LINEAR);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: <ul>
     * <li> "Anonymous field" ( you must use as clause definite filed name) : {@link LongType#INSTANCE}</li>
     * <li>ordinality (optional) : {@link LongType#INSTANCE} ,see {@link io.army.criteria.impl.Functions._WithOrdinalityClause}</li>
     * </ul>
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-INFO-CATALOG-TABLE">pg_tablespace_databases ( tablespace oid ) → setof oid<br/>
     * </a>
     */
    public static _ColumnWithOrdinalityFunction pgTablespaceDatabases(Expression tablespace) {
        return DialectFunctionUtils.oneArgColumnFunction("pg_tablespace_databases", tablespace, null, LongType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link TextType#INSTANCE}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-INFO-CATALOG-TABLE">pg_tablespace_location ( tablespace oid ) → text<br/>
     * </a>
     */
    public static SimpleExpression pgTablespaceLocation(Expression tablespace) {
        return FunctionUtils.oneArgFunc("pg_tablespace_location", tablespace, TextType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link TextType#INSTANCE}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-INFO-CATALOG-TABLE">pg_typeof ( "any" ) → regtype<br/>
     * </a>
     */
    public static SimpleExpression pgTypeOf(Expression any) {
        return FunctionUtils.oneArgFunc("pg_typeof", any, TextType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link TextType#INSTANCE}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-INFO-CATALOG-TABLE">COLLATION FOR ( "any" ) → text<br/>
     * </a>
     */
    public static SimpleExpression collationSpaceFor(Expression any) {
        return FunctionUtils.oneArgFunc("COLLATION FOR", any, TextType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link TextType#INSTANCE}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-INFO-CATALOG-TABLE">to_regclass ( text ) → regclass<br/>
     * </a>
     */
    public static SimpleExpression toRegClass(Expression exp) {
        return FunctionUtils.oneArgFunc("to_regclass", exp, TextType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link TextType#INSTANCE}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-INFO-CATALOG-TABLE">to_regcollation ( text ) → regcollation<br/>
     * </a>
     */
    public static SimpleExpression toRegCollation(Expression exp) {
        return FunctionUtils.oneArgFunc("to_regcollation", exp, TextType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link TextType#INSTANCE}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-INFO-CATALOG-TABLE">to_regnamespace ( text ) → regnamespace<br/>
     * </a>
     */
    public static SimpleExpression toRegNamespace(Expression exp) {
        return FunctionUtils.oneArgFunc("to_regnamespace", exp, TextType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link TextType#INSTANCE}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-INFO-CATALOG-TABLE">o_regoper ( text ) → regoper<br/>
     * </a>
     */
    public static SimpleExpression toRegOper(Expression exp) {
        return FunctionUtils.oneArgFunc("to_regoper", exp, TextType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link TextType#INSTANCE}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-INFO-CATALOG-TABLE">to_regoperator ( text ) → regoperator<br/>
     * </a>
     */
    public static SimpleExpression toRegOperator(Expression exp) {
        return FunctionUtils.oneArgFunc("to_regoperator", exp, TextType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link TextType#INSTANCE}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-INFO-CATALOG-TABLE">to_regproc ( text ) → regproc<br/>
     * </a>
     */
    public static SimpleExpression toRegProc(Expression exp) {
        return FunctionUtils.oneArgFunc("to_regproc", exp, TextType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link TextType#INSTANCE}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-INFO-CATALOG-TABLE">to_regprocedure ( text ) → regprocedure<br/>
     * </a>
     */
    public static SimpleExpression toRegProcedure(Expression exp) {
        return FunctionUtils.oneArgFunc("to_regprocedure", exp, TextType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link TextType#INSTANCE}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-INFO-CATALOG-TABLE">to_regrole ( text ) → regrole<br/>
     * </a>
     */
    public static SimpleExpression toRegRole(Expression exp) {
        return FunctionUtils.oneArgFunc("to_regrole", exp, TextType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link TextType#INSTANCE}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-INFO-CATALOG-TABLE">to_regtype ( text ) → regtype<br/>
     * </a>
     */
    public static SimpleExpression toRegType(Expression exp) {
        return FunctionUtils.oneArgFunc("to_regtype", exp, TextType.INSTANCE);
    }

    /*-------------------below Object Information and Addressing Functions-------------------*/

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link TextType#INSTANCE}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-INFO-OBJECT-TABLE">pg_describe_object ( classid oid, objid oid, objsubid integer ) → text<br/>
     * </a>
     */
    public static SimpleExpression pgDescribeObject(Expression classId, Expression objId, Expression objSubId) {
        return FunctionUtils.threeArgFunc("pg_describe_object", classId, objId, objSubId, TextType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:
     * <ul>
     *     <li>type : {@link TextType#INSTANCE}</li>
     *     <li>schema : {@link TextType#INSTANCE}</li>
     *     <li>name : {@link TextType#INSTANCE}</li>
     *     <li>identity : {@link TextType#INSTANCE}</li>
     *     <li>ordinality (optional) : {@link LongType#INSTANCE} ,see {@link io.army.criteria.impl.Functions._WithOrdinalityClause}</li>
     * </ul>
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-INFO-OBJECT-TABLE">pg_identify_object ( classid oid, objid oid, objsubid integer ) → record ( type text, schema text, name text, identity text )<br/>
     * </a>
     */
    public static _TabularWithOrdinalityFunction pgIdentifyObject(Expression classId, Expression objId, Expression objSubId) {
        final List<Selection> fieldList;
        fieldList = ArrayUtils.of(
                ArmySelections.forName("type", TextType.INSTANCE),
                ArmySelections.forName("schema", TextType.INSTANCE),
                ArmySelections.forName("name", TextType.INSTANCE),
                ArmySelections.forName("identity", TextType.INSTANCE)
        );
        return DialectFunctionUtils.threeArgTabularFunc("pg_identify_object", classId, objId, objSubId, fieldList);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:
     * <ul>
     *     <li>type : {@link TextType#INSTANCE}</li>
     *     <li>object_names : {@link TextArrayType#LINEAR}</li>
     *     <li>object_args : {@link TextArrayType#LINEAR}</li>
     *     <li>ordinality (optional) : {@link LongType#INSTANCE} ,see {@link io.army.criteria.impl.Functions._WithOrdinalityClause}</li>
     * </ul>
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-INFO-OBJECT-TABLE">pg_identify_object_as_address ( classid oid, objid oid, objsubid integer ) → record ( type text, object_names text[], object_args text[] )<br/>
     * </a>
     */
    public static _TabularWithOrdinalityFunction pgIdentifyObjectAsAddress(Expression classId, Expression objId, Expression objSubId) {
        final List<Selection> fieldList;
        fieldList = ArrayUtils.of(
                ArmySelections.forName("type", TextType.INSTANCE),
                ArmySelections.forName("object_names", TextArrayType.LINEAR),
                ArmySelections.forName("object_args", TextArrayType.LINEAR)
        );
        return DialectFunctionUtils.threeArgTabularFunc("pg_identify_object_as_address", classId, objId, objSubId, fieldList);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:
     * <ul>
     *     <li>classid : {@link LongType#INSTANCE}</li>
     *     <li>objid : {@link LongType#INSTANCE}</li>
     *     <li>objsubid : {@link IntegerType#INSTANCE}</li>
     *     <li>ordinality (optional) : {@link LongType#INSTANCE} ,see {@link io.army.criteria.impl.Functions._WithOrdinalityClause}</li>
     * </ul>
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-INFO-OBJECT-TABLE">pg_get_object_address ( type text, object_names text[], object_args text[] ) → record ( classid oid, objid oid, objsubid integer )<br/>
     * </a>
     */
    public static _TabularWithOrdinalityFunction pgGetObjectAddress(Expression type, Expression objectNames, Expression objectArgs) {
        final List<Selection> fieldList;
        fieldList = ArrayUtils.of(
                ArmySelections.forName("classid", LongType.INSTANCE),
                ArmySelections.forName("objid", LongType.INSTANCE),
                ArmySelections.forName("objsubid", IntegerType.INSTANCE)
        );
        return DialectFunctionUtils.threeArgTabularFunc("pg_get_object_address", type, objectNames, objectArgs, fieldList);
    }




    /*-------------------below private method -------------------*/

    /**
     * @see #tsDebug(Expression)
     * @see #tsDebug(Expression, Expression)
     */
    private static _TabularWithOrdinalityFunction _tsDebug(final @Nullable Expression config, final Expression document) {
        final List<Selection> fieldList = _Collections.arrayList(6);


        fieldList.add(ArmySelections.forName("alias", TextType.INSTANCE));
        fieldList.add(ArmySelections.forName("description", TextType.INSTANCE));
        fieldList.add(ArmySelections.forName("token", TextType.INSTANCE));
        fieldList.add(ArmySelections.forName("dictionaries", TextArrayType.LINEAR));

        fieldList.add(ArmySelections.forName("dictionary", TextType.INSTANCE));
        fieldList.add(ArmySelections.forName("lexemes", TextArrayType.LINEAR));

        final String name = "ts_debug";
        final _TabularWithOrdinalityFunction func;
        if (config == null) {
            func = DialectFunctionUtils.oneArgTabularFunc(name, document, fieldList);
        } else {
            func = DialectFunctionUtils.twoArgTabularFunc(name, config, document, fieldList);
        }
        return func;
    }

    /**
     * <p>
     * The {@link MappingType} of function returned fields type:<ol>
     * <li>word {@link TextType}</li>
     * <li>ndoc {@link IntegerType}</li>
     * <li>nentry {@link IntegerType}</li>
     * <li>ordinality (this is optional) {@link LongType},see {@link _WithOrdinalityClause#withOrdinality()}</li>
     * </ol>
     *
     *
     * @see #tsStat(Expression)
     * @see #tsStat(Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-textsearch.html#TEXTSEARCH-FUNCTIONS-DEBUG-TABLE">ts_stat ( sqlquery text [, weights text ] ) → setof record ( word text, ndoc integer, nentry integer )<br/>
     * Executes the sqlquery, which must return a single tsvector column, and returns statistics about each distinct lexeme contained in the data.<br/>
     * ts_stat('SELECT vector FROM apod') → (foo,10,15)<br/>
     * </a>
     */
    private static _TabularWithOrdinalityFunction _tsStat(final Expression sqlQuery, final @Nullable Expression weights) {
        final List<Selection> fieldList = _Collections.arrayList(3);

        fieldList.add(ArmySelections.forName("word", TextType.INSTANCE));
        fieldList.add(ArmySelections.forName("ndoc", IntegerType.INSTANCE));
        fieldList.add(ArmySelections.forName("nentry", IntegerType.INSTANCE));

        final String name = "ts_stat";
        _TabularWithOrdinalityFunction func;
        if (weights == null) {
            func = DialectFunctionUtils.oneArgTabularFunc(name, sqlQuery, fieldList);
        } else {
            func = DialectFunctionUtils.twoArgTabularFunc(name, sqlQuery, weights, fieldList);
        }
        return func;
    }


    /**
     * @see #arrayFill(Expression, Expression)
     * @see #arrayFill(Expression, Expression, Expression)
     */
    private static SimpleExpression _arrayFill(final Expression anyElement, final Expression dimensions,
                                               final @Nullable Expression bounds) {

        final String name = "array_fill";
        final SimpleExpression func;
        if (bounds == null) {
            func = FunctionUtils.twoArgFunc(name, anyElement, dimensions,
                    _returnType(anyElement, MappingType::arrayTypeOfThis)
            );
        } else {
            func = FunctionUtils.threeArgFunc(name, anyElement, dimensions, bounds,
                    _returnType(anyElement, MappingType::arrayTypeOfThis)
            );
        }
        return func;
    }


}
