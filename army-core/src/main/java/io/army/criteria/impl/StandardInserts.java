package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.impl.inner._Insert;
import io.army.criteria.impl.inner._ValueInsert;
import io.army.dialect.Dialect;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.*;
import io.army.util._Assert;
import io.army.util._Exceptions;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * <p>
 * This class representing standard value insert statement.
 * </p>
 *
 * @since 1.0
 */
abstract class StandardInserts extends InsertSupport {


    private StandardInserts() {
        throw new UnsupportedOperationException();
    }

    static <C> Insert._StandardDomainOptionSpec<C> domainInsert(@Nullable C criteria) {
        return new StandardDomainOptionClause<>(criteria);
    }

    static <C> Insert._StandardValueOptionSpec<C> valueInsert(@Nullable C criteria) {
        return new StandardValueInsertOptionClause<>(criteria);
    }

    static <C> Insert._StandardSubQueryInsertClause<C> rowSetInsert(@Nullable C criteria) {
        return new StandardSubQueryInsertIntoClause<C>(criteria);
    }


    /*-------------------below standard domain insert syntax class-------------------*/
    private static final class StandardDomainOptionClause<C> implements Insert._StandardDomainOptionSpec<C>, InsertOptions {

        final CriteriaContext criteriaContext;

        private boolean preferLiteral;

        private boolean migration;

        private NullHandleMode nullHandleMode = NullHandleMode.INSERT_DEFAULT;

        private StandardDomainOptionClause(@Nullable C criteria) {
            this.criteriaContext = CriteriaContexts.primaryInsertContext(criteria);
            CriteriaContextStack.setContextStack(this.criteriaContext);
        }

        @Override
        public Insert._StandardDomainInsertIntoClause<C> preferLiteral(boolean prefer) {
            this.preferLiteral = prefer;
            return this;
        }

        @Override
        public Insert._StandardPreferLiteralSpec<C> migration(final boolean migration) {
            this.migration = migration;
            if (migration) {
                this.nullHandleMode = NullHandleMode.INSERT_NULL;
            }
            return this;
        }

        @Override
        public Insert._StandardPreferLiteralSpec<C> nullHandle(NullHandleMode mode) {
            CriteriaContextStack.assertNonNull(mode);
            this.nullHandleMode = mode;
            return this;
        }

        @Override
        public <T extends IDomain> Insert._StandardDomainColumnsSpec<C, T, FieldMeta<T>> insertInto(SingleTableMeta<T> table) {
            return new StandardDomainInsertStatement<>(this.criteriaContext, this, table);
        }

        @Override
        public <T extends IDomain> Insert._StandardDomainColumnsSpec<C, T, FieldMeta<? super T>> insertInto(ChildTableMeta<T> table) {
            return new StandardDomainInsertStatement<>(this.criteriaContext, this, table);
        }

        @Override
        public boolean isMigration() {
            return this.migration;
        }

        @Override
        public NullHandleMode nullHandle() {
            final NullHandleMode mode = this.nullHandleMode;
            assert mode != null;
            return mode;
        }

        @Override
        public boolean isPreferLiteral() {
            return this.preferLiteral;
        }


    }//StandardDomainOptionClause


    static final class StandardDomainInsertStatement<C, T extends IDomain, F extends TableField>
            extends DomainValueClause<C, T, F, Insert._StandardDomainCommonExpSpec<C, T, F>, Insert._InsertSpec>
            implements Insert._StandardDomainColumnsSpec<C, T, F>, StandardStatement, Insert._InsertSpec {


        private Boolean prepared;

        private StandardDomainInsertStatement(CriteriaContext criteriaContext, InsertOptions options
                , TableMeta<T> table) {
            super(criteriaContext, options, table);
        }

        @Override
        public Insert asInsert() {
            _Assert.nonPrepared(this.prepared);
            CriteriaContextStack.clearContextStack(this.criteriaContext);
            this.unmodifiedCommonExpMap();
            this.prepared = Boolean.TRUE;
            return this;
        }

        @Override
        public void prepared() {
            _Assert.prepared(this.prepared);
        }

        @Override
        public boolean isPrepared() {
            return this.prepared;
        }


        @Override
        _StandardDomainCommonExpSpec<C, T, F> columnListEnd(int fieldSize, int childFieldSize) {
            return this;
        }

        @Override
        public void clear() {
            _Assert.prepared(this.prepared);
            this.prepared = Boolean.FALSE;
            super.clear();
        }

        @Override
        public String toString() {
            final String s;
            if (this.isPrepared()) {
                s = this.mockAsString(Dialect.MySQL57, Visible.ONLY_VISIBLE, true);
            } else {
                s = super.toString();
            }
            return s;
        }


    }//StandardDomainInsertStatement


