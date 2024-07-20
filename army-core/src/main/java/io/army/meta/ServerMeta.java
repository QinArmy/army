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

package io.army.meta;

import io.army.dialect.Database;
import io.army.dialect.Dialect;

import io.army.lang.Nullable;

public interface ServerMeta {

    String name();

    Database serverDatabase();

    @Nullable
    String catalog();

    @Nullable
    String schema();

    String version();

    int major();

    int minor();

    int subMinor();

    /**
     * from {@link io.army.env.ArmyKey#DIALECT}
     */
    Dialect usedDialect();


    boolean meetsMinimum(int major, int minor, int subMinor);

    boolean isSupportSavePoints();

    /**
     * @return spi name ,for example java.sql or io.jdbd
     */
    String driverSpi();

    static Builder builder() {
        return ServerMetaImpl.builder();
    }

    interface Builder {

        Builder name(String name);

        Builder catalog(String catalogName);

        Builder schema(String schemaName);

        Builder database(Database database);

        Builder version(String version);

        Builder major(int major);

        Builder minor(int minor);

        Builder subMinor(int subMinor);

        Builder usedDialect(Dialect dialect);

        Builder supportSavePoint(boolean support);

        Builder driverSpi(String spi);

        ServerMeta build();

    }


}
