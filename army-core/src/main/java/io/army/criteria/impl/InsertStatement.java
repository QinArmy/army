package io.army.criteria.impl;


import io.army.criteria.Insert;
import io.army.criteria.Statement;
import io.army.criteria.Visible;
import io.army.criteria.impl.inner._Insert;
import io.army.dialect.Dialect;
import io.army.domain.IDomain;
import io.army.meta.ChildTableMeta;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.stmt.Stmt;
import io.army.util._Exceptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;


abstract class InsertStatement<C, T extends IDomain, IR> implements Insert, Insert._ColumnListClause<C, T, IR>
        , Insert._ColumnClause<T, IR>, Statement._RightParenClause<IR>, _Insert, Insert._InsertSpec {

    final CriteriaContext criteriaContext;

    final C criteria;

    final TableMeta<T> table;

    private List<FieldMeta<?>> fieldList;

    private List<FieldMeta<?>> childFieldList;

    InsertStatement(CriteriaContext criteriaContext, TableMeta<T> table) {
        this.criteriaContext = criteriaContext;
        this.criteria = criteriaContext.criteria();
        this.table = table;
    }


    @Override
    public final _RightParenClause<IR> leftParen(Consumer<Consumer<FieldMeta<? super T>>> consumer) {
        consumer.accept(this::addField);
        this.finishFieldList();
        return this;
    }

    @Override
    public final _RightParenClause<IR> leftParen(BiConsumer<C, Consumer<FieldMeta<? super T>>> consumer) {
        consumer.accept(this.criteria, this::addField);
        this.finishFieldList();
        return this;
    }

    @Override
    public final _ColumnClause<T, IR> leftParen(FieldMeta<? super T> field) {
        this.addField(field);
        return this;
    }

    @Override
    public final _ColumnClause<T, IR> comma(FieldMeta<? super T> field) {
        this.addField(field);
        return this;
    }

    @Override
    public final IR rightParen() {
        this.finishFieldList();
        return this.endColumnList();
    }


    @Override
    public final TableMeta<?> table() {
        return this.table;
    }

    @Override
    public final List<FieldMeta<?>> fieldList() {
        List<FieldMeta<?>> fieldList = this.fieldList;
        if (fieldList == null) {
            fieldList = Collections.emptyList();
        }
        return fieldList;
    }

    @Override
    public final List<FieldMeta<?>> childFieldList() {
        List<FieldMeta<?>> childFieldList = this.childFieldList;
        if (childFieldList == null) {
            childFieldList = Collections.emptyList();
        }
        return childFieldList;
    }

    @Override
    public void clear() {
        this.fieldList = null;
        this.childFieldList = null;
    }


    abstract IR endColumnList();


    @Override
    public final String mockAsString(Dialect dialect, Visible visible, boolean none) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final Stmt mockAsStmt(Dialect dialect, Visible visible) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final String toString() {
        return super.toString();
    }

    private void addField(final FieldMeta<?> field) {
        if (!field.insertable()) {
            throw CriteriaContextStack.criteriaError(_Exceptions::nonInsertableField, field);
        }
        final TableMeta<?> fieldTable = field.tableMeta();
        final TableMeta<?> table = this.table;

        if (fieldTable instanceof ChildTableMeta) {
            if (fieldTable != table) {
                throw CriteriaContextStack.criteriaError(_Exceptions::unknownColumn, field);
            }
            List<FieldMeta<?>> childFieldList = this.childFieldList;
            if (childFieldList == null) {
                childFieldList = new ArrayList<>();
            }
            childFieldList.add(field);
        } else if (fieldTable == table
                || (table instanceof ChildTableMeta && fieldTable == ((ChildTableMeta<?>) table).parentMeta())) {
            List<FieldMeta<?>> fieldList = this.fieldList;
            if (fieldList == null) {
                fieldList = new ArrayList<>();
            }
            fieldList.add(field);
        } else {
            throw CriteriaContextStack.criteriaError(_Exceptions::unknownColumn, field);
        }


    }


    private void finishFieldList() {
        final List<FieldMeta<?>> fieldList, childFieldList;
        fieldList = this.fieldList;
        childFieldList = this.childFieldList;
        if ((fieldList == null || fieldList.size() == 0) && (childFieldList == null || childFieldList.size() == 0)) {
            throw CriteriaContextStack.criteriaError("Column list must not empty.");
        }
        if (fieldList == null) {
            this.fieldList = Collections.emptyList();
        } else {
            this.fieldList = Collections.unmodifiableList(fieldList);
        }
        if (childFieldList == null) {
            this.childFieldList = Collections.emptyList();
        } else {
            this.childFieldList = Collections.unmodifiableList(childFieldList);
        }

    }


}
