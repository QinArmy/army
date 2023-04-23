package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.dialect._DialectUtils;
import io.army.lang.Nullable;
import io.army.mapping.*;
import io.army.mapping.optional.ShortArrayType;
import io.army.mapping.optional.TextArrayType;
import io.army.mapping.optional.XmlArrayType;
import io.army.mapping.postgre.PostgreTsQueryType;
import io.army.mapping.postgre.PostgreTsVectorType;
import io.army.meta.TableMeta;
import io.army.sqltype.PgSqlType;
import io.army.util._Collections;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;

/**
 * <p>
 * This class hold tsvectory/ tsquery/xml /json/jsonb function method.
 * </p>
 *
 * @since 1.0
 */
abstract class PostgreDocumentFunctions extends PostgreMiscellaneous2Functions {

    /**
     * package constructor
     */
    PostgreDocumentFunctions() {
    }

    public interface XmlNameSpaces extends Item {

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
     * @see #xmlParse(DocumentValueOption, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/datatype-xml.html#id-1.5.7.21.6">XMLPARSE ( { DOCUMENT | CONTENT } value)<br/>
     * </a>
     */
    public static SimpleExpression xmlParse(final DocumentValueOption option, BiFunction<MappingType, String, Expression> funcRef, String value) {
        return xmlParse(option, funcRef.apply(TextType.INSTANCE, value));
    }

    /**
     * @param option {@link Postgres#DOCUMENT} or {@link Postgres#CONTENT}
     * @see <a href="https://www.postgresql.org/docs/current/datatype-xml.html#id-1.5.7.21.6">XMLPARSE ( { DOCUMENT | CONTENT } value)<br/>
     * </a>
     */
    public static SimpleExpression xmlParse(final DocumentValueOption option, final Expression value) {
        final String name = "XMLPARSE";
        if (!(option == Postgres.DOCUMENT || option == Postgres.CONTENT)) {
            throw CriteriaUtils.funcArgError(name, option);
        } else if (!(value instanceof FunctionArg.SingleFunctionArg)) {
            throw CriteriaUtils.funcArgError(name, value);
        }
        return FunctionUtils.complexArgFunc(name, XmlType.TEXT_INSTANCE, option, value);
    }

