package io.army.criteria.impl;

import io.army.criteria.Insert;
import io.army.criteria.StandardStatement;
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
        Insert._StandardOptionSpec<C, T>,
        Insert._ValueInsertIntoSpec<C, T>,//OR
        Insert._ValueSpec<C, T>, //IR
        Insert._ValueSpec<C, T>> // SR
        implements Insert._StandardOptionSpec<C, T>, Insert._ValueSpec<C, T>, Insert._ValueInsertIntoSpec<C, T>
        , Insert._StandardLiteralOptionSpec<C, T>, StandardStatement {


    static <C, T extends IDomain> StandardValueInsert<C, T> create(TableMeta<T> table, @Nullable C criteria) {
        return new StandardValueInsert<>(table, criteria);
    }

    private StandardValueInsert(TableMeta<T> table, @Nullable C criteria) {
        super(table, CriteriaContexts.primaryInsertContext(criteria));
    }

    @Override
    Dialect defaultDialect() {
        return Dialect.MySQL57;
    }

    @Override
    void validateDialect(Dialect dialect) {
        //no-op
    }


}
