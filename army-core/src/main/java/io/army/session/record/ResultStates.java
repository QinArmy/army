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
import io.army.session.OptionSpec;
import io.army.session.Warning;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public interface ResultStates extends ResultItem, OptionSpec {

    Consumer<ResultStates> IGNORE_STATES = states -> {
    };

    /**
     * Whether support {@link #lastInsertedId()} method or not.
     * <p>If false ,then database usually support RETURNING clause,it better than lastInsertedId ,for example : PostgreSQL
     *
     * @return true : support
     */
    boolean isSupportInsertId();

    /**
     * the last inserted id, a unsigned long .
     * <strong>NOTE</strong>:
     * <ul>
     *     <li>when {@link #isSupportInsertId()} is false,throw {@link DataAccessException} . now database usually support RETURNING clause,it better than lastInsertedId ,for example : PostgreSQL</li>
     *     <li>when use multi-row insert syntax ,the last inserted id is the first row id.</li>
     *     <li>If you use multi-row insert syntax and exists conflict clause(e.g : MySQL ON DUPLICATE KEY UPDATE),then database never return correct lastInsertedId</li>
     *     <li>Due to army auto append RETURNING id for INSERT statement if database support(eg: postgre),so database server don't return last inserted id</li>
     * </ul>
     *
     * @return the last inserted id
     * @throws DataAccessException throw when {@link #isSupportInsertId()} return false
     */
    long lastInsertedId() throws DataAccessException;

    boolean inTransaction();

    long affectedRows();

    /**
     * @return empty or  success info(maybe contain warning info)
     */
    String message();


    /**
     * <p>Test this result whether is one of batch result ot not.
     *
     * @return true : this result is one of batch result.
     */
    boolean isBatch();


    /**
     * Whether exists more result after this result or not .
     * <ul>
     *     <li>simple single statement always false</li>
     *     <li>multi-result statement,for example stored procedure ,multi-statement ,last result is false,before last result is true</li>
     *     <li>batch statement,for example,batch update/query  ,last batch item is false,before last batch item is true</li>
     * </ul>
     *
     * @return true : exists more result after this result
     */
    boolean hasMoreResult();


    /**
     * @return true representing exists server cursor and the last row don't send.
     * @see #hasColumn()
     */
    boolean hasMoreFetch();

    /**
     * <p>Test result whether is query result or not.
     *
     * @return <ul>
     * <li>true : this instance representing the terminator of query result (eg: SELECT command)</li>
     * <li>false : this instance representing the update result (eg: INSERT/UPDATE/DELETE command)</li>
     * </ul>
     * @see #rowCount()
     */
    boolean hasColumn();

    /**
     * @return the row count.
     * <ul>
     *     <li>If {@link #hasColumn()} is false, then 0</li>
     *     <li>Else if use fetch (eg: {@code  Statement#setFetchSize(int)} , {@code  RefCursor}) , then the row count representing only the row count of current fetch result.</li>
     *     <li>Else then the row count representing the total row count of query result.</li>
     * </ul>
     * @see #hasColumn()
     */
    long rowCount();


    @Nullable
    Warning warning();

}
