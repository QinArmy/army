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

import io.army.criteria.SQLWords;
import io.army.criteria.impl.FunctionArg;
import io.army.criteria.impl.NonOperationExpression;
import io.army.criteria.impl.SqlWords;
import io.army.criteria.standard.SQLs;
import io.army.dialect.impl._Constant;
import io.army.dialect.impl._SqlContext;
import io.army.mapping.StringType;
import io.army.meta.TypeMeta;

abstract class PostgreWords {

    private PostgreWords() {
        throw new UnsupportedOperationException();
    }


    enum WordExtractTimeField implements Postgres.ExtractTimeField, SqlWords.ArmyKeyWord {

        CENTURY(" CENTURY"),
        DAY(" DAY"),
        DECADE(" DECADE"),
        DOW(" DOW"),

        DOY(" DOY"),
        EPOCH(" EPOCH"),
        HOUR(" HOUR"),
        ISODOW(" ISODOW"),

        ISOYEAR(" ISOYEAR"),
        JULIAN(" JULIAN"),
        MICROSECONDS(" MICROSECONDS"),
        MILLENNIUM(" MILLENNIUM"),

        MILLISECONDS(" MILLISECONDS"),
        MINUTE(" MINUTE"),
        MONTH(" MONTH"),
        QUARTER(" QUARTER"),

        SECOND(" SECOND"),
        TIMEZONE(" TIMEZONE"),
        TIMEZONE_HOUR(" TIMEZONE_HOUR"),
        TIMEZONE_MINUTE(" TIMEZONE_MINUTE"),

        WEEK(" WEEK"),
        YEAR(" YEAR");

        private final String spaceWord;

        WordExtractTimeField(String spaceWord) {
            this.spaceWord = spaceWord;
        }

        @Override
        public final String spaceRender() {
            return this.spaceWord;
        }

        @Override
        public final String toString() {
            return Postgres.keyWordToString(this);
        }


    }//WordTimeField


    enum KeyWordName implements Postgres.WordName, SqlWords.ArmyKeyWord {

        NAME(" NAME");

        private final String spaceWord;

        KeyWordName(String spaceWord) {
            this.spaceWord = spaceWord;
        }

        @Override
        public final String spaceRender() {
            return this.spaceWord;
        }

        @Override
        public final String toString() {
            return Postgres.keyWordToString(this);
        }

    }

    enum KeyWordVersion implements Postgres.WordVersion, SqlWords.ArmyKeyWord {

        VERSION(" VERSION");

        private final String spaceWord;

        KeyWordVersion(String spaceWord) {
            this.spaceWord = spaceWord;
        }

        @Override
        public final String spaceRender() {
            return this.spaceWord;
        }

        @Override
        public final String toString() {
            return Postgres.keyWordToString(this);
        }

    }//KeyWordVersion

    enum KeyWordStandalone implements Postgres.WordStandalone, SqlWords.ArmyKeyWord {

        STANDALONE(" STANDALONE");

        private final String spaceWord;

        KeyWordStandalone(String spaceWord) {
            this.spaceWord = spaceWord;
        }

        @Override
        public final String spaceRender() {
            return this.spaceWord;
        }

        @Override
        public final String toString() {
            return Postgres.keyWordToString(this);
        }

    }//KeyWordStandalone

    enum KeyWordStandaloneOption implements Postgres.StandaloneOption, SqlWords.ArmyKeyWord {

        YES(" YES"),
        NO(" NO");

        private final String spaceWord;

        KeyWordStandaloneOption(String spaceWord) {
            this.spaceWord = spaceWord;
        }

        @Override
        public final String spaceRender() {
            return this.spaceWord;
        }

        @Override
        public final String toString() {
            return Postgres.keyWordToString(this);
        }

    }//KeyWordStandaloneOption

    enum KeyWordsNoValue implements Postgres.WordsNoValue, SqlWords.ArmyKeyWord {

        NO_VALUE(" NO VALUE");

        private final String spaceWord;

