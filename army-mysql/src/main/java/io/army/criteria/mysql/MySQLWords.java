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

package io.army.criteria.mysql;

import io.army.criteria.impl.SqlWords;

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

    enum KeyWordDistinct implements SqlWords.ArmyKeyWord, MySQLs.WordDistinct {

        DISTINCT;

        @Override
        public final String spaceRender() {
            return " DISTINCT";
        }


        @Override
        public final String toString() {
            return MySQLSyntax.keyWordsToString(this);
        }


    }//WordDistinct


}
