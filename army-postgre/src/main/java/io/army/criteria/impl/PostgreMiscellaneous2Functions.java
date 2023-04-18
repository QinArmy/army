package io.army.criteria.impl;

import io.army.criteria.Expression;
import io.army.criteria.SimpleExpression;
import io.army.criteria.TypeInfer;
import io.army.mapping.*;
import io.army.mapping.postgre.PostgreTsQueryType;
import io.army.mapping.postgre.PostgreTsVectorType;
import io.army.mapping.postgre.TextArrayType;

import java.util.function.BiFunction;

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


}
