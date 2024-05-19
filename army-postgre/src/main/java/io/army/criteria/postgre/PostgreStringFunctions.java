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
import io.army.criteria.impl.*;
import io.army.criteria.standard.SQLs;
import io.army.mapping.*;
import io.army.mapping.array.TextArrayType;
import io.army.mapping.postgre.PostgreRangeType;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.BiFunction;

/**
 * <p>
 * package class,hold postgre string functions and operators method.
*
 * @see <a href="https://www.postgresql.org/docs/current/functions-string.html">String Functions and Operators</a>
 */
abstract class PostgreStringFunctions extends Functions {

    /**
     * package constructor
     */
    PostgreStringFunctions() {
    }

    public interface WordNormalizeForm {

    }

    public interface WordPlacing {

    }

    public interface XmlAttributes extends Item {

    }

    public interface WordVersion extends SQLWords {

    }

    public interface WordStandalone extends SQLWords {

    }

    public interface StandaloneOption extends SQLWords {

    }

    public interface WordsNoValue extends StandaloneOption {

    }

    public interface WordPassing extends SQLWords {

    }

    public interface PassingOption extends SQLWords {

    }


    enum KeyWordNormalizeForm implements WordNormalizeForm, SqlWords.ArmyKeyWord, SQLWords {

        NFC(" NFC"),
        NFD(" NFD"),
        NFKC(" NFKC"),
        NFKD(" NFKD");

        private final String spaceWords;

        KeyWordNormalizeForm(String spaceWords) {
            this.spaceWords = spaceWords;
        }


        @Override
        public final String spaceRender() {
            return this.spaceWords;
        }

        @Override
        public final String toString() {
            return PostgreSyntax.keyWordToString(this);
        }

    }//KeyWordNormalizeForm

    private enum KeyWordPlacing implements WordPlacing, SqlWords.ArmyKeyWord, SQLWords {

        PLACING(" PLACING");

        private final String spaceWord;

        KeyWordPlacing(String spaceWord) {
            this.spaceWord = spaceWord;
        }

