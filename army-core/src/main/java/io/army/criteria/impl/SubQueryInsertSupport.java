package io.army.criteria.impl;

import io.army.criteria.Insert;
import io.army.criteria.Statement;
import io.army.criteria.SubQuery;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.util._Exceptions;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

abstract class SubQueryInsertSupport {

    private SubQueryInsertSupport() {
        throw new UnsupportedOperationException();
    }

    static abstract class SubQueryColumn<C, T extends IDomain, IR> implements Insert._SingleColumnListClause<C, T, IR>
            , Insert._SingleColumnClause<T, IR>, Statement._RightParenClause<IR> {

        final CriteriaContext criteriaContext;

        final TableMeta<T> table;

        final List<FieldMeta<?>> fieldList = new ArrayList<>();

        SubQueryColumn(CriteriaContext criteriaContext, TableMeta<T> table) {
            this.criteriaContext = criteriaContext;
            this.table = table;
        }

        @Override
        public final Statement._RightParenClause<IR> leftParen(Consumer<Consumer<FieldMeta<T>>> consumer) {
            consumer.accept(this::addField);
            return this;
        }

        @Override
        public final Statement._RightParenClause<IR> leftParen(BiConsumer<C, Consumer<FieldMeta<T>>> consumer) {
            consumer.accept(this.criteriaContext.criteria(), this::addField);
            return this;
        }

        @Override
        public final Insert._SingleColumnClause<T, IR> leftParen(FieldMeta<T> field) {
            this.addField(field);
            return this;
        }

        @Override
        public final Insert._SingleColumnClause<T, IR> comma(FieldMeta<T> field) {
            this.addField(field);
            return this;
        }

        private void addField(final FieldMeta<? super T> field) {
            if (!field.insertable()) {
                throw CriteriaContextStack.criteriaError(_Exceptions::nonInsertableField, field);
            }
            if (field.tableMeta() != this.table) {
                throw CriteriaContextStack.criteriaError(_Exceptions::unknownColumn, field);
            }
            this.fieldList.add(field);
        }


    }// SubQueryColumn


    static abstract class SubQueryClause<C, SR> implements Insert._SubQueryClause<C, SR>, Statement._RightParenClause<SR> {

        final CriteriaContext criteriaContext;

        final List<FieldMeta<?>> fieldList;

        SubQuery subQuery;

        SubQueryClause(CriteriaContext criteriaContext, List<FieldMeta<?>> fieldList) {
            this.criteriaContext = criteriaContext;
            this.fieldList = fieldList;
        }

        @Override
        public SR space(Supplier<? extends SubQuery> supplier) {
            return this.acceptSubQuery(supplier.get());
        }

        @Override
        public SR space(Function<C, ? extends SubQuery> function) {
            return this.acceptSubQuery(function.apply(this.criteriaContext.criteria()));
        }


        abstract SR endSubQuery();


        private SR acceptSubQuery(final @Nullable SubQuery subQuery) {
            CriteriaContextStack.assertNonNull(subQuery, "subQuery must non-null.");
            final int selectionCount;
            selectionCount = CriteriaUtils.selectionCount(subQuery);
            if (selectionCount != this.fieldList.size()) {
                String m = String.format("SubQuery selection list size[%s] and column list size[%s] not match."
                        , selectionCount, this.fieldList.size());
                throw CriteriaContextStack.criteriaError(m);
            }
            this.subQuery = subQuery;
            return this.endSubQuery();
        }


    }//SubQueryClause


}
