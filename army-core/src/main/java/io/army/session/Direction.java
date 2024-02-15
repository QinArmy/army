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

package io.army.session;

import io.army.util._StringUtils;

/**
 * <p> This enum representing cursor
 */
public enum Direction {


    /**
     * Fetch the next row.
     */
    NEXT,

    /**
     * Fetch the prior row.
     */
    PRIOR,

    /**
     * Fetch the first row of the query (same as ABSOLUTE 1).
     */
    FIRST,

    /**
     * Fetch the last row of the query (same as ABSOLUTE -1).
     */
    LAST,

    /**
     * must specified count
     */
    ABSOLUTE,

    /**
     * must specified count
     */
    RELATIVE,

    /**
     * Fetch the next row (same as NEXT).
     * must specified count
     */
    FORWARD,

    /**
     * Fetch all remaining rows.
     */
    FORWARD_ALL,

    /**
     * must specified count
     */
    BACKWARD,

    /**
     * Fetch all prior rows (scanning backwards).
     */
    BACKWARD_ALL;


    @Override
    public final String toString() {
        return _StringUtils.enumToString(this);
    }


    public final boolean isNotOneRow() {
        final boolean match;
        switch (this) {
            case FIRST:
            case LAST:
            case PRIOR:
            case NEXT:
            case FORWARD:
            case BACKWARD:
                match = false;
                break;
            default:
                match = true;
        }
        return match;
    }


    public final boolean isNotSupportRowCount() {
        final boolean match;
        switch (this) {
            case FORWARD:
            case BACKWARD:
            case ABSOLUTE:
            case RELATIVE:
                match = false;
                break;
            default:
                match = true;
        }
        return match;
    }


    public final boolean isNotNoRowCount() {
        final boolean match;
        switch (this) {
            case FIRST:
            case LAST:
            case PRIOR:
            case NEXT:
            case FORWARD:
            case BACKWARD:
            case BACKWARD_ALL:
            case FORWARD_ALL:
                match = false;
                break;
            default:
                match = true;
        }
        return match;
    }


}
