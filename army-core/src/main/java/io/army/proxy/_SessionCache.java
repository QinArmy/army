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

package io.army.proxy;


import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.meta.UniqueFieldMeta;

import java.util.List;

public interface _SessionCache {

    @Nullable
    <T> T get(TableMeta<T> table, Object id);

    @Nullable
    <T> T get(TableMeta<T> table, UniqueFieldMeta<? super T> field, Object fieldValue);

    <T> T putIfAbsent(TableMeta<T> table, T domain);


    List<_CacheBlock> getChangedList();

    void clearChangedOnRollback();


    void clearOnSessionCLose();

}
