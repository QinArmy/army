package io.army.criteria.impl;

import io.army.criteria.Expression;
import io.army.criteria.TablePart;
import io.army.criteria.Update;
import io.army.criteria.impl.inner._Predicate;
import io.army.criteria.mysql.MySQLQuery;
import io.army.meta.FieldMeta;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

abstract class MySQLTableBlocks {

    MySQLTableBlocks() {
        throw new UnsupportedOperationException();
    }


    private static class OptimizeFromBlock<C, IR> extends TableBlock implements MySQLQuery.IndexHintClause<C, IR> {

        final String alias;

        OptimizeFromBlock(TablePart tablePart, String alias) {
            super(tablePart, JoinType.NONE);
            this.alias = alias;
        }

        @Override
        public final String alias() {
            return this.alias;
        }

        @Override
        public final List<_Predicate> predicates() {
            return Collections.emptyList();
        }

        @Override
        public IR useIndex() {
            return null;
        }

        @Override
        public IR ignoreIndex() {
            return null;
        }

        @Override
        public IR forceIndex() {
            return null;
        }

        @Override
        public IR ifUseIndex(Predicate<C> predicate) {
            return null;
        }

        @Override
        public IR ifIgnoreIndex(Predicate<C> predicate) {
            return null;
        }

        @Override
        public IR ifForceIndex(Predicate<C> predicate) {
            return null;
        }


    }//OptimizeFromBlock


    static abstract class UpdateBlock<C, IR, SR> extends TableBlock implements MySQLQuery.IndexHintClause<C, IR>
            , Update.SimpleSetClause<C, SR> {


        UpdateBlock(TablePart tablePart) {
            super(tablePart, JoinType.NONE);
        }

        @Override
        public final SR set(FieldMeta<?, ?> field, Expression<?> value) {
            return null;
        }

        @Override
        public final <F> SR set(FieldMeta<?, F> field, Function<C, Expression<F>> function) {
            return null;
        }

        @Override
        public final <F> SR set(FieldMeta<?, F> field, Supplier<Expression<F>> supplier) {
            return null;
        }

        @Override
        public final SR setNull(FieldMeta<?, ?> field) {
            return null;
        }

        @Override
        public final SR setDefault(FieldMeta<?, ?> field) {
            return null;
        }

        @Override
        public final SR ifSetNull(Predicate<C> predicate, FieldMeta<?, ?> field) {
            return null;
        }

        @Override
        public final SR ifSetDefault(Predicate<C> predicate, FieldMeta<?, ?> field) {
            return null;
        }

        @Override
        public final <F> SR ifSet(FieldMeta<?, F> field, Function<C, Expression<F>> function) {
            return null;
        }

        @Override
        public final <F> SR ifSet(FieldMeta<?, F> field, Supplier<Expression<F>> supplier) {
            return null;
        }

        @Override
        public final SR set(List<FieldMeta<?, ?>> fieldList, List<Expression<?>> valueList) {
            return null;
        }

        @Override
        public final SR set(FieldMeta<?, ?> field, Object value) {
            return null;
        }

        @Override
        public final <F extends Number> SR setPlus(FieldMeta<?, F> field, F value) {
            return createSetClause().setPlus(field, value);
        }

        @Override
        public final <F extends Number> SR setPlus(FieldMeta<?, F> field, Expression<F> value) {
            return null;
        }

        @Override
        public final <F extends Number> SR setMinus(FieldMeta<?, F> field, F value) {
            return null;
        }

        @Override
        public final <F extends Number> SR setMinus(FieldMeta<?, F> field, Expression<F> value) {
            return null;
        }

        @Override
        public final <F extends Number> SR setMultiply(FieldMeta<?, F> field, F value) {
            return null;
        }

        @Override
        public final <F extends Number> SR setMultiply(FieldMeta<?, F> field, Expression<F> value) {
            return null;
        }

        @Override
        public final <F extends Number> SR setDivide(FieldMeta<?, F> field, F value) {
            return null;
        }

        @Override
        public final <F extends Number> SR setDivide(FieldMeta<?, F> field, Expression<F> value) {
            return null;
        }

        @Override
        public final <F extends Number> SR setMod(FieldMeta<?, F> field, F value) {
            return null;
        }

        @Override
        public final <F extends Number> SR setMod(FieldMeta<?, F> field, Expression<F> value) {
            return null;
        }

        @Override
        public final SR ifSet(List<FieldMeta<?, ?>> fieldList, List<Expression<?>> valueList) {
            return null;
        }

        @Override
        public final <F> SR ifSet(FieldMeta<?, F> field, F value) {
            return null;
        }

        @Override
        public final <F extends Number> SR ifSetPlus(FieldMeta<?, F> field, F value) {
            return null;
        }

        @Override
        public final <F extends Number> SR ifSetMinus(FieldMeta<?, F> field, F value) {
            return null;
        }

        @Override
        public final <F extends Number> SR ifSetMultiply(FieldMeta<?, F> field, F value) {
            return null;
        }

        @Override
        public final <F extends Number> SR ifSetDivide(FieldMeta<?, F> field, F value) {
            return null;
        }

        @Override
        public final <F extends Number> SR ifSetMod(FieldMeta<?, F> field, F value) {
            return null;
        }

        @Override
        public String alias() {
            return null;
        }

        @Override
        public List<_Predicate> predicates() {
            return null;
        }

        @Override
        public IR useIndex() {
            return null;
        }

        @Override
        public IR ignoreIndex() {
            return null;
        }

        @Override
        public IR forceIndex() {
            return null;
        }

        @Override
        public IR ifUseIndex(Predicate<C> predicate) {
            return null;
        }

        @Override
        public IR ifIgnoreIndex(Predicate<C> predicate) {
            return null;
        }

        @Override
        public IR ifForceIndex(Predicate<C> predicate) {
            return null;
        }

        private Update.SimpleSetClause<C, SR> createSetClause() {
            return null;
        }

    }


}
