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

package io.army.session.record;

import io.army.session.DataAccessException;

public interface ResultItem {

    /**
     * <p>Get result no (based one)
     *
     * @return result no (based one)
     */
    int resultNo();


    interface ResultAccessSpec {
        /**
         * Returns the number of row
         *
         * @return the number of row
         */
        int getColumnCount();

        /**
         * Get column label of appropriate column
         *
         * @param indexBasedZero index based zero,the first value is 0 .
         * @return the suggested column title              .
         * @throws DataAccessException throw when indexBasedZero error
         */
        String getColumnLabel(int indexBasedZero) throws DataAccessException;


        /**
         * <p>
         * Get column index , if columnLabel duplication ,then return last index that have same columnLabel.
         * <br/>
         *
         * @param columnLabel column label
         * @return index based zero,the first value is 0 .
         * @throws DataAccessException throw when indexBasedZero error
         */
        int getColumnIndex(String columnLabel) throws DataAccessException;

    } // ResultAccessSpec


}