    /*-------------------below standard value insert syntax class-------------------*/

    private static final class StandardValueInsertOptionClause<C> implements Insert._StandardValueOptionSpec<C>, InsertOptions {

        private final CriteriaContext criteriaContext;

        private boolean migration;

        private NullHandleMode nullHandleMode = NullHandleMode.INSERT_DEFAULT;

        public StandardValueInsertOptionClause(@Nullable C criteria) {
            this.criteriaContext = CriteriaContexts.primaryInsertContext(criteria);
            CriteriaContextStack.setContextStack(this.criteriaContext);
        }

        @Override
        public Insert._StandardValueInsertIntoClause<C> migration(boolean migration) {
            this.migration = migration;
            this.nullHandleMode = NullHandleMode.INSERT_NULL;
            return this;
        }


        @Override
        public Insert._StandardValueInsertIntoClause<C> nullHandle(NullHandleMode mode) {
            CriteriaContextStack.assertNonNull(mode);
            this.nullHandleMode = mode;
            return this;
        }
        @Override
        public <T extends IDomain> Insert._StandardColumnsSpec<C, T, FieldMeta<T>> insertInto(SingleTableMeta<T> table) {
            return new StandardValueClause<>(this.criteriaContext, this, table);
        }

        @Override
        public <T extends IDomain> Insert._StandardColumnsSpec<C, T, FieldMeta<? super T>> insertInto(ChildTableMeta<T> table) {
            return new StandardValueClause<>(this.criteriaContext, this, table);
        }

        @Override
        public boolean isMigration() {
            return this.migration;
        }

        @Override
        public NullHandleMode nullHandle() {
            return this.nullHandleMode;
        }

        @Override
        public boolean isPreferLiteral() {
            //non-domain insert,always false
            return false;
        }


    }//StandardValueInsertOptionClause

    /**
     * @param <C>
     * @param <T>
     * @param <F>
     */
    private static final class StandardValueClause<C, T extends IDomain, F extends TableField>
            extends ValueInsertValueClause<
            C,
            F,
            Insert._StandardCommonExpSpec<C, T, F>,
            Insert._StandardCommonExpSpec<C, T, F>,
            Insert._InsertSpec>
            implements Insert._StandardColumnsSpec<C, T, F> {


        private StandardValueClause(CriteriaContext criteriaContext, InsertOptions options, TableMeta<?> table) {
            super(criteriaContext, options, table);
        }

        @Override
        public _StandardStaticValueLeftParenClause<C, F> value() {
            return new StandardStaticValueClause<>(this);
        }

        @Override
        public _StandardStaticValuesLeftParenClause<C, F> values() {
            return new StandardStaticValuesPairClause<>(this);
        }


        @Override
        public void prepared() {
            //here,don't use CriteriaContextStack.criteriaError() method,because this is invoked by _Dialect
            throw new UnsupportedOperationException();
        }


        @Override
        public boolean isPrepared() {
            //here,don't use CriteriaContextStack.criteriaError() method,because this is invoked by _Dialect
            throw new UnsupportedOperationException();
        }

        @Override
        _StandardCommonExpSpec<C, T, F> columnListEnd(int fieldSize, int childFieldSize) {
            return this;
        }

        @Override
        _InsertSpec valueClauseEnd(List<Map<FieldMeta<?>, _Expression>> valuePairList) {
            return new StandardValueInsertStatement(this, valuePairList);
        }


    }//StandardValueInsert

