package io.army.criteria.impl;

import io.army.dialect._Constant;
import io.army.dialect._SqlContext;
import io.army.mapping.StringType;
import io.army.meta.TypeMeta;

abstract class PostgreWords {

    private PostgreWords() {
        throw new UnsupportedOperationException();
    }


    enum WordExtractTimeField implements PostgreDateTimeFunctions.ExtractTimeField, SQLs.ArmyKeyWord {

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


    enum KeyWordName implements PostgreSyntax.WordName, SQLs.ArmyKeyWord {

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

    enum KeyWordVersion implements PostgreStringFunctions.WordVersion, SQLs.ArmyKeyWord {

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

    enum KeyWordStandalone implements PostgreStringFunctions.WordStandalone, SQLs.ArmyKeyWord {

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

    enum KeyWordStandaloneOption implements PostgreStringFunctions.StandaloneOption, SQLs.ArmyKeyWord {

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

    enum KeyWordsNoValue implements PostgreStringFunctions.WordsNoValue, SQLs.ArmyKeyWord {

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

    enum KeyWordPassing implements PostgreStringFunctions.WordPassing, SQLs.ArmyKeyWord {

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

    enum WordPassingOption implements PostgreStringFunctions.PassingOption, SQLs.ArmyKeyWord {
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


    enum SelectModifier implements PostgreSyntax.Modifier {

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

    enum KeyWordDistinct implements PostgreSyntax.WordDistinct {

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

    enum KeyWordMaterialized implements PostgreSyntax.WordMaterialized {

        MATERIALIZED(" MATERIALIZED"),
        NOT_MATERIALIZED(" NOT MATERIALIZED");

        private final String spaceWord;

        KeyWordMaterialized(String spaceWord) {
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


    }//KeyWordMaterialized

    enum FromNormalizedWord implements SQLs.BooleanTestWord, SQLs.ArmyKeyWord {
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

    static final class NullTreatModeExpression extends NonOperationExpression
            implements PostgreDocumentFunctions.NullTreatMode,
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
        public void appendSql(final _SqlContext context) {
            context.sqlBuilder().append(this.spaceLiteral);
        }


    }//NullTreatModeLiteral

}
