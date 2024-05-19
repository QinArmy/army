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

package io.army.criteria.impl;

import io.army.criteria.SQLWords;
import io.army.dialect._Constant;

abstract class SqlWords {

    private SqlWords() {
        throw new UnsupportedOperationException();
    }


    enum SymbolSpaceEnum implements SQLs.SymbolSpace {

        SPACE;

        @Override
        public final String toString() {
            return SQLs.keyWordsToString(this);
        }

    }//SymbolSpaceEnum


    enum SymbolEqualEnum implements SQLs.SymbolEqual {

        EQUAL;

        @Override
        public final String toString() {
            return SQLs.keyWordsToString(this);
        }
    }

    enum SymbolColonEqualEnum implements SQLs.SymbolColonEqual {

        COLON_EQUAL;

        @Override
        public final String toString() {
            return SQLs.keyWordsToString(this);
        }

    }

    enum KeyWordNotNull implements SQLs.NullOption, ArmyKeyWord {

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


    enum KeyWordIn implements SQLs.WordIn, ArmyKeyWord {

        IN;

        @Override
        public final String spaceRender() {
            return " IN";
        }

        @Override
        public final String toString() {
            return CriteriaUtils.enumToString(this);
        }


    }//KeyWordIn

    enum KeyWordSimilar implements SQLs.WordSimilar, ArmyKeyWord {

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
            return SQLs.keyWordsToString(this);
        }

    }//KeyWordSimilar

    enum KeyWordFrom implements SQLs.WordFrom, ArmyKeyWord {

        FROM;

        @Override
        public final String spaceRender() {
            return " FROM";
        }

        @Override
        public final String toString() {
            return SQLs.keyWordsToString(this);
        }


    }//KeyWordFrom