    private static final class StandardStaticValueClause<C, F extends TableField>
            extends StaticColumnValuePairClause<C, F, Insert._InsertSpec>
            implements Insert._StandardStaticValueLeftParenClause<C, F> {

        final StandardValueClause<?, ?, ?> clause;

        final Map<FieldMeta<?>, _Expression> valuePairMap = new HashMap<>();

        private StandardStaticValueClause(StandardValueClause<C, ?, F> clause) {
            super(clause.criteriaContext);
            this.clause = clause;
        }


        @Override
        public Insert._InsertSpec rightParen() {
            return new StandardValueInsertStatement(this.clause, this.valuePairMap);
        }

        @Override
        void addValuePair(final FieldMeta<?> field, final _Expression value) {
            if (!this.clause.containField(field)) {
                throw notContainField(field);
            }
            if (this.valuePairMap.putIfAbsent(field, value) != null) {
                throw duplicationValuePair(field);
            }
        }


    }//StandardStaticValueClause


    private static final class StandardStaticValuesPairClause<C, F extends TableField>
            extends StaticColumnValuePairClause<C, F, Insert._StandardStaticValuesLeftParenSpec<C, F>>
            implements Insert._StandardStaticValuesLeftParenSpec<C, F> {

        final StandardValueClause<?, ?, ?> clause;

        private List<Map<FieldMeta<?>, _Expression>> valuePairList;

        private Map<FieldMeta<?>, _Expression> valuePairMap;

        private StandardStaticValuesPairClause(StandardValueClause<C, ?, F> clause) {
            super(clause.criteriaContext);
            this.clause = clause;
        }

        @Override
        public Insert asInsert() {
            List<Map<FieldMeta<?>, _Expression>> valuePairList = this.valuePairList;
            if (this.valuePairMap != null || !(valuePairList instanceof ArrayList)) {
                throw CriteriaContextStack.criteriaError(_Exceptions::castCriteriaApi);
            }
            switch (valuePairList.size()) {
                case 0:
                    throw CriteriaContextStack.criteriaError(_Exceptions::castCriteriaApi);
                case 1:
                    valuePairList = Collections.singletonList(valuePairList.get(0));
                    break;
                default:
                    valuePairList = Collections.unmodifiableList(valuePairList);
            }
            this.valuePairList = valuePairList;
            return new StandardValueInsertStatement(this.clause, valuePairList).asInsert();
        }

        @Override
        public Insert._StandardStaticValuesLeftParenSpec<C, F> rightParen() {
            final Map<FieldMeta<?>, _Expression> currentPairMap = this.valuePairMap;
            if (!(currentPairMap instanceof HashMap)) {
                throw CriteriaContextStack.criteriaError(_Exceptions::castCriteriaApi);
            }
            List<Map<FieldMeta<?>, _Expression>> valuePairList = this.valuePairList;
            if (valuePairList == null) {
                valuePairList = new ArrayList<>();
                this.valuePairList = valuePairList;
            }
            valuePairList.add(Collections.unmodifiableMap(currentPairMap));
            this.valuePairMap = null;
            return this;
        }

        @Override
        void addValuePair(final FieldMeta<?> field, final _Expression value) {
            Map<FieldMeta<?>, _Expression> currentPairMap = this.valuePairMap;
            if (currentPairMap == null) {
                currentPairMap = new HashMap<>();
                this.valuePairMap = currentPairMap;
            } else if (!(currentPairMap instanceof HashMap)) {
                throw CriteriaContextStack.criteriaError(_Exceptions::castCriteriaApi);
            }
            if (!this.clause.containField(field)) {
                throw notContainField(field);
            }
            if (currentPairMap.putIfAbsent(field, value) != null) {
                throw duplicationValuePair(field);
            }

        }


    }//StandardStaticValuesClause


