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

package io.army.option;

import io.army.session.TimeoutException;

public interface StmtOption extends StmtOptionSpec {

    /**
     * @see io.army.criteria.LiteralMode#LITERAL
     */
    boolean isPreferServerPrepare();

    /**
     * <p>{@link io.army.dialect.DialectParser} Whether parse batch statement as multi-statement or not .
     * <p>Default : false
     *
     * @return true : {@link io.army.dialect.DialectParser} Whether parse batch statement as multi-statement.
     */
    boolean isParseBatchAsMultiStmt();


    boolean isSupportTimeout();

    /**
     * <p>Transaction or statement timeout milli seconds
     *
     * @return 0 or timeout millis
     */
    int timeoutMillis();

    /**
     * <p>Transaction or statement start milli seconds
     *
     * @return 0 or  start time millis
     */
    long startTimeMillis();


    /**
     * <p>Get reset timeout seconds.
     *
     * @throws IllegalStateException throw when {@link #isSupportTimeout()} return false
     * @throws TimeoutException      throw  {@link #isSupportTimeout()} return true and reset time is zero.
     * @see #isSupportTimeout()
     */
    int restSeconds() throws TimeoutException;

    /**
     * <p>Get reset timeout mill seconds.
     *
     * @throws IllegalStateException throw when {@link #isSupportTimeout()} return false
     * @throws TimeoutException      throw  {@link #isSupportTimeout()} return true and reset time is zero.
     * @see #isSupportTimeout()
     */
    int restMillSeconds() throws TimeoutException;

    /**
     * <p>Get frequency to help driver caching server-prepared statement.
     * <p>Default : -1  in the implementation of jdbd-spi,so if you don't invoke this method,driver will ignore this option.
     * <p><Strong>NOTE</Strong>: JDBC don't support this option
     *
     * @return <ul>
     * <li>negative : no action</li>
     * <li>0 : never cache server-prepared statement,if have cached ,close server-prepared statement and delete cache</li>
     * <li>positive : representing frequency</li>
     * <li>{@link Integer#MAX_VALUE} : always cache server-prepared statement</li>
     * </ul>
     */
    int frequency();

    MultiStmtMode multiStmtMode();


    interface BuilderSpec<B> extends OptionBuilderSpec<B> {

        B frequency(int value);

        B timeoutMillis(int millis);

        B multiStmtMode(MultiStmtMode mode);

        B parseBatchAsMultiStmt(boolean yes);

        B preferServerPrepare(boolean yes);


    }


}
