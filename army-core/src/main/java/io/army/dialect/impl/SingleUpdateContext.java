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

import io.army.criteria.TableField;
import io.army.criteria.impl.inner._SingleUpdate;
import io.army.session.SessionSpec;
import io.army.util._Collections;

import javax.annotation.Nullable;
import java.util.List;

final class SingleUpdateContext extends SingleDmlContext implements _SingleUpdateContext {

    static SingleUpdateContext create(@Nullable _SqlContext outerContext, _SingleUpdate stmt,
                                      ArmyParser dialect, SessionSpec sessionSpec) {
        return new SingleUpdateContext((StatementContext) outerContext, stmt, dialect, sessionSpec);
    }

    static SingleUpdateContext forParent(_SingleUpdate._ChildUpdate stmt, ArmyParser dialect, SessionSpec sessionSpec) {
        return new SingleUpdateContext(null, stmt, dialect, sessionSpec);
    }

    static SingleUpdateContext forChild(_SingleUpdate._ChildUpdate stmt, SingleUpdateContext parentContext) {
        return new SingleUpdateContext(stmt, parentContext);
    }

    private final _UpdateContext parentContext;


    private List<TableField> conditionFieldList;

    private SingleUpdateContext(@Nullable StatementContext outerContext, _SingleUpdate stmt, ArmyParser dialect,
                                SessionSpec sessionSpec) {
        super(outerContext, stmt, dialect, sessionSpec);
        this.parentContext = null;
    }


    private SingleUpdateContext(_SingleUpdate stmt, SingleUpdateContext parentContext) {
        super(stmt, parentContext);
        this.parentContext = parentContext;
    }

    @Override
    public _UpdateContext parentContext() {
        return this.parentContext;
    }


    @Override
    public void appendConditionFields() {
        final List<TableField> conditionFieldList = this.conditionFieldList;
        if (conditionFieldList != null) {
            _DialectUtils.appendConditionFields(this, conditionFieldList);
        }

    }


    @Override
    void onAddConditionField(final TableField field) {
        List<TableField> list = this.conditionFieldList;
        if (list == null) {
            list = _Collections.arrayList();
            this.conditionFieldList = list;
        }
        list.add(field);
    }


}
