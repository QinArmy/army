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
final class StandardValueInsert<T extends IDomain, C> extends ValueInsert<
        T,
        C,
        Insert.ValueInsertIntoSpec<T, C>,//OR
        Insert.ValueSpec<T, C>, //IR
        Insert.ValueSpec<T, C>> // SR
        implements Insert.StandardValueInsertSpec<T, C>, Insert.ValueSpec<T, C>, Insert.ValueInsertIntoSpec<T, C> {


    static <T extends IDomain, C> StandardValueInsert<T, C> create(TableMeta<T> table, @Nullable C criteria) {
        return new StandardValueInsert<>(table, criteria);
    }

    private StandardValueInsert(TableMeta<T> table, @Nullable C criteria) {
        super(table, CriteriaContexts.insertContext(criteria));
        CriteriaContextStack.setContextStack(this.criteriaContext);
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