    static final class StandardValueInsertStatement extends ValueSyntaxStatement
            implements Insert._InsertSpec, StandardStatement, _ValueInsert {


        private final CriteriaContext criteriaContext;
        private final List<Map<FieldMeta<?>, _Expression>> valuePairList;
        private Boolean prepared;


        /**
         * @param valuePairMap a modified map
         * @see StandardStaticValueClause#rightParen()
         */
        private StandardValueInsertStatement(StandardValueClause<?, ?, ?> clause, Map<FieldMeta<?>
                , _Expression> valuePairMap) {
            super(clause);
            this.criteriaContext = clause.criteriaContext;
            this.valuePairList = Collections.singletonList(Collections.unmodifiableMap(valuePairMap));
        }

        /**
         * @param valuePairList a unmodified list
         * @see StandardValueClause#valueClauseEnd(List)
         * @see StandardStaticValuesPairClause#asInsert()
         */
        private StandardValueInsertStatement(StandardValueClause<?, ?, ?> clause
                , List<Map<FieldMeta<?>, _Expression>> valuePairList) {
            super(clause);
            this.criteriaContext = clause.criteriaContext;
            this.valuePairList = valuePairList;
        }

        @Override
        public Insert asInsert() {
            _Assert.nonPrepared(this.prepared);
            CriteriaContextStack.setContextStack(this.criteriaContext);
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
            this.prepared = Boolean.FALSE;
        }

        @Override
        public List<Map<FieldMeta<?>, _Expression>> rowValuesList() {
            return this.valuePairList;
        }

        @Override
        public String toString() {
            final String s;
            if (this.isPrepared()) {
                s = this.mockAsString(Dialect.MySQL57, Visible.ONLY_VISIBLE, true);
            } else {
                s = super.toString();
            }
            return s;
        }


    }//StandardValueInsert


    /**
     * @see #rowSetInsert(Object)
     */
    private static final class StandardSubQueryInsertIntoClause<C> implements Insert._StandardSubQueryInsertClause<C> {

        private final CriteriaContext criteriaContext;

        private StandardSubQueryInsertIntoClause(@Nullable C criteria) {
            this.criteriaContext = CriteriaContexts.primaryInsertContext(criteria);
            CriteriaContextStack.setContextStack(this.criteriaContext);
        }

        @Override
        public <T extends IDomain> Insert._StandardSingleColumnsClause<C, FieldMeta<T>> insertInto(SingleTableMeta<T> table) {
            return new StandardSingleColumnsClause<>(this.criteriaContext, table);
        }
        @Override
        public <P extends IDomain, T extends IDomain> Insert._StandardParentColumnsClause<C, FieldMeta<P>, FieldMeta<T>> insertInto(ComplexTableMeta<P, T> table) {
            CriteriaContextStack.assertNonNull(table);
            return new StandardParentColumnClause<>(this.criteriaContext, table);
        }


    }//StandardSubQueryInsertIntoClause

    /**
     * @see StandardSubQueryInsertIntoClause#insertInto(SingleTableMeta)
     */
    private static final class StandardSingleColumnsClause<C, F extends TableField>
            extends ColumnsClause<C, F, Insert._StandardSpaceSubQueryClause<C>>
            implements Insert._StandardSingleColumnsClause<C, F>, Insert._StandardSpaceSubQueryClause<C> {

        private StandardSingleColumnsClause(CriteriaContext criteriaContext, SingleTableMeta<?> table) {
            super(criteriaContext, true, table);
        }


        @Override
        public Insert._InsertSpec space(Supplier<? extends SubQuery> supplier) {
            return new StandardRowSetInsertStatement(this, supplier.get());
        }

        @Override
        public Insert._InsertSpec space(Function<C, ? extends SubQuery> function) {
            final SubQuery subQuery;
            subQuery = function.apply(this.criteriaContext.criteria());
            return new StandardRowSetInsertStatement(this, subQuery);
        }

        @Override
        public void prepared() {
            //here,don't use CriteriaContextStack.criteriaError() method,because this is invoked by _Dialect
            throw new UnsupportedOperationException();
        }
        @Override
        public boolean isPrepared() {
            //here,don't use CriteriaContextStack.criteriaError() method,because this is invoked by _Dialect
            throw new UnsupportedOperationException();
        }
        @Override
        _StandardSpaceSubQueryClause<C> columnListEnd(int fieldSize, int childFieldSize) {
            if (fieldSize == 0 || childFieldSize > 0) {
                throw CriteriaContextStack.criteriaError(_Exceptions::castCriteriaApi);
            }
            return this;
        }


    }//StandardSingleColumnsClause