        @Override
        public final String spaceRender() {
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
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-SQL">bit_length ( text ) → integer</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-bitstring.html#FUNCTIONS-BIT-STRING-TABLE">bit_length ( bit ) → integer</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-binarystring.html#FUNCTIONS-BINARYSTRING-SQL">bit_length ( bytea ) → integer</a>
     */
    public static SimpleExpression bitLength(Expression exp) {
        return LiteralFunctions.oneArgFunc("BIT_LENGTH", exp, IntegerType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  IntegerType}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-SQL">char_length ( text ) → integer</a>
     */
    public static SimpleExpression charLength(Expression exp) {
        return LiteralFunctions.oneArgFunc("CHAR_LENGTH", exp, IntegerType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  TextType} .
     *
     *
     * @see #upper(Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-SQL">lower ( text ) → text</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-range.html#RANGE-FUNCTIONS-TABLE">lower ( anyrange ) → anyelement</a>
     */
    public static SimpleExpression lower(Expression exp) {
        return LiteralFunctions.oneArgFunc("LOWER", exp, _returnType(exp, PostgreStringFunctions::lowerOrUpperType));
    }



    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link  MappingType} of exp.
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-SQL">normalize ( text [, form ] ) → text</a>
     */
    public static SimpleExpression normalize(Expression exp) {
        return LiteralFunctions.oneArgFunc("NORMALIZE", exp, exp.typeMeta());
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link  MappingType} of exp.
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-SQL">normalize ( text [, form ] ) → text</a>
     */
    public static SimpleExpression normalize(final Expression exp, final WordNormalizeForm form) {
        final String name = "NORMALIZE";
        final SimpleExpression func;
        if (exp instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, exp);
        } else if (!(form instanceof KeyWordNormalizeForm)) {
            throw CriteriaUtils.funcArgError(name, form);
        } else {
            func = FunctionUtils.complexArgFunc(name, exp.typeMeta(), exp, SqlWords.FuncWord.COMMA, form);
        }
        return func;
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link  IntegerType}.
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-SQL">octet_length ( text ) → integer ; octet_length ( character ) → integer</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-bitstring.html#FUNCTIONS-BIT-STRING-TABLE">octet_length ( bit ) → integer</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-binarystring.html#FUNCTIONS-BINARYSTRING-SQL">octet_length ( bytea ) → integer</a>
     */
    public static SimpleExpression octetLength(Expression exp) {
        return FunctionUtils.oneArgFunc("OCTET_LENGTH", exp, IntegerType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of string.
     *
     *
     * @see #overlay(Expression, WordPlacing, Expression, SQLs.WordFrom, Expression, SQLs.WordFor, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-SQL">overlay ( string text PLACING newsubstring text FROM start integer [ FOR count integer ] ) → text</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-bitstring.html#FUNCTIONS-BIT-STRING-TABLE">overlay ( bits bit PLACING newsubstring bit FROM start integer [ FOR count integer ] ) → bit</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-binarystring.html#FUNCTIONS-BINARYSTRING-SQL">overlay ( bytes bytea PLACING newsubstring bytea FROM start integer [ FOR count integer ] ) → bytea</a>
     */
    public static SimpleExpression overlay(Expression string, WordPlacing placing, Expression newSubstring,
                                           SQLs.WordFrom from, Expression start) {
        return _overlay(string, placing, newSubstring, from, start, SQLs.FOR, null);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of string.
     *
     *
     * @see #overlay(Expression, WordPlacing, Expression, SQLs.WordFrom, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-SQL">overlay ( string text PLACING newsubstring text FROM start integer [ FOR count integer ] ) → text</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-bitstring.html#FUNCTIONS-BIT-STRING-TABLE">overlay ( bits bit PLACING newsubstring bit FROM start integer [ FOR count integer ] ) → bit</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-binarystring.html#FUNCTIONS-BINARYSTRING-SQL">overlay ( bytes bytea PLACING newsubstring bytea FROM start integer [ FOR count integer ] ) → bytea</a>
     */
    public static SimpleExpression overlay(Expression string, WordPlacing placing, Expression newSubstring,
                                           SQLs.WordFrom from, Expression start, SQLs.WordFor wordFor,
                                           Expression count) {
        ContextStack.assertNonNull(count);
        return _overlay(string, placing, newSubstring, from, start, wordFor, count);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link  IntegerType}.
     *
     *
     * @param in {@link SQLs#IN}
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-SQL">position ( substring text IN string text ) → integer</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-bitstring.html#FUNCTIONS-BIT-STRING-TABLE">position ( substring bit IN bits bit ) → integer</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-binarystring.html#FUNCTIONS-BINARYSTRING-SQL">position ( substring bytea IN bytes bytea ) → integer</a>
     */
    public static SimpleExpression position(Expression substring, SQLs.WordIn in, Expression string) {
        final String name = "POSITION";
        final SimpleExpression func;
        if (substring instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, substring);
        } else if (string instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, string);
        } else if (in != SQLs.IN) {
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
     *
     *
     * @param from {@link SQLs#FROM}
     * @see #substring(Expression, SQLs.WordFor, Expression)
     * @see #substring(Expression, SQLs.WordFrom, Expression, SQLs.WordFor, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-SQL">substring ( string text [ FROM start integer ] [ FOR count integer ] ) → text ; substring ( string text FROM pattern text ) → text</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-bitstring.html#FUNCTIONS-BIT-STRING-TABLE">substring ( bits bit [ FROM start integer ] [ FOR count integer ] ) → bit</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-binarystring.html#FUNCTIONS-BINARYSTRING-SQL">substring ( bytes bytea [ FROM start integer ] [ FOR count integer ] ) → bytea</a>
     */
    public static SimpleExpression substring(Expression string, SQLs.WordFrom from, Expression startOrPattern) {
        ContextStack.assertNonNull(startOrPattern);
        return _substring(string, from, startOrPattern, SQLs.FOR, null);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of string.
     *
     *
     * @param wordFor {@link SQLs#FOR}
     * @see #substring(Expression, SQLs.WordFrom, Expression)
     * @see #substring(Expression, SQLs.WordFrom, Expression, SQLs.WordFor, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-SQL">substring ( string text [ FROM start integer ] [ FOR count integer ] ) → text</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-bitstring.html#FUNCTIONS-BIT-STRING-TABLE">substring ( bits bit [ FROM start integer ] [ FOR count integer ] ) → bit</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-binarystring.html#FUNCTIONS-BINARYSTRING-SQL">substring ( bytes bytea [ FROM start integer ] [ FOR count integer ] ) → bytea</a>
     */
    public static SimpleExpression substring(Expression string, SQLs.WordFor wordFor, Expression count) {
        ContextStack.assertNonNull(count);
        return _substring(string, SQLs.FROM, null, wordFor, count);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of string.
     *
     *
     * @param from    {@link SQLs#FROM}
     * @param wordFor {@link SQLs#FOR}
     * @see #substring(Expression, SQLs.WordFrom, Expression)
     * @see #substring(Expression, SQLs.WordFor, Expression)
     * @see #substring(Expression, SQLs.WordSimilar, Expression, SQLs.WordEscape, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-SQL">substring ( string text [ FROM start integer ] [ FOR count integer ] ) → text ; substring ( string text FROM pattern text FOR escape text ) → text</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-bitstring.html#FUNCTIONS-BIT-STRING-TABLE">substring ( bits bit [ FROM start integer ] [ FOR count integer ] ) → bit</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-binarystring.html#FUNCTIONS-BINARYSTRING-SQL">substring ( bytes bytea [ FROM start integer ] [ FOR count integer ] ) → bytea</a>
     */
    public static SimpleExpression substring(Expression string, SQLs.WordFrom from, Expression startOrPattern,
                                             SQLs.WordFor wordFor, Expression countOrEscape) {
        ContextStack.assertNonNull(startOrPattern);
        ContextStack.assertNonNull(countOrEscape);
        return _substring(string, from, startOrPattern, wordFor, countOrEscape);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of string.
     *
     *
     * @param similar    {@link SQLs#SIMILAR}
     * @param wordEscape {@link SQLs#ESCAPE}
     * @see #substring(Expression, SQLs.WordFrom, Expression, SQLs.WordFor, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-SQL">substring ( string text SIMILAR pattern text ESCAPE escape text ) → text</a>
     */
    public static SimpleExpression substring(Expression string, SQLs.WordSimilar similar, Expression pattern,
                                             SQLs.WordEscape wordEscape, Expression escape) {

        final String name = "SUBSTRING";
        final SimpleExpression func;
        if (string instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, string);
        } else if (pattern instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, pattern);
        } else if (escape instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, escape);
        } else if (similar != SQLs.SIMILAR) {
            throw CriteriaUtils.funcArgError(name, similar);
        } else if (wordEscape != SQLs.ESCAPE) {
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
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-SQL">trim ( [ LEADING | TRAILING | BOTH ] [ FROM ] string text [, characters text ] ) → text</a>
     */
    public static SimpleExpression trim(Expression string) {
        return FunctionUtils.oneArgFunc("TRIM", string, string.typeMeta());
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of string.
     *
     *
     * @param from {@link SQLs#FROM}
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-SQL">trim ( [ LEADING | TRAILING | BOTH ] [ FROM ] string text [, characters text ] ) → text</a>
     */
    public static SimpleExpression trim(SQLs.WordFrom from, Expression string) {
        final String name = "TRIM";
        if (from != SQLs.FROM) {
            throw CriteriaUtils.funcArgError(name, from);
        } else if (string instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, string);
        }
        return FunctionUtils.complexArgFunc(name, string.typeMeta(), from, string);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of string.
     *
     *
     * @param position below:
     *                 <ul>
     *                      <li>{@link SQLs#LEADING}</li>
     *                      <li>{@link SQLs#TRAILING}</li>
     *                      <li>{@link SQLs#BOTH}</li>
     *                 </ul>
     * @param from     {@link SQLs#FROM}
     * @see #substring(Expression, SQLs.WordFrom, Expression, SQLs.WordFor, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-SQL">trim ( [ LEADING | TRAILING | BOTH ] [ characters text ] FROM string text ) → text</a>
     */
    public static SimpleExpression trim(SQLs.TrimSpec position, SQLs.WordFrom from, Expression string) {
        final String name = "TRIM";
        if (string instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, string);
        } else if (!(position instanceof SqlWords.WordTrimPosition)) {
            throw CriteriaUtils.funcArgError(name, position);
        } else if (from != SQLs.FROM) {
            throw CriteriaUtils.funcArgError(name, from);
        }
        return FunctionUtils.complexArgFunc(name, string.typeMeta(),
                position, from, string);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of string.
     *
     *
     * @param from {@link SQLs#FROM}
     * @see #substring(Expression, SQLs.WordFrom, Expression, SQLs.WordFor, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-SQL">trim ( [ LEADING | TRAILING | BOTH ] [ characters text ] FROM string text ) → text</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-binarystring.html#FUNCTIONS-BINARYSTRING-SQL">trim ( [ LEADING | TRAILING | BOTH ] bytesremoved bytea FROM bytes bytea ) → bytea</a>
     */
    public static SimpleExpression trim(Expression characters, SQLs.WordFrom from, Expression string) {
        final String name = "TRIM";
        if (characters instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, characters);
        } else if (string instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, string);
        } else if (from != SQLs.FROM) {
            throw CriteriaUtils.funcArgError(name, from);
        }
        return FunctionUtils.complexArgFunc(name, string.typeMeta(),
                characters, from, string);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of string.
     *
     *
     * @param position below:
     *                 <ul>
     *                      <li>{@link SQLs#LEADING}</li>
     *                      <li>{@link SQLs#TRAILING}</li>
     *                      <li>{@link SQLs#BOTH}</li>
     *                 </ul>
     * @param from     {@link SQLs#FROM}
     * @see #substring(Expression, SQLs.WordFrom, Expression, SQLs.WordFor, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-SQL">trim ( [ LEADING | TRAILING | BOTH ] [ characters text ] FROM string text ) → text</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-binarystring.html#FUNCTIONS-BINARYSTRING-SQL">trim ( [ LEADING | TRAILING | BOTH ] bytesremoved bytea FROM bytes bytea ) → bytea</a>
     */
    public static SimpleExpression trim(SQLs.TrimSpec position, Expression characters, SQLs.WordFrom from, Expression string) {
        final String name = "TRIM";
        if (characters instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, characters);
        } else if (string instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, string);
        } else if (!(position instanceof SqlWords.WordTrimPosition)) {
            throw CriteriaUtils.funcArgError(name, position);
        } else if (from != SQLs.FROM) {
            throw CriteriaUtils.funcArgError(name, from);
        }
        return FunctionUtils.complexArgFunc(name, string.typeMeta(),
                position, characters, from, string);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of string.
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-SQL">trim ( [ LEADING | TRAILING | BOTH ] [ FROM ] string text [, characters text ] ) → text</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-binarystring.html#FUNCTIONS-BINARYSTRING-SQL">trim ( [ LEADING | TRAILING | BOTH ] [ FROM ] bytes bytea, bytesremoved bytea ) → bytea</a>
     */
    public static SimpleExpression trim(Expression string, Expression characters) {
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
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-SQL">trim ( [ LEADING | TRAILING | BOTH ] [ FROM ] string text [, characters text ] ) → text</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-binarystring.html#FUNCTIONS-BINARYSTRING-SQL">trim ( [ LEADING | TRAILING | BOTH ] [ FROM ] bytes bytea, bytesremoved bytea ) → bytea</a>
     */
    public static SimpleExpression trim(SQLs.WordFrom from, Expression string, Expression characters) {
        final String name = "TRIM";
        if (from != SQLs.FROM) {
            throw CriteriaUtils.funcArgError(name, from);
        } else if (string instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, string);
        } else if (characters instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, characters);
        }
        return FunctionUtils.complexArgFunc(name, string.typeMeta(),
                from, string, SqlWords.FuncWord.COMMA, characters);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of string.
     *
     *
     * @param position below:
     *                 <ul>
     *                      <li>{@link SQLs#LEADING}</li>
     *                      <li>{@link SQLs#TRAILING}</li>
     *                      <li>{@link SQLs#BOTH}</li>
     *                 </ul>
     * @param from     {@link SQLs#FROM}
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-SQL">trim ( [ LEADING | TRAILING | BOTH ] [ FROM ] string text [, characters text ] ) → text</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-binarystring.html#FUNCTIONS-BINARYSTRING-SQL">trim ( [ LEADING | TRAILING | BOTH ] [ FROM ] bytes bytea, bytesremoved bytea ) → bytea</a>
     */
    public static SimpleExpression trim(SQLs.TrimSpec position, SQLs.WordFrom from, Expression string, Expression characters) {
        final String name = "TRIM";
        if (string instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, string);
        } else if (characters instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, characters);
        } else if (!(position instanceof SqlWords.WordTrimPosition)) {
            throw CriteriaUtils.funcArgError(name, position);
        } else if (from != SQLs.FROM) {
            throw CriteriaUtils.funcArgError(name, from);
        }
        return FunctionUtils.complexArgFunc(name, string.typeMeta(),
                position, from, string, SqlWords.FuncWord.COMMA, characters);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  TextType} .
     *
     *
     * @see #lower(Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-SQL">upper ( text ) → text</a>
     */
    public static SimpleExpression upper(Expression exp) {
        return FunctionUtils.oneArgFunc("UPPER", exp, _returnType(exp, PostgreStringFunctions::lowerOrUpperType));
    }

    /*-------------------below Other String Functions and Operators -------------------*/

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  IntegerType} .
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-OTHER">ascii ( text ) → integer</a>
     */
    public static SimpleExpression ascii(Expression exp) {
        return FunctionUtils.oneArgFunc("ASCII", exp, IntegerType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link  MappingType} of exp.
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-OTHER">btrim ( string text [, characters text ] ) → text</a>
     */
    public static SimpleExpression btrim(Expression exp) {
        return FunctionUtils.oneArgFunc("BTRIM", exp, exp.typeMeta());
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link  MappingType} of exp.
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-OTHER">btrim ( string text [, characters text ] ) → text</a>
     */
    public static SimpleExpression btrim(Expression exp, Expression characters) {
        return FunctionUtils.twoArgFunc("BTRIM", exp, characters, exp.typeMeta());
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link StringType}.
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-OTHER">chr ( integer ) → text</a>
     */
    public static SimpleExpression chr(Expression exp) {
        return FunctionUtils.oneArgFunc("CHR", exp, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link StringType}.
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-OTHER">concat ( val1 "any" [, val2 "any" [, ...] ] ) → text</a>
     */
    public static SimpleExpression concat(Expression exp1, Expression... rest) {
        return FunctionUtils.oneAndRestFunc("CONCAT", StringType.INSTANCE, exp1, rest);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link StringType}.
     *
     *
     * @param expList non-null and non-empty.
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-OTHER">concat ( val1 "any" [, val2 "any" [, ...] ] ) → text</a>
     */
    public static SimpleExpression concat(List<Expression> expList) {
        return FunctionUtils.multiArgFunc("CONCAT", expList, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link StringType}.
     *
     *
     * @param sep  non-multi param value
     * @param exp1 expression ,possibly be multi param value:
     *             <ul>
     *                 <li>{@link SQLs#rowParam(TypeInfer, Collection)} </li>
     *                 <li>{@link SQLs#rowLiteral(TypeInfer, Collection)}</li>
     *                 <li>{@link SQLs#namedRowParam(TypeInfer, String, int)} </li>
     *                 <li>{@link SQLs#namedRowLiteral(TypeInfer, String, int)}</li>
     *             </ul>
     * @param rest element possibly be multi param value:
     *             <ul>
     *                 <li>{@link SQLs#rowParam(TypeInfer, Collection)} </li>
     *                 <li>{@link SQLs#rowLiteral(TypeInfer, Collection)}</li>
     *                 <li>{@link SQLs#namedRowParam(TypeInfer, String, int)} </li>
     *                 <li>{@link SQLs#namedRowLiteral(TypeInfer, String, int)}</li>
     *             </ul>
     * @see #concatWs(Expression, List)
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-OTHER">concat_ws ( sep text, val1 "any" [, val2 "any" [, ...] ] ) → text</a>
     */
    public static SimpleExpression concatWs(final Expression sep, final Expression exp1, final Expression... rest) {
        final List<Expression> list = new ArrayList<>(1 + rest.length);
        list.add(exp1);
        Collections.addAll(list, rest);
        return concatWs(sep, list);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link StringType}.
     *
     *
     * @param sep     non-multi param value
     * @param expList non-null and non-empty,element possibly be multi param value:
     *                <ul>
     *                    <li>{@link SQLs#rowParam(TypeInfer, Collection)} </li>
     *                    <li>{@link SQLs#rowLiteral(TypeInfer, Collection)}</li>
     *                    <li>{@link SQLs#namedRowParam(TypeInfer, String, int)} </li>
     *                    <li>{@link SQLs#namedRowLiteral(TypeInfer, String, int)}</li>
     *                </ul>
     * @see #concatWs(Expression, Expression, Expression...)
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-OTHER">concat_ws ( sep text, val1 "any" [, val2 "any" [, ...] ] ) → text</a>
     */
    public static SimpleExpression concatWs(Expression sep, List<Expression> expList) {
        return FunctionUtils.oneAndMultiArgFunc("CONCAT_WS", sep, expList, StringType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of formatStr.
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-OTHER">format ( formatstr text [, formatarg "any" [, ...] ] ) → text</a>
     */
    public static SimpleExpression format(Expression formatStr) {
        return FunctionUtils.oneArgFunc("FORMAT", formatStr, formatStr.typeMeta());
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of formatStr.
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-OTHER">format ( formatstr text [, formatarg "any" [, ...] ] ) → text</a>
     */
    public static SimpleExpression format(Expression formatStr, Expression... formatArgs) {
        return FunctionUtils.oneAndRestFunc("FORMAT", formatStr.typeMeta(), formatStr, formatArgs);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of exp.
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-OTHER">initcap ( text ) → text</a>
     */
    public static SimpleExpression initcap(Expression exp) {
        return FunctionUtils.oneArgFunc("INITCAP", exp, exp.typeMeta());
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of string.
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-OTHER">left ( string text, n integer ) → text</a>
     */
    public static SimpleExpression left(Expression string, Expression n) {
        return FunctionUtils.twoArgFunc("LEFT", string, n, string.typeMeta());
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of string.
     *
     *
     * @see #lpad(Expression, Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-OTHER">lpad ( string text, length integer [, fill text ] ) → text</a>
     */
    public static SimpleExpression lpad(Expression string, Expression length) {
        return FunctionUtils.twoArgFunc("LPAD", string, length, string.typeMeta());
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of string.
     *
     *
     * @see #lpad(Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-OTHER">lpad ( string text, length integer [, fill text ] ) → text</a>
     */
    public static SimpleExpression lpad(Expression string, Expression length, Expression fill) {
        return FunctionUtils.threeArgFunc("LPAD", string, length, fill, string.typeMeta());
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of string.
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-OTHER">ltrim ( string text [, characters text ] ) → text</a>
     */
    public static SimpleExpression ltrim(Expression string) {
        return FunctionUtils.oneArgFunc("LTRIM", string, string.typeMeta());
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of string.
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-OTHER">ltrim ( string text [, characters text ] ) → text</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-binarystring.html#FUNCTIONS-BINARYSTRING-OTHER">ltrim ( bytes bytea, bytesremoved bytea ) → bytea</a>
     */
    public static SimpleExpression ltrim(Expression string, Expression characters) {
        return FunctionUtils.twoArgFunc("LTRIM", string, characters, string.typeMeta());
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link TextType}.
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-OTHER">md5 ( text ) → text</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-binarystring.html#FUNCTIONS-BINARYSTRING-OTHER">md5 ( bytea ) → text</a>
     */
    public static SimpleExpression md5(Expression string) {
        return FunctionUtils.oneArgFunc("MD5", string, TextType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link TextArrayType} .
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-OTHER">parse_ident ( qualified_identifier text [, strict_mode boolean DEFAULT true ] ) → text[]</a>
     */
    public static SimpleExpression parseIdent(Expression qualifiedIdentifier) {
        return FunctionUtils.oneArgFunc("PARSE_IDENT", qualifiedIdentifier, TextArrayType.from(String[].class));
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link TextArrayType} .
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-OTHER">parse_ident ( qualified_identifier text [, strict_mode boolean DEFAULT true ] ) → text[]</a>
     */
    public static SimpleExpression parseIdent(Expression qualifiedIdentifier, SQLs.WordBooleans strictMode) {
        final String name = "PARSE_IDENT";
        if (strictMode != SQLs.TRUE && strictMode != SQLs.FALSE) {
            throw CriteriaUtils.funcArgError(name, strictMode);
        }
        return FunctionUtils.twoArgFunc(name, qualifiedIdentifier, strictMode, TextArrayType.from(String[].class));
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link StringType} .
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-OTHER">pg_client_encoding ( ) → name</a>
     */
    public static SimpleExpression pgClientEncoding() {
        return FunctionUtils.zeroArgFunc("PG_CLIENT_ENCODING", StringType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:
     * <ul>
     *     <li>If the {@link MappingType} of anyElement is string type,then the {@link MappingType} of anyElement</li>
     *     <li>Else {@link StringType}</li>
     * </ul>
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-OTHER">quote_literal ( anyelement ) → text</a>
     */
    public static SimpleExpression quoteLiteral(Expression anyElement) {
        return FunctionUtils.oneArgFunc("QUOTE_LITERAL", anyElement, _returnType(anyElement, Functions::_sqlStringType));
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:
     * <ul>
     *     <li>If the {@link MappingType} of anyElement is string type,then the {@link MappingType} of anyElement</li>
     *     <li>Else {@link StringType}</li>
     * </ul>
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-OTHER">quote_nullable ( anyelement ) → text</a>
     */
    public static SimpleExpression quoteNullable(Expression anyElement) {
        return FunctionUtils.oneArgFunc("QUOTE_NULLABLE", anyElement, _returnType(anyElement, Functions::_sqlStringType));
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link IntegerType}
     *
     *
     * @see #regexpCount(Expression, Expression, Expression)
     * @see #regexpCount(Expression, Expression, Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-OTHER">regexp_count ( string text, pattern text [, start integer [, flags text ] ] ) → integer</a>
     */
    public static SimpleExpression regexpCount(Expression string, Expression pattern) {
        return FunctionUtils.twoArgFunc("REGEXP_COUNT", string, pattern, IntegerType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link IntegerType}
     *
     *
     * @see #regexpCount(Expression, Expression)
     * @see #regexpCount(Expression, Expression, Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-OTHER">regexp_count ( string text, pattern text [, start integer [, flags text ] ] ) → integer</a>
     */
    public static SimpleExpression regexpCount(Expression string, Expression pattern, Expression start) {
        return FunctionUtils.threeArgFunc("REGEXP_COUNT", string, pattern, start, IntegerType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link IntegerType}
     *
     *
     * @see #regexpCount(Expression, Expression)
     * @see #regexpCount(Expression, Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-OTHER">regexp_count ( string text, pattern text [, start integer [, flags text ] ] ) → integer</a>
     */
    public static SimpleExpression regexpCount(Expression string, Expression pattern, Expression start, Expression flags) {
        return FunctionUtils.fourArgFunc("REGEXP_COUNT", string, pattern, start, flags, IntegerType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link io.army.mapping.BooleanType}
     *
     *
     * @param valueFunc valid function are :
     *                  <ul>
     *                      <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                      <li>{@link SQLs#param(TypeInfer, Object)} </li>
     *                      <li>{@link SQLs#namedLiteral(TypeInfer, String)}</li>
     *                      <li>{@link SQLs#namedParam(TypeInfer, String)}</li>
     *                  </ul>
     * @see #regexpLike(Expression, Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-OTHER">regexp_like ( string text, pattern text [, flags text ] ) → boolean</a>
     */
    public static IPredicate regexpLike(Expression string, BiFunction<MappingType, String, Expression> valueFunc,
                                        String pattern) {
        return regexpLike(string, valueFunc.apply(StringType.INSTANCE, pattern));
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link io.army.mapping.BooleanType}
     *
     *
     * @see #regexpLike(Expression, Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-OTHER">regexp_like ( string text, pattern text [, flags text ] ) → boolean</a>
     */
    public static IPredicate regexpLike(Expression string, Expression pattern) {
        return FunctionUtils.twoArgPredicateFunc("REGEXP_LIKE", string, pattern);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link io.army.mapping.BooleanType}
     *
     *
     * @param valueFunc valid function are :
     *                  <ul>
     *                      <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                      <li>{@link SQLs#param(TypeInfer, Object)} </li>
     *                      <li>{@link SQLs#namedLiteral(TypeInfer, String)}</li>
     *                      <li>{@link SQLs#namedParam(TypeInfer, String)}</li>
     *                  </ul>
     * @see #regexpLike(Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-OTHER">regexp_like ( string text, pattern text [, flags text ] ) → boolean</a>
     */
    public static IPredicate regexpLike(Expression string, BiFunction<MappingType, String, Expression> valueFunc,
                                        String pattern, String flags) {
        return regexpLike(
                string, valueFunc.apply(StringType.INSTANCE, pattern), SQLs.literal(StringType.INSTANCE, flags)
        );
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link io.army.mapping.BooleanType}
     *
     *
     * @param valueFunc valid function are :
     *                  <ul>
     *                      <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                      <li>{@link SQLs#param(TypeInfer, Object)} </li>
     *                      <li>{@link SQLs#namedLiteral(TypeInfer, String)}</li>
     *                      <li>{@link SQLs#namedParam(TypeInfer, String)}</li>
     *                  </ul>
     * @see #regexpLike(Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-OTHER">regexp_like ( string text, pattern text [, flags text ] ) → boolean</a>
     */
    public static IPredicate regexpLike(Expression string, BiFunction<MappingType, String, Expression> valueFunc,
                                        String pattern, Expression flags) {
        return regexpLike(string, valueFunc.apply(StringType.INSTANCE, pattern), flags);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link io.army.mapping.BooleanType}
     *
     *
     * @see #regexpLike(Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-OTHER">regexp_like ( string text, pattern text [, flags text ] ) → boolean</a>
     */
    public static IPredicate regexpLike(Expression string, Expression pattern, Expression flags) {
        return FunctionUtils.threeArgPredicateFunc("REGEXP_LIKE", string, pattern, flags);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link TextArrayType}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-OTHER">regexp_match ( string text, pattern text [, flags text ] ) → text[] </a>
     */
    public static SimpleExpression regexpMatch(Expression string, Expression pattern) {
        return FunctionUtils.twoArgFunc("REGEXP_MATCH", string, pattern, TextArrayType.from(String[].class));
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link TextArrayType}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-OTHER">regexp_match ( string text, pattern text [, flags text ] ) → text[]</a>
     */
    public static SimpleExpression regexpMatch(Expression string, Expression pattern, Expression flags) {
        return FunctionUtils.threeArgFunc("REGEXP_MATCH", string, pattern, flags, TextArrayType.from(String[].class));
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link TextArrayType}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-OTHER">regexp_like ( string text, pattern text [, flags text ] ) → boolean</a>
     */
    public static _ColumnWithOrdinalityFunction regexpMatches(Expression string, Expression pattern) {
        return DialectFunctionUtils.twoArgColumnFunction("REGEXP_MATCHES", string, pattern,
                null, TextArrayType.from(String[].class));
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link TextArrayType}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-OTHER">regexp_like ( string text, pattern text [, flags text ] ) → boolean</a>
     */
    public static _ColumnWithOrdinalityFunction regexpMatches(Expression string, Expression pattern, Expression flags) {
        final String name = "REGEXP_MATCHES";
        return DialectFunctionUtils.threeArgColumnFunction(name, string, pattern, flags,
                name.toLowerCase(Locale.ROOT), TextArrayType.from(String[].class));
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link TextType}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-OTHER">regexp_replace ( string text, pattern text, replacement text [, start integer ] [, flags text ] ) → text</a>
     */
    public static SimpleExpression regexpReplace(Expression string, Expression pattern, Expression replacement) {
        return FunctionUtils.threeArgFunc("REGEXP_REPLACE", string, pattern, replacement, TextType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link TextType}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-OTHER">regexp_replace ( string text, pattern text, replacement text [, start integer ] [, flags text ] ) → text</a>
     */
    public static SimpleExpression regexpReplace(Expression string, Expression pattern, Expression replacement, Expression startOrFlag) {
        return FunctionUtils.fourArgFunc("REGEXP_REPLACE", string, pattern, replacement, startOrFlag, TextType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link TextType}
     *
     *
     * @see #regexpLike(Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-OTHER">regexp_replace ( string text, pattern text, replacement text [, start integer ] [, flags text ] ) → text ;
     * regexp_replace ( string text, pattern text, replacement text, start integer, N integer [, flags text ] ) → text</a>
     */
    public static SimpleExpression regexpReplace(Expression string, Expression pattern, Expression replacement, Expression start, Expression nOrFlat) {
        return FunctionUtils.fiveArgFunc("REGEXP_REPLACE", string, pattern, replacement, start, nOrFlat, TextType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link TextType}
     *
     *
     * @see #regexpReplace(Expression, Expression, Expression, Expression, Expression)
     * @see #regexpReplace(Expression, Expression, Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-OTHER">regexp_replace ( string text, pattern text, replacement text [, start integer ] [, flags text ] ) → text</a>
     */
    public static SimpleExpression regexpReplace(Expression string, Expression pattern, Expression replacement, Expression start, Expression n, Expression flags) {
        return FunctionUtils.sixArgFunc("REGEXP_REPLACE", string, pattern, replacement, start, n, flags, TextType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link TextArrayType}
     *
     *
     * @see #regexpSplitToArray(Expression, Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-OTHER">regexp_split_to_array ( string text, pattern text [, flags text ] ) → text[]</a>
     */
    public static SimpleExpression regexpSplitToArray(Expression string, Expression pattern) {
        return FunctionUtils.twoArgFunc("REGEXP_SPLIT_TO_ARRAY", string, pattern, TextArrayType.from(String[].class));
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link TextArrayType}
     *
     *
     * @see #regexpSplitToArray(Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-OTHER">regexp_split_to_array ( string text, pattern text [, flags text ] ) → text[]</a>
     */
    public static SimpleExpression regexpSplitToArray(Expression string, Expression pattern, Expression flags) {
        return FunctionUtils.threeArgFunc("REGEXP_SPLIT_TO_ARRAY", string, pattern, flags, TextArrayType.from(String[].class));
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link TextType}
     *
     *
     * @see #regexpSplitToTable(Expression, Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-OTHER">regexp_split_to_table ( string text, pattern text [, flags text ] ) → setof text</a>
     */
    public static _ColumnWithOrdinalityFunction regexpSplitToTable(Expression string, Expression pattern) {
        return DialectFunctionUtils.twoArgColumnFunction("REGEXP_SPLIT_TO_TABLE", string, pattern,
                null, TextType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link TextType}
     *
     *
     * @see #regexpSplitToTable(Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-OTHER">regexp_split_to_table ( string text, pattern text [, flags text ] ) → setof text</a>
     */
    public static _ColumnWithOrdinalityFunction regexpSplitToTable(Expression string, Expression pattern, Expression flags) {
        final String name = "REGEXP_SPLIT_TO_TABLE";
        return DialectFunctionUtils.threeArgColumnFunction(name, string, pattern, flags,
                name.toLowerCase(Locale.ROOT), TextType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link TextType}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-OTHER">regexp_substr ( string text, pattern text [, start integer [, N integer [, flags text [, subexpr integer ] ] ] ] ) → text</a>
     */
    public static SimpleExpression regexpSubstr(Expression string, Expression pattern, Expression start) {
        return FunctionUtils.threeArgFunc("REGEXP_SUBSTR", string, pattern, start, TextType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link TextType}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-OTHER">regexp_substr ( string text, pattern text [, start integer [, N integer [, flags text [, subexpr integer ] ] ] ] ) → text</a>
     */
    public static SimpleExpression regexpSubstr(Expression string, Expression pattern, Expression start, Expression n) {
        return FunctionUtils.fourArgFunc("REGEXP_SUBSTR", string, pattern, start, n, TextType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link TextType}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-OTHER">regexp_substr ( string text, pattern text [, start integer [, N integer [, flags text [, subexpr integer ] ] ] ] ) → text</a>
     */
    public static SimpleExpression regexpSubstr(Expression string, Expression pattern, Expression start, Expression n, Expression flags) {
        return FunctionUtils.fiveArgFunc("REGEXP_SUBSTR", string, pattern, start, n, flags, TextType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link TextType}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-OTHER">regexp_substr ( string text, pattern text [, start integer [, N integer [, flags text [, subexpr integer ] ] ] ] ) → text</a>
     */
    public static SimpleExpression regexpSubstr(Expression string, Expression pattern, Expression start, Expression n, Expression flags, Expression subExpr) {
        return FunctionUtils.sixArgFunc("REGEXP_SUBSTR", string, pattern, start, n, flags, subExpr, TextType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link TextType}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-OTHER">repeat ( string text, number integer ) → text</a>
     */
    public static SimpleExpression repeat(Expression string, Expression number) {
        return FunctionUtils.twoArgFunc("REPEAT", string, number, TextType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link TextType}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-OTHER">replace ( string text, from text, to text ) → text</a>
     */
    public static SimpleExpression replace(Expression string, Expression from, Expression to) {
        return FunctionUtils.threeArgFunc("REPLACE", string, from, to, TextType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link TextType}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-OTHER">reverse ( text ) → text</a>
     */
    public static SimpleExpression reverse(Expression string) {
        return FunctionUtils.oneArgFunc("REVERSE", string, TextType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link TextType}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-OTHER">right ( string text, n integer ) → text</a>
     */
    public static SimpleExpression right(Expression string, Expression n) {
        return FunctionUtils.twoArgFunc("RIGHT", string, n, TextType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link TextType}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-OTHER">rpad ( string text, length integer [, fill text ] ) → text</a>
     */
    public static SimpleExpression rpad(Expression string, Expression length) {
        return FunctionUtils.twoArgFunc("RPAD", string, length, TextType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link TextType}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-OTHER">rpad ( string text, length integer [, fill text ] ) → text</a>
     */
    public static SimpleExpression rpad(Expression string, Expression length, Expression fill) {
        return FunctionUtils.threeArgFunc("RPAD", string, length, fill, TextType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link TextType}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-OTHER">rtrim ( string text [, characters text ] ) → text</a>
     */
    public static SimpleExpression rtrim(Expression string) {
        return FunctionUtils.oneArgFunc("RTRIM", string, TextType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of string
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-OTHER">rtrim ( string text [, characters text ] ) → text</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-binarystring.html#FUNCTIONS-BINARYSTRING-SQL">rtrim ( bytes bytea, bytesremoved bytea ) → bytea</a>
     */
    public static SimpleExpression rtrim(Expression string, Expression characters) {
        return FunctionUtils.twoArgFunc("RTRIM", string, characters, string.typeMeta());
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link TextType}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-OTHER">split_part ( string text, delimiter text, n integer ) → text</a>
     */
    public static SimpleExpression splitPart(Expression string, Expression delimiter, Expression n) {
        return FunctionUtils.threeArgFunc("SPLIT_PART", string, delimiter, n, TextType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link BooleanType}
     *
     *
     * @see Postgres#caretAt(Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-OTHER">starts_with ( string text, prefix text ) → boolean</a>
     */
    public static IPredicate startsWith(Expression string, Expression prefix) {
        return FunctionUtils.twoArgPredicateFunc("STARTS_WITH", string, prefix);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link TextArrayType}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-OTHER">string_to_array ( string text, delimiter text [, null_string text ] ) → text[]</a>
     */
    public static SimpleExpression stringToArray(Expression string, Expression delimiter) {
        return FunctionUtils.twoArgFunc("STRING_TO_ARRAY", string, delimiter, TextArrayType.from(String[].class));
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link TextArrayType}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-OTHER">string_to_array ( string text, delimiter text [, null_string text ] ) → text[]</a>
     */
    public static SimpleExpression stringToArray(Expression string, Expression delimiter, Expression nullString) {
        return FunctionUtils.threeArgFunc("STRING_TO_ARRAY", string, delimiter, nullString, TextArrayType.from(String[].class));
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link TextType}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-OTHER">string_to_table ( string text, delimiter text [, null_string text ] ) → setof text</a>
     */
    public static _ColumnWithOrdinalityFunction stringToTable(Expression string, Expression delimiter) {
        return DialectFunctionUtils.twoArgColumnFunction("STRING_TO_TABLE", string, delimiter,
                null, TextType.INSTANCE
        );
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link TextType}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-OTHER">string_to_table ( string text, delimiter text [, null_string text ] ) → setof text</a>
     */
    public static _ColumnWithOrdinalityFunction stringToTable(Expression string, Expression delimiter, Expression nullString) {
        return DialectFunctionUtils.threeArgColumnFunction("STRING_TO_TABLE", string, delimiter, nullString,
                null, TextType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link IntegerType}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-OTHER">strpos ( string text, substring text ) → integer</a>
     */
    public static SimpleExpression strPos(Expression string, Expression substring) {
        return FunctionUtils.twoArgFunc("STRPOS", string, substring, IntegerType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of string
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-OTHER">substr ( string text, start integer [, count integer ] ) → text</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-binarystring.html#FUNCTIONS-BINARYSTRING-SQL">substr ( bytes bytea, start integer [, count integer ] ) → bytea</a>
     */
    public static SimpleExpression substr(Expression string, Expression start) {
        return FunctionUtils.twoArgFunc("SUBSTR", string, start, string.typeMeta());
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of string
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-OTHER">substr ( string text, start integer [, count integer ] ) → text</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-binarystring.html#FUNCTIONS-BINARYSTRING-SQL">substr ( bytes bytea, start integer [, count integer ] ) → bytea</a>
     */
    public static SimpleExpression substr(Expression string, Expression start, Expression count) {
        return FunctionUtils.threeArgFunc("SUBSTR", string, start, count, string.typeMeta());
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link TextType}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-OTHER">to_ascii ( string text ) → text <br/>
     * to_ascii ( string text, encoding name ) → text <br/>
     * to_ascii ( string text, encoding integer ) → text
     * </a>
     */
    public static SimpleExpression toAscii(Expression string) {
        return FunctionUtils.oneArgFunc("TO_ASCII", string, TextType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link TextType}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-OTHER">to_ascii ( string text ) → text <br/>
     * to_ascii ( string text, encoding name ) → text <br/>
     * to_ascii ( string text, encoding integer ) → text
     * </a>
     */
    public static SimpleExpression toAscii(Expression string, Expression encoding) {
        return FunctionUtils.twoArgFunc("TO_ASCII", string, encoding, TextType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link TextType}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-OTHER">to_hex ( integer ) → text <br/>
     * to_hex ( bigint ) → text
     * </a>
     */
    public static SimpleExpression toHex(Expression integer) {
        return FunctionUtils.oneArgFunc("TO_HEX", integer, TextType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link TextType}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-OTHER">translate ( string text, from text, to text ) → text</a>
     */
    public static SimpleExpression translate(Expression string, Expression from, Expression to) {
        return FunctionUtils.threeArgFunc("TRANSLATE", string, from, to, TextType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link TextType}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-OTHER">unistr ( text ) → text<br/>
     * to_hex ( bigint ) → text
     * </a>
     */
    public static SimpleExpression uniStr(Expression string) {
        return FunctionUtils.oneArgFunc("UNISTR", string, TextType.INSTANCE);
    }


    /*-------------------below Bit String Functions and Operators -------------------*/

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link LongType}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-bitstring.html#FUNCTIONS-BIT-STRING-TABLE">bit_count ( bit ) → bigint</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-binarystring.html#FUNCTIONS-BINARYSTRING-OTHER">bit_count ( bytes bytea ) → bigint</a>
     */
    public static SimpleExpression bitCount(Expression bit) {
        return FunctionUtils.oneArgFunc("BIT_COUNT", bit, LongType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link IntegerType}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-bitstring.html#FUNCTIONS-BIT-STRING-TABLE">get_bit ( bits bit, n integer ) → integer</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-binarystring.html#FUNCTIONS-BINARYSTRING-OTHER">get_bit ( bytes bytea, n bigint ) → integer</a>
     */
    public static SimpleExpression getBit(Expression bits, Expression n) {
        return FunctionUtils.twoArgFunc("GET_BIT", bits, n, IntegerType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of bits
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-bitstring.html#FUNCTIONS-BIT-STRING-TABLE">set_bit ( bits bit, n integer, newvalue integer ) → bit</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-binarystring.html#FUNCTIONS-BINARYSTRING-OTHER">set_bit ( bytes bytea, n bigint, newvalue integer ) → bytea</a>
     */
    public static SimpleExpression setBit(Expression bits, Expression n) {
        return FunctionUtils.twoArgFunc("SET_BIT", bits, n, bits.typeMeta());
    }

    /*-------------------below Binary String Functions and Operators -------------------*/

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link IntegerType}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-binarystring.html#FUNCTIONS-BINARYSTRING-OTHER">get_bit ( bytes bytea, n bigint ) → integer</a>
     */
    public static SimpleExpression getByte(Expression bits, Expression n) {
        return FunctionUtils.twoArgFunc("GET_BIT", bits, n, IntegerType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link IntegerType}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-binarystring.html#FUNCTIONS-BINARYSTRING-OTHER">length ( bytes bytea, encoding name ) → integer</a>
     */
    public static SimpleExpression length(Expression bytes, Expression encoding) {
        return FunctionUtils.twoArgFunc("LENGTH", bytes, encoding, IntegerType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of bits
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-binarystring.html#FUNCTIONS-BINARYSTRING-OTHER">set_bit ( bytes bytea, n bigint, newvalue integer ) → bytea</a>
     */
    public static SimpleExpression setBit(Expression bits, Expression n, Expression newValue) {
        return FunctionUtils.threeArgFunc("SET_BIT", bits, n, newValue, bits.typeMeta());
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of bits
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-binarystring.html#FUNCTIONS-BINARYSTRING-OTHER">set_byte ( bytes bytea, n integer, newvalue integer ) → bytea</a>
     */
    public static SimpleExpression setByte(Expression bits, Expression n, Expression newValue) {
        return FunctionUtils.threeArgFunc("SET_BYTE", bits, n, newValue, bits.typeMeta());
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of bytea
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-binarystring.html#FUNCTIONS-BINARYSTRING-OTHER">sha224 ( bytea ) → bytea</a>
     */
    public static SimpleExpression sha224(Expression bytea) {
        return FunctionUtils.oneArgFunc("SHA224", bytea, bytea.typeMeta());
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of bytea
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-binarystring.html#FUNCTIONS-BINARYSTRING-OTHER">sha256 ( bytea ) → bytea</a>
     */
    public static SimpleExpression sha256(Expression bytea) {
        return FunctionUtils.oneArgFunc("SHA256", bytea, bytea.typeMeta());
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of bytea
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-binarystring.html#FUNCTIONS-BINARYSTRING-OTHER">sha384 ( bytea ) → bytea</a>
     */
    public static SimpleExpression sha384(Expression bytea) {
        return FunctionUtils.oneArgFunc("SHA384", bytea, bytea.typeMeta());
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of bytea
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-binarystring.html#FUNCTIONS-BINARYSTRING-OTHER">sha512 ( bytea ) → bytea</a>
     */
    public static SimpleExpression sha512(Expression bytea) {
        return FunctionUtils.oneArgFunc("SHA512", bytea, bytea.typeMeta());
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of bytea
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-binarystring.html#FUNCTIONS-BINARYSTRING-CONVERSIONS">convert ( bytes bytea, src_encoding name, dest_encoding name ) → bytea</a>
     */
    public static SimpleExpression convert(Expression bytea, Expression srcEncoding, Expression destEncoding) {
        return FunctionUtils.threeArgFunc("CONVERT", bytea, srcEncoding, destEncoding, bytea.typeMeta());
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link TextType} .
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-binarystring.html#FUNCTIONS-BINARYSTRING-CONVERSIONS">convert_from ( bytes bytea, src_encoding name ) → text</a>
     */
    public static SimpleExpression convertFrom(Expression bytea, Expression srcEncoding) {
        return FunctionUtils.twoArgFunc("CONVERT_FROM", bytea, srcEncoding, TextType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link VarBinaryType} .
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-binarystring.html#FUNCTIONS-BINARYSTRING-CONVERSIONS">convert_to ( string text, dest_encoding name ) → bytea</a>
     */
    public static SimpleExpression convertTo(Expression bytea, Expression destEncoding) {
        return FunctionUtils.twoArgFunc("CONVERT_TO", bytea, destEncoding, VarBinaryType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link TextType}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-binarystring.html#FUNCTIONS-BINARYSTRING-CONVERSIONS">encode ( bytes bytea, format text ) → text</a>
     */
    public static SimpleExpression encode(Expression bytea, Expression format) {
        return FunctionUtils.twoArgFunc("ENCODE", bytea, format, TextType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link VarBinaryType}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-binarystring.html#FUNCTIONS-BINARYSTRING-CONVERSIONS">decode ( string text, format text ) → bytea</a>
     */
    public static SimpleExpression decode(Expression text, Expression format) {
        return FunctionUtils.twoArgFunc("DECODE", text, format, VarBinaryType.INSTANCE);
    }




    /*-------------------below private method -------------------*/


    /**
     * @see #overlay(Expression, WordPlacing, Expression, SQLs.WordFrom, Expression)
     * @see #overlay(Expression, WordPlacing, Expression, SQLs.WordFrom, Expression, SQLs.WordFor, Expression)
     */
    private static SimpleExpression _overlay(Expression string, WordPlacing placing, Expression newSubstring,
                                             SQLs.WordFrom from, Expression start, @Nullable SQLs.WordFor wordFor,
                                             @Nullable Expression count) {
        final String name = "OVERLAY";
        final SimpleExpression func;
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
        } else if (from != SQLs.FROM) {
            throw CriteriaUtils.funcArgError(name, from);
        } else if (wordFor != SQLs.FOR) {
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
     * @see #substring(Expression, SQLs.WordFrom, Expression)
     * @see #substring(Expression, SQLs.WordFor, Expression)
     * @see #substring(Expression, SQLs.WordFrom, Expression, SQLs.WordFor, Expression)
     */
    private static SimpleExpression _substring(Expression string, SQLs.WordFrom from, @Nullable Expression start,
                                               SQLs.WordFor wordFor, @Nullable Expression count) {
        final String name = "SUBSTRING";
        final SimpleExpression func;
        if (string instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, string);
        } else if (start instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, start);
        } else if (count instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, count);
        } else if (from != SQLs.FROM) {
            throw CriteriaUtils.funcArgError(name, from);
        } else if (wordFor != SQLs.FOR) {
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

    /**
     * @see #lower(Expression)
     * @see #upper(Expression)
     */
    private static MappingType lowerOrUpperType(final MappingType type) {
        final MappingType returnType;
        if (type instanceof PostgreRangeType.RangeType) {
            returnType = ((PostgreRangeType.RangeType) type).subtype();
        } else {
            returnType = TextType.INSTANCE;
        }
        return returnType;
    }


}
