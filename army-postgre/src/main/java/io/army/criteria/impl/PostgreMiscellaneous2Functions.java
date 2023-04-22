package io.army.criteria.impl;

import io.army.criteria.Expression;
import io.army.criteria.Selection;
import io.army.criteria.SimpleExpression;
import io.army.lang.Nullable;
import io.army.mapping.*;
import io.army.mapping.optional.TextArrayType;
import io.army.util._Collections;

import java.util.List;

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


}
