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

import io.army.criteria.Selection;
import io.army.criteria.impl.inner._DmlStatement;
import io.army.criteria.impl.inner._ReturningDml;
import io.army.criteria.impl.inner._SelectItem;
import io.army.session.SessionSpec;
import io.army.stmt.DmlStmtParams;
import io.army.stmt.Stmt;
import io.army.stmt.StmtType;
import io.army.stmt.Stmts;
import io.army.util._Exceptions;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 * This class is base class of below:
 *     <ul>
 *         <li>{@link SingleTableDmlContext}</li>
 *         <li>{@link MultiTableDmlContext}</li>
 *     </ul>
 * * @since 0.6.0
 */
abstract class NarrowDmlStmtContext extends BatchSpecStatementContext implements NarrowDmlContext, DmlStmtParams {


    final boolean versionPredicate;

    private final List<? extends _SelectItem> returningList;


    NarrowDmlStmtContext(@Nullable StatementContext parentOrOuterContext, _DmlStatement stmt,
                         ArmyParser parser, SessionSpec sessionSpec) {
        super(parentOrOuterContext, stmt, parser, sessionSpec);

        this.versionPredicate = _DialectUtils.hasOptimistic(stmt.wherePredicateList());

        if (stmt instanceof _ReturningDml) {
            this.returningList = ((_ReturningDml) stmt).returningList();
        } else {
            this.returningList = Collections.emptyList();
        }


    }



    @Override
    public final Stmt build() {
        if (this.accessor != null) {
            //now,multi-multi statement
            throw new UnsupportedOperationException();
        }
        final List<?> paramList = this.paramList;
        final Stmt stmt;
        if (paramList != null) {
            stmt = Stmts.batchDmlStmt(this, paramList);
        } else if (this.hasNamedParam()) {
            throw _Exceptions.namedParamInNonBatch();
        } else {
            stmt = Stmts.dmlStmt(this);
        }
        return stmt;
    }

    @Override
    public final boolean hasOptimistic() {
        return this.versionPredicate;
    }

    @Override
    public final StmtType stmtType() {
        return StmtType.UPDATE;
    }

    @Override
    public final int idSelectionIndex() {
        //TODO for firebird
        return -1;
    }

    @Override
    public final List<? extends Selection> selectionList() {
        final List<? extends _SelectItem> list = this.returningList;
        if (list == null) {
            throw new UnsupportedOperationException();
        }
        return _DialectUtils.flatSelectItem(list);
    }




}
