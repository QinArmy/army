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
import io.army.criteria.standard.SQLs;
import io.army.dialect._Constant;
import io.army.util._StringUtils;

public abstract class SqlWords {

    private SqlWords() {
        throw new UnsupportedOperationException();
    }

    public static String keyWordsToString(Enum<?> wordEnum) {
        return _StringUtils.builder(20)
                .append(SQLs.class.getSimpleName())
                .append(_Constant.PERIOD)
                .append(wordEnum.name())
                .toString();
    }


    public enum SymbolSpaceEnum implements SQLs.SymbolSpace {

        SPACE;

        @Override
        public final String toString() {
            return keyWordsToString(this);
        }

    } // SymbolSpaceEnum


    public enum SymbolEqualEnum implements SQLs.SymbolEqual {

        EQUAL;

        @Override
        public final String toString() {
            return keyWordsToString(this);
        }
    }

    public enum SymbolColonEqualEnum implements SQLs.SymbolColonEqual {

        COLON_EQUAL;

        @Override
        public final String toString() {
            return keyWordsToString(this);
        }

    }

    public enum KeyWordNotNull implements SQLs.NullOption, ArmyKeyWord {

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


    public enum KeyWordIn implements SQLs.WordIn, ArmyKeyWord {

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

    public enum KeyWordSimilar implements SQLs.WordSimilar, ArmyKeyWord {

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
            return keyWordsToString(this);
        }

    }//KeyWordSimilar

    public enum KeyWordFrom implements SQLs.WordFrom, ArmyKeyWord {

        FROM;

        @Override
        public final String spaceRender() {
            return " FROM";
        }

        @Override
        public final String toString() {
            return keyWordsToString(this);
        }


    }//KeyWordFrom

    public enum KeyWordFor implements SQLs.WordFor, ArmyKeyWord {

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
            return keyWordsToString(this);
        }


    }//KeyWordFor

