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

package io.army.sync;


import io.army.env.ArmyKey;

import javax.annotation.Nullable;

public final class SyncKey<T> extends ArmyKey<T> {

    public static final SyncKey<Boolean> JDBC_FORBID_V18 = new SyncKey<>("sync.jdbc.v18", Boolean.class, Boolean.FALSE);

    /**
     * <p>Postgre jdbc command ,see
     * <ul>
     *     <li>{@code org.postgresql.jdbc.PgStatement.executeInternal()}</li>
     *     <li>org.postgresql.core.QueryExecutor.QUERY_FORWARD_CURSOR</li>
     * </ul>
     */
    public static final SyncKey<Boolean> POSTGRE_FETCH_SIZE_AUTO_COMMIT = new SyncKey<>("sync.jdbc.postgre.fetch_size_auto_commit", Boolean.class, Boolean.TRUE);

    public static final SyncKey<Boolean> SESSION_IDENTIFIER_ENABLE = new SyncKey<>("sync.session.identifier.enable", Boolean.class, Boolean.FALSE);

    /**
     * @see #EXECUTOR_PROVIDER_MD5
     */
    public static final SyncKey<String> EXECUTOR_PROVIDER = new SyncKey<>("sync.executor.provider", String.class, "io.army.jdbc.JdbcExecutorFactoryProvider");

    /**
     * @see #EXECUTOR_PROVIDER
     */
    public static final SyncKey<String> EXECUTOR_PROVIDER_MD5 = new SyncKey<>("sync.executor.provider_md5", String.class, "0e6c8da4cf23b959d280ff0b1122db5c");

    private SyncKey(String name, Class<T> javaType, @Nullable T defaultValue) {
        super(name, javaType, defaultValue);
    }

}