        KeyWordsNoValue(String spaceWord) {
            this.spaceWord = spaceWord;
        }

        @Override
        public final String spaceRender() {
            return this.spaceWord;
        }

        @Override
        public final String toString() {
            return Postgres.keyWordToString(this);
        }

    }// KeyWordsNoValue

    enum KeyWordPassing implements Postgres.WordPassing, SqlWords.ArmyKeyWord {

        PASSING(" PASSING");

        private final String spaceWord;

        KeyWordPassing(String spaceWord) {
            this.spaceWord = spaceWord;
        }

        @Override
        public final String spaceRender() {
            return this.spaceWord;
        }

        @Override
        public final String toString() {
            return Postgres.keyWordToString(this);
        }

    }// KeyWordPassing

    enum WordPassingOption implements Postgres.PassingOption, SqlWords.ArmyKeyWord {
        BY_REF(" BY REF"),
        BY_VALUE(" BY VALUE");

        private final String spaceWord;

        WordPassingOption(String spaceWord) {
            this.spaceWord = spaceWord;
        }

        @Override
        public final String spaceRender() {
            return this.spaceWord;
        }

        @Override
        public final String toString() {
            return Postgres.keyWordToString(this);
        }

    }//WordPassingOption


    enum SelectModifier implements Postgres.Modifier {

        ALL(" ALL");

        private final String spaceWord;

        SelectModifier(String spaceWord) {
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


    }//SelectModifier

    enum KeyWordDistinct implements Postgres.WordDistinct {

        DISTINCT(" DISTINCT");

        private final String spaceWord;

        KeyWordDistinct(String spaceWord) {
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
    }//KeyWordDistinct

    enum FromNormalizedWord implements SQLs.BooleanTestWord, SqlWords.ArmyKeyWord {
        FROM_NORMALIZED(" FROM NORMALIZED"),
        NORMALIZED(" NORMALIZED");

        private final String spaceWords;

        FromNormalizedWord(String spaceWords) {
            this.spaceWords = spaceWords;
        }

        @Override
        public final String spaceRender() {
            return this.spaceWords;
        }


        @Override
        public String toString() {
            return PostgreSyntax.keyWordToString(this);
        }


    }//FromNormalizedWord

    enum KeyWordNormalizeForm implements Postgres.WordNormalizeForm, SqlWords.ArmyKeyWord, SQLWords {

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

    enum KeyWordPlacing implements Postgres.WordPlacing, SqlWords.ArmyKeyWord, SQLWords {

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

    static final class NullTreatModeExpression extends NonOperationExpression
            implements Postgres.NullTreatMode,
            FunctionArg.SingleFunctionArg {

        static final NullTreatModeExpression RAISE_EXCEPTION = new NullTreatModeExpression(" 'raise_exception'");

        static final NullTreatModeExpression USE_JSON_NULL = new NullTreatModeExpression(" 'use_json_null'");

        static final NullTreatModeExpression DELETE_KEY = new NullTreatModeExpression(" 'delete_key'");

        static final NullTreatModeExpression RETURN_TARGET = new NullTreatModeExpression(" 'return_target'");

        private final String spaceLiteral;

        /**
         * private constructor
         */
        private NullTreatModeExpression(final String spaceLiteral) {
            assert spaceLiteral.charAt(0) == _Constant.SPACE;
            assert spaceLiteral.charAt(1) == _Constant.QUOTE;
            assert spaceLiteral.charAt(spaceLiteral.length() - 1) == _Constant.QUOTE;
            this.spaceLiteral = spaceLiteral;
        }

        @Override
        public TypeMeta typeMeta() {
            return StringType.INSTANCE;
        }

        @Override
        public void appendSql(final StringBuilder sqlBuilder, final _SqlContext context) {
            sqlBuilder.append(this.spaceLiteral);
        }


    }//NullTreatModeLiteral

    enum SymbolDoubleColon implements Postgres.DoubleColon {

        DOUBLE_COLON

    }//SymbolDoubleColon


}
