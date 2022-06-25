package io.army.criteria.impl;

import io.army.criteria.Insert;
import io.army.criteria.StandardStatement;
import io.army.criteria.Visible;
import io.army.dialect.Dialect;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;

/**
 * <p>
 * This class representing standard value insert statement.
 * </p>
 *
 * @param <T> domain java type.
 * @param <C> criteria java type used to dynamic statement.
 * @since 1.0
 */
final class StandardValueInsert<C, T extends IDomain> extends ValueInsert<
        C,
        T,
        Insert._StandardValueSpec<C, T>,//IR
        Insert._InsertSpec> // VR
        implements StandardStatement, Insert._StandardColumnsSpec<C, T>, Insert._StandardValueSpec<C, T> {


    static <C> _StandardLiteralOptionSpec<C> create(@Nullable C criteria) {
        return new InsertOption<>(criteria);
    }

    private StandardValueInsert(ValueInsetOptionClause<C, ?, ?> optionClause, TableMeta<T> table) {
        super(optionClause, table);
    }

    @Override
    _StandardValueSpec<C, T> endColumnList() {
        return this;
    }


    @Override
    public String toString() {
        final String s;
        if (isPrepared()) {
            s = this.mockAsString(Dialect.MySQL57, Visible.ONLY_VISIBLE, true);
        } else {
            s = super.toString();
        }
        return s;
    }

    private static final class InsertOption<C> extends ValueInsetOptionClause<
            C,
            Insert._StandardOptionSpec<C>,
            Insert._StandardInsertIntoClause<C>>
            implements Insert._StandardLiteralOptionSpec<C>, Insert._StandardOptionSpec<C> {

        private InsertOption(@Nullable C criteria) {
            super(CriteriaContexts.primaryInsertContext(criteria));
        }

        @Override
        public <T extends IDomain> _StandardColumnsSpec<C, T> insertInto(TableMeta<T> table) {
            CriteriaContextStack.assertNonNull(table, "table must non-null");
            return new StandardValueInsert<>(this, table);
        }


    }//InsertOption


}
