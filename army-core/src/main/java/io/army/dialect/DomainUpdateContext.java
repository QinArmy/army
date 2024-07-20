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

package io.army.dialect;

import io.army.criteria.TableField;
import io.army.criteria.impl.inner._DomainUpdate;
import io.army.meta.FieldMeta;
import io.army.session.SessionSpec;
import io.army.util._Collections;
import io.army.util._Exceptions;

import io.army.lang.Nullable;
import java.util.List;

final class DomainUpdateContext extends DomainDmlStmtContext implements _SingleUpdateContext {

    static DomainUpdateContext forSingle(@Nullable _SqlContext outerContext, _DomainUpdate stmt, ArmyParser parser,
                                         SessionSpec sessionSpec) {
        return new DomainUpdateContext((StatementContext) outerContext, stmt, parser, sessionSpec);
    }

    static DomainUpdateContext forChild(_DomainUpdate stmt, DomainUpdateContext parentContext) {
        return new DomainUpdateContext(stmt, parentContext);
    }

    final DomainUpdateContext parentContext;


    private List<FieldMeta<?>> conditionFieldList;

    private DomainUpdateContext(@Nullable StatementContext outerContext, _DomainUpdate stmt, ArmyParser parser,
                                SessionSpec sessionSpec) {
        super(outerContext, stmt, parser, sessionSpec);
        this.parentContext = null;
    }

    private DomainUpdateContext(_DomainUpdate stmt, DomainUpdateContext parentContext) {
        super(stmt, parentContext);
        this.parentContext = parentContext;
    }

    @Override
    public _UpdateContext parentContext() {
        return this.parentContext;
    }

    @Override
    public void appendConditionFields() {
        final List<FieldMeta<?>> list = this.conditionFieldList;
        if (list != null) {
            _DialectUtils.appendConditionFields(this, list);
        }
    }

    @Override
    void onAddConditionField(final TableField field) {
        if (!(field instanceof FieldMeta)) {
            throw _Exceptions.castCriteriaApi();
        }
        List<FieldMeta<?>> list = this.conditionFieldList;
        if (list == null) {
            list = _Collections.arrayList();
            this.conditionFieldList = list;
        }
        list.add((FieldMeta<?>) field);
    }


}
