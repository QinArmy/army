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

import io.army.criteria.*;
import io.army.criteria.impl.inner.*;
import io.army.util._Assert;

import java.util.List;

@SuppressWarnings("unchecked")
abstract class BracketRowSet<I extends Item, RR, OR, OD, LR, LO, LF, SP>
        extends LimitRowOrderByClause<OR, OD, LR, LO, LF>
        implements _ParensRowSet,
        RowSet._StaticUnionClause<SP>,
        RowSet._StaticIntersectClause<SP>,
        RowSet._StaticExceptClause<SP>,
        RowSet._StaticMinusClause<SP>,
        _SelectionMap,
        Query._AsQueryClause<I>,
        Statement._RightParenClause<RR>,
        _RowSet._SelectItemListSpec,
        _Statement._WithClauseSpec,
        RowSet {


    private final boolean recursive;

    private final List<_Cte> cteList;

    private _RowSet innerRowSet;

    private Boolean prepared;

    BracketRowSet(ArmyStmtSpec spec) {
        super(CriteriaContexts.bracketContext(spec)); //must migrate WITH clause and context when create bracket.
        this.recursive = spec.isRecursive();
        this.cteList = spec.cteList();
        ContextStack.push(this.context);
    }


    @Override
    public final RR rightParen() {
        return (RR) this;
    }

    @Override
    public final SP union() {
        return this.unionQuery(_UnionType.UNION);
    }

    @Override
    public final SP unionAll() {
        return this.unionQuery(_UnionType.UNION_ALL);
    }

    @Override
    public final SP unionDistinct() {
        return this.unionQuery(_UnionType.UNION_DISTINCT);
    }

    @Override
    public final SP intersect() {
        return this.unionQuery(_UnionType.INTERSECT);
    }

    @Override
    public final SP intersectAll() {
        return this.unionQuery(_UnionType.INTERSECT_ALL);
    }

    @Override
    public final SP intersectDistinct() {
        return this.unionQuery(_UnionType.INTERSECT_DISTINCT);
    }

    @Override
    public final SP except() {
        return this.unionQuery(_UnionType.EXCEPT);
    }

    @Override
    public final SP exceptAll() {
        return this.unionQuery(_UnionType.EXCEPT_ALL);
    }

    @Override
    public final SP exceptDistinct() {
        return this.unionQuery(_UnionType.EXCEPT_DISTINCT);
    }

    @Override
    public final SP minus() {
        return this.unionQuery(_UnionType.MINUS);
    }

    @Override
    public final SP minusAll() {
        return this.unionQuery(_UnionType.MINUS_ALL);
    }

    @Override
    public final SP minusDistinct() {
        return this.unionQuery(_UnionType.MINUS_DISTINCT);
    }

    @Override
    public final I asQuery() {
        this.endQueryStatement();
        return this.onAsQuery();
    }


    @Override
    public final _RowSet innerRowSet() {
        final _RowSet rowSet = this.innerRowSet;
        if (rowSet == null) {
            throw ContextStack.clearStackAndCastCriteriaApi();
        }
        return rowSet;
    }


    @Override
    public final boolean isRecursive() {
        return this.recursive;
    }

    @Override
    public final List<_Cte> cteList() {
        return this.cteList;
    }

    @Override
    public final List<? extends _SelectItem> selectItemList() {
        final _RowSet rowSet = this.innerRowSet;
        if (rowSet == null || !(this instanceof _PrimaryRowSet)) {
            throw ContextStack.clearStackAndCastCriteriaApi();
        }
        return ((_PrimaryRowSet) rowSet).selectItemList();
    }

    @Override
    public final int selectionSize() {
        final _RowSet rowSet = this.innerRowSet;
        if (rowSet == null) {
            throw ContextStack.clearStackAndCastCriteriaApi();
        }
        return rowSet.selectionSize();
    }

    @Override
    public final Selection refSelection(final String derivedAlias) {
        final RowSet rowSet = this.innerRowSet;
        if (!(rowSet instanceof DerivedTable)) {
            throw ContextStack.clearStackAndCastCriteriaApi();
        }
        return ((_DerivedTable) rowSet).refSelection(derivedAlias);
    }

    @Override
    public final List<? extends Selection> refAllSelection() {
        final RowSet rowSet = this.innerRowSet;
        if (!(rowSet instanceof DerivedTable)) {
            throw ContextStack.clearStackAndCastCriteriaApi();
        }
        return ((_DerivedTable) rowSet).refAllSelection();
    }

    @Override
    public final void clear() {
        _Assert.prepared(this.prepared);
        this.innerRowSet = null;
        this.clearOrderByList();
        this.prepared = Boolean.FALSE;
    }


    @Override
    public final void prepared() {
        _Assert.prepared(this.prepared);
    }

    @Override
    public final boolean isPrepared() {
        final Boolean prepared = this.prepared;
        return prepared != null && prepared;
    }

    final RR parensEnd(final RowSet parenRowSet) {
        if (this.innerRowSet != null) {
            throw ContextStack.clearStackAndCastCriteriaApi();
        }
        this.innerRowSet = (_RowSet) parenRowSet;
        this.context.onSetInnerContext(((CriteriaContextSpec) parenRowSet).getContext());
        return (RR) this;
    }


    void onEndQuery() {
        //no-op
    }


    abstract I onAsQuery();


    abstract SP createUnionRowSet(_UnionType unionType);


    private SP unionQuery(final _UnionType unionType) {
        this.endQueryStatement();
        return this.createUnionRowSet(unionType);
    }

    private void endQueryStatement() {
        _Assert.nonPrepared(this.prepared);
        final CriteriaContext context = this.context;
        if (this.innerRowSet == null) {
            throw ContextStack.clearStackAndCastCriteriaApi();
        }
        this.endOrderByClauseIfNeed();
        this.onEndQuery();
        ContextStack.pop(context);
        this.prepared = Boolean.TRUE;
    }


    static abstract class ArmyBatchBracketSelect extends CriteriaSupports.StatementMockSupport
            implements ArmyBatchSelect, _ParensRowSet {

        private final boolean recursive;

        private final List<_Cte> cteList;

        private final _PrimaryRowSet innerRowSet;

        private final List<? extends SortItem> orderByList;

        private final _Expression rowCountExpression;

        private final _Expression offsetExpression;

        private final List<?> paramList;

        private boolean prepared = true;

        ArmyBatchBracketSelect(BracketRowSet<?, ?, ?, ?, ?, ?, ?, ?> rowSet, List<?> paramList) {
            super(rowSet.context);

            this.recursive = rowSet.recursive;
            this.cteList = rowSet.cteList;
            this.innerRowSet = (_PrimaryRowSet) rowSet.innerRowSet;
            this.orderByList = rowSet.orderByList();

            this.rowCountExpression = rowSet.rowCountExp();
            this.offsetExpression = rowSet.offsetExp();
            this.paramList = paramList;
        }

        @Override
        public final boolean isRecursive() {
            return this.recursive;
        }

        @Override
        public final List<_Cte> cteList() {
            return this.cteList;
        }

        @Override
        public final int selectionSize() {
            return this.innerRowSet.selectionSize();
        }

        @Override
        public final List<? extends _SelectItem> selectItemList() {
            return this.innerRowSet.selectItemList();
        }

        @Override
        public final _RowSet innerRowSet() {
            return this.innerRowSet;
        }

        @Override
        public final List<? extends SortItem> orderByList() {
            return this.orderByList;
        }

        @Override
        public final _Expression rowCountExp() {
            return this.rowCountExpression;
        }

        @Override
        public final _Expression offsetExp() {
            return this.offsetExpression;
        }

        @Override
        public final List<?> paramList() {
            return this.paramList;
        }


        @Override
        public final void prepared() {
            _Assert.prepared(this.prepared);
        }

        @Override
        public final boolean isPrepared() {
            return this.prepared;
        }


        @Override
        public final void clear() {
            if (!this.prepared) {
                return;
            }
            this.prepared = false;
        }


    }//ArmyBatchBracketSelect


}
