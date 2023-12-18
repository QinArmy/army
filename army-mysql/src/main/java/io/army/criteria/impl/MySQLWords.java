package io.army.criteria.impl;

abstract class MySQLWords {

    private MySQLWords() {
        throw new UnsupportedOperationException();
    }


    enum MySQLModifier implements MySQLs.Modifier {


        ALL(" ALL"),
        DISTINCTROW(" DISTINCTROW"),

        HIGH_PRIORITY(" HIGH_PRIORITY"),

        STRAIGHT_JOIN(" STRAIGHT_JOIN"),

        SQL_SMALL_RESULT(" SQL_SMALL_RESULT"),
        SQL_BIG_RESULT(" SQL_BIG_RESULT"),
        SQL_BUFFER_RESULT(" SQL_BUFFER_RESULT"),

        SQL_NO_CACHE(" SQL_NO_CACHE"),
        SQL_CALC_FOUND_ROWS(" SQL_CALC_FOUND_ROWS"),

        LOW_PRIORITY(" LOW_PRIORITY"),
        DELAYED(" DELAYED"),

        QUICK(" QUICK"),
        IGNORE(" IGNORE"),

        CONCURRENT(" CONCURRENT"),
        LOCAL(" LOCAL");

        private final String spaceWords;

        MySQLModifier(String spaceWords) {
            this.spaceWords = spaceWords;
        }


        @Override
        public final String spaceRender() {
            return this.spaceWords;
        }

        @Override
        public final String toString() {
            return MySQLSyntax.keyWordsToString(this);
        }


    }//MySQLModifier

    enum KeyWordDistinct implements MySQLs.WordDistinct {

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
            return MySQLSyntax.keyWordsToString(this);
        }


    }//WordDistinct

    enum KeyWordUsing implements MySQLs.WordUsing {

        USING(" USING");

        private final String spaceWord;

        KeyWordUsing(String spaceWord) {
            this.spaceWord = spaceWord;
        }

        @Override
        public final String spaceRender() {
            return this.spaceWord;
        }


        @Override
        public final String toString() {
            return MySQLSyntax.keyWordsToString(this);
        }

    }//KeyWordUsing

    enum KeyWordExistsPath implements MySQLs.WordExistsPath {

        EXISTS_PATH(" EXISTS PATH");

        private final String spaceWord;

        KeyWordExistsPath(String spaceWord) {
            this.spaceWord = spaceWord;
        }

        @Override
        public final String spaceRender() {
            return this.spaceWord;
        }


        @Override
        public final String toString() {
            return MySQLSyntax.keyWordsToString(this);
        }


    }//KeyWordExistsPath

    enum KeyWordsAtTimeZone implements MySQLs.WordsAtTimeZone {

        AT_TIME_ZONE(" AT TIME ZONE");


        private final String spaceWord;

        KeyWordsAtTimeZone(String spaceWord) {
            this.spaceWord = spaceWord;
        }

        @Override
        public final String spaceRender() {
            return this.spaceWord;
        }


        @Override
        public final String toString() {
            return MySQLSyntax.keyWordsToString(this);
        }


    }//KeyWordsAtTimeZone


}
