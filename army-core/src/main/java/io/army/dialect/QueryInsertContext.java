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

import io.army.criteria.SubQuery;
import io.army.criteria.Visible;
import io.army.criteria.impl.inner._Insert;
import io.army.criteria.impl.inner._RowSet;
import io.army.meta.ChildTableMeta;
import io.army.meta.FieldMeta;
import io.army.session.SessionSpec;

import io.army.lang.Nullable;
import java.util.List;
import java.util.function.ObjIntConsumer;

final class QueryInsertContext extends InsertContext implements _QueryInsertContext {

    static QueryInsertContext forSingle(@Nullable _SqlContext outerContext, _Insert._QueryInsert stmt,
                                        ArmyParser dialect, SessionSpec sessionSpec) {
        return new QueryInsertContext((StatementContext) outerContext, stmt, dialect, sessionSpec);
    }

    static QueryInsertContext forParent(@Nullable _SqlContext outerContext, _Insert._ChildQueryInsert domainStmt,
                                        ArmyParser dialect, SessionSpec sessionSpec) {
        assert outerContext == null || outerContext instanceof MultiStmtContext;
        return new QueryInsertContext((StatementContext) outerContext, domainStmt, dialect, sessionSpec);
    }

    static QueryInsertContext forChild(@Nullable _SqlContext outerContext, _Insert._ChildQueryInsert domainStmt,
                                       QueryInsertContext parentContext) {
        return new QueryInsertContext((StatementContext) outerContext, domainStmt, parentContext);
    }


    private final SubQuery subQuery;

    private final int subQuerySelectionSize;

    /**
     * <p>
     * For {@link  io.army.meta.SingleTableMeta}
     *
     *
     * @see #forSingle(_SqlContext, _Insert._QueryInsert, ArmyParser, Visible)
     * @see #forParent(_SqlContext, _Insert._ChildQueryInsert, ArmyParser, Visible)
     */
    private QueryInsertContext(@Nullable StatementContext outerContext, _Insert._QueryInsert domainStmt,
                               ArmyParser parser, SessionSpec sessionSpec) {
        super(outerContext, domainStmt, parser, sessionSpec);


        final _Insert._QueryInsert targetStmt;
        if (domainStmt instanceof _Insert._ChildQueryInsert) {
            targetStmt = ((_Insert._ChildQueryInsert) domainStmt).parentStmt();
        } else {
            targetStmt = domainStmt;
        }
        this.subQuery = targetStmt.subQuery();
        this.subQuerySelectionSize = ((_RowSet) this.subQuery).selectionSize();
        assert this.fieldList.size() == this.subQuerySelectionSize;

    }

    /**
     * <p>
     * For {@link  io.army.meta.ChildTableMeta}
     *
     *
     * @see #forChild(_SqlContext, _Insert._ChildQueryInsert, QueryInsertContext)
     */
    private QueryInsertContext(@Nullable StatementContext outerContext, _Insert._ChildQueryInsert domainStmt
            , QueryInsertContext parentContext) {
        super(outerContext, domainStmt, parentContext);

        this.subQuery = domainStmt.subQuery();

        assert this.insertTable instanceof ChildTableMeta
                && parentContext.insertTable == ((ChildTableMeta<?>) this.insertTable).parentMeta()
                && this.fieldList != parentContext.fieldList
                && this.subQuery != parentContext.subQuery;

        this.subQuerySelectionSize = ((_RowSet) this.subQuery).selectionSize();
        assert this.fieldList.size() == this.subQuerySelectionSize;
    }


    @Override
    public int rowSize() {
        //no bug , never here
        throw new UnsupportedOperationException();
    }

    @Override
    public ObjIntConsumer<Object> idConsumer() {
        //no bug , never here
        throw new UnsupportedOperationException();
    }

    @Override
    int doAppendSubQuery(final int outputColumnSize, final List<FieldMeta<?>> fieldList) {

        assert outputColumnSize == this.subQuerySelectionSize;
        this.parser.handleQuery(this.subQuery, this);
        return this.subQuerySelectionSize;
    }




}
