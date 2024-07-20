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

package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.dialect._DialectUtils;
import io.army.mapping.*;
import io.army.mapping.array.TextArrayType;
import io.army.mapping.array.XmlArrayType;
import io.army.mapping.optional.JsonPathType;
import io.army.mapping.optional.NoCastTextType;
import io.army.mapping.postgre.PostgreTsQueryType;
import io.army.mapping.postgre.PostgreTsVectorType;
import io.army.meta.TableMeta;
import io.army.util.ArrayUtils;
import io.army.util._Collections;

import io.army.lang.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;

/**
 * <p>
 * This class hold tsvectory/ tsquery/xml /json/jsonb function method.
 * * @since 0.6.0
 */
@SuppressWarnings("unused")
abstract class PostgreDocumentFunctions extends PostgreMiscellaneous2Functions {

    /**
     * package constructor
     */
    PostgreDocumentFunctions() {
    }

    public interface XmlNameSpaces extends Item {

    }

    public interface NullTreatMode extends Expression {

    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  PostgreTsVectorType}
     * *
     *
     * @param funcRef the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                <ul>
     *                    <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                    <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                    <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                    <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                    <li>developer custom method</li>
     *                </ul>.
     *                The first argument of funcRef always is {@link TextArrayType} with one dimension array.
     * @param value   non-null,it will be passed to funcRef as the second argument of funcRef
     * @see #arrayToTsVector(Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-textsearch.html#TEXTSEARCH-FUNCTIONS-TABLE">array_to_tsvector ( text[] ) → tsvector</a>
     */
    public static <T> Expression arrayToTsVector(BiFunction<MappingType, T, Expression> funcRef, T value) {
        return arrayToTsVector(funcRef.apply(TextArrayType.from(String[].class), value));
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  PostgreTsVectorType}
     * *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-textsearch.html#TEXTSEARCH-FUNCTIONS-TABLE">array_to_tsvector ( text[] ) → tsvector</a>
     */
    public static SimpleExpression arrayToTsVector(Expression exp) {
        return FunctionUtils.oneArgFunc("ARRAY_TO_TSVECTOR", exp, PostgreTsVectorType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  PostgreTsQueryType}
     * *
     *
     * @param funcRefForQuery the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                        <ul>
     *                            <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                            <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                            <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                            <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                            <li>developer custom method</li>
     *                        </ul>.
     *                        the first argument of funcRefForQuery always is {@link TextType#INSTANCE}.
     * @param query           non-null,it will be passed to funcRefForQuery as the second argument of funcRefForQuery
     * @see #plainToTsQuery(Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-textsearch.html#TEXTSEARCH-FUNCTIONS-TABLE">plainto_tsquery ( [ config regconfig, ] query text ) → tsquery</a>
     */
    public static <T> Expression plainToTsQuery(BiFunction<MappingType, T, Expression> funcRefForQuery, T query) {
        return plainToTsQuery(funcRefForQuery.apply(TextType.INSTANCE, query));
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  PostgreTsQueryType}
     * *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-textsearch.html#TEXTSEARCH-FUNCTIONS-TABLE">plainto_tsquery ( [ config regconfig, ] query text ) → tsquery</a>
     */
    public static SimpleExpression plainToTsQuery(Expression query) {
        return FunctionUtils.oneArgFunc("PLAINTO_TSQUERY", query, PostgreTsQueryType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  PostgreTsQueryType}
     * *
     *
     * @param funcRefForConfig the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                         <ul>
     *                             <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                             <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                             <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                             <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                             <li>developer custom method</li>
     *                         </ul>.
     *                         The first argument of funcRefForConfig always is {@link StringType#INSTANCE}.
     * @param config           non-null,it will be passed to funcRefForConfig as the second argument of funcRefForConfig
     * @param funcRefForQuery  the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                         <ul>
     *                             <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                             <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                             <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                             <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                             <li>developer custom method</li>
     *                         </ul>.
     *                         the first argument of funcRefForQuery always is {@link TextType#INSTANCE}.
     * @param query            non-null,it will be passed to funcRefForQuery as the second argument of funcRefForQuery
     * @see #plainToTsQuery(Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-textsearch.html#TEXTSEARCH-FUNCTIONS-TABLE">plainto_tsquery ( [ config regconfig, ] query text ) → tsquery</a>
     */
    public static <T, U> Expression plainToTsQuery(BiFunction<MappingType, T, Expression> funcRefForConfig, T config,
                                                         BiFunction<MappingType, U, Expression> funcRefForQuery, U query) {
        return plainToTsQuery(funcRefForConfig.apply(StringType.INSTANCE, config),
                funcRefForQuery.apply(TextType.INSTANCE, query)
        );
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  PostgreTsQueryType}
     * *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-textsearch.html#TEXTSEARCH-FUNCTIONS-TABLE">plainto_tsquery ( [ config regconfig, ] query text ) → tsquery</a>
     */
    public static SimpleExpression plainToTsQuery(Expression config, Expression query) {
        return FunctionUtils.twoArgFunc("PLAINTO_TSQUERY", config, query, PostgreTsQueryType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  PostgreTsQueryType}
     * *
     *
     * @param funcRefForQuery the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                        <ul>
     *                            <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                            <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                            <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                            <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                            <li>developer custom method</li>
     *                        </ul>.
     *                        he first argument of funcRefForQuery always is {@link TextType#INSTANCE}.
     * @param query           non-null,it will be passed to funcRefForQuery as the second argument of funcRefForQuery
     * @see #phraseToTsQuery(Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-textsearch.html#TEXTSEARCH-FUNCTIONS-TABLE">phraseto_tsquery ( [ config regconfig, ] query text ) → tsquery</a>
     */
    public static <T> Expression phraseToTsQuery(BiFunction<MappingType, T, Expression> funcRefForQuery, T query) {
        return phraseToTsQuery(funcRefForQuery.apply(TextType.INSTANCE, query));
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  PostgreTsQueryType}
     * *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-textsearch.html#TEXTSEARCH-FUNCTIONS-TABLE">phraseto_tsquery ( [ config regconfig, ] query text ) → tsquery</a>
     */
    public static SimpleExpression phraseToTsQuery(Expression query) {
        return FunctionUtils.oneArgFunc("PHRASETO_TSQUERY", query, PostgreTsQueryType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  PostgreTsQueryType}
     * *
     *
     * @param funcRefForConfig the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                         <ul>
     *                             <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                             <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                             <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                             <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                             <li>developer custom method</li>
     *                         </ul>.
     *                         The first argument of funcRefForConfig always is {@link StringType#INSTANCE}.
     * @param config           non-null,it will be passed to funcRefForConfig as the second argument of funcRefForConfig
     * @param funcRefForQuery  the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                         <ul>
     *                             <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                             <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                             <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                             <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                             <li>developer custom method</li>
     *                         </ul>.
     *                         he first argument of funcRefForQuery always is {@link TextType#INSTANCE}.
     * @param query            non-null,it will be passed to funcRefForQuery as the second argument of funcRefForQuery
     * @see #phraseToTsQuery(Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-textsearch.html#TEXTSEARCH-FUNCTIONS-TABLE">phraseto_tsquery ( [ config regconfig, ] query text ) → tsquery</a>
     */
    public static <T, U> Expression phraseToTsQuery(BiFunction<MappingType, T, Expression> funcRefForConfig, T config,
                                                          BiFunction<MappingType, U, Expression> funcRefForQuery, U query) {
        return phraseToTsQuery(funcRefForConfig.apply(StringType.INSTANCE, config),
                funcRefForQuery.apply(TextType.INSTANCE, query)
        );
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  PostgreTsQueryType}
     * *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-textsearch.html#TEXTSEARCH-FUNCTIONS-TABLE">phraseto_tsquery ( [ config regconfig, ] query text ) → tsquery</a>
     */
    public static SimpleExpression phraseToTsQuery(Expression config, Expression query) {
        return FunctionUtils.twoArgFunc("PHRASETO_TSQUERY", config, query, PostgreTsQueryType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  PostgreTsQueryType}
     * *
     *
     * @param funcRefForQuery the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                        <ul>
     *                            <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                            <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                            <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                            <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                            <li>developer custom method</li>
     *                        </ul>.
     *                        the first argument of funcRefForQuery always is {@link TextType#INSTANCE}.
     * @param query           non-null,it will be passed to funcRefForQuery as the second argument of funcRefForQuery
     * @see #webSearchToTsQuery(Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-textsearch.html#TEXTSEARCH-FUNCTIONS-TABLE">websearch_to_tsquery ( [ config regconfig, ] query text ) → tsquery</a>
     */
    public static <T> Expression webSearchToTsQuery(BiFunction<MappingType, T, Expression> funcRefForQuery, T query) {
        return webSearchToTsQuery(funcRefForQuery.apply(TextType.INSTANCE, query));
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  PostgreTsQueryType}
     * *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-textsearch.html#TEXTSEARCH-FUNCTIONS-TABLE">websearch_to_tsquery ( [ config regconfig, ] query text ) → tsquery</a>
     */
    public static SimpleExpression webSearchToTsQuery(Expression query) {
        return FunctionUtils.oneArgFunc("WEBSEARCH_TO_TSQUERY", query, PostgreTsQueryType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  PostgreTsQueryType}
     * *
     *
     * @param funcRefForConfig the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                         <ul>
     *                             <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                             <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                             <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                             <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                             <li>developer custom method</li>
     *                         </ul>.
     *                         The first argument of funcRefForConfig always is {@link StringType#INSTANCE}.
     * @param config           non-null,it will be passed to funcRefForConfig as the second argument of funcRefForConfig
     * @param funcRefForQuery  the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                         <ul>
     *                             <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                             <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                             <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                             <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                             <li>developer custom method</li>
     *                         </ul>.
     *                         the first argument of funcRefForQuery always is {@link TextType#INSTANCE}.
     * @param query            non-null,it will be passed to funcRefForQuery as the second argument of funcRefForQuery
     * @see #webSearchToTsQuery(Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-textsearch.html#TEXTSEARCH-FUNCTIONS-TABLE">websearch_to_tsquery ( [ config regconfig, ] query text ) → tsquery</a>
     */
    public static <T, U> Expression webSearchToTsQuery(BiFunction<MappingType, T, Expression> funcRefForConfig, T config,
                                                             BiFunction<MappingType, U, Expression> funcRefForQuery, U query) {
        return webSearchToTsQuery(funcRefForConfig.apply(StringType.INSTANCE, config),
                funcRefForQuery.apply(TextType.INSTANCE, query)
        );
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  PostgreTsQueryType}
     * *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-textsearch.html#TEXTSEARCH-FUNCTIONS-TABLE">websearch_to_tsquery ( [ config regconfig, ] query text ) → tsquery</a>
     */
    public static SimpleExpression webSearchToTsQuery(Expression config, Expression query) {
        return FunctionUtils.twoArgFunc("WEBSEARCH_TO_TSQUERY", config, query, PostgreTsQueryType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  TextType}
     * *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-textsearch.html#TEXTSEARCH-FUNCTIONS-TABLE">querytree ( tsquery ) → text</a>
     */
    public static SimpleExpression queryTree(Expression tsQuery) {
        return FunctionUtils.oneArgFunc("QUERYTREE", tsQuery, TextType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  PostgreTsVectorType}
     * *
     *
     * @param funcRefForVector the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                         <ul>
     *                             <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                             <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                             <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                             <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                             <li>developer custom method</li>
     *                         </ul>.
     *                         The first argument of funcRefForVector always is {@link PostgreTsVectorType#INSTANCE}.
     * @param vector           non-null,it will be passed to funcRefForVector as the second argument of funcRefForVector
     * @param funcRefForWeight the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                         <ul>
     *                             <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                             <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                             <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                             <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                             <li>developer custom method</li>
     *                         </ul>.
     *                         the first argument of funcRefForWeight always is {@link CharacterType#INSTANCE}.
     * @param weight           non-null,it will be passed to funcRefForWeight as the second argument of funcRefForWeight
     * @see #setWeight(Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-textsearch.html#TEXTSEARCH-FUNCTIONS-TABLE">setweight ( vector tsvector, weight "char" ) → tsvector<br/>
     * Assigns the specified weight to each element of the vector.<br/>
     * setweight('fat:2,4 cat:3 rat:5B'::tsvector, 'A') → 'cat':3A 'fat':2A,4A 'rat':5A
     * </a>
     */
    public static <T, U> Expression setWeight(BiFunction<MappingType, T, Expression> funcRefForVector, T vector,
                                                    BiFunction<MappingType, U, Expression> funcRefForWeight, U weight) {
        return setWeight(funcRefForVector.apply(PostgreTsVectorType.INSTANCE, vector),
                funcRefForWeight.apply(CharacterType.INSTANCE, weight)
        );
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  PostgreTsVectorType}
     * *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-textsearch.html#TEXTSEARCH-FUNCTIONS-TABLE">setweight ( vector tsvector, weight "char" ) → tsvector<br/>
     * Assigns the specified weight to each element of the vector.<br/>
     * setweight('fat:2,4 cat:3 rat:5B'::tsvector, 'A') → 'cat':3A 'fat':2A,4A 'rat':5A
     * </a>
     */
    public static SimpleExpression setWeight(Expression vector, Expression weight) {
        return FunctionUtils.twoArgFunc("SETWEIGHT", vector, weight, PostgreTsVectorType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  PostgreTsVectorType}
     * *
     *
     * @param funcRefForVector  the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                          <ul>
     *                              <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                              <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                              <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                              <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                              <li>developer custom method</li>
     *                          </ul>.
     *                          The first argument of funcRefForVector always is {@link PostgreTsVectorType#INSTANCE}.
     * @param vector            non-null,it will be passed to funcRefForVector as the second argument of funcRefForVector
     * @param funcRefForWeight  the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                          <ul>
     *                              <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                              <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                              <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                              <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                              <li>developer custom method</li>
     *                          </ul>.
     *                          the first argument of funcRefForWeight always is {@link CharacterType#INSTANCE}.
     * @param weight            non-null,it will be passed to funcRefForWeight as the second argument of funcRefForWeight
     * @param funcRefForLexemes the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                          <ul>
     *                              <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                              <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                              <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                              <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                              <li>developer custom method</li>
     *                          </ul>.
     *                          the first argument of funcRefForLexemes always is {@link TextArrayType} with one dimension array.
     * @param lexemes           non-null,it will be passed to funcRefForLexemes as the second argument of funcRefForLexemes
     * @see #setWeight(Expression, Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-textsearch.html#TEXTSEARCH-FUNCTIONS-TABLE">setweight ( vector tsvector, weight "char", lexemes text[] ) → tsvector<br/>
     * Assigns the specified weight to elements of the vector that are listed in lexemes. The strings in lexemes are taken as lexemes as-is, without further<br/>
     * processing. Strings that do not match any lexeme in vector are ignored.<br/>
     * setweight('fat:2,4 cat:3 rat:5,6B'::tsvector, 'A', '{cat,rat}') → 'cat':3A 'fat':2,4 'rat':5A,6A<br/>
     * </a>
     */
    public static <T, U, V> Expression setWeight(BiFunction<MappingType, T, Expression> funcRefForVector, T vector,
                                                       BiFunction<MappingType, U, Expression> funcRefForWeight, U weight,
                                                       BiFunction<MappingType, V, Expression> funcRefForLexemes, V lexemes) {
        return setWeight(funcRefForVector.apply(PostgreTsVectorType.INSTANCE, vector),
                funcRefForWeight.apply(CharacterType.INSTANCE, weight),
                funcRefForLexemes.apply(TextArrayType.from(String[].class), lexemes)
        );
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  PostgreTsVectorType}
     * *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-textsearch.html#TEXTSEARCH-FUNCTIONS-TABLE">setweight ( vector tsvector, weight "char", lexemes text[] ) → tsvector<br/>
     * Assigns the specified weight to elements of the vector that are listed in lexemes. The strings in lexemes are taken as lexemes as-is, without further<br/>
     * processing. Strings that do not match any lexeme in vector are ignored.<br/>
     * setweight('fat:2,4 cat:3 rat:5,6B'::tsvector, 'A', '{cat,rat}') → 'cat':3A 'fat':2,4 'rat':5A,6A<br/>
     * </a>
     */
    public static SimpleExpression setWeight(Expression vector, Expression weight, Expression lexemes) {
        return FunctionUtils.threeArgFunc("SETWEIGHT", vector, weight, lexemes, PostgreTsVectorType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  PostgreTsVectorType}
     * *
     *
     * @param funcRef the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                <ul>
     *                    <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                    <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                    <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                    <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                    <li>developer custom method</li>
     *                </ul>.
     *                The first argument of funcRef always is {@link PostgreTsVectorType} with one dimension array.
     * @param value   non-null,it will be passed to funcRef as the second argument of funcRef
     * @see #strip(Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-textsearch.html#TEXTSEARCH-FUNCTIONS-TABLE">array_to_tsvector ( text[] ) → tsvector</a>
     */
    public static <T> Expression strip(BiFunction<MappingType, T, Expression> funcRef, T value) {
        return strip(funcRef.apply(PostgreTsVectorType.INSTANCE, value));
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  PostgreTsVectorType}
     * *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-textsearch.html#TEXTSEARCH-FUNCTIONS-TABLE">strip ( tsvector ) → tsvector<br/>
     * Removes positions and weights from the tsvector.<br/>
     * strip('fat:2,4 cat:3 rat:5A'::tsvector) → 'cat' 'fat' 'rat'<br/>
     * </a>
     */
    public static SimpleExpression strip(Expression tsVector) {
        return FunctionUtils.oneArgFunc("STRIP", tsVector, PostgreTsVectorType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  PostgreTsQueryType}
     * *
     *
     * @param funcRefForQuery the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                        <ul>
     *                            <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                            <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                            <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                            <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                            <li>developer custom method</li>
     *                        </ul>.
     *                        the first argument of funcRefForQuery always is {@link TextType#INSTANCE}.
     * @param query           non-null,it will be passed to funcRefForQuery as the second argument of funcRefForQuery
     * @see #toTsQuery(Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-textsearch.html#TEXTSEARCH-FUNCTIONS-TABLE">to_tsquery ( [ config regconfig, ] query text ) → tsquery</a>
     */
    public static <T> Expression toTsQuery(BiFunction<MappingType, T, Expression> funcRefForQuery, T query) {
        return toTsQuery(funcRefForQuery.apply(TextType.INSTANCE, query));
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  PostgreTsQueryType}
     * *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-textsearch.html#TEXTSEARCH-FUNCTIONS-TABLE">to_tsquery ( [ config regconfig, ] query text ) → tsquery</a>
     */
    public static SimpleExpression toTsQuery(Expression query) {
        return FunctionUtils.oneArgFunc("TO_TSQUERY", query, PostgreTsQueryType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  PostgreTsQueryType}
     * *
     *
     * @param funcRefForConfig the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                         <ul>
     *                             <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                             <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                             <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                             <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                             <li>developer custom method</li>
     *                         </ul>.
     *                         The first argument of funcRefForConfig always is {@link StringType#INSTANCE}.
     * @param config           non-null,it will be passed to funcRefForConfig as the second argument of funcRefForConfig
     * @param funcRefForQuery  the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                         <ul>
     *                             <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                             <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                             <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                             <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                             <li>developer custom method</li>
     *                         </ul>.
     *                         the first argument of funcRefForQuery always is {@link TextType#INSTANCE}.
     * @param query            non-null,it will be passed to funcRefForQuery as the second argument of funcRefForQuery
     * @see #toTsQuery(Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-textsearch.html#TEXTSEARCH-FUNCTIONS-TABLE">to_tsquery ( [ config regconfig, ] query text ) → tsquery</a>
     */
    public static <T, U> Expression toTsQuery(BiFunction<MappingType, T, Expression> funcRefForConfig, T config,
                                                    BiFunction<MappingType, U, Expression> funcRefForQuery, U query) {
        return toTsQuery(funcRefForConfig.apply(StringType.INSTANCE, config),
                funcRefForQuery.apply(TextType.INSTANCE, query)
        );
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  PostgreTsQueryType}
     * *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-textsearch.html#TEXTSEARCH-FUNCTIONS-TABLE">to_tsquery ( [ config regconfig, ] query text ) → tsquery</a>
     */
    public static SimpleExpression toTsQuery(Expression config, Expression query) {
        return FunctionUtils.twoArgFunc("TO_TSQUERY", config, query, PostgreTsQueryType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  PostgreTsVectorType}
     * *
     *
     * @see #toTsVector(Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-textsearch.html#TEXTSEARCH-FUNCTIONS-TABLE">to_tsvector ( [ config regconfig, ] document text ) → tsvector</a>
     */
    public static SimpleExpression toTsVector(Expression document) {
        return FunctionUtils.oneArgFunc("TO_TSVECTOR", document, PostgreTsVectorType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  PostgreTsVectorType}
     * *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-textsearch.html#TEXTSEARCH-FUNCTIONS-TABLE">to_tsvector ( [ config regconfig, ] document text ) → tsvector</a>
     */
    public static SimpleExpression toTsVector(Expression config, Expression document) {
        return FunctionUtils.twoArgFunc("TO_TSVECTOR", config, document, PostgreTsVectorType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  PostgreTsVectorType}
     * *
     *
     * @param funcRefForDocument the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                           <ul>
     *                               <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                               <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                               <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                               <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                               <li>developer custom method</li>
     *                           </ul>.
     *                           the first argument of funcRefForDocument always is {@link JsonType#TEXT}.
     * @param document           non-null,it will be passed to funcRefForDocument as the second argument of funcRefForDocument
     * @param funcRefForFilter   the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                           <ul>
     *                               <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                               <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                               <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                               <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                               <li>developer custom method</li>
     *                           </ul>.
     *                           the first argument of funcRefForFilter always is {@link JsonbType#TEXT} .
     * @param filter             non-null,it will be passed to funcRefForFilter as the second argument of funcRefForFilter
     * @see #jsonToTsVector(Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-textsearch.html#TEXTSEARCH-FUNCTIONS-TABLE">json_to_tsvector ( [ config regconfig, ] document json, filter jsonb ) → tsvector</a>
     */
    public static <T, U> Expression jsonToTsVector(BiFunction<MappingType, T, Expression> funcRefForDocument, T document,
                                                         BiFunction<MappingType, U, Expression> funcRefForFilter, U filter) {
        return jsonToTsVector(funcRefForDocument.apply(JsonType.TEXT, document),
                funcRefForFilter.apply(JsonbType.TEXT, filter)
        );
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  PostgreTsVectorType}
     * *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-textsearch.html#TEXTSEARCH-FUNCTIONS-TABLE">json_to_tsvector ( [ config regconfig, ] document json, filter jsonb ) → tsvector</a>
     */
    public static SimpleExpression jsonToTsVector(Expression document, Expression filter) {
        return FunctionUtils.twoArgFunc("JSON_TO_TSVECTOR", document, filter, PostgreTsVectorType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  PostgreTsVectorType}
     * *
     *
     * @param funcRefForConfig   the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                           <ul>
     *                               <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                               <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                               <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                               <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                               <li>developer custom method</li>
     *                           </ul>.
     *                           The first argument of funcRefForConfig always is {@link StringType#INSTANCE}.
     * @param config             non-null,it will be passed to funcRefForConfig as the second argument of funcRefForConfig
     * @param funcRefForDocument the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                           <ul>
     *                               <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                               <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                               <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                               <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                               <li>developer custom method</li>
     *                           </ul>.
     *                           the first argument of funcRefForDocument always is {@link JsonType#TEXT}.
     * @param document           non-null,it will be passed to funcRefForDocument as the second argument of funcRefForDocument
     * @param funcRefForFilter   the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                           <ul>
     *                               <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                               <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                               <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                               <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                               <li>developer custom method</li>
     *                           </ul>.
     *                           the first argument of funcRefForFilter always is {@link JsonbType#TEXT} .
     * @param filter             non-null,it will be passed to funcRefForFilter as the second argument of funcRefForFilter
     * @see #jsonToTsVector(Expression, Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-textsearch.html#TEXTSEARCH-FUNCTIONS-TABLE">json_to_tsvector ( [ config regconfig, ] document json, filter jsonb ) → tsvector</a>
     */
    public static <T, U, V> Expression jsonToTsVector(BiFunction<MappingType, T, Expression> funcRefForConfig, T config,
                                                            BiFunction<MappingType, U, Expression> funcRefForDocument, U document,
                                                            BiFunction<MappingType, V, Expression> funcRefForFilter, V filter) {
        return jsonToTsVector(funcRefForConfig.apply(StringType.INSTANCE, config),
                funcRefForDocument.apply(JsonType.TEXT, document),
                funcRefForFilter.apply(JsonbType.TEXT, filter)
        );
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  PostgreTsVectorType}
     * *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-textsearch.html#TEXTSEARCH-FUNCTIONS-TABLE">json_to_tsvector ( [ config regconfig, ] document json, filter jsonb ) → tsvector</a>
     */
    public static SimpleExpression jsonToTsVector(Expression config, Expression document, Expression filter) {
        return FunctionUtils.threeArgFunc("JSON_TO_TSVECTOR", config, document, filter, PostgreTsVectorType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  PostgreTsVectorType}
     * *
     *
     * @param funcRefForDocument the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                           <ul>
     *                               <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                               <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                               <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                               <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                               <li>developer custom method</li>
     *                           </ul>.
     *                           the first argument of funcRefForDocument always is {@link JsonType#TEXT}.
     * @param document           non-null,it will be passed to funcRefForDocument as the second argument of funcRefForDocument
     * @param funcRefForFilter   the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                           <ul>
     *                               <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                               <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                               <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                               <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                               <li>developer custom method</li>
     *                           </ul>.
     *                           the first argument of funcRefForFilter always is {@link JsonbType#TEXT} .
     * @param filter             non-null,it will be passed to funcRefForFilter as the second argument of funcRefForFilter
     * @see #jsonbToTsVector(Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-textsearch.html#TEXTSEARCH-FUNCTIONS-TABLE">jsonb_to_tsvector ( [ config regconfig, ] document json, filter jsonb ) → tsvector</a>
     */
    public static <T, U> Expression jsonbToTsVector(BiFunction<MappingType, T, Expression> funcRefForDocument, T document,
                                                          BiFunction<MappingType, U, Expression> funcRefForFilter, U filter) {
        return jsonbToTsVector(funcRefForDocument.apply(JsonType.TEXT, document),
                funcRefForFilter.apply(JsonbType.TEXT, filter)
        );
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  PostgreTsVectorType}
     * *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-textsearch.html#TEXTSEARCH-FUNCTIONS-TABLE">jsonb_to_tsvector ( [ config regconfig, ] document json, filter jsonb ) → tsvector</a>
     */
    public static SimpleExpression jsonbToTsVector(Expression document, Expression filter) {
        return FunctionUtils.twoArgFunc("JSONB_TO_TSVECTOR", document, filter, PostgreTsVectorType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  PostgreTsVectorType}
     * *
     *
     * @param funcRefForConfig   the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                           <ul>
     *                               <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                               <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                               <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                               <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                               <li>developer custom method</li>
     *                           </ul>.
     *                           The first argument of funcRefForConfig always is {@link StringType#INSTANCE}.
     * @param config             non-null,it will be passed to funcRefForConfig as the second argument of funcRefForConfig
     * @param funcRefForDocument the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                           <ul>
     *                               <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                               <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                               <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                               <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                               <li>developer custom method</li>
     *                           </ul>.
     *                           the first argument of funcRefForDocument always is {@link JsonType#TEXT}.
     * @param document           non-null,it will be passed to funcRefForDocument as the second argument of funcRefForDocument
     * @param funcRefForFilter   the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                           <ul>
     *                               <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                               <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                               <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                               <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                               <li>developer custom method</li>
     *                           </ul>.
     *                           the first argument of funcRefForFilter always is {@link JsonbType#TEXT} .
     * @param filter             non-null,it will be passed to funcRefForFilter as the second argument of funcRefForFilter
     * @see #jsonbToTsVector(Expression, Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-textsearch.html#TEXTSEARCH-FUNCTIONS-TABLE">jsonb_to_tsvector ( [ config regconfig, ] document json, filter jsonb ) → tsvector</a>
     */
    public static <T, U, V> Expression jsonbToTsVector(BiFunction<MappingType, T, Expression> funcRefForConfig, T config,
                                                             BiFunction<MappingType, U, Expression> funcRefForDocument, U document,
                                                             BiFunction<MappingType, V, Expression> funcRefForFilter, V filter) {
        return jsonbToTsVector(funcRefForConfig.apply(StringType.INSTANCE, config),
                funcRefForDocument.apply(JsonType.TEXT, document),
                funcRefForFilter.apply(JsonbType.TEXT, filter)
        );
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  PostgreTsVectorType}
     * *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-textsearch.html#TEXTSEARCH-FUNCTIONS-TABLE">jsonb_to_tsvector ( [ config regconfig, ] document json, filter jsonb ) → tsvector</a>
     */
    public static SimpleExpression jsonbToTsVector(Expression config, Expression document, Expression filter) {
        return FunctionUtils.threeArgFunc("JSONB_TO_TSVECTOR", config, document, filter, PostgreTsVectorType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  PostgreTsVectorType}
     * *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-textsearch.html#TEXTSEARCH-FUNCTIONS-TABLE">ts_delete ( vector tsvector, lexeme text ) → tsvector</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-textsearch.html#TEXTSEARCH-FUNCTIONS-TABLE">ts_delete ( vector tsvector, lexemes text[] ) → tsvector</a>
     */
    public static SimpleExpression tsDelete(Expression tsVector, Expression lexeme) {
        return FunctionUtils.twoArgFunc("TS_DELETE", tsVector, lexeme, PostgreTsVectorType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  PostgreTsVectorType}
     * *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-textsearch.html#TEXTSEARCH-FUNCTIONS-TABLE">ts_filter ( vector tsvector, weights "char"[] ) → tsvector</a>
     */
    public static SimpleExpression tsFilter(Expression tsVector, Expression lexeme) {
        return FunctionUtils.twoArgFunc("TS_FILTER", tsVector, lexeme, PostgreTsVectorType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  TextType}
     * *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-textsearch.html#TEXTSEARCH-FUNCTIONS-TABLE">ts_headline ( [ config regconfig, ] document text, query tsquery [, options text ] ) → text</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-textsearch.html#TEXTSEARCH-FUNCTIONS-TABLE">ts_headline ( [ config regconfig, ] document json, query tsquery [, options text ] ) → text</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-textsearch.html#TEXTSEARCH-FUNCTIONS-TABLE">ts_headline ( [ config regconfig, ] document jsonb, query tsquery [, options text ] ) → text</a>
     */
    public static SimpleExpression tsHeadline(Expression config, Expression document, Expression query, Expression options) {
        return FunctionUtils.fourArgFunc("TS_HEADLINE", config, document, query, options, TextType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  TextType}
     * *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-textsearch.html#TEXTSEARCH-FUNCTIONS-TABLE">ts_headline ( [ config regconfig, ] document text, query tsquery [, options text ] ) → text</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-textsearch.html#TEXTSEARCH-FUNCTIONS-TABLE">ts_headline ( [ config regconfig, ] document json, query tsquery [, options text ] ) → text</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-textsearch.html#TEXTSEARCH-FUNCTIONS-TABLE">ts_headline ( [ config regconfig, ] document jsonb, query tsquery [, options text ] ) → text</a>
     */
    public static SimpleExpression tsHeadline(Expression exp1, Expression exp2, Expression exp3) {
        return FunctionUtils.threeArgFunc("TS_HEADLINE", exp1, exp2, exp3, TextType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  TextType}
     * *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-textsearch.html#TEXTSEARCH-FUNCTIONS-TABLE">ts_headline ( [ config regconfig, ] document text, query tsquery [, options text ] ) → text</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-textsearch.html#TEXTSEARCH-FUNCTIONS-TABLE">ts_headline ( [ config regconfig, ] document json, query tsquery [, options text ] ) → text</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-textsearch.html#TEXTSEARCH-FUNCTIONS-TABLE">ts_headline ( [ config regconfig, ] document jsonb, query tsquery [, options text ] ) → text</a>
     */
    public static SimpleExpression tsHeadline(Expression document, Expression query) {
        return FunctionUtils.twoArgFunc("TS_HEADLINE", document, query, TextType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  FloatType}
     * *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-textsearch.html#TEXTSEARCH-FUNCTIONS-TABLE">ts_rank ( [ weights real[], ] vector tsvector, query tsquery [, normalization integer ] ) → real</a>
     */
    public static SimpleExpression tsRank(Expression weights, Expression vector, Expression query, Expression normalization) {
        return FunctionUtils.fourArgFunc("TS_RANK", weights, vector, query, normalization, FloatType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  FloatType}
     * *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-textsearch.html#TEXTSEARCH-FUNCTIONS-TABLE">ts_rank ( [ weights real[], ] vector tsvector, query tsquery [, normalization integer ] ) → real</a>
     */
    public static SimpleExpression tsRank(Expression exp1, Expression exp2, Expression exp3) {
        return FunctionUtils.threeArgFunc("TS_RANK", exp1, exp2, exp3, FloatType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  FloatType}
     * *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-textsearch.html#TEXTSEARCH-FUNCTIONS-TABLE">ts_rank ( [ weights real[], ] vector tsvector, query tsquery [, normalization integer ] ) → real</a>
     */
    public static SimpleExpression tsRank(Expression vector, Expression query) {
        return FunctionUtils.twoArgFunc("TS_RANK", vector, query, FloatType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  FloatType}
     * *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-textsearch.html#TEXTSEARCH-FUNCTIONS-TABLE">ts_rank_cd ( [ weights real[], ] vector tsvector, query tsquery [, normalization integer ] ) → real</a>
     */
    public static SimpleExpression tsRankCd(Expression weights, Expression vector, Expression query, Expression normalization) {
        return FunctionUtils.fourArgFunc("TS_RANK_CD", weights, vector, query, normalization, FloatType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  FloatType}
     * *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-textsearch.html#TEXTSEARCH-FUNCTIONS-TABLE">ts_rank_cd ( [ weights real[], ] vector tsvector, query tsquery [, normalization integer ] ) → real</a>
     */
    public static SimpleExpression tsRankCd(Expression exp1, Expression exp2, Expression exp3) {
        return FunctionUtils.threeArgFunc("TS_RANK_CD", exp1, exp2, exp3, FloatType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  FloatType}
     * *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-textsearch.html#TEXTSEARCH-FUNCTIONS-TABLE">ts_rank_cd ( [ weights real[], ] vector tsvector, query tsquery [, normalization integer ] ) → real</a>
     */
    public static SimpleExpression tsRankCd(Expression vector, Expression query) {
        return FunctionUtils.twoArgFunc("TS_RANK_CD", vector, query, FloatType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  PostgreTsQueryType}
     * *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-textsearch.html#TEXTSEARCH-FUNCTIONS-TABLE">ts_rewrite ( query tsquery, target tsquery, substitute tsquery ) → tsquery</a>
     */
    public static SimpleExpression tsRewrite(Expression query, Expression target, Expression substitute) {
        return FunctionUtils.threeArgFunc("TS_REWRITE", query, target, substitute, PostgreTsQueryType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  PostgreTsQueryType}
     * *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-textsearch.html#TEXTSEARCH-FUNCTIONS-TABLE">ts_rewrite ( query tsquery, select text ) → tsquery</a>
     */
    public static SimpleExpression tsRewrite(Expression query, Expression select) {
        return FunctionUtils.twoArgFunc("TS_REWRITE", query, select, PostgreTsQueryType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  PostgreTsQueryType}
     * *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-textsearch.html#TEXTSEARCH-FUNCTIONS-TABLE">tsquery_phrase ( query1 tsquery, query2 tsquery ) → tsquery</a>
     */
    public static SimpleExpression tsQueryPhrase(Expression query1, Expression query2) {
        return FunctionUtils.twoArgFunc("TSQUERY_PHRASE", query1, query2, PostgreTsQueryType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  PostgreTsQueryType}
     * *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-textsearch.html#TEXTSEARCH-FUNCTIONS-TABLE">tsquery_phrase ( query1 tsquery, query2 tsquery, distance integer ) → tsquery</a>
     */
    public static SimpleExpression tsQueryPhrase(Expression query1, Expression query2, Expression distance) {
        return FunctionUtils.threeArgFunc("TSQUERY_PHRASE", query1, query2, distance, PostgreTsQueryType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  TextArrayType} with one dimension.
     * *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-textsearch.html#TEXTSEARCH-FUNCTIONS-TABLE">tsvector_to_array ( tsvector ) → text[]</a>
     */
    public static SimpleExpression tsVectorToArray(Expression tsVector) {
        return FunctionUtils.oneArgFunc("TSVECTOR_TO_ARRAY", tsVector, TextArrayType.from(String[].class));
    }



    /*-------------------below XML function -------------------*/

    /**
     * @param option  {@link Postgres#DOCUMENT} or {@link Postgres#CONTENT}
     * @param funcRef the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                <ul>
     *                    <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                    <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                    <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                    <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                    <li>developer custom method</li>
     *                </ul>.
     *                The first argument of funcRef always is {@link TextType#INSTANCE}.
     * @param value   non-null,it will be passed to funcRef as the second argument of funcRef
     * @see #xmlParse(SQLs.DocumentValueOption, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/datatype-xml.html#id-1.5.7.21.6">XMLPARSE ( { DOCUMENT | CONTENT } value)<br/>
     * </a>
     */
    public static SimpleExpression xmlParse(final SQLs.DocumentValueOption option, BiFunction<MappingType, String, Expression> funcRef, String value) {
        return xmlParse(option, funcRef.apply(TextType.INSTANCE, value));
    }

    /**
     * @param option {@link Postgres#DOCUMENT} or {@link Postgres#CONTENT}
     * @see <a href="https://www.postgresql.org/docs/current/datatype-xml.html#id-1.5.7.21.6">XMLPARSE ( { DOCUMENT | CONTENT } value)<br/>
     * </a>
     */
    public static SimpleExpression xmlParse(final SQLs.DocumentValueOption option, final Expression value) {
        final String name = "XMLPARSE";
        if (!(option == Postgres.DOCUMENT || option == Postgres.CONTENT)) {
            throw CriteriaUtils.funcArgError(name, option);
        } else if (!(value instanceof FunctionArg.SingleFunctionArg)) {
            throw CriteriaUtils.funcArgError(name, value);
        }
        return FunctionUtils.complexArgFunc(name, XmlType.TEXT, option, value);
    }

    /**
     * @param option {@link Postgres#DOCUMENT} or {@link Postgres#CONTENT}
     * @see <a href="https://www.postgresql.org/docs/current/datatype-xml.html#id-1.5.7.21.6">XMLSERIALIZE ( { DOCUMENT | CONTENT } value AS type )<br/>
     * </a>
     */
    public static SimpleExpression xmlSerialize(final SQLs.DocumentValueOption option, final Expression value, final SQLs.WordAs as,
                                                final MappingType type) {
        final String name = "XMLSERIALIZE";
        if (!(option == Postgres.DOCUMENT || option == Postgres.CONTENT)) {
            throw CriteriaUtils.funcArgError(name, option);
        } else if (!(value instanceof FunctionArg.SingleFunctionArg)) {
            throw CriteriaUtils.funcArgError(name, value);
        } else if (as != SQLs.AS) {
            throw CriteriaUtils.funcArgError(name, as);
        }
        return FunctionUtils.complexArgFunc(name, XmlType.TEXT, option, value, as, type);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  XmlType#TEXT}
     * *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-xml.html#FUNCTIONS-PRODUCING-XML">Xmlforest<br/>
     * </a>
     */
    public SimpleExpression xmlForest(Consumer<Postgres._XmlNamedElementFieldClause> consumer) {
        final PostgreFunctionUtils.XmlNamedElementPart<SimpleExpression> part;
        part = PostgreFunctionUtils.xmlForest();
        consumer.accept(part);
        return part.endNamedPart();
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  XmlType}
     * *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-xml.html#FUNCTIONS-PRODUCING-XML">xmlcomment ( text ) → xml</a>
     */
    public static SimpleExpression xmlComment(Expression exp) {
        return FunctionUtils.oneArgFunc("XMLCOMMENT", exp, XmlType.TEXT);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  XmlType}
     * *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-xml.html#FUNCTIONS-PRODUCING-XML">xmlconcat ( xml [, ...] ) → xml</a>
     */
    public static SimpleExpression xmlConcat(Expression xmls) {
        return FunctionUtils.oneOrMultiArgFunc("XMLCONCAT", xmls, XmlType.TEXT);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  XmlType}
     * *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-xml.html#FUNCTIONS-PRODUCING-XML">xmlconcat ( xml [, ...] ) → xml</a>
     */
    public static SimpleExpression xmlConcat(Expression xml1, Expression... xml2) {
        return FunctionUtils.oneAndRestFunc("XMLCONCAT", XmlType.TEXT, xml1, xml2);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  XmlType}
     * *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-xml.html#FUNCTIONS-PRODUCING-XML">xmlconcat ( xml [, ...] ) → xml</a>
     */
    public static SimpleExpression xmlConcat(List<Expression> xmlList) {
        return FunctionUtils.multiArgFunc("XMLCONCAT", xmlList, XmlType.TEXT);
    }

    /**
     * <p>
     * <strong>Note:</strong>This function cannot exist independently,see {@link #xmlElement(Postgres.WordName, String, XmlAttributes, Expression...)}
     * *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-xml.html#FUNCTIONS-PRODUCING-XML">9.15.1.3. Xmlelement<br/>
     * xmlelement ( NAME name [, XMLATTRIBUTES ( attvalue [ AS attname ] [, ...] ) ] [, content [, ...]] ) → xml
     * </a>
     */
    public static XmlAttributes xmlAttributes(Consumer<Postgres._XmlNamedElementFieldClause> consumer) {
        final PostgreFunctionUtils.XmlNamedElementPart<PostgreFunctionUtils.XmlAttributes> part;
        part = PostgreFunctionUtils.xmlAttributes();
        consumer.accept(part);
        return part.endNamedPart();
    }

    /**
     * @param wordName   see {@link Postgres#NAME}
     * @param name       The nam items shown in the syntax are simple identifiers, not values.
     * @param attributes see {@link #xmlAttributes(Consumer)}
     * @see <a href="https://www.postgresql.org/docs/current/functions-xml.html#FUNCTIONS-PRODUCING-XML">9.15.1.3. Xmlelement<br/>
     * xmlelement ( NAME name [, XMLATTRIBUTES ( attvalue [ AS attname ] [, ...] ) ] [, content [, ...]] ) → xml
     * </a>
     */
    public static SimpleExpression xmlElement(Postgres.WordName wordName, String name, XmlAttributes attributes,
                                              Expression... contents) {
        ContextStack.assertNonNull(attributes);
        ContextStack.assertNonNull(contents);
        return _xmlElement(wordName, name, attributes, c -> {
            for (Expression content : contents) {
                c.accept(SqlWords.FuncWord.COMMA);
                c.accept(content);
            }
        });
    }

    /**
     * @param wordName see {@link Postgres#NAME}
     * @param name     The nam items shown in the syntax are simple identifiers, not values.
     * @see <a href="https://www.postgresql.org/docs/current/functions-xml.html#FUNCTIONS-PRODUCING-XML">9.15.1.3. Xmlelement<br/>
     * xmlelement ( NAME name [, XMLATTRIBUTES ( attvalue [ AS attname ] [, ...] ) ] [, content [, ...]] ) → xml
     * </a>
     */
    public static SimpleExpression xmlElement(Postgres.WordName wordName, String name, Expression... contents) {
        ContextStack.assertNonNull(contents);
        return _xmlElement(wordName, name, null, c -> {
            for (Expression content : contents) {
                c.accept(SqlWords.FuncWord.COMMA);
                c.accept(content);
            }
        });
    }

    /**
     * @param wordName   see {@link Postgres#NAME}
     * @param name       The nam items shown in the syntax are simple identifiers, not values.
     * @param attributes see {@link #xmlAttributes(Consumer)}
     * @see <a href="https://www.postgresql.org/docs/current/functions-xml.html#FUNCTIONS-PRODUCING-XML">9.15.1.3. Xmlelement<br/>
     * xmlelement ( NAME name [, XMLATTRIBUTES ( attvalue [ AS attname ] [, ...] ) ] [, content [, ...]] ) → xml
     * </a>
     */
    public static SimpleExpression xmlElement(Postgres.WordName wordName, String name, XmlAttributes attributes,
                                              List<Expression> contentList) {
        ContextStack.assertNonNull(attributes);
        return _xmlElement(wordName, name, attributes, c -> {
            for (Expression content : contentList) {
                c.accept(SqlWords.FuncWord.COMMA);
                c.accept(content);
            }
        });
    }

    /**
     * @param wordName see {@link Postgres#NAME}
     * @param name     The nam items shown in the syntax are simple identifiers, not values.
     * @see <a href="https://www.postgresql.org/docs/current/functions-xml.html#FUNCTIONS-PRODUCING-XML">9.15.1.3. Xmlelement<br/>
     * xmlelement ( NAME name [, XMLATTRIBUTES ( attvalue [ AS attname ] [, ...] ) ] [, content [, ...]] ) → xml
     * </a>
     */
    public static SimpleExpression xmlElement(Postgres.WordName wordName, String name,
                                              List<Expression> contentList) {
        return _xmlElement(wordName, name, null, c -> {
            for (Expression content : contentList) {
                c.accept(SqlWords.FuncWord.COMMA);
                c.accept(content);
            }
        });
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  XmlType#TEXT}
     * *
     *
     * @see #xmlPi(Postgres.WordName, String, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-xml.html#FUNCTIONS-PRODUCING-XML">xmlpi ( NAME name [, content ] ) → xml<br/>
     * </a>
     */
    public static SimpleExpression xmlPi(Postgres.WordName wordName, String name) {
        return _xmlPi(wordName, name, null);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  XmlType#TEXT}
     * *
     *
     * @see #xmlPi(Postgres.WordName, String)
     * @see <a href="https://www.postgresql.org/docs/current/functions-xml.html#FUNCTIONS-PRODUCING-XML">xmlpi ( NAME name [, content ] ) → xml<br/>
     * </a>
     */
    public static SimpleExpression xmlPi(Postgres.WordName wordName, String name,
                                         BiFunction<MappingType, String, Expression> funcRef, String content) {
        final Expression contentExp;
        contentExp = funcRef.apply(StringType.INSTANCE, content);
        ContextStack.assertNonNull(contentExp);
        return _xmlPi(wordName, name, contentExp);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  XmlType#TEXT}
     * *
     *
     * @see #xmlPi(Postgres.WordName, String)
     * @see <a href="https://www.postgresql.org/docs/current/functions-xml.html#FUNCTIONS-PRODUCING-XML">xmlpi ( NAME name [, content ] ) → xml<br/>
     * </a>
     */
    public static SimpleExpression xmlPi(Postgres.WordName wordName, String name, Expression content) {
        ContextStack.assertNonNull(content);
        return _xmlPi(wordName, name, content);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  XmlType#TEXT}
     * *
     *
     * @see #xmlRoot(Expression, WordVersion, Expression)
     * @see #xmlRoot(Expression, WordVersion, WordsNoValue, WordStandalone, StandaloneOption)
     * @see #xmlRoot(Expression, WordVersion, Expression, WordStandalone, StandaloneOption)
     * @see <a href="https://www.postgresql.org/docs/current/functions-xml.html#FUNCTIONS-PRODUCING-XML">xmlroot ( xml, VERSION {text|NO VALUE} [, STANDALONE {YES|NO|NO VALUE} ] ) → xml<br/>
     * </a>
     */
    public static SimpleExpression xmlRoot(Expression xml, WordVersion version, WordsNoValue noValue) {
        return _xmlRoot(xml, version, noValue, Postgres.STANDALONE, null);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  XmlType#TEXT}
     * *
     *
     * @see #xmlRoot(Expression, WordVersion, WordsNoValue)
     * @see #xmlRoot(Expression, WordVersion, WordsNoValue, WordStandalone, StandaloneOption)
     * @see #xmlRoot(Expression, WordVersion, Expression, WordStandalone, StandaloneOption)
     * @see <a href="https://www.postgresql.org/docs/current/functions-xml.html#FUNCTIONS-PRODUCING-XML">xmlroot ( xml, VERSION {text|NO VALUE} [, STANDALONE {YES|NO|NO VALUE} ] ) → xml<br/>
     * </a>
     */
    public static SimpleExpression xmlRoot(Expression xml, WordVersion version, Expression text) {
        return _xmlRoot(xml, version, text, Postgres.STANDALONE, null);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  XmlType#TEXT}
     * *
     *
     * @see #xmlRoot(Expression, WordVersion, WordsNoValue)
     * @see #xmlRoot(Expression, WordVersion, Expression)
     * @see #xmlRoot(Expression, WordVersion, Expression, WordStandalone, StandaloneOption)
     * @see <a href="https://www.postgresql.org/docs/current/functions-xml.html#FUNCTIONS-PRODUCING-XML">xmlroot ( xml, VERSION {text|NO VALUE} [, STANDALONE {YES|NO|NO VALUE} ] ) → xml<br/>
     * </a>
     */
    public static SimpleExpression xmlRoot(Expression xml, WordVersion version, WordsNoValue noValue,
                                           WordStandalone standalone, StandaloneOption option) {
        ContextStack.assertNonNull(option);
        return _xmlRoot(xml, version, noValue, standalone, option);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  XmlType#TEXT}
     * *
     *
     * @see #xmlRoot(Expression, WordVersion, WordsNoValue)
     * @see #xmlRoot(Expression, WordVersion, Expression)
     * @see #xmlRoot(Expression, WordVersion, WordsNoValue, WordStandalone, StandaloneOption)
     * @see <a href="https://www.postgresql.org/docs/current/functions-xml.html#FUNCTIONS-PRODUCING-XML">xmlroot ( xml, VERSION {text|NO VALUE} [, STANDALONE {YES|NO|NO VALUE} ] ) → xml<br/>
     * </a>
     */
    public static SimpleExpression xmlRoot(Expression xml, WordVersion version, Expression text,
                                           WordStandalone standalone, StandaloneOption option) {
        ContextStack.assertNonNull(option);
        return _xmlRoot(xml, version, text, standalone, option);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  XmlType#TEXT}
     * *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-xml.html#FUNCTIONS-PRODUCING-XML">xmlagg ( xml ) → xml<br/>
     * </a>
     */
    public static SimpleExpression xmlAgg(Expression xml) {
        return FunctionUtils.oneArgFunc("XMLAGG", xml, XmlType.TEXT);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  XmlType#TEXT}
     * *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-xml.html#FUNCTIONS-PRODUCING-XML">xmlagg ( xml ) → xml<br/>
     * </a>
     */
    public static SimpleExpression xmlAgg(Expression xml, Consumer<Statement._SimpleOrderByClause> consumer) {
        final FunctionUtils.OrderByOptionClause clause;
        clause = FunctionUtils.orderByOptionClause();
        consumer.accept(clause);
        clause.endOrderByClauseIfNeed();

        final SimpleExpression func;
        final String name = "XMLAGG";
        if (clause.orderByList().size() == 0) {
            func = FunctionUtils.oneArgFunc(name, xml, XmlType.TEXT);
        } else {
            func = FunctionUtils.complexArgFunc(name, XmlType.TEXT, xml, clause);
        }
        return func;
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link Boolean}
     * *
     *
     * @see #xmlExists(Expression, WordPassing, PassingOption, Expression)
     * @see #xmlExists(Expression, WordPassing, Expression, PassingOption)
     * @see #xmlExists(Expression, WordPassing, PassingOption, Expression, PassingOption)
     * @see <a href="https://www.postgresql.org/docs/current/functions-xml.html#FUNCTIONS-PRODUCING-XML">XMLEXISTS ( text PASSING [BY {REF|VALUE}] xml [BY {REF|VALUE}] ) → boolean<br/>
     * </a>
     */
    public static SimplePredicate xmlExists(Expression text, WordPassing passing, Expression xml) {
        return _xmlExists(text, passing, null, xml, null);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link Boolean}
     * *
     *
     * @see #xmlExists(Expression, WordPassing, Expression)
     * @see #xmlExists(Expression, WordPassing, PassingOption, Expression)
     * @see #xmlExists(Expression, WordPassing, PassingOption, Expression, PassingOption)
     * @see <a href="https://www.postgresql.org/docs/current/functions-xml.html#FUNCTIONS-PRODUCING-XML">XMLEXISTS ( text PASSING [BY {REF|VALUE}] xml [BY {REF|VALUE}] ) → boolean<br/>
     * </a>
     */
    public static SimplePredicate xmlExists(Expression text, WordPassing passing, Expression xml,
                                            PassingOption xmlOption) {
        ContextStack.assertNonNull(xmlOption);
        return _xmlExists(text, passing, null, xml, xmlOption);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link Boolean}
     * *
     *
     * @see #xmlExists(Expression, WordPassing, Expression)
     * @see #xmlExists(Expression, WordPassing, Expression, PassingOption)
     * @see #xmlExists(Expression, WordPassing, PassingOption, Expression, PassingOption)
     * @see <a href="https://www.postgresql.org/docs/current/functions-xml.html#FUNCTIONS-PRODUCING-XML">XMLEXISTS ( text PASSING [BY {REF|VALUE}] xml [BY {REF|VALUE}] ) → boolean<br/>
     * </a>
     */
    public static SimplePredicate xmlExists(Expression text, WordPassing passing, PassingOption textOption,
                                            Expression xml) {
        ContextStack.assertNonNull(textOption);
        return _xmlExists(text, passing, textOption, xml, null);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link Boolean}
     * *
     *
     * @see #xmlExists(Expression, WordPassing, Expression)
     * @see #xmlExists(Expression, WordPassing, PassingOption, Expression)
     * @see #xmlExists(Expression, WordPassing, Expression, PassingOption)
     * @see <a href="https://www.postgresql.org/docs/current/functions-xml.html#FUNCTIONS-XML-PREDICATES">XMLEXISTS ( text PASSING [BY {REF|VALUE}] xml [BY {REF|VALUE}] ) → boolean<br/>
     * </a>
     */
    public static SimplePredicate xmlExists(Expression text, WordPassing passing, PassingOption textOption,
                                            Expression xml, PassingOption xmlOption) {
        ContextStack.assertNonNull(textOption);
        ContextStack.assertNonNull(xmlOption);
        return _xmlExists(text, passing, textOption, xml, xmlOption);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link Boolean}
     * *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-xml.html#FUNCTIONS-XML-PREDICATES">xml_is_well_formed ( text ) → boolean<br/>
     * </a>
     */
    public static SimplePredicate xmlIsWellFormed(Expression text) {
        return FunctionUtils.oneArgPredicateFunc("XML_IS_WELL_FORMED", text);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link Boolean}
     * *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-xml.html#FUNCTIONS-XML-PREDICATES">xml_is_well_formed_document ( text ) → boolean<br/>
     * </a>
     */
    public static SimplePredicate xmlIsWellFormedDocument(Expression text) {
        return FunctionUtils.oneArgPredicateFunc("XML_IS_WELL_FORMED_DOCUMENT", text);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link Boolean}
     * *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-xml.html#FUNCTIONS-XML-PREDICATES">xml_is_well_formed_content ( text ) → boolean<br/>
     * </a>
     */
    public static SimplePredicate xmlIsWellFormedContent(Expression text) {
        return FunctionUtils.oneArgPredicateFunc("XML_IS_WELL_FORMED_CONTENT", text);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link XmlArrayType#TEXT_LINEAR}
     * *
     *
     * @see #xpath(Expression, Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-xml.html#FUNCTIONS-XML-PREDICATES">xpath ( xpath text, xml xml [, nsarray text[] ] ) → xml[]<br/>
     * </a>
     */
    public static SimpleExpression xpath(Expression xpath, Expression xml) {
        return FunctionUtils.twoArgFunc("XPATH", xpath, xml, XmlArrayType.TEXT_LINEAR);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link XmlArrayType#TEXT_LINEAR}
     * *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-xml.html#FUNCTIONS-XML-PREDICATES">xpath ( xpath text, xml xml [, nsarray text[] ] ) → xml[]<br/>
     * </a>
     */
    public static SimpleExpression xpath(Expression xpath, Expression xml, Expression nsArray) {
        return FunctionUtils.threeArgFunc("XPATH", xpath, xml, nsArray, XmlArrayType.TEXT_LINEAR);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link BooleanType}
     * *
     *
     * @see #xpathExists(Expression, Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-xml.html#FUNCTIONS-XML-PREDICATES">xpath_exists ( xpath text, xml xml [, nsarray text[] ] ) → boolean<br/>
     * </a>
     */
    public static SimplePredicate xpathExists(Expression xpath, Expression xml) {
        return FunctionUtils.twoArgPredicateFunc("XPATH_EXISTS", xpath, xml);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link BooleanType}
     * *
     *
     * @see #xpathExists(Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-xml.html#FUNCTIONS-XML-PREDICATES">xpath_exists ( xpath text, xml xml [, nsarray text[] ] ) → boolean<br/>
     * </a>
     */
    public static SimplePredicate xpathExists(Expression xpath, Expression xml, Expression nsArray) {
        return FunctionUtils.threeArgPredicateFunc("XPATH_EXISTS", xpath, xml, nsArray);
    }


    /**
     * @param funcRef       the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                      <ul>
     *                          <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                          <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                          <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                          <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                          <li>developer custom method</li>
     *                      </ul>.
     *                      The first argument of funcRef always is {@link TextType#INSTANCE}.
     * @param namespaceUri  non-null,it will be passed to funcRef as the second argument of funcRef
     * @param as            see {@link SQLs#AS}
     * @param namespaceName a simple identifier
     * @throws CriteriaException throw when :<ul>
     *                           <li>{@link Expression} returned by funcRef isn't operable {@link Expression},for example {@link SQLs#DEFAULT}</li>
     *                           <li>namespaceName isn't simple identifier</li>
     *                           </ul>
     * @see #xmlNamespaces(Expression, SQLs.WordAs, String)
     * @see <a href="https://www.postgresql.org/docs/current/functions-xml.html#FUNCTIONS-XML-PROCESSING">XMLNAMESPACES ( namespace_uri AS namespace_name [, ...] )<br/>
     * </a>
     */
    public static XmlNameSpaces xmlNamespaces(BiFunction<MappingType, String, Expression> funcRef, String namespaceUri, SQLs.WordAs as, String namespaceName) {
        return xmlNamespaces(funcRef.apply(TextType.INSTANCE, namespaceUri), as, namespaceName);
    }


    /**
     * @param namespaceUri  a text expression
     * @param as            see {@link SQLs#AS}
     * @param namespaceName a simple identifier
     * @see #xmlNamespaces(BiFunction, String, SQLs.WordAs, String)
     * @see #xmlNamespaces(Consumer)
     * @see <a href="https://www.postgresql.org/docs/current/functions-xml.html#FUNCTIONS-XML-PROCESSING">XMLNAMESPACES ( namespace_uri AS namespace_name [, ...] )<br/>
     * </a>
     */
    public static XmlNameSpaces xmlNamespaces(Expression namespaceUri, SQLs.WordAs as, String namespaceName) {
        final PostgreFunctionUtils.XmlNamedElementPart<XmlNameSpaces> clause;
        clause = PostgreFunctionUtils.xmlNamespaces();
        clause.accept(namespaceUri, as, namespaceName);
        return clause.endNamedPart();
    }


    /**
     * @see #xmlNamespaces(BiFunction, String, SQLs.WordAs, String)
     * @see #xmlNamespaces(Consumer)
     * @see <a href="https://www.postgresql.org/docs/current/functions-xml.html#FUNCTIONS-XML-PROCESSING">XMLNAMESPACES ( namespace_uri AS namespace_name [, ...] )<br/>
     * </a>
     */
    public static XmlNameSpaces xmlNamespaces(Consumer<Postgres._XmlNamedElementClause> consumer) {
        final PostgreFunctionUtils.XmlNamedElementPart<XmlNameSpaces> clause;
        clause = PostgreFunctionUtils.xmlNamespaces();
        consumer.accept(clause);
        return clause.endNamedPart();
    }

    /**
     * <pre><br/>
     *     XMLTABLE (
     *     [ XMLNAMESPACES ( namespace_uri AS namespace_name [, ...] ), ]
     *     row_expression PASSING [BY {REF|VALUE}] document_expression [BY {REF|VALUE}]
     *     COLUMNS name { type [PATH column_expression] [DEFAULT default_expression] [NOT NULL | NULL]
     *                   | FOR ORDINALITY }
     *             [, ...]
     * ) → setof record
     * </pre>
     *
     * @see #xmlTable(Expression, WordPassing, Expression, Consumer)
     * @see #xmlTable(Expression, WordPassing, PassingOption, Expression, Consumer)
     * @see #xmlTable(XmlNameSpaces, Expression, WordPassing, Expression, Consumer)
     * @see #xmlTable(XmlNameSpaces, Expression, WordPassing, Expression, PassingOption, Consumer)
     * @see #xmlTable(Expression, WordPassing, Expression, PassingOption, Consumer)
     * @see #xmlTable(XmlNameSpaces, Expression, WordPassing, PassingOption, Expression, Consumer)
     * @see #xmlTable(XmlNameSpaces, Expression, WordPassing, PassingOption, Expression, PassingOption, Consumer)
     * @see <a href="https://www.postgresql.org/docs/current/functions-xml.html#FUNCTIONS-XML-PROCESSING">XMLTABLE</a>
     */
    public static _TabularFunction xmlTable(Expression rowExp, WordPassing passing, Expression docExp,
                                            Consumer<Postgres._XmlTableColumnsClause> consumer) {
        return _xmlTable(null, rowExp, passing, null, docExp, null, consumer);
    }

    /**
     * <pre><br/>
     *     XMLTABLE (
     *     [ XMLNAMESPACES ( namespace_uri AS namespace_name [, ...] ), ]
     *     row_expression PASSING [BY {REF|VALUE}] document_expression [BY {REF|VALUE}]
     *     COLUMNS name { type [PATH column_expression] [DEFAULT default_expression] [NOT NULL | NULL]
     *                   | FOR ORDINALITY }
     *             [, ...]
     * ) → setof record
     * </pre>
     *
     * @see #xmlTable(Expression, WordPassing, Expression, Consumer)
     * @see #xmlTable(Expression, WordPassing, PassingOption, Expression, Consumer)
     * @see #xmlTable(XmlNameSpaces, Expression, WordPassing, Expression, Consumer)
     * @see #xmlTable(XmlNameSpaces, Expression, WordPassing, Expression, PassingOption, Consumer)
     * @see #xmlTable(Expression, WordPassing, Expression, PassingOption, Consumer)
     * @see #xmlTable(XmlNameSpaces, Expression, WordPassing, PassingOption, Expression, Consumer)
     * @see #xmlTable(XmlNameSpaces, Expression, WordPassing, PassingOption, Expression, PassingOption, Consumer)
     * @see <a href="https://www.postgresql.org/docs/current/functions-xml.html#FUNCTIONS-XML-PROCESSING">XMLTABLE</a>
     */
    public static _TabularFunction xmlTable(XmlNameSpaces nameSpaces, Expression rowExp, WordPassing passing,
                                            Expression docExp, Consumer<Postgres._XmlTableColumnsClause> consumer) {
        ContextStack.assertNonNull(nameSpaces);
        return _xmlTable(nameSpaces, rowExp, passing, null, docExp, null, consumer);
    }

    /**
     * <pre><br/>
     *     XMLTABLE (
     *     [ XMLNAMESPACES ( namespace_uri AS namespace_name [, ...] ), ]
     *     row_expression PASSING [BY {REF|VALUE}] document_expression [BY {REF|VALUE}]
     *     COLUMNS name { type [PATH column_expression] [DEFAULT default_expression] [NOT NULL | NULL]
     *                   | FOR ORDINALITY }
     *             [, ...]
     * ) → setof record
     * </pre>
     *
     * @see #xmlTable(Expression, WordPassing, Expression, Consumer)
     * @see #xmlTable(Expression, WordPassing, PassingOption, Expression, Consumer)
     * @see #xmlTable(XmlNameSpaces, Expression, WordPassing, Expression, Consumer)
     * @see #xmlTable(XmlNameSpaces, Expression, WordPassing, Expression, PassingOption, Consumer)
     * @see #xmlTable(Expression, WordPassing, Expression, PassingOption, Consumer)
     * @see #xmlTable(XmlNameSpaces, Expression, WordPassing, PassingOption, Expression, Consumer)
     * @see #xmlTable(XmlNameSpaces, Expression, WordPassing, PassingOption, Expression, PassingOption, Consumer)
     * @see <a href="https://www.postgresql.org/docs/current/functions-xml.html#FUNCTIONS-XML-PROCESSING">XMLTABLE</a>
     */
    public static _TabularFunction xmlTable(Expression rowExp, WordPassing passing, PassingOption rowOption,
                                            Expression docExp, Consumer<Postgres._XmlTableColumnsClause> consumer) {
        ContextStack.assertNonNull(rowOption);
        return _xmlTable(null, rowExp, passing, rowOption, docExp, null, consumer);
    }

    /**
     * <pre><br/>
     *     XMLTABLE (
     *     [ XMLNAMESPACES ( namespace_uri AS namespace_name [, ...] ), ]
     *     row_expression PASSING [BY {REF|VALUE}] document_expression [BY {REF|VALUE}]
     *     COLUMNS name { type [PATH column_expression] [DEFAULT default_expression] [NOT NULL | NULL]
     *                   | FOR ORDINALITY }
     *             [, ...]
     * ) → setof record
     * </pre>
     *
     * @see #xmlTable(Expression, WordPassing, Expression, Consumer)
     * @see #xmlTable(Expression, WordPassing, PassingOption, Expression, Consumer)
     * @see #xmlTable(XmlNameSpaces, Expression, WordPassing, Expression, Consumer)
     * @see #xmlTable(XmlNameSpaces, Expression, WordPassing, Expression, PassingOption, Consumer)
     * @see #xmlTable(Expression, WordPassing, Expression, PassingOption, Consumer)
     * @see #xmlTable(XmlNameSpaces, Expression, WordPassing, PassingOption, Expression, Consumer)
     * @see #xmlTable(XmlNameSpaces, Expression, WordPassing, PassingOption, Expression, PassingOption, Consumer)
     * @see <a href="https://www.postgresql.org/docs/current/functions-xml.html#FUNCTIONS-XML-PROCESSING">XMLTABLE</a>
     */
    public static _TabularFunction xmlTable(Expression rowExp, WordPassing passing, Expression docExp,
                                            PassingOption docOption,
                                            Consumer<Postgres._XmlTableColumnsClause> consumer) {
        ContextStack.assertNonNull(docOption);
        return _xmlTable(null, rowExp, passing, null, docExp, docOption, consumer);
    }

    /**
     * <pre><br/>
     *     XMLTABLE (
     *     [ XMLNAMESPACES ( namespace_uri AS namespace_name [, ...] ), ]
     *     row_expression PASSING [BY {REF|VALUE}] document_expression [BY {REF|VALUE}]
     *     COLUMNS name { type [PATH column_expression] [DEFAULT default_expression] [NOT NULL | NULL]
     *                   | FOR ORDINALITY }
     *             [, ...]
     * ) → setof record
     * </pre>
     *
     * @see #xmlTable(Expression, WordPassing, Expression, Consumer)
     * @see #xmlTable(Expression, WordPassing, PassingOption, Expression, Consumer)
     * @see #xmlTable(XmlNameSpaces, Expression, WordPassing, Expression, Consumer)
     * @see #xmlTable(XmlNameSpaces, Expression, WordPassing, Expression, PassingOption, Consumer)
     * @see #xmlTable(Expression, WordPassing, Expression, PassingOption, Consumer)
     * @see #xmlTable(XmlNameSpaces, Expression, WordPassing, PassingOption, Expression, Consumer)
     * @see #xmlTable(XmlNameSpaces, Expression, WordPassing, PassingOption, Expression, PassingOption, Consumer)
     * @see <a href="https://www.postgresql.org/docs/current/functions-xml.html#FUNCTIONS-XML-PROCESSING">XMLTABLE</a>
     */
    public static _TabularFunction xmlTable(XmlNameSpaces nameSpaces, Expression rowExp, WordPassing passing,
                                            PassingOption rowOption, Expression docExp,
                                            Consumer<Postgres._XmlTableColumnsClause> consumer) {
        ContextStack.assertNonNull(nameSpaces);
        ContextStack.assertNonNull(rowOption);
        return _xmlTable(nameSpaces, rowExp, passing, rowOption, docExp, null, consumer);
    }

    /**
     * <pre><br/>
     *     XMLTABLE (
     *     [ XMLNAMESPACES ( namespace_uri AS namespace_name [, ...] ), ]
     *     row_expression PASSING [BY {REF|VALUE}] document_expression [BY {REF|VALUE}]
     *     COLUMNS name { type [PATH column_expression] [DEFAULT default_expression] [NOT NULL | NULL]
     *                   | FOR ORDINALITY }
     *             [, ...]
     * ) → setof record
     * </pre>
     *
     * @see #xmlTable(Expression, WordPassing, Expression, Consumer)
     * @see #xmlTable(Expression, WordPassing, PassingOption, Expression, Consumer)
     * @see #xmlTable(XmlNameSpaces, Expression, WordPassing, Expression, Consumer)
     * @see #xmlTable(XmlNameSpaces, Expression, WordPassing, Expression, PassingOption, Consumer)
     * @see #xmlTable(Expression, WordPassing, Expression, PassingOption, Consumer)
     * @see #xmlTable(XmlNameSpaces, Expression, WordPassing, PassingOption, Expression, Consumer)
     * @see #xmlTable(XmlNameSpaces, Expression, WordPassing, PassingOption, Expression, PassingOption, Consumer)
     * @see <a href="https://www.postgresql.org/docs/current/functions-xml.html#FUNCTIONS-XML-PROCESSING">XMLTABLE</a>
     */
    public static _TabularFunction xmlTable(XmlNameSpaces nameSpaces, Expression rowExp, WordPassing passing,
                                            Expression docExp, PassingOption docOption,
                                            Consumer<Postgres._XmlTableColumnsClause> consumer) {
        ContextStack.assertNonNull(nameSpaces);
        ContextStack.assertNonNull(docOption);
        return _xmlTable(nameSpaces, rowExp, passing, null, docExp, docOption, consumer);
    }

    /**
     * <pre><br/>
     *     XMLTABLE (
     *     [ XMLNAMESPACES ( namespace_uri AS namespace_name [, ...] ), ]
     *     row_expression PASSING [BY {REF|VALUE}] document_expression [BY {REF|VALUE}]
     *     COLUMNS name { type [PATH column_expression] [DEFAULT default_expression] [NOT NULL | NULL]
     *                   | FOR ORDINALITY }
     *             [, ...]
     * ) → setof record
     * </pre>
     *
     * @see #xmlTable(Expression, WordPassing, Expression, Consumer)
     * @see #xmlTable(Expression, WordPassing, PassingOption, Expression, Consumer)
     * @see #xmlTable(XmlNameSpaces, Expression, WordPassing, Expression, Consumer)
     * @see #xmlTable(XmlNameSpaces, Expression, WordPassing, Expression, PassingOption, Consumer)
     * @see #xmlTable(Expression, WordPassing, Expression, PassingOption, Consumer)
     * @see #xmlTable(XmlNameSpaces, Expression, WordPassing, PassingOption, Expression, Consumer)
     * @see #xmlTable(XmlNameSpaces, Expression, WordPassing, PassingOption, Expression, PassingOption, Consumer)
     * @see <a href="https://www.postgresql.org/docs/current/functions-xml.html#FUNCTIONS-XML-PROCESSING">XMLTABLE</a>
     */
    public static _TabularFunction xmlTable(Expression rowExp, WordPassing passing, PassingOption rowOption,
                                            Expression docExp, PassingOption docOption,
                                            Consumer<Postgres._XmlTableColumnsClause> consumer) {
        ContextStack.assertNonNull(rowOption);
        ContextStack.assertNonNull(docOption);
        return _xmlTable(null, rowExp, passing, rowOption, docExp, docOption, consumer);
    }

    /**
     * <pre><br/>
     *     XMLTABLE (
     *     [ XMLNAMESPACES ( namespace_uri AS namespace_name [, ...] ), ]
     *     row_expression PASSING [BY {REF|VALUE}] document_expression [BY {REF|VALUE}]
     *     COLUMNS name { type [PATH column_expression] [DEFAULT default_expression] [NOT NULL | NULL]
     *                   | FOR ORDINALITY }
     *             [, ...]
     * ) → setof record
     * </pre>
     *
     * @see #xmlTable(Expression, WordPassing, Expression, Consumer)
     * @see #xmlTable(Expression, WordPassing, PassingOption, Expression, Consumer)
     * @see #xmlTable(XmlNameSpaces, Expression, WordPassing, Expression, Consumer)
     * @see #xmlTable(XmlNameSpaces, Expression, WordPassing, Expression, PassingOption, Consumer)
     * @see #xmlTable(Expression, WordPassing, Expression, PassingOption, Consumer)
     * @see #xmlTable(XmlNameSpaces, Expression, WordPassing, PassingOption, Expression, Consumer)
     * @see #xmlTable(XmlNameSpaces, Expression, WordPassing, PassingOption, Expression, PassingOption, Consumer)
     * @see <a href="https://www.postgresql.org/docs/current/functions-xml.html#FUNCTIONS-XML-PROCESSING">XMLTABLE</a>
     */
    public static _TabularFunction xmlTable(XmlNameSpaces nameSpaces, Expression rowExp, WordPassing passing,
                                            PassingOption rowOption, Expression docExp, PassingOption docOption,
                                            Consumer<Postgres._XmlTableColumnsClause> consumer) {
        ContextStack.assertNonNull(nameSpaces);
        ContextStack.assertNonNull(rowOption);
        ContextStack.assertNonNull(docOption);
        return _xmlTable(nameSpaces, rowExp, passing, rowOption, docExp, docOption, consumer);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link XmlArrayType#TEXT_LINEAR}
     * *
     *
     * @param table will output strings identifying tables using the usual notation, including optional schema qualification and double quotes
     * @see #tableToXml(Expression, Expression, Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-xml.html#FUNCTIONS-XML-MAPPING">table_to_xml</a>
     */
    public static SimpleExpression tableToXml(TableMeta<?> table, Expression nulls, Expression tableForest,
                                              Expression targetNs) {
        return tableToXml(PostgreFunctionUtils.tableNameExp(table), nulls, tableForest, targetNs);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link XmlArrayType#TEXT_LINEAR}
     * *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-xml.html#FUNCTIONS-XML-MAPPING">table_to_xml</a>
     */
    public static SimpleExpression tableToXml(Expression table, Expression nulls, Expression tableForest,
                                              Expression targetNs) {
        return FunctionUtils.fourArgFunc("TABLE_TO_XML", table, nulls, tableForest, targetNs, XmlType.TEXT);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link XmlArrayType#TEXT_LINEAR}
     * *
     *
     * @param query will output literal sql
     * @see <a href="https://www.postgresql.org/docs/current/functions-xml.html#FUNCTIONS-XML-MAPPING">query_to_xml</a>
     */
    public static SimpleExpression queryToXml(Select query, Visible visible, Expression nulls, Expression tableForest,
                                              Expression targetNs) {
        return queryToXml(PostgreFunctionUtils.queryStringExp(query, visible), nulls, tableForest, targetNs);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link XmlArrayType#TEXT_LINEAR}
     * *
     *
     * @param query will output literal sql
     * @see <a href="https://www.postgresql.org/docs/current/functions-xml.html#FUNCTIONS-XML-MAPPING">query_to_xml</a>
     */
    public static SimpleExpression queryToXml(Select query, Expression nulls, Expression tableForest,
                                              Expression targetNs) {
        return queryToXml(PostgreFunctionUtils.queryStringExp(query, Visible.ONLY_VISIBLE), nulls, tableForest, targetNs);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link XmlArrayType#TEXT_LINEAR}
     * *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-xml.html#FUNCTIONS-XML-MAPPING">query_to_xml</a>
     */
    public static SimpleExpression queryToXml(Expression query, Expression nulls, Expression tableForest,
                                              Expression targetNs) {
        return FunctionUtils.fourArgFunc("QUERY_TO_XML", query, nulls, tableForest, targetNs, XmlType.TEXT);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link XmlArrayType#TEXT_LINEAR}
     * *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-xml.html#FUNCTIONS-XML-MAPPING">cursor_to_xml</a>
     */
    public static SimpleExpression cursorToXml(Expression cursor, Expression nulls, Expression tableForest,
                                               Expression targetNs) {
        return FunctionUtils.fourArgFunc("CURSOR_TO_XML", cursor, nulls, tableForest, targetNs, XmlType.TEXT);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link XmlArrayType#TEXT_LINEAR}
     * *
     *
     * @param table will output strings identifying tables using the usual notation, including optional schema qualification and double quotes
     * @see #tableToXmlSchema(Expression, Expression, Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-xml.html#FUNCTIONS-XML-MAPPING">table_to_xmlschema</a>
     */
    public static SimpleExpression tableToXmlSchema(TableMeta<?> table, Expression nulls, Expression tableForest,
                                                    Expression targetNs) {
        return tableToXmlSchema(PostgreFunctionUtils.tableNameExp(table), nulls, tableForest, targetNs);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link XmlArrayType#TEXT_LINEAR}
     * *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-xml.html#FUNCTIONS-XML-MAPPING">table_to_xmlschema</a>
     */
    public static SimpleExpression tableToXmlSchema(Expression table, Expression nulls, Expression tableForest,
                                                    Expression targetNs) {
        return FunctionUtils.fourArgFunc("TABLE_TO_XMLSCHEMA", table, nulls, tableForest, targetNs, XmlType.TEXT);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link XmlArrayType#TEXT_LINEAR}
     * *
     *
     * @param query will output literal sql
     * @see <a href="https://www.postgresql.org/docs/current/functions-xml.html#FUNCTIONS-XML-MAPPING">query_to_xmlschema</a>
     */
    public static SimpleExpression queryToXmlSchema(Select query, Visible visible, Expression nulls, Expression tableForest,
                                                    Expression targetNs) {
        return queryToXmlSchema(PostgreFunctionUtils.queryStringExp(query, visible), nulls, tableForest, targetNs);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link XmlArrayType#TEXT_LINEAR}
     * *
     *
     * @param query will output literal sql
     * @see <a href="https://www.postgresql.org/docs/current/functions-xml.html#FUNCTIONS-XML-MAPPING">query_to_xmlschema</a>
     */
    public static SimpleExpression queryToXmlSchema(Select query, Expression nulls, Expression tableForest,
                                                    Expression targetNs) {
        return queryToXmlSchema(PostgreFunctionUtils.queryStringExp(query, Visible.ONLY_VISIBLE), nulls, tableForest,
                targetNs);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link XmlArrayType#TEXT_LINEAR}
     * *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-xml.html#FUNCTIONS-XML-MAPPING">query_to_xmlschema</a>
     */
    public static SimpleExpression queryToXmlSchema(Expression query, Expression nulls, Expression tableForest,
                                                    Expression targetNs) {
        return FunctionUtils.fourArgFunc("QUERY_TO_XMLSCHEMA", query, nulls, tableForest, targetNs, XmlType.TEXT);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link XmlArrayType#TEXT_LINEAR}
     * *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-xml.html#FUNCTIONS-XML-MAPPING">cursor_to_xmlschema</a>
     */
    public static SimpleExpression cursorToXmlSchema(Expression cursor, Expression nulls, Expression tableForest,
                                                     Expression targetNs) {
        return FunctionUtils.fourArgFunc("CURSOR_TO_XMLSCHEMA", cursor, nulls, tableForest, targetNs, XmlType.TEXT);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link XmlArrayType#TEXT_LINEAR}
     * *
     *
     * @param table will output strings identifying tables using the usual notation, including optional schema qualification and double quotes
     * @see #tableToXmlAndXmlSchema(Expression, Expression, Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-xml.html#FUNCTIONS-XML-MAPPING">table_to_xml_and_xmlschema</a>
     */
    public static SimpleExpression tableToXmlAndXmlSchema(TableMeta<?> table, Expression nulls, Expression tableForest,
                                                          Expression targetNs) {
        return tableToXmlAndXmlSchema(PostgreFunctionUtils.tableNameExp(table), nulls, tableForest, targetNs);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link XmlArrayType#TEXT_LINEAR}
     * *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-xml.html#FUNCTIONS-XML-MAPPING">table_to_xml_and_xmlschema</a>
     */
    public static SimpleExpression tableToXmlAndXmlSchema(Expression table, Expression nulls, Expression tableForest,
                                                          Expression targetNs) {
        return FunctionUtils.fourArgFunc("TABLE_TO_XML_AND_XMLSCHEMA", table, nulls, tableForest, targetNs, XmlType.TEXT);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link XmlArrayType#TEXT_LINEAR}
     * *
     *
     * @param query will output literal sql
     * @see <a href="https://www.postgresql.org/docs/current/functions-xml.html#FUNCTIONS-XML-MAPPING">query_to_xml_and_xmlschema</a>
     */
    public static SimpleExpression queryToXmlAndXmlSchema(Select query, Visible visible, Expression nulls, Expression tableForest,
                                                          Expression targetNs) {
        return queryToXmlAndXmlSchema(PostgreFunctionUtils.queryStringExp(query, visible), nulls, tableForest, targetNs);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link XmlArrayType#TEXT_LINEAR}
     * *
     *
     * @param query will output literal sql
     * @see <a href="https://www.postgresql.org/docs/current/functions-xml.html#FUNCTIONS-XML-MAPPING">query_to_xml_and_xmlschema</a>
     */
    public static SimpleExpression queryToXmlAndXmlSchema(Select query, Expression nulls, Expression tableForest,
                                                          Expression targetNs) {
        return queryToXmlAndXmlSchema(PostgreFunctionUtils.queryStringExp(query, Visible.ONLY_VISIBLE), nulls, tableForest, targetNs);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link XmlArrayType#TEXT_LINEAR}
     * *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-xml.html#FUNCTIONS-XML-MAPPING">query_to_xml_and_xmlschema</a>
     */
    public static SimpleExpression queryToXmlAndXmlSchema(Expression query, Expression nulls, Expression tableForest,
                                                          Expression targetNs) {
        return FunctionUtils.fourArgFunc("QUERY_TO_XML_AND_XMLSCHEMA", query, nulls, tableForest, targetNs, XmlType.TEXT);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link XmlArrayType#TEXT_LINEAR}
     * *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-xml.html#FUNCTIONS-XML-MAPPING">schema_to_xml</a>
     */
    public static SimpleExpression schemaToXml(Expression schema, Expression nulls, Expression tableForest,
                                               Expression targetNs) {
        return FunctionUtils.fourArgFunc("SCHEMA_TO_XML", schema, nulls, tableForest, targetNs, XmlType.TEXT);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link XmlArrayType#TEXT_LINEAR}
     * *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-xml.html#FUNCTIONS-XML-MAPPING">schema_to_xmlschema</a>
     */
    public static SimpleExpression schemaToXmlSchema(Expression schema, Expression nulls, Expression tableForest,
                                                     Expression targetNs) {
        return FunctionUtils.fourArgFunc("SCHEMA_TO_XMLSCHEMA", schema, nulls, tableForest, targetNs, XmlType.TEXT);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link XmlArrayType#TEXT_LINEAR}
     * *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-xml.html#FUNCTIONS-XML-MAPPING">schema_to_xml_and_xmlschema</a>
     */
    public static SimpleExpression schemaToXmlAndXmlSchema(Expression schema, Expression nulls, Expression tableForest,
                                                           Expression targetNs) {
        return FunctionUtils.fourArgFunc("SCHEMA_TO_XML_AND_XMLSCHEMA", schema, nulls, tableForest, targetNs,
                XmlType.TEXT);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link XmlArrayType#TEXT_LINEAR}
     * *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-xml.html#FUNCTIONS-XML-MAPPING">database_to_xml</a>
     */
    public static SimpleExpression databaseToXml(Expression nulls, Expression tableForest, Expression targetNs) {
        return FunctionUtils.threeArgFunc("DATABASE_TO_XML", nulls, tableForest, targetNs, XmlType.TEXT);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link XmlArrayType#TEXT_LINEAR}
     * *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-xml.html#FUNCTIONS-XML-MAPPING">database_to_xmlschema</a>
     */
    public static SimpleExpression databaseToXmlSchema(Expression nulls, Expression tableForest, Expression targetNs) {
        return FunctionUtils.threeArgFunc("DATABASE_TO_XMLSCHEMA", nulls, tableForest, targetNs, XmlType.TEXT);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link XmlArrayType#TEXT_LINEAR}
     * *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-xml.html#FUNCTIONS-XML-MAPPING">database_to_xml_and_xmlschema</a>
     */
    public static SimpleExpression databaseToXmlAndXmlSchema(Expression nulls, Expression tableForest,
                                                             Expression targetNs) {
        return FunctionUtils.threeArgFunc("DATABASE_TO_XML_AND_XMLSCHEMA", nulls, tableForest, targetNs,
                XmlType.TEXT);
    }

    /*-------------------below JSON function -------------------*/


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonType#TEXT}
     * *
     * <p>
     * This method don't support {@link SQLs#space(String, SQLs.SymbolPeriod, TableMeta)},because it will output non-mapping column .
     * you should use
     * <ul>
     *     <li>{@link #jsonBuildObject(String, SQLs.SymbolPeriod, TableMeta)}</li>
     *     <li>{@link #jsonbBuildObject(String, SQLs.SymbolPeriod, TableMeta)}</li>
     * </ul>
     * *
     *
     * @param arg valid type:<ul>
     *            <li>constant</li>
     *            <li>{@link RowExpression}</li>
     *            <li>{@link Expression}</li>
     *            <li> {@link SQLs#row(Object)} </li>
     *            <li>{@link SQLs#row(Consumer)}</li>
     *            <li>{@link SQLs#space(String, SQLs.SymbolPeriod, SQLs.SymbolAsterisk)}</li>
     *            </ul>
     * @throws CriteriaException throw when arg type error , but probably defer if arg is {@link SQLs#refField(String, String)}
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-CREATION-TABLE">to_json ( anyelement ) → json</a>
     */
    public static SimpleExpression toJson(Object arg) {
        return FunctionUtils.oneArgRowElementFunc("to_json", arg, JsonType.TEXT);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonbType#TEXT}
     * *
     * <p>
     * This method don't support {@link SQLs#space(String, SQLs.SymbolPeriod, TableMeta)},because it will output non-mapping column .
     * you should use
     * <ul>
     *     <li>{@link #jsonBuildObject(String, SQLs.SymbolPeriod, TableMeta)}</li>
     *     <li>{@link #jsonbBuildObject(String, SQLs.SymbolPeriod, TableMeta)}</li>
     * </ul>
     *
     * @param arg valid type:<ul>
     *            <li>constant</li>
     *            <li>{@link RowExpression}</li>
     *            <li>{@link Expression}</li>
     *            <li> {@link SQLs#row(Object)} </li>
     *            <li>{@link SQLs#row(Consumer)}</li>
     *            <li>{@link SQLs#space(String, SQLs.SymbolPeriod, SQLs.SymbolAsterisk)}</li>
     *            </ul>
     * @throws CriteriaException throw when arg type error , but probably defer if arg is {@link SQLs#refField(String, String)}
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-CREATION-TABLE">to_jsonb ( anyelement ) → jsonb</a>
     */
    public static SimpleExpression toJsonb(Object arg) {
        return FunctionUtils.oneArgRowElementFunc("to_jsonb", arg, JsonbType.TEXT);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonType#TEXT}
     * *
     *
     * @see #arrayToJson(Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-CREATION-TABLE">array_to_json ( anyarray [, boolean ] ) → json<br/>
     * Converts an SQL array to a JSON array. The behavior is the same as to_json except that line feeds will be added between top-level array elements if the optional boolean parameter is true.
     * </a>
     */
    public static SimpleExpression arrayToJson(Expression array) {
        return FunctionUtils.oneArgFunc("array_to_json", array, JsonType.TEXT);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonType#TEXT}
     * *
     *
     * @param lineFeed in most case ,{@link SQLs#TRUE} or {@link SQLs#FALSE}
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-CREATION-TABLE">array_to_json ( anyarray [, boolean ] ) → json<br/>
     * Converts an SQL array to a JSON array. The behavior is the same as to_json except that line feeds will be added between top-level array elements if the optional boolean parameter is true.
     * </a>
     */
    public static SimpleExpression arrayToJson(Expression array, Expression lineFeed) {
        return FunctionUtils.twoArgFunc("array_to_json", array, lineFeed, JsonType.TEXT);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonType#TEXT}
     * *<p>
     * This method don't support {@link SQLs#space(String, SQLs.SymbolPeriod, TableMeta)},because it will output non-mapping column .
     * you should use
     * <ul>
     *     <li>{@link #jsonBuildObject(String, SQLs.SymbolPeriod, TableMeta)}</li>
     *     <li>{@link #jsonbBuildObject(String, SQLs.SymbolPeriod, TableMeta)}</li>
     * </ul>
     *
     * @param record valid type:<ul>
     *               <li>{@link RowExpression}</li>
     *               <li>{@link Expression}</li>
     *               <li> {@link SQLs#row(Object)} </li>
     *               <li>{@link SQLs#row(Consumer)}</li>
     *               <li>{@link SQLs#space(String, SQLs.SymbolPeriod, SQLs.SymbolAsterisk)}</li>
     *               </ul>
     * @throws CriteriaException throw when arg type error , but probably defer if arg is {@link SQLs#refField(String, String)}
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-CREATION-TABLE">row_to_json ( record [, boolean ] ) → json<br/>
     * Converts an SQL composite value to a JSON object. The behavior is the same as to_json except that line feeds will be added between top-level elements if the optional boolean parameter is true.
     * </a>
     */
    public static SimpleExpression rowToJson(RowElement record) {
        return FunctionUtils.oneArgRowElementFunc("row_to_json", record, JsonType.TEXT);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonType#TEXT}
     * *<p>
     * This method don't support {@link SQLs#space(String, SQLs.SymbolPeriod, TableMeta)},because it will output non-mapping column .
     * you should use
     * <ul>
     *     <li>{@link #jsonBuildObject(String, SQLs.SymbolPeriod, TableMeta)}</li>
     *     <li>{@link #jsonbBuildObject(String, SQLs.SymbolPeriod, TableMeta)}</li>
     * </ul>
     *
     * @param record   valid type:<ul>
     *                 <li>{@link RowExpression}</li>
     *                 <li>{@link Expression}</li>
     *                 <li> {@link SQLs#row(Object)} </li>
     *                 <li>{@link SQLs#row(Consumer)}</li>
     *                 <li>{@link SQLs#space(String, SQLs.SymbolPeriod, SQLs.SymbolAsterisk)}</li>
     *                 </ul>
     * @param lineFeed in most case {@link SQLs#TRUE} or {@link SQLs#FALSE}
     * @throws CriteriaException throw when arg type error , but probably defer if arg is {@link SQLs#refField(String, String)}
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-CREATION-TABLE">row_to_json ( record [, boolean ] ) → json<br/>
     * Converts an SQL composite value to a JSON object. The behavior is the same as to_json except that line feeds will be added between top-level elements if the optional boolean parameter is true.
     * </a>
     */
    public static SimpleExpression rowToJson(RowElement record, Expression lineFeed) {
        return FunctionUtils.twoArgRowElementFunc("row_to_json", record, lineFeed, JsonType.TEXT);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonType#TEXT}
     * * <p>
     * create a empty json array
     * *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-CREATION-TABLE">json_build_array ( VARIADIC "any" ) → json<br/>
     * Builds a possibly-heterogeneously-typed JSON array out of a variadic argument list. Each argument is converted as per to_json or to_jsonb.
     * </a>
     */
    public static SimpleExpression jsonBuildArray() {
        return FunctionUtils.zeroArgFunc("json_build_array", JsonType.TEXT);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonType#TEXT}
     * * <p>
     * This method don't support {@link SQLs#space(String, SQLs.SymbolPeriod, TableMeta)},because it will output non-mapping column .
     * you should use
     * <ul>
     *     <li>{@link #jsonBuildObject(String, SQLs.SymbolPeriod, TableMeta)}</li>
     *     <li>{@link #jsonbBuildObject(String, SQLs.SymbolPeriod, TableMeta)}</li>
     * </ul>
     * *
     *
     * @param arg valid type:<ul>
     *            <li>constant</li>
     *            <li>{@link RowExpression}</li>
     *            <li>{@link Expression}</li>
     *            <li> {@link SQLs#row(Object)} </li>
     *            <li>{@link SQLs#row(Consumer)}</li>
     *            <li>{@link SQLs#space(String, SQLs.SymbolPeriod, SQLs.SymbolAsterisk)}</li>
     *            </ul>
     * @throws CriteriaException throw when arg type error , but probably defer if arg is {@link SQLs#refField(String, String)}
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-CREATION-TABLE">json_build_array ( VARIADIC "any" ) → json<br/>
     * Builds a possibly-heterogeneously-typed JSON array out of a variadic argument list. Each argument is converted as per to_json or to_jsonb.
     * </a>
     */
    public static SimpleExpression jsonBuildArray(Object arg) {
        return FunctionUtils.oneArgRowElementFunc("json_build_array", arg, JsonType.TEXT);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonType#TEXT}
     * * <p>
     * This method don't support {@link SQLs#space(String, SQLs.SymbolPeriod, TableMeta)},because it will output non-mapping column .
     * you should use
     * <ul>
     *     <li>{@link #jsonBuildObject(String, SQLs.SymbolPeriod, TableMeta)}</li>
     *     <li>{@link #jsonbBuildObject(String, SQLs.SymbolPeriod, TableMeta)}</li>
     * </ul>
     * *
     *
     * @param arg1 valid type:<ul>
     *             <li>constant</li>
     *             <li>{@link RowExpression}</li>
     *             <li>{@link Expression}</li>
     *             <li> {@link SQLs#row(Object)} </li>
     *             <li>{@link SQLs#row(Consumer)}</li>
     *             <li>{@link SQLs#space(String, SQLs.SymbolPeriod, SQLs.SymbolAsterisk)}</li>
     *             </ul>
     * @param arg2 valid type:<ul>
     *             <li>constant</li>
     *             <li>{@link RowExpression}</li>
     *             <li>{@link Expression}</li>
     *             <li> {@link SQLs#row(Object)} </li>
     *             <li>{@link SQLs#row(Consumer)}</li>
     *             <li>{@link SQLs#space(String, SQLs.SymbolPeriod, SQLs.SymbolAsterisk)}</li>
     *             </ul>
     * @throws CriteriaException throw when arg type error , but probably defer if arg is {@link SQLs#refField(String, String)}
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-CREATION-TABLE">json_build_array ( VARIADIC "any" ) → json<br/>
     * Builds a possibly-heterogeneously-typed JSON array out of a variadic argument list. Each argument is converted as per to_json or to_jsonb.
     * </a>
     */
    public static SimpleExpression jsonBuildArray(Object arg1, Object arg2) {
        return FunctionUtils.twoArgRowElementFunc("json_build_array", arg1, arg2, JsonType.TEXT);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonType#TEXT}
     * * <p>
     * This method don't support {@link SQLs#space(String, SQLs.SymbolPeriod, TableMeta)},because it will output non-mapping column .
     * you should use
     * <ul>
     *     <li>{@link #jsonBuildObject(String, SQLs.SymbolPeriod, TableMeta)}</li>
     *     <li>{@link #jsonbBuildObject(String, SQLs.SymbolPeriod, TableMeta)}</li>
     * </ul>
     * *
     *
     * @param arg1     valid type:<ul>
     *                 <li>constant</li>
     *                 <li>{@link RowExpression}</li>
     *                 <li>{@link Expression}</li>
     *                 <li> {@link SQLs#row(Object)} </li>
     *                 <li>{@link SQLs#row(Consumer)}</li>
     *                 <li>{@link SQLs#space(String, SQLs.SymbolPeriod, SQLs.SymbolAsterisk)}</li>
     *                 </ul>
     * @param arg2     valid type:<ul>
     *                 <li>constant</li>
     *                 <li>{@link RowExpression}</li>
     *                 <li>{@link Expression}</li>
     *                 <li> {@link SQLs#row(Object)} </li>
     *                 <li>{@link SQLs#row(Consumer)}</li>
     *                 <li>{@link SQLs#space(String, SQLs.SymbolPeriod, SQLs.SymbolAsterisk)}</li>
     *                 </ul>
     * @param arg3     valid type:<ul>
     *                 <li>constant</li>
     *                 <li>{@link RowExpression}</li>
     *                 <li>{@link Expression}</li>
     *                 <li> {@link SQLs#row(Object)} </li>
     *                 <li>{@link SQLs#row(Consumer)}</li>
     *                 <li>{@link SQLs#space(String, SQLs.SymbolPeriod, SQLs.SymbolAsterisk)}</li>
     *                 </ul>
     * @param variadic valid type:<ul>
     *                 <li>constant</li>
     *                 <li>{@link RowExpression}</li>
     *                 <li>{@link Expression}</li>
     *                 <li> {@link SQLs#row(Object)} </li>
     *                 <li>{@link SQLs#row(Consumer)}</li>
     *                 <li>{@link SQLs#space(String, SQLs.SymbolPeriod, SQLs.SymbolAsterisk)}</li>
     *                 </ul>
     * @throws CriteriaException throw when arg type error , but probably defer if arg is {@link SQLs#refField(String, String)}
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-CREATION-TABLE">json_build_array ( VARIADIC "any" ) → json<br/>
     * Builds a possibly-heterogeneously-typed JSON array out of a variadic argument list. Each argument is converted as per to_json or to_jsonb.
     * </a>
     */
    public static SimpleExpression jsonBuildArray(Object arg1, Object arg2, Object arg3, Object... variadic) {
        return FunctionUtils.threeAndRestRowElementFunc("json_build_array", JsonType.TEXT, arg1, arg2, arg3, variadic);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonType#TEXT}
     * * <p>
     * This method don't support {@link SQLs#space(String, SQLs.SymbolPeriod, TableMeta)},because it will output non-mapping column .
     * you should use
     * <ul>
     *     <li>{@link #jsonBuildObject(String, SQLs.SymbolPeriod, TableMeta)}</li>
     *     <li>{@link #jsonbBuildObject(String, SQLs.SymbolPeriod, TableMeta)}</li>
     * </ul>
     * *
     *
     * @param consumer valid type:<ul>
     *                 <li>constant</li>
     *                 <li>{@link RowExpression}</li>
     *                 <li>{@link Expression}</li>
     *                 <li> {@link SQLs#row(Object)} </li>
     *                 <li>{@link SQLs#row(Consumer)}</li>
     *                 <li>{@link SQLs#space(String, SQLs.SymbolPeriod, SQLs.SymbolAsterisk)}</li>
     *                 </ul>
     * @throws CriteriaException throw when arg type error , but probably defer if arg is {@link SQLs#refField(String, String)}
     * @see SQLs#space(String, SQLs.SymbolPeriod, SQLs.SymbolAsterisk)
     * @see SQLs#space(String, SQLs.SymbolPeriod, TableMeta)
     * @see #jsonBuildObject(String, SQLs.SymbolPeriod, TableMeta)
     * @see #jsonbBuildObject(String, SQLs.SymbolPeriod, TableMeta)
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-CREATION-TABLE">json_build_array ( VARIADIC "any" ) → json<br/>
     * Builds a possibly-heterogeneously-typed JSON array out of a variadic argument list. Each argument is converted as per to_json or to_jsonb.
     * </a>
     */
    public static SimpleExpression jsonBuildArray(Consumer<Consumer<Object>> consumer) {
        return FunctionUtils.rowElementFunc("json_build_array", false, consumer, JsonType.TEXT);
    }

    /*-------------------below jsonb_build_array-------------------*/


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonbType#TEXT}
     * * <p>
     * create a empty jsonb array
     * *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-CREATION-TABLE">jsonb_build_array ( VARIADIC "any" ) → json<br/>
     * Builds a possibly-heterogeneously-typed JSON array out of a variadic argument list. Each argument is converted as per to_json or to_jsonb.
     * </a>
     */
    public static SimpleExpression jsonbBuildArray() {
        return FunctionUtils.zeroArgFunc("jsonb_build_array", JsonbType.TEXT);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonbType#TEXT}
     * * <p>
     * This method don't support {@link SQLs#space(String, SQLs.SymbolPeriod, TableMeta)},because it will output non-mapping column .
     * you should use
     * <ul>
     *     <li>{@link #jsonBuildObject(String, SQLs.SymbolPeriod, TableMeta)}</li>
     *     <li>{@link #jsonbBuildObject(String, SQLs.SymbolPeriod, TableMeta)}</li>
     * </ul>
     * *
     *
     * @param arg valid type:<ul>
     *            <li>constant</li>
     *            <li>{@link RowExpression}</li>
     *            <li>{@link Expression}</li>
     *            <li> {@link SQLs#row(Object)} </li>
     *            <li>{@link SQLs#row(Consumer)}</li>
     *            <li>{@link SQLs#space(String, SQLs.SymbolPeriod, SQLs.SymbolAsterisk)}</li>
     *            </ul>
     * @throws CriteriaException throw when arg type error , but probably defer if arg is {@link SQLs#refField(String, String)}
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-CREATION-TABLE">jsonb_build_array ( VARIADIC "any" ) → json<br/>
     * Builds a possibly-heterogeneously-typed JSON array out of a variadic argument list. Each argument is converted as per to_json or to_jsonb.
     * </a>
     */
    public static SimpleExpression jsonbBuildArray(Object arg) {
        return FunctionUtils.oneArgRowElementFunc("jsonb_build_array", arg, JsonbType.TEXT);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonbType#TEXT}
     * *<p>
     * This method don't support {@link SQLs#space(String, SQLs.SymbolPeriod, TableMeta)},because it will output non-mapping column .
     * you should use
     * <ul>
     *     <li>{@link #jsonBuildObject(String, SQLs.SymbolPeriod, TableMeta)}</li>
     *     <li>{@link #jsonbBuildObject(String, SQLs.SymbolPeriod, TableMeta)}</li>
     * </ul>
     *
     * @param arg1 valid type:<ul>
     *             <li>constant</li>
     *             <li>{@link RowExpression}</li>
     *             <li>{@link Expression}</li>
     *             <li> {@link SQLs#row(Object)} </li>
     *             <li>{@link SQLs#row(Consumer)}</li>
     *             <li>{@link SQLs#space(String, SQLs.SymbolPeriod, SQLs.SymbolAsterisk)}</li>
     *             </ul>
     * @param arg2 valid type:<ul>
     *             <li>constant</li>
     *             <li>{@link RowExpression}</li>
     *             <li>{@link Expression}</li>
     *             <li> {@link SQLs#row(Object)} </li>
     *             <li>{@link SQLs#row(Consumer)}</li>
     *             <li>{@link SQLs#space(String, SQLs.SymbolPeriod, SQLs.SymbolAsterisk)}</li>
     *             </ul>
     * @throws CriteriaException throw when arg type error , but probably defer if arg is {@link SQLs#refField(String, String)}
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-CREATION-TABLE">jsonb_build_array ( VARIADIC "any" ) → json<br/>
     * Builds a possibly-heterogeneously-typed JSON array out of a variadic argument list. Each argument is converted as per to_json or to_jsonb.
     * </a>
     */
    public static SimpleExpression jsonbBuildArray(Object arg1, Object arg2) {
        return FunctionUtils.twoArgRowElementFunc("jsonb_build_array", arg1, arg2, JsonbType.TEXT);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonbType#TEXT}
     * *<p>
     * This method don't support {@link SQLs#space(String, SQLs.SymbolPeriod, TableMeta)},because it will output non-mapping column .
     * you should use
     * <ul>
     *     <li>{@link #jsonBuildObject(String, SQLs.SymbolPeriod, TableMeta)}</li>
     *     <li>{@link #jsonbBuildObject(String, SQLs.SymbolPeriod, TableMeta)}</li>
     * </ul>
     *
     * @param arg1     valid type:<ul>
     *                 <li>constant</li>
     *                 <li>{@link RowExpression}</li>
     *                 <li>{@link Expression}</li>
     *                 <li> {@link SQLs#row(Object)} </li>
     *                 <li>{@link SQLs#row(Consumer)}</li>
     *                 <li>{@link SQLs#space(String, SQLs.SymbolPeriod, SQLs.SymbolAsterisk)}</li>
     *                 </ul>
     * @param arg2     valid type:<ul>
     *                 <li>constant</li>
     *                 <li>{@link RowExpression}</li>
     *                 <li>{@link Expression}</li>
     *                 <li> {@link SQLs#row(Object)} </li>
     *                 <li>{@link SQLs#row(Consumer)}</li>
     *                 <li>{@link SQLs#space(String, SQLs.SymbolPeriod, SQLs.SymbolAsterisk)}</li>
     *                 </ul>
     * @param arg3     valid type:<ul>
     *                 <li>constant</li>
     *                 <li>{@link RowExpression}</li>
     *                 <li>{@link Expression}</li>
     *                 <li> {@link SQLs#row(Object)} </li>
     *                 <li>{@link SQLs#row(Consumer)}</li>
     *                 <li>{@link SQLs#space(String, SQLs.SymbolPeriod, SQLs.SymbolAsterisk)}</li>
     *                 </ul>
     * @param variadic valid type:<ul>
     *                 <li>constant</li>
     *                 <li>{@link RowExpression}</li>
     *                 <li>{@link Expression}</li>
     *                 <li> {@link SQLs#row(Object)} </li>
     *                 <li>{@link SQLs#row(Consumer)}</li>
     *                 <li>{@link SQLs#space(String, SQLs.SymbolPeriod, SQLs.SymbolAsterisk)}</li>
     *                 </ul>
     * @throws CriteriaException throw when arg type error , but probably defer if arg is {@link SQLs#refField(String, String)}
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-CREATION-TABLE">jsonb_build_array ( VARIADIC "any" ) → json<br/>
     * Builds a possibly-heterogeneously-typed JSON array out of a variadic argument list. Each argument is converted as per to_json or to_jsonb.
     * </a>
     */
    public static SimpleExpression jsonbBuildArray(Object arg1, Object arg2, Object arg3, Object... variadic) {
        return FunctionUtils.threeAndRestRowElementFunc("jsonb_build_array", JsonbType.TEXT, arg1, arg2, arg3, variadic);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonbType#TEXT}
     * * <p>
     * This method don't support {@link SQLs#space(String, SQLs.SymbolPeriod, TableMeta)},because it will output non-mapping column .
     * you should use
     * <ul>
     *     <li>{@link #jsonBuildObject(String, SQLs.SymbolPeriod, TableMeta)}</li>
     *     <li>{@link #jsonbBuildObject(String, SQLs.SymbolPeriod, TableMeta)}</li>
     * </ul>
     * *
     *
     * @param consumer valid type:<ul>
     *                 <li>constant</li>
     *                 <li>{@link RowExpression}</li>
     *                 <li>{@link Expression}</li>
     *                 <li> {@link SQLs#row(Object)} </li>
     *                 <li>{@link SQLs#row(Consumer)}</li>
     *                 <li>{@link SQLs#space(String, SQLs.SymbolPeriod, SQLs.SymbolAsterisk)}</li>
     *                 </ul>
     * @throws CriteriaException throw when arg type error , but probably defer if arg is {@link SQLs#refField(String, String)}
     * @see SQLs#space(String, SQLs.SymbolPeriod, SQLs.SymbolAsterisk)
     * @see SQLs#space(String, SQLs.SymbolPeriod, TableMeta)
     * @see #jsonBuildObject(String, SQLs.SymbolPeriod, TableMeta)
     * @see #jsonbBuildObject(String, SQLs.SymbolPeriod, TableMeta)
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-CREATION-TABLE">jsonb_build_array ( VARIADIC "any" ) → json<br/>
     * Builds a possibly-heterogeneously-typed JSON array out of a variadic argument list. Each argument is converted as per to_json or to_jsonb.
     * </a>
     */
    public static SimpleExpression jsonbBuildArray(Consumer<Consumer<Object>> consumer) {
        return FunctionUtils.rowElementFunc("jsonb_build_array", false, consumer, JsonbType.TEXT);
    }

    public static SimpleExpression jsonBuildObject() {
        return FunctionUtils.zeroArgFunc("json_build_object", JsonType.TEXT);
    }

    public static SimpleExpression jsonbBuildObject() {
        return FunctionUtils.zeroArgFunc("jsonb_build_object", JsonbType.TEXT);
    }


    public static SimpleExpression jsonBuildObject(String tableAlias, SQLs.SymbolPeriod period, TableMeta<?> table) {
        return FunctionUtils.oneArgObjectElementFunc("json_build_object", ContextStack.peek().row(tableAlias, period, table),
                JsonType.TEXT);
    }

    public static SimpleExpression jsonBuildObject(String derivedAlias, SQLs.SymbolPeriod period, SQLs.SymbolAsterisk asterisk) {
        return FunctionUtils.oneArgObjectElementFunc("json_build_object", ContextStack.peek().row(derivedAlias, period, asterisk),
                JsonType.TEXT);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonType#TEXT}
     * *
     *
     * @param consumer value valid type:<ul>
     *                 <li>constant</li>
     *                 <li>{@link RowExpression}</li>
     *                 <li>{@link Expression}</li>
     *                 <li> {@link SQLs#row(Object)} </li>
     *                 <li>{@link SQLs#row(Consumer)}</li>
     *                 <li>{@link SQLs#space(String, SQLs.SymbolPeriod, SQLs.SymbolAsterisk)}</li>
     *                 <li>{@link SQLs#space(String, SQLs.SymbolPeriod, TableMeta)}</li>
     *                 </ul>
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-CREATION-TABLE">json_build_object ( VARIADIC "any" ) → json<br/>
     * Builds a possibly-heterogeneously-typed JSON array out of a variadic argument list. Each argument is converted as per to_json or to_jsonb.
     * </a>
     */
    public static SimpleExpression jsonBuildObject(Consumer<Clause._PairVariadicSpaceClause> consumer) {
        return FunctionUtils.objectElementFunc("json_build_object", false, consumer, JsonType.TEXT);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonType#TEXT}
     * *
     *
     * @param space    see {@link SQLs#SPACE}
     * @param consumer value valid type:<ul>
     *                 <li>constant</li>
     *                 <li>{@link RowExpression}</li>
     *                 <li>{@link Expression}</li>
     *                 <li> {@link SQLs#row(Object)} </li>
     *                 <li>{@link SQLs#row(Consumer)}</li>
     *                 <li>{@link SQLs#space(String, SQLs.SymbolPeriod, SQLs.SymbolAsterisk)}</li>
     *                 <li>{@link SQLs#space(String, SQLs.SymbolPeriod, TableMeta)}</li>
     *                 </ul>
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-CREATION-TABLE">json_build_object ( VARIADIC "any" ) → json<br/>
     * Builds a possibly-heterogeneously-typed JSON array out of a variadic argument list. Each argument is converted as per to_json or to_jsonb.
     * </a>
     */
    public static SimpleExpression jsonBuildObject(SQLs.SymbolSpace space, Consumer<Clause._PairVariadicConsumerClause> consumer) {
        return FunctionUtils.objectElementFunc(space, "json_build_object", false, consumer, JsonType.TEXT);
    }

    public static SimpleExpression jsonbBuildObject(String tableAlias, SQLs.SymbolPeriod period, TableMeta<?> table) {
        return FunctionUtils.oneArgObjectElementFunc("jsonb_build_object", ContextStack.peek().row(tableAlias, period, table),
                JsonbType.TEXT);
    }

    public static SimpleExpression jsonbBuildObject(String derivedAlias, SQLs.SymbolPeriod period, SQLs.SymbolAsterisk asterisk) {
        return FunctionUtils.oneArgObjectElementFunc("jsonb_build_object", ContextStack.peek().row(derivedAlias, period, asterisk),
                JsonbType.TEXT);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonbType#TEXT}
     * *
     *
     * @param consumer value valid type:<ul>
     *                 <li>constant</li>
     *                 <li>{@link RowExpression}</li>
     *                 <li>{@link Expression}</li>
     *                 <li> {@link SQLs#row(Object)} </li>
     *                 <li>{@link SQLs#row(Consumer)}</li>
     *                 <li>{@link SQLs#space(String, SQLs.SymbolPeriod, SQLs.SymbolAsterisk)}</li>
     *                 <li>{@link SQLs#space(String, SQLs.SymbolPeriod, TableMeta)}</li>
     *                 </ul>
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-CREATION-TABLE">jsonb_build_object ( VARIADIC "any" ) → json<br/>
     * Builds a possibly-heterogeneously-typed JSON array out of a variadic argument list. Each argument is converted as per to_json or to_jsonb.
     * </a>
     */
    public static SimpleExpression jsonbBuildObject(Consumer<Clause._PairVariadicSpaceClause> consumer) {
        return FunctionUtils.objectElementFunc("jsonb_build_object", false, consumer, JsonbType.TEXT);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonbType#TEXT}
     * *
     *
     * @param space    see {@link SQLs#SPACE}
     * @param consumer value valid type:<ul>
     *                 <li>constant</li>
     *                 <li>{@link RowExpression}</li>
     *                 <li>{@link Expression}</li>
     *                 <li> {@link SQLs#row(Object)} </li>
     *                 <li>{@link SQLs#row(Consumer)}</li>
     *                 <li>{@link SQLs#space(String, SQLs.SymbolPeriod, SQLs.SymbolAsterisk)}</li>
     *                 <li>{@link SQLs#space(String, SQLs.SymbolPeriod, TableMeta)}</li>
     *                 </ul>
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-CREATION-TABLE">jsonb_build_object ( VARIADIC "any" ) → json<br/>
     * Builds a possibly-heterogeneously-typed JSON array out of a variadic argument list. Each argument is converted as per to_json or to_jsonb.
     * </a>
     */
    public static SimpleExpression jsonbBuildObject(SQLs.SymbolSpace space, Consumer<Clause._PairVariadicConsumerClause> consumer) {
        return FunctionUtils.objectElementFunc(space, "jsonb_build_object", false, consumer, JsonbType.TEXT);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonType#TEXT}
     * *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-CREATION-TABLE">json_object ( text[] ) → json<br/>
     * Builds a JSON object out of a text array. The array must have either exactly one dimension with an even number of members, in which case they are taken<br/>
     * as alternating key/value pairs, or two dimensions such that each inner array has exactly two elements, which are taken as a key/value pair. All values are<br/>
     * converted to JSON strings.
     * </a>
     */
    public static SimpleExpression jsonObject(Expression pairArray) {
        return FunctionUtils.oneArgFunc("json_object", pairArray, JsonType.TEXT);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonType#TEXT}
     * *
     *
     * @param funcRef the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                <ul>
     *                    <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                    <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                    <li>developer custom method</li>
     *                </ul>.
     *                The first argument of funcRef always is {@link TextArrayType#LINEAR}.
     * @see #jsonObject(Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-CREATION-TABLE">json_object ( text[] ) → json<br/>
     * Builds a JSON object out of a text array. The array must have either exactly one dimension with an even number of members, in which case they are taken<br/>
     * as alternating key/value pairs, or two dimensions such that each inner array has exactly two elements, which are taken as a key/value pair. All values are<br/>
     * converted to JSON strings.
     * </a>
     */
    public static SimpleExpression jsonObject(BiFunction<MappingType, String[], Expression> funcRef,
                                              Consumer<Statement._StringObjectSpaceClause> consumer) {
        return FunctionUtils.staticStringObjectStringFunc("json_object", false, funcRef, TextArrayType.LINEAR, consumer,
                JsonType.TEXT);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonType#TEXT}
     * *
     *
     * @param funcRef the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                <ul>
     *                    <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                    <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                    <li>developer custom method</li>
     *                </ul>.
     *                The first argument of funcRef always is {@link TextArrayType#LINEAR}.
     * @see #jsonObject(Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-CREATION-TABLE">json_object ( text[] ) → json<br/>
     * Builds a JSON object out of a text array. The array must have either exactly one dimension with an even number of members, in which case they are taken<br/>
     * as alternating key/value pairs, or two dimensions such that each inner array has exactly two elements, which are taken as a key/value pair. All values are<br/>
     * converted to JSON strings.
     * </a>
     */
    public static SimpleExpression jsonObject(SQLs.SymbolSpace space, final BiFunction<MappingType, String[], Expression> funcRef,
                                              final Consumer<Statement._StringObjectConsumer> consumer) {
        return FunctionUtils.dynamicStringObjectStringFunc("json_object", space, false, funcRef, TextArrayType.LINEAR, consumer,
                JsonType.TEXT);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonbType#TEXT}
     * *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-CREATION-TABLE">jsonb_object ( text[] ) → json<br/>
     * Builds a JSON object out of a text array. The array must have either exactly one dimension with an even number of members, in which case they are taken<br/>
     * as alternating key/value pairs, or two dimensions such that each inner array has exactly two elements, which are taken as a key/value pair. All values are<br/>
     * converted to JSON strings.
     * </a>
     */
    public static SimpleExpression jsonbObject(Expression pairArray) {
        return FunctionUtils.oneArgFunc("jsonb_object", pairArray, JsonbType.TEXT);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonbType#TEXT}
     * *
     *
     * @param funcRef the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                <ul>
     *                    <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                    <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                    <li>developer custom method</li>
     *                </ul>.
     *                The first argument of funcRef always is {@link TextArrayType#LINEAR}.
     * @see #jsonbObject(Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-CREATION-TABLE">jsonb_object ( text[] ) → json<br/>
     * Builds a JSON object out of a text array. The array must have either exactly one dimension with an even number of members, in which case they are taken<br/>
     * as alternating key/value pairs, or two dimensions such that each inner array has exactly two elements, which are taken as a key/value pair. All values are<br/>
     * converted to JSON strings.
     * </a>
     */
    public static SimpleExpression jsonbObject(BiFunction<MappingType, String[], Expression> funcRef,
                                               Consumer<Statement._StringObjectSpaceClause> consumer) {
        return FunctionUtils.staticStringObjectStringFunc("jsonb_object", false, funcRef, TextArrayType.LINEAR, consumer,
                JsonbType.TEXT);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonbType#TEXT}
     * *
     *
     * @param funcRef the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                <ul>
     *                    <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                    <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                    <li>developer custom method</li>
     *                </ul>.
     *                The first argument of funcRef always is {@link TextArrayType#LINEAR}.
     * @see #jsonbObject(Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-CREATION-TABLE">jsonb_object ( text[] ) → json<br/>
     * Builds a JSON object out of a text array. The array must have either exactly one dimension with an even number of members, in which case they are taken<br/>
     * as alternating key/value pairs, or two dimensions such that each inner array has exactly two elements, which are taken as a key/value pair. All values are<br/>
     * converted to JSON strings.
     * </a>
     */
    public static SimpleExpression jsonbObject(SQLs.SymbolSpace space, final BiFunction<MappingType, String[], Expression> funcRef,
                                               final Consumer<Statement._StringObjectConsumer> consumer) {
        return FunctionUtils.dynamicStringObjectStringFunc("jsonb_object", space, false, funcRef, TextArrayType.LINEAR, consumer,
                JsonbType.TEXT);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonType#TEXT}
     * *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-CREATION-TABLE">json_object ( keys text[], values text[] ) → json<br/>
     * This form of json_object takes keys and values pairwise from separate text arrays. Otherwise it is identical to the one-argument form.<br/>
     * json_object('{a,b}', '{1,2}') → {"a": "1", "b": "2"}<br/>
     * </a>
     */
    public static SimpleExpression jsonObject(Expression keyArray, Expression valueArray) {
        return FunctionUtils.twoArgFunc("json_object", keyArray, valueArray, JsonType.TEXT);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonbType#TEXT}
     * *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-CREATION-TABLE">jsonb_object ( keys text[], values text[] ) → json<br/>
     * This form of json_object takes keys and values pairwise from separate text arrays. Otherwise it is identical to the one-argument form.<br/>
     * json_object('{a,b}', '{1,2}') → {"a": "1", "b": "2"}<br/>
     * </a>
     */
    public static SimpleExpression jsonbObject(Expression keyArray, Expression valueArray) {
        return FunctionUtils.twoArgFunc("jsonb_object", keyArray, valueArray, JsonbType.TEXT);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonType#TEXT}.
     * Default Selection alias is 'value'.
     * * <pre><br/>
     *   select * from json_array_elements('[1,true, [2,false]]') →
     *
     *      value
     *   -----------
     *    1
     *    true
     *    [2,false]
     * </pre>
     *
     * @param funcRef the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                <ul>
     *                    <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                    <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                    <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                    <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                    <li>developer custom method</li>
     *                </ul>.
     *                The first argument of funcRef always is {@link JsonType#TEXT}.
     * @param value   non-null,it will be passed to funcRef as the second argument of funcRef
     * @see #jsonArrayElements(Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">json_array_elements ( json ) → setof json<br/>
     * Expands the top-level JSON array into a set of JSON values.<br/>
     * </a>
     */
    public static <T> _ColumnWithOrdinalityFunction jsonArrayElements(final BiFunction<MappingType, T, Expression> funcRef, T value) {
        return jsonArrayElements(funcRef.apply(JsonType.TEXT, value));
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonType#TEXT}.
     * Default Selection alias is 'value'.
     * * <pre><br/>
     *   select * from json_array_elements('[1,true, [2,false]]') →
     *
     *      value
     *   -----------
     *    1
     *    true
     *    [2,false]
     * </pre>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">json_array_elements ( json ) → setof json<br/>
     * Expands the top-level JSON array into a set of JSON values.<br/>
     * </a>
     */
    public static _ColumnWithOrdinalityFunction jsonArrayElements(final Expression json) {
        return DialectFunctionUtils.oneArgColumnFunction("JSON_ARRAY_ELEMENTS", json, "value", JsonType.TEXT);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonbType#TEXT}.
     * Default Selection alias is 'value'.
     * * <pre><br/>
     *   select * from jsonb_array_elements('[1,true, [2,false]]') →
     *
     *      value
     *   -----------
     *    1
     *    true
     *    [2,false]
     * </pre>
     *
     * @param funcRef the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                <ul>
     *                    <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                    <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                    <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                    <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                    <li>developer custom method</li>
     *                </ul>.
     *                The first argument of funcRef always is {@link JsonbType#TEXT}.
     * @param value   non-null,it will be passed to funcRef as the second argument of funcRef
     * @see #jsonbArrayElements(Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">jsonb_array_elements ( json ) → setof json<br/>
     * Expands the top-level JSON array into a set of JSON values.<br/>
     * </a>
     */
    public static <T> _ColumnWithOrdinalityFunction jsonbArrayElements(final BiFunction<MappingType, T, Expression> funcRef, T value) {
        return jsonbArrayElements(funcRef.apply(JsonbType.TEXT, value));
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonbType#TEXT}.
     * Default Selection alias is 'value'.
     * * <pre><br/>
     *   select * from jsonb_array_elements('[1,true, [2,false]]') →
     *
     *      value
     *   -----------
     *    1
     *    true
     *    [2,false]
     * </pre>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">jsonb_array_elements ( json ) → setof json<br/>
     * Expands the top-level JSON array into a set of JSON values.<br/>
     * </a>
     */
    public static _ColumnWithOrdinalityFunction jsonbArrayElements(final Expression json) {
        return DialectFunctionUtils.oneArgColumnFunction("JSONB_ARRAY_ELEMENTS", json, "value", JsonbType.TEXT);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link TextType#INSTANCE}.
     * Default Selection alias is 'value'.
     * * <pre><br/>
     *   select * from json_array_elements_text('["foo", "bar"]') →
     *
     *      value
     *   -----------
     *    foo
     *    bar
     * </pre>
     *
     * @param funcRef the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                <ul>
     *                    <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                    <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                    <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                    <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                    <li>developer custom method</li>
     *                </ul>.
     *                The first argument of funcRef always is {@link JsonType#TEXT}.
     * @param value   non-null,it will be passed to funcRef as the second argument of funcRef
     * @see #jsonArrayElementsText(Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">json_array_elements_text ( json ) → setof text<br/>
     * Expands the top-level JSON array into a set of JSON values.<br/>
     * </a>
     */
    public static <T> _ColumnWithOrdinalityFunction jsonArrayElementsText(final BiFunction<MappingType, T, Expression> funcRef, T value) {
        return jsonArrayElementsText(funcRef.apply(JsonType.TEXT, value));
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link TextType#INSTANCE}.
     * Default Selection alias is 'value'.
     * * <pre><br/>
     *   select * from json_array_elements_text('["foo", "bar"]') →
     *
     *      value
     *   -----------
     *    foo
     *    bar
     * </pre>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">json_array_elements_text ( json ) → setof text<br/>
     * Expands the top-level JSON array into a set of text values.<br/>
     * </a>
     */
    public static _ColumnWithOrdinalityFunction jsonArrayElementsText(final Expression json) {
        return DialectFunctionUtils.oneArgColumnFunction("JSON_ARRAY_ELEMENTS_TEXT", json, "value", TextType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link TextType#INSTANCE}.
     * Default Selection alias is 'value'.
     * * <pre><br/>
     *   select * from jsonb_array_elements_text('["foo", "bar"]') →
     *
     *      value
     *   -----------
     *    foo
     *    bar
     * </pre>
     *
     * @param funcRef the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                <ul>
     *                    <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                    <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                    <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                    <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                    <li>developer custom method</li>
     *                </ul>.
     *                The first argument of funcRef always is {@link JsonbType#TEXT}.
     * @param value   non-null,it will be passed to funcRef as the second argument of funcRef
     * @see #jsonbArrayElementsText(Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">jsonb_array_elements_text ( json ) → setof text<br/>
     * Expands the top-level JSON array into a set of JSON values.<br/>
     * </a>
     */
    public static <T> _ColumnWithOrdinalityFunction jsonbArrayElementsText(final BiFunction<MappingType, T, Expression> funcRef, T value) {
        return jsonbArrayElementsText(funcRef.apply(JsonbType.TEXT, value));
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link TextType#INSTANCE}.
     * Default Selection alias is 'value'.
     * * <pre><br/>
     *   select * from jsonb_array_elements_text('["foo", "bar"]') →
     *
     *      value
     *   -----------
     *    foo
     *    bar
     * </pre>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">jsonb_array_elements_text ( json ) → setof text<br/>
     * Expands the top-level JSON array into a set of text values.<br/>
     * </a>
     */
    public static _ColumnWithOrdinalityFunction jsonbArrayElementsText(final Expression json) {
        return DialectFunctionUtils.oneArgColumnFunction("JSONB_ARRAY_ELEMENTS_TEXT", json, "value", TextType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link IntegerType}.
     * *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">json_array_length ( json ) → integer<br/>
     * Returns the number of elements in the top-level JSON array.<br/>
     * </a>
     */
    public static SimpleExpression jsonArrayLength(Expression json) {
        return FunctionUtils.oneArgFunc("JSON_ARRAY_LENGTH", json, IntegerType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type: {@link IntegerType}.
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">jsonb_array_length ( json ) → integer<br/>
     * Returns the number of elements in the top-level JSON array.<br/>
     * </a>
     */
    public static SimpleExpression jsonbArrayLength(Expression jsonb) {
        return FunctionUtils.oneArgFunc("JSONB_ARRAY_LENGTH", jsonb, IntegerType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of fields of derived table :<ul>
     * <li>key : {@link TextType}</li>
     * <li>value : {@link JsonType#TEXT}</li>
     * <li>ordinality (optioinal) : {@link IntegerType}. see {@link _WithOrdinalityClause#withOrdinality()}</li>
     * </ul>
     * * <pre><br/>
     *   select * from json_each('{"a":"foo", "b":"bar"}') →
     *
     *    key | value
     *   -----+-------
     *    a   | "foo"
     *    b   | "bar"
     * </pre>
     *
     * @param funcRef the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                <ul>
     *                    <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                    <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                    <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                    <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                    <li>developer custom method</li>
     *                </ul>.
     *                The first argument of funcRef always is {@link JsonType#TEXT}.
     * @param value   non-null,it will be passed to funcRef as the second argument of funcRef
     * @see #jsonEach(Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">json_each ( json ) → setof record ( key text, value json )<br/>
     * Expands the top-level JSON object into a set of key/value pairs.<br/>
     * </a>
     */
    public static <T> _TabularWithOrdinalityFunction jsonEach(BiFunction<MappingType, T, Expression> funcRef, T value) {
        return jsonEach(funcRef.apply(JsonType.TEXT, value));
    }


    /**
     * <p>
     * The {@link MappingType} of fields of derived table :<ul>
     * <li>key : {@link TextType}</li>
     * <li>value : {@link JsonType#TEXT}</li>
     * <li>ordinality (optioinal) : {@link IntegerType}. see {@link _WithOrdinalityClause#withOrdinality()}</li>
     * </ul>
     * * <pre><br/>
     *   select * from json_each('{"a":"foo", "b":"bar"}') →
     *
     *    key | value
     *   -----+-------
     *    a   | "foo"
     *    b   | "bar"
     * </pre>
     *
     * @see #jsonEach(Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">json_each ( json ) → setof record ( key text, value json )<br/>
     * Expands the top-level JSON object into a set of key/value pairs.<br/>
     * </a>
     */
    public static _TabularWithOrdinalityFunction jsonEach(final Expression json) {
        final List<Selection> fieldList = _Collections.arrayList(2);
        fieldList.add(ArmySelections.forName("key", TextType.INSTANCE));
        fieldList.add(ArmySelections.forName("value", JsonType.TEXT));
        return DialectFunctionUtils.oneArgTabularFunc("JSON_EACH", json, fieldList);
    }

    /**
     * <p>
     * The {@link MappingType} of fields of derived table :<ul>
     * <li>key : {@link TextType}</li>
     * <li>value : {@link JsonbType#TEXT}</li>
     * <li>ordinality (optioinal) : {@link IntegerType}. see {@link _WithOrdinalityClause#withOrdinality()}</li>
     * </ul>
     *  <pre><br/>
     *   select * from jsonb_each('{"a":"foo", "b":"bar"}') →
     *
     *    key | value
     *   -----+-------
     *    a   | "foo"
     *    b   | "bar"
     * </pre>
     *
     * @param funcRef the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                <ul>
     *                    <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                    <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                    <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                    <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                    <li>developer custom method</li>
     *                </ul>.
     *                The first argument of funcRef always is {@link JsonbType#TEXT}.
     * @param value   non-null,it will be passed to funcRef as the second argument of funcRef
     * @see #jsonbEach(Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">jsonb_each ( json ) → setof record ( key text, value json )<br/>
     * Expands the top-level JSON object into a set of key/value pairs.<br/>
     * </a>
     */
    public static <T> _TabularWithOrdinalityFunction jsonbEach(BiFunction<MappingType, T, Expression> funcRef, T value) {
        return jsonbEach(funcRef.apply(JsonbType.TEXT, value));
    }


    /**
     * <p>
     * The {@link MappingType} of fields of derived table :<ul>
     * <li>key : {@link TextType}</li>
     * <li>value : {@link JsonbType#TEXT}</li>
     * <li>ordinality (optioinal) : {@link IntegerType}. see {@link _WithOrdinalityClause#withOrdinality()}</li>
     * </ul>
     * <pre><br/>
     *   select * from jsonb_each('{"a":"foo", "b":"bar"}') →
     *
     *    key | value
     *   -----+-------
     *    a   | "foo"
     *    b   | "bar"
     * </pre>
     *
     * @see #jsonEach(Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">jsonb_each ( json ) → setof record ( key text, value json )<br/>
     * Expands the top-level JSON object into a set of key/value pairs.<br/>
     * </a>
     */
    public static _TabularWithOrdinalityFunction jsonbEach(final Expression json) {
        final List<Selection> fieldList = _Collections.arrayList(2);
        fieldList.add(ArmySelections.forName("key", TextType.INSTANCE));
        fieldList.add(ArmySelections.forName("value", JsonbType.TEXT));
        return DialectFunctionUtils.oneArgTabularFunc("JSONB_EACH", json, fieldList);
    }


    /**
     * <p>
     * The {@link MappingType} of fields of derived table :<ul>
     * <li>key : {@link TextType}</li>
     * <li>value : {@link TextType}</li>
     * <li>ordinality (optioinal) : {@link IntegerType}. see {@link _WithOrdinalityClause#withOrdinality()}</li>
     * </ul>
     * * <pre><br/>
     *   select * from json_each_text('{"a":"foo", "b":"bar"}') →
     *
     *    key | value
     *   -----+-------
     *    a   | "foo"
     *    b   | "bar"
     * </pre>
     *
     * @param funcRef the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                <ul>
     *                    <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                    <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                    <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                    <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                    <li>developer custom method</li>
     *                </ul>.
     *                The first argument of funcRef always is {@link JsonType#TEXT}.
     * @param value   non-null,it will be passed to funcRef as the second argument of funcRef
     * @see #jsonEachText(Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">json_each_text ( json ) → setof record ( key text, value text )<br/>
     * Expands the top-level JSON object into a set of key/value pairs. The returned values will be of type text.<br/>
     * </a>
     */
    public static <T> _TabularWithOrdinalityFunction jsonEachText(BiFunction<MappingType, T, Expression> funcRef, T value) {
        return jsonEachText(funcRef.apply(JsonType.TEXT, value));
    }


    /**
     * <p>
     * The {@link MappingType} of fields of derived table :<ul>
     * <li>key : {@link TextType}</li>
     * <li>value : {@link TextType}</li>
     * <li>ordinality (optioinal) : {@link IntegerType}. see {@link _WithOrdinalityClause#withOrdinality()}</li>
     * </ul>
     * * <pre><br/>
     *   select * from json_each_text('{"a":"foo", "b":"bar"}') →
     *
     *    key | value
     *   -----+-------
     *    a   | "foo"
     *    b   | "bar"
     * </pre>
     *
     * @see #jsonEachText(BiFunction, Object)
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">json_each_text ( json ) → setof record ( key text, value text )<br/>
     * Expands the top-level JSON object into a set of key/value pairs. The returned values will be of type text.<br/>
     * </a>
     */
    public static _TabularWithOrdinalityFunction jsonEachText(final Expression json) {
        final List<Selection> fieldList = _Collections.arrayList(2);
        fieldList.add(ArmySelections.forName("key", TextType.INSTANCE));
        fieldList.add(ArmySelections.forName("value", TextType.INSTANCE));
        return DialectFunctionUtils.oneArgTabularFunc("JSON_EACH_TEXT", json, fieldList);
    }

    /**
     * <p>
     * The {@link MappingType} of fields of derived table :<ul>
     * <li>key : {@link TextType}</li>
     * <li>value : {@link TextType}</li>
     * <li>ordinality (optioinal) : {@link IntegerType}. see {@link _WithOrdinalityClause#withOrdinality()}</li>
     * </ul>
     * * <pre><br/>
     *   select * from jsonb_each_text('{"a":"foo", "b":"bar"}') →
     *
     *    key | value
     *   -----+-------
     *    a   | "foo"
     *    b   | "bar"
     * </pre>
     *
     * @param funcRef the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                <ul>
     *                    <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                    <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                    <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                    <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                    <li>developer custom method</li>
     *                </ul>.
     *                The first argument of funcRef always is {@link JsonbType#TEXT}.
     * @param value   non-null,it will be passed to funcRef as the second argument of funcRef
     * @see #jsonbEachText(Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">jsonb_each_text ( json ) → setof record ( key text, value text )<br/>
     * Expands the top-level JSON object into a set of key/value pairs. The returned values will be of type text.<br/>
     * </a>
     */
    public static <T> _TabularWithOrdinalityFunction jsonbEachText(BiFunction<MappingType, T, Expression> funcRef, T value) {
        return jsonbEachText(funcRef.apply(JsonbType.TEXT, value));
    }


    /**
     * <p>
     * The {@link MappingType} of fields of derived table :<ul>
     * <li>key : {@link StringType}</li>
     * <li>value : {@link TextType}</li>
     * <li>ordinality (optioinal) : {@link IntegerType}. see {@link _WithOrdinalityClause#withOrdinality()}</li>
     * </ul>
     * * <pre><br/>
     *   select * from jsonb_each_text('{"a":"foo", "b":"bar"}') →
     *
     *    key | value
     *   -----+-------
     *    a   | "foo"
     *    b   | "bar"
     * </pre>
     *
     * @see #jsonbEachText(BiFunction, Object)
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">jsonb_each_text ( json ) → setof record ( key text, value text )<br/>
     * Expands the top-level JSON object into a set of key/value pairs. The returned values will be of type text.<br/>
     * </a>
     */
    public static _TabularWithOrdinalityFunction jsonbEachText(final Expression json) {
        final List<Selection> fieldList = _Collections.arrayList(2);
        fieldList.add(ArmySelections.forName("key", TextType.INSTANCE));
        fieldList.add(ArmySelections.forName("value", TextType.INSTANCE));
        return DialectFunctionUtils.oneArgTabularFunc("JSONB_EACH_TEXT", json, fieldList);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonType#TEXT}.
     * *
     *
     * @param firstPath should be {@link TextType} or {@link StringType} type.
     * @param rest      each element should be {@link TextType} or {@link StringType} type.
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">json_extract_path ( from_json json, VARIADIC path_elems text[] ) → json<br/>
     * Extracts JSON sub-object at the specified path. (This is functionally equivalent to the #> operator, but writing the path out as a variadic list can be more convenient in some cases.)<br/>
     * </a>
     */
    public static SimpleExpression jsonExtractPath(Expression fromJson, Expression firstPath, Expression... rest) {
        final String name = "JSON_EXTRACT_PATH";
        return FunctionUtils.oneAndAtLeastFunc(name, JsonType.TEXT, fromJson, firstPath, rest);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonType#TEXT}.
     * *
     *
     * @param consumer should be {@link TextType} or {@link StringType} type.
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">json_extract_path ( from_json json, VARIADIC path_elems text[] ) → json<br/>
     * Extracts JSON sub-object at the specified path. (This is functionally equivalent to the #> operator, but writing the path out as a variadic list can be more convenient in some cases.)<br/>
     * </a>
     */
    public static SimpleExpression jsonExtractPath(Expression fromJson, Consumer<Consumer<Expression>> consumer) {
        final String name = "JSON_EXTRACT_PATH";
        return FunctionUtils.oneAndConsumer(name, true, fromJson, consumer, JsonType.TEXT);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonType#TEXT}.
     * *
     *
     * @param funcRefForJson the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                       <ul>
     *                           <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                           <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                           <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                           <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                           <li>developer custom method</li>
     *                       </ul>.
     *                       The first argument of funcRefForJson always is {@link JsonType#TEXT}.
     * @param json           non-null,it will be passed to funcRefForJson as the second argument of funcRefForJson
     * @param funcRefForPath the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                       <ul>
     *                           <li>{@link SQLs#rowParam(TypeInfer, Collection)}</li>
     *                           <li>{@link SQLs#rowLiteral(TypeInfer, Collection)}</li>
     *                           <li>developer custom method</li>
     *                       </ul>.
     *                       The first argument of funcRefForPath always is {@link NoCastTextType#INSTANCE}.
     * @param firstPath      firstPath and rest will be collected to unmodified {@link List} and passed to funcRef as the second argument of funcRef
     * @param rest           rest and firstPath will be collected to unmodified {@link List} and passed to funcRef as the second argument of funcRef
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">json_extract_path ( from_json json, VARIADIC path_elems text[] ) → json<br/>
     * Extracts JSON sub-object at the specified path. (This is functionally equivalent to the #> operator, but writing the path out as a variadic list can be more convenient in some cases.)<br/>
     * </a>
     */
    public static <T> Expression jsonExtractPath(BiFunction<MappingType, T, Expression> funcRefForJson, T json,
                                                       BiFunction<MappingType, List<String>, Expression> funcRefForPath,
                                                       String firstPath, String... rest) {
        return jsonExtractPath(funcRefForJson.apply(JsonType.TEXT, json), funcRefForPath, firstPath, rest);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonType#TEXT}.
     * *
     *
     * @param funcRef   the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                  <ul>
     *                      <li>{@link SQLs#rowParam(TypeInfer, Collection)}</li>
     *                      <li>{@link SQLs#rowLiteral(TypeInfer, Collection)}</li>
     *                      <li>developer custom method</li>
     *                  </ul>.
     *                  The first argument of funcRef always is {@link NoCastTextType#INSTANCE}.
     * @param firstPath firstPath and rest will be collected to unmodified {@link List} and passed to funcRef as the second argument of funcRef
     * @param rest      rest and firstPath will be collected to unmodified {@link List} and passed to funcRef as the second argument of funcRef
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">json_extract_path ( from_json json, VARIADIC path_elems text[] ) → json<br/>
     * Extracts JSON sub-object at the specified path. (This is functionally equivalent to the #> operator, but writing the path out as a variadic list can be more convenient in some cases.)<br/>
     * </a>
     */
    public static SimpleExpression jsonExtractPath(Expression fromJson, BiFunction<MappingType, List<String>, Expression> funcRef,
                                                   String firstPath, String... rest) {
        final String name = "JSON_EXTRACT_PATH";
        final List<String> pathElemList;
        pathElemList = ArrayUtils.unmodifiableListOf(firstPath, rest);
        return FunctionUtils.oneAndMulti(name, fromJson, funcRef.apply(NoCastTextType.INSTANCE, pathElemList), JsonType.TEXT);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonType#TEXT}.
     * *
     *
     * @param funcRef the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                <ul>
     *                    <li>{@link SQLs#rowParam(TypeInfer, Collection)}</li>
     *                    <li>{@link SQLs#rowLiteral(TypeInfer, Collection)}</li>
     *                    <li>developer custom method</li>
     *                </ul>.
     *                The first argument of funcRefForPath always is {@link NoCastTextType#INSTANCE}.
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">json_extract_path ( from_json json, VARIADIC path_elems text[] ) → json<br/>
     * Extracts JSON sub-object at the specified path. (This is functionally equivalent to the #> operator, but writing the path out as a variadic list can be more convenient in some cases.)<br/>
     * </a>
     */
    public static SimpleExpression jsonExtractPath(Expression fromJson, BiFunction<MappingType, List<String>, Expression> funcRef,
                                                   Consumer<Consumer<String>> consumer) {
        return FunctionUtils.oneAndMulti("JSON_EXTRACT_PATH", fromJson,
                funcRef.apply(NoCastTextType.INSTANCE, CriteriaUtils.stringList(null, true, consumer)),
                JsonType.TEXT
        );
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonbType#TEXT}.
     * *
     *
     * @param firstPath should be {@link TextType} or {@link StringType} type.
     * @param rest      each element should be {@link TextType} or {@link StringType} type.
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">jsonb_extract_path ( from_json jsonb, VARIADIC path_elems text[] ) → jsonb<br/>
     * Extracts JSON sub-object at the specified path. (This is functionally equivalent to the #> operator, but writing the path out as a variadic list can be more convenient in some cases.)<br/>
     * </a>
     */
    public static SimpleExpression jsonbExtractPath(Expression fromJson, Expression firstPath, Expression... rest) {
        final String name = "JSONB_EXTRACT_PATH";
        return FunctionUtils.oneAndAtLeastFunc(name, JsonbType.TEXT, fromJson, firstPath, rest);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonbType#TEXT}.
     * *
     *
     * @param consumer should be {@link TextType} or {@link StringType} type.
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">jsonb_extract_path ( from_json json, VARIADIC path_elems text[] ) → json<br/>
     * Extracts JSON sub-object at the specified path. (This is functionally equivalent to the #> operator, but writing the path out as a variadic list can be more convenient in some cases.)<br/>
     * </a>
     */
    public static SimpleExpression jsonbExtractPath(Expression fromJson, Consumer<Consumer<Expression>> consumer) {
        final String name = "JSONB_EXTRACT_PATH";
        return FunctionUtils.oneAndConsumer(name, true, fromJson, consumer, JsonbType.TEXT);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonbType#TEXT}.
     * *
     *
     * @param funcRefForJson the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                       <ul>
     *                           <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                           <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                           <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                           <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                           <li>developer custom method</li>
     *                       </ul>.
     *                       The first argument of funcRefForJson always is {@link JsonbType#TEXT}.
     * @param json           non-null,it will be passed to funcRefForJson as the second argument of funcRefForJson
     * @param funcRefForPath the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                       <ul>
     *                           <li>{@link SQLs#rowParam(TypeInfer, Collection)}</li>
     *                           <li>{@link SQLs#rowLiteral(TypeInfer, Collection)}</li>
     *                           <li>developer custom method</li>
     *                       </ul>.
     *                       The first argument of funcRefForPath always is {@link NoCastTextType#INSTANCE}.
     * @param firstPath      firstPath and rest will be collected to unmodified {@link List} and passed to funcRef as the second argument of funcRef
     * @param rest           rest and firstPath will be collected to unmodified {@link List} and passed to funcRef as the second argument of funcRef
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">jsonb_extract_path ( from_json jsonb, VARIADIC path_elems text[] ) → jsonb<br/>
     * Extracts JSON sub-object at the specified path. (This is functionally equivalent to the #> operator, but writing the path out as a variadic list can be more convenient in some cases.)<br/>
     * </a>
     */
    public static <T> Expression jsonbExtractPath(BiFunction<MappingType, T, Expression> funcRefForJson, T json,
                                                        BiFunction<MappingType, List<String>, Expression> funcRefForPath,
                                                        String firstPath, String... rest) {
        return jsonbExtractPath(funcRefForJson.apply(JsonbType.TEXT, json), funcRefForPath, firstPath, rest);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonbType#TEXT}.
     * *
     *
     * @param funcRef   the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                  <ul>
     *                      <li>{@link SQLs#rowParam(TypeInfer, Collection)}</li>
     *                      <li>{@link SQLs#rowLiteral(TypeInfer, Collection)}</li>
     *                      <li>developer custom method</li>
     *                  </ul>.
     *                  The first argument of funcRef always is {@link NoCastTextType#INSTANCE}.
     * @param firstPath firstPath and rest will be collected to unmodified {@link List} and passed to funcRef as the second argument of funcRef
     * @param rest      rest and firstPath will be collected to unmodified {@link List} and passed to funcRef as the second argument of funcRef
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">jsonb_extract_path ( from_json jsonb, VARIADIC path_elems text[] ) → jsonb<br/>
     * Extracts JSON sub-object at the specified path. (This is functionally equivalent to the #> operator, but writing the path out as a variadic list can be more convenient in some cases.)<br/>
     * </a>
     */
    public static SimpleExpression jsonbExtractPath(Expression fromJson, BiFunction<MappingType, List<String>, Expression> funcRef,
                                                    String firstPath, String... rest) {
        final String name = "JSONB_EXTRACT_PATH";
        final List<String> pathElemList;
        pathElemList = ArrayUtils.unmodifiableListOf(firstPath, rest);
        return FunctionUtils.oneAndMulti(name, fromJson, funcRef.apply(NoCastTextType.INSTANCE, pathElemList), JsonbType.TEXT);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonbType#TEXT}.
     * *
     *
     * @param funcRef the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                <ul>
     *                    <li>{@link SQLs#rowParam(TypeInfer, Collection)}</li>
     *                    <li>{@link SQLs#rowLiteral(TypeInfer, Collection)}</li>
     *                    <li>developer custom method</li>
     *                </ul>.
     *                The first argument of funcRefForPath always is {@link NoCastTextType#INSTANCE}.
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">jsonb_extract_path ( from_json json, VARIADIC path_elems text[] ) → json<br/>
     * Extracts JSON sub-object at the specified path. (This is functionally equivalent to the #> operator, but writing the path out as a variadic list can be more convenient in some cases.)<br/>
     * </a>
     */
    public static SimpleExpression jsonbExtractPath(Expression fromJson, BiFunction<MappingType, List<String>, Expression> funcRef,
                                                    Consumer<Consumer<String>> consumer) {
        return FunctionUtils.oneAndMulti("JSONB_EXTRACT_PATH", fromJson,
                funcRef.apply(NoCastTextType.INSTANCE, CriteriaUtils.stringList(null, true, consumer)),
                JsonbType.TEXT
        );
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link TextType#INSTANCE}.
     * *
     *
     * @param firstPath should be {@link TextType} or {@link StringType} type.
     * @param rest      each element should be {@link TextType} or {@link StringType} type.
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">json_extract_path_text ( from_json json, VARIADIC path_elems text[] ) → text<br/>
     * Extracts JSON sub-object at the specified path as text. (This is functionally equivalent to the #>> operator.)<br/>
     * </a>
     */
    public static SimpleExpression jsonExtractPathText(Expression fromJson, Expression firstPath, Expression... rest) {
        final String name = "JSON_EXTRACT_PATH_TEXT";
        return FunctionUtils.oneAndAtLeastFunc(name, TextType.INSTANCE, fromJson, firstPath, rest);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link TextType#INSTANCE}.
     * *
     *
     * @param funcRefForJson the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                       <ul>
     *                           <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                           <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                           <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                           <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                           <li>developer custom method</li>
     *                       </ul>.
     *                       The first argument of funcRefForJson always is {@link JsonType#TEXT}.
     * @param json           non-null,it will be passed to funcRefForJson as the second argument of funcRefForJson
     * @param funcRefForPath the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                       <ul>
     *                           <li>{@link SQLs#rowParam(TypeInfer, Collection)}</li>
     *                           <li>{@link SQLs#rowLiteral(TypeInfer, Collection)}</li>
     *                           <li>developer custom method</li>
     *                       </ul>.
     *                       The first argument of funcRefForPath always is {@link TextType#INSTANCE}.
     * @param firstPath      firstPath and rest will be collected to unmodified {@link List} and passed to funcRef as the second argument of funcRef
     * @param rest           rest and firstPath will be collected to unmodified {@link List} and passed to funcRef as the second argument of funcRef
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">json_extract_path_text ( from_json json, VARIADIC path_elems text[] ) → text<br/>
     * Extracts JSON sub-object at the specified path as text. (This is functionally equivalent to the #>> operator.)<br/>
     * </a>
     */
    public static <T> Expression jsonExtractPathText(BiFunction<MappingType, T, Expression> funcRefForJson, T json,
                                                           BiFunction<MappingType, List<String>, Expression> funcRefForPath,
                                                           String firstPath, String... rest) {
        return jsonExtractPathText(funcRefForJson.apply(JsonType.TEXT, json), funcRefForPath, firstPath, rest);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link TextType#INSTANCE}.
     * *
     *
     * @param funcRef   the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                  <ul>
     *                      <li>{@link SQLs#rowParam(TypeInfer, Collection)}</li>
     *                      <li>{@link SQLs#rowLiteral(TypeInfer, Collection)}</li>
     *                      <li>developer custom method</li>
     *                  </ul>.
     *                  The first argument of funcRef always is {@link NoCastTextType#INSTANCE}.
     * @param firstPath firstPath and rest will be collected to unmodified {@link List} and passed to funcRef as the second argument of funcRef
     * @param rest      rest and firstPath will be collected to unmodified {@link List} and passed to funcRef as the second argument of funcRef
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">json_extract_path_text ( from_json json, VARIADIC path_elems text[] ) → text<br/>
     * Extracts JSON sub-object at the specified path as text. (This is functionally equivalent to the #>> operator.)<br/>
     * </a>
     */
    public static SimpleExpression jsonExtractPathText(Expression fromJson, BiFunction<MappingType, List<String>, Expression> funcRef,
                                                       String firstPath, String... rest) {
        final String name = "JSON_EXTRACT_PATH_TEXT";
        final List<String> pathElemList;
        pathElemList = ArrayUtils.unmodifiableListOf(firstPath, rest);
        return FunctionUtils.oneAndMulti(name, fromJson, funcRef.apply(NoCastTextType.INSTANCE, pathElemList), TextType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link TextType#INSTANCE}.
     * *
     *
     * @param consumer should be {@link TextType} or {@link StringType} type.
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">json_extract_path_text ( from_json json, VARIADIC path_elems text[] ) → text<br/>
     * Extracts JSON sub-object at the specified path as text. (This is functionally equivalent to the #>> operator.)<br/>
     * </a>
     */
    public static SimpleExpression jsonExtractPathText(Expression fromJson, Consumer<Consumer<Expression>> consumer) {
        return FunctionUtils.oneAndConsumer("JSON_EXTRACT_PATH_TEXT", true, fromJson, consumer, TextType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link TextType#INSTANCE}.
     * *
     *
     * @param funcRef the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                <ul>
     *                    <li>{@link SQLs#rowParam(TypeInfer, Collection)}</li>
     *                    <li>{@link SQLs#rowLiteral(TypeInfer, Collection)}</li>
     *                    <li>developer custom method</li>
     *                </ul>.
     *                The first argument of funcRefForPath always is {@link NoCastTextType#INSTANCE}.
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">json_extract_path_text ( from_json json, VARIADIC path_elems text[] ) → text<br/>
     * Extracts JSON sub-object at the specified path as text. (This is functionally equivalent to the #>> operator.)<br/>
     * </a>
     */
    public static SimpleExpression jsonExtractPathText(Expression fromJson, BiFunction<MappingType, List<String>, Expression> funcRef,
                                                       Consumer<Consumer<String>> consumer) {
        return FunctionUtils.oneAndMulti("JSON_EXTRACT_PATH_TEXT", fromJson,
                funcRef.apply(NoCastTextType.INSTANCE, CriteriaUtils.stringList(null, true, consumer)),
                TextType.INSTANCE
        );
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link TextType#INSTANCE}.
     * *
     *
     * @param firstPath should be {@link TextType} or {@link StringType} type.
     * @param rest      each element should be {@link TextType} or {@link StringType} type.
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">jsonb_extract_path_text ( from_json jsonb, VARIADIC path_elems text[] ) → text<br/>
     * Extracts JSON sub-object at the specified path as text. (This is functionally equivalent to the #>> operator.)<br/>
     * </a>
     */
    public static SimpleExpression jsonbExtractPathText(Expression fromJson, Expression firstPath, Expression... rest) {
        final String name = "JSONB_EXTRACT_PATH_TEXT";
        return FunctionUtils.oneAndAtLeastFunc(name, TextType.INSTANCE, fromJson, firstPath, rest);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link TextType#INSTANCE}.
     * *
     *
     * @param funcRefForJson the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                       <ul>
     *                           <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                           <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                           <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                           <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                           <li>developer custom method</li>
     *                       </ul>.
     *                       The first argument of funcRefForJson always is {@link JsonbType#TEXT}.
     * @param json           non-null,it will be passed to funcRefForJson as the second argument of funcRefForJson
     * @param funcRefForPath the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                       <ul>
     *                           <li>{@link SQLs#rowParam(TypeInfer, Collection)}</li>
     *                           <li>{@link SQLs#rowLiteral(TypeInfer, Collection)}</li>
     *                           <li>developer custom method</li>
     *                       </ul>.
     *                       The first argument of funcRefForPath always is {@link TextType#INSTANCE}.
     * @param firstPath      firstPath and rest will be collected to unmodified {@link List} and passed to funcRef as the second argument of funcRef
     * @param rest           rest and firstPath will be collected to unmodified {@link List} and passed to funcRef as the second argument of funcRef
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">jsonb_extract_path_text ( from_json jsonb, VARIADIC path_elems text[] ) → text<br/>
     * Extracts JSON sub-object at the specified path as text. (This is functionally equivalent to the #>> operator.)<br/>
     * </a>
     */
    public static <T> Expression jsonbExtractPathText(BiFunction<MappingType, T, Expression> funcRefForJson, T json,
                                                            BiFunction<MappingType, List<String>, Expression> funcRefForPath,
                                                            String firstPath, String... rest) {
        return jsonbExtractPathText(funcRefForJson.apply(JsonbType.TEXT, json), funcRefForPath, firstPath, rest);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link TextType#INSTANCE}.
     * *
     *
     * @param funcRef   the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                  <ul>
     *                      <li>{@link SQLs#rowParam(TypeInfer, Collection)}</li>
     *                      <li>{@link SQLs#rowLiteral(TypeInfer, Collection)}</li>
     *                      <li>developer custom method</li>
     *                  </ul>.
     *                  The first argument of funcRef always is {@link NoCastTextType#INSTANCE}.
     * @param firstPath firstPath and rest will be collected to unmodified {@link List} and passed to funcRef as the second argument of funcRef
     * @param rest      rest and firstPath will be collected to unmodified {@link List} and passed to funcRef as the second argument of funcRef
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">jsonb_extract_path_text ( from_json jsonb, VARIADIC path_elems text[] ) → text<br/>
     * Extracts JSON sub-object at the specified path as text. (This is functionally equivalent to the #>> operator.)<br/>
     * </a>
     */
    public static SimpleExpression jsonbExtractPathText(Expression fromJson, BiFunction<MappingType, List<String>, Expression> funcRef,
                                                        String firstPath, String... rest) {
        final String name = "JSONB_EXTRACT_PATH_TEXT";
        final List<String> pathElemList;
        pathElemList = ArrayUtils.unmodifiableListOf(firstPath, rest);
        return FunctionUtils.oneAndMulti(name, fromJson, funcRef.apply(NoCastTextType.INSTANCE, pathElemList), TextType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link TextType#INSTANCE}.
     * *
     *
     * @param consumer should be {@link TextType} or {@link StringType} type.
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">jsonb_extract_path_text ( from_json jsonb, VARIADIC path_elems text[] ) → text<br/>
     * Extracts JSON sub-object at the specified path as text. (This is functionally equivalent to the #>> operator.)<br/>
     * </a>
     */
    public static SimpleExpression jsonbExtractPathText(Expression fromJson, Consumer<Consumer<Expression>> consumer) {
        return FunctionUtils.oneAndConsumer("JSONB_EXTRACT_PATH_TEXT", true, fromJson, consumer, TextType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type: {@link TextType#INSTANCE}.
     *
     * @param funcRef the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                <ul>
     *                    <li>{@link SQLs#rowParam(TypeInfer, Collection)}</li>
     *                    <li>{@link SQLs#rowLiteral(TypeInfer, Collection)}</li>
     *                    <li>developer custom method</li>
     *                </ul>.
     *                The first argument of funcRefForPath always is {@link NoCastTextType#INSTANCE}.
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">jsonb_extract_path_text ( from_json jsonb, VARIADIC path_elems text[] ) → text<br/>
     * Extracts JSON sub-object at the specified path as text. (This is functionally equivalent to the #>> operator.)<br/>
     * </a>
     */
    public static SimpleExpression jsonbExtractPathText(Expression fromJson, BiFunction<MappingType, List<String>, Expression> funcRef,
                                                        Consumer<Consumer<String>> consumer) {
        return FunctionUtils.oneAndMulti("JSONB_EXTRACT_PATH_TEXT", fromJson,
                funcRef.apply(NoCastTextType.INSTANCE, CriteriaUtils.stringList(null, true, consumer)),
                TextType.INSTANCE
        );
    }


    /*-------------------below json_object_keys-------------------*/

    /**
     * <p>
     * The {@link MappingType} of fields of derived table :<ul>
     * <li>function alias(is specified by AS clause) : {@link TextType}</li>
     * <li>ordinality (optioinal) : {@link IntegerType}. see {@link _WithOrdinalityClause#withOrdinality()}</li>
     * </ul>
     * * <pre><br/>
     *   select keys.keys from json_object_keys('{"f1":"abc","f2":{"f3":"a", "f4":"b"}}') as keys  →
     *
     *    keys
     *   ------------------
     *    f1
     *    f2
     * </pre>
     *
     * @param funcRef the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                <ul>
     *                    <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                    <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                    <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                    <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                    <li>developer custom method</li>
     *                </ul>.
     *                The first argument of funcRef always is {@link JsonType#TEXT}.
     * @param value   non-null,it will be passed to funcRef as the second argument of funcRef
     * @see #jsonObjectKeys(Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">json_object_keys ( json ) → setof text<br/>
     * Returns the set of keys in the top-level JSON object.<br/>
     * </a>
     */
    public static <T> _ColumnWithOrdinalityFunction jsonObjectKeys(BiFunction<MappingType, T, Expression> funcRef, T value) {
        return jsonObjectKeys(funcRef.apply(JsonType.TEXT, value));
    }


    /**
     * <p>
     * The {@link MappingType} of fields of derived table :<ul>
     * <li>function alias(is specified by AS clause) : {@link TextType}</li>
     * <li>ordinality (optioinal) : {@link IntegerType}. see {@link _WithOrdinalityClause#withOrdinality()}</li>
     * </ul>
     * * <pre><br/>
     *   select keys.keys from json_object_keys('{"f1":"abc","f2":{"f3":"a", "f4":"b"}}') as keys  →
     *
     *    keys
     *   ------------------
     *    f1
     *    f2
     * </pre>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">json_object_keys ( json ) → setof text<br/>
     * Returns the set of keys in the top-level JSON object.<br/>
     * </a>
     */
    public static _ColumnWithOrdinalityFunction jsonObjectKeys(Expression json) {
        return DialectFunctionUtils.oneArgColumnFunction("JSON_OBJECT_KEYS", json, null, TextType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of fields of derived table :<ul>
     * <li>function alias(is specified by AS clause) : {@link TextType}</li>
     * <li>ordinality (optioinal) : {@link IntegerType}. see {@link _WithOrdinalityClause#withOrdinality()}</li>
     * </ul>
     * * <pre><br/>
     *   select keys.keys from json_object_keys('{"f1":"abc","f2":{"f3":"a", "f4":"b"}}') as keys  →
     *
     *    keys
     *   ------------------
     *    f1
     *    f2
     * </pre>
     *
     * @param funcRef the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                <ul>
     *                    <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                    <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                    <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                    <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                    <li>developer custom method</li>
     *                </ul>.
     *                The first argument of funcRef always is {@link JsonbType#TEXT}.
     * @param value   non-null,it will be passed to funcRef as the second argument of funcRef
     * @see #jsonbObjectKeys(Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">jsonb_object_keys ( json ) → setof text<br/>
     * Returns the set of keys in the top-level JSON object.<br/>
     * </a>
     */
    public static <T> _ColumnWithOrdinalityFunction jsonbObjectKeys(BiFunction<MappingType, T, Expression> funcRef, T value) {
        return jsonbObjectKeys(funcRef.apply(JsonbType.TEXT, value));
    }

    /**
     * <p>
     * The {@link MappingType} of fields of derived table :<ul>
     * <li>function alias(is specified by AS clause) : {@link TextType}</li>
     * <li>ordinality (optioinal) : {@link IntegerType}. see {@link _WithOrdinalityClause#withOrdinality()}</li>
     * </ul>
     * * <pre><br/>
     *   select keys.keys from json_object_keys('{"f1":"abc","f2":{"f3":"a", "f4":"b"}}') as keys  →
     *
     *    keys
     *   ------------------
     *    f1
     *    f2
     * </pre>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">jsonb_object_keys ( json ) → setof text<br/>
     * Returns the set of keys in the top-level JSON object.<br/>
     * </a>
     */
    public static _ColumnWithOrdinalityFunction jsonbObjectKeys(Expression json) {
        return DialectFunctionUtils.oneArgColumnFunction("JSONB_OBJECT_KEYS", json, null, TextType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of fields of derived table :<ul>
     * <li>fields follow {@link MappingType.SqlCompositeType#fieldList()}</li>
     * <li>ordinality (optioinal) : {@link IntegerType}. see {@link _WithOrdinalityClause#withOrdinality()}</li>
     * </ul>
     *
     * @param base composite type expression.
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">json_populate_record ( base anyelement, from_json json ) → anyelement<br/>
     * <br/>
     * </a>
     */
    public static _TabularWithOrdinalityFunction jsonPopulateRecord(final Expression base, final Expression json) {
        return _jsonbPopulateRecordFunc("JSON_POPULATE_RECORD", base, json);
    }

    /**
     * <p>
     * The {@link MappingType} of fields of derived table :<ul>
     * <li>fields follow {@link MappingType.SqlCompositeType#fieldList()}</li>
     * <li>ordinality (optioinal) : {@link IntegerType}. see {@link _WithOrdinalityClause#withOrdinality()}</li>
     * </ul>
     * *
     *
     * @param base composite type expression.
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">jsonb_populate_record ( base anyelement, from_json json ) → anyelement<br/>
     * <br/>
     * </a>
     */
    public static _TabularWithOrdinalityFunction jsonbPopulateRecord(final Expression base, final Expression json) {
        return _jsonbPopulateRecordFunc("JSONB_POPULATE_RECORD", base, json);
    }

    /**
     * <p>
     * The {@link MappingType} of fields of derived table :<ul>
     * <li>fields follow {@link MappingType.SqlCompositeType#fieldList()}</li>
     * <li>ordinality (optioinal) : {@link IntegerType}. see {@link _WithOrdinalityClause#withOrdinality()}</li>
     * </ul>
     *
     * @param base composite type expression.
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">json_populate_recordset ( base anyelement, from_json json ) → anyelement<br/>
     * <br/>
     * </a>
     */
    public static _TabularWithOrdinalityFunction jsonPopulateRecordSet(final Expression base, final Expression json) {
        return _jsonbPopulateRecordFunc("JSON_POPULATE_RECORDSET", base, json);
    }

    /**
     * <p>
     * The {@link MappingType} of fields of derived table :<ul>
     * <li>fields follow {@link MappingType.SqlCompositeType#fieldList()}</li>
     * <li>ordinality (optioinal) : {@link IntegerType}. see {@link _WithOrdinalityClause#withOrdinality()}</li>
     * </ul>
     *
     * @param base composite type expression.
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">jsonb_populate_recordset ( base anyelement, from_json json ) → anyelement<br/>
     * <br/>
     * </a>
     */
    public static _TabularWithOrdinalityFunction jsonbPopulateRecordSet(final Expression base, final Expression json) {
        return _jsonbPopulateRecordFunc("JSONB_POPULATE_RECORDSET", base, json);
    }


    /**
     * <pre><br/>
     * Expands the top-level JSON object to a row having the composite type defined by an AS clause.
     * (As with all functions returning record, the calling query must explicitly define the structure of the record
     * with an AS clause.) The output record is filled from fields of the JSON object, in the same way as described
     * above for json[b]_populate_record. Since there is no input record value, unmatched columns are always filled
     * with nulls.
     *
     * create type myrowtype as (a int, b text);
     *
     * select * from json_to_record('{"a":1,"b":[1,2,3],"c":[1,2,3],"e":"bar","r": {"a": 123, "b": "a b c"}}') as x(a int, b text, c int[], d text, r myrowtype) →
     *
     *  a |    b    |    c    | d |       r
     * ---+---------+---------+---+---------------
     *  1 | [1,2,3] | {1,2,3} |   | (123,"a b c")
     * </pre>
     *
     * @param funcRef the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                <ul>
     *                    <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                    <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                    <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                    <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                    <li>developer custom method</li>
     *                </ul>.
     *                The first argument of funcRef always is {@link JsonType#TEXT}.
     * @param value   non-null,it will be passed to funcRef as the second argument of funcRef
     * @throws CriteriaException throw when Operand isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     * @see #jsonToRecord(Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">json_to_record ( json ) → record<br/>
     *
     * </a>
     */
    public static <T> UndoneFunction jsonToRecord(BiFunction<MappingType, T, Expression> funcRef, final T value) {
        return jsonToRecord(funcRef.apply(JsonType.TEXT, value));
    }

    /**
     * <pre><br/>
     * Expands the top-level JSON object to a row having the composite type defined by an AS clause.
     * (As with all functions returning record, the calling query must explicitly define the structure of the record
     * with an AS clause.) The output record is filled from fields of the JSON object, in the same way as described
     * above for json[b]_populate_record. Since there is no input record value, unmatched columns are always filled
     * with nulls.
     *
     * create type myrowtype as (a int, b text);
     *
     * select * from json_to_record('{"a":1,"b":[1,2,3],"c":[1,2,3],"e":"bar","r": {"a": 123, "b": "a b c"}}') as x(a int, b text, c int[], d text, r myrowtype) →
     *
     *  a |    b    |    c    | d |       r
     * ---+---------+---------+---+---------------
     *  1 | [1,2,3] | {1,2,3} |   | (123,"a b c")
     * </pre>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">json_to_record ( json ) → record<br/>
     *
     * </a>
     */
    public static UndoneFunction jsonToRecord(final Expression json) {
        return DialectFunctionUtils.oneArgUndoneFunc("JSON_TO_RECORD", json);
    }

    /**
     * <pre><br/>
     * Expands the top-level JSON object to a row having the composite type defined by an AS clause.
     * (As with all functions returning record, the calling query must explicitly define the structure of the record
     * with an AS clause.) The output record is filled from fields of the JSON object, in the same way as described
     * above for json[b]_populate_record. Since there is no input record value, unmatched columns are always filled
     * with nulls.
     *
     * create type myrowtype as (a int, b text);
     *
     * select * from json_to_record('{"a":1,"b":[1,2,3],"c":[1,2,3],"e":"bar","r": {"a": 123, "b": "a b c"}}') as x(a int, b text, c int[], d text, r myrowtype) →
     *
     *  a |    b    |    c    | d |       r
     * ---+---------+---------+---+---------------
     *  1 | [1,2,3] | {1,2,3} |   | (123,"a b c")
     * </pre>
     *
     * @param funcRef the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                <ul>
     *                    <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                    <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                    <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                    <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                    <li>developer custom method</li>
     *                </ul>.
     *                The first argument of funcRef always is {@link JsonbType#TEXT}.
     * @param value   non-null,it will be passed to funcRef as the second argument of funcRef
     * @throws CriteriaException throw when Operand isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     * @see #jsonbToRecord(Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">jsonb_to_record ( jsonb ) → record<br/>
     *
     * </a>
     */
    public static <T> UndoneFunction jsonbToRecord(BiFunction<MappingType, T, Expression> funcRef, final T value) {
        return jsonbToRecord(funcRef.apply(JsonbType.TEXT, value));
    }


    /**
     * <pre><br/>
     * Expands the top-level JSON object to a row having the composite type defined by an AS clause.
     * (As with all functions returning record, the calling query must explicitly define the structure of the record
     * with an AS clause.) The output record is filled from fields of the JSON object, in the same way as described
     * above for json[b]_populate_record. Since there is no input record value, unmatched columns are always filled
     * with nulls.
     *
     * create type myrowtype as (a int, b text);
     *
     * select * from json_to_record('{"a":1,"b":[1,2,3],"c":[1,2,3],"e":"bar","r": {"a": 123, "b": "a b c"}}') as x(a int, b text, c int[], d text, r myrowtype) →
     *
     *  a |    b    |    c    | d |       r
     * ---+---------+---------+---+---------------
     *  1 | [1,2,3] | {1,2,3} |   | (123,"a b c")
     * </pre>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">jsonb_to_record ( jsonb ) → record<br/>
     *
     * </a>
     */
    public static UndoneFunction jsonbToRecord(final Expression json) {
        return DialectFunctionUtils.oneArgUndoneFunc("JSONB_TO_RECORD", json);
    }


    /**
     * <pre><br/>
     * Expands the top-level JSON array of objects to a set of rows having the composite type defined by an AS clause.
     * (As with all functions returning record, the calling query must explicitly define the structure of the record
     * with an AS clause.) Each element of the JSON array is processed as described above for json[b]_populate_record.
     *
     * select * from json_to_recordset('[{"a":1,"b":"foo"}, {"a":"2","c":"bar"}]') as x(a int, b text) →
     *
     *  a |  b
     * ---+-----
     *  1 | foo
     *  2 |
     * </pre>
     *
     * @param funcRef the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                <ul>
     *                    <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                    <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                    <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                    <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                    <li>developer custom method</li>
     *                </ul>.
     *                The first argument of funcRef always is {@link JsonType#TEXT}.
     * @param value   non-null,it will be passed to funcRef as the second argument of funcRef
     * @throws CriteriaException throw when Operand isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     * @see #jsonToRecordSet(Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">json_to_recordset ( json ) → setof record<br/>
     *
     * </a>
     */
    public static <T> UndoneFunction jsonToRecordSet(BiFunction<MappingType, T, Expression> funcRef, final T value) {
        return jsonToRecordSet(funcRef.apply(JsonType.TEXT, value));
    }

    /**
     * <pre><br/>
     * Expands the top-level JSON array of objects to a set of rows having the composite type defined by an AS clause.
     * (As with all functions returning record, the calling query must explicitly define the structure of the record
     * with an AS clause.) Each element of the JSON array is processed as described above for json[b]_populate_record.
     *
     * select * from json_to_recordset('[{"a":1,"b":"foo"}, {"a":"2","c":"bar"}]') as x(a int, b text) →
     *
     *  a |  b
     * ---+-----
     *  1 | foo
     *  2 |
     * </pre>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">json_to_recordset ( json ) → setof record<br/>
     *
     * </a>
     */
    public static UndoneFunction jsonToRecordSet(final Expression json) {
        return DialectFunctionUtils.oneArgUndoneFunc("JSON_TO_RECORDSET", json);
    }

    /**
     * <pre><br/>
     * Expands the top-level JSON array of objects to a set of rows having the composite type defined by an AS clause.
     * (As with all functions returning record, the calling query must explicitly define the structure of the record
     * with an AS clause.) Each element of the JSON array is processed as described above for json[b]_populate_record.
     *
     * select * from json_to_recordset('[{"a":1,"b":"foo"}, {"a":"2","c":"bar"}]') as x(a int, b text) →
     *
     *  a |  b
     * ---+-----
     *  1 | foo
     *  2 |
     * </pre>
     *
     * @param funcRef the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                <ul>
     *                    <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                    <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                    <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                    <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                    <li>developer custom method</li>
     *                </ul>.
     *                The first argument of funcRef always is {@link JsonbType#TEXT}.
     * @param value   non-null,it will be passed to funcRef as the second argument of funcRef
     * @throws CriteriaException throw when Operand isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     * @see #jsonbToRecordSet(Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">jsonb_to_recordset ( jsonb ) → setof record<br/>
     *
     * </a>
     */
    public static <T> UndoneFunction jsonbToRecordSet(BiFunction<MappingType, T, Expression> funcRef, final T value) {
        return jsonbToRecordSet(funcRef.apply(JsonbType.TEXT, value));
    }


    /**
     * <pre><br/>
     * Expands the top-level JSON array of objects to a set of rows having the composite type defined by an AS clause.
     * (As with all functions returning record, the calling query must explicitly define the structure of the record
     * with an AS clause.) Each element of the JSON array is processed as described above for json[b]_populate_record.
     *
     * select * from json_to_recordset('[{"a":1,"b":"foo"}, {"a":"2","c":"bar"}]') as x(a int, b text) →
     *
     *  a |  b
     * ---+-----
     *  1 | foo
     *  2 |
     * </pre>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">jsonb_to_recordset ( jsonb ) → setof record<br/>
     *
     * </a>
     */
    public static UndoneFunction jsonbToRecordSet(final Expression json) {
        return DialectFunctionUtils.oneArgUndoneFunc("JSONB_TO_RECORDSET", json);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonbType#TEXT}.
     * *
     *
     * @param funcRefForPath  the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                        <ul>
     *                            <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                            <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                            <li>developer custom method</li>
     *                        </ul>.
     *                        The first argument of funcRefForPath always is {@link TextArrayType#LINEAR}.
     * @param paths           non-null nad non-empty,it will be passed to funcRefForPath as the second argument of funcRefForPath
     * @param funcRefForValue the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                        <ul>
     *                            <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                            <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                            <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                            <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                            <li>developer custom method</li>
     *                        </ul>.
     *                        The first argument of funcRefForValue always is {@link JsonbType#TEXT}.
     * @param newValue        non-null,it will be passed to funcRefForValue as the second argument of funcRefForValue
     * @param createIfMissing in most case {@link SQLs#TRUE} or {@link  SQLs#FALSE}
     * @throws CriteriaException throw when Operand isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     *                           {@link SQLs#rowParam(TypeInfer, Collection)}
     * @see #jsonbSet(Expression, Expression, Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">jsonb_set ( target jsonb, path text[], new_value jsonb [, create_if_missing boolean ] ) → jsonb<br/>
     *
     * </a>
     */
    public static <T, U> Expression jsonbSet(Expression jsonb, BiFunction<MappingType, T, Expression> funcRefForPath,
                                                   T paths, BiFunction<MappingType, U, Expression> funcRefForValue,
                                                   U newValue, Expression createIfMissing) {
        return jsonbSet(jsonb, funcRefForPath.apply(TextArrayType.LINEAR, paths),
                funcRefForValue.apply(JsonbType.TEXT, newValue),
                createIfMissing
        );
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonbType#TEXT}.
     * *
     *
     * @param createIfMissing in most case {@link SQLs#TRUE} or {@link  SQLs#FALSE}
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">jsonb_set ( target jsonb, path text[], new_value jsonb [, create_if_missing boolean ] ) → jsonb<br/>
     *
     * </a>
     */
    public static SimpleExpression jsonbSet(Expression jsonb, Expression path, Expression newValue,
                                            Expression createIfMissing) {
        return FunctionUtils.fourArgFunc("JSONB_SET", jsonb, path, newValue, createIfMissing, JsonbType.TEXT);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonbType#TEXT}.
     * *
     *
     * @param funcRefForPath  the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                        <ul>
     *                            <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                            <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                            <li>developer custom method</li>
     *                        </ul>.
     *                        The first argument of funcRefForPath always is {@link TextArrayType#LINEAR}.
     * @param paths           non-null nad non-empty,it will be passed to funcRefForPath as the second argument of funcRefForPath
     * @param funcRefForValue the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                        <ul>
     *                            <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                            <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                            <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                            <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                            <li>developer custom method</li>
     *                        </ul>.
     *                        The first argument of funcRefForValue always is {@link JsonbType#TEXT}.
     * @param newValue        non-null,it will be passed to funcRefForValue as the second argument of funcRefForValue
     * @throws CriteriaException throw when Operand isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     *                           {@link SQLs#rowParam(TypeInfer, Collection)}
     * @see #jsonbSet(Expression, Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">jsonb_set ( target jsonb, path text[], new_value jsonb [, create_if_missing boolean ] ) → jsonb<br/>
     *
     * </a>
     */
    public static <T, U> Expression jsonbSet(Expression jsonb, BiFunction<MappingType, T, Expression> funcRefForPath,
                                                   T paths, BiFunction<MappingType, U, Expression> funcRefForValue,
                                                   U newValue) {
        return jsonbSet(jsonb, funcRefForPath.apply(TextArrayType.LINEAR, paths),
                funcRefForValue.apply(JsonbType.TEXT, newValue)
        );
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonbType#TEXT}.
     * *
     *
     * @see #jsonbSet(Expression, Expression, Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">jsonb_set ( target jsonb, path text[], new_value jsonb [, create_if_missing boolean ] ) → jsonb<br/>
     *
     * </a>
     */
    public static SimpleExpression jsonbSet(Expression jsonb, Expression path, Expression newValue) {
        return FunctionUtils.threeArgFunc("JSONB_SET", jsonb, path, newValue, JsonbType.TEXT);
    }

    /*-------------------below jsonb_set_lax -------------------*/

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonbType#TEXT}.
     * *
     *
     * @param funcRefForPath  the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                        <ul>
     *                            <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                            <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                            <li>developer custom method</li>
     *                        </ul>.
     *                        The first argument of funcRefForPath always is {@link TextArrayType#LINEAR}.
     * @param paths           non-null nad non-empty,it will be passed to funcRefForPath as the second argument of funcRefForPath
     * @param funcRefForValue the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                        <ul>
     *                            <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                            <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                            <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                            <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                            <li>developer custom method</li>
     *                        </ul>.
     *                        The first argument of funcRefForValue always is {@link JsonbType#TEXT}.
     * @param newValue        non-null,it will be passed to funcRefForValue as the second argument of funcRefForValue
     * @param createIfMissing in most case {@link SQLs#TRUE} or {@link  SQLs#FALSE}
     * @throws CriteriaException throw when Operand isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     *                           {@link SQLs#rowParam(TypeInfer, Collection)}
     * @see #jsonbSetLax(Expression, Expression, Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">jsonb_set_lax ( target jsonb, path text[], new_value jsonb [, create_if_missing boolean [, null_value_treatment text ]] ) → jsonb<br/>
     *
     * </a>
     */
    public static <T, U> Expression jsonbSetLax(Expression jsonb, BiFunction<MappingType, T, Expression> funcRefForPath,
                                                      T paths, BiFunction<MappingType, U, Expression> funcRefForValue,
                                                      U newValue, Expression createIfMissing) {
        return jsonbSetLax(jsonb, funcRefForPath.apply(TextArrayType.LINEAR, paths),
                funcRefForValue.apply(JsonbType.TEXT, newValue),
                createIfMissing
        );
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonbType#TEXT}.
     * *
     *
     * @param createIfMissing in most case {@link SQLs#TRUE} or {@link  SQLs#FALSE}
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">jsonb_set_lax ( target jsonb, path text[], new_value jsonb [, create_if_missing boolean [, null_value_treatment text ]] ) → jsonb<br/>
     *
     * </a>
     */
    public static SimpleExpression jsonbSetLax(Expression jsonb, Expression path, Expression newValue,
                                               Expression createIfMissing) {
        return FunctionUtils.fourArgFunc("JSONB_SET_LAX", jsonb, path, newValue, createIfMissing, JsonbType.TEXT);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonbType#TEXT}.
     * *
     *
     * @param funcRefForPath  the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                        <ul>
     *                            <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                            <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                            <li>developer custom method</li>
     *                        </ul>.
     *                        The first argument of funcRefForPath always is {@link TextArrayType#LINEAR}.
     * @param paths           non-null nad non-empty,it will be passed to funcRefForPath as the second argument of funcRefForPath
     * @param funcRefForValue the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                        <ul>
     *                            <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                            <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                            <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                            <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                            <li>developer custom method</li>
     *                        </ul>.
     *                        The first argument of funcRefForValue always is {@link JsonbType#TEXT}.
     * @param newValue        non-null,it will be passed to funcRefForValue as the second argument of funcRefForValue
     * @throws CriteriaException throw when Operand isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     *                           {@link SQLs#rowParam(TypeInfer, Collection)}
     * @see #jsonbSetLax(Expression, Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">jsonb_set_lax ( target jsonb, path text[], new_value jsonb [, create_if_missing boolean [, null_value_treatment text ]] ) → jsonb<br/>
     *
     * </a>
     */
    public static <T, U> Expression jsonbSetLax(Expression jsonb, BiFunction<MappingType, T, Expression> funcRefForPath,
                                                      T paths, BiFunction<MappingType, U, Expression> funcRefForValue,
                                                      U newValue) {
        return jsonbSetLax(jsonb, funcRefForPath.apply(TextArrayType.LINEAR, paths),
                funcRefForValue.apply(JsonbType.TEXT, newValue)
        );
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonbType#TEXT}.
     * *
     *
     * @see #jsonbSetLax(Expression, Expression, Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">jsonb_set_lax ( target jsonb, path text[], new_value jsonb [, create_if_missing boolean [, null_value_treatment text ]] ) → jsonb<br/>
     *
     * </a>
     */
    public static SimpleExpression jsonbSetLax(Expression jsonb, Expression path, Expression newValue) {
        return FunctionUtils.threeArgFunc("JSONB_SET_LAX", jsonb, path, newValue, JsonbType.TEXT);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonbType#TEXT}.
     * *
     *
     * @param funcRefForPath  the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                        <ul>
     *                            <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                            <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                            <li>developer custom method</li>
     *                        </ul>.
     *                        The first argument of funcRefForPath always is {@link TextArrayType#LINEAR}.
     * @param paths           non-null nad non-empty,it will be passed to funcRefForPath as the second argument of funcRefForPath
     * @param funcRefForValue the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                        <ul>
     *                            <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                            <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                            <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                            <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                            <li>developer custom method</li>
     *                        </ul>.
     *                        The first argument of funcRefForValue always is {@link JsonbType#TEXT}.
     * @param newValue        non-null,it will be passed to funcRefForValue as the second argument of funcRefForValue
     * @param createIfMissing in most case {@link SQLs#TRUE} or {@link  SQLs#FALSE}
     * @param nullTreatMode   must be one of  <ul>
     *                        <li>{@link Postgres#RAISE_EXCEPTION}</li>
     *                        <li>{@link Postgres#USE_JSON_NULL}</li>
     *                        <li>{@link Postgres#DELETE_KEY}</li>
     *                        <li>{@link Postgres#RETURN_TARGET}</li>
     *                        </ul>
     * @throws CriteriaException throw when argument isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     *                           {@link SQLs#rowParam(TypeInfer, Collection)}
     * @see #jsonbSetLax(Expression, Expression, Expression, Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">jsonb_set_lax ( target jsonb, path text[], new_value jsonb [, create_if_missing boolean [, null_value_treatment text ]] ) → jsonb<br/>
     *
     * </a>
     */
    public static <T, U> Expression jsonbSetLax(Expression jsonb, BiFunction<MappingType, T, Expression> funcRefForPath,
                                                      T paths, BiFunction<MappingType, U, Expression> funcRefForValue,
                                                      U newValue, Expression createIfMissing, NullTreatMode nullTreatMode) {
        return jsonbSetLax(jsonb, funcRefForPath.apply(TextArrayType.LINEAR, paths),
                funcRefForValue.apply(JsonbType.TEXT, newValue), createIfMissing,
                nullTreatMode
        );
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonbType#TEXT}.
     * *
     *
     * @param createIfMissing in most case {@link SQLs#TRUE} or {@link  SQLs#FALSE}
     * @param nullTreatMode   is one of  <ul>
     *                        <li>{@link Postgres#RAISE_EXCEPTION}</li>
     *                        <li>{@link Postgres#USE_JSON_NULL}</li>
     *                        <li>{@link Postgres#DELETE_KEY}</li>
     *                        <li>{@link Postgres#RETURN_TARGET}</li>
     *                        </ul>
     * @throws CriteriaException throw when argument isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     *                           {@link SQLs#rowParam(TypeInfer, Collection)}
     * @see #jsonbSetLax(Expression, Expression, Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">jsonb_set_lax ( target jsonb, path text[], new_value jsonb [, create_if_missing boolean [, null_value_treatment text ]] ) → jsonb<br/>
     *
     * </a>
     */
    public static SimpleExpression jsonbSetLax(Expression jsonb, Expression path, Expression newValue,
                                               Expression createIfMissing, Expression nullTreatMode) {

        return FunctionUtils.fiveArgFunc("JSONB_SET_LAX", jsonb, path, newValue,
                createIfMissing, nullTreatMode,
                JsonbType.TEXT
        );

    }


    /*-------------------below jsonb_insert-------------------*/


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonbType#TEXT}.
     * *
     *
     * @param funcRefForPath  the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                        <ul>
     *                            <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                            <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                            <li>developer custom method</li>
     *                        </ul>.
     *                        The first argument of funcRefForPath always is {@link TextArrayType#LINEAR}.
     * @param paths           non-null nad non-empty,it will be passed to funcRefForPath as the second argument of funcRefForPath
     * @param funcRefForValue the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                        <ul>
     *                            <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                            <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                            <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                            <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                            <li>developer custom method</li>
     *                        </ul>.
     *                        The first argument of funcRefForValue always is {@link JsonbType#TEXT}.
     * @param newValue        non-null,it will be passed to funcRefForValue as the second argument of funcRefForValue
     * @param insertAfter     in most case {@link SQLs#TRUE} or {@link  SQLs#FALSE}
     * @throws CriteriaException throw when Operand isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     *                           {@link SQLs#rowParam(TypeInfer, Collection)}
     * @see #jsonbInsert(Expression, Expression, Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">jsonb_insert ( target jsonb, path text[], new_value jsonb [, insert_after boolean ] ) → jsonb<br/>
     *
     * </a>
     */
    public static <T, U> Expression jsonbInsert(Expression jsonb, BiFunction<MappingType, T, Expression> funcRefForPath,
                                                      T paths, BiFunction<MappingType, U, Expression> funcRefForValue,
                                                      U newValue, Expression insertAfter) {
        return jsonbInsert(jsonb, funcRefForPath.apply(TextArrayType.LINEAR, paths),
                funcRefForValue.apply(JsonbType.TEXT, newValue),
                insertAfter
        );
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonbType#TEXT}.
     * *
     *
     * @param insertAfter in most case {@link SQLs#TRUE} or {@link  SQLs#FALSE}
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">jsonb_insert ( target jsonb, path text[], new_value jsonb [, insert_after boolean ] ) → jsonb<br/>
     *
     * </a>
     */
    public static SimpleExpression jsonbInsert(Expression jsonb, Expression path, Expression newValue,
                                               Expression insertAfter) {
        return FunctionUtils.fourArgFunc("JSONB_INSERT", jsonb, path, newValue, insertAfter, JsonbType.TEXT);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonbType#TEXT}.
     * *
     *
     * @param funcRefForPath  the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                        <ul>
     *                            <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                            <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                            <li>developer custom method</li>
     *                        </ul>.
     *                        The first argument of funcRefForPath always is {@link TextArrayType#LINEAR}.
     * @param paths           non-null nad non-empty,it will be passed to funcRefForPath as the second argument of funcRefForPath
     * @param funcRefForValue the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                        <ul>
     *                            <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                            <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                            <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                            <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                            <li>developer custom method</li>
     *                        </ul>.
     *                        The first argument of funcRefForValue always is {@link JsonbType#TEXT}.
     * @param newValue        non-null,it will be passed to funcRefForValue as the second argument of funcRefForValue
     * @throws CriteriaException throw when Operand isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     *                           {@link SQLs#rowParam(TypeInfer, Collection)}
     * @see #jsonbInsert(Expression, Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">jsonb_insert ( target jsonb, path text[], new_value jsonb [, insert_after boolean ] ) → jsonb<br/>
     *
     * </a>
     */
    public static <T, U> Expression jsonbInsert(Expression jsonb, BiFunction<MappingType, T, Expression> funcRefForPath,
                                                      T paths, BiFunction<MappingType, U, Expression> funcRefForValue,
                                                      U newValue) {
        return jsonbInsert(jsonb, funcRefForPath.apply(TextArrayType.LINEAR, paths),
                funcRefForValue.apply(JsonbType.TEXT, newValue)
        );
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonbType#TEXT}.
     * *
     *
     * @throws CriteriaException throw when argument isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     *                           {@link SQLs#rowParam(TypeInfer, Collection)}
     * @see #jsonbInsert(Expression, Expression, Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">jsonb_insert ( target jsonb, path text[], new_value jsonb [, insert_after boolean ] ) → jsonb<br/>
     *
     * </a>
     */
    public static SimpleExpression jsonbInsert(Expression jsonb, Expression path, Expression newValue) {
        return FunctionUtils.threeArgFunc("JSONB_INSERT", jsonb, path, newValue, JsonbType.TEXT);
    }

    /*-------------------below json_strip_nulls-------------------*/

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonType#TEXT}.
     * *
     *
     * @throws CriteriaException throw when argument isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     *                           {@link SQLs#rowParam(TypeInfer, Collection)}
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">json_strip_nulls ( json ) → json<br/>
     *
     * </a>
     */
    public static SimpleExpression jsonStripNulls(Expression json) {
        return FunctionUtils.oneArgFunc("JSON_STRIP_NULLS", json, JsonType.TEXT);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonbType#TEXT}.
     * *
     *
     * @throws CriteriaException throw when argument isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     *                           {@link SQLs#rowParam(TypeInfer, Collection)}
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">jsonb_strip_nulls ( jsonb ) → jsonb<br/>
     *
     * </a>
     */
    public static SimpleExpression jsonbStripNulls(Expression json) {
        return FunctionUtils.oneArgFunc("JSONB_STRIP_NULLS", json, JsonbType.TEXT);
    }

    /*-------------------below jsonb_path_exists -------------------*/

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link BooleanType}.
     * *
     *
     * @param funcRefForPath the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                       <ul>
     *                           <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                           <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                           <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                           <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                           <li>developer custom method</li>
     *                       </ul>.
     *                       The first argument of funcRefForPath always is {@link JsonPathType#INSTANCE}.
     * @param path           non-null,it will be passed to funcRefForPath as the second argument of funcRefForPath
     * @throws CriteriaException throw when argument isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     *                           {@link SQLs#rowParam(TypeInfer, Collection)}
     * @see #jsonbPathExists(Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">jsonb_path_exists ( target jsonb, path jsonpath [, vars jsonb [, silent boolean ]] ) → boolean<br/>
     *
     * </a>
     */
    public static <T> SimplePredicate jsonbPathExists(Expression target, BiFunction<MappingType, T, Expression> funcRefForPath,
                                                      T path) {
        return jsonbPathExists(target, funcRefForPath.apply(JsonPathType.INSTANCE, path));
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link BooleanType}.
     * *
     *
     * @throws CriteriaException throw when argument isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     *                           {@link SQLs#rowParam(TypeInfer, Collection)}
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">jsonb_path_exists ( target jsonb, path jsonpath [, vars jsonb [, silent boolean ]] ) → boolean<br/>
     *
     * </a>
     */
    public static SimplePredicate jsonbPathExists(Expression target, Expression path) {
        return FunctionUtils.twoArgPredicateFunc("JSONB_PATH_EXISTS", target, path);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link BooleanType}.
     * *
     *
     * @param funcRefForPath the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                       <ul>
     *                           <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                           <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                           <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                           <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                           <li>developer custom method</li>
     *                       </ul>.
     *                       The first argument of funcRefForPath always is {@link JsonPathType#INSTANCE}.
     * @param path           non-null,it will be passed to funcRefForPath as the second argument of funcRefForPath
     * @throws CriteriaException throw when argument isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     *                           {@link SQLs#rowParam(TypeInfer, Collection)}
     * @see #jsonbPathExists(Expression, Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">jsonb_path_exists ( target jsonb, path jsonpath [, vars jsonb [, silent boolean ]] ) → boolean<br/>
     *
     * </a>
     */
    public static <T> SimplePredicate jsonbPathExists(Expression target, BiFunction<MappingType, T, Expression> funcRefForPath,
                                                      T path, Expression vars) {
        return jsonbPathExists(target, funcRefForPath.apply(JsonPathType.INSTANCE, path), vars);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link BooleanType}.
     * *
     *
     * @param funcRefForPath the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                       <ul>
     *                           <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                           <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                           <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                           <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                           <li>developer custom method</li>
     *                       </ul>.
     *                       The first argument of funcRefForPath always is {@link JsonPathType#INSTANCE}.
     * @param path           non-null,it will be passed to funcRefForPath as the second argument of funcRefForPath
     * @param funcRefForVars the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                       <ul>
     *                           <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                           <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                           <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                           <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                           <li>developer custom method</li>
     *                       </ul>.
     *                       The first argument of funcRefForVars always is {@link JsonbType#TEXT}.
     * @param vars           non-null,it will be passed to funcRefForVars as the second argument of funcRefForVars
     * @throws CriteriaException throw when argument isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     *                           {@link SQLs#rowParam(TypeInfer, Collection)}
     * @see #jsonbPathExists(Expression, Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">jsonb_path_exists ( target jsonb, path jsonpath [, vars jsonb [, silent boolean ]] ) → boolean<br/>
     *
     * </a>
     */
    public static <T, U> SimplePredicate jsonbPathExists(Expression target, BiFunction<MappingType, T, Expression> funcRefForPath,
                                                         T path, BiFunction<MappingType, U, Expression> funcRefForVars, U vars) {
        return jsonbPathExists(target, funcRefForPath.apply(JsonPathType.INSTANCE, path),
                funcRefForVars.apply(JsonbType.TEXT, vars)
        );
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link BooleanType}.
     * *
     *
     * @throws CriteriaException throw when argument isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     *                           {@link SQLs#rowParam(TypeInfer, Collection)}
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">jsonb_path_exists ( target jsonb, path jsonpath [, vars jsonb [, silent boolean ]] ) → boolean<br/>
     *
     * </a>
     */
    public static SimplePredicate jsonbPathExists(Expression target, Expression path, Expression vars) {
        return FunctionUtils.threeArgPredicateFunc("JSONB_PATH_EXISTS", target, path, vars);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link BooleanType}.
     * *
     *
     * @param funcRefForPath the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                       <ul>
     *                           <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                           <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                           <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                           <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                           <li>developer custom method</li>
     *                       </ul>.
     *                       The first argument of funcRefForPath always is {@link JsonPathType#INSTANCE}.
     * @param path           non-null,it will be passed to funcRefForPath as the second argument of funcRefForPath
     * @param silent         in most case {@link SQLs#TRUE} or {@link  SQLs#FALSE}
     * @throws CriteriaException throw when argument isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     *                           {@link SQLs#rowParam(TypeInfer, Collection)}
     * @see #jsonbPathExists(Expression, Expression, Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">jsonb_path_exists ( target jsonb, path jsonpath [, vars jsonb [, silent boolean ]] ) → boolean<br/>
     *
     * </a>
     */
    public static <T> SimplePredicate jsonbPathExists(Expression target, BiFunction<MappingType, T, Expression> funcRefForPath,
                                                      T path, Expression vars, Expression silent) {
        return jsonbPathExists(target, funcRefForPath.apply(JsonPathType.INSTANCE, path), vars, silent);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link BooleanType}.
     * *
     *
     * @param funcRefForPath the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                       <ul>
     *                           <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                           <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                           <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                           <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                           <li>developer custom method</li>
     *                       </ul>.
     *                       The first argument of funcRefForPath always is {@link JsonPathType#INSTANCE}.
     * @param path           non-null,it will be passed to funcRefForPath as the second argument of funcRefForPath
     * @param funcRefForVars the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                       <ul>
     *                           <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                           <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                           <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                           <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                           <li>developer custom method</li>
     *                       </ul>.
     *                       The first argument of funcRefForVars always is {@link JsonbType#TEXT}.
     * @param vars           non-null,it will be passed to funcRefForVars as the second argument of funcRefForVars
     * @param silent         in most case {@link SQLs#TRUE} or {@link  SQLs#FALSE}
     * @throws CriteriaException throw when argument isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     *                           {@link SQLs#rowParam(TypeInfer, Collection)}
     * @see #jsonbPathExists(Expression, Expression, Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">jsonb_path_exists ( target jsonb, path jsonpath [, vars jsonb [, silent boolean ]] ) → boolean<br/>
     *
     * </a>
     */
    public static <T, U> SimplePredicate jsonbPathExists(Expression target, BiFunction<MappingType, T, Expression> funcRefForPath,
                                                         T path, BiFunction<MappingType, U, Expression> funcRefForVars, U vars, Expression silent) {
        return jsonbPathExists(target, funcRefForPath.apply(JsonPathType.INSTANCE, path),
                funcRefForVars.apply(JsonbType.TEXT, vars), silent
        );
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link BooleanType}.
     * *
     *
     * @param silent in most case {@link SQLs#TRUE} or {@link  SQLs#FALSE}
     * @throws CriteriaException throw when argument isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     *                           {@link SQLs#rowParam(TypeInfer, Collection)}
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">jsonb_path_exists ( target jsonb, path jsonpath [, vars jsonb [, silent boolean ]] ) → boolean<br/>
     *
     * </a>
     */
    public static SimplePredicate jsonbPathExists(Expression target, Expression path, Expression vars, Expression silent) {
        return FunctionUtils.fourArgPredicateFunc("JSONB_PATH_EXISTS", target, path, vars, silent);
    }

    /*-------------------below jsonb_path_match-------------------*/

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link BooleanType}.
     * *
     *
     * @param funcRefForPath the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                       <ul>
     *                           <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                           <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                           <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                           <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                           <li>developer custom method</li>
     *                       </ul>.
     *                       The first argument of funcRefForPath always is {@link JsonPathType#INSTANCE}.
     * @param path           non-null,it will be passed to funcRefForPath as the second argument of funcRefForPath
     * @throws CriteriaException throw when argument isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     *                           {@link SQLs#rowParam(TypeInfer, Collection)}
     * @see #jsonbPathMatch(Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">jsonb_path_match ( target jsonb, path jsonpath [, vars jsonb [, silent boolean ]] ) → boolean<br/>
     *
     * </a>
     */
    public static SimplePredicate jsonbPathMatch(Expression target, BiFunction<MappingType, String, Expression> funcRefForPath,
                                                 String path) {
        return jsonbPathMatch(target, funcRefForPath.apply(JsonPathType.INSTANCE, path));
    }

    /*-------------------below jsonb_path_match-------------------*/

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link BooleanType}.
     * *
     *
     * @param funcRefForPath the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                       <ul>
     *                           <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                           <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                           <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                           <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                           <li>developer custom method</li>
     *                       </ul>.
     *                       The first argument of funcRefForPath always is {@link JsonPathType#INSTANCE}.
     * @param path           non-null,it will be passed to funcRefForPath as the second argument of funcRefForPath
     * @throws CriteriaException throw when argument isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     *                           {@link SQLs#rowParam(TypeInfer, Collection)}
     * @see #jsonbPathMatch(Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">jsonb_path_match ( target jsonb, path jsonpath [, vars jsonb [, silent boolean ]] ) → boolean<br/>
     *
     * </a>
     */
    public static <T> SimplePredicate jsonbPathMatch(Expression target, BiFunction<MappingType, T, Expression> funcRefForPath, T path) {
        return jsonbPathMatch(target, funcRefForPath.apply(JsonPathType.INSTANCE, path));
    }

    /**
     * <p>The {@link MappingType} of function return type: {@link BooleanType}.
     *
     * @throws CriteriaException throw when argument isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     *                           {@link SQLs#rowParam(TypeInfer, Collection)}
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">jsonb_path_match ( target jsonb, path jsonpath [, vars jsonb [, silent boolean ]] ) → boolean<br/>
     *
     * </a>
     */
    public static SimplePredicate jsonbPathMatch(Expression target, Expression path) {
        return FunctionUtils.twoArgPredicateFunc("JSONB_PATH_MATCH", target, path);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link BooleanType}.
     * *
     *
     * @param funcRefForPath the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                       <ul>
     *                           <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                           <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                           <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                           <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                           <li>developer custom method</li>
     *                       </ul>.
     *                       The first argument of funcRefForPath always is {@link JsonPathType#INSTANCE}.
     * @param path           non-null,it will be passed to funcRefForPath as the second argument of funcRefForPath
     * @throws CriteriaException throw when argument isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     *                           {@link SQLs#rowParam(TypeInfer, Collection)}
     * @see #jsonbPathMatch(Expression, Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">jsonb_path_match ( target jsonb, path jsonpath [, vars jsonb [, silent boolean ]] ) → boolean<br/>
     *
     * </a>
     */
    public static <T> SimplePredicate jsonbPathMatch(Expression target, BiFunction<MappingType, T, Expression> funcRefForPath,
                                                     T path, Expression vars) {
        return jsonbPathMatch(target, funcRefForPath.apply(JsonPathType.INSTANCE, path), vars);
    }

    /**
     * <p>The {@link MappingType} of function return type: {@link BooleanType}.
     *
     * @param funcRefForPath the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                       <ul>
     *                           <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                           <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                           <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                           <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                           <li>developer custom method</li>
     *                       </ul>.
     *                       The first argument of funcRefForPath always is {@link JsonPathType#INSTANCE}.
     * @param path           non-null,it will be passed to funcRefForPath as the second argument of funcRefForPath
     * @param silent         in most case {@link SQLs#TRUE} or {@link  SQLs#FALSE}
     * @throws CriteriaException throw when argument isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     *                           {@link SQLs#rowParam(TypeInfer, Collection)}
     * @see #jsonbPathMatch(Expression, Expression, Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">jsonb_path_match ( target jsonb, path jsonpath [, vars jsonb [, silent boolean ]] ) → boolean<br/>
     *
     * </a>
     */
    public static <T> SimplePredicate jsonbPathMatch(Expression target, BiFunction<MappingType, T, Expression> funcRefForPath,
                                                     T path, Expression vars, Expression silent) {
        return jsonbPathMatch(target, funcRefForPath.apply(JsonPathType.INSTANCE, path), vars, silent);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link BooleanType}.
     * *
     *
     * @param funcRefForPath the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                       <ul>
     *                           <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                           <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                           <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                           <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                           <li>developer custom method</li>
     *                       </ul>.
     *                       The first argument of funcRefForPath always is {@link JsonPathType#INSTANCE}.
     * @param path           non-null,it will be passed to funcRefForPath as the second argument of funcRefForPath
     * @throws CriteriaException throw when argument isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     *                           {@link SQLs#rowParam(TypeInfer, Collection)}
     * @see #jsonbPathMatch(Expression, Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">jsonb_path_match ( target jsonb, path jsonpath [, vars jsonb [, silent boolean ]] ) → boolean<br/>
     *
     * </a>
     */
    public static <T, U> SimplePredicate jsonbPathMatch(Expression target, BiFunction<MappingType, T, Expression> funcRefForPath,
                                                        T path, BiFunction<MappingType, U, Expression> funcRefForVars, U vars) {
        return jsonbPathMatch(target, funcRefForPath.apply(JsonPathType.INSTANCE, path),
                funcRefForVars.apply(JsonbType.TEXT, vars)
        );
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link BooleanType}.
     * *
     *
     * @throws CriteriaException throw when argument isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     *                           {@link SQLs#rowParam(TypeInfer, Collection)}
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">jsonb_path_match ( target jsonb, path jsonpath [, vars jsonb [, silent boolean ]] ) → boolean<br/>
     *
     * </a>
     */
    public static SimplePredicate jsonbPathMatch(Expression target, Expression path, Expression vars) {
        return FunctionUtils.threeArgPredicateFunc("JSONB_PATH_MATCH", target, path, vars);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link BooleanType}.
     * *
     *
     * @param funcRefForPath the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                       <ul>
     *                           <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                           <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                           <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                           <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                           <li>developer custom method</li>
     *                       </ul>.
     *                       The first argument of funcRefForPath always is {@link JsonPathType#INSTANCE}.
     * @param path           non-null,it will be passed to funcRefForPath as the second argument of funcRefForPath
     * @param funcRefForVars the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                       <ul>
     *                           <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                           <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                           <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                           <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                           <li>developer custom method</li>
     *                       </ul>.
     *                       The first argument of funcRefForVars always is {@link JsonbType#TEXT}.
     * @param vars           non-null,it will be passed to funcRefForVars as the second argument of funcRefForVars
     * @throws CriteriaException throw when argument isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     *                           {@link SQLs#rowParam(TypeInfer, Collection)}
     * @see #jsonbPathMatch(Expression, Expression, Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">jsonb_path_match ( target jsonb, path jsonpath [, vars jsonb [, silent boolean ]] ) → boolean<br/>
     *
     * </a>
     */
    public static <T, U> SimplePredicate jsonbPathMatch(Expression target, BiFunction<MappingType, T, Expression> funcRefForPath,
                                                        T path, BiFunction<MappingType, U, Expression> funcRefForVars, U vars,
                                                        Expression silent) {
        return jsonbPathMatch(target, funcRefForPath.apply(JsonPathType.INSTANCE, path),
                funcRefForVars.apply(JsonbType.TEXT, vars), silent
        );
    }

    /**
     * <p>The {@link MappingType} of function return type: {@link BooleanType}.
     *
     * @param silent in most case {@link SQLs#TRUE} or {@link  SQLs#FALSE}
     * @throws CriteriaException throw when argument isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     *                           {@link SQLs#rowParam(TypeInfer, Collection)}
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">jsonb_path_match ( target jsonb, path jsonpath [, vars jsonb [, silent boolean ]] ) → boolean<br/>
     *
     * </a>
     */
    public static SimplePredicate jsonbPathMatch(Expression target, Expression path, Expression vars, Expression silent) {
        return FunctionUtils.fourArgPredicateFunc("JSONB_PATH_MATCH", target, path, vars, silent);
    }


    /*-------------------below jsonb_path_query-------------------*/

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonbType#TEXT}.
     * this filed name of function is specified by AS clause.
     * * <pre><br/>
     * jsonb_path_query ( target jsonb, path jsonpath [, vars jsonb [, silent boolean ]] ) → setof jsonb
     *
     * Returns all JSON items returned by the JSON path for the specified JSON value. The optional vars
     * and silent arguments act the same as for jsonb_path_exists.
     *
     * select q.q from jsonb_path_query('{"a":[1,2,3,4,5]}', '$.a[*] ? (@ >= $min &amp;&amp; @ &lt;= $max)', '{"min":2, "max":4}') as q →
     *
     *  q
     * ------------------
     *  2
     *  3
     *  4
     * </pre>
     *
     * @param funcRefForPath the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                       <ul>
     *                           <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                           <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                           <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                           <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                           <li>developer custom method</li>
     *                       </ul>.
     *                       The first argument of funcRefForPath always is {@link JsonPathType#INSTANCE}.
     * @param path           non-null,it will be passed to funcRefForPath as the second argument of funcRefForPath
     * @throws CriteriaException throw when argument isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     *                           {@link SQLs#rowParam(TypeInfer, Collection)}
     * @see #jsonbPathQuery(Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">jsonb_path_query ( target jsonb, path jsonpath [, vars jsonb [, silent boolean ]] ) → setof jsonb<br/>
     *
     * </a>
     */
    public static <T> _ColumnWithOrdinalityFunction jsonbPathQuery(Expression target, BiFunction<MappingType, T, Expression> funcRefForPath,
                                                                   T path) {
        return jsonbPathQuery(target, funcRefForPath.apply(JsonPathType.INSTANCE, path));
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonbType#TEXT}.
     * this filed name of function is specified by AS clause.
     * * <pre><br/>
     * jsonb_path_query ( target jsonb, path jsonpath [, vars jsonb [, silent boolean ]] ) → setof jsonb
     *
     * Returns all JSON items returned by the JSON path for the specified JSON value. The optional vars
     * and silent arguments act the same as for jsonb_path_exists.
     *
     * select q.q from jsonb_path_query('{"a":[1,2,3,4,5]}', '$.a[*] ? (@ >= $min &amp;&amp; @ &lt;= $max)', '{"min":2, "max":4}') as q →
     *
     *  q
     * ------------------
     *  2
     *  3
     *  4
     * </pre>
     *
     * @throws CriteriaException throw when argument isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     *                           {@link SQLs#rowParam(TypeInfer, Collection)}
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">jsonb_path_query ( target jsonb, path jsonpath [, vars jsonb [, silent boolean ]] ) → setof jsonb<br/>
     *
     * </a>
     */
    public static _ColumnWithOrdinalityFunction jsonbPathQuery(Expression target, Expression path) {
        return DialectFunctionUtils.twoArgColumnFunction("JSONB_PATH_QUERY", target, path,
                null, JsonbType.TEXT
        );
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonbType#TEXT}.
     * this filed name of function is specified by AS clause.
     * * <pre><br/>
     * jsonb_path_query ( target jsonb, path jsonpath [, vars jsonb [, silent boolean ]] ) → setof jsonb
     *
     * Returns all JSON items returned by the JSON path for the specified JSON value. The optional vars
     * and silent arguments act the same as for jsonb_path_exists.
     *
     * select q.q from jsonb_path_query('{"a":[1,2,3,4,5]}', '$.a[*] ? (@ >= $min &amp;&amp; @ &lt;= $max)', '{"min":2, "max":4}') as q →
     *
     *  q
     * ------------------
     *  2
     *  3
     *  4
     * </pre>
     *
     * @param funcRefForPath the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                       <ul>
     *                           <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                           <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                           <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                           <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                           <li>developer custom method</li>
     *                       </ul>.
     *                       The first argument of funcRefForPath always is {@link JsonPathType#INSTANCE}.
     * @param path           non-null,it will be passed to funcRefForPath as the second argument of funcRefForPath
     * @throws CriteriaException throw when argument isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     *                           {@link SQLs#rowParam(TypeInfer, Collection)}
     * @see #jsonbPathQuery(Expression, Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">jsonb_path_query ( target jsonb, path jsonpath [, vars jsonb [, silent boolean ]] ) → setof jsonb<br/>
     *
     * </a>
     */
    public static <T> _ColumnWithOrdinalityFunction jsonbPathQuery(Expression target, BiFunction<MappingType, T, Expression> funcRefForPath,
                                                                   T path, Expression vars) {
        return jsonbPathQuery(target, funcRefForPath.apply(JsonPathType.INSTANCE, path), vars);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonbType#TEXT}.
     * this filed name of function is specified by AS clause.
     * * <pre><br/>
     * jsonb_path_query ( target jsonb, path jsonpath [, vars jsonb [, silent boolean ]] ) → setof jsonb
     *
     * Returns all JSON items returned by the JSON path for the specified JSON value. The optional vars
     * and silent arguments act the same as for jsonb_path_exists.
     *
     * select q.q from jsonb_path_query('{"a":[1,2,3,4,5]}', '$.a[*] ? (@ >= $min &amp;&amp; @ &lt;= $max)', '{"min":2, "max":4}') as q →
     *
     *  q
     * ------------------
     *  2
     *  3
     *  4
     * </pre>
     *
     * @param funcRefForPath the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                       <ul>
     *                           <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                           <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                           <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                           <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                           <li>developer custom method</li>
     *                       </ul>.
     *                       The first argument of funcRefForPath always is {@link JsonPathType#INSTANCE}.
     * @param path           non-null,it will be passed to funcRefForPath as the second argument of funcRefForPath
     * @throws CriteriaException throw when argument isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     *                           {@link SQLs#rowParam(TypeInfer, Collection)}
     * @see #jsonbPathQuery(Expression, Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">jsonb_path_query ( target jsonb, path jsonpath [, vars jsonb [, silent boolean ]] ) → setof jsonb<br/>
     *
     * </a>
     */
    public static <T, U> _ColumnWithOrdinalityFunction jsonbPathQuery(Expression target, BiFunction<MappingType, T, Expression> funcRefForPath,
                                                                      T path, BiFunction<MappingType, U, Expression> funcRefForVars, U vars) {
        return jsonbPathQuery(target, funcRefForPath.apply(JsonPathType.INSTANCE, path),
                funcRefForVars.apply(JsonbType.TEXT, vars)
        );
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonbType#TEXT}.
     * this filed name of function is specified by AS clause.
     * * <pre><br/>
     * jsonb_path_query ( target jsonb, path jsonpath [, vars jsonb [, silent boolean ]] ) → setof jsonb
     *
     * Returns all JSON items returned by the JSON path for the specified JSON value. The optional vars
     * and silent arguments act the same as for jsonb_path_exists.
     *
     * select q.q from jsonb_path_query('{"a":[1,2,3,4,5]}', '$.a[*] ? (@ >= $min &amp;&amp; @ &lt;= $max)', '{"min":2, "max":4}') as q →
     *
     *  q
     * ------------------
     *  2
     *  3
     *  4
     * </pre>
     *
     * @throws CriteriaException throw when argument isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     *                           {@link SQLs#rowParam(TypeInfer, Collection)}
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">jsonb_path_query ( target jsonb, path jsonpath [, vars jsonb [, silent boolean ]] ) → setof jsonb<br/>
     *
     * </a>
     */
    public static _ColumnWithOrdinalityFunction jsonbPathQuery(Expression target, Expression path, Expression vars) {
        return DialectFunctionUtils.threeArgColumnFunction("JSONB_PATH_QUERY", target, path, vars,
                null, JsonbType.TEXT
        );
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonbType#TEXT}.
     * this filed name of function is specified by AS clause.
     * * <pre><br/>
     * jsonb_path_query ( target jsonb, path jsonpath [, vars jsonb [, silent boolean ]] ) → setof jsonb
     *
     * Returns all JSON items returned by the JSON path for the specified JSON value. The optional vars
     * and silent arguments act the same as for jsonb_path_exists.
     *
     * select q.q from jsonb_path_query('{"a":[1,2,3,4,5]}', '$.a[*] ? (@ >= $min &amp;&amp; @ &lt;= $max)', '{"min":2, "max":4}') as q →
     *
     *  q
     * ------------------
     *  2
     *  3
     *  4
     * </pre>
     *
     * @param funcRefForPath the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                       <ul>
     *                           <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                           <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                           <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                           <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                           <li>developer custom method</li>
     *                       </ul>.
     *                       The first argument of funcRefForPath always is {@link JsonPathType#INSTANCE}.
     * @param path           non-null,it will be passed to funcRefForPath as the second argument of funcRefForPath
     * @param silent         in most case {@link SQLs#TRUE} or {@link  SQLs#FALSE}
     * @throws CriteriaException throw when argument isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     *                           {@link SQLs#rowParam(TypeInfer, Collection)}
     * @see #jsonbPathQuery(Expression, Expression, Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">jsonb_path_query ( target jsonb, path jsonpath [, vars jsonb [, silent boolean ]] ) → setof jsonb<br/>
     *
     * </a>
     */
    public static <T> _ColumnWithOrdinalityFunction jsonbPathQuery(Expression target, BiFunction<MappingType, T, Expression> funcRefForPath,
                                                                   T path, Expression vars, Expression silent) {
        return jsonbPathQuery(target, funcRefForPath.apply(JsonPathType.INSTANCE, path), vars, silent);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonbType#TEXT}.
     * this filed name of function is specified by AS clause.
     * * <pre><br/>
     * jsonb_path_query ( target jsonb, path jsonpath [, vars jsonb [, silent boolean ]] ) → setof jsonb
     *
     * Returns all JSON items returned by the JSON path for the specified JSON value. The optional vars
     * and silent arguments act the same as for jsonb_path_exists.
     *
     * select q.q from jsonb_path_query('{"a":[1,2,3,4,5]}', '$.a[*] ? (@ >= $min &amp;&amp; @ &lt;= $max)', '{"min":2, "max":4}') as q →
     *
     *  q
     * ------------------
     *  2
     *  3
     *  4
     * </pre>
     *
     * @param funcRefForPath the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                       <ul>
     *                           <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                           <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                           <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                           <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                           <li>developer custom method</li>
     *                       </ul>.
     *                       The first argument of funcRefForPath always is {@link JsonPathType#INSTANCE}.
     * @param path           non-null,it will be passed to funcRefForPath as the second argument of funcRefForPath
     * @param funcRefForVars the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                       <ul>
     *                           <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                           <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                           <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                           <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                           <li>developer custom method</li>
     *                       </ul>.
     *                       The first argument of funcRefForVars always is {@link JsonbType#TEXT}.
     * @param vars           non-null,it will be passed to funcRefForVars as the second argument of funcRefForVars
     * @param silent         in most case {@link SQLs#TRUE} or {@link  SQLs#FALSE}
     * @throws CriteriaException throw when argument isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     *                           {@link SQLs#rowParam(TypeInfer, Collection)}
     * @see #jsonbPathQuery(Expression, Expression, Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">jsonb_path_query ( target jsonb, path jsonpath [, vars jsonb [, silent boolean ]] ) → setof jsonb<br/>
     *
     * </a>
     */
    public static <T, U> _ColumnWithOrdinalityFunction jsonbPathQuery(Expression target, BiFunction<MappingType, T, Expression> funcRefForPath,
                                                                      T path, BiFunction<MappingType, U, Expression> funcRefForVars,
                                                                      U vars, Expression silent) {
        return jsonbPathQuery(target, funcRefForPath.apply(JsonPathType.INSTANCE, path),
                funcRefForVars.apply(JsonbType.TEXT, vars), silent
        );
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonbType#TEXT}.
     * this filed name of function is specified by AS clause.
     * * <pre><br/>
     * jsonb_path_query ( target jsonb, path jsonpath [, vars jsonb [, silent boolean ]] ) → setof jsonb
     *
     * Returns all JSON items returned by the JSON path for the specified JSON value. The optional vars
     * and silent arguments act the same as for jsonb_path_exists.
     *
     * select q.q from jsonb_path_query('{"a":[1,2,3,4,5]}', '$.a[*] ? (@ >= $min &amp;&amp; @ &lt;= $max)', '{"min":2, "max":4}') as q →
     *
     *  q
     * ------------------
     *  2
     *  3
     *  4
     * </pre>
     *
     * @param silent in most case {@link SQLs#TRUE} or {@link  SQLs#FALSE}
     * @throws CriteriaException throw when argument isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     *                           {@link SQLs#rowParam(TypeInfer, Collection)}
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">jsonb_path_query ( target jsonb, path jsonpath [, vars jsonb [, silent boolean ]] ) → setof jsonb<br/>
     *
     * </a>
     */
    public static _ColumnWithOrdinalityFunction jsonbPathQuery(Expression target, Expression path, Expression vars,
                                                               Expression silent) {
        return DialectFunctionUtils.fourArgColumnFunction("JSONB_PATH_QUERY", target, path, vars, silent,
                null, JsonbType.TEXT
        );
    }

    /*-------------------below jsonb_path_query_array-------------------*/

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonbType#TEXT}.
     * *
     *
     * @param funcRefForPath the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                       <ul>
     *                           <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                           <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                           <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                           <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                           <li>developer custom method</li>
     *                       </ul>.
     *                       The first argument of funcRefForPath always is {@link JsonPathType#INSTANCE}.
     * @param path           non-null,it will be passed to funcRefForPath as the second argument of funcRefForPath
     * @throws CriteriaException throw when argument isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     *                           {@link SQLs#rowParam(TypeInfer, Collection)}
     * @see #jsonbPathQueryArray(Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">jsonb_path_query_array ( target jsonb, path jsonpath [, vars jsonb [, silent boolean ]] ) → jsonb<br/>
     *
     * </a>
     */
    public static <T> Expression jsonbPathQueryArray(Expression target, BiFunction<MappingType, T, Expression> funcRefForPath,
                                                           T path) {
        return jsonbPathQueryArray(target, funcRefForPath.apply(JsonPathType.INSTANCE, path));
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonbType#TEXT}.
     * *
     *
     * @throws CriteriaException throw when argument isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     *                           {@link SQLs#rowParam(TypeInfer, Collection)}
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">jsonb_path_query_array ( target jsonb, path jsonpath [, vars jsonb [, silent boolean ]] ) → jsonb<br/>
     *
     * </a>
     */
    public static SimpleExpression jsonbPathQueryArray(Expression target, Expression path) {
        return FunctionUtils.twoArgFunc("JSONB_PATH_QUERY_ARRAY", target, path, JsonbType.TEXT);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonbType#TEXT}.
     * *
     *
     * @param funcRefForPath the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                       <ul>
     *                           <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                           <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                           <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                           <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                           <li>developer custom method</li>
     *                       </ul>.
     *                       The first argument of funcRefForPath always is {@link JsonPathType#INSTANCE}.
     * @param path           non-null,it will be passed to funcRefForPath as the second argument of funcRefForPath
     * @throws CriteriaException throw when argument isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     *                           {@link SQLs#rowParam(TypeInfer, Collection)}
     * @see #jsonbPathQueryArray(Expression, Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">jsonb_path_query_array ( target jsonb, path jsonpath [, vars jsonb [, silent boolean ]] ) → jsonb<br/>
     *
     * </a>
     */
    public static <T> Expression jsonbPathQueryArray(Expression target, BiFunction<MappingType, T, Expression> funcRefForPath,
                                                           T path, Expression vars) {
        return jsonbPathQueryArray(target, funcRefForPath.apply(JsonPathType.INSTANCE, path), vars);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonbType#TEXT}.
     * *
     *
     * @param funcRefForPath the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                       <ul>
     *                           <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                           <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                           <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                           <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                           <li>developer custom method</li>
     *                       </ul>.
     *                       The first argument of funcRefForPath always is {@link JsonPathType#INSTANCE}.
     * @param path           non-null,it will be passed to funcRefForPath as the second argument of funcRefForPath
     * @param funcRefForVars the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                       <ul>
     *                           <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                           <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                           <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                           <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                           <li>developer custom method</li>
     *                       </ul>.
     *                       The first argument of funcRefForVars always is {@link JsonbType#TEXT}.
     * @param vars           non-null,it will be passed to funcRefForVars as the second argument of funcRefForVars
     * @throws CriteriaException throw when argument isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     *                           {@link SQLs#rowParam(TypeInfer, Collection)}
     * @see #jsonbPathQueryArray(Expression, Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">jsonb_path_query_array ( target jsonb, path jsonpath [, vars jsonb [, silent boolean ]] ) → jsonb<br/>
     *
     * </a>
     */
    public static <T, U> Expression jsonbPathQueryArray(Expression target, BiFunction<MappingType, T, Expression> funcRefForPath,
                                                              T path, BiFunction<MappingType, U, Expression> funcRefForVars, U vars) {
        return jsonbPathQueryArray(target, funcRefForPath.apply(JsonPathType.INSTANCE, path),
                funcRefForVars.apply(JsonbType.TEXT, vars)
        );
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonbType#TEXT}.
     * *
     *
     * @throws CriteriaException throw when argument isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     *                           {@link SQLs#rowParam(TypeInfer, Collection)}
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">jsonb_path_query_array ( target jsonb, path jsonpath [, vars jsonb [, silent boolean ]] ) → jsonb<br/>
     *
     * </a>
     */
    public static SimpleExpression jsonbPathQueryArray(Expression target, Expression path, Expression vars) {
        return FunctionUtils.threeArgFunc("JSONB_PATH_QUERY_ARRAY", target, path, vars, JsonbType.TEXT);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonbType#TEXT}.
     * *
     *
     * @param funcRefForPath the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                       <ul>
     *                           <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                           <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                           <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                           <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                           <li>developer custom method</li>
     *                       </ul>.
     *                       The first argument of funcRefForPath always is {@link JsonPathType#INSTANCE}.
     * @param path           non-null,it will be passed to funcRefForPath as the second argument of funcRefForPath
     * @param silent         in most case {@link SQLs#TRUE} or {@link  SQLs#FALSE}
     * @throws CriteriaException throw when argument isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     *                           {@link SQLs#rowParam(TypeInfer, Collection)}
     * @see #jsonbPathQueryArray(Expression, Expression, Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">jsonb_path_query_array ( target jsonb, path jsonpath [, vars jsonb [, silent boolean ]] ) → jsonb<br/>
     *
     * </a>
     */
    public static <T> Expression jsonbPathQueryArray(Expression target, BiFunction<MappingType, T, Expression> funcRefForPath,
                                                           T path, Expression vars, Expression silent) {
        return jsonbPathQueryArray(target, funcRefForPath.apply(JsonPathType.INSTANCE, path), vars, silent);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonbType#TEXT}.
     * *
     *
     * @param funcRefForPath the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                       <ul>
     *                           <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                           <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                           <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                           <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                           <li>developer custom method</li>
     *                       </ul>.
     *                       The first argument of funcRefForPath always is {@link JsonPathType#INSTANCE}.
     * @param path           non-null,it will be passed to funcRefForPath as the second argument of funcRefForPath
     * @param funcRefForVars the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                       <ul>
     *                           <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                           <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                           <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                           <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                           <li>developer custom method</li>
     *                       </ul>.
     *                       The first argument of funcRefForVars always is {@link JsonbType#TEXT}.
     * @param vars           non-null,it will be passed to funcRefForVars as the second argument of funcRefForVars
     *                       @param silent in most case {@link SQLs#TRUE} or {@link  SQLs#FALSE}
     * @throws CriteriaException throw when argument isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     *                           {@link SQLs#rowParam(TypeInfer, Collection)}
     * @see #jsonbPathQueryArray(Expression, Expression, Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">jsonb_path_query_array ( target jsonb, path jsonpath [, vars jsonb [, silent boolean ]] ) → jsonb<br/>
     *
     * </a>
     */
    public static <T, U> Expression jsonbPathQueryArray(Expression target, BiFunction<MappingType, T, Expression> funcRefForPath,
                                                              T path, BiFunction<MappingType, U, Expression> funcRefForVars, U vars,
                                                              Expression silent) {
        return jsonbPathQueryArray(target, funcRefForPath.apply(JsonPathType.INSTANCE, path),
                funcRefForVars.apply(JsonbType.TEXT, vars), silent
        );
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonbType#TEXT}.
     * *
     *
     * @param silent in most case {@link SQLs#TRUE} or {@link  SQLs#FALSE}
     * @throws CriteriaException throw when argument isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     *                           {@link SQLs#rowParam(TypeInfer, Collection)}
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">jsonb_path_query_array ( target jsonb, path jsonpath [, vars jsonb [, silent boolean ]] ) → jsonb<br/>
     *
     * </a>
     */
    public static SimpleExpression jsonbPathQueryArray(Expression target, Expression path, Expression vars, Expression silent) {
        return FunctionUtils.fourArgFunc("JSONB_PATH_QUERY_ARRAY", target, path, vars, silent, JsonbType.TEXT);
    }


    /*-------------------below jsonb_path_query_first-------------------*/

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonbType#TEXT}.
     * *
     *
     * @param funcRefForPath the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                       <ul>
     *                           <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                           <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                           <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                           <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                           <li>developer custom method</li>
     *                       </ul>.
     *                       The first argument of funcRefForPath always is {@link JsonPathType#INSTANCE}.
     * @param path           non-null,it will be passed to funcRefForPath as the second argument of funcRefForPath
     * @throws CriteriaException throw when argument isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     *                           {@link SQLs#rowParam(TypeInfer, Collection)}
     * @see #jsonbPathQueryFirst(Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">jsonb_path_query_first ( target jsonb, path jsonpath [, vars jsonb [, silent boolean ]] ) → jsonb<br/>
     *
     * </a>
     */
    public static <T> Expression jsonbPathQueryFirst(Expression target, BiFunction<MappingType, T, Expression> funcRefForPath,
                                                           T path) {
        return jsonbPathQueryFirst(target, funcRefForPath.apply(JsonPathType.INSTANCE, path));
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonbType#TEXT}.
     * *
     *
     * @throws CriteriaException throw when argument isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     *                           {@link SQLs#rowParam(TypeInfer, Collection)}
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">jsonb_path_query_first ( target jsonb, path jsonpath [, vars jsonb [, silent boolean ]] ) → jsonb<br/>
     *
     * </a>
     */
    public static SimpleExpression jsonbPathQueryFirst(Expression target, Expression path) {
        return FunctionUtils.twoArgFunc("JSONB_PATH_QUERY_FIRST", target, path, JsonbType.TEXT);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonbType#TEXT}.
     * *
     *
     * @param funcRefForPath the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                       <ul>
     *                           <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                           <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                           <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                           <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                           <li>developer custom method</li>
     *                       </ul>.
     *                       The first argument of funcRefForPath always is {@link JsonPathType#INSTANCE}.
     * @param path           non-null,it will be passed to funcRefForPath as the second argument of funcRefForPath
     * @throws CriteriaException throw when argument isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     *                           {@link SQLs#rowParam(TypeInfer, Collection)}
     * @see #jsonbPathQueryFirst(Expression, Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">jsonb_path_query_first ( target jsonb, path jsonpath [, vars jsonb [, silent boolean ]] ) → jsonb<br/>
     *
     * </a>
     */
    public static <T> Expression jsonbPathQueryFirst(Expression target, BiFunction<MappingType, T, Expression> funcRefForPath,
                                                           T path, Expression vars) {
        return jsonbPathQueryFirst(target, funcRefForPath.apply(JsonPathType.INSTANCE, path), vars);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonbType#TEXT}.
     * *
     *
     * @param funcRefForPath the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                       <ul>
     *                           <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                           <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                           <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                           <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                           <li>developer custom method</li>
     *                       </ul>.
     *                       The first argument of funcRefForPath always is {@link JsonPathType#INSTANCE}.
     * @param path           non-null,it will be passed to funcRefForPath as the second argument of funcRefForPath
     * @param funcRefForVars the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                       <ul>
     *                           <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                           <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                           <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                           <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                           <li>developer custom method</li>
     *                       </ul>.
     *                       The first argument of funcRefForVars always is {@link JsonbType#TEXT}.
     * @param vars           non-null,it will be passed to funcRefForVars as the second argument of funcRefForVars
     * @throws CriteriaException throw when argument isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     *                           {@link SQLs#rowParam(TypeInfer, Collection)}
     * @see #jsonbPathQueryFirst(Expression, Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">jsonb_path_query_first ( target jsonb, path jsonpath [, vars jsonb [, silent boolean ]] ) → jsonb<br/>
     *
     * </a>
     */
    public static <T, U> Expression jsonbPathQueryFirst(Expression target, BiFunction<MappingType, T, Expression> funcRefForPath,
                                                              T path, BiFunction<MappingType, U, Expression> funcRefForVars, U vars) {
        return jsonbPathQueryFirst(target, funcRefForPath.apply(JsonPathType.INSTANCE, path),
                funcRefForVars.apply(JsonbType.TEXT, vars)
        );
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonbType#TEXT}.
     * *
     *
     * @throws CriteriaException throw when argument isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     *                           {@link SQLs#rowParam(TypeInfer, Collection)}
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">jsonb_path_query_first ( target jsonb, path jsonpath [, vars jsonb [, silent boolean ]] ) → jsonb<br/>
     *
     * </a>
     */
    public static SimpleExpression jsonbPathQueryFirst(Expression target, Expression path, Expression vars) {
        return FunctionUtils.threeArgFunc("JSONB_PATH_QUERY_FIRST", target, path, vars, JsonbType.TEXT);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonbType#TEXT}.
     * *
     *
     * @param funcRefForPath the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                       <ul>
     *                           <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                           <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                           <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                           <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                           <li>developer custom method</li>
     *                       </ul>.
     *                       The first argument of funcRefForPath always is {@link JsonPathType#INSTANCE}.
     * @param path           non-null,it will be passed to funcRefForPath as the second argument of funcRefForPath
     * @param silent         in most case {@link SQLs#TRUE} or {@link  SQLs#FALSE}
     * @throws CriteriaException throw when argument isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     *                           {@link SQLs#rowParam(TypeInfer, Collection)}
     * @see #jsonbPathQueryFirst(Expression, Expression, Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">jsonb_path_query_first ( target jsonb, path jsonpath [, vars jsonb [, silent boolean ]] ) → jsonb<br/>
     *
     * </a>
     */
    public static <T> Expression jsonbPathQueryFirst(Expression target, BiFunction<MappingType, T, Expression> funcRefForPath,
                                                           T path, Expression vars, Expression silent) {
        return jsonbPathQueryFirst(target, funcRefForPath.apply(JsonPathType.INSTANCE, path), vars, silent);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonbType#TEXT}.
     * *
     *
     * @param funcRefForPath the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                       <ul>
     *                           <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                           <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                           <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                           <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                           <li>developer custom method</li>
     *                       </ul>.
     *                       The first argument of funcRefForPath always is {@link JsonPathType#INSTANCE}.
     * @param path           non-null,it will be passed to funcRefForPath as the second argument of funcRefForPath
     * @param funcRefForVars the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                       <ul>
     *                           <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                           <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                           <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                           <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                           <li>developer custom method</li>
     *                       </ul>.
     *                       The first argument of funcRefForVars always is {@link JsonbType#TEXT}.
     * @param vars           non-null,it will be passed to funcRefForVars as the second argument of funcRefForVars
     * @throws CriteriaException throw when argument isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     *                           {@link SQLs#rowParam(TypeInfer, Collection)}
     * @see #jsonbPathQueryFirst(Expression, Expression, Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">jsonb_path_query_first ( target jsonb, path jsonpath [, vars jsonb [, silent boolean ]] ) → jsonb<br/>
     *
     * </a>
     */
    public static <T, U> Expression jsonbPathQueryFirst(Expression target, BiFunction<MappingType, T, Expression> funcRefForPath,
                                                              T path, BiFunction<MappingType, U, Expression> funcRefForVars, U vars,
                                                              Expression silent) {
        return jsonbPathQueryFirst(target, funcRefForPath.apply(JsonPathType.INSTANCE, path),
                funcRefForVars.apply(JsonbType.TEXT, vars), silent
        );
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonbType#TEXT}.
     * *
     *
     * @param silent in most case {@link SQLs#TRUE} or {@link  SQLs#FALSE}
     * @throws CriteriaException throw when argument isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     *                           {@link SQLs#rowParam(TypeInfer, Collection)}
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">jsonb_path_query_first ( target jsonb, path jsonpath [, vars jsonb [, silent boolean ]] ) → jsonb<br/>
     *
     * </a>
     */
    public static SimpleExpression jsonbPathQueryFirst(Expression target, Expression path, Expression vars,
                                                       Expression silent) {
        return FunctionUtils.fourArgFunc("JSONB_PATH_QUERY_FIRST", target, path, vars, silent, JsonbType.TEXT);
    }

    /*-------------------below jsonb_path_exists_tz-------------------*/

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link BooleanType}.
     * *
     *
     * @param funcRef the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                <ul>
     *                    <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                    <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                    <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                    <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                    <li>developer custom method</li>
     *                </ul>.
     *                The first argument of funcRef always is {@link JsonPathType#INSTANCE}.
     * @param path    non-null,it will be passed to funcRef as the second argument of funcRef
     * @throws CriteriaException throw when argument isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     *                           {@link SQLs#rowParam(TypeInfer, Collection)}
     * @see #jsonbPathExistsTz(Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">jsonb_path_exists_tz ( target jsonb, path jsonpath [, vars jsonb [, silent boolean ]] ) → boolean<br/>
     *
     * </a>
     */
    public static <T> SimplePredicate jsonbPathExistsTz(Expression target, BiFunction<MappingType, T, Expression> funcRef, T path) {
        return jsonbPathExistsTz(target, funcRef.apply(JsonPathType.INSTANCE, path));
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link BooleanType}.
     * *
     *
     * @throws CriteriaException throw when argument isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     *                           {@link SQLs#rowParam(TypeInfer, Collection)}
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">jsonb_path_exists_tz ( target jsonb, path jsonpath [, vars jsonb [, silent boolean ]] ) → boolean<br/>
     *
     * </a>
     */
    public static SimplePredicate jsonbPathExistsTz(Expression target, Expression path) {
        return FunctionUtils.twoArgPredicateFunc("JSONB_PATH_EXISTS_TZ", target, path);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link BooleanType}.
     * *
     *
     * @param funcRef the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                <ul>
     *                    <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                    <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                    <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                    <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                    <li>developer custom method</li>
     *                </ul>.
     *                The first argument of funcRef always is {@link JsonPathType#INSTANCE}.
     * @param path    non-null,it will be passed to funcRef as the second argument of funcRef
     * @throws CriteriaException throw when argument isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     *                           {@link SQLs#rowParam(TypeInfer, Collection)}
     * @see #jsonbPathExistsTz(Expression, Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">jsonb_path_exists_tz ( target jsonb, path jsonpath [, vars jsonb [, silent boolean ]] ) → boolean<br/>
     *
     * </a>
     */
    public static <T> SimplePredicate jsonbPathExistsTz(Expression target, BiFunction<MappingType, T, Expression> funcRef,
                                                        T path, Expression vars) {
        return jsonbPathExistsTz(target, funcRef.apply(JsonPathType.INSTANCE, path), vars);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link BooleanType}.
     * *
     *
     * @param funcRefForPath the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                       <ul>
     *                           <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                           <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                           <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                           <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                           <li>developer custom method</li>
     *                       </ul>.
     *                       The first argument of funcRefForPath always is {@link JsonPathType#INSTANCE}.
     * @param path           non-null,it will be passed to funcRefForPath as the second argument of funcRefForPath
     * @param funcRefForVars the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                       <ul>
     *                           <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                           <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                           <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                           <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                           <li>developer custom method</li>
     *                       </ul>.
     *                       The first argument of funcRefForVars always is {@link JsonbType#TEXT}.
     * @param vars           non-null,it will be passed to funcRefForVars as the second argument of funcRefForVars
     * @throws CriteriaException throw when argument isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     *                           {@link SQLs#rowParam(TypeInfer, Collection)}
     * @see #jsonbPathExistsTz(Expression, Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">jsonb_path_exists_tz ( target jsonb, path jsonpath [, vars jsonb [, silent boolean ]] ) → boolean<br/>
     *
     * </a>
     */
    public static <T, U> SimplePredicate jsonbPathExistsTz(Expression target, BiFunction<MappingType, T, Expression> funcRefForPath,
                                                           T path, BiFunction<MappingType, U, Expression> funcRefForVars, U vars) {
        return jsonbPathExistsTz(target, funcRefForPath.apply(JsonPathType.INSTANCE, path),
                funcRefForVars.apply(JsonbType.TEXT, vars)
        );
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link BooleanType}.
     * *
     *
     * @throws CriteriaException throw when argument isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     *                           {@link SQLs#rowParam(TypeInfer, Collection)}
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">jsonb_path_exists_tz ( target jsonb, path jsonpath [, vars jsonb [, silent boolean ]] ) → boolean<br/>
     *
     * </a>
     */
    public static SimplePredicate jsonbPathExistsTz(Expression target, Expression path, Expression vars) {
        return FunctionUtils.threeArgPredicateFunc("JSONB_PATH_EXISTS_TZ", target, path, vars);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link BooleanType}.
     * *
     *
     * @param funcRef the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                <ul>
     *                    <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                    <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                    <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                    <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                    <li>developer custom method</li>
     *                </ul>.
     *                The first argument of funcRef always is {@link JsonPathType#INSTANCE}.
     * @param path    non-null,it will be passed to funcRef as the second argument of funcRef
     * @param silent  in most case {@link SQLs#TRUE} or {@link  SQLs#FALSE}
     * @throws CriteriaException throw when argument isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     *                           {@link SQLs#rowParam(TypeInfer, Collection)}
     * @see #jsonbPathExistsTz(Expression, Expression, Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">jsonb_path_exists_tz ( target jsonb, path jsonpath [, vars jsonb [, silent boolean ]] ) → boolean<br/>
     *
     * </a>
     */
    public static <T> SimplePredicate jsonbPathExistsTz(Expression target, BiFunction<MappingType, T, Expression> funcRef,
                                                        T path, Expression vars, Expression silent) {
        return jsonbPathExistsTz(target, funcRef.apply(JsonPathType.INSTANCE, path), vars, silent);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link BooleanType}.
     * *
     *
     * @param funcRefForPath the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                       <ul>
     *                           <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                           <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                           <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                           <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                           <li>developer custom method</li>
     *                       </ul>.
     *                       The first argument of funcRefForPath always is {@link JsonPathType#INSTANCE}.
     * @param path           non-null,it will be passed to funcRefForPath as the second argument of funcRefForPath
     * @param funcRefForVars the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                       <ul>
     *                           <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                           <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                           <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                           <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                           <li>developer custom method</li>
     *                       </ul>.
     *                       The first argument of funcRefForVars always is {@link JsonbType#TEXT}.
     * @param vars           non-null,it will be passed to funcRefForVars as the second argument of funcRefForVars
     * @param silent         in most case {@link SQLs#TRUE} or {@link  SQLs#FALSE}
     * @throws CriteriaException throw when argument isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     *                           {@link SQLs#rowParam(TypeInfer, Collection)}
     * @see #jsonbPathExistsTz(Expression, Expression, Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">jsonb_path_exists_tz ( target jsonb, path jsonpath [, vars jsonb [, silent boolean ]] ) → boolean<br/>
     *
     * </a>
     */
    public static <T, U> SimplePredicate jsonbPathExistsTz(Expression target, BiFunction<MappingType, T, Expression> funcRefForPath,
                                                           T path, BiFunction<MappingType, U, Expression> funcRefForVars, U vars,
                                                           Expression silent) {
        return jsonbPathExistsTz(target, funcRefForPath.apply(JsonPathType.INSTANCE, path),
                funcRefForVars.apply(JsonbType.TEXT, vars), silent
        );
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link BooleanType}.
     * *
     *
     * @param silent in most case {@link SQLs#TRUE} or {@link  SQLs#FALSE}
     * @throws CriteriaException throw when argument isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     *                           {@link SQLs#rowParam(TypeInfer, Collection)}
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">jsonb_path_exists_tz ( target jsonb, path jsonpath [, vars jsonb [, silent boolean ]] ) → boolean<br/>
     *
     * </a>
     */
    public static SimplePredicate jsonbPathExistsTz(Expression target, Expression path, Expression vars,
                                                    Expression silent) {
        return FunctionUtils.fourArgPredicateFunc("JSONB_PATH_EXISTS_TZ", target, path, vars, silent);
    }

    /*-------------------below jsonb_path_match_tz-------------------*/

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link BooleanType}.
     * *
     *
     * @param funcRef the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                <ul>
     *                    <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                    <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                    <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                    <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                    <li>developer custom method</li>
     *                </ul>.
     *                The first argument of funcRef always is {@link JsonPathType#INSTANCE}.
     * @param path    non-null,it will be passed to funcRef as the second argument of funcRef
     * @throws CriteriaException throw when argument isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     *                           {@link SQLs#rowParam(TypeInfer, Collection)}
     * @see #jsonbPathMatchTz(Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">jsonb_path_match_tz ( target jsonb, path jsonpath [, vars jsonb [, silent boolean ]] ) → boolean<br/>
     *
     * </a>
     */
    public static <T> SimplePredicate jsonbPathMatchTz(Expression target, BiFunction<MappingType, T, Expression> funcRef, T path) {
        return jsonbPathMatchTz(target, funcRef.apply(JsonPathType.INSTANCE, path));
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link BooleanType}.
     * *
     *
     * @throws CriteriaException throw when argument isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     *                           {@link SQLs#rowParam(TypeInfer, Collection)}
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">jsonb_path_match_tz ( target jsonb, path jsonpath [, vars jsonb [, silent boolean ]] ) → boolean<br/>
     *
     * </a>
     */
    public static SimplePredicate jsonbPathMatchTz(Expression target, Expression path) {
        return FunctionUtils.twoArgPredicateFunc("JSONB_PATH_MATCH_TZ", target, path);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link BooleanType}.
     * *
     *
     * @param funcRef the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                <ul>
     *                    <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                    <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                    <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                    <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                    <li>developer custom method</li>
     *                </ul>.
     *                The first argument of funcRef always is {@link JsonPathType#INSTANCE}.
     * @param path    non-null,it will be passed to funcRef as the second argument of funcRef
     * @throws CriteriaException throw when argument isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     *                           {@link SQLs#rowParam(TypeInfer, Collection)}
     * @see #jsonbPathMatchTz(Expression, Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">jsonb_path_match_tz ( target jsonb, path jsonpath [, vars jsonb [, silent boolean ]] ) → boolean<br/>
     *
     * </a>
     */
    public static <T> SimplePredicate jsonbPathMatchTz(Expression target, BiFunction<MappingType, T, Expression> funcRef,
                                                       T path, Expression vars) {
        return jsonbPathMatchTz(target, funcRef.apply(JsonPathType.INSTANCE, path), vars);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link BooleanType}.
     * *
     *
     * @param funcRefForPath the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                       <ul>
     *                           <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                           <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                           <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                           <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                           <li>developer custom method</li>
     *                       </ul>.
     *                       The first argument of funcRefForPath always is {@link JsonPathType#INSTANCE}.
     * @param path           non-null,it will be passed to funcRefForPath as the second argument of funcRefForPath
     * @param funcRefForVars the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                       <ul>
     *                           <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                           <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                           <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                           <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                           <li>developer custom method</li>
     *                       </ul>.
     *                       The first argument of funcRefForVars always is {@link JsonbType#TEXT}.
     * @param vars           non-null,it will be passed to funcRefForVars as the second argument of funcRefForVars
     * @throws CriteriaException throw when argument isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     *                           {@link SQLs#rowParam(TypeInfer, Collection)}
     * @see #jsonbPathMatchTz(Expression, Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">jsonb_path_match_tz ( target jsonb, path jsonpath [, vars jsonb [, silent boolean ]] ) → boolean<br/>
     *
     * </a>
     */
    public static <T, U> SimplePredicate jsonbPathMatchTz(Expression target, BiFunction<MappingType, T, Expression> funcRefForPath,
                                                          T path, BiFunction<MappingType, U, Expression> funcRefForVars, U vars) {
        return jsonbPathMatchTz(target, funcRefForPath.apply(JsonPathType.INSTANCE, path),
                funcRefForVars.apply(JsonbType.TEXT, vars)
        );
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link BooleanType}.
     * *
     *
     * @throws CriteriaException throw when argument isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     *                           {@link SQLs#rowParam(TypeInfer, Collection)}
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">jsonb_path_match_tz ( target jsonb, path jsonpath [, vars jsonb [, silent boolean ]] ) → boolean<br/>
     *
     * </a>
     */
    public static SimplePredicate jsonbPathMatchTz(Expression target, Expression path, Expression vars) {
        return FunctionUtils.threeArgPredicateFunc("JSONB_PATH_MATCH_TZ", target, path, vars);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link BooleanType}.
     * *
     *
     * @param funcRef the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                <ul>
     *                    <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                    <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                    <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                    <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                    <li>developer custom method</li>
     *                </ul>.
     *                The first argument of funcRef always is {@link JsonPathType#INSTANCE}.
     * @param path    non-null,it will be passed to funcRef as the second argument of funcRef
     * @param silent  in most case {@link SQLs#TRUE} or {@link  SQLs#FALSE}
     * @throws CriteriaException throw when argument isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     *                           {@link SQLs#rowParam(TypeInfer, Collection)}
     * @see #jsonbPathMatchTz(Expression, Expression, Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">jsonb_path_match_tz ( target jsonb, path jsonpath [, vars jsonb [, silent boolean ]] ) → boolean<br/>
     *
     * </a>
     */
    public static <T> SimplePredicate jsonbPathMatchTz(Expression target, BiFunction<MappingType, T, Expression> funcRef,
                                                       T path, Expression vars, Expression silent) {
        return jsonbPathMatchTz(target, funcRef.apply(JsonPathType.INSTANCE, path), vars, silent);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link BooleanType}.
     * *
     *
     * @param funcRefForPath the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                       <ul>
     *                           <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                           <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                           <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                           <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                           <li>developer custom method</li>
     *                       </ul>.
     *                       The first argument of funcRefForPath always is {@link JsonPathType#INSTANCE}.
     * @param path           non-null,it will be passed to funcRefForPath as the second argument of funcRefForPath
     * @param funcRefForVars the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                       <ul>
     *                           <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                           <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                           <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                           <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                           <li>developer custom method</li>
     *                       </ul>.
     *                       The first argument of funcRefForVars always is {@link JsonbType#TEXT}.
     * @param vars           non-null,it will be passed to funcRefForVars as the second argument of funcRefForVars
     * @param silent         in most case {@link SQLs#TRUE} or {@link  SQLs#FALSE}
     * @throws CriteriaException throw when argument isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     *                           {@link SQLs#rowParam(TypeInfer, Collection)}
     * @see #jsonbPathMatchTz(Expression, Expression, Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">jsonb_path_match_tz ( target jsonb, path jsonpath [, vars jsonb [, silent boolean ]] ) → boolean<br/>
     *
     * </a>
     */
    public static <T, U> SimplePredicate jsonbPathMatchTz(Expression target, BiFunction<MappingType, T, Expression> funcRefForPath,
                                                          T path, BiFunction<MappingType, U, Expression> funcRefForVars, U vars,
                                                          Expression silent) {
        return jsonbPathMatchTz(target, funcRefForPath.apply(JsonPathType.INSTANCE, path),
                funcRefForVars.apply(JsonbType.TEXT, vars), silent
        );
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link BooleanType}.
     * *
     *
     * @param silent in most case {@link SQLs#TRUE} or {@link  SQLs#FALSE}
     * @throws CriteriaException throw when argument isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     *                           {@link SQLs#rowParam(TypeInfer, Collection)}
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">jsonb_path_match_tz ( target jsonb, path jsonpath [, vars jsonb [, silent boolean ]] ) → boolean<br/>
     *
     * </a>
     */
    public static SimplePredicate jsonbPathMatchTz(Expression target, Expression path, Expression vars,
                                                   Expression silent) {
        return FunctionUtils.fourArgPredicateFunc("JSONB_PATH_MATCH_TZ", target, path, vars, silent);
    }

    /*-------------------below jsonb_path_query_tz-------------------*/

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonbType#TEXT}.
     * the field of function is specified by AS clause.
     * * <pre><br/>
     * jsonb_path_query_tz ( target jsonb, path jsonpath [, vars jsonb [, silent boolean ]] ) → setof jsonb
     *
     * Returns all JSON items returned by the JSON path for the specified JSON value. The optional vars
     * and silent arguments act the same as for jsonb_path_exists.
     *
     * select tz.value from jsonb_path_query_tz(JSONB E'{
     *   "a": [
     *     "2015-08-01 12:00:00-05",
     *     "2015-08-02 12:00:00-05",
     *     "2015-08-03 12:00:00-05",
     *     "2015-08-04 12:00:00-05",
     *     "2015-08-05 12:00:00-05"
     *   ]
     * }', JSONPATH '$.a[*] ? (@.datetime() &lt; "2015-08-05".datetime())') AS(value) tz →
     *
     *  tz
     * ------------------
     *  "2015-08-01 12:00:00-05"
     *  "2015-08-02 12:00:00-05"
     *  "2015-08-03 12:00:00-05"
     *  "2015-08-04 12:00:00-05"
     * </pre>
     *
     * @param funcRefForPath the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                       <ul>
     *                           <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                           <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                           <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                           <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                           <li>developer custom method</li>
     *                       </ul>.
     *                       The first argument of funcRefForPath always is {@link JsonPathType#INSTANCE}.
     * @param path           non-null,it will be passed to funcRefForPath as the second argument of funcRefForPath
     * @throws CriteriaException throw when argument isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     *                           {@link SQLs#rowParam(TypeInfer, Collection)}
     * @see #jsonbPathQueryTz(Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">jsonb_path_query_tz ( target jsonb, path jsonpath [, vars jsonb [, silent boolean ]] ) → setof jsonb<br/>
     *
     * </a>
     */
    public static <T> _ColumnWithOrdinalityFunction jsonbPathQueryTz(Expression target, BiFunction<MappingType, T, Expression> funcRefForPath,
                                                                     T path) {
        return jsonbPathQueryTz(target, funcRefForPath.apply(JsonPathType.INSTANCE, path));
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonbType#TEXT}.
     * the field of function is specified by AS clause.
     * * <pre><br/>
     * jsonb_path_query_tz ( target jsonb, path jsonpath [, vars jsonb [, silent boolean ]] ) → setof jsonb
     *
     * Returns all JSON items returned by the JSON path for the specified JSON value. The optional vars
     * and silent arguments act the same as for jsonb_path_exists.
     *
     * select tz.value from jsonb_path_query_tz(JSONB E'{
     *   "a": [
     *     "2015-08-01 12:00:00-05",
     *     "2015-08-02 12:00:00-05",
     *     "2015-08-03 12:00:00-05",
     *     "2015-08-04 12:00:00-05",
     *     "2015-08-05 12:00:00-05"
     *   ]
     * }', JSONPATH '$.a[*] ? (@.datetime() &lt; "2015-08-05".datetime())') AS(value) tz →
     *
     *  tz
     * ------------------
     *  "2015-08-01 12:00:00-05"
     *  "2015-08-02 12:00:00-05"
     *  "2015-08-03 12:00:00-05"
     *  "2015-08-04 12:00:00-05"
     * </pre>
     *
     * @throws CriteriaException throw when argument isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     *                           {@link SQLs#rowParam(TypeInfer, Collection)}
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">jsonb_path_query_tz ( target jsonb, path jsonpath [, vars jsonb [, silent boolean ]] ) → setof jsonb<br/>
     *
     * </a>
     */
    public static _ColumnWithOrdinalityFunction jsonbPathQueryTz(Expression target, Expression path) {
        return DialectFunctionUtils.twoArgColumnFunction("JSONB_PATH_QUERY_TZ", target, path,
                null, JsonbType.TEXT
        );
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonbType#TEXT}.
     * the field of function is specified by AS clause.
     * * <pre><br/>
     * jsonb_path_query_tz ( target jsonb, path jsonpath [, vars jsonb [, silent boolean ]] ) → setof jsonb
     *
     * Returns all JSON items returned by the JSON path for the specified JSON value. The optional vars
     * and silent arguments act the same as for jsonb_path_exists.
     *
     * select tz.value from jsonb_path_query_tz(JSONB E'{
     *   "a": [
     *     "2015-08-01 12:00:00-05",
     *     "2015-08-02 12:00:00-05",
     *     "2015-08-03 12:00:00-05",
     *     "2015-08-04 12:00:00-05",
     *     "2015-08-05 12:00:00-05"
     *   ]
     * }', JSONPATH '$.a[*] ? (@.datetime() &lt; "2015-08-05".datetime())') AS(value) tz →
     *
     *  tz
     * ------------------
     *  "2015-08-01 12:00:00-05"
     *  "2015-08-02 12:00:00-05"
     *  "2015-08-03 12:00:00-05"
     *  "2015-08-04 12:00:00-05"
     * </pre>
     *
     * @param funcRefForPath the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                       <ul>
     *                           <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                           <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                           <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                           <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                           <li>developer custom method</li>
     *                       </ul>.
     *                       The first argument of funcRefForPath always is {@link JsonPathType#INSTANCE}.
     * @param path           non-null,it will be passed to funcRefForPath as the second argument of funcRefForPath
     * @throws CriteriaException throw when argument isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     *                           {@link SQLs#rowParam(TypeInfer, Collection)}
     * @see #jsonbPathQueryTz(Expression, Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">jsonb_path_query_tz ( target jsonb, path jsonpath [, vars jsonb [, silent boolean ]] ) → setof jsonb<br/>
     *
     * </a>
     */
    public static <T> _ColumnWithOrdinalityFunction jsonbPathQueryTz(Expression target, BiFunction<MappingType, T, Expression> funcRefForPath,
                                                                     T path, Expression vars) {
        return jsonbPathQueryTz(target, funcRefForPath.apply(JsonPathType.INSTANCE, path), vars);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonbType#TEXT}.
     * the field of function is specified by AS clause.
     * * <pre><br/>
     * jsonb_path_query_tz ( target jsonb, path jsonpath [, vars jsonb [, silent boolean ]] ) → setof jsonb
     *
     * Returns all JSON items returned by the JSON path for the specified JSON value. The optional vars
     * and silent arguments act the same as for jsonb_path_exists.
     *
     * select tz.value from jsonb_path_query_tz(JSONB E'{
     *   "a": [
     *     "2015-08-01 12:00:00-05",
     *     "2015-08-02 12:00:00-05",
     *     "2015-08-03 12:00:00-05",
     *     "2015-08-04 12:00:00-05",
     *     "2015-08-05 12:00:00-05"
     *   ]
     * }', JSONPATH '$.a[*] ? (@.datetime() &lt; "2015-08-05".datetime())') AS(value) tz →
     *
     *  tz
     * ------------------
     *  "2015-08-01 12:00:00-05"
     *  "2015-08-02 12:00:00-05"
     *  "2015-08-03 12:00:00-05"
     *  "2015-08-04 12:00:00-05"
     * </pre>
     *
     * @param funcRefForPath the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                       <ul>
     *                           <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                           <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                           <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                           <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                           <li>developer custom method</li>
     *                       </ul>.
     *                       The first argument of funcRefForPath always is {@link JsonPathType#INSTANCE}.
     * @param path           non-null,it will be passed to funcRefForPath as the second argument of funcRefForPath
     * @throws CriteriaException throw when argument isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     *                           {@link SQLs#rowParam(TypeInfer, Collection)}
     * @see #jsonbPathQueryTz(Expression, Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">jsonb_path_query_tz ( target jsonb, path jsonpath [, vars jsonb [, silent boolean ]] ) → setof jsonb<br/>
     *
     * </a>
     */
    public static <T, U> _ColumnWithOrdinalityFunction jsonbPathQueryTz(Expression target, BiFunction<MappingType, T, Expression> funcRefForPath,
                                                                        T path, BiFunction<MappingType, U, Expression> funcRefForVars, U vars) {
        return jsonbPathQueryTz(target, funcRefForPath.apply(JsonPathType.INSTANCE, path),
                funcRefForVars.apply(JsonbType.TEXT, vars)
        );
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonbType#TEXT}.
     * the field of function is specified by AS clause.
     * * <pre><br/>
     * jsonb_path_query_tz ( target jsonb, path jsonpath [, vars jsonb [, silent boolean ]] ) → setof jsonb
     *
     * Returns all JSON items returned by the JSON path for the specified JSON value. The optional vars
     * and silent arguments act the same as for jsonb_path_exists.
     *
     * select tz.value from jsonb_path_query_tz(JSONB E'{
     *   "a": [
     *     "2015-08-01 12:00:00-05",
     *     "2015-08-02 12:00:00-05",
     *     "2015-08-03 12:00:00-05",
     *     "2015-08-04 12:00:00-05",
     *     "2015-08-05 12:00:00-05"
     *   ]
     * }', JSONPATH '$.a[*] ? (@.datetime() &lt; "2015-08-05".datetime())') AS(value) tz →
     *
     *  tz
     * ------------------
     *  "2015-08-01 12:00:00-05"
     *  "2015-08-02 12:00:00-05"
     *  "2015-08-03 12:00:00-05"
     *  "2015-08-04 12:00:00-05"
     * </pre>
     *
     * @throws CriteriaException throw when argument isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     *                           {@link SQLs#rowParam(TypeInfer, Collection)}
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">jsonb_path_query_tz ( target jsonb, path jsonpath [, vars jsonb [, silent boolean ]] ) → setof jsonb<br/>
     *
     * </a>
     */
    public static _ColumnWithOrdinalityFunction jsonbPathQueryTz(Expression target, Expression path, Expression vars) {
        return DialectFunctionUtils.threeArgColumnFunction("JSONB_PATH_QUERY_TZ", target, path, vars,
                null, JsonbType.TEXT
        );
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonbType#TEXT}.
     * the field of function is specified by AS clause.
     * * <pre><br/>
     * jsonb_path_query_tz ( target jsonb, path jsonpath [, vars jsonb [, silent boolean ]] ) → setof jsonb
     *
     * Returns all JSON items returned by the JSON path for the specified JSON value. The optional vars
     * and silent arguments act the same as for jsonb_path_exists.
     *
     * select tz.value from jsonb_path_query_tz(JSONB E'{
     *   "a": [
     *     "2015-08-01 12:00:00-05",
     *     "2015-08-02 12:00:00-05",
     *     "2015-08-03 12:00:00-05",
     *     "2015-08-04 12:00:00-05",
     *     "2015-08-05 12:00:00-05"
     *   ]
     * }', JSONPATH '$.a[*] ? (@.datetime() &lt; "2015-08-05".datetime())') AS(value) tz →
     *
     *  tz
     * ------------------
     *  "2015-08-01 12:00:00-05"
     *  "2015-08-02 12:00:00-05"
     *  "2015-08-03 12:00:00-05"
     *  "2015-08-04 12:00:00-05"
     * </pre>
     *
     * @param funcRefForPath the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                       <ul>
     *                           <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                           <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                           <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                           <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                           <li>developer custom method</li>
     *                       </ul>.
     *                       The first argument of funcRefForPath always is {@link JsonPathType#INSTANCE}.
     * @param path           non-null,it will be passed to funcRefForPath as the second argument of funcRefForPath
     * @param silent         in most case {@link SQLs#TRUE} or {@link  SQLs#FALSE}
     * @throws CriteriaException throw when argument isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     *                           {@link SQLs#rowParam(TypeInfer, Collection)}
     * @see #jsonbPathQueryTz(Expression, Expression, Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">jsonb_path_query_tz ( target jsonb, path jsonpath [, vars jsonb [, silent boolean ]] ) → setof jsonb<br/>
     *
     * </a>
     */
    public static <T> _ColumnWithOrdinalityFunction jsonbPathQueryTz(Expression target, BiFunction<MappingType, T, Expression> funcRefForPath,
                                                                     T path, Expression vars, Expression silent) {
        return jsonbPathQueryTz(target, funcRefForPath.apply(JsonPathType.INSTANCE, path), vars, silent);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonbType#TEXT}.
     * the field of function is specified by AS clause.
     * * <pre><br/>
     * jsonb_path_query_tz ( target jsonb, path jsonpath [, vars jsonb [, silent boolean ]] ) → setof jsonb
     *
     * Returns all JSON items returned by the JSON path for the specified JSON value. The optional vars
     * and silent arguments act the same as for jsonb_path_exists.
     *
     * select tz.value from jsonb_path_query_tz(JSONB E'{
     *   "a": [
     *     "2015-08-01 12:00:00-05",
     *     "2015-08-02 12:00:00-05",
     *     "2015-08-03 12:00:00-05",
     *     "2015-08-04 12:00:00-05",
     *     "2015-08-05 12:00:00-05"
     *   ]
     * }', JSONPATH '$.a[*] ? (@.datetime() &lt; "2015-08-05".datetime())') AS(value) tz →
     *
     *  tz
     * ------------------
     *  "2015-08-01 12:00:00-05"
     *  "2015-08-02 12:00:00-05"
     *  "2015-08-03 12:00:00-05"
     *  "2015-08-04 12:00:00-05"
     * </pre>
     *
     * @param funcRefForPath the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                       <ul>
     *                           <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                           <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                           <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                           <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                           <li>developer custom method</li>
     *                       </ul>.
     *                       The first argument of funcRefForPath always is {@link JsonPathType#INSTANCE}.
     * @param path           non-null,it will be passed to funcRefForPath as the second argument of funcRefForPath
     * @throws CriteriaException throw when argument isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     *                           {@link SQLs#rowParam(TypeInfer, Collection)}
     * @see #jsonbPathQueryTz(Expression, Expression, Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">jsonb_path_query_tz ( target jsonb, path jsonpath [, vars jsonb [, silent boolean ]] ) → setof jsonb<br/>
     *
     * </a>
     */
    public static <T, U> _ColumnWithOrdinalityFunction jsonbPathQueryTz(Expression target, BiFunction<MappingType, T, Expression> funcRefForPath,
                                                                        T path, BiFunction<MappingType, U, Expression> funcRefForVars, U vars,
                                                                        Expression silent) {
        return jsonbPathQueryTz(target, funcRefForPath.apply(JsonPathType.INSTANCE, path),
                funcRefForVars.apply(JsonbType.TEXT, vars), silent
        );
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonbType#TEXT}.
     * the field of function is specified by AS clause.
     * * <pre><br/>
     * jsonb_path_query_tz ( target jsonb, path jsonpath [, vars jsonb [, silent boolean ]] ) → setof jsonb
     *
     * Returns all JSON items returned by the JSON path for the specified JSON value. The optional vars
     * and silent arguments act the same as for jsonb_path_exists.
     *
     * select tz.value from jsonb_path_query_tz(JSONB E'{
     *   "a": [
     *     "2015-08-01 12:00:00-05",
     *     "2015-08-02 12:00:00-05",
     *     "2015-08-03 12:00:00-05",
     *     "2015-08-04 12:00:00-05",
     *     "2015-08-05 12:00:00-05"
     *   ]
     * }', JSONPATH '$.a[*] ? (@.datetime() &lt; "2015-08-05".datetime())') AS(value) tz →
     *
     *  tz
     * ------------------
     *  "2015-08-01 12:00:00-05"
     *  "2015-08-02 12:00:00-05"
     *  "2015-08-03 12:00:00-05"
     *  "2015-08-04 12:00:00-05"
     * </pre>
     *
     * @param silent in most case {@link SQLs#TRUE} or {@link  SQLs#FALSE}
     * @throws CriteriaException throw when argument isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     *                           {@link SQLs#rowParam(TypeInfer, Collection)}
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">jsonb_path_query_tz ( target jsonb, path jsonpath [, vars jsonb [, silent boolean ]] ) → setof jsonb<br/>
     *
     * </a>
     */
    public static _ColumnWithOrdinalityFunction jsonbPathQueryTz(Expression target, Expression path, Expression vars,
                                                                 Expression silent) {
        return DialectFunctionUtils.fourArgColumnFunction("JSONB_PATH_QUERY_TZ", target, path, vars, silent,
                null, JsonbType.TEXT
        );
    }

    /*-------------------below jsonb_path_query_array_tz-------------------*/


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonbType#TEXT}.
     * *
     *
     * @param funcRefForPath the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                       <ul>
     *                           <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                           <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                           <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                           <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                           <li>developer custom method</li>
     *                       </ul>.
     *                       The first argument of funcRefForPath always is {@link JsonPathType#INSTANCE}.
     * @param path           non-null,it will be passed to funcRefForPath as the second argument of funcRefForPath
     * @throws CriteriaException throw when argument isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     *                           {@link SQLs#rowParam(TypeInfer, Collection)}
     * @see #jsonbPathQueryArrayTz(Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">jsonb_path_query_array_tz ( target jsonb, path jsonpath [, vars jsonb [, silent boolean ]] ) → jsonb<br/>
     *
     * </a>
     */
    public static <T> Expression jsonbPathQueryArrayTz(Expression target, BiFunction<MappingType, T, Expression> funcRefForPath,
                                                             T path) {
        return jsonbPathQueryArrayTz(target, funcRefForPath.apply(JsonPathType.INSTANCE, path));
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonbType#TEXT}.
     * *
     *
     * @throws CriteriaException throw when argument isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     *                           {@link SQLs#rowParam(TypeInfer, Collection)}
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">jsonb_path_query_array_tz ( target jsonb, path jsonpath [, vars jsonb [, silent boolean ]] ) → jsonb<br/>
     *
     * </a>
     */
    public static SimpleExpression jsonbPathQueryArrayTz(Expression target, Expression path) {
        return FunctionUtils.twoArgFunc("JSONB_PATH_QUERY_ARRAY_TZ", target, path, JsonbType.TEXT);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonbType#TEXT}.
     * *
     *
     * @param funcRefForPath the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                       <ul>
     *                           <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                           <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                           <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                           <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                           <li>developer custom method</li>
     *                       </ul>.
     *                       The first argument of funcRefForPath always is {@link JsonPathType#INSTANCE}.
     * @param path           non-null,it will be passed to funcRefForPath as the second argument of funcRefForPath
     * @throws CriteriaException throw when argument isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     *                           {@link SQLs#rowParam(TypeInfer, Collection)}
     * @see #jsonbPathQueryArrayTz(Expression, Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">jsonb_path_query_array_tz ( target jsonb, path jsonpath [, vars jsonb [, silent boolean ]] ) → jsonb<br/>
     *
     * </a>
     */
    public static <T> Expression jsonbPathQueryArrayTz(Expression target, BiFunction<MappingType, T, Expression> funcRefForPath,
                                                             T path, Expression vars) {
        return jsonbPathQueryArrayTz(target, funcRefForPath.apply(JsonPathType.INSTANCE, path), vars);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonbType#TEXT}.
     * *
     *
     * @param funcRefForPath the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                       <ul>
     *                           <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                           <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                           <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                           <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                           <li>developer custom method</li>
     *                       </ul>.
     *                       The first argument of funcRefForPath always is {@link JsonPathType#INSTANCE}.
     * @param path           non-null,it will be passed to funcRefForPath as the second argument of funcRefForPath
     * @param funcRefForVars the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                       <ul>
     *                           <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                           <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                           <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                           <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                           <li>developer custom method</li>
     *                       </ul>.
     *                       The first argument of funcRefForVars always is {@link JsonPathType#INSTANCE}.
     * @param vars           non-null,it will be passed to funcRefForVars as the second argument of funcRefForVars
     * @throws CriteriaException throw when argument isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     *                           {@link SQLs#rowParam(TypeInfer, Collection)}
     * @see #jsonbPathQueryArrayTz(Expression, Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">jsonb_path_query_array_tz ( target jsonb, path jsonpath [, vars jsonb [, silent boolean ]] ) → jsonb<br/>
     *
     * </a>
     */
    public static <T, U> Expression jsonbPathQueryArrayTz(Expression target, BiFunction<MappingType, T, Expression> funcRefForPath,
                                                                T path, BiFunction<MappingType, U, Expression> funcRefForVars,
                                                                U vars) {
        return jsonbPathQueryArrayTz(target, funcRefForPath.apply(JsonPathType.INSTANCE, path),
                funcRefForVars.apply(JsonbType.TEXT, vars)
        );
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonbType#TEXT}.
     * *
     *
     * @throws CriteriaException throw when argument isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     *                           {@link SQLs#rowParam(TypeInfer, Collection)}
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">jsonb_path_query_array_tz ( target jsonb, path jsonpath [, vars jsonb [, silent boolean ]] ) → jsonb<br/>
     *
     * </a>
     */
    public static SimpleExpression jsonbPathQueryArrayTz(Expression target, Expression path, Expression vars) {
        return FunctionUtils.threeArgFunc("JSONB_PATH_QUERY_ARRAY_TZ", target, path, vars, JsonbType.TEXT);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonbType#TEXT}.
     * *
     *
     * @param funcRefForPath the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                       <ul>
     *                           <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                           <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                           <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                           <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                           <li>developer custom method</li>
     *                       </ul>.
     *                       The first argument of funcRefForPath always is {@link JsonPathType#INSTANCE}.
     * @param path           non-null,it will be passed to funcRefForPath as the second argument of funcRefForPath
     * @param silent         in most case {@link SQLs#TRUE} or {@link  SQLs#FALSE}
     * @throws CriteriaException throw when argument isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     *                           {@link SQLs#rowParam(TypeInfer, Collection)}
     * @see #jsonbPathQueryArrayTz(Expression, Expression, Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">jsonb_path_query_array_tz ( target jsonb, path jsonpath [, vars jsonb [, silent boolean ]] ) → jsonb<br/>
     *
     * </a>
     */
    public static <T> Expression jsonbPathQueryArrayTz(Expression target, BiFunction<MappingType, T, Expression> funcRefForPath,
                                                             T path, Expression vars, Expression silent) {
        return jsonbPathQueryArrayTz(target, funcRefForPath.apply(JsonPathType.INSTANCE, path), vars, silent);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonbType#TEXT}.
     * *
     *
     * @param funcRefForPath the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                       <ul>
     *                           <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                           <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                           <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                           <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                           <li>developer custom method</li>
     *                       </ul>.
     *                       The first argument of funcRefForPath always is {@link JsonPathType#INSTANCE}.
     * @param path           non-null,it will be passed to funcRefForPath as the second argument of funcRefForPath
     * @param funcRefForVars the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                       <ul>
     *                           <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                           <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                           <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                           <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                           <li>developer custom method</li>
     *                       </ul>.
     *                       The first argument of funcRefForVars always is {@link JsonPathType#INSTANCE}.
     * @param vars           non-null,it will be passed to funcRefForVars as the second argument of funcRefForVars
     * @param silent         in most case {@link SQLs#TRUE} or {@link  SQLs#FALSE}
     * @throws CriteriaException throw when argument isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     *                           {@link SQLs#rowParam(TypeInfer, Collection)}
     * @see #jsonbPathQueryArrayTz(Expression, Expression, Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">jsonb_path_query_array_tz ( target jsonb, path jsonpath [, vars jsonb [, silent boolean ]] ) → jsonb<br/>
     *
     * </a>
     */
    public static <T, U> Expression jsonbPathQueryArrayTz(Expression target, BiFunction<MappingType, T, Expression> funcRefForPath,
                                                                T path, BiFunction<MappingType, U, Expression> funcRefForVars,
                                                                U vars, Expression silent) {
        return jsonbPathQueryArrayTz(target, funcRefForPath.apply(JsonPathType.INSTANCE, path),
                funcRefForVars.apply(JsonbType.TEXT, vars), silent
        );
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonbType#TEXT}.
     * *
     *
     * @param silent in most case {@link SQLs#TRUE} or {@link  SQLs#FALSE}
     * @throws CriteriaException throw when argument isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     *                           {@link SQLs#rowParam(TypeInfer, Collection)}
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">jsonb_path_query_array_tz ( target jsonb, path jsonpath [, vars jsonb [, silent boolean ]] ) → jsonb<br/>
     *
     * </a>
     */
    public static SimpleExpression jsonbPathQueryArrayTz(Expression target, Expression path, Expression vars, Expression silent) {
        return FunctionUtils.fourArgFunc("JSONB_PATH_QUERY_ARRAY_TZ", target, path, vars, silent, JsonbType.TEXT);
    }


    /*-------------------below jsonb_path_query_first_tz-------------------*/

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonbType#TEXT}.
     * *
     *
     * @param funcRefForPath the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                       <ul>
     *                           <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                           <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                           <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                           <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                           <li>developer custom method</li>
     *                       </ul>.
     *                       The first argument of funcRefForPath always is {@link JsonPathType#INSTANCE}.
     * @param path           non-null,it will be passed to funcRefForPath as the second argument of funcRefForPath
     * @throws CriteriaException throw when argument isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     *                           {@link SQLs#rowParam(TypeInfer, Collection)}
     * @see #jsonbPathQueryFirstTz(Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">jsonb_path_query_first_tz ( target jsonb, path jsonpath [, vars jsonb [, silent boolean ]] ) → jsonb<br/>
     *
     * </a>
     */
    public static <T> Expression jsonbPathQueryFirstTz(Expression target, BiFunction<MappingType, T, Expression> funcRefForPath,
                                                             T path) {
        return jsonbPathQueryFirstTz(target, funcRefForPath.apply(JsonPathType.INSTANCE, path));
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonbType#TEXT}.
     * *
     *
     * @throws CriteriaException throw when argument isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     *                           {@link SQLs#rowParam(TypeInfer, Collection)}
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">jsonb_path_query_first_tz ( target jsonb, path jsonpath [, vars jsonb [, silent boolean ]] ) → jsonb<br/>
     *
     * </a>
     */
    public static SimpleExpression jsonbPathQueryFirstTz(Expression target, Expression path) {
        return FunctionUtils.twoArgFunc("JSONB_PATH_QUERY_FIRST_TZ", target, path, JsonbType.TEXT);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonbType#TEXT}.
     * *
     *
     * @param funcRefForPath the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                       <ul>
     *                           <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                           <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                           <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                           <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                           <li>developer custom method</li>
     *                       </ul>.
     *                       The first argument of funcRefForPath always is {@link JsonPathType#INSTANCE}.
     * @param path           non-null,it will be passed to funcRefForPath as the second argument of funcRefForPath
     * @throws CriteriaException throw when argument isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     *                           {@link SQLs#rowParam(TypeInfer, Collection)}
     * @see #jsonbPathQueryFirstTz(Expression, Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">jsonb_path_query_first_tz ( target jsonb, path jsonpath [, vars jsonb [, silent boolean ]] ) → jsonb<br/>
     *
     * </a>
     */
    public static <T> Expression jsonbPathQueryFirstTz(Expression target, BiFunction<MappingType, T, Expression> funcRefForPath,
                                                             T path, Expression vars) {
        return jsonbPathQueryFirstTz(target, funcRefForPath.apply(JsonPathType.INSTANCE, path), vars);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonbType#TEXT}.
     * *
     *
     * @param funcRefForPath the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                       <ul>
     *                           <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                           <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                           <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                           <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                           <li>developer custom method</li>
     *                       </ul>.
     *                       The first argument of funcRefForPath always is {@link JsonPathType#INSTANCE}.
     * @param path           non-null,it will be passed to funcRefForPath as the second argument of funcRefForPath
     * @param funcRefForVars the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                       <ul>
     *                           <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                           <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                           <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                           <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                           <li>developer custom method</li>
     *                       </ul>.
     *                       The first argument of funcRefForVars always is {@link JsonPathType#INSTANCE}.
     * @param vars           non-null,it will be passed to funcRefForVars as the second argument of funcRefForVars
     * @throws CriteriaException throw when argument isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     *                           {@link SQLs#rowParam(TypeInfer, Collection)}
     * @see #jsonbPathQueryFirstTz(Expression, Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">jsonb_path_query_first_tz ( target jsonb, path jsonpath [, vars jsonb [, silent boolean ]] ) → jsonb<br/>
     *
     * </a>
     */
    public static <T, U> Expression jsonbPathQueryFirstTz(Expression target, BiFunction<MappingType, T, Expression> funcRefForPath,
                                                                T path, BiFunction<MappingType, U, Expression> funcRefForVars, U vars) {
        return jsonbPathQueryFirstTz(target, funcRefForPath.apply(JsonPathType.INSTANCE, path),
                funcRefForVars.apply(JsonbType.TEXT, vars)
        );
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonbType#TEXT}.
     * *
     *
     * @throws CriteriaException throw when argument isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     *                           {@link SQLs#rowParam(TypeInfer, Collection)}
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">jsonb_path_query_first_tz ( target jsonb, path jsonpath [, vars jsonb [, silent boolean ]] ) → jsonb<br/>
     *
     * </a>
     */
    public static SimpleExpression jsonbPathQueryFirstTz(Expression target, Expression path, Expression vars) {
        return FunctionUtils.threeArgFunc("JSONB_PATH_QUERY_FIRST_TZ", target, path, vars, JsonbType.TEXT);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonbType#TEXT}.
     * *
     *
     * @param funcRefForPath the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                       <ul>
     *                           <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                           <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                           <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                           <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                           <li>developer custom method</li>
     *                       </ul>.
     *                       The first argument of funcRefForPath always is {@link JsonPathType#INSTANCE}.
     * @param path           non-null,it will be passed to funcRefForPath as the second argument of funcRefForPath
     * @param silent         in most case {@link SQLs#TRUE} or {@link  SQLs#FALSE}
     * @throws CriteriaException throw when argument isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     *                           {@link SQLs#rowParam(TypeInfer, Collection)}
     * @see #jsonbPathQueryFirstTz(Expression, Expression, Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">jsonb_path_query_first_tz ( target jsonb, path jsonpath [, vars jsonb [, silent boolean ]] ) → jsonb<br/>
     *
     * </a>
     */
    public static <T> Expression jsonbPathQueryFirstTz(Expression target, BiFunction<MappingType, T, Expression> funcRefForPath,
                                                             T path, Expression vars, Expression silent) {
        return jsonbPathQueryFirstTz(target, funcRefForPath.apply(JsonPathType.INSTANCE, path), vars, silent);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonbType#TEXT}.
     * *
     *
     * @param funcRefForPath the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                       <ul>
     *                           <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                           <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                           <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                           <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                           <li>developer custom method</li>
     *                       </ul>.
     *                       The first argument of funcRefForPath always is {@link JsonPathType#INSTANCE}.
     * @param path           non-null,it will be passed to funcRefForPath as the second argument of funcRefForPath
     * @param funcRefForVars the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                       <ul>
     *                           <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                           <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                           <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                           <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                           <li>developer custom method</li>
     *                       </ul>.
     *                       The first argument of funcRefForVars always is {@link JsonPathType#INSTANCE}.
     * @param vars           non-null,it will be passed to funcRefForVars as the second argument of funcRefForVars
     * @throws CriteriaException throw when argument isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     *                           {@link SQLs#rowParam(TypeInfer, Collection)}
     * @see #jsonbPathQueryFirstTz(Expression, Expression, Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">jsonb_path_query_first_tz ( target jsonb, path jsonpath [, vars jsonb [, silent boolean ]] ) → jsonb<br/>
     *
     * </a>
     */
    public static <T, U> Expression jsonbPathQueryFirstTz(Expression target, BiFunction<MappingType, T, Expression> funcRefForPath,
                                                                T path, BiFunction<MappingType, U, Expression> funcRefForVars, U vars,
                                                                Expression silent) {
        return jsonbPathQueryFirstTz(target, funcRefForPath.apply(JsonPathType.INSTANCE, path),
                funcRefForVars.apply(JsonbType.TEXT, vars), silent
        );
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonbType#TEXT}.
     * *
     *
     * @param silent in most case {@link SQLs#TRUE} or {@link  SQLs#FALSE}
     * @throws CriteriaException throw when argument isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     *                           {@link SQLs#rowParam(TypeInfer, Collection)}
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">jsonb_path_query_first_tz ( target jsonb, path jsonpath [, vars jsonb [, silent boolean ]] ) → jsonb<br/>
     *
     * </a>
     */
    public static SimpleExpression jsonbPathQueryFirstTz(Expression target, Expression path, Expression vars,
                                                         Expression silent) {
        return FunctionUtils.fourArgFunc("JSONB_PATH_QUERY_FIRST_TZ", target, path, vars, silent, JsonbType.TEXT);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link TextType}.
     * *
     *
     * @throws CriteriaException throw when argument isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     *                           {@link SQLs#rowParam(TypeInfer, Collection)}
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">jsonb_pretty ( jsonb ) → text<br/>
     *
     * </a>
     */
    public static SimpleExpression jsonbPretty(Expression jsonb) {
        return FunctionUtils.oneArgFunc("JSONB_PRETTY", jsonb, TextType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link TextType}.
     * *
     *
     * @throws CriteriaException throw when argument isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     *                           {@link SQLs#rowParam(TypeInfer, Collection)}
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">json_typeof ( json ) → text<br/>
     *
     * </a>
     */
    public static SimpleExpression jsonTypeOf(Expression json) {
        return FunctionUtils.oneArgFunc("JSON_TYPEOF", json, TextType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link TextType}.
     * *
     *
     * @throws CriteriaException throw when argument isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     *                           {@link SQLs#rowParam(TypeInfer, Collection)}
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">jsonb_typeof ( json ) → text<br/>
     *
     * </a>
     */
    public static SimpleExpression jsonbTypeOf(Expression json) {
        return FunctionUtils.oneArgFunc("JSONB_TYPEOF", json, TextType.INSTANCE);
    }






    /*-------------------below private method -------------------*/

    /**
     * @see #xmlElement(Postgres.WordName, String, XmlAttributes, Expression...)
     * @see #xmlElement(Postgres.WordName, String, Expression...)
     * @see #xmlElement(Postgres.WordName, String, List)
     * @see #xmlElement(Postgres.WordName, String, XmlAttributes, List)
     */
    private static SimpleExpression _xmlElement(final Postgres.WordName nameWord, final @Nullable String name,
                                                final @Nullable XmlAttributes attributes,
                                                Consumer<Consumer<Object>> consumer) {
        final String funcName = "XMLELEMENT";
        if (nameWord != Postgres.NAME) {
            throw CriteriaUtils.funcArgError(funcName, nameWord);
        } else if (name == null) {
            throw ContextStack.clearStackAndNullPointer();
        } else if (!(attributes == null || attributes instanceof PostgreFunctionUtils.XmlAttributes)) {
            throw CriteriaUtils.funcArgError(funcName, attributes);
        }
        final List<Object> argList = _Collections.arrayList();
        argList.add(nameWord);
        argList.add(name);
        if (attributes != null) {
            argList.add(SqlWords.FuncWord.COMMA);
            argList.add(attributes);
        }
        consumer.accept(argList::add);
        return FunctionUtils.complexArgFunc(funcName, argList, XmlType.TEXT);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  XmlType#TEXT}
     * *
     *
     * @see #xmlPi(Postgres.WordName, String)
     * @see #xmlPi(Postgres.WordName, String, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-xml.html#FUNCTIONS-PRODUCING-XML">xmlpi ( NAME name [, content ] ) → xml<br/>
     * </a>
     */
    private static SimpleExpression _xmlPi(Postgres.WordName wordName, String name, @Nullable Expression content) {
        final String funcName = "XMLPI";
        if (wordName != Postgres.NAME) {
            throw CriteriaUtils.funcArgError(funcName, wordName);
        } else if (!_DialectUtils.isSimpleIdentifier(name)) {
            throw CriteriaUtils.funcArgError(funcName, name);
        }
        final SimpleExpression func;
        if (content == null) {
            func = FunctionUtils.complexArgFunc(funcName, XmlType.TEXT, wordName, name);
        } else {
            func = FunctionUtils.complexArgFunc(funcName, XmlType.TEXT, wordName, name, SqlWords.FuncWord.COMMA, content);
        }
        return func;
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  XmlType#TEXT}
     * *
     *
     * @see #xmlRoot(Expression, WordVersion, WordsNoValue)
     * @see #xmlRoot(Expression, WordVersion, Expression)
     * @see #xmlRoot(Expression, WordVersion, WordsNoValue, WordStandalone, StandaloneOption)
     * @see #xmlRoot(Expression, WordVersion, Expression, WordStandalone, StandaloneOption)
     * @see <a href="https://www.postgresql.org/docs/current/functions-xml.html#FUNCTIONS-PRODUCING-XML">xmlroot ( xml, VERSION {text|NO VALUE} [, STANDALONE {YES|NO|NO VALUE} ] ) → xml<br/>
     * </a>
     */
    private static SimpleExpression _xmlRoot(final Expression xml, final WordVersion version,
                                             final Object textOrNoValue, final WordStandalone standalone,
                                             final @Nullable StandaloneOption option) {
        final String name = "XMLROOT";
        if (!(xml instanceof FunctionArg.SingleFunctionArg)) {
            throw CriteriaUtils.funcArgError(name, xml);
        } else if (version != Postgres.VERSION) {
            throw CriteriaUtils.funcArgError(name, version);
        } else if (!(textOrNoValue instanceof FunctionArg.SingleFunctionArg || textOrNoValue == Postgres.NO_VALUE)) {
            throw CriteriaUtils.funcArgError(name, textOrNoValue);
        } else if (standalone != Postgres.STANDALONE) {
            throw CriteriaUtils.funcArgError(name, standalone);
        } else if (!(option == null || option == Postgres.YES || option == Postgres.NO || option == Postgres.NO_VALUE)) {
            throw CriteriaUtils.funcArgError(name, option);
        }
        final SimpleExpression func;
        if (option == null) {
            func = FunctionUtils.complexArgFunc(name, XmlType.TEXT, xml, SqlWords.FuncWord.COMMA, version,
                    textOrNoValue);
        } else {
            func = FunctionUtils.complexArgFunc(name, XmlType.TEXT, xml, SqlWords.FuncWord.COMMA, version,
                    textOrNoValue, SqlWords.FuncWord.COMMA, standalone, option);
        }
        return func;
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link Boolean}
     * *
     *
     * @see #xmlExists(Expression, WordPassing, Expression)
     * @see #xmlExists(Expression, WordPassing, PassingOption, Expression)
     * @see #xmlExists(Expression, WordPassing, Expression, PassingOption)
     * @see #xmlExists(Expression, WordPassing, PassingOption, Expression, PassingOption)
     * @see <a href="https://www.postgresql.org/docs/current/functions-xml.html#FUNCTIONS-PRODUCING-XML">XMLEXISTS ( text PASSING [BY {REF|VALUE}] xml [BY {REF|VALUE}] ) → boolean<br/>
     * </a>
     */
    private static SimplePredicate _xmlExists(final Expression text, final WordPassing passing,
                                              final @Nullable PassingOption textOption,
                                              final Expression xml, final @Nullable PassingOption xmlOption) {

        final String name = "XMLEXISTS";
        if (!(text instanceof FunctionArg.SingleFunctionArg)) {
            throw CriteriaUtils.funcArgError(name, text);
        } else if (passing != Postgres.PASSING) {
            throw CriteriaUtils.funcArgError(name, passing);
        } else if (!(textOption == null || textOption == Postgres.BY_REF || textOption == Postgres.BY_VALUE)) {
            throw CriteriaUtils.funcArgError(name, textOption);
        } else if (!(xml instanceof FunctionArg.SingleFunctionArg)) {
            throw CriteriaUtils.funcArgError(name, xml);
        } else if (!(xmlOption == null || xmlOption == Postgres.BY_REF || xmlOption == Postgres.BY_VALUE)) {
            throw CriteriaUtils.funcArgError(name, xmlOption);
        }

        final SimplePredicate func;
        if (textOption != null && xmlOption != null) {
            func = FunctionUtils.complexArgPredicateFrom(name, text, passing, textOption, xml, xmlOption);
        } else if (textOption != null) {
            func = FunctionUtils.complexArgPredicateFrom(name, text, passing, textOption, xml);
        } else if (xmlOption != null) {
            func = FunctionUtils.complexArgPredicateFrom(name, text, passing, xml, xmlOption);
        } else {
            func = FunctionUtils.complexArgPredicateFrom(name, text, passing, xml);
        }
        return func;
    }

    /**
     * <pre><br/>
     *     XMLTABLE (
     *     [ XMLNAMESPACES ( namespace_uri AS namespace_name [, ...] ), ]
     *     row_expression PASSING [BY {REF|VALUE}] document_expression [BY {REF|VALUE}]
     *     COLUMNS name { type [PATH column_expression] [DEFAULT default_expression] [NOT NULL | NULL]
     *                   | FOR ORDINALITY }
     *             [, ...]
     * ) → setof record
     * </pre>
     *
     * @see #xmlTable(Expression, WordPassing, Expression, Consumer)
     * @see #xmlTable(Expression, WordPassing, PassingOption, Expression, Consumer)
     * @see #xmlTable(XmlNameSpaces, Expression, WordPassing, Expression, Consumer)
     * @see #xmlTable(XmlNameSpaces, Expression, WordPassing, Expression, PassingOption, Consumer)
     * @see #xmlTable(Expression, WordPassing, Expression, PassingOption, Consumer)
     * @see #xmlTable(XmlNameSpaces, Expression, WordPassing, PassingOption, Expression, Consumer)
     * @see #xmlTable(XmlNameSpaces, Expression, WordPassing, PassingOption, Expression, PassingOption, Consumer)
     * @see <a href="https://www.postgresql.org/docs/current/functions-xml.html#FUNCTIONS-XML-PROCESSING">XMLTABLE</a>
     */
    private static _TabularFunction _xmlTable(final @Nullable XmlNameSpaces nameSpaces, final Expression rowExp,
                                              final WordPassing passing, final @Nullable PassingOption rowOption,
                                              final Expression docExp, final @Nullable PassingOption docOption,
                                              final @Nullable Consumer<Postgres._XmlTableColumnsClause> consumer) {

        final String name = PostgreFunctionUtils.XmlTableColumnsClause.XMLTABLE;

        if (!(nameSpaces == null || nameSpaces instanceof PostgreFunctionUtils.XmlNameSpaces)) {
            throw CriteriaUtils.funcArgError(name, nameSpaces);
        } else if (!(rowExp instanceof FunctionArg.SingleFunctionArg)) {
            throw CriteriaUtils.funcArgError(name, rowExp);
        } else if (passing != Postgres.PASSING) {
            throw CriteriaUtils.funcArgError(name, passing);
        } else if (!(rowOption == null || rowOption == Postgres.BY_REF || rowOption == Postgres.BY_VALUE)) {
            throw CriteriaUtils.funcArgError(name, rowOption);
        } else if (!(docExp instanceof FunctionArg.SingleFunctionArg)) {
            throw CriteriaUtils.funcArgError(name, docExp);
        } else if (!(docOption == null || docOption == Postgres.BY_REF || docOption == Postgres.BY_VALUE)) {
            throw CriteriaUtils.funcArgError(name, docOption);
        } else if (consumer == null) {
            throw ContextStack.clearStackAndNullPointer();
        }

        final PostgreFunctionUtils.XmlTableColumnsClause columnsClause;
        columnsClause = PostgreFunctionUtils.xmlTableColumnsClause();
        consumer.accept(columnsClause);

        final List<? extends Selection> selectionList;
        selectionList = columnsClause.endColumnsClause();

        final List<Object> argList = _Collections.arrayList(8);
        if (nameSpaces != null) {
            argList.add(nameSpaces);
            argList.add(SqlWords.FuncWord.COMMA);
        }
        argList.add(rowExp);
        argList.add(passing);
        if (rowOption != null) {
            argList.add(rowOption);
        }
        argList.add(docExp);
        if (docOption != null) {
            argList.add(docOption);
        }
        argList.add(columnsClause);
        return PostgreFunctionUtils.compositeTabularFunc(name, argList, selectionList, columnsClause.getSelectionMap());
    }


    /**
     * @see #jsonPopulateRecord(Expression, Expression)
     * @see #jsonbPopulateRecord(Expression, Expression)
     * @see #jsonPopulateRecordSet(Expression, Expression)
     * @see #jsonbPopulateRecordSet(Expression, Expression)
     */
    private static _TabularWithOrdinalityFunction _jsonbPopulateRecordFunc(final String name, final Expression base,
                                                                           final Expression json) {
        final List<Selection> fieldList;
        fieldList = DialectFunctionUtils.compositeFieldList(name, base);
        return DialectFunctionUtils.twoArgTabularFunc(name, base, json, fieldList);
    }


}
