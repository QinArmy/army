package io.army.criteria.impl;

import io.army.criteria.Expression;
import io.army.criteria.Selection;
import io.army.criteria.SimpleExpression;
import io.army.criteria.TypeInfer;
import io.army.dialect._DialectUtils;
import io.army.lang.Nullable;
import io.army.mapping.*;
import io.army.mapping.optional.ShortArrayType;
import io.army.mapping.optional.TextArrayType;
import io.army.mapping.postgre.PostgreTsQueryType;
import io.army.mapping.postgre.PostgreTsVectorType;
import io.army.util._Collections;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;

abstract class PostgreMiscellaneous2Functions extends PostgreMiscellaneousFunctions {

    /**
     * package constructor
     */
    PostgreMiscellaneous2Functions() {
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  PostgreTsVectorType}
     * </p>
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
    public static <T> SimpleExpression arrayToTsVector(BiFunction<MappingType, T, Expression> funcRef, T value) {
        return arrayToTsVector(funcRef.apply(TextArrayType.from(String[].class), value));
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  PostgreTsVectorType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-textsearch.html#TEXTSEARCH-FUNCTIONS-TABLE">array_to_tsvector ( text[] ) → tsvector</a>
     */
    public static SimpleExpression arrayToTsVector(Expression exp) {
        return FunctionUtils.oneArgFunc("ARRAY_TO_TSVECTOR", exp, PostgreTsVectorType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  StringType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-textsearch.html#TEXTSEARCH-FUNCTIONS-TABLE">get_current_ts_config ( ) → regconfig</a>
     */
    public static SimpleExpression getCurrentTsConfig() {
        return FunctionUtils.zeroArgFunc("GET_CURRENT_TS_CONFIG", StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  IntegerType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-textsearch.html#TEXTSEARCH-FUNCTIONS-TABLE">numnode ( tsquery ) → integer</a>
     */
    public static SimpleExpression numNode(Expression exp) {
        return FunctionUtils.oneArgFunc("NUMNODE", exp, IntegerType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  PostgreTsQueryType}
     * </p>
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
    public static <T> SimpleExpression plainToTsQuery(BiFunction<MappingType, T, Expression> funcRefForQuery, T query) {
        return plainToTsQuery(funcRefForQuery.apply(TextType.INSTANCE, query));
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  PostgreTsQueryType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-textsearch.html#TEXTSEARCH-FUNCTIONS-TABLE">plainto_tsquery ( [ config regconfig, ] query text ) → tsquery</a>
     */
    public static SimpleExpression plainToTsQuery(Expression query) {
        return FunctionUtils.oneArgFunc("PLAINTO_TSQUERY", query, PostgreTsQueryType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  PostgreTsQueryType}
     * </p>
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
    public static <T, U> SimpleExpression plainToTsQuery(BiFunction<MappingType, T, Expression> funcRefForConfig, T config,
                                                         BiFunction<MappingType, U, Expression> funcRefForQuery, U query) {
        return plainToTsQuery(funcRefForConfig.apply(StringType.INSTANCE, config),
                funcRefForQuery.apply(TextType.INSTANCE, query)
        );
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  PostgreTsQueryType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-textsearch.html#TEXTSEARCH-FUNCTIONS-TABLE">plainto_tsquery ( [ config regconfig, ] query text ) → tsquery</a>
     */
    public static SimpleExpression plainToTsQuery(Expression config, Expression query) {
        return FunctionUtils.twoArgFunc("PLAINTO_TSQUERY", config, query, PostgreTsQueryType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  PostgreTsQueryType}
     * </p>
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
    public static <T> SimpleExpression phraseToTsQuery(BiFunction<MappingType, T, Expression> funcRefForQuery, T query) {
        return phraseToTsQuery(funcRefForQuery.apply(TextType.INSTANCE, query));
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  PostgreTsQueryType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-textsearch.html#TEXTSEARCH-FUNCTIONS-TABLE">phraseto_tsquery ( [ config regconfig, ] query text ) → tsquery</a>
     */
    public static SimpleExpression phraseToTsQuery(Expression query) {
        return FunctionUtils.oneArgFunc("PHRASETO_TSQUERY", query, PostgreTsQueryType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  PostgreTsQueryType}
     * </p>
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
    public static <T, U> SimpleExpression phraseToTsQuery(BiFunction<MappingType, T, Expression> funcRefForConfig, T config,
                                                          BiFunction<MappingType, U, Expression> funcRefForQuery, U query) {
        return phraseToTsQuery(funcRefForConfig.apply(StringType.INSTANCE, config),
                funcRefForQuery.apply(TextType.INSTANCE, query)
        );
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  PostgreTsQueryType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-textsearch.html#TEXTSEARCH-FUNCTIONS-TABLE">phraseto_tsquery ( [ config regconfig, ] query text ) → tsquery</a>
     */
    public static SimpleExpression phraseToTsQuery(Expression config, Expression query) {
        return FunctionUtils.twoArgFunc("PHRASETO_TSQUERY", config, query, PostgreTsQueryType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  PostgreTsQueryType}
     * </p>
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
    public static <T> SimpleExpression webSearchToTsQuery(BiFunction<MappingType, T, Expression> funcRefForQuery, T query) {
        return webSearchToTsQuery(funcRefForQuery.apply(TextType.INSTANCE, query));
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  PostgreTsQueryType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-textsearch.html#TEXTSEARCH-FUNCTIONS-TABLE">websearch_to_tsquery ( [ config regconfig, ] query text ) → tsquery</a>
     */
    public static SimpleExpression webSearchToTsQuery(Expression query) {
        return FunctionUtils.oneArgFunc("WEBSEARCH_TO_TSQUERY", query, PostgreTsQueryType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  PostgreTsQueryType}
     * </p>
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
    public static <T, U> SimpleExpression webSearchToTsQuery(BiFunction<MappingType, T, Expression> funcRefForConfig, T config,
                                                             BiFunction<MappingType, U, Expression> funcRefForQuery, U query) {
        return webSearchToTsQuery(funcRefForConfig.apply(StringType.INSTANCE, config),
                funcRefForQuery.apply(TextType.INSTANCE, query)
        );
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  PostgreTsQueryType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-textsearch.html#TEXTSEARCH-FUNCTIONS-TABLE">websearch_to_tsquery ( [ config regconfig, ] query text ) → tsquery</a>
     */
    public static SimpleExpression webSearchToTsQuery(Expression config, Expression query) {
        return FunctionUtils.twoArgFunc("WEBSEARCH_TO_TSQUERY", config, query, PostgreTsQueryType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  TextType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-textsearch.html#TEXTSEARCH-FUNCTIONS-TABLE">querytree ( tsquery ) → text</a>
     */
    public static SimpleExpression queryTree(Expression tsQuery) {
        return FunctionUtils.oneArgFunc("QUERYTREE", tsQuery, TextType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  PostgreTsVectorType}
     * </p>
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
    public static <T, U> SimpleExpression setWeight(BiFunction<MappingType, T, Expression> funcRefForVector, T vector,
                                                    BiFunction<MappingType, U, Expression> funcRefForWeight, U weight) {
        return setWeight(funcRefForVector.apply(PostgreTsVectorType.INSTANCE, vector),
                funcRefForWeight.apply(CharacterType.INSTANCE, weight)
        );
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  PostgreTsVectorType}
     * </p>
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
     * </p>
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
    public static <T, U, V> SimpleExpression setWeight(BiFunction<MappingType, T, Expression> funcRefForVector, T vector,
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
     * </p>
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
     * </p>
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
    public static <T> SimpleExpression strip(BiFunction<MappingType, T, Expression> funcRef, T value) {
        return strip(funcRef.apply(PostgreTsVectorType.INSTANCE, value));
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  PostgreTsVectorType}
     * </p>
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
     * </p>
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
    public static <T> SimpleExpression toTsQuery(BiFunction<MappingType, T, Expression> funcRefForQuery, T query) {
        return toTsQuery(funcRefForQuery.apply(TextType.INSTANCE, query));
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  PostgreTsQueryType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-textsearch.html#TEXTSEARCH-FUNCTIONS-TABLE">to_tsquery ( [ config regconfig, ] query text ) → tsquery</a>
     */
    public static SimpleExpression toTsQuery(Expression query) {
        return FunctionUtils.oneArgFunc("TO_TSQUERY", query, PostgreTsQueryType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  PostgreTsQueryType}
     * </p>
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
    public static <T, U> SimpleExpression toTsQuery(BiFunction<MappingType, T, Expression> funcRefForConfig, T config,
                                                    BiFunction<MappingType, U, Expression> funcRefForQuery, U query) {
        return toTsQuery(funcRefForConfig.apply(StringType.INSTANCE, config),
                funcRefForQuery.apply(TextType.INSTANCE, query)
        );
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  PostgreTsQueryType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-textsearch.html#TEXTSEARCH-FUNCTIONS-TABLE">to_tsquery ( [ config regconfig, ] query text ) → tsquery</a>
     */
    public static SimpleExpression toTsQuery(Expression config, Expression query) {
        return FunctionUtils.twoArgFunc("TO_TSQUERY", config, query, PostgreTsQueryType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  PostgreTsVectorType}
     * </p>
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
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-textsearch.html#TEXTSEARCH-FUNCTIONS-TABLE">to_tsvector ( [ config regconfig, ] document text ) → tsvector</a>
     */
    public static SimpleExpression toTsVector(Expression config, Expression document) {
        return FunctionUtils.twoArgFunc("TO_TSVECTOR", config, document, PostgreTsVectorType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  PostgreTsVectorType}
     * </p>
     *
     * @param funcRefForDocument the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                           <ul>
     *                               <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                               <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                               <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                               <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                               <li>developer custom method</li>
     *                           </ul>.
     *                           the first argument of funcRefForDocument always is {@link JsonType#TEXT_INSTANCE}.
     * @param document           non-null,it will be passed to funcRefForDocument as the second argument of funcRefForDocument
     * @param funcRefForFilter   the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                           <ul>
     *                               <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                               <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                               <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                               <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                               <li>developer custom method</li>
     *                           </ul>.
     *                           the first argument of funcRefForFilter always is {@link JsonbType#TEXT_INSTANCE} .
     * @param filter             non-null,it will be passed to funcRefForFilter as the second argument of funcRefForFilter
     * @see #jsonToTsVector(Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-textsearch.html#TEXTSEARCH-FUNCTIONS-TABLE">json_to_tsvector ( [ config regconfig, ] document json, filter jsonb ) → tsvector</a>
     */
    public static <T, U> SimpleExpression jsonToTsVector(BiFunction<MappingType, T, Expression> funcRefForDocument, T document,
                                                         BiFunction<MappingType, U, Expression> funcRefForFilter, U filter) {
        return jsonToTsVector(funcRefForDocument.apply(JsonType.TEXT_INSTANCE, document),
                funcRefForFilter.apply(JsonbType.TEXT_INSTANCE, filter)
        );
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  PostgreTsVectorType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-textsearch.html#TEXTSEARCH-FUNCTIONS-TABLE">json_to_tsvector ( [ config regconfig, ] document json, filter jsonb ) → tsvector</a>
     */
    public static SimpleExpression jsonToTsVector(Expression document, Expression filter) {
        return FunctionUtils.twoArgFunc("JSON_TO_TSVECTOR", document, filter, PostgreTsVectorType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  PostgreTsVectorType}
     * </p>
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
     *                           the first argument of funcRefForDocument always is {@link JsonType#TEXT_INSTANCE}.
     * @param document           non-null,it will be passed to funcRefForDocument as the second argument of funcRefForDocument
     * @param funcRefForFilter   the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                           <ul>
     *                               <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                               <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                               <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                               <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                               <li>developer custom method</li>
     *                           </ul>.
     *                           the first argument of funcRefForFilter always is {@link JsonbType#TEXT_INSTANCE} .
     * @param filter             non-null,it will be passed to funcRefForFilter as the second argument of funcRefForFilter
     * @see #jsonToTsVector(Expression, Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-textsearch.html#TEXTSEARCH-FUNCTIONS-TABLE">json_to_tsvector ( [ config regconfig, ] document json, filter jsonb ) → tsvector</a>
     */
    public static <T, U, V> SimpleExpression jsonToTsVector(BiFunction<MappingType, T, Expression> funcRefForConfig, T config,
                                                            BiFunction<MappingType, U, Expression> funcRefForDocument, U document,
                                                            BiFunction<MappingType, V, Expression> funcRefForFilter, V filter) {
        return jsonToTsVector(funcRefForConfig.apply(StringType.INSTANCE, config),
                funcRefForDocument.apply(JsonType.TEXT_INSTANCE, document),
                funcRefForFilter.apply(JsonbType.TEXT_INSTANCE, filter)
        );
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  PostgreTsVectorType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-textsearch.html#TEXTSEARCH-FUNCTIONS-TABLE">json_to_tsvector ( [ config regconfig, ] document json, filter jsonb ) → tsvector</a>
     */
    public static SimpleExpression jsonToTsVector(Expression config, Expression document, Expression filter) {
        return FunctionUtils.threeArgFunc("JSON_TO_TSVECTOR", config, document, filter, PostgreTsVectorType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  PostgreTsVectorType}
     * </p>
     *
     * @param funcRefForDocument the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                           <ul>
     *                               <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                               <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                               <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                               <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                               <li>developer custom method</li>
     *                           </ul>.
     *                           the first argument of funcRefForDocument always is {@link JsonType#TEXT_INSTANCE}.
     * @param document           non-null,it will be passed to funcRefForDocument as the second argument of funcRefForDocument
     * @param funcRefForFilter   the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                           <ul>
     *                               <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                               <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                               <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                               <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                               <li>developer custom method</li>
     *                           </ul>.
     *                           the first argument of funcRefForFilter always is {@link JsonbType#TEXT_INSTANCE} .
     * @param filter             non-null,it will be passed to funcRefForFilter as the second argument of funcRefForFilter
     * @see #jsonbToTsVector(Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-textsearch.html#TEXTSEARCH-FUNCTIONS-TABLE">jsonb_to_tsvector ( [ config regconfig, ] document json, filter jsonb ) → tsvector</a>
     */
    public static <T, U> SimpleExpression jsonbToTsVector(BiFunction<MappingType, T, Expression> funcRefForDocument, T document,
                                                          BiFunction<MappingType, U, Expression> funcRefForFilter, U filter) {
        return jsonbToTsVector(funcRefForDocument.apply(JsonType.TEXT_INSTANCE, document),
                funcRefForFilter.apply(JsonbType.TEXT_INSTANCE, filter)
        );
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  PostgreTsVectorType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-textsearch.html#TEXTSEARCH-FUNCTIONS-TABLE">jsonb_to_tsvector ( [ config regconfig, ] document json, filter jsonb ) → tsvector</a>
     */
    public static SimpleExpression jsonbToTsVector(Expression document, Expression filter) {
        return FunctionUtils.twoArgFunc("JSONB_TO_TSVECTOR", document, filter, PostgreTsVectorType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  PostgreTsVectorType}
     * </p>
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
     *                           the first argument of funcRefForDocument always is {@link JsonType#TEXT_INSTANCE}.
     * @param document           non-null,it will be passed to funcRefForDocument as the second argument of funcRefForDocument
     * @param funcRefForFilter   the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                           <ul>
     *                               <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                               <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                               <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                               <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                               <li>developer custom method</li>
     *                           </ul>.
     *                           the first argument of funcRefForFilter always is {@link JsonbType#TEXT_INSTANCE} .
     * @param filter             non-null,it will be passed to funcRefForFilter as the second argument of funcRefForFilter
     * @see #jsonbToTsVector(Expression, Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-textsearch.html#TEXTSEARCH-FUNCTIONS-TABLE">jsonb_to_tsvector ( [ config regconfig, ] document json, filter jsonb ) → tsvector</a>
     */
    public static <T, U, V> SimpleExpression jsonbToTsVector(BiFunction<MappingType, T, Expression> funcRefForConfig, T config,
                                                             BiFunction<MappingType, U, Expression> funcRefForDocument, U document,
                                                             BiFunction<MappingType, V, Expression> funcRefForFilter, V filter) {
        return jsonbToTsVector(funcRefForConfig.apply(StringType.INSTANCE, config),
                funcRefForDocument.apply(JsonType.TEXT_INSTANCE, document),
                funcRefForFilter.apply(JsonbType.TEXT_INSTANCE, filter)
        );
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  PostgreTsVectorType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-textsearch.html#TEXTSEARCH-FUNCTIONS-TABLE">jsonb_to_tsvector ( [ config regconfig, ] document json, filter jsonb ) → tsvector</a>
     */
    public static SimpleExpression jsonbToTsVector(Expression config, Expression document, Expression filter) {
        return FunctionUtils.threeArgFunc("JSONB_TO_TSVECTOR", config, document, filter, PostgreTsVectorType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  PostgreTsVectorType}
     * </p>
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
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-textsearch.html#TEXTSEARCH-FUNCTIONS-TABLE">ts_filter ( vector tsvector, weights "char"[] ) → tsvector</a>
     */
    public static SimpleExpression tsFilter(Expression tsVector, Expression lexeme) {
        return FunctionUtils.twoArgFunc("TS_FILTER", tsVector, lexeme, PostgreTsVectorType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  TextType}
     * </p>
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
     * </p>
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
     * </p>
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
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-textsearch.html#TEXTSEARCH-FUNCTIONS-TABLE">ts_rank ( [ weights real[], ] vector tsvector, query tsquery [, normalization integer ] ) → real</a>
     */
    public static SimpleExpression tsRank(Expression weights, Expression vector, Expression query, Expression normalization) {
        return FunctionUtils.fourArgFunc("TS_RANK", weights, vector, query, normalization, FloatType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  FloatType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-textsearch.html#TEXTSEARCH-FUNCTIONS-TABLE">ts_rank ( [ weights real[], ] vector tsvector, query tsquery [, normalization integer ] ) → real</a>
     */
    public static SimpleExpression tsRank(Expression exp1, Expression exp2, Expression exp3) {
        return FunctionUtils.threeArgFunc("TS_RANK", exp1, exp2, exp3, FloatType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  FloatType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-textsearch.html#TEXTSEARCH-FUNCTIONS-TABLE">ts_rank ( [ weights real[], ] vector tsvector, query tsquery [, normalization integer ] ) → real</a>
     */
    public static SimpleExpression tsRank(Expression vector, Expression query) {
        return FunctionUtils.twoArgFunc("TS_RANK", vector, query, FloatType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  FloatType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-textsearch.html#TEXTSEARCH-FUNCTIONS-TABLE">ts_rank_cd ( [ weights real[], ] vector tsvector, query tsquery [, normalization integer ] ) → real</a>
     */
    public static SimpleExpression tsRankCd(Expression weights, Expression vector, Expression query, Expression normalization) {
        return FunctionUtils.fourArgFunc("TS_RANK_CD", weights, vector, query, normalization, FloatType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  FloatType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-textsearch.html#TEXTSEARCH-FUNCTIONS-TABLE">ts_rank_cd ( [ weights real[], ] vector tsvector, query tsquery [, normalization integer ] ) → real</a>
     */
    public static SimpleExpression tsRankCd(Expression exp1, Expression exp2, Expression exp3) {
        return FunctionUtils.threeArgFunc("TS_RANK_CD", exp1, exp2, exp3, FloatType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  FloatType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-textsearch.html#TEXTSEARCH-FUNCTIONS-TABLE">ts_rank_cd ( [ weights real[], ] vector tsvector, query tsquery [, normalization integer ] ) → real</a>
     */
    public static SimpleExpression tsRankCd(Expression vector, Expression query) {
        return FunctionUtils.twoArgFunc("TS_RANK_CD", vector, query, FloatType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  PostgreTsQueryType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-textsearch.html#TEXTSEARCH-FUNCTIONS-TABLE">ts_rewrite ( query tsquery, target tsquery, substitute tsquery ) → tsquery</a>
     */
    public static SimpleExpression tsRewrite(Expression query, Expression target, Expression substitute) {
        return FunctionUtils.threeArgFunc("TS_REWRITE", query, target, substitute, PostgreTsQueryType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  PostgreTsQueryType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-textsearch.html#TEXTSEARCH-FUNCTIONS-TABLE">ts_rewrite ( query tsquery, select text ) → tsquery</a>
     */
    public static SimpleExpression tsRewrite(Expression query, Expression select) {
        return FunctionUtils.twoArgFunc("TS_REWRITE", query, select, PostgreTsQueryType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  PostgreTsQueryType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-textsearch.html#TEXTSEARCH-FUNCTIONS-TABLE">tsquery_phrase ( query1 tsquery, query2 tsquery ) → tsquery</a>
     */
    public static SimpleExpression tsQueryPhrase(Expression query1, Expression query2) {
        return FunctionUtils.twoArgFunc("TSQUERY_PHRASE", query1, query2, PostgreTsQueryType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  PostgreTsQueryType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-textsearch.html#TEXTSEARCH-FUNCTIONS-TABLE">tsquery_phrase ( query1 tsquery, query2 tsquery, distance integer ) → tsquery</a>
     */
    public static SimpleExpression tsQueryPhrase(Expression query1, Expression query2, Expression distance) {
        return FunctionUtils.threeArgFunc("TSQUERY_PHRASE", query1, query2, distance, PostgreTsQueryType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  TextArrayType} with one dimension.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-textsearch.html#TEXTSEARCH-FUNCTIONS-TABLE">tsvector_to_array ( tsvector ) → text[]</a>
     */
    public static SimpleExpression tsVectorToArray(Expression tsVector) {
        return FunctionUtils.oneArgFunc("TSVECTOR_TO_ARRAY", tsVector, TextArrayType.from(String[].class));
    }

    /**
     * <p>
     * The {@link MappingType} of function returned fields type:<ol>
     * <li>lexeme {@link TextType}</li>
     * <li>positions {@link ShortArrayType} with one dimension</li>
     * <li>weights {@link TextType}</li>
     * <li>ordinality (this is optional) {@link LongType},see {@link _WithOrdinalityClause#withOrdinality()}</li>
     * </ol>
     * </p>
     * <p>
     * <pre>
     *          select * from unnest('cat:3 fat:2,4 rat:5A'::tsvector) →
     *
     *          lexeme | positions | weights
     *          --------+-----------+---------
     *          cat    | {3}       | {D}
     *          fat    | {2,4}     | {D,D}
     *          rat    | {5}       | {A}
     *     </pre>
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-textsearch.html#TEXTSEARCH-FUNCTIONS-TABLE">unnest ( tsvector ) → setof record ( lexeme text, positions smallint[], weights text ) <br/>
     * Expands a tsvector into a set of rows, one per lexeme
     * </a>
     */
    public static _TabularWithOrdinalityFunction unnest(final Expression exp) {
        final String name = "UNNEST";
        if (exp instanceof TypeInfer.DelayTypeInfer && ((TypeInfer.DelayTypeInfer) exp).isDelay()) {
            throw CriteriaUtils.tabularFuncErrorPosition(name);
        }
        final List<Selection> fieldList = _Collections.arrayList(3);

        fieldList.add(ArmySelections.forName("lexeme", TextType.INSTANCE));
        fieldList.add(ArmySelections.forName("positions", ShortArrayType.from(Short[].class)));
        fieldList.add(ArmySelections.forName("weights", TextType.INSTANCE));

        return FunctionUtils.oneArgTabularFunc(name, exp, fieldList);
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
     * </p>
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
     * </p>
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
     * </p>
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
     * </p>
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

        return FunctionUtils.twoArgTabularFunc("TS_PARSE", parserName, document, fieldList);
    }

    /**
     * <p>
     * The {@link MappingType} of function returned fields type:<ol>
     * <li>tokid {@link IntegerType}</li>
     * <li>alias {@link TextType}</li>
     * <li>description {@link TextType}</li>
     * <li>ordinality (this is optional) {@link LongType},see {@link _WithOrdinalityClause#withOrdinality()}</li>
     * </ol>
     * </p>
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

        return FunctionUtils.oneArgTabularFunc("TS_TOKEN_TYPE", exp, fieldList);
    }


    /**
     * <p>
     * The {@link MappingType} of function returned fields type:<ol>
     * <li>word {@link TextType}</li>
     * <li>ndoc {@link IntegerType}</li>
     * <li>nentry {@link IntegerType}</li>
     * <li>ordinality (this is optional) {@link LongType},see {@link _WithOrdinalityClause#withOrdinality()}</li>
     * </ol>
     * </p>
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
     * </p>
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
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-uuid.html">UUID Functions</a>
     */
    public static SimpleExpression genRandomUuid() {
        return FunctionUtils.zeroArgFunc("GEN_RANDOM_UUID", UUIDType.INSTANCE);
    }

    /*-------------------below XML Functions-------------------*/

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  XmlType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-xml.html#FUNCTIONS-PRODUCING-XML">xmlcomment ( text ) → xml</a>
     */
    public static SimpleExpression xmlComment(Expression exp) {
        return FunctionUtils.oneArgFunc("XMLCOMMENT", exp, XmlType.TEXT_INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  XmlType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-xml.html#FUNCTIONS-PRODUCING-XML">xmlconcat ( xml [, ...] ) → xml</a>
     */
    public static SimpleExpression xmlConcat(Expression xmls) {
        return FunctionUtils.oneOrMultiArgFunc("XMLCONCAT", xmls, XmlType.TEXT_INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  XmlType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-xml.html#FUNCTIONS-PRODUCING-XML">xmlconcat ( xml [, ...] ) → xml</a>
     */
    public static SimpleExpression xmlConcat(Expression xml1, Expression... xml2) {
        return FunctionUtils.oneAndRestFunc("XMLCONCAT", XmlType.TEXT_INSTANCE, xml1, xml2);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  XmlType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-xml.html#FUNCTIONS-PRODUCING-XML">xmlconcat ( xml [, ...] ) → xml</a>
     */
    public static SimpleExpression xmlConcat(List<Expression> xmlList) {
        return FunctionUtils.multiArgFunc("XMLCONCAT", xmlList, XmlType.TEXT_INSTANCE);
    }

    /**
     * <p>
     * <strong>Note:</strong>This function cannot exist independently,see {@link #xmlElement(PostgreSyntax.WordName, String, XmlAttributes, Expression...)}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-xml.html#FUNCTIONS-PRODUCING-XML">9.15.1.3. Xmlelement<br/>
     * xmlelement ( NAME name [, XMLATTRIBUTES ( attvalue [ AS attname ] [, ...] ) ] [, content [, ...]] ) → xml
     * </a>
     */
    public static XmlAttributes xmlAttributes(Consumer<Postgres._XmlNamedElementPart> consumer) {
        final PostgreFunctionUtils.XmlNamedElementPart<PostgreFunctionUtils.XmlAttributes> part;
        part = PostgreFunctionUtils.xmlAttributes();
        consumer.accept(part);
        return part.endNamedPart();
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  XmlType#TEXT_INSTANCE}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-xml.html#FUNCTIONS-PRODUCING-XML">Xmlforest<br/>
     * </a>
     */
    public SimpleExpression xmlForest(Consumer<Postgres._XmlNamedElementPart> consumer) {
        final PostgreFunctionUtils.XmlNamedElementPart<SimpleExpression> part;
        part = PostgreFunctionUtils.xmlForest();
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
    public static SimpleExpression xmlElement(PostgreSyntax.WordName wordName, String name, XmlAttributes attributes,
                                              Expression... contents) {
        ContextStack.assertNonNull(attributes);
        ContextStack.assertNonNull(contents);
        return _xmlElement(wordName, name, attributes, c -> {
            for (Expression content : contents) {
                c.accept(FuncWord.COMMA);
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
    public static SimpleExpression xmlElement(PostgreSyntax.WordName wordName, String name, Expression... contents) {
        ContextStack.assertNonNull(contents);
        return _xmlElement(wordName, name, null, c -> {
            for (Expression content : contents) {
                c.accept(FuncWord.COMMA);
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
    public static SimpleExpression xmlElement(PostgreSyntax.WordName wordName, String name, XmlAttributes attributes,
                                              List<Expression> contentList) {
        ContextStack.assertNonNull(attributes);
        return _xmlElement(wordName, name, attributes, c -> {
            for (Expression content : contentList) {
                c.accept(FuncWord.COMMA);
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
    public static SimpleExpression xmlElement(PostgreSyntax.WordName wordName, String name,
                                              List<Expression> contentList) {
        return _xmlElement(wordName, name, null, c -> {
            for (Expression content : contentList) {
                c.accept(FuncWord.COMMA);
                c.accept(content);
            }
        });
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  XmlType#TEXT_INSTANCE}
     * </p>
     *
     * @see #xmlPi(PostgreSyntax.WordName, String, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-xml.html#FUNCTIONS-PRODUCING-XML">xmlpi ( NAME name [, content ] ) → xml<br/>
     * </a>
     */
    public static SimpleExpression xmlPi(PostgreSyntax.WordName wordName, String name) {
        return _xmlPi(wordName, name, null);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  XmlType#TEXT_INSTANCE}
     * </p>
     *
     * @see #xmlPi(PostgreSyntax.WordName, String)
     * @see <a href="https://www.postgresql.org/docs/current/functions-xml.html#FUNCTIONS-PRODUCING-XML">xmlpi ( NAME name [, content ] ) → xml<br/>
     * </a>
     */
    public static SimpleExpression xmlPi(PostgreSyntax.WordName wordName, String name,
                                         BiFunction<MappingType, String, Expression> funcRef, String content) {
        final Expression contentExp;
        contentExp = funcRef.apply(StringType.INSTANCE, content);
        ContextStack.assertNonNull(contentExp);
        return _xmlPi(wordName, name, contentExp);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  XmlType#TEXT_INSTANCE}
     * </p>
     *
     * @see #xmlPi(PostgreSyntax.WordName, String)
     * @see <a href="https://www.postgresql.org/docs/current/functions-xml.html#FUNCTIONS-PRODUCING-XML">xmlpi ( NAME name [, content ] ) → xml<br/>
     * </a>
     */
    public static SimpleExpression xmlPi(PostgreSyntax.WordName wordName, String name, Expression content) {
        ContextStack.assertNonNull(content);
        return _xmlPi(wordName, name, content);
    }

    public static SimpleExpression xmlRoot(WordVersion version, Expression text,) {

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

        final String name = "TS_DEBUG";
        final _TabularWithOrdinalityFunction func;
        if (config == null) {
            func = FunctionUtils.oneArgTabularFunc(name, document, fieldList);
        } else {
            func = FunctionUtils.twoArgTabularFunc(name, config, document, fieldList);
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
     * </p>
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

        final String name = "TS_STAT";
        _TabularWithOrdinalityFunction func;
        if (weights == null) {
            func = FunctionUtils.oneArgTabularFunc(name, sqlQuery, fieldList);
        } else {
            func = FunctionUtils.twoArgTabularFunc(name, sqlQuery, weights, fieldList);
        }
        return func;
    }


    /**
     * @see #xmlElement(PostgreSyntax.WordName, String, XmlAttributes, Expression...)
     * @see #xmlElement(PostgreSyntax.WordName, String, Expression...)
     * @see #xmlElement(PostgreSyntax.WordName, String, List)
     * @see #xmlElement(PostgreSyntax.WordName, String, XmlAttributes, List)
     */
    private static SimpleExpression _xmlElement(final PostgreSyntax.WordName nameWord, final @Nullable String name,
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
            argList.add(FuncWord.COMMA);
            argList.add(attributes);
        }
        consumer.accept(argList::add);
        return FunctionUtils.complexArgFunc(funcName, argList, XmlType.TEXT_INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  XmlType#TEXT_INSTANCE}
     * </p>
     *
     * @see #xmlPi(PostgreSyntax.WordName, String)
     * @see #xmlPi(PostgreSyntax.WordName, String, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-xml.html#FUNCTIONS-PRODUCING-XML">xmlpi ( NAME name [, content ] ) → xml<br/>
     * </a>
     */
    private static SimpleExpression _xmlPi(PostgreSyntax.WordName wordName, String name, @Nullable Expression content) {
        final String funcName = "XMLPI";
        if (wordName != Postgres.NAME) {
            throw CriteriaUtils.funcArgError(funcName, wordName);
        } else if (!_DialectUtils.isSimpleIdentifier(name)) {
            throw CriteriaUtils.funcArgError(funcName, name);
        }
        final SimpleExpression func;
        if (content == null) {
            func = FunctionUtils.complexArgFunc(funcName, XmlType.TEXT_INSTANCE, wordName, name);
        } else {
            func = FunctionUtils.complexArgFunc(funcName, XmlType.TEXT_INSTANCE, wordName, name, FuncWord.COMMA, content);
        }
        return func;
    }


}