    private static final class StandardParentColumnClause<C, PF extends TableField, TF extends TableField>
            extends ColumnsClause<C, PF, Insert._StandardParentSubQueryClause<C, TF>>
            implements Insert._StandardParentColumnsClause<C, PF, TF>
            , Insert._StandardParentSubQueryClause<C, TF> {

        private final ChildTableMeta<?> childTable;
        private StandardParentColumnClause(CriteriaContext criteriaContext, ChildTableMeta<?> table) {
            super(criteriaContext, true, table.parentMeta());
            this.childTable = table;
        }

        @Override
        public _StandardSingleColumnsClause<C, TF> space(Supplier<? extends SubQuery> supplier) {
            return new StandardChildColumnClause<>(this, supplier.get());
        }
        @Override
        public _StandardSingleColumnsClause<C, TF> space(Function<C, ? extends SubQuery> function) {
            return new StandardChildColumnClause<>(this, function.apply(this.criteria));
        }
        @Override
        public void prepared() {
            //here,don't use CriteriaContextStack.criteriaError() method,because this is invoked by _Dialect
            throw new UnsupportedOperationException();
        }
        @Override
        public boolean isPrepared() {
            //here,don't use CriteriaContextStack.criteriaError() method,because this is invoked by _Dialect
            throw new UnsupportedOperationException();
        }
        @Override
        _StandardParentSubQueryClause<C, TF> columnListEnd(int fieldSize, int childFieldSize) {
            if (fieldSize == 0 || childFieldSize > 0) {
                throw CriteriaContextStack.criteriaError(_Exceptions::castCriteriaApi);
            }
            return this;
        }
    }//StandardParentColumnClause


    private static final class StandardChildColumnClause<C, F extends TableField>
            extends ColumnsClause<C, F, Insert._StandardSpaceSubQueryClause<C>>
            implements Insert._StandardSpaceSubQueryClause<C>, Insert._StandardSingleColumnsClause<C, F> {
        private final _Insert parentClause;

        private final RowSet parentRowSet;

        private StandardChildColumnClause(StandardParentColumnClause<?, ?, ?> clause, RowSet parentRowSet) {
            super(clause.criteriaContext, true, clause.childTable);
            this.parentClause = clause;
            this.parentRowSet = parentRowSet;
        }

        @Override
        public _InsertSpec space(Supplier<? extends SubQuery> supplier) {
            return new StandardRowSetInsertStatement(this, supplier.get());
        }
        @Override
        public _InsertSpec space(Function<C, ? extends SubQuery> function) {
            return new StandardRowSetInsertStatement(this, function.apply(this.criteria));
        }
        @Override
        public void prepared() {
            //here,don't use CriteriaContextStack.criteriaError() method,because this is invoked by _Dialect
            throw new UnsupportedOperationException();
        }
        @Override
        public boolean isPrepared() {
            //here,don't use CriteriaContextStack.criteriaError() method,because this is invoked by _Dialect
            throw new UnsupportedOperationException();
        }
        @Override
        _StandardSpaceSubQueryClause<C> columnListEnd(int fieldSize, int childFieldSize) {
            if (fieldSize > 0 || childFieldSize == 0) {
                throw CriteriaContextStack.criteriaError(_Exceptions::castCriteriaApi);
            }
            return this;
        }

    }//StandardChildColumnClause


    static final class StandardRowSetInsertStatement extends RowSetInsertStatement implements StandardStatement {

        private final CriteriaContext criteriaContext;
        private Boolean prepared;


        private StandardRowSetInsertStatement(StandardSingleColumnsClause<?, ?> clause, RowSet rowSet) {
            super(clause, rowSet);
            this.criteriaContext = clause.criteriaContext;
        }

        private StandardRowSetInsertStatement(StandardChildColumnClause<?, ?> clause, RowSet childRowSet) {
            super(clause.parentClause, clause.parentRowSet, clause, childRowSet);
            this.criteriaContext = clause.criteriaContext;
        }


        @Override
        public Insert asInsert() {
            _Assert.nonPrepared(this.prepared);
            CriteriaContextStack.clearContextStack(this.criteriaContext);
            this.validateStatement();
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
            this.prepared = Boolean.FALSE;
        }

        @Override
        public String toString() {
            final String s;
            if (this.isPrepared()) {
                s = this.mockAsString(Dialect.MySQL57, Visible.ONLY_VISIBLE, true);
            } else {
                s = super.toString();
            }
            return s;
        }


    }// StandardRowSetInsertStatement


}
