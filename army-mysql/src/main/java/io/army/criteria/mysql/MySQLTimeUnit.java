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
import io.army.util._StringUtils;

public enum MySQLTimeUnit implements SqlWords.ArmyKeyWord {

    MICROSECOND(" MICROSECOND"),
    SECOND(" SECOND"),
    MINUTE(" MINUTE"),
    HOUR(" HOUR"),

    DAY(" DAY"),
    WEEK(" WEEK"),
    MONTH(" MONTH"),
    QUARTER(" QUARTER"),

    YEAR(" YEAR"),
    SECOND_MICROSECOND(" SECOND_MICROSECOND"),
    MINUTE_MICROSECOND(" MINUTE_MICROSECOND"),
    MINUTE_SECOND(" MINUTE_SECOND"),

    HOUR_MICROSECOND(" HOUR_MICROSECOND"),
    HOUR_SECOND(" HOUR_SECOND"),
    HOUR_MINUTE(" HOUR_MINUTE"),
    DAY_MICROSECOND(" DAY_MICROSECOND"),

    DAY_SECOND(" DAY_SECOND"),
    DAY_MINUTE(" DAY_MINUTE"),
    DAY_HOUR(" DAY_HOUR"),
    YEAR_MONTH(" YEAR_MONTH");

    private final String spaceWords;

    MySQLTimeUnit(String spaceWords) {
        this.spaceWords = spaceWords;
    }

    @Override
    public final String spaceRender() {
        return this.spaceWords;
    }


    @Override
    public final String toString() {
        return _StringUtils.enumToString(this);
    }

    public final boolean isTimePart() {
        final boolean match;
        switch (this) {
            case HOUR:
            case MINUTE:
            case SECOND:
            case DAY_HOUR:
            case DAY_MINUTE:
            case DAY_SECOND:
            case DAY_MICROSECOND:
            case HOUR_MINUTE:
            case HOUR_SECOND:
            case HOUR_MICROSECOND:
            case MINUTE_SECOND:
            case MINUTE_MICROSECOND:
            case SECOND_MICROSECOND:
            case MICROSECOND:
                match = true;
                break;
            default:
                match = false;

        }

        return match;
    }


}
