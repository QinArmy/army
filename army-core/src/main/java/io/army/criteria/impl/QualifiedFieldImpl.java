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

package io.army.criteria.impl;

import io.army.annotation.GeneratorType;
import io.army.annotation.UpdateMode;
import io.army.criteria.QualifiedField;
import io.army.criteria.TableField;
import io.army.criteria.Visible;
import io.army.criteria.impl.inner._Expression;
import io.army.dialect._Constant;
import io.army.dialect._SqlContext;
import io.army.mapping.MappingType;
import io.army.meta.FieldMeta;
import io.army.meta.ParentTableMeta;
import io.army.meta.TableMeta;
import io.army.meta.TypeMeta;
import io.army.modelgen._MetaBridge;
import io.army.util._Exceptions;
import io.army.util._StringUtils;

import java.util.Objects;


final class QualifiedFieldImpl<T> extends OperationDataField implements QualifiedField<T> {

    static <T> QualifiedFieldImpl<T> create(final String tableAlias, final FieldMeta<T> field) {
        return new QualifiedFieldImpl<>(tableAlias, field);
    }


    final String tableAlias;

    final TableFieldMeta<T> field;

    private QualifiedFieldImpl(String tableAlias, FieldMeta<T> field) {
        this.field = (TableFieldMeta<T>) field;
        this.tableAlias = tableAlias;
    }

    @Override
    public UpdateMode updateMode() {
        return this.field.updateMode;
    }

    @Override
    public boolean codec() {
        return this.field.codec();
    }

    @Override
    public boolean notNull() {
        return this.field.notNull;
    }

    @Override
    public String comment() {
        return this.field.comment();
    }

    @Override
    public TypeMeta typeMeta() {
        return this.field;
    }

    @Override
    public TableField tableField() {
        // return this
        return this;
    }

    @Override
    public _Expression underlyingExp() {
        return this;
    }

    @Override
    public void appendSelectItem(final StringBuilder sqlBuilder, final _SqlContext context) {
        context.appendField(this.tableAlias, this.field);

        sqlBuilder.append(_Constant.SPACE_AS_SPACE);

        context.identifier(this.field.fieldName, sqlBuilder);
    }

    @Override
    public void appendSql(final StringBuilder sqlBuilder, final _SqlContext context) {
        if (context.visible() != Visible.BOTH && _MetaBridge.VISIBLE.equals(this.field.fieldName)) {
            throw _Exceptions.visibleField(context.visible(), this);
        }
        context.appendField(this.tableAlias, this.field);
    }

    @Override
    public boolean currentLevelContainFieldOf(ParentTableMeta<?> table) {
        return this.field.table == table;
    }

    @Override
    public String label() {
        return this.field.fieldName;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.tableAlias, this.field);
    }

    @Override
    public boolean equals(final Object obj) {
        final boolean match;
        if (obj == this) {
            match = true;
        } else if (obj instanceof QualifiedFieldImpl) {
            final QualifiedFieldImpl<?> o = (QualifiedFieldImpl<?>) obj;
            match = o.field == this.field && o.tableAlias.equals(this.tableAlias);
        } else {
            match = false;
        }
        return match;
    }

    @Override
    public String toString() {
        return _StringUtils.builder()
                .append(_Constant.SPACE)
                .append(this.tableAlias)
                .append(_Constant.PERIOD)
                .append(this.field)
                .toString();
    }

    @Override
    public String tableAlias() {
        return this.tableAlias;
    }

    @Override
    public FieldMeta<T> fieldMeta() {
        return this.field;
    }

    @Override
    public TableMeta<T> tableMeta() {
        return this.field.table;
    }

    @Override
    public Class<?> javaType() {
        return this.field.javaType;
    }

    @Override
    public String fieldName() {
        return this.field.fieldName;
    }

    @Override
    public String columnName() {
        return this.field.columnName;
    }

    @Override
    public String objectName() {
        return this.field.columnName;
    }

    @Override
    public MappingType mappingType() {
        return this.field.mappingType;
    }

    @Override
    public boolean insertable() {
        return this.field.insertable;
    }

    @Override
    public GeneratorType generatorType() {
        return this.field.generatorType;
    }


}
