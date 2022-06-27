package io.army.criteria.impl;

import io.army.criteria.Insert;
import io.army.criteria.NullHandleMode;
import io.army.criteria.StandardStatement;
import io.army.criteria.TableField;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.ChildTableMeta;
import io.army.meta.FieldMeta;
import io.army.meta.SingleTableMeta;
import io.army.meta.TableMeta;
import io.army.util._Assert;

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


}
