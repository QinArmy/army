package io.army.criteria.impl;

import io.army.criteria.Expression;
import io.army.criteria.SQLWords;
import io.army.criteria.SqlValueParam;
import io.army.criteria.TypeInfer;
import io.army.lang.Nullable;
import io.army.mapping.IntegerType;
import io.army.mapping.MappingType;
import io.army.mapping.StringType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

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


    enum KeyWordNormalizeForm implements WordNormalizeForm, ArmyKeyWord, SQLWords {

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

    private enum KeyWordPlacing implements WordPlacing, ArmyKeyWord, SQLWords {

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
     * @see #upper(Expression)
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
            func = FunctionUtils.complexArgFunc(name, exp.typeMeta(), exp, Functions.FuncWord.COMMA, form);
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
     * @see #overlay(Expression, WordPlacing, Expression, WordFrom, Expression, WordFor, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-SQL">overlay ( string text PLACING newsubstring text FROM start integer [ FOR count integer ] ) → text</a>
     */
    public static Expression overlay(Expression string, WordPlacing placing, Expression newSubstring,
                                     WordFrom from, Expression start) {
        return _overlay(string, placing, newSubstring, from, start, Functions.FOR, null);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of string.
     * </p>
     *
     * @see #overlay(Expression, WordPlacing, Expression, WordFrom, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-SQL">overlay ( string text PLACING newsubstring text FROM start integer [ FOR count integer ] ) → text</a>
     */
    public static Expression overlay(Expression string, WordPlacing placing, Expression newSubstring,
                                     WordFrom from, Expression start, WordFor wordFor,
                                     Expression count) {
        ContextStack.assertNonNull(count);
        return _overlay(string, placing, newSubstring, from, start, wordFor, count);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link  IntegerType}.
     * </p>
     *
     * @param in {@link Functions#IN}
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-SQL">position ( substring text IN string text ) → integer</a>
     */
    public static Expression position(Expression substring, WordIn in, Expression string) {
        final String name = "POSITION";
        final Expression func;
        if (substring instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, substring);
        } else if (string instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, string);
        } else if (in != Functions.IN) {
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
     * @param from {@link Functions#FROM}
     * @see #substring(Expression, WordFor, Expression)
     * @see #substring(Expression, WordFrom, Expression, WordFor, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-SQL">substring ( string text [ FROM start integer ] [ FOR count integer ] ) → text ; substring ( string text FROM pattern text ) → text</a>
     */
    public static Expression substring(Expression string, WordFrom from, Expression startOrPattern) {
        ContextStack.assertNonNull(startOrPattern);
        return _substring(string, from, startOrPattern, Functions.FOR, null);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of string.
     * </p>
     *
     * @param wordFor {@link Functions#FOR}
     * @see #substring(Expression, WordFrom, Expression)
     * @see #substring(Expression, WordFrom, Expression, WordFor, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-SQL">substring ( string text [ FROM start integer ] [ FOR count integer ] ) → text</a>
     */
    public static Expression substring(Expression string, WordFor wordFor, Expression count) {
        ContextStack.assertNonNull(count);
        return _substring(string, Functions.FROM, null, wordFor, count);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of string.
     * </p>
     *
     * @param from    {@link Functions#FROM}
     * @param wordFor {@link Functions#FOR}
     * @see #substring(Expression, WordFrom, Expression)
     * @see #substring(Expression, WordFor, Expression)
     * @see #substring(Expression, WordSimilar, Expression, WordEscape, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-SQL">substring ( string text [ FROM start integer ] [ FOR count integer ] ) → text ; substring ( string text FROM pattern text FOR escape text ) → text</a>
     */
    public static Expression substring(Expression string, WordFrom from, Expression startOrPattern,
                                       WordFor wordFor, Expression countOrEscape) {
        ContextStack.assertNonNull(startOrPattern);
        ContextStack.assertNonNull(countOrEscape);
        return _substring(string, from, startOrPattern, wordFor, countOrEscape);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of string.
     * </p>
     *
     * @param similar    {@link Functions#SIMILAR}
     * @param wordEscape {@link Functions#ESCAPE}
     * @see #substring(Expression, WordFrom, Expression, WordFor, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-SQL">substring ( string text SIMILAR pattern text ESCAPE escape text ) → text</a>
     */
    public static Expression substring(Expression string, WordSimilar similar, Expression pattern,
                                       WordEscape wordEscape, Expression escape) {

        final String name = "SUBSTRING";
        final Expression func;
        if (string instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, string);
        } else if (pattern instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, pattern);
        } else if (escape instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, escape);
        } else if (similar != Functions.SIMILAR) {
            throw CriteriaUtils.funcArgError(name, similar);
        } else if (wordEscape != Functions.ESCAPE) {
            throw CriteriaUtils.funcArgError(name, wordEscape);
        } else {
            func = FunctionUtils.complexArgFunc(name, string.typeMeta(),
                    string, similar, pattern, wordEscape, escape);
        }
        return func;
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of string.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-SQL">trim ( [ LEADING | TRAILING | BOTH ] [ FROM ] string text [, characters text ] ) → text</a>
     */
    public static Expression trim(Expression string) {
        return FunctionUtils.oneArgFunc("TRIM", string, string.typeMeta());
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of string.
     * </p>
     *
     * @param from {@link Functions#FROM}
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-SQL">trim ( [ LEADING | TRAILING | BOTH ] [ FROM ] string text [, characters text ] ) → text</a>
     */
    public static Expression trim(WordFrom from, Expression string) {
        final String name = "TRIM";
        if (from != Functions.FROM) {
            throw CriteriaUtils.funcArgError(name, from);
        } else if (string instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, string);
        }
        return FunctionUtils.complexArgFunc(name, string.typeMeta(), from, string);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of string.
     * </p>
     *
     * @param position below:
     *                 <ul>
     *                      <li>{@link Functions#LEADING}</li>
     *                      <li>{@link Functions#TRAILING}</li>
     *                      <li>{@link Functions#BOTH}</li>
     *                 </ul>
     * @param from     {@link Functions#FROM}
     * @see #substring(Expression, WordFrom, Expression, WordFor, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-SQL">trim ( [ LEADING | TRAILING | BOTH ] [ characters text ] FROM string text ) → text</a>
     */
    public static Expression trim(TrimPosition position, WordFrom from, Expression string) {
        final String name = "TRIM";
        if (string instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, string);
        } else if (!(position instanceof Functions.WordTrimPosition)) {
            throw CriteriaUtils.funcArgError(name, position);
        } else if (from != Functions.FROM) {
            throw CriteriaUtils.funcArgError(name, from);
        }
        return FunctionUtils.complexArgFunc(name, string.typeMeta(),
                position, from, string);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of string.
     * </p>
     *
     * @param from {@link Functions#FROM}
     * @see #substring(Expression, WordFrom, Expression, WordFor, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-SQL">trim ( [ LEADING | TRAILING | BOTH ] [ characters text ] FROM string text ) → text</a>
     */
    public static Expression trim(Expression characters, WordFrom from, Expression string) {
        final String name = "TRIM";
        if (characters instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, characters);
        } else if (string instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, string);
        } else if (from != Functions.FROM) {
            throw CriteriaUtils.funcArgError(name, from);
        }
        return FunctionUtils.complexArgFunc(name, string.typeMeta(),
                characters, from, string);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of string.
     * </p>
     *
     * @param position below:
     *                 <ul>
     *                      <li>{@link Functions#LEADING}</li>
     *                      <li>{@link Functions#TRAILING}</li>
     *                      <li>{@link Functions#BOTH}</li>
     *                 </ul>
     * @param from     {@link Functions#FROM}
     * @see #substring(Expression, WordFrom, Expression, WordFor, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-SQL">trim ( [ LEADING | TRAILING | BOTH ] [ characters text ] FROM string text ) → text</a>
     */
    public static Expression trim(TrimPosition position, Expression characters, WordFrom from, Expression string) {
        final String name = "TRIM";
        if (characters instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, characters);
        } else if (string instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, string);
        } else if (!(position instanceof Functions.WordTrimPosition)) {
            throw CriteriaUtils.funcArgError(name, position);
        } else if (from != Functions.FROM) {
            throw CriteriaUtils.funcArgError(name, from);
        }
        return FunctionUtils.complexArgFunc(name, string.typeMeta(),
                position, characters, from, string);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of string.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-SQL">trim ( [ LEADING | TRAILING | BOTH ] [ FROM ] string text [, characters text ] ) → text</a>
     */
    public static Expression trim(Expression string, Expression characters) {
        final String name = "TRIM";
        if (string instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, string);
        } else if (characters instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, characters);
        }
        return FunctionUtils.twoArgFunc(name, string, characters, string.typeMeta());
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of string.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-SQL">trim ( [ LEADING | TRAILING | BOTH ] [ FROM ] string text [, characters text ] ) → text</a>
     */
    public static Expression trim(WordFrom from, Expression string, Expression characters) {
        final String name = "TRIM";
        if (from != Functions.FROM) {
            throw CriteriaUtils.funcArgError(name, from);
        } else if (string instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, string);
        } else if (characters instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, characters);
        }
        return FunctionUtils.complexArgFunc(name, string.typeMeta(),
                from, string, Functions.FuncWord.COMMA, characters);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of string.
     * </p>
     *
     * @param position below:
     *                 <ul>
     *                      <li>{@link Functions#LEADING}</li>
     *                      <li>{@link Functions#TRAILING}</li>
     *                      <li>{@link Functions#BOTH}</li>
     *                 </ul>
     * @param from     {@link Functions#FROM}
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-SQL">trim ( [ LEADING | TRAILING | BOTH ] [ FROM ] string text [, characters text ] ) → text</a>
     */
    public static Expression trim(TrimPosition position, WordFrom from, Expression string, Expression characters) {
        final String name = "TRIM";
        if (string instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, string);
        } else if (characters instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, characters);
        } else if (!(position instanceof Functions.WordTrimPosition)) {
            throw CriteriaUtils.funcArgError(name, position);
        } else if (from != Functions.FROM) {
            throw CriteriaUtils.funcArgError(name, from);
        }
        return FunctionUtils.complexArgFunc(name, string.typeMeta(),
                position, from, string, Functions.FuncWord.COMMA, characters);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link  MappingType} of exp.
     * </p>
     *
     * @see #lower(Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-SQL">upper ( text ) → text</a>
     */
    public static Expression upper(Expression exp) {
        return FunctionUtils.oneArgFunc("UPPER", exp, exp.typeMeta());
    }

    /*-------------------below Other String Functions and Operators -------------------*/

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  IntegerType} .
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-OTHER">ascii ( text ) → integer</a>
     */
    public static Expression ascii(Expression exp) {
        return FunctionUtils.oneArgFunc("ASCII", exp, IntegerType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link  MappingType} of exp.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-OTHER">btrim ( string text [, characters text ] ) → text</a>
     */
    public static Expression btrim(Expression exp) {
        return FunctionUtils.oneArgFunc("BTRIM", exp, exp.typeMeta());
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link  MappingType} of exp.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-OTHER">btrim ( string text [, characters text ] ) → text</a>
     */
    public static Expression btrim(Expression exp, Expression characters) {
        return FunctionUtils.twoArgFunc("BTRIM", exp, characters, exp.typeMeta());
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link StringType}.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-OTHER">chr ( integer ) → text</a>
     */
    public static Expression chr(Expression exp) {
        return FunctionUtils.oneArgFunc("CHR", exp, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link StringType}.
     * </p>
     *
     * @see Expression#concat(Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-OTHER">concat ( val1 "any" [, val2 "any" [, ...] ] ) → text</a>
     */
    public static Expression concat(Expression exp1, Expression... rest) {
        return FunctionUtils.oneAndRestFunc("CONCAT", StringType.INSTANCE, exp1, rest);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link StringType}.
     * </p>
     *
     * @param expList non-null and non-empty.
     * @see Expression#concat(Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-OTHER">concat ( val1 "any" [, val2 "any" [, ...] ] ) → text</a>
     */
    public static Expression concat(List<Expression> expList) {
        return FunctionUtils.multiArgFunc("CONCAT", expList, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link StringType}.
     * </p>
     *
     * @param sep  non-multi param value
     * @param exp1 expression ,possibly be multi param value:
     *             <ul>
     *                 <li>{@link SQLs#multiParams(TypeInfer, Collection)} </li>
     *                 <li>{@link SQLs#multiLiterals(TypeInfer, Collection)}</li>
     *                 <li>{@link SQLs#namedMultiParams(TypeInfer, String, int)} </li>
     *                 <li>{@link SQLs#namedMultiLiterals(TypeInfer, String, int)}</li>
     *             </ul>
     * @param rest element possibly be multi param value:
     *             <ul>
     *                 <li>{@link SQLs#multiParams(TypeInfer, Collection)} </li>
     *                 <li>{@link SQLs#multiLiterals(TypeInfer, Collection)}</li>
     *                 <li>{@link SQLs#namedMultiParams(TypeInfer, String, int)} </li>
     *                 <li>{@link SQLs#namedMultiLiterals(TypeInfer, String, int)}</li>
     *             </ul>
     * @see #concatWs(Expression, List)
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-OTHER">concat_ws ( sep text, val1 "any" [, val2 "any" [, ...] ] ) → text</a>
     */
    public static Expression concatWs(final Expression sep, final Expression exp1, final Expression... rest) {
        final List<Expression> list = new ArrayList<>(1 + rest.length);
        list.add(exp1);
        Collections.addAll(list, rest);
        return concatWs(sep, list);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link StringType}.
     * </p>
     *
     * @param sep     non-multi param value
     * @param expList non-null and non-empty,element possibly be multi param value:
     *                <ul>
     *                    <li>{@link SQLs#multiParams(TypeInfer, Collection)} </li>
     *                    <li>{@link SQLs#multiLiterals(TypeInfer, Collection)}</li>
     *                    <li>{@link SQLs#namedMultiParams(TypeInfer, String, int)} </li>
     *                    <li>{@link SQLs#namedMultiLiterals(TypeInfer, String, int)}</li>
     *                </ul>
     * @see #concatWs(Expression, Expression, Expression...)
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-OTHER">concat_ws ( sep text, val1 "any" [, val2 "any" [, ...] ] ) → text</a>
     */
    public static Expression concatWs(Expression sep, List<Expression> expList) {
        return FunctionUtils.oneAndMultiArgFunc("CONCAT_WS", sep, expList, StringType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of formatStr.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-OTHER">format ( formatstr text [, formatarg "any" [, ...] ] ) → text</a>
     */
    public static Expression format(Expression formatStr) {
        return FunctionUtils.oneArgFunc("FORMAT", formatStr, formatStr.typeMeta());
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of formatStr.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-OTHER">format ( formatstr text [, formatarg "any" [, ...] ] ) → text</a>
     */
    public static Expression format(Expression formatStr, Expression... formatArgs) {
        return FunctionUtils.oneAndRestFunc("FORMAT", formatStr.typeMeta(), formatStr, formatArgs);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of exp.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-OTHER">initcap ( text ) → text</a>
     */
    public static Expression initcap(Expression exp) {
        return FunctionUtils.oneArgFunc("INITCAP", exp, exp.typeMeta());
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of string.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-OTHER">left ( string text, n integer ) → text</a>
     */
    public static Expression left(Expression string, Expression n) {
        return FunctionUtils.twoArgFunc("LEFT", string, n, string.typeMeta());
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of string.
     * </p>
     *
     * @see #lpad(Expression, Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-OTHER">lpad ( string text, length integer [, fill text ] ) → text</a>
     */
    public static Expression lpad(Expression string, Expression length) {
        return FunctionUtils.twoArgFunc("LPAD", string, length, string.typeMeta());
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of string.
     * </p>
     *
     * @see #lpad(Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-OTHER">lpad ( string text, length integer [, fill text ] ) → text</a>
     */
    public static Expression lpad(Expression string, Expression length, Expression fill) {
        return FunctionUtils.threeArgFunc("LPAD", string, length, fill, string.typeMeta());
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of string.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-OTHER">ltrim ( string text [, characters text ] ) → text</a>
     */
    public static Expression ltrim(Expression string) {
        return FunctionUtils.oneArgFunc("LTRIM", string, string.typeMeta());
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of string.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-OTHER">ltrim ( string text [, characters text ] ) → text</a>
     */
    public static Expression ltrim(Expression string, Expression characters) {
        return FunctionUtils.twoArgFunc("LTRIM", string, characters, string.typeMeta());
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of string.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-OTHER">md5 ( text ) → text</a>
     */
    public static Expression md5(Expression string) {
        return FunctionUtils.oneArgFunc("MD5", string, string.typeMeta());
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the array of the {@link MappingType} of qualifiedIdentifier.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-OTHER">parse_ident ( qualified_identifier text [, strict_mode boolean DEFAULT true ] ) → text[]</a>
     */
    public static Expression parseIdent(Expression qualifiedIdentifier) { //TODO array type
        return FunctionUtils.oneArgFunc("parse_ident", qualifiedIdentifier, qualifiedIdentifier.typeMeta());
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the array of the {@link MappingType} of qualifiedIdentifier.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-OTHER">parse_ident ( qualified_identifier text [, strict_mode boolean DEFAULT true ] ) → text[]</a>
     */
    public static Expression parseIdent(Expression qualifiedIdentifier, SQLsSyntax.WordBooleans strictMode) { //TODO array type
        final String name = "PARSE_IDENT";
        if (strictMode != SQLs.TRUE && strictMode != SQLs.FALSE) {
            throw CriteriaUtils.funcArgError(name, strictMode);
        }
        return FunctionUtils.twoArgFunc(name, qualifiedIdentifier, strictMode, qualifiedIdentifier.typeMeta());
    }





    /*-------------------below private method -------------------*/


    /**
     * @see #overlay(Expression, WordPlacing, Expression, WordFrom, Expression)
     * @see #overlay(Expression, WordPlacing, Expression, WordFrom, Expression, WordFor, Expression)
     */
    private static Expression _overlay(Expression string, WordPlacing placing, Expression newSubstring,
                                       WordFrom from, Expression start, @Nullable WordFor wordFor,
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
        } else if (from != Functions.FROM) {
            throw CriteriaUtils.funcArgError(name, from);
        } else if (wordFor != Functions.FOR) {
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
     * @see #substring(Expression, WordFrom, Expression)
     * @see #substring(Expression, WordFor, Expression)
     * @see #substring(Expression, WordFrom, Expression, WordFor, Expression)
     */
    private static Expression _substring(Expression string, WordFrom from, @Nullable Expression start,
                                         WordFor wordFor, @Nullable Expression count) {
        final String name = "SUBSTRING";
        final Expression func;
        if (string instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, string);
        } else if (start instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, start);
        } else if (count instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, count);
        } else if (from != Functions.FROM) {
            throw CriteriaUtils.funcArgError(name, from);
        } else if (wordFor != Functions.FOR) {
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
