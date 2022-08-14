package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner.*;
import io.army.dialect.Dialect;
import io.army.lang.Nullable;
import io.army.meta.ChildTableMeta;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.util._Assert;
import io.army.util._ClassUtils;
import io.army.util._CollectionUtils;
import io.army.util._Exceptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.*;

/**
 * <p>
 * This class is base class of below:
 *     <ul>
 *         <li>{@link SingleUpdate}</li>
 *         <li>{@link MultiUpdate}</li>
 *     </ul>
 * </p>
 */
@SuppressWarnings("unchecked")
abstract class JoinableUpdate<C, F extends DataField, SR, FT, FS, FP, FJ, JT, JS, JP, WR, WA, U extends DmlStatement.DmlUpdate>
        extends DmlWhereClause<C, FT, FS, FP, FJ, JT, JS, JP, WR, WA>
        implements DmlStatement.DmlUpdate, DmlStatement._DmlUpdateSpec<U>
        , Update._SimpleSetClause<C, F, SR>, Update._BatchSetClause<C, F, SR>
        , _Update {


    final CriteriaContext criteriaContext;

    private final boolean supportRowLeftItem;

    private List<_ItemPair> itemPairList;

    private List<_ItemPair> childItemPairList;

    private Boolean prepared;


    JoinableUpdate(CriteriaContext criteriaContext) {
        super(criteriaContext);
        assert this instanceof MultiUpdate;

        this.criteriaContext = criteriaContext;
        this.supportRowLeftItem = this.isSupportRowLeftItem();

        if (this instanceof SubStatement) {
            CriteriaContextStack.push(criteriaContext);
        } else {
            CriteriaContextStack.setContextStack(criteriaContext);
        }
    }

    JoinableUpdate(CriteriaContext criteriaContext, ClauseCreator<FP, JT, JS, JP> clauseCreator) {
        super(criteriaContext, clauseCreator);
        assert this instanceof SingleUpdate;

        this.criteriaContext = criteriaContext;
        this.supportRowLeftItem = this.isSupportRowLeftItem();
        //single update no CriteriaContextStack operation
    }

    /*################################## blow SetClause method ##################################*/


    @Override
    public final SR setPairs(Consumer<Consumer<ItemPair>> consumer) {
        consumer.accept(this::addItemPair);
        return (SR) this;
    }

    @Override
    public final SR setPairs(BiConsumer<C, Consumer<ItemPair>> consumer) {
        consumer.accept(this.criteria, this::addItemPair);
        return (SR) this;
    }

    @Override
    public final SR setExp(final F field, final Expression value) {
        return this.addFieldAndExp(field, null, value);
    }

    @Override
    public final SR setExp(F field, Supplier<? extends Expression> supplier) {
        return this.addFieldAndExp(field, null, supplier.get());
    }

    @Override
    public final SR setExp(F field, Function<C, ? extends Expression> function) {
        return this.addFieldAndExp(field, null, function.apply(this.criteria));
    }

    @Override
    public final SR ifSetExp(F field, Supplier<? extends Expression> supplier) {
        final Expression exp;
        exp = supplier.get();
        if (exp != null) {
            this.addFieldAndExp(field, null, exp);
        }
        return (SR) this;
    }

    @Override
    public final SR ifSetExp(F field, Function<C, ? extends Expression> function) {
        final Expression exp;
        exp = function.apply(this.criteria);
        if (exp != null) {
            this.addFieldAndExp(field, null, exp);
        }
        return (SR) this;
    }

    @Override
    public final SR setDefault(F field) {
        return this.addFieldAndExp(field, null, SQLs.defaultWord());
    }

    @Override
    public final SR setNull(F field) {
        return this.addFieldAndExp(field, null, SQLs.nullWord());
    }

    /*################################## blow _SimpleSetClause method ##################################*/

    @Override
    public final SR set(F field, @Nullable Object value) {
        return this.addFieldAndExp(field, null, SQLs._nullableParam(field, value));
    }


    @Override
    public final SR setLiteral(F field, @Nullable Object value) {
        return this.addFieldAndExp(field, null, SQLs._nullableLiteral(field, value));
    }

    @Override
    public final SR setPlus(F field, Object value) {
        return this.addFieldAndExp(field, SQLs::plusEqual, SQLs._nonNullParam(field, value));
    }

    @Override
    public final SR setMinus(F field, Object value) {
        return this.addFieldAndExp(field, SQLs::minusEqual, SQLs._nonNullParam(field, value));
    }

    @Override
    public final SR setPlusLiteral(F field, Object value) {
        return this.addFieldAndExp(field, SQLs::plusEqual, SQLs._nonNullLiteral(field, value));
    }

    @Override
    public final SR setMinusLiteral(F field, Object value) {
        return this.addFieldAndExp(field, SQLs::minusEqual, SQLs._nonNullLiteral(field, value));
    }

    @Override
    public final SR set(F field, BiFunction<DataField, Object, ItemPair> operator, Object value) {
        return this.addFieldAndExp(field, operator, SQLs._nonNullParam(field, value));
    }

    @Override
    public final SR setExp(F field, BiFunction<DataField, Object, ItemPair> operator, Supplier<? extends Expression> supplier) {
        return this.addFieldAndExp(field, operator, supplier.get());
    }

    @Override
    public final SR setExp(F field, BiFunction<DataField, Object, ItemPair> operator, Function<C, ? extends Expression> function) {
        return this.addFieldAndExp(field, operator, function.apply(this.criteria));
    }

    @Override
    public final SR setLiteral(F field, BiFunction<DataField, Object, ItemPair> operator, Object value) {
        return this.addFieldAndExp(field, operator, SQLs._nonNullParam(field, value));
    }

    @Override
    public final SR ifSet(F field, Supplier<?> supplier) {
        final Object value;
        value = supplier.get();
        if (value != null) {
            this.addFieldAndExp(field, null, SQLs._nonNullParam(field, value));
        }
        return (SR) this;
    }

    @Override
    public final SR ifSet(F field, Function<String, ?> function, String keyName) {
        final Object value;
        value = function.apply(keyName);
        if (value != null) {
            this.addFieldAndExp(field, null, SQLs._nonNullParam(field, value));
        }
        return (SR) this;
    }

    @Override
    public final SR ifSet(F field, BiFunction<DataField, Object, ItemPair> operator, Supplier<?> supplier) {
        final Object value;
        value = supplier.get();
        if (value != null) {
            this.addFieldAndExp(field, operator, SQLs._nonNullParam(field, value));
        }
        return (SR) this;
    }

    @Override
    public final SR ifSet(F field, BiFunction<DataField, Object, ItemPair> operator, Function<C, ?> function) {
        final Object value;
        value = function.apply(this.criteria);
        if (value != null) {
            this.addFieldAndExp(field, operator, SQLs._nonNullParam(field, value));
        }
        return (SR) this;
    }

    @Override
    public final SR ifSet(F field, BiFunction<DataField, Object, ItemPair> operator, Function<String, ?> function, String keyName) {
        final Object value;
        value = function.apply(keyName);
        if (value != null) {
            this.addFieldAndExp(field, operator, SQLs._nonNullParam(field, value));
        }
        return (SR) this;
    }

    @Override
    public final SR ifNonNullSet(F field, BiFunction<DataField, Object, ItemPair> operator, @Nullable Object operand) {
        if (operand != null) {
            this.addFieldAndExp(field, operator, SQLs._nonNullParam(field, operand));
        }
        return (SR) this;
    }

    @Override
    public final SR ifSetLiteral(F field, Supplier<?> supplier) {
        final Object value;
        value = supplier.get();
        if (value != null) {
            this.addFieldAndExp(field, null, SQLs._nonNullLiteral(field, value));
        }
        return (SR) this;
    }

    @Override
    public final SR ifSetLiteral(F field, Function<String, ?> function, String keyName) {
        final Object value;
        value = function.apply(keyName);
        if (value != null) {
            this.addFieldAndExp(field, null, SQLs._nonNullLiteral(field, value));
        }
        return (SR) this;
    }

    @Override
    public final SR ifSetLiteral(F field, BiFunction<DataField, Object, ItemPair> operator, Supplier<?> supplier) {
        final Object value;
        value = supplier.get();
        if (value != null) {
            this.addFieldAndExp(field, operator, SQLs._nonNullLiteral(field, value));
        }
        return (SR) this;
    }


    @Override
    public final SR ifNonNullSetLiteral(F field, BiFunction<DataField, Object, ItemPair> operator, @Nullable Object operand) {
        if (operand != null) {
            this.addFieldAndExp(field, operator, SQLs._nonNullLiteral(field, operand));
        }
        return (SR) this;
    }

    @Override
    public final SR ifSetLiteral(F field, BiFunction<DataField, Object, ItemPair> operator, Function<C, ?> function) {
        final Object value;
        value = function.apply(this.criteria);
        if (value != null) {
            this.addFieldAndExp(field, operator, SQLs._nonNullLiteral(field, value));
        }
        return (SR) this;
    }

    @Override
    public final SR ifSetLiteral(F field, BiFunction<DataField, Object, ItemPair> operator, Function<String, ?> function, String keyName) {
        final Object value;
        value = function.apply(keyName);
        if (value != null) {
            this.addFieldAndExp(field, operator, SQLs._nonNullLiteral(field, value));
        }
        return (SR) this;
    }


    /*################################## blow _BatchSetClause method ##################################*/

    @Override
    public final SR set(F field) {
        return this.addFieldAndExp(field, null, SQLs.namedParam(field));
    }

    @Override
    public final SR setNullable(F field) {
        return this.addFieldAndExp(field, null, SQLs.nullableNamedParam(field));
    }

    @Override
    public final SR setNamed(F field, String parameterName) {
        return this.addFieldAndExp(field, null, SQLs._namedParam(field, parameterName));
    }

    @Override
    public final SR setNullableNamed(F field, String parameterName) {
        return this.addFieldAndExp(field, null, SQLs._nullableNamedParam(field, parameterName));
    }

    @Override
    public final SR setPlus(F field) {
        return this.addFieldAndExp(field, SQLs::plusEqual, SQLs.namedParam(field));
    }

    @Override
    public final SR setMinus(F field) {
        return this.addFieldAndExp(field, SQLs::minusEqual, SQLs.namedParam(field));
    }

    @Override
    public final SR set(F field, BiFunction<DataField, Object, ItemPair> operator) {
        return this.addFieldAndExp(field, operator, SQLs.namedParam(field));
    }

    @Override
    public final SR setNamed(F field, BiFunction<DataField, Object, ItemPair> operator, String parameterName) {
        return this.addFieldAndExp(field, operator, SQLs._namedParam(field, parameterName));
    }

    @Override
    public final SR setFields(Consumer<Consumer<F>> consumer) {
        consumer.accept(this::set);
        return (SR) this;
    }

    @Override
    public final SR setFields(BiConsumer<C, Consumer<F>> consumer) {
        consumer.accept(this.criteria, this::set);
        return (SR) this;
    }

    @Override
    public final SR setNullableFields(Consumer<Consumer<F>> consumer) {
        consumer.accept(this::setNullable);
        return (SR) this;
    }

    @Override
    public final SR setNullableFields(BiConsumer<C, Consumer<F>> consumer) {
        consumer.accept(this.criteria, this::setNullable);
        return (SR) this;
    }

    @Override
    public final void prepared() {
        _Assert.prepared(this.prepared);
    }

    @Override
    public final boolean isPrepared() {
        return this.prepared;
    }

    @Override
    public final U asUpdate() {
        _Assert.nonPrepared(this.prepared);
        if (this instanceof SubStatement) {
            CriteriaContextStack.pop(this.criteriaContext);
        } else {
            CriteriaContextStack.clearContextStack(this.criteriaContext);
        }
        if (this instanceof SingleUpdate) {
            /// only single update clear context.
            this.criteriaContext.clear();
        }
        super.asDmlStatement();

        final List<_ItemPair> itemPairList = this.itemPairList;
        final List<_ItemPair> childItemPairList = this.childItemPairList;
        if ((itemPairList == null || itemPairList.size() == 0)
                && (childItemPairList == null || childItemPairList.size() == 0)) {
            throw _Exceptions.updateFieldListEmpty();
        }

        if (itemPairList == null) {
            this.itemPairList = Collections.emptyList();
        } else {
            this.itemPairList = _CollectionUtils.unmodifiableList(itemPairList);
        }
        if (childItemPairList == null) {
            this.childItemPairList = Collections.emptyList();
        } else {
            this.childItemPairList = _CollectionUtils.unmodifiableList(childItemPairList);
        }

        this.onAsUpdate();
        this.prepared = Boolean.TRUE;
        return (U) this;
    }

    @Override
    public final void clear() {
        _Assert.prepared(this.prepared);
        this.prepared = Boolean.FALSE;
        super.clearWherePredicate();
        this.onClear();
    }


    @Override
    public final List<_ItemPair> itemPairList() {
        _Assert.prepared(this.prepared);
        return this.itemPairList;
    }

    @Override
    public final List<_ItemPair> childItemPairList() {
        _Assert.prepared(this.prepared);
        return this.childItemPairList;
    }

    void onAsUpdate() {

    }

    void onClear() {

    }

    abstract boolean isSupportRowLeftItem();


    @Nullable
    abstract Dialect dialect();


    private SR addFieldAndExp(final F field, final @Nullable BiFunction<DataField, Object, ItemPair> operator
            , final @Nullable Expression operand) {
        if (operand == null) {
            throw CriteriaContextStack.nullPointer(this.criteriaContext);
        }
        if (!(operand instanceof ArmyExpression)) {
            throw CriteriaContextStack.nonArmyExp(this.criteriaContext);
        }
        final ItemPair itemPair;
        if (operator == null) {
            itemPair = SQLs._itemPair(field, null, operand);
        } else {
            itemPair = operator.apply(field, operand);
            if (!(itemPair instanceof SQLs.ArmyItemPair)) {
                throw illegalItemPair(itemPair);
            }
        }

        if (this instanceof _MultiUpdate) {
            this.doAddItemPair((SQLs.ArmyItemPair) itemPair);
        } else if (!(field instanceof FieldMeta)) {
            throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
        } else if (this instanceof _DomainUpdate) {
            if (((FieldMeta<?>) field).tableMeta() instanceof ChildTableMeta) {
                this.doAddChildItemPair((SQLs.ArmyItemPair) itemPair);
            } else {
                this.doAddItemPair((SQLs.ArmyItemPair) itemPair);
            }
        } else if (this instanceof _SingleUpdate) {
            if (((FieldMeta<?>) field).tableMeta() instanceof ChildTableMeta) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            this.doAddItemPair((SQLs.ArmyItemPair) itemPair);
        } else {
            //no bug,never here
            throw new IllegalStateException("unknown update statement");
        }
        return (SR) this;
    }


    private void addItemPair(final ItemPair pair) {
        if (!(pair instanceof SQLs.ArmyItemPair)) {
            throw illegalItemPair(pair);
        }
        if (pair instanceof SQLs.FieldItemPair) {
            final DataField field = ((SQLs.FieldItemPair) pair).field;
            if (this instanceof _MultiUpdate) {
                this.doAddItemPair((SQLs.ArmyItemPair) pair);
            } else if (!(field instanceof FieldMeta)) {
                String m = "not multi-table update support only %s" + FieldMeta.class.getName();
                throw CriteriaContextStack.criteriaError(this.criteriaContext, m);
            } else if (this instanceof _DomainUpdate) {
                if (((TableField) field).tableMeta() instanceof ChildTableMeta) {
                    this.doAddChildItemPair((SQLs.ArmyItemPair) pair);
                } else {
                    this.doAddItemPair((SQLs.ArmyItemPair) pair);
                }
            } else if (!(this instanceof _SingleUpdate)) {
                //no bug,never here
                throw new IllegalStateException("unknown update statement");
            } else if (((TableField) field).tableMeta() instanceof ChildTableMeta) {
                String m = "single update don't support child field";
                throw CriteriaContextStack.criteriaError(this.criteriaContext, m);
            } else {
                this.doAddItemPair((SQLs.ArmyItemPair) pair);
            }
        } else if (!(pair instanceof SQLs.RowItemPair)) {
            //no bug,never here
            throw new IllegalStateException("unknown ItemPair");
        } else if (!this.supportRowLeftItem) {
            throw CriteriaContextStack.criteriaError(this.criteriaContext, _Exceptions::dontSupportRowLeftItem
                    , this.dialect());
        } else if (this instanceof _MultiUpdate || !isChildTable((SQLs.RowItemPair) pair)) {
            this.doAddItemPair((SQLs.ArmyItemPair) pair);
        } else {
            this.doAddChildItemPair((SQLs.ArmyItemPair) pair);
        }

    }

    private void doAddChildItemPair(final SQLs.ArmyItemPair pair) {
        List<_ItemPair> childItemPairList = this.childItemPairList;
        if (childItemPairList == null) {
            childItemPairList = new ArrayList<>();
            this.childItemPairList = childItemPairList;
        } else if (!(childItemPairList instanceof ArrayList)) {
            throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
        }
        childItemPairList.add(pair);
    }

    private void doAddItemPair(final SQLs.ArmyItemPair pair) {
        List<_ItemPair> itemPairList = this.itemPairList;
        if (itemPairList == null) {
            itemPairList = new ArrayList<>();
            this.itemPairList = itemPairList;
        } else if (!(itemPairList instanceof ArrayList)) {
            throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
        }
        itemPairList.add(pair);
    }

    private CriteriaException illegalItemPair(@Nullable ItemPair itemPair) {
        String m = String.format("%s Illegal %s", _ClassUtils.safeClassName(itemPair), ItemPair.class.getName());
        throw CriteriaContextStack.criteriaError(this.criteriaContext, m);
    }

    private static boolean isChildTable(final SQLs.RowItemPair pair) {
        TableMeta<?> table = null;
        for (DataField field : pair.fieldList) {
            if (!(field instanceof TableField)) {
                // no derived field,because don't support multi-table update
                throw _Exceptions.immutableField(field);
            }
            if (table == null) {
                table = ((TableField) field).tableMeta();
            } else if (((TableField) field).tableMeta() != table) {
                throw rowColumnTableNotMatch(pair.fieldList);
            }
        }
        assert table != null;
        return table instanceof ChildTableMeta;
    }


    private static CriteriaException rowColumnTableNotMatch(List<? extends DataField> fieldList) {
        String m = String.format("Row columns %s table not match", fieldList);
        return new CriteriaException(m);
    }


}
