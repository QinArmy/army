package io.army.criteria.impl;

import io.army.criteria.DialectStatement;
import io.army.criteria.Statement;
import io.army.criteria.TableItem;
import io.army.criteria.impl.inner._TableBlock;
import io.army.meta.TableMeta;
import io.army.util._Exceptions;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * <p>
 * This class is base class of implementation of {@link Statement}.
 * </p>
 *
 * @since 1.0
 */
@SuppressWarnings("unchecked")
abstract class JoinableClause<C, JT, JS, JP, JC, JD, JE, JF>
        implements DialectStatement.DialectJoinClause<C, JT, JS, JP, JC, JD, JE, JF>
        , DialectStatement.JointLeftBracketClause<C, JT, JS, JP>, Statement._RightBracketClause<JD>
        , CriteriaContextSpec, CriteriaSpec<C> {

    final CriteriaContext criteriaContext;

    final C criteria;

    JoinableClause(CriteriaContext criteriaContext) {
        this.criteriaContext = criteriaContext;
        this.criteria = criteriaContext.criteria();
    }


    /*################################## blow JoinSpec method ##################################*/

    @Override
    public final JE leftJoin() {
        this.criteriaContext.onJoinType(_JoinType.LEFT_JOIN);
        return (JE) this;
    }

    @Override
    public final JP leftJoin(TableMeta<?> table) {
        return this.createNextClauseWithOnClause(_JoinType.LEFT_JOIN, table);
    }

    @Override
    public final JT leftJoin(TableMeta<?> table, String tableAlias) {
        final JT block;
        block = this.createTableBlock(_JoinType.LEFT_JOIN, table, tableAlias);
        this.criteriaContext.onAddBlock((_TableBlock) block);
        return block;
    }

    @Override
    public final <T extends TableItem> JS leftJoin(Supplier<T> supplier, String alias) {
        final JS block;
        block = this.createOnBlock(_JoinType.LEFT_JOIN, supplier.get(), alias);
        this.criteriaContext.onAddBlock((_TableBlock) block);
        return block;
    }

    @Override
    public final <T extends TableItem> JS leftJoin(Function<C, T> function, String alias) {
        final JS block;
        block = createOnBlock(_JoinType.LEFT_JOIN, function.apply(this.criteria), alias);
        this.criteriaContext.onAddBlock((_TableBlock) block);
        return block;
    }

    @Override
    public final JE join() {
        this.criteriaContext.onJoinType(_JoinType.JOIN);
        return (JE) this;
    }

    @Override
    public final JP join(TableMeta<?> table) {
        return this.createNextClauseWithOnClause(_JoinType.JOIN, table);
    }


    @Override
    public final JT join(TableMeta<?> table, String tableAlias) {
        final JT block;
        block = this.createTableBlock(_JoinType.JOIN, table, tableAlias);
        this.criteriaContext.onAddBlock((_TableBlock) block);
        return block;
    }

    @Override
    public final <T extends TableItem> JS join(Supplier<T> supplier, String alias) {
        final JS block;
        block = this.createOnBlock(_JoinType.JOIN, supplier.get(), alias);
        this.criteriaContext.onAddBlock((_TableBlock) block);
        return block;
    }

    @Override
    public final <T extends TableItem> JS join(Function<C, T> function, String alias) {
        final JS block;
        block = createOnBlock(_JoinType.JOIN, function.apply(this.criteria), alias);
        this.criteriaContext.onAddBlock((_TableBlock) block);
        return block;
    }

    @Override
    public final JE rightJoin() {
        this.criteriaContext.onJoinType(_JoinType.RIGHT_JOIN);
        return (JE) this;
    }

    @Override
    public final JP rightJoin(TableMeta<?> table) {
        return this.createNextClauseWithOnClause(_JoinType.RIGHT_JOIN, table);
    }

    @Override
    public final JT rightJoin(TableMeta<?> table, String tableAlias) {
        final JT block;
        block = this.createTableBlock(_JoinType.RIGHT_JOIN, table, tableAlias);
        this.criteriaContext.onAddBlock((_TableBlock) block);
        return block;
    }

    @Override
    public final <T extends TableItem> JS rightJoin(Supplier<T> supplier, String alias) {
        final JS block;
        block = createOnBlock(_JoinType.RIGHT_JOIN, supplier.get(), alias);
        this.criteriaContext.onAddBlock((_TableBlock) block);
        return block;
    }

    @Override
    public final <T extends TableItem> JS rightJoin(Function<C, T> function, String alias) {
        final JS block;
        block = createOnBlock(_JoinType.RIGHT_JOIN, function.apply(this.criteria), alias);
        this.criteriaContext.onAddBlock((_TableBlock) block);
        return block;
    }

    @Override
    public final JE crossJoin() {
        this.criteriaContext.onJoinType(_JoinType.CROSS_JOIN);
        return (JE) this;
    }

    @Override
    public final JF crossJoin(TableMeta<?> table) {
        return this.createNextClauseWithoutOnClause(_JoinType.CROSS_JOIN, table);
    }

    @Override
    public final JC crossJoin(TableMeta<?> table, String tableAlias) {
        final _TableBlock block;
        block = this.createTableBlockWithoutOnClause(_JoinType.CROSS_JOIN, table, tableAlias);
        this.criteriaContext.onAddBlock(block);
        return (JC) this;
    }

    @Override
    public final <T extends TableItem> JD crossJoin(Function<C, T> function, String alias) {
        this.criteriaContext.onAddBlock(TableBlock.crossBlock(function.apply(this.criteria), alias));
        return (JD) this;
    }

    @Override
    public final <T extends TableItem> JD crossJoin(Supplier<T> supplier, String alias) {
        this.criteriaContext.onAddBlock(TableBlock.crossBlock(supplier.get(), alias));
        return (JD) this;
    }

    @Override
    public final JE fullJoin() {
        this.criteriaContext.onJoinType(_JoinType.FULL_JOIN);
        return (JE) this;
    }

    @Override
    public final JP fullJoin(TableMeta<?> table) {
        return this.createNextClauseWithOnClause(_JoinType.FULL_JOIN, table);
    }

    @Override
    public final JT fullJoin(TableMeta<?> table, String tableAlias) {
        final JT block;
        block = createTableBlock(_JoinType.FULL_JOIN, table, tableAlias);
        this.criteriaContext.onAddBlock((_TableBlock) block);
        return block;
    }

    @Override
    public final <T extends TableItem> JS fullJoin(Supplier<T> supplier, String alias) {
        final JS block;
        block = createOnBlock(_JoinType.FULL_JOIN, supplier.get(), alias);
        this.criteriaContext.onAddBlock((_TableBlock) block);
        return block;
    }

    @Override
    public final <T extends TableItem> JS fullJoin(Function<C, T> function, String alias) {
        final JS block;
        block = createOnBlock(_JoinType.FULL_JOIN, function.apply(this.criteria), alias);
        this.criteriaContext.onAddBlock((_TableBlock) block);
        return block;
    }

    @Override
    public final JE straightJoin() {
        this.criteriaContext.onJoinType(_JoinType.STRAIGHT_JOIN);
        return (JE) this;
    }

    @Override
    public final JP straightJoin(TableMeta<?> table) {
        return this.createNextClauseWithOnClause(_JoinType.STRAIGHT_JOIN, table);
    }

    @Override
    public final JT straightJoin(TableMeta<?> table, String tableAlias) {
        final JT block;
        block = this.createTableBlock(_JoinType.STRAIGHT_JOIN, table, tableAlias);
        this.criteriaContext.onAddBlock((_TableBlock) block);
        return block;
    }

    @Override
    public final <T extends TableItem> JS straightJoin(Function<C, T> function, String alias) {
        final JS block;
        block = this.createOnBlock(_JoinType.STRAIGHT_JOIN, function.apply(this.criteria), alias);
        this.criteriaContext.onAddBlock((_TableBlock) block);
        return block;
    }

    @Override
    public final <T extends TableItem> JS straightJoin(Supplier<T> supplier, String alias) {
        final JS block;
        block = this.createOnBlock(_JoinType.STRAIGHT_JOIN, supplier.get(), alias);
        this.criteriaContext.onAddBlock((_TableBlock) block);
        return block;
    }

    @Override
    public final DialectStatement.JointLeftBracketClause<C, JT, JS, JP> leftBracket() {
        this.criteriaContext.onBracketBlock(CriteriaUtils.leftBracketBlock());
        return this;
    }

    @Override
    public final JP leftBracket(TableMeta<?> table) {
        this.criteriaContext.onBracketBlock(CriteriaUtils.leftBracketBlock());
        return (JP) this;
    }

    @Override
    public final JT leftBracket(final TableMeta<?> table, final String tableAlias) {
        this.criteriaContext.onBracketBlock(CriteriaUtils.leftBracketBlock());
        this.criteriaContext.onBlockWithoutOnClause(this.createTableBlockWithoutOnClause(_JoinType.NONE, table, tableAlias));
        return (JT) this;
    }

    @Override
    public final <T extends TableItem> JS leftBracket(Function<C, T> function, String alias) {
        this.criteriaContext.onBracketBlock(CriteriaUtils.leftBracketBlock());
        this.criteriaContext.onBlockWithoutOnClause(TableBlock.noneBlock(function.apply(this.criteria), alias));
        return (JS) this;
    }

    @Override
    public final <T extends TableItem> JS leftBracket(Supplier<T> supplier, String alias) {
        this.criteriaContext.onBracketBlock(CriteriaUtils.leftBracketBlock());
        this.criteriaContext.onBlockWithoutOnClause(TableBlock.noneBlock(supplier.get(), alias));
        return (JS) this;
    }

    @Override
    public final JD rightBracket() {
        this.criteriaContext.onBracketBlock(CriteriaUtils.rightBracketBlock());
        return (JD) this;
    }


    @Override
    public final CriteriaContext getCriteriaContext() {
        return this.criteriaContext;
    }

    @Override
    public final C getCriteria() {
        return this.criteria;
    }


    abstract _TableBlock createTableBlockWithoutOnClause(_JoinType joinType, TableMeta<?> table, String tableAlias);

    abstract JT createTableBlock(_JoinType joinType, TableMeta<?> table, String tableAlias);

    abstract JS createOnBlock(_JoinType joinType, TableItem tableItem, String alias);

    abstract JF createNextClauseWithoutOnClause(_JoinType joinType, TableMeta<?> table);


    JP createNextClauseWithOnClause(_JoinType joinType, TableMeta<?> table) {
        throw _Exceptions.castCriteriaApi();
    }


}