    /**
     * @param option {@link Postgres#DOCUMENT} or {@link Postgres#CONTENT}
     * @see <a href="https://www.postgresql.org/docs/current/datatype-xml.html#id-1.5.7.21.6">XMLSERIALIZE ( { DOCUMENT | CONTENT } value AS type )<br/>
     * </a>
     */
    public static SimpleExpression xmlSerialize(final DocumentValueOption option, final Expression value, final WordAs as,
                                                final MappingType type) {
        final String name = "XMLSERIALIZE";
        if (!(option == Postgres.DOCUMENT || option == Postgres.CONTENT)) {
            throw CriteriaUtils.funcArgError(name, option);
        } else if (!(value instanceof FunctionArg.SingleFunctionArg)) {
            throw CriteriaUtils.funcArgError(name, value);
        } else if (as != SQLs.AS) {
            throw CriteriaUtils.funcArgError(name, as);
        }
        return FunctionUtils.complexArgFunc(name, XmlType.TEXT_INSTANCE, option, value, as,
                NonOperationExpression.sqlTypeNameExp(type, PgSqlType.class)
        );
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  XmlType#TEXT_INSTANCE}
     * </p>
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

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  XmlType#TEXT_INSTANCE}
     * </p>
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
     * The {@link MappingType} of function return type: {@link  XmlType#TEXT_INSTANCE}
     * </p>
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
     * The {@link MappingType} of function return type: {@link  XmlType#TEXT_INSTANCE}
     * </p>
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
     * The {@link MappingType} of function return type: {@link  XmlType#TEXT_INSTANCE}
     * </p>
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
     * The {@link MappingType} of function return type: {@link  XmlType#TEXT_INSTANCE}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-xml.html#FUNCTIONS-PRODUCING-XML">xmlagg ( xml ) → xml<br/>
     * </a>
     */
    public static SimpleExpression xmlAgg(Expression xml) {
        return FunctionUtils.oneArgFunc("XMLAGG", xml, XmlType.TEXT_INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  XmlType#TEXT_INSTANCE}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-xml.html#FUNCTIONS-PRODUCING-XML">xmlagg ( xml ) → xml<br/>
     * </a>
     */
    public static SimpleExpression xmlAgg(Expression xml, Consumer<Statement._OrderByClause<Item>> consumer) {
        final FunctionUtils.OrderByOptionClause clause;
        clause = FunctionUtils.orderByOptionClause();
        consumer.accept(clause);
        clause.endOrderByClause();

        final SimpleExpression func;
        final String name = "XMLAGG";
        if (clause.orderByList().size() == 0) {
            func = FunctionUtils.oneArgFunc(name, xml, XmlType.TEXT_INSTANCE);
        } else {
            func = FunctionUtils.complexArgFunc(name, XmlType.TEXT_INSTANCE, xml, clause);
        }
        return func;
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link Boolean}
     * </p>
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
     * </p>
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
     * </p>
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
     * </p>
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
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-xml.html#FUNCTIONS-XML-PREDICATES">xml_is_well_formed ( text ) → boolean<br/>
     * </a>
     */
    public static SimplePredicate xmlIsWellFormed(Expression text) {
        return FunctionUtils.oneArgFuncPredicate("XML_IS_WELL_FORMED", text);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link Boolean}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-xml.html#FUNCTIONS-XML-PREDICATES">xml_is_well_formed_document ( text ) → boolean<br/>
     * </a>
     */
    public static SimplePredicate xmlIsWellFormedDocument(Expression text) {
        return FunctionUtils.oneArgFuncPredicate("XML_IS_WELL_FORMED_DOCUMENT", text);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link Boolean}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-xml.html#FUNCTIONS-XML-PREDICATES">xml_is_well_formed_content ( text ) → boolean<br/>
     * </a>
     */
    public static SimplePredicate xmlIsWellFormedContent(Expression text) {
        return FunctionUtils.oneArgFuncPredicate("XML_IS_WELL_FORMED_CONTENT", text);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link XmlArrayType#TEXT_LINEAR}
     * </p>
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
     * </p>
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
     * </p>
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
     * </p>
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
     * @see #xmlNamespaces(Expression, WordAs, String)
     * @see <a href="https://www.postgresql.org/docs/current/functions-xml.html#FUNCTIONS-XML-PROCESSING">XMLNAMESPACES ( namespace_uri AS namespace_name [, ...] )<br/>
     * </a>
     */
    public static XmlNameSpaces xmlNamespaces(BiFunction<MappingType, String, Expression> funcRef, String namespaceUri, WordAs as, String namespaceName) {
        return xmlNamespaces(funcRef.apply(TextType.INSTANCE, namespaceUri), as, namespaceName);
    }


    /**
     * @param namespaceUri  a text expression
     * @param as            see {@link SQLs#AS}
     * @param namespaceName a simple identifier
     * @see #xmlNamespaces(BiFunction, String, WordAs, String)
     * @see #xmlNamespaces(Consumer)
     * @see <a href="https://www.postgresql.org/docs/current/functions-xml.html#FUNCTIONS-XML-PROCESSING">XMLNAMESPACES ( namespace_uri AS namespace_name [, ...] )<br/>
     * </a>
     */
    public static XmlNameSpaces xmlNamespaces(Expression namespaceUri, WordAs as, String namespaceName) {
        final PostgreFunctionUtils.XmlNamedElementPart<XmlNameSpaces> clause;
        clause = PostgreFunctionUtils.xmlNamespaces();
        clause.accept(namespaceUri, as, namespaceName);
        return clause.endNamedPart();
    }


    /**
     * @see #xmlNamespaces(BiFunction, String, WordAs, String)
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
     * @param table will output strings identifying tables using the usual notation, including optional schema qualification and double quotes
     * @see #tableToXml(Expression, Expression, Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-xml.html#FUNCTIONS-XML-MAPPING">table_to_xml</a>
     */
    public static SimpleExpression tableToXml(TableMeta<?> table, Expression nulls, Expression tableForest,
                                              Expression targetNs) {
        return tableToXml(PostgreFunctionUtils.tableNameExp(table), nulls, tableForest, targetNs);
    }

    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-xml.html#FUNCTIONS-XML-MAPPING">table_to_xml</a>
     */
    public static SimpleExpression tableToXml(Expression table, Expression nulls, Expression tableForest,
                                              Expression targetNs) {
        return FunctionUtils.fourArgFunc("TABLE_TO_XML", table, nulls, tableForest, targetNs, XmlType.TEXT_INSTANCE);
    }

    /**
     * @param query will output literal sql
     * @see <a href="https://www.postgresql.org/docs/current/functions-xml.html#FUNCTIONS-XML-MAPPING">query_to_xml</a>
     */
    public static SimpleExpression queryToXml(Select query, Visible visible, Expression nulls, Expression tableForest,
                                              Expression targetNs) {
        return queryToXml(PostgreFunctionUtils.queryStringExp(query, visible), nulls, tableForest, targetNs);
    }

    /**
     * @param query will output literal sql
     * @see <a href="https://www.postgresql.org/docs/current/functions-xml.html#FUNCTIONS-XML-MAPPING">query_to_xml</a>
     */
    public static SimpleExpression queryToXml(Select query, Expression nulls, Expression tableForest,
                                              Expression targetNs) {
        return queryToXml(PostgreFunctionUtils.queryStringExp(query, Visible.ONLY_VISIBLE), nulls, tableForest, targetNs);
    }

    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-xml.html#FUNCTIONS-XML-MAPPING">query_to_xml</a>
     */
    public static SimpleExpression queryToXml(Expression query, Expression nulls, Expression tableForest,
                                              Expression targetNs) {
        return FunctionUtils.fourArgFunc("QUERY_TO_XML", query, nulls, tableForest, targetNs, XmlType.TEXT_INSTANCE);
    }

    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-xml.html#FUNCTIONS-XML-MAPPING">cursor_to_xml</a>
     */
    public static SimpleExpression cursorToXml(Expression cursor, Expression nulls, Expression tableForest,
                                               Expression targetNs) {
        return FunctionUtils.fourArgFunc("CURSOR_TO_XML", cursor, nulls, tableForest, targetNs, XmlType.TEXT_INSTANCE);
    }


    /**
     * @param table will output strings identifying tables using the usual notation, including optional schema qualification and double quotes
     * @see #tableToXmlSchema(Expression, Expression, Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-xml.html#FUNCTIONS-XML-MAPPING">table_to_xmlschema</a>
     */
    public static SimpleExpression tableToXmlSchema(TableMeta<?> table, Expression nulls, Expression tableForest,
                                                    Expression targetNs) {
        return tableToXmlSchema(PostgreFunctionUtils.tableNameExp(table), nulls, tableForest, targetNs);
    }

    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-xml.html#FUNCTIONS-XML-MAPPING">table_to_xmlschema</a>
     */
    public static SimpleExpression tableToXmlSchema(Expression table, Expression nulls, Expression tableForest,
                                                    Expression targetNs) {
        return FunctionUtils.fourArgFunc("TABLE_TO_XMLSCHEMA", table, nulls, tableForest, targetNs, XmlType.TEXT_INSTANCE);
    }

    /**
     * @param query will output literal sql
     * @see <a href="https://www.postgresql.org/docs/current/functions-xml.html#FUNCTIONS-XML-MAPPING">query_to_xmlschema</a>
     */
    public static SimpleExpression queryToXmlSchema(Select query, Visible visible, Expression nulls, Expression tableForest,
                                                    Expression targetNs) {
        return queryToXmlSchema(PostgreFunctionUtils.queryStringExp(query, visible), nulls, tableForest, targetNs);
    }

    /**
     * @param query will output literal sql
     * @see <a href="https://www.postgresql.org/docs/current/functions-xml.html#FUNCTIONS-XML-MAPPING">query_to_xmlschema</a>
     */
    public static SimpleExpression queryToXmlSchema(Select query, Expression nulls, Expression tableForest,
                                                    Expression targetNs) {
        return queryToXmlSchema(PostgreFunctionUtils.queryStringExp(query, Visible.ONLY_VISIBLE), nulls, tableForest,
                targetNs);
    }


    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-xml.html#FUNCTIONS-XML-MAPPING">query_to_xmlschema</a>
     */
    public static SimpleExpression queryToXmlSchema(Expression query, Expression nulls, Expression tableForest,
                                                    Expression targetNs) {
        return FunctionUtils.fourArgFunc("QUERY_TO_XMLSCHEMA", query, nulls, tableForest, targetNs, XmlType.TEXT_INSTANCE);
    }

    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-xml.html#FUNCTIONS-XML-MAPPING">cursor_to_xmlschema</a>
     */
    public static SimpleExpression cursorToXmlSchema(Expression cursor, Expression nulls, Expression tableForest,
                                                     Expression targetNs) {
        return FunctionUtils.fourArgFunc("CURSOR_TO_XMLSCHEMA", cursor, nulls, tableForest, targetNs, XmlType.TEXT_INSTANCE);
    }


    /**
     * @param table will output strings identifying tables using the usual notation, including optional schema qualification and double quotes
     * @see #tableToXmlAndXmlSchema(Expression, Expression, Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-xml.html#FUNCTIONS-XML-MAPPING">table_to_xml_and_xmlschema</a>
     */
    public static SimpleExpression tableToXmlAndXmlSchema(TableMeta<?> table, Expression nulls, Expression tableForest,
                                                          Expression targetNs) {
        return tableToXmlAndXmlSchema(PostgreFunctionUtils.tableNameExp(table), nulls, tableForest, targetNs);
    }

    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-xml.html#FUNCTIONS-XML-MAPPING">table_to_xml_and_xmlschema</a>
     */
    public static SimpleExpression tableToXmlAndXmlSchema(Expression table, Expression nulls, Expression tableForest,
                                                          Expression targetNs) {
        return FunctionUtils.fourArgFunc("TABLE_TO_XML_AND_XMLSCHEMA", table, nulls, tableForest, targetNs, XmlType.TEXT_INSTANCE);
    }

    /**
     * @param query will output literal sql
     * @see <a href="https://www.postgresql.org/docs/current/functions-xml.html#FUNCTIONS-XML-MAPPING">query_to_xml_and_xmlschema</a>
     */
    public static SimpleExpression queryToXmlAndXmlSchema(Select query, Visible visible, Expression nulls, Expression tableForest,
                                                          Expression targetNs) {
        return queryToXmlAndXmlSchema(PostgreFunctionUtils.queryStringExp(query, visible), nulls, tableForest, targetNs);
    }

    /**
     * @param query will output literal sql
     * @see <a href="https://www.postgresql.org/docs/current/functions-xml.html#FUNCTIONS-XML-MAPPING">query_to_xml_and_xmlschema</a>
     */
    public static SimpleExpression queryToXmlAndXmlSchema(Select query, Expression nulls, Expression tableForest,
                                                          Expression targetNs) {
        return queryToXmlAndXmlSchema(PostgreFunctionUtils.queryStringExp(query, Visible.ONLY_VISIBLE), nulls, tableForest, targetNs);
    }

    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-xml.html#FUNCTIONS-XML-MAPPING">query_to_xml_and_xmlschema</a>
     */
    public static SimpleExpression queryToXmlAndXmlSchema(Expression query, Expression nulls, Expression tableForest,
                                                          Expression targetNs) {
        return FunctionUtils.fourArgFunc("QUERY_TO_XML_AND_XMLSCHEMA", query, nulls, tableForest, targetNs, XmlType.TEXT_INSTANCE);
    }


    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-xml.html#FUNCTIONS-XML-MAPPING">schema_to_xml</a>
     */
    public static SimpleExpression schemaToXml(Expression schema, Expression nulls, Expression tableForest,
                                               Expression targetNs) {
        return FunctionUtils.fourArgFunc("SCHEMA_TO_XML", schema, nulls, tableForest, targetNs, XmlType.TEXT_INSTANCE);
    }

    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-xml.html#FUNCTIONS-XML-MAPPING">schema_to_xmlschema</a>
     */
    public static SimpleExpression schemaToXmlSchema(Expression schema, Expression nulls, Expression tableForest,
                                                     Expression targetNs) {
        return FunctionUtils.fourArgFunc("SCHEMA_TO_XMLSCHEMA", schema, nulls, tableForest, targetNs, XmlType.TEXT_INSTANCE);
    }

    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-xml.html#FUNCTIONS-XML-MAPPING">schema_to_xml_and_xmlschema</a>
     */
    public static SimpleExpression schemaToXmlAndXmlSchema(Expression schema, Expression nulls, Expression tableForest,
                                                           Expression targetNs) {
        return FunctionUtils.fourArgFunc("SCHEMA_TO_XML_AND_XMLSCHEMA", schema, nulls, tableForest, targetNs,
                XmlType.TEXT_INSTANCE);
    }

    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-xml.html#FUNCTIONS-XML-MAPPING">database_to_xml</a>
     */
    public static SimpleExpression databaseToXml(Expression nulls, Expression tableForest, Expression targetNs) {
        return FunctionUtils.threeArgFunc("DATABASE_TO_XML", nulls, tableForest, targetNs, XmlType.TEXT_INSTANCE);
    }

    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-xml.html#FUNCTIONS-XML-MAPPING">database_to_xmlschema</a>
     */
    public static SimpleExpression databaseToXmlSchema(Expression nulls, Expression tableForest, Expression targetNs) {
        return FunctionUtils.threeArgFunc("DATABASE_TO_XMLSCHEMA", nulls, tableForest, targetNs, XmlType.TEXT_INSTANCE);
    }

    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-xml.html#FUNCTIONS-XML-MAPPING">database_to_xml_and_xmlschema</a>
     */
    public static SimpleExpression databaseToXmlAndXmlSchema(Expression nulls, Expression tableForest,
                                                             Expression targetNs) {
        return FunctionUtils.threeArgFunc("DATABASE_TO_XML_AND_XMLSCHEMA", nulls, tableForest, targetNs,
                XmlType.TEXT_INSTANCE);
    }


    /*-------------------below private method -------------------*/

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

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  XmlType#TEXT_INSTANCE}
     * </p>
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
            func = FunctionUtils.complexArgFunc(name, XmlType.TEXT_INSTANCE, xml, FuncWord.COMMA, version,
                    textOrNoValue);
        } else {
            func = FunctionUtils.complexArgFunc(name, XmlType.TEXT_INSTANCE, xml, FuncWord.COMMA, version,
                    textOrNoValue, FuncWord.COMMA, standalone, option);
        }
        return func;
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link Boolean}
     * </p>
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
            argList.add(FuncWord.COMMA);
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


}
