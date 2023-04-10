package io.army.criteria.impl;

import io.army.criteria.Expression;
import io.army.criteria.SQLWords;
import io.army.criteria.SqlValueParam;
import io.army.lang.Nullable;
import io.army.mapping.IntegerType;
import io.army.mapping.MappingType;

/**
 * <p>
 * package class,hold postgre string functions and operators method.
 * </p>
 *
 * @see <a href="https://www.postgresql.org/docs/current/functions-string.html">String Functions and Operators</a>
 */
abstract class PostgreStringFunctions extends PostgreFuncSyntax {

    /**
     * package constructor
     */
    PostgreStringFunctions() {
    }

    public interface WordNormalizeForm {

    }

    public interface WordPlacing {

    }


    enum KeyWordNormalizeForm implements WordNormalizeForm, SQLsSyntax.ArmyKeyWord, SQLWords {

        NFC(" NFC"),
        NFD(" NFD"),
        NFKC(" NFKC"),
        NFKD(" NFKD");

        private final String spaceWords;

        KeyWordNormalizeForm(String spaceWords) {
            this.spaceWords = spaceWords;
        }


        @Override
        public final String render() {
            return this.spaceWords;
        }

        @Override
        public final String toString() {
            return PostgreSyntax.keyWordToString(this);
        }

    }//KeyWordNormalizeForm

    private enum KeyWordPlacing implements WordPlacing, SQLsSyntax.ArmyKeyWord, SQLWords {

        PLACING(" PLACING");

        private final String spaceWord;

        KeyWordPlacing(String spaceWord) {
            this.spaceWord = spaceWord;
        }

        @Override
        public final String render() {
            return this.spaceWord;
        }

        @Override
        public final String toString() {
            return PostgreSyntax.keyWordToString(this);
        }


    }//KeyWordPlacing


    public static final WordNormalizeForm NFC = KeyWordNormalizeForm.NFC;

    public static final WordNormalizeForm NFD = KeyWordNormalizeForm.NFD;

    public static final WordNormalizeForm NFKC = KeyWordNormalizeForm.NFKC;

    public static final WordNormalizeForm NFKD = KeyWordNormalizeForm.NFKD;

    public static final WordPlacing PLACING = KeyWordPlacing.PLACING;


