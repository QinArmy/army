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

import io.army.criteria.Selection;
import io.army.meta.FieldMeta;
import io.army.session.SessionSpec;
import io.army.stmt.MultiStmt;
import io.army.stmt.MultiStmtBatchStmt;
import io.army.stmt.StmtType;
import io.army.stmt.Stmts;
import io.army.util._Collections;

import javax.annotation.Nullable;
import java.util.List;

final class MultiStmtBatchContext extends StatementContext implements MultiStmtContext {

    static MultiStmtBatchContext create(ArmyParser parser, SessionSpec sessionSpec) {
        return new MultiStmtBatchContext(parser, sessionSpec);
    }


    private int batchSize = -1;

    private boolean optimistic;

    private List<? extends Selection> selectionList;

    private MultiStmt.StmtItem batchFirstItem;

    private int batchNumber;

    private MultiStmtBatchContext(ArmyParser parser, SessionSpec sessionSpec) {
        super(null, parser, sessionSpec);
    }

    @Override
    public boolean hasOptimistic() {
        return this.optimistic;
    }

    @Override
    public StmtType stmtType() {
        return StmtType.UPDATE;
    }

    @Override
    public void batchStmtStart(final int batchSize) {
        if (this.batchSize > 0) {
            // no bug,never here
            throw new IllegalStateException();
        } else if (batchSize < 1) {
            // no bug,never here
            throw new IllegalArgumentException();
        }
        this.batchSize = batchSize;
        this.batchNumber = 0;
    }

    @Override
    public void startChildItem() {
        //TODO for h2,firebird
        throw new UnsupportedOperationException();
    }

    @Override
    public void appendItemForChild() {
        //TODO for h2,firebird
        throw new UnsupportedOperationException();
    }

    @Override
    public void addBatchItem(final @Nullable MultiStmt.StmtItem item) {
        final int batchSize = this.batchSize;
        if (batchSize < 1) {
            // no bug,never here
            throw new IllegalStateException();
        } else if (this.batchNumber++ >= batchSize) {
            // no bug,never here
            throw new IllegalStateException();
        }

        final MultiStmt.StmtItem firstItem = this.batchFirstItem;
        if (firstItem == null) {
            if (item == null) {
                // no bug,never here
                throw new NullPointerException();
            }
            this.batchFirstItem = item;
        } else if (firstItem != item) {
            // no bug,never here
            throw new IllegalArgumentException();
        }

        this.sqlBuilder.append(_Constant.SPACE_SEMICOLON_TWO_LINE);

    }

    @Override
    public void endChildItem() {
        //TODO for h2,firebird
        throw new UnsupportedOperationException();
    }

    @Override
    public MultiStmtContext batchStmtEnd() {
        final int batchSize = this.batchSize;
        if (batchSize < 1) {
            // no bug,never here
            throw new IllegalStateException();
        }
        if (this.batchNumber != batchSize) {
            // no bug,never here
            throw new IllegalStateException("item count error");
        }
        final MultiStmt.StmtItem firstItem = this.batchFirstItem;
        assert firstItem != null;
        this.optimistic = firstItem.hasOptimistic();
        if (firstItem instanceof MultiStmt.QueryStmt) {
            this.selectionList = ((MultiStmt.QueryStmt) firstItem).selectionList();
        } else {
            this.selectionList = _Collections.emptyList();
        }
        return this;
    }

    @Override
    public MultiStmtBatchStmt build() {
        if (this.selectionList == null) {
            //no bug,never here
            throw new IllegalStateException();
        }
        return Stmts.multiStmtBatchStmt(this, this.batchSize);
    }


    @Override
    public List<? extends Selection> selectionList() {
        final List<? extends Selection> list = this.selectionList;
        if (list == null) {
            //no bug,never here
            throw new IllegalStateException();
        }
        return list;
    }

    @Override
    public void appendField(String tableAlias, FieldMeta<?> field) {
        // no bug,never here
        throw new UnsupportedOperationException();
    }

    @Override
    public void appendField(FieldMeta<?> field) {
        // no bug,never here
        throw new UnsupportedOperationException();
    }


}