    enum KeyWordFor implements SQLs.WordFor, ArmyKeyWord {

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
            return SQLs.keyWordsToString(this);
        }


    }//KeyWordFor

    enum WordTrimPosition implements SQLs.TrimSpec, ArmyKeyWord {

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
            return SQLs.keyWordsToString(this);
        }


    } // WordTrimPosition


    enum KeyWordPath implements SQLs.WordPath, ArmyKeyWord {

        PATH;

        @Override
        public final String spaceRender() {
            return " PATH";
        }

        @Override
        public final String toString() {
            return CriteriaUtils.enumToString(this);
        }

    }//KeyWordPath

    enum KeyWordColumns implements SQLs.WordColumns, ArmyKeyWord {

        COLUMNS;

        @Override
        public final String spaceRender() {
            return " COLUMNS";
        }

        @Override
        public final String toString() {
            return CriteriaUtils.enumToString(this);
        }

    }//KeyWordColumns


    enum KeyWordNested implements SQLs.WordNested, ArmyKeyWord {

        NESTED;

        @Override
        public final String spaceRender() {
            return " NESTED";
        }

        @Override
        public final String toString() {
            return CriteriaUtils.enumToString(this);
        }

    } // KeyWordNested

    enum KeyWordExists implements SQLs.WordExists, ArmyKeyWord {

        EXISTS;

        @Override
        public final String spaceRender() {
            return " EXISTS";
        }

        @Override
        public final String toString() {
            return CriteriaUtils.enumToString(this);
        }

    }//KeyWordExists

    enum KeyWordsForOrdinality implements SQLs.WordsForOrdinality, ArmyKeyWord {

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


    enum KeyWordError implements SQLs.WordError, ArmyKeyWord {

        ERROR;

        @Override
        public final String spaceRender() {
            return " ERROR";
        }


        @Override
        public String toString() {
            return CriteriaUtils.enumToString(this);
        }


    } // KeyWordError

    enum KeyWordDocument implements SQLs.WordDocument, ArmyKeyWord {

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

    enum KeyWordContent implements SQLs.WordContent, ArmyKeyWord {

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
     *
     * @since 0.6.0
     */
    enum BooleanTestKeyWord implements SQLs.BooleanTestWord, ArmyKeyWord {

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

    enum IsComparisonKeyWord implements SQLs.IsComparisonWord, ArmyKeyWord {

        DISTINCT_FROM(" DISTINCT FROM");

        final String spaceOperator;

        IsComparisonKeyWord(String spaceOperator) {
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

    }//IsComparisonKeyWord


    enum KeyWordAll implements SQLWords, SQLs.WordAll, ArmyKeyWord {

        ALL(" ALL");

        private final String spaceWord;

        KeyWordAll(String spaceWord) {
            this.spaceWord = spaceWord;
        }

        @Override
        public final String spaceRender() {
            return this.spaceWord;
        }

        @Override
        public final String toString() {
            return SQLs.keyWordsToString(this);
        }


    }//KeyWordAll

    enum KeyWordDistinct implements ArmyKeyWord, SQLs.WordDistinct {

        DISTINCT;

        @Override
        public final String spaceRender() {
            return " DISTINCT";
        }

        @Override
        public final String toString() {
            return SQLs.keyWordsToString(this);
        }


    }//KeyWordDistinct

    enum KeyWordInterval implements SQLs.WordInterval, ArmyKeyWord {

        INTERVAL;

        @Override
        public final String spaceRender() {
            return " INTERVAL";
        }

        @Override
        public final String toString() {
            return SQLs.keyWordsToString(this);
        }

    }//KeyWordInterval

    enum KeyWordPercent implements SQLs.WordPercent, SQLWords {

        PERCENT(" PERCENT");

        private final String spaceWord;

        KeyWordPercent(String spaceWord) {
            this.spaceWord = spaceWord;
        }

        @Override
        public final String spaceRender() {
            return this.spaceWord;
        }

        @Override
        public final String toString() {
            return SQLs.keyWordsToString(this);
        }

    }//KeyWordPercent

    enum KeyWordUnknown implements SQLs.BooleanTestWord, ArmyKeyWord {

        UNKNOWN(" UNKNOWN");

        private final String spaceWord;

        KeyWordUnknown(String spaceWord) {
            this.spaceWord = spaceWord;
        }

        @Override
        public final String spaceRender() {
            return this.spaceWord;
        }

        @Override
        public final String toString() {
            return SQLs.keyWordsToString(this);
        }

    } //KeyWordUnknown

    enum KeyWordAscDesc implements SQLs.AscDesc, SQLWords {

        ASC(" ASC"),
        DESC(" DESC");

        final String spaceWord;

        KeyWordAscDesc(String spaceWord) {
            this.spaceWord = spaceWord;
        }

        @Override
        public final String spaceRender() {
            return this.spaceWord;
        }

        @Override
        public final String toString() {
            return SQLs.keyWordsToString(this);
        }

    }//KeyWordAscDesc

    enum KeyWordLateral implements SQLs.WordLateral {

        LATERAL(" LATERAL");

        private final String spaceWord;

        KeyWordLateral(String spaceWord) {
            this.spaceWord = spaceWord;
        }

        @Override
        public final String spaceRender() {
            return this.spaceWord;
        }

        @Override
        public final String toString() {
            return SQLs.keyWordsToString(this);
        }

    }//KeyWordLateral

    enum KeyWordFirst implements SQLs.WordFirst, SQLWords {

        FIRST(" FIRST");

        private final String spaceWord;

        KeyWordFirst(String spaceWord) {
            this.spaceWord = spaceWord;
        }

        @Override
        public final String spaceRender() {
            return this.spaceWord;
        }

        @Override
        public final String toString() {
            return SQLs.keyWordsToString(this);
        }

    }//KeyWordFirst

    enum KeyWordNext implements SQLs.WordNext, SQLWords {

        NEXT(" NEXT");

        private final String spaceWord;

        KeyWordNext(String spaceWord) {
            this.spaceWord = spaceWord;
        }

        @Override
        public final String spaceRender() {
            return this.spaceWord;
        }

        @Override
        public final String toString() {
            return SQLs.keyWordsToString(this);
        }

    }//KeyWordNext

    enum KeyWordsNullsFirstLast implements SQLs.NullsFirstLast, SQLWords {

        NULLS_FIRST(" NULLS FIRST"),
        NULLS_LAST(" NULLS LAST");

        final String spaceWords;

        KeyWordsNullsFirstLast(String spaceWords) {
            this.spaceWords = spaceWords;
        }

        @Override
        public final String spaceRender() {
            return this.spaceWords;
        }

        @Override
        public final String toString() {
            return SQLs.keyWordsToString(this);
        }

    }//KeyWordsNullsFirstLast

    enum KeyWordRow implements SQLs.WordRow, SQLWords {

        ROW(" ROW");

        private final String spaceWord;

        KeyWordRow(String spaceWord) {
            this.spaceWord = spaceWord;
        }

        @Override
        public final String spaceRender() {
            return this.spaceWord;
        }

        @Override
        public final String toString() {
            return SQLs.keyWordsToString(this);
        }

    }//KeyWordRow

    enum KeyWordRows implements SQLs.WordRows, ArmyKeyWord {

        ROWS;

        @Override
        public final String spaceRender() {
            return " ROWS";
        }

        @Override
        public final String toString() {
            return SQLs.keyWordsToString(this);
        }

    } //KeyWordRows


    enum KeyWordLines implements SQLs.WordLines, ArmyKeyWord {

        LINES;

        @Override
        public final String spaceRender() {
            return " LINES";
        }

        @Override
        public final String toString() {
            return SQLs.keyWordsToString(this);
        }

    } //KeyWordLines

    enum KeyWordWithTies implements SQLs.WordsWithTies, SQLWords {

        WITH_TIES(" WITH TIES");

        private final String spaceWords;

        KeyWordWithTies(String spaceWords) {
            this.spaceWords = spaceWords;
        }

        @Override
        public final String spaceRender() {
            return this.spaceWords;
        }

        @Override
        public final String toString() {
            return SQLs.keyWordsToString(this);
        }

    }//KeyWordNext

    enum KeyWordOny implements SQLs.WordOnly, SQLWords {

        ONLY;

        @Override
        public final String spaceRender() {
            return _Constant.SPACE_ONLY;
        }

        @Override
        public final String toString() {
            return SQLs.keyWordsToString(this);
        }

    }//KeyWordOny

    enum KeyWordAs implements SQLs.WordAs, ArmyKeyWord {

        AS;

        @Override
        public final String spaceRender() {
            return " AS";
        }

        @Override
        public final String toString() {
            return SQLs.keyWordsToString(this);
        }

    } // KeyWordAs

    enum KeyWordTo implements SQLs.WordTo, ArmyKeyWord {

        TO;

        @Override
        public final String spaceRender() {
            return " TO";
        }

        @Override
        public final String toString() {
            return SQLs.keyWordsToString(this);
        }

    } // KeyWordTo

    enum KeyWordAnd implements SQLs.WordAnd, ArmyKeyWord {

        AND(" AND");

        private final String spaceWord;

        KeyWordAnd(String spaceWord) {
            this.spaceWord = spaceWord;
        }

        @Override
        public final String spaceRender() {
            return this.spaceWord;
        }

        @Override
        public final String toString() {
            return SQLs.keyWordsToString(this);
        }

    }//KeyWordAnd

    enum KeyWordSymmetric implements SQLs.BetweenModifier, ArmyKeyWord {

        SYMMETRIC(" SYMMETRIC"),
        ASYMMETRIC(" ASYMMETRIC");

        private final String spaceWord;

        KeyWordSymmetric(String spaceWord) {
            this.spaceWord = spaceWord;
        }

        @Override
        public final String spaceRender() {
            return this.spaceWord;
        }

        @Override
        public final String toString() {
            return SQLs.keyWordsToString(this);
        }

    }//KeyWordSymmetric

    enum SQLSymbolPeriod implements SQLs.SymbolPeriod {

        PERIOD;

        @Override
        public final String toString() {
            return SQLs.keyWordsToString(this);
        }

    }//SQLSymbolPoint

    enum SQLSymbolAsterisk implements SQLs.SymbolAsterisk, SQLWords {

        ASTERISK(" *");

        private final String spaceStar;

        SQLSymbolAsterisk(String spaceStar) {
            this.spaceStar = spaceStar;
        }

        @Override
        public final String spaceRender() {
            return this.spaceStar;
        }

        @Override
        public final String toString() {
            return SQLs.keyWordsToString(this);
        }

    }//SQLSymbolStar

    enum KeyWordEscape implements SQLs.WordEscape, ArmyKeyWord {

        ESCAPE(" ESCAPE");

        private final String spaceWord;

        KeyWordEscape(String spaceWord) {
            this.spaceWord = spaceWord;
        }

        @Override
        public final String spaceRender() {
            return this.spaceWord;
        }


        @Override
        public final String toString() {
            return SQLs.keyWordsToString(this);
        }

    }//KeyWordEscape

    /**
     * @see SQLs#ALL
     */
    enum QueryOperator implements SQLs.QuantifiedWord {

        ANY(" ANY"),
        SOME(" SOME");

        final String spaceWord;

        QueryOperator(String spaceWord) {
            this.spaceWord = spaceWord;
        }


        @Override
        public String spaceRender() {
            return null;
        }

        @Override
        public final String toString() {
            return SQLs.keyWordsToString(this);
        }


    }

    enum KeyWordOn implements SQLs.WordOn {

        ON;


    }//KeyWordOn


    enum KeyWordsCharacterSet implements SQLs.WordsCharacterSet, ArmyKeyWord {

        CHARACTER_SET(" CHARACTER SET");


        private final String spaceWord;

        KeyWordsCharacterSet(String spaceWord) {
            this.spaceWord = spaceWord;
        }

        @Override
        public final String spaceRender() {
            return this.spaceWord;
        }


        @Override
        public final String toString() {
            return SQLs.keyWordsToString(this);
        }

    }//KeyWordsCharacterSet

    enum KeyWordsCollate implements SQLs.WordCollate, ArmyKeyWord {

        COLLATE(" COLLATE");


        private final String spaceWord;

        KeyWordsCollate(String spaceWord) {
            this.spaceWord = spaceWord;
        }

        @Override
        public final String spaceRender() {
            return this.spaceWord;
        }


        @Override
        public final String toString() {
            return SQLs.keyWordsToString(this);
        }

    } // KeyWordsCollate


    enum KeyWordUsing implements SQLs.WordUsing {

        USING;

        @Override
        public final String spaceRender() {
            return " USING";
        }

        @Override
        public final String toString() {
            return SQLs.keyWordsToString(this);
        }


    } //KeyWordUsing

    enum FuncWord implements ArmyKeyWord {

        INTERVAL(" INTERVAL"),
        COMMA(_Constant.SPACE_COMMA),
        USING(_Constant.SPACE_USING),
        AT_TIME_ZONE(" AT TIME ZONE"),
        LEFT_PAREN(_Constant.SPACE_LEFT_PAREN),
        RIGHT_PAREN(_Constant.SPACE_RIGHT_PAREN);

        private final String spaceWords;

        FuncWord(String spaceWords) {
            this.spaceWords = spaceWords;
        }

        @Override
        public final String spaceRender() {
            return this.spaceWords;
        }

        @Override
        public final String toString() {
            return String.format("%s.%s", FuncWord.class.getSimpleName(), this.name());
        }


    } // Word


    enum KeyWordJoin implements SQLs.WordJoin, ArmyKeyWord {

        JOIN;

        @Override
        public final String spaceRender() {
            return " JOIN";
        }

        @Override
        public final String toString() {
            return SQLs.keyWordsToString(this);
        }


    } //  KeyWordJoin

    enum KeyWordsOrderBy implements SQLs.WordsOrderBy, ArmyKeyWord {

        ORDER_BY;

        @Override
        public final String spaceRender() {
            return " ORDER BY";
        }

        @Override
        public final String toString() {
            return SQLs.keyWordsToString(this);
        }

    } //  KeyWordsOrderBy

    enum KeyWordsGroupBy implements SQLs.WordsGroupBy, ArmyKeyWord {

        GROUP_BY;

        @Override
        public final String spaceRender() {
            return " GROUP BY";
        }

        @Override
        public final String toString() {
            return SQLs.keyWordsToString(this);
        }

    } //  KeyWordsGroupBy


    enum KeyWordsAtTimeZone implements SQLs.WordsAtTimeZone, ArmyKeyWord {

        AT_TIME_ZONE;

        @Override
        public final String spaceRender() {
            return " AT TIME ZONE";
        }

        @Override
        public final String toString() {
            return SQLs.keyWordsToString(this);
        }


    } // KeyWordsAtTimeZone

    enum KeyWordVarScope implements SQLs.VarScope {

        AT,

        GLOBAL,
        PERSIST,
        PERSIST_ONLY,
        SESSION,
        LOCAL;

        @Override
        public final String toString() {
            return SQLs.keyWordsToString(this);
        }


    } // KeyWordVariableType

    enum KeyWordMaterialized implements SQLs.WordMaterialized {

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
            return SQLs.keyWordsToString(this);
        }


    } // KeyWordMaterialized

    /**
     * package interface,this interface only is implemented by class or enum,couldn't is extended by interface.
     */
    interface ArmyKeyWord extends SQLWords {

    }
}
