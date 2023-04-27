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
import io.army.util._ArrayUtils;
import io.army.util._Collections;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.BiConsumer;
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
    public static <T, U> SimpleExpression jsonToTsVector(BiFunction<MappingType, T, Expression> funcRefForDocument, T document,
                                                         BiFunction<MappingType, U, Expression> funcRefForFilter, U filter) {
        return jsonToTsVector(funcRefForDocument.apply(JsonType.TEXT, document),
                funcRefForFilter.apply(JsonbType.TEXT, filter)
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
    public static <T, U, V> SimpleExpression jsonToTsVector(BiFunction<MappingType, T, Expression> funcRefForConfig, T config,
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
    public static <T, U> SimpleExpression jsonbToTsVector(BiFunction<MappingType, T, Expression> funcRefForDocument, T document,
                                                          BiFunction<MappingType, U, Expression> funcRefForFilter, U filter) {
        return jsonbToTsVector(funcRefForDocument.apply(JsonType.TEXT, document),
                funcRefForFilter.apply(JsonbType.TEXT, filter)
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
    public static <T, U, V> SimpleExpression jsonbToTsVector(BiFunction<MappingType, T, Expression> funcRefForConfig, T config,
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

        return DialectFunctionUtils.oneArgTabularFunc(name, exp, fieldList);
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
     * <p>
     * The {@link MappingType} of function return type: {@link XmlArrayType#TEXT_LINEAR}
     * </p>
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
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-xml.html#FUNCTIONS-XML-MAPPING">table_to_xml</a>
     */
    public static SimpleExpression tableToXml(Expression table, Expression nulls, Expression tableForest,
                                              Expression targetNs) {
        return FunctionUtils.fourArgFunc("TABLE_TO_XML", table, nulls, tableForest, targetNs, XmlType.TEXT_INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link XmlArrayType#TEXT_LINEAR}
     * </p>
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
     * </p>
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
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-xml.html#FUNCTIONS-XML-MAPPING">query_to_xml</a>
     */
    public static SimpleExpression queryToXml(Expression query, Expression nulls, Expression tableForest,
                                              Expression targetNs) {
        return FunctionUtils.fourArgFunc("QUERY_TO_XML", query, nulls, tableForest, targetNs, XmlType.TEXT_INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link XmlArrayType#TEXT_LINEAR}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-xml.html#FUNCTIONS-XML-MAPPING">cursor_to_xml</a>
     */
    public static SimpleExpression cursorToXml(Expression cursor, Expression nulls, Expression tableForest,
                                               Expression targetNs) {
        return FunctionUtils.fourArgFunc("CURSOR_TO_XML", cursor, nulls, tableForest, targetNs, XmlType.TEXT_INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link XmlArrayType#TEXT_LINEAR}
     * </p>
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
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-xml.html#FUNCTIONS-XML-MAPPING">table_to_xmlschema</a>
     */
    public static SimpleExpression tableToXmlSchema(Expression table, Expression nulls, Expression tableForest,
                                                    Expression targetNs) {
        return FunctionUtils.fourArgFunc("TABLE_TO_XMLSCHEMA", table, nulls, tableForest, targetNs, XmlType.TEXT_INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link XmlArrayType#TEXT_LINEAR}
     * </p>
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
     * </p>
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
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-xml.html#FUNCTIONS-XML-MAPPING">query_to_xmlschema</a>
     */
    public static SimpleExpression queryToXmlSchema(Expression query, Expression nulls, Expression tableForest,
                                                    Expression targetNs) {
        return FunctionUtils.fourArgFunc("QUERY_TO_XMLSCHEMA", query, nulls, tableForest, targetNs, XmlType.TEXT_INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link XmlArrayType#TEXT_LINEAR}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-xml.html#FUNCTIONS-XML-MAPPING">cursor_to_xmlschema</a>
     */
    public static SimpleExpression cursorToXmlSchema(Expression cursor, Expression nulls, Expression tableForest,
                                                     Expression targetNs) {
        return FunctionUtils.fourArgFunc("CURSOR_TO_XMLSCHEMA", cursor, nulls, tableForest, targetNs, XmlType.TEXT_INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link XmlArrayType#TEXT_LINEAR}
     * </p>
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
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-xml.html#FUNCTIONS-XML-MAPPING">table_to_xml_and_xmlschema</a>
     */
    public static SimpleExpression tableToXmlAndXmlSchema(Expression table, Expression nulls, Expression tableForest,
                                                          Expression targetNs) {
        return FunctionUtils.fourArgFunc("TABLE_TO_XML_AND_XMLSCHEMA", table, nulls, tableForest, targetNs, XmlType.TEXT_INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link XmlArrayType#TEXT_LINEAR}
     * </p>
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
     * </p>
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
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-xml.html#FUNCTIONS-XML-MAPPING">query_to_xml_and_xmlschema</a>
     */
    public static SimpleExpression queryToXmlAndXmlSchema(Expression query, Expression nulls, Expression tableForest,
                                                          Expression targetNs) {
        return FunctionUtils.fourArgFunc("QUERY_TO_XML_AND_XMLSCHEMA", query, nulls, tableForest, targetNs, XmlType.TEXT_INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link XmlArrayType#TEXT_LINEAR}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-xml.html#FUNCTIONS-XML-MAPPING">schema_to_xml</a>
     */
    public static SimpleExpression schemaToXml(Expression schema, Expression nulls, Expression tableForest,
                                               Expression targetNs) {
        return FunctionUtils.fourArgFunc("SCHEMA_TO_XML", schema, nulls, tableForest, targetNs, XmlType.TEXT_INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link XmlArrayType#TEXT_LINEAR}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-xml.html#FUNCTIONS-XML-MAPPING">schema_to_xmlschema</a>
     */
    public static SimpleExpression schemaToXmlSchema(Expression schema, Expression nulls, Expression tableForest,
                                                     Expression targetNs) {
        return FunctionUtils.fourArgFunc("SCHEMA_TO_XMLSCHEMA", schema, nulls, tableForest, targetNs, XmlType.TEXT_INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link XmlArrayType#TEXT_LINEAR}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-xml.html#FUNCTIONS-XML-MAPPING">schema_to_xml_and_xmlschema</a>
     */
    public static SimpleExpression schemaToXmlAndXmlSchema(Expression schema, Expression nulls, Expression tableForest,
                                                           Expression targetNs) {
        return FunctionUtils.fourArgFunc("SCHEMA_TO_XML_AND_XMLSCHEMA", schema, nulls, tableForest, targetNs,
                XmlType.TEXT_INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link XmlArrayType#TEXT_LINEAR}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-xml.html#FUNCTIONS-XML-MAPPING">database_to_xml</a>
     */
    public static SimpleExpression databaseToXml(Expression nulls, Expression tableForest, Expression targetNs) {
        return FunctionUtils.threeArgFunc("DATABASE_TO_XML", nulls, tableForest, targetNs, XmlType.TEXT_INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link XmlArrayType#TEXT_LINEAR}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-xml.html#FUNCTIONS-XML-MAPPING">database_to_xmlschema</a>
     */
    public static SimpleExpression databaseToXmlSchema(Expression nulls, Expression tableForest, Expression targetNs) {
        return FunctionUtils.threeArgFunc("DATABASE_TO_XMLSCHEMA", nulls, tableForest, targetNs, XmlType.TEXT_INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link XmlArrayType#TEXT_LINEAR}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-xml.html#FUNCTIONS-XML-MAPPING">database_to_xml_and_xmlschema</a>
     */
    public static SimpleExpression databaseToXmlAndXmlSchema(Expression nulls, Expression tableForest,
                                                             Expression targetNs) {
        return FunctionUtils.threeArgFunc("DATABASE_TO_XML_AND_XMLSCHEMA", nulls, tableForest, targetNs,
                XmlType.TEXT_INSTANCE);
    }

    /*-------------------below JSON function -------------------*/

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonType#TEXT}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-CREATION-TABLE">to_json ( anyelement ) → json</a>
     */
    public static SimpleExpression toJson(Expression exp) {
        return FunctionUtils.oneArgFunc("TO_JSON", exp, JsonType.TEXT);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonbType#TEXT}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-CREATION-TABLE">to_jsonb ( anyelement ) → jsonb</a>
     */
    public static SimpleExpression toJsonb(Expression exp) {
        return FunctionUtils.oneArgFunc("TO_JSONB", exp, JsonbType.TEXT);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonType#TEXT}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-CREATION-TABLE">array_to_json ( anyarray [, boolean ] ) → json<br/>
     * Converts an SQL array to a JSON array. The behavior is the same as to_json except that line feeds will be added between top-level array elements if the optional boolean parameter is true.
     * </a>
     */
    public static SimpleExpression arrayToJson(Expression array, Expression lineFeed) {
        return FunctionUtils.twoArgFunc("ARRAY_TO_JSON", array, lineFeed, JsonType.TEXT);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonType#TEXT}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-CREATION-TABLE">row_to_json ( record [, boolean ] ) → json<br/>
     * Converts an SQL composite value to a JSON object. The behavior is the same as to_json except that line feeds will be added between top-level elements if the optional boolean parameter is true.
     * </a>
     */
    public static SimpleExpression rowToJson(Expression array, Expression lineFeed) {
        return FunctionUtils.twoArgFunc("ROW_TO_JSON", array, lineFeed, JsonType.TEXT);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonType#TEXT}
     * </p>
     *
     * @param rest here,<ul>
     *             <li>{@link SQLs#multiParam(TypeInfer, Collection)}</li>
     *             <li>{@link SQLs#multiLiteral(TypeInfer, Collection)}</li>
     *             <li>{@link SQLs#namedMultiParam(TypeInfer, String, int)}</li>
     *             <li>{@link SQLs#namedMultiLiteral(TypeInfer, String, int)}</li>
     *             </ul> is allowed.
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-CREATION-TABLE">json_build_array ( VARIADIC "any" ) → json<br/>
     * Builds a possibly-heterogeneously-typed JSON array out of a variadic argument list. Each argument is converted as per to_json or to_jsonb.
     * </a>
     */
    public static SimpleExpression jsonBuildArray(Expression first, Expression... rest) {
        return FunctionUtils.varargsFunc("JSON_BUILD_ARRAY", JsonType.TEXT, first, rest);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonType#TEXT}
     * </p>
     *
     * @param consumer here,<ul>
     *                 <li>{@link SQLs#multiParam(TypeInfer, Collection)}</li>
     *                 <li>{@link SQLs#multiLiteral(TypeInfer, Collection)}</li>
     *                 <li>{@link SQLs#namedMultiParam(TypeInfer, String, int)}</li>
     *                 <li>{@link SQLs#namedMultiLiteral(TypeInfer, String, int)}</li>
     *                 </ul> is allowed.
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-CREATION-TABLE">json_build_array ( VARIADIC "any" ) → json<br/>
     * Builds a possibly-heterogeneously-typed JSON array out of a variadic argument list. Each argument is converted as per to_json or to_jsonb.
     * </a>
     */
    public static SimpleExpression jsonBuildArray(Consumer<Consumer<Expression>> consumer) {
        return FunctionUtils.varargsFunc("JSON_BUILD_ARRAY", consumer, JsonType.TEXT);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonbType#TEXT}
     * </p>
     *
     * @param rest here,<ul>
     *             <li>{@link SQLs#multiParam(TypeInfer, Collection)}</li>
     *             <li>{@link SQLs#multiLiteral(TypeInfer, Collection)}</li>
     *             <li>{@link SQLs#namedMultiParam(TypeInfer, String, int)}</li>
     *             <li>{@link SQLs#namedMultiLiteral(TypeInfer, String, int)}</li>
     *             </ul> is allowed.
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-CREATION-TABLE">jsonb_build_array ( VARIADIC "any" ) → json<br/>
     * Builds a possibly-heterogeneously-typed JSON array out of a variadic argument list. Each argument is converted as per to_json or to_jsonb.
     * </a>
     */
    public static SimpleExpression jsonbBuildArray(Expression first, Expression... rest) {
        return FunctionUtils.varargsFunc("JSONB_BUILD_ARRAY", JsonbType.TEXT, first, rest);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonbType#TEXT}
     * </p>
     *
     * @param consumer here,<ul>
     *                 <li>{@link SQLs#multiParam(TypeInfer, Collection)}</li>
     *                 <li>{@link SQLs#multiLiteral(TypeInfer, Collection)}</li>
     *                 <li>{@link SQLs#namedMultiParam(TypeInfer, String, int)}</li>
     *                 <li>{@link SQLs#namedMultiLiteral(TypeInfer, String, int)}</li>
     *                 </ul> is allowed.
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-CREATION-TABLE">jsonb_build_array ( VARIADIC "any" ) → json<br/>
     * Builds a possibly-heterogeneously-typed JSON array out of a variadic argument list. Each argument is converted as per to_json or to_jsonb.
     * </a>
     */
    public static SimpleExpression jsonbBuildArray(Consumer<Consumer<Expression>> consumer) {
        return FunctionUtils.varargsFunc("JSONB_BUILD_ARRAY", consumer, JsonbType.TEXT);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonType#TEXT}
     * </p>
     *
     * @param first here,<ul>
     *              <li>{@link SQLs#multiParam(TypeInfer, Collection)}</li>
     *              <li>{@link SQLs#multiLiteral(TypeInfer, Collection)}</li>
     *              <li>{@link SQLs#namedMultiParam(TypeInfer, String, int)}</li>
     *              <li>{@link SQLs#namedMultiLiteral(TypeInfer, String, int)}</li>
     *              </ul> is allowed.
     * @param rest  here,<ul>
     *              <li>{@link SQLs#multiParam(TypeInfer, Collection)}</li>
     *              <li>{@link SQLs#multiLiteral(TypeInfer, Collection)}</li>
     *              <li>{@link SQLs#namedMultiParam(TypeInfer, String, int)}</li>
     *              <li>{@link SQLs#namedMultiLiteral(TypeInfer, String, int)}</li>
     *              </ul> is allowed.
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-CREATION-TABLE">json_build_object ( VARIADIC "any" ) → json<br/>
     * Builds a possibly-heterogeneously-typed JSON array out of a variadic argument list. Each argument is converted as per to_json or to_jsonb.
     * </a>
     */
    public static SimpleExpression jsonBuildObject(Expression first, Expression... rest) {
        return FunctionUtils.varargsFunc("JSON_BUILD_OBJECT", JsonType.TEXT, first, rest);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonType#TEXT}
     * </p>
     *
     * @param consumer here,<ul>
     *                 <li>{@link SQLs#multiParam(TypeInfer, Collection)}</li>
     *                 <li>{@link SQLs#multiLiteral(TypeInfer, Collection)}</li>
     *                 <li>{@link SQLs#namedMultiParam(TypeInfer, String, int)}</li>
     *                 <li>{@link SQLs#namedMultiLiteral(TypeInfer, String, int)}</li>
     *                 </ul> is allowed.
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-CREATION-TABLE">json_build_object ( VARIADIC "any" ) → json<br/>
     * Builds a possibly-heterogeneously-typed JSON array out of a variadic argument list. Each argument is converted as per to_json or to_jsonb.
     * </a>
     */
    public static SimpleExpression jsonBuildObject(Consumer<Consumer<Expression>> consumer) {
        return FunctionUtils.varargsFunc("JSON_BUILD_OBJECT", consumer, JsonType.TEXT);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonbType#TEXT}
     * </p>
     *
     * @param first here,<ul>
     *              <li>{@link SQLs#multiParam(TypeInfer, Collection)}</li>
     *              <li>{@link SQLs#multiLiteral(TypeInfer, Collection)}</li>
     *              <li>{@link SQLs#namedMultiParam(TypeInfer, String, int)}</li>
     *              <li>{@link SQLs#namedMultiLiteral(TypeInfer, String, int)}</li>
     *              </ul> is allowed.
     * @param rest  here,<ul>
     *              <li>{@link SQLs#multiParam(TypeInfer, Collection)}</li>
     *              <li>{@link SQLs#multiLiteral(TypeInfer, Collection)}</li>
     *              <li>{@link SQLs#namedMultiParam(TypeInfer, String, int)}</li>
     *              <li>{@link SQLs#namedMultiLiteral(TypeInfer, String, int)}</li>
     *              </ul> is allowed.
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-CREATION-TABLE">jsonb_build_object ( VARIADIC "any" ) → jsonb<br/>
     * Builds a possibly-heterogeneously-typed JSON array out of a variadic argument list. Each argument is converted as per to_json or to_jsonb.
     * </a>
     */
    public static SimpleExpression jsonbBuildObject(Expression first, Expression... rest) {
        return FunctionUtils.varargsFunc("JSONB_BUILD_OBJECT", JsonbType.TEXT, first, rest);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonbType#TEXT}
     * </p>
     *
     * @param consumer here,<ul>
     *                 <li>{@link SQLs#multiParam(TypeInfer, Collection)}</li>
     *                 <li>{@link SQLs#multiLiteral(TypeInfer, Collection)}</li>
     *                 <li>{@link SQLs#namedMultiParam(TypeInfer, String, int)}</li>
     *                 <li>{@link SQLs#namedMultiLiteral(TypeInfer, String, int)}</li>
     *                 </ul> is allowed.
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-CREATION-TABLE">jsonb_build_array ( VARIADIC "any" ) → json<br/>
     * Builds a possibly-heterogeneously-typed JSON array out of a variadic argument list. Each argument is converted as per to_json or to_jsonb.
     * </a>
     */
    public static SimpleExpression jsonbBuildObject(Consumer<Consumer<Expression>> consumer) {
        return FunctionUtils.varargsFunc("JSONB_BUILD_OBJECT", consumer, JsonbType.TEXT);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonType#TEXT}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-CREATION-TABLE">json_object ( text[] ) → json<br/>
     * Builds a JSON object out of a text array. The array must have either exactly one dimension with an even number of members, in which case they are taken<br/>
     * as alternating key/value pairs, or two dimensions such that each inner array has exactly two elements, which are taken as a key/value pair. All values are<br/>
     * converted to JSON strings.
     * </a>
     */
    public static SimpleExpression jsonObject(Expression pairArray) {
        return FunctionUtils.oneArgFunc("JSON_OBJECT", pairArray, JsonType.TEXT);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonType#TEXT}
     * </p>
     *
     * @param funcRef the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                <ul>
     *                    <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                    <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                    <li>developer custom method</li>
     *                </ul>.
     *                The first argument of funcRef always is {@link TextArrayType#LIST}.
     * @param map     non-null and non-empty,it will be wrapped to unmodified list that will be  passed to funcRef as the second argument of funcRef
     * @see #jsonObject(Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-CREATION-TABLE">json_object ( text[] ) → json<br/>
     * Builds a JSON object out of a text array. The array must have either exactly one dimension with an even number of members, in which case they are taken<br/>
     * as alternating key/value pairs, or two dimensions such that each inner array has exactly two elements, which are taken as a key/value pair. All values are<br/>
     * converted to JSON strings.
     * </a>
     */
    public static SimpleExpression jsonObject(final BiFunction<MappingType, List<String>, Expression> funcRef,
                                              final Map<String, String> map) {
        final String name = "JSON_OBJECT";
        return FunctionUtils.oneArgFunc(name, funcRef.apply(TextArrayType.LIST, FunctionUtils.pairMapToList(name, map)),
                JsonType.TEXT);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonType#TEXT}
     * </p>
     *
     * @param funcRef the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                <ul>
     *                    <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                    <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                    <li>developer custom method</li>
     *                </ul>.
     *                The first argument of funcRef always is {@link TextArrayType#LIST}.
     * @see #jsonObject(Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-CREATION-TABLE">json_object ( text[] ) → json<br/>
     * Builds a JSON object out of a text array. The array must have either exactly one dimension with an even number of members, in which case they are taken<br/>
     * as alternating key/value pairs, or two dimensions such that each inner array has exactly two elements, which are taken as a key/value pair. All values are<br/>
     * converted to JSON strings.
     * </a>
     */
    public static SimpleExpression jsonObject(final BiFunction<MappingType, List<String>, Expression> funcRef,
                                              final Consumer<BiConsumer<String, String>> consumer) {
        return jsonObject(funcRef.apply(TextArrayType.LIST, FunctionUtils.pairConsumerToList(consumer)));
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonbType#TEXT}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-CREATION-TABLE">jsonb_object ( text[] ) → json<br/>
     * Builds a JSON object out of a text array. The array must have either exactly one dimension with an even number of members, in which case they are taken<br/>
     * as alternating key/value pairs, or two dimensions such that each inner array has exactly two elements, which are taken as a key/value pair. All values are<br/>
     * converted to JSON strings.
     * </a>
     */
    public static SimpleExpression jsonbObject(Expression pairArray) {
        return FunctionUtils.oneArgFunc("JSONB_OBJECT", pairArray, JsonbType.TEXT);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonbType#TEXT}
     * </p>
     *
     * @param funcRef the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                <ul>
     *                    <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                    <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                    <li>developer custom method</li>
     *                </ul>.
     *                The first argument of funcRef always is {@link TextArrayType#LIST}.
     * @param map     non-null and non-empty,it will be wrapped to unmodified list that will be  passed to funcRef as the second argument of funcRef
     * @see #jsonbObject(Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-CREATION-TABLE">jsonb_object ( text[] ) → json<br/>
     * Builds a JSON object out of a text array. The array must have either exactly one dimension with an even number of members, in which case they are taken<br/>
     * as alternating key/value pairs, or two dimensions such that each inner array has exactly two elements, which are taken as a key/value pair. All values are<br/>
     * converted to JSON strings.
     * </a>
     */
    public static SimpleExpression jsonbObject(final BiFunction<MappingType, List<String>, Expression> funcRef,
                                               final Map<String, String> map) {
        final String name = "JSONB_OBJECT";
        return FunctionUtils.oneArgFunc(name, funcRef.apply(TextArrayType.LIST, FunctionUtils.pairMapToList(name, map)),
                JsonbType.TEXT);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonbType#TEXT}
     * </p>
     *
     * @param funcRef the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                <ul>
     *                    <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                    <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                    <li>developer custom method</li>
     *                </ul>.
     *                The first argument of funcRef always is {@link TextArrayType#LIST}.
     * @see #jsonbObject(Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-CREATION-TABLE">jsonb_object ( text[] ) → json<br/>
     * Builds a JSON object out of a text array. The array must have either exactly one dimension with an even number of members, in which case they are taken<br/>
     * as alternating key/value pairs, or two dimensions such that each inner array has exactly two elements, which are taken as a key/value pair. All values are<br/>
     * converted to JSON strings.
     * </a>
     */
    public static SimpleExpression jsonbObject(final BiFunction<MappingType, List<String>, Expression> funcRef,
                                               final Consumer<BiConsumer<String, String>> consumer) {
        return jsonbObject(funcRef.apply(TextArrayType.LIST, FunctionUtils.pairConsumerToList(consumer)));
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonType#TEXT}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-CREATION-TABLE">json_object ( keys text[], values text[] ) → json<br/>
     * This form of json_object takes keys and values pairwise from separate text arrays. Otherwise it is identical to the one-argument form.<br/>
     * json_object('{a,b}', '{1,2}') → {"a": "1", "b": "2"}<br/>
     * </a>
     */
    public static SimpleExpression jsonObject(Expression keyArray, Expression valueArray) {
        return FunctionUtils.twoArgFunc("JSON_OBJECT", keyArray, valueArray, JsonType.TEXT);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonbType#TEXT}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-CREATION-TABLE">jsonb_object ( keys text[], values text[] ) → json<br/>
     * This form of json_object takes keys and values pairwise from separate text arrays. Otherwise it is identical to the one-argument form.<br/>
     * json_object('{a,b}', '{1,2}') → {"a": "1", "b": "2"}<br/>
     * </a>
     */
    public static SimpleExpression jsonbObject(Expression keyArray, Expression valueArray) {
        return FunctionUtils.twoArgFunc("JSONB_OBJECT", keyArray, valueArray, JsonbType.TEXT);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonType#TEXT}.
     * Default Selection alias is 'value'.
     * </p>
     * <pre><br/>
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
     * </p>
     * <pre><br/>
     *   select * from json_array_elements('[1,true, [2,false]]') →
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
     * The {@link MappingType} of function return type: {@link JsonType#TEXT}.
     * Default Selection alias is 'value'.
     * </p>
     * <pre><br/>
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
        return DialectFunctionUtils.oneArgColumnFunction("JSON_ARRAY_ELEMENTS_TEXT", json, "value", JsonType.TEXT);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonbType#TEXT}.
     * Default Selection alias is 'value'.
     * </p>
     * <pre><br/>
     *   select * from json_array_elements_text('["foo", "bar"]') →
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
        return DialectFunctionUtils.oneArgColumnFunction("JSONB_ARRAY_ELEMENTS_TEXT", json, "value", JsonbType.TEXT);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link IntegerType}.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">json_array_length ( json ) → integer<br/>
     * Returns the number of elements in the top-level JSON array.<br/>
     * </a>
     */
    public static SimpleExpression jsonArrayLength(Expression json) {
        return FunctionUtils.oneArgFunc("JSON_ARRAY_LENGTH", json, IntegerType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link IntegerType}.
     * </p>
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
     * The {@link MappingType} of fields of derived table :<li>
     * <li>key : {@link StringType}</li>
     * <li>value : {@link JsonType#TEXT}</li>
     * <li>ordinality (optioinal) : {@link IntegerType}. see {@link _WithOrdinalityClause#withOrdinality()}</li>
     * </li>
     * </p>
     * <pre><br/>
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
     * The {@link MappingType} of fields of derived table :<li>
     * <li>key : {@link StringType}</li>
     * <li>value : {@link JsonType#TEXT}</li>
     * <li>ordinality (optioinal) : {@link IntegerType}. see {@link _WithOrdinalityClause#withOrdinality()}</li>
     * </li>
     * </p>
     * <pre><br/>
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
        fieldList.add(ArmySelections.forName("key", StringType.INSTANCE));
        fieldList.add(ArmySelections.forName("value", JsonType.TEXT));
        return DialectFunctionUtils.oneArgTabularFunc("JSON_EACH", json, fieldList);
    }

    /**
     * <p>
     * The {@link MappingType} of fields of derived table :<li>
     * <li>key : {@link StringType}</li>
     * <li>value : {@link JsonbType#TEXT}</li>
     * <li>ordinality (optioinal) : {@link IntegerType}. see {@link _WithOrdinalityClause#withOrdinality()}</li>
     * </li>
     * </p>
     * <pre><br/>
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
     * @see #jsonEach(Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">jsonb_each ( json ) → setof record ( key text, value json )<br/>
     * Expands the top-level JSON object into a set of key/value pairs.<br/>
     * </a>
     */
    public static <T> _TabularWithOrdinalityFunction jsonbEach(BiFunction<MappingType, T, Expression> funcRef, T value) {
        return jsonbEach(funcRef.apply(JsonbType.TEXT, value));
    }


    /**
     * <p>
     * The {@link MappingType} of fields of derived table :<li>
     * <li>key : {@link StringType}</li>
     * <li>value : {@link JsonbType#TEXT}</li>
     * <li>ordinality (optioinal) : {@link IntegerType}. see {@link _WithOrdinalityClause#withOrdinality()}</li>
     * </li>
     * </p>
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
        fieldList.add(ArmySelections.forName("key", StringType.INSTANCE));
        fieldList.add(ArmySelections.forName("value", JsonbType.TEXT));
        return DialectFunctionUtils.oneArgTabularFunc("JSONB_EACH", json, fieldList);
    }


    /**
     * <p>
     * The {@link MappingType} of fields of derived table :<li>
     * <li>key : {@link StringType}</li>
     * <li>value : {@link TextType}</li>
     * <li>ordinality (optioinal) : {@link IntegerType}. see {@link _WithOrdinalityClause#withOrdinality()}</li>
     * </li>
     * </p>
     * <pre><br/>
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
     * The {@link MappingType} of fields of derived table :<li>
     * <li>key : {@link StringType}</li>
     * <li>value : {@link TextType}</li>
     * <li>ordinality (optioinal) : {@link IntegerType}. see {@link _WithOrdinalityClause#withOrdinality()}</li>
     * </li>
     * </p>
     * <pre><br/>
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
        fieldList.add(ArmySelections.forName("key", StringType.INSTANCE));
        fieldList.add(ArmySelections.forName("value", TextType.INSTANCE));
        return DialectFunctionUtils.oneArgTabularFunc("JSON_EACH_TEXT", json, fieldList);
    }

    /**
     * <p>
     * The {@link MappingType} of fields of derived table :<li>
     * <li>key : {@link StringType}</li>
     * <li>value : {@link TextType}</li>
     * <li>ordinality (optioinal) : {@link IntegerType}. see {@link _WithOrdinalityClause#withOrdinality()}</li>
     * </li>
     * </p>
     * <pre><br/>
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
     * The {@link MappingType} of fields of derived table :<li>
     * <li>key : {@link StringType}</li>
     * <li>value : {@link TextType}</li>
     * <li>ordinality (optioinal) : {@link IntegerType}. see {@link _WithOrdinalityClause#withOrdinality()}</li>
     * </li>
     * </p>
     * <pre><br/>
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
        fieldList.add(ArmySelections.forName("key", StringType.INSTANCE));
        fieldList.add(ArmySelections.forName("value", TextType.INSTANCE));
        return DialectFunctionUtils.oneArgTabularFunc("JSONB_EACH_TEXT", json, fieldList);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonType#TEXT}.
     * </p>
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
     * </p>
     *
     * @param funcRef   the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                  <ul>
     *                      <li>{@link SQLs#multiParam(TypeInfer, Collection)}</li>
     *                      <li>{@link SQLs#multiLiteral(TypeInfer, Collection)}</li>
     *                      <li>developer custom method</li>
     *                  </ul>.
     *                  The first argument of funcRef always is {@link TextType#INSTANCE}.
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
        pathElemList = _ArrayUtils.unmodifiableListOf(firstPath, rest);
        return FunctionUtils.oneAndMulti(name, fromJson, funcRef.apply(TextType.INSTANCE, pathElemList), JsonType.TEXT);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonType#TEXT}.
     * </p>
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
     *                           <li>{@link SQLs#multiParam(TypeInfer, Collection)}</li>
     *                           <li>{@link SQLs#multiLiteral(TypeInfer, Collection)}</li>
     *                           <li>developer custom method</li>
     *                       </ul>.
     *                       The first argument of funcRefForPath always is {@link TextType#INSTANCE}.
     * @param firstPath      firstPath and rest will be collected to unmodified {@link List} and passed to funcRef as the second argument of funcRef
     * @param rest           rest and firstPath will be collected to unmodified {@link List} and passed to funcRef as the second argument of funcRef
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">json_extract_path ( from_json json, VARIADIC path_elems text[] ) → json<br/>
     * Extracts JSON sub-object at the specified path. (This is functionally equivalent to the #> operator, but writing the path out as a variadic list can be more convenient in some cases.)<br/>
     * </a>
     */
    public static <T> SimpleExpression jsonExtractPath(BiFunction<MappingType, T, Expression> funcRefForJson, T json,
                                                       BiFunction<MappingType, List<String>, Expression> funcRefForPath,
                                                       String firstPath, String... rest) {
        return jsonExtractPath(funcRefForJson.apply(JsonType.TEXT, json), funcRefForPath, firstPath, rest);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonbType#TEXT}.
     * </p>
     *
     * @param firstPath should be {@link TextType} or {@link StringType} type.
     * @param rest      each element should be {@link TextType} or {@link StringType} type.
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">json_extract_path ( from_json jsonb, VARIADIC path_elems text[] ) → jsonb<br/>
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
     * </p>
     *
     * @param funcRef   the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                  <ul>
     *                      <li>{@link SQLs#multiParam(TypeInfer, Collection)}</li>
     *                      <li>{@link SQLs#multiLiteral(TypeInfer, Collection)}</li>
     *                      <li>developer custom method</li>
     *                  </ul>.
     *                  The first argument of funcRef always is {@link TextType#INSTANCE}.
     * @param firstPath firstPath and rest will be collected to unmodified {@link List} and passed to funcRef as the second argument of funcRef
     * @param rest      rest and firstPath will be collected to unmodified {@link List} and passed to funcRef as the second argument of funcRef
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">json_extract_path ( from_json jsonb, VARIADIC path_elems text[] ) → jsonb<br/>
     * Extracts JSON sub-object at the specified path. (This is functionally equivalent to the #> operator, but writing the path out as a variadic list can be more convenient in some cases.)<br/>
     * </a>
     */
    public static SimpleExpression jsonbExtractPath(Expression fromJson, BiFunction<MappingType, List<String>, Expression> funcRef,
                                                    String firstPath, String... rest) {
        final String name = "JSONB_EXTRACT_PATH";
        final List<String> pathElemList;
        pathElemList = _ArrayUtils.unmodifiableListOf(firstPath, rest);
        return FunctionUtils.oneAndMulti(name, fromJson, funcRef.apply(TextType.INSTANCE, pathElemList), JsonbType.TEXT);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonbType#TEXT}.
     * </p>
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
     *                           <li>{@link SQLs#multiParam(TypeInfer, Collection)}</li>
     *                           <li>{@link SQLs#multiLiteral(TypeInfer, Collection)}</li>
     *                           <li>developer custom method</li>
     *                       </ul>.
     *                       The first argument of funcRefForPath always is {@link TextType#INSTANCE}.
     * @param firstPath      firstPath and rest will be collected to unmodified {@link List} and passed to funcRef as the second argument of funcRef
     * @param rest           rest and firstPath will be collected to unmodified {@link List} and passed to funcRef as the second argument of funcRef
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">json_extract_path ( from_json jsonb, VARIADIC path_elems text[] ) → jsonb<br/>
     * Extracts JSON sub-object at the specified path. (This is functionally equivalent to the #> operator, but writing the path out as a variadic list can be more convenient in some cases.)<br/>
     * </a>
     */
    public static <T> SimpleExpression jsonbExtractPath(BiFunction<MappingType, T, Expression> funcRefForJson, T json,
                                                        BiFunction<MappingType, List<String>, Expression> funcRefForPath,
                                                        String firstPath, String... rest) {
        return jsonbExtractPath(funcRefForJson.apply(JsonbType.TEXT, json), funcRefForPath, firstPath, rest);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link TextType#INSTANCE}.
     * </p>
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
     * </p>
     *
     * @param funcRef   the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                  <ul>
     *                      <li>{@link SQLs#multiParam(TypeInfer, Collection)}</li>
     *                      <li>{@link SQLs#multiLiteral(TypeInfer, Collection)}</li>
     *                      <li>developer custom method</li>
     *                  </ul>.
     *                  The first argument of funcRef always is {@link TextType#INSTANCE}.
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
        pathElemList = _ArrayUtils.unmodifiableListOf(firstPath, rest);
        return FunctionUtils.oneAndMulti(name, fromJson, funcRef.apply(TextType.INSTANCE, pathElemList), TextType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link TextType#INSTANCE}.
     * </p>
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
     *                           <li>{@link SQLs#multiParam(TypeInfer, Collection)}</li>
     *                           <li>{@link SQLs#multiLiteral(TypeInfer, Collection)}</li>
     *                           <li>developer custom method</li>
     *                       </ul>.
     *                       The first argument of funcRefForPath always is {@link TextType#INSTANCE}.
     * @param firstPath      firstPath and rest will be collected to unmodified {@link List} and passed to funcRef as the second argument of funcRef
     * @param rest           rest and firstPath will be collected to unmodified {@link List} and passed to funcRef as the second argument of funcRef
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">json_extract_path_text ( from_json json, VARIADIC path_elems text[] ) → text<br/>
     * Extracts JSON sub-object at the specified path as text. (This is functionally equivalent to the #>> operator.)<br/>
     * </a>
     */
    public static <T> SimpleExpression jsonExtractPathText(BiFunction<MappingType, T, Expression> funcRefForJson, T json,
                                                           BiFunction<MappingType, List<String>, Expression> funcRefForPath,
                                                           String firstPath, String... rest) {
        return jsonExtractPathText(funcRefForJson.apply(JsonType.TEXT, json), funcRefForPath, firstPath, rest);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link TextType#INSTANCE}.
     * </p>
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
     * </p>
     *
     * @param funcRef   the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                  <ul>
     *                      <li>{@link SQLs#multiParam(TypeInfer, Collection)}</li>
     *                      <li>{@link SQLs#multiLiteral(TypeInfer, Collection)}</li>
     *                      <li>developer custom method</li>
     *                  </ul>.
     *                  The first argument of funcRef always is {@link TextType#INSTANCE}.
     * @param firstPath firstPath and rest will be collected to unmodified {@link List} and passed to funcRef as the second argument of funcRef
     * @param rest      rest and firstPath will be collected to unmodified {@link List} and passed to funcRef as the second argument of funcRef
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">jsonb_extract_path_text ( from_json jsonb, VARIADIC path_elems text[] ) → text<br/>
     * Extracts JSON sub-object at the specified path as text. (This is functionally equivalent to the #>> operator.)<br/>
     * </a>
     */
    public static SimpleExpression jsonbExtractPathText(Expression fromJson, BiFunction<MappingType, List<String>, Expression> funcRef,
                                                        String firstPath, String... rest) {
        final String name = "JSONB_EXTRACT_PATH";
        final List<String> pathElemList;
        pathElemList = _ArrayUtils.unmodifiableListOf(firstPath, rest);
        return FunctionUtils.oneAndMulti(name, fromJson, funcRef.apply(TextType.INSTANCE, pathElemList), TextType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link TextType#INSTANCE}.
     * </p>
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
     *                           <li>{@link SQLs#multiParam(TypeInfer, Collection)}</li>
     *                           <li>{@link SQLs#multiLiteral(TypeInfer, Collection)}</li>
     *                           <li>developer custom method</li>
     *                       </ul>.
     *                       The first argument of funcRefForPath always is {@link TextType#INSTANCE}.
     * @param firstPath      firstPath and rest will be collected to unmodified {@link List} and passed to funcRef as the second argument of funcRef
     * @param rest           rest and firstPath will be collected to unmodified {@link List} and passed to funcRef as the second argument of funcRef
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">jsonb_extract_path_text ( from_json jsonb, VARIADIC path_elems text[] ) → text<br/>
     * Extracts JSON sub-object at the specified path as text. (This is functionally equivalent to the #>> operator.)<br/>
     * </a>
     */
    public static <T> SimpleExpression jsonbExtractPathText(BiFunction<MappingType, T, Expression> funcRefForJson, T json,
                                                            BiFunction<MappingType, List<String>, Expression> funcRefForPath,
                                                            String firstPath, String... rest) {
        return jsonbExtractPathText(funcRefForJson.apply(JsonbType.TEXT, json), funcRefForPath, firstPath, rest);
    }


    /**
     * <p>
     * The {@link MappingType} of fields of derived table :<li>
     * <li>json_object_keys : {@link TextType}</li>
     * <li>ordinality (optioinal) : {@link IntegerType}. see {@link _WithOrdinalityClause#withOrdinality()}</li>
     * </li>
     * </p>
     * <pre><br/>
     *   select * from json_object_keys('{"f1":"abc","f2":{"f3":"a", "f4":"b"}}') →
     *
     *    json_object_keys
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
        final String name = "JSON_OBJECT_KEYS";
        return DialectFunctionUtils.oneArgColumnFunction(name, json, name.toLowerCase(Locale.ROOT), TextType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of fields of derived table :<li>
     * <li>json_object_keys : {@link TextType}</li>
     * <li>ordinality (optioinal) : {@link IntegerType}. see {@link _WithOrdinalityClause#withOrdinality()}</li>
     * </li>
     * </p>
     * <pre><br/>
     *   select * from json_object_keys('{"f1":"abc","f2":{"f3":"a", "f4":"b"}}') →
     *
     *    json_object_keys
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
     * The {@link MappingType} of fields of derived table :<li>
     * <li>jsonb_object_keys : {@link TextType}</li>
     * <li>ordinality (optioinal) : {@link IntegerType}. see {@link _WithOrdinalityClause#withOrdinality()}</li>
     * </li>
     * </p>
     * <pre><br/>
     *   select * from jsonb_object_keys('{"f1":"abc","f2":{"f3":"a", "f4":"b"}}') →
     *
     *    json_object_keys
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
        final String name = "JSONB_OBJECT_KEYS";
        return DialectFunctionUtils.oneArgColumnFunction(name, json, name.toLowerCase(Locale.ROOT), TextType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of fields of derived table :<li>
     * <li>jsonb_object_keys : {@link TextType}</li>
     * <li>ordinality (optioinal) : {@link IntegerType}. see {@link _WithOrdinalityClause#withOrdinality()}</li>
     * </li>
     * </p>
     * <pre><br/>
     *   select * from jsonb_object_keys('{"f1":"abc","f2":{"f3":"a", "f4":"b"}}') →
     *
     *    json_object_keys
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
     * The {@link MappingType} of fields of derived table :<li>
     * <li>fields follow {@link MappingType.SqlCompositeType#fieldList()}</li>
     * <li>ordinality (optioinal) : {@link IntegerType}. see {@link _WithOrdinalityClause#withOrdinality()}</li>
     * </li>
     * </p>
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
     * The {@link MappingType} of fields of derived table :<li>
     * <li>fields follow {@link MappingType.SqlCompositeType#fieldList()}</li>
     * <li>ordinality (optioinal) : {@link IntegerType}. see {@link _WithOrdinalityClause#withOrdinality()}</li>
     * </li>
     * </p>
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
     * The {@link MappingType} of fields of derived table :<li>
     * <li>fields follow {@link MappingType.SqlCompositeType#fieldList()}</li>
     * <li>ordinality (optioinal) : {@link IntegerType}. see {@link _WithOrdinalityClause#withOrdinality()}</li>
     * </li>
     * </p>
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
     * The {@link MappingType} of fields of derived table :<li>
     * <li>fields follow {@link MappingType.SqlCompositeType#fieldList()}</li>
     * <li>ordinality (optioinal) : {@link IntegerType}. see {@link _WithOrdinalityClause#withOrdinality()}</li>
     * </li>
     * </p>
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
     * </p>
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
     *                           {@link SQLs#multiParam(TypeInfer, Collection)}
     * @see #jsonbSet(Expression, Expression, Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">jsonb_set ( target jsonb, path text[], new_value jsonb [, create_if_missing boolean ] ) → jsonb<br/>
     *
     * </a>
     */
    public static <T> SimpleExpression jsonbSet(Expression jsonb, BiFunction<MappingType, String[], Expression> funcRefForPath,
                                                String[] paths, BiFunction<MappingType, T, Expression> funcRefForValue,
                                                T newValue, Expression createIfMissing) {
        if (paths.length == 0) {
            throw CriteriaUtils.funcArgError("JSONB_SET", paths);
        }
        return jsonbSet(jsonb, funcRefForPath.apply(TextArrayType.LINEAR, paths),
                funcRefForValue.apply(JsonbType.TEXT, newValue),
                createIfMissing
        );
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonbType#TEXT}.
     * </p>
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
     * </p>
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
     *                           {@link SQLs#multiParam(TypeInfer, Collection)}
     * @see #jsonbSet(Expression, Expression, Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">jsonb_set ( target jsonb, path text[], new_value jsonb [, create_if_missing boolean ] ) → jsonb<br/>
     *
     * </a>
     */
    public static <T> SimpleExpression jsonbSet(Expression jsonb, BiFunction<MappingType, String[], Expression> funcRefForPath,
                                                String[] paths, BiFunction<MappingType, T, Expression> funcRefForValue,
                                                T newValue) {
        if (paths.length == 0) {
            throw CriteriaUtils.funcArgError("JSONB_SET", paths);
        }
        return jsonbSet(jsonb, funcRefForPath.apply(TextArrayType.LINEAR, paths),
                funcRefForValue.apply(JsonbType.TEXT, newValue)
        );
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link JsonbType#TEXT}.
     * </p>
     *
     * @see #jsonbSet(Expression, Expression, Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING-TABLE">jsonb_set ( target jsonb, path text[], new_value jsonb [, create_if_missing boolean ] ) → jsonb<br/>
     *
     * </a>
     */
    public static SimpleExpression jsonbSet(Expression jsonb, Expression path, Expression newValue) {
        return FunctionUtils.threeArgFunc("JSONB_SET", jsonb, path, newValue, JsonbType.TEXT);
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


    /**
     * @see #jsonPopulateRecord(Expression, Expression)
     * @see #jsonbPopulateRecord(Expression, Expression)
     * @see #jsonPopulateRecordSet(Expression, Expression)
     * @see #jsonbPopulateRecordSet(Expression, Expression)
     */
    private static _TabularWithOrdinalityFunction _jsonbPopulateRecordFunc(final String name, final Expression base,
                                                                           final Expression json) {
        if (base instanceof TypeInfer.DelayTypeInfer && ((TypeInfer.DelayTypeInfer) base).isDelay()) {
            throw CriteriaUtils.tabularFuncErrorPosition(name);
        }
        final List<Selection> fieldList;
        fieldList = DialectFunctionUtils.compositeFieldList(name, base);
        return DialectFunctionUtils.twoArgTabularFunc(name, base, json, fieldList);
    }


}
