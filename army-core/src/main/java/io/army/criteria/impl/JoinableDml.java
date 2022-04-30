package io.army.criteria.impl;

import io.army.criteria.DialectStatement;
import io.army.criteria.TableItem;
import io.army.criteria.impl.inner._Dml;
import io.army.criteria.impl.inner._TableBlock;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.util._Exceptions;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * <p>
 * This is base class of below:
 *     <ul>
 *         <li>{@link MultiUpdate}</li>
 *         <li>{@link MultiDelete}</li>
 *     </ul>
 * </p>
 */
@SuppressWarnings("unchecked")
@Deprecated
abstract class JoinableDml<C, JT, JS, JP, JC, JD, JE, JF, WR, WA> extends DmlWhereClause<C, WR, WA>
        implements DialectStatement.DialectJoinClause<C, JT, JS, JP, JC, JD, JE, JF>, _Dml {


    JoinableDml(CriteriaContext criteriaContext) {
        super(criteriaContext);
    }

    @Override
    public final JE leftJoin() {
        this.criteriaContext.onJoinType(_JoinType.LEFT_JOIN);
        return (JE) this;
    }

    @Override
    public final JP leftJoin(TableMeta<?> table) {
        return this.createBlockBeforeAs(_JoinType.LEFT_JOIN, table);
    }

    @Override
    public final JT leftJoin(TableMeta<?> table, String tableAlias) {
        final JT block;
        block = this.createTableBlock(_JoinType.LEFT_JOIN, table, tableAlias);
        this.criteriaContext.onAddBlock((_TableBlock) block);
        return block;
    }

    @Override
    public final <T extends TableItem> JS leftJoin(Function<C, T> function, String alias) {
        final JS block;
        block = this.createOnBlock(_JoinType.LEFT_JOIN, function.apply(this.criteria), alias);
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
    public final JE join() {
        this.criteriaContext.onJoinType(_JoinType.JOIN);
        return (JE) this;
    }

    @Override
    public final JP join(TableMeta<?> table) {
        return this.createBlockBeforeAs(_JoinType.JOIN, table);
    }

    @Override
    public final JT join(TableMeta<?> table, String tableAlias) {
        final JT block;
        block = this.createTableBlock(_JoinType.JOIN, table, tableAlias);
        this.criteriaContext.onAddBlock((_TableBlock) block);
        return block;
    }

    @Override
    public final <T extends TableItem> JS join(Function<C, T> function, String alias) {
        final JS block;
        block = this.createOnBlock(_JoinType.JOIN, function.apply(this.criteria), alias);
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
    public final JE rightJoin() {
        this.criteriaContext.onJoinType(_JoinType.RIGHT_JOIN);
        return (JE) this;
    }


    @Override
    public final JP rightJoin(TableMeta<?> table) {
        return this.createBlockBeforeAs(_JoinType.RIGHT_JOIN, table);
    }

    @Override
    public final JT rightJoin(TableMeta<?> table, String tableAlias) {
        final JT block;
        block = this.createTableBlock(_JoinType.RIGHT_JOIN, table, tableAlias);
        this.criteriaContext.onAddBlock((_TableBlock) block);
        return block;
    }

    @Override
    public final <T extends TableItem> JS rightJoin(Function<C, T> function, String alias) {
        final JS block;
        block = this.createOnBlock(_JoinType.RIGHT_JOIN, function.apply(this.criteria), alias);
        this.criteriaContext.onAddBlock((_TableBlock) block);
        return block;
    }

    @Override
    public final <T extends TableItem> JS rightJoin(Supplier<T> supplier, String alias) {
        final JS block;
        block = this.createOnBlock(_JoinType.RIGHT_JOIN, supplier.get(), alias);
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
        return null;
    }

    @Override
    public final JC crossJoin(TableMeta<?> table, String tableAlias) {
        return null;
    }

    @Override
    public final <T extends TableItem> JD crossJoin(Function<C, T> function, String alias) {
        return null;
    }

    @Override
    public final <T extends TableItem> JD crossJoin(Supplier<T> supplier, String alias) {
        return null;
    }

    @Override
    public final JE fullJoin() {
        this.criteriaContext.onJoinType(_JoinType.FULL_JOIN);
        return (JE) this;
    }

    @Override
    public final JT fullJoin(TableMeta<?> table, String tableAlias) {
        final JT block;
        block = this.createTableBlock(_JoinType.FULL_JOIN, table, tableAlias);
        this.criteriaContext.onAddBlock((_TableBlock) block);
        return block;
    }

    @Override
    public final <T extends TableItem> JS fullJoin(Function<C, T> function, String alias) {
        final JS block;
        block = this.createOnBlock(_JoinType.FULL_JOIN, function.apply(this.criteria), alias);
        this.criteriaContext.onAddBlock((_TableBlock) block);
        return block;
    }

    @Override
    public final <T extends TableItem> JS fullJoin(Supplier<T> supplier, String alias) {
        final JS block;
        block = this.createOnBlock(_JoinType.FULL_JOIN, supplier.get(), alias);
        this.criteriaContext.onAddBlock((_TableBlock) block);
        return block;
    }

    @Override
    public final JE straightJoin() {
        this.criteriaContext.onJoinType(_JoinType.STRAIGHT_JOIN);
        return null;
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
    public final JP straightJoin(TableMeta<?> table) {
        return this.createBlockBeforeAs(_JoinType.STRAIGHT_JOIN, table);
    }

    @Override
    public final JP fullJoin(TableMeta<?> table) {
        return this.createBlockBeforeAs(_JoinType.FULL_JOIN, table);
    }

    abstract JT createTableBlock(_JoinType joinType, TableMeta<?> table, String tableAlias);

    abstract JS createOnBlock(_JoinType joinType, TableItem tableItem, String alias);

    abstract JT createNoActionTableBlock();

    abstract JS createNoActionOnBlock();

    abstract JT getNoActionTableBlock();

    abstract JS getNoActionOnBlock();

    JP createBlockBeforeAs(_JoinType joinType, TableMeta<?> table) {
        throw _Exceptions.castCriteriaApi();
    }

    JP ifJointTableBeforeAs(Predicate<C> predicate, _JoinType joinType, TableMeta<?> table) {
        throw _Exceptions.castCriteriaApi();
    }


    /*################################## blow private method ##################################*/


    final JS ifAddOnBlock(_JoinType joinType, @Nullable TableItem tableItem, String alias) {
        final JS block;
        if (tableItem == null) {
            block = getNoActionOnBlock();
        } else {
            block = createOnBlock(joinType, tableItem, alias);
        }
        return block;
    }

    final JT ifAddTableBlock(Predicate<C> predicate, _JoinType joinType, TableMeta<?> table, String tableAlias) {
        final JT block;
        if (predicate.test(this.criteria)) {
            block = createTableBlock(joinType, table, tableAlias);
        } else {
            block = getNoActionTableBlock();
        }
        return block;
    }


}
