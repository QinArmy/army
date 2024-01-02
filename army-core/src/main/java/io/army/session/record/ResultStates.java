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

import io.army.session.OptionSpec;
import io.army.session.Warning;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public interface ResultStates extends ResultItem, OptionSpec {

    Consumer<ResultStates> IGNORE_STATES = states -> {
    };


    boolean inTransaction();

    long affectedRows();

    /**
     * @return empty or  success info(maybe contain warning info)
     */
    String message();

    boolean hasMoreResult();


    /**
     * @return true representing exists server cursor and the last row don't send.
     */
    boolean hasMoreFetch();

    /**
     * @return <ul>
     * <li>true : this instance representing the terminator of query result (eg: SELECT command)</li>
     * <li>false : this instance representing the update result (eg: INSERT/UPDATE/DELETE command)</li>
     * </ul>
     */
    boolean hasColumn();

    /**
     * @return the row count.<ul>
     * <li>If use fetch (eg: {@code  Statement#setFetchSize(int)} , {@code  RefCursor}) , then the row count representing only the row count of current fetch result.</li>
     * <li>Else then the row count representing the total row count of query result.</li>
     * </ul>
     */
    long rowCount();


    @Nullable
    Warning warning();

}
