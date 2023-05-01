package io.army.criteria.impl;

import io.army.criteria.SQLWords;

abstract class SqlWords {

    private SqlWords() {
        throw new UnsupportedOperationException();
    }


    enum SymbolSpaceEnum implements SqlSyntax.SymbolSpace {

        SPACE;


    }//SymbolSpaceEnum

    enum KeyWordNotNull implements SqlSyntax.NullOption, SqlSyntax.ArmyKeyWord {

        NOT_NULL(" NOT NULL");

        private final String spaceWord;

        KeyWordNotNull(String spaceWord) {
            this.spaceWord = spaceWord;
        }

        @Override
        public final String spaceRender() {
            return this.spaceWord;
        }

        @Override
        public final String toString() {
            return CriteriaUtils.enumToString(this);
        }

    }//KeyWordNotNull


    enum KeyWordIn implements Functions.WordIn, SqlSyntax.ArmyKeyWord {

        IN(" IN");

        private final String spaceWord;

        KeyWordIn(String spaceWord) {
            this.spaceWord = spaceWord;
        }

        @Override
        public final String spaceRender() {
            return this.spaceWord;
        }

        @Override
        public final String toString() {
            return CriteriaUtils.enumToString(this);
        }


    }//KeyWordIn

    enum KeyWordSimilar implements Functions.WordSimilar, SqlSyntax.ArmyKeyWord {

        SIMILAR(" SIMILAR");

        private final String spaceWord;

        KeyWordSimilar(String spaceWord) {
            this.spaceWord = spaceWord;
        }

        @Override
        public final String spaceRender() {
            return this.spaceWord;
        }


        @Override
        public final String toString() {
            return SQLs.sqlKeyWordsToString(this);
        }

    }//KeyWordSimilar

    enum KeyWordFrom implements Functions.WordFrom, SqlSyntax.ArmyKeyWord {

        FROM(" FROM");

        private final String spaceWords;

        KeyWordFrom(String spaceWords) {
            this.spaceWords = spaceWords;
        }

        @Override
        public final String spaceRender() {
            return this.spaceWords;
        }

        @Override
        public final String toString() {
            return SQLs.sqlKeyWordsToString(this);
        }


    }//KeyWordFrom

    enum KeyWordFor implements Functions.WordFor, SqlSyntax.ArmyKeyWord {

        FOR(" FOR");

        private final String spaceWords;

        KeyWordFor(String spaceWords) {
            this.spaceWords = spaceWords;
        }

        @Override
        public final String spaceRender() {
            return this.spaceWords;
        }

        @Override
        public final String toString() {
            return SQLs.sqlKeyWordsToString(this);
        }


    }//KeyWordFor

    enum WordTrimPosition implements Functions.TrimPosition, SqlSyntax.ArmyKeyWord, SQLWords {

        BOTH(" BOTH"),
        LEADING(" LEADING"),
        TRAILING(" TRAILING");

        private final String spaceWords;

        WordTrimPosition(String spaceWords) {
            this.spaceWords = spaceWords;
        }

        @Override
        public final String spaceRender() {
            return this.spaceWords;
        }

        @Override
        public final String toString() {
            return SQLs.sqlKeyWordsToString(this);
        }


    }//WordTrimPosition


    enum KeyWordPath implements Functions.WordPath, SqlSyntax.ArmyKeyWord {

        PATH(" PATH");

        private final String spaceWord;

        KeyWordPath(String spaceWord) {
            this.spaceWord = spaceWord;
        }

        @Override
        public final String spaceRender() {
            return this.spaceWord;
        }


        @Override
        public final String toString() {
            return CriteriaUtils.enumToString(this);
        }

    }//KeyWordPath

    enum KeyWordsForOrdinality implements Functions.WordsForOrdinality, SqlSyntax.ArmyKeyWord {

        FOR_ORDINALITY(" FOR ORDINALITY");

        private final String spaceWord;

        KeyWordsForOrdinality(String spaceWord) {
            this.spaceWord = spaceWord;
        }

        @Override
        public final String spaceRender() {
            return this.spaceWord;
        }


        @Override
        public final String toString() {
            return CriteriaUtils.enumToString(this);
        }

    }//KeyWordsForOrdinality

    enum KeyWordDocument implements SqlSyntax.WordDocument, SqlSyntax.ArmyKeyWord {

        /**
         * @see <a href="https://www.postgresql.org/docs/current/functions-xml.html#FUNCTIONS-XML-PREDICATES">xml IS DOCUMENT → boolean<br/>
         * </a>
         */
        DOCUMENT(" DOCUMENT");

        private final String spaceWord;

        KeyWordDocument(String spaceWord) {
            this.spaceWord = spaceWord;
        }

        @Override
        public final String spaceRender() {
            return this.spaceWord;
        }


        @Override
        public final String toString() {
            return CriteriaUtils.enumToString(this);
        }

    }//KeyWordDocument

    enum KeyWordContent implements SqlSyntax.WordContent, SqlSyntax.ArmyKeyWord {

        CONTENT(" CONTENT");

        private final String spaceWord;

        KeyWordContent(String spaceWord) {
            this.spaceWord = spaceWord;
        }

        @Override
        public final String spaceRender() {
            return this.spaceWord;
        }


        @Override
        public final String toString() {
            return CriteriaUtils.enumToString(this);
        }

    }//KeyWordContent


    /**
     * <p>
     * package enum
     * </p>
     *
     * @since 1.0
     */
    enum BooleanTestKeyWord implements SQLsSyntax.BooleanTestWord, Functions.ArmyKeyWord {

        JSON(" JSON");

        final String spaceOperator;

        BooleanTestKeyWord(String spaceOperator) {
            this.spaceOperator = spaceOperator;
        }


        @Override
        public final String spaceRender() {
            return this.spaceOperator;
        }


        @Override
        public final String toString() {
            return CriteriaUtils.enumToString(this);
        }


    }
}
