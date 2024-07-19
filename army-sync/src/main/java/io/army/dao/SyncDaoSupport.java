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

package io.army.dao;


import javax.annotation.Nullable;
import java.util.List;

/**
 * <p>This interface is designed for dao layer.
 *
 * @since 0.6.0
 */
public interface SyncDaoSupport {

    <T> void save(T domain);

    <T> void batchSave(List<T> domainList);

    @Nullable
    <T> T get(Class<T> domainClass, Object id);

    @Nullable
    <T> T getByUnique(Class<T> domainClass, String fieldName, Object fieldValue);

    @Nullable
    <T> T findById(Class<T> domainClass, Object id);

    @Nullable
    <T> T findByUnique(Class<T> domainClass, String fieldName, Object fieldValue);


    <T> long rowCount(Class<T> domainClass);

    <T> int rowCountAsInt(Class<T> domainClass);


}
