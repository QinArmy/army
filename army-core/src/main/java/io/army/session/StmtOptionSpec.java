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

import io.army.session.record.ResultStates;

import javax.annotation.Nullable;
import java.util.function.Consumer;


/**
 * <p>This interface is base interface of following :
 * <ul>
 *     <li>{@link StmtOption}</li>
 *     <li>{@code  io.army.sync.StreamOption}</li>
 * </ul>
 *
 * @since 0.6.0
 */
public interface StmtOptionSpec {


    /**
     * <p>If fetch size is positive then use database server forward cursor.
     * <p><strong>Limitations</strong> of fetch size :
     * <ul>
     *     <li>JDBC :
     *          <ul>
     *              <li>Postgre : postgre jdbc command {@code java.sql.Connection} is auto commit,so you have to use fetch size in transaction. More document see {@code  io.army.env.SyncKey.POSTGRE_FETCH_SIZE_AUTO_COMMIT}</li>
     *          </ul>
     *     </li>
     * </ul>
     */
    int fetchSize();

    Consumer<ResultStates> stateConsumer();


    interface OptionBuilderSpec<B> {

        B fetchSize(int value);

        B stateConsumer(@Nullable Consumer<ResultStates> consumer);

    }

}