    /*-------------------below SQL String Functions and Operators-------------------*/

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  IntegerType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-SQL">bit_length ( text ) → integer</a>
     */
    public static Expression bitLength(Expression exp) {
        return FunctionUtils.oneArgFunc("BIT_LENGTH", exp, IntegerType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  IntegerType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-SQL">char_length ( text ) → integer</a>
     */
    public static Expression charLength(Expression exp) {
        return FunctionUtils.oneArgFunc("CHAR_LENGTH", exp, IntegerType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link  MappingType} of exp.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-SQL">lower ( text ) → text</a>
     */
    public static Expression lower(Expression exp) {
        return FunctionUtils.oneArgFunc("LOWER", exp, exp.typeMeta());
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link  MappingType} of exp.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-SQL">normalize ( text [, form ] ) → text</a>
     */
    public static Expression normalize(Expression exp) {
        return FunctionUtils.oneArgFunc("NORMALIZE", exp, exp.typeMeta());
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link  MappingType} of exp.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-SQL">normalize ( text [, form ] ) → text</a>
     */
    public static Expression normalize(final Expression exp, final WordNormalizeForm form) {
        final String name = "NORMALIZE";
        final Expression func;
        if (exp instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, exp);
        } else if (!(form instanceof KeyWordNormalizeForm)) {
            throw CriteriaUtils.funcArgError(name, form);
        } else {
            func = FunctionUtils.complexArgFunc(name, exp.typeMeta(), exp, FuncWord.COMMA, form);
        }
        return func;
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link  IntegerType}.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-SQL">octet_length ( text ) → integer ; octet_length ( character ) → integer</a>
     */
    public static Expression octetLength(Expression exp) {
        return FunctionUtils.oneArgFunc("OCTET_LENGTH", exp, IntegerType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of string.
     * </p>
     *
     * @see #overlay(Expression, WordPlacing, Expression, SQLsSyntax.WordFrom, Expression, SQLsSyntax.WordFor, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-SQL">overlay ( string text PLACING newsubstring text FROM start integer [ FOR count integer ] ) → text</a>
     */
    public static Expression overlay(Expression string, WordPlacing placing, Expression newSubstring,
                                     SQLsSyntax.WordFrom from, Expression start) {
        return _overlay(string, placing, newSubstring, from, start, SQLsSyntax.FOR, null);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of string.
     * </p>
     *
     * @see #overlay(Expression, WordPlacing, Expression, SQLsSyntax.WordFrom, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-SQL">overlay ( string text PLACING newsubstring text FROM start integer [ FOR count integer ] ) → text</a>
     */
    public static Expression overlay(Expression string, WordPlacing placing, Expression newSubstring,
                                     SQLsSyntax.WordFrom from, Expression start, SQLsSyntax.WordFor wordFor,
                                     Expression count) {
        ContextStack.assertNonNull(count);
        return _overlay(string, placing, newSubstring, from, start, wordFor, count);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link  IntegerType}.
     * </p>
     *
     * @param in {@link SQLsSyntax#IN}
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-SQL">position ( substring text IN string text ) → integer</a>
     */
    public static Expression position(Expression substring, SQLsSyntax.WordIn in, Expression string) {
        final String name = "POSITION";
        final Expression func;
        if (substring instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, substring);
        } else if (string instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, string);
        } else if (in != SQLsSyntax.IN) {
            throw CriteriaUtils.funcArgError(name, in);
        } else {
            func = FunctionUtils.complexArgFunc(name, IntegerType.INSTANCE,
                    substring, in, string);
        }
        return func;
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of string.
     * </p>
     *
     * @param from {@link SQLsSyntax#FROM}
     * @see #substring(Expression, SQLsSyntax.WordFor, Expression)
     * @see #substring(Expression, SQLsSyntax.WordFrom, Expression, SQLsSyntax.WordFor, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-SQL">substring ( string text [ FROM start integer ] [ FOR count integer ] ) → text ; substring ( string text FROM pattern text ) → text</a>
     */
    public static Expression substring(Expression string, SQLsSyntax.WordFrom from, Expression startOrPattern) {
        ContextStack.assertNonNull(startOrPattern);
        return _substring(string, from, startOrPattern, SQLsSyntax.FOR, null);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of string.
     * </p>
     *
     * @param wordFor {@link SQLsSyntax#FOR}
     * @see #substring(Expression, SQLsSyntax.WordFrom, Expression)
     * @see #substring(Expression, SQLsSyntax.WordFrom, Expression, SQLsSyntax.WordFor, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-SQL">substring ( string text [ FROM start integer ] [ FOR count integer ] ) → text</a>
     */
    public static Expression substring(Expression string, SQLsSyntax.WordFor wordFor, Expression count) {
        ContextStack.assertNonNull(count);
        return _substring(string, SQLsSyntax.FROM, null, wordFor, count);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of string.
     * </p>
     *
     * @param from    {@link SQLsSyntax#FROM}
     * @param wordFor {@link SQLsSyntax#FOR}
     * @see #substring(Expression, SQLsSyntax.WordFrom, Expression)
     * @see #substring(Expression, SQLsSyntax.WordFor, Expression)
     * @see #substring(Expression, SQLsSyntax.WordSimilar, Expression, SQLsSyntax.WordEscape, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-SQL">substring ( string text [ FROM start integer ] [ FOR count integer ] ) → text ; substring ( string text FROM pattern text FOR escape text ) → text</a>
     */
    public static Expression substring(Expression string, SQLsSyntax.WordFrom from, Expression startOrPattern,
                                       SQLsSyntax.WordFor wordFor, Expression countOrEscape) {
        ContextStack.assertNonNull(startOrPattern);
        ContextStack.assertNonNull(countOrEscape);
        return _substring(string, from, startOrPattern, wordFor, countOrEscape);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of string.
     * </p>
     *
     * @param similar    {@link SQLsSyntax#SIMILAR}
     * @param wordEscape {@link SQLsSyntax#ESCAPE}
     * @see #substring(Expression, SQLsSyntax.WordFrom, Expression, SQLsSyntax.WordFor, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-SQL">substring ( string text SIMILAR pattern text ESCAPE escape text ) → text</a>
     */
    public static Expression substring(Expression string, SQLsSyntax.WordSimilar similar, Expression pattern,
                                       SQLsSyntax.WordEscape wordEscape, Expression escape) {

        final String name = "SUBSTRING";
        final Expression func;
        if (string instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, string);
        } else if (pattern instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, pattern);
        } else if (escape instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, escape);
        } else if (similar != SQLsSyntax.SIMILAR) {
            throw CriteriaUtils.funcArgError(name, similar);
        } else if (wordEscape != SQLsSyntax.ESCAPE) {
            throw CriteriaUtils.funcArgError(name, wordEscape);
        } else {
            func = FunctionUtils.complexArgFunc(name, string.typeMeta(),
                    string, similar, pattern, wordEscape, escape);
        }
        return func;
    }


    /*-------------------below private method -------------------*/


    /**
     * @see #overlay(Expression, WordPlacing, Expression, SQLsSyntax.WordFrom, Expression)
     * @see #overlay(Expression, WordPlacing, Expression, SQLsSyntax.WordFrom, Expression, SQLsSyntax.WordFor, Expression)
     */
    private static Expression _overlay(Expression string, WordPlacing placing, Expression newSubstring,
                                       SQLsSyntax.WordFrom from, Expression start, @Nullable SQLsSyntax.WordFor wordFor,
                                       @Nullable Expression count) {
        final String name = "OVERLAY";
        final Expression func;
        if (string instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, string);
        } else if (newSubstring instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, newSubstring);
        } else if (start instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, start);
        } else if (count instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, count);
        } else if (placing != PLACING) {
            throw CriteriaUtils.funcArgError(name, placing);
        } else if (from != SQLsSyntax.FROM) {
            throw CriteriaUtils.funcArgError(name, from);
        } else if (wordFor != SQLsSyntax.FOR) {
            throw CriteriaUtils.funcArgError(name, wordFor);
        } else if (count == null) {
            func = FunctionUtils.complexArgFunc(name, string.typeMeta(),
                    string, placing, newSubstring, from, start);
        } else {
            func = FunctionUtils.complexArgFunc(name, string.typeMeta(),
                    string, placing, newSubstring, from, start, wordFor, count);
        }
        return func;
    }


    /**
     * @see #substring(Expression, SQLsSyntax.WordFrom, Expression)
     * @see #substring(Expression, SQLsSyntax.WordFor, Expression)
     * @see #substring(Expression, SQLsSyntax.WordFrom, Expression, SQLsSyntax.WordFor, Expression)
     */
    private static Expression _substring(Expression string, SQLsSyntax.WordFrom from, @Nullable Expression start,
                                         SQLsSyntax.WordFor wordFor, @Nullable Expression count) {
        final String name = "SUBSTRING";
        final Expression func;
        if (string instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, string);
        } else if (start instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, start);
        } else if (count instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, count);
        } else if (from != SQLsSyntax.FROM) {
            throw CriteriaUtils.funcArgError(name, from);
        } else if (wordFor != SQLsSyntax.FOR) {
            throw CriteriaUtils.funcArgError(name, wordFor);
        } else if (start != null && count != null) {
            func = FunctionUtils.complexArgFunc(name, string.typeMeta(),
                    string, from, start, wordFor, count);
        } else if (start != null) {
            func = FunctionUtils.complexArgFunc(name, string.typeMeta(),
                    string, from, start);
        } else if (count != null) {
            func = FunctionUtils.complexArgFunc(name, string.typeMeta(),
                    string, wordFor, count);
        } else {
            //no bug,never here
            throw new IllegalArgumentException();
        }
        return func;

    }


}
