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

package io.army.schema;

import javax.annotation.Nullable;

public interface ColumnInfo {

    String columnName();

    /**
     * @return upper case type name.
     */
    String typeName();

    @Nullable
    String defaultExp();

    @Nullable
    Boolean nullable();

    int precision();

    int scale();

    @Nullable
    String comment();

    boolean autoincrement();

    static Builder builder() {
        return ColumnInfoImpl.createBuilder();
    }


    interface Builder {

        Builder name(String columnName);

        Builder type(String sqlType);

        Builder precision(int precision);

        Builder scale(int scale);

        Builder defaultExp(@Nullable String defaultExp);

        Builder nullable(@Nullable Boolean nullable);

        Builder comment(@Nullable String comment);

        Builder autoincrement(boolean autoincrement);

        ColumnInfo buildAndClear();

    }


}
