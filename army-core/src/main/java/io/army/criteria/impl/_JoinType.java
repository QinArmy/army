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
import io.army.util._StringUtils;


public enum _JoinType implements SQLWords {

    NONE(""),
    LEFT_JOIN(" LEFT JOIN"),
    JOIN(" JOIN"),
    RIGHT_JOIN(" RIGHT JOIN"),
    FULL_JOIN(" FULL JOIN"),
    CROSS_JOIN(" CROSS JOIN"),

    /**
     * MySQL
     */
    STRAIGHT_JOIN(" STRAIGHT_JOIN");

    public final String spaceWords;

    _JoinType(String spaceWords) {
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


}
