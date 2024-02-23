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

import io.army.criteria.Statement;
import io.army.criteria.SubQuery;
import io.army.criteria.dialect.DmlCommand;
import io.army.criteria.impl.inner.postgre._PostgreDeclareCursor;
import io.army.criteria.postgre.PostgreCursor;
import io.army.criteria.postgre.PostgreQuery;
import io.army.util._Assert;
import io.army.util._Exceptions;
import io.army.util._StringUtils;

import javax.annotation.Nullable;
import java.util.function.BooleanSupplier;

final class PostgreDeclareCursors extends CriteriaSupports.StatementMockSupport implements PostgreCursor,
        PostgreCursor._PostgreDeclareClause,
        PostgreCursor._BinarySpec,
        PostgreCursor._CursorClause,
        PostgreCursor._HoldSpec,
        DmlCommand,
        Statement._AsCommandClause<DmlCommand>,
        _PostgreDeclareCursor {


    static PostgreCursor._PostgreDeclareClause declare() {
        return new PostgreDeclareCursors();
    }


    private String cursorName;

    private boolean binary;

    private Boolean sensitive;

    private Boolean scroll;

    private Boolean hold;

    private SubQuery query;

    private Boolean prepared;

    private PostgreDeclareCursors() {
        super(CriteriaContexts.otherPrimaryContext(PostgreUtils.DIALECT));
        ContextStack.push(this.context);
    }


    @Override
    public PostgreCursor._BinarySpec declare(final String name) {
        if (!_StringUtils.hasText(name)) {
            throw ContextStack.clearStackAnd(_Exceptions::cursorNameNoText);
        } else if (this.cursorName != null) {
            throw ContextStack.clearStackAndCastCriteriaApi();
        }
        this.cursorName = name;
        return this;
    }


    @Override
    public PostgreCursor._InsensitiveSpec binary() {
        this.binary = true;
        return this;
    }

    @Override
    public PostgreCursor._ScrollSpec insensitive() {
        this.sensitive = Boolean.FALSE;
        return this;
    }

    @Override
    public PostgreCursor._ScrollSpec asensitive() {
        this.sensitive = Boolean.TRUE;
        return this;
    }

    @Override
    public PostgreCursor._ScrollSpec ifInsensitive(BooleanSupplier supplier) {
        if (ClauseUtils.invokeBooleanSupplier(supplier)) {
            this.sensitive = Boolean.FALSE;
        } else {
            this.sensitive = null;
        }
        return this;
    }

    @Override
    public PostgreCursor._ScrollSpec ifAsensitive(BooleanSupplier supplier) {
        if (ClauseUtils.invokeBooleanSupplier(supplier)) {
            this.sensitive = Boolean.TRUE;
        } else {
            this.sensitive = null;
        }
        return this;
    }


    @Override
    public PostgreCursor._CursorClause scroll() {
        this.scroll = Boolean.TRUE;
        return this;
    }

    @Override
    public PostgreCursor._CursorClause noScroll() {
        this.scroll = Boolean.FALSE;
        return this;
    }

    @Override
    public PostgreCursor._CursorClause ifScroll(BooleanSupplier supplier) {
        if (ClauseUtils.invokeBooleanSupplier(supplier)) {
            this.scroll = Boolean.TRUE;
        } else {
            this.scroll = null;
        }
        return this;
    }

    @Override
    public PostgreCursor._CursorClause ifNoScroll(BooleanSupplier supplier) {
        if (ClauseUtils.invokeBooleanSupplier(supplier)) {
            this.scroll = Boolean.FALSE;
        } else {
            this.scroll = null;
        }
        return this;
    }


    @Override
    public PostgreCursor._HoldSpec cursor() {
        return this;
    }

    @Override
    public PostgreCursor._ForQueryClause withHold() {
        this.hold = Boolean.TRUE;
        return this;
    }

    @Override
    public PostgreCursor._ForQueryClause withoutHold() {
        this.hold = Boolean.FALSE;
        return this;
    }

    @Override
    public PostgreCursor._ForQueryClause ifWithHold(BooleanSupplier supplier) {
        if (ClauseUtils.invokeBooleanSupplier(supplier)) {
            this.hold = Boolean.TRUE;
        } else {
            this.hold = null;
        }
        return this;
    }

    @Override
    public PostgreCursor._ForQueryClause ifWithoutHold(BooleanSupplier supplier) {
        if (ClauseUtils.invokeBooleanSupplier(supplier)) {
            this.hold = Boolean.FALSE;
        } else {
            this.hold = null;
        }
        return this;
    }


    @Override
    public PostgreQuery.WithSpec<_AsCommandClause<DmlCommand>> forSpace() {
        return PostgreQueries.subQuery(this.context, this::forSpace);
    }

    @Override
    public _AsCommandClause<DmlCommand> forSpace(final @Nullable SubQuery query) {
        if (query == null) {
            throw ContextStack.clearStackAndNullPointer();
        } else if (this.query != null) {
            throw ContextStack.clearStackAndCastCriteriaApi();
        }
        this.query = query;
        return this;
    }

    @Override
    public DmlCommand asCommand() {
        _Assert.nonPrepared(this.prepared);
        if (this.cursorName == null || this.query == null) {
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
        this.cursorName = null;
        this.query = null;
        this.prepared = Boolean.FALSE;
    }

    @Override
    public String cursorName() {
        return this.cursorName;
    }

    @Override
    public boolean isBinary() {
        return this.binary;
    }

    @Nullable
    @Override
    public Boolean sensitiveMode() {
        return this.sensitive;
    }

    @Nullable
    @Override
    public Boolean scrollMode() {
        return this.scroll;
    }

    @Nullable
    @Override
    public Boolean holdMode() {
        return this.hold;
    }

    @Override
    public SubQuery forQuery() {
        return this.query;
    }



    /*-------------------below private methods -------------------*/


}