    public enum WordTrimPosition implements SQLs.TrimSpec, ArmyKeyWord {

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
            return keyWordsToString(this);
        }


    } // WordTrimPosition


    public enum KeyWordPath implements SQLs.WordPath, ArmyKeyWord {

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

    public enum KeyWordColumns implements SQLs.WordColumns, ArmyKeyWord {

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


    public enum KeyWordNested implements SQLs.WordNested, ArmyKeyWord {

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

    public enum KeyWordExists implements SQLs.WordExists, ArmyKeyWord {

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

    public enum KeyWordsForOrdinality implements SQLs.WordsForOrdinality, ArmyKeyWord {

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


    public enum KeyWordError implements SQLs.WordError, ArmyKeyWord {

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

    public enum KeyWordDocument implements SQLs.WordDocument, ArmyKeyWord {

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

    public enum KeyWordContent implements SQLs.WordContent, ArmyKeyWord {

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
    public enum BooleanTestKeyWord implements SQLs.BooleanTestWord, ArmyKeyWord {

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

    public enum IsComparisonKeyWord implements SQLs.IsComparisonWord, ArmyKeyWord {

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


    public enum KeyWordAll implements SQLWords, SQLs.WordAll, ArmyKeyWord {

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
            return keyWordsToString(this);
        }


    }//KeyWordAll

    public enum KeyWordDistinct implements ArmyKeyWord, SQLs.WordDistinct {

        DISTINCT;

        @Override
        public final String spaceRender() {
            return " DISTINCT";
        }

        @Override
        public final String toString() {
            return keyWordsToString(this);
        }


    }//KeyWordDistinct

    public enum KeyWordInterval implements SQLs.WordInterval, ArmyKeyWord {

        INTERVAL;

        @Override
        public final String spaceRender() {
            return " INTERVAL";
        }

        @Override
        public final String toString() {
            return keyWordsToString(this);
        }

    }//KeyWordInterval

    public enum KeyWordPercent implements SQLs.WordPercent, SQLWords {

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
            return keyWordsToString(this);
        }

    }//KeyWordPercent

    public enum KeyWordUnknown implements SQLs.BooleanTestWord, ArmyKeyWord {

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
            return keyWordsToString(this);
        }

    } //KeyWordUnknown

    public enum KeyWordAscDesc implements SQLs.AscDesc, SQLWords {

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
            return keyWordsToString(this);
        }

    }//KeyWordAscDesc

    public enum KeyWordLateral implements SQLs.WordLateral {

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
            return keyWordsToString(this);
        }

    }//KeyWordLateral

    public enum KeyWordFirst implements SQLs.WordFirst, SQLWords {

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
            return keyWordsToString(this);
        }

    }//KeyWordFirst

    public enum KeyWordNext implements SQLs.WordNext, SQLWords {

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
            return keyWordsToString(this);
        }

    }//KeyWordNext

    public enum KeyWordsNullsFirstLast implements SQLs.NullsFirstLast, SQLWords {

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
            return keyWordsToString(this);
        }

    }//KeyWordsNullsFirstLast

    public enum KeyWordRow implements SQLs.WordRow, SQLWords {

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
            return keyWordsToString(this);
        }

    }//KeyWordRow

    public enum KeyWordRows implements SQLs.WordRows, ArmyKeyWord {

        ROWS;

        @Override
        public final String spaceRender() {
            return " ROWS";
        }

        @Override
        public final String toString() {
            return keyWordsToString(this);
        }

    } //KeyWordRows


    public enum KeyWordLines implements SQLs.WordLines, ArmyKeyWord {

        LINES;

        @Override
        public final String spaceRender() {
            return " LINES";
        }

        @Override
        public final String toString() {
            return keyWordsToString(this);
        }

    } //KeyWordLines

    public enum KeyWordWithTies implements SQLs.WordsWithTies, SQLWords {

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
            return keyWordsToString(this);
        }

    }//KeyWordNext

    public enum KeyWordOny implements SQLs.WordOnly, SQLWords {

        ONLY;

        @Override
        public final String spaceRender() {
            return _Constant.SPACE_ONLY;
        }

        @Override
        public final String toString() {
            return keyWordsToString(this);
        }

    }//KeyWordOny

    public enum KeyWordAs implements SQLs.WordAs, ArmyKeyWord {

        AS;

        @Override
        public final String spaceRender() {
            return " AS";
        }

        @Override
        public final String toString() {
            return keyWordsToString(this);
        }

    } // KeyWordAs

    public enum KeyWordTo implements SQLs.WordTo, ArmyKeyWord {

        TO;

        @Override
        public final String spaceRender() {
            return " TO";
        }

        @Override
        public final String toString() {
            return keyWordsToString(this);
        }

    } // KeyWordTo

    public enum KeyWordAnd implements SQLs.WordAnd, ArmyKeyWord {

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
            return keyWordsToString(this);
        }

    }//KeyWordAnd

    public enum KeyWordSymmetric implements SQLs.BetweenModifier, ArmyKeyWord {

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
            return keyWordsToString(this);
        }

    }//KeyWordSymmetric

    public enum SQLSymbolPeriod implements SQLs.SymbolPeriod {

        PERIOD;

        @Override
        public final String toString() {
            return keyWordsToString(this);
        }

    }//SQLSymbolPoint

    public enum SQLSymbolAsterisk implements SQLs.SymbolAsterisk, SQLWords {

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
            return keyWordsToString(this);
        }

    }//SQLSymbolStar

    public enum KeyWordEscape implements SQLs.WordEscape, ArmyKeyWord {

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
            return keyWordsToString(this);
        }

    }//KeyWordEscape

    /**
     * @see SQLs#ALL
     */
    public enum QueryOperator implements SQLs.QuantifiedWord {

        ANY(" ANY"),
        SOME(" SOME");

        final String spaceWord;

        QueryOperator(String spaceWord) {
            this.spaceWord = spaceWord;
        }


        @Override
        public final String spaceRender() {
            return this.spaceWord;
        }

        @Override
        public final String toString() {
            return keyWordsToString(this);
        }


    }

    public enum KeyWordOn implements SQLs.WordOn {

        ON;


    }//KeyWordOn


    public enum KeyWordsCharacterSet implements SQLs.WordsCharacterSet, ArmyKeyWord {

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
            return keyWordsToString(this);
        }

    }//KeyWordsCharacterSet

    public enum KeyWordsCollate implements SQLs.WordCollate, ArmyKeyWord {

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
            return keyWordsToString(this);
        }

    } // KeyWordsCollate


    public enum KeyWordUsing implements SQLs.WordUsing {

        USING;

        @Override
        public final String spaceRender() {
            return " USING";
        }

        @Override
        public final String toString() {
            return keyWordsToString(this);
        }


    } //KeyWordUsing

    public enum FuncWord implements ArmyKeyWord {

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


    public enum KeyWordJoin implements SQLs.WordJoin, ArmyKeyWord {

        JOIN;

        @Override
        public final String spaceRender() {
            return " JOIN";
        }

        @Override
        public final String toString() {
            return keyWordsToString(this);
        }


    } //  KeyWordJoin

    public enum KeyWordsOrderBy implements SQLs.WordsOrderBy, ArmyKeyWord {

        ORDER_BY;

        @Override
        public final String spaceRender() {
            return " ORDER BY";
        }

        @Override
        public final String toString() {
            return keyWordsToString(this);
        }

    } //  KeyWordsOrderBy

    public enum KeyWordsGroupBy implements SQLs.WordsGroupBy, ArmyKeyWord {

        GROUP_BY;

        @Override
        public final String spaceRender() {
            return " GROUP BY";
        }

        @Override
        public final String toString() {
            return keyWordsToString(this);
        }

    } //  KeyWordsGroupBy


    public enum KeyWordsAtTimeZone implements SQLs.WordsAtTimeZone, ArmyKeyWord {

        AT_TIME_ZONE;

        @Override
        public final String spaceRender() {
            return " AT TIME ZONE";
        }

        @Override
        public final String toString() {
            return keyWordsToString(this);
        }


    } // KeyWordsAtTimeZone

    public enum KeyWordVarScope implements SQLs.VarScope {

        AT,

        GLOBAL,
        PERSIST,
        PERSIST_ONLY,
        SESSION,
        LOCAL;

        @Override
        public final String toString() {
            return keyWordsToString(this);
        }


    } // KeyWordVariableType

    public enum KeyWordMaterialized implements SQLs.WordMaterialized {

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
            return keyWordsToString(this);
        }


    } // KeyWordMaterialized

    /**
     * package interface,this interface only is implemented by class or enum,couldn't is extended by interface.
     */
    public interface ArmyKeyWord extends SQLWords {

    }


}
