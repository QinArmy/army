package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.standard.SQLFunction;
import io.army.lang.Nullable;
import io.army.mapping.*;
import io.army.mapping.optional.IntegerArrayType;
import io.army.mapping.optional.ShortArrayType;
import io.army.mapping.optional.TextArrayType;
import io.army.mapping.postgre.PostgreTsVectorType;
import io.army.util._Collections;

import java.util.Collection;
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

    /*-------------------below Sequence Manipulation Functions-------------------*/

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  LongType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-sequence.html">nextval ( regclass ) → bigint</a>
     */
    public static SimpleExpression nextVal(Expression exp) {
        return FunctionUtils.oneArgFunc("NEXTVAL", exp, LongType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  LongType}
     * </p>
     *
     * @see #setVal(Expression, Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-sequence.html">setval ( regclass, bigint [, boolean ] ) → bigint/a>
     */
    public static SimpleExpression setVal(Expression regClass, Expression value) {
        return FunctionUtils.twoArgFunc("SETVAL", regClass, value, LongType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  LongType}
     * </p>
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
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-sequence.html">currval ( regclass ) → bigint</a>
     */
    public static SimpleExpression currVal(Expression exp) {
        return FunctionUtils.oneArgFunc("CURRVAL", exp, LongType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  LongType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-sequence.html">lastval ( regclass ) → bigint</a>
     */
    public static SimpleExpression lastVal(Expression exp) {
        return FunctionUtils.oneArgFunc("LASTVAL", exp, LongType.INSTANCE);
    }

    /*-------------------below Conditional Expressions-------------------*/

    public static SQLFunction._CaseFuncWhenClause cases(Expression exp) {
        return FunctionUtils.caseFunction(exp);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of firstValue
     * </p>
     *
     * @throws CriteriaException throw when<ul>
     *                           <li><firstValue isn't operable {@link Expression},eg:{@link SQLs#DEFAULT}/li>
     *                           <li>firstValue is multi value {@link Expression},eg: {@link SQLs#multiLiteral(TypeInfer, Collection)}</li>
     *                           <li><the element of rest isn't operable {@link Expression},eg:{@link SQLs#DEFAULT}/li>
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
     * </p>
     *
     * @throws CriteriaException throw when<ul>
     *                           <li><the element of consumer isn't operable {@link Expression},eg:{@link SQLs#DEFAULT}/li>
     *                           </ul>
     * @see <a href="https://www.postgresql.org/docs/current/functions-conditional.html#FUNCTIONS-COALESCE-NVL-IFNULL">COALESCE(value [, ...])</a>
     */
    public static SimpleExpression coalesce(Consumer<Consumer<Expression>> consumer) {
        return FunctionUtils.consumerAndFirstTypeFunc("COALESCE", consumer);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of firstValue
     * </p>
     *
     * @throws CriteriaException throw when<ul>
     *                           <li><firstValue isn't operable {@link Expression},eg:{@link SQLs#DEFAULT}/li>
     *                           <li>firstValue is multi value {@link Expression},eg: {@link SQLs#multiLiteral(TypeInfer, Collection)}</li>
     *                           <li><the element of rest isn't operable {@link Expression},eg:{@link SQLs#DEFAULT}/li>
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
     * </p>
     *
     * @throws CriteriaException throw when<ul>
     *                           <li><the element of consumer isn't operable {@link Expression},eg:{@link SQLs#DEFAULT}/li>
     *                           </ul>
     * @see <a href="https://www.postgresql.org/docs/current/functions-conditional.html#FUNCTIONS-GREATEST-LEAST">GREATEST(value [, ...])</a>
     */
    public static SimpleExpression greatest(Consumer<Consumer<Expression>> consumer) {
        return FunctionUtils.consumerAndFirstTypeFunc("GREATEST", consumer);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of firstValue
     * </p>
     *
     * @throws CriteriaException throw when<ul>
     *                           <li><firstValue isn't operable {@link Expression},eg:{@link SQLs#DEFAULT}/li>
     *                           <li>firstValue is multi value {@link Expression},eg: {@link SQLs#multiLiteral(TypeInfer, Collection)}</li>
     *                           <li><the element of rest isn't operable {@link Expression},eg:{@link SQLs#DEFAULT}/li>
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
     * </p>
     *
     * @throws CriteriaException throw when<ul>
     *                           <li><the element of consumer isn't operable {@link Expression},eg:{@link SQLs#DEFAULT}/li>
     *                           </ul>
     * @see <a href="https://www.postgresql.org/docs/current/functions-conditional.html#FUNCTIONS-GREATEST-LEAST">LEAST(value [, ...])</a>
     */
    public static SimpleExpression least(Consumer<Consumer<Expression>> consumer) {
        return FunctionUtils.consumerAndFirstTypeFunc("LEAST", consumer);
    }

    /*-------------------below  Array Functions-------------------*/

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of fist anyCompatibleArray
     * </p>
     *
     * @throws CriteriaException throw when<ul>
     *                           <li><the element of consumer isn't operable {@link Expression},eg:{@link SQLs#DEFAULT}/li>
     *                           </ul>
     * @see <a href="https://www.postgresql.org/docs/current/functions-array.html#ARRAY-FUNCTIONS-TABLE">array_append ( anycompatiblearray, anycompatible ) → anycompatiblearray</a>
     */
    public static SimpleExpression arrayAppend(Expression anyCompatibleArray, Expression anyCompatible) {
        return FunctionUtils.twoArgFunc("ARRAY_APPEND", anyCompatibleArray, anyCompatible,
                _returnType(anyCompatibleArray, Expressions::identityType)
        );
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of fist anyCompatibleArray1
     * </p>
     *
     * @throws CriteriaException throw when<ul>
     *                           <li><the element of consumer isn't operable {@link Expression},eg:{@link SQLs#DEFAULT}/li>
     *                           </ul>
     * @see <a href="https://www.postgresql.org/docs/current/functions-array.html#ARRAY-FUNCTIONS-TABLE">array_cat ( anycompatiblearray, anycompatiblearray ) → anycompatiblearray</a>
     */
    public static SimpleExpression arrayCat(Expression anyCompatibleArray1, Expression anyCompatibleArray2) {
        return FunctionUtils.twoArgFunc("ARRAY_CAT", anyCompatibleArray1, anyCompatibleArray2,
                _returnType(anyCompatibleArray1, Expressions::identityType)
        );
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link TextType#INSTANCE}
     * </p>
     *
     * @throws CriteriaException throw when<ul>
     *                           <li><the element of consumer isn't operable {@link Expression},eg:{@link SQLs#DEFAULT}/li>
     *                           </ul>
     * @see <a href="https://www.postgresql.org/docs/current/functions-array.html#ARRAY-FUNCTIONS-TABLE">array_dims ( anyarray ) → text</a>
     */
    public static SimpleExpression arrayDims(Expression anyArray) {
        return FunctionUtils.oneArgFunc("ARRAY_DIMS", anyArray, TextType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the array type of {@link MappingType} of anyElement.
     * </p>
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
     *                           <li><the element of consumer isn't operable {@link Expression},eg:{@link SQLs#DEFAULT}/li>
     *                           </ul>
     * @see <a href="https://www.postgresql.org/docs/current/functions-array.html#ARRAY-FUNCTIONS-TABLE">array_fill ( anyelement, integer[] [, integer[] ] ) → anyarray</a>
     */
    public static <T> SimpleExpression arrayFill(Expression anyElement, BiFunction<MappingType, T, Expression> funcRefForDimension,
                                                 T dimensions) {
        return _arrayFill(anyElement, funcRefForDimension.apply(IntegerArrayType.LINEAR, dimensions), null);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the array type of {@link MappingType} of anyElement.
     * </p>
     *
     * @throws CriteriaException throw when<ul>
     *                           <li><the element of consumer isn't operable {@link Expression},eg:{@link SQLs#DEFAULT}/li>
     *                           </ul>
     * @see <a href="https://www.postgresql.org/docs/current/functions-array.html#ARRAY-FUNCTIONS-TABLE">array_fill ( anyelement, integer[] [, integer[] ] ) → anyarray</a>
     */
    public static SimpleExpression arrayFill(Expression anyElement, Expression dimensions) {
        return _arrayFill(anyElement, dimensions, null);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the array type of {@link MappingType} of anyElement.
     * </p>
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
     *                           <li><the element of consumer isn't operable {@link Expression},eg:{@link SQLs#DEFAULT}/li>
     *                           </ul>
     * @see <a href="https://www.postgresql.org/docs/current/functions-array.html#ARRAY-FUNCTIONS-TABLE">array_fill ( anyelement, integer[] [, integer[] ] ) → anyarray</a>
     */
    public static <T, U> SimpleExpression arrayFill(Expression anyElement, BiFunction<MappingType, T, Expression> funcRefForDimension,
                                                    T dimensions, BiFunction<MappingType, U, Expression> funcRefForBound, U bounds) {
        return _arrayFill(anyElement, funcRefForDimension.apply(IntegerArrayType.LINEAR, dimensions),
                funcRefForBound.apply(IntegerArrayType.LINEAR, bounds)
        );
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the array type of {@link MappingType} of anyElement.
     * </p>
     *
     * @throws CriteriaException throw when<ul>
     *                           <li><the element of consumer isn't operable {@link Expression},eg:{@link SQLs#DEFAULT}/li>
     *                           </ul>
     * @see <a href="https://www.postgresql.org/docs/current/functions-array.html#ARRAY-FUNCTIONS-TABLE">array_fill ( anyelement, integer[] [, integer[] ] ) → anyarray</a>
     */
    public static SimpleExpression arrayFill(Expression anyElement, Expression dimensions, Expression bounds) {
        ContextStack.assertNonNull(bounds);
        return _arrayFill(anyElement, dimensions, bounds);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link IntegerType#INSTANCE}
     * </p>
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
     *                           <li><the element of consumer isn't operable {@link Expression},eg:{@link SQLs#DEFAULT}/li>
     *                           </ul>
     * @see #arrayLength(Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-array.html#ARRAY-FUNCTIONS-TABLE">array_length ( anyarray, integer ) → integer</a>
     */
    public static <T> SimpleExpression arrayLength(Expression anyArray, BiFunction<MappingType, T, Expression> funcRef,
                                                   T dimension) {
        return arrayLength(anyArray, funcRef.apply(IntegerType.INSTANCE, dimension));
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link IntegerType#INSTANCE}
     * </p>
     *
     * @throws CriteriaException throw when<ul>
     *                           <li><the element of consumer isn't operable {@link Expression},eg:{@link SQLs#DEFAULT}/li>
     *                           </ul>
     * @see <a href="https://www.postgresql.org/docs/current/functions-array.html#ARRAY-FUNCTIONS-TABLE">array_length ( anyarray, integer ) → integer</a>
     */
    public static SimpleExpression arrayLength(Expression anyArray, Expression dimension) {
        return FunctionUtils.twoArgFunc("ARRAY_LENGTH", anyArray, dimension, IntegerType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link IntegerType#INSTANCE}
     * </p>
     *
     * @throws CriteriaException throw when<ul>
     *                           <li><the element of consumer isn't operable {@link Expression},eg:{@link SQLs#DEFAULT}/li>
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
     * </p>
     *
     * @throws CriteriaException throw when<ul>
     *                           <li><the element of consumer isn't operable {@link Expression},eg:{@link SQLs#DEFAULT}/li>
     *                           </ul>
     * @see <a href="https://www.postgresql.org/docs/current/functions-array.html#ARRAY-FUNCTIONS-TABLE">array_ndims ( anyarray ) → integer</a>
     */
    public static SimpleExpression arrayNDims(Expression anyArray) {
        return FunctionUtils.oneArgFunc("ARRAY_NDIMS", anyArray, IntegerType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link IntegerType#INSTANCE}
     * </p>
     *
     * @throws CriteriaException throw when<ul>
     *                           <li><the element of consumer isn't operable {@link Expression},eg:{@link SQLs#DEFAULT}/li>
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
     * </p>
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
     *                           <li><the element of consumer isn't operable {@link Expression},eg:{@link SQLs#DEFAULT}/li>
     *                           </ul>
     * @see #arrayPosition(Expression, Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-array.html#ARRAY-FUNCTIONS-TABLE">array_position ( anycompatiblearray, anycompatible [, integer ] ) → integer</a>
     */
    public static <T> SimpleExpression arrayPosition(Expression anyCompatibleArray, Expression anyCompatible,
                                                     BiFunction<MappingType, T, Expression> funcRef, T subscript) {
        return arrayPosition(anyCompatibleArray, anyCompatible, funcRef.apply(IntegerType.INSTANCE, subscript));
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link IntegerType#INSTANCE}
     * </p>
     *
     * @throws CriteriaException throw when<ul>
     *                           <li><the element of consumer isn't operable {@link Expression},eg:{@link SQLs#DEFAULT}/li>
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
     * </p>
     *
     * @throws CriteriaException throw when<ul>
     *                           <li><the element of consumer isn't operable {@link Expression},eg:{@link SQLs#DEFAULT}/li>
     *                           </ul>
     * @see <a href="https://www.postgresql.org/docs/current/functions-array.html#ARRAY-FUNCTIONS-TABLE">array_positions ( anycompatiblearray, anycompatible ) → integer[]</a>
     */
    public static SimpleExpression arrayPositions(Expression anyCompatibleArray, Expression anyCompatible) {
        return FunctionUtils.twoArgFunc("ARRAY_POSITIONS", anyCompatibleArray, anyCompatible, IntegerArrayType.LINEAR);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of anyCompatibleArray.
     * </p>
     *
     * @throws CriteriaException throw when<ul>
     *                           <li><the element of consumer isn't operable {@link Expression},eg:{@link SQLs#DEFAULT}/li>
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
     * </p>
     *
     * @throws CriteriaException throw when<ul>
     *                           <li><the element of consumer isn't operable {@link Expression},eg:{@link SQLs#DEFAULT}/li>
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
     * </p>
     *
     * @throws CriteriaException throw when<ul>
     *                           <li><the element of consumer isn't operable {@link Expression},eg:{@link SQLs#DEFAULT}/li>
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
     * </p>
     *
     * @throws CriteriaException throw when<ul>
     *                           <li><the element of consumer isn't operable {@link Expression},eg:{@link SQLs#DEFAULT}/li>
     *                           </ul>
     * @see <a href="https://www.postgresql.org/docs/current/functions-array.html#ARRAY-FUNCTIONS-TABLE">array_to_string ( array anyarray, delimiter text [, null_string text ] ) → text</a>
     */
    public static SimpleExpression arrayToString(Expression array, Expression delimiter, Expression nullString) {
        return FunctionUtils.threeArgFunc("ARRAY_TO_STRING", array, delimiter, nullString, TextType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link IntegerType#INSTANCE}.
     * </p>
     *
     * @throws CriteriaException throw when<ul>
     *                           <li><the element of consumer isn't operable {@link Expression},eg:{@link SQLs#DEFAULT}/li>
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
     * </p>
     *
     * @throws CriteriaException throw when<ul>
     *                           <li><the element of consumer isn't operable {@link Expression},eg:{@link SQLs#DEFAULT}/li>
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
     * </p>
     *
     * @throws CriteriaException throw when<ul>
     *                           <li><the element of consumer isn't operable {@link Expression},eg:{@link SQLs#DEFAULT}/li>
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
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-textsearch.html#TEXTSEARCH-FUNCTIONS-TABLE">unnest ( tsvector ) → setof record ( lexeme text, positions smallint[], weights text ) <br/>
     * Expands a tsvector into a set of rows, one per lexeme
     * </a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-array.html#ARRAY-FUNCTIONS-TABLE">unnest ( anyarray ) → setof anyelement<br/>
     * Expands an array into a set of rows. The array's elements are read out in storage order.
     * </a>
     */
    public static _TabularWithOrdinalityFunction unnest(final Expression exp) {
        final String name = "UNNEST";
        if (exp instanceof TypeInfer.DelayTypeInfer && ((TypeInfer.DelayTypeInfer) exp).isDelay()) {
            throw CriteriaUtils.tabularFuncErrorPosition(name);
        }

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
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-array.html#ARRAY-FUNCTIONS-TABLE">unnest ( anyarray ) → setof anyelement<br/>
     * Expands an array into a set of rows. The array's elements are read out in storage order.
     * </a>
     */
    public static _ColumnWithOrdinalityFunction unnest(final ArrayExpression exp) {
        final String name = "UNNEST";
        if (exp instanceof TypeInfer.DelayTypeInfer && ((TypeInfer.DelayTypeInfer) exp).isDelay()) {
            throw CriteriaUtils.tabularFuncErrorPosition(name);
        }
        return DialectFunctionUtils.oneArgColumnFunction(name, exp, null,
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
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-array.html#ARRAY-FUNCTIONS-TABLE">unnest ( anyarray, anyarray [, ... ] ) → setof anyelement, anyelement [, ... ]<br/>
     * Expands multiple arrays (possibly of different data types) into a set of rows. If the arrays are not all the same length then the shorter ones are padded with NULLs. This form is only allowed in a query's FROM clause;
     * </a>
     */
    public static _TabularWithOrdinalityFunction unnest(ArrayExpression array1, ArrayExpression array2) {
        final String name = "UNNEST";
        if (array1 instanceof TypeInfer.DelayTypeInfer && ((TypeInfer.DelayTypeInfer) array1).isDelay()) {
            throw CriteriaUtils.tabularFuncErrorPosition(name);
        } else if (array2 instanceof TypeInfer.DelayTypeInfer && ((TypeInfer.DelayTypeInfer) array2).isDelay()) {
            throw CriteriaUtils.tabularFuncErrorPosition(name);
        }
        final List<Selection> fieldList = _Collections.arrayList(2);

        fieldList.add(ArmySelections.forAnonymous(CriteriaUtils.arrayUnderlyingType(array1.typeMeta())));
        fieldList.add(ArmySelections.forAnonymous(CriteriaUtils.arrayUnderlyingType(array2.typeMeta())));
        return DialectFunctionUtils.twoArgTabularFunc(name, array1, array2, fieldList);
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
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-array.html#ARRAY-FUNCTIONS-TABLE">unnest ( anyarray, anyarray [, ... ] ) → setof anyelement, anyelement [, ... ]<br/>
     * Expands multiple arrays (possibly of different data types) into a set of rows. If the arrays are not all the same length then the shorter ones are padded with NULLs. This form is only allowed in a query's FROM clause;
     * </a>
     */
    public static _TabularWithOrdinalityFunction unnest(ArrayExpression array1, ArrayExpression array2,
                                                        ArrayExpression array3, ArrayExpression... restArray) {
        final String name = "UNNEST";
        if (array1 instanceof TypeInfer.DelayTypeInfer && ((TypeInfer.DelayTypeInfer) array1).isDelay()) {
            throw CriteriaUtils.tabularFuncErrorPosition(name);
        } else if (array2 instanceof TypeInfer.DelayTypeInfer && ((TypeInfer.DelayTypeInfer) array2).isDelay()) {
            throw CriteriaUtils.tabularFuncErrorPosition(name);
        } else if (array3 instanceof TypeInfer.DelayTypeInfer && ((TypeInfer.DelayTypeInfer) array3).isDelay()) {
            throw CriteriaUtils.tabularFuncErrorPosition(name);
        }

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
                if (array instanceof TypeInfer.DelayTypeInfer && ((TypeInfer.DelayTypeInfer) array).isDelay()) {
                    throw CriteriaUtils.tabularFuncErrorPosition(name);
                }
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
     * </p>
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
            if (exp instanceof TypeInfer.DelayTypeInfer && ((TypeInfer.DelayTypeInfer) exp).isDelay()) {
                throw CriteriaUtils.tabularFuncErrorPosition(name);
            }
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
     * </p>
     *
     * @throws CriteriaException throw when<ul>
     *                           <li><the element of consumer isn't operable {@link Expression},eg:{@link SQLs#DEFAULT}/li>
     *                           </ul>
     * @see <a href="https://www.postgresql.org/docs/current/functions-range.html#RANGE-FUNCTIONS-TABLE">isempty ( anyrange ) → boolean<br/>
     * Is the range empty?<br/>
     * isempty(numrange(1.1,2.2)) → f
     * </a>
     */
    public static SimplePredicate isEmpty(Expression exp) {
        return FunctionUtils.oneArgFuncPredicate("ISEMPTY", exp);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link BooleanType#INSTANCE}.
     * </p>
     *
     * @throws CriteriaException throw when<ul>
     *                           <li><the element of consumer isn't operable {@link Expression},eg:{@link SQLs#DEFAULT}/li>
     *                           </ul>
     * @see <a href="https://www.postgresql.org/docs/current/functions-range.html#RANGE-FUNCTIONS-TABLE">lower_inc ( anyrange ) → boolean<br/>
     * Is the range's lower bound inclusive?<br/>
     * lower_inc(numrange(1.1,2.2)) → t
     * </a>
     */
    public static SimplePredicate lowerInc(Expression exp) {
        return FunctionUtils.oneArgFuncPredicate("LOWER_INC", exp);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link BooleanType#INSTANCE}.
     * </p>
     *
     * @throws CriteriaException throw when<ul>
     *                           <li><the element of consumer isn't operable {@link Expression},eg:{@link SQLs#DEFAULT}/li>
     *                           </ul>
     * @see <a href="https://www.postgresql.org/docs/current/functions-range.html#RANGE-FUNCTIONS-TABLE">upper_inc ( anyrange ) → boolean<br/>
     * Is the range's upper bound inclusive?<br/>
     * upper_inc(numrange(1.1,2.2)) → t
     * </a>
     */
    public static SimplePredicate upperInc(Expression exp) {
        return FunctionUtils.oneArgFuncPredicate("UPPER_INC", exp);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link BooleanType#INSTANCE}.
     * </p>
     *
     * @throws CriteriaException throw when<ul>
     *                           <li><the element of consumer isn't operable {@link Expression},eg:{@link SQLs#DEFAULT}/li>
     *                           </ul>
     * @see <a href="https://www.postgresql.org/docs/current/functions-range.html#RANGE-FUNCTIONS-TABLE">lower_inf ( anyrange ) → boolean<br/>
     * Is the range's lower bound infinite?<br/>
     * lower_inf(numrange(1.1,2.2)) → t
     * </a>
     */
    public static SimplePredicate lowerInf(Expression exp) {
        return FunctionUtils.oneArgFuncPredicate("LOWER_INF", exp);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link BooleanType#INSTANCE}.
     * </p>
     *
     * @throws CriteriaException throw when<ul>
     *                           <li><the element of consumer isn't operable {@link Expression},eg:{@link SQLs#DEFAULT}/li>
     *                           </ul>
     * @see <a href="https://www.postgresql.org/docs/current/functions-range.html#RANGE-FUNCTIONS-TABLE">upper_inf ( anyrange ) → boolean<br/>
     * Is the range's upper bound infinite?<br/>
     * upper_inf(numrange(1.1,2.2)) → t
     * </a>
     */
    public static SimplePredicate upperInf(Expression exp) {
        return FunctionUtils.oneArgFuncPredicate("UPPER_INF", exp);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of range1.
     * </p>
     *
     * @throws CriteriaException throw when<ul>
     *                           <li><the element of consumer isn't operable {@link Expression},eg:{@link SQLs#DEFAULT}/li>
     *                           </ul>
     * @see <a href="https://www.postgresql.org/docs/current/functions-range.html#RANGE-FUNCTIONS-TABLE">range_merge ( anyrange, anyrange ) → anyrange<br/>
     * Computes the smallest range that includes both of the given ranges.<br/>
     * range_merge('[1,2)'::int4range, '[3,4)'::int4range) → [1,4)
     * </a>
     */
    public static SimpleExpression rangeMerge(Expression range1, Expression range2) {
        return FunctionUtils.twoArgFunc("RANGE_MERGE", range1, range2, _returnType(range1, Expressions::identityType));
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

        final String name = "ARRAY_FILL";
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
