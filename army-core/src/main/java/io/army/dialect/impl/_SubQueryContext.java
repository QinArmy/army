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

package io.army.dialect.impl;

import io.army.meta.FieldMeta;

/**
 * <p>
 * Package interface,this interface representing sub query context.
 *
 * @see _SelectContext
 * @see _SimpleQueryContext
 * @since 0.6.0
 */
interface _SubQueryContext extends _SqlContext {

    /**
     * <p>
     * Just append this context field,don't contain outer context field.
     *
     */
    void appendThisField(String tableAlias, FieldMeta<?> field);

    /**
     * <p>
     * Just append this context field,don't contain outer context field.
     *
     */
    void appendThisField(FieldMeta<?> field);

    /**
     * <p>
     * Just append this context field,don't contain outer context field.
     * no preceding space ,no preceding table alias.
     *
     *
     * @see _SqlContext#appendFieldOnly(FieldMeta)
     */
    void appendThisFieldOnly(FieldMeta<?> field);

}
