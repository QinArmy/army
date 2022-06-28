package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._Expression;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.ChildTableMeta;
import io.army.meta.FieldMeta;
import io.army.meta.SingleTableMeta;
import io.army.meta.TableMeta;
import io.army.util._Assert;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

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


    private static final class StandardDomainOptionClause<C> implements Insert._StandardDomainOptionSpec<C>
            , InsertOptions {

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
            return new StandardDomainInsert<>(this.criteriaContext, this, table);
        }

        @Override
        public <T extends IDomain> Insert._StandardDomainColumnsSpec<C, T, FieldMeta<? super T>> insertInto(ChildTableMeta<T> table) {
            return new StandardDomainInsert<>(this.criteriaContext, this, table);
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


    private static final class StandardDomainInsert<C, T extends IDomain, F extends TableField>
            extends DomainValueClause<C, T, F, Insert._StandardDomainCommonExpSpec<C, T, F>, Insert._InsertSpec>
            implements Insert._StandardDomainColumnsSpec<C, T, F>, StandardStatement, Insert._InsertSpec {


        private boolean prepared;

        private StandardDomainInsert(CriteriaContext criteriaContext, InsertOptions options, TableMeta<T> table) {
            super(criteriaContext, options, table);
        }

        @Override
        public Insert asInsert() {
            _Assert.nonPrepared(this.prepared);
            this.unmodifiedCommonExpMap();
            this.prepared = true;
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
        _StandardDomainCommonExpSpec<C, T, F> columnListEnd() {
            return this;
        }

        @Override
        public void clear() {
            _Assert.prepared(this.prepared);
            this.prepared = false;
            super.clear();
        }


    }//StandardDomainInsert


    private static final class StandardValueInsertOptionClause<C>
            implements Insert._StandardValueOptionSpec<C>, InsertOptions {

        private final CriteriaContext criteriaContext;

        private boolean migration;

        public StandardValueInsertOptionClause(@Nullable C criteria) {
            this.criteriaContext = CriteriaContexts.primaryInsertContext(criteria);
            CriteriaContextStack.setContextStack(this.criteriaContext);
        }

        @Override
        public Insert._StandardValueInsertIntoClause<C> migration(boolean migration) {
            this.migration = migration;
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
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isPreferLiteral() {
            //always false
            return false;
        }


    }//StandardValueInsertOptionClause

    private static final class StandardValueClause<C, T extends IDomain, F extends TableField>
            extends CommonExpClause<C, F, Insert._StandardCommonExpSpec<C, T, F>, Insert._StandardCommonExpSpec<C, T, F>>
            implements Insert._StandardColumnsSpec<C, T, F> {


        private StandardValueClause(CriteriaContext criteriaContext, InsertOptions options, TableMeta<?> table) {
            super(criteriaContext, options, table);
        }

        @Override
        public _StandardStaticValueLeftParenClause<C, F> value() {
            this.unmodifiedCommonExpMap();
            return new StandardStaticValueColumnsClause<>(this);
        }

        @Override
        public _InsertSpec value(Consumer<ColumnConsumer<F>> consumer) {
            this.unmodifiedCommonExpMap();
            return null;
        }

        @Override
        public _InsertSpec value(BiConsumer<C, ColumnConsumer<F>> consumer) {
            this.unmodifiedCommonExpMap();
            return null;
        }

        @Override
        public _StandardStaticValuesLeftParenClause<C, F> values() {
            this.unmodifiedCommonExpMap();
            return null;
        }

        @Override
        public _InsertSpec values(Consumer<RowConstructor<F>> consumer) {
            this.unmodifiedCommonExpMap();
            return null;
        }

        @Override
        public _InsertSpec values(BiConsumer<C, RowConstructor<F>> consumer) {
            this.unmodifiedCommonExpMap();
            return null;
        }

        @Override
        public void prepared() {

        }


        @Override
        public boolean isPrepared() {
            return false;
        }

        @Override
        _StandardCommonExpSpec<C, T, F> columnListEnd() {
            return null;
        }


    }//StandardValueInsert

    private static final class StandardStaticValueColumnsClause<C, F extends TableField>
            extends StaticValueColumnClause<C, F, Insert._InsertSpec>
            implements Insert._StandardStaticValueLeftParenClause<C, F> {

        final StandardValueClause<?, ?, ?> clause;

        final Map<FieldMeta<?>, _Expression> commonExpMap;

        final Map<FieldMeta<?>, _Expression> valueMap = new HashMap<>();

        private StandardStaticValueColumnsClause(StandardValueClause<C, ?, F> clause) {
            super(clause.criteriaContext);
            this.clause = clause;
            this.commonExpMap = clause.commonExpMap();
        }


        @Override
        public Insert._InsertSpec rightParen() {
            Map<FieldMeta<?>, _Expression> valueMap = this.valueMap;
            return new StandardValueInsert(this.clause, valueMap);
        }

        @Override
        void addValuePair(final FieldMeta<?> field, final _Expression value) {
            if (!this.clause.containField(field)) {

            }
            if (this.commonExpMap.containsKey(field)) {

            }
            if (this.valueMap.putIfAbsent(field, value) != null) {

            }
        }


    }//StaticValueClause

    static final class StandardValueInsert extends ValueInsertStatement implements Insert._InsertSpec {

        private boolean prepared;

        private StandardValueInsert(_CommonExpInsert clause, Map<FieldMeta<?>, _Expression> rowValues) {
            super(clause, rowValues);
        }

        @Override
        public Insert asInsert() {
            _Assert.nonPrepared(this.prepared);
            this.prepared = true;
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
        public void clear() {
            _Assert.prepared(this.prepared);
            this.prepared = false;
        }


    }//StandardValueInsert


}
