package io.army.criteria.impl;

abstract class PostgreWords {

    private PostgreWords() {
        throw new UnsupportedOperationException();
    }


    enum WordExtractTimeField implements PostgreDateTimeFunctions.ExtractTimeField, SqlSyntax.ArmyKeyWord {

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


    enum KeyWordName implements PostgreSyntax.WordName, SqlSyntax.ArmyKeyWord {

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

    enum KeyWordVersion implements PostgreStringFunctions.WordVersion, SqlSyntax.ArmyKeyWord {

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

    enum KeyWordStandalone implements PostgreStringFunctions.WordStandalone, SqlSyntax.ArmyKeyWord {

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

    enum KeyWordStandaloneOption implements PostgreStringFunctions.StandaloneOption, SqlSyntax.ArmyKeyWord {

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

    enum KeyWordsNoValue implements PostgreStringFunctions.WordsNoValue, SqlSyntax.ArmyKeyWord {

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

    enum KeyWordPassing implements PostgreStringFunctions.WordPassing, SqlSyntax.ArmyKeyWord {

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

    enum WordPassingOption implements PostgreStringFunctions.PassingOption, SqlSyntax.ArmyKeyWord {
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


}
