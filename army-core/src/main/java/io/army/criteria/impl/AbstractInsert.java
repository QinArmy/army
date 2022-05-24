package io.army.criteria.impl;

import io.army.criteria.CriteriaException;
import io.army.criteria.Insert;
import io.army.criteria.Visible;
import io.army.criteria.WithElement;
import io.army.criteria.impl.inner._Insert;
import io.army.dialect.Dialect;
import io.army.dialect._MockDialects;
import io.army.domain.IDomain;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.stmt.PairStmt;
import io.army.stmt.SimpleStmt;
import io.army.stmt.Stmt;
import io.army.util._CollectionUtils;
import io.army.util._Exceptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;


/**
 * @since 1.0
 */
@SuppressWarnings("unchecked")
abstract class AbstractInsert<C, T extends IDomain, IR>
        implements Insert, Insert._InsertSpec, Insert._InsertIntoClause<C, T, IR>, _Insert {

    final TableMeta<T> table;

    final C criteria;

    final CriteriaContext criteriaContext;

    private List<FieldMeta<?>> fieldList;


    AbstractInsert(TableMeta<T> table, CriteriaContext criteriaContext) {
        this.table = table;
        this.criteria = criteriaContext.criteria();
        this.criteriaContext = criteriaContext;
    }


    @Override
    public final IR insertInto(Consumer<Consumer<FieldMeta<? super T>>> consumer) {
        final List<FieldMeta<?>> fieldList = new ArrayList<>();
        consumer.accept(fieldList::add);
        this.fieldList = _CollectionUtils.unmodifiableList(fieldList);
        return (IR) this;
    }

    @Override
    public final IR insertInto(BiConsumer<C, Consumer<FieldMeta<? super T>>> consumer) {
        final List<FieldMeta<?>> fieldList = new ArrayList<>();
        consumer.accept(this.criteria, fieldList::add);
        this.fieldList = _CollectionUtils.unmodifiableList(fieldList);
        return (IR) this;
    }

    @Override
    public final IR insertInto(TableMeta<T> table) {
        if (table != this.table) {
            throw new CriteriaException("table not match.");
        }
        this.fieldList = Collections.emptyList();
        return (IR) this;
    }


    @Override
    public final TableMeta<?> table() {
        return this.table;
    }

    @Override
    public final List<FieldMeta<?>> fieldList() {
        prepared();
        final List<FieldMeta<?>> fieldList = this.fieldList;
        if (fieldList == null) {
            throw _Exceptions.castCriteriaApi();
        }
        return fieldList;
    }


    @Override
    public void clear() {
        this.fieldList = null;
    }


    abstract Dialect defaultDialect();

    abstract void validateDialect(Dialect dialect);


    @Override
    public final String toString() {
        final String s;
        if (!(this instanceof WithElement) && this.isPrepared()) {
            s = this.mockAsString(this.defaultDialect(), Visible.ONLY_VISIBLE, true);
        } else {
            s = super.toString();
        }
        return s;
    }

    @Override
    public final String mockAsString(Dialect dialect, Visible visible, boolean beautify) {
        final Stmt stmt;
        stmt = mockAsStmt(dialect, visible);
        final StringBuilder builder = new StringBuilder();
        if (stmt instanceof SimpleStmt) {
            builder.append("insert sql:\n")
                    .append(((SimpleStmt) stmt).sql());
        } else if (stmt instanceof PairStmt) {
            builder.append("parent insert sql:\n")
                    .append(((PairStmt) stmt).parentStmt().sql())
                    .append("\n\nchild insert sql:\n")
                    .append(((PairStmt) stmt).childStmt().sql());
        } else {
            throw new IllegalStateException("Unknown stmt type.");
        }
        return builder.toString();
    }

    @Override
    public final Stmt mockAsStmt(final Dialect dialect, final Visible visible) {
        if (this instanceof WithElement) {
            throw new IllegalStateException("mockAsStmt(DialectMode) support only non-with element statement.");
        }
        this.validateDialect(dialect);
        return _MockDialects.from(dialect).insert(this, visible);
    }


}
