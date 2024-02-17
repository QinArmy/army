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

import io.army.criteria.Expression;
import io.army.criteria.Item;
import io.army.criteria.dialect.DmlCommand;
import io.army.criteria.impl.inner.mysql._MySQLSet;
import io.army.criteria.mysql.MySQLSet;
import io.army.util._Assert;
import io.army.util._Collections;
import io.army.util._Exceptions;
import io.army.util._StringUtils;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

final class MySQLSets extends CriteriaSupports.StatementMockSupport implements MySQLSet._SetClause,
        MySQLSet._SetCommaClause, _MySQLSet, DmlCommand, MySQLSet {

    static MySQLSet._SetClause setStmt() {
        return new MySQLSets();
    }

    private List<_Triple<MySQLs.VarScope, String, Object>> tripleList = _Collections.arrayList();

    private Boolean prepared;


    private MySQLSets() {
        super(CriteriaContexts.otherPrimaryContext(MySQLUtils.DIALECT));
        ContextStack.push(this.context);
    }


    @Override
    public MySQLSet._SetCommaClause set(MySQLs.VarScope scope, String name, @Nullable Object value) {
        return addOne(scope, name, value);
    }

    @Override
    public MySQLSet._SetCommaClause set(MySQLs.VarScope scope1, String name1, @Nullable Object value1, MySQLs.VarScope scope2, String name2, @Nullable Object value2) {
        return addOne(scope1, name1, value1)
                .addOne(scope2, name2, value2);
    }


    @Override
    public MySQLSet._SetCommaClause comma(MySQLs.VarScope scope, String name, @Nullable Object value) {
        return addOne(scope, name, value);
    }

    @Override
    public MySQLSet._SetCommaClause comma(MySQLs.VarScope scope1, String name1, @Nullable Object value1, MySQLs.VarScope scope2, String name2, @Nullable Object value2) {
        return addOne(scope1, name1, value1)
                .addOne(scope2, name2, value2);
    }


    @Override
    public DmlCommand asCommand() {
        _Assert.nonPrepared(this.prepared);

        final List<_Triple<MySQLs.VarScope, String, Object>> list = this.tripleList;

        if (!(list instanceof ArrayList) || list.size() == 0) {
            throw ContextStack.clearStackAndCastCriteriaApi();
        }
        this.tripleList = _Collections.unmodifiableList(list);

        ContextStack.pop(this.context);

        this.prepared = Boolean.TRUE;
        return this;
    }

    @Override
    public void prepared() {
        _Assert.prepared(this.prepared);
    }

    @Override
    public boolean isPrepared() {
        final Boolean prepared = this.prepared;
        return prepared != null && prepared;
    }

    @Override
    public void clear() {
        _Assert.prepared(this.prepared);
        this.tripleList = null;
        this.prepared = Boolean.FALSE;
    }

    @Override
    public List<_Triple<MySQLs.VarScope, String, Object>> tripleList() {
        final List<_Triple<MySQLs.VarScope, String, Object>> list = this.tripleList;
        if (list == null || list instanceof ArrayList) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return list;
    }

    private MySQLSets addOne(final MySQLs.VarScope type, final String name, @Nullable Object value) {
        final List<_Triple<MySQLs.VarScope, String, Object>> list = this.tripleList;

        if (!(list instanceof ArrayList)) {
            throw ContextStack.clearStackAndCastCriteriaApi();
        }

        if (!(type instanceof MySQLWords.KeyWordVarScope)) {
            throw CriteriaUtils.unknownWords(type);
        } else if (!_StringUtils.hasText(name)) {
            throw ContextStack.clearStackAnd(_Exceptions::varNameNoText);
        }

        if (value == null) {
            value = SQLs.NULL;
        } else if (value instanceof Expression) {
            if (!(value instanceof ArmyExpression)) {
                throw ContextStack.clearStackAndNonArmyItem((Expression) value);
            }
        } else if (value instanceof Item) {
            throw CriteriaUtils.mustExpressionOrLiteral("value");
        }

        list.add(_Triple.create(type, name, value));
        return this;
    }


}
