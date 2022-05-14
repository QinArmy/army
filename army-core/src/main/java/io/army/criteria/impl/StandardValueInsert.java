package io.army.criteria.impl;

import io.army.criteria.Insert;
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
        Insert.ValueInsertIntoSpec<C, T>,//OR
        Insert.ValueSpec<C, T>, //IR
        Insert.ValueSpec<C, T>> // SR
        implements Insert.StandardValueInsertSpec<C, T>, Insert.ValueSpec<C, T>, Insert.ValueInsertIntoSpec<C, T> {


    static <C, T extends IDomain> StandardValueInsert<C, T> create(TableMeta<T> table, @Nullable C criteria) {
        return new StandardValueInsert<>(table, criteria);
    }

    private StandardValueInsert(TableMeta<T> table, @Nullable C criteria) {
        super(table, CriteriaContexts.insertContext(criteria));
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
