package io.army.criteria.impl;

import io.army.criteria.StandardQuery;
import io.army.criteria.TableItem;
import io.army.criteria.impl.inner._TableBlock;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;

/**
 * @since 1.0
 */
final class StandardNestedItems<C> extends JoinableClause.LeftParenNestedItem<
        C,
        StandardQuery._NestedJoinSpec<C>,
        StandardQuery._NestedJoinSpec<C>,
        Void,
        StandardQuery._NestedJoinSpec<C>,
        StandardQuery._NestedOnSpec<C>,
        StandardQuery._NestedOnSpec<C>,
        Void> implements StandardQuery._StandardNestedLeftParenClause<C> {

    static <C> StandardQuery._StandardNestedLeftParenClause<C> create(@Nullable C criteria) {
        final CriteriaContext currentContext;
        currentContext = CriteriaContextStack.peek();
        if (criteria != null && currentContext.criteria() != criteria) {
            throw CriteriaUtils.criteriaNotMatch(currentContext);
        }
        return new StandardNestedItems<>(currentContext);
    }


    private StandardNestedItems(CriteriaContext criteriaContext) {
        super(criteriaContext);
    }


    @Override
    public _TableBlock createNoOnTableBlock(_JoinType joinType, @Nullable ItemWord itemWord, TableMeta<?> table, String alias) {
        if (itemWord != null) {
            throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
        }
        return new TableBlock.NoOnTableBlock(joinType, table, alias);
    }

    @Override
    public _TableBlock createNoOnItemBlock(_JoinType joinType, @Nullable ItemWord itemWord, TableItem tableItem, String alias) {
        if (itemWord != null) {
            throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
        }
        return new TableBlock.NoOnTableBlock(joinType, tableItem, alias);
    }

    @Override
    public _TableBlock createDynamicBlock(final _JoinType joinType, final DynamicBlock<?> block) {
        return CriteriaUtils.createStandardDynamicBlock(joinType, block);
    }

    @Override
    public StandardQuery._NestedOnSpec<C> createTableBlock(_JoinType joinType, @Nullable ItemWord itemWord, TableMeta<?> table, String tableAlias) {
        if (itemWord != null) {
            throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
        }
        return new OnClauseTableBlock<>(this, joinType, table, tableAlias);
    }

    @Override
    public StandardQuery._NestedOnSpec<C> createItemBlock(_JoinType joinType, @Nullable ItemWord itemWord, TableItem tableItem, String alias) {
        if (itemWord != null) {
            throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
        }
        return new OnClauseTableBlock<>(this, joinType, tableItem, alias);
    }

    private static final class OnClauseTableBlock<C> extends JoinableClause.OnOrJoinBlock<
            C,
            StandardQuery._NestedJoinSpec<C>,
            StandardQuery._NestedJoinSpec<C>,
            Void,
            StandardQuery._NestedJoinSpec<C>,
            StandardQuery._NestedOnSpec<C>,
            StandardQuery._NestedOnSpec<C>,
            Void> implements StandardQuery._NestedOnSpec<C> {

        private OnClauseTableBlock(StandardNestedItems<C> clause, _JoinType joinType, TableItem tableItem, String alias) {
            super(clause, joinType, tableItem, alias);
        }


    }//OnClauseTableBlock


}
