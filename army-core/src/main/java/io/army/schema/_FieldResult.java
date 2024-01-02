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

import io.army.meta.FieldMeta;

/**
 * @see _TableResult
 */
public interface _FieldResult {

    FieldMeta<?> field();

    boolean containSqlType();

    boolean containDefault();

    boolean containNullable();

    boolean containComment();

    static Builder builder() {
        return FieldResultImpl.builder();
    }

    interface Builder {

        Builder field(FieldMeta<?> field);

        Builder sqlType(boolean sqlType);

        Builder defaultExp(boolean defaultExp);

        Builder nullable(boolean nullable);

        Builder comment(boolean comment);

        boolean hasDifference();

        void clear();

        _FieldResult build();

    }


}
