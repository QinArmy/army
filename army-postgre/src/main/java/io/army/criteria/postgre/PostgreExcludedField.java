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

package io.army.criteria.postgre;

import io.army.criteria.Expression;
import io.army.criteria.FieldSelection;
import io.army.criteria.TableField;
import io.army.criteria.Visible;
import io.army.criteria.impl.OperationDataField;
import io.army.criteria.impl.inner._Selection;
import io.army.dialect.impl._Constant;
import io.army.dialect.impl._SqlContext;
import io.army.meta.FieldMeta;
import io.army.meta.TypeMeta;
import io.army.modelgen._MetaBridge;
import io.army.util._Exceptions;

final class PostgreExcludedField extends OperationDataField implements _Selection, FieldSelection {


    static PostgreExcludedField excludedField(FieldMeta<?> field) {
        return new PostgreExcludedField(field);
    }

    private static final String SPACE_EXCLUDED_PERIOD = " EXCLUDED.";

    private final FieldMeta<?> field;

    private PostgreExcludedField(FieldMeta<?> field) {
        this.field = field;
    }

    @Override
    public String fieldName() {
        return this.field.fieldName();
    }

    @Override
    public TypeMeta typeMeta() {
        return this.field;
    }

    @Override
    public String label() {
        return this.field.fieldName();
    }

    @Override
    public TableField tableField() {
        return this.field;
    }

    @Override
    public Expression underlyingExp() {
        return this.field;
    }

    @Override
    public FieldMeta<?> fieldMeta() {
        return this.field;
    }

    @Override
    public void appendSelectItem(final StringBuilder sqlBuilder, final _SqlContext context) {
        sqlBuilder.append(SPACE_EXCLUDED_PERIOD);

        context.appendFieldOnly(this.field);

        sqlBuilder.append(_Constant.SPACE_AS_SPACE);

        context.parser().identifier(this.field.fieldName(), sqlBuilder);

    }


    @Override
    public void appendSql(final StringBuilder sqlBuilder, final _SqlContext context) {
        if (context.visible() != Visible.BOTH && _MetaBridge.VISIBLE.equals(this.field.fieldName())) {
            throw _Exceptions.visibleField(context.visible(), this.field);
        }

        sqlBuilder.append(SPACE_EXCLUDED_PERIOD);

        context.appendFieldOnly(this.field);
    }



}
