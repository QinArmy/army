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

import io.army.criteria.TabularItem;
import io.army.struct.CodeEnum;

import javax.annotation.Nullable;
import java.util.List;

/**
 * @see SchemaMeta
 * @see FieldMeta
 * @see IndexMeta
 * @see IndexFieldMeta
 */
public interface TableMeta<T> extends TabularItem, DatabaseObject {


    Class<T> javaType();

    /**
     * <p>
     * Table name,Equivalence : {@link  FieldMeta#objectName()}
     *
     */
    String tableName();

    boolean immutable();

    String comment();

    PrimaryFieldMeta<T> id();

    PrimaryFieldMeta<? super T> nonChildId();


    @Nullable
    FieldMeta<? super T> version();

    @Nullable
    FieldMeta<? super T> visible();

    @Nullable
    FieldMeta<? super T> discriminator();

    @Nullable
    CodeEnum discriminatorValue();


    /**
     * contain primary key
     */
    List<IndexMeta<T>> indexList();

    /**
     * @return unmodified list, always same instance.
     * @see io.army.util.ArmyCriteria#fieldListOf(TableMeta)
     */
    List<FieldMeta<T>> fieldList();


    String tableOption();

    SchemaMeta schema();

    boolean containField(String fieldName);

    boolean containComplexField(String fieldName);

    boolean isThisField(FieldMeta<?> field);

    boolean isComplexField(FieldMeta<?> field);

    /**
     * @throws IllegalArgumentException when not found matched {@link FieldMeta} for fieldName
     */
    FieldMeta<T> getField(String fieldName);

    @Nullable
    FieldMeta<T> tryGetField(String fieldName);


    FieldMeta<? super T> getComplexFiled(String filedName);

    @Nullable
    FieldMeta<? super T> tryGetComplexFiled(String filedName);

    /**
     * @throws IllegalArgumentException when not found matched {@link IndexFieldMeta} for fieldName
     */
    IndexFieldMeta<T> getIndexField(String fieldName);

    /**
     * @throws IllegalArgumentException when not found matched {@link UniqueFieldMeta} for fieldName
     */
    UniqueFieldMeta<T> getUniqueField(String fieldName);

    List<FieldMeta<?>> fieldChain();

    @Override
    boolean equals(Object o);

    @Override
    int hashCode();

    @Override
    String toString();


}
