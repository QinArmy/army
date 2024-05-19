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

import io.army.criteria.Statement;
import io.army.criteria.dialect.DmlCommand;
import io.army.criteria.postgre.inner._PostgreCommand;
import io.army.criteria.standard.SQLs;
import io.army.util.ArrayUtils;
import io.army.util._Assert;
import io.army.util._Exceptions;
import io.army.util._StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

final class PostgreSets extends CriteriaSupports.StatementMockSupport
        implements PostgreCommand,
        PostgreCommand._SetClause,
        _PostgreCommand._SetCommand,
        DmlCommand,
        Statement._AsCommandClause<DmlCommand> {


    static PostgreCommand._SetClause setStmt() {
        return new PostgreSets();
    }

    private _ParamValue pair;

    private Boolean prepared;

    private PostgreSets() {
        super(CriteriaContexts.otherPrimaryContext(PostgreUtils.DIALECT));
        ContextStack.push(this.context);
    }


    @Override
    public _AsCommandClause<DmlCommand> set(SQLs.VarScope scope, String name, SQLs.SymbolEqual equal, Object value) {
        if (this.pair != null) {
            throw ContextStack.clearStackAndCastCriteriaApi();
        }
        this.pair = new ParamValue(scope, name, equal, Collections.singletonList(value));
        return this;
    }

    @Override
    public _AsCommandClause<DmlCommand> set(SQLs.VarScope scope, String name, SQLs.SymbolEqual equal, Object value1, Object value2) {
        if (this.pair != null) {
            throw ContextStack.clearStackAndCastCriteriaApi();
        }
        this.pair = new ParamValue(scope, name, equal, ArrayUtils.of(value1, value2));
        return this;
    }

    @Override
    public _AsCommandClause<DmlCommand> set(SQLs.VarScope scope, String name, SQLs.SymbolEqual equal, Object value1, Object value2, Object value3) {
        if (this.pair != null) {
            throw ContextStack.clearStackAndCastCriteriaApi();
        }
        this.pair = new ParamValue(scope, name, equal, ArrayUtils.of(value1, value2, value3));
        return this;
    }

    @Override
    public _AsCommandClause<DmlCommand> set(SQLs.VarScope scope, String name, SQLs.SymbolEqual equal, Object value1, Object value2, Object value3, Object value4) {
        if (this.pair != null) {
            throw ContextStack.clearStackAndCastCriteriaApi();
        }
        this.pair = new ParamValue(scope, name, equal, ArrayUtils.of(value1, value2, value3, value4));
        return this;
    }

    @Override
    public _AsCommandClause<DmlCommand> set(SQLs.VarScope scope, String name, SQLs.WordTo to, Consumer<Consumer<Object>> consumer) {
        if (this.pair != null) {
            throw ContextStack.clearStackAndCastCriteriaApi();
        }
        final List<Object> valueList;
        valueList = ClauseUtils.invokingDynamicConsumer(true, true, consumer);
        this.pair = new ParamValue(scope, name, to, valueList);
        return this;
    }

    @Override
    public DmlCommand asCommand() {
        _Assert.nonPrepared(this.prepared);
        if (this.pair == null) {
            throw ContextStack.clearStackAndCastCriteriaApi();
        }
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
        this.pair = null;
        this.prepared = Boolean.FALSE;
    }


    @Override
    public _ParamValue paramValuePair() {
        final _ParamValue pair = this.pair;
        if (pair == null) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return pair;
    }

    private static final class ParamValue implements _ParamValue {

        private final SQLs.VarScope scope;

        private final String name;

        private final Object word;

        private final List<Object> valueList;

        private ParamValue(SQLs.VarScope scope, String name, Object word, List<Object> valueList) {
            if (scope != SQLs.SESSION && scope != SQLs.LOCAL) {
                throw CriteriaUtils.unknownWords(scope);
            } else if (!_StringUtils.hasText(name)) {
                throw ContextStack.clearStackAnd(_Exceptions::varNameNoText);
            } else if (word != SQLs.EQUAL && word != SQLs.TO) {
                throw CriteriaUtils.unknownWords(word);
            }
            this.scope = scope;
            this.name = name;
            this.word = word;
            this.valueList = valueList;
        }

        @Override
        public SQLs.VarScope scope() {
            return this.scope;
        }

        @Override
        public String name() {
            return this.name;
        }

        @Override
        public Object word() {
            return this.word;
        }

        @Override
        public List<Object> valueList() {
            return this.valueList;
        }


    } // ParamValue


}
