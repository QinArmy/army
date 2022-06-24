package io.army.criteria.impl;

import io.army.criteria.CriteriaException;
import io.army.criteria.Insert;
import io.army.criteria.SubStatement;
import io.army.criteria.Visible;
import io.army.criteria.impl.inner._Insert;
import io.army.dialect.Dialect;
import io.army.dialect._MockDialects;
import io.army.domain.IDomain;
import io.army.meta.ChildTableMeta;
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
        implements Insert, Insert._InsertSpec, Insert._InsertIntoClause<C, T, IR>, _Insert
        , Insert._ColumnClause<T, IR> {

    final TableMeta<T> table;

    final C criteria;

    final CriteriaContext criteriaContext;

    private List<FieldMeta<?>> fieldList;

    private List<FieldMeta<?>> childFieldList;


    AbstractInsert(TableMeta<T> table, CriteriaContext criteriaContext) {
        this.table = table;
        this.criteria = criteriaContext.criteria();
        this.criteriaContext = criteriaContext;
    }


    @Override
    public final IR insertInto(Consumer<Consumer<FieldMeta<? super T>>> consumer) {
        consumer.accept(this::addField);
        this.prepareFieldList();
        return (IR) this;
    }

    @Override
    public final IR insertInto(BiConsumer<C, Consumer<FieldMeta<? super T>>> consumer) {
        consumer.accept(this.criteria, this::addField);
        this.prepareFieldList();
        return (IR) this;
    }

    @Override
    public final IR insertInto(TableMeta<T> table) {
        if (table != this.table) {
            throw new CriteriaException("table not match.");
        }
        this.fieldList = Collections.emptyList();
        this.childFieldList = Collections.emptyList();
        return (IR) this;
    }

    @Override
    public final _ColumnClause<T, IR> insertInto(FieldMeta<? super T> field) {
        final List<FieldMeta<?>> fieldList = new ArrayList<>();
        fieldList.add(field);
        this.fieldList = fieldList;
        return this;
    }

    @Override
    public final _ColumnClause<T, IR> comma(FieldMeta<? super T> field) {
        this.fieldList.add(field);
        return this;
    }

    @Override
    public final IR rightParen() {
        this.fieldList = Collections.unmodifiableList(this.fieldList);
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
    public final List<FieldMeta<?>> childFieldList() {
        prepared();
        final List<FieldMeta<?>> childFieldList = this.childFieldList;
        if (childFieldList == null) {
            throw _Exceptions.castCriteriaApi();
        }
        return childFieldList;
    }

    @Override
    public void clear() {
        this.fieldList = null;
        this.childFieldList = null;
    }


    abstract Dialect defaultDialect();

    abstract void validateDialect(Dialect dialect);


    @Override
    public final String toString() {
        final String s;
        if (!(this instanceof SubStatement) && this.isPrepared()) {
            s = this.mockAsString(this.defaultDialect(), Visible.ONLY_VISIBLE, true);
        } else {
            s = super.toString();
        }
        return s;
    }

    @Override
    public final String mockAsString(Dialect dialect, Visible visible, boolean none) {
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
        if (this instanceof SubStatement) {
            throw new IllegalStateException("mockAsStmt(DialectMode) support only non-with element statement.");
        }
        this.validateDialect(dialect);
        return _MockDialects.from(dialect).insert(this, visible);
    }


    private void addField(final FieldMeta<?> field) {
        if (!field.insertable()) {
            throw _Exceptions.nonInsertableField(field);
        }
        final TableMeta<?> belongOf = field.tableMeta();
        final TableMeta<?> table = this.table;
        if (belongOf instanceof ChildTableMeta) {
            if (belongOf != table) {
                throw _Exceptions.unknownColumn(null, field);
            }
            List<FieldMeta<?>> childFieldList = this.childFieldList;
            if (childFieldList == null) {
                childFieldList = new ArrayList<>();
                this.childFieldList = childFieldList;
            }
            childFieldList.add(field);
        } else if (belongOf == table
                || (table instanceof ChildTableMeta && belongOf == ((ChildTableMeta<?>) table).parentMeta())) {
            List<FieldMeta<?>> fieldList = this.fieldList;
            if (fieldList == null) {
                fieldList = new ArrayList<>();
                this.fieldList = fieldList;
            }
            fieldList.add(field);
        } else {
            throw _Exceptions.unknownColumn(null, field);
        }

    }

    private void prepareFieldList() {
        final List<FieldMeta<?>> fieldList, childFieldList;
        fieldList = this.fieldList;
        childFieldList = this.childFieldList;

        if ((fieldList == null || fieldList.size() == 0)
                && (childFieldList == null || childFieldList.size() == 0)) {
            throw new CriteriaException("Not found any Field");
        }

        if (fieldList == null) {
            this.fieldList = Collections.emptyList();
        } else {
            this.fieldList = _CollectionUtils.unmodifiableList(fieldList);
        }
        if (childFieldList == null) {
            this.childFieldList = Collections.emptyList();
        } else {
            this.childFieldList = _CollectionUtils.unmodifiableList(childFieldList);
        }

    }


}
