package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner.*;
import io.army.dialect.Dialect;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.util.ArrayUtils;
import io.army.util._Exceptions;
import io.army.util._StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * <p>
 * This class is base class of all implementation of below:
 * <ul>
 *     <li>{@link io.army.criteria.Query}</li>
 *     <li>{@link UpdateStatement}</li>
 *     <li>{@link DeleteStatement}</li>
 * </ul>
 * </p>
 *
 * @since 1.0
 */
@SuppressWarnings("unchecked")
abstract class JoinableClause<FT, FS, FC, FF, JT, JS, JC, JF, WR, WA, OR, LR, LO, LF>
        extends WhereClause<WR, WA, OR, LR, LO, LF>
        implements Statement._JoinModifierClause<JT, JS>,
        Statement._JoinModifierUndoneFunctionClause<JF>,
        Statement._CrossJoinModifierClause<FT, FS>,
        Statement._CrossModifierUndoneFunctionClause<FF>,
        Statement._FromModifierClause<FT, FS>,
        Statement._FromModifierCteClause<FC>,
        Statement._FromModifierUndoneFunctionClause<FF>,
        Statement._UsingModifierClause<FT, FS>,
        Statement._UsingModifierCteClause<FC>,
        Statement._UsingModifierUndoneFunctionClause<FF>,
        DialectStatement._JoinModifierCteClause<JC>,
        DialectStatement._CrossJoinModifierCteClause<FC>,
        DialectStatement._StraightJoinModifierTabularClause<JT, JS>,
        DialectStatement._StraightJoinModifierCteClause<JC>,
        DialectStatement._StraightJoinModifierUndoneFunctionClause<JF> {


    final Consumer<_TabularBlock> blockConsumer;

    /**
     * <p>
     * private constructor
     * </p>
     */
    private JoinableClause(CriteriaContext context, Consumer<_TabularBlock> blockConsumer) {
        super(context);
        this.blockConsumer = blockConsumer;
    }


    /**
     * <p>
     * package constructor for {@link  Statement}
     * </p>
     */
    JoinableClause(CriteriaContext context) {
        super(context);
        this.blockConsumer = context::onAddBlock;
    }

    @Override
    public final FT from(TableMeta<?> table, SQLsSyntax.WordAs as, String tableAlias) {
        return this.onFromTable(_JoinType.NONE, null, table, tableAlias);
    }

    @Override
    public final FT from(@Nullable Query.TableModifier modifier, TableMeta<?> table, SQLsSyntax.WordAs wordAs,
                         String tableAlias) {
        if (modifier != null && this.isIllegalTableModifier(modifier)) {
            throw CriteriaUtils.errorModifier(this.context, modifier);
        }
        return this.onFromTable(_JoinType.NONE, modifier, table, tableAlias);
    }

    @Override
    public final FS from(@Nullable DerivedTable derivedTable) {
        if (derivedTable == null) {
            throw ContextStack.nullPointer(this.context);
        }
        return this.onFromDerived(_JoinType.NONE, null, derivedTable);
    }

    @Override
    public final <T extends DerivedTable> FS from(Supplier<T> supplier) {
        return this.from(supplier.get());
    }

    @Override
    public final FS from(@Nullable Query.DerivedModifier modifier, @Nullable DerivedTable derivedTable) {
        if (derivedTable == null) {
            throw ContextStack.nullPointer(this.context);
        } else if (modifier != null && this.isIllegalDerivedModifier(modifier)) {
            throw CriteriaUtils.errorModifier(this.context, modifier);
        }
        return this.onFromDerived(_JoinType.NONE, modifier, derivedTable);
    }

    @Override
    public final <T extends DerivedTable> FS from(@Nullable Query.DerivedModifier modifier, Supplier<T> supplier) {
        return this.from(modifier, supplier.get());
    }


    @Override
    public final FF from(UndoneFunction func) {
        return this.from(null, func);
    }

    @Override
    public final FF from(@Nullable Statement.DerivedModifier modifier, @Nullable UndoneFunction func) {
        if (func == null) {
            throw ContextStack.nullPointer(this.context);
        } else if (modifier != null && this.isIllegalDerivedModifier(modifier)) {
            throw CriteriaUtils.errorModifier(this.context, modifier);
        }
        return this.onFromUndoneFunc(_JoinType.NONE, modifier, func);
    }

    @Override
    public final FC from(String cteName) {
        return this.onFromCte(_JoinType.NONE, null, this.context.refCte(cteName), "");
    }

    @Override
    public final FC from(String cteName, SQLsSyntax.WordAs wordAs, String alias) {
        return this.onFromCte(_JoinType.NONE, null, this.context.refCte(cteName), this.cteAlias(alias));
    }

    @Override
    public final FC from(Query.DerivedModifier modifier, String cteName) {
        return this.onFromCte(_JoinType.NONE, modifier, this.context.refCte(cteName), "");
    }

    @Override
    public final FC from(Query.DerivedModifier modifier, String cteName, SQLsSyntax.WordAs wordAs, String alias) {
        return this.onFromCte(_JoinType.NONE, modifier, this.context.refCte(cteName), this.cteAlias(alias));
    }

    @Override
    public final FT using(TableMeta<?> table, SQLsSyntax.WordAs wordAs, String tableAlias) {
        return this.onFromTable(_JoinType.NONE, null, table, tableAlias);
    }

    @Override
    public final FT using(@Nullable Query.TableModifier modifier, TableMeta<?> table, SQLsSyntax.WordAs wordAs, String tableAlias) {
        if (modifier != null && this.isIllegalTableModifier(modifier)) {
            throw CriteriaUtils.errorModifier(this.context, modifier);
        }
        return this.onFromTable(_JoinType.NONE, modifier, table, tableAlias);
    }

    @Override
    public final FS using(@Nullable DerivedTable derivedTable) {
        if (derivedTable == null) {
            throw ContextStack.nullPointer(this.context);
        }
        return this.onFromDerived(_JoinType.NONE, null, derivedTable);
    }

    @Override
    public final <T extends DerivedTable> FS using(Supplier<T> supplier) {
        return this.using(supplier.get());
    }

    @Override
    public final FS using(final @Nullable Query.DerivedModifier modifier, final @Nullable DerivedTable derivedTable) {
        if (derivedTable == null) {
            throw ContextStack.nullPointer(this.context);
        } else if (modifier != null && this.isIllegalDerivedModifier(modifier)) {
            throw CriteriaUtils.errorModifier(this.context, modifier);
        }
        return this.onFromDerived(_JoinType.NONE, modifier, derivedTable);
    }

    @Override
    public final <T extends DerivedTable> FS using(@Nullable Query.DerivedModifier modifier, Supplier<T> supplier) {
        return this.using(modifier, supplier.get());
    }

    @Override
    public final FF using(UndoneFunction func) {
        return this.using(null, func);
    }

    @Override
    public final FF using(@Nullable Statement.DerivedModifier modifier, @Nullable UndoneFunction func) {
        if (func == null) {
            throw ContextStack.nullPointer(this.context);
        } else if (modifier != null && this.isIllegalDerivedModifier(modifier)) {
            throw CriteriaUtils.errorModifier(this.context, modifier);
        }
        return this.onFromUndoneFunc(_JoinType.NONE, modifier, func);
    }

    @Override
    public final FC using(String cteName) {
        return this.onFromCte(_JoinType.NONE, null, this.context.refCte(cteName), "");
    }

    @Override
    public final FC using(String cteName, SQLsSyntax.WordAs wordAs, String alias) {
        return this.onFromCte(_JoinType.NONE, null, this.context.refCte(cteName), this.cteAlias(alias));
    }

    @Override
    public final FC using(Query.DerivedModifier modifier, String cteName) {
        return this.onFromCte(_JoinType.NONE, modifier, this.context.refCte(cteName), "");
    }

    @Override
    public final FC using(Query.DerivedModifier modifier, String cteName, SQLsSyntax.WordAs wordAs, String alias) {
        return this.onFromCte(_JoinType.NONE, modifier, this.context.refCte(cteName), this.cteAlias(alias));
    }



    /*################################## blow JoinSpec method ##################################*/

    @Override
    public final JT leftJoin(TableMeta<?> table, SQLs.WordAs wordAs, String tableAlias) {
        return this.onJoinTable(_JoinType.LEFT_JOIN, null, table, tableAlias);
    }

    @Override
    public final JS leftJoin(@Nullable DerivedTable derivedTable) {
        if (derivedTable == null) {
            throw ContextStack.nullPointer(this.context);
        }
        return this.onJoinDerived(_JoinType.LEFT_JOIN, null, derivedTable);
    }

    @Override
    public final <T extends DerivedTable> JS leftJoin(Supplier<T> supplier) {
        return this.leftJoin(supplier.get());
    }


    @Override
    public final JC leftJoin(String cteName) {
        return this.onJoinCte(_JoinType.LEFT_JOIN, null, this.context.refCte(cteName), "");
    }

    @Override
    public final JC leftJoin(String cteName, SQLs.WordAs wordAs, String alias) {
        return this.onJoinCte(_JoinType.LEFT_JOIN, null, this.context.refCte(cteName), this.cteAlias(alias));
    }

    @Override
    public final JT leftJoin(@Nullable Query.TableModifier modifier, TableMeta<?> table, SQLs.WordAs as, String tableAlias) {
        if (modifier != null && this.isIllegalTableModifier(modifier)) {
            throw CriteriaUtils.errorModifier(this.context, modifier);
        }
        return this.onJoinTable(_JoinType.LEFT_JOIN, modifier, table, tableAlias);
    }

    @Override
    public final JS leftJoin(@Nullable Query.DerivedModifier modifier, @Nullable DerivedTable derivedTable) {
        if (derivedTable == null) {
            throw ContextStack.nullPointer(this.context);
        } else if (modifier != null && this.isIllegalDerivedModifier(modifier)) {
            throw CriteriaUtils.errorModifier(this.context, modifier);
        }
        return this.onJoinDerived(_JoinType.LEFT_JOIN, modifier, derivedTable);
    }

    @Override
    public final <T extends DerivedTable> JS leftJoin(@Nullable Query.DerivedModifier modifier, Supplier<T> supplier) {
        return this.leftJoin(modifier, supplier.get());
    }

    @Override
    public final JF leftJoin(UndoneFunction func) {
        return this.leftJoin(null, func);
    }

    @Override
    public final JF leftJoin(@Nullable Statement.DerivedModifier modifier, @Nullable UndoneFunction func) {
        if (func == null) {
            throw ContextStack.nullPointer(this.context);
        } else if (modifier != null && this.isIllegalDerivedModifier(modifier)) {
            throw CriteriaUtils.errorModifier(this.context, modifier);
        }
        return this.onJoinUndoneFunc(_JoinType.LEFT_JOIN, modifier, func);
    }

    @Override
    public final JC leftJoin(Query.DerivedModifier modifier, String cteName) {
        return this.onJoinCte(_JoinType.LEFT_JOIN, modifier, this.context.refCte(cteName), "");
    }

    @Override
    public final JC leftJoin(Query.DerivedModifier modifier, String cteName, SQLs.WordAs as, String alias) {
        return this.onJoinCte(_JoinType.LEFT_JOIN, modifier, this.context.refCte(cteName), this.cteAlias(alias));
    }

    @Override
    public final JT join(TableMeta<?> table, SQLs.WordAs as, String tableAlias) {
        return this.onJoinTable(_JoinType.JOIN, null, table, tableAlias);
    }

    @Override
    public final JS join(@Nullable DerivedTable derivedTable) {
        if (derivedTable == null) {
            throw ContextStack.nullPointer(this.context);
        }
        return this.onJoinDerived(_JoinType.JOIN, null, derivedTable);
    }

    @Override
    public final <T extends DerivedTable> JS join(Supplier<T> supplier) {
        return this.join(supplier.get());
    }

    @Override
    public final JC join(String cteName) {
        return this.onJoinCte(_JoinType.JOIN, null, this.context.refCte(cteName), "");
    }

    @Override
    public final JC join(String cteName, SQLs.WordAs as, String alias) {
        return this.onJoinCte(_JoinType.JOIN, null, this.context.refCte(cteName), this.cteAlias(alias));
    }

    @Override
    public final JT join(@Nullable Query.TableModifier modifier, TableMeta<?> table, SQLs.WordAs as, String tableAlias) {
        if (modifier != null && this.isIllegalTableModifier(modifier)) {
            throw CriteriaUtils.errorModifier(this.context, modifier);
        }
        return this.onJoinTable(_JoinType.JOIN, modifier, table, tableAlias);
    }

    @Override
    public final JS join(@Nullable Query.DerivedModifier modifier, @Nullable DerivedTable derivedTable) {
        if (derivedTable == null) {
            throw ContextStack.nullPointer(this.context);
        } else if (modifier != null && this.isIllegalDerivedModifier(modifier)) {
            throw CriteriaUtils.errorModifier(this.context, modifier);
        }
        return this.onJoinDerived(_JoinType.JOIN, modifier, derivedTable);
    }

    @Override
    public final <T extends DerivedTable> JS join(@Nullable Query.DerivedModifier modifier, Supplier<T> supplier) {
        return this.join(modifier, supplier.get());
    }

    @Override
    public final JF join(UndoneFunction func) {
        return this.join(null, func);
    }

    @Override
    public final JF join(@Nullable Statement.DerivedModifier modifier, @Nullable UndoneFunction func) {
        if (func == null) {
            throw ContextStack.nullPointer(this.context);
        } else if (modifier != null && this.isIllegalDerivedModifier(modifier)) {
            throw CriteriaUtils.errorModifier(this.context, modifier);
        }
        return this.onJoinUndoneFunc(_JoinType.JOIN, modifier, func);
    }

    @Override
    public final JC join(Query.DerivedModifier modifier, String cteName) {
        return this.onJoinCte(_JoinType.JOIN, modifier, this.context.refCte(cteName), "");
    }

    @Override
    public final JC join(Query.DerivedModifier modifier, String cteName, SQLs.WordAs as, String alias) {
        return this.onJoinCte(_JoinType.JOIN, modifier, this.context.refCte(cteName), this.cteAlias(alias));
    }

    @Override
    public final JT rightJoin(TableMeta<?> table, SQLs.WordAs as, String tableAlias) {
        return this.onJoinTable(_JoinType.RIGHT_JOIN, null, table, tableAlias);
    }

    @Override
    public final JS rightJoin(@Nullable DerivedTable derivedTable) {
        if (derivedTable == null) {
            throw ContextStack.nullPointer(this.context);
        }
        return this.onJoinDerived(_JoinType.RIGHT_JOIN, null, derivedTable);
    }

    @Override
    public final <T extends DerivedTable> JS rightJoin(Supplier<T> supplier) {
        return this.rightJoin(supplier.get());
    }

    @Override
    public final JC rightJoin(String cteName) {
        return this.onJoinCte(_JoinType.RIGHT_JOIN, null, this.context.refCte(cteName), "");
    }

    @Override
    public final JC rightJoin(String cteName, SQLs.WordAs as, String alias) {
        return this.onJoinCte(_JoinType.RIGHT_JOIN, null, this.context.refCte(cteName), this.cteAlias(alias));
    }

    @Override
    public final JT rightJoin(@Nullable Query.TableModifier modifier, TableMeta<?> table, SQLs.WordAs as, String tableAlias) {
        if (modifier != null && this.isIllegalTableModifier(modifier)) {
            throw CriteriaUtils.errorModifier(this.context, modifier);
        }
        return this.onJoinTable(_JoinType.RIGHT_JOIN, modifier, table, tableAlias);
    }

    @Override
    public final JS rightJoin(@Nullable Query.DerivedModifier modifier, @Nullable DerivedTable derivedTable) {
        if (derivedTable == null) {
            throw ContextStack.nullPointer(this.context);
        } else if (modifier != null && this.isIllegalDerivedModifier(modifier)) {
            throw CriteriaUtils.errorModifier(this.context, modifier);
        }
        return this.onJoinDerived(_JoinType.RIGHT_JOIN, modifier, derivedTable);
    }

    @Override
    public final <T extends DerivedTable> JS rightJoin(@Nullable Query.DerivedModifier modifier, Supplier<T> supplier) {
        return this.rightJoin(modifier, supplier.get());
    }

    @Override
    public final JF rightJoin(UndoneFunction func) {
        return this.rightJoin(null, func);
    }

    @Override
    public final JF rightJoin(@Nullable Statement.DerivedModifier modifier, @Nullable UndoneFunction func) {
        if (func == null) {
            throw ContextStack.nullPointer(this.context);
        } else if (modifier != null && this.isIllegalDerivedModifier(modifier)) {
            throw CriteriaUtils.errorModifier(this.context, modifier);
        }
        return this.onJoinUndoneFunc(_JoinType.RIGHT_JOIN, modifier, func);
    }

    @Override
    public final JC rightJoin(Query.DerivedModifier modifier, String cteName) {
        return this.onJoinCte(_JoinType.RIGHT_JOIN, modifier, this.context.refCte(cteName), "");
    }

    @Override
    public final JC rightJoin(Query.DerivedModifier modifier, String cteName, SQLs.WordAs as, String alias) {
        return this.onJoinCte(_JoinType.RIGHT_JOIN, modifier, this.context.refCte(cteName), this.cteAlias(alias));
    }

    @Override
    public final JT fullJoin(TableMeta<?> table, SQLs.WordAs as, String tableAlias) {
        return this.onJoinTable(_JoinType.FULL_JOIN, null, table, tableAlias);
    }

    @Override
    public final JS fullJoin(@Nullable DerivedTable derivedTable) {
        if (derivedTable == null) {
            throw ContextStack.nullPointer(this.context);
        }
        return this.onJoinDerived(_JoinType.FULL_JOIN, null, derivedTable);
    }

    @Override
    public final <T extends DerivedTable> JS fullJoin(Supplier<T> supplier) {
        return this.fullJoin(supplier.get());
    }

    @Override
    public final JC fullJoin(String cteName) {
        return this.onJoinCte(_JoinType.FULL_JOIN, null, this.context.refCte(cteName), "");
    }

    @Override
    public final JC fullJoin(String cteName, SQLs.WordAs as, String alias) {
        return this.onJoinCte(_JoinType.FULL_JOIN, null, this.context.refCte(cteName), this.cteAlias(alias));
    }

    @Override
    public final JT fullJoin(@Nullable Query.TableModifier modifier, TableMeta<?> table, SQLs.WordAs as, String tableAlias) {
        if (modifier != null && this.isIllegalTableModifier(modifier)) {
            throw CriteriaUtils.errorModifier(this.context, modifier);
        }
        return this.onJoinTable(_JoinType.FULL_JOIN, modifier, table, tableAlias);
    }

    @Override
    public final JS fullJoin(@Nullable Query.DerivedModifier modifier, @Nullable DerivedTable derivedTable) {
        if (derivedTable == null) {
            throw ContextStack.nullPointer(this.context);
        } else if (modifier != null && this.isIllegalDerivedModifier(modifier)) {
            throw CriteriaUtils.errorModifier(this.context, modifier);
        }
        return this.onJoinDerived(_JoinType.FULL_JOIN, modifier, derivedTable);
    }

    @Override
    public final <T extends DerivedTable> JS fullJoin(@Nullable Query.DerivedModifier modifier, Supplier<T> supplier) {
        return this.fullJoin(modifier, supplier.get());
    }

    @Override
    public final JF fullJoin(UndoneFunction func) {
        return this.fullJoin(null, func);
    }

    @Override
    public final JF fullJoin(@Nullable Statement.DerivedModifier modifier, @Nullable UndoneFunction func) {
        if (func == null) {
            throw ContextStack.nullPointer(this.context);
        } else if (modifier != null && this.isIllegalDerivedModifier(modifier)) {
            throw CriteriaUtils.errorModifier(this.context, modifier);
        }
        return this.onJoinUndoneFunc(_JoinType.FULL_JOIN, modifier, func);
    }

    @Override
    public final JC fullJoin(Query.DerivedModifier modifier, String cteName) {
        return this.onJoinCte(_JoinType.FULL_JOIN, modifier, this.context.refCte(cteName), "");
    }

    @Override
    public final JC fullJoin(Query.DerivedModifier modifier, String cteName, SQLs.WordAs as, String alias) {
        return this.onJoinCte(_JoinType.FULL_JOIN, modifier, this.context.refCte(cteName), this.cteAlias(alias));
    }

    @Override
    public final JT straightJoin(TableMeta<?> table, SQLs.WordAs as, String tableAlias) {
        return this.onJoinTable(_JoinType.STRAIGHT_JOIN, null, table, tableAlias);
    }

    @Override
    public final JS straightJoin(@Nullable DerivedTable derivedTable) {
        if (derivedTable == null) {
            throw ContextStack.nullPointer(this.context);
        }
        return this.onJoinDerived(_JoinType.STRAIGHT_JOIN, null, derivedTable);
    }

    @Override
    public final <T extends DerivedTable> JS straightJoin(Supplier<T> supplier) {
        return this.straightJoin(supplier.get());
    }

    @Override
    public final JC straightJoin(String cteName) {
        return this.onJoinCte(_JoinType.STRAIGHT_JOIN, null, this.context.refCte(cteName), "");
    }

    @Override
    public final JC straightJoin(String cteName, SQLs.WordAs as, String alias) {
        return this.onJoinCte(_JoinType.STRAIGHT_JOIN, null, this.context.refCte(cteName), this.cteAlias(alias));
    }

    @Override
    public final JC straightJoin(Query.DerivedModifier modifier, String cteName) {
        return this.onJoinCte(_JoinType.STRAIGHT_JOIN, modifier, this.context.refCte(cteName), "");
    }

    @Override
    public final JC straightJoin(Query.DerivedModifier modifier, String cteName, SQLs.WordAs as, String alias) {
        return this.onJoinCte(_JoinType.STRAIGHT_JOIN, modifier, this.context.refCte(cteName), this.cteAlias(alias));
    }

    @Override
    public final JS straightJoin(@Nullable Query.DerivedModifier modifier, @Nullable DerivedTable derivedTable) {
        if (derivedTable == null) {
            throw ContextStack.nullPointer(this.context);
        } else if (modifier != null && this.isIllegalDerivedModifier(modifier)) {
            throw CriteriaUtils.errorModifier(this.context, modifier);
        }
        return this.onJoinDerived(_JoinType.STRAIGHT_JOIN, modifier, derivedTable);
    }

    @Override
    public final <T extends DerivedTable> JS straightJoin(@Nullable Query.DerivedModifier modifier, Supplier<T> supplier) {
        return this.straightJoin(modifier, supplier.get());
    }

    @Override
    public final JF straightJoin(UndoneFunction func) {
        return this.straightJoin(null, func);
    }

    @Override
    public final JF straightJoin(@Nullable Statement.DerivedModifier modifier, @Nullable UndoneFunction func) {
        if (func == null) {
            throw ContextStack.nullPointer(this.context);
        } else if (modifier != null && this.isIllegalDerivedModifier(modifier)) {
            throw CriteriaUtils.errorModifier(this.context, modifier);
        }
        return this.onJoinUndoneFunc(_JoinType.STRAIGHT_JOIN, modifier, func);
    }

    @Override
    public final FT crossJoin(TableMeta<?> table, SQLs.WordAs as, String tableAlias) {
        return this.onFromTable(_JoinType.CROSS_JOIN, null, table, tableAlias);
    }

    @Override
    public final FS crossJoin(@Nullable DerivedTable derivedTable) {
        if (derivedTable == null) {
            throw ContextStack.nullPointer(this.context);
        }
        return this.onFromDerived(_JoinType.CROSS_JOIN, null, derivedTable);
    }

    @Override
    public final <T extends DerivedTable> FS crossJoin(Supplier<T> supplier) {
        return this.crossJoin(supplier.get());
    }

    @Override
    public final FC crossJoin(String cteName) {
        return this.onFromCte(_JoinType.CROSS_JOIN, null, this.context.refCte(cteName), "");
    }

    @Override
    public final FC crossJoin(String cteName, SQLs.WordAs wordAs, String alias) {
        return this.onFromCte(_JoinType.CROSS_JOIN, null, this.context.refCte(cteName), this.cteAlias(alias));
    }

    @Override
    public final FT crossJoin(@Nullable Query.TableModifier modifier, TableMeta<?> table, SQLs.WordAs wordAs, String tableAlias) {
        if (modifier != null && this.isIllegalTableModifier(modifier)) {
            throw CriteriaUtils.errorModifier(this.context, modifier);
        }
        return this.onFromTable(_JoinType.CROSS_JOIN, modifier, table, tableAlias);
    }

    @Override
    public final FS crossJoin(@Nullable Statement.DerivedModifier modifier, @Nullable DerivedTable derivedTable) {
        if (derivedTable == null) {
            throw ContextStack.nullPointer(this.context);
        } else if (modifier != null && this.isIllegalDerivedModifier(modifier)) {
            throw CriteriaUtils.errorModifier(this.context, modifier);
        }
        return this.onFromDerived(_JoinType.CROSS_JOIN, modifier, derivedTable);
    }

    @Override
    public final <T extends DerivedTable> FS crossJoin(@Nullable Query.DerivedModifier modifier, Supplier<T> supplier) {
        return this.crossJoin(modifier, supplier.get());
    }

    @Override
    public final FF crossJoin(UndoneFunction func) {
        return this.crossJoin(null, func);
    }

    @Override
    public final FF crossJoin(@Nullable Statement.DerivedModifier modifier, @Nullable UndoneFunction func) {
        if (func == null) {
            throw ContextStack.nullPointer(this.context);
        } else if (modifier != null && this.isIllegalDerivedModifier(modifier)) {
            throw CriteriaUtils.errorModifier(this.context, modifier);
        }
        return this.onFromUndoneFunc(_JoinType.CROSS_JOIN, modifier, func);
    }

    @Override
    public final FC crossJoin(Query.DerivedModifier modifier, String cteName) {
        return this.onFromCte(_JoinType.CROSS_JOIN, modifier, this.context.refCte(cteName), "");
    }

    @Override
    public final FC crossJoin(Query.DerivedModifier modifier, String cteName, SQLs.WordAs wordAs, String alias) {
        return this.onFromCte(_JoinType.CROSS_JOIN, modifier, this.context.refCte(cteName), this.cteAlias(alias));
    }


    final <T> T nonNull(final @Nullable T value) {
        if (value == null) {
            throw ContextStack.nullPointer(this.context);
        }
        return value;
    }

    final String cteAlias(final String alias) {
        if (!_StringUtils.hasText(alias)) {
            throw ContextStack.criteriaError(this.context, _Exceptions::cteNameNotText);
        }
        return alias;
    }


    boolean isIllegalDerivedModifier(@Nullable Query.DerivedModifier modifier) {
        throw ContextStack.castCriteriaApi(this.context);
    }

    boolean isIllegalTableModifier(@Nullable Query.TableModifier modifier) {
        throw ContextStack.castCriteriaApi(this.context);
    }


    abstract FT onFromTable(_JoinType joinType, @Nullable Query.TableModifier modifier, TableMeta<?> table,
                            String alias);

    /**
     * @see #crossJoin(Supplier)
     * @see #crossJoin(Query.DerivedModifier, Supplier)
     */
    abstract FS onFromDerived(final _JoinType joinType, final @Nullable Query.DerivedModifier modifier,
                              final DerivedTable table);

    abstract FC onFromCte(_JoinType joinType, @Nullable Query.DerivedModifier modifier, _Cte cteItem, String alias);

    abstract JT onJoinTable(_JoinType joinType, @Nullable Query.TableModifier modifier, TableMeta<?> table, String alias);

    abstract JS onJoinDerived(final _JoinType joinType, final @Nullable Query.DerivedModifier modifier,
                              final DerivedTable table);

    abstract JC onJoinCte(_JoinType joinType, @Nullable Query.DerivedModifier modifier, _Cte cteItem,
                          String alias);

    FF onFromUndoneFunc(_JoinType joinType, @Nullable Statement.DerivedModifier modifier, UndoneFunction func) {
        throw ContextStack.castCriteriaApi(this.context);
    }

    JF onJoinUndoneFunc(_JoinType joinType, @Nullable Statement.DerivedModifier modifier, UndoneFunction func) {
        throw ContextStack.castCriteriaApi(this.context);
    }

    static abstract class NestedLeftParenClause<I extends Item, LT, LS, LC, LF>
            implements _NestedItems,
            Statement._NestedLeftParenModifierClause<LT, LS>,
            Statement._NestedLeftParenModifierUndoneFunctionClause<LF>,
            DialectStatement._LeftParenCteClause<LC> {

        final CriteriaContext context;

        private final _JoinType joinType;

        private final BiFunction<_JoinType, _NestedItems, I> function;

        private List<_TabularBlock> blockList = new ArrayList<>();

        NestedLeftParenClause(CriteriaContext context, _JoinType joinType,
                              BiFunction<_JoinType, _NestedItems, I> function) {
            this.context = context;
            this.joinType = joinType;
            this.function = function;
        }

        @Override
        public final LT leftParen(TableMeta<?> table, SQLsSyntax.WordAs wordAs, String tableAlias) {
            return this.onLeftTable(null, table, tableAlias);
        }

        @Override
        public final LT leftParen(@Nullable Query.TableModifier modifier, TableMeta<?> table, SQLs.WordAs wordAs,
                                  String tableAlias) {
            if (this.isIllegalTableModifier(modifier)) {
                throw CriteriaUtils.errorModifier(this.context, modifier);
            }
            return this.onLeftTable(modifier, table, tableAlias);
        }

        @Override
        public final LS leftParen(@Nullable DerivedTable derivedTable) {
            if (derivedTable == null) {
                throw ContextStack.nullPointer(this.context);
            }
            return this.onLeftDerived(null, derivedTable);
        }

        @Override
        public final <T extends DerivedTable> LS leftParen(Supplier<T> supplier) {
            return this.leftParen(supplier.get());
        }

        @Override
        public final LS leftParen(@Nullable Statement.DerivedModifier modifier, @Nullable DerivedTable derivedTable) {
            if (derivedTable == null) {
                throw ContextStack.nullPointer(this.context);
            } else if (modifier != null && this.isIllegalDerivedModifier(modifier)) {
                throw CriteriaUtils.errorModifier(this.context, modifier);
            }
            return this.onLeftDerived(modifier, derivedTable);
        }

        @Override
        public final <T extends DerivedTable> LS leftParen(@Nullable Query.DerivedModifier modifier, Supplier<T> supplier) {
            return this.leftParen(modifier, supplier.get());
        }

        @Override
        public final LF leftParen(UndoneFunction func) {
            return this.leftParen(null, func);
        }

        @Override
        public final LF leftParen(@Nullable Statement.DerivedModifier modifier, @Nullable UndoneFunction func) {
            if (func == null) {
                throw ContextStack.nullPointer(this.context);
            } else if (modifier != null && this.isIllegalDerivedModifier(modifier)) {
                throw CriteriaUtils.errorModifier(this.context, modifier);
            }
            return this.onLeftUndoneFunc(modifier, func);
        }

        @Override
        public final LC leftParen(String cteName) {
            return this.onLeftCte(this.context.refCte(cteName), "");
        }

        @Override
        public final LC leftParen(String cteName, SQLsSyntax.WordAs wordAs, String alias) {
            return this.onLeftCte(this.context.refCte(cteName), alias);
        }

        @Override
        public final List<_TabularBlock> tableBlockList() {
            final List<_TabularBlock> blockList = this.blockList;
            if (blockList == null || blockList instanceof ArrayList) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            return blockList;
        }

        final void onAddTabularBlock(final _TabularBlock block) {
            final List<_TabularBlock> blockList = this.blockList;
            if (!(blockList instanceof ArrayList)) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            blockList.add(block);
            if (block instanceof _AliasDerivedBlock) {
                //buffer for column alias clause
                this.context.bufferNestedDerived((_AliasDerivedBlock) block);
            }

        }

        final <T> T nonNull(final @Nullable T value) {
            if (value == null) {
                throw ContextStack.nullPointer(this.context);
            }
            return value;
        }


        boolean isIllegalDerivedModifier(@Nullable Query.DerivedModifier modifier) {
            throw ContextStack.castCriteriaApi(this.context);
        }

        boolean isIllegalTableModifier(@Nullable Query.TableModifier modifier) {
            throw ContextStack.castCriteriaApi(this.context);
        }


        abstract LT onLeftTable(@Nullable Query.TableModifier modifier, TableMeta<?> table, String tableAlias);

        abstract LS onLeftDerived(@Nullable Query.DerivedModifier modifier, DerivedTable table);

        abstract LC onLeftCte(_Cte cteItem, String alias);

        LF onLeftUndoneFunc(@Nullable Statement.DerivedModifier modifier, UndoneFunction func) {
            throw ContextStack.castCriteriaApi(this.context);
        }

        @Deprecated
        final void onAddFirstBlock(final _TabularBlock block) {
            final List<_TabularBlock> blockList = this.blockList;
            if (!(blockList instanceof ArrayList && blockList.size() == 0)) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            blockList.add(block);
        }


        final I thisNestedJoinEnd() {
            final List<_TabularBlock> blockList = this.blockList;
            if (!(blockList instanceof ArrayList && blockList.size() > 0)) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            this.blockList = Collections.unmodifiableList(blockList);
            return this.function.apply(this.joinType, this);
        }

    }//NestedLeftParenClause


    @SuppressWarnings("unchecked")
    static abstract class JoinableBlock<FT, FS, FC, FF, JT, JS, JC, JF, OR>
            extends JoinableClause<FT, FS, FC, FF, JT, JS, JC, JF, Object, Object, Object, Object, Object, Object>
            implements Statement._OnClause<OR>, _TabularBlock, _TabularBlock._ModifierTableBlockSpec {

        private final _JoinType joinType;

        private final SQLWords modifier;

        final TabularItem tabularItem;

         final String alias;

        private List<_Predicate> onPredicateList;

        JoinableBlock(CriteriaContext context, Consumer<_TabularBlock> blockConsumer, _JoinType joinType,
                      @Nullable SQLWords modifier, TabularItem tabularItem, String alias) {
            super(context, blockConsumer);
            this.joinType = joinType;
            this.modifier = modifier;
            this.tabularItem = tabularItem;
            this.alias = alias;
        }

        JoinableBlock(CriteriaContext context, Consumer<_TabularBlock> blockConsumer, TabularBlocks.BlockParams params) {
            super(context, blockConsumer);
            this.joinType = params.joinType();
            this.tabularItem = params.tableItem();
            this.alias = params.alias();

            if (params instanceof TabularBlocks.DialectBlockParams) {
                this.modifier = ((TabularBlocks.DialectBlockParams) params).modifier();
            } else {
                this.modifier = null;
            }

        }

        @Override
        public final OR on(IPredicate predicate) {
            this.onPredicateList = Collections.singletonList((OperationPredicate) predicate);
            return (OR) this;
        }

        @Override
        public final OR on(IPredicate predicate1, IPredicate predicate2) {
            this.onPredicateList = ArrayUtils.asUnmodifiableList(
                    (OperationPredicate) predicate1,
                    (OperationPredicate) predicate2
            );
            return (OR) this;
        }

        @Override
        public final OR on(Function<Expression, IPredicate> operator, DataField operandField) {
            this.onPredicateList = Collections.singletonList((OperationPredicate) operator.apply(operandField));
            return (OR) this;
        }

        @Override
        public final OR on(Function<Expression, IPredicate> operator1, DataField operandField1
                , Function<Expression, IPredicate> operator2, DataField operandField2) {
            this.onPredicateList = ArrayUtils.asUnmodifiableList(
                    (OperationPredicate) operator1.apply(operandField1),
                    (OperationPredicate) operator2.apply(operandField2)
            );
            return (OR) this;
        }

        @Override
        public final OR on(Consumer<Consumer<IPredicate>> consumer) {
            final List<IPredicate> list = new ArrayList<>();
            consumer.accept(list::add);
            this.onPredicateList = CriteriaUtils.asPredicateList(this.context, list);
            return (OR) this;
        }

        @Override
        public final _JoinType jointType() {
            return this.joinType;
        }

        @Override
        public final SQLWords modifier() {
            return this.modifier;
        }

        @Override
        public final TabularItem tableItem() {
            return this.tabularItem;
        }

        @Override
        public final String alias() {
            return this.alias;
        }

        @Override
        public final List<_Predicate> onClauseList() {
            List<_Predicate> list = this.onPredicateList;
            if (list == null) {
                list = Collections.emptyList();
                this.onPredicateList = list;
            }
            return list;
        }


        @Override
        final Dialect statementDialect() {
            throw ContextStack.castCriteriaApi(this.context);
        }


    }//JoinableBlock


    static abstract class NestedJoinableBlock<FT, FS, FC, FF, JT, JS, JC, JF, OR>
            extends JoinableBlock<FT, FS, FC, FF, JT, JS, JC, JF, OR> {

        NestedJoinableBlock(CriteriaContext context, Consumer<_TabularBlock> blockConsumer, _JoinType joinType,
                            @Nullable SQLWords modifier, TabularItem tabularItem, String alias) {
            super(context, blockConsumer, joinType, modifier, tabularItem, alias);
        }

        NestedJoinableBlock(CriteriaContext context, Consumer<_TabularBlock> blockConsumer,
                            TabularBlocks.BlockParams params) {
            super(context, blockConsumer, params);
        }


    }//NestedJoinClause

    static abstract class DynamicJoinableBlock<FT, FS, FC, FF, JT, JS, JC, JF, OR>
            extends JoinableBlock<FT, FS, FC, FF, JT, JS, JC, JF, OR> {


        DynamicJoinableBlock(CriteriaContext context, Consumer<_TabularBlock> blockConsumer, _JoinType joinType,
                             @Nullable SQLWords modifier, TabularItem tabularItem, String alias) {
            super(context, blockConsumer, joinType, modifier, tabularItem, alias);
        }

        DynamicJoinableBlock(CriteriaContext context, Consumer<_TabularBlock> blockConsumer,
                             TabularBlocks.BlockParams params) {
            super(context, blockConsumer, params);
        }


    }//DynamicJoinableBlock

    static abstract class DynamicBuilderSupport<FT, FS, FC extends Item, FF>
            implements Statement._DynamicTabularModifierClause<FT, FS>,
            Statement._DynamicTabularModifierUndoneFunctionClause<FF>,
            Statement._DynamicTabularCteClause<FC>,
            Statement.JoinBuilder {

        final CriteriaContext context;

        final _JoinType joinType;

        final Consumer<_TabularBlock> blockConsumer;

        private boolean started;

        DynamicBuilderSupport(CriteriaContext context, _JoinType joinType, Consumer<_TabularBlock> blockConsumer) {
            this.context = context;
            this.joinType = joinType;
            this.blockConsumer = blockConsumer;
        }

        @Override
        public final FT space(TableMeta<?> table, SQLsSyntax.WordAs wordAs, String alias) {
            this.checkStart();
            return this.onTable(null, table, alias);
        }

        @Override
        public final FT space(@Nullable Query.TableModifier modifier, TableMeta<?> table, SQLsSyntax.WordAs wordAs,
                              String alias) {
            this.checkStart();
            if (modifier != null && this.isIllegalTableModifier(modifier)) {
                throw CriteriaUtils.errorModifier(this.context, modifier);
            }
            return this.onTable(modifier, table, alias);
        }


        @Override
        public final FS space(@Nullable DerivedTable derivedTable) {
            this.checkStart();
            if (derivedTable == null) {
                throw ContextStack.nullPointer(this.context);
            }
            return this.onDerived(null, derivedTable);
        }

        @Override
        public final <T extends DerivedTable> FS space(Supplier<T> supplier) {
            return this.space(supplier.get());
        }

        @Override
        public final <T extends DerivedTable> FS space(@Nullable Query.DerivedModifier modifier,
                                                       Supplier<T> supplier) {
            return this.space(modifier, supplier.get());
        }

        @Override
        public final FS space(@Nullable Statement.DerivedModifier modifier, @Nullable DerivedTable derivedTable) {
            this.checkStart();
            if (derivedTable == null) {
                throw ContextStack.nullPointer(this.context);
            } else if (modifier != null && this.isIllegalDerivedModifier(modifier)) {
                throw CriteriaUtils.errorModifier(this.context, modifier);
            }
            return this.onDerived(modifier, derivedTable);
        }

        @Override
        public final FF space(UndoneFunction func) {
            return this.space(null, func);
        }

        @Override
        public final FF space(@Nullable Statement.DerivedModifier modifier, @Nullable UndoneFunction func) {
            this.checkStart();
            if (func == null) {
                throw ContextStack.nullPointer(this.context);
            } else if (modifier != null && this.isIllegalDerivedModifier(modifier)) {
                throw CriteriaUtils.errorModifier(this.context, modifier);
            }
            return this.onUndoneFunc(modifier, func);
        }

        @Override
        public final FC space(String cteName) {
            this.checkStart();
            return this.onCte(this.context.refCte(cteName), "");
        }

        @Override
        public final FC space(String cteName, SQLsSyntax.WordAs as, String alias) {
            this.checkStart();
            return this.onCte(this.context.refCte(cteName), alias);
        }


        final <T> T nonNull(final @Nullable T value) {
            if (value == null) {
                throw ContextStack.nullPointer(this.context);
            }
            return value;
        }

        final void checkStart() {
            if (this.started) {
                throw CriteriaUtils.duplicateDynamicMethod(this.context);
            }
            this.started = true;
        }

        boolean isIllegalDerivedModifier(@Nullable Query.DerivedModifier modifier) {
            throw ContextStack.castCriteriaApi(this.context);
        }

        boolean isIllegalTableModifier(@Nullable Query.TableModifier modifier) {
            throw ContextStack.castCriteriaApi(this.context);
        }


        abstract FT onTable(@Nullable Query.TableModifier modifier, TableMeta<?> table, String tableAlias);

        abstract FS onDerived(@Nullable Query.DerivedModifier modifier, DerivedTable table);

        abstract FC onCte(_Cte cteItem, String alias);

        FF onUndoneFunc(@Nullable Statement.DerivedModifier modifier, UndoneFunction func) {
            throw ContextStack.castCriteriaApi(this.context);
        }


    }//DynamicBuilderSupport


}
