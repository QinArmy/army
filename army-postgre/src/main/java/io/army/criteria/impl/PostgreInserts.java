package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner.postgre._ConflictTargetItem;
import io.army.criteria.postgre.PostgreInsert;
import io.army.dialect._SqlContext;
import io.army.lang.Nullable;
import io.army.meta.IndexFieldMeta;
import io.army.meta.ParentTableMeta;
import io.army.meta.SimpleTableMeta;

import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Supplier;

abstract class PostgreInserts extends InsertSupport {

    private PostgreInserts() {
    }


    static <C> PostgreInsert._DomainOptionSpec<C> domainInsert(@Nullable C criteria) {
        return new DomainInsertIntoClause<>(criteria);
    }


    private abstract class ConflictTargetItem<T, CA, CR, PR, RR>
            implements PostgreInsert._ConflictTargetCommaClause<T, CA>
            , PostgreInsert._ConflictCollateClause<CR>
            , PostgreInsert._ConflictOpClassClause<PR>
            , Statement._RightParenClause<RR>
            , _ConflictTargetItem {

        private final Consumer<_ConflictTargetItem> itemConsumer;

        private final IndexFieldMeta<T> indexColumn;

        ConflictTargetItem(Consumer<_ConflictTargetItem> itemConsumer, IndexFieldMeta<T> indexColumn) {
            this.itemConsumer = itemConsumer;
            this.indexColumn = indexColumn;
        }

        @Override
        public final void appendSql(final _SqlContext context) {

        }

        @Override
        public final CA comma(IndexFieldMeta<T> indexColumn) {
            return null;
        }

        @Override
        public final CR collation(String collationName) {
            return null;
        }

        @Override
        public final CR collation(Supplier<String> supplier) {
            return null;
        }

        @Override
        public final PR opClass() {
            return null;
        }

        @Override
        public final PR ifOpClass(BooleanSupplier supplier) {
            return null;
        }

        @Override
        public final RR rightParen() {
            return null;
        }


    }//ConflictTargetItem


    private abstract class AbstractOnConflictClause<C, T, LR, OC, CR, PR, RR, WR, WA, SR, DR>
            implements PostgreInsert._ConflictItemClause<T, LR, OC>
            , PostgreInsert._ConflictCollateClause<CR>
            , PostgreInsert._ConflictOpClassClause<C, PR>
            , PostgreInsert._ConflictTargetCommaClause<T, LR>
            , Statement._RightParenClause<RR>
            , Statement._MinQueryWhereClause<C, WR, WA>
            , Statement._MinWhereAndClause<C, WA>

            , PostgreInsert._DynamicReturningClause<C, DR>
            , PostgreInsert._StaticReturningClause<SR>
            , PostgreInsert._StaticReturningCommaClause<SR> {

        private CriteriaContext context;
        private C criteria;


        private String constraintName;
        private List<SelectItem> selectItemList;

        @Override
        public final LR leftParen(IndexFieldMeta<T> indexColumn) {
            return this.comma(indexColumn);
        }

        @Override
        public final OC onConstraint(final @Nullable String constraintName) {
            if (constraintName == null) {
                throw CriteriaContextStack.nullPointer(this.context);
            }
            this.constraintName = constraintName;
            return (OC) this;
        }

        @Override
        public final LR comma(IndexFieldMeta<T> indexColumn) {
            return (LR) this;
        }


        @Override
        public final DR returning() {
            this.selectItemList = Collections.emptyList();
            return (DR) this;
        }

        @Override
        public final DR returning(SelectItem selectItem) {
            this.selectItemList = Collections.singletonList(selectItem);
            return (DR) this;
        }

        @Override
        public final DR returning(Consumer<Consumer<SelectItem>> consumer) {
            consumer.accept(this::addSelectItem);
            return (DR) this;
        }

        @Override
        public final DR returning(BiConsumer<C, Consumer<SelectItem>> consumer) {
            consumer.accept(this.criteria, this::addSelectItem);
            return (DR) this;
        }

        @Override
        public final SR returning(SelectItem selectItem1, SelectItem selectItem2) {
            this.addSelectItem(selectItem1);
            this.addSelectItem(selectItem2);
            return (SR) this;
        }


        @Override
        public final SR comma(SelectItem selectItem) {
            this.addSelectItem(selectItem);
            return (SR) this;
        }

        @Override
        public final PostgreInsert._StaticReturningCommaClause<SR> comma(SelectItem selectItem1, SelectItem selectItem2) {
            this.addSelectItem(selectItem1);
            this.addSelectItem(selectItem2);
            return this;
        }


        private void addSelectItem(SelectItem selectItem) {

        }


    }//AbstractOnConflictClause



    /*-------------------below domain insert syntax class-------------------*/


    private static final class DomainInsertIntoClause<C> extends NonQueryWithCteOption<
            C,
            PostgreInsert._DomainNullOptionSpec<C>,
            PostgreInsert._DomainPreferLiteralSpec<C>,
            PostgreInsert._DomainWithCteSpec<C>,
            SubStatement,
            PostgreInsert._DomainInsertIntoClause<C>>
            implements PostgreInsert._DomainOptionSpec<C> {

        private DomainInsertIntoClause(@Nullable C criteria) {
            super(CriteriaContexts.primaryInsertContext(criteria));
        }


        @Override
        public <T> PostgreInsert._DomainTableAliasSpec<C, T, Insert, ReturningInsert> insertInto(SimpleTableMeta<T> table) {
            return new DomainInsertIntoValuesClause<>(this, table);
        }

        @Override
        public <P> PostgreInsert._DomainParentAliasSpec<C, P> insertInto(ParentTableMeta<P> table) {
            return null;
        }


    }//DomainInsertIntoClause


    private static final class DomainInsertIntoValuesClause<C, T, I extends DmlStatement.DmlInsert, Q extends DqlStatement.DqlInsert>
            extends DomainValueClause<
            C,
            T,
            PostgreInsert._DomainOverridingValueSpec<C, T, I, Q>,
            PostgreInsert._DomainColumnDefaultSpec<C, T, I, Q>,
            PostgreInsert._OnConflictSpec<C, T, I, Q>>
            implements PostgreInsert._DomainTableAliasSpec<C, T, I, Q> {

        private String tableAlias;

        private DomainInsertIntoValuesClause(WithValueSyntaxOptions options, SimpleTableMeta<T> table) {
            super(options, table);
        }

        @Override
        public PostgreInsert._DomainColumnListSpec<C, T, I, Q> as(final @Nullable String alias) {
            if (alias == null) {
                throw CriteriaContextStack.nullPointer(this.context);
            }
            this.tableAlias = alias;
            return this;
        }

        @Override
        PostgreInsert._OnConflictSpec<C, T, I, Q> valuesEnd() {
            return null;
        }

        @Override
        public PostgreInsert._OnConflictSpec<C, T, I, Q> defaultValues() {
            return null;
        }

        @Override
        public PostgreInsert._DomainColumnDefaultSpec<C, T, I, Q> overridingSystemValue() {
            return null;
        }

        @Override
        public PostgreInsert._DomainColumnDefaultSpec<C, T, I, Q> overridingUserValue() {
            return null;
        }


    }//DomainInsertIntoValuesClause


}
