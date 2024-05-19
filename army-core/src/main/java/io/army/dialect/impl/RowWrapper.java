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

import io.army.bean.ReadWrapper;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;

import javax.annotation.Nullable;
import java.time.temporal.Temporal;

/**
 * <p>
 * package interface
 *
 * @since 0.6.0
 */
interface RowWrapper {

    /**
     * <p>
     * This method is invoked by {@link FieldValueGenerator#generate(TableMeta, boolean, RowWrapper)}
     *
     *
     * @see FieldValueGenerator#generate(TableMeta, boolean, RowWrapper)
     */
    void set(FieldMeta<?> field, @Nullable Object value);

    /**
     * <p>
     * This method is invoked by {@link  FieldValueGenerator#validate(TableMeta, RowWrapper) }.
     * Note: couldn't read value from default expression map.
     *
     *
     * @see FieldValueGenerator#validate(TableMeta, RowWrapper)
     */
    boolean isNullValueParam(FieldMeta<?> field);


    /**
     * <p>
     * This method is invoked by {@link FieldValueGenerator#generate(TableMeta, boolean, RowWrapper)}
     *
     *
     * @see FieldValueGenerator#generate(TableMeta, boolean, RowWrapper)
     */
    ReadWrapper readonlyWrapper();

    /**
     * @return context create time for createTime field
     */
    Temporal getCreateTime();

    boolean isManageVisible();


}
